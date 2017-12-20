package com.workmarket.domains.model;

import com.workmarket.domains.model.company.CompanyStatusType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Created with IntelliJ IDEA.
 * User: ianha
 * Date: 11/16/13
 * Time: 5:47 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class CompanyTest {
	Company company;

	CompanyStatusType activeCompanyStatusType;
	CompanyStatusType lockedCompanyStatusType;

	@Before
	public void setup() {
		company = new Company();

		activeCompanyStatusType = mock(CompanyStatusType.class);
		when(activeCompanyStatusType.getCode()).thenReturn(CompanyStatusType.ACTIVE);

		lockedCompanyStatusType = mock(CompanyStatusType.class);
		when(lockedCompanyStatusType.getCode()).thenReturn(CompanyStatusType.LOCKED);
	}

	private void setupUnlockedCompany() {
		company.setCompanyStatusType(activeCompanyStatusType);
	}

	private void setupLockedCompany() {
		company.setCompanyStatusType(lockedCompanyStatusType);
	}

	@Test
	public void defaultGracePeriodHours() {
		assertEquals(Company.UNLOCK_HOURS_NO_LIMIT, 876581);
	}

	@Test
	public void defaultIsActive() {
		setupUnlockedCompany();

		assertTrue(company.isActive());
	}

	@Test
	public void defaultNotIsLocked() {
		setupUnlockedCompany();

		assertFalse(company.isLocked());
	}

	@Test
	public void defaultNotIsVipFlag() {
		setupUnlockedCompany();

		assertFalse(company.isVipFlag());
	}

	@Test
	public void lock_unlockedCompany_isLocked() {
		setupUnlockedCompany();

		company.lock();
		assertTrue(company.isLocked());
	}

	@Test
	public void unlock_lockedCompany_isUnlocked() {
		setupLockedCompany();

		company.unlock();
		assertFalse(company.isLocked());
	}

	@Test
	public void markAsVip_lockedCompany_isUnlocked() {
		setupLockedCompany();

		company.lock();
		company.markAsVip();
		assertFalse(company.isLocked());
	}

	@Test
	public void lock_unlockedVipCompany_isUnlocked() {
		setupUnlockedCompany();

		company.markAsVip();
		company.lock();
		assertFalse(company.isLocked());
	}

	@Test
	public void company_companyPreference_hasPreferencesByDefault() {
		assertNotNull(company.getCompanyPreference());
	}
}
