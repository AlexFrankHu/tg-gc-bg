package com.ruoyi.web.controller.tg;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.ruoyi.system.domain.TgClusterNode;
import com.ruoyi.system.domain.TgContact;
import com.ruoyi.system.domain.TgTelethonAccount;
import com.ruoyi.system.mapper.TgTelethonAccountMapper;
import com.ruoyi.system.mapper.TgContactMapper;
import com.ruoyi.system.mapper.TgClusterNodeMapper;

/**
 * 统计定时任务 - 每5分钟执行一次
 */
@Component
public class TgStatisticsTask
{
    private static final Logger log = LoggerFactory.getLogger(TgStatisticsTask.class);

    @Autowired
    private TgTelethonAccountMapper telethonAccountMapper;

    @Autowired
    private TgContactMapper contactMapper;

    @Autowired
    private TgClusterNodeMapper clusterNodeMapper;

    @Value("${tg.bot.token:8534398194:AAF6CKDeS_yGeo167C4znOq9cR3porDGJa0}")
    private String botToken;

    @Value("${tg.bot.chatId:-5181774632}")
    private String botChatId;

    /** 已通知过离线的节点ID集合，避免重复通知 */
    private final Set<String> notifiedOfflineNodes = new HashSet<>();

    @Scheduled(fixedRate = 300000)
    public void runStatistics()
    {
        try
        {
            updateAccountMessageCounts();
            updateContactMessageCounts();
            checkOfflineNodes();
        }
        catch (Exception e)
        {
            log.error("统计任务异常", e);
        }
    }

    /** 每批更新的行数, 控制单条 UPDATE 的锁范围 */
    private static final int BATCH_SIZE = 500;

    /**
     * 统计账号消息数量。
     * 先用普通 SELECT(一致性非锁定读, 不加行锁)算出结果, 再按主键分批 UPDATE,
     * 避免原来 UPDATE...JOIN 长时间锁住上千万行导致节点写入 1205/1213。
     */
    private void updateAccountMessageCounts()
    {
        try
        {
            List<TgTelethonAccount> stats = telethonAccountMapper.selectAccountMessageStats();
            int updated = 0;
            for (int i = 0; i < stats.size(); i += BATCH_SIZE)
            {
                List<TgTelethonAccount> batch = stats.subList(i, Math.min(i + BATCH_SIZE, stats.size()));
                updated += telethonAccountMapper.batchUpdateAccountMessageCounts(batch);
            }
            if (updated > 0)
            {
                log.info("消息统计: 更新了 {} 个账号的消息数量", updated);
            }
        }
        catch (Exception e)
        {
            log.error("统计账号消息数量失败", e);
        }
    }

    /**
     * 统计好友消息数量。策略同上: 只读快照 + 按主键分批 UPDATE。
     */
    private void updateContactMessageCounts()
    {
        try
        {
            List<TgContact> stats = contactMapper.selectContactMessageStats();
            int updated = 0;
            for (int i = 0; i < stats.size(); i += BATCH_SIZE)
            {
                List<TgContact> batch = stats.subList(i, Math.min(i + BATCH_SIZE, stats.size()));
                updated += contactMapper.batchUpdateContactMessageCounts(batch);
            }
            if (updated > 0)
            {
                log.info("好友消息统计: 更新了 {} 个好友的消息数量", updated);
            }
        }
        catch (Exception e)
        {
            log.error("统计好友消息数量失败", e);
        }
    }

    /**
     * 检查离线节点并发送TG通知
     * 最后活跃时间超过3分钟的节点视为离线
     */
    private void checkOfflineNodes()
    {
        try
        {
            List<TgClusterNode> offlineNodes = clusterNodeMapper.selectOfflineNodes(3);

            if (offlineNodes == null || offlineNodes.isEmpty())
            {
                // 所有节点在线，清空已通知集合
                if (!notifiedOfflineNodes.isEmpty())
                {
                    notifiedOfflineNodes.clear();
                }
                return;
            }

            // 收集当前离线节点ID
            Set<String> currentOffline = new HashSet<>();
            for (TgClusterNode node : offlineNodes)
            {
                currentOffline.add(node.getNodeId());
            }

            // 恢复在线的节点从已通知集合中移除
            notifiedOfflineNodes.retainAll(currentOffline);

            // 对新发现的离线节点发送通知
            StringBuilder msg = new StringBuilder();
            int newOfflineCount = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (TgClusterNode node : offlineNodes)
            {
                if (!notifiedOfflineNodes.contains(node.getNodeId()))
                {
                    newOfflineCount++;
                    notifiedOfflineNodes.add(node.getNodeId());
                    String lastTime = node.getLastActiveTime() != null ? sdf.format(node.getLastActiveTime()) : "未知";
                    msg.append(String.format("\n- %s (%s:%d) 最后活跃: %s",
                        node.getNodeId().substring(0, 8),
                        node.getPublicIp(),
                        node.getNodePort(),
                        lastTime));
                }
            }

            if (newOfflineCount > 0)
            {
                String text = "⚠️ 节点离线告警\n"
                    + newOfflineCount + " 个节点已离线（超过3分钟无心跳）："
                    + msg.toString();
                sendTgNotification(text);
                log.warn("节点离线告警: {} 个节点离线", newOfflineCount);
            }
        }
        catch (Exception e)
        {
            log.error("检查离线节点失败", e);
        }
    }

    /**
     * 发送TG机器人通知
     */
    private void sendTgNotification(String text)
    {
        try
        {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendMessage";
            String json = "{\"chat_id\":\"" + botChatId + "\",\"text\":\"" + escapeJson(text) + "\"}";

            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            try (OutputStream os = conn.getOutputStream())
            {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }
            int code = conn.getResponseCode();
            if (code != 200)
            {
                log.warn("TG通知发送失败, HTTP {}", code);
            }
        }
        catch (Exception e)
        {
            log.error("TG通知发送异常", e);
        }
    }

    private String escapeJson(String text)
    {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
