package com.ruoyi.web.controller.tg;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.TgClusterNode;
import com.ruoyi.system.service.ITgClusterNodeService;

/**
 * 集群节点管理Controller
 */
@RestController
@RequestMapping("/tg/node")
public class TgClusterNodeController extends BaseController
{
    @Autowired
    private ITgClusterNodeService clusterNodeService;

    /**
     * 查询节点列表
     */
    @PreAuthorize("@ss.hasPermi('tg:node:list')")
    @GetMapping("/list")
    public TableDataInfo list(TgClusterNode node)
    {
        startPage();
        List<TgClusterNode> list = clusterNodeService.selectTgClusterNodeList(node);
        return getDataTable(list);
    }

    /**
     * 获取节点详细信息
     */
    @PreAuthorize("@ss.hasPermi('tg:node:query')")
    @GetMapping(value = "/{nodeId}")
    public AjaxResult getInfo(@PathVariable("nodeId") String nodeId)
    {
        return success(clusterNodeService.selectTgClusterNodeById(nodeId));
    }

    /**
     * 修改节点信息（如最大账号数）
     */
    @PreAuthorize("@ss.hasPermi('tg:node:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody TgClusterNode node)
    {
        return toAjax(clusterNodeService.updateTgClusterNode(node));
    }
}
