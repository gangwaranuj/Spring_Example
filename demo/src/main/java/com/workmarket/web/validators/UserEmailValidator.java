package com.workmarket.web.validators;

import com.workmarket.domains.model.BlacklistedDomain;
import com.workmarket.service.infra.business.InvariantDataService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

/**
 * Created by nick on 4/18/14 6:21 PM
 */
@Component
public class UserEmailValidator implements Validator {

	@Autowired InvariantDataService invariantDataService;

	private static final Pattern EMAIL_REGEX = Pattern.compile("([A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4})?");

	public boolean supports(Class<?> clazz) {
		return String.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		String email = StringUtils.trim((String) target);

		if (StringUtils.isBlank(email) || !EMAIL_REGEX.matcher(email).matches()) {
			errors.rejectValue(null, "Pattern", new Object[]{"Email"}, "");
		} else {
			for (BlacklistedDomain blacklisted : invariantDataService.getBlacklistedDomains()) {
				if (blacklisted.isEmailMatch(email)) {
					errors.rejectValue(null, "Pattern", new Object[]{"Email"}, "");
				}
			}
		}
	}
}
