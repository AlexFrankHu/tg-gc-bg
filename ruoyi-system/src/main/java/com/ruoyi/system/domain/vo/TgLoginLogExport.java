package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

import java.util.Date;

/**
 * 登录日志导出VO
 */
public class TgLoginLogExport
{
    @Excel(name = "记录ID")
    private Integer id;

    @Excel(name = "账号")
    private String phone;

    @Excel(name = "昵称")
    private String nickname;

    @Excel(name = "登录结果", readConverterExp = "success=成功,failed=失败,banned=已注销,logout=登出")
    private String result;

    @Excel(name = "错误原因", width = 40)
    private String reason;

    @Excel(name = "代理信息", width = 30)
    private String proxyInfo;

    @Excel(name = "节点ID", width = 20)
    private String nodeId;

    @Excel(name = "登录时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getProxyInfo() { return proxyInfo; }
    public void setProxyInfo(String proxyInfo) { this.proxyInfo = proxyInfo; }
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public Date getLoginTime() { return loginTime; }
    public void setLoginTime(Date loginTime) { this.loginTime = loginTime; }
}
