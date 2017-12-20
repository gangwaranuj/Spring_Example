package com.workmarket.service.business;

import com.workmarket.dao.user.PersonaPreferenceDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class RegistrationServiceImplIT extends BaseServiceIT {

	@Autowired private PersonaPreferenceDAO personaPreferenceDao;

	@Test
	@Transactional
	public void newCompanyUser_HasAPersonaPreference() throws Exception {
		Company company = newCompany();
		User employee = newCompanyEmployee(company.getId());

		PersonaPreference pref = personaPreferenceDao.get(employee.getId());

		assertThat(pref, is(notNullValue()));
	}
}
