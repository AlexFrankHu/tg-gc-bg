package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 集群节点信息对象 tg_cluster_node
 */
public class TgClusterNode extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 节点ID (MD5中间16位) */
    private String nodeId;

    /** 节点公网IP */
    private String publicIp;

    /** 节点内网IP */
    private String privateIp;

    /** 历史总账号数 */
    private Integer totalAccountCount;

    /** 在线账号数 */
    private Integer onlineAccountCount;

    /** 节点目录 */
    private String nodeDir;

    /** 节点端口 */
    private Integer nodePort;

    /** 节点状态(1开启 0关闭) */
    private String nodeStatus;

    /** 最后活跃时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastActiveTime;

    /** 最大账号数 */
    private Integer maxAccountCount;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public String getPublicIp() { return publicIp; }
    public void setPublicIp(String publicIp) { this.publicIp = publicIp; }

    public String getPrivateIp() { return privateIp; }
    public void setPrivateIp(String privateIp) { this.privateIp = privateIp; }

    public Integer getTotalAccountCount() { return totalAccountCount; }
    public void setTotalAccountCount(Integer totalAccountCount) { this.totalAccountCount = totalAccountCount; }

    public Integer getOnlineAccountCount() { return onlineAccountCount; }
    public void setOnlineAccountCount(Integer onlineAccountCount) { this.onlineAccountCount = onlineAccountCount; }

    public String getNodeDir() { return nodeDir; }
    public void setNodeDir(String nodeDir) { this.nodeDir = nodeDir; }

    public Integer getNodePort() { return nodePort; }
    public void setNodePort(Integer nodePort) { this.nodePort = nodePort; }

    public String getNodeStatus() { return nodeStatus; }
    public void setNodeStatus(String nodeStatus) { this.nodeStatus = nodeStatus; }

    public Date getLastActiveTime() { return lastActiveTime; }
    public void setLastActiveTime(Date lastActiveTime) { this.lastActiveTime = lastActiveTime; }

    public Integer getMaxAccountCount() { return maxAccountCount; }
    public void setMaxAccountCount(Integer maxAccountCount) { this.maxAccountCount = maxAccountCount; }

    @Override
    public Date getCreateTime() { return createTime; }
    @Override
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
