package dev.sheldan.abstracto.utility.listener.starboard;

import dev.sheldan.abstracto.core.command.service.UserService;
import dev.sheldan.abstracto.core.listener.ReactedAddedListener;
import dev.sheldan.abstracto.core.listener.ReactedRemovedListener;
import dev.sheldan.abstracto.core.models.cache.CachedMessage;
import dev.sheldan.abstracto.core.models.cache.CachedReaction;
import dev.sheldan.abstracto.core.models.dto.EmoteDto;
import dev.sheldan.abstracto.core.models.dto.UserDto;
import dev.sheldan.abstracto.core.models.dto.UserInServerDto;
import dev.sheldan.abstracto.core.service.Bot;
import dev.sheldan.abstracto.core.service.ConfigService;
import dev.sheldan.abstracto.core.service.EmoteService;
import dev.sheldan.abstracto.core.service.MessageCache;
import dev.sheldan.abstracto.core.utils.EmoteUtils;
import dev.sheldan.abstracto.utility.config.UtilityFeatures;
import dev.sheldan.abstracto.utility.models.database.StarboardPost;
import dev.sheldan.abstracto.utility.service.StarboardService;
import dev.sheldan.abstracto.utility.service.management.StarboardPostManagementServiceBean;
import dev.sheldan.abstracto.utility.service.management.StarboardPostReactorManagementServiceBean;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageReaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class StarboardListener implements ReactedAddedListener, ReactedRemovedListener {

    public static final String STAR_EMOTE = "star";

    @Autowired
    private EmoteService emoteManagementService;

    @Autowired
    private Bot bot;

    @Autowired
    private MessageCache messageCache;

    @Autowired
    private ConfigService configManagementService;

    @Autowired
    private StarboardService starboardService;

    @Autowired
    private StarboardPostManagementServiceBean starboardPostManagementService;

    @Autowired
    private StarboardPostReactorManagementServiceBean starboardPostReactorManagementService;

    @Autowired
    private UserService userManagementService;

    @Override
    @Transactional
    public void executeReactionAdded(CachedMessage message, MessageReaction addedReaction, UserInServerDto userAdding) {
        if(userAdding.getUser().getId().equals(message.getAuthorId())) {
            return;
        }
        Long guildId = message.getServerId();
        Optional<EmoteDto> aEmote = emoteManagementService.getEmoteByName(STAR_EMOTE, guildId);
        if(aEmote.isPresent()) {
            EmoteDto emote = aEmote.get();
            MessageReaction.ReactionEmote reactionEmote = addedReaction.getReactionEmote();
            Optional<Emote> emoteInGuild = bot.getEmote(guildId, emote);
            if(EmoteUtils.isReactionEmoteAEmote(reactionEmote, emote, emoteInGuild.orElse(null))) {
                Optional<CachedReaction> reactionOptional = EmoteUtils.getReactionFromMessageByEmote(message, emote);
                    updateStarboardPost(message, reactionOptional.orElse(null), userAdding, true);
            }
        } else {
            log.warn("Emote {} is not defined for guild {}. Starboard not functional.", STAR_EMOTE, guildId);
        }
    }

    private void updateStarboardPost(CachedMessage message, CachedReaction reaction, UserInServerDto userReacting, boolean adding)  {
        Optional<StarboardPost> starboardPostOptional = starboardPostManagementService.findByMessageId(message.getMessageId());
        if(reaction != null) {
            List<UserDto> userExceptAuthor = getUsersExcept(reaction.getUsers(), message.getAuthorId());
            Double starMinimum = configManagementService.getDoubleValue("starLvl1", message.getServerId());
            if (userExceptAuthor.size() >= starMinimum) {
                UserInServerDto author = userManagementService.loadUser(message.getServerId(), message.getAuthorId());
                if(starboardPostOptional.isPresent()) {
                    StarboardPost starboardPost = starboardPostOptional.get();
                    starboardPost.setIgnored(false);
                    starboardService.updateStarboardPost(starboardPost, message, userExceptAuthor);
                    if(adding) {
                        starboardPostReactorManagementService.addReactor(starboardPost, userReacting.getUser());
                    } else {
                        starboardPostReactorManagementService.removeReactor(starboardPost, userReacting.getUser());
                    }
                } else {
                    starboardService.createStarboardPost(message, userExceptAuthor, userReacting, author);
                }
            } else {
                if(starboardPostOptional.isPresent()) {
                    this.completelyRemoveStarboardPost(starboardPostOptional.get());
                }
            }
        } else {
            if(starboardPostOptional.isPresent()) {
                this.completelyRemoveStarboardPost(starboardPostOptional.get());
            }
        }
    }

    private void completelyRemoveStarboardPost(StarboardPost starboardPost)  {
        starboardPostReactorManagementService.removeReactors(starboardPost);
        starboardService.removeStarboardPost(starboardPost);
        starboardPostManagementService.removePost(starboardPost);
    }

    @Override
    @Transactional
    public void executeReactionRemoved(CachedMessage message, MessageReaction removedReaction, UserInServerDto userRemoving) {
        if(message.getAuthorId().equals(userRemoving.getUser().getId())) {
            return;
        }
        Long guildId = message.getServerId();
        Optional<EmoteDto> aEmote = emoteManagementService.getEmoteByName(STAR_EMOTE, guildId);
        if(aEmote.isPresent()) {
            EmoteDto emote = aEmote.get();
            MessageReaction.ReactionEmote reactionEmote = removedReaction.getReactionEmote();
            Optional<Emote> emoteInGuild = bot.getEmote(guildId, emote);
            if(EmoteUtils.isReactionEmoteAEmote(reactionEmote, emote, emoteInGuild.orElse(null))) {
                Optional<CachedReaction> reactionOptional = EmoteUtils.getReactionFromMessageByEmote(message, emote);
                    updateStarboardPost(message, reactionOptional.orElse(null), userRemoving, false);
            }
        } else {
            log.warn("Emote {} is not defined for guild {}. Starboard not functional.", STAR_EMOTE, guildId);
        }
    }

    private List<UserDto> getUsersExcept(List<UserDto> users, Long userId) {
        return users.stream().filter(user -> !user.getId().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public String getFeature() {
        return UtilityFeatures.STARBOARD;
    }
}
