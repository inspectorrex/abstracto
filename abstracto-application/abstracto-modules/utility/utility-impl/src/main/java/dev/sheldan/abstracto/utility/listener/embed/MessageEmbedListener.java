package dev.sheldan.abstracto.utility.listener.embed;

import dev.sheldan.abstracto.core.command.service.UserService;
import dev.sheldan.abstracto.core.listener.MessageReceivedListener;
import dev.sheldan.abstracto.core.models.dto.UserInServerDto;
import dev.sheldan.abstracto.core.service.MessageCache;
import dev.sheldan.abstracto.utility.models.MessageEmbedLink;
import dev.sheldan.abstracto.utility.config.UtilityFeatures;
import dev.sheldan.abstracto.utility.service.MessageEmbedService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class MessageEmbedListener implements MessageReceivedListener {

    @Autowired
    private MessageCache messageCache;

    public static final String MESSAGE_EMBED_TEMPLATE = "message";

    @Autowired
    private UserService userManagementService;

    @Autowired
    private MessageEmbedService messageEmbedService;

    @Override
    public void execute(Message message) {
        String messageRaw = message.getContentRaw();
        List<MessageEmbedLink> links = messageEmbedService.getLinksInMessage(messageRaw);
        for (MessageEmbedLink messageEmbedLink : links) {
            messageRaw = messageRaw.replace(messageEmbedLink.getWholeUrl(), "");
            UserInServerDto cause = userManagementService.loadUser(message.getMember());
            messageCache.getMessageFromCache(messageEmbedLink.getServerId(), messageEmbedLink.getChannelId(), messageEmbedLink.getMessageId()).thenAccept(cachedMessage -> {
                messageEmbedService.embedLink(cachedMessage, message.getTextChannel(), cause, message);
            });
        }
        if(StringUtils.isBlank(messageRaw) && links.size() > 0) {
            message.delete().queue();
        }
    }

    @Override
    public String getFeature() {
        return UtilityFeatures.LINK_EMBEDS;
    }
}
