package dev.sheldan.abstracto.moderation.commands;

import dev.sheldan.abstracto.core.command.condition.AbstractConditionableCommand;
import dev.sheldan.abstracto.core.command.condition.CommandCondition;
import dev.sheldan.abstracto.core.command.config.HelpInfo;
import dev.sheldan.abstracto.core.command.config.CommandConfiguration;
import dev.sheldan.abstracto.core.command.config.Parameter;
import dev.sheldan.abstracto.core.command.execution.*;
import dev.sheldan.abstracto.core.config.FeatureEnum;
import dev.sheldan.abstracto.moderation.config.ModerationModule;
import dev.sheldan.abstracto.moderation.config.features.ModerationFeatures;
import dev.sheldan.abstracto.moderation.models.template.commands.KickLogModel;
import dev.sheldan.abstracto.moderation.service.KickServiceBean;
import dev.sheldan.abstracto.templating.service.TemplateService;
import net.dv8tion.jda.api.entities.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Kick extends AbstractConditionableCommand {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private KickServiceBean kickService;

    @Override
    public CommandResult execute(CommandContext commandContext) {

        List<Object> parameters = commandContext.getParameters().getParameters();
        Member member = (Member) parameters.get(0);
        String defaultReason = templateService.renderTemplateWithMap("ban_default_reason", null);
        String reason = parameters.size() == 2 ? (String) parameters.get(1) : defaultReason;

        KickLogModel kickLogModel = (KickLogModel) ContextConverter.fromCommandContext(commandContext, KickLogModel.class);
        kickLogModel.setKickedUser(member);
        kickLogModel.setKickingUser(commandContext.getAuthor());
        kickLogModel.setReason(reason);
        kickService.kickMember(member, reason, kickLogModel);
        return CommandResult.fromSuccess();
    }

    @Override
    public CommandConfiguration getConfiguration() {
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(Parameter.builder().name("user").templated(true).type(Member.class).build());
        parameters.add(Parameter.builder().name("reason").templated(true).type(String.class).optional(true).remainder(true).build());
        HelpInfo helpInfo = HelpInfo.builder().templated(true).build();
        return CommandConfiguration.builder()
                .name("kick")
                .module(ModerationModule.MODERATION)
                .templated(true)
                .causesReaction(true)
                .parameters(parameters)
                .help(helpInfo)
                .build();
    }

    @Override
    public FeatureEnum getFeature() {
        return ModerationFeatures.MODERATION;
    }

    @Override
    public List<CommandCondition> getConditions() {
        List<CommandCondition> conditions = super.getConditions();
        conditions.add(immuneUserCondition);
        return conditions;
    }
}
