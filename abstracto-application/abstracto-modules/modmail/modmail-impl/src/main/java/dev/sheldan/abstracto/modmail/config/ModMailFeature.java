package dev.sheldan.abstracto.modmail.config;

import dev.sheldan.abstracto.core.config.FeatureConfig;
import dev.sheldan.abstracto.core.config.FeatureEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ModMailFeature implements FeatureConfig {

    @Autowired
    private ModMailLoggingFeature modMailLoggingFeature;

    @Override
    public FeatureEnum getFeature() {
        return ModMailFeatures.MOD_MAIL;
    }

    @Override
    public List<FeatureConfig> getDependantFeatures() {
        return Arrays.asList(modMailLoggingFeature);
    }
}
