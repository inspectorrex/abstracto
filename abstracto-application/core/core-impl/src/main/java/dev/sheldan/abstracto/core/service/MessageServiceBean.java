package dev.sheldan.abstracto.core.service;

import dev.sheldan.abstracto.core.exception.EmoteNotDefinedException;
import dev.sheldan.abstracto.core.exception.GuildException;
import dev.sheldan.abstracto.core.models.database.AChannel;
import dev.sheldan.abstracto.core.models.database.AUserInAServer;
import dev.sheldan.abstracto.core.service.management.EmoteManagementService;
import dev.sheldan.abstracto.core.models.database.AEmote;
import dev.sheldan.abstracto.templating.model.MessageToSend;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class MessageServiceBean implements MessageService {

    @Autowired
    private BotService botService;

    @Autowired
    private EmoteManagementService emoteManagementService;

    @Autowired
    private EmoteService emoteService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private MessageServiceBean self;

    @Override
    public void addReactionToMessage(String emoteKey, Long serverId, Message message) {
        addReactionToMessageWithFuture(emoteKey, serverId, message);
    }

    @Override
    public CompletableFuture<Void> addReactionToMessageWithFuture(String emoteKey, Long serverId, Message message) {
        Optional<Guild> guildByIdOptional = botService.getGuildById(serverId);
        AEmote emote = emoteService.getEmoteOrFakeEmote(emoteKey, serverId);
        if(guildByIdOptional.isPresent()) {
            Guild guild = guildByIdOptional.get();
            if(Boolean.TRUE.equals(emote.getCustom())) {
                Emote emoteById = botService.getInstance().getEmoteById(emote.getEmoteId());
                if(emoteById != null) {
                    return message.addReaction(emoteById).submit();
                } else {
                    log.error("Emote with key {} and id {} for guild {} was not found.", emoteKey, emote.getEmoteId(), guild.getId());
                    throw new EmoteNotDefinedException(emoteKey);
                }
            } else {
                return message.addReaction(emote.getEmoteKey()).submit();
            }
        } else {
            log.error("Cannot add reaction, guild not found {}", serverId);
            throw new GuildException(serverId);
        }
    }

    @Override
    public List<CompletableFuture<Void>> addReactionsToMessageWithFuture(List<String> emoteKeys, Long serverId, Message message) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        emoteKeys.forEach(s -> futures.add(addReactionToMessageWithFuture(s, serverId, message)));
        return futures;
    }


    @Override
    public CompletableFuture<Void> deleteMessageInChannelInServer(Long serverId, Long channelId, Long messageId) {
        return botService.deleteMessage(serverId, channelId, messageId);
    }

    @Override
    public CompletableFuture<Message> createStatusMessage(MessageToSend messageToSend, AChannel channel) {
        return channelService.sendMessageToSendToAChannel(messageToSend, channel).get(0);
    }

    @Override
    public CompletableFuture<Message> createStatusMessage(MessageToSend messageToSend, MessageChannel channel) {
        return channelService.sendMessageToSendToChannel(messageToSend, channel).get(0);
    }

    @Override
    public CompletableFuture<Long> createStatusMessageId(MessageToSend messageToSend, MessageChannel channel) {
        return channelService.sendMessageToSendToChannel(messageToSend, channel).get(0).thenApply(ISnowflake::getIdLong);
    }

    @Override
    public void updateStatusMessage(AChannel channel, Long messageId, MessageToSend messageToSend) {
        channelService.editMessageInAChannel(messageToSend, channel, messageId);
    }

    @Override
    public void updateStatusMessage(MessageChannel channel, Long messageId, MessageToSend messageToSend) {
        channelService.editMessageInAChannel(messageToSend, channel, messageId);
    }

    @Override
    public void sendMessageToUser(AUserInAServer userInAServer, String text, MessageChannel feedbackChannel) {
        Member memberInServer = botService.getMemberInServer(userInAServer);
        sendMessageToUser(memberInServer.getUser(), text, feedbackChannel);
    }

    @Override
    public void sendMessageToUser(User user, String text, MessageChannel feedbackChannel) {
        CompletableFuture<Message> messageFuture = new CompletableFuture<>();

        user.openPrivateChannel().queue(privateChannel ->
            privateChannel.sendMessage(text).queue(messageFuture::complete, messageFuture::completeExceptionally)
        );

        messageFuture.exceptionally(e -> {
            log.warn("Failed to send message. ", e);
            if(feedbackChannel != null){
                self.sendFeedbackAboutException(e, feedbackChannel);
            }
            return null;
        });
    }

    @Transactional
    public void sendFeedbackAboutException(Throwable e, MessageChannel feedbackChannel) {
        channelService.sendTextToChannelNoFuture(String.format("Failed to send message: %s", e.getMessage()), feedbackChannel);
    }
}
