package dev.sheldan.abstracto.utility.listener.embed;

import dev.sheldan.abstracto.core.listener.ReactedAddedListener;
import dev.sheldan.abstracto.core.models.cache.CachedMessage;
import dev.sheldan.abstracto.core.models.database.AEmote;
import dev.sheldan.abstracto.core.models.database.AUserInAServer;
import dev.sheldan.abstracto.core.service.Bot;
import dev.sheldan.abstracto.core.service.MessageService;
import dev.sheldan.abstracto.core.service.management.EmoteManagementService;
import dev.sheldan.abstracto.core.utils.EmoteUtils;
import dev.sheldan.abstracto.utility.config.UtilityFeatures;
import dev.sheldan.abstracto.utility.models.database.EmbeddedMessage;
import dev.sheldan.abstracto.utility.service.management.MessageEmbedPostManagementService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageReaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class MessageEmbedRemovalReactionListener implements ReactedAddedListener {

    public static final String REMOVAL_EMOTE = "removeEmbed";

    @Autowired
    private EmoteManagementService emoteManagementService;

    @Autowired
    private Bot bot;

    @Autowired
    private MessageEmbedPostManagementService messageEmbedPostManagementService;

    @Autowired
    private MessageService messageService;

    @Override
    public void executeReactionAdded(CachedMessage message, MessageReaction reaction, AUserInAServer userAdding) {
        Long guildId = message.getServerId();
        Optional<AEmote> aEmote = emoteManagementService.loadEmoteByName(REMOVAL_EMOTE, guildId);
        if(aEmote.isPresent()) {
            AEmote emote = aEmote.get();
            MessageReaction.ReactionEmote reactionEmote = reaction.getReactionEmote();
            Optional<Emote> emoteInGuild = bot.getEmote(guildId, emote);
            if(EmoteUtils.isReactionEmoteAEmote(reactionEmote, emote, emoteInGuild.orElse(null))) {
                Optional<EmbeddedMessage> embeddedMessageOptional = messageEmbedPostManagementService.findEmbeddedPostByMessageId(message.getMessageId());
                if(embeddedMessageOptional.isPresent()) {
                    EmbeddedMessage embeddedMessage = embeddedMessageOptional.get();
                    if(embeddedMessage.getEmbeddedUser().getUserReference().getId().equals(userAdding.getUserReference().getId())
                        || embeddedMessage.getEmbeddingUser().getUserReference().getId().equals(userAdding.getUserReference().getId())
                    ) {
                        messageService.deleteMessageInChannelInServer(message.getServerId(), message.getChannelId(), message.getMessageId()).thenAccept(aVoid -> {
                            messageEmbedPostManagementService.deleteEmbeddedMessageTransactional(embeddedMessage);
                        });
                    }

                }
            }
        } else {
            log.warn("Emote {} is not defined for guild {}. Embed link deletion not functional.", REMOVAL_EMOTE, guildId);
        }
    }

    @Override
    public String getFeature() {
        return UtilityFeatures.LINK_EMBEDS;
    }
}