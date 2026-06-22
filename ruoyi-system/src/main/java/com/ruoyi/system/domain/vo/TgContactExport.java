package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

import java.util.Date;

/**
 * 好友列表导出VO
 */
public class TgContactExport
{
    @Excel(name = "所属账号")
    private String accountPhone;

    @Excel(name = "TG用户ID")
    private Long userId;

    @Excel(name = "昵称")
    private String nickname;

    @Excel(name = "用户名")
    private String username;

    @Excel(name = "手机号")
    private String phoneNumber;

    @Excel(name = "类型")
    private String userType;

    @Excel(name = "互为好友")
    private String isMutualLabel;

    @Excel(name = "自动回复")
    private String autoReplyLabel;

    @Excel(name = "添加时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Excel(name = "最后在线", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastOnlineTime;

    @Excel(name = "最后发送", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastSendTime;

    @Excel(name = "最后接收", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastReceiveTime;

    public String getAccountPhone() { return accountPhone; }
    public void setAccountPhone(String accountPhone) { this.accountPhone = accountPhone; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    public String getIsMutualLabel() { return isMutualLabel; }
    public void setIsMutualLabel(String isMutualLabel) { this.isMutualLabel = isMutualLabel; }
    public String getAutoReplyLabel() { return autoReplyLabel; }
    public void setAutoReplyLabel(String autoReplyLabel) { this.autoReplyLabel = autoReplyLabel; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getLastOnlineTime() { return lastOnlineTime; }
    public void setLastOnlineTime(Date lastOnlineTime) { this.lastOnlineTime = lastOnlineTime; }
    public Date getLastSendTime() { return lastSendTime; }
    public void setLastSendTime(Date lastSendTime) { this.lastSendTime = lastSendTime; }
    public Date getLastReceiveTime() { return lastReceiveTime; }
    public void setLastReceiveTime(Date lastReceiveTime) { this.lastReceiveTime = lastReceiveTime; }
}
