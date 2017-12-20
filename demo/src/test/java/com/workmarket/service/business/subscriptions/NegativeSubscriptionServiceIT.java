package com.workmarket.service.business.subscriptions;

import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionCancelDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionConfigurationDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionPaymentTierDTO;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import net.jcip.annotations.NotThreadSafe;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

/** <pre>
 * Includes most common invalid subscription configurations possible
 * All methods in this class should return a validation exception of some kind
 * If possible we should assert that all of them return the desired errors
 * </pre> */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@NotThreadSafe
public class NegativeSubscriptionServiceIT extends SubscriptionServiceBaseIT {
	@Test
	public void testSetUpFee() {
		// Negative setUpFee
		SubscriptionConfigurationDTO newSubscriptionDTO = new SubscriptionConfigurationDTO();
		BeanUtilities.copyProperties(newSubscriptionDTO, subscriptionDTO);
		newSubscriptionDTO.setSetUpFee(BigDecimal.valueOf(-1));
		try {
			subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), newSubscriptionDTO, true);
			Assert.fail();
		} catch (Exception exception) {}
	}

	@Test
	public void testNumberOfPeriods() {
		// Negative numberOfPeriods
		SubscriptionConfigurationDTO newSubscriptionDTO = new SubscriptionConfigurationDTO();
		BeanUtilities.copyProperties(newSubscriptionDTO, subscriptionDTO);
		newSubscriptionDTO.setNumberOfPeriods(-1);
		try {
			subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), newSubscriptionDTO, true);
			Assert.fail();
		} catch (Exception exception) {}

		// Zero numberOfPeriods
		newSubscriptionDTO = new SubscriptionConfigurationDTO();
		BeanUtilities.copyProperties(newSubscriptionDTO, subscriptionDTO);
		newSubscriptionDTO.setNumberOfPeriods(0);
		try {
			subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), newSubscriptionDTO, true);
			Assert.fail();
		} catch (Exception exception) {}
	}
	@Test
	public void testPaymentTiers() {

		// Negative payment amount
		SubscriptionConfigurationDTO newSubscriptionDTO = new SubscriptionConfigurationDTO();
		BeanUtilities.copyProperties(newSubscriptionDTO, subscriptionDTO);
		List<SubscriptionPaymentTierDTO> paymentTierDTOs = generateRandomPaymentTierDTOs(3, 5);
		for (SubscriptionPaymentTierDTO paymentTierDTO : paymentTierDTOs) {
			paymentTierDTO.setPaymentAmount(paymentTierDTO.getPaymentAmount().negate());
		}
		newSubscriptionDTO.setSubscriptionPaymentTierDTOs(paymentTierDTOs);
		try {
			subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), newSubscriptionDTO, true);
			Assert.fail();
		} catch (Exception exception) {}

		// Zero payment amount
		newSubscriptionDTO = new SubscriptionConfigurationDTO();
		BeanUtilities.copyProperties(newSubscriptionDTO, subscriptionDTO);
		paymentTierDTOs = generateRandomPaymentTierDTOs(3, 5);
		for (SubscriptionPaymentTierDTO paymentTierDTO : paymentTierDTOs) {
			paymentTierDTO.setPaymentAmount(BigDecimal.ZERO);
		}
		newSubscriptionDTO.setSubscriptionPaymentTierDTOs(paymentTierDTOs);
		try {
			subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), newSubscriptionDTO, true);
			Assert.fail();
		} catch (Exception exception) {}

		// Payment amount in descending order
		newSubscriptionDTO = new SubscriptionConfigurationDTO();
		BeanUtilities.copyProperties(newSubscriptionDTO, subscriptionDTO);
		paymentTierDTOs = generateRandomPaymentTierDTOs(4, 4);
		BigDecimal[] paymentAmounts = CollectionUtilities.newGenericArrayPropertyProjection(paymentTierDTOs, BigDecimal.class, "paymentAmount");
		CollectionUtils.reverseArray(paymentAmounts);
		for (int i = paymentTierDTOs.size() - 1; i >= 0; i--) {
			paymentTierDTOs.get(i).setPaymentAmount(paymentAmounts[i]);
		}
		newSubscriptionDTO.setSubscriptionPaymentTierDTOs(paymentTierDTOs);
		try {
			subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), newSubscriptionDTO, true);
			Assert.fail();
		} catch (Exception exception) {}

		// All tiers have the same upper bound
		newSubscriptionDTO = new SubscriptionConfigurationDTO();
		BeanUtilities.copyProperties(newSubscriptionDTO, subscriptionDTO);
		paymentTierDTOs = generateRandomPaymentTierDTOs(3, 5);
		BigDecimal bound = BigDecimal.valueOf(100);
		for (SubscriptionPaymentTierDTO paymentTierDTO : paymentTierDTOs) {
			paymentTierDTO.setMaximum(bound);
			if (paymentTierDTO.getMinimum().compareTo(BigDecimal.ZERO) != 0) {
				paymentTierDTO.setMinimum(bound);
			}
		}
		newSubscriptionDTO.setSubscriptionPaymentTierDTOs(paymentTierDTOs);
		try {
			subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), newSubscriptionDTO, true);
			Assert.fail();
		} catch (Exception exception) {}
	}

	// }

	// { Modify an unmodifiable subscription tests

	@Test
	public void modifyAnExpiredSubscription() {
		SubscriptionConfiguration subscription = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, true);
		Assert.assertNotNull(subscription);
		testSubscriptionPendingApprovalToApproved(subscription.getId());
		testSubscriptionApprovedToExpired(subscription.getId());
		try {
			subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), new SubscriptionConfigurationDTO(subscription), true);
			Assert.fail();
		} catch (Exception exception) {}
		try {
			subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), new SubscriptionConfigurationDTO(subscription), false);
			Assert.fail();
		} catch (Exception exception) {}
	}

	@Test
	public void modifyARejectedSubscription() {
		SubscriptionConfiguration subscription = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, true);
		Assert.assertNotNull(subscription);
		testRejectSubscription(subscription.getId());
		try {
			subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), new SubscriptionConfigurationDTO(subscription), true);
			Assert.fail();
		} catch (Exception exception) {}
		try {
			subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), new SubscriptionConfigurationDTO(subscription), false);
			Assert.fail();
		} catch (Exception exception) {}
	}

	@Test
	@Ignore
	public void modifyACancelledSubscription() {
		SubscriptionConfiguration subscription = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, true);
		Assert.assertNotNull(subscription);
		testSubscriptionPendingApprovalToApproved(subscription.getId());
		submitAndApproveCancelSubscription(subscription);
		subscriptionService.updateSubscriptionConfigurationChanges(subscription.getId(), subscriptionService.findSubscriptionConfigurationById(subscription.getId()).getSubscriptionCancellation().getEffectiveDate());
		try {
			subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), new SubscriptionConfigurationDTO(subscription), true);
			Assert.fail();
		} catch (Exception exception) {}
		try {
			subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), new SubscriptionConfigurationDTO(subscription), false);
			Assert.fail();
		} catch (Exception exception) {}
	}

	@Test
	public void testCancelAnExpiredSubscription(){
		SubscriptionConfiguration subscription = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, true);
		Assert.assertNotNull(subscription);
		testSubscriptionPendingApprovalToApproved(subscription.getId());
		testSubscriptionApprovedToExpired(subscription.getId());
		try {
			submitAndApproveCancelSubscription(subscription);
			Assert.fail();
		} catch (Exception exception) {}
	}

	@Test
	public void testCancelARejectedSubscription(){
		SubscriptionConfiguration subscription = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, true);
		Assert.assertNotNull(subscription);
		testRejectSubscription(subscription.getId());
		try {
			submitAndApproveCancelSubscription(subscription);
		Assert.fail();
		} catch (Exception exception) {}
	}

	@Test
	@Ignore
	public void testCancelACancelledSubscription(){
		SubscriptionConfiguration subscription = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, true);
		Assert.assertNotNull(subscription);
		testSubscriptionPendingApprovalToApproved(subscription.getId());
		submitAndApproveCancelSubscription(subscription);
		try {
			submitAndApproveCancelSubscription(subscription);
			Assert.fail();
		} catch (Exception exception) {}
	}

	@Test
	public void testRejectAnExpiredSubscription(){
		SubscriptionConfiguration subscription = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, true);
		Assert.assertNotNull(subscription);
		testSubscriptionPendingApprovalToApproved(subscription.getId());
		testSubscriptionApprovedToExpired(subscription.getId());
		try {
			subscriptionService.rejectSubscriptionConfiguration(subscription.getId());
			Assert.fail();
		} catch (Exception exception) {}
	}

	@Test
	public void testRejectARejectedSubscription(){
		SubscriptionConfiguration subscription = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, true);
		Assert.assertNotNull(subscription);
		testRejectSubscription(subscription.getId());
		try {
			subscriptionService.rejectSubscriptionConfiguration(subscription.getId());
			Assert.fail();
		} catch (Exception exception) {}
	}

	@Test
	@Ignore
	public void testRejectACancelledSubscription(){
		SubscriptionConfiguration subscription = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, true);
		Assert.assertNotNull(subscription);
		testSubscriptionPendingApprovalToApproved(subscription.getId());
		submitAndApproveCancelSubscription(subscription);
		subscriptionService.updateSubscriptionConfigurationChanges(subscription.getId(), subscriptionService.findSubscriptionConfigurationById(subscription.getId()).getSubscriptionCancellation().getEffectiveDate());
		try {
			subscriptionService.rejectSubscriptionConfiguration(subscription.getId());
			Assert.fail();
		} catch (Exception exception) {}
	}

	@Test
	public void testSubmitCancellationEffectiveAfterExpiration() {
		SubscriptionConfiguration subscription = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, true);
		Assert.assertNotNull(subscription);
		testSubscriptionPendingApprovalToApproved(subscription.getId());
		SubscriptionCancelDTO subscriptionCancelDTO = new SubscriptionCancelDTO();
		Calendar c = DateUtilities.cloneCalendar(subscription.getEndDate());
		c.add(Calendar.DAY_OF_MONTH, 5);
		subscriptionCancelDTO.setCancellationDate(c);
		subscriptionCancelDTO.setCancellationFee(30.00);
		try {
			subscriptionService.submitCancellationForSubscriptionConfiguration(subscription.getId(), subscriptionCancelDTO);
			Assert.fail();
		} catch (Exception exception) {}
	}

	// }
}
