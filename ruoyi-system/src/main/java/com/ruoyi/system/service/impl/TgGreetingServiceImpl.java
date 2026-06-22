package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TgGreetingMapper;
import com.ruoyi.system.domain.TgGreeting;
import com.ruoyi.system.service.ITgGreetingService;

@Service
public class TgGreetingServiceImpl implements ITgGreetingService
{
    @Autowired
    private TgGreetingMapper mapper;

    @Override
    public TgGreeting selectById(Integer id) {
        return mapper.selectById(id);
    }

    @Override
    public List<TgGreeting> selectList(TgGreeting greeting) {
        return mapper.selectList(greeting);
    }

    @Override
    public int insert(TgGreeting greeting) {
        return mapper.insert(greeting);
    }

    @Override
    public int update(TgGreeting greeting) {
        return mapper.update(greeting);
    }

    @Override
    public int deleteByIds(Integer[] ids) {
        return mapper.deleteByIds(ids);
    }
}
