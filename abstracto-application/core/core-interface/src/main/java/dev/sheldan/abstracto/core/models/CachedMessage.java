package dev.sheldan.abstracto.core.models;

import dev.sheldan.abstracto.core.models.embed.CachedEmbed;
import dev.sheldan.abstracto.core.utils.MessageUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class CachedMessage {
    private Long serverId;
    private Long channelId;
    private Long messageId;
    private Long authorId;
    private OffsetDateTime timeCreated;
    private String content;
    private List<CachedEmbed> embeds;
    private List<String> attachmentUrls;

    public String getMessageUrl() {
        return MessageUtils.buildMessageUrl(this.serverId ,this.channelId, this.messageId);
    }
}