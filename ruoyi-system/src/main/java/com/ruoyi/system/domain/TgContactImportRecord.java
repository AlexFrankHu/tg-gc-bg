package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 好友导入记录对象 tg_contact_import_record
 */
public class TgContactImportRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String batchNo;
    private String phone;
    private String username;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @Override
    public Date getCreateTime() { return createTime; }

    @Override
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
