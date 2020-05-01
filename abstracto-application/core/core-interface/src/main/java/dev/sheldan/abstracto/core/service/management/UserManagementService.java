package dev.sheldan.abstracto.core.service.management;

import dev.sheldan.abstracto.core.models.database.AUser;
import net.dv8tion.jda.api.entities.Member;

public interface UserManagementService {
    AUser createUser(Member member);
    AUser createUser(Long userId);
    AUser loadUser(Long userId);
}
