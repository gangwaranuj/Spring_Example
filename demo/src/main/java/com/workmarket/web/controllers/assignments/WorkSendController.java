package com.workmarket.web.controllers.assignments;


import com.workmarket.domains.work.service.WorkBatchSendRequest;
import com.workmarket.domains.work.service.WorkBatchSendService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.validators.WorkBatchSendRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.workmarket.utility.StringUtilities.pluralize;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = "/assignments/batch_send")
@PreAuthorize("hasAnyRole('PERMISSION_MANAGEMYWORK', 'PERMISSION_MANAGECOWORK')")
public class WorkSendController extends BaseWorkController {

	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private WorkBatchSendRequestValidator workBatchSendFormValidator;
	@Autowired private WorkBatchSendService workBatchSendService;

	@RequestMapping(
		value = "/specific_talent",
		method = POST,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder specificTalent(
		@RequestBody WorkBatchSendRequest workBatchSendRequest,
		BindingResult bindingResult) {

		workBatchSendFormValidator.validate(workBatchSendRequest, bindingResult);
		if (bindingResult.hasErrors()) {
			return specificTalentBatchSendErrorResponse();
		}

		workBatchSendService.sendWork(workBatchSendRequest);

		return specificTalentBatchSendSuccessResponse(workBatchSendRequest);
	}

	private AjaxResponseBuilder specificTalentBatchSendErrorResponse() {
		String message = messageHelper.getMessage("work.batch.send.specific_talent.required");
		return AjaxResponseBuilder.fail().addMessage(message);
	}

	private AjaxResponseBuilder specificTalentBatchSendSuccessResponse(WorkBatchSendRequest workBatchSendRequest) {
		String message = messageHelper.getMessage("work.batch.send.success",
			workBatchSendRequest.getWorkNumbers().size(), pluralize("assignment", workBatchSendRequest.getWorkNumbers().size()));
		return AjaxResponseBuilder.success().addMessage(message);
	}

	@RequestMapping(
		value = "/work_send",
		method = POST,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder workSend(@RequestBody WorkBatchSendRequest workBatchSendRequest) {

		List<String> workNumbers = workBatchSendRequest.getWorkNumbers();

		if (workNumbers == null) {
			return AjaxResponseBuilder.fail();
		}

		workBatchSendService.sendWorkBatchViaWorkSend(workNumbers);
		String message = messageHelper.getMessage("work.batch.send.success", workNumbers.size(), pluralize("assignment", workNumbers.size()));
		AjaxResponseBuilder response = AjaxResponseBuilder.success();
		messageHelper.addMessage(response, message);

		return response;
	}

	@RequestMapping(
		value = "/routable_groups",
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody List<Object> getRoutableGroupList() {
		Map<Long, String> routableGroups = getRoutableGroups();
		List<Object> routableGroupsJson = new ArrayList<>();
		for (Map.Entry<Long, String> entry : routableGroups.entrySet()) {
			Map<String, Object> routableGroup = CollectionUtilities.newObjectMap();
			routableGroup.put("id", entry.getKey());
			routableGroup.put("name", entry.getValue());
			routableGroupsJson.add(routableGroup);
		}
		return routableGroupsJson;
	}
}
