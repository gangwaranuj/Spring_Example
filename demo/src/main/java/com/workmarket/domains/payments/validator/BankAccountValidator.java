package com.workmarket.domains.payments.validator;

import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.banking.util.BankRoutingUtil;
import com.workmarket.domains.banking.validators.BankRoutingValidator;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccountType;
import com.workmarket.domains.model.banking.BankRouting;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.payments.model.BankAccountDTO;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.EmailUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Calendar;
import java.util.Collections;
import java.util.Map;

@Component
public class BankAccountValidator implements Validator {
	final InvariantDataService dataService;
	final SecurityContextFacade securityContext;
	final Map<String, Map<String, String>> fieldMapping;

	@Autowired
	public BankAccountValidator(
		final InvariantDataService dataService,
		final SecurityContextFacade securityContext){

		this(dataService, securityContext, Collections.EMPTY_MAP);
	}

	public BankAccountValidator(
		final InvariantDataService dataService,
		final SecurityContextFacade securityContext,
		final Map<String, Map<String, String>> fieldMapping){

		this.dataService = dataService;
		this.fieldMapping = fieldMapping;
		this.securityContext = securityContext;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return BankAccountDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object o, Errors errors) {
		final BankAccountDTO dto = (BankAccountDTO)o;
		final String bankAccountType = dto.getType();

		if (StringUtils.equalsIgnoreCase(bankAccountType, AbstractBankAccount.ACH)) {
			validateACH(dto, errors);

			return;
		}

		if (StringUtils.equalsIgnoreCase(bankAccountType, AbstractBankAccount.PAYPAL)) {
			validatePayPall(dto, errors);

			return;
		}

		if (StringUtils.equalsIgnoreCase(bankAccountType, AbstractBankAccount.GCC)) {
			validateGCC(dto, errors);

			return;
		}

		errors.rejectValue("type", "Invalid");
	}

	protected String getFieldName(final BankAccountDTO dto, final String name) {
		if (!fieldMapping.containsKey(dto.getType())) {
			return name;
		}

		final Map<String, String> typeMapping = fieldMapping.get(dto.getType());
		final String fieldName = typeMapping.get(name);

		if (fieldName == null) {
			return name;
		}

		return fieldName;
	}

	private void validatePayPall(final BankAccountDTO dto, final Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, getFieldName(dto, "emailAddress"), "NotNull");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, getFieldName(dto, "countryCode"), "NotNull");

		if (!EmailUtilities.isValidEmailAddress(dto.getEmailAddress())) {
			errors.rejectValue(getFieldName(dto, "emailAddress"), "banking.paypal.email.invalid");
		}

		if (Country.WITHOUTCOUNTRY.equals(Country.valueOf(dto.getCountryCode()).getId())) {
			errors.rejectValue(getFieldName(dto, "countryCode"), "banking.paypal.country.invalid");
		}
	}

	private void validateACH(final BankAccountDTO dto, final Errors errors) {
		final ExtendedUserDetails user = securityContext.getCurrentUser();
		final String country = dto.getCountry();

		// buyer can only define US bank accounts
		if (user.isBuyer() && !Country.USA_COUNTRY.equals(Country.valueOf(country))) {
			errors.rejectValue(getFieldName(dto, "country"), "country.notsupported");
		}

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, getFieldName(dto, "bankName"), "NotNull");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, getFieldName(dto, "nameOnAccount"), "NotNull");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, getFieldName(dto, "accountNumber"), "NotNull");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, getFieldName(dto, "accountNumberConfirm"), "NotNull");

		// adding verification to insure bank country matches users tax info
		String routingNumber = "";
		if (Country.CANADA.equals(country)) {
			if (!BankRoutingValidator.isValidInstitutionNumber(dto.getInstitutionNumber())) {
				errors.rejectValue(getFieldName(dto, "institutionNumber"), "banking.institutionNumber.invalid", CollectionUtilities.newArray("Financial Institution Number"), "Invalid Financial Institution Number");
			}
			if (!BankRoutingValidator.isValidBranchNumber(dto.getBranchNumber())) {
				errors.rejectValue(getFieldName(dto, "branchNumber"), "banking.branchNumber.invalid", CollectionUtilities.newArray("Transit Branch Number"), "Invalid Branch Number");
			}

			routingNumber = BankRoutingUtil.buildRoutingNumber(dto.getBranchNumber(), dto.getInstitutionNumber());
		} else if (Country.USA.equals(country)) {
			if (!BankRoutingValidator.isValidRoutingNumber(dto.getRoutingNumber())) {
				errors.rejectValue(getFieldName(dto, "routingNumber"), "banking.routingNumber.invalid", CollectionUtilities.newArray("Routing Number"), "Invalid Routing Number");
			}

			routingNumber = dto.getRoutingNumber();
		}

		if (!StringUtilities.isLengthBetween(dto.getBankName(), 1, 100)) {
			errors.rejectValue(getFieldName(dto, "bankName"), "Size", CollectionUtilities.newArray("bank name", 100, 1), "Invalid length");
		}

		if (!StringUtilities.isLengthBetween(dto.getNameOnAccount(), 1, 45)) {
			errors.rejectValue(getFieldName(dto, "nameOnAccount"), "Size", CollectionUtilities.newArray("name on account", 45, 1), "Invalid length");
		}

		if (!(StringUtils.equals(dto.getBankAccountTypeCode(), BankAccountType.CHECKING) || StringUtils.equals(dto.getBankAccountTypeCode(), BankAccountType.SAVINGS))) {
			errors.rejectValue(getFieldName(dto, "bankAccountTypeCode"), "Pattern");
		}

		if (!StringUtils.equals(dto.getAccountNumber(), dto.getAccountNumberConfirm())) {
			errors.rejectValue(getFieldName(dto, "accountNumber"), "FieldMatch", CollectionUtilities.newArray("Account Number", "Confirm Account Number"), "Does not match");
		}

		BankRouting bankDetails = dataService.getBankRouting(routingNumber, country);
		if (bankDetails == null) {
			if (Country.CANADA.equals(country)) {
				errors.rejectValue(getFieldName(dto, "branchNumber"), "banking.routingNumber.noBankFound.can");
			} else if (Country.USA.equals(country)) {
				errors.rejectValue(getFieldName(dto, "routingNumber"), "banking.routingNumber.noBankFound.usa");
			}
		} else {
			// this check is only for sellers
			if (user.isSeller()) {
				// verify country - a user can only define a bank account in the country for which their tax information is defined
				Country taxCountry = securityContext.getCurrentUser().getTaxCountry();
				if (taxCountry != null) {
					if (bankDetails.getCountry() == null) {
						if (Country.CANADA.equals(country)) {
							errors.rejectValue(getFieldName(dto, "branchNumber"), "routingNumber.invalidCountry");
						} else if (Country.USA.equals(country)) {
							errors.rejectValue(getFieldName(dto, "routingNumber"), "routingNumber.invalidCountry");
						}
					} else {
						if (!ObjectUtils.equals(taxCountry, bankDetails.getCountry())) {
							if (Country.CANADA.equals(country)) {
								errors.rejectValue(getFieldName(dto, "branchNumber"), "routingNumber.invalidCountry");
							} else if (Country.USA.equals(country)) {
								errors.rejectValue(getFieldName(dto, "routingNumber"), "routingNumber.invalidCountry");
							}
						}
					}
				}
			}
		}
	}

	private void validateGCC(final BankAccountDTO dto, final Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "firstName", "NotEmpty");
		ValidationUtils.rejectIfEmpty(errors, "lastName", "NotEmpty");
		ValidationUtils.rejectIfEmpty(errors, "address1", "NotEmpty");

		ValidationUtils.rejectIfEmpty(errors, "city", "NotEmpty");
		ValidationUtils.rejectIfEmpty(errors, "state", "NotEmpty");
		ValidationUtils.rejectIfEmpty(errors, "postalCode", "NotEmpty");
		ValidationUtils.rejectIfEmpty(errors, "govIdType", "NotEmpty");
		ValidationUtils.rejectIfEmpty(errors, "govId", "NotEmpty");

		ValidationUtils.rejectIfEmpty(errors, "dobDay", "NotEmpty");
		ValidationUtils.rejectIfEmpty(errors, "dobMonth", "NotEmpty");
		ValidationUtils.rejectIfEmpty(errors, "dobYear", "NotEmpty");

		Calendar now = Calendar.getInstance();

		if(dto.getDobYear() != null && ((now.get(Calendar.YEAR) - dto.getDobYear())  < 18)){
			errors.rejectValue("dobYear","gcc.invalid.dobYear");
		}

		if("SSN".equals(dto.getGovIdType()) && StringUtils.isNotEmpty(dto.getGovId())) {
			if (!StringUtilities.isUsaIndividualTaxIdentificationNumber(dto.getGovId())) {
				errors.rejectValue("govId", "account.tax.ssn.invalid");
			}
		}

		if("USDRIVE".equals(dto.getGovIdType()) && StringUtils.isNotEmpty(dto.getGovId())){
			if(!StringUtilities.isValidUSADriverLicence(dto.getGovId())){
				errors.rejectValue("govId","gcc.invalid.driver.license");
			}
		}

		if("NONUSTAXID".equals(dto.getGovIdType()) && StringUtils.isNotEmpty(dto.getGovId())){
			if(!StringUtilities.validateLuhn(dto.getGovId())){
				errors.rejectValue("govId","account.tax.sin.invalid");
			}
		}

		if((StringUtils.isNotEmpty(dto.getAddress1()) && StringUtilities.isPoBox(dto.getAddress1())) ||
				Boolean.TRUE.equals(dto.isMainAddressIsDifferentThenPermanent())){
			ValidationUtils.rejectIfEmpty(errors,"alternativeAddress","NotEmpty");
			ValidationUtils.rejectIfEmpty(errors,"city2","NotEmpty");
			ValidationUtils.rejectIfEmpty(errors,"state2","NotEmpty");
			ValidationUtils.rejectIfEmpty(errors,"postalCode2","NotEmpty");
		}
	}
}
