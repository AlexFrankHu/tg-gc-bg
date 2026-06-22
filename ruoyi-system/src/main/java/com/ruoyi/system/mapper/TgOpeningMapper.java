package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TgOpening;

public interface TgOpeningMapper
{
    public TgOpening selectById(Integer id);

    public List<TgOpening> selectList(TgOpening opening);

    public int insert(TgOpening opening);

    public int update(TgOpening opening);

    public int deleteByIds(Integer[] ids);
}
