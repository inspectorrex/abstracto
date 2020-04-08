package dev.sheldan.abstracto.moderation.commands;

import dev.sheldan.abstracto.core.command.condition.AbstractConditionableCommand;
import dev.sheldan.abstracto.core.command.config.CommandConfiguration;
import dev.sheldan.abstracto.core.command.config.HelpInfo;
import dev.sheldan.abstracto.core.command.execution.CommandContext;
import dev.sheldan.abstracto.core.command.config.Parameter;
import dev.sheldan.abstracto.core.command.execution.CommandResult;
import dev.sheldan.abstracto.moderation.Moderation;
import dev.sheldan.abstracto.moderation.config.ModerationFeatures;
import dev.sheldan.abstracto.moderation.service.SlowModeService;
import net.dv8tion.jda.api.entities.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class SlowMode extends AbstractConditionableCommand {

    @Autowired
    private SlowModeService slowModeService;

    @Override
    public CommandResult execute(CommandContext commandContext) {
        TextChannel channel;
        long seconds = (Long) commandContext.getParameters().getParameters().get(0);
        if(commandContext.getParameters().getParameters().size() == 2) {
            channel = (TextChannel) commandContext.getParameters().getParameters().get(1);
        } else {
            channel = commandContext.getChannel();
        }
        slowModeService.setSlowMode(channel, Duration.ofSeconds(seconds));
        return CommandResult.fromSuccess();
    }

    @Override
    public CommandConfiguration getConfiguration() {
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(Parameter.builder().name("seconds").type(Long.class).optional(false).build());
        parameters.add(Parameter.builder().name("channel").type(TextChannel.class).optional(true).build());
        HelpInfo helpInfo = HelpInfo.builder().templated(true).build();
        return CommandConfiguration.builder()
                .name("slowmode")
                .module(Moderation.MODERATION)
                .templated(true)
                .causesReaction(true)
                .parameters(parameters)
                .help(helpInfo)
                .build();
    }

    @Override
    public String getFeature() {
        return ModerationFeatures.MODERATION;
    }
}