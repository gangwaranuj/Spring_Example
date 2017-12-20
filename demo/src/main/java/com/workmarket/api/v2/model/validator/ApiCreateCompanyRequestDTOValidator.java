package com.workmarket.api.v2.model.validator;

import com.workmarket.api.v2.model.AddressApiDTO;
import com.workmarket.api.v2.model.ApiCreateCompanyRequestDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.PostalCodeUtilities;
import com.workmarket.service.business.UserService;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.validators.PasswordValidator;
import com.workmarket.web.validators.UserEmailValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

@Component("companyRequestDTOValidator")
public class ApiCreateCompanyRequestDTOValidator implements Validator {
  private static final Pattern NAME_REGEX = Pattern.compile("[a-zA-Z0-9'àáâäãåèéêëìíîïòóôöõøùúûüÿýñçčšžÀÁÂÄÃÅÈÉÊËÌÍÎÏÒÓÔÖÕØÙÚÛÜŸÝÑßÇŒÆČŠŽ∂ð\\-\\p{Space}]{2,50}");
  public static final String BLANK_USERNAME_TO_PREVENT_WEIRD_PREDICTABLE_PASSWORD_ERRORS = "";

  private final UserService userService;
  private final UserEmailValidator userEmailValidator;
  private final PasswordValidator passwordValidator;

  @Autowired
  public ApiCreateCompanyRequestDTOValidator(
    final UserEmailValidator userEmailValidator,
    final PasswordValidator passwordValidator,
    final UserService userService) {
    this.userEmailValidator = userEmailValidator;
    this.passwordValidator = passwordValidator;
    this.userService = userService;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return ApiCreateCompanyRequestDTO.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ApiCreateCompanyRequestDTO dto = (ApiCreateCompanyRequestDTO)target;

    validateBasicCompanyFields(errors, dto);

    validateEmailAddress(errors, dto);

    validatePassword(errors, dto);

    validateAddress(errors, dto);
  }

  private void validateAddress(Errors errors, ApiCreateCompanyRequestDTO dto) {
    AddressApiDTO addressDto = dto.getAddress();
    if(addressDto != null) {
      if (StringUtils.isBlank(addressDto.getAddressLine1())) {
        errors.rejectValue("address.addressLine1", "onboarding.validation.mustDefine", "Address Line 1 is required");
      }
      if (StringUtils.isBlank(addressDto.getCity())) {
        errors.rejectValue("address.city", "onboarding.validation.mustDefine", "City is required");
      }
      if (StringUtils.isBlank(addressDto.getCountry())) {
        errors.rejectValue("address.country", "onboarding.validation.mustDefine", "Country is required");
      }

      if (addressDto.getLatitude() == null && addressDto.getLongitude() != null) {
        errors.rejectValue("address.latitude", "onboarding.validation.mustDefine", "Latitude is required");
      }
      if (addressDto.getLongitude() == null && addressDto.getLatitude() != null) {
        errors.rejectValue("address.longitude", "onboarding.validation.mustDefine", "Longitude is required");
      }

      if(addressDto.getPostalCode() == null) {
        errors.rejectValue("address.postalCode", "locations.manage.invalid.postalcode", "Postal Code is required");
      }
      else {
        if (StringUtilities.equalsAny(addressDto.getCountry(), Country.USA, Country.newInstance(Country.USA).getName())){
          if(!PostalCodeUtilities.isValidPostalCode(addressDto.getPostalCode())) {
            errors.rejectValue("address.postalCode", "locations.manage.invalid.postalcode", "Please provide a valid Postal Code");
          }
        }
        else if (StringUtilities.equalsAny(addressDto.getCountry(), Country.CANADA, Country.newInstance(Country.CANADA).getName())) {
          try {
            PostalCodeUtilities.formatCanadianPostalCode(addressDto.getPostalCode());
          }
          catch(Exception e) {
            errors.rejectValue("address.postalCode", "locations.manage.invalid.postalcode", "Please provide a valid Postal Code");
          }
        }
        else {
          if (org.apache.commons.lang.StringUtils.length(addressDto.getPostalCode()) > Constants.POSTAL_CODE_MAX_LENGTH) {
            errors.rejectValue("address.postalCode", "locations.manage.invalid.postalcode","Postal code is too long");
          }
        }
      }
    }
  }

  private void validatePassword(Errors errors, ApiCreateCompanyRequestDTO dto) {
    if (StringUtils.isBlank(dto.getPassword())) {
      errors.rejectValue("password", "mysettings.password.newPassword.invalid","Password required.");
    }

    errors.pushNestedPath("password");
    passwordValidator.validate(dto.getPassword(),
      BLANK_USERNAME_TO_PREVENT_WEIRD_PREDICTABLE_PASSWORD_ERRORS, errors);
    errors.popNestedPath();
  }

  private void validateEmailAddress(Errors errors, ApiCreateCompanyRequestDTO dto) {
    errors.pushNestedPath("userEmail");
    userEmailValidator.validate(dto.getUserEmail(), errors);
    errors.popNestedPath();
    if (userService.findUserByEmail(dto.getUserEmail()) != null) {
      errors.rejectValue("userEmail", "user.validation.emailExists","The email address you entered is already being used.");
    }
  }

  private void validateBasicCompanyFields(Errors errors, ApiCreateCompanyRequestDTO dto) {
    if(StringUtils.isBlank(dto.getFirstName())) {
      errors.rejectValue("firstName", "user.validation.firstNameRequired", "First Name is a required field.");
    }
    else if(!NAME_REGEX.matcher(dto.getFirstName()).matches()) {
      errors.rejectValue("firstName", "user.validation.firstNameFormat", "First Name must be 2-50 letters long.");
    }

    if(StringUtils.isBlank(dto.getLastName())) {
      errors.rejectValue("lastName", "user.validation.lastNameRequired", "Last Name is a required field.");
    }
    else if(!NAME_REGEX.matcher(dto.getLastName()).matches()) {
      errors.rejectValue("lastName", "user.validation.lastNameFormat", "Last Name must be 2-50 letters long.");
    }

    if(StringUtils.isBlank(dto.getCompanyName())) {
      errors.rejectValue("companyName", "company.name.required", "Company name is a required field");
    }
    else if(dto.getCompanyName().length() > 50) {
      errors.rejectValue("companyName", "company.name.max.length", "Company name must be less than 50 letters long");
    }

    if(dto.getIndustryId() == null) {
      errors.rejectValue("industryId", "user.validation.industryRequired", "Industry is a required field");
    }
  }
}
