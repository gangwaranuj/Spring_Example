package com.workmarket.domains.model.feedback;

import java.io.Serializable;
import java.util.List;

public class FeedbackFilters implements Serializable {
	private List<FeedbackConcern> concerns;
	private List<FeedbackPriority> priorities;

	public List<FeedbackPriority> getPriorities() {
		return priorities;
	}

	public void setPriorities(List<FeedbackPriority> priorities) {
		this.priorities = priorities;
	}
	public List<FeedbackConcern> getConcerns() {
		return concerns;
	}

	public void setConcerns(List<FeedbackConcern> concerns) {
		this.concerns = concerns;
	}
}
