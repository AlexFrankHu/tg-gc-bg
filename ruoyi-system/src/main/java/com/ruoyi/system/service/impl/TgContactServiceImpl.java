package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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

    private static final int FRIEND_CHECK_BATCH_SIZE = 1000;

    @Override
    public List<Long> filterFriendUserIds(List<Long> userIds)
    {
        List<Long> distinct = new ArrayList<>(new LinkedHashSet<>(userIds));
        Set<Long> found = new HashSet<>();
        for (int i = 0; i < distinct.size(); i += FRIEND_CHECK_BATCH_SIZE)
        {
            List<Long> batch = distinct.subList(i, Math.min(i + FRIEND_CHECK_BATCH_SIZE, distinct.size()));
            found.addAll(tgContactMapper.selectExistingUserIds(batch));
        }
        List<Long> result = new ArrayList<>();
        for (Long uid : distinct)
        {
            if (found.contains(uid))
            {
                result.add(uid);
            }
        }
        return result;
    }
}
