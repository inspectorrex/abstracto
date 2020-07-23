package dev.sheldan.abstracto.assignableroles.exceptions;

import dev.sheldan.abstracto.assignableroles.models.exception.AssignedUserNotFoundModel;
import dev.sheldan.abstracto.core.exception.AbstractoRunTimeException;
import dev.sheldan.abstracto.core.models.database.AUserInAServer;
import dev.sheldan.abstracto.templating.Templatable;

public class AssignedUserNotFoundException extends AbstractoRunTimeException implements Templatable {

    private final AssignedUserNotFoundModel model;

    public AssignedUserNotFoundException(AUserInAServer userInAServer) {
        super("Assigned user was not found");
        this.model = AssignedUserNotFoundModel.builder().aUserInAServer(userInAServer).build();
    }

    @Override
    public String getTemplateName() {
        return "assignable_role_place_assigned_user_not_found_exception";
    }

    @Override
    public Object getTemplateModel() {
        return model;
    }
}