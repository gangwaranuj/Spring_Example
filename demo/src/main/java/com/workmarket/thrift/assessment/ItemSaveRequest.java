package com.workmarket.thrift.assessment;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class ItemSaveRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private long userId;
	private long assessmentId;
	private Item item;

	public ItemSaveRequest() {
	}

	public ItemSaveRequest(long userId, long assessmentId, Item item) {
		this();
		this.userId = userId;
		this.assessmentId = assessmentId;
		this.item = item;
	}

	public long getUserId() {
		return this.userId;
	}

	public ItemSaveRequest setUserId(long userId) {
		this.userId = userId;
		return this;
	}

	public boolean isSetUserId() {
		return (userId > 0L);
	}

	public long getAssessmentId() {
		return this.assessmentId;
	}

	public ItemSaveRequest setAssessmentId(long assessmentId) {
		this.assessmentId = assessmentId;
		return this;
	}

	public boolean isSetAssessmentId() {
		return (assessmentId > 0L);
	}

	public Item getItem() {
		return this.item;
	}

	public ItemSaveRequest setItem(Item item) {
		this.item = item;
		return this;
	}

	public boolean isSetItem() {
		return this.item != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof ItemSaveRequest)
			return this.equals((ItemSaveRequest) that);
		return false;
	}

	private boolean equals(ItemSaveRequest that) {
		if (that == null)
			return false;

		boolean this_present_userId = true;
		boolean that_present_userId = true;
		if (this_present_userId || that_present_userId) {
			if (!(this_present_userId && that_present_userId))
				return false;
			if (this.userId != that.userId)
				return false;
		}

		boolean this_present_assessmentId = true;
		boolean that_present_assessmentId = true;
		if (this_present_assessmentId || that_present_assessmentId) {
			if (!(this_present_assessmentId && that_present_assessmentId))
				return false;
			if (this.assessmentId != that.assessmentId)
				return false;
		}

		boolean this_present_item = true && this.isSetItem();
		boolean that_present_item = true && that.isSetItem();
		if (this_present_item || that_present_item) {
			if (!(this_present_item && that_present_item))
				return false;
			if (!this.item.equals(that.item))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_userId = true;
		builder.append(present_userId);
		if (present_userId)
			builder.append(userId);

		boolean present_assessmentId = true;
		builder.append(present_assessmentId);
		if (present_assessmentId)
			builder.append(assessmentId);

		boolean present_item = true && (isSetItem());
		builder.append(present_item);
		if (present_item)
			builder.append(item);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ItemSaveRequest(");
		boolean first = true;

		sb.append("userId:");
		sb.append(this.userId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("assessmentId:");
		sb.append(this.assessmentId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("item:");
		if (this.item == null) {
			sb.append("null");
		} else {
			sb.append(this.item);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

