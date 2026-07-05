package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TgSystemConfigMapper;
import com.ruoyi.system.domain.TgSystemConfig;
import com.ruoyi.system.service.ITgSystemConfigService;

/**
 * 系统配置Service业务层处理
 */
@Service
public class TgSystemConfigServiceImpl implements ITgSystemConfigService
{
    @Autowired
    private TgSystemConfigMapper tgSystemConfigMapper;

    @Override
    public List<TgSystemConfig> selectTgSystemConfigList(TgSystemConfig tgSystemConfig)
    {
        return tgSystemConfigMapper.selectTgSystemConfigList(tgSystemConfig);
    }

    @Override
    public TgSystemConfig selectTgSystemConfigById(Integer id)
    {
        return tgSystemConfigMapper.selectTgSystemConfigById(id);
    }

    @Override
    public int updateConfigValue(TgSystemConfig tgSystemConfig)
    {
        return tgSystemConfigMapper.updateConfigValue(tgSystemConfig);
    }
}
