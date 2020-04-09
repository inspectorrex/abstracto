package dev.sheldan.abstracto.core.repository;

import dev.sheldan.abstracto.core.models.AFeatureFlag;
import dev.sheldan.abstracto.core.models.AServer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeatureFlagRepository extends JpaRepository<AFeatureFlag, Long> {
    AFeatureFlag findByServerAndKey(AServer server, String key);
}
