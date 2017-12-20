package com.workmarket.dao;

import com.workmarket.domains.model.CompanyPreference;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.pricing.FlatPricePricingStrategy;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkUniqueId;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.RandomUtilities;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkUniqueIdDAOImplIT extends BaseServiceIT {

	private User employee;

	@Before
	public void initTest() throws Exception {
		employee = newEmployeeWithCashBalance();
	}

	@Test
	public void createWorkUniqueId_withUniqueId() {

		String idValue = String.format("test1%d", System.currentTimeMillis());
		String displayName = "testDisplayName";
		Integer version = 1;

		Work work = createWorkWithUniqueId(employee, idValue, displayName, version);
		WorkUniqueId workUniqueId = work.getWorkUniqueId();

		assertNotNull(workUniqueId);
		assertEquals(workUniqueId.getDisplayName(), displayName);
		assertEquals(workUniqueId.getVersion(), version);
		assertEquals(workUniqueId.getVersion(), version);

	}

	@Test
	public void createWork_withoutWorkUniqueId() {

		Work work = createWorkWithoutUniqueId(employee);
		WorkUniqueId workUniqueId = work.getWorkUniqueId();

		assertNull(workUniqueId);
	}

	@Test(expected = ConstraintViolationException.class)
	public void createWorkUniqueId_duplicateDisplayNameVersionIdValue_throwsException() {

		String displayName = "testDisplayName";
		Integer version = 1;
		String idValue = String.format("test2%d", System.currentTimeMillis());

		createWorkWithUniqueId(employee, idValue, displayName, version);

		createWorkWithUniqueId(employee, idValue, displayName, version);

	}

	@Test
	public void createWorkUniqueId_duplicateDisplayNameValue_noException() {

		String displayName = "testDisplayName";
		String idValue = String.format("test3%d", System.currentTimeMillis());

		createWorkWithUniqueId(employee, idValue, displayName, 1);

		createWorkWithUniqueId(employee, idValue, displayName, 2);

	}

	@Test
	public void createWorkUniqueId_duplicateVersionValue_noException() {

		Integer version = 1;
		String idValue = String.format("test4%d", System.currentTimeMillis());

		createWorkWithUniqueId(employee, idValue, "testDisplayName1", 1);

		createWorkWithUniqueId(employee, idValue, "testDisplayName2", 2);

	}

	@Test
	public void createWorkUniqueId_duplicateCompanyVersionDifferentValue_noException() {

		String displayName = "testDisplayName";
		Integer version = 1;

		createWorkWithUniqueId(employee, String.format("test4%d", System.currentTimeMillis()), displayName, version);

		createWorkWithUniqueId(employee, String.format("test5%d", System.currentTimeMillis()), displayName, version);

	}

	private Work createWorkWithUniqueId(User user, String idValue, String displayName, int version) {

		CompanyPreference companyPreference = user.getCompany().getCompanyPreference();
		companyPreference.setExternalIdActive(true);
		companyPreference.setExternalIdDisplayName(displayName);
		companyPreference.setExternalIdVersion(version);
		companyService.updateCompanyPreference(companyPreference);

		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(DEFAULT_WORK_FLAT_PRICE);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString(DateUtilities.getISO8601(Calendar.getInstance()));
		workDTO.setIndustryId(INDUSTRY_ID);
		workDTO.setBuyerSupportUserId(user.getId());
		workDTO.setUniqueExternalId(idValue);

		return workFacadeService.saveOrUpdateWork(user.getId(), workDTO);

	}

	private Work createWorkWithoutUniqueId(User user) {

		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(DEFAULT_WORK_FLAT_PRICE);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString(DateUtilities.getISO8601(Calendar.getInstance()));
		workDTO.setIndustryId(INDUSTRY_ID);
		workDTO.setBuyerSupportUserId(user.getId());

		return workFacadeService.saveOrUpdateWork(user.getId(), workDTO);

	}
}
