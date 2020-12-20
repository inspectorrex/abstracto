package dev.sheldan.abstracto.core.command.handler;

import dev.sheldan.abstracto.core.command.CommandConstants;
import dev.sheldan.abstracto.core.command.handler.provided.ChannelGroupTypeParameterHandler;
import dev.sheldan.abstracto.core.models.database.ChannelGroupType;
import dev.sheldan.abstracto.core.service.management.ChannelGroupTypeManagementService;
import dev.sheldan.abstracto.core.service.management.ServerManagementService;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChannelGroupTypeParameterHandlerImpl implements ChannelGroupTypeParameterHandler {

    @Autowired
    private ChannelGroupTypeManagementService channelGroupTypeManagementService;

    @Autowired
    private ServerManagementService serverManagementService;

    @Override
    public boolean handles(Class clazz) {
        return clazz.equals(ChannelGroupType.class);
    }

    @Override
    public Object handle(String input, CommandParameterIterators iterators, Class clazz, Message context) {
        ChannelGroupType actualGroupType = channelGroupTypeManagementService.findChannelGroupTypeByKey(input);
        return ChannelGroupType
                .builder()
                .groupTypeKey(actualGroupType.getGroupTypeKey())
                .id(actualGroupType.getId())
                .build();
    }

    @Override
    public Integer getPriority() {
        return CommandConstants.CORE_HANDLER_PRIORITY;
    }
}