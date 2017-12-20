package com.workmarket.web.helpers;

import com.workmarket.domains.model.User;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.service.business.UserService;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.thrift.work.WorkSaveRequest;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WorkAuthorizationFailureHelper {

	@Autowired MessageBundleHelper messageHelper;
	@Autowired UserService userService;

	public void handleErrorsFromAuthResponse(WorkAuthorizationResponse workAuthorizationResponse, Work work, MessageBundle messageBundle) {
		String messageKey = "search.cart.push.assignment.generic_spend_authorization_error";
		Object args = null;

		if (workAuthorizationResponse == WorkAuthorizationResponse.INSUFFICIENT_FUNDS) {
			messageKey = work.getConfiguration().isSetPaymentTermsDays() ? "search.cart.push.assignment.insufficient_funds_terms" : "search.cart.push.assignment.insufficient_funds_prefund";

		} else if (workAuthorizationResponse == WorkAuthorizationResponse.INSUFFICIENT_BUDGET) {
			messageKey = workAuthorizationResponse.getMessagePropertyKey();

		} else if (workAuthorizationResponse == WorkAuthorizationResponse.INSUFFICIENT_SPEND_LIMIT) {
			messageKey = workAuthorizationResponse.getMessagePropertyKey();
			args = work.getBuyer().getName().getFullName();

		} else if (workAuthorizationResponse == WorkAuthorizationResponse.INVALID_SPEND_LIMIT) {
			messageKey = workAuthorizationResponse.getMessagePropertyKey();
		}

		messageHelper.addError(messageBundle, messageKey, args);
	}


	public void handleErrorsFromAuthResponse(WorkSaveRequest saveRequest, WorkResponse workResponse, MessageBundle messages) {
		if (workResponse == null || CollectionUtils.isEmpty(workResponse.getWorkAuthorizationResponses())) {
			return;
		}

		if (workResponse.getWorkAuthorizationResponses().contains(WorkAuthorizationResponse.INSUFFICIENT_FUNDS)) {
			messageHelper.addError(messages, "work.form.insufficient_funds");
		}
		if (workResponse.getWorkAuthorizationResponses().contains(WorkAuthorizationResponse.INSUFFICIENT_BUDGET)) {
			messageHelper.addError(messages, "work.form.insufficient_project_budget");
		}
		if (workResponse.getWorkAuthorizationResponses().contains(WorkAuthorizationResponse.INVALID_BUNDLE_STATE)) {
			messageHelper.addError(messages, "work.form.invalid_bundle_state");
		}
		if (workResponse.getWorkAuthorizationResponses().contains(WorkAuthorizationResponse.INSUFFICIENT_SPEND_LIMIT)) {
			User actor = userService.getUser(saveRequest.getWork().getBuyer().getId());
			String actorNoun = saveRequest.getUserId() == actor.getId() ? "your" : actor.getFullName() + "'s";
			messageHelper.addError(messages, "work.form.spend_limit_exceeded", actorNoun, String.format("$%.02f", actor.getSpendLimit()));
		}
	}
}
