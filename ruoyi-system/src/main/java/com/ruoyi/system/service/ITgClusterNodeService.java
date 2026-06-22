package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TgClusterNode;

/**
 * 集群节点信息Service接口
 */
public interface ITgClusterNodeService
{
    public List<TgClusterNode> selectTgClusterNodeList(TgClusterNode tgClusterNode);

    public TgClusterNode selectTgClusterNodeById(String nodeId);

    public List<TgClusterNode> selectActiveNodes(int minutes);

    public int updateTgClusterNode(TgClusterNode tgClusterNode);
}
