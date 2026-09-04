package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TgAccountTask;
import com.ruoyi.system.domain.TgTelethonAccount;

public interface ITgAccountTaskService
{
    public List<TgAccountTask> selectList(TgAccountTask query);

    /**
     * 为一批账号创建任务(昵称/头像/2FA), 由节点轮询执行.
     * 返回中文结果描述(下发数/跳过数及原因).
     */
    public String createTasks(String taskType, List<TgTelethonAccount> accounts, String source, String sourceRef);

    public int deleteByIds(Integer[] ids);

    public int deleteFinished();
}
