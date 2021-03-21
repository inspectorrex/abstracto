package dev.sheldan.abstracto.core.models.listener;

import dev.sheldan.abstracto.core.listener.FeatureAwareListenerModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Role;

@Getter
@Setter
@Builder
public class RoleDeletedModel implements FeatureAwareListenerModel {
    private Role role;

    @Override
    public Long getServerId() {
        return role.getGuild().getIdLong();
    }
}
