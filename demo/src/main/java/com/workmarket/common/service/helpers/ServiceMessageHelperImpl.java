package com.workmarket.common.service.helpers;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.utility.LocaleUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by nick on 4/5/13 12:32 PM
 */
@Component
public class ServiceMessageHelperImpl implements ServiceMessageHelper {

	@Autowired protected MessageSource messageSource;

	private static final Log logger = LogFactory.getLog(ServiceMessageHelperImpl.class);
	private final Locale locale = LocaleUtilities.getDefaultLocale();

	@Override public List<String> getMessages(List<ConstraintViolation> violations) {
		ArrayList<String> result = Lists.newArrayList();
		for (ConstraintViolation v : checkNotNull(violations))
			result.add(messageSource.getMessage(v.getKey(), v.getParams(), locale));
		return result;
	}

	@Override
	public List<String> getAllErrors(BindingResult binding) {
		List<String> result = Lists.newArrayList();
		if (binding.hasErrors()) {
			for (ObjectError e : binding.getAllErrors()) {
				result.add(getMessage(e));
			}
		}
		return result;
	}

	@Override
	public String getMessage(ObjectError error) {
		try {
			return messageSource.getMessage(error, null);
		} catch (NoSuchMessageException e) {
			return error.getDefaultMessage();
		}
	}

	@Override
	public String getMessage(String key, Object... arguments) {
		try {
			return messageSource.getMessage(key, arguments, null);
		} catch (NoSuchMessageException e) {
			logger.error(String.format("Error on message key %s: ", key), e);
		}
		return key;
	}

	/* TODO: this will come in handy when converting older services to format messages for now leaving it commented out
	@Override
	public List<String> getThriftMessages(List<com.workmarket.thrift.core.ConstraintViolation> violations) {
		ArrayList<String> result = Lists.newArrayList();
		for (com.workmarket.thrift.core.ConstraintViolation v : checkNotNull(violations))
			result.add(messageSource.getMessage(v.getError(), v.getParamArray(), locale));
		return result;
	}*/
}
