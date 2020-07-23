package dev.sheldan.abstracto.moderation.commands;

import dev.sheldan.abstracto.core.command.exception.IncorrectParameter;
import dev.sheldan.abstracto.core.command.execution.CommandContext;
import dev.sheldan.abstracto.core.command.execution.CommandResult;
import dev.sheldan.abstracto.core.models.database.AUserInAServer;
import dev.sheldan.abstracto.core.service.ChannelService;
import dev.sheldan.abstracto.core.service.management.UserInServerManagementService;
import dev.sheldan.abstracto.moderation.converter.UserNotesConverter;
import dev.sheldan.abstracto.moderation.models.database.UserNote;
import dev.sheldan.abstracto.moderation.models.template.commands.ListNotesModel;
import dev.sheldan.abstracto.moderation.models.template.commands.NoteEntryModel;
import dev.sheldan.abstracto.moderation.service.management.UserNoteManagementService;
import dev.sheldan.abstracto.templating.service.TemplateService;
import dev.sheldan.abstracto.test.MockUtils;
import dev.sheldan.abstracto.test.command.CommandConfigValidator;
import dev.sheldan.abstracto.test.command.CommandTestUtilities;
import net.dv8tion.jda.api.entities.Member;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserNotesTest {

    @InjectMocks
    private UserNotes testUnit;

    @Mock
    private UserNoteManagementService userNoteManagementService;

    @Mock
    private TemplateService templateService;

    @Mock
    private UserInServerManagementService userInServerManagementService;

    @Mock
    private ChannelService channelService;

    @Mock
    private UserNotesConverter userNotesConverter;

    @Captor
    private ArgumentCaptor<ListNotesModel> captor;

    @Test
    public void testExecuteUserNotesCommandForMember() {
        Member member = Mockito.mock(Member.class);
        CommandContext parameters = CommandTestUtilities.getWithParameters(Arrays.asList(member));
        AUserInAServer userNoteUser = MockUtils.getUserObject(4L, parameters.getUserInitiatedContext().getServer());
        when(userInServerManagementService.loadUser(member)).thenReturn(userNoteUser);
        UserNote firstNote = UserNote.builder().build();
        UserNote secondNote = UserNote.builder().build();
        List<UserNote> userNotes = Arrays.asList(firstNote, secondNote);
        when(userNoteManagementService.loadNotesForUser(userNoteUser)).thenReturn(userNotes);
        NoteEntryModel firstConvertedNote = NoteEntryModel.builder().build();
        NoteEntryModel secondConvertedNote = NoteEntryModel.builder().build();
        List<NoteEntryModel> convertedNotes = Arrays.asList(firstConvertedNote, secondConvertedNote);
        when(userNotesConverter.fromNotes(userNotes)).thenReturn(convertedNotes);
        CommandResult result = testUnit.execute(parameters);
        verify(channelService, times(1)).sendEmbedTemplateInChannel(eq(UserNotes.USER_NOTES_RESPONSE_TEMPLATE), captor.capture(), eq(parameters.getChannel()));
        ListNotesModel usedModel = captor.getValue();
        Assert.assertEquals(convertedNotes.size(), usedModel.getUserNotes().size());
        for (int i = 0; i < usedModel.getUserNotes().size(); i++) {
            NoteEntryModel usedEntry = usedModel.getUserNotes().get(i);
            NoteEntryModel expectedEntry = convertedNotes.get(i);
            Assert.assertEquals(expectedEntry, usedEntry);
        }
        Assert.assertEquals(userNoteUser, usedModel.getSpecifiedUser().getAUserInAServer());
        Assert.assertEquals(member, usedModel.getSpecifiedUser().getMember());
        CommandTestUtilities.checkSuccessfulCompletion(result);
    }

    @Test
    public void testExecuteUserNotesCommandForServer() {
        CommandContext parameters = CommandTestUtilities.getNoParameters();
        UserNote firstNote = UserNote.builder().build();
        UserNote secondNote = UserNote.builder().build();
        List<UserNote> userNotes = Arrays.asList(firstNote, secondNote);
        when(userNoteManagementService.loadNotesForServer(parameters.getUserInitiatedContext().getServer())).thenReturn(userNotes);
        NoteEntryModel firstConvertedNote = NoteEntryModel.builder().build();
        NoteEntryModel secondConvertedNote = NoteEntryModel.builder().build();
        List<NoteEntryModel> convertedNotes = Arrays.asList(firstConvertedNote, secondConvertedNote);
        when(userNotesConverter.fromNotes(userNotes)).thenReturn(convertedNotes);
        CommandResult result = testUnit.execute(parameters);
        verify(channelService, times(1)).sendEmbedTemplateInChannel(eq(UserNotes.USER_NOTES_RESPONSE_TEMPLATE), captor.capture(), eq(parameters.getChannel()));
        ListNotesModel usedModel = captor.getValue();
        Assert.assertEquals(convertedNotes.size(), usedModel.getUserNotes().size());
        for (int i = 0; i < usedModel.getUserNotes().size(); i++) {
            NoteEntryModel usedEntry = usedModel.getUserNotes().get(i);
            NoteEntryModel expectedEntry = convertedNotes.get(i);
            Assert.assertEquals(expectedEntry, usedEntry);
        }
        Assert.assertNull(usedModel.getSpecifiedUser());
        CommandTestUtilities.checkSuccessfulCompletion(result);
    }

    @Test(expected = IncorrectParameter.class)
    public void testIncorrectParameterType() {
        CommandTestUtilities.executeWrongParametersTest(testUnit);
    }

    @Test
    public void validateCommand() {
        CommandConfigValidator.validateCommandConfiguration(testUnit.getConfiguration());
    }

}