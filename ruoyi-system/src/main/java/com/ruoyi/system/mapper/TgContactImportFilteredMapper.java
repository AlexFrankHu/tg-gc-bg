package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.TgContactImportFiltered;

public interface TgContactImportFilteredMapper
{
    public List<TgContactImportFiltered> selectByBatchNo(@Param("batchNo") String batchNo, @Param("filterType") String filterType);

    public int batchInsert(List<TgContactImportFiltered> list);

    public int deleteByBatchNo(String batchNo);
}
