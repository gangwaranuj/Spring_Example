package com.workmarket.service.business.queue;

import java.util.List;

import com.workmarket.domains.work.service.audit.WorkActionRequest;

public interface WorkUploadProcessQueue {
	
	void onWorkUploaded(List<Long> workIds, WorkActionRequest workActionRequest);
}
