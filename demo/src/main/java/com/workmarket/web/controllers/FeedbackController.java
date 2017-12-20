package com.workmarket.web.controllers;

import com.workmarket.domains.model.feedback.FeedbackConcern;
import com.workmarket.domains.model.feedback.FeedbackPriority;
import com.workmarket.service.business.FeedbackService;
import com.workmarket.service.business.dto.FeedbackDTO;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.web.editors.LookupEntityEditor;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.FeedbackValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/feedback")
@SessionAttributes({"feedback"})
public class FeedbackController extends BaseController {

	private static final Log logger = LogFactory.getLog(FeedbackController.class);

	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private FeedbackService feedbackService;

	private final FeedbackValidator validator = new FeedbackValidator();

	@ModelAttribute("concern")
	private List<FeedbackConcern> getFeedbackConcerns() {
		List<FeedbackConcern> list = feedbackService.getFeedbackConcerns();
		Collections.sort(list);
		return list;
	}

	@ModelAttribute("priority")
	private List<FeedbackPriority> getFeedbackPriorities() {
		return feedbackService.getFeedbackPriorities();
	}

	@InitBinder({"feedback", "feedbackFilters"})
	private void initBaseFeedbackBinder(WebDataBinder binder) {
		binder.registerCustomEditor(FeedbackConcern.class, new LookupEntityEditor(FeedbackConcern.class));
		binder.registerCustomEditor(FeedbackPriority.class, new LookupEntityEditor(FeedbackPriority.class));
	}

	@ModelAttribute("feedback")
	private FeedbackDTO populateFeedback() {
		return new FeedbackDTO();
	}

	@RequestMapping(method = GET)
	public String getFeedbackPage(@ModelAttribute("feedback") FeedbackDTO feedback,
		Model model) {
		return "redirect:/home";
		// model.addAttribute("uploadTypes", MimeTypeUtilities.getMimeTypesForPage("/feedback"));
		// return "web/pages/feedback/index";
	}

	@RequestMapping(method = POST, produces = APPLICATION_JSON_VALUE)
	public String sendFeedback(@ModelAttribute("feedback") FeedbackDTO feedback,
		BindingResult bindingResult,
		RedirectAttributes flash,
		HttpServletRequest request,
		WebRequest webRequest) {

		String view = "redirect:/feedback";
		String userAgent = request.getHeader("User-Agent");

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		feedback.setUserId(getCurrentUser().getId());
		feedback.setCompanyId(getCurrentUser().getCompanyId());
		feedback.setUserAgent(userAgent);

		validator.validate(feedback, bindingResult);

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(bundle, bindingResult);
			return view;
		}

		//create assignment
		feedbackService.convertFeedbackToWorkAndSend(feedback);
		messageHelper.addSuccess(bundle, "feedback.alert.success");
		webRequest.removeAttribute("feedback", WebRequest.SCOPE_SESSION);
		return view;
	}
}
