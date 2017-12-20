package com.workmarket.domains.work.service.actions.handlers;

import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.service.business.dto.CancelWorkDTO;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.actions.AbstractWorkEvent;
import com.workmarket.domains.work.service.actions.BulkCancelWorksEvent;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;


import java.util.List;

@Service
public class BulkCancelWorksEventHandler implements WorkEventHandler {

	protected static final Logger logger = LoggerFactory.getLogger(BulkCancelWorksEventHandler.class);
	@Autowired MessageBundleHelper messageHelper;
	@Autowired protected WorkService workService;

	@Override
	public AjaxResponseBuilder handleEvent(AbstractWorkEvent event) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Assert.notNull(event);
		Assert.isTrue(event instanceof BulkCancelWorksEvent);
		BulkCancelWorksEvent bulkCancelWorksEvent = (BulkCancelWorksEvent) event;
		if (!event.isValid()) {
			messageHelper.addMessage(event.getResponse().setSuccessful(false), event.getMessageKey() + ".empty");
			return event.getResponse();
		}

		for (Long workId : event.getWorkIds()) {
			try {
				CancelWorkDTO cancelWorkDTO = new CancelWorkDTO(workId, bulkCancelWorksEvent.getPrice(), bulkCancelWorksEvent.getCancellationReasonTypeCode(), bulkCancelWorksEvent.getNote());
				List<ConstraintViolation> violations = workService.cancelWork(cancelWorkDTO);
				if (isNotEmpty(violations)) {
					logger.error(String.format("assignment %d could not be canceled because it has a violation errors", workId), violations);
				}
			} catch (Exception ex) {
				logger.error(String.format("Exception while canceling assignment: %d", workId), ex);
			}
		}
		messageHelper.addMessage(event.getResponse().setSuccessful(true), event.getMessageKey() + ".success");
		stopWatch.stop();
		float time = stopWatch.getTotalTimeMillis()/1000;

		logger.info(String.format("[bulk_cancel_works] completed in %.2f seconds and worked on %d assignments", time, event.getWorkIds().size()));
		return event.getResponse();
	}
}
