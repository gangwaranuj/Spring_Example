package com.workmarket.service.business.tax;

import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.TaxVerificationStatusType;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.TaxEntityDTO;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.RandomUtilities;
import com.workmarket.vault.models.Secured;
import com.workmarket.vault.services.VaultHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class TaxServiceIT extends BaseServiceIT {

	@Autowired TaxService taxService;
	@Autowired VaultHelper vaultHelper;

	@Test
	public void editTaxEntity_success() throws Exception {
		User worker = newRegisteredWorker();
		String taxId1 = RandomUtilities.generateAlphaNumericString(10);
		String taxId2 = RandomUtilities.generateAlphaNumericString(10);

		TaxEntityDTO taxEntityDTO = TaxEntityDTO.newTaxEntityDTO()
				.setTaxCountry("other")
				.setBusinessFlag(false)
				.setFirstName("BLOOP")
				.setLastName("BLARP")
				.setTaxEntityTypeCode("individual")
				.setAddress("Germany St")
				.setCity("Deutsche City")
				.setState("Deutsche State")
				.setPostalCode("32836")
				.setForeignStatusAcceptedFlag(true)
				.setActiveFlag(true)
				.setTaxNumber(taxId1);

		taxEntityDTO.setTaxName(taxEntityDTO.getFullName());

		authenticationService.setCurrentUser(worker.getId());
		AbstractTaxEntity taxEntity1 = taxService.saveTaxEntityForCompany(worker.getCompany().getId(), taxEntityDTO);
		vaultHelper.secureEntity(taxEntity1);

		taxEntityDTO.setAddress("new address");
		AbstractTaxEntity taxEntity2 = taxService.saveTaxEntityForCompany(worker.getCompany().getId(), taxEntityDTO);
		vaultHelper.secureEntity(taxEntity2);

		taxEntityDTO.setFirstName("BLOOP2");
		AbstractTaxEntity taxEntity3 = taxService.saveTaxEntityForCompany(worker.getCompany().getId(), taxEntityDTO);
		vaultHelper.secureEntity(taxEntity3);

		taxEntityDTO.setBusinessFlag(true);
		taxEntityDTO.setTaxName(taxEntityDTO.getLastName());
		AbstractTaxEntity taxEntity4 = taxService.saveTaxEntityForCompany(worker.getCompany().getId(), taxEntityDTO);
		vaultHelper.secureEntity(taxEntity4);

		taxEntityDTO.setTaxNumber(taxId2);
		AbstractTaxEntity taxEntity5 = taxService.saveTaxEntityForCompany(worker.getCompany().getId(), taxEntityDTO);
		vaultHelper.secureEntity(taxEntity5);

		taxEntityDTO.setTaxCountry(AbstractTaxEntity.COUNTRY_USA);
		AbstractTaxEntity taxEntity6 = taxService.saveTaxEntityForCompany(worker.getCompany().getId(), taxEntityDTO);
		vaultHelper.secureEntity(taxEntity6);

		Collection<AbstractTaxEntity> activeTaxEntities =
				CollectionUtils.select(taxService.findAllTaxEntities(worker.getId()), new Predicate() {
					public boolean evaluate(Object o) {
						AbstractTaxEntity te = (AbstractTaxEntity) o;
						return te.getActiveFlag();
					}
				});

		assertEquals(activeTaxEntities.size(), 1);
	}

	@Test
	public void saveTaxEntityForCompany_findActiveTaxEntitiesByTinAndCountry_success() throws Exception {
		User worker1 = newRegisteredWorker();
		User worker2 = newRegisteredWorker();
		String taxId1 = RandomUtilities.generateAlphaNumericString(10);
		String taxId2 = RandomUtilities.generateAlphaNumericString(10);

		TaxEntityDTO taxEntityDTO = TaxEntityDTO.newTaxEntityDTO()
			.setTaxCountry("other")
			.setBusinessFlag(false)
			.setFirstName("BLOOP")
			.setLastName("BLARP")
			.setTaxEntityTypeCode("individual")
			.setAddress("Germany St")
			.setCity("Deutsche City")
			.setState("Deutsche State")
			.setPostalCode("32836")
			.setForeignStatusAcceptedFlag(true)
			.setActiveFlag(true)
			.setTaxNumber(taxId1);

		authenticationService.setCurrentUser(worker1.getId());
		AbstractTaxEntity taxEntity1 = taxService.saveTaxEntityForCompany(worker1.getCompany().getId(), taxEntityDTO);
		vaultHelper.secureEntity(taxEntity1);
		List<? extends AbstractTaxEntity> taxEntities1 = taxService.findTaxEntitiesByTinAndCountry(taxEntity1.getTaxNumber(), "other");
		assertEquals(taxEntities1.size(), 1);
		assertEquals(taxEntities1.get(0), taxEntity1);

		taxEntityDTO.setTaxNumber(taxId2);
		authenticationService.setCurrentUser(worker2.getId());
		AbstractTaxEntity taxEntity2 = taxService.saveTaxEntityForCompany(worker2.getCompany().getId(), taxEntityDTO);
		vaultHelper.secureEntity(taxEntity2);
		List<? extends AbstractTaxEntity> taxEntities2 = taxService.findTaxEntitiesByTinAndCountry(taxEntity2.getTaxNumber(), "other");
		assertEquals(taxEntities2.size(), 1);
		assertEquals(taxEntities2.get(0), taxEntity2);

		taxEntity1.setActiveFlag(false);
		taxService.saveTaxEntity(taxEntity1);
		taxEntity2.setActiveFlag(false);
		taxService.saveTaxEntity(taxEntity2);

		assertEquals(taxService.findTaxEntitiesByTinAndCountry(taxEntity1.getTaxNumber(), "other").size(), 1);
		assertEquals(taxService.findTaxEntitiesByTinAndCountry(taxEntity2.getTaxNumber(), "other").size(), 1);
	}

	@Test
	public void findAllCompaniesWithMultipleApprovedTaxEntities_withNullEffectiveDate_returnsAllInRange() throws Exception {

		User worker1 = newRegisteredWorker();

		Calendar now = DateUtilities.getCalendarNow();
		Calendar oneMonthsAgo = new DateTime(now).minusMonths(1).toCalendar(Locale.ENGLISH);
		Calendar twoMonthsAgo = new DateTime(now).minusMonths(2).toCalendar(Locale.ENGLISH);
		Calendar threeMonthsAgo = new DateTime(now).minusMonths(2).toCalendar(Locale.ENGLISH);

		// Create inactive tax entity with non-null effective date
		AbstractTaxEntity inactiveTaxEntity = createTaxEntity(worker1.getCompany().getId(), twoMonthsAgo,
			twoMonthsAgo, false,
			TaxVerificationStatusType.APPROVED);

		// Create active tax entity with null effective date
		AbstractTaxEntity activeTaxEntity = createTaxEntity(worker1.getCompany().getId(), oneMonthsAgo, null, true,
			TaxVerificationStatusType.APPROVED);

		Calendar firstDayOfYear = DateUtilities.getCalendarNow();
		firstDayOfYear.set(Calendar.MONTH, Calendar.JANUARY);
		firstDayOfYear.set(Calendar.DAY_OF_MONTH, 1);

		Calendar lastDayOfYear = DateUtilities.getCalendarNow();
		lastDayOfYear.set(Calendar.MONTH, Calendar.DECEMBER);
		lastDayOfYear.set(Calendar.DAY_OF_MONTH, 31);

		Set<Long> companyIds = taxService.findAllCompaniesWithMultipleApprovedTaxEntities(new DateRange(threeMonthsAgo, now));

		// Company should be returned since both tax entities are within in the given date range
		assertTrue(companyIds.contains(worker1.getCompany().getId()));
	}

	/**
	 * This test failed before my change to TaxEntityDAOImpl#findAllCompaniesWithMultipleApprovedTaxEntities
	 * because it used effective_date in a condition and that field can be null.
	 * Now we check whichever is not null out of: effective_date, active_date
	 */
	@Test
	@Ignore
	public void findAllCompaniesWithMultipleApprovedTaxEntities_withNullEffectiveDate_excludesOutOfDateRange() throws Exception {

		User worker1 = newRegisteredWorker();

		Calendar now = DateUtilities.getCalendarNow();
		Calendar oneYearAgo = new DateTime(now).minusYears(1).toCalendar(Locale.ENGLISH);
		Calendar twoYearsAgo = new DateTime(now).minusYears(2).toCalendar(Locale.ENGLISH);

		// Create inactive tax entity with non-null effective date
		AbstractTaxEntity inactiveTaxEntity = createTaxEntity(worker1.getCompany().getId(), twoYearsAgo, twoYearsAgo, false,
				TaxVerificationStatusType.APPROVED);

		// Create active tax entity with null effective date
		AbstractTaxEntity activeTaxEntity = createTaxEntity(worker1.getCompany().getId(), oneYearAgo, null, true,
				TaxVerificationStatusType.APPROVED);

		Calendar firstDayOfYear = DateUtilities.getCalendarNow();
		firstDayOfYear.set(Calendar.MONTH, Calendar.JANUARY);
		firstDayOfYear.set(Calendar.DAY_OF_MONTH, 1);

		Calendar lastDayOfYear = DateUtilities.getCalendarNow();
		lastDayOfYear.set(Calendar.MONTH, Calendar.DECEMBER);
		lastDayOfYear.set(Calendar.DAY_OF_MONTH, 31);

		Set<Long> companyIds = taxService.findAllCompaniesWithMultipleApprovedTaxEntities(new DateRange(firstDayOfYear, lastDayOfYear));

		// Company should not be returned because the first tax entity is out of range
		assertFalse(companyIds.contains(worker1.getCompany().getId()));
	}

	/**
	 * This test failed before my change to TaxEntityDAOImpl#findAllUsaApprovedTaxEntitiesByCompanyId
	 * because it used effective_date to do sorting and this field can be null.
	 * Now we check whichever is not null out of: effective_date, active_date
	 */
	@Test
	public void findAllUsaApprovedTaxEntitiesByCompanyId_returnsInDateOrderAsc() throws Exception {

		User worker1 = newRegisteredWorker();

		Calendar now = DateUtilities.getCalendarNow();
		Calendar oneMonthAgo = new DateTime(now).minusMonths(1).toCalendar(Locale.ENGLISH);
		Calendar twoMonthsAgo = new DateTime(now).minusMonths(2).toCalendar(Locale.ENGLISH);

		// Create inactive tax entity with non-null effective date
		AbstractTaxEntity inactiveTaxEntity = createTaxEntity(worker1.getCompany().getId(), twoMonthsAgo, twoMonthsAgo, false,
				TaxVerificationStatusType.APPROVED);

		// Create active tax entity with null effective date
		AbstractTaxEntity activeTaxEntity = createTaxEntity(worker1.getCompany().getId(), oneMonthAgo, null, true,
				TaxVerificationStatusType.APPROVED);

		List<UsaTaxEntity> taxEntities = taxService.findAllUsaApprovedTaxEntitiesByCompanyId(worker1.getCompany().getId());

		assertEquals(2, taxEntities.size());
		assertTrue(taxEntities.get(0).getActiveDate().before(taxEntities.get(1).getActiveDate()));
	}

	private AbstractTaxEntity createTaxEntity(
		Long companyId,
		Calendar activeDate,
		Calendar effectiveDate,
		boolean active,
		String status) {

		TaxEntityDTO taxEntityDTO = TaxEntityDTO.newTaxEntityDTO()
				.setTaxCountry("usa")
				.setBusinessFlag(false)
				.setFirstName("BLOOP")
				.setLastName("BLARP")
				.setTaxEntityTypeCode("individual")
				.setAddress("Germany St")
				.setCity("Deutsche City")
				.setState("Deutsche State")
				.setPostalCode("32836")
				.setForeignStatusAcceptedFlag(true)
				.setTaxNumber(UUID.randomUUID().toString().substring(0, 24));

		if(activeDate != null) {
			taxEntityDTO.setActiveDateString(DateUtilities.format("yyyy-MM-dd HH:mm:ss", activeDate));
		}
		if(effectiveDate != null) {
			taxEntityDTO.setEffectiveDateString(DateUtilities.format("yyyy-MM-dd HH:mm:ss", effectiveDate));
		}
		AbstractTaxEntity taxEntity = taxService.saveTaxEntityForCompany(companyId, taxEntityDTO);

		taxEntity.setActiveFlag(active);
		taxEntity.setStatus(TaxVerificationStatusType.newInstance(status));
		taxService.saveTaxEntity(taxEntity);

		return taxEntity;
	}

	@Test
	public void saveTaxEntityForCompany_testSinglePrependOnSecondSave_success() throws Exception {
		setFeatureToggle("vaultObfuscate", Boolean.TRUE);
		setFeatureToggle("vaultObfuscatePrepend", Boolean.TRUE);

		User worker1 = newRegisteredWorker();
		String taxId1 = RandomUtilities.generateAlphaNumericString(10);

		TaxEntityDTO taxEntityDTO = TaxEntityDTO.newTaxEntityDTO()
			.setTaxCountry("other")
			.setBusinessFlag(false)
			.setFirstName("BLOOP")
			.setLastName("BLARP")
			.setTaxEntityTypeCode("individual")
			.setAddress("Germany St")
			.setCity("Deutsche City")
			.setState("Deutsche State")
			.setPostalCode("32836")
			.setForeignStatusAcceptedFlag(true)
			.setActiveFlag(true)
			.setTaxNumber(taxId1);

		authenticationService.setCurrentUser(worker1.getId());
		AbstractTaxEntity taxEntity1 = taxService.saveTaxEntityForCompany(worker1.getCompany().getId(), taxEntityDTO);
		assertEquals(Secured.PREPEND_MASKING_PATTERN + taxId1, taxEntity1.getTaxNumber());
		taxEntity1.setActiveFlag(Boolean.TRUE);
		taxService.saveTaxEntity(taxEntity1);
		taxEntity1 = taxService.findActiveTaxEntityByCompany(worker1.getCompany().getId());
		assertEquals(Secured.PREPEND_MASKING_PATTERN + taxId1, taxEntity1.getTaxNumber());
	}


	@Test
	public void shouldGetAllAccountsFromId() throws Exception {
		final AbstractTaxEntity activeTaxEntity = createActiveTaxEntity();
		final List<? extends AbstractTaxEntity> accounts = taxService.findAllAccountsFromId(activeTaxEntity.getId());
		assertTrue("Expected accounts.size() to be >= 1", accounts.size() >= 1);
		assertTrue("Expected accounts to contain activeTaxEntity", accounts.contains(activeTaxEntity));
	}

	@Test
	public void shouldGetAllAccountIdsFromId() throws Exception {
		final AbstractTaxEntity activeTaxEntity = createActiveTaxEntity();
		final List<Long> ids = taxService.getAllAccountIdsFromId(activeTaxEntity.getId());
		assertTrue("Expected ids.size() to be >= 1", ids.size() >= 1);
		assertTrue("Expected ids to contain activeTaxEntity", ids.contains(activeTaxEntity.getId()));
	}

	private AbstractTaxEntity createActiveTaxEntity() throws Exception {
		User worker1 = newRegisteredWorker();
		Calendar now = DateUtilities.getCalendarNow();
		Calendar oneMonthAgo = new DateTime(now).minusMonths(1).toCalendar(Locale.ENGLISH);
		return createTaxEntity(worker1.getCompany().getId(), oneMonthAgo, null, true, TaxVerificationStatusType.APPROVED);
	}
}
