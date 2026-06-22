package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TgImportAccount;

/**
 * 账号导入明细Service接口
 */
public interface ITgImportAccountService
{
    public TgImportAccount selectTgImportAccountById(Integer id);

    public List<TgImportAccount> selectTgImportAccountList(TgImportAccount account);

    public List<TgImportAccount> selectTgImportAccountByBatchNo(String batchNo);

    public int insertTgImportAccount(TgImportAccount account);

    public int batchInsertTgImportAccount(List<TgImportAccount> list);

    public int updateTgImportAccount(TgImportAccount account);

    public int syncStatusFromTelethonAccount(String batchNo);
}
