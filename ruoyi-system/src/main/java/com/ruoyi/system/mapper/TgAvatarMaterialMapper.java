package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TgAvatarMaterial;

public interface TgAvatarMaterialMapper
{
    public List<TgAvatarMaterial> selectList(TgAvatarMaterial query);

    public List<TgAvatarMaterial> selectByIds(Integer[] ids);

    public List<String> selectAllPaths();

    public int insert(TgAvatarMaterial material);

    public int deleteByIds(Integer[] ids);
}
