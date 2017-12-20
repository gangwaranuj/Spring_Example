package com.workmarket.web.validators;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.User;

import com.workmarket.service.business.PricingService;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.thrift.work.ManageMyWorkMarket;
import com.workmarket.thrift.work.PricingStrategy;
import com.workmarket.thrift.work.Project;
import com.workmarket.thrift.work.Work;
import com.workmarket.domains.model.pricing.PricingStrategyUtilities;
import com.workmarket.web.converters.WorkFormToThriftWorkConverter;
import com.workmarket.web.forms.work.WorkForm;
import com.workmarket.web.forms.work.WorkFormRouting;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySet;
import static org.mockito.Mockito.*;

/**
 * Created by rahul on 1/8/14
 */
@RunWith(MockitoJUnitRunner.class)
public class BulkWorkRouteValidatorTest {

	@Mock protected WorkFormToThriftWorkConverter toWorkConverter;
	@Mock protected AccountRegisterService accountRegisterServicePrefund;
	@Mock protected AccountRegisterService accountRegisterServicePaymentTerms;
	@Mock protected PricingService pricingService;
	@Mock protected PricingStrategyUtilities pricingStrategyUtilities;
	@Mock protected MessageBundleHelper messageHelper;

	private MessageBundle messageBundle;

	@InjectMocks BulkWorkRouteValidator bulkWorkRouteValidator;

	private PricingStrategy thriftPricingStrategy;
	private com.workmarket.domains.model.pricing.PricingStrategy pricingStrategy;
	private Project project;
	private WorkForm workForm;
	private Work work;
	private ManageMyWorkMarket manageMyWorkMarket;
	private WorkFormRouting workFormRouting;
	private Set<String> routingResourcesNone = Sets.newHashSet();
	private Set<String> routingResourcesOne = Sets.newHashSet("Mr.Spacey");
	private User userBuyer;

	private int SINGLE_COPY = 1;
	private int MULTIPLE_COPIES = 2;
	private int COPY_LIMIT = 10;

	String ROUTING_VALIDATION_ERROR = "work.form.error";

	WorkAuthorizationResponse authorizationResponse;

	@Before
	public void init() {

		thriftPricingStrategy = mock(PricingStrategy.class);
		pricingStrategy = mock(com.workmarket.domains.model.pricing.PricingStrategy.class);
		project = mock(Project.class);
		work = mock(Work.class);
		workForm = mock(WorkForm.class);
		workFormRouting = mock(WorkFormRouting.class);
		manageMyWorkMarket = mock(ManageMyWorkMarket.class);
		messageBundle = mock(MessageBundle.class);
		userBuyer = mock(User.class);
		authorizationResponse = WorkAuthorizationResponse.SUCCEEDED;

		when(project.getId()).thenReturn(1L);

		when(manageMyWorkMarket.isSetPaymentTermsDays()).thenReturn(true);

		when(work.isSetProject()).thenReturn(false);
		when(work.getProject()).thenReturn(project);
		when(work.getConfiguration()).thenReturn(manageMyWorkMarket);

		when(workForm.getRouting()).thenReturn(workFormRouting);

		when(userBuyer.getId()).thenReturn(2L);

		when(pricingService.calculateMaximumResourceCost(any(com.workmarket.domains.model.pricing.PricingStrategy.class))).thenReturn(BigDecimal.valueOf(100));

		when(toWorkConverter.convert(any(WorkForm.class))).thenReturn(work);

		when(accountRegisterServicePaymentTerms.authorizeMultipleWork(any(BigDecimal.class), any(User.class), any(com.workmarket.domains.work.model.project.Project.class), anySet())).thenReturn(authorizationResponse);

		when(accountRegisterServicePrefund.authorizeMultipleWork(any(BigDecimal.class), any(User.class), any(com.workmarket.domains.work.model.project.Project.class), anySet())).thenReturn(authorizationResponse);
	}

	@Test
	public void validateMultipleWorkCopy_NullWorkForm_NoErrors() {
		when(workFormRouting.getResourceNumbers()).thenReturn(routingResourcesOne);

		bulkWorkRouteValidator.validateMultipleWorkCopy(null, SINGLE_COPY, COPY_LIMIT, userBuyer, messageBundle);

		verify(messageHelper, never()).addError(messageBundle, ROUTING_VALIDATION_ERROR);
	}

	@Test
	public void validateMultipleWorkCopy_RoutingNull_NoErrors() {
		when(workForm.getRouting()).thenReturn(null);

		bulkWorkRouteValidator.validateMultipleWorkCopy(workForm, SINGLE_COPY, COPY_LIMIT, userBuyer, messageBundle);

		verify(messageHelper, never()).addError(messageBundle, ROUTING_VALIDATION_ERROR);
	}

	@Test
	public void validateMultipleWorkCopy_WorkFormRoutingNull_NoErrors() {
		when(workForm.getRouting()).thenReturn(null);

		bulkWorkRouteValidator.validateMultipleWorkCopy(workForm, SINGLE_COPY, COPY_LIMIT, userBuyer, messageBundle);

		verify(messageHelper, never()).addError(messageBundle, ROUTING_VALIDATION_ERROR);
	}

	@Test
	public void validateMultipleWorkCopy_OneAssignmentResourceRouted_NoErrors() {
		when(workFormRouting.getResourceNumbers()).thenReturn(routingResourcesOne);

		bulkWorkRouteValidator.validateMultipleWorkCopy(workForm, SINGLE_COPY, COPY_LIMIT, userBuyer, messageBundle);

		verify(messageHelper, never()).addError(messageBundle, ROUTING_VALIDATION_ERROR);
	}

	@Test
	public void validateMultipleWorkCopy_OneAssignmentNoResourceRouted_NoErrors() {
		when(workFormRouting.getResourceNumbers()).thenReturn(routingResourcesOne);

		bulkWorkRouteValidator.validateMultipleWorkCopy(workForm, SINGLE_COPY, COPY_LIMIT, userBuyer, messageBundle);

		verify(messageHelper, never()).addError(messageBundle, ROUTING_VALIDATION_ERROR);
	}

	@Test
	public void validateMultipleWorkCopy_MultipleAssignmentsNoResourcesRouted_NoErrors() {
		when(workFormRouting.getResourceNumbers()).thenReturn(routingResourcesNone);

		bulkWorkRouteValidator.validateMultipleWorkCopy(workForm, MULTIPLE_COPIES, COPY_LIMIT, userBuyer, messageBundle);

		verify(messageHelper, never()).addError(messageBundle, ROUTING_VALIDATION_ERROR);
	}

	@Test
	public void validateMultipleWorkCopy_MultipleAssignmentsResourceRouted_Errors() {
		when(workFormRouting.getResourceNumbers()).thenReturn(routingResourcesOne);

		bulkWorkRouteValidator.validateMultipleWorkCopy(workForm, MULTIPLE_COPIES, COPY_LIMIT, userBuyer, messageBundle);

		verify(messageHelper, atMost(1)).addError(messageBundle, ROUTING_VALIDATION_ERROR);
	}

	@Test
	public void validateMultipleWorkCopy_SingleAssignment_InsufficientFundsTerms_Error() {
		when(accountRegisterServicePaymentTerms.authorizeMultipleWork(
				any(BigDecimal.class), any(User.class), any(com.workmarket.domains.work.model.project.Project.class), anySet())).thenReturn(WorkAuthorizationResponse.INSUFFICIENT_FUNDS);

		bulkWorkRouteValidator.validateMultipleWorkCopy(workForm, MULTIPLE_COPIES, COPY_LIMIT, userBuyer, messageBundle);

		verify(messageHelper, times(1)).addError(messageBundle, "search.cart.push.assignment.insufficient_funds_terms");
	}

	@Test
	public void validateMultipleWorkCopy_SingleAssignment_InsufficientFundsPrefund_Error() {
		when(accountRegisterServicePrefund.authorizeMultipleWork(
				any(BigDecimal.class), any(User.class), any(com.workmarket.domains.work.model.project.Project.class), anySet())).thenReturn(WorkAuthorizationResponse.INSUFFICIENT_FUNDS);
		when(manageMyWorkMarket.isSetPaymentTermsDays()).thenReturn(false);

		bulkWorkRouteValidator.validateMultipleWorkCopy(workForm, MULTIPLE_COPIES, COPY_LIMIT, userBuyer, messageBundle);

		verify(messageHelper, times(1)).addError(messageBundle, "search.cart.push.assignment.insufficient_funds_prefund");
	}

	@Test
	public void validateMultipleWorkCopy_SingleAssignment_InsufficientBudget_Error() {
		when(accountRegisterServicePaymentTerms.authorizeMultipleWork(
				any(BigDecimal.class), any(User.class), any(com.workmarket.domains.work.model.project.Project.class), anySet())).thenReturn(WorkAuthorizationResponse.INSUFFICIENT_BUDGET);

		bulkWorkRouteValidator.validateMultipleWorkCopy(workForm, MULTIPLE_COPIES, COPY_LIMIT, userBuyer, messageBundle);

		verify(messageHelper, times(1)).addError(messageBundle, "search.cart.push.assignment.insufficient_budget");
	}

	@Test
	public void validateMultipleWorkCopy_SingleAssignment_InsufficientSpendLimit_Error() {
		when(accountRegisterServicePaymentTerms.authorizeMultipleWork(
				any(BigDecimal.class), any(User.class), any(com.workmarket.domains.work.model.project.Project.class), anySet())).thenReturn(WorkAuthorizationResponse.INSUFFICIENT_SPEND_LIMIT);
		when(userBuyer.getFullName()).thenReturn("userBuyer");

		bulkWorkRouteValidator.validateMultipleWorkCopy(workForm, MULTIPLE_COPIES, COPY_LIMIT, userBuyer, messageBundle);

		verify(messageHelper, times(1)).addError(messageBundle, "search.cart.push.assignment.insufficient_spend_limit", "userBuyer");
	}

	@Test
	public void validateMultipleWorkCopy_SingleAssignment_InvalidSpendLimit_Error() {
		when(accountRegisterServicePaymentTerms.authorizeMultipleWork(
				any(BigDecimal.class), any(User.class), any(com.workmarket.domains.work.model.project.Project.class), anySet())).thenReturn(WorkAuthorizationResponse.INVALID_SPEND_LIMIT);

		bulkWorkRouteValidator.validateMultipleWorkCopy(workForm, MULTIPLE_COPIES, COPY_LIMIT, userBuyer, messageBundle);

		verify(messageHelper, times(1)).addError(messageBundle, "search.cart.push.assignment.invalid_spend_limit");
	}
}
