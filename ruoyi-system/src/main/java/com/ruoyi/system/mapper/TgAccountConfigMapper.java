package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TgAccountConfig;

/**
 * Telegram账号配置Mapper接口
 *
 * @author ruoyi
 */
public interface TgAccountConfigMapper
{
    /**
     * 查询Telegram账号配置
     *
     * @param id 账号ID
     * @return Telegram账号配置
     */
    public TgAccountConfig selectTgAccountConfigById(Integer id);

    /**
     * 查询Telegram账号配置列表
     *
     * @param tgAccountConfig Telegram账号配置
     * @return Telegram账号配置集合
     */
    public List<TgAccountConfig> selectTgAccountConfigList(TgAccountConfig tgAccountConfig);

    /**
     * 新增Telegram账号配置
     *
     * @param tgAccountConfig Telegram账号配置
     * @return 结果
     */
    public int insertTgAccountConfig(TgAccountConfig tgAccountConfig);

    /**
     * 修改Telegram账号配置
     *
     * @param tgAccountConfig Telegram账号配置
     * @return 结果
     */
    public int updateTgAccountConfig(TgAccountConfig tgAccountConfig);

    /**
     * 删除Telegram账号配置
     *
     * @param id 账号ID
     * @return 结果
     */
    public int deleteTgAccountConfigById(Integer id);

    /**
     * 批量删除Telegram账号配置
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteTgAccountConfigByIds(Integer[] ids);
}
