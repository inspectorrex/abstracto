package dev.sheldan.abstracto.core.management;

import dev.sheldan.abstracto.core.models.*;

public interface ServerManagementService {
    AServer createServer(Long id);
    AServer loadServer(Long id);
    void addChannelToServer(AServer server, AChannel channel);
    AUserInAServer addUserToServer(AServer server, AUser user);
    AChannel getPostTarget(Long serverId, String name);
    AChannel getPostTarget(Long serverId, PostTarget target);
    AChannel getPostTarget(AServer server, PostTarget target);
    AChannel getPostTarget(AServer server, String name);
}
