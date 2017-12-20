package com.workmarket.api.v2.worker.marshaller;

import com.workmarket.api.v2.worker.fulfillment.FulfillmentPayloadDTO;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.cache.PaymentCenterAggregateSummary;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.ForeignTaxEntity;
import com.workmarket.domains.model.tax.TaxVerificationStatusType;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.when;

public class FundsMarshallerTest {

	private FundsMarshaller fundsMarshaller;
	@Mock private MessageBundleHelper messageHelper;

	@Before
	public void setup() {

		messageHelper = mock(MessageBundleHelper.class);

		when
			(
				messageHelper.getMessage(FundsMarshaller.MESSAGE_BUNDLE_NO_US_TAX_ENTITY_KEY)
			)
			.thenReturn
			(
				"No US tax information was found"
			);

		when
			(
				messageHelper.getMessage(FundsMarshaller.MESSAGE_BUNDLE_NO_TAX_ENTITY_KEY)
			)
			.thenReturn
			(
				"No tax information was found"
			);

		when
			(
				messageHelper.getMessage(FundsMarshaller.MESSAGE_BUNDLE_TAX_ENTITY_NOT_VERIFIED_KEY)
			)
			.thenReturn
			(
				"US tax information has not been verified."
			);

		fundsMarshaller = new FundsMarshaller();

		fundsMarshaller.setMessageHelper(messageHelper);
	}

	@Test
	public void addAvailableBalanceToResponse_EmptyResponse_CreatesStructureAddsBalanceOk() {

		FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();
		response.setPayload(null);

		BigDecimal availableBalance = new BigDecimal(15678.92D);
		fundsMarshaller.addAvailableBalanceToGetFundsResponse(availableBalance, response);

		assertNotNull(response.getPayload());
		assertEquals(1, response.getPayload().size());

		Map resultMap = (Map) response.getPayload().get(0);

		assertEquals(15678.92D, resultMap.get(FundsMarshaller.RESULTS_AVAILABLE_BALANCE_KEY));
		assertNull(resultMap.get(FundsMarshaller.RESULTS_CAN_WITHDRAW_FUNDS_KEY));
		assertNull(resultMap.get(FundsMarshaller.RESULTS_TOTAL_EARNINGS_KEY));
		assertNull(resultMap.get(FundsMarshaller.RESULTS_CURRENT_RECEIVABLES_KEY));
		assertNull(resultMap.get(FundsMarshaller.RESULTS_PAST_DUE_RECEIVABLES_KEY));
	}

	@Test
	public void marshallFundsSummary_normalData_returnsExpectedResponse() {

		FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();

		PaymentCenterAggregateSummary fundsSummary = new PaymentCenterAggregateSummary();
		fundsSummary.setPastDue(new BigDecimal(1250.45D));
		fundsSummary.setUpcomingDue(new BigDecimal(2753.78D));
		fundsSummary.setPaidYtd(new BigDecimal(304D));

		fundsMarshaller.marshallFundsSummaryIntoGetFundsResponse(fundsSummary, response);

		assertNotNull(response.getPayload());
		assertEquals(1, response.getPayload().size());
		Map resultMap = (Map) response.getPayload().get(0);
		assertEquals(1250.45D, resultMap.get(FundsMarshaller.RESULTS_PAST_DUE_RECEIVABLES_KEY));
		assertEquals(2753.78D, resultMap.get(FundsMarshaller.RESULTS_CURRENT_RECEIVABLES_KEY));
		assertEquals(304D, resultMap.get(FundsMarshaller.RESULTS_TOTAL_EARNINGS_KEY));
		assertNull(resultMap.get(FundsMarshaller.RESULTS_AVAILABLE_BALANCE_KEY));
		assertNull(resultMap.get(FundsMarshaller.RESULTS_CAN_WITHDRAW_FUNDS_KEY));
	}

	@Test
	public void marshallTaxEntity_NoEntityUSAUser_messageAndCantWithdraw() {

		ExtendedUserDetails user = createUSUser();

		FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();

		fundsMarshaller.marshallTaxEntityIntoGetFundsResponse(user, null, response);

		assertNotNull(response.getPayload());
		assertEquals(1, response.getPayload().size());

		Map resultMap = (Map) response.getPayload().get(0);
		assertEquals(Boolean.FALSE, resultMap.get(FundsMarshaller.RESULTS_CAN_WITHDRAW_FUNDS_KEY));
		assertNull(resultMap.get(FundsMarshaller.RESULTS_AVAILABLE_BALANCE_KEY));
		assertNull(resultMap.get(FundsMarshaller.RESULTS_TOTAL_EARNINGS_KEY));
		assertNull(resultMap.get(FundsMarshaller.RESULTS_CURRENT_RECEIVABLES_KEY));
		assertNull(resultMap.get(FundsMarshaller.RESULTS_PAST_DUE_RECEIVABLES_KEY));

		assertEquals("No US tax information was found", response.getMessage());
	}

	@Test
	public void marshallTaxEntity_noEntityForeignUser_messageAndCantWithdraw() {

		ExtendedUserDetails user = createNonUSUser();

		FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();
		fundsMarshaller.marshallTaxEntityIntoGetFundsResponse(user, null, response);

		assertNotNull(response.getPayload());
		assertEquals(1, response.getPayload().size());

		Map resultMap = (Map) response.getPayload().get(0);
		assertEquals(Boolean.FALSE, resultMap.get(FundsMarshaller.RESULTS_CAN_WITHDRAW_FUNDS_KEY));
		assertEquals("No tax information was found", response.getMessage());
	}

	@Test
	public void marshallTaxEntity_approvedEntityUSUser_noMessageAndCanWithdraw() {

		ExtendedUserDetails user = createUSUser();

		AbstractTaxEntity taxEntity = new UsaTaxEntity();
		taxEntity.setStatus(new TaxVerificationStatusType(TaxVerificationStatusType.APPROVED));

		FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();
		fundsMarshaller.marshallTaxEntityIntoGetFundsResponse(user, taxEntity, response);

		assertNotNull(response.getPayload());
		assertEquals(1, response.getPayload().size());

		Map resultMap = (Map) response.getPayload().get(0);
		assertEquals(Boolean.TRUE, resultMap.get(FundsMarshaller.RESULTS_CAN_WITHDRAW_FUNDS_KEY));
		assertNull(response.getMessage());
	}

	@Test
	public void marshallTaxEntity_unApprovedEntityUSUser_messageAndCantWithdraw() {

		ExtendedUserDetails user = createUSUser();

		AbstractTaxEntity taxEntity = new UsaTaxEntity();
		taxEntity.setStatus(new TaxVerificationStatusType(TaxVerificationStatusType.UNVERIFIED));

		FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();
		fundsMarshaller.marshallTaxEntityIntoGetFundsResponse(user, taxEntity, response);

		assertNotNull(response.getPayload());
		assertEquals(1, response.getPayload().size());

		Map resultMap = (Map) response.getPayload().get(0);
		assertEquals(Boolean.FALSE, resultMap.get(FundsMarshaller.RESULTS_CAN_WITHDRAW_FUNDS_KEY));
		assertEquals("US tax information has not been verified.", response.getMessage());
	}

	@Test
	public void marshallTaxEntity_entityForeignUser_noMessageAndCanWithdraw() {

		ExtendedUserDetails user = createNonUSUser();

		AbstractTaxEntity taxEntity = new ForeignTaxEntity();

		FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();
		fundsMarshaller.marshallTaxEntityIntoGetFundsResponse(user, taxEntity, response);

		assertNotNull(response.getPayload());
		assertEquals(1, response.getPayload().size());

		Map resultMap = (Map) response.getPayload().get(0);
		assertEquals(Boolean.TRUE, resultMap.get(FundsMarshaller.RESULTS_CAN_WITHDRAW_FUNDS_KEY));
		assertNull(response.getMessage());
	}

	private ExtendedUserDetails createUSUser() {

		ExtendedUserDetails user = new ExtendedUserDetails("TESTID", "TESTPASSWORD", CollectionUtils.EMPTY_COLLECTION);
		user.setId(9999L);
		user.setCompanyId(543876L);
		user.setCountry(Country.USA);

		return user;
	}

	private ExtendedUserDetails createNonUSUser() {

		ExtendedUserDetails user = new ExtendedUserDetails("TESTID", "TESTPASSWORD", CollectionUtils.EMPTY_COLLECTION);
		user.setId(9998L);
		user.setCompanyId(543875L);
		user.setCountry(Country.CANADA);

		return user;
	}
}
