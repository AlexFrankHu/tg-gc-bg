package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

import java.util.Date;

/**
 * 发送失败日志导出VO
 */
public class TgSendFailLogExport
{
    @Excel(name = "记录ID")
    private Integer id;

    @Excel(name = "账号")
    private String phone;

    @Excel(name = "账号昵称")
    private String nickname;

    @Excel(name = "好友昵称")
    private String friendNickname;

    @Excel(name = "好友号码")
    private String friendPhone;

    @Excel(name = "消息类型")
    private String contentType;

    @Excel(name = "发送内容", width = 40)
    private String content;

    @Excel(name = "失败原因", width = 40)
    private String errorReason;

    @Excel(name = "节点ID")
    private String nodeId;

    @Excel(name = "发送时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
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
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public Date getSendTime() { return sendTime; }
    public void setSendTime(Date sendTime) { this.sendTime = sendTime; }
}
