package com.workmarket.service.business;

import com.workmarket.domains.model.feedback.FeedbackConcern;
import com.workmarket.domains.model.feedback.FeedbackPriority;
import com.workmarket.service.business.dto.FeedbackDTO;

import java.util.List;

public interface FeedbackService {
	public void convertFeedbackToWorkAndSend(FeedbackDTO feedback);

	public List<FeedbackConcern> getFeedbackConcerns();
	public List<FeedbackPriority> getFeedbackPriorities();
}
