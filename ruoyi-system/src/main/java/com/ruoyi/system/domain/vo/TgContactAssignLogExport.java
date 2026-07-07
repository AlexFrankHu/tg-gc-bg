package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

import java.util.Date;

/**
 * 好友分配日志导出VO
 */
public class TgContactAssignLogExport
{
    @Excel(name = "记录ID")
    private Integer id;

    @Excel(name = "来源")
    private String sourceLabel;

    @Excel(name = "账号分组")
    private String groupName;

    @Excel(name = "账号批次")
    private String accountBatchTitle;

    @Excel(name = "账号")
    private String accountPhone;

    @Excel(name = "好友批次")
    private String contactBatchTitle;

    @Excel(name = "好友号码/用户名")
    private String contactInfo;

    @Excel(name = "状态")
    private String statusLabel;

    @Excel(name = "添加次数")
    private Integer retryCount;

    @Excel(name = "失败原因", width = 40)
    private String errorReason;

    @Excel(name = "备注", width = 40)
    private String remark;

    @Excel(name = "时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getSourceLabel() { return sourceLabel; }
    public void setSourceLabel(String sourceLabel) { this.sourceLabel = sourceLabel; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public String getAccountBatchTitle() { return accountBatchTitle; }
    public void setAccountBatchTitle(String accountBatchTitle) { this.accountBatchTitle = accountBatchTitle; }
    public String getAccountPhone() { return accountPhone; }
    public void setAccountPhone(String accountPhone) { this.accountPhone = accountPhone; }
    public String getContactBatchTitle() { return contactBatchTitle; }
    public void setContactBatchTitle(String contactBatchTitle) { this.contactBatchTitle = contactBatchTitle; }
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public String getStatusLabel() { return statusLabel; }
    public void setStatusLabel(String statusLabel) { this.statusLabel = statusLabel; }
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    public String getErrorReason() { return errorReason; }
    public void setErrorReason(String errorReason) { this.errorReason = errorReason; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
