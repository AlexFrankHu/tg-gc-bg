package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Telegram好友列表对象 tg_contact
 */
public class TgContact extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer tgAccountId;
    private Long userId;
    private String firstName;
    private String lastName;
    private String nickname;
    private String username;
    private String phoneNumber;
    private Boolean isMutual;
    private Boolean isBot;
    private Boolean isPremium;
    private Boolean isVerified;
    private String userType;
    private String restrictionReason;
    private String bio;
    private Integer photoSmallFileId;
    private Integer photoBigFileId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastOnlineTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastSendTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastReceiveTime;

    private Boolean autoReply;

    /** 是否已注销(对方账号被删除) */
    private Boolean isDeregistered;

    /** 消息总数 */
    private Integer totalMsgCount;

    /** 账号发送数 */
    private Integer accountSentCount;

    /** 好友发送数 */
    private Integer friendSentCount;

    /** 查询用：账号手机号（非数据库字段） */
    private String accountPhone;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    // Getters and Setters
    /** 账号所属节点ID */
    private String nodeId;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getTgAccountId() { return tgAccountId; }
    public void setTgAccountId(Integer tgAccountId) { this.tgAccountId = tgAccountId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public Boolean getIsMutual() { return isMutual; }
    public void setIsMutual(Boolean isMutual) { this.isMutual = isMutual; }
    public Boolean getIsBot() { return isBot; }
    public void setIsBot(Boolean isBot) { this.isBot = isBot; }
    public Boolean getIsPremium() { return isPremium; }
    public void setIsPremium(Boolean isPremium) { this.isPremium = isPremium; }
    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    public String getRestrictionReason() { return restrictionReason; }
    public void setRestrictionReason(String restrictionReason) { this.restrictionReason = restrictionReason; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public Integer getPhotoSmallFileId() { return photoSmallFileId; }
    public void setPhotoSmallFileId(Integer photoSmallFileId) { this.photoSmallFileId = photoSmallFileId; }
    public Integer getPhotoBigFileId() { return photoBigFileId; }
    public void setPhotoBigFileId(Integer photoBigFileId) { this.photoBigFileId = photoBigFileId; }
    public Date getLastOnlineTime() { return lastOnlineTime; }
    public void setLastOnlineTime(Date lastOnlineTime) { this.lastOnlineTime = lastOnlineTime; }
    public Date getLastSendTime() { return lastSendTime; }
    public void setLastSendTime(Date lastSendTime) { this.lastSendTime = lastSendTime; }
    public Date getLastReceiveTime() { return lastReceiveTime; }
    public void setLastReceiveTime(Date lastReceiveTime) { this.lastReceiveTime = lastReceiveTime; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Boolean getAutoReply() { return autoReply; }
    public void setAutoReply(Boolean autoReply) { this.autoReply = autoReply; }
    public Boolean getIsDeregistered() { return isDeregistered; }
    public void setIsDeregistered(Boolean isDeregistered) { this.isDeregistered = isDeregistered; }
    public Integer getTotalMsgCount() { return totalMsgCount; }
    public void setTotalMsgCount(Integer totalMsgCount) { this.totalMsgCount = totalMsgCount; }
    public Integer getAccountSentCount() { return accountSentCount; }
    public void setAccountSentCount(Integer accountSentCount) { this.accountSentCount = accountSentCount; }
    public Integer getFriendSentCount() { return friendSentCount; }
    public void setFriendSentCount(Integer friendSentCount) { this.friendSentCount = friendSentCount; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public String getAccountPhone() { return accountPhone; }
    public void setAccountPhone(String accountPhone) { this.accountPhone = accountPhone; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
}
