package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TgContactImportBatchMapper;
import com.ruoyi.system.domain.TgContactImportBatch;
import com.ruoyi.system.service.ITgContactImportBatchService;

@Service
public class TgContactImportBatchServiceImpl implements ITgContactImportBatchService
{
    @Autowired
    private TgContactImportBatchMapper mapper;

    @Override
    public TgContactImportBatch selectById(Integer id) {
        return mapper.selectById(id);
    }

    @Override
    public List<TgContactImportBatch> selectList(TgContactImportBatch batch) {
        return mapper.selectList(batch);
    }

    @Override
    public int insert(TgContactImportBatch batch) {
        return mapper.insert(batch);
    }

    @Override
    public int update(TgContactImportBatch batch) {
        return mapper.update(batch);
    }

    @Override
    public int deleteByIds(Integer[] ids) {
        return mapper.deleteByIds(ids);
    }
}
