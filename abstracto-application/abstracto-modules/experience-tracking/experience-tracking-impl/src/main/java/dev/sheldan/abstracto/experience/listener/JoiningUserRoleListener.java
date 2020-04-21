package dev.sheldan.abstracto.experience.listener;

import dev.sheldan.abstracto.core.listener.JoinListener;
import dev.sheldan.abstracto.core.models.database.AUserInAServer;
import dev.sheldan.abstracto.core.service.management.UserManagementService;
import dev.sheldan.abstracto.experience.config.ExperienceFeatures;
import dev.sheldan.abstracto.experience.models.database.AUserExperience;
import dev.sheldan.abstracto.experience.service.ExperienceTrackerService;
import dev.sheldan.abstracto.experience.service.management.UserExperienceManagementService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JoiningUserRoleListener implements JoinListener {

    @Autowired
    private UserExperienceManagementService userExperienceManagementService;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private ExperienceTrackerService experienceTrackerService;

    @Override
    public void execute(Member member, Guild guild, AUserInAServer aUserInAServer) {
        AUserExperience userExperience = userExperienceManagementService.findUserInServer(aUserInAServer);
        if(userExperience != null) {
            log.info("User {} joined {} with previous experience. Setting up experience role again (if necessary).", member.getUser().getIdLong(), guild.getIdLong());
            experienceTrackerService.syncForSingleUser(userExperience);
        }
    }

    @Override
    public String getFeature() {
        return ExperienceFeatures.EXPERIENCE;
    }
}
