package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TgAccountGroup;

/**
 * 账号分组Service接口
 */
public interface ITgAccountGroupService
{
    public List<TgAccountGroup> selectTgAccountGroupList(TgAccountGroup tgAccountGroup);

    public TgAccountGroup selectTgAccountGroupById(Integer id);

    public int insertTgAccountGroup(TgAccountGroup tgAccountGroup);

    public int updateTgAccountGroup(TgAccountGroup tgAccountGroup);
}
