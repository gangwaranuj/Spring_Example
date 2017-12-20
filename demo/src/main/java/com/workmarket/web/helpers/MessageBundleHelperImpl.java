package com.workmarket.web.helpers;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

import com.workmarket.common.service.wrapper.response.MessageResponse;
import com.workmarket.helpers.ResponseBuilderBase;
import com.workmarket.web.models.MessageBundle;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageBundleHelperImpl implements MessageBundleHelper {

	private final static String MODEL_ATTRIBUTE_NAME = "bundle";

	@Autowired private MessageSource messageSource;

	@Override
	public MessageBundle newBundle() {
		return MessageBundle.newInstance();
	}

	@Override
	public MessageBundle newBundle(Model model) {
		Assert.notNull(model);
		MessageBundle bundle = MessageBundle.newInstance();
		model.addAttribute(MODEL_ATTRIBUTE_NAME, bundle);
		return bundle;
	}

	@Override
	public MessageBundle newFlashBundle(RedirectAttributes flash) {
		Assert.notNull(flash);
		MessageBundle bundle = MessageBundle.newInstance();
		flash.addFlashAttribute(MODEL_ATTRIBUTE_NAME, bundle);
		return bundle;
	}

	@Override
	public MessageBundleHelper addSuccess(MessageBundle bundle, String message, Object... arguments) {
		bundle.addSuccess(getMessage(message, arguments));
		return this;
	}

	@Override
	public MessageBundleHelper addError(MessageBundle bundle, String message, Object... arguments) {
		bundle.addError(getMessage(message, arguments));
		return this;
	}

	@Override
	public MessageBundleHelper addWarning(MessageBundle bundle, String message, Object... arguments) {
		bundle.addWarning(getMessage(message, arguments));
		return this;
	}

	@Override
	public MessageBundleHelper addNotice(MessageBundle bundle, String message, Object... arguments) {
		bundle.addNotice(getMessage(message, arguments));
		return this;
	}

	@Override
	public <T extends ResponseBuilderBase> MessageBundleHelper addMessage(T responseBuilder, String message, Object... arguments) {
		List<String> messages = MoreObjects.firstNonNull(responseBuilder.getMessages(), new ArrayList<String>());
		messages.add(getMessage(message, arguments));
		responseBuilder.setMessages(messages);
		return this;
	}

	@Override
	public <T extends ResponseBuilderBase>MessageBundleHelper clearMessages(T responseBuilder) {
		List<String> messages = MoreObjects.firstNonNull(responseBuilder.getMessages(), new ArrayList<String>());
		messages.clear();
		responseBuilder.setMessages(messages);
		return this;
	}

	@Override
	public MessageBundleHelper addSuccessOrErrorMessage(MessageBundle bundle, AjaxResponseBuilder response,String message, Object... arguments){
		if(response.isSuccessful()){
			bundle.addSuccess(getMessage(message, arguments));
		}else{
			for(String error : response.getMessages()){
				bundle.addError(error);
			}
		}
		return this;
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
	public List<String> getAllFieldErrors(BindingResult binding) {
		List<String> result = Lists.newArrayList();
		if (binding.hasErrors()) {
			for (FieldError e : binding.getFieldErrors()) {
				result.add(getMessage(e));
			}
		}
		return result;
	}

	@Override
	public  MessageBundleHelper setErrors(MessageBundle bundle, BindingResult binding) {
		if (binding.hasErrors()) {
			for (ObjectError e : binding.getAllErrors()) {
				bundle.addError(getMessage(e));
			}
		}
		return this;
	}

	@Override
	public <T extends ResponseBuilderBase> MessageBundleHelper setErrors(T responseBuilder, BindingResult binding) {
		List<String> messages = MoreObjects.firstNonNull(responseBuilder.getMessages(), Lists.<String>newArrayList());
		if (binding.hasErrors()) {
			for (ObjectError e : binding.getAllErrors()) {
				messages.add(getMessage(e));
			}
		}
		responseBuilder.setMessages(messages);
		return this;
	}

	@Override
	public MessageBundleHelper setErrors(AjaxResponseBuilder responseBuilder, MessageResponse bulkResponse) {
		List<String> messages = MoreObjects.firstNonNull(responseBuilder.getMessages(), Lists.<String>newArrayList());
		for (String message: bulkResponse.getMessages()) {
			messages.add(message);
		}
		responseBuilder.setMessages(messages);
		return this;
	}

	@Override
	public MessageBundleHelper setErrors(MessageBundle bundle, MessageResponse bulkResponse) {
		for (String message : bulkResponse.getMessages())
			bundle.addError(message);
		return this;
	}

	@Override
	public String getMessage(String message, Object... arguments) {
		message = StringEscapeUtils.escapeHtml4(StringEscapeUtils.unescapeHtml4(message));
		try {
			return messageSource.getMessage(message, arguments, null);
		} catch (NoSuchMessageException e) {
			return message;
		}
	}

	@Override
	public String getMessage(ObjectError error) {
		try {
			return messageSource.getMessage(error, null);
		} catch (NoSuchMessageException e) {
			return error.getDefaultMessage();
		}
	}
}
