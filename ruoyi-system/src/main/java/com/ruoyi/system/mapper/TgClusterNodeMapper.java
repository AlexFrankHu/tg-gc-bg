package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.TgClusterNode;

/**
 * 集群节点信息Mapper接口
 */
public interface TgClusterNodeMapper
{
    public List<TgClusterNode> selectTgClusterNodeList(TgClusterNode tgClusterNode);

    public TgClusterNode selectTgClusterNodeById(String nodeId);

    public List<TgClusterNode> selectActiveNodes(@Param("minutes") int minutes);

    public int updateTgClusterNode(TgClusterNode tgClusterNode);
}
