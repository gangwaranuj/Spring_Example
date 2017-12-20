package com.workmarket.domains.payments.service;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.search.response.user.PeopleSearchResult;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.service.business.wrapper.WorkRoutingResponseSummary;
import com.workmarket.service.option.OptionsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@RunWith(MockitoJUnitRunner.class)
public class AccountRegisterAuthorizationServiceTest {

	private static final double OVERRIDE_PRICE = 1d;
	private static final Long WORK_ID = 2L;

	private WorkResource workResource;
	private Work work;
	private WorkBundle workBundle;
	private User user;
	private PricingStrategy pricingStrategy;
	private FullPricingStrategy fullPricingStrategy;

	@Mock AccountRegisterServicePaymentTermsImpl accountRegisterServicePaymentTermsImpl;
	@Mock AccountRegisterServicePrefundImpl accountRegisterServicePrefundImpl;
	@Mock WorkService workService;
	@Mock OptionsService<AbstractWork> workOptionsService;
	@Mock WorkResourceService workResourceService;
	@InjectMocks AccountRegisterAuthorizationServiceImpl accountRegisterAuthorizationService = spy(new AccountRegisterAuthorizationServiceImpl());

	@Before
	public void setUp() throws Exception {
		workResource = mock(WorkResource.class);
		work = mock(Work.class);
		workBundle = mock(WorkBundle.class);
		user = mock(User.class);
		pricingStrategy = mock(PricingStrategy.class);
		fullPricingStrategy = mock(FullPricingStrategy.class);

		when(workResource.getWork()).thenReturn(work);
		when(workOptionsService.hasOption(any(Work.class), anyString(), anyString())).thenReturn(false);
		when(workService.findWork(anyLong())).thenReturn(work);
		when(workResourceService.findActiveWorkResource(WORK_ID)).thenReturn(workResource);
		when(work.getPricingStrategy()).thenReturn(pricingStrategy);
		when(pricingStrategy.getFullPricingStrategy()).thenReturn(fullPricingStrategy);
	}

	@Test
	public void offlinePaymentAssignmentWithTermsAccepted_verifyNoAccountRegisterActivity() throws Exception {
		when(workOptionsService.hasOption(any(Work.class), anyString(), anyString())).thenReturn(true);
		when(work.hasPaymentTerms()).thenReturn(true);
		accountRegisterAuthorizationService.acceptWork(workResource);
		verify(accountRegisterServicePaymentTermsImpl, never()).acceptWork(workResource);
	}

	@Test
	public void offlinePaymentAssignmentPrefundedAccepted_verifyNoAccountRegisterActivity() throws Exception {
		when(workOptionsService.hasOption(any(Work.class), anyString(), anyString())).thenReturn(true);
		when(work.hasPaymentTerms()).thenReturn(false);
		accountRegisterAuthorizationService.acceptWork(workResource);
		verify(accountRegisterServicePrefundImpl, never()).acceptWork(workResource);
	}

	@Test
	public void onlinePaymentAssignmentWithTermsAccepted_verifyAccountRegisterActivity() throws Exception {
		when(workOptionsService.hasOption(any(Work.class), anyString(), anyString())).thenReturn(false);
		when(work.hasPaymentTerms()).thenReturn(true);
		accountRegisterAuthorizationService.acceptWork(workResource);
		verify(accountRegisterServicePaymentTermsImpl).acceptWork(workResource);
	}

	@Test
	public void onlinePaymentAssignmentPrefundedAccepted_verifyAccountRegisterActivity() throws Exception {
		when(workOptionsService.hasOption(any(Work.class), anyString(), anyString())).thenReturn(false);
		when(work.hasPaymentTerms()).thenReturn(false);
		accountRegisterAuthorizationService.acceptWork(workResource);
		verify(accountRegisterServicePrefundImpl).acceptWork(workResource);
	}

	@Test
	public void offlinePaymentAssignmentWithTermsAuthorize_verifyNoAccountRegisterActivity() throws Exception {
		when(workOptionsService.hasOption(any(Work.class), anyString(), anyString())).thenReturn(true);
		when(work.hasPaymentTerms()).thenReturn(true);
		accountRegisterAuthorizationService.authorizeWork(work);
		verify(accountRegisterServicePaymentTermsImpl, never()).authorizeWork(work);
	}

	@Test
	public void offlinePaymentAssignmentPrefundedAuthorize_verifyNoAccountRegisterActivity() throws Exception {
		when(workOptionsService.hasOption(any(Work.class), anyString(), anyString())).thenReturn(true);
		when(work.hasPaymentTerms()).thenReturn(false);
		accountRegisterAuthorizationService.authorizeWork(work);
		verify(accountRegisterServicePrefundImpl, never()).authorizeWork(work);
	}

	@Test
	public void onlinePaymentAssignmentWithTermsAuthorize_verifyAccountRegisterActivity() throws Exception {
		when(workOptionsService.hasOption(any(Work.class), anyString(), anyString())).thenReturn(false);
		when(work.hasPaymentTerms()).thenReturn(true);
		accountRegisterAuthorizationService.authorizeWork(work);
		verify(accountRegisterServicePaymentTermsImpl).authorizeWork(work);
	}

	@Test
	public void onlinePaymentAssignmentPrefundedAuthorize_verifyAccountRegisterActivity() throws Exception {
		when(workOptionsService.hasOption(any(Work.class), anyString(), anyString())).thenReturn(false);
		when(work.hasPaymentTerms()).thenReturn(false);
		accountRegisterAuthorizationService.authorizeWork(work);
		verify(accountRegisterServicePrefundImpl).authorizeWork(work);
	}

	@Test
	public void offlinePaymentAssignmentWithTermsReprice_verifyNoAccountRegisterActivity() throws Exception {
		when(workOptionsService.hasOption(any(Work.class), anyString(), anyString())).thenReturn(true);
		when(work.hasPaymentTerms()).thenReturn(true);
		accountRegisterAuthorizationService.repriceWork(work);
		verify(accountRegisterServicePaymentTermsImpl, never()).repriceWork(work);
	}

	@Test
	public void offlinePaymentAssignmentPrefundedReprice_verifyNoAccountRegisterActivity() throws Exception {
		when(workOptionsService.hasOption(any(Work.class), anyString(), anyString())).thenReturn(true);
		when(work.hasPaymentTerms()).thenReturn(false);
		accountRegisterAuthorizationService.repriceWork(work);
		verify(accountRegisterServicePrefundImpl, never()).repriceWork(work);
	}

	@Test
	public void onlinePaymentAssignmentWithTermsReprice_verifyAccountRegisterActivity() throws Exception {
		when(workOptionsService.hasOption(any(Work.class), anyString(), anyString())).thenReturn(false);
		when(work.hasPaymentTerms()).thenReturn(true);
		accountRegisterAuthorizationService.repriceWork(work);
		verify(accountRegisterServicePaymentTermsImpl).repriceWork(work);
	}

	@Test
	public void onlinePaymentAssignmentPrefundedReprice_verifyAccountRegisterActivity() throws Exception {
		when(workOptionsService.hasOption(any(Work.class), anyString(), anyString())).thenReturn(false);
		when(work.hasPaymentTerms()).thenReturn(false);
		accountRegisterAuthorizationService.repriceWork(work);
		verify(accountRegisterServicePrefundImpl).repriceWork(work);
	}

	@Test
	public void authorizeWork_success() {
		accountRegisterAuthorizationService.authorizeWork(1L);
		verify(workService).findWork(eq(1L));
	}

	@Test
	public void authorizeWorkBundle_withPaymentTerms() {
		when(workBundle.hasPaymentTerms()).thenReturn(true);
		accountRegisterAuthorizationService.verifyFundsForAuthorization(user, workBundle, BigDecimal.TEN);
		Project project = null;
		verify(accountRegisterServicePaymentTermsImpl).verifyFundsForAuthorization(eq(BigDecimal.TEN), eq(user), eq(project));
	}

	@Test
	public void authorizeWorkBundle_withoutPaymentTerms() {
		when(workBundle.hasPaymentTerms()).thenReturn(false);
		accountRegisterAuthorizationService.verifyFundsForAuthorization(user, workBundle, BigDecimal.TEN);
		Project project = null;
		verify(accountRegisterServicePrefundImpl).verifyFundsForAuthorization(eq(BigDecimal.TEN), eq(user), eq(project));
	}

	@Test
	public void authorizeWorkBundle_withoutPaymentTermsThrowsException() {
		when(workBundle.hasPaymentTerms()).thenReturn(false);
		when(accountRegisterServicePrefundImpl.verifyFundsForAuthorization(any(BigDecimal.class), any(User.class), any(Project.class))).thenThrow(Exception.class);
		WorkAuthorizationResponse authorizationResponse = accountRegisterAuthorizationService.verifyFundsForAuthorization(user, workBundle, BigDecimal.TEN);
		Project project = null;
		verify(accountRegisterServicePrefundImpl).verifyFundsForAuthorization(eq(BigDecimal.TEN), eq(user), eq(project));
		assertTrue(authorizationResponse.fail());
	}

	@Test
	public void authorizeContractors_withEmptyList() {
		Set<PeopleSearchResult> peopleSearchResultList = Sets.newHashSet();
		assertTrue(accountRegisterAuthorizationService.authorizeContractors(peopleSearchResultList, work, new WorkRoutingResponseSummary()).success());
	}

	@Test
	public void authorizeContractors_success() {
		WorkRoutingResponseSummary workRoutingResponseSummary = new WorkRoutingResponseSummary();
		Set<PeopleSearchResult> peopleSearchResultList = Sets.newHashSet(new PeopleSearchResult());
		when(accountRegisterServicePrefundImpl.authorizeWork(eq(work))).thenReturn(WorkAuthorizationResponse.SUCCEEDED);
		assertTrue(accountRegisterAuthorizationService.authorizeContractors(peopleSearchResultList, work, workRoutingResponseSummary).success());
		assertTrue(workRoutingResponseSummary.getResponse().isEmpty());
	}

	@Test
	public void authorizeContractors_withMaxResourcesExceeded_fails() {
		WorkRoutingResponseSummary workRoutingResponseSummary = new WorkRoutingResponseSummary();
		Set<PeopleSearchResult> peopleSearchResultList = Sets.newHashSet(new PeopleSearchResult());
		when(accountRegisterServicePrefundImpl.authorizeWork(eq(work))).thenReturn(WorkAuthorizationResponse.MAX_RESOURCES_EXCEEDED);
		assertTrue(accountRegisterAuthorizationService.authorizeContractors(peopleSearchResultList, work, workRoutingResponseSummary).fail());
		assertFalse(workRoutingResponseSummary.getResponse().isEmpty());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void authorizeOnCompleteWork_nullWorkId_throwException() {
		accountRegisterAuthorizationService.authorizeOnCompleteWork(null, OVERRIDE_PRICE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void authorizeOnCompleteWork_activeWorkResourceDoesNotExist_throwException() {
		when(workResourceService.findActiveWorkResource(WORK_ID)).thenReturn(null);

		accountRegisterAuthorizationService.authorizeOnCompleteWork(WORK_ID, OVERRIDE_PRICE);
	}

	@Test
	public void authorizeOnCompleteWork_activeWorkResourceExists_doSetOverridePrice() {
		accountRegisterAuthorizationService.authorizeOnCompleteWork(WORK_ID, OVERRIDE_PRICE);

		verify(fullPricingStrategy).setOverridePrice(BigDecimal.valueOf(OVERRIDE_PRICE));
	}

	@Test(expected = IllegalArgumentException.class)
	public void acceptWork_nullWorkId_throwException() {
		accountRegisterAuthorizationService.acceptWork((Long)null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void acceptWork_activeWorkResourceDoesNotExist_throwException() {
		when(workResourceService.findActiveWorkResource(WORK_ID)).thenReturn(null);

		accountRegisterAuthorizationService.acceptWork(WORK_ID);
	}

	@Test
	public void acceptWork_activeWorkResourceExists_doAcceptWork() {
		accountRegisterAuthorizationService.acceptWork(WORK_ID);

		verify(accountRegisterAuthorizationService).acceptWork(workResource);
	}

}
