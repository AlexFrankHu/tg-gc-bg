package com.ruoyi.web.controller.tg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.sql.SqlUtil;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.TgChatMessage;
import com.ruoyi.system.domain.vo.TgChatMessageExport;
import com.ruoyi.system.service.ITgChatMessageService;

/**
 * Telegram聊天记录Controller
 */
@RestController
@RequestMapping("/tg/chatMessage")
public class TgChatMessageController extends BaseController
{
    @Autowired
    private ITgChatMessageService tgChatMessageService;

    @PreAuthorize("@ss.hasPermi('tg:chatMessage:list')")
    @GetMapping("/list")
    public TableDataInfo list(TgChatMessage tgChatMessage)
    {
        // tg_chat_message 有千万级数据, PageHelper 默认每次翻页都对全表 COUNT(约24秒),
        // 这里改为分页查询不带自动 COUNT, 总数由 service 单独计算(带过滤时限时精确统计, 否则近似).
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
        PageHelper.startPage(pageNum, pageSize, orderBy).setCount(false);
        List<TgChatMessage> list = tgChatMessageService.selectTgChatMessageList(tgChatMessage);

        TableDataInfo rsp = new TableDataInfo();
        rsp.setCode(200);
        rsp.setMsg("查询成功");
        rsp.setRows(list);
        rsp.setTotal(tgChatMessageService.countForPage(tgChatMessage));
        return rsp;
    }

    @PreAuthorize("@ss.hasPermi('tg:chatMessage:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(tgChatMessageService.selectTgChatMessageById(id));
    }

    @PreAuthorize("@ss.hasPermi('tg:chatMessage:remove')")
    @Log(title = "Telegram聊天记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(tgChatMessageService.deleteTgChatMessageByIds(ids));
    }

    @Log(title = "导出聊天记录", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('tg:chatMessage:list')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, TgChatMessage tgChatMessage)
    {
        tgChatMessage.setContentType(null);
        List<TgChatMessage> list = tgChatMessageService.selectTgChatMessageList(tgChatMessage);
        Collections.reverse(list);
        List<TgChatMessageExport> exportList = new ArrayList<>();
        for (TgChatMessage msg : list)
        {
            TgChatMessageExport vo = new TgChatMessageExport();
            vo.setSenderName(msg.getSenderName());
            vo.setSendTime(msg.getSendTime());
            vo.setDirection(Boolean.TRUE.equals(msg.getIsOutgoing()) ? "发送" : "接收");
            vo.setContentTypeLabel(mapContentType(msg.getContentType()));
            String content = msg.getTextContent();
            if (content == null || content.isEmpty())
            {
                content = "[" + mapContentType(msg.getContentType()) + "]";
                if (msg.getMediaFileName() != null && !msg.getMediaFileName().isEmpty())
                {
                    content += " " + msg.getMediaFileName();
                }
            }
            vo.setTextContent(content);
            exportList.add(vo);
        }
        ExcelUtil<TgChatMessageExport> util = new ExcelUtil<>(TgChatMessageExport.class);
        util.exportExcel(response, exportList, "聊天记录");
    }

    private String mapContentType(String type)
    {
        if (type == null) return "未知";
        switch (type)
        {
            case "text": return "文本";
            case "photo": return "图片";
            case "video": return "视频";
            case "voice": return "语音";
            case "audio": return "音频";
            case "video_note": return "圆形视频";
            case "gif": return "GIF";
            case "document": return "文件";
            case "sticker": return "贴纸";
            case "location": return "位置";
            case "contact": return "联系人";
            case "poll": return "投票";
            default: return type;
        }
    }
}
