package com.workmarket.service.business.account;

import com.google.common.base.Optional;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionAddOnType;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionCancellation;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfigurationPagination;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionRenewalRequest;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionThroughputIncrementTransaction;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionAddOnDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionCancelDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionConfigurationDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionPaymentTierDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionRenewalRequestDTO;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

/**
 * Author: rocio
 */
public interface SubscriptionService {

	SubscriptionInvoice issueFutureSubscriptionInvoice(long subscriptionPaymentPeriodId);

	SubscriptionInvoice issueRegularSubscriptionInvoice(long subscriptionConfigurationId);

	SubscriptionInvoice issueRegularInvoiceableSubscriptionInvoice(long subscriptionConfigurationId, Calendar firstDayOfNextMonth);

	SubscriptionInvoice issueIncrementalSubscriptionInvoice(List<Long> subscriptionTransactionIds);

	void processThroughputIncrementTransaction(SubscriptionThroughputIncrementTransaction throughputIncrementTx) throws IllegalStateException;

	List<SubscriptionThroughputIncrementTransaction> findAllSubmittedSubscriptionThroughputIncrementTxs();

	Set<SubscriptionConfiguration> findSubscriptionsPendingInvoiceByPaymentPeriodStartDate(Calendar startDate);

	Set<SubscriptionConfiguration> findSubscriptionRenewalsPendingInvoiceByPaymentPeriodStartDate(Calendar startDate);

	/**
	 * Returns a paginated list of all the Subscription Configurations matching the filters if any.
	 *
	 * @return {@link com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfigurationPagination SubscriptionConfigurationPagination}
	 */
	SubscriptionConfigurationPagination findAllSubscriptionConfigurations(SubscriptionConfigurationPagination pagination);
	SubscriptionConfigurationPagination findAllActiveSubscriptionConfigurations();

	/** Returns a paginated list of all the pending Subscription Configurations matching the filters if any.
	 *
	 * @return {@link com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfigurationPagination SubscriptionConfigurationPagination}
	 */
	SubscriptionConfigurationPagination findAllPendingApprovalSubscriptionConfigurations(SubscriptionConfigurationPagination pagination);

	SubscriptionConfiguration findSubscriptionConfigurationById(long subscriptionConfigurationId);

	/**
	 * Returns the active Subscription Configuration for a specific company.
	 *
	 * @param companyId
	 * @return {@link com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration SubscriptionConfiguration} if found.
	 */
	SubscriptionConfiguration findActiveSubscriptionConfigurationByCompanyId(long companyId);

	/**
	 * Saves or updates a subscription configuration.
	 * If the subscription is active and modifications were made then it will go to the approval queue
	 * If the subscription is not active then it will go to the approval queue only if submitForApproval is true
	 *
	 * @param companyId
	 * @param subscriptionConfigurationDTO
	 * @param submitForApproval
	 * @return {@link com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration SubscriptionConfiguration}
	 */
	SubscriptionConfiguration saveOrUpdateSubscriptionConfigurationForCompany(long companyId, SubscriptionConfigurationDTO subscriptionConfigurationDTO, boolean submitForApproval);

	/**
	 * Approves a pending Subscription Configuration.
	 *
	 *
	 * @param subscriptionId
	 * @return {@link com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration SubscriptionConfiguration}
	 */

	SubscriptionConfiguration approveSubscriptionConfiguration(long subscriptionId);

	int approveSubscriptionConfigurations(List<Long> subscriptionConfigurationIds);

	/**
	 * Rejects a pending Subscription Configuration.
	 *
	 * @param subscriptionId
	 * @return {@link com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration SubscriptionConfiguration}
	 */
	SubscriptionConfiguration rejectSubscriptionConfiguration(Long subscriptionId);

	int rejectSubscriptionConfigurations(List<Long> subscriptionConfigurationIds);

	/**
	 * Submit a cancellation to an existing Subscription Configuration
	 * @param subscriptionId
	 * @return {@link com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration SubscriptionConfiguration}
	 */
	SubscriptionCancellation submitCancellationForSubscriptionConfiguration(long subscriptionId, SubscriptionCancelDTO subscriptionCancelDTO);

	void approveSubscriptionCancellation(long subscriptionId);

	void rejectSubscriptionCancellation(long subscriptionId);

	SubscriptionConfiguration renewSubscriptionConfigurationForCompany(long subscriptionId, Optional<SubscriptionRenewalRequest> renewalRequest);

	SubscriptionConfiguration addOrRemoveAddOnsToOrFromSubscriptionConfiguration(long subscriptionId, List<SubscriptionAddOnDTO> subscriptionAddOnDTOs);

	SubscriptionConfiguration addAddOnToSubscriptionConfiguration(long subscriptionId, SubscriptionAddOnDTO subscriptionAddOnDTO);

	void addNoteToSubscriptionConfiguration(long subscriptionId, NoteDTO noteDTO);

	List<SubscriptionPaymentPeriod> findAllSubscriptionPaymentPeriods(long subscriptionId);

	SubscriptionPaymentPeriod findNextNotInvoicedSubscriptionPaymentPeriod(long subscriptionId);

	SubscriptionConfiguration changeSubscriptionFeeConfiguration(long subscriptionId, List<SubscriptionPaymentTierDTO> subscriptionPaymentTierDTOs, String subscriptionTypeCode, BigDecimal blockTierPercentage);

	SubscriptionConfiguration changeSubscriptionFeeConfiguration(long subscriptionId, List<SubscriptionPaymentTierDTO> subscriptionPaymentTierDTOs, Calendar effectiveDate, String subscriptionTypeCode, BigDecimal blockTierPercentage);

	Set<SubscriptionConfiguration> findAllUpdatableSubscriptionConfigurationsByUpdateDate(Calendar updateDate);

	Set<SubscriptionConfiguration> findAllSubscriptionConfigurationsWithNextThroughputReset(Calendar updateDate);

	/**
	 * This process updates active subscriptions based on the effective date of modifications
	 * It also changes the pricing type of companies based on the subscription status
	 * @param subscriptionId
	 */
	void updateSubscriptionConfigurationChanges(long subscriptionId, Calendar updateDate);

	void updateApprovedSubscriptionsPricingType(Calendar updateDate);

	SubscriptionConfiguration findLatestPendingApprovalSubscriptionConfigurationByCompanyId(long companyId);

	SubscriptionConfiguration findLatestNotReadySubscriptionConfigurationByCompanyId(long companyId);

	BigDecimal calculateSubscriptionPaymentTotalAmount(long subscriptionConfigurationId);

	Set<SubscriptionConfiguration> findPreviousSubscriptionConfigurationsByCompanyId(long companyId);

	Calendar findNextPossibleSubscriptionUpdateDate(long companyId);

	/**
	 * Returns a list containing all the available add-on types
	 * @return
	 */
	List<SubscriptionAddOnType> findAllSubscriptionAddOnTypes();

	SubscriptionConfiguration findRenewSubscriptionConfiguration(long subscriptionId);

	void submitSubscriptionRenewalRequest(SubscriptionRenewalRequestDTO renewalRequestDTO);

	SubscriptionRenewalRequest findLatestPendingApprovalSubscriptionRenewalRequest(long subscriptionId);

	boolean hasMboServiceType(long companyId);

	SubscriptionConfiguration updateYearlySubscriptionThroughput(long subscriptionId);

	Calendar calculateNextThroughputResetDate(SubscriptionConfiguration subscriptionConfiguration);

	Calendar calculateNextThroughputResetDate(long subscriptionConfigurationId);
}