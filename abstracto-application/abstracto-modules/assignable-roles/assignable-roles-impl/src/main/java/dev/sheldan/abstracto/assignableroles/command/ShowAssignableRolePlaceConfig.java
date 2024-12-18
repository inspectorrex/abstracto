package dev.sheldan.abstracto.assignableroles.command;

import dev.sheldan.abstracto.assignableroles.config.AssignableRoleFeatureDefinition;
import dev.sheldan.abstracto.assignableroles.model.template.AssignableRolePlaceConfig;
import dev.sheldan.abstracto.assignableroles.service.AssignableRolePlaceService;
import dev.sheldan.abstracto.core.command.condition.AbstractConditionableCommand;
import dev.sheldan.abstracto.core.command.config.CommandConfiguration;
import dev.sheldan.abstracto.core.command.config.HelpInfo;
import dev.sheldan.abstracto.core.command.config.Parameter;
import dev.sheldan.abstracto.core.command.execution.CommandContext;
import dev.sheldan.abstracto.core.command.execution.CommandResult;
import dev.sheldan.abstracto.core.config.FeatureDefinition;
import dev.sheldan.abstracto.core.service.ChannelService;
import dev.sheldan.abstracto.core.service.management.ServerManagementService;
import dev.sheldan.abstracto.core.utils.FutureUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Command used to show the configuration of an {@link dev.sheldan.abstracto.assignableroles.model.database.AssignableRolePlace place}
 */
@Component
public class ShowAssignableRolePlaceConfig extends AbstractConditionableCommand {

    @Autowired
    private AssignableRolePlaceService service;

    @Autowired
    private ServerManagementService serverManagementService;

    @Autowired
    private ChannelService channelService;

    public static final String ASSIGNABLE_ROLES_CONFIG_POST_TEMPLATE_KEY = "assignable_roles_config_post";

    @Override
    public CompletableFuture<CommandResult> executeAsync(CommandContext commandContext) {
        List<Object> parameters = commandContext.getParameters().getParameters();
        String name = (String) parameters.get(0);
        AssignableRolePlaceConfig config = service.getAssignableRolePlaceConfig(commandContext.getGuild(), name);
        return FutureUtils.toSingleFutureGeneric(channelService.sendEmbedTemplateInMessageChannel(ASSIGNABLE_ROLES_CONFIG_POST_TEMPLATE_KEY, config, commandContext.getChannel()))
                .thenApply(unused -> CommandResult.fromSuccess());
    }

    @Override
    public CommandConfiguration getConfiguration() {
        Parameter rolePostName = Parameter
                .builder()
                .name("name")
                .type(String.class)
                .templated(true)
                .build();
        List<Parameter> parameters = Arrays.asList(rolePostName);
        HelpInfo helpInfo = HelpInfo
                .builder()
                .templated(true)
                .build();
        return CommandConfiguration.builder()
                .name("showAssignableRolePlaceConfig")
                .module(AssignableRoleModuleDefinition.ASSIGNABLE_ROLES)
                .templated(true)
                .async(true)
                .causesReaction(false)
                .messageCommandOnly(true)
                .supportsEmbedException(true)
                .parameters(parameters)
                .help(helpInfo)
                .build();
    }

    @Override
    public FeatureDefinition getFeature() {
        return AssignableRoleFeatureDefinition.ASSIGNABLE_ROLES;
    }
}
