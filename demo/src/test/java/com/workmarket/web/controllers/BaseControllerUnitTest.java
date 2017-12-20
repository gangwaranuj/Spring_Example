package com.workmarket.web.controllers;

import com.google.common.collect.Lists;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.PrivacyEvaluator;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.UserNavService;
import com.workmarket.service.locale.LocaleService;
import com.workmarket.biz.gen.Messages.WMLocale;
import com.workmarket.biz.gen.Messages.WMFormat;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.helpers.MessageBundleHelperImpl;
import com.workmarket.web.models.MessageBundle;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

import java.math.BigDecimal;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

/**
 * Created by nick on 6/6/13 10:18 PM
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class BaseControllerUnitTest {

	protected static final Long COMPANY_ID = 1L;
	protected static final Long USER_ID = 2L;
	protected static final Long WORK_ID = 3L;

	@Mock protected AccountRegisterService accountRegisterServicePrefundImpl;
	@Mock protected PricingService pricingService;
	@Mock protected FeatureEvaluator featureEvaluator;
	@Mock protected FeatureEntitlementService featureEntitlementService;
	@Mock protected PrivacyEvaluator privacyEvaluator;
	@Mock protected UserNavService userNavService;
	@Mock protected CompanyService companyService;
	@Mock protected LocaleService localeService;

	protected SecurityContextFacade securityContextFacade;
	protected MessageBundleHelper messageBundleHelper;
	protected AccountRegisterSummaryFields accountRegisterSummaryFields;

	@InjectMocks BaseController baseController;

	public static ResultHandler verifyFlashHasNoErrors() {
		return new ResultHandler() {
			@Override
			public void handle(MvcResult mvcResult) throws Exception {
				MessageBundle bundle = (MessageBundle) mvcResult.getFlashMap().get("bundle");
				assertFalse(bundle.hasErrors());
			}
		};
	}

	@Before
	public void initBaseControllerTest() {
		securityContextFacade = mock(SecurityContextFacade.class);
		init(securityContextFacade);

		when(companyService.findCompanyAvatars(any(Long.class))).thenReturn(null);

		initController(baseController);
	}

	public void initController(Object controller) {
		accountRegisterSummaryFields = mock(AccountRegisterSummaryFields.class);
		when(accountRegisterSummaryFields.getWithdrawableCash()).thenReturn(new BigDecimal(0));
		when(accountRegisterServicePrefundImpl.getAccountRegisterSummaryFields(any(Long.class))).thenReturn(accountRegisterSummaryFields);

		featureEntitlementService = mock(FeatureEntitlementService.class);
		when(featureEntitlementService.hasFeatureToggle(any(Long.class), any(String.class))).thenReturn(false);

		Whitebox.setInternalState(controller, "accountRegisterServicePrefundImpl", accountRegisterServicePrefundImpl);
		Whitebox.setInternalState(controller, "securityContextFacade", securityContextFacade);
		Whitebox.setInternalState(controller, "featureEntitlementService", featureEntitlementService);

		WMLocale mockLocale = WMLocale.newBuilder().build();
		when(localeService.getPreferredLocale(any(String.class))).thenReturn(mockLocale);

		WMFormat mockFormat = WMFormat.newBuilder().build();
		when(localeService.getPreferredFormat(any(String.class))).thenReturn(mockFormat);
	}

	public void init(SecurityContextFacade securityContextFacade) {
		when(securityContextFacade.getSecurityContext()).thenReturn(new SecurityContextImpl());
		ExtendedUserDetails extendedUserDetails = new ExtendedUserDetails("test", "user", Lists.<GrantedAuthority>newArrayList());
		extendedUserDetails.setCompanyId(COMPANY_ID);
		extendedUserDetails.setId(USER_ID);
		when(securityContextFacade.getCurrentUser()).thenReturn(extendedUserDetails);
	}

	public void initMessageBundleHelper(Object controller) {
		initMessageBundleHelper(controller, "messageHelper");
	}

	public void initMessageBundleHelper(Object controller, String propertyName) {
		MessageBundleHelper helperSpy = spy(new MessageBundleHelperImpl());
		MessageSource messageSource = mock(MessageSource.class);
		Whitebox.setInternalState(helperSpy, "messageSource", messageSource);
		Whitebox.setInternalState(controller, propertyName, helperSpy);
	}
}