package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TgContactImportBatch;

public interface ITgContactImportBatchService
{
    public TgContactImportBatch selectById(Integer id);

    public List<TgContactImportBatch> selectList(TgContactImportBatch batch);

    public int insert(TgContactImportBatch batch);

    public int update(TgContactImportBatch batch);

    public int deleteByIds(Integer[] ids);
}
