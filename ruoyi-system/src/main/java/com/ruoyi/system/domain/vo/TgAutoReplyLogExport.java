package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

import java.util.Date;

/**
 * 自动回复日志导出VO
 */
public class TgAutoReplyLogExport
{
    @Excel(name = "记录ID")
    private Long id;

    @Excel(name = "账号")
    private String accountPhone;

    @Excel(name = "账号昵称")
    private String accountNickname;

    @Excel(name = "好友昵称")
    private String friendNickname;

    @Excel(name = "好友号码")
    private String friendPhone;

    @Excel(name = "触发方式")
    private String triggerTypeLabel;

    @Excel(name = "State")
    private Integer state;

    @Excel(name = "回复内容", width = 40)
    private String replyContent;

    @Excel(name = "发送结果")
    private String sendResultLabel;

    @Excel(name = "错误原因", width = 40)
    private String errorReason;

    @Excel(name = "节点ID")
    private String nodeId;

    @Excel(name = "时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAccountPhone() { return accountPhone; }
    public void setAccountPhone(String accountPhone) { this.accountPhone = accountPhone; }
    public String getAccountNickname() { return accountNickname; }
    public void setAccountNickname(String accountNickname) { this.accountNickname = accountNickname; }
    public String getFriendNickname() { return friendNickname; }
    public void setFriendNickname(String friendNickname) { this.friendNickname = friendNickname; }
    public String getFriendPhone() { return friendPhone; }
    public void setFriendPhone(String friendPhone) { this.friendPhone = friendPhone; }
    public String getTriggerTypeLabel() { return triggerTypeLabel; }
    public void setTriggerTypeLabel(String triggerTypeLabel) { this.triggerTypeLabel = triggerTypeLabel; }
    public Integer getState() { return state; }
    public void setState(Integer state) { this.state = state; }
    public String getReplyContent() { return replyContent; }
    public void setReplyContent(String replyContent) { this.replyContent = replyContent; }
    public String getSendResultLabel() { return sendResultLabel; }
    public void setSendResultLabel(String sendResultLabel) { this.sendResultLabel = sendResultLabel; }
    public String getErrorReason() { return errorReason; }
    public void setErrorReason(String errorReason) { this.errorReason = errorReason; }
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
