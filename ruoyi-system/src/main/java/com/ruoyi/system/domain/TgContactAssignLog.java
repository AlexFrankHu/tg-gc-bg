package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class TgContactAssignLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String accountBatchNo;
    private String accountBatchTitle;
    private Integer accountId;
    private String accountPhone;
    private String contactBatchNo;
    private String contactBatchTitle;
    private String contactPhone;
    private String contactUsername;
    private String status;
    private String remark;
    private Integer retryCount;
    private String accountStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

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
    public String getContactBatchNo() { return contactBatchNo; }
    public void setContactBatchNo(String contactBatchNo) { this.contactBatchNo = contactBatchNo; }
    public String getContactBatchTitle() { return contactBatchTitle; }
    public void setContactBatchTitle(String contactBatchTitle) { this.contactBatchTitle = contactBatchTitle; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getContactUsername() { return contactUsername; }
    public void setContactUsername(String contactUsername) { this.contactUsername = contactUsername; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }
    @Override
    public Date getCreateTime() { return createTime; }
    @Override
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
