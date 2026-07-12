package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TgChatMessage;

public interface TgChatMessageMapper
{
    public TgChatMessage selectTgChatMessageById(Long id);
    public List<TgChatMessage> selectTgChatMessageList(TgChatMessage tgChatMessage);
    public Long countTgChatMessageList(TgChatMessage tgChatMessage);
    public Long approximateTgChatMessageCount();
    public int insertTgChatMessage(TgChatMessage tgChatMessage);
    public int updateTgChatMessage(TgChatMessage tgChatMessage);
    public int deleteTgChatMessageById(Long id);
    public int deleteTgChatMessageByIds(Long[] ids);
}
