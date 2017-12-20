package com.workmarket.thrift.core;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Profile implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private Address address;
	private List<Phone> phoneNumbers;

	public Profile() {
	}

	public Profile(long id, Address address, List<Phone> phoneNumbers) {
		this();
		this.id = id;
		this.address = address;
		this.phoneNumbers = phoneNumbers;
	}

	public long getId() {
		return this.id;
	}

	public Profile setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public Address getAddress() {
		return this.address;
	}

	public Profile setAddress(Address address) {
		this.address = address;
		return this;
	}

	public boolean isSetAddress() {
		return this.address != null;
	}

	public int getPhoneNumbersSize() {
		return (this.phoneNumbers == null) ? 0 : this.phoneNumbers.size();
	}

	public java.util.Iterator<Phone> getPhoneNumbersIterator() {
		return (this.phoneNumbers == null) ? null : this.phoneNumbers.iterator();
	}

	public void addToPhoneNumbers(Phone elem) {
		if (this.phoneNumbers == null) {
			this.phoneNumbers = new ArrayList<Phone>();
		}
		this.phoneNumbers.add(elem);
	}

	public List<Phone> getPhoneNumbers() {
		return this.phoneNumbers;
	}

	public Profile setPhoneNumbers(List<Phone> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
		return this;
	}

	public boolean isSetPhoneNumbers() {
		return this.phoneNumbers != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof Profile)
			return this.equals((Profile) that);
		return false;
	}

	private boolean equals(Profile that) {
		if (that == null)
			return false;

		boolean this_present_id = true;
		boolean that_present_id = true;
		if (this_present_id || that_present_id) {
			if (!(this_present_id && that_present_id))
				return false;
			if (this.id != that.id)
				return false;
		}

		boolean this_present_address = true && this.isSetAddress();
		boolean that_present_address = true && that.isSetAddress();
		if (this_present_address || that_present_address) {
			if (!(this_present_address && that_present_address))
				return false;
			if (!this.address.equals(that.address))
				return false;
		}

		boolean this_present_phoneNumbers = true && this.isSetPhoneNumbers();
		boolean that_present_phoneNumbers = true && that.isSetPhoneNumbers();
		if (this_present_phoneNumbers || that_present_phoneNumbers) {
			if (!(this_present_phoneNumbers && that_present_phoneNumbers))
				return false;
			if (!this.phoneNumbers.equals(that.phoneNumbers))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_id = true;
		builder.append(present_id);
		if (present_id)
			builder.append(id);

		boolean present_address = true && (isSetAddress());
		builder.append(present_address);
		if (present_address)
			builder.append(address);

		boolean present_phoneNumbers = true && (isSetPhoneNumbers());
		builder.append(present_phoneNumbers);
		if (present_phoneNumbers)
			builder.append(phoneNumbers);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Profile(");
		boolean first = true;

		sb.append("id:");
		sb.append(this.id);
		first = false;
		if (!first) sb.append(", ");
		sb.append("address:");
		if (this.address == null) {
			sb.append("null");
		} else {
			sb.append(this.address);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("phoneNumbers:");
		if (this.phoneNumbers == null) {
			sb.append("null");
		} else {
			sb.append(this.phoneNumbers);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

