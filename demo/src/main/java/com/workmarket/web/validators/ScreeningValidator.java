package com.workmarket.web.validators;

import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.screening.Screening;
import com.workmarket.service.business.dto.ScreeningDTO;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("screeningValidator")
public class ScreeningValidator extends AddressValidator {

    private static final String LEGAL_STRING_PATTERN_REGEX = "^[^<>\"]*$";
    private static final Pattern XSS_CHECK_PATTERN = Pattern.compile(LEGAL_STRING_PATTERN_REGEX);

    @Override
    public boolean supports(Class<?> clazz) {
        return (ScreeningDTO.class == clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        super.validate(target, errors);

        ScreeningDTO screening = (ScreeningDTO) target;

        if (screening.getBirthMonth() == null ||
            screening.getBirthMonth() < 1 ||
            screening.getBirthDay() == null ||
            screening.getBirthDay() < 1 ||
            screening.getBirthYear() == null ||
            screening.getBirthYear() < 1) {

            errors.reject("NotEmpty", new Object[] {"Date of Birth"}, null);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "NotEmpty", new Object[] {"firstName"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "NotEmpty", new Object[] {"lastName"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty", new Object[] {"email"});

        if (!validateXssSafety(screening.getFirstName())) {

            errors.rejectValue("firstName",
                               "screening.background.field.xssFail",
                               ArrayUtils.toArray("First Name"),
                               "Invalid First Name field");
        }
        if (!validateXssSafety(screening.getMiddleName())) {

            errors.rejectValue("middleName",
                               "screening.background.field.xssFail",
                               ArrayUtils.toArray("Middle Name"),
                               "Invalid Middle Name field");
        }
        if (!validateXssSafety(screening.getLastName())) {

            errors.rejectValue("lastName",
                               "screening.background.field.xssFail",
                               ArrayUtils.toArray("Last Name"),
                               "Invalid Last Name field");
        }
        if (!validateXssSafety(screening.getMaidenName())) {

            errors.rejectValue("maidenName",
                               "screening.background.field.xssFail",
                               ArrayUtils.toArray("Maiden Name"),
                               "Invalid Maiden Name field");
        }
        if (!validateXssSafety(screening.getAddress1())) {

            errors.rejectValue("address1",
                               "screening.background.field.xssFail",
                               ArrayUtils.toArray("Address"),
                               "Invalid Address field");
        }
        if (!validateXssSafety(screening.getAddress2())) {

            errors.rejectValue("address2",
                               "screening.background.field.xssFail",
                               ArrayUtils.toArray("Address 2"),
                               "Invalid Address 2 field");
        }
        if (!validateXssSafety(screening.getCity())) {

            errors.rejectValue("city",
                               "screening.background.field.xssFail",
                               ArrayUtils.toArray("City"),
                               "Invalid City field");
        }
        if (!validateXssSafety(screening.getPostalCode())) {

            errors.rejectValue("postalCode",
                               "screening.background.field.xssFail",
                               ArrayUtils.toArray("Postal Code"),
                               "Invalid Postal Code field");
        }
        if (!validateXssSafety(screening.getEmail())) {

            errors.rejectValue("email",
                               "screening.background.field.xssFail",
                               ArrayUtils.toArray("Email"),
                               "Invalid Email field");
        }

        if (Country.USA.equalsIgnoreCase(screening.getCountry())) {

            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "city", "NotEmpty", new Object[] {"city"});
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "postalCode", "NotEmpty", new Object[] {"postal code"});

            // is SSN/ITIN valid
            if (!StringUtilities.isUsaIndividualTaxIdentificationNumber(screening.getWorkIdentificationNumber())) {

                errors.rejectValue("workIdentificationNumber", "screening.background.ssn.invalid");
            }
        }
        else if (Country.CANADA.equalsIgnoreCase(screening.getCountry())) {

            if (!StringUtilities.isCanadaSin(screening.getWorkIdentificationNumber())) {

                errors.rejectValue("workIdentificationNumber", "screening.background.gid.invalid");
            }
        }
        else {

            if (!StringUtils.hasText(screening.getWorkIdentificationNumber())) {

                errors.rejectValue("workIdentificationNumber", "screening.background.gid.invalid");
            }
        }

        if (Screening.DRUG_TEST_TYPE.equals(screening.getScreeningType())) {

            if (StringUtilities.isNotEmpty(screening.getCountry()) &&
                !CollectionUtilities.containsAny(screening.getCountry(), Country.USA)) {

                errors.rejectValue("country", "screening.drug.country.invalid");
            }
        }
    }

    private boolean validateXssSafety(String input) {

        if (!StringUtils.hasText(input)) {
            return true;
        }

        Matcher matcher = XSS_CHECK_PATTERN.matcher(input);
        return matcher.matches();
    }
}
