package com.ruoyi.web.controller.tg;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.TgSystemConfig;
import com.ruoyi.system.service.ITgSystemConfigService;

/**
 * 系统配置Controller
 *
 * 仅支持查询与修改配置值, 不提供新增/删除接口。
 */
@RestController
@RequestMapping("/tg/systemConfig")
public class TgSystemConfigController extends BaseController
{
    @Autowired
    private ITgSystemConfigService systemConfigService;

    /**
     * 查询系统配置列表
     */
    @PreAuthorize("@ss.hasPermi('tg:systemConfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(TgSystemConfig tgSystemConfig)
    {
        startPage();
        List<TgSystemConfig> list = systemConfigService.selectTgSystemConfigList(tgSystemConfig);
        return getDataTable(list);
    }

    /**
     * 获取系统配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('tg:systemConfig:list')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        return success(systemConfigService.selectTgSystemConfigById(id));
    }

    /**
     * 修改配置值(仅更新已存在配置的值, 不能新增/删除配置项)
     */
    @PreAuthorize("@ss.hasPermi('tg:systemConfig:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody TgSystemConfig tgSystemConfig)
    {
        if (tgSystemConfig.getId() == null)
        {
            return error("缺少配置ID, 无法修改");
        }
        TgSystemConfig exist = systemConfigService.selectTgSystemConfigById(tgSystemConfig.getId());
        if (exist == null)
        {
            return error("配置不存在, 不允许新增配置");
        }
        // 只更新配置值, 其余字段(config_key/config_name)不可改
        TgSystemConfig update = new TgSystemConfig();
        update.setId(exist.getId());
        update.setConfigValue(tgSystemConfig.getConfigValue());
        return toAjax(systemConfigService.updateConfigValue(update));
    }
}
