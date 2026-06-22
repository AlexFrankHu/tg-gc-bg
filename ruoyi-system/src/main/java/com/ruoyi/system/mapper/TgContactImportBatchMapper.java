package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TgContactImportBatch;

public interface TgContactImportBatchMapper
{
    public TgContactImportBatch selectById(Integer id);

    public TgContactImportBatch selectByBatchNo(String batchNo);

    public List<TgContactImportBatch> selectList(TgContactImportBatch batch);

    public int insert(TgContactImportBatch batch);

    public int update(TgContactImportBatch batch);

    public int deleteByIds(Integer[] ids);

    public int refreshStats(String batchNo);
}
