package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TgAccountTask;

public interface TgAccountTaskMapper
{
    public List<TgAccountTask> selectList(TgAccountTask query);

    public int batchInsert(List<TgAccountTask> tasks);

    public int deleteByIds(Integer[] ids);

    public int deleteFinished();
}
