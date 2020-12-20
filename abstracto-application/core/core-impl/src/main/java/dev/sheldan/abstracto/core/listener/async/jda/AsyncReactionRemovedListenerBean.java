package dev.sheldan.abstracto.core.listener.async.jda;

import dev.sheldan.abstracto.core.config.FeatureConfig;
import dev.sheldan.abstracto.core.exception.AbstractoRunTimeException;
import dev.sheldan.abstracto.core.models.ServerUser;
import dev.sheldan.abstracto.core.models.cache.CachedMessage;
import dev.sheldan.abstracto.core.models.cache.CachedReactions;
import dev.sheldan.abstracto.core.service.*;
import dev.sheldan.abstracto.core.service.management.UserInServerManagementService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class AsyncReactionRemovedListenerBean extends ListenerAdapter {

    @Autowired
    private CacheEntityService cacheEntityService;

    @Autowired
    private MessageCache messageCache;

    @Autowired
    private UserInServerManagementService userInServerManagementService;

    @Autowired(required = false)
    private List<AsyncReactionRemovedListener> reactionRemovedListeners;

    @Autowired
    private AsyncReactionRemovedListenerBean self;

    @Autowired
    private FeatureConfigService featureConfigService;

    @Autowired
    private FeatureFlagService featureFlagService;

    @Autowired
    private BotService botService;

    @Autowired
    private EmoteService emoteService;

    @Autowired
    @Qualifier("reactionRemovedExecutor")
    private TaskExecutor reactionRemovedExecutor;

    private void removeReactionIfThere(CachedMessage message, CachedReactions reaction, ServerUser serverUser) {
        Optional<CachedReactions> existingReaction = message.getReactions().stream().filter(reaction1 ->
                reaction1.getEmote().equals(reaction.getEmote())
        ).findAny();
        if(existingReaction.isPresent()) {
            CachedReactions cachedReaction = existingReaction.get();
            cachedReaction.getUsers().removeIf(user -> user.getUserId().equals(serverUser.getUserId()) && user.getServerId().equals(serverUser.getServerId()));
            message.getReactions().removeIf(reaction1 -> reaction1.getUsers().isEmpty());
        }
    }

    @Override
    @Transactional
    public void onGuildMessageReactionRemove(@Nonnull GuildMessageReactionRemoveEvent event) {
        if(reactionRemovedListeners == null) return;
        if(event.getUserIdLong() == botService.getInstance().getSelfUser().getIdLong()) {
            return;
        }
        CompletableFuture<CachedMessage> asyncMessageFromCache = messageCache.getMessageFromCache(event.getGuild().getIdLong(), event.getChannel().getIdLong(), event.getMessageIdLong());
        asyncMessageFromCache.thenAccept(cachedMessage -> {
            messageCache.putMessageInCache(cachedMessage);
            cacheEntityService.getCachedReactionFromReaction(event.getReaction()).thenAccept(reaction ->
                self.callRemoveListeners(event, cachedMessage, reaction)
            ) .exceptionally(throwable -> {
                log.error("Failed to retrieve cached reaction for message {} ", event.getMessageIdLong(), throwable);
                return null;
            });
        }).exceptionally(throwable -> {
            log.error("Message retrieval {} from cache failed. ", event.getMessageIdLong(), throwable);
            return null;
        });
    }

    @Transactional
    public void callRemoveListeners(@Nonnull GuildMessageReactionRemoveEvent event, CachedMessage cachedMessage, CachedReactions reaction) {
        ServerUser serverUser = ServerUser.builder().serverId(cachedMessage.getServerId()).userId(event.getUserIdLong()).build();
        removeReactionIfThere(cachedMessage, reaction, serverUser);
        reactionRemovedListeners.forEach(reactionRemovedListener -> {
            try {
                CompletableFuture.runAsync(() ->
                    self.executeIndividualReactionRemovedListener(reaction, cachedMessage, serverUser, reactionRemovedListener)
                , reactionRemovedExecutor)
                .exceptionally(throwable -> {
                    log.error("Async reaction removed listener {} failed with exception.", reactionRemovedListener, throwable);
                    return null;
                });
            } catch (AbstractoRunTimeException e) {
                log.warn(String.format("Failed to execute reaction removed listener %s.", reactionRemovedListener.getClass().getName()), e);
            }
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void executeIndividualReactionRemovedListener(@Nonnull CachedReactions reaction, CachedMessage cachedMessage, ServerUser userRemoving, AsyncReactionRemovedListener reactionRemovedListener) {
        FeatureConfig feature = featureConfigService.getFeatureDisplayForFeature(reactionRemovedListener.getFeature());
        if(!featureFlagService.isFeatureEnabled(feature, cachedMessage.getServerId())) {
            return;
        }
        reactionRemovedListener.executeReactionRemoved(cachedMessage, reaction, userRemoving);
    }

}
