package dev.sheldan.abstracto.experience.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:experience.properties")
public class ExperienceProperties {
}