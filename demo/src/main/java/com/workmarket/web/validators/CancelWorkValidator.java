package com.workmarket.web.validators;

import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.work.model.CancellationReasonType;
import com.workmarket.service.business.dto.CancelWorkDTO;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;

import static com.workmarket.utility.StringUtilities.equalsAny;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class CancelWorkValidator implements Validator {

	@Autowired
	protected MessageBundleHelper messageHelper;

	@Override
	public boolean supports(Class<?> aClass) {
		return CancelWorkDTO.class == aClass;
	}

	@Override
	public void validate(Object o, Errors errors) {
		CancelWorkDTO dto = (CancelWorkDTO) o;

		if (isBlank(dto.getNote()))
			errors.rejectValue("note", "NotEmpty", new String[]{"Note"}, null);
		if (!equalsAny(dto.getCancellationReasonTypeCode(), CancellationReasonType.CANCELLATION_REASON_TYPES))
			errors.rejectValue("cancellationReasonTypeCode", "invalid_reason");
		if (dto.getPrice() == null || dto.getPrice() < 0D)
			errors.rejectValue("price", "price_positive");
	}

	public boolean getWorkCancelErrors(CancelWorkDTO cancelWorkDTO, com.workmarket.domains.work.model.Work work, MessageBundle bundle) {
		if (PricingStrategyType.FLAT.equals(work.getPricingStrategyType())) {
			BigDecimal totalPrice = work.getPricingStrategy().getFullPricingStrategy().getFlatPrice();
			return validateAndAddErrors(cancelWorkDTO.getPrice(), totalPrice, work.getWorkNumber(), bundle);
		} else if (PricingStrategyType.PER_UNIT.equals(work.getPricingStrategyType())) {
			BigDecimal pricePerUnit = work.getPricingStrategy().getFullPricingStrategy().getPerUnitPrice();
			BigDecimal totalUnits = work.getPricingStrategy().getFullPricingStrategy().getMaxNumberOfUnits();
			BigDecimal totalPrice = pricePerUnit.multiply(totalUnits);
			return validateAndAddErrors(cancelWorkDTO.getPrice(), totalPrice, work.getWorkNumber(), bundle);
		} else if (PricingStrategyType.PER_HOUR.equals(work.getPricingStrategyType())) {
			BigDecimal pricePerUnit = work.getPricingStrategy().getFullPricingStrategy().getPerHourPrice();
			BigDecimal totalUnits = work.getPricingStrategy().getFullPricingStrategy().getMaxNumberOfHours();
			BigDecimal totalPrice = pricePerUnit.multiply(totalUnits);
			return validateAndAddErrors(cancelWorkDTO.getPrice(), totalPrice, work.getWorkNumber(), bundle);
		} else if (PricingStrategyType.BLENDED_PER_HOUR.equals(work.getPricingStrategyType())) {
			BigDecimal initialPerHourPrice = work.getPricingStrategy().getFullPricingStrategy().getInitialPerHourPrice();
			BigDecimal initialNumberOfHours = work.getPricingStrategy().getFullPricingStrategy().getInitialNumberOfHours();
			BigDecimal additionalPerHourPrice = work.getPricingStrategy().getFullPricingStrategy().getAdditionalPerHourPrice();
			BigDecimal maxBlendedHours = work.getPricingStrategy().getFullPricingStrategy().getMaxBlendedNumberOfHours();
			BigDecimal totalPrice = initialPerHourPrice.multiply(initialNumberOfHours).add(additionalPerHourPrice.multiply(maxBlendedHours));
			return validateAndAddErrors(cancelWorkDTO.getPrice(), totalPrice, work.getWorkNumber(), bundle);
		} else if (PricingStrategyType.BLENDED_PER_UNIT.equals(work.getPricingStrategyType())) {
			BigDecimal initialPerUnitPrice = work.getPricingStrategy().getFullPricingStrategy().getInitialPerUnitPrice();
			BigDecimal initialNumberOfUnits = work.getPricingStrategy().getFullPricingStrategy().getInitialNumberOfUnits();
			BigDecimal additionalPerUnitPrice = work.getPricingStrategy().getFullPricingStrategy().getAdditionalPerUnitPrice();
			BigDecimal maxBlendedUnits = work.getPricingStrategy().getFullPricingStrategy().getMaxBlendedNumberOfUnits();
			BigDecimal totalPrice = initialPerUnitPrice.multiply(initialNumberOfUnits).add(additionalPerUnitPrice.multiply(maxBlendedUnits));
			return validateAndAddErrors(cancelWorkDTO.getPrice(), totalPrice, work.getWorkNumber(), bundle);
		}
		return true;
	}

	private boolean validateAndAddErrors(Double dtoPrice, BigDecimal assignmentPrice, String workNumber, MessageBundle bundle) {
		if (BigDecimal.valueOf(dtoPrice).compareTo(assignmentPrice) > 0) {
			messageHelper.addError(bundle, "assignment.bulk_cancel_works.suggested.cancel.refund.too.high", workNumber);
			return false;
		}
		return true;
	}
}
