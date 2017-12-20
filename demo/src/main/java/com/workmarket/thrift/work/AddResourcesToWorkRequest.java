package com.workmarket.thrift.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class AddResourcesToWorkRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private String workNumber;
	private String currentUserNumber;
	private Set<String> userNumbers;

	public AddResourcesToWorkRequest() {
	}

	public AddResourcesToWorkRequest(String workNumber, String currentUserNumber, Set<String> userNumbers) {
		this();
		this.workNumber = workNumber;
		this.currentUserNumber = currentUserNumber;
		this.userNumbers = userNumbers;
	}

	public String getWorkNumber() {
		return this.workNumber;
	}

	public AddResourcesToWorkRequest setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
		return this;
	}

	public boolean isSetWorkNumber() {
		return this.workNumber != null;
	}

	public String getCurrentUserNumber() {
		return this.currentUserNumber;
	}

	public AddResourcesToWorkRequest setCurrentUserNumber(String currentUserNumber) {
		this.currentUserNumber = currentUserNumber;
		return this;
	}

	public boolean isSetCurrentUserNumber() {
		return this.currentUserNumber != null;
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

	public AddResourcesToWorkRequest setUserNumbers(Set<String> userNumbers) {
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
		if (that instanceof AddResourcesToWorkRequest)
			return this.equals((AddResourcesToWorkRequest) that);
		return false;
	}

	private boolean equals(AddResourcesToWorkRequest that) {
		if (that == null)
			return false;

		boolean this_present_workNumber = true && this.isSetWorkNumber();
		boolean that_present_workNumber = true && that.isSetWorkNumber();
		if (this_present_workNumber || that_present_workNumber) {
			if (!(this_present_workNumber && that_present_workNumber))
				return false;
			if (!this.workNumber.equals(that.workNumber))
				return false;
		}

		boolean this_present_currentUserNumber = true && this.isSetCurrentUserNumber();
		boolean that_present_currentUserNumber = true && that.isSetCurrentUserNumber();
		if (this_present_currentUserNumber || that_present_currentUserNumber) {
			if (!(this_present_currentUserNumber && that_present_currentUserNumber))
				return false;
			if (!this.currentUserNumber.equals(that.currentUserNumber))
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

		boolean present_workNumber = true && (isSetWorkNumber());
		builder.append(present_workNumber);
		if (present_workNumber)
			builder.append(workNumber);

		boolean present_currentUserNumber = true && (isSetCurrentUserNumber());
		builder.append(present_currentUserNumber);
		if (present_currentUserNumber)
			builder.append(currentUserNumber);

		boolean present_userNumbers = true && (isSetUserNumbers());
		builder.append(present_userNumbers);
		if (present_userNumbers)
			builder.append(userNumbers);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("AddResourcesToWorkRequest(");
		boolean first = true;

		sb.append("workNumber:");
		if (this.workNumber == null) {
			sb.append("null");
		} else {
			sb.append(this.workNumber);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("currentUserNumber:");
		if (this.currentUserNumber == null) {
			sb.append("null");
		} else {
			sb.append(this.currentUserNumber);
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