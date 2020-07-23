package dev.sheldan.abstracto.moderation.service;

import dev.sheldan.abstracto.core.models.database.AServer;
import dev.sheldan.abstracto.core.service.PostTargetService;
import dev.sheldan.abstracto.moderation.config.posttargets.ModerationPostTarget;
import dev.sheldan.abstracto.moderation.models.template.commands.KickLogModel;
import dev.sheldan.abstracto.templating.model.MessageToSend;
import dev.sheldan.abstracto.templating.service.TemplateService;
import dev.sheldan.abstracto.test.MockUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KickServiceBeanTest {

    @InjectMocks
    private KickServiceBean testUnit;

    @Mock
    private TemplateService templateService;

    @Mock
    private PostTargetService postTargetService;

    @Test
    public void testKickMember() {
        AServer server = MockUtils.getServer();
        User user = Mockito.mock(User.class);
        Member member = Mockito.mock(Member.class);
        when(member.getUser()).thenReturn(user);
        when(user.getIdLong()).thenReturn(6L);
        Guild mockedGuild = Mockito.mock(Guild.class);
        when(member.getGuild()).thenReturn(mockedGuild);
        AuditableRestAction<Void> mockedAction = Mockito.mock(AuditableRestAction.class);
        String reason = "reason";
        when(mockedGuild.kick(member, reason)).thenReturn(mockedAction);
        KickLogModel model = Mockito.mock(KickLogModel.class);
        when(model.getServer()).thenReturn(server);
        MessageToSend messageToSend = Mockito.mock(MessageToSend.class);
        when(templateService.renderEmbedTemplate(KickServiceBean.KICK_LOG_TEMPLATE, model)).thenReturn(messageToSend);
        testUnit.kickMember(member, reason, model);
        verify(mockedGuild, times(1)).kick(member, reason);
        verify(postTargetService, times(1)).sendEmbedInPostTarget(messageToSend, ModerationPostTarget.KICK_LOG, server.getId());
    }
}