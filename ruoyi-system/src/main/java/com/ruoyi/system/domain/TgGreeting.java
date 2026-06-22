package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 主动问候语对象 tg_greeting
 */
public class TgGreeting extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Integer id;

    /** 标题/分类 */
    private String title;

    /** 问候语内容 */
    private String content;

    /** 关联的自动回复state值 */
    private Integer state;

    /** 是否启用 0否 1是 */
    private Integer isEnabled;

    /** 排序 */
    private Integer sortOrder;

    /** 图片路径 */
    private String imagePath;

    /** 备注 */
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getState() { return state; }
    public void setState(Integer state) { this.state = state; }

    public Integer getIsEnabled() { return isEnabled; }
    public void setIsEnabled(Integer isEnabled) { this.isEnabled = isEnabled; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    @Override
    public String getRemark() { return remark; }

    @Override
    public void setRemark(String remark) { this.remark = remark; }

    @Override
    public Date getCreateTime() { return createTime; }

    @Override
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
