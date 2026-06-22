package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Telegram账号配置对象 tg_account_config
 *
 * @author ruoyi
 */
public class TgAccountConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Integer id;

    /** Telegram用户ID */
    private Long tgUserId;

    /** Telegram昵称(firstName+lastName) */
    private String nickname;

    /** Telegram用户名 */
    private String username;

    /** 自定义用户名 */
    private String customUsername;

    /** 通知标识 */
    private Integer noticeFlag;

    /** 手机号 */
    private String phoneNum;

    /** 登录状态（0=待登录 1=已登录 -1=异常） */
    private Integer loginStatus;

    /** Telegram API ID */
    private Integer apiId;

    /** Telegram API Hash */
    private String apiHash;

    /** 设备型号（如 Samsung Galaxy S24, iPhone 15 Pro） */
    private String deviceModel;

    /** 系统版本（如 Android 14, iOS 17.4） */
    private String systemVersion;

    /** 应用版本（如 10.8.3） */
    private String appVersion;

    /** 系统语言代码（如 zh-Hans, en-US） */
    private String systemLanguageCode;

    /** 二级密码（两步验证密码） */
    private String twoFaPassword;

    /** 最后在线时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastOnlineTime;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Long getTgUserId()
    {
        return tgUserId;
    }

    public void setTgUserId(Long tgUserId)
    {
        this.tgUserId = tgUserId;
    }

    public String getNickname()
    {
        return nickname;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getCustomUsername()
    {
        return customUsername;
    }

    public void setCustomUsername(String customUsername)
    {
        this.customUsername = customUsername;
    }

    public Integer getNoticeFlag()
    {
        return noticeFlag;
    }

    public void setNoticeFlag(Integer noticeFlag)
    {
        this.noticeFlag = noticeFlag;
    }

    public String getPhoneNum()
    {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum)
    {
        this.phoneNum = phoneNum;
    }

    public Integer getLoginStatus()
    {
        return loginStatus;
    }

    public void setLoginStatus(Integer loginStatus)
    {
        this.loginStatus = loginStatus;
    }

    public Integer getApiId()
    {
        return apiId;
    }

    public void setApiId(Integer apiId)
    {
        this.apiId = apiId;
    }

    public String getApiHash()
    {
        return apiHash;
    }

    public void setApiHash(String apiHash)
    {
        this.apiHash = apiHash;
    }

    public String getDeviceModel()
    {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel)
    {
        this.deviceModel = deviceModel;
    }

    public String getSystemVersion()
    {
        return systemVersion;
    }

    public void setSystemVersion(String systemVersion)
    {
        this.systemVersion = systemVersion;
    }

    public String getAppVersion()
    {
        return appVersion;
    }

    public void setAppVersion(String appVersion)
    {
        this.appVersion = appVersion;
    }

    public String getSystemLanguageCode()
    {
        return systemLanguageCode;
    }

    public void setSystemLanguageCode(String systemLanguageCode)
    {
        this.systemLanguageCode = systemLanguageCode;
    }

    public String getTwoFaPassword()
    {
        return twoFaPassword;
    }

    public void setTwoFaPassword(String twoFaPassword)
    {
        this.twoFaPassword = twoFaPassword;
    }

    public Date getLastOnlineTime()
    {
        return lastOnlineTime;
    }

    public void setLastOnlineTime(Date lastOnlineTime)
    {
        this.lastOnlineTime = lastOnlineTime;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("tgUserId", getTgUserId())
            .append("nickname", getNickname())
            .append("username", getUsername())
            .append("customUsername", getCustomUsername())
            .append("noticeFlag", getNoticeFlag())
            .append("phoneNum", getPhoneNum())
            .append("loginStatus", getLoginStatus())
            .append("apiId", getApiId())
            .append("apiHash", getApiHash())
            .append("deviceModel", getDeviceModel())
            .append("systemVersion", getSystemVersion())
            .append("appVersion", getAppVersion())
            .append("systemLanguageCode", getSystemLanguageCode())
            .append("twoFaPassword", getTwoFaPassword())
            .append("lastOnlineTime", getLastOnlineTime())
            .toString();
    }
}
