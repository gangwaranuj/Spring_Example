package com.workmarket.common.template.push;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NotificationPushTemplateTest {
    @Test
    public void testMakePushTemplatePath() {
        final String templatePath = NotificationPushTemplate.makePushTemplatePath("WorkCreatedPushTemplate");
        assertEquals("/template/push/WorkCreatedPushTemplate.vm", templatePath);
    }
}
