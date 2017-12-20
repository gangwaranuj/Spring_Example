package com.workmarket.web.controllers.settings;

import com.google.api.client.util.Lists;
import com.google.common.collect.ImmutableList;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.authentication.features.FeatureEvaluatorImpl;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.payments.service.BankingService;
import com.workmarket.service.web.cachebusting.CacheBusterServiceImpl;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.controllers.BaseControllerUnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.View;

import java.util.List;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class SettingsHomeControllerTest extends BaseControllerUnitTest {

	@Mock private View mockView;
	@Mock private ExtendedUserDetails user;
	@Mock private CacheBusterServiceImpl cacheBusterService;
	@Mock private Authentication authentication;
	@Mock private BankingService bankingService;
	@InjectMocks SettingsHomeController controller;
	@InjectMocks BaseController baseController;
	@InjectMocks private FeatureEvaluator featureEvaluator = new FeatureEvaluatorImpl();

	private MockMvc mockMvc;
	private String EMPLOYER_ONBOARDING_WEBPAGES_ROOT_PATH;
	private AbstractBankAccount bankAccount;
	private List<AbstractBankAccount> accounts;

	protected static class MockSettingsHomeControllerRequest {
		public static MockHttpServletRequestBuilder index() {
			return MockMvcRequestBuilders.get("/settings");
		}
		public static MockHttpServletRequestBuilder onboard() { return MockMvcRequestBuilders.get("/settings/onboarding"); }
		public static MockHttpServletRequestBuilder employees() {
			return MockMvcRequestBuilders.get("/settings/onboarding/employees"); }
		public static MockHttpServletRequestBuilder addFunds() {
			return MockMvcRequestBuilders.get("/settings/onboarding/add_funds"); }
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		initController(controller);
		when(securityContextFacade.getCurrentUser()).thenReturn(user);

		mockMvc = standaloneSetup(controller)
			.setSingleView(mockView)
			.build();

		EMPLOYER_ONBOARDING_WEBPAGES_ROOT_PATH = controller.getEmployerOnboardingWebpagesRootPath();

		bankAccount = mock(BankAccount.class);
		when(bankAccount.getCountry()).thenReturn(Country.USA_COUNTRY);

		accounts = Lists.newArrayList();
		accounts.add(bankAccount);

		doReturn(accounts).when(bankingService).findConfirmedACHBankAccounts(anyLong());
		doReturn(ImmutableList.of("blah")).when(bankingService).getUnobfuscatedAccountNumbers(accounts);
	}

	@Test
	public void settings_redirectHome_success() throws Exception {
		mockMvc.perform(SettingsHomeControllerTest.MockSettingsHomeControllerRequest.index())
			.andExpect(status().isOk())
			.andExpect(view().name("redirect:/settings/manage"));
	}

	@Test
	public void settings_employerOnboarding_withAdminAndBuyerAccess_success() throws Exception {
		when(user.isBuyer()).thenReturn(true);

		mockMvc.perform(SettingsHomeControllerTest.MockSettingsHomeControllerRequest.onboard())
			.andExpect(status().isOk())
			.andExpect(view().name(EMPLOYER_ONBOARDING_WEBPAGES_ROOT_PATH + "index"));
	}

	@Test
	public void settings_employerOnboarding_withoutAdminAndBuyerAccess_redirectedToErrorPage() throws Exception {
		when(user.isBuyer()).thenReturn(false);

		mockMvc.perform(SettingsHomeControllerTest.MockSettingsHomeControllerRequest.onboard())
			.andExpect(status().isOk())
			.andExpect(view().name("redirect:/error/no_access"));
	}

	@Test
	public void settings_employerOnboardingEmployeesPage_withAdminAndBuyerAccess_success() throws Exception {
		when(user.isBuyer()).thenReturn(true);

		mockMvc.perform(SettingsHomeControllerTest.MockSettingsHomeControllerRequest.employees())
			.andExpect(status().isOk())
			.andExpect(view().name(EMPLOYER_ONBOARDING_WEBPAGES_ROOT_PATH + "employees"));
	}

	@Test
	public void settings_employerOnboardingEmployeesPage_withoutAdminAndBuyerAccess_redirectedToErrorPage()
		throws Exception {
		when(user.isBuyer()).thenReturn(false);

		mockMvc.perform(SettingsHomeControllerTest.MockSettingsHomeControllerRequest.employees())
			.andExpect(status().isOk())
			.andExpect(view().name("redirect:/error/no_access"));
	}

	@Test
	public void settings_employerOnboardingAddFundsPage_withAdminAndBuyerAccess_success() throws Exception {
		when(user.isBuyer()).thenReturn(true);

		mockMvc.perform(SettingsHomeControllerTest.MockSettingsHomeControllerRequest.addFunds())
			.andExpect(status().isOk())
			.andExpect(view().name(EMPLOYER_ONBOARDING_WEBPAGES_ROOT_PATH + "addFunds"));
	}

	@Test
	public void settings_employerOnboardingAddFundsPage_withoutAdminAndBuyerAccess_redirectedToErrorPage()
		throws Exception {
		when(user.isBuyer()).thenReturn(false);

		mockMvc.perform(SettingsHomeControllerTest.MockSettingsHomeControllerRequest.addFunds())
			.andExpect(status().isOk())
			.andExpect(view().name("redirect:/error/no_access"));
	}
}
