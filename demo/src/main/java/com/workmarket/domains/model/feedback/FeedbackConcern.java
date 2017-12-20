package com.workmarket.domains.model.feedback;

import com.workmarket.domains.model.OrderedLookupEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="feedbackConcern")
@Table(name="feedback_concern_type")
public class FeedbackConcern extends OrderedLookupEntity implements Comparable<FeedbackConcern> {

	public FeedbackConcern (){}

	private FeedbackConcern(String code) {
		super(code);
	}

	private static FeedbackConcern newInstance(String code) {
		return new FeedbackConcern(code);
	}

	@Override
	public int compareTo(FeedbackConcern other) {
		int result = this.getDescription().compareTo(other.getDescription());
		return result != 0 ? result : 0;
	}
}
