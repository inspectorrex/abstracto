package dev.sheldan.abstracto.moderation.commands;

import dev.sheldan.abstracto.core.command.condition.AbstractConditionableCommand;
import dev.sheldan.abstracto.core.command.config.HelpInfo;
import dev.sheldan.abstracto.core.command.config.CommandConfiguration;
import dev.sheldan.abstracto.core.command.config.Parameter;
import dev.sheldan.abstracto.core.command.execution.*;
import dev.sheldan.abstracto.moderation.Moderation;
import dev.sheldan.abstracto.moderation.config.ModerationFeatures;
import dev.sheldan.abstracto.moderation.converter.WarnConverter;
import dev.sheldan.abstracto.moderation.models.dto.WarnDto;
import dev.sheldan.abstracto.moderation.models.template.commands.WarnLogModel;
import dev.sheldan.abstracto.moderation.service.WarnService;
import dev.sheldan.abstracto.templating.service.TemplateService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class Warn extends AbstractConditionableCommand {

    @Autowired
    private WarnService warnService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private WarnConverter warnConverter;

    @Override
    public CommandResult execute(CommandContext commandContext) {
        List<Object> parameters = commandContext.getParameters().getParameters();
        Member member = (Member) parameters.get(0);
        String defaultReason = templateService.renderTemplateWithMap("warn_default_reason", null);
        String reason = parameters.size() == 2 ? (String) parameters.get(1) : defaultReason;
        WarnLogModel warnLogModel = (WarnLogModel) ContextConverter.fromCommandContext(commandContext, WarnLogModel.class);
        warnLogModel.setMessage(commandContext.getMessage());
        WarnDto warnDto = warnService.warnUser(member, commandContext.getAuthor(), reason);
        warnLogModel.setWarning(warnConverter.convertFromWarnDto(warnDto));
        warnService.sendWarnLog(warnLogModel);
        return CommandResult.fromSuccess();
    }

    @Override
    public CommandConfiguration getConfiguration() {
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(Parameter.builder().name("user").type(Member.class).optional(false).build());
        parameters.add(Parameter.builder().name("reason").type(String.class).optional(true).remainder(true).build());
        HelpInfo helpInfo = HelpInfo.builder().templated(true).build();
        return CommandConfiguration.builder()
                .name("warn")
                .module(Moderation.MODERATION)
                .templated(true)
                .causesReaction(true)
                .parameters(parameters)
                .help(helpInfo)
                .build();
    }

    @Override
    public String getFeature() {
        return ModerationFeatures.WARNINGS;
    }
}
