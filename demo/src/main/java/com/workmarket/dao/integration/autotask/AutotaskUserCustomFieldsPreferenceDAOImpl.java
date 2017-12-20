package com.workmarket.dao.integration.autotask;

import com.google.common.base.Optional;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.integration.autotask.AutotaskUserCustomFieldsPreference;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AutotaskUserCustomFieldsPreferenceDAOImpl extends AbstractDAO<AutotaskUserCustomFieldsPreference> implements AutotaskUserCustomFieldsPreferenceDAO{
	protected Class<AutotaskUserCustomFieldsPreference> getEntityClass() {
		return AutotaskUserCustomFieldsPreference.class;
	}

	@Override
	public Optional<AutotaskUserCustomFieldsPreference> findByAutotaskUserAndIntegrationCustomFieldCode(Long autotaskUserId, String integrationCustomFieldCode) {

		return Optional.fromNullable((AutotaskUserCustomFieldsPreference) getFactory().getCurrentSession().getNamedQuery("autotaskUserCustomFieldsPreference.byAutotaskUserAndFieldType")
				.setLong("autotask_user_id", autotaskUserId)
				.setString("integration_custom_field_code", integrationCustomFieldCode)
				.uniqueResult());


	}

	@Override
	public Optional<List<AutotaskUserCustomFieldsPreference>> findAllPreferencesByAutotaskUser(Long autotaskUserId) {

		return Optional.of((List<AutotaskUserCustomFieldsPreference>) getFactory().getCurrentSession().getNamedQuery("autotaskUserCustomFieldsPreference.byAutataskUser")
				.setLong("autotask_user_id", autotaskUserId).list());
	}
}