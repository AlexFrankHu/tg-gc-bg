package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.TgTelethonAccount;
import com.ruoyi.system.mapper.TgTelethonAccountMapper;
import com.ruoyi.system.service.ITgTelethonAccountService;

/**
 * Telethon账号管理Service实现
 */
@Service
public class TgTelethonAccountServiceImpl implements ITgTelethonAccountService
{
    @Autowired
    private TgTelethonAccountMapper tgTelethonAccountMapper;

    @Override
    public TgTelethonAccount selectTgTelethonAccountById(Integer id)
    {
        return tgTelethonAccountMapper.selectTgTelethonAccountById(id);
    }

    @Override
    public List<TgTelethonAccount> selectTgTelethonAccountList(TgTelethonAccount account)
    {
        return tgTelethonAccountMapper.selectTgTelethonAccountList(account);
    }

    @Override
    public int updateTgTelethonAccount(TgTelethonAccount account)
    {
        return tgTelethonAccountMapper.updateTgTelethonAccount(account);
    }

    @Override
    public int deleteTgTelethonAccountByIds(Integer[] ids)
    {
        return tgTelethonAccountMapper.deleteTgTelethonAccountByIds(ids);
    }

    @Override
    public int insertWaitingAccount(String phone, String batchNo)
    {
        return tgTelethonAccountMapper.insertWaitingAccount(phone, batchNo);
    }

    @Override
    public int updateAccountProxy(TgTelethonAccount account)
    {
        return tgTelethonAccountMapper.updateAccountProxy(account);
    }

    @Override
    public int updateAutoReplyById(Integer id, Boolean autoReply)
    {
        return tgTelethonAccountMapper.updateAutoReplyById(id, autoReply);
    }

    @Override
    public int updateAllAutoReply(Boolean autoReply)
    {
        return tgTelethonAccountMapper.updateAllAutoReply(autoReply);
    }
}
