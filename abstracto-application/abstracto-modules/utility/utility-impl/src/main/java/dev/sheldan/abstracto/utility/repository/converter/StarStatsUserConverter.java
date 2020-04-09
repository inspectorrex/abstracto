package dev.sheldan.abstracto.utility.repository.converter;

import dev.sheldan.abstracto.core.command.service.UserService;
import dev.sheldan.abstracto.core.models.dto.UserDto;
import dev.sheldan.abstracto.core.service.Bot;
import dev.sheldan.abstracto.utility.models.template.commands.starboard.StarStatsUser;
import dev.sheldan.abstracto.utility.repository.StarStatsUserResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StarStatsUserConverter {

    @Autowired
    private UserService userManagementService;

    @Autowired
    private Bot bot;

    public List<StarStatsUser> convertToStarStatsUser(List<StarStatsUserResult> users, Long serverId) {
        List<StarStatsUser> result = new ArrayList<>();
        users.forEach(starStatsUserResult -> {
            StarStatsUser newUser = StarStatsUser
                    .builder()
                    .starCount(starStatsUserResult.getStarCount())
                    .member(bot.getMemberInServer(serverId, starStatsUserResult.getUserId()))
                    .user(UserDto.builder().id(starStatsUserResult.getUserId()).build())
                    .build();
            result.add(newUser);
        });
        return result;
    }
}
