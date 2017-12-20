package com.workmarket.dao.integration.autotask;

import com.google.common.base.Optional;
import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.integration.autotask.AutotaskUserPreference;

public interface AutotaskUserPreferenceDAO extends DAOInterface<AutotaskUserPreference>{

	public Optional<AutotaskUserPreference> findByAutotaskUserAndNotificationType(Long autotaskUserId, String notificationTypeCode);

}
