package com.ruoyi.web.controller.tg;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.TgImportBatch;
import com.ruoyi.system.domain.TgImportAccount;
import com.ruoyi.system.domain.TgTelethonAccount;
import com.ruoyi.system.domain.TgContactImportBatch;
import com.ruoyi.system.domain.TgContactImportRecord;
import com.ruoyi.system.domain.TgContactAssignLog;
import com.ruoyi.system.domain.TgClusterNode;
import com.ruoyi.system.service.ITgImportBatchService;
import com.ruoyi.system.service.ITgImportAccountService;
import com.ruoyi.system.service.ITgTelethonAccountService;
import com.ruoyi.system.service.ITgContactImportBatchService;
import com.ruoyi.system.service.ITgContactImportRecordService;
import com.ruoyi.system.service.ITgClusterNodeService;
import com.ruoyi.system.mapper.TgContactImportBatchMapper;
import com.ruoyi.system.mapper.TgContactAssignLogMapper;
import com.ruoyi.system.mapper.TgContactMapper;
import com.ruoyi.system.mapper.TgLoginLogMapper;
import com.ruoyi.system.mapper.TgSendFailLogMapper;
import com.ruoyi.system.mapper.TgAutoReplyLogMapper;
import com.ruoyi.system.domain.TgContact;
import com.ruoyi.system.domain.TgLoginLog;
import com.ruoyi.system.domain.TgSendFailLog;
import com.ruoyi.system.domain.TgAutoReplyLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 账号导入Controller
 */
@RestController
@RequestMapping("/tg/import")
public class TgImportController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(TgImportController.class);

    @Value("${tg-client-telethon.url:http://localhost:8807}")
    private String telethonUrl;

    @Value("${tg-client-telethon.account-dir:}")
    private String accountDir;

    @Autowired
    private ITgImportBatchService batchService;

    @Autowired
    private ITgImportAccountService accountService;

    @Autowired
    private ITgTelethonAccountService telethonAccountService;

    @Autowired
    private ITgContactImportBatchService contactBatchService;

    @Autowired
    private TgContactImportBatchMapper contactBatchMapper;

    @Autowired
    private ITgContactImportRecordService contactRecordService;

    @Autowired
    private TgContactAssignLogMapper contactAssignLogMapper;

    @Autowired
    private TgContactMapper tgContactMapper;

    @Autowired
    private TgLoginLogMapper loginLogMapper;

    @Autowired
    private TgSendFailLogMapper sendFailLogMapper;

    @Autowired
    private TgAutoReplyLogMapper autoReplyLogMapper;

    @Autowired
    private ITgClusterNodeService clusterNodeService;

    @Autowired
    private com.ruoyi.system.service.ITgAccountGroupService accountGroupService;

    /**
     * 查询导入批次列表（逆序）
     */
    @PreAuthorize("@ss.hasPermi('tg:import:list')")
    @GetMapping("/batch/list")
    public TableDataInfo batchList(TgImportBatch batch)
    {
        batchService.refreshAllBatchStats();
        startPage();
        List<TgImportBatch> list = batchService.selectTgImportBatchList(batch);
        return getDataTable(list);
    }

    /**
     * 查询所有批次（用于下拉选择）
     */
    @PreAuthorize("@ss.hasPermi('tg:import:list')")
    @GetMapping("/batch/all")
    public AjaxResult batchAll()
    {
        List<TgImportBatch> list = batchService.selectTgImportBatchList(new TgImportBatch());
        return success(list);
    }

    /**
     * 修改批次标题
     */
    @PreAuthorize("@ss.hasPermi('tg:import:edit')")
    @PutMapping("/batch/title")
    public AjaxResult updateBatchTitle(@RequestBody TgImportBatch batch)
    {
        if (batch.getId() == null || batch.getTitle() == null)
        {
            return error("参数不完整");
        }
        TgImportBatch update = new TgImportBatch();
        update.setId(batch.getId());
        update.setTitle(batch.getTitle());
        return toAjax(batchService.updateTgImportBatch(update));
    }

    /**
     * 查询导入详情（某批次的账号列表）
     */
    @PreAuthorize("@ss.hasPermi('tg:import:list')")
    @GetMapping("/account/list")
    public TableDataInfo accountList(TgImportAccount account)
    {
        // Sync status from tg_telethon_account before querying
        accountService.syncStatusFromTelethonAccount(account.getBatchNo());
        startPage();
        List<TgImportAccount> list = accountService.selectTgImportAccountList(account);
        return getDataTable(list);
    }

    /**
     * 导入账号（上传zip）
     */
    @PreAuthorize("@ss.hasPermi('tg:import:add')")
    @PostMapping("/upload")
    public AjaxResult importAccounts(@RequestParam("file") MultipartFile file,
                                     @RequestParam(value = "title", required = false) String title,
                                     @RequestParam(value = "nodeId", required = false) String nodeId)
    {
        if (file.isEmpty())
        {
            return error("请选择要导入的文件");
        }

        // 指定节点时校验节点存在; 不指定(空)时走原有的自动分配流程
        final String assignNodeId = (nodeId != null && !nodeId.trim().isEmpty()) ? nodeId.trim() : null;
        if (assignNodeId != null)
        {
            TgClusterNode node = clusterNodeService.selectTgClusterNodeById(assignNodeId);
            if (node == null)
            {
                return error("指定的节点不存在: " + assignNodeId);
            }
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase().endsWith(".zip"))
        {
            return error("仅支持zip格式文件");
        }

        Path tempDir = null;
        try
        {
            // 1. Save zip to temp dir
            tempDir = Files.createTempDirectory("tg_import_");
            Path zipPath = tempDir.resolve(originalName);
            file.transferTo(zipPath.toFile());

            // 2. Recursively unzip
            Path extractDir = tempDir.resolve("extracted");
            Files.createDirectories(extractDir);
            recursiveUnzip(zipPath.toFile(), extractDir.toFile());

            // 3. Find .session/.json pairs (also supports session-only)
            Map<String, Path[]> pairs = findAccountPairs(extractDir.toFile());
            if (pairs.isEmpty())
            {
                return error("未找到有效的账号文件（需要 .session 文件，.json 文件可选）");
            }

            // 4. Get existing accounts for deduplication
            TgTelethonAccount queryAccount = new TgTelethonAccount();
            List<TgTelethonAccount> existingAccounts = telethonAccountService.selectTgTelethonAccountList(queryAccount);
            Set<String> existingPhones = new HashSet<>();
            for (TgTelethonAccount ea : existingAccounts)
            {
                if (ea.getPhone() != null)
                {
                    existingPhones.add(ea.getPhone());
                }
            }

            // 5. Generate batch
            String batchNo = UUID.randomUUID().toString().replace("-", "");
            List<TgImportAccount> importAccountList = new ArrayList<>();
            int skippedCount = 0;

            for (Map.Entry<String, Path[]> entry : pairs.entrySet())
            {
                String phone = entry.getKey();
                Path jsonPath = entry.getValue()[0];  // may be null if session-only
                Path sessionPath = entry.getValue()[1];

                // Skip if already exists in tg_telethon_account
                if (existingPhones.contains(phone))
                {
                    skippedCount++;
                    log.info("跳过已存在账号: {}", phone);
                    continue;
                }

                // Read file contents for DB storage
                String jsonContentStr = null;
                byte[] sessionContentBytes = null;
                if (jsonPath != null)
                {
                    jsonContentStr = Files.readString(jsonPath, StandardCharsets.UTF_8);
                }
                sessionContentBytes = Files.readAllBytes(sessionPath);

                // Create import account record
                TgImportAccount importAccount = new TgImportAccount();
                importAccount.setBatchNo(batchNo);
                importAccount.setPhone(phone);
                importAccount.setStatus("waiting");
                importAccountList.add(importAccount);

                // Insert into tg_telethon_account with status 'waiting', batch_no, and file contents
                TgTelethonAccount newAccount = new TgTelethonAccount();
                newAccount.setPhone(phone);
                newAccount.setBatchNo(batchNo);
                newAccount.setJsonContent(jsonContentStr);
                newAccount.setSessionContent(sessionContentBytes);
                // 指定节点时直接落库 node_id, 使其跳过自动分配任务, 直接归属该节点
                newAccount.setNodeId(assignNodeId);
                telethonAccountService.insertWaitingAccount(newAccount);
            }

            if (importAccountList.isEmpty())
            {
                return error("所有账号都已存在，无需导入（共 " + skippedCount + " 个已有账号）");
            }

            // 6. Save batch record
            TgImportBatch batch = new TgImportBatch();
            batch.setBatchNo(batchNo);
            batch.setTitle(title != null && !title.isEmpty() ? title : originalName);
            batch.setFileName(originalName);
            batch.setTotalCount(importAccountList.size());
            batch.setSuccessCount(0);
            batch.setFailedCount(0);
            batch.setWaitingCount(importAccountList.size());
            batch.setImportTime(new Date());
            batchService.insertTgImportBatch(batch);

            // 7. Save import account records
            if (!importAccountList.isEmpty())
            {
                accountService.batchInsertTgImportAccount(importAccountList);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("batchNo", batchNo);
            result.put("totalCount", importAccountList.size());
            result.put("skippedCount", skippedCount);
            result.put("phones", importAccountList.stream().map(TgImportAccount::getPhone).toArray());
            return success(result);
        }
        catch (Exception e)
        {
            log.error("导入账号失败", e);
            return error("导入失败: " + e.getMessage());
        }
        finally
        {
            // Clean up temp dir
            if (tempDir != null)
            {
                deleteDir(tempDir.toFile());
            }
        }
    }

    /**
     * Recursively unzip a file, handling nested archives
     */
    private void recursiveUnzip(File zipFile, File destDir) throws IOException
    {
        unzipFile(zipFile, destDir);

        // Find and unzip any nested zip files
        File[] files = destDir.listFiles();
        if (files != null)
        {
            for (File f : files)
            {
                if (f.isDirectory())
                {
                    recursiveUnzipDir(f);
                }
                else if (isZipFile(f))
                {
                    File nestedDir = new File(f.getParent(), f.getName().replaceAll("\\.[^.]+$", ""));
                    nestedDir.mkdirs();
                    recursiveUnzip(f, nestedDir);
                    f.delete();
                }
            }
        }
    }

    private void recursiveUnzipDir(File dir) throws IOException
    {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File f : files)
        {
            if (f.isDirectory())
            {
                recursiveUnzipDir(f);
            }
            else if (isZipFile(f))
            {
                File nestedDir = new File(f.getParent(), f.getName().replaceAll("\\.[^.]+$", ""));
                nestedDir.mkdirs();
                recursiveUnzip(f, nestedDir);
                f.delete();
            }
        }
    }

    private boolean isZipFile(File file)
    {
        String name = file.getName().toLowerCase();
        return name.endsWith(".zip");
    }

    private void unzipFile(File zipFile, File destDir) throws IOException
    {
        byte[] buffer = new byte[4096];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile), StandardCharsets.UTF_8))
        {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null)
            {
                String entryName = entry.getName();
                if (entryName.startsWith("__MACOSX") || entryName.contains(".DS_Store"))
                {
                    zis.closeEntry();
                    continue;
                }

                File newFile = new File(destDir, entryName);
                if (!newFile.getCanonicalPath().startsWith(destDir.getCanonicalPath() + File.separator)
                        && !newFile.getCanonicalPath().equals(destDir.getCanonicalPath()))
                {
                    zis.closeEntry();
                    continue;
                }

                if (entry.isDirectory())
                {
                    newFile.mkdirs();
                }
                else
                {
                    newFile.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(newFile))
                    {
                        int len;
                        while ((len = zis.read(buffer)) > 0)
                        {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }

    private Map<String, Path[]> findAccountPairs(File dir)
    {
        Map<String, Path[]> pairs = new LinkedHashMap<>();
        Map<String, Path> jsonFiles = new HashMap<>();
        Map<String, Path> sessionFiles = new HashMap<>();

        collectFiles(dir, jsonFiles, sessionFiles);

        // Support both .json+.session pairs and session-only files
        for (String phone : sessionFiles.keySet())
        {
            Path jsonPath = jsonFiles.get(phone);  // may be null
            Path sessionPath = sessionFiles.get(phone);
            pairs.put(phone, new Path[]{jsonPath, sessionPath});
        }
        return pairs;
    }

    private void collectFiles(File dir, Map<String, Path> jsonFiles, Map<String, Path> sessionFiles)
    {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File f : files)
        {
            if (f.isDirectory())
            {
                collectFiles(f, jsonFiles, sessionFiles);
            }
            else
            {
                String name = f.getName();
                if (name.endsWith(".json"))
                {
                    String phone = name.substring(0, name.length() - 5);
                    jsonFiles.put(phone, f.toPath());
                }
                else if (name.endsWith(".session"))
                {
                    String phone = name.substring(0, name.length() - 8);
                    sessionFiles.put(phone, f.toPath());
                }
            }
        }
    }

    /**
     * 给账号批次分配好友并添加
     */
    @PreAuthorize("@ss.hasPermi('tg:import:edit')")
    @PostMapping("/assignContacts")
    public AjaxResult assignContacts(@RequestBody Map<String, Object> params)
    {
        String accountBatchNo = (String) params.get("accountBatchNo");
        String contactBatchNo = (String) params.get("contactBatchNo");
        String mode = (String) params.get("mode"); // "average" or "fixed"
        Integer fixedCount = params.get("fixedCount") != null ? Integer.parseInt(params.get("fixedCount").toString()) : null;
        String addMethod = params.get("addMethod") != null ? (String) params.get("addMethod") : "one_by_one";
        String contactType = normalizeContactType(params.get("contactType"));

        if (accountBatchNo == null || contactBatchNo == null || mode == null)
        {
            return error("参数不完整");
        }

        // Get account batch info
        TgImportBatch accountBatch = batchService.selectTgImportBatchByBatchNo(accountBatchNo);
        String accountBatchTitle = accountBatch != null ? accountBatch.getTitle() : accountBatchNo;

        // Get contact batch info
        TgContactImportBatch contactBatch = contactBatchService.selectList(new TgContactImportBatch() {{ setBatchNo(contactBatchNo); }}).stream().findFirst().orElse(null);
        String contactBatchTitle = contactBatch != null ? contactBatch.getTitle() : contactBatchNo;

        // Get accounts in this batch (not deleted, online only)
        TgTelethonAccount query = new TgTelethonAccount();
        query.setBatchNo(accountBatchNo);
        List<TgTelethonAccount> accounts = telethonAccountService.selectTgTelethonAccountList(query);
        accounts.removeIf(a -> (a.getIsDeleted() != null && a.getIsDeleted() == 1) || !"online".equals(a.getStatus()));

        // Filter out restricted or frozen accounts — do not assign contacts to them
        List<String> restrictedPhones = accounts.stream()
            .filter(TgImportController::isBlocked)
            .map(TgTelethonAccount::getPhone)
            .collect(java.util.stream.Collectors.toList());
        accounts.removeIf(TgImportController::isBlocked);

        if (accounts.isEmpty()) {
            if (!restrictedPhones.isEmpty()) {
                return error("该批次下在线账号均被限制或冻结，无法分配好友");
            }
            return error("该批次下没有在线的账号");
        }

        // Validate all accounts have node_id assigned
        List<String> noNodeAccounts = accounts.stream()
            .filter(a -> a.getNodeId() == null || a.getNodeId().isEmpty())
            .map(TgTelethonAccount::getPhone)
            .collect(java.util.stream.Collectors.toList());
        if (!noNodeAccounts.isEmpty()) {
            return error("以下账号未分配节点，无法分配好友: " + String.join(", ", noNodeAccounts));
        }

        return doAssignToAccounts(accounts, contactBatchNo, mode, fixedCount, addMethod, contactType,
            "import", accountBatchNo, accountBatchTitle, null, null);
    }

    /**
     * 给账号分组分配好友并添加(与账号导入页的添加好友逻辑一致, 目标账号来自分组)
     */
    @PreAuthorize("@ss.hasPermi('tg:accountGroup:assign')")
    @PostMapping("/assignContactsByGroup")
    public AjaxResult assignContactsByGroup(@RequestBody Map<String, Object> params)
    {
        Integer groupId = params.get("groupId") != null ? Integer.parseInt(params.get("groupId").toString()) : null;
        String contactBatchNo = (String) params.get("contactBatchNo");
        String mode = (String) params.get("mode"); // "average" or "fixed"
        Integer fixedCount = params.get("fixedCount") != null ? Integer.parseInt(params.get("fixedCount").toString()) : null;
        String addMethod = params.get("addMethod") != null ? (String) params.get("addMethod") : "one_by_one";
        String contactType = normalizeContactType(params.get("contactType"));

        if (groupId == null || contactBatchNo == null || mode == null)
        {
            return error("参数不完整");
        }

        // Get group info
        com.ruoyi.system.domain.TgAccountGroup group = accountGroupService.selectTgAccountGroupById(groupId);
        if (group == null)
        {
            return error("账号分组不存在");
        }
        String groupName = group.getGroupName();

        // Get accounts in this group (not deleted, online only)
        TgTelethonAccount query = new TgTelethonAccount();
        query.setGroupId(groupId);
        List<TgTelethonAccount> accounts = telethonAccountService.selectTgTelethonAccountList(query);
        accounts.removeIf(a -> (a.getIsDeleted() != null && a.getIsDeleted() == 1) || !"online".equals(a.getStatus()));

        // Filter out restricted or frozen accounts — do not assign contacts to them
        List<String> restrictedPhones = accounts.stream()
            .filter(TgImportController::isBlocked)
            .map(TgTelethonAccount::getPhone)
            .collect(java.util.stream.Collectors.toList());
        accounts.removeIf(TgImportController::isBlocked);

        if (accounts.isEmpty()) {
            if (!restrictedPhones.isEmpty()) {
                return error("该分组下在线账号均被限制或冻结，无法分配好友");
            }
            return error("该分组下没有在线的账号");
        }

        // Validate all accounts have node_id assigned
        List<String> noNodeAccounts = accounts.stream()
            .filter(a -> a.getNodeId() == null || a.getNodeId().isEmpty())
            .map(TgTelethonAccount::getPhone)
            .collect(java.util.stream.Collectors.toList());
        if (!noNodeAccounts.isEmpty()) {
            return error("以下账号未分配节点，无法分配好友: " + String.join(", ", noNodeAccounts));
        }

        return doAssignToAccounts(accounts, contactBatchNo, mode, fixedCount, addMethod, contactType,
            "group", null, null, groupId, groupName);
    }

    /**
     * 受限或被 TG 冻结的账号一律不参与任何操作。
     */
    private static boolean isBlocked(TgTelethonAccount a)
    {
        return Boolean.TRUE.equals(a.getIsRestricted()) || Boolean.TRUE.equals(a.getIsFrozen());
    }

    /**
     * 好友类型: fake-伪好友(节点仅 resolvePhone 解析后入库, 不加入TG联系人), 其余一律按 real-好友处理。
     */
    private String normalizeContactType(Object raw)
    {
        return "fake".equals(raw) ? "fake" : "real";
    }

    /**
     * 共享的好友分配逻辑: 对给定账号集合按分配方式写入 pending 日志, 实际添加由 Telethon 轮询完成。
     * source=import 时携带账号批次信息; source=group 时携带分组信息。
     */
    private AjaxResult doAssignToAccounts(List<TgTelethonAccount> accounts, String contactBatchNo,
            String mode, Integer fixedCount, String addMethod, String contactType,
            String source, String accountBatchNo, String accountBatchTitle,
            Integer groupId, String groupName)
    {
        // Get contact batch info
        TgContactImportBatch contactBatch = contactBatchService.selectList(new TgContactImportBatch() {{ setBatchNo(contactBatchNo); }}).stream().findFirst().orElse(null);
        String contactBatchTitle = contactBatch != null ? contactBatch.getTitle() : contactBatchNo;

        // Get contact records
        List<TgContactImportRecord> contacts = contactRecordService.selectByBatchNo(contactBatchNo);
        if (contacts.isEmpty()) return error("好友批次中没有数据");

        // Determine import type from batch
        String importType = contactBatch != null && contactBatch.getImportType() != null ? contactBatch.getImportType() : "phone";

        // --- Load existing friends for all accounts in one batched query ---
        Map<Integer, Set<String>> existingFriends = new HashMap<>();
        List<Integer> accountIds = new ArrayList<>();
        for (TgTelethonAccount acc : accounts)
        {
            existingFriends.put(acc.getId(), new HashSet<>());
            accountIds.add(acc.getId());
        }
        List<TgContact> allFriends = tgContactMapper.selectContactKeysByAccountIds(accountIds);
        for (TgContact f : allFriends)
        {
            Set<String> friendSet = existingFriends.get(f.getTgAccountId());
            if (friendSet == null) continue;
            if (f.getPhoneNumber() != null && !f.getPhoneNumber().isEmpty())
                friendSet.add("phone:" + f.getPhoneNumber());
            if (f.getUsername() != null && !f.getUsername().isEmpty())
                friendSet.add("username:" + f.getUsername().toLowerCase());
        }

        // --- Load previously assigned contact keys (distinct, no full-row/join load) ---
        Set<String> previouslyAssigned = new HashSet<>();
        for (String p : contactAssignLogMapper.selectAssignedContactPhones())
            previouslyAssigned.add("phone:" + p);
        for (String u : contactAssignLogMapper.selectAssignedContactUsernames())
            previouslyAssigned.add("username:" + u.toLowerCase());

        // --- Sort contacts: never-assigned first ---
        List<TgContactImportRecord> neverAssignedList = new ArrayList<>();
        List<TgContactImportRecord> alreadyAssignedList = new ArrayList<>();
        for (TgContactImportRecord c : contacts)
        {
            String key = "username".equals(importType)
                ? "username:" + (c.getUsername() != null ? c.getUsername().toLowerCase() : "")
                : "phone:" + (c.getPhone() != null ? c.getPhone() : "");
            if (previouslyAssigned.contains(key))
                alreadyAssignedList.add(c);
            else
                neverAssignedList.add(c);
        }
        List<TgContactImportRecord> sortedContacts = new ArrayList<>(neverAssignedList);
        sortedContacts.addAll(alreadyAssignedList);

        // --- Smart distribution: skip accounts where contact is already a friend ---
        Map<TgTelethonAccount, List<TgContactImportRecord>> assignments = new LinkedHashMap<>();
        for (TgTelethonAccount acc : accounts) {
            assignments.put(acc, new ArrayList<>());
        }

        if ("average".equals(mode))
        {
            int[] assignCounts = new int[accounts.size()];
            for (TgContactImportRecord contact : sortedContacts)
            {
                int bestIdx = -1;
                int bestCount = Integer.MAX_VALUE;
                for (int i = 0; i < accounts.size(); i++)
                {
                    if (!isContactFriendOfAccount(existingFriends, accounts.get(i).getId(), contact, importType)
                        && assignCounts[i] < bestCount)
                    {
                        bestCount = assignCounts[i];
                        bestIdx = i;
                    }
                }
                if (bestIdx >= 0)
                {
                    assignments.get(accounts.get(bestIdx)).add(contact);
                    assignCounts[bestIdx]++;
                }
                // else: contact is already a friend of all accounts, skip
            }
        }
        else if ("fixed".equals(mode))
        {
            if (fixedCount == null || fixedCount <= 0) return error("请输入每个账号分配的好友数");
            LinkedList<TgContactImportRecord> remaining = new LinkedList<>(sortedContacts);
            for (TgTelethonAccount acc : accounts)
            {
                int assigned = 0;
                Iterator<TgContactImportRecord> it = remaining.iterator();
                while (it.hasNext() && assigned < fixedCount)
                {
                    TgContactImportRecord contact = it.next();
                    if (!isContactFriendOfAccount(existingFriends, acc.getId(), contact, importType))
                    {
                        assignments.get(acc).add(contact);
                        it.remove();
                        assigned++;
                    }
                }
            }
        }

        // Only distribute and write assign logs with status=pending
        // Actual add-contact is done by Telethon polling timer
        int totalAssigned = 0;
        final int INSERT_BATCH_SIZE = 1000;
        List<TgContactAssignLog> pendingBatch = new ArrayList<>();

        for (Map.Entry<TgTelethonAccount, List<TgContactImportRecord>> entry : assignments.entrySet())
        {
            TgTelethonAccount acc = entry.getKey();
            List<TgContactImportRecord> records = entry.getValue();
            if (records.isEmpty()) continue;

            // 写入日志时携带账号自身的批次信息; import 来源沿用传入批次, group 来源取账号各自的批次
            String effBatchNo = accountBatchNo != null ? accountBatchNo : acc.getBatchNo();
            String effBatchTitle = accountBatchTitle != null ? accountBatchTitle
                : (acc.getBatchTitle() != null ? acc.getBatchTitle() : acc.getBatchNo());

            for (TgContactImportRecord record : records)
            {
                TgContactAssignLog logEntry = new TgContactAssignLog();
                logEntry.setAccountBatchNo(effBatchNo);
                logEntry.setAccountBatchTitle(effBatchTitle);
                logEntry.setAccountId(acc.getId());
                logEntry.setAccountPhone(acc.getPhone());
                logEntry.setContactBatchNo(contactBatchNo);
                logEntry.setContactBatchTitle(contactBatchTitle);
                if ("username".equals(importType))
                {
                    logEntry.setContactUsername(record.getUsername());
                }
                else
                {
                    logEntry.setContactPhone(record.getPhone());
                }
                logEntry.setStatus("pending");
                logEntry.setRetryCount(0);
                logEntry.setAddMethod(addMethod);
                logEntry.setContactType(contactType);
                logEntry.setNodeId(acc.getNodeId());
                logEntry.setSource(source);
                logEntry.setGroupId(groupId);
                logEntry.setGroupName(groupName);

                pendingBatch.add(logEntry);
                totalAssigned++;

                if (pendingBatch.size() >= INSERT_BATCH_SIZE)
                {
                    contactAssignLogMapper.batchInsert(pendingBatch);
                    pendingBatch.clear();
                }
            }
        }
        if (!pendingBatch.isEmpty())
        {
            contactAssignLogMapper.batchInsert(pendingBatch);
            pendingBatch.clear();
        }

        // Refresh contact batch stats
        contactBatchMapper.refreshStats(contactBatchNo);

        Map<String, Object> data = new HashMap<>();
        data.put("totalAssigned", totalAssigned);

        data.put("message", "分配成功，等待添加");

        return success(data);
    }

    /**
     * 刷新好友批次统计数据（Telethon调用）
     */
    @GetMapping("/refreshContactBatchStats")
    public AjaxResult refreshContactBatchStats(@RequestParam String batchNo)
    {
        if (batchNo == null || batchNo.isEmpty()) return error("batchNo不能为空");
        contactBatchMapper.refreshStats(batchNo);
        return success();
    }

    /**
     * 查询好友分配日志
     */
    @PreAuthorize("@ss.hasPermi('tg:import:list')")
    @GetMapping("/contactAssignLog")
    public TableDataInfo contactAssignLog(TgContactAssignLog logQuery)
    {
        startPage();
        List<TgContactAssignLog> list = contactAssignLogMapper.selectList(logQuery);
        return getDataTable(list);
    }

    /**
     * 导出好友分配日志
     */
    @PreAuthorize("@ss.hasPermi('tg:import:list')")
    @PostMapping("/contactAssignLog/export")
    public void exportContactAssignLog(jakarta.servlet.http.HttpServletResponse response, TgContactAssignLog logQuery)
    {
        List<TgContactAssignLog> list = contactAssignLogMapper.selectList(logQuery);
        List<com.ruoyi.system.domain.vo.TgContactAssignLogExport> exportList = new ArrayList<>();
        for (TgContactAssignLog l : list)
        {
            com.ruoyi.system.domain.vo.TgContactAssignLogExport vo = new com.ruoyi.system.domain.vo.TgContactAssignLogExport();
            vo.setId(l.getId());
            vo.setSourceLabel("group".equals(l.getSource()) ? "账号分组" : "账号导入");
            vo.setGroupName(l.getGroupName());
            vo.setAccountBatchTitle(l.getAccountBatchTitle());
            vo.setAccountPhone(l.getAccountPhone());
            vo.setContactBatchTitle(l.getContactBatchTitle());
            vo.setContactInfo(l.getContactPhone() != null && !l.getContactPhone().isEmpty() ? l.getContactPhone() : l.getContactUsername());
            vo.setContactTypeLabel("fake".equals(l.getContactType()) ? "伪好友" : "好友");
            String statusLabel = "pending".equals(l.getStatus()) ? "待办" : "success".equals(l.getStatus()) ? "成功" : "failed".equals(l.getStatus()) ? "失败" : "skipped".equals(l.getStatus()) ? "跳过" : l.getStatus();
            vo.setStatusLabel(statusLabel);
            vo.setRetryCount(l.getRetryCount());
            vo.setErrorReason(l.getErrorReason());
            vo.setRemark(l.getRemark());
            vo.setCreateTime(l.getCreateTime());
            exportList.add(vo);
        }
        com.ruoyi.common.utils.poi.ExcelUtil<com.ruoyi.system.domain.vo.TgContactAssignLogExport> util = new com.ruoyi.common.utils.poi.ExcelUtil<>(com.ruoyi.system.domain.vo.TgContactAssignLogExport.class);
        util.exportExcel(response, exportList, "好友分配日志");
    }

    /**
     * 查询登录日志列表
     */
    @PreAuthorize("@ss.hasPermi('tg:import:list')")
    @GetMapping("/loginLog")
    public TableDataInfo loginLog(TgLoginLog logQuery)
    {
        startPage();
        List<TgLoginLog> list = loginLogMapper.selectList(logQuery);
        return getDataTable(list);
    }

    /**
     * 导出登录日志
     */
    @PreAuthorize("@ss.hasPermi('tg:import:list')")
    @PostMapping("/loginLog/export")
    public void exportLoginLog(jakarta.servlet.http.HttpServletResponse response, TgLoginLog logQuery)
    {
        List<TgLoginLog> list = loginLogMapper.selectList(logQuery);
        List<com.ruoyi.system.domain.vo.TgLoginLogExport> exportList = new ArrayList<>();
        for (TgLoginLog l : list)
        {
            com.ruoyi.system.domain.vo.TgLoginLogExport vo = new com.ruoyi.system.domain.vo.TgLoginLogExport();
            vo.setId(l.getId());
            vo.setPhone(l.getPhone());
            vo.setNickname(l.getNickname());
            vo.setResult(l.getResult());
            vo.setReason(l.getReason());
            vo.setProxyInfo(l.getProxyInfo());
            vo.setNodeId(l.getNodeId());
            vo.setLoginTime(l.getLoginTime());
            exportList.add(vo);
        }
        com.ruoyi.common.utils.poi.ExcelUtil<com.ruoyi.system.domain.vo.TgLoginLogExport> util = new com.ruoyi.common.utils.poi.ExcelUtil<>(com.ruoyi.system.domain.vo.TgLoginLogExport.class);
        util.exportExcel(response, exportList, "登录日志");
    }

    /**
     * 查询发送失败日志列表
     */
    @PreAuthorize("@ss.hasPermi('tg:import:list')")
    @GetMapping("/sendFailLog")
    public TableDataInfo sendFailLog(TgSendFailLog logQuery)
    {
        // If the 好友 input looks like a phone number, search by friend phone instead of nickname
        String friendInput = logQuery.getFriendNickname();
        if (friendInput != null && !friendInput.isEmpty() && friendInput.matches("\\+?\\d+"))
        {
            logQuery.setFriendPhone(friendInput.replaceFirst("^\\+", ""));
            logQuery.setFriendNickname(null);
        }
        startPage();
        List<TgSendFailLog> list = sendFailLogMapper.selectList(logQuery);
        return getDataTable(list);
    }

    /**
     * 导出发送失败日志
     */
    @PreAuthorize("@ss.hasPermi('tg:import:list')")
    @PostMapping("/sendFailLog/export")
    public void exportSendFailLog(jakarta.servlet.http.HttpServletResponse response, TgSendFailLog logQuery)
    {
        String friendInput = logQuery.getFriendNickname();
        if (friendInput != null && !friendInput.isEmpty() && friendInput.matches("\\+?\\d+"))
        {
            logQuery.setFriendPhone(friendInput.replaceFirst("^\\+", ""));
            logQuery.setFriendNickname(null);
        }
        List<TgSendFailLog> list = sendFailLogMapper.selectList(logQuery);
        List<com.ruoyi.system.domain.vo.TgSendFailLogExport> exportList = new ArrayList<>();
        for (TgSendFailLog l : list)
        {
            com.ruoyi.system.domain.vo.TgSendFailLogExport vo = new com.ruoyi.system.domain.vo.TgSendFailLogExport();
            vo.setId(l.getId());
            vo.setPhone(l.getPhone());
            vo.setNickname(l.getNickname());
            vo.setFriendNickname(l.getFriendNickname());
            vo.setFriendPhone(l.getFriendPhone());
            vo.setContentType(l.getContentType());
            vo.setContent(l.getContent());
            vo.setErrorReason(l.getErrorReason());
            vo.setNodeId(l.getNodeId());
            vo.setSendTime(l.getSendTime());
            exportList.add(vo);
        }
        com.ruoyi.common.utils.poi.ExcelUtil<com.ruoyi.system.domain.vo.TgSendFailLogExport> util = new com.ruoyi.common.utils.poi.ExcelUtil<>(com.ruoyi.system.domain.vo.TgSendFailLogExport.class);
        util.exportExcel(response, exportList, "发送失败日志");
    }

    /**
     * 查询自动回复日志列表
     */
    @PreAuthorize("@ss.hasPermi('tg:import:list')")
    @GetMapping("/autoReplyLog")
    public TableDataInfo autoReplyLog(TgAutoReplyLog logQuery)
    {
        // If friendNickname looks like a phone number, search friend_phone instead (indexed, much faster)
        String friendInput = logQuery.getFriendNickname();
        if (friendInput != null && !friendInput.isEmpty() && friendInput.matches("\\+?\\d+"))
        {
            logQuery.setFriendPhone(friendInput.replaceFirst("^\\+", ""));
            logQuery.setFriendNickname(null);
        }
        startPage();
        List<TgAutoReplyLog> list = autoReplyLogMapper.selectList(logQuery);
        return getDataTable(list);
    }

    /**
     * 导出自动回复日志
     */
    @PreAuthorize("@ss.hasPermi('tg:import:list')")
    @PostMapping("/autoReplyLog/export")
    public void exportAutoReplyLog(jakarta.servlet.http.HttpServletResponse response, TgAutoReplyLog logQuery)
    {
        String friendInput = logQuery.getFriendNickname();
        if (friendInput != null && !friendInput.isEmpty() && friendInput.matches("\\+?\\d+"))
        {
            logQuery.setFriendPhone(friendInput.replaceFirst("^\\+", ""));
            logQuery.setFriendNickname(null);
        }
        List<TgAutoReplyLog> list = autoReplyLogMapper.selectList(logQuery);
        List<com.ruoyi.system.domain.vo.TgAutoReplyLogExport> exportList = new ArrayList<>();
        for (TgAutoReplyLog l : list)
        {
            com.ruoyi.system.domain.vo.TgAutoReplyLogExport vo = new com.ruoyi.system.domain.vo.TgAutoReplyLogExport();
            vo.setId(l.getId());
            vo.setAccountPhone(l.getAccountPhone());
            vo.setAccountNickname(l.getAccountNickname());
            vo.setFriendNickname(l.getFriendNickname());
            vo.setFriendPhone(l.getFriendPhone());
            String triggerLabel = "incoming".equals(l.getTriggerType()) ? "收到消息" : "polling".equals(l.getTriggerType()) ? "定时轮询" : l.getTriggerType();
            vo.setTriggerTypeLabel(triggerLabel);
            vo.setState(l.getState());
            vo.setReplyContent(l.getReplyContent());
            String resultLabel = "success".equals(l.getSendResult()) ? "成功" : "failed".equals(l.getSendResult()) ? "失败" : "no_reply".equals(l.getSendResult()) ? "无回复" : "api_error".equals(l.getSendResult()) ? "API错误" : l.getSendResult();
            vo.setSendResultLabel(resultLabel);
            vo.setErrorReason(l.getErrorReason());
            vo.setNodeId(l.getNodeId());
            vo.setCreateTime(l.getCreateTime());
            exportList.add(vo);
        }
        com.ruoyi.common.utils.poi.ExcelUtil<com.ruoyi.system.domain.vo.TgAutoReplyLogExport> util = new com.ruoyi.common.utils.poi.ExcelUtil<>(com.ruoyi.system.domain.vo.TgAutoReplyLogExport.class);
        util.exportExcel(response, exportList, "自动回复日志");
    }

    private String callTelethonAddContact(String phone, String contactPhone) throws Exception
    {
        URL url = new URL(telethonUrl + "/api/add-contact");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        String body = "{\"phone\":\"" + phone + "\",\"contact_phone\":\"" + contactPhone + "\"}";
        try (OutputStream os = conn.getOutputStream())
        {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name()))
        {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    private void deleteDir(File dir)
    {
        if (dir == null || !dir.exists()) return;
        File[] files = dir.listFiles();
        if (files != null)
        {
            for (File f : files)
            {
                if (f.isDirectory()) deleteDir(f);
                else f.delete();
            }
        }
        dir.delete();
    }

    private boolean isContactFriendOfAccount(Map<Integer, Set<String>> existingFriends, Integer accountId,
                                              TgContactImportRecord contact, String importType)
    {
        Set<String> friends = existingFriends.get(accountId);
        if (friends == null || friends.isEmpty()) return false;
        if ("username".equals(importType))
        {
            String username = contact.getUsername();
            return username != null && !username.isEmpty() && friends.contains("username:" + username.toLowerCase());
        }
        else
        {
            String phone = contact.getPhone();
            return phone != null && !phone.isEmpty() && friends.contains("phone:" + phone);
        }
    }
}
