package com.ruoyi.system.service.impl;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.TgAccountTask;
import com.ruoyi.system.domain.TgTelethonAccount;
import com.ruoyi.system.mapper.TgAccountTaskMapper;
import com.ruoyi.system.mapper.TgAvatarMaterialMapper;
import com.ruoyi.system.mapper.TgNicknameMaterialMapper;
import com.ruoyi.system.service.ITgAccountTaskService;

@Service
public class TgAccountTaskServiceImpl implements ITgAccountTaskService
{
    private static final int BATCH_SIZE = 500;
    private static final String[] JSON_TWOFA_KEYS = {"twofa", "twoFA", "twoFa", "2fa", "two_fa", "password", "twoFactor"};
    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    private TgAccountTaskMapper taskMapper;

    @Autowired
    private TgNicknameMaterialMapper nicknameMapper;

    @Autowired
    private TgAvatarMaterialMapper avatarMapper;

    @Override
    public List<TgAccountTask> selectList(TgAccountTask query)
    {
        return taskMapper.selectList(query);
    }

    @Override
    public String createTasks(String taskType, List<TgTelethonAccount> accounts, String source, String sourceRef)
    {
        List<String> materials = null;
        if (TgAccountTask.TYPE_NICKNAME.equals(taskType))
        {
            materials = nicknameMapper.selectAllNicknames();
            if (materials.isEmpty())
            {
                throw new RuntimeException("昵称素材库为空，请先导入昵称素材");
            }
        }
        else if (TgAccountTask.TYPE_AVATAR.equals(taskType))
        {
            materials = avatarMapper.selectAllPaths();
            if (materials.isEmpty())
            {
                throw new RuntimeException("头像素材库为空，请先导入头像素材");
            }
        }
        else if (!TgAccountTask.TYPE_TWOFA.equals(taskType))
        {
            throw new RuntimeException("未知任务类型: " + taskType);
        }

        List<TgAccountTask> tasks = new ArrayList<>();
        int skipOffline = 0, skipBlocked = 0, skipNoPassword = 0;
        for (TgTelethonAccount acc : accounts)
        {
            if (!"online".equals(acc.getStatus()))
            {
                skipOffline++;
                continue;
            }
            if (Boolean.TRUE.equals(acc.getIsRestricted()) || Boolean.TRUE.equals(acc.getIsFrozen()))
            {
                skipBlocked++;
                continue;
            }
            JSONObject param = new JSONObject();
            if (TgAccountTask.TYPE_NICKNAME.equals(taskType))
            {
                param.put("nickname", materials.get(ThreadLocalRandom.current().nextInt(materials.size())));
            }
            else if (TgAccountTask.TYPE_AVATAR.equals(taskType))
            {
                param.put("filePath", materials.get(ThreadLocalRandom.current().nextInt(materials.size())));
            }
            else
            {
                String oldPassword = resolveOldPassword(acc);
                if (StringUtils.isEmpty(oldPassword))
                {
                    skipNoPassword++;
                    continue;
                }
                param.put("oldPassword", oldPassword);
                param.put("newPassword", randomDigits(8));
            }
            TgAccountTask t = new TgAccountTask();
            t.setAccountId(acc.getId());
            t.setPhone(acc.getPhone());
            t.setNodeId(acc.getNodeId());
            t.setTaskType(taskType);
            t.setParam(param.toJSONString());
            t.setSource(source);
            t.setSourceRef(sourceRef);
            tasks.add(t);
        }
        for (int i = 0; i < tasks.size(); i += BATCH_SIZE)
        {
            taskMapper.batchInsert(tasks.subList(i, Math.min(i + BATCH_SIZE, tasks.size())));
        }

        StringBuilder sb = new StringBuilder("已下发 " + tasks.size() + " 个账号任务，节点将在数秒内开始执行");
        List<String> skips = new ArrayList<>();
        if (skipOffline > 0) skips.add("未在线 " + skipOffline);
        if (skipBlocked > 0) skips.add("受限/冻结 " + skipBlocked);
        if (skipNoPassword > 0) skips.add("无旧2FA密码 " + skipNoPassword);
        if (!skips.isEmpty())
        {
            sb.append("；跳过：").append(String.join("，", skips));
        }
        return sb.toString();
    }

    /** 旧 2FA 密码: 先取账号表 twofa_password 字段, 其次取导入 json 内容 */
    private String resolveOldPassword(TgTelethonAccount acc)
    {
        if (StringUtils.isNotEmpty(acc.getTwofaPassword()))
        {
            return acc.getTwofaPassword();
        }
        String json = acc.getJsonContent();
        if (StringUtils.isEmpty(json))
        {
            return null;
        }
        try
        {
            JSONObject obj = JSONObject.parseObject(json);
            for (String key : JSON_TWOFA_KEYS)
            {
                String v = obj.getString(key);
                if (StringUtils.isNotEmpty(v))
                {
                    return v;
                }
            }
        }
        catch (Exception ignored)
        {
        }
        return null;
    }

    private static String randomDigits(int len)
    {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
        {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    @Override
    public int deleteByIds(Integer[] ids)
    {
        return taskMapper.deleteByIds(ids);
    }

    @Override
    public int deleteFinished()
    {
        return taskMapper.deleteFinished();
    }
}
