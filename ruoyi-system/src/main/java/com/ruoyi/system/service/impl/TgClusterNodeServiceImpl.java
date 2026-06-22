package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TgClusterNodeMapper;
import com.ruoyi.system.domain.TgClusterNode;
import com.ruoyi.system.service.ITgClusterNodeService;

/**
 * 集群节点信息Service业务层处理
 */
@Service
public class TgClusterNodeServiceImpl implements ITgClusterNodeService
{
    @Autowired
    private TgClusterNodeMapper tgClusterNodeMapper;

    @Override
    public List<TgClusterNode> selectTgClusterNodeList(TgClusterNode tgClusterNode)
    {
        return tgClusterNodeMapper.selectTgClusterNodeList(tgClusterNode);
    }

    @Override
    public TgClusterNode selectTgClusterNodeById(String nodeId)
    {
        return tgClusterNodeMapper.selectTgClusterNodeById(nodeId);
    }

    @Override
    public List<TgClusterNode> selectActiveNodes(int minutes)
    {
        return tgClusterNodeMapper.selectActiveNodes(minutes);
    }

    @Override
    public int updateTgClusterNode(TgClusterNode tgClusterNode)
    {
        return tgClusterNodeMapper.updateTgClusterNode(tgClusterNode);
    }
}
