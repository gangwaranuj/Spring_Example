package com.workmarket.service.business.queue;

import java.util.Calendar;

import com.workmarket.domains.model.User;

public interface WorkEventProcessQueue {

	void onWorkPaid(Long workId, Long workResourceId, Calendar date, User actor);
}
