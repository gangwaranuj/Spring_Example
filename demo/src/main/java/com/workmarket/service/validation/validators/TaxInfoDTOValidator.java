package com.workmarket.service.validation.validators;

import com.google.common.collect.ImmutableList;
import com.workmarket.api.v2.employer.settings.models.TaxInfoDTO;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.TaxEntityType;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.LocaleUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.vault.services.VaultHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class TaxInfoDTOValidator implements Validator{

	private static final String XSS_CHECK_REGEX_PATTERN = "^[^<>\"]*$";
	private static final Pattern XSS_CHECK_PATTERN = Pattern.compile(XSS_CHECK_REGEX_PATTERN);

	public static final int TAX_NAME_MAX_LENGTH = 100;
	public final List<String> VALID_USA_TAX_ENTITIES = ImmutableList.<String>builder()
		.add(TaxEntityType.INDIVIDUAL)
		.add(TaxEntityType.C_CORP)
		.add(TaxEntityType.S_CORP)
		.add(TaxEntityType.PARTNER)
		.add(TaxEntityType.TRUST)
		.add(TaxEntityType.LLC_C_CORPORATION)
		.add(TaxEntityType.LLC_S_CORPORATION)
		.add(TaxEntityType.LLC_PARTNERSHIP)
		.build();

	public final List<String> VALID_USA_INDIVIDUAL_TAX_ENTITIES = ImmutableList.of(
		TaxEntityType.INDIVIDUAL
	);

	public final List<String> VALID_FOREIGN_BUSINESS_TAX_ENTITIES = ImmutableList.of(
		TaxEntityType.PARTNER,
		TaxEntityType.CORP,
		TaxEntityType.LLC_DISREGARDED
	);

	public final List<String> VALID_FOREIGN_INDIVIDUAL_TAX_ENTITIES = ImmutableList.of(
		TaxEntityType.INDIVIDUAL
	);

	@Autowired private VaultHelper vaultHelper;

	@Override
	public boolean supports(final Class<?> clazz) {
		return TaxInfoDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {

		TaxInfoDTO dto = (TaxInfoDTO) target;
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address", "NotEmpty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "city", "NotEmpty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "state", "NotEmpty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "postalCode", "NotEmpty");

		if (!dto.getBusinessFlag()) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "NotEmpty");
		}

		String fullName = StringUtilities.fullName(dto.getFirstName(), dto.getMiddleName(), dto.getLastName());
		if (dto.getLastName() != null && fullName.length() > TAX_NAME_MAX_LENGTH) {
			errors.rejectValue("lastName", "account.tax.last_name.length", new Object[]{TAX_NAME_MAX_LENGTH}, "");
		}

		validateXssSafety(errors, dto.getTaxCountry(), "taxCountry", "Country of residence");
		validateXssSafety(errors, dto.getTaxNumber(), "taxNumber", dto.getBusinessFlag() ? "Employer Identification Number" : "Social Security Number");
		validateXssSafety(errors, dto.getFirstName(), "firstName", "First Name");
		validateXssSafety(errors, dto.getMiddleName(), "middleName", "Middle Name");
		validateXssSafety(errors, dto.getLastName(), "lastName", dto.getBusinessFlag() ? "Company Name" : "Last Name");
		validateXssSafety(errors, dto.getAddress(), "address", "Address");
		validateXssSafety(errors, dto.getCity(), "city", "City");
		validateXssSafety(errors, dto.getState(), "state", "State");
		validateXssSafety(errors, dto.getPostalCode(), "postalCode", "Zip Code");
		validateXssSafety(errors, dto.getTaxEntityTypeCode(), "taxEntityTypeCode", "Federal Tax Classification");
		validateXssSafety(errors, dto.getBusinessName(), "businessName", "Business Name");
		validateXssSafety(errors, dto.getEffectiveDateString(), "effectiveDateString", "Effective Date");
		validateXssSafety(errors, dto.getCountry(), "country", "Country");
		validateXssSafety(errors, dto.getCountryOfIncorporation(), "countryOfIncorporation", "Country of Incorporation");

		if (AbstractTaxEntity.COUNTRY_USA.equalsIgnoreCase(dto.getTaxCountry())) {
			validateUsa(dto, errors);
		} else if (AbstractTaxEntity.COUNTRY_CANADA.equalsIgnoreCase(dto.getTaxCountry())) {
			validateCanada(dto, errors);
		} else if (AbstractTaxEntity.COUNTRY_OTHER.equalsIgnoreCase(dto.getTaxCountry())) {
			validateForeign(dto, errors);
		} else {
			errors.rejectValue("country", "Pattern");
		}

		if(!errors.hasErrors()) {
			String rawTaxNumber = dto.getTaxNumber().replaceAll("[^\\p{Alnum}]", "");
			if(isNotBlank(rawTaxNumber)) {
				if (vaultHelper.isDuplicateOutsideCompany(dto.getTaxCountry(), rawTaxNumber, String.valueOf(dto.getCompanyNumber()))) {
					errors.rejectValue("taxNumber", "account.tax.tax_number.exists");
				}
			}
			//check if the form is signed
			if (!isSigned(dto)) {
				errors.rejectValue("signature", "account.tax.signing.empty");
			}
		}

	}

	@SuppressWarnings("unchecked")
	private void validateUsa(TaxInfoDTO dto, Errors errors) {
		// USA stores tax name in last name field for business
		if (dto.getBusinessFlag()) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "NotEmpty.companyName");
			// is EIN valid
			if (!StringUtilities.isUsaEin(dto.getTaxNumber())) {
				errors.rejectValue("taxNumber", "account.tax.ein.invalid");
			}
			// is LLC type valid
			if (!VALID_USA_TAX_ENTITIES.contains(dto.getTaxEntityTypeCode())) {
				errors.rejectValue("taxEntityTypeCode", "Pattern");
			}
		} else {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "NotEmpty");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "taxNumber", "account.tax.ssn.invalid");
			// is SSN valid
			if (!StringUtilities.isUsaIndividualTaxIdentificationNumber(dto.getTaxNumber())) {
				errors.rejectValue("taxNumber", "account.tax.ssn.invalid");
			}
			// is individual entity
			if (!VALID_USA_INDIVIDUAL_TAX_ENTITIES.contains(dto.getTaxEntityTypeCode())) {
				errors.rejectValue("taxEntityTypeCode", "Pattern");
			}
		}

		if (BooleanUtils.isTrue(dto.getBusinessNameFlag()) && isBlank(dto.getBusinessName())) {
			errors.rejectValue("businessName", "account.tax.business_name.invalid");
		}

		if (BooleanUtils.isNotTrue(dto.getDeliveryPolicyFlag())) {
			errors.rejectValue("deliveryPolicyFlag", "account.tax.delivery_policy.error");
		}
		// check for within one day to account for time zone, which we don't know
		Calendar midnightUtc = DateUtilities.getMidnightTodayRelativeToTimezone("UTC");
		Calendar effectiveDate = DateUtilities.getCalendarFromDateString(dto.getEffectiveDateString(), "UTC");
		if (effectiveDate != null
			&& midnightUtc.getTimeInMillis() - effectiveDate.getTimeInMillis() > Days.ONE.toStandardDuration().getMillis()) {
			errors.rejectValue("effectiveDateString", "account.tax.effective_date.in_past");
		}
	}

	private void validateCanada(TaxInfoDTO dto, Errors errors) {
		if (dto.getBusinessFlag()) {
			// is BN valid
			if (StringUtils.isEmpty(dto.getTaxNumber()) || !StringUtilities.isCanadaBn(dto.getTaxNumber())) {
				errors.rejectValue("taxNumber", "account.tax.bn.invalid");
			}
			if (StringUtils.isBlank(dto.getBusinessName())) {
				errors.rejectValue("businessName", "account.tax.company_name.empty");
			}
		} else {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "NotEmpty");
			// is SIN valid
			if (!StringUtilities.isCanadaSin(dto.getTaxNumber())) {
				errors.rejectValue("taxNumber", "account.tax.sin.invalid");
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void validateForeign(TaxInfoDTO dto, Errors errors) {

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "taxNumber", "account.tax.fti.invalid");
		if (dto.getBusinessFlag()) {
			// business entities must be corp, partner, disregarded
			if (!VALID_FOREIGN_BUSINESS_TAX_ENTITIES.contains(dto.getTaxEntityTypeCode())) {
				errors.rejectValue("taxEntityTypeCode", "NotNull");
			}
			// country should be valid
			if (StringUtils.isBlank(dto.getCountryOfIncorporation())) {
				errors.rejectValue("countryOfIncorporation", "NotNull");
			}
			else if (!LocaleUtilities.isValidCountryCode(dto.getCountryOfIncorporation())) {
				errors.rejectValue("countryOfIncorporation", "Pattern");
			}
		} else {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "NotEmpty");
			// business entity must be individual
			if (!VALID_FOREIGN_INDIVIDUAL_TAX_ENTITIES.contains(dto.getTaxEntityTypeCode())) {
				errors.rejectValue("taxEntityTypeCode", "NotNull");
			}
			// user must accept foreign status terms
			if (!BooleanUtils.isTrue(dto.getForeignStatusAcceptedFlag())) {
				errors.rejectValue("foreignStatusAcceptedFlag", "Pattern");
			}
		}
	}

	private void validateXssSafety(Errors errors, String input, String fieldName, String errorMessageFieldName) {
		if (StringUtils.isBlank(input)) {
			return;
		}
		Matcher matcher = XSS_CHECK_PATTERN.matcher(input);
		if (!matcher.matches()) {
			errors.rejectValue(fieldName, "account.tax.xss_validation.failed", ArrayUtils.toArray(errorMessageFieldName),
				errorMessageFieldName + " cannot include <, >, or \" character.");
		}
	}

	private boolean isSigned(TaxInfoDTO dto) {
		return ((AbstractTaxEntity.COUNTRY_USA.equals(dto.getTaxCountry()) && StringUtilities.all(dto.getSignature(), dto.getSignatureDateString()))
			|| AbstractTaxEntity.COUNTRY_CANADA.equals(dto.getTaxCountry())
			|| (AbstractTaxEntity.COUNTRY_OTHER.equals(dto.getTaxCountry()) && (dto.getBusinessFlag() || dto.getForeignStatusAcceptedFlag())));
	}
}
