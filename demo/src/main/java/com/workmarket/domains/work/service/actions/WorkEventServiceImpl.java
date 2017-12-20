package com.workmarket.domains.work.service.actions;

import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class WorkEventServiceImpl implements WorkEventService {

	@Autowired MessageBundleHelper messageBundleHelper;
	@Autowired EventRouter eventRouter;

	@Override
	public AjaxResponseBuilder doAction(AbstractWorkEvent event) {
		Assert.notNull(event);

		if (!event.isValid()) {
			return event.getResponse().setSuccessful(false);
		}

		if (event.isQueue()) {
			event.setWorkEventHandler(null);
			eventRouter.sendEvent(event);
			messageBundleHelper.addMessage(event.getResponse().setSuccessful(true), event.getMessageKey() + ".success");
			return event.getResponse();
		}

		return event.handleEvent();
	}
}
