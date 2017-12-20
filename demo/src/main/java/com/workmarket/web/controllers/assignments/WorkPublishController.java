package com.workmarket.web.controllers.assignments;

import com.workmarket.domains.work.service.WorkPublishService;
import com.workmarket.service.exception.WorkMarketException;
import com.workmarket.thrift.work.WorkPublishRequest;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.validation.Valid;

import static com.workmarket.utility.StringUtilities.pluralize;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = "/assignments/publish")
@PreAuthorize("hasAnyRole('PERMISSION_MANAGEMYWORK', 'PERMISSION_MANAGECOWORK')")
public class WorkPublishController extends BaseWorkController {

	@Resource private MessageBundleHelper messageHelper;
	@Resource private WorkPublishService workPublishService;

	@RequestMapping(
		value = "/marketplace/publish",
		method = POST,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody
	AjaxResponseBuilder publish(
		@RequestBody @Valid WorkPublishRequest workPublishRequest,
		BindingResult bindingResult) {

		try {
			workPublishService.publish(workPublishRequest);
			return getSuccessResponse(workPublishRequest, "work.publish.marketplace.add.success");
		} catch (WorkMarketException e) {
			return getErrorResponse(workPublishRequest, "work.publish.marketplace.add.error");
		}
	}

	@RequestMapping(
		value = "/marketplace/remove",
		method = POST,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody
	AjaxResponseBuilder remove(
		@RequestBody @Valid WorkPublishRequest workPublishRequest,
		BindingResult bindingResult) {

		try {
			workPublishService.removeFromFeed(workPublishRequest);
			return getSuccessResponse(workPublishRequest, "work.publish.marketplace.remove.success");
		} catch (WorkMarketException e) {
			return getErrorResponse(workPublishRequest, "work.publish.marketplace.remove.error");
		}
	}

	private AjaxResponseBuilder getSuccessResponse(WorkPublishRequest workPublishRequest, String messageKey) {
		String message = getMessage(workPublishRequest, messageKey);
		AjaxResponseBuilder response = AjaxResponseBuilder.success();
		messageHelper.addMessage(response, message);
		return response;
	}

	private AjaxResponseBuilder getErrorResponse(WorkPublishRequest workPublishRequest, String messageKey) {
		String message = getMessage(workPublishRequest, messageKey);
		AjaxResponseBuilder response = AjaxResponseBuilder.fail();
		messageHelper.addMessage(response, message);
		return response;
	}

	private String getMessage(WorkPublishRequest workPublishRequest, String messageKey) {
		return messageHelper.getMessage(messageKey,
				workPublishRequest.getWorkNumbers().size(),
				pluralize("assignment", workPublishRequest.getWorkNumbers().size()));
	}
}
