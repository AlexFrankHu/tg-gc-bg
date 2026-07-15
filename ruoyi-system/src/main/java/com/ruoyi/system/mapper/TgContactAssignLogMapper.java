package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.TgContactAssignLog;

public interface TgContactAssignLogMapper
{
    public List<TgContactAssignLog> selectList(TgContactAssignLog log);
    public int insert(TgContactAssignLog log);
    public int batchInsert(@Param("list") List<TgContactAssignLog> list);
    public List<String> selectAssignedContactPhones();
    public List<String> selectAssignedContactUsernames();
}
