package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class TgAutoReplyLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String accountPhone;
    private String accountNickname;
    private Long friendUserId;
    private String friendNickname;
    private String friendPhone;
    private String triggerType;
    private Integer state;
    private String requestParams;
    private String chatContext;
    private String replyContent;
    private String sendResult;
    private String errorReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /** 账号所属节点ID */
    private String nodeId;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAccountPhone() { return accountPhone; }
    public void setAccountPhone(String accountPhone) { this.accountPhone = accountPhone; }
    public String getAccountNickname() { return accountNickname; }
    public void setAccountNickname(String accountNickname) { this.accountNickname = accountNickname; }
    public Long getFriendUserId() { return friendUserId; }
    public void setFriendUserId(Long friendUserId) { this.friendUserId = friendUserId; }
    public String getFriendNickname() { return friendNickname; }
    public void setFriendNickname(String friendNickname) { this.friendNickname = friendNickname; }
    public String getFriendPhone() { return friendPhone; }
    public void setFriendPhone(String friendPhone) { this.friendPhone = friendPhone; }
    public String getTriggerType() { return triggerType; }
    public void setTriggerType(String triggerType) { this.triggerType = triggerType; }
    public Integer getState() { return state; }
    public void setState(Integer state) { this.state = state; }
    public String getRequestParams() { return requestParams; }
    public void setRequestParams(String requestParams) { this.requestParams = requestParams; }
    public String getChatContext() { return chatContext; }
    public void setChatContext(String chatContext) { this.chatContext = chatContext; }
    public String getReplyContent() { return replyContent; }
    public void setReplyContent(String replyContent) { this.replyContent = replyContent; }
    public String getSendResult() { return sendResult; }
    public void setSendResult(String sendResult) { this.sendResult = sendResult; }
    public String getErrorReason() { return errorReason; }
    public void setErrorReason(String errorReason) { this.errorReason = errorReason; }
    @Override
    public Date getCreateTime() { return createTime; }
    @Override
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
}
