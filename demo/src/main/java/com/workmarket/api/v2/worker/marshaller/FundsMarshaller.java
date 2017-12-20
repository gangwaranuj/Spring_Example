package com.workmarket.api.v2.worker.marshaller;

import com.workmarket.api.v2.worker.fulfillment.FulfillmentPayloadDTO;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.cache.PaymentCenterAggregateSummary;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.web.helpers.MessageBundleHelper;
import edu.emory.mathcs.backport.java.util.LinkedList;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class FundsMarshaller
    extends ApiMarshaller {

    @Autowired private MessageBundleHelper messageHelper;

    protected static final String RESULTS_AVAILABLE_BALANCE_KEY = "availableToWithdraw";
    protected static final String RESULTS_TOTAL_EARNINGS_KEY = "totalEarnings";
    protected static final String RESULTS_PAST_DUE_RECEIVABLES_KEY = "pastDueReceivables";
    protected static final String RESULTS_CURRENT_RECEIVABLES_KEY = "currentReceivables";
    protected static final String RESULTS_CAN_WITHDRAW_FUNDS_KEY = "canWithdrawFunds";
    protected static final String RESULTS_AVAILABLE_FAST_FUNDS = "totalFastFundableAmount";

    protected static final String MESSAGE_BUNDLE_NO_US_TAX_ENTITY_KEY = "funds.withdraw.no_usa_taxentity.mobile";
    protected static final String MESSAGE_BUNDLE_NO_TAX_ENTITY_KEY = "funds.withdraw.no_taxentity.mobile";
    protected static final String MESSAGE_BUNDLE_TAX_ENTITY_NOT_VERIFIED_KEY = "funds.withdraw.unverified_taxentity.mobile";

    public void marshallTaxEntityIntoGetFundsResponse(ExtendedUserDetails user,
													  AbstractTaxEntity taxEntity,
                                                      FulfillmentPayloadDTO response) {

        if (user == null || response == null) {
            return;
        }

        Map resultsPayload = getResultsPayload(response);
        if (taxEntity == null) {
            if (Country.USA.equals(user.getCountry()) || Country.US.equals(user.getCountry())) {
                response.setMessage(messageHelper.getMessage(MESSAGE_BUNDLE_NO_US_TAX_ENTITY_KEY));
            } else {
                response.setMessage(messageHelper.getMessage(MESSAGE_BUNDLE_NO_TAX_ENTITY_KEY));
            }
            resultsPayload.put(RESULTS_CAN_WITHDRAW_FUNDS_KEY, Boolean.FALSE);
            return;
        }

        if (taxEntity instanceof UsaTaxEntity) {
            if (taxEntity.getStatus().isApproved()) {
                resultsPayload.put(RESULTS_CAN_WITHDRAW_FUNDS_KEY, Boolean.TRUE);
            } else {
                response.setMessage(messageHelper.getMessage(MESSAGE_BUNDLE_TAX_ENTITY_NOT_VERIFIED_KEY));
                resultsPayload.put(RESULTS_CAN_WITHDRAW_FUNDS_KEY, Boolean.FALSE);
            }
        } else {
            resultsPayload.put(RESULTS_CAN_WITHDRAW_FUNDS_KEY, Boolean.TRUE);
        }
    }

    public void addAvailableBalanceToGetFundsResponse(BigDecimal availableBalance,
													  FulfillmentPayloadDTO response) {

        Map resultsPayload = getResultsPayload(response);
        resultsPayload.put(RESULTS_AVAILABLE_BALANCE_KEY, availableBalance.doubleValue());
    }

    public void marshallFundsSummaryIntoGetFundsResponse(PaymentCenterAggregateSummary fundsSummary,
                                                         FulfillmentPayloadDTO response) {

        Map resultsPayload = getResultsPayload(response);
        resultsPayload.put(RESULTS_CURRENT_RECEIVABLES_KEY, fundsSummary.getUpcomingDue() != null ?
                           fundsSummary.getUpcomingDue().doubleValue() : null);
        resultsPayload.put(RESULTS_TOTAL_EARNINGS_KEY, fundsSummary.getPaidYtd() != null ?
                           fundsSummary.getPaidYtd().doubleValue() : null);
        resultsPayload.put(RESULTS_PAST_DUE_RECEIVABLES_KEY, fundsSummary.getPastDue() != null ?
                           fundsSummary.getPastDue().doubleValue() : null);
        resultsPayload.put(RESULTS_AVAILABLE_FAST_FUNDS, fundsSummary.getTotalFastFundableAmount() != null ?
            fundsSummary.getTotalFastFundableAmount().doubleValue() : null);
    }

    private Map getResultsPayload(FulfillmentPayloadDTO response) {
        if (response.getPayload() == null) {
            response.setPayload(new LinkedList());
        }
        if (CollectionUtils.isEmpty(response.getPayload())) {
            response.getPayload().add(new HashMap());
        }
        return (Map) response.getPayload().get(0);
    }

    /**
     * Utility method supports mock injection of this object for testing purposes
     */
    protected void setMessageHelper(MessageBundleHelper messageHelper) {
        this.messageHelper = messageHelper;
    }
}
