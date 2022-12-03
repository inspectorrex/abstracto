package dev.sheldan.abstracto.entertainment.config;

import dev.sheldan.abstracto.core.config.FeatureConfig;
import dev.sheldan.abstracto.core.config.FeatureDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class EconomyFeatureConfig implements FeatureConfig {

    public static final String PAYDAY_CREDITS_CONFIG_KEY = "paydayCredits";
    public static final String PAYDAY_COOLDOWN_CONFIG_KEY = "paydayCooldown";
    public static final String SLOTS_COOLDOWN_CONFIG_KEY = "slotsCooldown";

    @Override
    public FeatureDefinition getFeature() {
        return EntertainmentFeatureDefinition.ECONOMY;
    }

    @Override
    public List<String> getRequiredSystemConfigKeys() {
        return Arrays.asList(PAYDAY_CREDITS_CONFIG_KEY, PAYDAY_COOLDOWN_CONFIG_KEY, SLOTS_COOLDOWN_CONFIG_KEY);
    }
}
