package com.workmarket.web.helpers;

import com.workmarket.common.service.wrapper.response.MessageResponse;
import com.workmarket.helpers.ResponseBuilderBase;
import com.workmarket.web.models.MessageBundle;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/*
  * This Class should only be used in Controllers
  */
public interface MessageBundleHelper {
	MessageBundle newBundle();
	MessageBundle newBundle(Model model);
	MessageBundle newFlashBundle(RedirectAttributes flash);

	MessageBundleHelper addSuccess(MessageBundle bundle, String message, Object... arguments);
	MessageBundleHelper addError(MessageBundle bundle, String message, Object... arguments);
	MessageBundleHelper addWarning(MessageBundle bundle, String message, Object... arguments);
	MessageBundleHelper addNotice(MessageBundle bundle, String message, Object... arguments);

	<T extends ResponseBuilderBase> MessageBundleHelper addMessage(T responseBuilder, String message, Object... arguments);
	<T extends ResponseBuilderBase> MessageBundleHelper clearMessages(T responseBuilder);

	MessageBundleHelper addSuccessOrErrorMessage(MessageBundle bundle, AjaxResponseBuilder response,String message, Object... arguments);

	MessageBundleHelper setErrors(MessageBundle bundle, BindingResult binding);
	<T extends ResponseBuilderBase> MessageBundleHelper setErrors(T responseBuilder, BindingResult binding);
	MessageBundleHelper setErrors(MessageBundle bundle, MessageResponse response);

	String getMessage(String message, Object... arguments);

	String getMessage(ObjectError error);

	List<String> getAllErrors(BindingResult binding);
	List<String> getAllFieldErrors(BindingResult binding);

	MessageBundleHelper setErrors(AjaxResponseBuilder responseBuilder, MessageResponse bulkResponse);
}
