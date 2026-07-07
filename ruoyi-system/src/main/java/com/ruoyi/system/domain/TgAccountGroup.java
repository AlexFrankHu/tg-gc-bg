package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 账号分组对象 tg_account_group
 */
public class TgAccountGroup extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 组ID */
    private Integer id;

    /** 组名称 */
    private String groupName;

    /** 是否可用 1-可用 0-不可用 */
    private Integer enabled;

    /** 该分组下账号数量(关联统计) */
    private Integer accountCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }
    public Integer getAccountCount() { return accountCount; }
    public void setAccountCount(Integer accountCount) { this.accountCount = accountCount; }
    @Override
    public Date getCreateTime() { return createTime; }
    @Override
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
