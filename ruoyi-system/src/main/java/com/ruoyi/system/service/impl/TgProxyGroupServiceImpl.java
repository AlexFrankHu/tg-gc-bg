package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.TgProxyGroup;
import com.ruoyi.system.mapper.TgProxyGroupMapper;
import com.ruoyi.system.service.ITgProxyGroupService;

@Service
public class TgProxyGroupServiceImpl implements ITgProxyGroupService
{
    @Autowired
    private TgProxyGroupMapper tgProxyGroupMapper;

    @Override
    public List<TgProxyGroup> selectTgProxyGroupList(TgProxyGroup group) {
        return tgProxyGroupMapper.selectTgProxyGroupList(group);
    }

    @Override
    public List<TgProxyGroup> selectAllGroups() {
        return tgProxyGroupMapper.selectAllGroups();
    }

    @Override
    public TgProxyGroup selectTgProxyGroupById(Integer id) {
        return tgProxyGroupMapper.selectTgProxyGroupById(id);
    }

    @Override
    public int insertTgProxyGroup(TgProxyGroup group) {
        return tgProxyGroupMapper.insertTgProxyGroup(group);
    }

    @Override
    public int updateTgProxyGroup(TgProxyGroup group) {
        return tgProxyGroupMapper.updateTgProxyGroup(group);
    }

    @Override
    public int deleteTgProxyGroupByIds(Integer[] ids) {
        return tgProxyGroupMapper.deleteTgProxyGroupByIds(ids);
    }
}
