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
    public int insertWaitingAccount(TgTelethonAccount account)
    {
        return tgTelethonAccountMapper.insertWaitingAccount(account);
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
    public int updateIsRestrictedById(Integer id, Integer isRestricted)
    {
        return tgTelethonAccountMapper.updateIsRestrictedById(id, isRestricted);
    }

    @Override
    public int batchUpdateIsRestricted(List<Integer> ids, Integer isRestricted)
    {
        return tgTelethonAccountMapper.batchUpdateIsRestricted(ids, isRestricted);
    }

    @Override
    public int batchUpdateGroupId(List<Integer> ids, Integer groupId)
    {
        return tgTelethonAccountMapper.batchUpdateGroupId(ids, groupId);
    }

    @Override
    public int updateAllAutoReply(Boolean autoReply)
    {
        return tgTelethonAccountMapper.updateAllAutoReply(autoReply);
    }

    @Override
    public List<TgTelethonAccount> selectUnassignedAccounts()
    {
        return tgTelethonAccountMapper.selectUnassignedAccounts();
    }

    @Override
    public int updateNodeId(Integer id, String nodeId)
    {
        return tgTelethonAccountMapper.batchUpdateNodeId(id, nodeId);
    }

    @Override
    public int updateStatusById(Integer id, String status)
    {
        return tgTelethonAccountMapper.updateStatusById(id, status);
    }

    @Override
    public int batchUpdateStatus(List<Integer> ids, String status)
    {
        return tgTelethonAccountMapper.batchUpdateStatus(ids, status);
    }
}
