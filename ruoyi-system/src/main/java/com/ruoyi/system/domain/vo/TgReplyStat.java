package com.ruoyi.system.domain.vo;

import java.math.BigDecimal;

/**
 * 回复率统计VO（按天聚合）
 */
public class TgReplyStat
{
    /** 统计日期 */
    private String statDate;

    /** 添加人数 */
    private Long totalAddCount;

    /** 发送人数 */
    private Long totalSendCount;

    /** 添加发送率 = 发送人数/添加人数 */
    private BigDecimal sendRatio;

    /** 回复人数 */
    private Long sendReplyCount;

    /** 发送回复率 = 回复人数/发送人数 */
    private BigDecimal sendReplyRatio;

    /** 添加回复率 = 回复人数/添加人数 */
    private BigDecimal addReplyRatio;

    public String getStatDate() { return statDate; }
    public void setStatDate(String statDate) { this.statDate = statDate; }

    public Long getTotalAddCount() { return totalAddCount; }
    public void setTotalAddCount(Long totalAddCount) { this.totalAddCount = totalAddCount; }

    public Long getTotalSendCount() { return totalSendCount; }
    public void setTotalSendCount(Long totalSendCount) { this.totalSendCount = totalSendCount; }

    public BigDecimal getSendRatio() { return sendRatio; }
    public void setSendRatio(BigDecimal sendRatio) { this.sendRatio = sendRatio; }

    public Long getSendReplyCount() { return sendReplyCount; }
    public void setSendReplyCount(Long sendReplyCount) { this.sendReplyCount = sendReplyCount; }

    public BigDecimal getSendReplyRatio() { return sendReplyRatio; }
    public void setSendReplyRatio(BigDecimal sendReplyRatio) { this.sendReplyRatio = sendReplyRatio; }

    public BigDecimal getAddReplyRatio() { return addReplyRatio; }
    public void setAddReplyRatio(BigDecimal addReplyRatio) { this.addReplyRatio = addReplyRatio; }
}
