package com.workmarket.domains.model.feedback;

import com.workmarket.domains.model.LookupEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="feedbackType")
@Table(name="feedback_type")
public class FeedbackType extends LookupEntity {

	private static final long serialVersionUID = -7120587655139883626L;

	public static final String PLATFORM = "platform";
	public static final String BUSINESS = "business";
	public static final String ENHANCEMENT = "enhancement";
	public static final String PRODUCT = "product";

	private Long mappedTemplateId;

	private FeedbackType() {}

	private FeedbackType(String code) {
		super(code);
	}

	@Column(name="mapped_template_id")
	public Long getMappedTemplateId() {
		return mappedTemplateId;
	}

	public void setMappedTemplateId(Long mappedTemplateId) {
		this.mappedTemplateId = mappedTemplateId;
	}

	private static FeedbackType newInstance(String code) {
		return new FeedbackType(code);
	}
}
