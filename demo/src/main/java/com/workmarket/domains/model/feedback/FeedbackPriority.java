package com.workmarket.domains.model.feedback;

import com.workmarket.domains.model.OrderedLookupEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="feedbackPriority")
@Table(name="feedback_priority_type")
public class FeedbackPriority extends OrderedLookupEntity {

	public FeedbackPriority() {}

	private FeedbackPriority(String code) {
		super(code);
	}

	private static FeedbackPriority newInstance(String code) {
		return new FeedbackPriority(code);
	}
}
