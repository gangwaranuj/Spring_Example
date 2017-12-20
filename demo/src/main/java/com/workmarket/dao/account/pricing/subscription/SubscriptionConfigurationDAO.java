package com.workmarket.dao.account.pricing.subscription;

import com.workmarket.dao.PaginatableDAOInterface;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfigurationPagination;

import java.util.Calendar;
import java.util.Set;

/**
 * Author: rocio
 */
public interface SubscriptionConfigurationDAO extends PaginatableDAOInterface<SubscriptionConfiguration> {

	SubscriptionConfiguration findActiveSubscriptionConfigurationByCompanyId(long companyId);
	
	SubscriptionConfiguration findLatestPendingApprovalSubscriptionConfigurationByCompanyId(long companyId);
	
	SubscriptionConfiguration findLatestNotReadySubscriptionConfigurationByCompanyId(long companyId);

	SubscriptionConfigurationPagination findAllSubscriptionConfigurations(SubscriptionConfigurationPagination pagination);

	SubscriptionConfigurationPagination findAllPendingSubscriptionConfigurations(SubscriptionConfigurationPagination pagination);

	Set<SubscriptionConfiguration> findAllUpdatableSubscriptionConfigurationsByUpdateDate(Calendar effectiveDate);

	Set<SubscriptionConfiguration> findSubscriptionsPendingInvoiceByPaymentPeriodStartDate(Calendar periodStartDate);

	Set<SubscriptionConfiguration> findSubscriptionRenewalsPendingInvoiceByPaymentPeriodStartDate(Calendar periodStartDate);

	Set<SubscriptionConfiguration> findPreviousSubscriptionConfigurationsByCompanyId(long companyId);

	SubscriptionConfiguration findRenewSubscriptionConfiguration(long subscriptionId);

	Set<SubscriptionConfiguration> findApprovedSubscriptionConfigurationsWithTransactionalPricingByEffectiveDate(Calendar effectiveDate);

	Set<SubscriptionConfiguration> findAllSubscriptionConfigurationsWithNextThroughputReset(Calendar updateDate);


}
