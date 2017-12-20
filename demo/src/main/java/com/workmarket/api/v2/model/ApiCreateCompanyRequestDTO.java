package com.workmarket.api.v2.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.api.client.util.Lists;
import com.workmarket.api.ApiBaseHttpMessageConverter;
import com.workmarket.api.model.ApiPhoneNumberDTO;
import com.workmarket.domains.model.CallingCode;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.dto.CreateNewWorkerRequest;
import com.workmarket.service.business.dto.ProfileDTO;
import com.workmarket.service.business.dto.UserDTO;
import io.swagger.annotations.ApiModel;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@ApiModel("CreateCompanyRequest")
@JsonFilter(ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS)
@JsonDeserialize(builder = ApiCreateCompanyRequestDTO.Builder.class)
public class ApiCreateCompanyRequestDTO {
	private final String firstName;
	private final String lastName;
	private final String password; // validated by PasswordValidator
	private final String userEmail; // validated by UserEmailValidator
	private final String locale;

	private final AddressApiDTO address;
	private final ApiPhoneNumberDTO workPhone;
	private final String companyName;
	private final Long industryId;
	private final boolean termsAgree;
	private final String pictureUrl;
	private final String campaignId;
	private final String title;
	private final String function;

	private ApiCreateCompanyRequestDTO(Builder builder) {
		this.firstName = builder.firstName;
		this.lastName = builder.lastName;
		this.password = builder.password;
		this.userEmail = builder.userEmail;
		this.locale = builder.locale;
		this.address = builder.address;
		this.workPhone = builder.workPhone;
		this.companyName = builder.companyName;
		this.industryId = builder.industryId;
		this.termsAgree = builder.termsAgree;
		this.pictureUrl = builder.pictureUrl;
		this.campaignId = builder.campaignId;
		this.title = builder.title;
		this.function = builder.function;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPassword() {
		return password;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public String getLocale() {
		return locale;
	}

	public AddressApiDTO getAddress() {
		return address;
	}

	public ApiPhoneNumberDTO getWorkPhone() {
		return workPhone;
	}

	public String getCompanyName() {
		return companyName;
	}

	public Long getIndustryId() {
		return industryId;
	}

	public boolean getTermsAgree() {
		return termsAgree;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public String getTitle() {
		return title;
	}

	public String getFunction() {
		return function;
	}

	public static class Builder {
		private String firstName;
		private String lastName;
		private String password;
		private String userEmail;
		private String locale;
		private String addressTyper;
		private List<CallingCode> callingCodesList;
		private String planCode;
		private ApiPhoneNumberDTO workPhone;
		private String companyName;
		private Long industryId;
		private boolean termsAgree;
		private String pictureUrl;
		private String campaignId;
		private String title;
		private String function;
		private AddressApiDTO address;

		@JsonProperty("firstName")
		public Builder firstName(String firstName) {
			this.firstName = firstName;
			return this;
		}

		@JsonProperty("lastName")
		public Builder lastName(String lastName) {
			this.lastName = lastName;
			return this;
		}

		@JsonProperty("password")
		public Builder password(String password) {
			this.password = password;
			return this;
		}

		@JsonProperty("userEmail")
		public Builder userEmail(String userEmail) {
			this.userEmail = userEmail;
			return this;
		}

		@JsonProperty("locale")
		public Builder language(final String locale) {
			this.locale = locale;
			return this;
		}

		@JsonProperty("address")
		public Builder address(AddressApiDTO address) {
			this.address = address;
			return this;
		}

		@JsonProperty("callingCodesList")
		public Builder callingCodesList(List<CallingCode> callingCodesList) {
			this.callingCodesList = callingCodesList;
			return this;
		}

		@JsonProperty("planCode")
		public Builder planCode(String planCode) {
			this.planCode = planCode;
			return this;
		}

		@JsonProperty("workPhone")
		public Builder workPhone(ApiPhoneNumberDTO workPhone) {
			this.workPhone = workPhone;
			return this;
		}

		@JsonProperty("companyName")
		public Builder companyName(String companyName) {
			this.companyName = companyName;
			return this;
		}

		@JsonProperty("industryId")
		public Builder industryId(Long industryId) {
			this.industryId = industryId;
			return this;
		}

		@JsonProperty("termsAgree")
		public Builder termsAgree(boolean termsAgree) {
			this.termsAgree = termsAgree;
			return this;
		}

		@JsonProperty("pictureUrl")
		public Builder pictureUrl(String pictureUrl) {
			this.pictureUrl = pictureUrl;
			return this;
		}

		@JsonProperty("campaignId")
		public Builder campaignId(String campaignId) {
			this.campaignId = campaignId;
			return this;
		}

		@JsonProperty("title")
		public Builder title(String title) {
			this.title = title;
			return this;
		}

		@JsonProperty("function")
		public Builder function(String function) {
			this.function = function;
			return this;
		}

		@JsonProperty("fromPrototype")
		public Builder fromPrototype(ApiCreateCompanyRequestDTO prototype) {
			firstName = prototype.firstName;
			lastName = prototype.lastName;
			password = prototype.password;
			userEmail = prototype.userEmail;
			locale = prototype.locale;
			address = prototype.address;
			workPhone = prototype.workPhone;
			companyName = prototype.companyName;
			industryId = prototype.industryId;
			termsAgree = prototype.termsAgree;
			pictureUrl = prototype.pictureUrl;
			campaignId = prototype.campaignId;
			title = prototype.title;
			function = prototype.function;
			return this;
		}

		public ApiCreateCompanyRequestDTO build() {
			return new ApiCreateCompanyRequestDTO(this);
		}
	}

	public ProfileDTO convertToProfileDTO() {
		final ProfileDTO profileDTO = new ProfileDTO();
		AddressApiDTO address = getAddress();
		if (address != null) {
			profileDTO.setCountry(address.getCountry());
			profileDTO.setState(address.getState());
			profileDTO.setCity(address.getCity());
			profileDTO.setPostalCode(address.getPostalCode());
		}
		ApiPhoneNumberDTO workPhone = getWorkPhone();
		if (workPhone != null) {
			profileDTO.setWorkPhone(workPhone.getPhone());
			profileDTO.setWorkPhoneExtension(workPhone.getExtension());
			profileDTO.setWorkPhoneInternationalCallingCodeId(workPhone.getCountryCode());
		}
		return profileDTO;
	}

	public AddressDTO convertToAddressDTO() {
		final AddressDTO addressDTO = new AddressDTO();
		AddressApiDTO address = getAddress();
		if (address != null) {
			addressDTO.setCountry(address.getCountry());
			addressDTO.setCity(address.getCity());
			addressDTO.setPostalCode(address.getPostalCode());
			addressDTO.setState(address.getState());
			addressDTO.setLongitude(address.getLongitude());
			addressDTO.setLatitude(address.getLatitude());
		}
		return addressDTO;
	}

	public UserDTO convertToUserDTO() {
		final UserDTO userDTO = new UserDTO();
		userDTO.setEmail(getUserEmail());
		userDTO.setPassword(getPassword());
		userDTO.setFirstName(getFirstName());
		userDTO.setLastName(getLastName());
		userDTO.setOperatingAsIndividualFlag(false);
		return userDTO;
	}

}
