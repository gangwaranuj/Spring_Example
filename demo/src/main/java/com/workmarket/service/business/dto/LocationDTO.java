package com.workmarket.service.business.dto;

import com.workmarket.domains.model.DressCode;
import com.workmarket.domains.model.LocationType;
import com.workmarket.dto.AddressDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.Size;

public class LocationDTO extends AddressDTO {

	private static final long serialVersionUID = -5519283782656315792L;

	private Long id;
	private Long companyId;
	private Long clientCompanyId;

	@Size(min = 0, max = 50)
	private String locationNumber;

	@Size(min = 0, max = 100)
	private String name;

	@Size(min = 0, max = 500)
	private String instructions;

	public LocationDTO() {}

	public LocationDTO(
		Long id, String name, String number, String address1, String address2,
		String city, String state, String postalCode, String locationType, String country) {

		this.id = id;
		this.name = name;
		this.locationNumber = number;
		this.address1 = address1;
		this.address2 = address2;
		this.city = city;
		this.state = state;
		this.postalCode = postalCode;
		this.locationTypeId = LocationType.valueOf(locationType);
		this.country = country;
		this.dressCodeId = DressCode.BUSINESS_CASUAL;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isSetId() {
		return this.id != null;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getClientCompanyId() {
		return clientCompanyId;
	}

	public void setClientCompanyId(Long clientCompanyId) {
		this.clientCompanyId = clientCompanyId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSetName() {
		return this.name != null;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public boolean isSetInstructions() {
		return this.instructions != null;
	}

	public String getLocationNumber() {
		return locationNumber;
	}

	public void setLocationNumber(String locationNumber) {
		this.locationNumber = locationNumber;
	}

	public boolean isSetLocationNumber() {
		return locationNumber != null;
	}

	public void setAddressFields(AddressDTO addressDTO) {
		if (addressDTO == null) {
			return;
		}

		setAddress1(addressDTO.getAddress1());
		setAddress2(addressDTO.getAddress2());
		setCity(addressDTO.getCity());
		setState(addressDTO.getState());
		setPostalCode(addressDTO.getPostalCode());
		setCountry(addressDTO.getCountry());
		setLocationTypeId(addressDTO.getLocationTypeId());
		setDressCodeId(addressDTO.getDressCodeId());
		setLatitude(addressDTO.getLatitude());
		setLongitude(addressDTO.getLongitude());
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof LocationDTO)) {
			return false;
		}

		LocationDTO that = (LocationDTO) o;

		return new EqualsBuilder()
			.append(id, that.getId())
			.append(companyId, that.getCompanyId())
			.append(clientCompanyId, that.getClientCompanyId())
			.append(locationNumber, that.getLocationNumber())
			.append(name, that.getName())
			.appendSuper(super.equals(that))
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
			.append(id)
			.append(companyId)
			.append(clientCompanyId)
			.append(locationNumber)
			.append(name)
			.appendSuper(super.hashCode())
			.toHashCode();
	}
}
