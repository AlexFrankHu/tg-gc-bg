package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TgProxyIp;

public interface ITgProxyIpService
{
    public List<TgProxyIp> selectTgProxyIpList(TgProxyIp proxyIp);

    public TgProxyIp selectTgProxyIpById(Integer id);

    public int insertTgProxyIp(TgProxyIp proxyIp);

    public int batchInsertTgProxyIp(List<TgProxyIp> list);

    public int updateTgProxyIp(TgProxyIp proxyIp);

    public int deleteTgProxyIpByIds(Integer[] ids);
}
