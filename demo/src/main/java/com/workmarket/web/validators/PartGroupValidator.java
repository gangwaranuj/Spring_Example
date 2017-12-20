package com.workmarket.web.validators;

import ch.lambdaj.group.Group;
import com.google.common.collect.Lists;
import com.workmarket.domains.work.model.part.ShippingDestinationType;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.business.dto.PartGroupDTO;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;

import static ch.lambdaj.Lambda.group;
import static ch.lambdaj.Lambda.by;
import static ch.lambdaj.Lambda.on;

@Component
public class PartGroupValidator implements Validator {

	@Autowired private AddressWorkValidator addressWorkValidator;
	@Autowired private PartValidator partValidator;
	@Autowired private MessageBundleHelper messageHelper;

	@Override
	public boolean supports(Class<?> aClass) {
		return PartGroupDTO.class.isAssignableFrom(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		PartGroupDTO partGroup = (PartGroupDTO)o;
		if (!partGroup.isSuppliedByWorker()) {
			if (partGroup.isSetShippingDestinationType()) {
				if (ShippingDestinationType.PICKUP.equals(partGroup.getShippingDestinationType())) {
					if (partGroup.hasShipToLocation()) {
						addLocationErrors(errors, "shipToLocation", partGroup.getShipToLocation());
					} else {
						errors.rejectValue("shipToLocation", "NotNull",  messageHelper.getMessage("partsAndLogistics.shipToLocation.required"));
					}
				}
			} else {
				errors.rejectValue("shippingDestinationType", "NotNull",  messageHelper.getMessage("partsAndLogistics.distributionMethodType.required"));
			}
		}

		if (partGroup.isReturnRequired()) {
			if (!partGroup.hasReturnToLocation()) {
				errors.rejectValue("returnToLocation", "NotNull", "partsAndLogistics.returnToLocation.required");
			} else {
				addLocationErrors(errors, "returnToLocation", partGroup.getReturnToLocation());
			}
		}

		List<PartDTO> parts = partGroup.getParts();
		Group<PartDTO> partsSplit = group(parts, by(on(PartDTO.class).isReturn()));
		addPartsErrors(errors, partsSplit.find("false"));
		addPartsErrors(errors, partsSplit.find("true"));
	}

	private void addPartsErrors(Errors errors, List<PartDTO> parts) {
		int partNumber = 1;
		for (PartDTO part : parts) {
			BindingResult partErrors = new DataBinder(part).getBindingResult();
			partValidator.validate(part, partErrors);
			for (ObjectError error : partErrors.getAllErrors()) {
				String errorCode = (part.isReturn() ? "return." : "") + ((FieldError)error).getField() + "." + error.getCode();
				List<Object> messageArgs = Lists.<Object>newArrayList(partNumber);
				if (error.getArguments() != null) {
					messageArgs.addAll(Arrays.asList(error.getArguments()));
				}
				String defaultMessage = messageHelper.getMessage("partsAndLogistics.parts." + errorCode, messageArgs.toArray());
				errors.rejectValue(String.format("parts[%d]", partNumber), errorCode, defaultMessage);
			}
			partNumber++;
		}
	}

	private void addLocationErrors(Errors errors, String propertyName, LocationDTO locationDTO) {
		BindingResult locationErrors = new DataBinder(locationDTO).getBindingResult();
		addressWorkValidator.validate(locationDTO, locationErrors);
		for (ObjectError error : locationErrors.getAllErrors()) {
			String errorCode = ((FieldError)error).getField() + "." + error.getCode();
			String defaultMessage = messageHelper.getMessage("partsAndLogistics." + propertyName + "." + errorCode);
			errors.rejectValue(propertyName, errorCode, defaultMessage);
		}
	}
}
