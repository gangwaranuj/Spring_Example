package com.workmarket.dao.integration.autotask;

import com.google.common.base.Optional;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.integration.autotask.AutotaskUserPreference;
import org.springframework.stereotype.Repository;

@Repository
public class AutotaskUserPreferenceDAOImpl extends AbstractDAO<AutotaskUserPreference> implements AutotaskUserPreferenceDAO {

	protected Class<AutotaskUserPreference> getEntityClass() {
		return AutotaskUserPreference.class;
	}

	@Override
	public Optional<AutotaskUserPreference> findByAutotaskUserAndNotificationType(Long autotaskUserId, String notificationTypeCode) {

		return Optional.fromNullable((AutotaskUserPreference) getFactory().getCurrentSession().getNamedQuery("autotaskUserPreference.byAutotaskUserAndType")
				.setLong("autotask_user_id", autotaskUserId)
				.setString("notification_type_code", notificationTypeCode)
				.uniqueResult());
	}
}
