package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TgAvatarMaterial;

public interface ITgAvatarMaterialService
{
    public List<TgAvatarMaterial> selectList(TgAvatarMaterial query);

    public List<TgAvatarMaterial> selectByIds(Integer[] ids);

    public List<String> selectAllPaths();

    public int insert(TgAvatarMaterial material);

    public int deleteByIds(Integer[] ids);
}
