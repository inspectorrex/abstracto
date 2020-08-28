package dev.sheldan.abstracto.utility.commands.suggest;

import dev.sheldan.abstracto.core.command.exception.IncorrectParameterException;
import dev.sheldan.abstracto.core.command.exception.InsufficientParametersException;
import dev.sheldan.abstracto.core.command.execution.CommandContext;
import dev.sheldan.abstracto.core.command.execution.CommandResult;
import dev.sheldan.abstracto.test.command.CommandConfigValidator;
import dev.sheldan.abstracto.test.command.CommandTestUtilities;
import dev.sheldan.abstracto.utility.models.template.commands.SuggestionLog;
import dev.sheldan.abstracto.utility.service.SuggestionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SuggestTest {

    @InjectMocks
    private Suggest testUnit;

    @Mock
    private SuggestionService suggestionService;

    @Test(expected = InsufficientParametersException.class)
    public void testTooLittleParameters() {
        CommandTestUtilities.executeAsyncNoParametersTest(testUnit);
    }

    @Test(expected = IncorrectParameterException.class)
    public void testIncorrectParameterType() {
        CommandTestUtilities.executeAsyncWrongParametersTest(testUnit);
    }

    @Test
    public void testExecuteCommand() throws ExecutionException, InterruptedException {
        String text = "text";
        CommandContext context = CommandTestUtilities.getWithParameters(Arrays.asList(text));
        when(suggestionService.createSuggestion(eq(context.getAuthor()), eq(text), any(SuggestionLog.class))).thenReturn(CompletableFuture.completedFuture(null));
        CompletableFuture<CommandResult> result = testUnit.executeAsync(context);
        verify(suggestionService, times(1)).createSuggestion(eq(context.getAuthor()), eq(text), any(SuggestionLog.class));
        CommandTestUtilities.checkSuccessfulCompletion(result.get());
    }

    @Test
    public void validateCommand() {
        CommandConfigValidator.validateCommandConfiguration(testUnit.getConfiguration());
    }

}
