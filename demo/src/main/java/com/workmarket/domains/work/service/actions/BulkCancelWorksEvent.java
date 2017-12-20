package com.workmarket.domains.work.service.actions;

import com.workmarket.domains.model.User;

import java.util.Set;

public class BulkCancelWorksEvent extends AbstractWorkEvent {

	final private String note;
	final private Double price;
	final private String cancellationReasonTypeCode;

	public static class Builder extends AbstractWorkEvent.Builder {
		final private String note;
		final private Double price;
		final private String cancellationReasonTypeCode;

		public Builder(Set<Long> workIds, User user, String actionName, String messageKey, String note, Double price, String cancellationReasonTypeCode) {
			super(workIds, user, actionName, messageKey);
			this.note = note;
			this.price = price;
			this.cancellationReasonTypeCode = cancellationReasonTypeCode;
		}

		@Override
		public BulkCancelWorksEvent build() {
			return new BulkCancelWorksEvent(this);
		}
	}

	private BulkCancelWorksEvent(Builder builder) {
		super(builder);
		this.note = builder.note;
		this.price = builder.price;
		this.cancellationReasonTypeCode = builder.cancellationReasonTypeCode;
	}

	public String getNote() {
		return note;
	}

	public Double getPrice() {
		return price;
	}

	public String getCancellationReasonTypeCode() {
		return cancellationReasonTypeCode;
	}
}
