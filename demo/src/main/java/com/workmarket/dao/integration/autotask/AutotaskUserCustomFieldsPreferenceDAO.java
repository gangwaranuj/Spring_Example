package com.workmarket.dao.integration.autotask;

import com.google.common.base.Optional;
import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.integration.autotask.AutotaskUserCustomFieldsPreference;

import java.util.List;

public interface AutotaskUserCustomFieldsPreferenceDAO extends DAOInterface<AutotaskUserCustomFieldsPreference>{

	public Optional<AutotaskUserCustomFieldsPreference> findByAutotaskUserAndIntegrationCustomFieldCode(Long autotaskUserId, String integrationCustomFieldCode);

	public Optional<List<AutotaskUserCustomFieldsPreference>> findAllPreferencesByAutotaskUser(Long autotaskUserId);
}
