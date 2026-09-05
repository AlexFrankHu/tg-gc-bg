package com.ruoyi.web.controller.tg;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.TgAccountTask;
import com.ruoyi.system.service.ITgAccountTaskService;

/**
 * 账号任务(修改昵称/头像/2FA)记录Controller
 */
@RestController
@RequestMapping("/tg/accountTask")
public class TgAccountTaskController extends BaseController
{
    @Autowired
    private ITgAccountTaskService service;

    @PreAuthorize("@ss.hasPermi('tg:accountTask:list')")
    @GetMapping("/list")
    public TableDataInfo list(TgAccountTask query)
    {
        startPage();
        List<TgAccountTask> list = service.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('tg:accountTask:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(service.deleteByIds(ids));
    }

    /** 清除已完成(成功/失败)的任务记录 */
    @PreAuthorize("@ss.hasPermi('tg:accountTask:remove')")
    @DeleteMapping("/clearFinished")
    public AjaxResult clearFinished()
    {
        return success("已清除 " + service.deleteFinished() + " 条记录");
    }
}
