package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.TgAvatarMaterial;
import com.ruoyi.system.mapper.TgAvatarMaterialMapper;
import com.ruoyi.system.service.ITgAvatarMaterialService;

@Service
public class TgAvatarMaterialServiceImpl implements ITgAvatarMaterialService
{
    @Autowired
    private TgAvatarMaterialMapper mapper;

    @Override
    public List<TgAvatarMaterial> selectList(TgAvatarMaterial query)
    {
        return mapper.selectList(query);
    }

    @Override
    public List<TgAvatarMaterial> selectByIds(Integer[] ids)
    {
        return mapper.selectByIds(ids);
    }

    @Override
    public List<String> selectAllPaths()
    {
        return mapper.selectAllPaths();
    }

    @Override
    public int insert(TgAvatarMaterial material)
    {
        return mapper.insert(material);
    }

    @Override
    public int deleteByIds(Integer[] ids)
    {
        return mapper.deleteByIds(ids);
    }
}
