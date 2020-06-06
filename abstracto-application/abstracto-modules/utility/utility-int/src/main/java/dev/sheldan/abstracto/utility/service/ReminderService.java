package dev.sheldan.abstracto.utility.service;

import dev.sheldan.abstracto.core.models.database.AUserInAServer;
import dev.sheldan.abstracto.utility.models.database.Reminder;
import net.dv8tion.jda.api.entities.Message;

import java.time.Duration;

public interface ReminderService {
    Reminder createReminderInForUser(AUserInAServer user, String remindText, Duration remindIn, Message message);
    void executeReminder(Long reminderId);
    void unRemind(Long reminderId, AUserInAServer userInAServer);
}
