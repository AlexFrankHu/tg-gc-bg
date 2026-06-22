package com.ruoyi.web.controller.tg;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.TgGreeting;
import com.ruoyi.system.service.ITgGreetingService;

/**
 * 主动问候语Controller
 */
@RestController
@RequestMapping("/tg/greeting")
public class TgGreetingController extends BaseController
{
    @Autowired
    private ITgGreetingService greetingService;

    /**
     * 查询主动问候语列表
     */
    @PreAuthorize("@ss.hasPermi('tg:greeting:list')")
    @GetMapping("/list")
    public TableDataInfo list(TgGreeting greeting)
    {
        startPage();
        List<TgGreeting> list = greetingService.selectList(greeting);
        return getDataTable(list);
    }

    /**
     * 获取主动问候语详细信息
     */
    @PreAuthorize("@ss.hasPermi('tg:greeting:list')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Integer id)
    {
        return success(greetingService.selectById(id));
    }

    /**
     * 新增主动问候语
     */
    @PreAuthorize("@ss.hasPermi('tg:greeting:add')")
    @PostMapping
    public AjaxResult add(@RequestBody TgGreeting greeting)
    {
        return toAjax(greetingService.insert(greeting));
    }

    /**
     * 修改主动问候语
     */
    @PreAuthorize("@ss.hasPermi('tg:greeting:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody TgGreeting greeting)
    {
        return toAjax(greetingService.update(greeting));
    }

    /**
     * 删除主动问候语
     */
    @PreAuthorize("@ss.hasPermi('tg:greeting:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(greetingService.deleteByIds(ids));
    }
}
