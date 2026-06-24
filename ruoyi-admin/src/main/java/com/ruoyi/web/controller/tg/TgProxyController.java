package com.ruoyi.web.controller.tg;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.TgClusterNode;
import com.ruoyi.system.domain.TgProxyGroup;
import com.ruoyi.system.domain.TgProxyIp;
import com.ruoyi.system.service.ITgClusterNodeService;
import com.ruoyi.system.service.ITgProxyGroupService;
import com.ruoyi.system.service.ITgProxyIpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IP代理管理Controller
 */
@RestController
@RequestMapping("/tg/proxy")
public class TgProxyController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(TgProxyController.class);

    @Autowired
    private ITgProxyGroupService proxyGroupService;

    @Autowired
    private ITgProxyIpService proxyIpService;

    @Autowired
    private ITgClusterNodeService clusterNodeService;

    // proxy URL pattern: {protocol}://{username}:{password}@{host}:{port}
    private static final Pattern PROXY_PATTERN = Pattern.compile(
        "^(socks5|socks4|http|https)://(?:([^:]+):([^@]+)@)?([^:]+):(\\d+)$"
    );

    /**
     * 导入代理IP组
     */
    @PreAuthorize("@ss.hasPermi('tg:proxy:add')")
    @PostMapping("/import")
    public AjaxResult importProxy(@RequestParam("file") MultipartFile file,
                                  @RequestParam("title") String title,
                                  @RequestParam("country") String country,
                                  @RequestParam(value = "expireTime", required = false) String expireTime,
                                  @RequestParam(value = "maxBindable", defaultValue = "1") Integer maxBindable)
    {
        try
        {
            // Read file lines
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)))
            {
                String line;
                while ((line = reader.readLine()) != null)
                {
                    line = line.trim();
                    if (!line.isEmpty())
                    {
                        lines.add(line);
                    }
                }
            }

            if (lines.isEmpty())
            {
                return error("文件中没有有效的代理IP数据");
            }

            // Parse proxy lines
            List<TgProxyIp> proxyIps = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++)
            {
                String proxyLine = lines.get(i);
                Matcher matcher = PROXY_PATTERN.matcher(proxyLine);
                if (matcher.matches())
                {
                    TgProxyIp ip = new TgProxyIp();
                    ip.setProtocol(matcher.group(1));
                    ip.setUsername(matcher.group(2));
                    ip.setPassword(matcher.group(3));
                    ip.setHost(matcher.group(4));
                    ip.setPort(Integer.parseInt(matcher.group(5)));
                    ip.setProxyUrl(proxyLine);
                    ip.setMaxBindable(maxBindable);
                    proxyIps.add(ip);
                }
                else
                {
                    errors.add("第" + (i + 1) + "行格式错误: " + proxyLine);
                }
            }

            if (proxyIps.isEmpty())
            {
                return error("没有解析到有效的代理IP。" + (errors.isEmpty() ? "" : "错误: " + String.join("; ", errors)));
            }

            // Create group
            String groupNo = UUID.randomUUID().toString().replace("-", "");
            TgProxyGroup group = new TgProxyGroup();
            group.setGroupNo(groupNo);
            group.setTitle(title);
            group.setCountry(country);
            group.setMaxBindable(maxBindable);
            group.setTotalCount(proxyIps.size());
            group.setImportTime(new Date());

            if (expireTime != null && !expireTime.isEmpty())
            {
                try
                {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    group.setExpireTime(sdf.parse(expireTime));
                }
                catch (Exception e)
                {
                    try
                    {
                        java.text.SimpleDateFormat sdf2 = new java.text.SimpleDateFormat("yyyy-MM-dd");
                        group.setExpireTime(sdf2.parse(expireTime));
                    }
                    catch (Exception e2)
                    {
                        // Use default
                    }
                }
            }

            proxyGroupService.insertTgProxyGroup(group);

            // Insert IPs
            for (TgProxyIp ip : proxyIps)
            {
                ip.setGroupNo(groupNo);
            }
            proxyIpService.batchInsertTgProxyIp(proxyIps);

            String msg = "导入成功，共 " + proxyIps.size() + " 个代理IP";
            if (!errors.isEmpty())
            {
                msg += "（" + errors.size() + " 行解析失败）";
            }
            return success(msg);
        }
        catch (Exception e)
        {
            log.error("导入代理IP失败", e);
            return error("导入失败: " + e.getMessage());
        }
    }

    /**
     * 导入ipfly代理IP组
     * 格式: username:password:host:port (每行一个, 默认socks5协议)
     */
    @PreAuthorize("@ss.hasPermi('tg:proxy:add')")
    @PostMapping("/importIpfly")
    public AjaxResult importIpfly(@RequestParam("file") MultipartFile file,
                                  @RequestParam("title") String title,
                                  @RequestParam("country") String country,
                                  @RequestParam(value = "expireTime", required = false) String expireTime,
                                  @RequestParam(value = "maxBindable", defaultValue = "1") Integer maxBindable)
    {
        try
        {
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)))
            {
                String line;
                while ((line = reader.readLine()) != null)
                {
                    line = line.trim();
                    if (!line.isEmpty())
                    {
                        lines.add(line);
                    }
                }
            }

            if (lines.isEmpty())
            {
                return error("文件中没有有效的代理IP数据");
            }

            // Parse ipfly format: username:password:host:port
            List<TgProxyIp> proxyIps = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++)
            {
                String proxyLine = lines.get(i);
                String[] parts = proxyLine.split(":");
                if (parts.length == 4)
                {
                    String username = parts[0].trim();
                    String password = parts[1].trim();
                    String host = parts[2].trim();
                    String portStr = parts[3].trim();
                    try
                    {
                        int port = Integer.parseInt(portStr);
                        TgProxyIp ip = new TgProxyIp();
                        ip.setProtocol("socks5");
                        ip.setUsername(username);
                        ip.setPassword(password);
                        ip.setHost(host);
                        ip.setPort(port);
                        ip.setProxyUrl("socks5://" + username + ":" + password + "@" + host + ":" + port);
                        ip.setMaxBindable(maxBindable);
                        proxyIps.add(ip);
                    }
                    catch (NumberFormatException e)
                    {
                        errors.add("第" + (i + 1) + "行端口格式错误: " + proxyLine);
                    }
                }
                else
                {
                    errors.add("第" + (i + 1) + "行格式错误(应为 username:password:host:port): " + proxyLine);
                }
            }

            if (proxyIps.isEmpty())
            {
                return error("没有解析到有效的代理IP。" + (errors.isEmpty() ? "" : "错误: " + String.join("; ", errors)));
            }

            // Create group
            String groupNo = UUID.randomUUID().toString().replace("-", "");
            TgProxyGroup group = new TgProxyGroup();
            group.setGroupNo(groupNo);
            group.setTitle(title);
            group.setCountry(country);
            group.setMaxBindable(maxBindable);
            group.setTotalCount(proxyIps.size());
            group.setImportTime(new Date());

            if (expireTime != null && !expireTime.isEmpty())
            {
                try
                {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    group.setExpireTime(sdf.parse(expireTime));
                }
                catch (Exception e)
                {
                    try
                    {
                        java.text.SimpleDateFormat sdf2 = new java.text.SimpleDateFormat("yyyy-MM-dd");
                        group.setExpireTime(sdf2.parse(expireTime));
                    }
                    catch (Exception e2)
                    {
                        // Use default
                    }
                }
            }

            proxyGroupService.insertTgProxyGroup(group);

            for (TgProxyIp ip : proxyIps)
            {
                ip.setGroupNo(groupNo);
            }
            proxyIpService.batchInsertTgProxyIp(proxyIps);

            String msg = "导入成功，共 " + proxyIps.size() + " 个代理IP";
            if (!errors.isEmpty())
            {
                msg += "（" + errors.size() + " 行解析失败）";
            }
            return success(msg);
        }
        catch (Exception e)
        {
            log.error("导入ipfly代理IP失败", e);
            return error("导入失败: " + e.getMessage());
        }
    }

    /**
     * IP组列表
     */
    @PreAuthorize("@ss.hasPermi('tg:proxy:list')")
    @GetMapping("/group/list")
    public TableDataInfo groupList(TgProxyGroup group)
    {
        startPage();
        List<TgProxyGroup> list = proxyGroupService.selectTgProxyGroupList(group);
        return getDataTable(list);
    }

    /**
     * 所有IP组(下拉用)
     */
    @PreAuthorize("@ss.hasPermi('tg:proxy:list')")
    @GetMapping("/group/all")
    public AjaxResult allGroups()
    {
        return success(proxyGroupService.selectAllGroups());
    }

    /**
     * 修改IP组信息
     */
    @PreAuthorize("@ss.hasPermi('tg:proxy:edit')")
    @PutMapping("/group")
    public AjaxResult editGroup(@RequestBody TgProxyGroup group)
    {
        return toAjax(proxyGroupService.updateTgProxyGroup(group));
    }

    /**
     * 删除IP组
     */
    @PreAuthorize("@ss.hasPermi('tg:proxy:remove')")
    @DeleteMapping("/group/{ids}")
    public AjaxResult removeGroup(@PathVariable Integer[] ids)
    {
        return toAjax(proxyGroupService.deleteTgProxyGroupByIds(ids));
    }

    /**
     * IP代理列表
     */
    @PreAuthorize("@ss.hasPermi('tg:proxy:list')")
    @GetMapping("/ip/list")
    public TableDataInfo ipList(TgProxyIp proxyIp)
    {
        startPage();
        List<TgProxyIp> list = proxyIpService.selectTgProxyIpList(proxyIp);
        return getDataTable(list);
    }

    /**
     * 查看IP代理详情
     */
    @PreAuthorize("@ss.hasPermi('tg:proxy:list')")
    @GetMapping("/ip/{id}")
    public AjaxResult getIpDetail(@PathVariable Integer id)
    {
        return success(proxyIpService.selectTgProxyIpById(id));
    }

    /**
     * 修改IP代理状态
     */
    @PreAuthorize("@ss.hasPermi('tg:proxy:edit')")
    @PutMapping("/ip/status")
    public AjaxResult changeIpStatus(@RequestBody TgProxyIp proxyIp)
    {
        TgProxyIp update = new TgProxyIp();
        update.setId(proxyIp.getId());
        update.setStatus(proxyIp.getStatus());
        return toAjax(proxyIpService.updateTgProxyIp(update));
    }

    /**
     * 删除IP代理
     */
    @PreAuthorize("@ss.hasPermi('tg:proxy:remove')")
    @DeleteMapping("/ip/{ids}")
    public AjaxResult removeIp(@PathVariable Integer[] ids)
    {
        return toAjax(proxyIpService.deleteTgProxyIpByIds(ids));
    }

    /**
     * 测试代理IP
     */
    @PreAuthorize("@ss.hasPermi('tg:proxy:edit')")
    @PostMapping("/ip/test/{id}")
    public AjaxResult testProxy(@PathVariable Integer id)
    {
        try
        {
            TgProxyIp proxyIp = proxyIpService.selectTgProxyIpById(id);
            if (proxyIp == null)
            {
                return error("代理IP不存在");
            }
            // Pick any active cluster node to test the proxy
            List<TgClusterNode> activeNodes = clusterNodeService.selectActiveNodes(2);
            if (activeNodes == null || activeNodes.isEmpty())
            {
                return error("没有活跃的节点，无法测试代理");
            }
            TgClusterNode node = activeNodes.get(0);
            String nodeIp = node.getPrivateIp() != null ? node.getPrivateIp() : node.getPublicIp();
            String apiUrl = "http://" + nodeIp + ":" + node.getNodePort() + "/api/proxy/test";
            String json = "{\"proxy_url\":\"" + proxyIp.getProxyUrl() + "\"}";

            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            try (OutputStream os = conn.getOutputStream())
            {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                conn.getResponseCode() < 400 ? conn.getInputStream() : conn.getErrorStream(), StandardCharsets.UTF_8)))
            {
                String line;
                while ((line = br.readLine()) != null)
                {
                    sb.append(line);
                }
            }
            return success((Object) sb.toString());
        }
        catch (Exception e)
        {
            log.error("测试代理IP失败", e);
            return error("测试失败: " + e.getMessage());
        }
    }
}
