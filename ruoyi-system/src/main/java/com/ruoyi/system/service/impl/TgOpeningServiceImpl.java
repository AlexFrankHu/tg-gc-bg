package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TgOpeningMapper;
import com.ruoyi.system.domain.TgOpening;
import com.ruoyi.system.service.ITgOpeningService;

@Service
public class TgOpeningServiceImpl implements ITgOpeningService
{
    @Autowired
    private TgOpeningMapper mapper;

    @Override
    public TgOpening selectById(Integer id) {
        return mapper.selectById(id);
    }

    @Override
    public List<TgOpening> selectList(TgOpening opening) {
        return mapper.selectList(opening);
    }

    @Override
    public int insert(TgOpening opening) {
        return mapper.insert(opening);
    }

    @Override
    public int update(TgOpening opening) {
        return mapper.update(opening);
    }

    @Override
    public int deleteByIds(Integer[] ids) {
        return mapper.deleteByIds(ids);
    }
}
