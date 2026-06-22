package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TgContactImportRecord;

public interface TgContactImportRecordMapper
{
    public List<TgContactImportRecord> selectByBatchNo(String batchNo);

    public int insert(TgContactImportRecord record);

    public int batchInsert(List<TgContactImportRecord> records);

    public int deleteByBatchNo(String batchNo);
}
