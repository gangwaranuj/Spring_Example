package com.workmarket.thrift.core;

import com.workmarket.data.solr.model.GeoPoint;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class Address implements Serializable {
	private static final long serialVersionUID = 1L;

	private String addressLine1;
	private String addressLine2;
	private String city;
	private String state;
	private String zip;
	private String country;
	private GeoPoint point;
	@Deprecated
	private String dressCode;
	private String type;
	private Long locationType;

	public Address() {}

	public Address(
			String addressLine1,
			String addressLine2,
			String city,
			String state,
			String zip,
			String country,
			GeoPoint point,
			String dressCode,
			String type) {
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.country = country;
		this.point = point;
		this.dressCode = dressCode;
		this.type = type;
	}

	public String getAddressLine1() {
		return this.addressLine1;
	}

	public Address setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
		return this;
	}

	public boolean isSetAddressLine1() {
		return this.addressLine1 != null;
	}

	public String getAddressLine2() {
		return this.addressLine2;
	}

	public Address setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
		return this;
	}

	public boolean isSetAddressLine2() {
		return this.addressLine2 != null;
	}

	public String getCity() {
		return this.city;
	}

	public Address setCity(String city) {
		this.city = city;
		return this;
	}

	public boolean isSetCity() {
		return this.city != null;
	}

	public String getState() {
		return this.state;
	}

	public Address setState(String state) {
		this.state = state;
		return this;
	}

	public boolean isSetState() {
		return this.state != null;
	}

	public String getZip() {
		return this.zip;
	}

	public Address setZip(String zip) {
		this.zip = zip;
		return this;
	}

	public boolean isSetZip() {
		return this.zip != null;
	}

	public String getCountry() {
		return this.country;
	}

	public Address setCountry(String country) {
		this.country = country;
		return this;
	}

	public boolean isSetCountry() {
		return this.country != null;
	}

	public GeoPoint getPoint() {
		return this.point;
	}

	public Address setPoint(GeoPoint point) {
		this.point = point;
		return this;
	}

	public boolean isSetPoint() {
		return this.point != null;
	}

	public String getDressCode() {
		return this.dressCode;
	}

	public Address setDressCode(String dressCode) {
		this.dressCode = dressCode;
		return this;
	}

	public boolean isSetDressCode() {
		return this.dressCode != null;
	}

	public String getType() {
		return this.type;
	}

	public Address setType(String type) {
		this.type = type;
		return this;
	}

	public boolean isSetType() {
		return this.type != null;
	}

	public Long getLocationType() {
		return this.locationType;
	}

	public Address setLocationType(Long locationType) {
		this.locationType = locationType;
		return this;
	}

	public boolean isSetLocationType() {
		return this.locationType != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == this) {
			return true;
		}
		if (!(that instanceof Address)) {
			return false;
		}

		Address o = (Address) that;
		return new EqualsBuilder()
			.append(addressLine1, o.getAddressLine1())
			.append(addressLine2, o.getAddressLine2())
			.append(city, o.getCity())
			.append(state, o.getState())
			.append(zip, o.getZip())
			.append(country, o.getCountry())
			.append(point, o.getPoint())
			.append(type, o.getType())
			.append(locationType, o.getLocationType())
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
			.append(addressLine1)
			.append(addressLine2)
			.append(city)
			.append(state)
			.append(zip)
			.append(country)
			.append(point)
			.append(type)
			.append(locationType)
			.toHashCode();
	}
}
