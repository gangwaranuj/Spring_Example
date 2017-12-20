
package com.workmarket.service.business.subscriptions;

import com.google.common.collect.Lists;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.Pagination.SORT_DIRECTION;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.account.pricing.SubscriptionAccountServiceTypeConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfigurationPagination;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionFeeConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTier;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionType;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.dto.EmailAddressDTO;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.service.business.dto.account.pricing.AccountServiceTypeDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionConfigurationDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionPaymentTierDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionRenewalRequestDTO;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Author: rocio
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@NotThreadSafe
public class SubscriptionServiceIT extends SubscriptionServiceBaseIT {

	// { SIMPLE : These tests should run correctly for the COMPLEX tests to run

	@Test
	public void testSubmitAndApproveSubscription() throws Exception {
		// We don't have an active subscription yet
		Assert.assertNull(subscriptionService.findActiveSubscriptionConfigurationByCompanyId(employee.getCompany().getId()));

		// Our subscription will be pending approval now
		SubscriptionConfiguration configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, true);
		Assert.assertNotNull(configuration);
		Assert.assertTrue(configuration.isPendingApproval());
		Assert.assertTrue(configuration.isPending());
		Assert.assertFalse(configuration.isVerified());

		companyService.saveOrUpdateSubscriptionInvoicesEmailToCompany(employee.getCompany().getId(), Lists.newArrayList(new EmailAddressDTO("workmarket@workmarket.com")));
		// Our subscription will be approved and active now
		configuration = subscriptionService.approveSubscriptionConfiguration(configuration.getId());
		Assert.assertNotNull(configuration);
		Assert.assertTrue(configuration.isApproved());
		Assert.assertTrue(configuration.isActive());
		Assert.assertTrue(configuration.isVerified());

		// Our approved subscription and our active subscription are the same
		SubscriptionConfiguration activeConfiguration = subscriptionService.findActiveSubscriptionConfigurationByCompanyId(employee.getCompany().getId());
		Assert.assertNotNull(activeConfiguration);
		Assert.assertTrue(activeConfiguration.isApproved());
		Assert.assertTrue(activeConfiguration.isActive());
		Assert.assertTrue(configuration.isVerified());
		Assert.assertEquals(configuration.getId(), activeConfiguration.getId());
	}

	@Test
	public void testSubscriptionConfigurationClientRefId() throws Exception {
		// We don't have an active subscription yet
		Assert.assertNull(subscriptionService.findActiveSubscriptionConfigurationByCompanyId(employee.getCompany().getId()));

		// Our subscription will be pending approval now
		SubscriptionConfiguration configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, true);
		Assert.assertNotNull(configuration);
		Assert.assertEquals(configuration.getClientRefId(), SUBSCRIPTION_CLIENT_REF_ID);

		companyService.saveOrUpdateSubscriptionInvoicesEmailToCompany(employee.getCompany().getId(), Lists.newArrayList(new EmailAddressDTO("workmarket@workmarket.com")));
		// Our subscription will be approved and active now
		configuration = subscriptionService.approveSubscriptionConfiguration(configuration.getId());
		Assert.assertNotNull(configuration);
		Assert.assertEquals(configuration.getClientRefId(), SUBSCRIPTION_CLIENT_REF_ID);

		// Our approved subscription and our active subscription are the same
		SubscriptionConfiguration activeConfiguration = subscriptionService.findActiveSubscriptionConfigurationByCompanyId(employee.getCompany().getId());
		Assert.assertNotNull(activeConfiguration);
		Assert.assertEquals(configuration.getId(), activeConfiguration.getId());

	}

	@Test
	@Rollback
	public void testSubmitAndApproveSubscriptionWithDiscount() throws Exception {
		// We don't have an active subscription yet
		Assert.assertNull(subscriptionService.findActiveSubscriptionConfigurationByCompanyId(employee.getCompany().getId()));

		// Our subscription will be pending approval now
		SubscriptionConfigurationDTO dto = subscriptionDTO;
		dto.setDiscountedAmountPerPeriod(BigDecimal.TEN);
		dto.setDiscountedPeriods(2);
		SubscriptionConfiguration configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), dto, true);
		Assert.assertNotNull(configuration);
		Assert.assertTrue(configuration.isPendingApproval());
		Assert.assertTrue(configuration.isPending());
		Assert.assertFalse(configuration.isVerified());
		assertTrue(configuration.hasDiscount());

		// Our subscription will be approved and active now
		configuration = subscriptionService.approveSubscriptionConfiguration(configuration.getId());
		Assert.assertNotNull(configuration);
		Assert.assertTrue(configuration.isApproved());
		Assert.assertTrue(configuration.isActive());
		Assert.assertTrue(configuration.isVerified());

		List<EmailAddressDTO> emails = Lists.newArrayList(new EmailAddressDTO("rocio@workmarket.com"));
		companyService.saveOrUpdateSubscriptionInvoicesEmailToCompany(employee.getCompany().getId(), emails);
		// Our approved subscription and our active subscription are the same
		SubscriptionConfiguration activeConfiguration = subscriptionService.findActiveSubscriptionConfigurationByCompanyId(employee.getCompany().getId());
		Assert.assertNotNull(activeConfiguration);
		Assert.assertTrue(activeConfiguration.isApproved());
		Assert.assertTrue(activeConfiguration.isActive());
		Assert.assertTrue(configuration.isVerified());
		Assert.assertEquals(configuration.getId(), activeConfiguration.getId());

		List<SubscriptionPaymentPeriod> paymentPeriods = subscriptionService.findAllSubscriptionPaymentPeriods(activeConfiguration.getId());
		assertFalse(paymentPeriods.isEmpty());
		SubscriptionPaymentPeriod firstPaymentPeriod = paymentPeriods.get(0);
		assertNotNull(firstPaymentPeriod);
		assertTrue(firstPaymentPeriod.hasSubscriptionInvoice());
		authenticationService.setCurrentUser(employee.getId());
		SubscriptionInvoice invoice = billingService.findInvoiceById(firstPaymentPeriod.getSubscriptionInvoice().getId());
		assertEquals(invoice.getInvoiceLineItems().size(), 3);

		SubscriptionPaymentPeriod nextPaymentPeriod = subscriptionService.findNextNotInvoicedSubscriptionPaymentPeriod(activeConfiguration.getId());
		assertNotNull(nextPaymentPeriod);
		SubscriptionInvoice futureInvoice = subscriptionService.issueFutureSubscriptionInvoice(nextPaymentPeriod.getId());
		assertNotNull(futureInvoice);
		assertEquals(futureInvoice.getInvoiceLineItems().size(),2);
		activeConfiguration = subscriptionService.findActiveSubscriptionConfigurationByCompanyId(employee.getCompany().getId());
		assertFalse(activeConfiguration.isDiscountApplicable());

	}

	@Test
	@Rollback
	public void testSubmitAndApproveSubscriptionWithAutoRenewal() throws Exception {
		final int MAX_RENEWALS = 4;

		// We don't have an active subscription yet
		Assert.assertNull(subscriptionService.findActiveSubscriptionConfigurationByCompanyId(employee.getCompany().getId()));

		Random rand = new Random();
		Integer numberOfRenewals = rand.nextInt(MAX_RENEWALS-1) + 1 ;
		// Our subscription will be pending approval now
		SubscriptionConfigurationDTO dto = subscriptionDTO;
		dto.setNumberOfRenewals(numberOfRenewals);
		dto.setNumberOfRenewalPeriods(rand.nextInt(numberOfRenewals));
		SubscriptionConfiguration configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), dto, true);
		Assert.assertNotNull(configuration);
		Assert.assertTrue(configuration.isPendingApproval());
		Assert.assertTrue(configuration.isPending());
		Assert.assertFalse(configuration.isVerified());

		// Our subscription will be approved and active now
		configuration = subscriptionService.approveSubscriptionConfiguration(configuration.getId());
		Assert.assertNotNull(configuration);
		Assert.assertTrue(configuration.isApproved());
		Assert.assertTrue(configuration.isActive());
		Assert.assertTrue(configuration.isVerified());

		// Our approved subscription and our active subscription are the same
		SubscriptionConfiguration activeConfiguration = subscriptionService.findActiveSubscriptionConfigurationByCompanyId(employee.getCompany().getId());
		Assert.assertNotNull(activeConfiguration);
		Assert.assertTrue(activeConfiguration.isApproved());
		Assert.assertTrue(activeConfiguration.isActive());
		Assert.assertTrue(configuration.isVerified());
		Assert.assertEquals(configuration.getId(), activeConfiguration.getId());

		// The subscription has the given amount of renewals
		long subscriptionId = activeConfiguration.getId();
		for (int i = numberOfRenewals; i > 0; i--) {
			SubscriptionConfiguration renewSubscription = subscriptionService.findRenewSubscriptionConfiguration(subscriptionId);
			Assert.assertNotNull(renewSubscription);
			subscriptionId = renewSubscription.getId();
		}
	}

	@Test
	@Rollback
	public void testSubmitAndApproveSubscriptionWithManualRenewal() throws Exception {
		final int MAX_RENEWAL_PERDIODS = 4;

		// We don't have an active subscription yet
		Assert.assertNull(subscriptionService.findActiveSubscriptionConfigurationByCompanyId(employee.getCompany().getId()));

		// Our subscription will be pending approval now
		SubscriptionConfigurationDTO dto = subscriptionDTO;
		SubscriptionConfiguration configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), dto, true);
		Assert.assertNotNull(configuration);
		Assert.assertTrue(configuration.isPendingApproval());
		Assert.assertTrue(configuration.isPending());
		Assert.assertFalse(configuration.isVerified());

		// Our subscription will be approved and active now
		configuration = subscriptionService.approveSubscriptionConfiguration(configuration.getId());
		Assert.assertNotNull(configuration);
		Assert.assertTrue(configuration.isApproved());
		Assert.assertTrue(configuration.isActive());
		Assert.assertTrue(configuration.isVerified());

		// Our approved subscription and our active subscription are the same
		SubscriptionConfiguration activeConfiguration = subscriptionService.findActiveSubscriptionConfigurationByCompanyId(employee.getCompany().getId());
		Assert.assertNotNull(activeConfiguration);
		Assert.assertTrue(activeConfiguration.isApproved());
		Assert.assertTrue(activeConfiguration.isActive());
		Assert.assertTrue(configuration.isVerified());
		Assert.assertEquals(configuration.getId(), activeConfiguration.getId());
		this.testSubscriptionIsOnApprovalQueue(activeConfiguration.getId(), false);

		// Submit a renewal request
		SubscriptionRenewalRequestDTO renewalRequest = new SubscriptionRenewalRequestDTO();
		Random rand = new Random();
		Integer renewalPeriods = rand.nextInt(MAX_RENEWAL_PERDIODS) + 1;
		renewalRequest.setNumberOfPeriods(renewalPeriods);
		renewalRequest.setParentSubscriptionId(activeConfiguration.getId());
		// Slightly modify the payment tiers
		for (SubscriptionPaymentTierDTO paymentTierDTO: dto.getSubscriptionPaymentTierDTOs()) {
			SubscriptionPaymentTierDTO newPaymentTierDTO = new SubscriptionPaymentTierDTO();
			BeanUtilities.copyProperties(newPaymentTierDTO, paymentTierDTO);
			newPaymentTierDTO.setPaymentAmount(paymentTierDTO.getPaymentAmount().add(BigDecimal.TEN));
			renewalRequest.getSubscriptionPaymentTierDTOs().add(newPaymentTierDTO);
		}
		renewalRequest.setModifyPricing(true);
		subscriptionService.submitSubscriptionRenewalRequest(renewalRequest);
		this.testSubscriptionIsOnApprovalQueue(activeConfiguration.getId(), true);
		subscriptionService.approveSubscriptionConfiguration(activeConfiguration.getId());
		this.testSubscriptionIsOnApprovalQueue(activeConfiguration.getId(), false);
		SubscriptionConfiguration renewSubscription;
		for (int i=0; i<MAX_RENEWAL_PERDIODS;i++) {
			renewSubscription = subscriptionService.findRenewSubscriptionConfiguration(activeConfiguration.getId());
			Assert.assertNotNull(renewSubscription);
			testPaymentTierEquality(renewSubscription.getSubscriptionPaymentTiers(), renewalRequest.getSubscriptionPaymentTierDTOs());
		}
	}

	@Test
	@Rollback
	public void testEditPendingApprovalSubscriptionConfiguration() throws Exception {
		// Our subscription will be pending approval
		SubscriptionConfiguration configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, false);
		Assert.assertNotNull(configuration);

		SubscriptionConfigurationDTO newConfiguration = new SubscriptionConfigurationDTO(configuration);
		newConfiguration.setSubscriptionConfigurationId(configuration.getId());
		newConfiguration.setSubscriptionPeriod(SubscriptionPeriod.SEMIANNUAL);
		newConfiguration.setNumberOfPeriods(FOUR);
		newConfiguration.setSubscriptionPaymentTierDTOs(subscriptionDTO.getSubscriptionPaymentTierDTOs());
		newConfiguration.setSubscriptionTypeCode(subscriptionDTO.getSubscriptionTypeCode());

		// Our subscription will be modified now
		configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), newConfiguration, false);
		Assert.assertNotNull(configuration);
		Assert.assertEquals(SubscriptionPeriod.SEMIANNUAL, configuration.getSubscriptionPeriod());
		Assert.assertEquals(FOUR, newConfiguration.getNumberOfPeriods());

		newConfiguration = new SubscriptionConfigurationDTO(configuration);
		newConfiguration.setSubscriptionConfigurationId(configuration.getId());
		newConfiguration.setSubscriptionPeriod(SubscriptionPeriod.MONTHLY);
		newConfiguration.setNumberOfPeriods(THREE);

		// Our subscription will be modified now
		configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), newConfiguration, false);
		Assert.assertNotNull(configuration);
		Assert.assertEquals(SubscriptionPeriod.MONTHLY, configuration.getSubscriptionPeriod());
		Assert.assertEquals(THREE, newConfiguration.getNumberOfPeriods());
	}

	@Test
	@Rollback
	public void testEditApprovedSubscriptionConfigurationPaymentTiers() throws Exception {
		SubscriptionConfiguration configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, true);
		configuration = subscriptionService.approveSubscriptionConfiguration(configuration.getId());
		Assert.assertNotNull(configuration);
		Assert.assertTrue(configuration.isApproved());

		SubscriptionConfigurationDTO newConfiguration = new SubscriptionConfigurationDTO(configuration);
		newConfiguration.setSubscriptionConfigurationId(configuration.getId());

		List<SubscriptionPaymentTierDTO> newPaymentTiers = Lists.newArrayList();
		SubscriptionPaymentTierDTO subscriptionPaymentTierDTO = new SubscriptionPaymentTierDTO();
		subscriptionPaymentTierDTO.setMinimum(BigDecimal.ZERO);
		subscriptionPaymentTierDTO.setPaymentAmount(BigDecimal.ONE);
		newPaymentTiers.add(subscriptionPaymentTierDTO);

		configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), newConfiguration, false);
		Assert.assertNotNull(configuration);
		Assert.assertNotNull(configuration.getActiveSubscriptionFeeConfiguration());
		Assert.assertTrue(configuration.isApproved());

		this.testPaymentTierEquality(configuration.getActiveSubscriptionFeeConfiguration().getSubscriptionPaymentTiers(), subscriptionDTO.getSubscriptionPaymentTierDTOs());
		Assert.assertTrue(configuration.isApproved());


		Set<SubscriptionFeeConfiguration> feeConfigurations = configuration.getSubscriptionFeeConfigurations();
		Assert.assertNotNull(feeConfigurations);

		for (SubscriptionFeeConfiguration feeConfiguration : feeConfigurations) {
			if (feeConfiguration.isApproved()) {
				Assert.assertTrue(feeConfiguration.getActive());
				this.testPaymentTierEquality(feeConfiguration.getSubscriptionPaymentTiers(), subscriptionDTO.getSubscriptionPaymentTierDTOs());
			} else if (feeConfiguration.isPendingApproval()) {
				Assert.assertFalse(feeConfiguration.getActive());
				this.testPaymentTierEquality(feeConfiguration.getSubscriptionPaymentTiers(), newPaymentTiers);
			} else {
				Assert.fail("Invalid payment fee configuration approval status");
			}
		}
	}

	@Ignore
	@Test
	@Rollback
	public void testSubmitAndApproveCancelSubscription() throws Exception {
		SubscriptionConfiguration configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, true);
		subscriptionService.approveSubscriptionConfiguration(configuration.getId());

		this.submitAndApproveCancelSubscription(configuration);
	}

	@Test
	@Rollback
	public void testSubmitAndRejectCancelSubscription() throws Exception {
		SubscriptionConfiguration configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, true);
		subscriptionService.approveSubscriptionConfiguration(configuration.getId());

		this.submitAndRejectCancelSubscription(configuration);
	}

	@Ignore
	@Test
	@Rollback
	public void testSubmitRejectResubmitAndApproveCancelSubscription() throws Exception {
		SubscriptionConfiguration configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, true);
		subscriptionService.approveSubscriptionConfiguration(configuration.getId());

		this.submitAndRejectCancelSubscription(configuration);
		this.submitAndApproveCancelSubscription(configuration);
	}

	@Test
	@Rollback
	public void testAddNoteToSubscriptionConfiguration() throws Exception {
		SubscriptionConfiguration configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, false);
		NoteDTO noteDTO = new NoteDTO();
		noteDTO.setContent("This is a Note");
		subscriptionService.addNoteToSubscriptionConfiguration(configuration.getId(), noteDTO);
		configuration = subscriptionService.findSubscriptionConfigurationById(configuration.getId());
		Assert.assertTrue(configuration.hasNotes());
	}


	@Test
	public void testFindAllSubscriptionConfigurations() throws Exception {
		SubscriptionConfigurationPagination pagination = new SubscriptionConfigurationPagination(false);
		pagination.setResultsLimit(Pagination.MAX_ROWS);
		pagination.setSortColumn(SubscriptionConfigurationPagination.SORTS.EFFECTIVE_DATE.getColumn());
		pagination.setSortDirection(SORT_DIRECTION.DESC);
		Assert.assertNotNull(subscriptionService.findAllSubscriptionConfigurations(pagination));

		Calendar previousEffectiveDate = null;
		if (pagination.getResults() != null) {
			@SuppressWarnings("unchecked")
			List<Calendar> effectiveDates = CollectionUtilities.newListPropertyProjection(pagination.getResults(), "effectiveDate");
			for (Calendar effectiveDate : effectiveDates) {
				if (previousEffectiveDate != null && effectiveDate.compareTo(previousEffectiveDate) > 0) {
					Assert.assertTrue(effectiveDate.compareTo(previousEffectiveDate) <= 0);
				}
				previousEffectiveDate = effectiveDate;
			}
		}
	}

	@Test
	@Rollback
	public void testFindPendingApprovalSubscriptionConfigurations() throws Exception {
		SubscriptionConfiguration configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, true);

		SubscriptionConfigurationPagination pagination = new SubscriptionConfigurationPagination(false);
		pagination.setResultsLimit(Pagination.MAX_ROWS);
		pagination = subscriptionService.findAllPendingApprovalSubscriptionConfigurations(pagination);

		Assert.assertNotNull(pagination);
		Assert.assertFalse(CollectionUtilities.isEmpty(pagination.getResults()));
		boolean isPendingApproval = false;
		for (SubscriptionConfiguration pendingApprovalConfiguration : pagination.getResults()){
			if (pendingApprovalConfiguration.getId().equals(configuration.getId())) {
				isPendingApproval = true;
			}
		}
		Assert.assertTrue(isPendingApproval);

		configuration = subscriptionService.approveSubscriptionConfiguration(configuration.getId());
		Assert.assertTrue(configuration.isApproved());

		SubscriptionConfigurationDTO newConfiguration = new SubscriptionConfigurationDTO(configuration);
		newConfiguration.setPaymentTierEffectiveDate(configuration.getNextPaymentPeriodStartDate());
		newConfiguration.setSubscriptionConfigurationId(configuration.getId());

		List<SubscriptionPaymentTierDTO> newPaymentTiers = Lists.newArrayList();
		SubscriptionPaymentTierDTO subscriptionPaymentTierDTO = new SubscriptionPaymentTierDTO();
		subscriptionPaymentTierDTO.setMinimum(BigDecimal.ZERO);
		subscriptionPaymentTierDTO.setMaximum(SubscriptionPaymentTier.MAXIMUM);
		subscriptionPaymentTierDTO.setPaymentAmount(BigDecimal.ONE);
		newPaymentTiers.add(subscriptionPaymentTierDTO);
		newConfiguration.setSubscriptionPaymentTierDTOs(newPaymentTiers);

		configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), newConfiguration, true);
		Set<SubscriptionFeeConfiguration> paymentConfigurations = configuration.getSubscriptionFeeConfigurations();
		Assert.assertNotNull(paymentConfigurations);

		for (SubscriptionFeeConfiguration paymentConfiguration : paymentConfigurations) {
			if (paymentConfiguration.isApproved()) {
				List<SubscriptionPaymentTier> paymentTiers = paymentConfiguration.getSubscriptionPaymentTiers();
				Assert.assertTrue(paymentTiers.size() == 1);
				Assert.assertTrue(BigDecimal.TEN.compareTo(paymentTiers.get(0).getPaymentAmount()) == 0);
			} else if (paymentConfiguration.isPendingApproval()) {
				List<SubscriptionPaymentTier> paymentTiers = paymentConfiguration.getSubscriptionPaymentTiers();
				Assert.assertTrue(paymentTiers.size() == 1);
				Assert.assertTrue(BigDecimal.ONE.compareTo(paymentTiers.get(0).getPaymentAmount()) == 0);
			}
		}

		pagination = new SubscriptionConfigurationPagination(false);
		pagination.setResultsLimit(Pagination.MAX_ROWS);
		pagination = subscriptionService.findAllPendingApprovalSubscriptionConfigurations(pagination);

		Assert.assertNotNull(pagination);
		Assert.assertFalse(CollectionUtilities.isEmpty(pagination.getResults()));
		isPendingApproval = false;
		for (SubscriptionConfiguration pendingApprovalConfiguration : pagination.getResults()){
			if (pendingApprovalConfiguration.getId().equals(configuration.getId())) {
				isPendingApproval = true;
			}
		}
		Assert.assertTrue(isPendingApproval);

		configuration = subscriptionService.approveSubscriptionConfiguration(configuration.getId());
		subscriptionService.updateSubscriptionConfigurationChanges(configuration.getId(), newConfiguration.getPaymentTierEffectiveDate());
		configuration = subscriptionService.findSubscriptionConfigurationById(configuration.getId());
		Assert.assertTrue(configuration.isApproved());
		paymentConfigurations = configuration.getSubscriptionFeeConfigurations();
		Assert.assertNotNull(paymentConfigurations);

		for (SubscriptionFeeConfiguration paymentConfiguration : paymentConfigurations) {
			Assert.assertTrue(paymentConfiguration.isApproved() || paymentConfiguration.isRemoved());
			if (!paymentConfiguration.getActive()) {
				List<SubscriptionPaymentTier> paymentTiers = paymentConfiguration.getSubscriptionPaymentTiers();
				Assert.assertTrue(paymentTiers.size() == 1);
				Assert.assertTrue(BigDecimal.TEN.compareTo(paymentTiers.get(0).getPaymentAmount()) == 0);
			} else if (paymentConfiguration.getActive()) {
				List<SubscriptionPaymentTier> paymentTiers = paymentConfiguration.getSubscriptionPaymentTiers();
				Assert.assertTrue(paymentTiers.size() == 1);
				Assert.assertTrue(BigDecimal.ONE.compareTo(paymentTiers.get(0).getPaymentAmount()) == 0);
			}

		}
	}

	@Test
	@Rollback
	public void testApproveSubscriptionConfiguration() throws Exception {
		SubscriptionConfiguration configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, true);
		subscriptionService.approveSubscriptionConfiguration(configuration.getId());
		List<SubscriptionPaymentPeriod> subscriptionPaymentPeriods = subscriptionService.findAllSubscriptionPaymentPeriods(configuration.getId());
		Assert.assertNotNull(subscriptionPaymentPeriods);
		Assert.assertEquals(subscriptionPaymentPeriods.size(), subscriptionDTO.getNumberOfPeriods().intValue());

		SubscriptionPaymentPeriod paymentPeriod = subscriptionService.findNextNotInvoicedSubscriptionPaymentPeriod(configuration.getId());
		Assert.assertNotNull(paymentPeriod);
		Assert.assertEquals(subscriptionService.findNextPossibleSubscriptionUpdateDate(employee.getCompany().getId()).getTimeInMillis(), paymentPeriod.getPeriodDateRange().getFrom().getTimeInMillis());

	}

	@Test
	@Rollback
	public void testFeeForCompanyWithSubscription() throws Exception {
		SubscriptionConfiguration configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, true);
		subscriptionService.approveSubscriptionConfiguration(configuration.getId());
		// Change the company account pricing type as if it were the subscription's effectiveDate
		subscriptionService.updateApprovedSubscriptionsPricingType(configuration.getEffectiveDate());

		Work work = newWorkWithPaymentTerms(employee.getId(), 30);
		BigDecimal fee = pricingService.calculateBuyerNetMoneyFee(work, BigDecimal.valueOf(100));
		Assert.assertTrue(fee.compareTo(BigDecimal.ZERO) == 0);
	}

	@Test
	public void testSubscriptionAccountServiceType() throws Exception {
		SubscriptionConfiguration configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, false);

		SubscriptionConfigurationDTO newConfiguration = new SubscriptionConfigurationDTO();
		BeanUtilities.copyProperties(newConfiguration, subscriptionDTO);
		newConfiguration.setAccountServiceTypeDTOs(generateRandomAccountServiceTypes());

		configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), newConfiguration, false);

		for (AccountServiceTypeDTO serviceTypeDTO : newConfiguration.getAccountServiceTypeDTOs()) {
    		for (SubscriptionAccountServiceTypeConfiguration serviceTypeConfiguration : configuration.getAccountServiceTypeConfigurations()) {
    			if (serviceTypeDTO.getCountryCode().equals(serviceTypeConfiguration.getCountry().getId())) {
    				assertEquals(serviceTypeDTO.getAccountServiceTypeCode(), serviceTypeConfiguration.getAccountServiceType().getCode());
    			}
    		}
		}

		subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, false);

		newConfiguration = new SubscriptionConfigurationDTO();
		BeanUtilities.copyProperties(newConfiguration, subscriptionDTO);
		newConfiguration.setAccountServiceTypeDTOs(generateRandomAccountServiceTypes());

		configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), newConfiguration, true);
		configuration = subscriptionService.approveSubscriptionConfiguration(configuration.getId());

		for (AccountServiceTypeDTO serviceTypeDTO : newConfiguration.getAccountServiceTypeDTOs()) {
    		for (SubscriptionAccountServiceTypeConfiguration serviceTypeConfiguration : configuration.getAccountServiceTypeConfigurations()) {
    			if (serviceTypeDTO.getCountryCode().equals(serviceTypeConfiguration.getCountry().getId())) {
    				assertEquals(serviceTypeDTO.getAccountServiceTypeCode(), serviceTypeConfiguration.getAccountServiceType().getCode());
    			}
    		}
		}
	}

	@Test
	public void testMboAccountServiceType() throws Exception {
		SubscriptionConfiguration configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, false);

		SubscriptionConfigurationDTO newConfiguration = new SubscriptionConfigurationDTO();
		BeanUtilities.copyProperties(newConfiguration, subscriptionDTO);
		newConfiguration.setAccountServiceTypeDTOs(Lists.newArrayList(new AccountServiceTypeDTO(AccountServiceType.MBO, Country.USA)));

		configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), newConfiguration, false);

		for (AccountServiceTypeDTO serviceTypeDTO : newConfiguration.getAccountServiceTypeDTOs()) {
			for (SubscriptionAccountServiceTypeConfiguration serviceTypeConfiguration : configuration.getAccountServiceTypeConfigurations()) {
				if (serviceTypeDTO.getCountryCode().equals(serviceTypeConfiguration.getCountry().getId())) {
					assertEquals(serviceTypeDTO.getAccountServiceTypeCode(), serviceTypeConfiguration.getAccountServiceType().getCode());
				}
			}
		}
	}

	// } SIMPLE

	// { COMPLEX

	@Test
	@Rollback
	@Ignore
	public void testSubscriptionConfigurationLifecycle(){
		SubscriptionConfiguration configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, false);
		Assert.assertNotNull(configuration);

		this.testSubscriptionNotReadyToPendingApproval(configuration.getId());
		this.testSubscriptionPendingApprovalToApproved(configuration.getId());
		this.testSubscriptionApprovedToApprovedButModified(configuration.getId());
		this.testSubscriptionApprovedToExpired(configuration.getId());
	}


	// TODO : Consider moving this to the incremental subscription service test
	// { Invoices tests

	@Test
	public void testIssueFutureInvoice() {
		Assert.assertNull(subscriptionService.findActiveSubscriptionConfigurationByCompanyId(employee.getCompany().getId()));

		SubscriptionConfiguration configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, true);
		Assert.assertNotNull(configuration);

		configuration = subscriptionService.approveSubscriptionConfiguration(configuration.getId());
		Assert.assertNotNull(configuration);

		SubscriptionPaymentPeriod paymentPeriod = subscriptionService.findNextNotInvoicedSubscriptionPaymentPeriod(configuration.getId());
		Assert.assertNotNull(paymentPeriod);
		Assert.assertTrue(configuration.getEffectiveDate().compareTo(paymentPeriod.getPeriodDateRange().getFrom()) <= 0);

		Assert.assertNotNull(subscriptionService.issueFutureSubscriptionInvoice(paymentPeriod.getId()));
	}

	@Test
	public void testIssueInvoices() throws Exception {

		// Save and approve a subscription with a random number of tiers between minNumberOfTiers and maxNumberOfTiers
		int minNumberOfTiers = 3;
		int maxNumberOfTiers = 7;

		SubscriptionConfigurationDTO newConfiguration = new SubscriptionConfigurationDTO();
		BeanUtilities.copyProperties(newConfiguration, subscriptionDTO);
		newConfiguration.setSubscriptionPaymentTierDTOs(generateRandomPaymentTierDTOs(minNumberOfTiers, maxNumberOfTiers));
		newConfiguration.setEffectiveDate(DateUtilities.getCalendarWithFirstDayOfTheMonth(DateUtilities.getMidnightNextMonth(), Constants.EST_TIME_ZONE));
		SubscriptionConfiguration configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), newConfiguration, true);
		configuration = subscriptionService.approveSubscriptionConfiguration(configuration.getId());
		//

		// TODO(sgomez): Is the subscription regular invoice been issued?

		// Change into subscription mode
		subscriptionService.updateSubscriptionConfigurationChanges(configuration.getId(), configuration.getEffectiveDate());

		// Issue future invoices
		for (int i = 0; i < minNumberOfTiers; i++) {
    		SubscriptionPaymentPeriod paymentPeriod = subscriptionService.findNextNotInvoicedSubscriptionPaymentPeriod(configuration.getId());
    		if (paymentPeriod == null) break;
    		Assert.assertNotNull(subscriptionService.issueFutureSubscriptionInvoice(paymentPeriod.getId()));
    	}

		// Generate work to issue incremental invoices with a random work pricing
		long activeFeeConfigurationId = configuration.getActiveSubscriptionFeeConfiguration().getId();
		authenticationService.setCurrentUser(employee);
    	for (int i = 0; i < maxNumberOfTiers; i++) {
    		BigDecimal workPricing = null;
    		if (i < minNumberOfTiers) {
    			workPricing = BigDecimal.valueOf(newConfiguration.getSubscriptionPaymentTierDTOs().get(i).getMaximum().longValue() * i + 1L);
    		} else {
    			workPricing = BigDecimal.valueOf(newConfiguration.getSubscriptionPaymentTierDTOs().get(minNumberOfTiers - 1).getMinimum().longValue() * i + 1L);
    		}
    		this.generateAndPayWork(employee.getId(), contractor);

    	}

	}

	@Test
	@Rollback
	public void testSubscriptionRevenueQuarterly() throws Exception {
		logger.info("addAttachmentsEventHandler");
		// We don't have an active subscription yet
		Assert.assertNull(subscriptionService.findActiveSubscriptionConfigurationByCompanyId(employee.getCompany().getId()));

		// Our subscription will be pending approval now
		SubscriptionConfigurationDTO dto = subscriptionDTO;
		dto.setDiscountedAmountPerPeriod(BigDecimal.TEN);
		dto.setDiscountedPeriods(2);
		dto.setSubscriptionTypeCode(SubscriptionType.BLOCK);
		dto.setBlockTierPercentage(BigDecimal.ONE);
		SubscriptionConfiguration configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), dto, true);
		Assert.assertNotNull(configuration);
		Assert.assertTrue(configuration.isPendingApproval());
		Assert.assertTrue(configuration.isPending());
		Assert.assertFalse(configuration.isVerified());
		assertTrue(configuration.hasDiscount());

		// Our subscription will be approved and active now
		configuration = subscriptionService.approveSubscriptionConfiguration(configuration.getId());
		Assert.assertNotNull(configuration);
		Assert.assertTrue(configuration.isApproved());
		Assert.assertTrue(configuration.isActive());
		Assert.assertTrue(configuration.isVerified());

		// Our approved subscription and our active subscription are the same
		SubscriptionConfiguration activeConfiguration = subscriptionService.findActiveSubscriptionConfigurationByCompanyId(employee.getCompany().getId());
		Assert.assertNotNull(activeConfiguration);
		Assert.assertTrue(activeConfiguration.isApproved());
		Assert.assertTrue(activeConfiguration.isActive());
		Assert.assertTrue(configuration.isVerified());
		Assert.assertEquals(configuration.getId(), activeConfiguration.getId());

		SubscriptionFeeConfiguration feeConfiguration = activeConfiguration.getActiveSubscriptionFeeConfiguration();
		assertEquals(subscriptionDTO.getSubscriptionTypeCode(), feeConfiguration.getSubscriptionType().getCode());
		assertEquals(subscriptionDTO.getBlockTierPercentage().intValue(), feeConfiguration.getBlockTierPercentage().intValue());

		List<SubscriptionPaymentPeriod> paymentPeriods = subscriptionService.findAllSubscriptionPaymentPeriods(activeConfiguration.getId());
		assertFalse(paymentPeriods.isEmpty());
		SubscriptionPaymentPeriod firstPaymentPeriod = paymentPeriods.get(0);
		assertNotNull(firstPaymentPeriod);
		assertTrue(firstPaymentPeriod.hasSubscriptionInvoice());

		for (SubscriptionPaymentPeriod paymentPeriod: paymentPeriods) {
			Map<Calendar, BigDecimal> revenueDates = accountPricingService.calculateSubscriptionRevenueEffectiveDates(paymentPeriod, BigDecimal.valueOf(99));
			assertTrue(revenueDates.size() == 3);
			Calendar firstRevenueDate = DateUtilities.cloneCalendar(paymentPeriod.getPeriodDateRange().getFrom());
			assertTrue(revenueDates.containsKey(DateUtilities.getCalendarWithLastDayOfTheMonthWithMinimumTime(firstRevenueDate, TimeZone.getTimeZone("UTC"))));
			for (Map.Entry<Calendar, BigDecimal> entry : revenueDates.entrySet()) {
				assertTrue(entry.getValue().compareTo(BigDecimal.valueOf(33)) == 0);
			}
		}
	}
	// } Invoices tests

	// } COMPLEX
}

