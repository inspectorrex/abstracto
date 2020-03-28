package dev.sheldan.abstracto.scheduling.factory;

import dev.sheldan.abstracto.scheduling.config.SchedulerJobProperties;
import dev.sheldan.abstracto.scheduling.model.SchedulerJob;
import org.springframework.stereotype.Component;

@Component
public class SchedulerJobConverter {

    public SchedulerJob fromJobProperties(SchedulerJobProperties properties) {
        return SchedulerJob
                .builder()
                .name(properties.getName())
                .groupName(properties.getGroup())
                .active(properties.getActive())
                .cronExpression(properties.getCronExpression())
                .clazz(properties.getClazz())
                .build();
    }
}