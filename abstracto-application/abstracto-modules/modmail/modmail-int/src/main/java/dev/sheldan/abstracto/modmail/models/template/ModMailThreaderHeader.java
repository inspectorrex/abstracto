package dev.sheldan.abstracto.modmail.models.template;

import dev.sheldan.abstracto.core.models.FullUser;
import dev.sheldan.abstracto.modmail.models.database.ModMailThread;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ModMailThreaderHeader {
    private FullUser threadUser;
    private ModMailThread latestModMailThread;
    private Long pastModMailThreads;
}