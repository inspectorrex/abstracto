package dev.sheldan.abstracto.core.listener.async.jda;

import dev.sheldan.abstracto.core.listener.ListenerService;
import dev.sheldan.abstracto.core.models.ServerUser;
import dev.sheldan.abstracto.core.models.cache.CachedMessage;
import dev.sheldan.abstracto.core.models.cache.CachedReactions;
import dev.sheldan.abstracto.core.models.listener.ReactionAddedModel;
import dev.sheldan.abstracto.core.service.*;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class AsyncReactionAddedListenerBean extends ListenerAdapter {

    @Autowired
    private CacheEntityService cacheEntityService;

    @Autowired
    private MessageCache messageCache;

    @Autowired(required = false)
    private List<AsyncReactionAddedListener> listenerList;

    @Autowired
    private AsyncReactionAddedListenerBean self;

    @Autowired
    private EmoteService emoteService;

    @Autowired
    @Qualifier("reactionAddedExecutor")
    private TaskExecutor reactionAddedTaskExecutor;

    @Autowired
    private ListenerService listenerService;

    @Override
    @Transactional
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if(listenerList == null) return;
        if(event.getUserIdLong() == event.getJDA().getSelfUser().getIdLong()) {
            return;
        }
        CompletableFuture<CachedMessage> asyncMessageFromCache = messageCache.getMessageFromCache(event.getGuild().getIdLong(), event.getChannel().getIdLong(), event.getMessageIdLong());
        asyncMessageFromCache.thenAccept(cachedMessage ->
                cacheEntityService.getCachedReactionFromReaction(event.getReaction()).thenAccept(reaction ->
                self.callAddedListeners(event, cachedMessage, reaction)
            ).exceptionally(throwable -> {
                log.error("Failed to handle add reaction to message {} ", event.getMessageIdLong(), throwable);
                return null;
            })
        ).exceptionally(throwable -> {
            log.error("Message retrieval {} from cache failed. ", event.getMessageIdLong(), throwable);
            return null;
        });
    }

    private void addReactionIfNotThere(CachedMessage message, CachedReactions reaction, ServerUser userReacting) {
        Optional<CachedReactions> existingReaction = message.getReactions().stream().filter(reaction1 ->
                reaction1.getEmote().equals(reaction.getEmote())
        ).findAny();
        if(!existingReaction.isPresent()) {
            message.getReactions().add(reaction);
        } else {
            CachedReactions cachedReaction = existingReaction.get();
            Optional<ServerUser> any = cachedReaction.getUsers().stream().filter(user -> user.getUserId().equals(userReacting.getUserId()) && user.getServerId().equals(userReacting.getServerId())).findAny();
            if(!any.isPresent()){
                cachedReaction.getUsers().add(userReacting);
            }
        }
    }

    @Transactional
    public void callAddedListeners(GuildMessageReactionAddEvent event, CachedMessage cachedMessage, CachedReactions reaction) {
        ServerUser serverUser = ServerUser.builder().serverId(event.getGuild().getIdLong()).userId(event.getUserIdLong()).build();
        addReactionIfNotThere(cachedMessage, reaction, serverUser);
        ReactionAddedModel model = getModel(event, cachedMessage, serverUser);
        messageCache.putMessageInCache(cachedMessage);
        listenerList.forEach(asyncReactionAddedListener -> listenerService.executeFeatureAwareListener(asyncReactionAddedListener, model, reactionAddedTaskExecutor));
    }

    private ReactionAddedModel getModel(GuildMessageReactionAddEvent event, CachedMessage cachedMessage, ServerUser userReacting) {
        return ReactionAddedModel
                .builder()
                .reaction(event.getReaction())
                .message(cachedMessage)
                .memberReacting(event.getMember())
                .userReacting(userReacting)
                .build();
    }
}
