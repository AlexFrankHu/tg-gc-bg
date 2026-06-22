package com.ruoyi.web.controller.tg;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.ruoyi.system.domain.TgClusterNode;
import com.ruoyi.system.domain.TgTelethonAccount;
import com.ruoyi.system.service.ITgClusterNodeService;
import com.ruoyi.system.service.ITgTelethonAccountService;

/**
 * 节点分配账号定时任务 - 每1分钟执行一次
 * 将未分配节点的账号分配到活跃节点
 */
@Component
public class TgNodeAssignTask
{
    private static final Logger log = LoggerFactory.getLogger(TgNodeAssignTask.class);

    @Autowired
    private ITgClusterNodeService clusterNodeService;

    @Autowired
    private ITgTelethonAccountService telethonAccountService;

    /**
     * 每1分钟执行一次，将未分配节点的账号分配到活跃节点
     */
    @Scheduled(fixedRate = 60000)
    public void assignAccountsToNodes()
    {
        try
        {
            // 1. 查询未分配节点的账号
            List<TgTelethonAccount> unassigned = telethonAccountService.selectUnassignedAccounts();
            if (unassigned == null || unassigned.isEmpty())
            {
                return;
            }

            // 2. 查询活跃节点（最后活跃时间在2分钟内）
            List<TgClusterNode> activeNodes = clusterNodeService.selectActiveNodes(2);
            if (activeNodes == null || activeNodes.isEmpty())
            {
                log.debug("无活跃节点，跳过分配");
                return;
            }

            log.info("节点分配: {} 个未分配账号, {} 个活跃节点", unassigned.size(), activeNodes.size());

            // 3. 按规则分配：优先未饱和节点，优先在线账号数少的节点
            int assignedCount = 0;
            for (TgTelethonAccount account : unassigned)
            {
                TgClusterNode bestNode = selectBestNode(activeNodes);
                if (bestNode == null)
                {
                    log.warn("所有节点已饱和，停止分配");
                    break;
                }

                telethonAccountService.updateNodeId(account.getId(), bestNode.getNodeId());
                assignedCount++;

                // Update local count for subsequent assignments
                if (bestNode.getTotalAccountCount() == null)
                {
                    bestNode.setTotalAccountCount(1);
                }
                else
                {
                    bestNode.setTotalAccountCount(bestNode.getTotalAccountCount() + 1);
                }
            }

            if (assignedCount > 0)
            {
                log.info("节点分配完成: 分配了 {} 个账号", assignedCount);
            }
        }
        catch (Exception e)
        {
            log.error("节点分配任务异常", e);
        }
    }

    /**
     * 选择最佳节点：优先未饱和，再优先账号数少的
     */
    private TgClusterNode selectBestNode(List<TgClusterNode> nodes)
    {
        TgClusterNode best = null;
        int bestCount = Integer.MAX_VALUE;

        for (TgClusterNode node : nodes)
        {
            int current = node.getTotalAccountCount() != null ? node.getTotalAccountCount() : 0;
            int max = node.getMaxAccountCount() != null ? node.getMaxAccountCount() : 200;

            // Skip saturated nodes
            if (current >= max)
            {
                continue;
            }

            if (current < bestCount)
            {
                bestCount = current;
                best = node;
            }
        }
        return best;
    }
}
