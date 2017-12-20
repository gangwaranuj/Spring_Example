
package com.workmarket.web.controllers;

import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.models.MessageBundleType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyEditorSupport;

@Controller
@RequestMapping("/message")
public class MessageController extends BaseController {

	@Autowired private MessageBundleHelper messageHelper;

	class MessageBundleTypeEditor extends PropertyEditorSupport {
		@Override
		public void setAsText(String s) throws IllegalArgumentException {
			try {
				setValue(MessageBundleType.valueOf(StringUtils.upperCase(s)));
			} catch (Exception e) {
				setValue(MessageBundleType.NOTICE);
			}
		}
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(MessageBundleType.class, new MessageBundleTypeEditor());
	}

	/**
	 * Utility for setting flash message via an AJAX request.
	 * Expects a POST request with the following parameters:
	 *  - message String value of the flash message
	 *  - type    String type of the flash message. Valid values include: 'error', 'notice', 'success', and 'warn'
	 */
	@RequestMapping(value="/create", method=RequestMethod.POST)
	public String create(
			HttpServletRequest request,
			RedirectAttributes redirectAttributes,
			@RequestParam(value="type", defaultValue="success") MessageBundleType messageType,
			@RequestParam(required=false) String url) {

		MessageBundle messageBundle = messageHelper.newFlashBundle(redirectAttributes);
		String[] messages = request.getParameterValues("message[]"); // don't do this in RequestParam, it splits on commas
		if (messages != null)
			for (String m : messages)
				messageBundle.addMessage(messageType, m);
		String redirectPath = !StringUtils.isBlank(url)
			? url
			: StringUtils.substringAfter(request.getHeader("referer"), request.getHeader("host"));

		return "redirect:" + redirectPath;
	}
}
