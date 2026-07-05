package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TgSystemConfig;

/**
 * 系统配置Service接口
 */
public interface ITgSystemConfigService
{
    public List<TgSystemConfig> selectTgSystemConfigList(TgSystemConfig tgSystemConfig);

    public TgSystemConfig selectTgSystemConfigById(Integer id);

    /** 仅更新配置值(不允许新增/删除配置项) */
    public int updateConfigValue(TgSystemConfig tgSystemConfig);
}
