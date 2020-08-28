package dev.sheldan.abstracto.moderation.commands;

import dev.sheldan.abstracto.core.command.exception.IncorrectParameterException;
import dev.sheldan.abstracto.core.command.exception.InsufficientParametersException;
import dev.sheldan.abstracto.core.command.execution.CommandContext;
import dev.sheldan.abstracto.core.command.execution.CommandResult;
import dev.sheldan.abstracto.moderation.models.template.commands.BanIdLog;
import dev.sheldan.abstracto.moderation.service.BanService;
import dev.sheldan.abstracto.templating.service.TemplateService;
import dev.sheldan.abstracto.test.command.CommandConfigValidator;
import dev.sheldan.abstracto.test.command.CommandTestUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BanIdTest {

    @InjectMocks
    private BanId testUnit;

    @Mock
    private BanService banService;

    @Mock
    private TemplateService templateService;

    @Captor
    private ArgumentCaptor<BanIdLog> banLogModelCaptor;

    private static final String REASON = "reason";
    private static final Long BANNED_USER_ID = 4L;

    @Test
    public void testBanIdWithDefaultReason() {
        CommandContext parameters = CommandTestUtilities.getWithParameters(Arrays.asList(BANNED_USER_ID));
        Long guildId = parameters.getUserInitiatedContext().getServer().getId();
        when(templateService.renderSimpleTemplate(Ban.BAN_DEFAULT_REASON_TEMPLATE)).thenReturn(REASON);
        when(parameters.getGuild().getIdLong()).thenReturn(guildId);
        CommandResult result = testUnit.execute(parameters);
        verify(banService, times(1)).banMember(eq(guildId), eq(BANNED_USER_ID), eq(REASON), banLogModelCaptor.capture());
        BanIdLog usedModel = banLogModelCaptor.getValue();
        Assert.assertEquals(REASON, usedModel.getReason());
        Assert.assertEquals(BANNED_USER_ID, usedModel.getBannedUserId());
        Assert.assertEquals(parameters.getAuthor(), usedModel.getBanningUser());
        CommandTestUtilities.checkSuccessfulCompletion(result);
    }

    @Test
    public void testBanWithReason() {
        String customReason = "reason2";
        CommandContext parameters = CommandTestUtilities.getWithParameters(Arrays.asList(BANNED_USER_ID, customReason));
        Long guildId = parameters.getUserInitiatedContext().getServer().getId();
        when(parameters.getGuild().getIdLong()).thenReturn(guildId);
        when(templateService.renderSimpleTemplate(Ban.BAN_DEFAULT_REASON_TEMPLATE)).thenReturn(REASON);
        CommandResult result = testUnit.execute(parameters);
        verify(banService, times(1)).banMember(eq(guildId), eq(BANNED_USER_ID), eq(customReason), banLogModelCaptor.capture());
        BanIdLog usedModel = banLogModelCaptor.getValue();
        Assert.assertEquals(customReason, usedModel.getReason());
        Assert.assertEquals(BANNED_USER_ID, usedModel.getBannedUserId());
        Assert.assertEquals(parameters.getAuthor(), usedModel.getBanningUser());
        CommandTestUtilities.checkSuccessfulCompletion(result);
    }



    @Test(expected = InsufficientParametersException.class)
    public void testTooLittleParameters() {
        CommandTestUtilities.executeNoParametersTest(testUnit);
    }

    @Test(expected = IncorrectParameterException.class)
    public void testIncorrectParameterType() {
        CommandTestUtilities.executeWrongParametersTest(testUnit);
    }

    @Test
    public void validateCommand() {
        CommandConfigValidator.validateCommandConfiguration(testUnit.getConfiguration());
    }
}
