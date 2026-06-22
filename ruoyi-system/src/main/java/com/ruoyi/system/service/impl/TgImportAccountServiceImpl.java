package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.TgImportAccount;
import com.ruoyi.system.mapper.TgImportAccountMapper;
import com.ruoyi.system.service.ITgImportAccountService;

/**
 * 账号导入明细Service实现
 */
@Service
public class TgImportAccountServiceImpl implements ITgImportAccountService
{
    @Autowired
    private TgImportAccountMapper tgImportAccountMapper;

    @Override
    public TgImportAccount selectTgImportAccountById(Integer id)
    {
        return tgImportAccountMapper.selectTgImportAccountById(id);
    }

    @Override
    public List<TgImportAccount> selectTgImportAccountList(TgImportAccount account)
    {
        return tgImportAccountMapper.selectTgImportAccountList(account);
    }

    @Override
    public List<TgImportAccount> selectTgImportAccountByBatchNo(String batchNo)
    {
        return tgImportAccountMapper.selectTgImportAccountByBatchNo(batchNo);
    }

    @Override
    public int insertTgImportAccount(TgImportAccount account)
    {
        return tgImportAccountMapper.insertTgImportAccount(account);
    }

    @Override
    public int batchInsertTgImportAccount(List<TgImportAccount> list)
    {
        return tgImportAccountMapper.batchInsertTgImportAccount(list);
    }

    @Override
    public int updateTgImportAccount(TgImportAccount account)
    {
        return tgImportAccountMapper.updateTgImportAccount(account);
    }

    @Override
    public int syncStatusFromTelethonAccount(String batchNo)
    {
        return tgImportAccountMapper.syncStatusFromTelethonAccount(batchNo);
    }
}
