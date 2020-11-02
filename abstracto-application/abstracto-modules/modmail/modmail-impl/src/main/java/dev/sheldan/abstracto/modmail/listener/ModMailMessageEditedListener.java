package dev.sheldan.abstracto.modmail.listener;

import dev.sheldan.abstracto.core.command.config.Parameters;
import dev.sheldan.abstracto.core.command.service.CommandRegistry;
import dev.sheldan.abstracto.core.command.service.CommandService;
import dev.sheldan.abstracto.core.config.FeatureEnum;
import dev.sheldan.abstracto.core.config.ListenerPriority;
import dev.sheldan.abstracto.core.listener.MessageTextUpdatedListener;
import dev.sheldan.abstracto.core.models.FullUserInServer;
import dev.sheldan.abstracto.core.models.cache.CachedMessage;
import dev.sheldan.abstracto.core.models.database.AChannel;
import dev.sheldan.abstracto.core.service.BotService;
import dev.sheldan.abstracto.core.service.ChannelService;
import dev.sheldan.abstracto.core.service.MessageService;
import dev.sheldan.abstracto.modmail.config.ModMailFeatures;
import dev.sheldan.abstracto.modmail.models.database.ModMailMessage;
import dev.sheldan.abstracto.modmail.models.template.ModMailModeratorReplyModel;
import dev.sheldan.abstracto.modmail.service.ModMailThreadService;
import dev.sheldan.abstracto.modmail.service.ModMailThreadServiceBean;
import dev.sheldan.abstracto.modmail.service.management.ModMailMessageManagementService;
import dev.sheldan.abstracto.templating.model.MessageToSend;
import dev.sheldan.abstracto.templating.service.TemplateService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class ModMailMessageEditedListener implements MessageTextUpdatedListener {

    public static final String DEFAULT_COMMAND_FOR_MODMAIL_EDIT = "reply";
    @Autowired
    private ModMailMessageManagementService modMailMessageManagementService;

    @Autowired
    private CommandService commandService;

    @Autowired
    private BotService botService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private ModMailMessageEditedListener self;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private CommandRegistry commandRegistry;

    @Autowired
    private ModMailThreadService modMailThreadService;

    @Override
    public void execute(CachedMessage messageBefore, Message messageAfter) {
        if(!modMailThreadService.isModMailThread(messageBefore.getChannelId())) {
            return;
        }
        Optional<ModMailMessage> messageOptional = modMailMessageManagementService.getByMessageIdOptional(messageBefore.getMessageId());
        messageOptional.ifPresent(modMailMessage -> {
            log.info("Editing send message {} in channel {} in mod mail thread {} in server {}.", messageBefore.getMessageId(), messageBefore.getChannelId(), modMailMessage.getThreadReference().getId(), messageBefore.getServerId());
            String contentStripped = messageAfter.getContentRaw();
            String commandName = commandRegistry.getCommandName(contentStripped.substring(0, contentStripped.indexOf(" ")), messageBefore.getServerId());
            if(!commandService.doesCommandExist(commandName)) {
                commandName = DEFAULT_COMMAND_FOR_MODMAIL_EDIT;
                log.info("Edit did not contain the original command to retrieve the parameters for. Resulting to {}.", DEFAULT_COMMAND_FOR_MODMAIL_EDIT);
            }
            CompletableFuture<Parameters> parameterParseFuture = commandService.getParametersForCommand(commandName, messageAfter);
            CompletableFuture<Member> loadTargetUser = botService.getMemberInServerAsync(messageBefore.getServerId(), modMailMessage.getThreadReference().getUser().getUserReference().getId());
            CompletableFuture<Member> loadEditingUser = botService.getMemberInServerAsync(messageBefore.getServerId(), modMailMessage.getAuthor().getUserReference().getId());
            CompletableFuture.allOf(parameterParseFuture, loadTargetUser, loadEditingUser).thenAccept(unused ->
                    self.updateMessageInThread(messageAfter, parameterParseFuture.join(), loadTargetUser.join(), loadEditingUser.join())
            );
        });
    }

    @Transactional
    public void updateMessageInThread(Message messageAfter, Parameters parameters, Member targetMember, Member editingUser) {
        String newText = (String) parameters.getParameters().get(0);
        Optional<ModMailMessage> messageOptional = modMailMessageManagementService.getByMessageIdOptional(messageAfter.getIdLong());
        messageOptional.ifPresent(modMailMessage -> {
            FullUserInServer fullThreadUser = FullUserInServer
                    .builder()
                    .aUserInAServer(modMailMessage.getThreadReference().getUser())
                    .member(targetMember)
                    .build();
            ModMailModeratorReplyModel.ModMailModeratorReplyModelBuilder modMailModeratorReplyModelBuilder = ModMailModeratorReplyModel
                    .builder()
                    .text(newText)
                    .modMailThread(modMailMessage.getThreadReference())
                    .postedMessage(messageAfter)
                    .anonymous(modMailMessage.getAnonymous())
                    .threadUser(fullThreadUser);
            if(modMailMessage.getAnonymous()) {
                modMailModeratorReplyModelBuilder.moderator(botService.getBotInGuild(modMailMessage.getThreadReference().getServer()));
            } else {
                modMailModeratorReplyModelBuilder.moderator(editingUser);
            }
            ModMailModeratorReplyModel modMailUserReplyModel = modMailModeratorReplyModelBuilder.build();
            MessageToSend messageToSend = templateService.renderEmbedTemplate(ModMailThreadServiceBean.MODMAIL_STAFF_MESSAGE_TEMPLATE_KEY, modMailUserReplyModel);
            Long threadId = modMailMessage.getThreadReference().getId();
            long serverId = editingUser.getGuild().getIdLong();
            if(modMailMessage.getCreatedMessageInChannel() != null) {
                AChannel channel = modMailMessage.getThreadReference().getChannel();
                log.trace("Editing message {} in mod mail channel {} for thread {} in server {} as well.", modMailMessage.getCreatedMessageInChannel(), channel.getId(), threadId, serverId);
                channelService.editMessageInAChannel(messageToSend, channel, modMailMessage.getCreatedMessageInChannel());
            }
            log.trace("Editing message {} in DM channel with user {} for thread {} in server {}.", modMailMessage.getCreatedMessageInDM(), targetMember.getUser().getIdLong(), threadId, serverId);
            messageService.editMessageInDMChannel(targetMember.getUser(), messageToSend, modMailMessage.getCreatedMessageInDM());
        });

        if(!messageOptional.isPresent()) {
            log.warn("Message {} of user {} in channel {} for server {} for thread about user {} could not be found in the mod mail messages when updating the text.",
                    messageAfter.getIdLong(), editingUser.getIdLong(), messageAfter.getChannel().getIdLong(), messageAfter.getGuild().getIdLong(), targetMember.getIdLong());
        }
    }

    @Override
    public FeatureEnum getFeature() {
        return ModMailFeatures.MOD_MAIL;
    }

    @Override
    public Integer getPriority() {
        return ListenerPriority.HIGH;
    }
}