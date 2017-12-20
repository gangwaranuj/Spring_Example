package com.workmarket.dao.notification;

import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.notification.AssessmentNotificationPreference;

@Repository
public class AssessmentNotificationPreferenceDAOImpl extends AbstractDAO<AssessmentNotificationPreference> implements AssessmentNotificationPreferenceDAO  {
	
	protected Class<AssessmentNotificationPreference> getEntityClass() {
		return AssessmentNotificationPreference.class;
	}
	
	@Override
	public AssessmentNotificationPreference findByAssessmentAndNotificationType(Long assessmentId, String notificationTypeCode) {
		return (AssessmentNotificationPreference)getFactory().getCurrentSession().getNamedQuery("assessmentNotificationPreference.byAssessmentAndType")
			.setLong("assessment_id", assessmentId)
			.setString("notification_type_code", notificationTypeCode)
			.uniqueResult();
	}
}