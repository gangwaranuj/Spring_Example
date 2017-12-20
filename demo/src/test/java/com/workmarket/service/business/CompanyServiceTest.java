package com.workmarket.service.business;

import com.google.common.collect.Sets;
import com.workmarket.auth.AuthenticationClient;
import com.workmarket.auth.gen.Messages.Status;
import com.workmarket.common.core.RequestContext;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.company.CompanySearchTrackingDAO;
import com.workmarket.dao.random.CompanyRandomIdentifierDAO;
import com.workmarket.dao.summary.company.CompanySummaryDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.company.CompanySearchTracking;
import com.workmarket.domains.payments.dao.PaymentConfigurationDAO;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.service.business.event.search.IndexerEvent;
import com.workmarket.service.infra.business.AuthTrialCommon;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.DateUtilities;
import com.workmarket.web.controllers.users.EmployeeSettingsDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;

import java.util.Calendar;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class CompanyServiceTest {

	@Mock CompanyDAO companyDao;
	@Mock AuthenticationService authenticationService;
	@Mock CompanySearchTrackingDAO companySearchTrackingDAO;
	@Mock WorkDAO workDAO;
	@Mock EventRouter eventRouter;
	@Mock PaymentConfigurationDAO paymentConfigurationDAO;
	@Mock CompanyRandomIdentifierDAO companyRandomIdentifierDAO;
	@Mock CompanySummaryDAO companySummaryDAO;
	@Mock WorkSubStatusService workSubStatusService;
	@Mock WebRequestContextProvider webRequestContextProvider;
	@Mock AuthTrialCommon trialCommon;
	@Mock AuthenticationClient authClient;
	@InjectMocks CompanyServiceImpl companyService = spy(new CompanyServiceImpl());

	Company company;
	EmployeeSettingsDTO employeeSettingsDTO;
	static final Long COMPANY_ID = 1L;
	static final String COMPANY_NAME = "Mock";
	static final String CUSTOMER_TYPE = "buyer";
	static final String COMPANY_OVERVIEW = "Dunder Mifflin offers the right products at the right price.";
	static final String COMPANY_NUM = "9999";

	@Before
	public void setup() {
		company = mock(Company.class);
		when(company.getId()).thenReturn(COMPANY_ID);
		when(companyDao.findCompanyById(anyLong())).thenReturn(company);
		when(companyDao.findCompanyByName(anyString())).thenReturn(company);
		when(companyDao.get(anyLong())).thenReturn(company);
		when(companyService.findById(COMPANY_ID)).thenReturn(company);
		when(companyService.findCompanyByName(COMPANY_NAME)).thenReturn(company);
		when(companyRandomIdentifierDAO.generateUniqueNumber()).thenReturn(COMPANY_NUM);

		employeeSettingsDTO = mock(EmployeeSettingsDTO.class);
		when(employeeSettingsDTO.isHidePricing()).thenReturn(true);
		when(authClient.createCompany(anyString(), (RequestContext) anyObject())).thenReturn(
			Observable.just(Status.newBuilder().setSuccess(true).build()));
	}

	@Test
	public void isInstantWorkerPoolEnabled_callDao() {
		companyService.isInstantWorkerPoolEnabled(anyLong());

		verify(companyDao).isInstantWorkerPoolEnabled(anyLong());
	}

	@Test
	public void processDueInvoicesForCompany_noDue_clearOverdueWarning() {
		when(companyDao.doesCompanyHaveOverdueInvoice(anyLong(), any(Calendar.class))).thenReturn(false);

		companyService.processDueInvoicesForCompany(COMPANY_ID);

		verify(company).setOverdueAccountWarningSentOn(null);
	}

	@Test
	public void processDueInvoicesForCompany_haveDueInvoices_doNothing() {
		when(companyDao.doesCompanyHaveOverdueInvoice(anyLong(), any(Calendar.class))).thenReturn(true);

		companyService.processDueInvoicesForCompany(COMPANY_ID);

		verify(company, never()).setOverdueAccountWarningSentOn(null);
	}

	@Test
	public void processDueInvoicesForLockedCompany_noDueInvoices_unlockCompany() {
		when(companyDao.doesCompanyHaveOverdueInvoice(anyLong(), any(Calendar.class))).thenReturn(false);
		when(company.isLocked()).thenReturn(true);
		when(workDAO.countAllDueWorkByCompany(anyLong())).thenReturn(0);
		when(company.unlock(anyInt())).thenReturn(true);

		companyService.processDueInvoicesForCompany(COMPANY_ID);

		verify(company).setUnlockedBy(any(User.class));
	}

	@Test
	public void saveCompanySearchTrackingSetting_success() {
		companyService.saveCompanySearchTrackingSetting(COMPANY_ID, "test@workmarket.com", Sets.newHashSet(1L, 2L));

		verify(companySearchTrackingDAO).findCompanySearchTrackingByCompanyId(eq(COMPANY_ID));
		verify(companySearchTrackingDAO).saveOrUpdate(any(CompanySearchTracking.class));
	}

	@Test
	public void hasWorkPastDueMoreThanXDays_success() {
		companyService.hasWorkPastDueMoreThanXDays(anyLong(), anyInt());

		verify(companyDao).hasWorkPastDueMoreThanXDays(anyLong(), anyInt());
	}

	@Test
	public void updateListInVendorSearch_trueToggle_andEligible_setInVendorSearchToTrue() {
		when(companyDao.hasAtLeastOneUserWithActiveRoles(company.getId(), AclRole.ACL_DISPATCHER)).thenReturn(true);
		when(companyDao.hasAtLeastOneUserWithActiveRoles(company.getId(), AclRole.ACL_WORKER, AclRole.ACL_SHARED_WORKER)).thenReturn(true);
		when(companyService.getCustomerType(COMPANY_ID)).thenReturn(CUSTOMER_TYPE);
		when(company.isInVendorSearch()).thenReturn(false);

		companyService.updateListInVendorSearch(company.getId(), Boolean.TRUE);

		verify(company).setInVendorSearch(true);
		saveCompanyAndSendIndexEvents(1);
	}

	@Test
	public void updateListInVendorSearch_falseToggle_setInVendorSearchToFalse() {
		when(companyService.getCustomerType(COMPANY_ID)).thenReturn(CUSTOMER_TYPE);
		when(company.isInVendorSearch()).thenReturn(true);

		companyService.updateListInVendorSearch(company.getId(), Boolean.FALSE);

		verify(company).setInVendorSearch(false);
		saveCompanyAndSendIndexEvents(1); // send additional vendor index delete event
	}

	@Test
	public void updateListInVendorSearch_nullToggle_setInVendorSearchToFalse() {
		when(companyService.getCustomerType(COMPANY_ID)).thenReturn(CUSTOMER_TYPE);
		when(company.isInVendorSearch()).thenReturn(true);

		companyService.updateListInVendorSearch(company.getId(), null);

		verify(company).setInVendorSearch(false);
		saveCompanyAndSendIndexEvents(1); // send additional vendor index delete event
	}

	private void saveCompanyAndSendIndexEvents(int times) {
		verify(companyService).saveOrUpdateCompany(company);
		verify(eventRouter, times(times)).sendEvent(any(IndexerEvent.class));
	}

	@Test
	public void saveEmployeeSettings_ForCompany_WithCompanyID_FindsTheCompany() throws Exception {
		companyService.saveEmployeeSettings(COMPANY_ID, employeeSettingsDTO);

		verify(companyDao).findById(COMPANY_ID);
	}

	@Test
	public void saveEmployeeSettings_ForCompany_WithDTO_SetsHidePricing() throws Exception {
		companyService.saveEmployeeSettings(COMPANY_ID, employeeSettingsDTO);

		verify(company).setHidePricing(true);
	}

	@Test
	public void findCompanyByName_ForCompany_WithCompanyName_VerifyCompanyDao() throws Exception {
		companyService.findCompanyByName(COMPANY_NAME);

		verify(companyDao).findCompanyByName(COMPANY_NAME);
	}

	@Test
	public void setCustomerType_verifySetTypeAndSaveCompany() throws Exception {
		companyService.setCustomerType(COMPANY_ID, CUSTOMER_TYPE);

		verify(companyDao).get(COMPANY_ID);
		verify(company).setCustomerType(CUSTOMER_TYPE);
		verify(companyDao).saveOrUpdate(company);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setOverview_nullOverview_exceptionThrown() {
		companyService.setCustomerType(COMPANY_ID, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setOverview_companyNotFound_exceptionThrown() {
		when(companyDao.get(anyLong())).thenReturn(null);

		companyService.setCustomerType(COMPANY_ID, COMPANY_OVERVIEW);
	}

	@Test
	public void setOverview_newOverview_greatSuccess() {
		companyService.setOverview(COMPANY_ID, COMPANY_OVERVIEW);

		verify(companyDao).get(COMPANY_ID);
		verify(company).setOverview(COMPANY_OVERVIEW);
		verify(companyDao).saveOrUpdate(company);
	}

	@Test
	public void isApplicableToRenderOnboardingProgress_False() {
		Calendar createdOn = DateUtilities.getCalendarFromISO8601("2016-09-01");
		Calendar company_onboarding_rollout_date = DateUtilities.getCalendarFromISO8601("2016-09-14");
		when(company.getCreatedOn()).thenReturn(createdOn);

		companyService.isApplicableToRenderOnboardingProgress(COMPANY_ID);

		verify(companyDao).findCompanyById(COMPANY_ID);
		assertFalse(!createdOn.before(company_onboarding_rollout_date));
	}

	@Test
	public void isApplicableToRenderOnboardingProgress_True() {
		Calendar createdOn = DateUtilities.getCalendarFromISO8601("2016-09-14");
		Calendar company_onboarding_rollout_date = DateUtilities.getCalendarFromISO8601("2016-09-14");
		when(company.getCreatedOn()).thenReturn(createdOn);

		companyService.isApplicableToRenderOnboardingProgress(COMPANY_ID);

		verify(companyDao).findCompanyById(COMPANY_ID);
		assertTrue(!createdOn.before(company_onboarding_rollout_date));
	}

	@Test
	public void test_customFieldsEnabledFlagFalseForNewCompanyByDefault_True() {
		ManageMyWorkMarket mmw = mock(ManageMyWorkMarket.class);
		companyService.createCompany("some company", false, CUSTOMER_TYPE);
		when(company.getManageMyWorkMarket()).thenReturn(mmw);
		verify(company.getManageMyWorkMarket(), never()).setCustomFieldsEnabledFlag(true);
	}
}
