package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.TgTelethonAccount;

/**
 * Telethon账号管理Mapper接口
 */
public interface TgTelethonAccountMapper
{
    public TgTelethonAccount selectTgTelethonAccountById(Integer id);

    public List<TgTelethonAccount> selectTgTelethonAccountList(TgTelethonAccount account);

    public int updateTgTelethonAccount(TgTelethonAccount account);

    public int deleteTgTelethonAccountById(Integer id);

    public int deleteTgTelethonAccountByIds(Integer[] ids);

    public int insertWaitingAccount(@Param("phone") String phone, @Param("batchNo") String batchNo);

    public int updateAccountProxy(TgTelethonAccount account);

    public int updateAutoReplyById(@Param("id") Integer id, @Param("autoReply") Boolean autoReply);

    public int updateAllAutoReply(@Param("autoReply") Boolean autoReply);
}
