package dev.sheldan.abstracto.utility.repository;

import dev.sheldan.abstracto.utility.models.database.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

}
