package com.workmarket.domains.work.service.actions;

import com.workmarket.domains.model.User;

import java.util.List;
import java.util.Set;

/**
 * author: gbluv
 */
public class BulkLabelRemovalEvent extends AbstractWorkEvent {

	private static final long serialVersionUID = -7590129628906135893L;
	final private String note;
	final private List<Long> labelIds;

	public static class Builder extends AbstractWorkEvent.Builder{
		  final private String note;
			final private List<Long> labelIds;

		public Builder(Set<Long> workIds, User user, String note, List<Long> labelIds, String actionName, String messageKey) {
			super(workIds, user, actionName, messageKey);
			this.note = note;
			this.labelIds = labelIds;
		}
		@Override
		public BulkLabelRemovalEvent build(){
			return new BulkLabelRemovalEvent(this);
		}
	}
	private BulkLabelRemovalEvent(Builder builder) {
		super(builder);
		this.note = builder.note;
		this.labelIds = builder.labelIds;
	}

	public String getNote() {
		return note;
	}

	public List<Long> getLabelIds() {
		return labelIds;
	}
}
