package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TgProxyGroup;

public interface ITgProxyGroupService
{
    public List<TgProxyGroup> selectTgProxyGroupList(TgProxyGroup group);

    public List<TgProxyGroup> selectAllGroups();

    public TgProxyGroup selectTgProxyGroupById(Integer id);

    public int insertTgProxyGroup(TgProxyGroup group);

    public int updateTgProxyGroup(TgProxyGroup group);

    public int deleteTgProxyGroupByIds(Integer[] ids);
}
