package com.workmarket.common.template.notification;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserNotificationTemplateTest {
    @Test
    public void testMakeNotificationTemplatePath() {
        final String templatePath = UserNotificationTemplate.makeNotificationTemplatePath("HeaderNotificationTemplate");
        assertEquals("/template/notification/HeaderNotificationTemplate.vm", templatePath);
    }
}
