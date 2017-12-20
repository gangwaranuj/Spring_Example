package com.workmarket.service.thrift;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.pricing.BlendedPerHourPricingStrategy;
import com.workmarket.domains.model.pricing.FlatPricePricingStrategy;
import com.workmarket.domains.model.pricing.PerHourPricingStrategy;
import com.workmarket.domains.model.pricing.PerUnitPricingStrategy;
import com.workmarket.thrift.work.Work;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class WorkResponsePricingHelperImpl implements  WorkResponsePricingHelper {

	@Override
	public void normalizePricing(final Work work) {
		if (work.getConfiguration() != null && work.getConfiguration().isUseMaxSpendPricingDisplayModeFlag()) {

			BigDecimal price;
			BigDecimal percentage = BigDecimal.valueOf(work.getPayment().getBuyerFeePercentage()).movePointLeft(2);
			BigDecimal workFee = BigDecimal.valueOf(work.getPayment().getBuyerFeePercentage()).movePointLeft(2).add(BigDecimal.ONE);
			Long pricingId = work.getPricing().getId();

			if (new FlatPricePricingStrategy().getId().equals(pricingId)) {
				BigDecimal flatPrice = BigDecimal.valueOf(work.getPricing().getFlatPrice());

				if (flatPrice.multiply(percentage).compareTo(Constants.MAX_WORK_FEE) > 0)    // Is fee > MAX_WORK_FEE ?
					// price = flatPrice + MAX_WORK_FEE
					price = flatPrice.add(Constants.MAX_WORK_FEE);
				else
					// price = workFee * flatPrice
					price = workFee.multiply(BigDecimal.valueOf(work.getPricing().getFlatPrice())).setScale(2, BigDecimal.ROUND_HALF_UP);

				work.getPricing().setFlatPrice(price.doubleValue());
			} else if (new PerHourPricingStrategy().getId().equals(pricingId)) {
				// totalPerHourPrice = perHourPrice * maxNumberOfHours
				BigDecimal totalPerHourPrice = BigDecimal.valueOf(work.getPricing().getPerHourPrice() * work.getPricing().getMaxNumberOfHours());

				if (totalPerHourPrice.multiply(percentage).compareTo(Constants.MAX_WORK_FEE) > 0)    // Is fee > MAX_WORK_FEE ?
					// price = (totalPerHourPrice + MAX_WORK_FEE) / maxNumberOfHours
					price = totalPerHourPrice.add(Constants.MAX_WORK_FEE).divide(BigDecimal.valueOf(work.getPricing().getMaxNumberOfHours()), 8, RoundingMode.HALF_UP);
				else
					// price = workFee * perHourPrice
					price = workFee.multiply(BigDecimal.valueOf(work.getPricing().getPerHourPrice()));

				work.getPricing().setPerHourPrice(price.doubleValue());
			} else if (new PerUnitPricingStrategy().getId().equals(pricingId)) {
				// totalPerUnitPrice = perUnitPrice * maxNumberOfUnits
				BigDecimal totalPerUnitPrice = BigDecimal.valueOf(work.getPricing().getPerUnitPrice() * work.getPricing().getMaxNumberOfUnits());

				if (totalPerUnitPrice.multiply(percentage).compareTo(Constants.MAX_WORK_FEE) > 0)    // Is fee > MAX_WORK_FEE ?
					// price = (totalPerUnitPrice + MAX_WORK_FEE) / maxNumberOfUnits
					price = totalPerUnitPrice.add(Constants.MAX_WORK_FEE).divide(BigDecimal.valueOf(work.getPricing().getMaxNumberOfUnits()), 8, RoundingMode.HALF_UP);
				else
					// price = workFee * perUnitPrice
					price = workFee.multiply(BigDecimal.valueOf(work.getPricing().getPerUnitPrice()));

				work.getPricing().setPerUnitPrice(price.doubleValue());
			} else if (new BlendedPerHourPricingStrategy().getId().equals(pricingId)) {
				BigDecimal initialNumberOfHours = BigDecimal.valueOf(work.getPricing().getInitialNumberOfHours()),
					maxBlendedNumberOfHours = BigDecimal.valueOf(work.getPricing().getMaxBlendedNumberOfHours()),

					initialPerHourPrice = BigDecimal.valueOf(work.getPricing().getInitialPerHourPrice()),
					additionalPerHourPrice = BigDecimal.valueOf(work.getPricing().getAdditionalPerHourPrice());

				// totalBlendedPrice = (initialNumberOfHours * initialPerHourPrice) + (maxBlendedNumberOfHours * additionalPerHourPrice)
				BigDecimal totalBlendedPrice = initialNumberOfHours.multiply(initialPerHourPrice)
					.add(maxBlendedNumberOfHours.multiply(additionalPerHourPrice));

				if (totalBlendedPrice.multiply(percentage).compareTo(Constants.MAX_WORK_FEE) > 0) {        // Is fee > MAX_WORK_FEE ?
					// coefficient for scaling price = totalBlendedPrice / (totalBlendedPrice + MAX_WORK_FEE)
					BigDecimal coefficient = totalBlendedPrice.divide(totalBlendedPrice.add(Constants.MAX_WORK_FEE), 8, RoundingMode.HALF_UP);

					// Scale initialPerHourPrice: price = initialPerHourPrice / coefficient
					price = initialPerHourPrice.divide(coefficient, 8, RoundingMode.HALF_UP);
					work.getPricing().setInitialPerHourPrice(price.doubleValue());

					// Scale additionalPerHourPrice: price = additionalPerHourPrice / coefficient
					price = additionalPerHourPrice.divide(coefficient, 8, RoundingMode.HALF_UP);
					work.getPricing().setAdditionalPerHourPrice(price.doubleValue());
				} else {
					// new initial per hour price = workFee * initialPerHourPrice
					price = workFee.multiply(initialPerHourPrice);
					work.getPricing().setInitialPerHourPrice(price.doubleValue());

					// new additional per hour price = workFee * additionalPerHourPrice
					price = workFee.multiply(additionalPerHourPrice);
					work.getPricing().setAdditionalPerHourPrice(price.doubleValue());
				}
			}
		}
	}
}
