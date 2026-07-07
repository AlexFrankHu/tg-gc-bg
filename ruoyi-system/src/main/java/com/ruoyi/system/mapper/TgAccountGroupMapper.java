package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TgAccountGroup;

/**
 * 账号分组Mapper接口
 */
public interface TgAccountGroupMapper
{
    public List<TgAccountGroup> selectTgAccountGroupList(TgAccountGroup tgAccountGroup);

    public TgAccountGroup selectTgAccountGroupById(Integer id);

    public int insertTgAccountGroup(TgAccountGroup tgAccountGroup);

    public int updateTgAccountGroup(TgAccountGroup tgAccountGroup);
}
