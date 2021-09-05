package dev.sheldan.abstracto.suggestion.service;

import dev.sheldan.abstracto.core.exception.AbstractoRunTimeException;
import dev.sheldan.abstracto.core.exception.ChannelNotInGuildException;
import dev.sheldan.abstracto.core.models.ServerSpecificId;
import dev.sheldan.abstracto.core.models.database.AChannel;
import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.abstracto.core.models.database.AUser;
import dev.sheldan.abstracto.core.models.database.AUserInAServer;
import dev.sheldan.abstracto.core.service.*;
import dev.sheldan.abstracto.core.service.management.ServerManagementService;
import dev.sheldan.abstracto.core.service.management.UserInServerManagementService;
import dev.sheldan.abstracto.core.templating.model.MessageToSend;
import dev.sheldan.abstracto.core.templating.service.TemplateService;
import dev.sheldan.abstracto.suggestion.config.SuggestionFeatureDefinition;
import dev.sheldan.abstracto.suggestion.config.SuggestionFeatureMode;
import dev.sheldan.abstracto.suggestion.config.SuggestionPostTarget;
import dev.sheldan.abstracto.suggestion.exception.SuggestionNotFoundException;
import dev.sheldan.abstracto.suggestion.model.database.Suggestion;
import dev.sheldan.abstracto.suggestion.model.database.SuggestionState;
import dev.sheldan.abstracto.suggestion.model.template.SuggestionLog;
import dev.sheldan.abstracto.suggestion.model.template.SuggestionUpdateModel;
import dev.sheldan.abstracto.suggestion.service.management.SuggestionManagementService;
import net.dv8tion.jda.api.entities.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SuggestionServiceBeanTest {

    public static final String CLOSING_TEXT = "accepted";

    @InjectMocks
    private SuggestionServiceBean testUnit;

    @Mock
    private SuggestionManagementService suggestionManagementService;

    @Mock
    private PostTargetService postTargetService;

    @Mock
    private TemplateService templateService;

    @Mock
    private ChannelService channelService;

    @Mock
    private MemberService memberService;

    @Mock
    private ReactionService reactionService;

    @Mock
    private SuggestionServiceBean self;

    @Mock
    private Guild guild;

    @Mock
    private TextChannel textChannel;

    @Mock
    private CounterService counterService;

    @Mock
    private UserInServerManagementService userInServerManagementService;

    @Mock
    private ServerManagementService serverManagementService;

    @Mock
    private FeatureModeService featureModeService;

    @Mock
    private UserService userService;

    @Mock
    private AServer server;

    @Mock
    private AChannel channel;

    @Mock
    private AUserInAServer suggester;

    @Mock
    private User suggesterUser;

    @Mock
    private Member member;

    @Mock
    private Message message;

    private static final Long SUGGESTER_ID = 8L;
    private static final Long SERVER_ID = 3L;
    private static final Long CHANNEL_ID = 7L;
    private static final Long SUGGESTION_ID = 5L;
    private static final Long USER_ID = 6L;

    @Test
    public void testCreateSuggestionMessage() {
        String suggestionText = "text";
        when(guild.getIdLong()).thenReturn(SERVER_ID);
        when(serverManagementService.loadServer(SERVER_ID)).thenReturn(server);
        MessageToSend messageToSend = Mockito.mock(MessageToSend.class);
        when(templateService.renderEmbedTemplate(eq(SuggestionServiceBean.SUGGESTION_CREATION_TEMPLATE), any(SuggestionLog.class), eq(SERVER_ID))).thenReturn(messageToSend);
        Message suggestionMessage = Mockito.mock(Message.class);
        when(counterService.getNextCounterValue(server, SuggestionServiceBean.SUGGESTION_COUNTER_KEY)).thenReturn(SUGGESTION_ID);
        List<CompletableFuture<Message>> postingFutures = Arrays.asList(CompletableFuture.completedFuture(suggestionMessage));
        when(postTargetService.sendEmbedInPostTarget(messageToSend, SuggestionPostTarget.SUGGESTION, SERVER_ID)).thenReturn(postingFutures);
        when(message.getMember()).thenReturn(member);
        when(member.getGuild()).thenReturn(guild);
        when(member.getIdLong()).thenReturn(SUGGESTER_ID);
        testUnit.createSuggestionMessage(message, suggestionText);
    }

    @Test
    public void testCreateSuggestion() {
        when(member.getGuild()).thenReturn(guild);
        when(guild.getIdLong()).thenReturn(SERVER_ID);
        String text = "text";
        Message message = Mockito.mock(Message.class);
        Message commandMessage = Mockito.mock(Message.class);
        when(featureModeService.featureModeActive(SuggestionFeatureDefinition.SUGGEST, SERVER_ID, SuggestionFeatureMode.SUGGESTION_REMINDER)).thenReturn(false);
        testUnit.persistSuggestionInDatabase(member, text, message, SUGGESTION_ID, commandMessage);
        verify(suggestionManagementService, times(1)).createSuggestion(member, text, message, SUGGESTION_ID, commandMessage);
    }


    @Test(expected = SuggestionNotFoundException.class)
    public void testAcceptNotExistingSuggestion() {
        when(suggestionManagementService.getSuggestion(SERVER_ID, SUGGESTION_ID)).thenThrow(new SuggestionNotFoundException(SUGGESTION_ID));
        when(guild.getIdLong()).thenReturn(SERVER_ID);
        when(message.getGuild()).thenReturn(guild);
        testUnit.acceptSuggestion(SUGGESTION_ID, message, CLOSING_TEXT);
    }

    @Test(expected = SuggestionNotFoundException.class)
    public void testRejectNotExistingSuggestion() {
        when(suggestionManagementService.getSuggestion(SERVER_ID, SUGGESTION_ID)).thenThrow(new SuggestionNotFoundException(SUGGESTION_ID));
        when(guild.getIdLong()).thenReturn(SERVER_ID);
        when(message.getGuild()).thenReturn(guild);
        testUnit.rejectSuggestion(SUGGESTION_ID, message, CLOSING_TEXT);
    }

}
