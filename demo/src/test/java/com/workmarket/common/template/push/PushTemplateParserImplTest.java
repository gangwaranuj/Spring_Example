package com.workmarket.common.template.push;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PushTemplateParserImplTest {
    private static final String MESSAGE = "New: D1.  Move Technician\n" + "(Disconnects/Reconnects) - New York, NY 10041 (29.12 miles) Thu, 30 Apr 2015 03:30 PM EDT Hourly rate of $25.00 for a maximum of 6.00 hours \" { }";
    private static final String ACTION = "this is an action string with funny characters \" { }";
    private static final String TEMPLATE_WITH_NO_SPACES = String.format("{\"message\":\"%s\",\"action\":\"%s\"}", MESSAGE, ACTION);
    private static final String TEMPLATE_WITH_SPACES = String.format("  {  \n   \"message\" :   \n\r \"%s\",\"action\":\"%s\"  } ", MESSAGE, ACTION);

    PushTemplateParserImpl parser;

    @Before
    public void setup() {
        parser = new PushTemplateParserImpl();
    }

    @Test
    public void shouldReturnEmptyMessageIfCantParseTemplateString() {
        assertEquals(parser.parseMessage(""), "");
    }

    @Test
    public void shouldReturnEmptyActionIfCantParseTemplateString() {
        assertEquals(parser.parseAction(""), "");
    }

    @Test
    public void shouldReturnMessageWithTrimmedTemplateString() {
        assertEquals(MESSAGE, parser.parseMessage(TEMPLATE_WITH_NO_SPACES));
    }

    @Test
    public void shouldReturnActionWithTrimmedTemplateString() {
        assertEquals(ACTION, parser.parseAction(TEMPLATE_WITH_NO_SPACES));
    }

    @Test
    public void shouldReturnMessageWithUntrimmedTemplateString() {
        assertEquals(MESSAGE, parser.parseMessage(TEMPLATE_WITH_SPACES));
    }

    @Test
    public void shouldReturnActionWithUntrimmedTemplateString() {
        assertEquals(ACTION, parser.parseAction(TEMPLATE_WITH_SPACES));
    }
}