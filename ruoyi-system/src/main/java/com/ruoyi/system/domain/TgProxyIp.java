package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * IP代理详情对象 tg_proxy_ip
 */
public class TgProxyIp extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Integer id;

    /** 归属IP组 */
    private String groupNo;

    /** 协议: socks5/socks4/http */
    private String protocol;

    /** 代理主机 */
    private String host;

    /** 代理端口 */
    private Integer port;

    /** 认证用户名 */
    private String username;

    /** 认证密码 */
    private String password;

    /** 完整代理URL */
    private String proxyUrl;

    /** 最大绑定账号数 */
    private Integer maxBindable;

    /** 当前绑定账号数 */
    private Integer currentBindCount;

    /** 历史绑定账号数 */
    private Integer historyBindCount;

    /** 状态: active/expired/disabled */
    private String status;

    /** 组标题(关联查询) */
    private String groupTitle;

    /** 绑定账号手机号(关联查询) */
    private String accountPhone;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getGroupNo() { return groupNo; }
    public void setGroupNo(String groupNo) { this.groupNo = groupNo; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public Integer getPort() { return port; }
    public void setPort(Integer port) { this.port = port; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getProxyUrl() { return proxyUrl; }
    public void setProxyUrl(String proxyUrl) { this.proxyUrl = proxyUrl; }

    public Integer getMaxBindable() { return maxBindable; }
    public void setMaxBindable(Integer maxBindable) { this.maxBindable = maxBindable; }

    public Integer getCurrentBindCount() { return currentBindCount; }
    public void setCurrentBindCount(Integer currentBindCount) { this.currentBindCount = currentBindCount; }

    public Integer getHistoryBindCount() { return historyBindCount; }
    public void setHistoryBindCount(Integer historyBindCount) { this.historyBindCount = historyBindCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getGroupTitle() { return groupTitle; }
    public void setGroupTitle(String groupTitle) { this.groupTitle = groupTitle; }

    public String getAccountPhone() { return accountPhone; }
    public void setAccountPhone(String accountPhone) { this.accountPhone = accountPhone; }

    @Override
    public Date getCreateTime() { return createTime; }
    @Override
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
