package com.ruoyi.web.controller.tg;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.TgTelethonAccount;
import com.ruoyi.system.domain.TgProxyIp;
import com.ruoyi.system.domain.TgProxyGroup;
import com.ruoyi.system.domain.TgProxyAssignLog;
import com.ruoyi.system.domain.TgImportBatch;
import com.ruoyi.system.service.ITgTelethonAccountService;
import com.ruoyi.system.service.ITgImportBatchService;
import com.ruoyi.system.mapper.TgProxyIpMapper;
import com.ruoyi.system.mapper.TgProxyAssignLogMapper;
import com.ruoyi.system.mapper.TgProxyGroupMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Telegram账号管理Controller (对接 tg-client-telethon)
 */
@RestController
@RequestMapping("/tg/account")
public class TgAccountConfigController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(TgAccountConfigController.class);

    // 冻结账号必须保持受限状态，因此解除限制对已冻结账号无效
    private static final String FROZEN_KEEP_RESTRICTED_TIP = "账号已被TG冻结，冻结账号必须保持受限，无法解除限制";

    // Serialize batch proxy assignment per IP-group to avoid concurrent/duplicate
    // requests (front-end retries on timeout) double-assigning IPs and clobbering
    // the non-atomic bind counters.
    private static final java.util.concurrent.ConcurrentHashMap<String, Object> BATCH_ASSIGN_LOCKS =
            new java.util.concurrent.ConcurrentHashMap<>();

    @Value("${tg-client-telethon.url:http://localhost:8807}")
    private String telethonUrl;

    @Autowired
    private ITgTelethonAccountService tgTelethonAccountService;

    @Autowired
    private TgProxyIpMapper tgProxyIpMapper;

    @Autowired
    private TgProxyAssignLogMapper tgProxyAssignLogMapper;

    @Autowired
    private TgProxyGroupMapper tgProxyGroupMapper;

    @Autowired
    private ITgImportBatchService tgImportBatchService;

    /**
     * 查询账号列表
     */
    @PreAuthorize("@ss.hasPermi('tg:account:list')")
    @GetMapping("/list")
    public TableDataInfo list(TgTelethonAccount account)
    {
        startPage();
        List<TgTelethonAccount> list = tgTelethonAccountService.selectTgTelethonAccountList(account);
        return getDataTable(list);
    }

    /**
     * 导出账号列表(按当前筛选条件)
     */
    @PreAuthorize("@ss.hasPermi('tg:account:list')")
    @PostMapping("/export")
    public void export(jakarta.servlet.http.HttpServletResponse response, TgTelethonAccount account)
    {
        List<TgTelethonAccount> list = tgTelethonAccountService.selectTgTelethonAccountList(account);
        java.util.List<com.ruoyi.system.domain.vo.TgTelethonAccountExport> exportList = new java.util.ArrayList<>();
        for (TgTelethonAccount a : list)
        {
            com.ruoyi.system.domain.vo.TgTelethonAccountExport vo = new com.ruoyi.system.domain.vo.TgTelethonAccountExport();
            vo.setId(a.getId());
            vo.setPhone(a.getPhone());
            vo.setNickname(a.getNickname());
            vo.setUsername(a.getUsername());
            vo.setTgUserId(a.getTgUserId());
            vo.setCountry(a.getCountry());
            vo.setStatus(statusText(a.getStatus()));
            vo.setBatchTitle(a.getBatchTitle());
            vo.setGroupName(a.getGroupName());
            vo.setProxyGroupTitle(a.getProxyGroupTitle());
            vo.setAutoReply(Boolean.TRUE.equals(a.getAutoReply()) ? "开" : "关");
            vo.setIsRestricted(Boolean.TRUE.equals(a.getIsRestricted()) ? "是" : "否");
            vo.setIsFrozen(Boolean.TRUE.equals(a.getIsFrozen()) ? "是" : "否");
            vo.setTotalMsgCount(a.getTotalMsgCount());
            vo.setSentMsgCount(a.getSentMsgCount());
            vo.setRecvMsgCount(a.getRecvMsgCount());
            vo.setNodeId(a.getNodeId());
            vo.setLastLoginTime(a.getLastLoginTime());
            vo.setCreateTime(a.getCreateTime());
            exportList.add(vo);
        }
        com.ruoyi.common.utils.poi.ExcelUtil<com.ruoyi.system.domain.vo.TgTelethonAccountExport> util =
            new com.ruoyi.common.utils.poi.ExcelUtil<>(com.ruoyi.system.domain.vo.TgTelethonAccountExport.class);
        util.exportExcel(response, exportList, "账号列表");
    }

    private String statusText(String status)
    {
        if (status == null) return "";
        switch (status)
        {
            case "online": return "在线";
            case "offline": return "离线";
            case "waiting": return "等待登录";
            case "login1": return "登录中(代理)";
            case "login2": return "登录中(无代理)";
            case "waitLogout": return "等待登出";
            case "failed": return "失败";
            case "banned": return "已注销";
            default: return status;
        }
    }

    /**
     * 获取账号详细信息
     */
    @PreAuthorize("@ss.hasPermi('tg:account:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        return success(tgTelethonAccountService.selectTgTelethonAccountById(id));
    }

    /**
     * 删除账号
     */
    @PreAuthorize("@ss.hasPermi('tg:account:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(tgTelethonAccountService.deleteTgTelethonAccountByIds(ids));
    }

    /**
     * 触发登录 - 设置账号状态为login1(使用代理)
     * 如果没有设置代理，则不做任何处理
     */
    @PreAuthorize("@ss.hasPermi('tg:account:edit')")
    @PutMapping("/login/{id}")
    public AjaxResult triggerLogin(@PathVariable("id") Integer id)
    {
        TgTelethonAccount account = tgTelethonAccountService.selectTgTelethonAccountById(id);
        if (account == null)
        {
            return error("账号不存在");
        }
        if ("online".equals(account.getStatus()))
        {
            return error("该账号已登录");
        }
        if (account.getProxyHost() == null || account.getProxyHost().isEmpty())
        {
            return error("未配置代理IP，请先为该账号配置代理IP。");
        }
        tgTelethonAccountService.updateStatusById(id, "login1");
        return success("设置成功等待登录");
    }

    /**
     * 无代理登录 - 设置账号状态为login2(不使用代理)
     */
    @PreAuthorize("@ss.hasPermi('tg:account:edit')")
    @PutMapping("/loginNoProxy/{id}")
    public AjaxResult loginNoProxy(@PathVariable("id") Integer id)
    {
        TgTelethonAccount account = tgTelethonAccountService.selectTgTelethonAccountById(id);
        if (account == null)
        {
            return error("账号不存在");
        }
        if ("online".equals(account.getStatus()))
        {
            return error("该账号已登录");
        }
        tgTelethonAccountService.updateStatusById(id, "login2");
        return success("设置成功等待登录");
    }

    /**
     * 登出账号 - 设置状态为 waitLogout(等待登出)，节点定时器轮询到后真正登出并释放资源
     */
    @PreAuthorize("@ss.hasPermi('tg:account:edit')")
    @PutMapping("/logout/{id}")
    public AjaxResult logoutAccount(@PathVariable("id") Integer id)
    {
        TgTelethonAccount account = tgTelethonAccountService.selectTgTelethonAccountById(id);
        if (account == null)
        {
            return error("账号不存在");
        }
        tgTelethonAccountService.updateStatusById(id, "waitLogout");
        return success("设置成功，等待节点登出");
    }

    /**
     * 批量登录 - 按批次将所有有代理的账号设置为login1状态
     */
    @PreAuthorize("@ss.hasPermi('tg:account:edit')")
    @PutMapping("/loginBatch/{batchNo}")
    public AjaxResult loginBatch(@PathVariable("batchNo") String batchNo)
    {
        TgTelethonAccount query = new TgTelethonAccount();
        if (!"all".equals(batchNo))
        {
            query.setBatchNo(batchNo);
        }
        List<TgTelethonAccount> accounts = tgTelethonAccountService.selectTgTelethonAccountList(query);

        int setCount = 0;
        for (TgTelethonAccount acc : accounts)
        {
            if (acc.getIsDeleted() != null && acc.getIsDeleted() == 1) continue;
            if ("online".equals(acc.getStatus())) continue;
            if (acc.getProxyHost() != null && !acc.getProxyHost().isEmpty())
            {
                tgTelethonAccountService.updateStatusById(acc.getId(), "login1");
                setCount++;
            }
        }
        return success("设置成功等待登录，共设置 " + setCount + " 个账号");
    }

    /**
     * 账号分组批量登录 - 按账号分组将所有有代理的账号设置为login1状态
     */
    @PreAuthorize("@ss.hasPermi('tg:account:edit')")
    @PutMapping("/loginByGroup/{groupId}")
    public AjaxResult loginByGroup(@PathVariable("groupId") Integer groupId)
    {
        TgTelethonAccount query = new TgTelethonAccount();
        query.setGroupId(groupId);
        List<TgTelethonAccount> accounts = tgTelethonAccountService.selectTgTelethonAccountList(query);

        int setCount = 0;
        for (TgTelethonAccount acc : accounts)
        {
            if (acc.getIsDeleted() != null && acc.getIsDeleted() == 1) continue;
            if ("online".equals(acc.getStatus())) continue;
            if (acc.getProxyHost() != null && !acc.getProxyHost().isEmpty())
            {
                tgTelethonAccountService.updateStatusById(acc.getId(), "login1");
                setCount++;
            }
        }
        return success("设置成功等待登录，共设置 " + setCount + " 个账号");
    }

    /**
     * 批量登出 - 设置状态为 waitLogout(等待登出)，节点定时器轮询到后真正登出并释放资源
     */
    @PreAuthorize("@ss.hasPermi('tg:account:edit')")
    @PutMapping("/logoutBatch/{batchNo}")
    public AjaxResult logoutBatch(@PathVariable("batchNo") String batchNo)
    {
        TgTelethonAccount query = new TgTelethonAccount();
        if (!"all".equals(batchNo))
        {
            query.setBatchNo(batchNo);
        }
        query.setStatus("online");
        List<TgTelethonAccount> accounts = tgTelethonAccountService.selectTgTelethonAccountList(query);

        int setCount = 0;
        for (TgTelethonAccount acc : accounts)
        {
            tgTelethonAccountService.updateStatusById(acc.getId(), "waitLogout");
            setCount++;
        }
        return success("设置成功，共设置 " + setCount + " 个账号");
    }

    /**
     * 账号分组登出 - 按账号分组将在线账号设置为 waitLogout(等待登出)，节点定时器轮询到后真正登出并释放资源
     */
    @PreAuthorize("@ss.hasPermi('tg:account:edit')")
    @PutMapping("/logoutByGroup/{groupId}")
    public AjaxResult logoutByGroup(@PathVariable("groupId") Integer groupId)
    {
        TgTelethonAccount query = new TgTelethonAccount();
        query.setGroupId(groupId);
        query.setStatus("online");
        List<TgTelethonAccount> accounts = tgTelethonAccountService.selectTgTelethonAccountList(query);

        int setCount = 0;
        for (TgTelethonAccount acc : accounts)
        {
            tgTelethonAccountService.updateStatusById(acc.getId(), "waitLogout");
            setCount++;
        }
        return success("设置成功，共设置 " + setCount + " 个账号");
    }

    /**
     * 获取网页端 WebSocket token
     */
    @PreAuthorize("@ss.hasPermi('tg:account:query')")
    @GetMapping("/wsToken/{id}")
    public AjaxResult getWsToken(@PathVariable("id") Integer id)
    {
        TgTelethonAccount account = tgTelethonAccountService.selectTgTelethonAccountById(id);
        if (account == null)
        {
            return error("账号不存在");
        }
        try
        {
            String apiUrl = telethonUrl + "/api/token?account_id=" + id + "&phone=" + account.getPhone();
            String response = httpPost(apiUrl, "");
            return success((Object) response);
        }
        catch (Exception e)
        {
            log.error("获取wsToken失败", e);
            return error("获取token失败: " + e.getMessage());
        }
    }

    /**
     * 查看账号绑定的代理IP信息
     */
    @PreAuthorize("@ss.hasPermi('tg:account:query')")
    @GetMapping("/proxy/{id}")
    public AjaxResult getProxyInfo(@PathVariable("id") Integer id)
    {
        TgTelethonAccount account = tgTelethonAccountService.selectTgTelethonAccountById(id);
        if (account == null)
        {
            return error("账号不存在");
        }
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("proxyIpId", account.getProxyIpId());
        data.put("proxyGroupNo", account.getProxyGroupNo());
        data.put("proxyGroupTitle", account.getProxyGroupTitle());
        data.put("proxyUrl", account.getProxyUrl());
        data.put("proxyProtocol", account.getProxyProtocol());
        data.put("proxyHost", account.getProxyHost());
        data.put("proxyPort", account.getProxyPort());
        data.put("proxyUsername", account.getProxyUsername());
        data.put("proxyPassword", account.getProxyPassword());
        return success(data);
    }

    /**
     * 自动选择代理IP — 在指定IP组中自动匹配一个可用IP
     */
    @PreAuthorize("@ss.hasPermi('tg:account:edit')")
    @PutMapping("/proxy/auto/{id}")
    public AjaxResult autoSelectProxy(@PathVariable("id") Integer id, @org.springframework.web.bind.annotation.RequestParam("groupNo") String groupNo)
    {
        TgTelethonAccount account = tgTelethonAccountService.selectTgTelethonAccountById(id);
        if (account == null) return error("账号不存在");

        List<TgProxyIp> available = tgProxyIpMapper.selectAvailableByGroupNo(groupNo);
        if (available == null || available.isEmpty()) return error("该IP组下没有可用IP");

        TgProxyIp ip = available.get(0);
        bindProxyToAccount(account, ip);
        return success("代理IP分配成功");
    }

    /**
     * 手动选择代理IP — 指定一个具体的IP
     */
    @PreAuthorize("@ss.hasPermi('tg:account:edit')")
    @PutMapping("/proxy/manual/{id}")
    public AjaxResult manualSelectProxy(@PathVariable("id") Integer id, @org.springframework.web.bind.annotation.RequestParam("proxyIpId") Integer proxyIpId)
    {
        TgTelethonAccount account = tgTelethonAccountService.selectTgTelethonAccountById(id);
        if (account == null) return error("账号不存在");

        TgProxyIp ip = tgProxyIpMapper.selectTgProxyIpById(proxyIpId);
        if (ip == null) return error("代理IP不存在");

        if (ip.getCurrentBindCount() != null && ip.getMaxBindable() != null && ip.getCurrentBindCount() >= ip.getMaxBindable())
        {
            return error("该IP已达到最大绑定数");
        }

        bindProxyToAccount(account, ip);
        return success("代理IP绑定成功");
    }

    /**
     * 手动配置代理IP — 直接填入代理信息，不关联IP组
     */
    @PreAuthorize("@ss.hasPermi('tg:account:edit')")
    @PutMapping("/proxy/config/{id}")
    public AjaxResult configProxy(@PathVariable("id") Integer id, @org.springframework.web.bind.annotation.RequestBody java.util.Map<String, String> params)
    {
        TgTelethonAccount account = tgTelethonAccountService.selectTgTelethonAccountById(id);
        if (account == null) return error("账号不存在");

        // Unbind old proxy if exists
        unbindOldProxy(account);

        account.setProxyIpId(null);
        account.setProxyGroupNo(null);
        account.setProxyProtocol(params.get("protocol"));
        account.setProxyHost(params.get("host"));
        account.setProxyPort(params.get("port") != null ? Integer.parseInt(params.get("port")) : null);
        account.setProxyUsername(params.get("username"));
        account.setProxyPassword(params.get("password"));

        String proxyUrl = buildProxyUrl(params.get("protocol"), params.get("username"), params.get("password"), params.get("host"), params.get("port"));
        account.setProxyUrl(proxyUrl);

        tgTelethonAccountService.updateAccountProxy(account);
        return success("代理IP配置成功");
    }

    /**
     * 批量为批次下所有账号配置代理IP
     */
    @PreAuthorize("@ss.hasPermi('tg:account:edit')")
    @PutMapping("/proxy/batch/{batchNo}")
    public AjaxResult batchAssignProxy(@PathVariable("batchNo") String batchNo, @org.springframework.web.bind.annotation.RequestParam("groupNo") String groupNo)
    {
        Object lock = BATCH_ASSIGN_LOCKS.computeIfAbsent(groupNo, k -> new Object());
        synchronized (lock)
        {
            TgTelethonAccount query = new TgTelethonAccount();
            query.setBatchNo(batchNo);
            List<TgTelethonAccount> accounts = tgTelethonAccountService.selectTgTelethonAccountList(query);

            // Filter out already deleted accounts
            accounts.removeIf(a -> a.getIsDeleted() != null && a.getIsDeleted() == 1);

            if (accounts.isEmpty()) return error("该批次下没有账号");

            // Skip accounts already bound to an IP in this group (idempotent retries)
            accounts.removeIf(a -> a.getProxyIpId() != null && groupNo.equals(a.getProxyGroupNo()));

            int alreadyAssigned = 0;
            if (accounts.isEmpty())
            {
                return success("该批次账号已全部分配到该IP组，无需重复分配");
            }

            List<TgProxyIp> available = tgProxyIpMapper.selectAvailableByGroupNo(groupNo);
            if (available == null || available.isEmpty()) return error("该IP组下没有可用IP");

            int assignedCount = 0;
            int ipIndex = 0;
            for (TgTelethonAccount account : accounts)
            {
                // Re-fetch available IPs since bind counts change
                if (ipIndex >= available.size())
                {
                    available = tgProxyIpMapper.selectAvailableByGroupNo(groupNo);
                    if (available == null || available.isEmpty())
                    {
                        return success("IP不够，已分配 " + assignedCount + " 个账号，还有 " + (accounts.size() - assignedCount) + " 个账号未分配");
                    }
                    ipIndex = 0;
                }

                TgProxyIp ip = available.get(ipIndex);
                bindProxyToAccount(account, ip);
                assignedCount++;

                // Track in-memory bind count on the snapshot to decide when this IP is full
                int newBind = (ip.getCurrentBindCount() != null ? ip.getCurrentBindCount() : 0) + 1;
                ip.setCurrentBindCount(newBind);
                if (ip.getMaxBindable() != null && newBind >= ip.getMaxBindable())
                {
                    ipIndex++;
                }
            }

            return success("批量分配完成，共分配 " + assignedCount + " 个账号");
        }
    }

    private void bindProxyToAccount(TgTelethonAccount account, TgProxyIp ip)
    {
        // Unbind old proxy first
        unbindOldProxy(account);

        // Bind new proxy
        account.setProxyIpId(ip.getId());
        account.setProxyGroupNo(ip.getGroupNo());
        account.setProxyUrl(ip.getProxyUrl());
        account.setProxyProtocol(ip.getProtocol());
        account.setProxyHost(ip.getHost());
        account.setProxyPort(ip.getPort());
        account.setProxyUsername(ip.getUsername());
        account.setProxyPassword(ip.getPassword());
        tgTelethonAccountService.updateAccountProxy(account);

        // Atomic bind-count increment (avoids lost updates under concurrent runs)
        tgProxyIpMapper.incrementBindCount(ip.getId());

        // Write proxy assign log
        try {
            TgProxyAssignLog proxyLog = new TgProxyAssignLog();
            proxyLog.setAccountBatchNo(account.getBatchNo());
            // Get batch title
            if (account.getBatchNo() != null) {
                TgImportBatch batch = tgImportBatchService.selectTgImportBatchByBatchNo(account.getBatchNo());
                proxyLog.setAccountBatchTitle(batch != null ? batch.getTitle() : null);
            }
            proxyLog.setAccountId(account.getId());
            proxyLog.setAccountPhone(account.getPhone());
            proxyLog.setProxyGroupNo(ip.getGroupNo());
            // Get group title
            if (ip.getGroupNo() != null) {
                TgProxyGroup group = tgProxyGroupMapper.selectTgProxyGroupByGroupNo(ip.getGroupNo());
                proxyLog.setProxyGroupTitle(group != null ? group.getTitle() : null);
            }
            proxyLog.setProxyIpId(ip.getId());
            proxyLog.setProxyUrl(ip.getProxyUrl());
            tgProxyAssignLogMapper.insert(proxyLog);
        } catch (Exception e) {
            log.warn("Failed to write proxy assign log: " + e.getMessage());
        }
    }

    private void unbindOldProxy(TgTelethonAccount account)
    {
        if (account.getProxyIpId() != null)
        {
            tgProxyIpMapper.decrementBindCount(account.getProxyIpId());
        }
    }

    /**
     * 查询IP分配日志
     */
    @PreAuthorize("@ss.hasPermi('tg:account:list')")
    @GetMapping("/proxyAssignLog")
    public TableDataInfo proxyAssignLog(TgProxyAssignLog logQuery)
    {
        startPage();
        List<TgProxyAssignLog> list = tgProxyAssignLogMapper.selectList(logQuery);
        return getDataTable(list);
    }

    private String buildProxyUrl(String protocol, String username, String password, String host, String port)
    {
        StringBuilder sb = new StringBuilder();
        if (protocol != null) sb.append(protocol).append("://");
        else sb.append("socks5://");
        if (username != null && !username.isEmpty())
        {
            sb.append(username);
            if (password != null && !password.isEmpty()) sb.append(":").append(password);
            sb.append("@");
        }
        if (host != null) sb.append(host);
        if (port != null) sb.append(":").append(port);
        return sb.toString();
    }

    private String httpGet(String urlStr) throws Exception
    {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name()))
        {
            return scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
        }
        finally
        {
            conn.disconnect();
        }
    }

    @PreAuthorize("@ss.hasPermi('tg:account:edit')")
    @PutMapping("/autoReply/{id}/{autoReply}")
    public AjaxResult updateAutoReply(@PathVariable("id") Integer id, @PathVariable("autoReply") Boolean autoReply)
    {
        return toAjax(tgTelethonAccountService.updateAutoReplyById(id, autoReply));
    }

    @PreAuthorize("@ss.hasPermi('tg:account:edit')")
    @PutMapping("/autoReply/all/{autoReply}")
    public AjaxResult updateAllAutoReply(@PathVariable("autoReply") Boolean autoReply)
    {
        return toAjax(tgTelethonAccountService.updateAllAutoReply(autoReply));
    }

    @PreAuthorize("@ss.hasPermi('tg:account:edit')")
    @PutMapping("/restricted/{id}/{isRestricted}")
    public AjaxResult updateIsRestricted(@PathVariable("id") Integer id, @PathVariable("isRestricted") Integer isRestricted)
    {
        int rows = tgTelethonAccountService.updateIsRestrictedById(id, isRestricted);
        if (rows == 0 && isRestricted != null && isRestricted == 0)
        {
            return error(FROZEN_KEEP_RESTRICTED_TIP);
        }
        return toAjax(rows);
    }

    @PreAuthorize("@ss.hasPermi('tg:account:edit')")
    @PutMapping("/restricted/all")
    public AjaxResult unrestrictAll()
    {
        int rows = tgTelethonAccountService.unrestrictAllAccounts();
        return AjaxResult.success("已解除 " + rows + " 个账号的限制(已冻结账号不解除)", rows);
    }

    @PreAuthorize("@ss.hasPermi('tg:account:edit')")
    @PutMapping("/restricted/batch")
    public AjaxResult batchUpdateIsRestricted(@org.springframework.web.bind.annotation.RequestBody java.util.Map<String, Object> params)
    {
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) params.get("ids");
        Integer isRestricted = (Integer) params.get("isRestricted");
        if (ids == null || ids.isEmpty()) return error("请选择账号");
        int rows = tgTelethonAccountService.batchUpdateIsRestricted(ids, isRestricted);
        if (isRestricted != null && isRestricted == 0)
        {
            if (rows == 0) return error(FROZEN_KEEP_RESTRICTED_TIP);
            if (rows < ids.size())
            {
                return AjaxResult.success("已解除 " + rows + " 个账号的限制, " + (ids.size() - rows)
                    + " 个账号已被TG冻结不能解除", rows);
            }
        }
        return toAjax(rows);
    }

    /**
     * 批量设置账号分组
     */
    @PreAuthorize("@ss.hasPermi('tg:account:edit')")
    @PutMapping("/group/batch")
    public AjaxResult batchUpdateGroupId(@org.springframework.web.bind.annotation.RequestBody java.util.Map<String, Object> params)
    {
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) params.get("ids");
        Integer groupId = params.get("groupId") != null ? Integer.parseInt(params.get("groupId").toString()) : null;
        if (ids == null || ids.isEmpty()) return error("请选择账号");
        if (groupId == null) return error("请选择分组");
        return toAjax(tgTelethonAccountService.batchUpdateGroupId(ids, groupId));
    }

    private String httpPost(String urlStr, String jsonBody) throws Exception
    {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(30000);
        if (jsonBody != null && !jsonBody.isEmpty())
        {
            conn.getOutputStream().write(jsonBody.getBytes(StandardCharsets.UTF_8));
        }
        else
        {
            conn.getOutputStream().write("{}".getBytes(StandardCharsets.UTF_8));
        }
        try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name()))
        {
            return scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
        }
        finally
        {
            conn.disconnect();
        }
    }
}
