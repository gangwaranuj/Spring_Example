package com.workmarket.dto;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.AddressType;
import com.workmarket.domains.model.geocoding.Coordinate;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.utility.BeanUtilities;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressDTO implements Serializable {

	private static final long serialVersionUID = -9075755325885178249L;

	protected Long addressId;
	protected String address1;

	@Size(max = Constants.ADDRESS_LINE_2_MAX_LENGTH)
	protected String address2;

	@NotNull
	@Size(min = Constants.CITY_MIN_LENGTH, max = Constants.CITY_MAX_LENGTH)
	protected String city;

	protected String state;

	@Size(max = Constants.POSTAL_CODE_MAX_LENGTH)
	protected String postalCode;

	@NotNull
	@Size(min = Constants.COUNTRY_MIN_LENGTH, max = Constants.COUNTRY_MAX_LENGTH)
	protected String country;

	@NotNull
	protected String addressTypeCode;
	protected Long locationTypeId;
	@Deprecated
	protected Long dressCodeId;
	protected BigDecimal latitude;
	protected BigDecimal longitude;

	public AddressDTO() {
	}

	public AddressDTO(Address address) {
		if(address == null) {
			return;
		}
		this.addressId = address.getId();
		this.address1 = address.getAddress1();
		this.address2 = address.getAddress2();
		this.city = address.getCity();
		this.state = address.getState() != null ? address.getState().getShortName() : null;
		this.postalCode = address.getPostalCode();
		this.country =  address.getCountry() != null ? address.getCountry().getId() : null;
		this.addressTypeCode = address.getAddressType() != null ? address.getAddressType().getCode() : null;
		this.locationTypeId = address.getLocationType() != null ? address.getLocationType().getId() : null;
		this.latitude = address.getLatitude();
		this.longitude = address.getLongitude();
	}

	public static AddressDTO newGeocodeDTO(Address address) {
		Assert.notNull(address);
		Assert.notNull(address.getCountry());
		//Google doesn't like CAN, it likes CANADA
		AddressDTO addressDTO = new AddressDTO(address);
		addressDTO.setCountry(address.getCountry().getName());
		return addressDTO;
	}

	public Address toAddress() {
		Assert.hasText(getAddressTypeCode());

		Address address = new Address();
		BeanUtilities.copyProperties(address, this, new String[]{"country", "state"});
		address.setCountry(Country.valueOf(getCountry()));
		address.setAddressType(AddressType.newAddressType(getAddressTypeCode()));

		return address;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public boolean isLatLongSet() {
		return latitude != null && longitude != null;
	}

	public Long getAddressId() {
		return addressId;
	}

	public String getAddress1() {
		return address1;
	}

	public boolean isSetAddress1() {
		return address1 != null;
	}

	public String getAddress2() {
		return address2;
	}

	public boolean isSetAddress2() {
		return address2 != null;
	}

	public String getCity() {
		return city;
	}

	public boolean isSetCity() {
		return city != null;
	}

	public String getState() {
		return state;
	}

	public boolean isSetState() {
		return state != null;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public boolean isSetPostalCode() {
		return postalCode != null;
	}

	public String getCountry() {
		return country;
	}

	public boolean isSetCountry() {
		return country != null;
	}

	public String getAddressTypeCode() {
		return addressTypeCode;
	}

	public boolean isSetAddressTypeCode() {
		return addressTypeCode != null;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
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

	public void setState(String state) {
		this.state = state;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setAddressTypeCode(String addressTypeCode) {
		this.addressTypeCode = addressTypeCode;
	}

	public void setLocationTypeId(Long locationTypeId) {
		this.locationTypeId = locationTypeId;
	}

	public Long getLocationTypeId() {
		return locationTypeId;
	}

	public boolean isSetLocationTypeId() {
		return locationTypeId != null;
	}

	public Long getDressCodeId() {
		return dressCodeId;
	}

	public void setDressCodeId(Long dressCodeId) {
		this.dressCodeId = dressCodeId;
	}

	public String getFullAddress() {
		return AddressDTOUtilities.formatAddressLong(this, ", ");
	}

	public String getShortAddress() {
		return AddressDTOUtilities.formatAddressShort(this);
	}

	public String getAddressForGeocoder() {
        return AddressDTOUtilities.formatAddressForGeocoder(this);
    }

	public Coordinate getCoordinate() {
		if (isLatLongSet()) {
			return new Coordinate(longitude.doubleValue(), latitude.doubleValue());
		}
		return null;
	}

	public AddressDTO copy() {
		final AddressDTO copiedAddress = new AddressDTO();
		copiedAddress.setLatitude(latitude);
		copiedAddress.setLongitude(longitude);
		copiedAddress.setAddress1(address1);
		copiedAddress.setAddress2(address2);
		copiedAddress.setCity(city);
		copiedAddress.setState(state);
		copiedAddress.setPostalCode(postalCode);
		copiedAddress.setCountry(country);
		copiedAddress.setAddressId(addressId);
		copiedAddress.setAddressTypeCode(addressTypeCode);
		copiedAddress.setLocationTypeId(locationTypeId);
		copiedAddress.setDressCodeId(dressCodeId);
		return copiedAddress;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		AddressDTO that = (AddressDTO) o;

		if (address1 != null ? !address1.equals(that.address1) : that.address1 != null) {
			return false;
		}
		if (address2 != null ? !address2.equals(that.address2) : that.address2 != null) {
			return false;
		}
		if (addressTypeCode != null ? !addressTypeCode.equals(that.addressTypeCode) : that.addressTypeCode != null) {
			return false;
		}
		if (city != null ? !city.equals(that.city) : that.city != null) {
			return false;
		}
		if (country != null ? !country.equals(that.country) : that.country != null) {
			return false;
		}
		if (postalCode != null ? !postalCode.equals(that.postalCode) : that.postalCode != null) {
			return false;
		}
		if (state != null ? !state.equals(that.state) : that.state != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = address1 != null ? address1.hashCode() : 0;
		result = 31 * result + (address2 != null ? address2.hashCode() : 0);
		result = 31 * result + (city != null ? city.hashCode() : 0);
		result = 31 * result + (state != null ? state.hashCode() : 0);
		result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
		result = 31 * result + (country != null ? country.hashCode() : 0);
		result = 31 * result + (addressTypeCode != null ? addressTypeCode.hashCode() : 0);
		return result;
	}
}
