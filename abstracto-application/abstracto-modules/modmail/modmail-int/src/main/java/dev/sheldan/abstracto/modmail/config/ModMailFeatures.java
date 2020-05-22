package dev.sheldan.abstracto.modmail.config;

import dev.sheldan.abstracto.core.config.FeatureEnum;

public enum ModMailFeatures implements FeatureEnum {
    MOD_MAIL("modmail");

    private String key;

    ModMailFeatures(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}