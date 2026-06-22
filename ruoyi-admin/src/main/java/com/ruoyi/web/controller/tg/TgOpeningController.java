package com.ruoyi.web.controller.tg;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.TgOpening;
import com.ruoyi.system.service.ITgOpeningService;

/**
 * 主动开场白Controller
 */
@RestController
@RequestMapping("/tg/opening")
public class TgOpeningController extends BaseController
{
    @Autowired
    private ITgOpeningService openingService;

    /**
     * 查询主动开场白列表
     */
    @PreAuthorize("@ss.hasPermi('tg:opening:list')")
    @GetMapping("/list")
    public TableDataInfo list(TgOpening opening)
    {
        startPage();
        List<TgOpening> list = openingService.selectList(opening);
        return getDataTable(list);
    }

    /**
     * 获取主动开场白详细信息
     */
    @PreAuthorize("@ss.hasPermi('tg:opening:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Integer id)
    {
        return success(openingService.selectById(id));
    }

    /**
     * 新增主动开场白
     */
    @PreAuthorize("@ss.hasPermi('tg:opening:add')")
    @PostMapping
    public AjaxResult add(@RequestBody TgOpening opening)
    {
        return toAjax(openingService.insert(opening));
    }

    /**
     * 修改主动开场白
     */
    @PreAuthorize("@ss.hasPermi('tg:opening:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody TgOpening opening)
    {
        return toAjax(openingService.update(opening));
    }

    /**
     * 删除主动开场白
     */
    @PreAuthorize("@ss.hasPermi('tg:opening:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(openingService.deleteByIds(ids));
    }
}
