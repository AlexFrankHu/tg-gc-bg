package com.ruoyi.web.controller.tg;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.TgAccountGroup;
import com.ruoyi.system.service.ITgAccountGroupService;

/**
 * 账号分组Controller (仅支持新增与修改, 不提供删除)
 */
@RestController
@RequestMapping("/tg/accountGroup")
public class TgAccountGroupController extends BaseController
{
    @Autowired
    private ITgAccountGroupService accountGroupService;

    /**
     * 查询账号分组列表
     */
    @PreAuthorize("@ss.hasPermi('tg:accountGroup:list')")
    @GetMapping("/list")
    public TableDataInfo list(TgAccountGroup tgAccountGroup)
    {
        startPage();
        List<TgAccountGroup> list = accountGroupService.selectTgAccountGroupList(tgAccountGroup);
        return getDataTable(list);
    }

    /**
     * 查询所有可用分组(用于下拉选择)
     */
    @PreAuthorize("@ss.hasPermi('tg:accountGroup:list')")
    @GetMapping("/enabledList")
    public AjaxResult enabledList()
    {
        TgAccountGroup query = new TgAccountGroup();
        query.setEnabled(1);
        return success(accountGroupService.selectTgAccountGroupList(query));
    }

    /**
     * 获取账号分组详细信息
     */
    @PreAuthorize("@ss.hasPermi('tg:accountGroup:list')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        return success(accountGroupService.selectTgAccountGroupById(id));
    }

    /**
     * 新增账号分组
     */
    @PreAuthorize("@ss.hasPermi('tg:accountGroup:add')")
    @PostMapping
    public AjaxResult add(@RequestBody TgAccountGroup tgAccountGroup)
    {
        if (tgAccountGroup.getGroupName() == null || tgAccountGroup.getGroupName().trim().isEmpty())
        {
            return error("组名称不能为空");
        }
        return toAjax(accountGroupService.insertTgAccountGroup(tgAccountGroup));
    }

    /**
     * 修改账号分组
     */
    @PreAuthorize("@ss.hasPermi('tg:accountGroup:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody TgAccountGroup tgAccountGroup)
    {
        if (tgAccountGroup.getId() == null)
        {
            return error("缺少分组ID, 无法修改");
        }
        return toAjax(accountGroupService.updateTgAccountGroup(tgAccountGroup));
    }
}
