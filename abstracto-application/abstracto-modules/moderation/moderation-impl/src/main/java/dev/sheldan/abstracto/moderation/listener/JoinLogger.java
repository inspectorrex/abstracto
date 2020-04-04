package dev.sheldan.abstracto.moderation.listener;

import dev.sheldan.abstracto.core.listener.JoinListener;
import dev.sheldan.abstracto.core.service.PostTargetService;
import dev.sheldan.abstracto.core.service.management.ServerManagementService;
import dev.sheldan.abstracto.moderation.config.ModerationFeatures;
import dev.sheldan.abstracto.templating.TemplateService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.HashMap;

@Service
@Slf4j
public class JoinLogger implements JoinListener {

    private static final String USER_JOIN_TEMPLATE = "user_join";
    private static final String JOIN_LOG_TARGET = "joinLog";

    @Autowired
    private ServerManagementService serverManagementService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private PostTargetService postTargetService;


    @NotNull
    private HashMap<String, Object> getUserParameter(@Nonnull User user) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("user", user);
        return parameters;
    }

    @Override
    public void execute(Member member, Guild guild) {
        HashMap<String, Object> parameters = getUserParameter(member.getUser());
        String text = templateService.renderTemplate(USER_JOIN_TEMPLATE, parameters);;
        postTargetService.sendTextInPostTarget(text, JOIN_LOG_TARGET, guild.getIdLong());
    }

    @Override
    public String getFeature() {
        return ModerationFeatures.LOGGING;
    }
}