package dev.sheldan.abstracto.modmail.config;

import dev.sheldan.abstracto.core.config.FeatureConfig;
import dev.sheldan.abstracto.core.config.FeatureEnum;
import dev.sheldan.abstracto.core.config.PostTargetEnum;
import dev.sheldan.abstracto.core.service.FeatureValidator;
import dev.sheldan.abstracto.modmail.validator.ModMailFeatureValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ModMailFeature implements FeatureConfig {

    @Autowired
    private ModMailFeatureValidator modMailFeatureValidator;

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

    @Override
    public List<PostTargetEnum> getRequiredPostTargets() {
        return Arrays.asList(ModMailPostTargets.MOD_MAIL_PING);
    }

    @Override
    public List<FeatureValidator> getAdditionalFeatureValidators() {
        return Arrays.asList(modMailFeatureValidator);
    }

    @Override
    public List<String> getRequiredEmotes() {
        return Arrays.asList("readReaction");
    }
}
