package com.workmarket.thrift.search.cart;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class SearchCartResponse implements Serializable {

	private static final long serialVersionUID = -3824283410490986281L;

	private List<SearchCartResult> result;
	private Cart currentCart;

	public SearchCartResponse() {}

	public SearchCartResponse(List<SearchCartResult> result, Cart currentCart) {
		this.result = result;
		this.currentCart = currentCart;
	}

	public int getResultSize() {
		return (this.result == null) ? 0 : this.result.size();
	}

	public java.util.Iterator<SearchCartResult> getResultIterator() {
		return (this.result == null) ? null : this.result.iterator();
	}

	public void addToResult(SearchCartResult elem) {
		if (this.result == null) {
			this.result = new ArrayList<SearchCartResult>();
		}
		this.result.add(elem);
	}

	public List<SearchCartResult> getResult() {
		return this.result;
	}

	public SearchCartResponse setResult(List<SearchCartResult> result) {
		this.result = result;
		return this;
	}

	public boolean isSetResult() {
		return this.result != null;
	}

	public Cart getCurrentCart() {
		return this.currentCart;
	}

	public SearchCartResponse setCurrentCart(Cart currentCart) {
		this.currentCart = currentCart;
		return this;
	}

	public boolean isSetCurrentCart() {
		return this.currentCart != null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("SearchCartResponse(");
		boolean first = true;

		sb.append("result:");
		if (this.result == null) {
			sb.append("null");
		} else {
			sb.append(this.result);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("currentCart:");
		if (this.currentCart == null) {
			sb.append("null");
		} else {
			sb.append(this.currentCart);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof SearchCartResponse)) {
			return false;
		}

		SearchCartResponse response = (SearchCartResponse) o;

		return new EqualsBuilder()
			.append(currentCart, response.getCurrentCart())
			.append(result, response.getResult())
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
			.append(currentCart)
			.append(result)
			.toHashCode();
	}
}