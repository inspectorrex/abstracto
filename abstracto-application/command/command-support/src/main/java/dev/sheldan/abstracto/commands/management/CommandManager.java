package dev.sheldan.abstracto.commands.management;

import dev.sheldan.abstracto.command.Command;
import dev.sheldan.abstracto.command.Module;
import dev.sheldan.abstracto.command.execution.CommandConfiguration;
import dev.sheldan.abstracto.command.execution.Parameter;
import dev.sheldan.abstracto.command.meta.CommandRegistry;
import dev.sheldan.abstracto.command.meta.UnParsedCommandParameter;
import dev.sheldan.abstracto.commands.management.exception.CommandNotFoundException;
import dev.sheldan.abstracto.commands.management.exception.InsufficientParametersException;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommandManager implements CommandRegistry {

    @Autowired
    private List<Command> commands;

    @Override
    public Command findCommandByParameters(String name, UnParsedCommandParameter unParsedCommandParameter) {
        Optional<Command> commandOptional = commands.stream().filter((Command o )-> {
            CommandConfiguration commandConfiguration = o.getConfiguration();
            if(!commandConfiguration.getName().equals(name)) {
                return false;
            }
            boolean parameterFit;
            if(commandConfiguration.getParameters() != null){
                boolean paramCountFits = unParsedCommandParameter.getParameters().size() >= commandConfiguration.getNecessaryParameterCount();
                boolean hasRemainderParameter = commandConfiguration.getParameters().stream().anyMatch(Parameter::isRemainder);
                if(unParsedCommandParameter.getParameters().size() < commandConfiguration.getNecessaryParameterCount()) {
                    String nextParameterName = commandConfiguration.getParameters().get(commandConfiguration.getNecessaryParameterCount() - 1).getName();
                    throw new InsufficientParametersException("Insufficient parameters", o, nextParameterName);
                }
                parameterFit = paramCountFits || hasRemainderParameter;
            } else {
                parameterFit = unParsedCommandParameter.getParameters().size() == 0;
            }
            return parameterFit;
        }).findFirst();
        if(commandOptional.isPresent()){
            return commandOptional.get();
        }
        throw new CommandNotFoundException("Command not found.");
    }

    public Command findCommand(String name) {
        Optional<Command> commandOptional = commands.stream().filter((Command o )-> {
            CommandConfiguration commandConfiguration = o.getConfiguration();
            return commandConfiguration.getName().equals(name);
        }).findFirst();
        if(commandOptional.isPresent()){
            return commandOptional.get();
        }
        throw new CommandNotFoundException("Command not found.");
    }

    @Override
    public List<Command> getAllCommands() {
        return commands;
    }

    @Override
    public List<Command> getAllCommandsFromModule(Module module) {
        List<Command> commands = new ArrayList<>();
        this.getAllCommands().forEach(command -> {
            if(command.getConfiguration().getModule().equals(module.getInfo().getName())){
                commands.add(command);
            }
        });
        return commands;
    }

    @Override
    public boolean isCommand(Message message) {
        return message.getContentRaw().startsWith("!");
    }
}
