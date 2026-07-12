package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TgChatMessage;

public interface ITgChatMessageService
{
    public TgChatMessage selectTgChatMessageById(Long id);
    public List<TgChatMessage> selectTgChatMessageList(TgChatMessage tgChatMessage);

    /**
     * 统计聊天记录总数（用于分页）。带过滤条件时做精确统计并限时，
     * 超时或无过滤条件时回退为 InnoDB 近似行数，避免每次翻页对千万级表全表 COUNT。
     */
    public long countForPage(TgChatMessage tgChatMessage);
    public int insertTgChatMessage(TgChatMessage tgChatMessage);
    public int updateTgChatMessage(TgChatMessage tgChatMessage);
    public int deleteTgChatMessageByIds(Long[] ids);
    public int deleteTgChatMessageById(Long id);
}
