package com.ruoyi.web.controller.tg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.TgNicknameMaterial;
import com.ruoyi.system.service.ITgNicknameMaterialService;

/**
 * 昵称素材库Controller
 */
@RestController
@RequestMapping("/tg/nicknameMaterial")
public class TgNicknameMaterialController extends BaseController
{
    @Autowired
    private ITgNicknameMaterialService service;

    @PreAuthorize("@ss.hasPermi('tg:nicknameMaterial:list')")
    @GetMapping("/list")
    public TableDataInfo list(TgNicknameMaterial query)
    {
        startPage();
        List<TgNicknameMaterial> list = service.selectList(query);
        return getDataTable(list);
    }

    /**
     * 导入 txt 文件, 每行一个昵称
     */
    @PreAuthorize("@ss.hasPermi('tg:nicknameMaterial:add')")
    @PostMapping("/import")
    public AjaxResult importTxt(@RequestParam("file") MultipartFile file)
    {
        if (file == null || file.isEmpty())
        {
            return error("请选择要导入的 txt 文件");
        }
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                lines.add(line.replace("\uFEFF", ""));
            }
        }
        catch (Exception e)
        {
            return error("读取文件失败: " + e.getMessage());
        }
        int count = service.importNicknames(lines);
        return success("导入完成，新增 " + count + " 个昵称（重复/空行已忽略）");
    }

    @PreAuthorize("@ss.hasPermi('tg:nicknameMaterial:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(service.deleteByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('tg:nicknameMaterial:remove')")
    @DeleteMapping("/clear")
    public AjaxResult clear()
    {
        return success("已清空，共删除 " + service.deleteAll() + " 个昵称");
    }
}
