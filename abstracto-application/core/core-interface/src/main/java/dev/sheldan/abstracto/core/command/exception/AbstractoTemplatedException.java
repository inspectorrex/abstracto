package dev.sheldan.abstracto.core.command.exception;

import dev.sheldan.abstracto.core.exception.AbstractoRunTimeException;
import dev.sheldan.abstracto.templating.Templatable;


public class AbstractoTemplatedException extends AbstractoRunTimeException implements Templatable {

    private final String templateKey;

    public AbstractoTemplatedException(String message, String templateKey) {
        super(message);
        this.templateKey = templateKey;
    }

    @Override
    public String getTemplateName() {
        return templateKey;
    }

    @Override
    public Object getTemplateModel() {
        return new Object();
    }
}
