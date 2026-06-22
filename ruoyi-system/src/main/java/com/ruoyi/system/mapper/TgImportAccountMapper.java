package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TgImportAccount;
import org.apache.ibatis.annotations.Param;

/**
 * 账号导入明细Mapper接口
 */
public interface TgImportAccountMapper
{
    public TgImportAccount selectTgImportAccountById(Integer id);

    public List<TgImportAccount> selectTgImportAccountList(TgImportAccount account);

    public List<TgImportAccount> selectTgImportAccountByBatchNo(String batchNo);

    public int insertTgImportAccount(TgImportAccount account);

    public int batchInsertTgImportAccount(List<TgImportAccount> list);

    public int updateTgImportAccount(TgImportAccount account);

    public int deleteTgImportAccountById(Integer id);

    public int deleteTgImportAccountByBatchNo(String batchNo);

    public int syncStatusFromTelethonAccount(@Param("batchNo") String batchNo);
}
