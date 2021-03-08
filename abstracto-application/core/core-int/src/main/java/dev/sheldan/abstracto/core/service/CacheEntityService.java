package dev.sheldan.abstracto.core.service;

import dev.sheldan.abstracto.core.models.cache.*;
import net.dv8tion.jda.api.entities.*;

import java.util.concurrent.CompletableFuture;

public interface CacheEntityService {
    CachedEmote getCachedEmoteFromEmote(Emote emote, Guild guild);
    CachedEmote getCachedEmoteFromEmote(MessageReaction.ReactionEmote emote, Guild guild);
    CachedAttachment getCachedAttachment(Message.Attachment attachment);
    CachedEmbed getCachedEmbedFromEmbed(MessageEmbed embed);
    CachedThumbnail buildCachedThumbnail(MessageEmbed.Thumbnail thumbnail);
    CachedImageInfo buildCachedImage(MessageEmbed.ImageInfo image);
    CompletableFuture<CachedReactions> getCachedReactionFromReaction(MessageReaction reaction);
    CompletableFuture<CachedMessage> buildCachedMessageFromMessage(Message message);
}