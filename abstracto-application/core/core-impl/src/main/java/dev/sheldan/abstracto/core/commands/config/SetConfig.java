package dev.sheldan.abstracto.core.commands.config;

import dev.sheldan.abstracto.core.command.condition.AbstractConditionableCommand;
import dev.sheldan.abstracto.core.command.config.CommandConfiguration;
import dev.sheldan.abstracto.core.command.config.HelpInfo;
import dev.sheldan.abstracto.core.command.config.Parameter;
import dev.sheldan.abstracto.core.command.execution.CommandContext;
import dev.sheldan.abstracto.core.command.execution.CommandResult;
import dev.sheldan.abstracto.core.config.FeatureEnum;
import dev.sheldan.abstracto.core.config.features.CoreFeatures;
import dev.sheldan.abstracto.core.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SetConfig extends AbstractConditionableCommand {

    @Autowired
    private ConfigService configService;


    @Override
    public CommandResult execute(CommandContext commandContext) {
        String key = (String) commandContext.getParameters().getParameters().get(0);
        String value = (String) commandContext.getParameters().getParameters().get(1);
        configService.setConfigValue(key, commandContext.getGuild().getIdLong(), value);

        return CommandResult.fromSuccess();
    }

    @Override
    public CommandConfiguration getConfiguration() {
        Parameter keyToChange = Parameter.builder().name("key").type(String.class).description("The key to change.").build();
        Parameter valueToSet = Parameter.builder().name("value").type(String.class).description("The value to set the key to.").build();
        List<Parameter> parameters = Arrays.asList(keyToChange, valueToSet);
        HelpInfo helpInfo = HelpInfo.builder().templated(true).build();
        return CommandConfiguration.builder()
                .name("setConfig")
                .module(ConfigModuleInterface.CONFIG)
                .parameters(parameters)
                .templated(true)
                .help(helpInfo)
                .causesReaction(true)
                .build();
    }

    @Override
    public FeatureEnum getFeature() {
        return CoreFeatures.CORE_FEATURE;
    }
}