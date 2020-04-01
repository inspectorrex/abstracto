package dev.sheldan.abstracto.core.management;

import dev.sheldan.abstracto.core.models.database.AConfig;

public interface ConfigManagementService {
    void setOrCreateStringValue(Long serverId, String name, String value);
    void setOrCreateDoubleValue(Long serverId, String name, Double value);
    AConfig createConfig(Long serverId, String name, String value);
    AConfig createConfig(Long serverId, String name, Double value);
    AConfig createIfNotExists(Long serverId, String name, String value);
    AConfig createIfNotExists(Long serverId, String name, Double value);
    AConfig loadConfig(Long serverId, String name);
}
