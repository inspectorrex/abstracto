package dev.sheldan.abstracto.core.service;

import dev.sheldan.abstracto.core.command.exception.ChannelGroupException;
import dev.sheldan.abstracto.core.command.exception.CommandException;
import dev.sheldan.abstracto.core.command.models.database.ACommand;
import dev.sheldan.abstracto.core.command.service.management.ChannelGroupCommandManagementService;
import dev.sheldan.abstracto.core.command.service.management.CommandManagementService;
import dev.sheldan.abstracto.core.models.database.AChannel;
import dev.sheldan.abstracto.core.models.database.AChannelGroup;
import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.abstracto.core.service.management.ChannelGroupManagementService;
import dev.sheldan.abstracto.core.service.management.ChannelManagementService;
import dev.sheldan.abstracto.core.service.management.ServerManagementService;
import net.dv8tion.jda.api.entities.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChannelGroupServiceBean implements ChannelGroupService {

    @Autowired
    private ChannelGroupManagementService channelGroupManagementService;

    @Autowired
    private ChannelManagementService channelManagementService;

    @Autowired
    private CommandManagementService commandManagementService;

    @Autowired
    private ChannelGroupCommandManagementService channelGroupCommandManagementService;

    @Autowired
    private ServerManagementService serverManagementService;

    @Override
    public AChannelGroup createChannelGroup(String name, Long serverId) {
        AServer server = serverManagementService.loadOrCreate(serverId);
        return channelGroupManagementService.createChannelGroup(name, server);
    }

    @Override
    public void deleteChannelGroup(String name, Long serverId) {
        AServer server = serverManagementService.loadOrCreate(serverId);
        channelGroupManagementService.deleteChannelGroup(name, server);
    }

    @Override
    public void addChannelToChannelGroup(String channelGroupName, TextChannel textChannel) {
        addChannelToChannelGroup(channelGroupName, textChannel.getIdLong());
    }

    @Override
    public void addChannelToChannelGroup(String channelGroupName, Long channelId) {
        AChannel aChannel = channelManagementService.loadChannel(channelId);
        addChannelToChannelGroup(channelGroupName, aChannel);
    }

    @Override
    public void addChannelToChannelGroup(String channelGroupName, AChannel channel) {
        AServer server = serverManagementService.loadOrCreate(channel.getServer().getId());
        AChannelGroup channelGroup = channelGroupManagementService.findByNameAndServer(channelGroupName, server);
        if(channelGroup == null) {
            throw new ChannelGroupException(String.format("Channel group %s was not found.", channelGroupName));
        }
        channelGroupManagementService.addChannelToChannelGroup(channelGroup, channel);
    }

    @Override
    public void removeChannelFromChannelGroup(String channelGroupName, TextChannel textChannel) {
        removeChannelFromChannelGroup(channelGroupName, textChannel.getIdLong());
    }

    @Override
    public void removeChannelFromChannelGroup(String channelGroupName, Long channelId) {
        AChannel aChannel = channelManagementService.loadChannel(channelId);
        removeChannelFromChannelGroup(channelGroupName, aChannel);
    }

    @Override
    public void removeChannelFromChannelGroup(String channelGroupName, AChannel channel) {
        AServer server = serverManagementService.loadOrCreate(channel.getServer().getId());
        AChannelGroup channelGroup = channelGroupManagementService.findByNameAndServer(channelGroupName, server);
        if(channelGroup == null) {
            throw new ChannelGroupException(String.format("Channel group %s was not found", channelGroupName));
        }
        channelGroupManagementService.removeChannelFromChannelGroup(channelGroup, channel);
    }

    @Override
    public void disableCommandInChannelGroup(String commandName, String channelGroupName, Long serverId) {
        AServer server = serverManagementService.loadOrCreate(serverId);
        AChannelGroup channelGroup = channelGroupManagementService.findByNameAndServer(channelGroupName, server);
        if(channelGroup == null) {
            throw new ChannelGroupException(String.format("Channel group %s was not found", channelGroupName));
        }
        ACommand command = commandManagementService.findCommandByName(commandName);
        if(command == null) {
            throw new CommandException(String.format("Command %s not found.", commandName));
        }
        channelGroupCommandManagementService.setCommandInGroupTo(command, channelGroup, false);
    }

    @Override
    public void enableCommandInChannelGroup(String commandName, String channelGroupName, Long serverId) {
        AServer server = serverManagementService.loadOrCreate(serverId);
        AChannelGroup channelGroup = channelGroupManagementService.findByNameAndServer(channelGroupName, server);
        if(channelGroup == null) {
            throw new ChannelGroupException(String.format("Channel group %s was not found", channelGroupName));
        }
        ACommand command = commandManagementService.findCommandByName(commandName);
        if(command == null) {
            throw new CommandException(String.format("Command %s not found.", commandName));
        }
        channelGroupCommandManagementService.setCommandInGroupTo(command, channelGroup, true);
    }

    @Override
    public boolean doesGroupExist(String groupName, Long serverId) {
        AServer server = serverManagementService.loadOrCreate(serverId);
        return channelGroupManagementService.findByNameAndServer(groupName, server) != null;
    }
}
