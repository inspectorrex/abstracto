package dev.sheldan.abstracto.assignableroles.service.management;

import dev.sheldan.abstracto.assignableroles.exceptions.AssignableRolePlacePostNotFoundException;
import dev.sheldan.abstracto.assignableroles.models.database.AssignableRolePlace;
import dev.sheldan.abstracto.assignableroles.models.database.AssignableRolePlacePost;
import dev.sheldan.abstracto.assignableroles.repository.AssignableRolePlacePostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AssignableRolePlacePostManagementServiceBean implements AssignableRolePlacePostManagementService {

    @Autowired
    private AssignableRolePlacePostRepository repository;

    @Override
    public Optional<AssignableRolePlacePost> findByMessageIdOptional(Long messageId) {
        return repository.findById(messageId);
    }

    @Override
    public AssignableRolePlacePost findByMessageId(Long messageId) {
        return findByMessageIdOptional(messageId).orElseThrow(() -> new AssignableRolePlacePostNotFoundException(messageId));
    }

    @Override
    public AssignableRolePlacePost createAssignableRolePlacePost(AssignableRolePlace updatedPlace, Long messageId) {
        AssignableRolePlacePost post = AssignableRolePlacePost
                .builder()
                .id(messageId)
                .usedChannel(updatedPlace.getChannel())
                .server(updatedPlace.getServer())
                .assignablePlace(updatedPlace)
                .build();
        updatedPlace.getMessagePosts().add(post);
        return post;
    }

}
