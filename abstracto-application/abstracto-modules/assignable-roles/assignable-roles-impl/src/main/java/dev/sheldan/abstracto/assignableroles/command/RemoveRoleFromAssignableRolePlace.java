package dev.sheldan.abstracto.assignableroles.command;

import dev.sheldan.abstracto.assignableroles.config.features.AssignableRoleFeature;
import dev.sheldan.abstracto.assignableroles.service.AssignableRolePlaceService;
import dev.sheldan.abstracto.core.command.condition.AbstractConditionableCommand;
import dev.sheldan.abstracto.core.command.config.CommandConfiguration;
import dev.sheldan.abstracto.core.command.config.HelpInfo;
import dev.sheldan.abstracto.core.command.config.Parameter;
import dev.sheldan.abstracto.core.command.execution.CommandContext;
import dev.sheldan.abstracto.core.command.execution.CommandResult;
import dev.sheldan.abstracto.core.config.FeatureEnum;
import dev.sheldan.abstracto.core.models.FullEmote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class RemoveRoleFromAssignableRolePlace extends AbstractConditionableCommand {

    @Autowired
    private AssignableRolePlaceService service;

    @Override
    public CommandResult execute(CommandContext commandContext) {
        checkParameters(commandContext);
        List<Object> parameters = commandContext.getParameters().getParameters();
        String name = (String) parameters.get(0);
        FullEmote emote = (FullEmote) parameters.get(1);
        service.removeRoleFromAssignableRolePlace(commandContext.getUserInitiatedContext().getServer(), name, emote);
        return CommandResult.fromSuccess();
    }

    @Override
    public CommandConfiguration getConfiguration() {
        Parameter rolePostName = Parameter.builder().name("name").type(String.class).templated(true).build();
        Parameter emote = Parameter.builder().name("emote").type(FullEmote.class).templated(true).build();
        List<Parameter> parameters = Arrays.asList(rolePostName, emote);
        HelpInfo helpInfo = HelpInfo.builder().templated(true).build();
        return CommandConfiguration.builder()
                .name("removeRoleFromAssignableRolePlace")
                .module(AssignableRoleModule.ASSIGNABLE_ROLES)
                .templated(true)
                .causesReaction(true)
                .supportsEmbedException(true)
                .parameters(parameters)
                .help(helpInfo)
                .build();
    }

    @Override
    public FeatureEnum getFeature() {
        return AssignableRoleFeature.ASSIGNABLE_ROLES;
    }
}
