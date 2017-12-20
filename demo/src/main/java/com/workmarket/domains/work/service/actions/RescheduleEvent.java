package com.workmarket.domains.work.service.actions;

import com.google.common.base.Optional;
import com.workmarket.domains.model.User;

import java.util.List;

public class RescheduleEvent extends AbstractWorkEvent {

	private static final long serialVersionUID = -876677739383721896L;
	final private String note;

	final private Optional<String> startDateTime;
	final private Optional<String> startDateTimeFormat;
	final private Optional<String> endDateTime;
	final private Optional<String> endDateTimeFormat;

	public static class Builder extends AbstractWorkEvent.Builder{

		final private String note;

		private Optional<String> startDateTime;
		private Optional<String> startDateTimeFormat;
		private Optional<String> endDateTime;
		private Optional<String> endDateTimeFormat;

		public Builder(
				final List<String> workNumbers,
				final User user,
				final String note,
				final String actionName,
				final String messageKey) {
			super(workNumbers, user, actionName, messageKey);
			this.note = note;
		}

		public Builder setStartDateTime(final Optional<String> startDateTime) {
			this.startDateTime = startDateTime;
			return this;
		}

		public Builder setStartDateTimeFormat(final Optional<String> startDateTimeFormat) {
			this.startDateTimeFormat = startDateTimeFormat;
			return this;
		}

		public Builder setEndDateTime(final Optional<String> endDateTime) {
			this.endDateTime = endDateTime;
			return this;
		}

		public Builder setEndDateTimeFormat(final Optional<String> endDateTimeFormat) {
			this.endDateTimeFormat = endDateTimeFormat;
			return this;
		}

		@Override
		public RescheduleEvent build(){
			return new RescheduleEvent(this);
		}
	}

	private RescheduleEvent(Builder builder) {
		super(builder);
		this.note = builder.note;
		this.startDateTime = builder.startDateTime;
		this.startDateTimeFormat = builder.startDateTimeFormat;
		this.endDateTime = builder.endDateTime;
		this.endDateTimeFormat = builder.endDateTimeFormat;
	}

	public String getNote() {
		return note;
	}

	public Optional<String> getStartDateTime() {
		return startDateTime;
	}

	public Optional<String> getStartDateTimeFormat() {
		return startDateTimeFormat;
	}

	public Optional<String> getEndDateTime() {
		return endDateTime;
	}

	public Optional<String> getEndDateTimeFormat() {
		return endDateTimeFormat;
	}
}
