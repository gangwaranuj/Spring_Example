package com.workmarket.api.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Location")
@JsonDeserialize(builder = LocationDTO.Builder.class)
public class LocationDTO {
	// id being 0 indicates new location by default, -1 indicates virtual location
	private final long id;
	private final String name;
	private final String number;
	private final String addressLine1;
	private final String addressLine2;
	private final String city;
	private final String state;
	private final String zip;
	private final String country;
	private final Long locationType;
	private final String instructions;
	private final Double longitude;
	private final Double latitude;
	private final Long clientCompanyId;
	private final ContactDTO contact;
	private final ContactDTO secondaryContact;
	private final int locationMode;

	private LocationDTO(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.number = builder.number;
		this.addressLine1 = builder.addressLine1;
		this.addressLine2 = builder.addressLine2;
		this.city = builder.city;
		this.state = builder.state;
		this.zip = builder.zip;
		this.country = builder.country;
		this.locationType = builder.locationType;
		this.instructions = builder.instructions;
		this.longitude = builder.longitude;
		this.latitude = builder.latitude;
		this.clientCompanyId = builder.clientCompanyId;
		this.locationMode = builder.locationMode;

		if (builder.contact != null) {
			this.contact = builder.contact.build();
		} else {
			this.contact = null;
		}

		if (builder.secondaryContact != null) {
			this.secondaryContact = builder.secondaryContact.build();
		} else {
			this.secondaryContact = null;
		}
	}

	@ApiModelProperty(name = "id")
	@JsonProperty("id")
	public long getId() {
		return id;
	}

	@ApiModelProperty(name = "number")
	@JsonProperty("number")
	public String getNumber() {
		return number;
	}

	@ApiModelProperty(name = "name")
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@ApiModelProperty(name = "addressLine1")
	@JsonProperty("addressLine1")
	public String getAddressLine1() {
		return addressLine1;
	}

	@ApiModelProperty(name = "addressLine2")
	@JsonProperty("addressLine2")
	public String getAddressLine2() {
		return addressLine2;
	}

	@ApiModelProperty(name = "city")
	@JsonProperty("city")
	public String getCity() {
		return city;
	}

	@ApiModelProperty(name = "state")
	@JsonProperty("state")
	public String getState() {
		return state;
	}

	@ApiModelProperty(name = "zip")
	@JsonProperty("zip")
	public String getZip() {
		return zip;
	}

	@ApiModelProperty(name = "country")
	@JsonProperty("country")
	public String getCountry() {
		return country;
	}

	@ApiModelProperty(name = "locationType")
	@JsonProperty("locationType")
	public Long getLocationType() {
		return locationType;
	}

	@ApiModelProperty(name = "instructions")
	@JsonProperty("instructions")
	public String getInstructions() {
		return instructions;
	}

	@ApiModelProperty(name = "longitude")
	@JsonProperty("longitude")
	public Double getLongitude() {
		return longitude;
	}

	@ApiModelProperty(name = "latitude")
	@JsonProperty("latitude")
	public Double getLatitude() {
		return latitude;
	}

	@ApiModelProperty(name = "clientCompanyId")
	@JsonProperty("clientCompanyId")
	public Long getClientCompanyId() {
		return clientCompanyId;
	}

	@ApiModelProperty(name = "contact")
	@JsonProperty("contact")
	public ContactDTO getContact() {
		return contact;
	}

	@ApiModelProperty(name = "secondaryContact")
	@JsonProperty("secondaryContact")
	public ContactDTO getSecondaryContact() {
		return secondaryContact;
	}

	@ApiModelProperty(name = "locationMode")
	@JsonProperty("locationMode")
	public int getLocationMode() {
		return locationMode;
	}

	public static class Builder implements AbstractBuilder<LocationDTO> {
		private long id;
		private String name;
		private String number;
		private String addressLine1;
		private String addressLine2;
		private String city;
		private String state;
		private String zip;
		private String country;
		private Long locationType;
		private String instructions;
		private Double longitude;
		private Double latitude;
		private Long clientCompanyId;
		private ContactDTO.Builder contact;
		private ContactDTO.Builder secondaryContact;
		private int locationMode;

		public Builder() {}

		public Builder(LocationDTO locationDTO) {
			this.id = locationDTO.id;
			this.name = locationDTO.name;
			this.number = locationDTO.number;
			this.addressLine1 = locationDTO.addressLine1;
			this.addressLine2 = locationDTO.addressLine2;
			this.city = locationDTO.city;
			this.state = locationDTO.state;
			this.zip = locationDTO.zip;
			this.country = locationDTO.country;
			this.locationType = locationDTO.locationType;
			this.instructions = locationDTO.instructions;
			this.longitude = locationDTO.longitude;
			this.latitude = locationDTO.latitude;
			this.clientCompanyId = locationDTO.clientCompanyId;
			this.locationMode = locationDTO.locationMode;

			if (locationDTO.contact != null) {
				this.contact = new ContactDTO.Builder(locationDTO.contact);
			}

			if (locationDTO.secondaryContact != null) {
				this.secondaryContact = new ContactDTO.Builder(locationDTO.secondaryContact);
			}
		}

		@JsonProperty("id") public Builder setId(long id) {
			this.id = id;
			return this;
		}

		@JsonProperty("number") public Builder setNumber(String number) {
			this.number = number;
			return this;
		}

		@JsonProperty("name") public Builder setName(String name) {
			this.name = name;
			return this;
		}

		@JsonProperty("addressLine1") public Builder setAddressLine1(String addressLine1) {
			this.addressLine1 = addressLine1;
			return this;
		}

		@JsonProperty("addressLine2") public Builder setAddressLine2(String addressLine2) {
			this.addressLine2 = addressLine2;
			return this;
		}

		@JsonProperty("city") public Builder setCity(String city) {
			this.city = city;
			return this;
		}

		@JsonProperty("state") public Builder setState(String state) {
			this.state = state;
			return this;
		}

		@JsonProperty("zip") public Builder setZip(String zip) {
			this.zip = zip;
			return this;
		}

		@JsonProperty("country") public Builder setCountry(String country) {
			this.country = country;
			return this;
		}

		@JsonProperty("locationType") public Builder setLocationType(Long locationType) {
			this.locationType = locationType;
			return this;
		}

		@JsonProperty("instructions") public Builder setInstructions(String instructions) {
			this.instructions = instructions;
			return this;
		}

		@JsonProperty("longitude") public Builder setLongitude(Double longitude) {
			this.longitude = longitude;
			return this;
		}

		@JsonProperty("latitude") public Builder setLatitude(Double latitude) {
			this.latitude = latitude;
			return this;
		}

		@JsonProperty("clientCompanyId") public Builder setClientCompanyId(Long clientCompanyId) {
			this.clientCompanyId = clientCompanyId;
			return this;
		}

		@JsonProperty("contact") public Builder setContact(ContactDTO.Builder contact) {
			this.contact = contact;
			return this;
		}

		@JsonProperty("secondaryContact") public Builder setSecondaryContact(ContactDTO.Builder secondaryContact) {
			this.secondaryContact = secondaryContact;
			return this;
		}

		@JsonProperty("locationMode")
		public Builder setLocationMode(int locationMode) {
			this.locationMode = locationMode;
			return this;
		}

		public LocationDTO build() {
			return new LocationDTO(this);
		}
	}
}
