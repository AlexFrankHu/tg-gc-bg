package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TgProxyGroup;

public interface TgProxyGroupMapper
{
    public List<TgProxyGroup> selectTgProxyGroupList(TgProxyGroup group);

    public List<TgProxyGroup> selectAllGroups();

    public TgProxyGroup selectTgProxyGroupById(Integer id);

    public TgProxyGroup selectTgProxyGroupByGroupNo(String groupNo);

    public int insertTgProxyGroup(TgProxyGroup group);

    public int updateTgProxyGroup(TgProxyGroup group);

    public int deleteTgProxyGroupByIds(Integer[] ids);
}
