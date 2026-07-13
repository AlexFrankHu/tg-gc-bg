package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

import java.util.Date;

/**
 * 账号管理导出VO
 */
public class TgTelethonAccountExport
{
    @Excel(name = "ID")
    private Integer id;

    @Excel(name = "手机号")
    private String phone;

    @Excel(name = "昵称")
    private String nickname;

    @Excel(name = "用户名")
    private String username;

    @Excel(name = "TG用户ID")
    private Long tgUserId;

    @Excel(name = "国家")
    private String country;

    @Excel(name = "状态")
    private String status;

    @Excel(name = "批次")
    private String batchTitle;

    @Excel(name = "分组")
    private String groupName;

    @Excel(name = "代理IP组")
    private String proxyGroupTitle;

    @Excel(name = "自动回复")
    private String autoReply;

    @Excel(name = "是否限制")
    private String isRestricted;

    @Excel(name = "消息总数")
    private Integer totalMsgCount;

    @Excel(name = "发送总数")
    private Integer sentMsgCount;

    @Excel(name = "接收总数")
    private Integer recvMsgCount;

    @Excel(name = "节点ID")
    private String nodeId;

    @Excel(name = "最后登录时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;

    @Excel(name = "创建时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Long getTgUserId() { return tgUserId; }
    public void setTgUserId(Long tgUserId) { this.tgUserId = tgUserId; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getBatchTitle() { return batchTitle; }
    public void setBatchTitle(String batchTitle) { this.batchTitle = batchTitle; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public String getProxyGroupTitle() { return proxyGroupTitle; }
    public void setProxyGroupTitle(String proxyGroupTitle) { this.proxyGroupTitle = proxyGroupTitle; }
    public String getAutoReply() { return autoReply; }
    public void setAutoReply(String autoReply) { this.autoReply = autoReply; }
    public String getIsRestricted() { return isRestricted; }
    public void setIsRestricted(String isRestricted) { this.isRestricted = isRestricted; }
    public Integer getTotalMsgCount() { return totalMsgCount; }
    public void setTotalMsgCount(Integer totalMsgCount) { this.totalMsgCount = totalMsgCount; }
    public Integer getSentMsgCount() { return sentMsgCount; }
    public void setSentMsgCount(Integer sentMsgCount) { this.sentMsgCount = sentMsgCount; }
    public Integer getRecvMsgCount() { return recvMsgCount; }
    public void setRecvMsgCount(Integer recvMsgCount) { this.recvMsgCount = recvMsgCount; }
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public Date getLastLoginTime() { return lastLoginTime; }
    public void setLastLoginTime(Date lastLoginTime) { this.lastLoginTime = lastLoginTime; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
