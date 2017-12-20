package com.workmarket.domains.model;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.State;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Entity(name="address")
@Table(name="address")
@AuditChanges
public class Address extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	private String address1;
	private String address2;

	@NotNull(message = "City cannot be empty")
	private String city;

	private State state;

	@NotNull(message = "Postal code cannot be empty")
	@Size(min = 0, max = 9, message = "Postal code is invalid")
	private String postalCode;

	@NotNull(message = "Country cannot be empty")
	private Country country;

	private AddressType addressType;
	private BigDecimal latitude;
	private BigDecimal longitude;
	private VerificationStatus geocodeVerificationStatus = VerificationStatus.PENDING;
	private LocationType locationType;
	@Deprecated
	private DressCode dressCode;

	public Address() {
		super();
	}

	public Address(String typeCode) {
		super();
		addressType = new AddressType(typeCode);

	}

	@Column(name = "line1", nullable = true, length=200)
	public String getAddress1() {
		return address1;
	}

	@Column(name = "line2", nullable = true, length=100)
	public String getAddress2() {
		return address2;
	}

	@Column(name = "city", nullable = true, length=100)
	public String getCity() {
		return city;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name="state", referencedColumnName="id", nullable = false)
	public State getState() {
		return state;
	}

	@Column(name = "postal_code", nullable = true, length=9)
	public String getPostalCode() {
		return postalCode;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name="country", referencedColumnName="id", nullable = false)
	public Country getCountry() {
		return country;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="address_type_code", referencedColumnName="code")
	public AddressType getAddressType() {
		return addressType;
	}

	@Column(name = "latitude", nullable = true)
	public BigDecimal getLatitude() {
		return latitude;
	}

	@Column(name = "longitude", nullable = true)
	public BigDecimal getLongitude() {
		return longitude;
	}

	@Column(name = "geocode_verification_status_code")
	public VerificationStatus getGeocodeVerificationStatus() {
		return geocodeVerificationStatus;
	}

	@Transient
	public Boolean getDeactivatedFlag() {
		return getDeleted();
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public void setAddressType(AddressType addressType){
		this.addressType = addressType;
	}

	public void setLatitude(BigDecimal latitude){
		this.latitude = latitude;
	}

	public void setLongitude(BigDecimal longitude){
		this.longitude = longitude;
	}

	public void setGeocodeVerificationStatus(VerificationStatus geocodeVerificationStatus) {
		this.geocodeVerificationStatus = geocodeVerificationStatus;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name="location_type_id", referencedColumnName="id")
	public LocationType getLocationType() {
		return locationType;
	}

	public void setLocationType(LocationType locationType) {
		this.locationType = locationType;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name="dress_code_id", referencedColumnName="id")
	public DressCode getDressCode() {
		return dressCode;
	}

	public void setDressCode(DressCode dressCode) {
		this.dressCode = dressCode;
	}

	@Transient
	public void setDeactivatedFlag(Boolean deactivatedFlag){
		setDeleted(deactivatedFlag);
	}

	@Transient
	public String getFullAddress() {
		return AddressUtilities.formatAddressLong(this, "\n");
	}

	@Transient
	public String getShortAddress() {
		return AddressUtilities.formatAddressShort(this);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).append("Address: ").append("id", getId()).toString();
	}

	public String toText() {
		return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
			.append(address1)
			.append(address2)
			.append(city)
			.append(state)
			.append(postalCode)
			.toString();
	}

}
