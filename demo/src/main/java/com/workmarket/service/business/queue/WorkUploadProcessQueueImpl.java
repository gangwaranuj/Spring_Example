package com.workmarket.service.business.queue;

import static com.google.gdata.util.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.workmarket.service.SpringInitializedService;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.business.AuthenticationService;

@Service
public class WorkUploadProcessQueueImpl extends SpringInitializedService implements WorkUploadProcessQueue, Runnable {
	private final DelayQueue<WorkUploadDelayedEvent> queue;
	private static final long delayTimeMillis = 300000;
	private static final Log logger = LogFactory.getLog(WorkUploadProcessQueueImpl.class);
	private static final ExecutorService runner = Executors.newSingleThreadExecutor();
	private static final AtomicBoolean isRunning = new AtomicBoolean(false);
	@Autowired
	private WorkEventQueueService workEventQueueService;

	@Autowired
	private AuthenticationService authenticationService;

	public WorkUploadProcessQueueImpl() {
		this.queue = new DelayQueue<>();
	}

	@Override
	public void onWorkUploaded(List<Long> workIds, WorkActionRequest workActionRequest) {
		WorkUploadDelayedEvent workCreatedDelayedEvent = new WorkUploadDelayedEvent(workIds, workActionRequest, delayTimeMillis);
		queue.offer(workCreatedDelayedEvent);
	}

	@Override
	public void run() {
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		WorkUploadDelayedEvent workCreatedDelayedEvent;
		try {
			while (true) {
				workCreatedDelayedEvent = queue.take();
				try {
					workEventQueueService.onWorkUploaded(workCreatedDelayedEvent);
				} catch (Exception e) {
					logger.error("Event process failed for " + workCreatedDelayedEvent, e);
				}
			}
		} catch (InterruptedException e1) {
			logger.error(e1);
			return;
		}
	}

	@Override
	public void initialize() {
		checkNotNull(this.queue);
		if (!isRunning.get()) {
			isRunning.set(true);
			runner.execute(this);
		}
	}
}
