package dev.sheldan.abstracto.utility.service.management;

import dev.sheldan.abstracto.core.models.AServerAChannelAUser;
import dev.sheldan.abstracto.core.models.database.AChannel;
import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.abstracto.core.models.database.AUser;
import dev.sheldan.abstracto.core.models.database.AUserInAServer;
import dev.sheldan.abstracto.utility.models.database.Reminder;
import dev.sheldan.abstracto.utility.repository.ReminderRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReminderManagementServiceBeanTest {

    @InjectMocks
    private ReminderManagementServiceBean testUnit;

    @Mock
    private ReminderRepository reminderRepository;

    @Mock
    private AUserInAServer aUserInAServer;

    @Mock
    private AChannel channel;

    @Mock
    private AServer server;

    @Mock
    private Reminder reminder;

    private static final Long REMINDER_ID = 8L;

    @Test
    public void testCreateReminder() {
        AServerAChannelAUser serverAChannelAUser = Mockito.mock(AServerAChannelAUser.class);
        AUser user = Mockito.mock(AUser.class);
        when(aUserInAServer.getUserReference()).thenReturn(user);
        when(serverAChannelAUser.getAUserInAServer()).thenReturn(aUserInAServer);
        when(serverAChannelAUser.getGuild()).thenReturn(server);
        when(serverAChannelAUser.getChannel()).thenReturn(channel);
        String reminderText = "text";
        Instant reminderTargetDate = Instant.ofEpochSecond(1590615937);
        Long messageId = 5L;
        Reminder createdReminder = testUnit.createReminder(serverAChannelAUser, reminderText, reminderTargetDate, messageId);
        Assert.assertEquals(messageId, createdReminder.getMessageId());
        Assert.assertEquals(aUserInAServer, createdReminder.getRemindedUser());
        Assert.assertEquals(server, createdReminder.getServer());
        Assert.assertEquals(reminderText, createdReminder.getText());
        Assert.assertEquals(reminderTargetDate, createdReminder.getTargetDate());
        Assert.assertEquals(channel, createdReminder.getChannel());
        Assert.assertFalse(createdReminder.isReminded());
        verify(reminderRepository, times(1)).save(createdReminder);
    }

    @Test
    public void testSetReminded() {
        testUnit.setReminded(reminder);
        verify(reminder, times(1)).setReminded(true);
        verify(reminderRepository, times(1)).save(reminder);
    }

    @Test
    public void testSaveReminder() {
        testUnit.saveReminder(reminder);
        verify(reminderRepository, times(1)).save(reminder);
    }

    @Test
    public void testRetrieveActiveReminders() {
        Reminder reminder2 = Mockito.mock(Reminder.class);
        List<Reminder> reminders = Arrays.asList(reminder, reminder2);
        when(reminderRepository.getByRemindedUserAndRemindedFalse(aUserInAServer)).thenReturn(reminders);
        List<Reminder> activeRemindersForUser = testUnit.getActiveRemindersForUser(aUserInAServer);
        for (int i = 0; i < reminders.size(); i++) {
            Reminder reference = reminders.get(i);
            Reminder returned = activeRemindersForUser.get(i);
            Assert.assertEquals(reference, returned);
        }
        Assert.assertEquals(reminders.size(), activeRemindersForUser.size());
    }

    @Test
    public void testGetReminderByIdAndNotReminded() {
        when(reminderRepository.getByIdAndRemindedUserAndRemindedFalse(REMINDER_ID, aUserInAServer)).thenReturn(reminder);
        Optional<Reminder> returned = testUnit.getReminderByAndByUserNotReminded(aUserInAServer, REMINDER_ID);
        Assert.assertTrue(returned.isPresent());
        returned.ifPresent(returnedReminder -> Assert.assertEquals(reminder, returnedReminder));
    }

    @Test
    public void testGetReminderByIdAndNotRemindedNothingFound() {
        when(reminderRepository.getByIdAndRemindedUserAndRemindedFalse(REMINDER_ID, aUserInAServer)).thenReturn(null);
        Optional<Reminder> returned = testUnit.getReminderByAndByUserNotReminded(aUserInAServer, REMINDER_ID);
        Assert.assertFalse(returned.isPresent());
    }

    @Test
    public void testLoadingReminder() {
        when(reminderRepository.findById(REMINDER_ID)).thenReturn(Optional.of(reminder));
        Optional<Reminder> returned = testUnit.loadReminderOptional(REMINDER_ID);
        Assert.assertTrue(returned.isPresent());
        returned.ifPresent(returnedReminder -> Assert.assertEquals(reminder, returnedReminder));
    }

    @Test
    public void testLoadingReminderNotFound() {
        when(reminderRepository.findById(REMINDER_ID)).thenReturn(Optional.empty());
        Optional<Reminder> returned = testUnit.loadReminderOptional(REMINDER_ID);
        Assert.assertFalse(returned.isPresent());
    }

}
