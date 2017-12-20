package com.workmarket.web.helpers;

import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.thrift.core.Name;
import com.workmarket.thrift.core.User;
import com.workmarket.thrift.work.ManageMyWorkMarket;
import com.workmarket.thrift.work.Work;
import com.workmarket.web.models.MessageBundle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkAuthorizationFailureHelperTest {
	@Mock MessageBundleHelper messageBundleHelper;
	@InjectMocks WorkAuthorizationFailureHelper workAuthorizationFailureHelper;

	Work work;
	MessageBundle messageBundle;
	ManageMyWorkMarket mmw;
	User user;
	Name name;
	private static String FULLNAME = "John Doe";

	@Before
	public void setup() {
		work = mock(Work.class);
		messageBundle = mock(MessageBundle.class);
		mmw = mock(ManageMyWorkMarket.class);
		user = mock(User.class);
		name = mock(Name.class);

		when(work.getConfiguration()).thenReturn(mmw);
		when(mmw.isSetPaymentTermsDays()).thenReturn(true);
		when(work.getBuyer()).thenReturn(user);
		when(user.getName()).thenReturn(name);
		when(name.getFullName()).thenReturn(FULLNAME);
	}


	@Test
	public void handleErrorsFromAuthResponse_setsInsufficientFunds_terms() {
		workAuthorizationFailureHelper.handleErrorsFromAuthResponse(WorkAuthorizationResponse.INSUFFICIENT_FUNDS, work, messageBundle);
		verify(messageBundleHelper).addError(messageBundle, "search.cart.push.assignment.insufficient_funds_terms", null);
	}

	@Test
	public void handleErrorsFromAuthResponse_setsInsufficientFunds_prefund() {
		when(mmw.isSetPaymentTermsDays()).thenReturn(false);
		workAuthorizationFailureHelper.handleErrorsFromAuthResponse(WorkAuthorizationResponse.INSUFFICIENT_FUNDS, work, messageBundle);
		verify(messageBundleHelper).addError(messageBundle, "search.cart.push.assignment.insufficient_funds_prefund", null);
	}

	@Test
	public void handleErrorsFromAuthResponse_setsInsufficientBudget() {
		workAuthorizationFailureHelper.handleErrorsFromAuthResponse(WorkAuthorizationResponse.INSUFFICIENT_BUDGET, work, messageBundle);
		verify(messageBundleHelper).addError(messageBundle, WorkAuthorizationResponse.INSUFFICIENT_BUDGET.getMessagePropertyKey(), null);
	}

	@Test
	public void handleErrorsFromAuthResponse_setsInsufficientSpendLimit() {
		workAuthorizationFailureHelper.handleErrorsFromAuthResponse(WorkAuthorizationResponse.INSUFFICIENT_SPEND_LIMIT, work, messageBundle);
		verify(messageBundleHelper).addError(messageBundle, WorkAuthorizationResponse.INSUFFICIENT_SPEND_LIMIT.getMessagePropertyKey(), FULLNAME);
	}

	@Test
	public void handleErrorsFromAuthResponse_setsInvalidSpendLimit() {
		workAuthorizationFailureHelper.handleErrorsFromAuthResponse(WorkAuthorizationResponse.INVALID_SPEND_LIMIT, work, messageBundle);
		verify(messageBundleHelper).addError(messageBundle, WorkAuthorizationResponse.INVALID_SPEND_LIMIT.getMessagePropertyKey(), null);
	}

	@Test
	public void handleErrorsFromAuthResponse_setsAuthorizationError() {
		workAuthorizationFailureHelper.handleErrorsFromAuthResponse(WorkAuthorizationResponse.UNKNOWN, work, messageBundle);
		verify(messageBundleHelper).addError(messageBundle, "search.cart.push.assignment.generic_spend_authorization_error", null);
	}
}

