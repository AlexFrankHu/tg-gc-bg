package com.ruoyi.web.controller.tg;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.vo.TgReplyStat;
import com.ruoyi.system.service.ITgReplyStatService;

/**
 * 回复率统计Controller
 */
@RestController
@RequestMapping("/tg/replyStat")
public class TgReplyStatController extends BaseController
{
    /** 最多查询最近30天 */
    private static final int MAX_DAYS = 30;
    /** 默认查询最近7天 */
    private static final int DEFAULT_DAYS = 7;

    @Autowired
    private ITgReplyStatService tgReplyStatService;

    @PreAuthorize("@ss.hasPermi('tg:replyStat:list')")
    @GetMapping("/list")
    public AjaxResult list(@RequestParam(value = "startDate", required = false) String startDate)
    {
        LocalDate today = LocalDate.now();
        LocalDate minDate = today.minusDays(MAX_DAYS - 1L);
        LocalDate start;
        if (startDate == null || startDate.trim().isEmpty()) {
            start = today.minusDays(DEFAULT_DAYS - 1L);
        } else {
            String s = startDate.trim();
            if (s.length() > 10) {
                s = s.substring(0, 10);
            }
            try {
                start = LocalDate.parse(s);
            } catch (Exception e) {
                start = today.minusDays(DEFAULT_DAYS - 1L);
            }
        }
        // 限制最多最近30天，且不超过今天
        if (start.isBefore(minDate)) {
            start = minDate;
        }
        if (start.isAfter(today)) {
            start = today;
        }
        List<TgReplyStat> list = tgReplyStatService.selectReplyStat(start.toString());
        return success(list);
    }
}
