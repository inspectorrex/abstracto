package dev.sheldan.abstracto.core.command.exception;

import dev.sheldan.abstracto.core.command.Command;
import dev.sheldan.abstracto.core.command.Templatable;
import dev.sheldan.abstracto.core.exception.AbstractoRunTimeException;

import java.util.HashMap;

public class ParameterTooLong extends AbstractoRunTimeException implements Templatable {


    private Command command;
    private String parameterName;
    private Integer actualLength;
    private Integer maximumLength;

    public ParameterTooLong(String s, Command command, String parameterName, Integer actualLength, Integer maximumLength) {
        super(s);
        this.command = command;
        this.parameterName = parameterName;
        this.actualLength = actualLength;
        this.maximumLength = maximumLength;
    }

    @Override
    public String getTemplateName() {
        return "parameter_too_long";
    }

    @Override
    public Object getTemplateModel() {
        HashMap<String, Object> model = new HashMap<>();
        model.put("parameterName", parameterName);
        model.put("actualLength", actualLength);
        model.put("maximumLength", maximumLength);
        model.put("command", command);
        return model;
    }
}