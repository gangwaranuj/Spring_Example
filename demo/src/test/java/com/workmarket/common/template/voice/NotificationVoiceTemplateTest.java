package com.workmarket.common.template.voice;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NotificationVoiceTemplateTest {
    @Test
    public void testMakeVoiceTemplatePath() {
        final String actual = VoiceTemplate.makeVoiceTemplatePath("WorkInvitationVoiceTemplate.start");
        assertEquals("/template/voice/WorkInvitationVoiceTemplate.start.vm", actual);
    }
}
