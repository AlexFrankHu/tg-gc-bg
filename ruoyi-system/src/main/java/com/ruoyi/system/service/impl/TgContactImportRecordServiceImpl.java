package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TgContactImportRecordMapper;
import com.ruoyi.system.domain.TgContactImportRecord;
import com.ruoyi.system.service.ITgContactImportRecordService;

@Service
public class TgContactImportRecordServiceImpl implements ITgContactImportRecordService
{
    @Autowired
    private TgContactImportRecordMapper mapper;

    @Override
    public List<TgContactImportRecord> selectByBatchNo(String batchNo) {
        return mapper.selectByBatchNo(batchNo);
    }

    @Override
    public int batchInsert(List<TgContactImportRecord> records) {
        return mapper.batchInsert(records);
    }

    @Override
    public int deleteByBatchNo(String batchNo) {
        return mapper.deleteByBatchNo(batchNo);
    }
}
