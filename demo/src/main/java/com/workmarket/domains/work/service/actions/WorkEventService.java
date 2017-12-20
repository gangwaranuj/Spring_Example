package com.workmarket.domains.work.service.actions;

import com.workmarket.web.helpers.AjaxResponseBuilder;

public interface WorkEventService {
	//Performs the action
	AjaxResponseBuilder doAction(AbstractWorkEvent workAction);
}
