package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * IP代理组对象 tg_proxy_group
 */
public class TgProxyGroup extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Integer id;

    /** 组唯一ID */
    private String groupNo;

    /** 组标题 */
    private String title;

    /** 国家 */
    private String country;

    /** 到期时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    /** 单IP最大支持账号数 */
    private Integer maxBindable;

    /** 组内IP总数 */
    private Integer totalCount;

    /** IP使用数(current_bind_count > 0) */
    private Integer usedCount;

    /** IP未使用数 */
    private Integer unusedCount;

    /** IP废弃数(status=disabled) */
    private Integer disabledCount;

    /** 导入时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date importTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getGroupNo() { return groupNo; }
    public void setGroupNo(String groupNo) { this.groupNo = groupNo; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Date getExpireTime() { return expireTime; }
    public void setExpireTime(Date expireTime) { this.expireTime = expireTime; }

    public Integer getMaxBindable() { return maxBindable; }
    public void setMaxBindable(Integer maxBindable) { this.maxBindable = maxBindable; }

    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }

    public Integer getUsedCount() { return usedCount; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }

    public Integer getUnusedCount() { return unusedCount; }
    public void setUnusedCount(Integer unusedCount) { this.unusedCount = unusedCount; }

    public Integer getDisabledCount() { return disabledCount; }
    public void setDisabledCount(Integer disabledCount) { this.disabledCount = disabledCount; }

    public Date getImportTime() { return importTime; }
    public void setImportTime(Date importTime) { this.importTime = importTime; }

    @Override
    public Date getCreateTime() { return createTime; }
    @Override
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
