package com.workmarket.service.business.pay;

import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.pricing.BlendedPerHourPricingStrategy;
import com.workmarket.domains.model.pricing.FlatPricePricingStrategy;
import com.workmarket.domains.model.pricing.InternalPricingStrategy;
import com.workmarket.domains.model.pricing.PerHourPricingStrategy;
import com.workmarket.domains.model.pricing.PerUnitPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.domains.model.summary.work.WorkMilestones;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.AbstractWorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkBonusNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkBudgetNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkExpenseNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.dto.PaymentSummaryDTO;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.domains.work.service.WorkMilestonesService;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.configuration.Constants;
import com.workmarket.utility.DateUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * Author: rocio
 */
@Service
public class PaymentSummaryServiceImpl implements PaymentSummaryService {

	@Autowired private TaxService taxService;
	@Autowired private PricingService pricingService;
	@Autowired private WorkService workService;
	@Autowired private WorkNegotiationService workNegotiationService;
	@Autowired private WorkMilestonesService workMilestonesService;

	@Override
	public PaymentSummaryDTO generatePaymentSummaryForWork(long workId) {
		Assert.notNull(workId);

		AbstractWork work = workService.findWork(workId, false);
		return generatePaymentSummaryForWork(work);
	}

	@Override
	public PaymentSummaryDTO generatePaymentSummaryForNegotiation(long negotiationId) {
		Assert.notNull(negotiationId);

		AbstractWorkNegotiation negotiation = workNegotiationService.findById(negotiationId);
		Assert.notNull(negotiation);
		Work work = workService.findWork(negotiation.getWork().getId(), false);
		Assert.notNull(work);

		AccountRegister accountRegister = pricingService.findDefaultRegisterForCompany(work.getCompany().getId());
		Assert.notNull(accountRegister);

		PricingStrategy pricingStrategy = null;
		if (negotiation instanceof WorkExpenseNegotiation) {
			pricingStrategy = ((WorkExpenseNegotiation) negotiation).getPricingStrategy();
		} else if (negotiation instanceof WorkBudgetNegotiation) {
			pricingStrategy = ((WorkBudgetNegotiation) negotiation).getPricingStrategy();
		} else if (negotiation instanceof WorkBonusNegotiation) {
			pricingStrategy = ((WorkBonusNegotiation) negotiation).getPricingStrategy();
		} else if (negotiation instanceof WorkNegotiation) {
			pricingStrategy = ((WorkNegotiation) negotiation).getPricingStrategy();
		}

		Assert.notNull(pricingStrategy);

		BigDecimal maxSpend = pricingService.calculateMaximumResourceCost(pricingStrategy);

		Assert.notNull(accountRegister);
		Assert.notNull(maxSpend);

		PaymentSummaryDTO payment = new PaymentSummaryDTO();
		payment.setMaxSpendLimit(maxSpend);
		payment.setBuyerFeePercentage(pricingService.getCurrentFeePercentageForWork(work));
		payment.setBuyerFeeBand(accountRegister.getWorkFeeLevel());

		BigDecimal buyerFee = pricingService.calculateBuyerNetMoneyFee(work, maxSpend);
		payment.setBuyerFee(buyerFee);
		payment.setTotalCost(maxSpend.add(buyerFee));

		return calculatePriceWithFee(pricingStrategy, work, payment);
	}

	@Override
	public PaymentSummaryDTO generatePaymentSummaryForWork(AbstractWork work) {
		Assert.notNull(work);
		AccountRegister accountRegister = pricingService.findDefaultRegisterForCompany(work.getCompany().getId());
		Assert.notNull(accountRegister);
		BigDecimal maxSpend = pricingService.calculateMaximumResourceCost(work.getPricingStrategy());
		Assert.notNull(maxSpend);

		// NOTE Removed "lane fee credit"
		PaymentSummaryDTO payment = new PaymentSummaryDTO();
		payment.setMaxSpendLimit(maxSpend);
		payment.setBuyerFeePercentage(pricingService.getCurrentFeePercentageForWork(work));
		payment.setBuyerFeeBand(accountRegister.getWorkFeeLevel());

		WorkResource resource = workService.findActiveWorkResource(work.getId());

		if (!(work instanceof Work) || resource == null) {
			BigDecimal buyerFee = pricingService.calculateBuyerNetMoneyFee(work, maxSpend);
			payment.setBuyerFee(buyerFee);
			payment.setTotalCost(maxSpend.add(buyerFee));
		} else {

			if (work.isComplete()) {
				BigDecimal actualSpend = pricingService.calculateTotalResourceCost((Work) work, resource);
				BigDecimal buyerFee = pricingService.calculateBuyerNetMoneyFee(work, actualSpend);

				Assert.notNull(actualSpend);
				payment.setActualSpendLimit(actualSpend);
				payment.setBuyerFee(buyerFee);
				payment.setTotalCost(actualSpend.add(buyerFee));

			} else if ((work.getFulfillmentStrategy() != null) && (work.isPaid() || work.isPaymentPending() || work.isInvoiced())) {
				Assert.notNull(work.getFulfillmentStrategy(), "FulfillmentStrategy is not set");

				BigDecimal actualSpend = work.getFulfillmentStrategy().getWorkPrice() != null ? work.getFulfillmentStrategy().getWorkPrice() : BigDecimal.ZERO;
				BigDecimal buyerFee = work.getFulfillmentStrategy().getBuyerFee() != null ? work.getFulfillmentStrategy().getBuyerFee() : BigDecimal.ZERO;

				payment.setActualSpendLimit(actualSpend);
				payment.setBuyerFee(buyerFee);
				payment.setTotalCost(actualSpend.add(buyerFee));

				if (work.isPaid()) {
					// Identity those assignments paid/closed prior to the switch to net-money,

					Calendar closedOn = null;
					if (work.getClosedOn() != null) {
						closedOn = (Calendar) work.getClosedOn().clone();
					}
					if (closedOn == null) {
						WorkMilestones milestones = workMilestonesService.findWorkMilestonesByWorkId(work.getId());
						if (milestones.getPaidOn() != null) {
							closedOn = (Calendar) milestones.getPaidOn().clone();
						}
					}

					payment.setPaidOn(closedOn);

					if (DateUtilities.isBefore(closedOn, DateUtilities.getCalendarFromISO8601(Constants.WM_NET_MONEY_SWITCHOVER_DATE))) {
						payment.setLegacyBuyerFee(true);
					}
				} else if (work.isPaymentPending() || work.isInvoiced()) {
					Calendar paymentDate = (Calendar) ((Work) work).getDueOn().clone();
					payment.setPaymentDueOn(paymentDate);
				}
			} else {
				BigDecimal buyerFee = pricingService.calculateBuyerNetMoneyFee(work, maxSpend);
				payment.setBuyerFee(buyerFee);
				payment.setTotalCost(maxSpend.add(buyerFee));
			}

			payment.setHoursWorked(resource.getHoursWorked());
			payment.setUnitsProcessed(resource.getUnitsProcessed());

			if (payment.getAdditionalExpenses() != null) {
				BigDecimal additionalExpenseFee = pricingService.calculateBuyerNetMoneyFee(work, payment.getAdditionalExpenses());
				payment.setAdditionalExpensesWithFee(payment.getAdditionalExpenses().add(additionalExpenseFee));
			}
			if (payment.getBonus() != null) {
				BigDecimal bonusFee = pricingService.calculateBuyerNetMoneyFee(work, payment.getBonus());
				payment.setBonusWithFee(payment.getBonus().add(bonusFee));
			}

			payment.setSalesTaxCollectedFlag(work.getPricingStrategy().getFullPricingStrategy().getSalesTaxCollectedFlag());
			payment.setSalesTaxCollected(taxService.calculateTaxAmount(work.getId()));
			payment.setSalesTaxRate(work.getPricingStrategy().getFullPricingStrategy().getSalesTaxRate());

			if (work.isPaid()) {
				payment.setPaidOn((Calendar) workMilestonesService.findWorkMilestonesByWorkId(work.getId()).getPaidOn().clone());
			} else if (work.isPaymentPending()) {
				Calendar paymentDate = (Calendar) ((Work) work).getDueOn().clone();
				payment.setPaymentDueOn(paymentDate);
			}
		}

		return calculatePriceWithFee(work.getPricingStrategy(), work, payment);
	}

	private PaymentSummaryDTO calculatePriceWithFee(PricingStrategy pricingStrategy, AbstractWork work, PaymentSummaryDTO paymentSummaryDTO) {
		Assert.notNull(pricingStrategy);

		if (pricingStrategy instanceof InternalPricingStrategy) {
			return paymentSummaryDTO;
		}

		if (pricingStrategy instanceof FlatPricePricingStrategy) {
			return paymentSummaryDTO;
		}

		if (pricingStrategy instanceof PerHourPricingStrategy) {
			PerHourPricingStrategy s = (PerHourPricingStrategy) pricingStrategy;
			BigDecimal perHourPrice = s.getPerHourPrice();
			if (perHourPrice != null) {
				BigDecimal feePerHour = pricingService.calculateBuyerNetMoneyFee(work, perHourPrice);
				paymentSummaryDTO.setPerHourPriceWithFee(perHourPrice.add(feePerHour));
				return paymentSummaryDTO;
			}
		}

		if (pricingStrategy instanceof PerUnitPricingStrategy) {
			PerUnitPricingStrategy s = (PerUnitPricingStrategy) pricingStrategy;
			BigDecimal perUnitPrice = s.getPerUnitPrice();
			if (perUnitPrice != null) {
				BigDecimal feePerUnit = pricingService.calculateBuyerNetMoneyFee(work, perUnitPrice);
				paymentSummaryDTO.setPerUnitPriceWithFee(perUnitPrice.add(feePerUnit));
				return paymentSummaryDTO;
			}
		}

		if (pricingStrategy instanceof BlendedPerHourPricingStrategy) {
			BlendedPerHourPricingStrategy s = (BlendedPerHourPricingStrategy) pricingStrategy;
			BigDecimal initialPerHourPrice = s.getInitialPerHourPrice();
			BigDecimal additionalPerHourPrice = s.getAdditionalPerHourPrice();

			if (initialPerHourPrice != null) {
				BigDecimal feePerInitialHour = pricingService.calculateBuyerNetMoneyFee(work, initialPerHourPrice);
				paymentSummaryDTO.setInitialPerHourPriceWithFee(initialPerHourPrice.add(feePerInitialHour));
			}

			if (additionalPerHourPrice != null) {
				BigDecimal feePerAdditionalHour = pricingService.calculateBuyerNetMoneyFee(work, additionalPerHourPrice);
				paymentSummaryDTO.setAdditionalPerHourPriceWithFee(additionalPerHourPrice.add(feePerAdditionalHour));
			}
		}
		return paymentSummaryDTO;
	}

}
