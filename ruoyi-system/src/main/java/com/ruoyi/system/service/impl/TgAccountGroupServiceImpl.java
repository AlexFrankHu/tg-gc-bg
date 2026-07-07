package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TgAccountGroupMapper;
import com.ruoyi.system.domain.TgAccountGroup;
import com.ruoyi.system.service.ITgAccountGroupService;

/**
 * 账号分组Service业务层处理
 */
@Service
public class TgAccountGroupServiceImpl implements ITgAccountGroupService
{
    @Autowired
    private TgAccountGroupMapper tgAccountGroupMapper;

    @Override
    public List<TgAccountGroup> selectTgAccountGroupList(TgAccountGroup tgAccountGroup)
    {
        return tgAccountGroupMapper.selectTgAccountGroupList(tgAccountGroup);
    }

    @Override
    public TgAccountGroup selectTgAccountGroupById(Integer id)
    {
        return tgAccountGroupMapper.selectTgAccountGroupById(id);
    }

    @Override
    public int insertTgAccountGroup(TgAccountGroup tgAccountGroup)
    {
        if (tgAccountGroup.getEnabled() == null) {
            tgAccountGroup.setEnabled(1);
        }
        return tgAccountGroupMapper.insertTgAccountGroup(tgAccountGroup);
    }

    @Override
    public int updateTgAccountGroup(TgAccountGroup tgAccountGroup)
    {
        return tgAccountGroupMapper.updateTgAccountGroup(tgAccountGroup);
    }
}
