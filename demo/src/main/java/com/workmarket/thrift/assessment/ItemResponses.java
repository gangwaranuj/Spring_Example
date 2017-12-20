package com.workmarket.thrift.assessment;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ItemResponses implements Serializable {
	private static final long serialVersionUID = 1L;

	private long itemId;
	private List<Response> responses;

	public ItemResponses() {
	}

	public ItemResponses(
			long itemId,
			List<Response> responses) {
		this();
		this.itemId = itemId;
		this.responses = responses;
	}

	public long getItemId() {
		return this.itemId;
	}

	public ItemResponses setItemId(long itemId) {
		this.itemId = itemId;
		return this;
	}

	public boolean isSetItemId() {
		return (itemId > 0L);
	}

	public int getResponsesSize() {
		return (this.responses == null) ? 0 : this.responses.size();
	}

	public java.util.Iterator<Response> getResponsesIterator() {
		return (this.responses == null) ? null : this.responses.iterator();
	}

	public void addToResponses(Response elem) {
		if (this.responses == null) {
			this.responses = new ArrayList<Response>();
		}
		this.responses.add(elem);
	}

	public List<Response> getResponses() {
		return this.responses;
	}

	public ItemResponses setResponses(List<Response> responses) {
		this.responses = responses;
		return this;
	}

	public boolean isSetResponses() {
		return this.responses != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof ItemResponses)
			return this.equals((ItemResponses) that);
		return false;
	}

	private boolean equals(ItemResponses that) {
		if (that == null)
			return false;

		boolean this_present_itemId = true;
		boolean that_present_itemId = true;
		if (this_present_itemId || that_present_itemId) {
			if (!(this_present_itemId && that_present_itemId))
				return false;
			if (this.itemId != that.itemId)
				return false;
		}

		boolean this_present_responses = true && this.isSetResponses();
		boolean that_present_responses = true && that.isSetResponses();
		if (this_present_responses || that_present_responses) {
			if (!(this_present_responses && that_present_responses))
				return false;
			if (!this.responses.equals(that.responses))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_itemId = true;
		builder.append(present_itemId);
		if (present_itemId)
			builder.append(itemId);

		boolean present_responses = true && (isSetResponses());
		builder.append(present_responses);
		if (present_responses)
			builder.append(responses);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ItemResponses(");
		boolean first = true;

		sb.append("itemId:");
		sb.append(this.itemId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("responses:");
		if (this.responses == null) {
			sb.append("null");
		} else {
			sb.append(this.responses);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

