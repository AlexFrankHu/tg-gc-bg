package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.TgContact;

public interface TgContactMapper
{
    public TgContact selectTgContactById(Integer id);
    public List<TgContact> selectTgContactList(TgContact tgContact);
    public int insertTgContact(TgContact tgContact);
    public int updateTgContact(TgContact tgContact);
    public int deleteTgContactById(Integer id);
    public int deleteTgContactByIds(Integer[] ids);
    public int updateAutoReplyById(@Param("id") Integer id, @Param("autoReply") Boolean autoReply);
    public int updateAllAutoReply(@Param("autoReply") Boolean autoReply);

    public int updateMessageCountsForActiveContacts();

    public List<TgContact> selectContactMessageStats();
    public int batchUpdateContactMessageCounts(@Param("list") List<TgContact> list);
}
