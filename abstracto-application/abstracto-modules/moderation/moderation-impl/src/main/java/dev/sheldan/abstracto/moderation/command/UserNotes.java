package dev.sheldan.abstracto.moderation.command;

import dev.sheldan.abstracto.core.command.condition.AbstractConditionableCommand;
import dev.sheldan.abstracto.core.command.config.CommandConfiguration;
import dev.sheldan.abstracto.core.command.config.HelpInfo;
import dev.sheldan.abstracto.core.command.config.Parameter;
import dev.sheldan.abstracto.core.command.config.SlashCommandConfig;
import dev.sheldan.abstracto.core.command.execution.CommandContext;
import dev.sheldan.abstracto.core.command.execution.CommandResult;
import dev.sheldan.abstracto.core.command.slash.parameter.SlashCommandParameterService;
import dev.sheldan.abstracto.core.config.FeatureDefinition;
import dev.sheldan.abstracto.core.exception.EntityGuildMismatchException;
import dev.sheldan.abstracto.core.interaction.InteractionService;
import dev.sheldan.abstracto.core.models.FullUserInServer;
import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.abstracto.core.models.database.AUserInAServer;
import dev.sheldan.abstracto.core.service.ChannelService;
import dev.sheldan.abstracto.core.service.management.ServerManagementService;
import dev.sheldan.abstracto.core.service.management.UserInServerManagementService;
import dev.sheldan.abstracto.core.utils.FutureUtils;
import dev.sheldan.abstracto.moderation.config.ModerationModuleDefinition;
import dev.sheldan.abstracto.moderation.config.ModerationSlashCommandNames;
import dev.sheldan.abstracto.moderation.config.feature.ModerationFeatureDefinition;
import dev.sheldan.abstracto.moderation.converter.UserNotesConverter;
import dev.sheldan.abstracto.moderation.model.database.UserNote;
import dev.sheldan.abstracto.moderation.model.template.command.ListNotesModel;
import dev.sheldan.abstracto.moderation.model.template.command.NoteEntryModel;
import dev.sheldan.abstracto.moderation.service.management.UserNoteManagementService;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class UserNotes extends AbstractConditionableCommand {
    public static final String USER_NOTES_RESPONSE_TEMPLATE = "user_notes_response";
    public static final String USER_NOTES_COMMAND = "userNotes";
    public static final String USER_PARAMETER = "user";
    @Autowired
    private UserNoteManagementService userNoteManagementService;

    @Autowired
    private UserInServerManagementService userInServerManagementService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private UserNotesConverter userNotesConverter;

    @Autowired
    private ServerManagementService serverManagementService;

    @Autowired
    private SlashCommandParameterService slashCommandParameterService;

    @Autowired
    private InteractionService interactionService;

    @Override
    public CompletableFuture<CommandResult> executeAsync(CommandContext commandContext) {
        List<Object> parameters = commandContext.getParameters().getParameters();
        List<UserNote> userNotes;

        ListNotesModel model = ListNotesModel
                .builder()
                .member(commandContext.getAuthor())
                .build();
        if(parameters.size() == 1) {
            Member member = (Member) parameters.get(0);
            if(!member.getGuild().equals(commandContext.getGuild())) {
                throw new EntityGuildMismatchException();
            }
            AUserInAServer userInAServer = userInServerManagementService.loadOrCreateUser(member);
            userNotes = userNoteManagementService.loadNotesForUser(userInAServer);
            FullUserInServer specifiedUser = FullUserInServer
                    .builder()
                    .aUserInAServer(userInAServer)
                    .member(member)
                    .build();
            model.setSpecifiedUser(specifiedUser);
        } else {
            AServer server = serverManagementService.loadServer(commandContext.getGuild());
            userNotes = userNoteManagementService.loadNotesForServer(server);
        }
        CompletableFuture<List<NoteEntryModel>> listCompletableFuture = userNotesConverter.fromNotes(userNotes);
        return listCompletableFuture.thenCompose(noteEntryModels -> {
            model.setUserNotes(noteEntryModels);
            return FutureUtils.toSingleFutureGeneric(channelService.sendEmbedTemplateInTextChannelList(USER_NOTES_RESPONSE_TEMPLATE, model, commandContext.getChannel()))
                    .thenApply(aVoid -> CommandResult.fromIgnored());
        });
    }

    @Override
    public CompletableFuture<CommandResult> executeSlash(SlashCommandInteractionEvent event) {
        List<UserNote> userNotes;

        ListNotesModel model = ListNotesModel
                .builder()
                .member(event.getMember())
                .build();
        if(slashCommandParameterService.hasCommandOption(USER_PARAMETER, event)) {
            Member member = slashCommandParameterService.getCommandOption(USER_PARAMETER, event, Member.class);
            if(!member.getGuild().equals(event.getGuild())) {
                throw new EntityGuildMismatchException();
            }
            AUserInAServer userInAServer = userInServerManagementService.loadOrCreateUser(member);
            userNotes = userNoteManagementService.loadNotesForUser(userInAServer);
            FullUserInServer specifiedUser = FullUserInServer
                    .builder()
                    .aUserInAServer(userInAServer)
                    .member(member)
                    .build();
            model.setSpecifiedUser(specifiedUser);
        } else {
            AServer server = serverManagementService.loadServer(event.getGuild());
            userNotes = userNoteManagementService.loadNotesForServer(server);
        }
        CompletableFuture<List<NoteEntryModel>> listCompletableFuture = userNotesConverter.fromNotes(userNotes);
        return listCompletableFuture.thenCompose(noteEntryModels -> {
            model.setUserNotes(noteEntryModels);
            return interactionService.replyEmbed(USER_NOTES_RESPONSE_TEMPLATE, model, event)
                    .thenApply(interactionHook -> CommandResult.fromSuccess());
        });
    }

    @Override
    public CommandConfiguration getConfiguration() {
        List<Parameter> parameters = new ArrayList<>();
        Parameter user = Parameter
                .builder()
                .name(USER_PARAMETER)
                .type(Member.class)
                .optional(true)
                .templated(true)
                .build();
        parameters.add(user);
        HelpInfo helpInfo = HelpInfo
                .builder()
                .templated(true)
                .build();

        SlashCommandConfig slashCommandConfig = SlashCommandConfig
                .builder()
                .enabled(true)
                .rootCommandName(ModerationSlashCommandNames.USER_NOTES)
                .commandName("list")
                .build();

        return CommandConfiguration.builder()
                .name(USER_NOTES_COMMAND)
                .module(ModerationModuleDefinition.MODERATION)
                .templated(true)
                .async(true)
                .slashCommandConfig(slashCommandConfig)
                .supportsEmbedException(true)
                .causesReaction(true)
                .parameters(parameters)
                .help(helpInfo)
                .build();
    }

    @Override
    public FeatureDefinition getFeature() {
        return ModerationFeatureDefinition.USER_NOTES;
    }
}
