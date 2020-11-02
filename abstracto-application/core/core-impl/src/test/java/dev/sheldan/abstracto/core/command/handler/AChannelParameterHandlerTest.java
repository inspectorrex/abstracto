package dev.sheldan.abstracto.core.command.handler;

import dev.sheldan.abstracto.core.models.database.AChannel;
import dev.sheldan.abstracto.core.service.ChannelService;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AChannelParameterHandlerTest {

    @InjectMocks
    private AChannelParameterHandler testUnit;

    @Mock
    private TextChannelParameterHandler textChannelParameterHandler;

    @Mock
    private ChannelService channelService;

    @Mock
    private CommandParameterIterators iterators;

    @Mock
    private TextChannel channel;

    @Mock
    private Message message;

    @Mock
    private AChannel aChannel;

    @Test
    public void testSuccessfulCondition() {
        Assert.assertTrue(testUnit.handles(AChannel.class));
    }

    @Test
    public void testWrongCondition() {
        Assert.assertFalse(testUnit.handles(String.class));
    }

    @Test
    public void testProperChannelMention() {
        String input = "test";
        when(textChannelParameterHandler.handle(input, iterators, TextChannel.class, message)).thenReturn(channel);
        when(channelService.getFakeChannelFromTextChannel(channel)).thenReturn(aChannel);
        AChannel parsed = (AChannel) testUnit.handle(input, iterators, TextChannel.class, message);
        Assert.assertEquals(aChannel, parsed);
    }


}