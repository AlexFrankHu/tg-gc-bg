package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.mapper.TgChatMessageMapper;
import com.ruoyi.system.domain.TgChatMessage;
import com.ruoyi.system.service.ITgChatMessageService;

@Service
public class TgChatMessageServiceImpl implements ITgChatMessageService
{
    private static final Logger log = LoggerFactory.getLogger(TgChatMessageServiceImpl.class);

    @Autowired
    private TgChatMessageMapper tgChatMessageMapper;

    @Override
    public TgChatMessage selectTgChatMessageById(Long id) { return tgChatMessageMapper.selectTgChatMessageById(id); }

    @Override
    public List<TgChatMessage> selectTgChatMessageList(TgChatMessage tgChatMessage) { return tgChatMessageMapper.selectTgChatMessageList(tgChatMessage); }

    @Override
    public long countForPage(TgChatMessage q)
    {
        // Plain browse (no narrowing filter): full COUNT would scan ~13M rows (~24s),
        // so use the instant InnoDB approximate row count instead.
        if (!hasNarrowingFilter(q))
        {
            Long approx = tgChatMessageMapper.approximateTgChatMessageCount();
            return approx == null ? 0L : approx;
        }
        // With filters: try an exact count, but capped by MAX_EXECUTION_TIME in SQL.
        // If it exceeds the cap MySQL aborts (error 3024); fall back to approximate.
        try
        {
            Long cnt = tgChatMessageMapper.countTgChatMessageList(q);
            return cnt == null ? 0L : cnt;
        }
        catch (Exception e)
        {
            log.warn("聊天记录精确统计超时, 回退近似行数: {}", e.getMessage());
            Long approx = tgChatMessageMapper.approximateTgChatMessageCount();
            return approx == null ? 0L : approx;
        }
    }

    /** True when a filter narrows the result set (any non-empty query condition). */
    private boolean hasNarrowingFilter(TgChatMessage q)
    {
        if (q == null) return false;
        if (q.getTgAccountId() != null || q.getChatId() != null || q.getSenderUserId() != null
            || q.getIsOutgoing() != null
            || StringUtils.isNotEmpty(q.getPhone())
            || StringUtils.isNotEmpty(q.getContentType())
            || StringUtils.isNotEmpty(q.getTextContent()))
        {
            return true;
        }
        Map<String, Object> params = q.getParams();
        if (params != null)
        {
            Object begin = params.get("beginSendTime");
            Object end = params.get("endSendTime");
            if (begin != null && !"".equals(begin)) return true;
            if (end != null && !"".equals(end)) return true;
        }
        return false;
    }

    @Override
    public int insertTgChatMessage(TgChatMessage tgChatMessage) { return tgChatMessageMapper.insertTgChatMessage(tgChatMessage); }

    @Override
    public int updateTgChatMessage(TgChatMessage tgChatMessage) { return tgChatMessageMapper.updateTgChatMessage(tgChatMessage); }

    @Override
    public int deleteTgChatMessageByIds(Long[] ids) { return tgChatMessageMapper.deleteTgChatMessageByIds(ids); }

    @Override
    public int deleteTgChatMessageById(Long id) { return tgChatMessageMapper.deleteTgChatMessageById(id); }
}
