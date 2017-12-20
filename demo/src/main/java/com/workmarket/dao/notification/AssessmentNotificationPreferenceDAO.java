package com.workmarket.dao.notification;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.notification.AssessmentNotificationPreference;

public interface AssessmentNotificationPreferenceDAO extends DAOInterface<AssessmentNotificationPreference> {
	AssessmentNotificationPreference findByAssessmentAndNotificationType(Long assessmentId, String notificationTypeCode);
}