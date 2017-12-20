package com.workmarket.service.business.queue;

import com.workmarket.domains.model.User;
import com.workmarket.service.infra.event.EventRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class WorkEventProcessQueueImpl implements WorkEventProcessQueue {
	private static final long delayTimeMillis = 60000;

	@Autowired private EventRouter eventRouter;
	
	@Override
	public void onWorkPaid(Long workId, Long workResourceId, Calendar date, User actor) {
		WorkPaidDelayedEvent delay = new WorkPaidDelayedEvent(workId, workResourceId, delayTimeMillis, date, actor);
		eventRouter.sendEvent(delay);
	}
}
