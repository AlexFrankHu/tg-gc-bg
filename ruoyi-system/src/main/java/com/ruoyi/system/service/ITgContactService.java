package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TgContact;

public interface ITgContactService
{
    public TgContact selectTgContactById(Integer id);
    public List<TgContact> selectTgContactList(TgContact tgContact);
    public int insertTgContact(TgContact tgContact);
    public int updateTgContact(TgContact tgContact);
    public int deleteTgContactByIds(Integer[] ids);
    public int deleteTgContactById(Integer id);
    public int updateAutoReplyById(Integer id, Boolean autoReply);
    public int updateAllAutoReply(Boolean autoReply);
}
