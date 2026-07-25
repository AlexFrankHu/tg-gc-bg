package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TgContactImportFiltered;

public interface TgContactImportFilteredMapper
{
    public List<TgContactImportFiltered> selectByBatchNo(String batchNo);

    public int batchInsert(List<TgContactImportFiltered> list);

    public int deleteByBatchNo(String batchNo);
}
