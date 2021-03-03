package dev.sheldan.abstracto.assignableroles.exceptions;

import dev.sheldan.abstracto.assignableroles.models.exception.AssignableRolePlaceAlreadyExistsExceptionModel;
import dev.sheldan.abstracto.core.exception.AbstractoRunTimeException;
import dev.sheldan.abstracto.core.templating.Templatable;

/**
 * Exception thrown in case the {@link dev.sheldan.abstracto.assignableroles.models.database.AssignableRolePlace place}
 * identified by {@link dev.sheldan.abstracto.assignableroles.models.database.AssignableRolePlace#key}
 */
public class AssignableRolePlaceAlreadyExistsException extends AbstractoRunTimeException implements Templatable {

    private final AssignableRolePlaceAlreadyExistsExceptionModel model;

    public AssignableRolePlaceAlreadyExistsException(String name) {
        super("Assignable role place already exists");
        this.model = AssignableRolePlaceAlreadyExistsExceptionModel.builder().name(name).build();
    }

    @Override
    public String getTemplateName() {
        return "assignable_role_place_exists_exception";
    }

    @Override
    public Object getTemplateModel() {
        return model;
    }
}
