package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TgImportBatch;

/**
 * 账号导入批次Service接口
 */
public interface ITgImportBatchService
{
    public TgImportBatch selectTgImportBatchById(Integer id);

    public TgImportBatch selectTgImportBatchByBatchNo(String batchNo);

    public List<TgImportBatch> selectTgImportBatchList(TgImportBatch batch);

    public int insertTgImportBatch(TgImportBatch batch);

    public int updateTgImportBatch(TgImportBatch batch);

    public int deleteTgImportBatchByIds(Integer[] ids);

    public void refreshAllBatchStats();
}
