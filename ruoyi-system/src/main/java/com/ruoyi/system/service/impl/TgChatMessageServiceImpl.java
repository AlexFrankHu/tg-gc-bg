package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TgChatMessageMapper;
import com.ruoyi.system.domain.TgChatMessage;
import com.ruoyi.system.service.ITgChatMessageService;

@Service
public class TgChatMessageServiceImpl implements ITgChatMessageService
{
    @Autowired
    private TgChatMessageMapper tgChatMessageMapper;

    @Override
    public TgChatMessage selectTgChatMessageById(Long id) { return tgChatMessageMapper.selectTgChatMessageById(id); }

    @Override
    public List<TgChatMessage> selectTgChatMessageList(TgChatMessage tgChatMessage) { return tgChatMessageMapper.selectTgChatMessageList(tgChatMessage); }

    @Override
    public int insertTgChatMessage(TgChatMessage tgChatMessage) { return tgChatMessageMapper.insertTgChatMessage(tgChatMessage); }

    @Override
    public int updateTgChatMessage(TgChatMessage tgChatMessage) { return tgChatMessageMapper.updateTgChatMessage(tgChatMessage); }

    @Override
    public int deleteTgChatMessageByIds(Long[] ids) { return tgChatMessageMapper.deleteTgChatMessageByIds(ids); }

    @Override
    public int deleteTgChatMessageById(Long id) { return tgChatMessageMapper.deleteTgChatMessageById(id); }
}
