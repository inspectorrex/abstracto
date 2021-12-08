package dev.sheldan.abstracto.core.commands.channels;

import dev.sheldan.abstracto.core.command.condition.AbstractConditionableCommand;
import dev.sheldan.abstracto.core.command.config.CommandConfiguration;
import dev.sheldan.abstracto.core.command.config.HelpInfo;
import dev.sheldan.abstracto.core.command.config.Parameter;
import dev.sheldan.abstracto.core.command.config.features.CoreFeatureDefinition;
import dev.sheldan.abstracto.core.command.execution.CommandContext;
import dev.sheldan.abstracto.core.command.execution.CommandResult;
import dev.sheldan.abstracto.core.config.FeatureDefinition;
import dev.sheldan.abstracto.core.exception.PostTargetNotValidException;
import dev.sheldan.abstracto.core.service.PostTargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class EnablePostTarget extends AbstractConditionableCommand {

    @Autowired
    private PostTargetService postTargetService;

    @Override
    public CommandResult execute(CommandContext commandContext) {
        String targetName = (String) commandContext.getParameters().getParameters().get(0);
        if(!postTargetService.validPostTarget(targetName)) {
            throw new PostTargetNotValidException(targetName, postTargetService.getAvailablePostTargets());
        }
        postTargetService.enablePostTarget(targetName, commandContext.getGuild().getIdLong());
        return CommandResult.fromSuccess();
    }

    @Override
    public CommandConfiguration getConfiguration() {
        Parameter postTargetName = Parameter
                .builder()
                .name("name")
                .type(String.class)
                .templated(true)
                .build();
        List<Parameter> parameters = Arrays.asList(postTargetName);
        HelpInfo helpInfo = HelpInfo
                .builder()
                .templated(true)
                .build();
        return CommandConfiguration.builder()
                .name("enablePosttarget")
                .module(ChannelsModuleDefinition.CHANNELS)
                .parameters(parameters)
                .supportsEmbedException(true)
                .help(helpInfo)
                .templated(true)
                .causesReaction(true)
                .build();
    }

    @Override
    public FeatureDefinition getFeature() {
        return CoreFeatureDefinition.CORE_FEATURE;
    }
}