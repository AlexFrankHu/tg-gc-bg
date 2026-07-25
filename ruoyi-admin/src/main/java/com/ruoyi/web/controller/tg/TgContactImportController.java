package com.ruoyi.web.controller.tg;

import java.io.InputStream;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.TgContactImportBatch;
import com.ruoyi.system.domain.TgContactImportRecord;
import com.ruoyi.system.domain.TgContactImportFiltered;
import com.ruoyi.system.service.ITgContactImportBatchService;
import com.ruoyi.system.service.ITgContactImportRecordService;
import com.ruoyi.system.service.ITgContactImportFilteredService;
import com.ruoyi.system.service.ITgTelethonAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 好友导入Controller
 */
@RestController
@RequestMapping("/tg/contactImport")
public class TgContactImportController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(TgContactImportController.class);

    @Autowired
    private ITgContactImportBatchService batchService;

    @Autowired
    private ITgContactImportRecordService recordService;

    @Autowired
    private ITgContactImportFilteredService filteredService;

    @Autowired
    private ITgTelethonAccountService telethonAccountService;

    /**
     * 查询某批次被过滤的号码（重复/废弃）
     */
    @PreAuthorize("@ss.hasPermi('tg:contactImport:list')")
    @GetMapping("/filtered/list")
    public TableDataInfo filteredList(@RequestParam("batchNo") String batchNo,
                                      @RequestParam(value = "filterType", required = false) String filterType)
    {
        startPage();
        List<TgContactImportFiltered> list = filteredService.selectByBatchNo(batchNo, filterType);
        return getDataTable(list);
    }

    /**
     * 查询好友导入批次列表
     */
    @PreAuthorize("@ss.hasPermi('tg:contactImport:list')")
    @GetMapping("/batch/list")
    public TableDataInfo batchList(TgContactImportBatch batch)
    {
        startPage();
        List<TgContactImportBatch> list = batchService.selectList(batch);
        return getDataTable(list);
    }

    /**
     * 查询所有批次（用于下拉选择）
     */
    @PreAuthorize("@ss.hasPermi('tg:contactImport:list')")
    @GetMapping("/batch/all")
    public AjaxResult batchAll()
    {
        List<TgContactImportBatch> list = batchService.selectList(new TgContactImportBatch());
        return success(list);
    }

    /**
     * 修改批次标题
     */
    @PreAuthorize("@ss.hasPermi('tg:contactImport:edit')")
    @PutMapping("/batch/title")
    public AjaxResult updateBatchTitle(@RequestBody TgContactImportBatch batch)
    {
        if (batch.getId() == null || batch.getTitle() == null)
        {
            return error("参数不完整");
        }
        TgContactImportBatch update = new TgContactImportBatch();
        update.setId(batch.getId());
        update.setTitle(batch.getTitle());
        return toAjax(batchService.update(update));
    }

    /**
     * 删除批次
     */
    @PreAuthorize("@ss.hasPermi('tg:contactImport:remove')")
    @DeleteMapping("/batch/{ids}")
    public AjaxResult deleteBatch(@PathVariable Integer[] ids)
    {
        return toAjax(batchService.deleteByIds(ids));
    }

    /**
     * 查询导入详情（某批次的号码列表）
     */
    @PreAuthorize("@ss.hasPermi('tg:contactImport:list')")
    @GetMapping("/record/list")
    public TableDataInfo recordList(@RequestParam("batchNo") String batchNo)
    {
        startPage();
        List<TgContactImportRecord> list = recordService.selectByBatchNo(batchNo);
        return getDataTable(list);
    }

    /**
     * 导入好友-手机号（上传xlsx）
     */
    @PreAuthorize("@ss.hasPermi('tg:contactImport:add')")
    @PostMapping("/upload")
    public AjaxResult importContacts(@RequestParam("file") MultipartFile file,
                                     @RequestParam(value = "title", required = false) String title)
    {
        return doImport(file, title, "phone");
    }

    /**
     * 导入好友-用户名（上传xlsx）
     */
    @PreAuthorize("@ss.hasPermi('tg:contactImport:add')")
    @PostMapping("/uploadUsername")
    public AjaxResult importContactsByUsername(@RequestParam("file") MultipartFile file,
                                               @RequestParam(value = "title", required = false) String title)
    {
        return doImport(file, title, "username");
    }

    private AjaxResult doImport(MultipartFile file, String title, String importType)
    {
        if (file.isEmpty())
        {
            return error("请选择要导入的文件");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase().endsWith(".xlsx"))
        {
            return error("仅支持xlsx格式文件");
        }

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is))
        {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null)
            {
                return error("Excel文件为空");
            }

            boolean isPhone = "phone".equals(importType);

            // 手机号导入: 预加载账号列表手机号(去+归一化), 用于过滤"废弃"
            Set<String> accountPhones = new HashSet<>();
            if (isPhone)
            {
                for (String p : telethonAccountService.selectAllPhones())
                {
                    if (p == null) continue;
                    String np = p.trim();
                    if (np.startsWith("+")) np = np.substring(1);
                    if (!np.isEmpty()) accountPhones.add(np);
                }
            }

            List<String> values = new ArrayList<>();
            List<TgContactImportFiltered> filteredList = new ArrayList<>();
            Set<String> kept = new HashSet<>();        // 已保留的号码(用于识别重复)
            Set<String> discarded = new HashSet<>();   // 已记录为"废弃"的号码(每个唯一号只记一次)

            for (int i = 0; i <= sheet.getLastRowNum(); i++)
            {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Cell cell = row.getCell(0);
                if (cell == null) continue;

                String val = getCellValueAsString(cell).trim();
                if (isPhone)
                {
                    if (val.isEmpty() || !val.matches("\\+?\\d+")) continue;
                    if (val.startsWith("+")) val = val.substring(1);

                    // 1) 在账号列表中 -> 废弃
                    if (accountPhones.contains(val))
                    {
                        if (discarded.add(val))
                        {
                            filteredList.add(newFiltered(val, TgContactImportFiltered.TYPE_DISCARD));
                        }
                        continue;
                    }
                    // 2) 文件内重复 -> 重复
                    if (!kept.add(val))
                    {
                        filteredList.add(newFiltered(val, TgContactImportFiltered.TYPE_DUPLICATE));
                        continue;
                    }
                    // 3) 干净号码 -> 保留
                    values.add(val);
                }
                else
                {
                    // username: remove leading @ if present
                    if (!val.isEmpty())
                    {
                        if (val.startsWith("@")) val = val.substring(1);
                        values.add(val);
                    }
                }
            }

            if (values.isEmpty() && filteredList.isEmpty())
            {
                return error(isPhone ? "未找到有效的手机号码" : "未找到有效的用户名");
            }

            // Create batch
            String batchNo = "CF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            TgContactImportBatch batch = new TgContactImportBatch();
            batch.setBatchNo(batchNo);
            batch.setTitle(title != null && !title.isEmpty() ? title : originalName);
            batch.setImportType(importType);
            batch.setFileName(originalName);
            batch.setTotalCount(values.size());
            batch.setImportTime(new Date());
            batchService.insert(batch);

            // Create records
            List<TgContactImportRecord> records = new ArrayList<>();
            for (String val : values)
            {
                TgContactImportRecord record = new TgContactImportRecord();
                record.setBatchNo(batchNo);
                if (isPhone)
                {
                    record.setPhone(val);
                }
                else
                {
                    record.setUsername(val);
                }
                records.add(record);
            }

            // Batch insert in chunks of 500
            int chunkSize = 500;
            for (int i = 0; i < records.size(); i += chunkSize)
            {
                List<TgContactImportRecord> chunk = records.subList(i, Math.min(i + chunkSize, records.size()));
                recordService.batchInsert(chunk);
            }

            // 写入过滤记录(废弃/重复), 带批次号
            int duplicateCount = 0;
            int discardCount = 0;
            for (TgContactImportFiltered f : filteredList)
            {
                f.setBatchNo(batchNo);
                if (TgContactImportFiltered.TYPE_DUPLICATE.equals(f.getFilterType())) duplicateCount++;
                else discardCount++;
            }
            for (int i = 0; i < filteredList.size(); i += chunkSize)
            {
                List<TgContactImportFiltered> chunk = filteredList.subList(i, Math.min(i + chunkSize, filteredList.size()));
                filteredService.batchInsert(chunk);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("batchNo", batchNo);
            result.put("totalCount", values.size());
            result.put("discardCount", discardCount);
            result.put("duplicateCount", duplicateCount);
            return success(result);
        }
        catch (Exception e)
        {
            log.error("导入好友失败", e);
            return error("导入失败: " + e.getMessage());
        }
    }

    private TgContactImportFiltered newFiltered(String phone, String type)
    {
        TgContactImportFiltered f = new TgContactImportFiltered();
        f.setPhone(phone);
        f.setFilterType(type);
        return f;
    }

    private String getCellValueAsString(Cell cell)
    {
        if (cell == null) return "";
        switch (cell.getCellType())
        {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                // Avoid scientific notation for phone numbers
                double num = cell.getNumericCellValue();
                if (num == Math.floor(num) && !Double.isInfinite(num))
                {
                    return String.valueOf((long) num);
                }
                return String.valueOf(num);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }
}
