package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TgTelethonAccount;

/**
 * Telethon账号管理Service接口
 */
public interface ITgTelethonAccountService
{
    public TgTelethonAccount selectTgTelethonAccountById(Integer id);

    public List<TgTelethonAccount> selectTgTelethonAccountList(TgTelethonAccount account);

    public int updateTgTelethonAccount(TgTelethonAccount account);

    public int deleteTgTelethonAccountByIds(Integer[] ids);

    public int insertWaitingAccount(String phone, String batchNo);

    public int updateAccountProxy(TgTelethonAccount account);

    public int updateAutoReplyById(Integer id, Boolean autoReply);

    public int updateAllAutoReply(Boolean autoReply);
}
