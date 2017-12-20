package com.workmarket.api.v2.validators;

import com.google.common.collect.ImmutableMap;
import com.workmarket.api.v2.model.ApiBankAccountDTO;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.payments.model.BankAccountDTO;
import com.workmarket.domains.payments.validator.BankAccountValidator;
import com.workmarket.service.infra.business.InvariantDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.Validator;

import java.io.Serializable;
import java.util.Map;

@Component
public class ApiBankAccountValidator implements Validator {

	private final Map<String, String> PAYPAL_FIELD_MAPPING = ImmutableMap.<String, String>builder()
		.put("emailAddress", "name")
		.put("countryCode", "country")
		.build();

	private final Map<String, String> ACH_FIELD_MAPPING = ImmutableMap.<String, String>builder()
		.put("bankName", "bankName")
		.put("countryCode", "country")
		.put("routingNumber", "routingNumber")
		.put("accountNumber", "accountNumber")
		.put("nameOnAccount", "accountHolder")
		.put("bankAccountTypeCode", "accountType")
		.put("branchNumber", "transitBranchNumber")
		.put("accountNumberConfirm", "accountNumber")
		.put("institutionNumber", "financialInstNumber")
		.build();

	private final Map<String, Map<String, String>> FIELD_MAPPING = ImmutableMap.<String, Map<String, String>>builder()
		.put(AbstractBankAccount.PAYPAL, PAYPAL_FIELD_MAPPING)
		.put(AbstractBankAccount.ACH, ACH_FIELD_MAPPING)
		.build();

	final BankAccountValidator validator;
	final InvariantDataService dataService;
	final SecurityContextFacade securityContext;

	@Autowired
	public ApiBankAccountValidator(
		final InvariantDataService dataService,
		final SecurityContextFacade securityContext){

		this.dataService = dataService;
		this.securityContext = securityContext;
		this.validator = new BankAccountValidator(dataService, securityContext, FIELD_MAPPING);
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return ApiBankAccountDTO.class.equals(clazz);
	}

	@Override
	public void validate(final Object object, final Errors errors) {
		final ApiBankAccountDTO apiDto = (ApiBankAccountDTO) object;
		final BankAccountDTO domainDto = apiDto.toBankAccountDTO();

		validateBankAccountDTO(apiDto, domainDto, errors);
	}

	private void validateBankAccountDTO(final ApiBankAccountDTO apiDto, final BankAccountDTO domainDto, final Errors errors) {
		final Map<String, Serializable> map = apiDto.toMap();
		final MapBindingResult mapErrors = new MapBindingResult(map, errors.getObjectName());

		validator.validate(domainDto, mapErrors);

		errors.addAllErrors(mapErrors);
	}
}
