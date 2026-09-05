package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Telethon账号管理对象 tg_telethon_account
 */
public class TgTelethonAccount extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Integer id;

    /** 手机号 */
    private String phone;

    /** Telegram API ID */
    private Integer apiId;

    /** Telegram API Hash */
    private String apiHash;

    /** Telegram用户ID */
    private Long tgUserId;

    /** 昵称(firstName + lastName) */
    private String nickname;

    /** 2FA密码 */
    private String twofaPassword;

    /** 用户名 */
    private String username;

    /** 账号国家 */
    private String country;

    /** 设备型号 */
    private String deviceModel;

    /** 系统版本 */
    private String systemVersion;

    /** 应用版本 */
    private String appVersion;

    /** 语言代码 */
    private String langCode;

    /** 系统语言代码 */
    private String systemLangCode;

    /** 导入批次号 */
    private String batchNo;

    /** 所属账号分组ID */
    private Integer groupId;

    /** 所属账号分组名称(关联查询) */
    private String groupName;

    /** 状态: online-在线, offline-下线, banned-已被注销, waiting-等待登录 */
    private String status;

    /** 代理IP ID */
    private Integer proxyIpId;

    /** 代理IP组编号 */
    private String proxyGroupNo;

    /** 代理URL */
    private String proxyUrl;

    /** 代理协议 */
    private String proxyProtocol;

    /** 代理主机 */
    private String proxyHost;

    /** 代理端口 */
    private Integer proxyPort;

    /** 代理用户名 */
    private String proxyUsername;

    /** 代理密码 */
    private String proxyPassword;

    /** 代理IP组标题(关联查询) */
    private String proxyGroupTitle;

    /** 是否开启自动回复 */
    private Boolean autoReply;

    /** 是否被限制 0-否 1-是 */
    private Boolean isRestricted;

    /** 是否被TG冻结 0-否 1-是 (冻结账号一定同时被限制) */
    private Boolean isFrozen;

    /** 消息总数 */
    private Integer totalMsgCount;

    /** 发送总数 */
    private Integer sentMsgCount;

    /** 接收总数 */
    private Integer recvMsgCount;

    /** 账号所属节点ID */
    private String nodeId;

    /** JSON文件内容 */
    private String jsonContent;

    /** Session文件内容(二进制) */
    private byte[] sessionContent;

    /** 是否已删除 0-否 1-是 */
    private Integer isDeleted;

    /** 批次标题(关联查询) */
    private String batchTitle;

    /** 最后登录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Integer getApiId() { return apiId; }
    public void setApiId(Integer apiId) { this.apiId = apiId; }

    public String getApiHash() { return apiHash; }
    public void setApiHash(String apiHash) { this.apiHash = apiHash; }

    public Long getTgUserId() { return tgUserId; }
    public void setTgUserId(Long tgUserId) { this.tgUserId = tgUserId; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getTwofaPassword() { return twofaPassword; }
    public void setTwofaPassword(String twofaPassword) { this.twofaPassword = twofaPassword; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getDeviceModel() { return deviceModel; }
    public void setDeviceModel(String deviceModel) { this.deviceModel = deviceModel; }

    public String getSystemVersion() { return systemVersion; }
    public void setSystemVersion(String systemVersion) { this.systemVersion = systemVersion; }

    public String getAppVersion() { return appVersion; }
    public void setAppVersion(String appVersion) { this.appVersion = appVersion; }

    public String getLangCode() { return langCode; }
    public void setLangCode(String langCode) { this.langCode = langCode; }

    public String getSystemLangCode() { return systemLangCode; }
    public void setSystemLangCode(String systemLangCode) { this.systemLangCode = systemLangCode; }

    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }

    public Integer getGroupId() { return groupId; }
    public void setGroupId(Integer groupId) { this.groupId = groupId; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getProxyIpId() { return proxyIpId; }
    public void setProxyIpId(Integer proxyIpId) { this.proxyIpId = proxyIpId; }

    public String getProxyGroupNo() { return proxyGroupNo; }
    public void setProxyGroupNo(String proxyGroupNo) { this.proxyGroupNo = proxyGroupNo; }

    public String getProxyUrl() { return proxyUrl; }
    public void setProxyUrl(String proxyUrl) { this.proxyUrl = proxyUrl; }

    public String getProxyProtocol() { return proxyProtocol; }
    public void setProxyProtocol(String proxyProtocol) { this.proxyProtocol = proxyProtocol; }

    public String getProxyHost() { return proxyHost; }
    public void setProxyHost(String proxyHost) { this.proxyHost = proxyHost; }

    public Integer getProxyPort() { return proxyPort; }
    public void setProxyPort(Integer proxyPort) { this.proxyPort = proxyPort; }

    public String getProxyUsername() { return proxyUsername; }
    public void setProxyUsername(String proxyUsername) { this.proxyUsername = proxyUsername; }

    public String getProxyPassword() { return proxyPassword; }
    public void setProxyPassword(String proxyPassword) { this.proxyPassword = proxyPassword; }

    public String getProxyGroupTitle() { return proxyGroupTitle; }
    public void setProxyGroupTitle(String proxyGroupTitle) { this.proxyGroupTitle = proxyGroupTitle; }

    public Boolean getAutoReply() { return autoReply; }
    public void setAutoReply(Boolean autoReply) { this.autoReply = autoReply; }

    public Boolean getIsRestricted() { return isRestricted; }
    public void setIsRestricted(Boolean isRestricted) { this.isRestricted = isRestricted; }

    public Boolean getIsFrozen() { return isFrozen; }
    public void setIsFrozen(Boolean isFrozen) { this.isFrozen = isFrozen; }

    public Integer getTotalMsgCount() { return totalMsgCount; }
    public void setTotalMsgCount(Integer totalMsgCount) { this.totalMsgCount = totalMsgCount; }

    public Integer getSentMsgCount() { return sentMsgCount; }
    public void setSentMsgCount(Integer sentMsgCount) { this.sentMsgCount = sentMsgCount; }

    public Integer getRecvMsgCount() { return recvMsgCount; }
    public void setRecvMsgCount(Integer recvMsgCount) { this.recvMsgCount = recvMsgCount; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }

    public String getBatchTitle() { return batchTitle; }
    public void setBatchTitle(String batchTitle) { this.batchTitle = batchTitle; }

    public Date getLastLoginTime() { return lastLoginTime; }
    public void setLastLoginTime(Date lastLoginTime) { this.lastLoginTime = lastLoginTime; }

    @Override
    public Date getCreateTime() { return createTime; }

    @Override
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public String getJsonContent() { return jsonContent; }
    public void setJsonContent(String jsonContent) { this.jsonContent = jsonContent; }

    public byte[] getSessionContent() { return sessionContent; }
    public void setSessionContent(byte[] sessionContent) { this.sessionContent = sessionContent; }
}
