package com.workmarket.api.v2.utils;

import com.workmarket.api.internal.model.UserRegistration;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.domains.onboarding.model.PhoneInfoDTO;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.workmarket.utility.StringUtilities.equalsAny;


@Service
public class UserRegistrationValidator implements Validator {
    @Autowired MessageBundleHelper messageBundleHelper;
    @Autowired @Qualifier("phoneInfoDTOValidator") Validator phoneValidator;
    @Autowired @Qualifier("addressValidator") Validator addressValidator;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRegistration.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target == null) {
            return;
        }

        UserRegistration userRegistration = (UserRegistration) target;
        EmailValidator emailValidator = EmailValidator.getInstance();
        UrlValidator urlValidator = new UrlValidator();

        if (StringUtils.isBlank(userRegistration.getEmail())) {
            errors.rejectValue("email", "email", messageBundleHelper.getMessage("userRegistration.validation.empty_email"));
        } else if (!emailValidator.isValid(userRegistration.getEmail())) {
            errors.rejectValue("email", "email", messageBundleHelper.getMessage("userRegistration.validation.malformed_email"));
        }

        if (StringUtils.isNotBlank(userRegistration.getResumeUrl()) &&
            !urlValidator.isValid(userRegistration.getResumeUrl())) {
            errors.rejectValue("resumeUrl", "resumeUrl", messageBundleHelper.getMessage("userRegistration.validation.malformed_resume_url"));
        }

        if (!StringUtils.isBlank(userRegistration.getPhoneNumber())) {
            PhoneInfoDTO phone = new PhoneInfoDTO();
            if (StringUtils.isNotBlank(userRegistration.getCountryCallingCode())) {
                phone.setCode(userRegistration.getCountryCallingCode());
            } else if (StringUtils.isNotBlank(userRegistration.getIsoCountryCode())) {
                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                phone.setCode(String.valueOf(phoneUtil.getCountryCodeForRegion(userRegistration.getIsoCountryCode())));
            }
            phone.setNumber(userRegistration.getPhoneNumber());
            phoneValidator.validate(phone, errors);
        }

        // If any address info is sent, verify all address data points have values, and then
        // perform normal address validation
        if (StringUtils.isNotBlank(userRegistration.getAddress()) ||
            StringUtils.isNotBlank(userRegistration.getCity()) ||
            StringUtils.isNotBlank(userRegistration.getStateCode()) ||
            StringUtils.isNotBlank(userRegistration.getPostalCode()) ||
            StringUtils.isNotBlank(userRegistration.getIsoCountryCode())) {

            addressValidator.validate(userRegistration.toAddressDTO(), errors);

            // Special case: if DTO has Puerto Rico set as country, change country to USA, and state to PR
            if (equalsAny(userRegistration.getIsoCountryCode(), Country.PR, Country.PUERTO_RICO)) {
                userRegistration.setIsoCountryCode(Country.US);
                userRegistration.setStateCode(State.PR);
            }
        }
    }
}