package com.workmarket.service.business.queue;


public interface WorkEventQueueService {

	void onWorkPaid(WorkPaidDelayedEvent delay);
	
	void onWorkUploaded(WorkUploadDelayedEvent delay);

}
