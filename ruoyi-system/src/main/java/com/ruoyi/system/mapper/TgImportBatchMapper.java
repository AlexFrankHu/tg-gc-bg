package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TgImportBatch;

/**
 * 账号导入批次Mapper接口
 */
public interface TgImportBatchMapper
{
    public TgImportBatch selectTgImportBatchById(Integer id);

    public TgImportBatch selectTgImportBatchByBatchNo(String batchNo);

    public List<TgImportBatch> selectTgImportBatchList(TgImportBatch batch);

    public int insertTgImportBatch(TgImportBatch batch);

    public int updateTgImportBatch(TgImportBatch batch);

    public int deleteTgImportBatchById(Integer id);

    public int deleteTgImportBatchByIds(Integer[] ids);

    public void refreshAllBatchStats();
}
