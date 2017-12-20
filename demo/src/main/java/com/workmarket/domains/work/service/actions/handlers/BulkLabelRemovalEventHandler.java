package com.workmarket.domains.work.service.actions.handlers;

import com.workmarket.domains.work.service.actions.AbstractWorkEvent;
import com.workmarket.domains.work.service.actions.BulkLabelRemovalEvent;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.domains.model.User;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;


import java.util.List;

@Service
public class BulkLabelRemovalEventHandler implements WorkEventHandler {
	protected static final Logger logger = LoggerFactory.getLogger(BulkLabelRemovalEventHandler.class);
	@Autowired MessageBundleHelper messageHelper;
	@Autowired WorkSubStatusService workSubStatusService;

	@Override
	public AjaxResponseBuilder handleEvent(AbstractWorkEvent event) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Assert.notNull(event);
		Assert.isTrue(event instanceof BulkLabelRemovalEvent);
		BulkLabelRemovalEvent bulkLabelRemovalEvent = (BulkLabelRemovalEvent) event;
		if (!event.isValid()) {
			messageHelper.addMessage(event.getResponse().setSuccessful(false), event.getMessageKey() + ".empty");
			return event.getResponse();
		}

		List<Long> labelIds = bulkLabelRemovalEvent.getLabelIds();
		User user = bulkLabelRemovalEvent.getUser();
		String note = bulkLabelRemovalEvent.getNote();
		for (Long labelId : labelIds) {
			for (Long workId : event.getWorkIds()) {
				workSubStatusService.resolveSubStatus(user.getId(), workId, labelId, note);
			}
		}
		messageHelper.addMessage(event.getResponse().setSuccessful(true), event.getMessageKey() + ".success");
		stopWatch.stop();
		float time = stopWatch.getTotalTimeMillis()/1000;
		logger.info(String.format(
			"[bulk_label_removal] completed in %.2f seconds and removed %d label(s) from %d assignments",
			time,
			labelIds.size(),
			event.getWorkIds().size()
		));
		return event.getResponse();
	}
}
