package dev.sheldan.abstracto.experience.service.management;

import dev.sheldan.abstracto.core.exception.AbstractoRunTimeException;
import dev.sheldan.abstracto.core.exception.UserInServerNotFoundException;
import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.abstracto.core.models.database.AUserInAServer;
import dev.sheldan.abstracto.experience.models.database.LeaderBoardEntryResult;
import dev.sheldan.abstracto.experience.models.database.AExperienceLevel;
import dev.sheldan.abstracto.experience.models.database.AUserExperience;
import dev.sheldan.abstracto.experience.repository.UserExperienceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class UserExperienceManagementServiceBean implements UserExperienceManagementService {

    @Autowired
    private UserExperienceRepository repository;

    @Autowired
    private ExperienceLevelManagementService experienceLevelManagementService;

    @Override
    public AUserExperience findUserInServer(AUserInAServer aUserInAServer) {
        Optional<AUserExperience> byId = repository.findById(aUserInAServer.getUserInServerId());
        return byId.orElseGet(() -> createUserInServer(aUserInAServer));
    }

    @Override
    public Optional<AUserExperience> findByUserInServerIdOptional(Long userInServerId) {
       return repository.findById(userInServerId);
    }

    @Override
    public AUserExperience findByUserInServerId(Long userInServerId) {
        return findByUserInServerIdOptional(userInServerId).orElseThrow(() -> new UserInServerNotFoundException(userInServerId));
    }

    /**
     * Initializes the {@link AUserExperience} with default values the following: 0 experience, 0 messages and experience gain enabled
     * @param aUserInAServer The {@link AUserInAServer} to create the {@link AUserExperience} object for.
     * @return The created/changed {@link AUserExperience} object
     */
    @Override
    public AUserExperience createUserInServer(AUserInAServer aUserInAServer) {
        log.info("Creating user experience for user {} in server {}.", aUserInAServer.getUserReference().getId(),aUserInAServer.getServerReference().getId());
        AExperienceLevel startingLevel = experienceLevelManagementService.getLevel(0).orElseThrow(() -> new AbstractoRunTimeException(String.format("Could not find level %s", 0)));
        return AUserExperience
                .builder()
                .experience(0L)
                .messageCount(0L)
                .experienceGainDisabled(false)
                .user(aUserInAServer)
                .id(aUserInAServer.getUserInServerId())
                .currentLevel(startingLevel)
                .build();
    }

    @Override
    public List<AUserExperience> loadAllUsers(AServer server) {
        return repository.findByUser_ServerReference(server);
    }

    @Override
    public List<AUserExperience> findLeaderBoardUsersPaginated(AServer aServer, Integer start, Integer end) {
        return repository.findTop10ByUser_ServerReferenceOrderByExperienceDesc(aServer, PageRequest.of(start, end));
    }

    @Override
    public LeaderBoardEntryResult getRankOfUserInServer(AUserExperience userExperience) {
        return repository.getRankOfUserInServer(userExperience.getId(), userExperience.getUser().getServerReference().getId());
    }

    @Override
    public AUserExperience saveUser(AUserExperience userExperience) {
        return repository.save(userExperience);
    }
}


