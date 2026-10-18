package com.ruoyi.web.controller.tg;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.security.MessageDigest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.TgContact;
import com.ruoyi.system.domain.TgGreeting;
import com.ruoyi.system.domain.TgTelethonAccount;
import com.ruoyi.system.domain.vo.TgContactExport;
import com.ruoyi.system.service.ITgContactService;
import com.ruoyi.system.service.ITgGreetingService;
import com.ruoyi.system.service.ITgTelethonAccountService;

/**
 * Telegram好友管理Controller
 */
@RestController
@RequestMapping("/tg/contact")
public class TgContactController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(TgContactController.class);

    @Value("${tg-client-telethon.url:http://localhost:8807}")
    private String telethonUrl;

    @Autowired
    private ITgContactService tgContactService;

    @Autowired
    private ITgTelethonAccountService telethonAccountService;

    @Autowired
    private ITgGreetingService greetingService;

    @PreAuthorize("@ss.hasPermi('tg:contact:list')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, TgContact tgContact)
    {
        List<TgContact> list = tgContactService.selectTgContactList(tgContact);
        // Build account id -> phone mapping
        List<TgTelethonAccount> accounts = telethonAccountService.selectTgTelethonAccountList(new TgTelethonAccount());
        java.util.Map<Integer, String> accountPhoneMap = new java.util.HashMap<>();
        for (TgTelethonAccount acc : accounts) {
            accountPhoneMap.put(acc.getId(), acc.getPhone());
        }

        List<TgContactExport> exportList = new ArrayList<>();
        for (TgContact c : list)
        {
            TgContactExport vo = new TgContactExport();
            vo.setAccountPhone(accountPhoneMap.getOrDefault(c.getTgAccountId(), String.valueOf(c.getTgAccountId())));
            vo.setUserId(c.getUserId());
            vo.setNickname(c.getNickname());
            vo.setUsername(c.getUsername() != null ? "@" + c.getUsername() : "");
            vo.setPhoneNumber(c.getPhoneNumber());
            vo.setUserType("regular".equals(c.getUserType()) ? "普通" : "bot".equals(c.getUserType()) ? "机器人" : "deleted".equals(c.getUserType()) ? "已注销" : c.getUserType());
            vo.setIsMutualLabel(Boolean.TRUE.equals(c.getIsMutual()) ? "是" : "否");
            vo.setAutoReplyLabel(Boolean.TRUE.equals(c.getAutoReply()) ? "开启" : "关闭");
            vo.setCreateTime(c.getCreateTime());
            vo.setLastOnlineTime(c.getLastOnlineTime());
            vo.setLastSendTime(c.getLastSendTime());
            vo.setLastReceiveTime(c.getLastReceiveTime());
            exportList.add(vo);
        }
        ExcelUtil<TgContactExport> util = new ExcelUtil<>(TgContactExport.class);
        util.exportExcel(response, exportList, "好友列表");
    }

    @PreAuthorize("@ss.hasPermi('tg:contact:list')")
    @GetMapping("/list")
    public TableDataInfo list(TgContact tgContact)
    {
        startPage();
        List<TgContact> list = tgContactService.selectTgContactList(tgContact);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('tg:contact:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        return success(tgContactService.selectTgContactById(id));
    }

    @PreAuthorize("@ss.hasPermi('tg:contact:remove')")
    @Log(title = "Telegram好友", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(tgContactService.deleteTgContactByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('tg:contact:edit')")
    @PutMapping("/autoReply/{id}/{autoReply}")
    public AjaxResult updateAutoReply(@PathVariable("id") Integer id, @PathVariable("autoReply") Boolean autoReply)
    {
        return toAjax(tgContactService.updateAutoReplyById(id, autoReply));
    }

    @PreAuthorize("@ss.hasPermi('tg:contact:edit')")
    @PutMapping("/autoReply/all/{autoReply}")
    public AjaxResult updateAllAutoReply(@PathVariable("autoReply") Boolean autoReply)
    {
        return toAjax(tgContactService.updateAllAutoReply(autoReply));
    }

    private static final int CHECK_FRIENDS_MAX_SIZE = 100;

    private static final ObjectMapper CHECK_FRIENDS_MAPPER = new ObjectMapper();

    @Value("${tg.open-api.key:}")
    private String openApiKey;

    /**
     * 查询好友(外部系统调用): 传入 TG 用户ID 数组(最多 100 个), 返回其中是任意账号好友(real/fake)的用户ID数组
     * 鉴权: 请求头 X-API-Key, 值为配置项 tg.open-api.key
     */
    @Anonymous
    @PostMapping("/checkFriends")
    public ResponseEntity<AjaxResult> checkFriends(@RequestHeader(value = "X-API-Key", required = false) String apiKey,
                                                   @RequestBody(required = false) String body)
    {
        if (openApiKey == null || openApiKey.isEmpty())
        {
            return checkFriendsError(HttpStatus.SERVICE_UNAVAILABLE, "接口未配置 API Key");
        }
        if (apiKey == null || !MessageDigest.isEqual(openApiKey.getBytes(StandardCharsets.UTF_8), apiKey.getBytes(StandardCharsets.UTF_8)))
        {
            return checkFriendsError(HttpStatus.UNAUTHORIZED, "API Key 无效");
        }
        JsonNode userIds;
        try
        {
            userIds = body == null || body.trim().isEmpty() ? null : CHECK_FRIENDS_MAPPER.readTree(body);
        }
        catch (Exception e)
        {
            return checkFriendsError(HttpStatus.BAD_REQUEST, "请求体不是合法的 JSON");
        }
        if (userIds != null && !userIds.isArray())
        {
            return checkFriendsError(HttpStatus.BAD_REQUEST, "请求体必须是用户ID数组");
        }
        if (userIds == null || userIds.size() == 0)
        {
            return checkFriendsError(HttpStatus.BAD_REQUEST, "用户ID数组不能为空");
        }
        if (userIds.size() > CHECK_FRIENDS_MAX_SIZE)
        {
            return checkFriendsError(HttpStatus.BAD_REQUEST, "单次最多 " + CHECK_FRIENDS_MAX_SIZE + " 个用户ID");
        }
        List<Long> ids = new ArrayList<>(userIds.size());
        for (JsonNode o : userIds)
        {
            Long id = parseUserId(o);
            if (id == null)
            {
                return checkFriendsError(HttpStatus.BAD_REQUEST, "非法用户ID: " + o);
            }
            ids.add(id);
        }
        return ResponseEntity.ok(AjaxResult.success(tgContactService.filterFriendUserIds(ids)));
    }

    private static ResponseEntity<AjaxResult> checkFriendsError(HttpStatus status, String msg)
    {
        return ResponseEntity.status(status).body(AjaxResult.error(status.value(), msg));
    }

    private static Long parseUserId(JsonNode o)
    {
        if (o.isIntegralNumber() && o.canConvertToLong())
        {
            return o.longValue();
        }
        if (o.isTextual() && o.asText().trim().matches("-?\\d{1,19}"))
        {
            try
            {
                return Long.parseLong(o.asText().trim());
            }
            catch (NumberFormatException e)
            {
                return null;
            }
        }
        return null;
    }

    @PostMapping("/sendGreeting")
    public AjaxResult sendGreeting(@RequestBody Map<String, Object> params)
    {
        Integer accountId = (Integer) params.get("accountId");
        Long userId = Long.valueOf(params.get("userId").toString());
        Integer greetingId = (Integer) params.get("greetingId");

        if (accountId == null || userId == null || greetingId == null) {
            return error("参数不完整");
        }

        TgGreeting greeting = greetingService.selectById(greetingId);
        if (greeting == null) {
            return error("问候语不存在");
        }

        try {
            ObjectMapper om = new ObjectMapper();
            Map<String, Object> body = new java.util.HashMap<>();
            body.put("accountId", accountId);
            body.put("userId", userId);
            body.put("content", greeting.getContent());
            if (greeting.getImagePath() != null && !greeting.getImagePath().isEmpty()) {
                body.put("imagePath", greeting.getImagePath());
            }

            String jsonBody = om.writeValueAsString(body);
            URL url = new URL(telethonUrl + "/api/send-greeting");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(60000);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            String respBody;
            try (Scanner sc = new Scanner(
                    code >= 400 ? conn.getErrorStream() : conn.getInputStream(),
                    StandardCharsets.UTF_8.name())) {
                respBody = sc.useDelimiter("\\A").hasNext() ? sc.next() : "";
            }

            Map<String, Object> result = om.readValue(respBody, Map.class);
            if (Boolean.TRUE.equals(result.get("success"))) {
                return success("发送成功");
            } else {
                return error("发送失败: " + result.getOrDefault("error", "未知错误"));
            }
        } catch (Exception e) {
            log.error("发送问候语失败", e);
            return error("发送问候语失败: " + e.getMessage());
        }
    }
}
