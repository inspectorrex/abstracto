package dev.sheldan.abstracto.experience.commands;

import dev.sheldan.abstracto.core.command.condition.AbstractConditionableCommand;
import dev.sheldan.abstracto.core.command.config.CommandConfiguration;
import dev.sheldan.abstracto.core.command.config.HelpInfo;
import dev.sheldan.abstracto.core.command.config.Parameter;
import dev.sheldan.abstracto.core.command.execution.CommandContext;
import dev.sheldan.abstracto.core.command.execution.CommandResult;
import dev.sheldan.abstracto.core.config.FeatureEnum;
import dev.sheldan.abstracto.core.models.database.ARole;
import dev.sheldan.abstracto.core.service.management.RoleManagementService;
import dev.sheldan.abstracto.experience.config.features.ExperienceFeature;
import dev.sheldan.abstracto.experience.service.ExperienceRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UnSetExpRole extends AbstractConditionableCommand {

    @Autowired
    private RoleManagementService roleManagementService;

    @Autowired
    private ExperienceRoleService experienceRoleService;

    @Override
    public CommandResult execute(CommandContext commandContext) {
        ARole role = (ARole) commandContext.getParameters().getParameters().get(0);
        // do not check for the existence of the role, because if the role was deleted, users should be able
        // to get rid of it in the configuration
        experienceRoleService.unsetRole(role, commandContext.getUserInitiatedContext().getServer(), commandContext.getUserInitiatedContext().getChannel());
        return CommandResult.fromSuccess();
    }

    @Override
    public CommandConfiguration getConfiguration() {
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(Parameter.builder().name("role").type(ARole.class).build());
        HelpInfo helpInfo = HelpInfo.builder().templated(true).build();
        return CommandConfiguration.builder()
                .name("unSetExpRole")
                .module(ExperienceModule.EXPERIENCE)
                .templated(true)
                .causesReaction(true)
                .parameters(parameters)
                .help(helpInfo)
                .build();
    }

    @Override
    public FeatureEnum getFeature() {
        return ExperienceFeature.EXPERIENCE;
    }
}
