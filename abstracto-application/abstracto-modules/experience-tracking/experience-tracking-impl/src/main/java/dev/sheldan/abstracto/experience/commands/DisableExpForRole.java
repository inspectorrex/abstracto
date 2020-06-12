package dev.sheldan.abstracto.experience.commands;

import dev.sheldan.abstracto.core.command.condition.AbstractConditionableCommand;
import dev.sheldan.abstracto.core.command.config.CommandConfiguration;
import dev.sheldan.abstracto.core.command.config.HelpInfo;
import dev.sheldan.abstracto.core.command.config.Parameter;
import dev.sheldan.abstracto.core.command.execution.CommandContext;
import dev.sheldan.abstracto.core.command.execution.CommandResult;
import dev.sheldan.abstracto.core.config.FeatureEnum;
import dev.sheldan.abstracto.core.models.database.ARole;
import dev.sheldan.abstracto.experience.config.features.ExperienceFeature;
import dev.sheldan.abstracto.experience.service.management.DisabledExpRoleManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Command used to add a role to the roles for which experience has been disabled.
 */
@Component
public class DisableExpForRole extends AbstractConditionableCommand {

    @Autowired
    private DisabledExpRoleManagementService disabledExpRoleManagementService;

    @Override
    public CommandResult execute(CommandContext commandContext) {
        checkParameters(commandContext);
        List<Object> parameters = commandContext.getParameters().getParameters();
        ARole role = (ARole) parameters.get(0);
        if(!disabledExpRoleManagementService.isExperienceDisabledForRole(role)) {
            disabledExpRoleManagementService.setRoleToBeDisabledForExp(role);
        }
        return CommandResult.fromSuccess();
    }

    @Override
    public CommandConfiguration getConfiguration() {
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(Parameter.builder().name("role").templated(true).type(ARole.class).build());
        HelpInfo helpInfo = HelpInfo.builder().templated(true).build();
        return CommandConfiguration.builder()
                .name("disableExpForRole")
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
