package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.TgAccountConfig;
import com.ruoyi.system.mapper.TgAccountConfigMapper;
import com.ruoyi.system.service.ITgAccountConfigService;

/**
 * Telegram账号配置Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class TgAccountConfigServiceImpl implements ITgAccountConfigService
{
    @Autowired
    private TgAccountConfigMapper tgAccountConfigMapper;

    @Override
    public TgAccountConfig selectTgAccountConfigById(Integer id)
    {
        return tgAccountConfigMapper.selectTgAccountConfigById(id);
    }

    @Override
    public List<TgAccountConfig> selectTgAccountConfigList(TgAccountConfig tgAccountConfig)
    {
        return tgAccountConfigMapper.selectTgAccountConfigList(tgAccountConfig);
    }

    @Override
    public int insertTgAccountConfig(TgAccountConfig tgAccountConfig)
    {
        return tgAccountConfigMapper.insertTgAccountConfig(tgAccountConfig);
    }

    @Override
    public int updateTgAccountConfig(TgAccountConfig tgAccountConfig)
    {
        return tgAccountConfigMapper.updateTgAccountConfig(tgAccountConfig);
    }

    @Override
    public int deleteTgAccountConfigById(Integer id)
    {
        return tgAccountConfigMapper.deleteTgAccountConfigById(id);
    }

    @Override
    public int deleteTgAccountConfigByIds(Integer[] ids)
    {
        return tgAccountConfigMapper.deleteTgAccountConfigByIds(ids);
    }
}
