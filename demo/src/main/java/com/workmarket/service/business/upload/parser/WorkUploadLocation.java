package com.workmarket.service.business.upload.parser;

import com.workmarket.domains.model.DressCode;
import com.workmarket.domains.model.LocationType;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.thrift.core.Location;

import java.io.Serializable;

public class WorkUploadLocation implements Serializable {

	private final Location location;
	private final Long clientCompanyId;

	public WorkUploadLocation(Location location) {
		this(location, null);
	}

	public WorkUploadLocation(Location location, Long clientCompanyId) {
		super();
		this.location = location;
		this.clientCompanyId = clientCompanyId;
	}

	public Location getLocation() {
		return location;
	}

	public Long getClientCompanyId() {
		return clientCompanyId;
	}

	public boolean hasClientCompany() {
		return clientCompanyId != null;
	}

	public LocationDTO toDTO() {
		LocationDTO dto = new LocationDTO();
		dto.setName(location.getName());
		dto.setCompanyId(location.getCompany().getId());
		dto.setLocationNumber(location.getNumber());
		dto.setInstructions(location.getInstructions());
		dto.setAddress1(location.getAddress().getAddressLine1());
		dto.setAddress2(location.getAddress().getAddressLine2());
		dto.setCity(location.getAddress().getCity());
		dto.setState(location.getAddress().getState());
		dto.setPostalCode(location.getAddress().getZip());
		dto.setCountry(location.getAddress().getCountry());
		dto.setAddressTypeCode("company");
		dto.setLocationTypeId(LocationType.valueOf(location.getAddress().getType()));
		dto.setDressCodeId(DressCode.valueOf(location.getAddress().getDressCode()));
		return dto;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((clientCompanyId == null) ? 0 : clientCompanyId.hashCode());
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WorkUploadLocation other = (WorkUploadLocation) obj;
		if (clientCompanyId == null) {
			if (other.clientCompanyId != null)
				return false;
		} else if (!clientCompanyId.equals(other.clientCompanyId))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		return true;
	}

}
