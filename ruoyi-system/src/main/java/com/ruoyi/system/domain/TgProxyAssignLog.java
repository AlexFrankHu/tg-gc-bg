package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class TgProxyAssignLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String accountBatchNo;
    private String accountBatchTitle;
    private Integer accountId;
    private String accountPhone;
    private String proxyGroupNo;
    private String proxyGroupTitle;
    private Integer proxyIpId;
    private String proxyUrl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /** 账号所属节点ID */
    private String nodeId;


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getAccountBatchNo() { return accountBatchNo; }
    public void setAccountBatchNo(String accountBatchNo) { this.accountBatchNo = accountBatchNo; }
    public String getAccountBatchTitle() { return accountBatchTitle; }
    public void setAccountBatchTitle(String accountBatchTitle) { this.accountBatchTitle = accountBatchTitle; }
    public Integer getAccountId() { return accountId; }
    public void setAccountId(Integer accountId) { this.accountId = accountId; }
    public String getAccountPhone() { return accountPhone; }
    public void setAccountPhone(String accountPhone) { this.accountPhone = accountPhone; }
    public String getProxyGroupNo() { return proxyGroupNo; }
    public void setProxyGroupNo(String proxyGroupNo) { this.proxyGroupNo = proxyGroupNo; }
    public String getProxyGroupTitle() { return proxyGroupTitle; }
    public void setProxyGroupTitle(String proxyGroupTitle) { this.proxyGroupTitle = proxyGroupTitle; }
    public Integer getProxyIpId() { return proxyIpId; }
    public void setProxyIpId(Integer proxyIpId) { this.proxyIpId = proxyIpId; }
    public String getProxyUrl() { return proxyUrl; }
    public void setProxyUrl(String proxyUrl) { this.proxyUrl = proxyUrl; }
    @Override
    public Date getCreateTime() { return createTime; }
    @Override
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
}
