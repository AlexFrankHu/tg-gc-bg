package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TgContactMapper;
import com.ruoyi.system.domain.TgContact;
import com.ruoyi.system.service.ITgContactService;

@Service
public class TgContactServiceImpl implements ITgContactService
{
    @Autowired
    private TgContactMapper tgContactMapper;

    @Override
    public TgContact selectTgContactById(Integer id) { return tgContactMapper.selectTgContactById(id); }

    @Override
    public List<TgContact> selectTgContactList(TgContact tgContact) { return tgContactMapper.selectTgContactList(tgContact); }

    @Override
    public int insertTgContact(TgContact tgContact) { return tgContactMapper.insertTgContact(tgContact); }

    @Override
    public int updateTgContact(TgContact tgContact) { return tgContactMapper.updateTgContact(tgContact); }

    @Override
    public int deleteTgContactByIds(Integer[] ids) { return tgContactMapper.deleteTgContactByIds(ids); }

    @Override
    public int deleteTgContactById(Integer id) { return tgContactMapper.deleteTgContactById(id); }

    @Override
    public int updateAutoReplyById(Integer id, Boolean autoReply) { return tgContactMapper.updateAutoReplyById(id, autoReply); }

    @Override
    public int updateAllAutoReply(Boolean autoReply) { return tgContactMapper.updateAllAutoReply(autoReply); }
}
