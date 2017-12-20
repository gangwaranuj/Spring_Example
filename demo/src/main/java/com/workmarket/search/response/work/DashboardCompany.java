package com.workmarket.search.response.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class DashboardCompany implements Serializable {
	private static final long serialVersionUID = 1L;

	private String companyName;
	private com.workmarket.thrift.core.Address address;

	public DashboardCompany() {
	}

	public String getCompanyName() {
		return this.companyName;
	}

	public DashboardCompany setCompanyName(String companyName) {
		this.companyName = companyName;
		return this;
	}

	public boolean isSetCompanyName() {
		return this.companyName != null;
	}

	public com.workmarket.thrift.core.Address getAddress() {
		return this.address;
	}

	public DashboardCompany setAddress(com.workmarket.thrift.core.Address address) {
		this.address = address;
		return this;
	}

	public boolean isSetAddress() {
		return this.address != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof DashboardCompany)
			return this.equals((DashboardCompany) that);
		return false;
	}

	private boolean equals(DashboardCompany that) {
		if (that == null)
			return false;

		boolean this_present_companyName = true && this.isSetCompanyName();
		boolean that_present_companyName = true && that.isSetCompanyName();
		if (this_present_companyName || that_present_companyName) {
			if (!(this_present_companyName && that_present_companyName))
				return false;
			if (!this.companyName.equals(that.companyName))
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

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_companyName = true && (isSetCompanyName());
		builder.append(present_companyName);
		if (present_companyName)
			builder.append(companyName);

		boolean present_address = true && (isSetAddress());
		builder.append(present_address);
		if (present_address)
			builder.append(address);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("DashboardCompany(");
		boolean first = true;

		if (isSetCompanyName()) {
			sb.append("companyName:");
			if (this.companyName == null) {
				sb.append("null");
			} else {
				sb.append(this.companyName);
			}
			first = false;
		}
		if (isSetAddress()) {
			if (!first) sb.append(", ");
			sb.append("address:");
			if (this.address == null) {
				sb.append("null");
			} else {
				sb.append(this.address);
			}
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}