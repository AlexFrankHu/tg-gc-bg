package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TgContactImportRecord;

public interface ITgContactImportRecordService
{
    public List<TgContactImportRecord> selectByBatchNo(String batchNo);

    public int batchInsert(List<TgContactImportRecord> records);

    public int deleteByBatchNo(String batchNo);
}
