package com.workmarket.domains.onboarding.model;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by ianha on 3/19/15.
 */
@Service
public class PhoneInfoDTOValidator implements Validator {

    @Autowired MessageBundleHelper messageBundleHelper;

    @Override
    public boolean supports(Class<?> clazz) {
        return PhoneInfoDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target == null || !(target instanceof PhoneInfoDTO)) {
            return;
        }

        PhoneInfoDTO phone = (PhoneInfoDTO) target;

        if (StringUtils.isBlank(phone.getNumber())) {
            return;
        }

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        String code = phone.getCode(),
            number = phone.getNumber(),
            region = phoneUtil.getRegionCodeForCountryCode(code == null ? 1 : Integer.parseInt(code)),
            formattedNumber = number;

        try {
            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(number, region);
            formattedNumber = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);

            if (!phoneUtil.isValidNumber(phoneNumber)) {
                errors.rejectValue("phoneNumber", "phoneNumber", String.format(messageBundleHelper.getMessage("phoneInfo.validation.invalidPhone"), formattedNumber, region));
            }
        }
        catch (NumberParseException e) {
            errors.rejectValue("phoneNumber", "phoneNumber", String.format(messageBundleHelper.getMessage("phoneInfo.validation.invalidPhone"), formattedNumber, region));
        }
    }
}
