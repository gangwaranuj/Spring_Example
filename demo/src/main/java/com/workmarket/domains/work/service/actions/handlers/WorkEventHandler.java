package com.workmarket.domains.work.service.actions.handlers;

import com.workmarket.domains.work.service.actions.AbstractWorkEvent;
import com.workmarket.web.helpers.AjaxResponseBuilder;

public interface WorkEventHandler {
	public AjaxResponseBuilder handleEvent(AbstractWorkEvent workEvent);
}
