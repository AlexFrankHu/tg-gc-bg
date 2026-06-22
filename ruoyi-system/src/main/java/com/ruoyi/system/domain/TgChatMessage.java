package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Telegram聊天记录对象 tg_chat_message
 */
public class TgChatMessage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer tgAccountId;
    private Long chatId;
    private Long messageId;
    private Long senderUserId;
    private Long senderChatId;
    private String senderName;
    private Boolean isOutgoing;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;

    private String contentType;
    private String textContent;
    private Long mediaFileId;
    private Long mediaFileSize;
    private String mediaMimeType;
    private String mediaFileName;
    private Integer mediaDuration;
    private Integer mediaWidth;
    private Integer mediaHeight;
    private Integer thumbnailFileId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getTgAccountId() { return tgAccountId; }
    public void setTgAccountId(Integer tgAccountId) { this.tgAccountId = tgAccountId; }
    public Long getChatId() { return chatId; }
    public void setChatId(Long chatId) { this.chatId = chatId; }
    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }
    public Long getSenderUserId() { return senderUserId; }
    public void setSenderUserId(Long senderUserId) { this.senderUserId = senderUserId; }
    public Long getSenderChatId() { return senderChatId; }
    public void setSenderChatId(Long senderChatId) { this.senderChatId = senderChatId; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public Boolean getIsOutgoing() { return isOutgoing; }
    public void setIsOutgoing(Boolean isOutgoing) { this.isOutgoing = isOutgoing; }
    public Date getSendTime() { return sendTime; }
    public void setSendTime(Date sendTime) { this.sendTime = sendTime; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getTextContent() { return textContent; }
    public void setTextContent(String textContent) { this.textContent = textContent; }
    public Long getMediaFileId() { return mediaFileId; }
    public void setMediaFileId(Long mediaFileId) { this.mediaFileId = mediaFileId; }
    public Long getMediaFileSize() { return mediaFileSize; }
    public void setMediaFileSize(Long mediaFileSize) { this.mediaFileSize = mediaFileSize; }
    public String getMediaMimeType() { return mediaMimeType; }
    public void setMediaMimeType(String mediaMimeType) { this.mediaMimeType = mediaMimeType; }
    public String getMediaFileName() { return mediaFileName; }
    public void setMediaFileName(String mediaFileName) { this.mediaFileName = mediaFileName; }
    public Integer getMediaDuration() { return mediaDuration; }
    public void setMediaDuration(Integer mediaDuration) { this.mediaDuration = mediaDuration; }
    public Integer getMediaWidth() { return mediaWidth; }
    public void setMediaWidth(Integer mediaWidth) { this.mediaWidth = mediaWidth; }
    public Integer getMediaHeight() { return mediaHeight; }
    public void setMediaHeight(Integer mediaHeight) { this.mediaHeight = mediaHeight; }
    public Integer getThumbnailFileId() { return thumbnailFileId; }
    public void setThumbnailFileId(Integer thumbnailFileId) { this.thumbnailFileId = thumbnailFileId; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
