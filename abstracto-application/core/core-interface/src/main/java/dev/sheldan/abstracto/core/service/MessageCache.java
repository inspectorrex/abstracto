package dev.sheldan.abstracto.core.service;

import dev.sheldan.abstracto.core.models.CachedMessage;
import dev.sheldan.abstracto.core.models.CachedReaction;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface MessageCache {
    CompletableFuture<CachedMessage> putMessageInCache(Message message);
    CompletableFuture<CachedMessage> getMessageFromCache(Long guildId, Long textChannelId, Long messageId);
    CompletableFuture<CachedMessage> getMessageFromCache(Message message);
    CompletableFuture<CachedMessage> putMessageInCache(CachedMessage message);
    void loadMessage(CompletableFuture<CachedMessage> future, Long guildId, Long textChannelId, Long messageId);
    void getCachedReactionFromReaction(CompletableFuture<CachedReaction> future, MessageReaction reaction);
    void buildCachedMessageFromMessage(CompletableFuture<CachedMessage> future, Message message);
}
