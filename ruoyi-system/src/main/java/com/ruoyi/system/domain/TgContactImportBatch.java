package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 好友导入批次对象 tg_contact_import_batch
 */
public class TgContactImportBatch extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String batchNo;
    private String title;
    private String importType;
    private String fileName;
    private Integer totalCount;
    private Integer usedCount;
    private Integer waitingCount;
    private Integer invalidCount;
    private Integer remainCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date importTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImportType() { return importType; }
    public void setImportType(String importType) { this.importType = importType; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }

    public Integer getUsedCount() { return usedCount; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }

    public Integer getWaitingCount() { return waitingCount; }
    public void setWaitingCount(Integer waitingCount) { this.waitingCount = waitingCount; }

    public Integer getInvalidCount() { return invalidCount; }
    public void setInvalidCount(Integer invalidCount) { this.invalidCount = invalidCount; }

    public Integer getRemainCount() {
        int total = totalCount != null ? totalCount : 0;
        int used = usedCount != null ? usedCount : 0;
        int waiting = waitingCount != null ? waitingCount : 0;
        int invalid = invalidCount != null ? invalidCount : 0;
        return total - used - waiting - invalid;
    }
    public void setRemainCount(Integer remainCount) { this.remainCount = remainCount; }

    public Date getImportTime() { return importTime; }
    public void setImportTime(Date importTime) { this.importTime = importTime; }

    @Override
    public Date getCreateTime() { return createTime; }

    @Override
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
