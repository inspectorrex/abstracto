package dev.sheldan.abstracto.core.command.exception;

import dev.sheldan.abstracto.core.exception.AbstractoRunTimeException;
import dev.sheldan.abstracto.templating.Templatable;

public class CommandNotFound extends AbstractoRunTimeException implements Templatable {

    public CommandNotFound(String s) {
        super(s);
    }

    @Override
    public String getTemplateName() {
        return "command_not_found";
    }

    @Override
    public Object getTemplateModel() {
        return null;
    }
}
