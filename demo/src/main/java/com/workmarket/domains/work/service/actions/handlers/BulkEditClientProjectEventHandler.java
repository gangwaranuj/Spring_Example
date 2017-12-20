package com.workmarket.domains.work.service.actions.handlers;


import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.actions.AbstractWorkEvent;
import com.workmarket.domains.work.service.actions.BulkEditClientProjectEvent;
import com.workmarket.domains.work.service.project.ProjectService;
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
public class BulkEditClientProjectEventHandler implements WorkEventHandler {

	protected static final Logger logger = LoggerFactory.getLogger(BulkEditClientProjectEventHandler.class);
	@Autowired MessageBundleHelper messageHelper;
	@Autowired ProjectService projectService;
	@Autowired WorkService workService;

	@Override
	public AjaxResponseBuilder handleEvent(AbstractWorkEvent event) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Assert.notNull(event);
		Assert.isTrue(event instanceof BulkEditClientProjectEvent);
		@SuppressWarnings("ConstantConditions") BulkEditClientProjectEvent bulkEditClientProjectEvent = (BulkEditClientProjectEvent) event;
		List<Work> works = bulkEditClientProjectEvent.getWorks();

		if (!event.isValid()) {
			messageHelper.addMessage(event.getResponse().setSuccessful(false), event.getMessageKey() + ".empty");
			return event.getResponse();
		}

		for (Work work : works) {
			try {
				workService.updateClientAndProject(bulkEditClientProjectEvent.getClient(), bulkEditClientProjectEvent.getProject(), work.getId());
			} catch (Exception ex) {
				logger.error(String.format("Exception while changing project/client for assignment: %d", work.getId()), ex);
			}

		}
		messageHelper.addMessage(event.getResponse().setSuccessful(true), event.getMessageKey() + ".success");
		stopWatch.stop();

		logger.info(String.format("[change_project_client] completed in %d seconds and changed %d assignments",
				stopWatch.getTotalTimeMillis(),
				works.size()));
		return event.getResponse();
	}
}
