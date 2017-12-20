package com.workmarket.common.template.sms;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SMSTemplateTest {
    @Test
    public void testMakeSMSTemplatePath() {
        final String actual = SMSTemplate.makeSMSTemplatePath("WorkAccepted");
        assertEquals("/template/sms/WorkAccepted.vm", actual);
    }
}
