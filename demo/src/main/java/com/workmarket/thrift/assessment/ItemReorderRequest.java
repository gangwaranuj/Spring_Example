package com.workmarket.thrift.assessment;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ItemReorderRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private long userId;
	private long assessmentId;
	private List<Item> items;

	public ItemReorderRequest() {
	}

	public ItemReorderRequest(long userId, long assessmentId, List<Item> items) {
		this();
		this.userId = userId;
		this.assessmentId = assessmentId;
		this.items = items;
	}

	public long getUserId() {
		return this.userId;
	}

	public ItemReorderRequest setUserId(long userId) {
		this.userId = userId;
		return this;
	}

	public boolean isSetUserId() {
		return (userId > 0L);
	}

	public long getAssessmentId() {
		return this.assessmentId;
	}

	public ItemReorderRequest setAssessmentId(long assessmentId) {
		this.assessmentId = assessmentId;
		return this;
	}

	public boolean isSetAssessmentId() {
		return (assessmentId > 0L);
	}

	public int getItemsSize() {
		return (this.items == null) ? 0 : this.items.size();
	}

	public java.util.Iterator<Item> getItemsIterator() {
		return (this.items == null) ? null : this.items.iterator();
	}

	public void addToItems(Item elem) {
		if (this.items == null) {
			this.items = new ArrayList<Item>();
		}
		this.items.add(elem);
	}

	public List<Item> getItems() {
		return this.items;
	}

	public ItemReorderRequest setItems(List<Item> items) {
		this.items = items;
		return this;
	}

	public boolean isSetItems() {
		return this.items != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof ItemReorderRequest)
			return this.equals((ItemReorderRequest) that);
		return false;
	}

	private boolean equals(ItemReorderRequest that) {
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

		boolean this_present_items = true && this.isSetItems();
		boolean that_present_items = true && that.isSetItems();
		if (this_present_items || that_present_items) {
			if (!(this_present_items && that_present_items))
				return false;
			if (!this.items.equals(that.items))
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

		boolean present_items = true && (isSetItems());
		builder.append(present_items);
		if (present_items)
			builder.append(items);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ItemReorderRequest(");
		boolean first = true;

		sb.append("userId:");
		sb.append(this.userId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("assessmentId:");
		sb.append(this.assessmentId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("items:");
		if (this.items == null) {
			sb.append("null");
		} else {
			sb.append(this.items);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

