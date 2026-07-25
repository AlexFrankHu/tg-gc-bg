package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.TgContactImportFiltered;
import com.ruoyi.system.mapper.TgContactImportFilteredMapper;
import com.ruoyi.system.service.ITgContactImportFilteredService;

@Service
public class TgContactImportFilteredServiceImpl implements ITgContactImportFilteredService
{
    @Autowired
    private TgContactImportFilteredMapper mapper;

    @Override
    public List<TgContactImportFiltered> selectByBatchNo(String batchNo, String filterType)
    {
        return mapper.selectByBatchNo(batchNo, filterType);
    }

    @Override
    public int batchInsert(List<TgContactImportFiltered> list)
    {
        return mapper.batchInsert(list);
    }

    @Override
    public int deleteByBatchNo(String batchNo)
    {
        return mapper.deleteByBatchNo(batchNo);
    }
}
