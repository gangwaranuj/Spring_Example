
package com.workmarket.service.business.subscriptions;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.pricing.AccountPricingType;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.account.pricing.CompanyAccountPricingTypeChange;
import com.workmarket.domains.model.account.pricing.subscription.*;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.invoice.InvoiceStatusType;
import com.workmarket.domains.model.invoice.PaymentFulfillmentStatusType;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.data.report.work.AccountStatementDetailPagination;
import com.workmarket.data.report.work.AccountStatementFilters;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.account.pricing.AccountServiceTypeDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionAddOnDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionCancelDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionConfigurationDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionPaymentTierDTO;
import com.workmarket.service.business.scheduler.subscription.SubscriptionTransactionExecutor;
import com.workmarket.configuration.Constants;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;

/** Implements all common asserts which are used throughout all of the subscription testing process */
@Ignore
public abstract class SubscriptionServiceBaseIT extends BaseServiceIT {

	protected User employee;
	protected User contractor;
	private final Random rand  = new Random();
	protected SubscriptionConfigurationDTO subscriptionDTO;
	final String SUBSCRIPTION_CLIENT_REF_ID = "subscription_client_ref_id";
	@Autowired protected SubscriptionTransactionExecutor subscriptionTransactionExecutor;

	@Before
	public void before() throws Exception {
		User currentUser = userService.findUserById(Constants.WORKMARKET_SYSTEM_USER_ID);
		authenticationService.setCurrentUser(currentUser);

		employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		List<SubscriptionPaymentTierDTO> paymentTiers = Lists.newArrayList();
		SubscriptionPaymentTierDTO subscriptionPaymentTierDTO = new SubscriptionPaymentTierDTO();
		subscriptionPaymentTierDTO.setMinimum(BigDecimal.ZERO);
		subscriptionPaymentTierDTO.setPaymentAmount(BigDecimal.TEN);
		subscriptionPaymentTierDTO.setVendorOfRecordAmount(BigDecimal.TEN);
		paymentTiers.add(subscriptionPaymentTierDTO);

		subscriptionDTO = new SubscriptionConfigurationDTO();
		subscriptionDTO.setSubscriptionPaymentTierDTOs(paymentTiers);
		subscriptionDTO.setEffectiveDate(DateUtilities.getCalendarWithFirstDayOfTheMonth(DateUtilities.getMidnightNextMonth(), Constants.EST_TIME_ZONE));
		subscriptionDTO.setNumberOfPeriods(2);
		subscriptionDTO.setSetUpFee(BigDecimal.TEN);
		subscriptionDTO.setSubscriptionPeriod(SubscriptionPeriod.QUARTERLY);
		subscriptionDTO.setClientRefId(SUBSCRIPTION_CLIENT_REF_ID);
		subscriptionDTO.setSubscriptionTypeCode(SubscriptionType.BAND);
	}

	// { Subscription utilities

	protected void testRejectSubscription(long subscriptionId) {
		// Our subscription will be rejected
		SubscriptionConfiguration subscription = subscriptionService.rejectSubscriptionConfiguration(subscriptionId);
		Assert.assertNotNull(subscription);
		Assert.assertTrue(subscription.isRejected());
		Assert.assertFalse(subscription.isVerified());
	}

	protected void submitAndApproveCancelSubscription(SubscriptionConfiguration configuration){
		SubscriptionCancelDTO subscriptionCancelDTO = new SubscriptionCancelDTO();
		Calendar c = (Calendar) subscriptionDTO.getEffectiveDate().clone();
		c.add(Calendar.DAY_OF_MONTH, 5);
		subscriptionCancelDTO.setCancellationDate(c);
		subscriptionCancelDTO.setCancellationFee(30.00);
		SubscriptionCancellation cancellation = subscriptionService.submitCancellationForSubscriptionConfiguration(configuration.getId(), subscriptionCancelDTO);

		Assert.assertNotNull(cancellation);
		Assert.assertNotNull(cancellation.getCancellationFee());
		testSubscriptionIsOnApprovalQueue(configuration.getId(), true);
		subscriptionService.approveSubscriptionCancellation(configuration.getId());
		testSubscriptionIsOnApprovalQueue(configuration.getId(), false);

		// Scheduled subscription expiration is no longer on the expiration date but on the cancellation date
		boolean cancellationIsScheduled = false;
		boolean expirationIsScheduled = false;
		for (CompanyAccountPricingTypeChange pricingTypeChange : accountPricingService.getCompanyAccountPricingTypeChangeScheduledBeforeDate(configuration.getEndDate(), false)) {
			if (pricingTypeChange.getCompany().getId().equals(employee.getCompany().getId())
					&& pricingTypeChange.getFromAccountPricingType().getCode().equals(AccountPricingType.SUBSCRIPTION_PRICING_TYPE)
					&& pricingTypeChange.getToAccountPricingType().getCode().equals(AccountPricingType.TRANSACTIONAL_PRICING_TYPE)) {
				if (pricingTypeChange.getScheduledChangeDate().compareTo(configuration.getEndDate()) == 0) {
					expirationIsScheduled = true;
					break;
				} else if (pricingTypeChange.getScheduledChangeDate().compareTo(cancellation.getEffectiveDate()) == 0) {
					cancellationIsScheduled = true;
					break;
				}
			}
		}
		Assert.assertTrue(cancellationIsScheduled);
		Assert.assertFalse(expirationIsScheduled);

		AccountStatementDetailPagination pagination = new AccountStatementDetailPagination();
		AccountStatementFilters filters = new AccountStatementFilters();
		filters.setPayables(true);
		authenticationService.setCurrentUser(employee.getId());
		pagination = billingService.getStatementDashboard(filters, pagination);
		Assert.assertNotNull(pagination);
		Assert.assertFalse(pagination.getResults().isEmpty());
		SubscriptionInvoice invoice = billingService.findInvoiceById(pagination.getResults().get(0).getInvoiceId());
		billingService.payInvoice(employee.getId(), invoice.getId());
	}

	protected void submitAndRejectCancelSubscription(SubscriptionConfiguration configuration){
		SubscriptionCancelDTO subscriptionCancelDTO = new SubscriptionCancelDTO();
		Calendar c = (Calendar) subscriptionDTO.getEffectiveDate().clone();
		c.add(Calendar.DAY_OF_MONTH, 5);
		subscriptionCancelDTO.setCancellationDate(c);
		subscriptionCancelDTO.setCancellationFee(30.00);
		SubscriptionCancellation cancellation = subscriptionService.submitCancellationForSubscriptionConfiguration(configuration.getId(), subscriptionCancelDTO);

		Assert.assertNotNull(cancellation);
		Assert.assertNotNull(cancellation.getCancellationFee());
		testSubscriptionIsOnApprovalQueue(configuration.getId(), true);
		configuration = subscriptionService.rejectSubscriptionConfiguration(configuration.getId());
		testSubscriptionIsOnApprovalQueue(configuration.getId(), false);

		configuration = subscriptionService.findSubscriptionConfigurationById(configuration.getId());
		Assert.assertNotNull(configuration.getSubscriptionCancellation());
		Assert.assertTrue(configuration.getSubscriptionCancellation().isDeclined());
		Assert.assertTrue(configuration.isActive());
		Assert.assertTrue(configuration.isApproved());
	}

	/**
	 * Edits a subscription and submits it for approval
	*/
	protected void testSubscriptionNotReadyToPendingApproval(long subscriptionId) {

		// Preconditions for this test
		// Subscription exists, is not ready for approval and status is pending
		Assert.assertNotNull(subscriptionId);
		SubscriptionConfiguration configuration = subscriptionService.findSubscriptionConfigurationById(subscriptionId);
		Assert.assertNotNull(configuration);

		Assert.assertTrue(configuration.isNotReady());
		Assert.assertTrue(configuration.isPending());
		testSubscriptionIsOnApprovalQueue(subscriptionId, false);
		//
		// Invalid submitForApproval - Nothing changes
		SubscriptionConfigurationDTO dto = new SubscriptionConfigurationDTO(configuration);
		dto.setSetUpFee(BigDecimal.valueOf(-25L));
		try{
			subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(configuration.getCompany().getId(), dto, true);
		} catch (Exception exception){
			Assert.assertTrue(exception instanceof IllegalArgumentException || exception instanceof IllegalStateException);
		} finally {
			configuration = subscriptionService.findSubscriptionConfigurationById(subscriptionId);
			Assert.assertFalse(configuration.getSetUpFee().compareTo(dto.getSetUpFee()) == 0);
			Assert.assertTrue(configuration.isNotReady());
			Assert.assertTrue(configuration.isPending());
			testSubscriptionIsOnApprovalQueue(subscriptionId, false);
		}
		//
		// Valid edit
		dto = new SubscriptionConfigurationDTO(configuration);
		dto.setSetUpFee(BigDecimal.valueOf(25L));
		configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(configuration.getCompany().getId(), dto, false);

		Assert.assertTrue(configuration.getSetUpFee().compareTo(dto.getSetUpFee()) == 0);
		Assert.assertTrue(configuration.isNotReady());
		Assert.assertTrue(configuration.isPending());
		testSubscriptionIsOnApprovalQueue(subscriptionId, false);
		//
		// Invalid submitForApproval - Nothing changes
		dto = new SubscriptionConfigurationDTO(configuration);
		dto.setSetUpFee(BigDecimal.valueOf(-25L));
		try{
			subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(configuration.getCompany().getId(), dto, true);
		} catch (Exception exception){
			Assert.assertTrue(exception instanceof IllegalArgumentException || exception instanceof IllegalStateException);
		} finally {
			configuration = subscriptionService.findSubscriptionConfigurationById(subscriptionId);
			Assert.assertFalse(configuration.getSetUpFee().compareTo(dto.getSetUpFee()) == 0);
			Assert.assertTrue(configuration.isNotReady());
			Assert.assertTrue(configuration.isPending());
			testSubscriptionIsOnApprovalQueue(subscriptionId, false);
		}
		//
		// Valid submitForApproval
		dto = new SubscriptionConfigurationDTO(configuration);
		configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(configuration.getCompany().getId(), dto, true);

		Assert.assertTrue(configuration.isPendingApproval());
		Assert.assertTrue(configuration.isPending());
		testSubscriptionIsOnApprovalQueue(subscriptionId, true);
		//
	}

	/**
	 * Edits a pending approval subscription and approves it
	 */
	protected void testSubscriptionPendingApprovalToApproved(long subscriptionId) {

		// Preconditions for this test
		// Subscription exists, is pending approval and status is pending
		Assert.assertNotNull(subscriptionId);
		SubscriptionConfiguration configuration = subscriptionService.findSubscriptionConfigurationById(subscriptionId);
		Assert.assertNotNull(configuration);

		Assert.assertTrue(configuration.isPendingApproval());
		Assert.assertTrue(configuration.isPending());
		testSubscriptionIsOnApprovalQueue(subscriptionId, true);
		//
		// Invalid submitForApproval - Nothing changes
		SubscriptionConfigurationDTO dto = new SubscriptionConfigurationDTO(configuration);
		dto.setSetUpFee(BigDecimal.valueOf(-25L));
		try{
			subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(configuration.getCompany().getId(), dto, true);
				} catch (Exception exception){
			Assert.assertTrue(exception instanceof IllegalArgumentException || exception instanceof IllegalStateException);
		} finally {
			configuration = subscriptionService.findSubscriptionConfigurationById(subscriptionId);
			Assert.assertFalse(configuration.getSetUpFee().compareTo(dto.getSetUpFee()) == 0);
			Assert.assertTrue(configuration.isPendingApproval());
			Assert.assertTrue(configuration.isPending());
			testSubscriptionIsOnApprovalQueue(subscriptionId, true);
		}

		//
		// Valid edit
		dto = new SubscriptionConfigurationDTO(configuration);
		dto.setSetUpFee(BigDecimal.valueOf(15L));
		configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(configuration.getCompany().getId(), dto, true);

		Assert.assertTrue(configuration.getSetUpFee().compareTo(dto.getSetUpFee()) == 0);
		Assert.assertTrue(configuration.isPendingApproval());
		Assert.assertTrue(configuration.isPending());
		testSubscriptionIsOnApprovalQueue(subscriptionId, true);
		//
		// Approve
		configuration = subscriptionService.approveSubscriptionConfiguration(configuration.getId());

		Assert.assertTrue(configuration.isApproved());
		Assert.assertTrue(configuration.isActive());
		testSubscriptionIsOnApprovalQueue(subscriptionId, false);
		//

	}

	/**
	 * Edits an active subscription and approves/rejects the changes
	 */
	protected void testSubscriptionApprovedToApprovedButModified(long subscriptionId) {

		// Preconditions for this test
		// Subscription exists, is approved and status is active
		Assert.assertNotNull(subscriptionId);
		SubscriptionConfiguration configuration = subscriptionService.findSubscriptionConfigurationById(subscriptionId);
		Assert.assertNotNull(configuration);

		Assert.assertTrue(configuration.isApproved());
		Assert.assertTrue(configuration.isActive());
		testSubscriptionIsOnApprovalQueue(subscriptionId, false);
		//
		// AddOnModifications
		{
			//
			// Invalid edit - Nothing changes
			List<SubscriptionAddOnDTO> initialAddOnDTOs = Lists.newArrayList();
			// This will be added later
			List<SubscriptionAddOnDTO> finalAddOnDTOs = Lists.newArrayList();
			Calendar nextPossibleSubscriptionUpdateDate = subscriptionService.findNextPossibleSubscriptionUpdateDate(configuration.getCompany().getId());
			for (String addOnTypeCode : SubscriptionAddOnType.addOnTypeCodes) {
				SubscriptionAddOnDTO addOnDTO = new SubscriptionAddOnDTO();
				addOnDTO.setAddOnTypeCode(addOnTypeCode);
				addOnDTO.setEffectiveDate(nextPossibleSubscriptionUpdateDate);
				if (rand.nextBoolean()) {
					// I want these to fail
					addOnDTO.setCostPerPeriod(BigDecimal.valueOf(-rand.nextInt(32)));
					initialAddOnDTOs.add(addOnDTO);
				} else {
					addOnDTO.setCostPerPeriod(BigDecimal.valueOf(rand.nextInt(32)));
					finalAddOnDTOs.add(addOnDTO);
				}
			}

			SubscriptionConfigurationDTO dto = new SubscriptionConfigurationDTO(configuration);
			dto.setSubscriptionAddOnDTOs(initialAddOnDTOs);
			try{
				subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), dto, true);
			} catch (Exception exception){
				Assert.assertTrue(exception instanceof IllegalArgumentException || exception instanceof IllegalStateException);
			} finally {
				configuration = subscriptionService.findSubscriptionConfigurationById(subscriptionId);
				Assert.assertTrue(configuration.isApproved());
				Assert.assertTrue(configuration.isActive());
				Assert.assertFalse(configuration.hasActiveAddOns());
				testSubscriptionIsOnApprovalQueue(subscriptionId, false);
			}
			//
			// Invalid edit - Nothing changes
			for (SubscriptionAddOnDTO addOnDTO : initialAddOnDTOs) {
				addOnDTO.setCostPerPeriod(BigDecimal.valueOf(rand.nextInt(32) + 1));
				addOnDTO.setEffectiveDate(DateUtilities.getCalendarNow());
			}
			try{
				subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), dto, true);
			} catch (Exception exception){
				Assert.assertTrue(exception instanceof IllegalArgumentException || exception instanceof IllegalStateException);
			} finally {
				configuration = subscriptionService.findSubscriptionConfigurationById(subscriptionId);
				Assert.assertTrue(configuration.isApproved());
				Assert.assertTrue(configuration.isActive());
				Assert.assertFalse(configuration.hasActiveAddOns());
				testSubscriptionIsOnApprovalQueue(subscriptionId, false);
			}
			//
			// Valid edit - Nothing changes because it wasn't approved
			for (SubscriptionAddOnDTO addOnDTO : initialAddOnDTOs) {
				addOnDTO.setCostPerPeriod(BigDecimal.valueOf(rand.nextInt(32) + 1));
				addOnDTO.setEffectiveDate(nextPossibleSubscriptionUpdateDate);
			}
			configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), dto, true);
			Assert.assertTrue(configuration.isApproved());
			Assert.assertTrue(configuration.isActive());
			Assert.assertFalse(configuration.hasActiveAddOns());
			testSubscriptionIsOnApprovalQueue(subscriptionId, true);
			//
			//
			// Reject - Nothing changes
			configuration = subscriptionService.rejectSubscriptionConfiguration(configuration.getId());
			Assert.assertTrue(configuration.isApproved());
			Assert.assertTrue(configuration.isActive());
			Assert.assertFalse(configuration.hasActiveAddOns());
			testSubscriptionIsOnApprovalQueue(subscriptionId, false);
			//
			// Approve - Nothing changes : we rejected the last change
			configuration = subscriptionService.approveSubscriptionConfiguration(configuration.getId());
			Assert.assertTrue(configuration.isApproved());
			Assert.assertTrue(configuration.isActive());
			Assert.assertFalse(configuration.hasActiveAddOns());
			testSubscriptionIsOnApprovalQueue(subscriptionId, false);
			//
			// Valid edit - Nothing changes because it wasn't approved
			configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), dto, true);
			Assert.assertTrue(configuration.isApproved());
			Assert.assertTrue(configuration.isActive());
			Assert.assertFalse(configuration.hasActiveAddOns());
			testSubscriptionIsOnApprovalQueue(subscriptionId, true);
			//
			// Valid edit (overrides the previous edit)
			//
			for (SubscriptionAddOnDTO addOnDTO : initialAddOnDTOs) {
				addOnDTO.setCostPerPeriod(BigDecimal.valueOf(rand.nextInt(32) + 1));
			}
			configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), dto, true);
			Assert.assertTrue(configuration.isApproved());
			Assert.assertTrue(configuration.isActive());
			Assert.assertFalse(configuration.hasActiveAddOns());
			testSubscriptionIsOnApprovalQueue(subscriptionId, true);
			//
			// Approve - Nothing changes because the effectiveDate is not here yet
			configuration = subscriptionService.approveSubscriptionConfiguration(configuration.getId());
			Assert.assertTrue(configuration.isApproved());
			Assert.assertTrue(configuration.isActive());
			Assert.assertFalse(configuration.hasActiveAddOns());
			testSubscriptionIsOnApprovalQueue(subscriptionId, false);
			//
			// Update on the effective date - Now we have addOns
			subscriptionService.updateSubscriptionConfigurationChanges(configuration.getId(), nextPossibleSubscriptionUpdateDate);
			configuration = subscriptionService.findSubscriptionConfigurationById(configuration.getId());
			Assert.assertTrue(configuration.isApproved());
			Assert.assertTrue(configuration.isActive());
			testAddOnEquality(configuration.getAddOns(), initialAddOnDTOs);
			testSubscriptionIsOnApprovalQueue(subscriptionId, false);
			//
			// I want the other addOns now - Nothing changes because the effectiveDate is not here yet
			dto.setSubscriptionAddOnDTOs(finalAddOnDTOs);
			configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), dto, true);
			Assert.assertTrue(configuration.isApproved());
			Assert.assertTrue(configuration.isActive());
			testAddOnEquality(configuration.getAddOns(), initialAddOnDTOs);
			testSubscriptionIsOnApprovalQueue(subscriptionId, true);
			//
			// Approve - Nothing changes because the effectiveDate is not here yet
			configuration = subscriptionService.approveSubscriptionConfiguration(configuration.getId());
			Assert.assertTrue(configuration.isApproved());
			Assert.assertTrue(configuration.isActive());
			testAddOnEquality(configuration.getAddOns(), initialAddOnDTOs);
			testSubscriptionIsOnApprovalQueue(subscriptionId, false);
			//
			// Update on the effective date - Now we have the other addOns
			subscriptionService.updateSubscriptionConfigurationChanges(configuration.getId(), nextPossibleSubscriptionUpdateDate);
			configuration = subscriptionService.findSubscriptionConfigurationById(configuration.getId());
			Assert.assertTrue(configuration.isApproved());
			Assert.assertTrue(configuration.isActive());
			testAddOnEquality(configuration.getAddOns(), finalAddOnDTOs);
			testSubscriptionIsOnApprovalQueue(subscriptionId, false);
			//

		}
		//
		// FeeConfiguration modifications
		{
			//
			// Invalid edit - Nothing changes
			List<SubscriptionPaymentTierDTO> oldPaymentTiers = Lists.newArrayList();
			for (SubscriptionPaymentTier paymentTier : configuration.getActiveSubscriptionFeeConfiguration().getSubscriptionPaymentTiers()) {
				oldPaymentTiers.add(new SubscriptionPaymentTierDTO(paymentTier));
			}

			SubscriptionConfigurationDTO dto = new SubscriptionConfigurationDTO(configuration);

			List<SubscriptionPaymentTierDTO> newPaymentTiers = Lists.newArrayList();
			SubscriptionPaymentTierDTO subscriptionPaymentTierDTO = new SubscriptionPaymentTierDTO();
			subscriptionPaymentTierDTO.setMaximum(BigDecimal.ZERO);
			newPaymentTiers.add(subscriptionPaymentTierDTO);
			dto.setSubscriptionPaymentTierDTOs(newPaymentTiers);
			dto.setPaymentTierEffectiveDate(DateUtilities.getCalendarWithFirstDayOfTheMonth(DateUtilities.getMidnightNextMonth(), TimeZone.getDefault()));

			try{
				subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), dto, true);
			} catch (Exception exception){
				Assert.assertTrue(exception instanceof IllegalArgumentException || exception instanceof IllegalStateException);
			} finally {
				configuration = subscriptionService.findSubscriptionConfigurationById(subscriptionId);
				Assert.assertTrue(configuration.isApproved());
				Assert.assertTrue(configuration.isActive());
				testPaymentTierEquality(configuration.getActiveSubscriptionFeeConfiguration().getSubscriptionPaymentTiers(), oldPaymentTiers);
				testSubscriptionIsOnApprovalQueue(subscriptionId, false);
			}

			//
			// Valid edit - Nothing changes because it wasn't approved
			dto = new SubscriptionConfigurationDTO(configuration);

			newPaymentTiers = Lists.newArrayList();
			subscriptionPaymentTierDTO = new SubscriptionPaymentTierDTO();
			subscriptionPaymentTierDTO.setMinimum(BigDecimal.ZERO);
			subscriptionPaymentTierDTO.setMaximum(SubscriptionPaymentTier.MAXIMUM);
			subscriptionPaymentTierDTO.setPaymentAmount(BigDecimal.ONE);
			newPaymentTiers.add(subscriptionPaymentTierDTO);
			dto.setSubscriptionPaymentTierDTOs(newPaymentTiers);
			dto.setPaymentTierEffectiveDate(configuration.getNextPaymentPeriodStartDate());

			configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), dto, true);
			Assert.assertTrue(configuration.isApproved());
			Assert.assertTrue(configuration.isActive());
			testPaymentTierEquality(configuration.getActiveSubscriptionFeeConfiguration().getSubscriptionPaymentTiers(), oldPaymentTiers);
			testSubscriptionIsOnApprovalQueue(subscriptionId, true);
			//
			// Reject - Nothing changes
			configuration = subscriptionService.rejectSubscriptionConfiguration(configuration.getId());
			 Assert.assertTrue(configuration.isApproved());
			 Assert.assertTrue(configuration.isActive());
			 testPaymentTierEquality(configuration.getActiveSubscriptionFeeConfiguration().getSubscriptionPaymentTiers(), oldPaymentTiers);
			 testSubscriptionIsOnApprovalQueue(subscriptionId, false);
			//
			// Approve - Nothing changes : we rejected the last change
			configuration = subscriptionService.approveSubscriptionConfiguration(configuration.getId());
			Assert.assertTrue(configuration.isApproved());
			Assert.assertTrue(configuration.isActive());
			testPaymentTierEquality(configuration.getActiveSubscriptionFeeConfiguration().getSubscriptionPaymentTiers(), oldPaymentTiers);
			testSubscriptionIsOnApprovalQueue(subscriptionId, false);
			//
			// Valid edit
			dto = new SubscriptionConfigurationDTO(configuration);

			newPaymentTiers = Lists.newArrayList();
			subscriptionPaymentTierDTO = new SubscriptionPaymentTierDTO();
			subscriptionPaymentTierDTO.setMinimum(BigDecimal.ZERO);
			subscriptionPaymentTierDTO.setMaximum(BigDecimal.ONE);
			subscriptionPaymentTierDTO.setPaymentAmount(BigDecimal.ONE);
			newPaymentTiers.add(subscriptionPaymentTierDTO);
			subscriptionPaymentTierDTO = new SubscriptionPaymentTierDTO();
			subscriptionPaymentTierDTO.setMinimum(BigDecimal.ONE);
			subscriptionPaymentTierDTO.setMaximum(SubscriptionPaymentTier.MAXIMUM);
			subscriptionPaymentTierDTO.setPaymentAmount(BigDecimal.TEN);
			newPaymentTiers.add(subscriptionPaymentTierDTO);
			dto.setSubscriptionPaymentTierDTOs(newPaymentTiers);
			dto.setPaymentTierEffectiveDate(subscriptionService.findNextPossibleSubscriptionUpdateDate(employee.getCompany().getId()));

			configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), dto, true);
			Assert.assertTrue(configuration.isApproved());
			Assert.assertTrue(configuration.isActive());
			testPaymentTierEquality(configuration.getActiveSubscriptionFeeConfiguration().getSubscriptionPaymentTiers(), oldPaymentTiers);
			testSubscriptionIsOnApprovalQueue(subscriptionId, true);
			//
			// Valid edit (overrides the previous edit)
			//
			dto = new SubscriptionConfigurationDTO(configuration);

			newPaymentTiers = Lists.newArrayList();
			subscriptionPaymentTierDTO = new SubscriptionPaymentTierDTO();
			subscriptionPaymentTierDTO.setMinimum(BigDecimal.ZERO);
			subscriptionPaymentTierDTO.setMaximum(BigDecimal.TEN);
			subscriptionPaymentTierDTO.setPaymentAmount(BigDecimal.ONE);
			newPaymentTiers.add(subscriptionPaymentTierDTO);
			subscriptionPaymentTierDTO = new SubscriptionPaymentTierDTO();
			subscriptionPaymentTierDTO.setMinimum(BigDecimal.TEN);
			subscriptionPaymentTierDTO.setMaximum(SubscriptionPaymentTier.MAXIMUM);
			subscriptionPaymentTierDTO.setPaymentAmount(BigDecimal.TEN);
			newPaymentTiers.add(subscriptionPaymentTierDTO);
			dto.setSubscriptionPaymentTierDTOs(newPaymentTiers);
			dto.setPaymentTierEffectiveDate(subscriptionService.findNextPossibleSubscriptionUpdateDate(employee.getCompany().getId()));

			configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), dto, true);
			Assert.assertTrue(configuration.isApproved());
			Assert.assertTrue(configuration.isActive());
			testPaymentTierEquality(configuration.getActiveSubscriptionFeeConfiguration().getSubscriptionPaymentTiers(), oldPaymentTiers);
			testSubscriptionIsOnApprovalQueue(subscriptionId, true);
			// Approve
			configuration = subscriptionService.approveSubscriptionConfiguration(configuration.getId());
			subscriptionService.updateSubscriptionConfigurationChanges(configuration.getId(), dto.getPaymentTierEffectiveDate());
			configuration = subscriptionService.findSubscriptionConfigurationById(configuration.getId());
			Assert.assertTrue(configuration.isApproved());
			Assert.assertTrue(configuration.isActive());
			testPaymentTierEquality(configuration.getActiveSubscriptionFeeConfiguration().getSubscriptionPaymentTiers(), newPaymentTiers);
			testSubscriptionIsOnApprovalQueue(subscriptionId, false);
			//
		}
		//
	}

	/**
	 * Expires a subscription
	 */
	protected void testSubscriptionApprovedToExpired(Long subscriptionId) {
		// Preconditions for this test=
		// Subscription exists, is approved and status is active
		Assert.assertNotNull(subscriptionId);
		SubscriptionConfiguration configuration = subscriptionService.findSubscriptionConfigurationById(subscriptionId);
		Assert.assertNotNull(configuration);

		Assert.assertTrue(configuration.isApproved());
		Assert.assertTrue(configuration.isActive());
		testSubscriptionIsOnApprovalQueue(subscriptionId, false);
		//
		// Subscription expires
		subscriptionService.updateSubscriptionConfigurationChanges(configuration.getId(), configuration.getEndDate());
		configuration = subscriptionService.findSubscriptionConfigurationById(subscriptionId);
		Assert.assertNotNull(configuration);
		Assert.assertTrue(configuration.isExpired());

		Company company = companyService.findCompanyById(configuration.getCompany().getId());
		Assert.assertTrue(company.getAccountPricingType().isTransactionalPricing());
	}

	protected void testSubscriptionIsOnApprovalQueue(long subscriptionId, boolean isOnApprovalQueue){
		SubscriptionConfigurationPagination pagination = new SubscriptionConfigurationPagination(true);
		pagination = subscriptionService.findAllPendingApprovalSubscriptionConfigurations(pagination);

		Assert.assertNotNull(pagination);
		if (isOnApprovalQueue)
			Assert.assertFalse(CollectionUtilities.isEmpty(pagination.getResults()));

		boolean isPendingApproval = false;
		for (SubscriptionConfiguration pendingApprovalConfiguration : pagination.getResults()){
			if (pendingApprovalConfiguration.getId().equals(subscriptionId)) {
				isPendingApproval = true;
				break;
			}
		}
		Assert.assertTrue(isPendingApproval == isOnApprovalQueue);
	}

	// } Subscription utilities

	// { Payment tier utilities

	protected void testPaymentTierEquality(List<SubscriptionPaymentTier> paymentTiers, List<SubscriptionPaymentTierDTO> paymentTierDTOs) {
		Assert.assertNotNull(paymentTiers);
		Assert.assertNotNull(paymentTierDTOs);
		Assert.assertEquals(paymentTiers.size(), paymentTierDTOs.size());
		for (int i = 0; i < paymentTiers.size(); i++) {
			testPaymentTierEquality(paymentTiers.get(i), paymentTierDTOs.get(i));
		}
	}

	void testPaymentTierEquality(SubscriptionPaymentTier paymentTier, SubscriptionPaymentTierDTO paymentTierDTO){
		Assert.assertTrue(paymentTier.getMaximum().compareTo(paymentTierDTO.getMaximum()) == 0);
		Assert.assertTrue(paymentTier.getMinimum().compareTo(paymentTierDTO.getMinimum()) == 0);
		Assert.assertTrue(paymentTier.getPaymentAmount().compareTo(paymentTierDTO.getPaymentAmount()) == 0);
		Assert.assertTrue(paymentTier.getVendorOfRecordAmount().compareTo(paymentTierDTO.getVendorOfRecordAmount()) == 0);
	}

	protected List<SubscriptionPaymentTierDTO> generateRandomPaymentTierDTOs(int minNumberOfTiers, int maxNumberOfTiers){
		Assert.assertTrue(minNumberOfTiers > 0);
		Assert.assertTrue(maxNumberOfTiers >= minNumberOfTiers);

		List<SubscriptionPaymentTierDTO> randomPaymentTiers = Lists.newArrayList();
		int numberOfTiers = rand.nextInt(maxNumberOfTiers) + minNumberOfTiers;
		int tierThroughput = 100 - rand.nextInt(maxNumberOfTiers) + minNumberOfTiers;
		int paymentAmount = 1000 - rand.nextInt(maxNumberOfTiers) + minNumberOfTiers * tierThroughput;
		for (int i = 0; i < numberOfTiers; i++) {
			SubscriptionPaymentTierDTO subscriptionPaymentTierDTO = new SubscriptionPaymentTierDTO();
			subscriptionPaymentTierDTO.setMinimum(BigDecimal.valueOf(tierThroughput).multiply(BigDecimal.valueOf(i)));
			subscriptionPaymentTierDTO.setMaximum(BigDecimal.valueOf(tierThroughput) .multiply(BigDecimal.valueOf(i + 1)));
			subscriptionPaymentTierDTO.setPaymentAmount(BigDecimal.valueOf(paymentAmount).multiply(BigDecimal.valueOf(i+1)));
			randomPaymentTiers.add(subscriptionPaymentTierDTO);
		}
		randomPaymentTiers.get(randomPaymentTiers.size() - 1).setMaximum(SubscriptionPaymentTier.MAXIMUM);
		return randomPaymentTiers;
	}

	// } Payment tier utilities

	// { Invoice utilities

	protected Work generateAndPayWork(long employeeId, User contractor) throws Exception {
		Work work = newWorkWithPaymentTerms(employeeId, 30);
		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));
		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));

		workService.completeWork(work.getId(), new CompleteWorkDTO());
		workService.closeWork(work.getId());

		Invoice invoice = billingService.findInvoiceByWorkId(work.getId());
		Assert.assertNotNull(invoice);
		Map<String, List<ConstraintViolation>> violations = billingService.payInvoice(employee.getId(), invoice.getId());
		Assert.assertTrue(violations.isEmpty());

		invoice = billingService.findInvoiceByWorkId(work.getId());
		Assert.assertEquals(invoice.getInvoiceStatusType().getCode(), InvoiceStatusType.PAID);
		Assert.assertEquals(invoice.getPaymentFulfillmentStatusType().getCode(), PaymentFulfillmentStatusType.FULFILLED);
		return work;
	}

	// } Invoice utilities

	// { AccountServiceType utilities

	protected List<AccountServiceTypeDTO> generateRandomAccountServiceTypes() {
		List<AccountServiceTypeDTO> serviceTypes = Lists.newArrayList();
		for (String countryId : Country.WM_SUPPORTED_COUNTRIES) {
			serviceTypes.add(new AccountServiceTypeDTO((rand.nextBoolean()) ? AccountServiceType.VENDOR_OF_RECORD : AccountServiceType.TAX_SERVICE_1099, countryId));
		}
		return serviceTypes;
	}

	void testAddOnEquality(List<SubscriptionAddOnTypeAssociation> subscriptionAddOns, List<SubscriptionAddOnDTO> subscriptionAddOnDTOs) {
		Assert.assertNotNull(subscriptionAddOns);
		Assert.assertNotNull(subscriptionAddOnDTOs);
		Assert.assertEquals(subscriptionAddOns.size(), subscriptionAddOns.size());
		for (int i = 0; i < subscriptionAddOns.size(); i++) {
			testAddOnEquality(subscriptionAddOns.get(i), subscriptionAddOnDTOs.get(i));
		}
	}

	void testAddOnEquality(SubscriptionAddOnTypeAssociation subscriptionAddOn, SubscriptionAddOnDTO subscriptionAddOnDTO){
		Assert.assertTrue(subscriptionAddOn.getCostPerPeriod().compareTo(subscriptionAddOnDTO.getCostPerPeriod()) == 0);
		Assert.assertTrue(DateUtilities.equals(subscriptionAddOn.getEffectiveDate(), subscriptionAddOnDTO.getEffectiveDate()));
		Assert.assertTrue(subscriptionAddOn.getSubscriptionAddOnType().getCode().equals(subscriptionAddOnDTO.getAddOnTypeCode()));
	}

	// }
}

