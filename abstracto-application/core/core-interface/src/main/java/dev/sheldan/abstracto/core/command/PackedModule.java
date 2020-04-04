package dev.sheldan.abstracto.core.command;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PackedModule {
    private ModuleInterface moduleInterface;
    private PackedModule parentModule;
    private List<PackedModule> subModules;
    private List<Command> commands;
}