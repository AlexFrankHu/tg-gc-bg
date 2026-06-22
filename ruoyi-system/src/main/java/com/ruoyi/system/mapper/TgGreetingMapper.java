package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TgGreeting;

public interface TgGreetingMapper
{
    public TgGreeting selectById(Integer id);

    public List<TgGreeting> selectList(TgGreeting greeting);

    public int insert(TgGreeting greeting);

    public int update(TgGreeting greeting);

    public int deleteByIds(Integer[] ids);
}
