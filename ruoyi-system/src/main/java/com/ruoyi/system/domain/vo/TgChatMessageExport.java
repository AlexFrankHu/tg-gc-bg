package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

import java.util.Date;

/**
 * 聊天记录导出VO
 */
public class TgChatMessageExport
{
    @Excel(name = "发送方")
    private String senderName;

    @Excel(name = "发送时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;

    @Excel(name = "消息类型")
    private String contentTypeLabel;

    @Excel(name = "消息内容", width = 50)
    private String textContent;

    @Excel(name = "收/发")
    private String direction;

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public Date getSendTime() { return sendTime; }
    public void setSendTime(Date sendTime) { this.sendTime = sendTime; }
    public String getContentTypeLabel() { return contentTypeLabel; }
    public void setContentTypeLabel(String contentTypeLabel) { this.contentTypeLabel = contentTypeLabel; }
    public String getTextContent() { return textContent; }
    public void setTextContent(String textContent) { this.textContent = textContent; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
}
