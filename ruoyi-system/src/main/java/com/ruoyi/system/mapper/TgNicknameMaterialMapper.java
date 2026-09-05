package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TgNicknameMaterial;

public interface TgNicknameMaterialMapper
{
    public List<TgNicknameMaterial> selectList(TgNicknameMaterial query);

    public List<String> selectAllNicknames();

    public int batchInsert(List<String> nicknames);

    public int deleteByIds(Integer[] ids);

    public int deleteAll();
}
