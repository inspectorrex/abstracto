package dev.sheldan.abstracto.core.command.service;

import dev.sheldan.abstracto.core.command.models.ACommand;
import dev.sheldan.abstracto.core.command.models.AModule;
import dev.sheldan.abstracto.core.command.service.CommandService;
import dev.sheldan.abstracto.core.command.service.management.CommandManagementService;
import dev.sheldan.abstracto.core.command.service.management.ModuleManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommandServiceBean implements CommandService {

    @Autowired
    private ModuleManagementService moduleManagementService;

    @Autowired
    private CommandManagementService commandManagementService;

    @Override
    public ACommand createCommand(String name, String moduleName) {
        AModule module = moduleManagementService.getOrCreate(moduleName);
        return commandManagementService.createCommand(name, module);
    }

    @Override
    public Boolean doesCommandExist(String name) {
        return commandManagementService.doesCommandExist(name);
    }


}