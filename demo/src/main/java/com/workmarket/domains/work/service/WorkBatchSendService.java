package com.workmarket.domains.work.service;

import java.util.List;

public interface WorkBatchSendService {
	void sendWorkBatchViaWorkSend(List<String> workNumbers);
	void sendWork(WorkBatchSendRequest workBatchSendRequest);
}
