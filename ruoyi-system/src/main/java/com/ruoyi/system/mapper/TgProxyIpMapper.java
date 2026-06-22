package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.TgProxyIp;

public interface TgProxyIpMapper
{
    public List<TgProxyIp> selectTgProxyIpList(TgProxyIp proxyIp);

    public TgProxyIp selectTgProxyIpById(Integer id);

    public int insertTgProxyIp(TgProxyIp proxyIp);

    public int batchInsertTgProxyIp(@Param("list") List<TgProxyIp> list);

    public int updateTgProxyIp(TgProxyIp proxyIp);

    public int deleteTgProxyIpByIds(Integer[] ids);

    public List<TgProxyIp> selectAvailableByGroupNo(@Param("groupNo") String groupNo);
}
