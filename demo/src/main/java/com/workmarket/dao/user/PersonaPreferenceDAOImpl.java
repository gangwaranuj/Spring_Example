package com.workmarket.dao.user;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.user.PersonaPreference;
import org.springframework.stereotype.Repository;

@Repository
public class PersonaPreferenceDAOImpl extends AbstractDAO<PersonaPreference> implements PersonaPreferenceDAO {
	@Override
	protected Class<?> getEntityClass() {
		return PersonaPreference.class;
	}
}
