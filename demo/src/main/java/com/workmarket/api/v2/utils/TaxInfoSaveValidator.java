package com.workmarket.api.v2.utils;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import com.workmarket.api.v2.model.TaxInfoSaveApiDTO;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.TaxEntityType;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;

import org.apache.commons.lang.StringUtils;

import java.util.Calendar;
import java.util.List;

public class TaxInfoSaveValidator {
  public static final int MAX_MIDDLE_NAME = 50;
  public static final int MAX_LAST_NAME = 100;
  public static final int MAX_FIRST_NAME = 100;
  public static final int MAX_STATE = 16;

  public static List<String> validate(final TaxInfoSaveApiDTO dto) {
    final ImmutableList.Builder errors = ImmutableList.builder();

    if (dto == null) {
      errors.add("Save request can not be null.");
      return errors.build();
    }

    // Validate country up front
    if (dto.getCountry() == null) {
      errors.add("Country must be defined.");
      return errors.build();
    }

    if (AbstractTaxEntity.COUNTRY_USA.equals(dto.getCountry().code())) {
      if (dto.isBusiness()) {
        errors.addAll(validateUsaBusiness(dto));
      } else {
        errors.addAll(validateUsaIndividual(dto));
      }
    }

    if (AbstractTaxEntity.COUNTRY_CANADA.equals(dto.getCountry().code())) {
      if (dto.isBusiness()) {
        errors.addAll(validateCanadianBusiness(dto));
      } else {
        errors.addAll(validateCanadianIndividual(dto));
      }
    }

    if (AbstractTaxEntity.COUNTRY_OTHER.equals(dto.getCountry().code())) {
      if (dto.isBusiness()) {
        errors.addAll(validateForeignBusiness(dto));
      } else {
        errors.addAll(validateForeignIndividual(dto));
      }
    }

    return errors.build();
  }

  private static List<String> validateForeignIndividual(TaxInfoSaveApiDTO dto) {
    final ImmutableList.Builder<String> errors = ImmutableList.builder();

    validateTaxNumber(errors, dto.getTaxNumber(), "Missing National ID.");
    validateName(errors, dto);
    validateAddress(errors, dto);
    validateForeignStatus(errors, dto.isForeignStatusAccepted());

    if (StringUtils.isBlank(dto.getTaxEntityTypeCode().code())) {
      errors.add("Missing Tax Entity Type Code.");
    } else if (!StringUtilities.equalsAny(dto.getTaxEntityTypeCode().code(), TaxEntityType.INDIVIDUAL)) {
      errors.add(String.format("Unknown Tax Entity Type Code %s. Must be %s.",
        dto.getTaxEntityTypeCode(),
        TaxEntityType.INDIVIDUAL));
    }

    return errors.build();
  }


  private static List<String> validateForeignBusiness(TaxInfoSaveApiDTO dto) {
    final ImmutableList.Builder<String> errors = ImmutableList.builder();

    validateTaxNumber(errors, dto.getTaxNumber(), "Missing National ID.");
    validateCompanyName(errors, dto.getCompanyName());
    validateCountryOfIncorporation(errors, dto.getCountryOfIncorporation());
    validateAddress(errors, dto);

    if (dto.getTaxEntityTypeCode() == null) {
      errors.add("Missing Tax Entity Type Code.");
    } else if (!StringUtilities.equalsAny(dto.getTaxEntityTypeCode().code(),
      TaxEntityType.CORP,
      TaxEntityType.LLC_DISREGARDED,
      TaxEntityType.PARTNER)) {
      errors.add(String.format("Unknown Tax Entity Type Code %s. Must be one of %s.",
        dto.getTaxEntityTypeCode(),
        Joiner.on(", ").join(ImmutableList.of(
          TaxEntityType.CORP,
          TaxEntityType.LLC_DISREGARDED,
          TaxEntityType.PARTNER))));
    }

    return errors.build();
  }


  private static List<String> validateCanadianIndividual(final TaxInfoSaveApiDTO dto) {
    final ImmutableList.Builder<String> errors = ImmutableList.builder();

    validateAddress(errors, dto);
    validateName(errors, dto);

    if (StringUtils.isBlank(dto.getTaxNumber())) {
      errors.add("Missing Tax Number.");
    } else if (!StringUtilities.isCanadaSin(dto.getTaxNumber())) {
      errors.add("Tax Number is not a valid Canadian SIN.");
    }

    if (dto.getTaxEntityTypeCode() != null && !TaxEntityType.NONE.equals(dto.getTaxEntityTypeCode().code())) {
      errors.add("Tax Entity Type code can only be 'none' for Canadian tax entities.");
    }

    return errors.build();
  }

  private static List<String> validateCanadianBusiness(final TaxInfoSaveApiDTO dto) {
    final ImmutableList.Builder errors = ImmutableList.builder();

    validateCompanyName(errors, dto.getCompanyName());
    validateAddress(errors, dto);

    if (StringUtils.isBlank(dto.getTaxNumber())) {
      errors.add("Missing Tax Number.");
    } else if (!StringUtilities.isCanadaBn(dto.getTaxNumber())) {
      errors.add("Tax Number is not a valid Canadian Business Number.");
    }

    return errors.build();
  }

  private static List<String> validateUsaIndividual(final TaxInfoSaveApiDTO dto) {
    final ImmutableList.Builder errors = ImmutableList.builder();

    validateAddress(errors, dto);
    validateAgreeToTerms(errors, dto.isAgreeToTerms());
    validateSignature(errors, dto);
    validateName(errors, dto);
    validateEffectiveDate(errors, dto.getEffectiveDate());

    if (StringUtils.isBlank(dto.getTaxNumber())) {
      errors.add("Missing Tax Number.");
    } else if (!StringUtilities.isUsaTaxIdentificationNumber(dto.getTaxNumber())) { // EIN, SSN or ITIN
      errors.add("Tax Number is not a valid EIN, SSN or ITIN number.");
    }

    if (dto.getTaxEntityTypeCode() == null) {
      errors.add("Missing Tax Entity Type Code.");
    } else if (!StringUtilities.equalsAny(dto.getTaxEntityTypeCode().code(), TaxEntityType.INDIVIDUAL)) {
      errors.add(String.format("Unknown Tax Entity Type Code %s. Must be %s.",
        dto.getTaxEntityTypeCode(),
        TaxEntityType.INDIVIDUAL));
    }

    return errors.build();
  }

  private static List<String> validateUsaBusiness(final TaxInfoSaveApiDTO dto) {
    final ImmutableList.Builder errors = ImmutableList.builder();

    validateCompanyName(errors, dto.getCompanyName());
    validateAddress(errors, dto);
    validateAgreeToTerms(errors, dto.isAgreeToTerms());
    validateEffectiveDate(errors, dto.getEffectiveDate());
    validateSignature(errors, dto);

    if (StringUtils.isBlank(dto.getTaxNumber())) {
      errors.add("Missing Tax Number.");
    } else if (!StringUtilities.isUsaEin(dto.getTaxNumber())) {
      errors.add("Tax Number is not a valid EIN number.");
    }

    if (dto.getTaxEntityTypeCode() == null) {
      errors.add("Missing Tax Entity Type Code.");
    } else if (!StringUtilities.equalsAny(dto.getTaxEntityTypeCode().code(),
      TaxEntityType.INDIVIDUAL,
      TaxEntityType.C_CORP,
      TaxEntityType.S_CORP,
      TaxEntityType.PARTNER,
      TaxEntityType.TRUST,
      TaxEntityType.LLC_C_CORPORATION,
      TaxEntityType.LLC_S_CORPORATION,
      TaxEntityType.LLC_PARTNERSHIP,
      TaxEntityType.LLC_DISREGARDED,
      TaxEntityType.LLC_CORPORATION)) {
      errors.add(String.format("Unknown Tax Entity Type Code %s. Must be one of %s.",
        dto.getTaxEntityTypeCode(),
        Joiner.on(", ").join(ImmutableList.of(
          TaxEntityType.INDIVIDUAL,
          TaxEntityType.C_CORP,
          TaxEntityType.S_CORP,
          TaxEntityType.PARTNER,
          TaxEntityType.TRUST,
          TaxEntityType.LLC_C_CORPORATION,
          TaxEntityType.LLC_S_CORPORATION,
          TaxEntityType.LLC_PARTNERSHIP,
          TaxEntityType.LLC_DISREGARDED,
          TaxEntityType.LLC_CORPORATION))));
    }

    return errors.build();
  }

  private static void validateForeignStatus(
    final ImmutableList.Builder<String> errors,
    final boolean foreignStatusAccepted) {
    if (!foreignStatusAccepted) {
      errors.add("Foreign Status Certification must be accepted for foreign tax entities.");
    }
  }

  private static void validateAddress(final ImmutableList.Builder<String> errors, final TaxInfoSaveApiDTO dto) {
    if (StringUtils.isBlank(dto.getAddress())) {
      errors.add("Missing address.");
    }
    if (StringUtils.isBlank(dto.getCity())) {
      errors.add("Missing city.");
    }
    if (StringUtils.isBlank(dto.getState())) {
      errors.add("Missing state/province.");
    }
    else if (dto.getState().length() > MAX_STATE) {
      errors.add("State/province exceeded max length " + MAX_STATE + ".");
    }
    if (StringUtils.isBlank(dto.getPostalCode())) {
      errors.add("Missing postal code.");
    }
  }

  private static void validateName(final ImmutableList.Builder<String> errors, final TaxInfoSaveApiDTO dto) {
    if (StringUtils.isBlank(dto.getFirstName())) {
      errors.add("Missing first name.");
    } else if (dto.getFirstName().length() > MAX_FIRST_NAME) {
      errors.add("First name exceeded max length " + MAX_FIRST_NAME + ".");
    }
    if (StringUtils.isBlank(dto.getLastName())) {
      errors.add("Missing last name.");
    } else if (dto.getLastName().length() > MAX_LAST_NAME) {
      errors.add("Last name exceeded max length " + MAX_LAST_NAME+ ".");
    }
    if (StringUtils.isNotBlank(dto.getLastName()) && dto.getMiddleName().length() > MAX_MIDDLE_NAME) {
      errors.add("Middle name exceeded max length " + MAX_MIDDLE_NAME + ".");
    }
  }

  private static void validateTaxNumber(
    final ImmutableList.Builder<String> errors,
    final String value,
    final String message) {
    if (StringUtils.isBlank(value)) {
      errors.add(message);
    }
  }

  private static void validateEffectiveDate(final ImmutableList.Builder<String> errors, final Long effectiveDate) {
    if (effectiveDate != null && effectiveDate > 0) {
      errors.add("Effective date can not be defined for new tax entities.");
    }
  }

  private static void validateSignature(
    final ImmutableList.Builder<String> errors,
    final TaxInfoSaveApiDTO dto) {
    if (StringUtils.isBlank(dto.getSignature())) {
      errors.add("Missing signature.");
    }
    if (dto.getSignatureDate() == null) {
      errors.add("Missing signature date.");
    } else if (!DateUtilities.isOn(Calendar.getInstance(), dto.getSignatureDate())) {
      errors.add("Signature date must be today.");
    }
  }

  private static void validateAgreeToTerms(final ImmutableList.Builder<String> errors, final boolean agreeToTerms) {
    if (!agreeToTerms) {
      errors.add("Must agree to terms.");
    }
  }

  private static void validateCountryOfIncorporation(
    final ImmutableList.Builder<String> errors,
    final String countryOfIncorporation) {
    if (StringUtils.isBlank(countryOfIncorporation)) {
      errors.add("Missing Country of Incorporation.");
    }
  }

  private static void validateCompanyName(final ImmutableList.Builder<String> errors, final String companyName) {
    if (StringUtils.isBlank(companyName)) {
      errors.add("Missing Company Name.");
    }
  }
}