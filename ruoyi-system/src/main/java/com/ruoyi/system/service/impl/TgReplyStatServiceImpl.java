package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.vo.TgReplyStat;
import com.ruoyi.system.mapper.TgReplyStatMapper;
import com.ruoyi.system.service.ITgReplyStatService;

/**
 * 回复率统计Service实现
 */
@Service
public class TgReplyStatServiceImpl implements ITgReplyStatService
{
    @Autowired
    private TgReplyStatMapper tgReplyStatMapper;

    @Override
    public List<TgReplyStat> selectReplyStat(String startDate)
    {
        return tgReplyStatMapper.selectReplyStat(startDate);
    }
}
