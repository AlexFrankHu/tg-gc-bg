package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 好友导入过滤记录对象 tg_contact_import_filtered
 */
public class TgContactImportFiltered extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 过滤类型：重复 */
    public static final String TYPE_DUPLICATE = "重复";
    /** 过滤类型：废弃（在账号列表中） */
    public static final String TYPE_DISCARD = "废弃";

    private Long id;
    private String batchNo;
    private String phone;
    /** 过滤类型：重复 / 废弃 */
    private String filterType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getFilterType() { return filterType; }
    public void setFilterType(String filterType) { this.filterType = filterType; }

    @Override
    public Date getCreateTime() { return createTime; }

    @Override
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
