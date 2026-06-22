package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.TgImportBatch;
import com.ruoyi.system.mapper.TgImportBatchMapper;
import com.ruoyi.system.service.ITgImportBatchService;

/**
 * 账号导入批次Service实现
 */
@Service
public class TgImportBatchServiceImpl implements ITgImportBatchService
{
    @Autowired
    private TgImportBatchMapper tgImportBatchMapper;

    @Override
    public TgImportBatch selectTgImportBatchById(Integer id)
    {
        return tgImportBatchMapper.selectTgImportBatchById(id);
    }

    @Override
    public TgImportBatch selectTgImportBatchByBatchNo(String batchNo)
    {
        return tgImportBatchMapper.selectTgImportBatchByBatchNo(batchNo);
    }

    @Override
    public List<TgImportBatch> selectTgImportBatchList(TgImportBatch batch)
    {
        return tgImportBatchMapper.selectTgImportBatchList(batch);
    }

    @Override
    public int insertTgImportBatch(TgImportBatch batch)
    {
        return tgImportBatchMapper.insertTgImportBatch(batch);
    }

    @Override
    public int updateTgImportBatch(TgImportBatch batch)
    {
        return tgImportBatchMapper.updateTgImportBatch(batch);
    }

    @Override
    public int deleteTgImportBatchByIds(Integer[] ids)
    {
        return tgImportBatchMapper.deleteTgImportBatchByIds(ids);
    }

    @Override
    public void refreshAllBatchStats()
    {
        tgImportBatchMapper.refreshAllBatchStats();
    }
}
