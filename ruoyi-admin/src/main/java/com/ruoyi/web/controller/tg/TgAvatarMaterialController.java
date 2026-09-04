package com.ruoyi.web.controller.tg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.system.domain.TgAvatarMaterial;
import com.ruoyi.system.service.ITgAvatarMaterialService;

/**
 * 头像素材库Controller
 * 文件保存在 {profile}/tg_avatar/ 下, 通过 /profile/tg_avatar/xxx 访问(节点下载用)
 */
@RestController
@RequestMapping("/tg/avatarMaterial")
public class TgAvatarMaterialController extends BaseController
{
    private static final String SUB_DIR = "tg_avatar";
    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024L;

    @Autowired
    private ITgAvatarMaterialService service;

    @PreAuthorize("@ss.hasPermi('tg:avatarMaterial:list')")
    @GetMapping("/list")
    public TableDataInfo list(TgAvatarMaterial query)
    {
        startPage();
        List<TgAvatarMaterial> list = service.selectList(query);
        return getDataTable(list);
    }

    /**
     * 导入: zip 压缩包(内含图片, 支持子目录) 或 单张图片
     */
    @PreAuthorize("@ss.hasPermi('tg:avatarMaterial:add')")
    @PostMapping("/import")
    public AjaxResult importFile(@RequestParam("file") MultipartFile file)
    {
        if (file == null || file.isEmpty())
        {
            return error("请选择要导入的 zip 或图片文件");
        }
        String originalName = file.getOriginalFilename();
        String ext = extOf(originalName);
        try
        {
            if ("zip".equalsIgnoreCase(ext))
            {
                int[] result = importZip(file);
                return success("导入完成，新增 " + result[0] + " 张头像，跳过非图片文件 " + result[1] + " 个");
            }
            if (!isImage(ext))
            {
                return error("不支持的文件类型: " + ext + "（仅支持 zip 或 " + String.join("/", MimeTypeUtils.IMAGE_EXTENSION) + "）");
            }
            if (file.getSize() > MAX_IMAGE_SIZE)
            {
                return error("图片大小不能超过 10MB");
            }
            try (InputStream in = file.getInputStream())
            {
                saveImage(originalName, ext, in);
            }
            return success("导入完成，新增 1 张头像");
        }
        catch (Exception e)
        {
            logger.error("头像素材导入失败", e);
            return error("导入失败: " + e.getMessage());
        }
    }

    private int[] importZip(MultipartFile file) throws Exception
    {
        int added = 0, skipped = 0;
        try (ZipInputStream zis = new ZipInputStream(file.getInputStream(), java.nio.charset.StandardCharsets.UTF_8))
        {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null)
            {
                if (entry.isDirectory())
                {
                    continue;
                }
                String name = entry.getName();
                String base = name.contains("/") ? name.substring(name.lastIndexOf('/') + 1) : name;
                if (base.startsWith(".") || base.startsWith("__MACOSX"))
                {
                    continue;
                }
                String ext = extOf(base);
                if (!isImage(ext) || entry.getSize() > MAX_IMAGE_SIZE)
                {
                    skipped++;
                    continue;
                }
                saveImage(base, ext, zis);
                added++;
            }
        }
        return new int[]{added, skipped};
    }

    private static String extOf(String name)
    {
        if (name == null) return "";
        int i = name.lastIndexOf('.');
        return i < 0 ? "" : name.substring(i + 1);
    }

    private boolean isImage(String ext)
    {
        return StringUtils.isNotEmpty(ext) && Arrays.asList(MimeTypeUtils.IMAGE_EXTENSION).contains(ext.toLowerCase());
    }

    private void saveImage(String originalName, String ext, InputStream in) throws Exception
    {
        File dir = new File(RuoYiConfig.getProfile(), SUB_DIR);
        if (!dir.exists() && !dir.mkdirs())
        {
            throw new IllegalStateException("无法创建目录 " + dir.getAbsolutePath());
        }
        String storedName = UUID.randomUUID().toString().replace("-", "") + "." + ext.toLowerCase();
        File target = new File(dir, storedName);
        try (OutputStream out = new FileOutputStream(target))
        {
            byte[] buf = new byte[8192];
            int n;
            long total = 0;
            while ((n = in.read(buf)) > 0)
            {
                total += n;
                if (total > MAX_IMAGE_SIZE)
                {
                    out.close();
                    target.delete();
                    throw new IllegalArgumentException(originalName + " 超过 10MB");
                }
                out.write(buf, 0, n);
            }
        }
        TgAvatarMaterial m = new TgAvatarMaterial();
        m.setFileName(originalName);
        m.setFilePath(Constants.RESOURCE_PREFIX + "/" + SUB_DIR + "/" + storedName);
        service.insert(m);
    }

    /**
     * 删除素材(同时删除磁盘文件)
     */
    @PreAuthorize("@ss.hasPermi('tg:avatarMaterial:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        List<TgAvatarMaterial> list = service.selectByIds(ids);
        int rows = service.deleteByIds(ids);
        for (TgAvatarMaterial m : list)
        {
            String p = m.getFilePath();
            if (p != null && p.startsWith(Constants.RESOURCE_PREFIX + "/" + SUB_DIR + "/"))
            {
                Path f = new File(RuoYiConfig.getProfile(), p.substring(Constants.RESOURCE_PREFIX.length())).toPath();
                try
                {
                    Files.deleteIfExists(f);
                }
                catch (Exception e)
                {
                    logger.warn("删除头像文件失败: {}", f, e);
                }
            }
        }
        return toAjax(rows);
    }
}
