package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TgContactAssignLog;

public interface TgContactAssignLogMapper
{
    public List<TgContactAssignLog> selectList(TgContactAssignLog log);
    public int insert(TgContactAssignLog log);
}
