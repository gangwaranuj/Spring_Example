package com.workmarket.dao.company;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.CompanyIdentityDTO;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class CompanyDAOImplIT extends BaseServiceIT {

	@Autowired CompanyDAO companyDAO;

	@Test
	@Transactional
	public void company_companyPreferences_hasPreferencesOnCreate() {
		Company company = newCompany();
		assertNotNull(company.getCompanyPreference().getId());
	}

	@Test
	@Transactional
	public void getMaxCompanyId_newCompany_hasValueGreaterThanZero() {
		newCompany();

		Integer maxId = companyDAO.getMaxCompanyId();

		assertNotNull(maxId);
		assertTrue(maxId > 0);
	}

	@Test
	@Transactional
	public void createCompany_uuidIsSet() {
		Company company = newCompany();
		assertNotNull(company.getUuid());
	}

	@Test
	@Transactional
	public void getCompanyByUUID() {
		Company company = newCompany();
		assertNotNull(company.getUuid());
		String uuid = company.getUuid();

		Company fetchCompany = companyDAO.findBy("uuid", uuid);

		assertNotNull(fetchCompany);
		assertEquals(fetchCompany.getId(), company.getId());
	}

	@Test
	@Transactional
	public void getCompanyByName_duplicateCompany_retNull() {
		Company company1 = companyService.createCompany("Test", true, "unknown");
		assertNotNull(company1.getName());

		Company company2 = companyService.createCompany("Test", true, "unknown");
		assertNotNull(company2.getName());

		Company fetchCompany = companyService.findCompanyByName("Test");

		assertNull(fetchCompany);
	}

	@Test
	public void getCompanyWorkers_companyWithoutWorkers_retZero() {
		Company company = companyService.createCompany("Test", false, "unknown");
		List<String> workerNumbers = companyDAO.findWorkerNumbers(company.getCompanyNumber());

		assertTrue(workerNumbers.size() == 0);
	}

	@Test
	public void getCompanyWorkers_companyWithInternalWorkers_retZero() throws Exception {
		Company company = companyService.createCompany("Test", false, "unknown");
		User user = newCompanyEmployeeWorkerConfirmed(company.getId());
		List<String> workerNumbers = companyDAO.findWorkerNumbers(company.getCompanyNumber());

		assertTrue(workerNumbers.size() == 0);
	}

	@Test
	public void getCompanyWorkers_companyWithSharedWorkers_retWorkNumber() throws Exception {
		Company company = companyService.createCompany("Test", false, "unknown");
		User sharedUser = newCompanyEmployeeSharedWorkerApproved(company.getId());
		List<String> workerNumbers = companyDAO.findWorkerNumbers(company.getCompanyNumber());

		assertTrue(workerNumbers.size() == 1);
		assertTrue(workerNumbers.contains(sharedUser.getUserNumber()));
	}

	@Test
	public void getCompanyIdentitiesByUuids_returnsExistingCompanies() {
		Company company = newCompany();
		List<String> uuids = Lists.newArrayList("non-existing-uuid", company.getUuid());
		List<CompanyIdentityDTO> companyIdentities = companyDAO.findCompanyIdentitiesByUuids(uuids);
		assertEquals(1, companyIdentities.size());
		assertEquals(company.getCompanyNumber(), companyIdentities.get(0).getCompanyNumber());
		assertEquals(company.getUuid(), companyIdentities.get(0).getUuid());
		assertEquals(company.getId(), companyIdentities.get(0).getCompanyId());
	}
}
