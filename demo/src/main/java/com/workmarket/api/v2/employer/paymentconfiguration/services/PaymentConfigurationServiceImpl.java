package com.workmarket.api.v2.employer.paymentconfiguration.services;

import com.workmarket.api.v2.employer.paymentconfiguration.models.PaymentConfigurationDTO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.velvetrope.guest.UserGuest;
import com.workmarket.domains.velvetrope.rope.OfflinePaymentRope;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.velvetrope.Doorman;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service("employerPaymentConfigurationService")
public class PaymentConfigurationServiceImpl implements PaymentConfigurationService {
	@Autowired private AuthenticationService authenticationService;
	@Autowired private PricingService pricingService;
	@Autowired private CompanyService companyService;
	@Autowired @Qualifier("offlinePaymentDoorman") Doorman doorman;

	@Override
	public PaymentConfigurationDTO get() {
		Long currentUserCompanyId = getCurrentUserCompanyId();
		BigDecimal workFeePercentage = getWorkFeePercentage(currentUserCompanyId);
		PaymentConfiguration paymentConfiguration = getPaymentConfiguration(currentUserCompanyId);
		PaymentConfigurationDTO.Builder paymentConfigurationDtoBuilder = new PaymentConfigurationDTO.Builder();

		doorman.welcome(new UserGuest(getCurrentUser()), new OfflinePaymentRope(paymentConfigurationDtoBuilder));

		return paymentConfigurationDtoBuilder
			.setWorkFeePercentage(workFeePercentage)
			.setSubscribed(paymentConfiguration.isSubscriptionPricing())
			.setAssignmentPricingType(paymentConfiguration.getPaymentCalculatorType())
			.build();
	}

	private PaymentConfiguration getPaymentConfiguration(Long currentUserCompanyId) {
		return companyService.findCompanyById(currentUserCompanyId).getPaymentConfiguration();
	}

	private BigDecimal getWorkFeePercentage(Long currentUserCompanyId) {
		AccountRegister register = pricingService.findDefaultRegisterForCompany(currentUserCompanyId);
		return register.getCurrentWorkFeePercentage();
	}

	private Long getCurrentUserCompanyId() {
		return authenticationService.getCurrentUserCompanyId();
	}

	private User getCurrentUser() {
		return authenticationService.getCurrentUser();
	}
}