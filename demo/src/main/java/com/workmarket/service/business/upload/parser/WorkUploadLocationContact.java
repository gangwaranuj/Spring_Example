package com.workmarket.service.business.upload.parser;

import com.workmarket.domains.model.directory.ContactContextType;
import com.workmarket.service.business.dto.ClientContactDTO;
import com.workmarket.service.business.dto.EmailAddressDTO;
import com.workmarket.service.business.dto.PhoneNumberDTO;
import com.workmarket.thrift.core.Location;
import com.workmarket.thrift.core.Phone;
import com.workmarket.thrift.core.User;

import java.io.Serializable;

public class WorkUploadLocationContact implements Serializable {

	private static final long serialVersionUID = 2182828492409112875L;
	private final Long clientCompanyId;
	private final Location location;
	private final User locationContact;
	private final boolean isPrimary;

	public WorkUploadLocationContact(Long clientCompanyId, Location location,
			User locationContact, boolean isPrimary) {
		super();
		this.clientCompanyId = clientCompanyId;
		this.location = location;
		this.locationContact = locationContact;
		this.isPrimary = isPrimary;
	}

	public Location getLocation() {
		return location;
	}

	public Long getClientCompanyId() {
		return clientCompanyId;
	}

	public User getLocationContact() {
		return locationContact;
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public ClientContactDTO toDTO() {
		ClientContactDTO dto = new ClientContactDTO();

		dto.setClientCompanyId(clientCompanyId);
		dto.setFirstName(locationContact.getName().getFirstName());
		dto.setLastName(locationContact.getName().getLastName());

		if (locationContact.isSetEmail()) {
			dto.getEmails().add(new EmailAddressDTO(locationContact.getEmail()));
		}

		if (locationContact.getProfile() != null && locationContact.getProfile().isSetPhoneNumbers()) {
			for (Phone n : locationContact.getProfile().getPhoneNumbers()) {
				if (n.getPhone() == null)
					continue;
				dto.getPhoneNumbers().add(new PhoneNumberDTO(n.getPhone(), n.getExtension(), ContactContextType.WORK));
			}
		}

		if (location != null) {
			dto.setClientLocationName(location.getName());
			if (location.isSetAddress()) {
				dto.setAddress1(location.getAddress().getAddressLine1());
				dto.setAddress2(location.getAddress().getAddressLine2());
				dto.setCity(location.getAddress().getCity());
				dto.setState(location.getAddress().getState());
				dto.setCountry(location.getAddress().getCountry());
				dto.setPostalCode(location.getAddress().getZip());
			}
		}

		return dto;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((clientCompanyId == null) ? 0 : clientCompanyId.hashCode());
		result = prime * result + (isPrimary ? 1231 : 1237);
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result
				+ ((locationContact == null) ? 0 : locationContact.hashCode());
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
		WorkUploadLocationContact other = (WorkUploadLocationContact) obj;
		if (clientCompanyId == null) {
			if (other.clientCompanyId != null)
				return false;
		} else if (!clientCompanyId.equals(other.clientCompanyId))
			return false;
		if (isPrimary != other.isPrimary)
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (locationContact == null) {
			if (other.locationContact != null)
				return false;
		} else if (!locationContact.equals(other.locationContact))
			return false;
		return true;
	}

}
