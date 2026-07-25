package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TgContactImportFiltered;

public interface ITgContactImportFilteredService
{
    public List<TgContactImportFiltered> selectByBatchNo(String batchNo, String filterType);

    public int batchInsert(List<TgContactImportFiltered> list);

    public int deleteByBatchNo(String batchNo);
}
