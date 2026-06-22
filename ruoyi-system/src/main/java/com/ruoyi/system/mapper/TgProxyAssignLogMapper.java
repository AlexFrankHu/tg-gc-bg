package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TgProxyAssignLog;

public interface TgProxyAssignLogMapper
{
    public List<TgProxyAssignLog> selectList(TgProxyAssignLog log);
    public int insert(TgProxyAssignLog log);
}
