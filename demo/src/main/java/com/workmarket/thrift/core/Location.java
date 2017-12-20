package com.workmarket.thrift.core;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class Location implements Serializable {

	private static final long serialVersionUID = -2020670608735610911L;

	private long id;
	private String number;
	private String name;
	private String instructions;
	private Address address;
	private Company company;

	public Location() {}

	public Location(long id, String number, String name, Address address) {
		this.id = id;
		this.number = number;
		this.name = name;
		this.address = address;
	}

	public Location(long id, String number, String name, Address address, Company company) {
		this.id = id;
		this.number = number;
		this.name = name;
		this.address = address;
		this.company = company;
	}

	public long getId() {
		return this.id;
	}

	public Location setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public String getNumber() {
		return this.number;
	}

	public Location setNumber(String number) {
		this.number = number;
		return this;
	}

	public boolean isSetNumber() {
		return this.number != null;
	}

	public String getName() {
		return this.name;
	}

	public Location setName(String name) {
		this.name = name;
		return this;
	}

	public boolean isSetInstructions() {
		return this.instructions != null;
	}

	public String getInstructions() {
		return this.instructions;
	}

	public Location setInstructions(String instructions) {
		this.instructions = instructions;
		return this;
	}

	public boolean isSetName() {
		return this.name != null && StringUtils.isNotBlank(this.name);
	}

	public Address getAddress() {
		return this.address;
	}

	public Location setAddress(Address address) {
		this.address = address;
		return this;
	}

	public boolean isSetAddress() {
		return this.address != null;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public boolean isSetCompany() {
		return this.company != null;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Location)) {
			return false;
		}

		Location that = (Location) o;

		return new EqualsBuilder()
			.append(id, that.getId())
			.append(number, that.getNumber())
			.append(name, that.getName())
			.append(address, that.getAddress())
			.append(company, that.getCompany())
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
			.append(id)
			.append(number)
			.append(name)
			.append(address)
			.append(company)
			.toHashCode();
	}

	@Override
	public String toString() {
		return "Location{" +
			"id=" + id +
			", number='" + number + '\'' +
			", name='" + name + '\'' +
			", address=" + address +
			", company=" + company +
			'}';
	}
}