package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.CompanyPreference;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.service.business.dto.ManageMyWorkMarketDTO;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.RandomUtilities;
import com.workmarket.web.controllers.users.EmployeeSettingsDTO;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class CompanyServiceIT extends BaseServiceIT {

	@Autowired private CompanyService companyService;
	@Autowired private CompanyAlertService companyAlertService;
	@Autowired private ProfileService profileService;

	@Test
	public void createCompany_newCompany_companyNumberExists() {
		Company company =
			companyService.createCompany(
				"companyName" + RandomUtilities.generateNumericString(10),
				true,
				Company.TEST_CUSTOMER_TYPE
			);
		assertNotNull(company.getCompanyNumber());
	}

	@Test
	public void findCompanyById_existingCompany_settingsAreFetched() throws Exception {
		Company company = companyService.findCompanyById(COMPANY_ID);

		assertNotNull(company.getCompanyStatusType().getDescription());
		assertNotNull(company.getManageMyWorkMarket());
	}

	@Test
	public void lockCompanyAccount_ExistingCompany_WithNoOutstandingAssignments_CompanyLocked() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(ANONYMOUS_USER_ID);

		companyService.lockCompanyAccount(employee.getCompany().getId());

		Company company = companyService.findCompanyById(employee.getCompany().getId());
		assertTrue(company.isLocked());
		assertFalse(company.isActive());
	}

	@Test
	public void unlockCompanyAccount_LockedCompany_CompanyUnlocked() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(ANONYMOUS_USER_ID);
		companyService.lockCompanyAccount(employee.getCompany().getId());

		companyService.unlockCompanyAccount(employee.getCompany().getId());

		Company company = companyService.findCompanyById(employee.getCompany().getId());
		assertFalse(company.isLocked());
		assertTrue(company.isActive());
		assertEquals(userService.findUserById(ANONYMOUS_USER_ID), company.getUnlockedBy());
		assertNotNull(company.getLockedOn());
		assertNotNull(company.getUnlockedOn());
	}

	@Test
	public void hasPaymentTermsEnabled_withEnabled_isTrue() {
		ManageMyWorkMarket mmw = companyService.getManageMyWorkMarket(COMPANY_ID);
		mmw.setPaymentTermsEnabled(true);

		profileService.updateManageMyWorkMarket(COMPANY_ID, BeanUtilities.newBean(ManageMyWorkMarketDTO.class, mmw));

		Company company = companyService.findCompanyById(COMPANY_ID);
		assertTrue(company.isPaymentTermsEnabled());
	}

	@Test
	public void clearAlreadySentLowBalanceAlertTodayFlag_companyResetFlag() {
		companyAlertService.setLowBalanceAlertSentToday(COMPANY_ID);
		assertTrue(companyAlertService.isLowBalanceAlertSentToday(COMPANY_ID));

		companyService.resetLowBalanceAlertSentToday(COMPANY_ID);
		assertFalse(companyAlertService.isLowBalanceAlertSentToday(COMPANY_ID));
	}

	@Test
	public void hasAtLeastOneWorker_companyHasNoUsers_returnFalse() {
		Company company =
			companyService.createCompany(
				"companyName" + RandomUtilities.generateNumericString(10),
				true,
				Company.TEST_CUSTOMER_TYPE
			);

		assertFalse(companyService.hasAtLeastOneActiveWorker(company.getId()));
	}


	@Test
	public void hasAtLeastOneWorker_companyHasNoWorkers_returnFalse() throws Exception {
		User worker = newFirstEmployee();

		assertFalse(companyService.hasAtLeastOneActiveWorker(worker.getCompany().getId()));
	}

	@Test
	public void hasAtLeastOneWorker_companyHasAWorker_returnTrue() throws Exception {
		User worker = newContractor();
		authenticationService.updateUserAclRoles(worker.getId(), Lists.newArrayList(AclRole.ACL_WORKER));

		assertTrue(companyService.hasAtLeastOneActiveWorker(worker.getCompany().getId()));
	}

	@Test
	public void hasAtLeastOneWorker_companyHasASharedWorker_returnTrue() throws Exception {
		User worker = newContractor();
		authenticationService.updateUserAclRoles(worker.getId(), Lists.newArrayList(AclRole.ACL_SHARED_WORKER));

		assertTrue(companyService.hasAtLeastOneActiveWorker(worker.getCompany().getId()));
	}

	@Test
	public void hasAtLeastOneDispatcher_companyHasNoUsers_returnFalse() {
		Company company =
			companyService.createCompany(
				"companyName" + RandomUtilities.generateNumericString(10),
				true,
				Company.TEST_CUSTOMER_TYPE
			);

		assertFalse(companyService.hasAtLeastOneActiveDispatcher(company.getId()));
	}

	@Test
	public void hasAtLeastOneDispatcher_companyHasNoDispatchers_returnFalse() throws Exception {
		User worker = newContractor();
		authenticationService.updateUserAclRoles(worker.getId(), Lists.newArrayList(AclRole.ACL_MANAGER));

		assertFalse(companyService.hasAtLeastOneActiveDispatcher(worker.getCompany().getId()));
	}

	@Test
	public void hasAtLeastOneDispatcher_companyHasADispatcher_returnTrue() throws Exception {
		User worker = newContractor();
		authenticationService.updateUserAclRoles(worker.getId(), Lists.newArrayList(AclRole.ACL_DISPATCHER));

		assertTrue(companyService.hasAtLeastOneActiveDispatcher(worker.getCompany().getId()));
	}

	@Test
	public void saveEmployeeSettings() throws Exception {
		Company company = newCompany();
		EmployeeSettingsDTO dto = EmployeeSettingsDTO.newInstance(company)
			.setHidePricing(true);
		companyService.saveEmployeeSettings(company.getId(), dto);

		boolean hidePricing = companyService.findById(company.getId()).isHidePricing();
		assertThat(hidePricing, is(true));
	}

	@Test
	public void findCompanyIdsForUsers_noRecordForUser_returnEmptyList() {
		assertEquals(0, companyService.findCompanyIdsForUsers(Lists.newArrayList(0L)).size());
	}

	@Test
	public void findCompanyIdsForUsers_emptyList_returnEmptyList() {
		assertEquals(0, companyService.findCompanyIdsForUsers(Lists.<Long>newArrayList()).size());
	}

	@Test
	public void createCompanyUniqueId_withUniqueId() throws Exception {

		User employee = newEmployeeWithCashBalance();

		assertFalse(employee.getCompany().getCompanyPreference().isExternalIdActive());

		String displayName = "testDisplayName";

		CompanyPreference companyPreference = employee.getCompany().getCompanyPreference();
		companyPreference.setExternalIdActive(true);
		companyPreference.setExternalIdDisplayName(displayName);
		companyPreference.setExternalIdVersion(1);
		companyService.updateCompanyPreference(companyPreference);

		assertEquals(displayName, employee.getCompany().getCompanyPreference().getExternalIdDisplayName());
		assertTrue(employee.getCompany().getCompanyPreference().isExternalIdActive());
	}

}
