package com.workmarket.thrift.search.cart;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class SearchCartRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private String cartOwnerNumber;
	private Set<String> userNumbers;

	public SearchCartRequest() {
	}

	public SearchCartRequest(String cartOwnerNumber, Set<String> userNumbers) {
		this();
		this.cartOwnerNumber = cartOwnerNumber;
		this.userNumbers = userNumbers;
	}

	public String getCartOwnerNumber() {
		return this.cartOwnerNumber;
	}

	public SearchCartRequest setCartOwnerNumber(String cartOwnerNumber) {
		this.cartOwnerNumber = cartOwnerNumber;
		return this;
	}

	public boolean isSetCartOwnerNumber() {
		return this.cartOwnerNumber != null;
	}

	public int getUserNumbersSize() {
		return (this.userNumbers == null) ? 0 : this.userNumbers.size();
	}

	public java.util.Iterator<String> getUserNumbersIterator() {
		return (this.userNumbers == null) ? null : this.userNumbers.iterator();
	}

	public void addToUserNumbers(String elem) {
		if (this.userNumbers == null) {
			this.userNumbers = new HashSet<String>();
		}
		this.userNumbers.add(elem);
	}

	public Set<String> getUserNumbers() {
		return this.userNumbers;
	}

	public SearchCartRequest setUserNumbers(Set<String> userNumbers) {
		this.userNumbers = userNumbers;
		return this;
	}

	public boolean isSetUserNumbers() {
		return this.userNumbers != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof SearchCartRequest)
			return this.equals((SearchCartRequest) that);
		return false;
	}

	private boolean equals(SearchCartRequest that) {
		if (that == null)
			return false;

		boolean this_present_cartOwnerNumber = true && this.isSetCartOwnerNumber();
		boolean that_present_cartOwnerNumber = true && that.isSetCartOwnerNumber();
		if (this_present_cartOwnerNumber || that_present_cartOwnerNumber) {
			if (!(this_present_cartOwnerNumber && that_present_cartOwnerNumber))
				return false;
			if (!this.cartOwnerNumber.equals(that.cartOwnerNumber))
				return false;
		}

		boolean this_present_userNumbers = true && this.isSetUserNumbers();
		boolean that_present_userNumbers = true && that.isSetUserNumbers();
		if (this_present_userNumbers || that_present_userNumbers) {
			if (!(this_present_userNumbers && that_present_userNumbers))
				return false;
			if (!this.userNumbers.equals(that.userNumbers))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_cartOwnerNumber = true && (isSetCartOwnerNumber());
		builder.append(present_cartOwnerNumber);
		if (present_cartOwnerNumber)
			builder.append(cartOwnerNumber);

		boolean present_userNumbers = true && (isSetUserNumbers());
		builder.append(present_userNumbers);
		if (present_userNumbers)
			builder.append(userNumbers);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("SearchCartRequest(");
		boolean first = true;

		sb.append("cartOwnerNumber:");
		if (this.cartOwnerNumber == null) {
			sb.append("null");
		} else {
			sb.append(this.cartOwnerNumber);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("userNumbers:");
		if (this.userNumbers == null) {
			sb.append("null");
		} else {
			sb.append(this.userNumbers);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

