package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.vo.TgReplyStat;

/**
 * 回复率统计Service
 */
public interface ITgReplyStatService
{
    public List<TgReplyStat> selectReplyStat(String startDate);
}
