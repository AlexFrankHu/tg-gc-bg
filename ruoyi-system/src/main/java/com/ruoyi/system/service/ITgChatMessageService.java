package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TgChatMessage;

public interface ITgChatMessageService
{
    public TgChatMessage selectTgChatMessageById(Long id);
    public List<TgChatMessage> selectTgChatMessageList(TgChatMessage tgChatMessage);
    public int insertTgChatMessage(TgChatMessage tgChatMessage);
    public int updateTgChatMessage(TgChatMessage tgChatMessage);
    public int deleteTgChatMessageByIds(Long[] ids);
    public int deleteTgChatMessageById(Long id);
}
