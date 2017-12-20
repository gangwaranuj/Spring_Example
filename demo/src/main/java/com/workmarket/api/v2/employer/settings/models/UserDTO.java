package com.workmarket.api.v2.employer.settings.models;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;
import com.workmarket.api.ApiBaseHttpMessageConverter;
import com.workmarket.api.v1.model.ApiAddressDTO;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import com.workmarket.api.v2.model.AddressApiDTO;
import io.swagger.annotations.ApiModel;
import com.workmarket.domains.model.UserStatusType;

import java.math.BigDecimal;
import java.util.List;

@ApiModel("User")
@JsonFilter(ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS)
@JsonDeserialize(builder = UserDTO.Builder.class)
public class UserDTO {

	private final Long id;
	private final String userNumber;
	private final String firstName;
	private final String lastName;
	private final String email;
	private final String workPhone;
	private final String workPhoneExtension;
	private final String workPhoneInternationalCode;
	private final AddressApiDTO address;
	private final String jobTitle;
	private final Long industryId;
	private final BigDecimal spendLimit;
	private final RoleSettingsDTO roleSettings;
	private final PermissionSettingsDTO permissionSettings;
	private final String workStatus;
	private final String userStatusType;
	private final List<String> orgUnitUuids;
	private final List<String> orgUnitPaths;

	protected UserDTO(Builder builder) {
		this.id = builder.id;
		this.userNumber = builder.userNumber;
		this.firstName = builder.firstName;
		this.lastName = builder.lastName;
		this.email = builder.email;
		this.workPhone = builder.workPhone;
		this.workPhoneExtension = builder.workPhoneExtension;
		this.workPhoneInternationalCode = builder.workPhoneInternationalCode;
		this.address = builder.address.build();
		this.jobTitle = builder.jobTitle;
		this.industryId = builder.industryId;
		this.spendLimit = builder.spendLimit;
		this.roleSettings = builder.roleSettings.build();
		this.permissionSettings = builder.permissionSettings.build();
		this.workStatus = builder.workStatus;
		this.userStatusType = builder.userStatusType;
		this.orgUnitUuids = builder.orgUnitUuids;
		this.orgUnitPaths = builder.orgUnitPaths;
	}

	@JsonIgnore
	public Long getId() {
		return id;
	}

	public String getUserNumber() {
		return userNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public String getWorkPhoneExtension() {
		return workPhoneExtension;
	}

	public String getWorkPhoneInternationalCode() {
		return workPhoneInternationalCode;
	}

	public AddressApiDTO getAddress() { return address; }

	public String getJobTitle() {
		return jobTitle;
	}

	public Long getIndustryId() {
		return industryId;
	}

	public BigDecimal getSpendLimit() {
		return spendLimit;
	}

	public RoleSettingsDTO getRoleSettings() {
		return roleSettings;
	}

	public PermissionSettingsDTO getPermissionSettings() {
		return permissionSettings;
	}

	public String getWorkStatus() {
		return workStatus;
	}

	public String getUserStatusType() {
		return userStatusType;
	}

	public List<String> getOrgUnitUuids() {
		return orgUnitUuids;
	}

	@JsonIgnore
	public List<String> getOrgUnitPaths() {
		return orgUnitPaths;
	}

	public static class Builder implements AbstractBuilder<UserDTO> {

		private Long id;
		private String userNumber;
		private String firstName;
		private String lastName;
		private String email;
		private String workPhone;
		private String workPhoneExtension;
		private String workPhoneInternationalCode;
		private AddressApiDTO.Builder address = new AddressApiDTO.Builder();
		private String jobTitle;
		private BigDecimal spendLimit;
		private Long industryId;
		private RoleSettingsDTO.Builder roleSettings = new RoleSettingsDTO.Builder();
		private PermissionSettingsDTO.Builder permissionSettings = new PermissionSettingsDTO.Builder();
		private String workStatus;
		private String userStatusType;
		private List<String> orgUnitUuids;
		private List<String> orgUnitPaths;

		public Builder(){}

		public Builder(UserDTO userDTO) {
			this.id = userDTO.id;
			this.userNumber = userDTO.userNumber;
			this.firstName = userDTO.firstName;
			this.lastName = userDTO.lastName;
			this.email = userDTO.email;
			this.workPhone = userDTO.workPhone;
			this.workPhoneExtension = userDTO.workPhoneExtension;
			this.workPhoneInternationalCode = userDTO.workPhoneInternationalCode;
			this.address = new AddressApiDTO.Builder(userDTO.address);
			this.jobTitle = userDTO.jobTitle;
			this.industryId = userDTO.industryId;
			this.spendLimit = userDTO.spendLimit;
			this.roleSettings = new RoleSettingsDTO.Builder(userDTO.roleSettings);
			this.permissionSettings = new PermissionSettingsDTO.Builder(userDTO.permissionSettings);
			this.workStatus = userDTO.workStatus;
			this.userStatusType = userDTO.userStatusType;
			this.orgUnitUuids = userDTO.orgUnitUuids;
			this.orgUnitPaths = userDTO.orgUnitPaths;
		}

		@JsonProperty("id") public Builder setId(final Long id) {
			this.id = id;
			return this;
		}

		@JsonProperty("userNumber") public Builder setUserNumber(final String userNumber) {
			this.userNumber = userNumber;
			return this;
		}

		@JsonProperty("firstName") public Builder setFirstName(final String firstName) {
			this.firstName = firstName;
			return this;
		}

		@JsonProperty("lastName") public Builder setLastName(final String lastName) {
			this.lastName = lastName;
			return this;
		}

		@JsonProperty("email") public Builder setEmail(final String email) {
			this.email = email;
			return this;
		}

		@JsonProperty("workPhone") public Builder setWorkPhone(final String workPhone) {
			this.workPhone = workPhone;
			return this;
		}

		@JsonProperty("workPhoneExtension") public Builder setWorkPhoneExtension(final String workPhoneExtension) {
			this.workPhoneExtension = workPhoneExtension;
			return this;
		}

		@JsonProperty("workPhoneInternationalCode") public Builder setWorkPhoneInternationalCode(final String workPhoneInternationalCode) {
			this.workPhoneInternationalCode = workPhoneInternationalCode;
			return this;
		}

		@JsonProperty("address") public Builder setAddress(final AddressApiDTO.Builder address) {
			this.address = address;
			return this;
		}

		@JsonProperty("jobTitle") public Builder setJobTitle(final String jobTitle) {
			this.jobTitle = jobTitle;
			return this;
		}

		@JsonProperty("industryId") public Builder setIndustryId(final Long industryId) {
			this.industryId = industryId;
			return this;
		}

		@JsonProperty("spendLimit") public Builder setSpendLimit(final BigDecimal spendLimit) {
			this.spendLimit = spendLimit;
			return this;
		}

		@JsonProperty("roleSettings") public Builder setRoleSettings(final RoleSettingsDTO.Builder roleSettings) {
			this.roleSettings = roleSettings;
			return this;
		}

		@JsonProperty("permissionSettings") public Builder setPermissionSettings(final PermissionSettingsDTO.Builder permissionSettings) {
			this.permissionSettings = permissionSettings;
			return this;
		}

		@JsonProperty("workStatus") public Builder setWorkStatus(final String workStatus) {
			this.workStatus = workStatus;
			return this;
		}

		@JsonProperty("userStatusType") public Builder setUserStatusType(final String userStatusType) {
			this.userStatusType = userStatusType;
			return this;
		}

		@JsonProperty("orgUnitUuids") public Builder setOrgUnitUuids(final List<String> orgUnitUuids) {
			this.orgUnitUuids = orgUnitUuids == null ? null : ImmutableList.copyOf(orgUnitUuids);
			return this;
		}

		@JsonProperty("orgUnitPaths") public Builder setOrgUnitPaths(final List<String> orgUnitPaths) {
			this.orgUnitPaths = orgUnitPaths == null ? null : ImmutableList.copyOf(orgUnitPaths);
			return this;
		}

		@Override
		public UserDTO build() {
			return new UserDTO(this);
		}
	}
}
