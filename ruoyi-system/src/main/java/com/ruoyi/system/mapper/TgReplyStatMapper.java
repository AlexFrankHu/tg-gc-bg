package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.vo.TgReplyStat;

/**
 * 回复率统计Mapper
 */
public interface TgReplyStatMapper
{
    /**
     * 按天统计回复率（create_time >= startDate）
     */
    public List<TgReplyStat> selectReplyStat(@Param("startDate") String startDate);
}
