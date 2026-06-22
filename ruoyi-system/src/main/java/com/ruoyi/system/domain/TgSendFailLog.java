package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class TgSendFailLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String phone;
    private Integer tgAccountId;
    private String nickname;
    private Long userId;
    private String friendNickname;
    private String friendPhone;
    private String contentType;
    private String content;
    private String errorReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /** 账号所属节点ID */
    private String nodeId;


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Integer getTgAccountId() { return tgAccountId; }
    public void setTgAccountId(Integer tgAccountId) { this.tgAccountId = tgAccountId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getFriendNickname() { return friendNickname; }
    public void setFriendNickname(String friendNickname) { this.friendNickname = friendNickname; }
    public String getFriendPhone() { return friendPhone; }
    public void setFriendPhone(String friendPhone) { this.friendPhone = friendPhone; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getErrorReason() { return errorReason; }
    public void setErrorReason(String errorReason) { this.errorReason = errorReason; }
    public Date getSendTime() { return sendTime; }
    public void setSendTime(Date sendTime) { this.sendTime = sendTime; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
}
