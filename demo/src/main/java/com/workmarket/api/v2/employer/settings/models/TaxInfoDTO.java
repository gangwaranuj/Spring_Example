package com.workmarket.api.v2.employer.settings.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "TaxInfo")
@JsonDeserialize(builder = TaxInfoDTO.Builder.class)
public class TaxInfoDTO {
	private final Long id;
	private final Long companyNumber;
	private final String taxCountry;
	private final Boolean activeFlag;
	private final String taxNumber;
	private final String taxEntityTypeCode;
	private final String activeDateString;
	private final String taxName;
	private final String firstName;
	private final String middleName;
	private final String lastName;
	private final Boolean businessFlag;
	private final String taxVerificationStatusCode;
	private final Boolean verificationPending;
	private final Boolean deliveryPolicyFlag;
	private final String address;
	private final String city;
	private final String state;
	private final String postalCode;
	private final String country;

	// USA
	private final String businessName;
	private final Boolean businessNameFlag;
	private final String effectiveDateString;
	private final String signature;
	private final String signatureDateString;

	// foreign
	private final String countryOfIncorporation;
	private final Boolean foreignStatusAcceptedFlag;

	public TaxInfoDTO(TaxInfoDTO.Builder builder) {
		this.id = builder.id;
		this.companyNumber = builder.companyNumber;
		this.taxCountry = builder.taxCountry;
		this.activeFlag = builder.activeFlag;
		this.taxNumber = builder.taxNumber;
		this.taxEntityTypeCode = builder.taxEntityTypeCode;
		this.activeDateString = builder.activeDateString;
		this.taxName = builder.taxName;
		this.firstName = builder.firstName;
		this.middleName = builder.middleName;
		this.lastName = builder.lastName;
		this.businessFlag = builder.businessFlag;
		this.taxVerificationStatusCode = builder.taxVerificationStatusCode;
		this.verificationPending = builder.verificationPending;
		this.deliveryPolicyFlag = builder.deliveryPolicyFlag;
		this.address = builder.address;
		this.city = builder.city;
		this.state = builder.state;
		this.country = builder.country;
		this.postalCode = builder.postalCode;
		this.businessName = builder.businessName;
		this.businessNameFlag = builder.businessNameFlag;
		this.effectiveDateString = builder.effectiveDateString;
		this.signature = builder.signature;
		this.signatureDateString = builder.signatureDateString;
		this.countryOfIncorporation = builder.countryOfIncorporation;
		this.foreignStatusAcceptedFlag = builder.foreignStatusAcceptedFlag;
	}

	@ApiModelProperty(name = "id")
	@JsonProperty("id")
	public Long getId() {
		return id;
	}

	@ApiModelProperty(name = "companyNumber")
	@JsonProperty("companyNumber")
	public Long getCompanyNumber() {
		return companyNumber;
	}

	@ApiModelProperty(name = "taxCountry")
	@JsonProperty("taxCountry")
	public String getTaxCountry() {
		return taxCountry;
	}

	@ApiModelProperty(name = "activeFlag")
	@JsonProperty("activeFlag")
	public Boolean getActiveFlag() {
		return activeFlag;
	}

	@ApiModelProperty(name = "taxNumber")
	@JsonProperty("taxNumber")
	public String getTaxNumber() {
		return taxNumber;
	}

	@ApiModelProperty(name = "taxEntityTypeCode")
	@JsonProperty("taxEntityTypeCode")
	public String getTaxEntityTypeCode() {
		return taxEntityTypeCode;
	}

	@ApiModelProperty(name = "activeDateString")
	@JsonProperty("activeDateString")
	public String getActiveDateString() {
		return activeDateString;
	}

	@ApiModelProperty(name = "taxName")
	@JsonProperty("taxName")
	public String getTaxName() {
		return taxName;
	}

	@ApiModelProperty(name = "firstName")
	@JsonProperty("firstName")
	public String getFirstName() {
		return firstName;
	}

	@ApiModelProperty(name = "middleName")
	@JsonProperty("middleName")
	public String getMiddleName() {
		return middleName;
	}

	@ApiModelProperty(name = "lastName")
	@JsonProperty("lastName")
	public String getLastName() {
		return lastName;
	}

	@ApiModelProperty(name = "businessFlag")
	@JsonProperty("businessFlag")
	public Boolean getBusinessFlag() {
		return businessFlag;
	}

	@ApiModelProperty(name = "taxVerificationStatusCode")
	@JsonProperty("taxVerificationStatusCode")
	public String getTaxVerificationStatusCode() {
		return taxVerificationStatusCode;
	}

	@ApiModelProperty(name = "verificationPending")
	@JsonProperty("verificationPending")
	public Boolean getVerificationPending() {
		return verificationPending;
	}

	@ApiModelProperty(name = "deliveryPolicyFlag")
	@JsonProperty("deliveryPolicyFlag")
	public Boolean getDeliveryPolicyFlag() {
		return deliveryPolicyFlag;
	}

	@ApiModelProperty(name = "address")
	@JsonProperty("address")
	public String getAddress() {
		return address;
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

	@ApiModelProperty(name = "postalCode")
	@JsonProperty("postalCode")
	public String getPostalCode() {
		return postalCode;
	}

	@ApiModelProperty(name = "country")
	@JsonProperty("country")
	public String getCountry() {
		return country;
	}

	@ApiModelProperty(name = "businessName")
	@JsonProperty("businessName")
	public String getBusinessName() {
		return businessName;
	}

	@ApiModelProperty(name = "businessNameFlag")
	@JsonProperty("businessNameFlag")
	public Boolean getBusinessNameFlag() {
		return businessNameFlag;
	}

	@ApiModelProperty(name = "effectiveDateString")
	@JsonProperty("effectiveDateString")
	public String getEffectiveDateString() {
		return effectiveDateString;
	}

	@ApiModelProperty(name = "signature")
	@JsonProperty("signature")
	public String getSignature() {
		return signature;
	}

	@ApiModelProperty(name = "signatureDateString")
	@JsonProperty("signatureDateString")
	public String getSignatureDateString() {
		return signatureDateString;
	}

	@ApiModelProperty(name = "countryOfIncorporation")
	@JsonProperty("countryOfIncorporation")
	public String getCountryOfIncorporation() {
		return countryOfIncorporation;
	}

	@ApiModelProperty(name = "foreignStatusAcceptedFlag")
	@JsonProperty("foreignStatusAcceptedFlag")
	public Boolean getForeignStatusAcceptedFlag() {
		return foreignStatusAcceptedFlag;
	}

	public static class Builder {
		private Long id;
		private Long companyNumber;
		private String taxCountry;
		private Boolean activeFlag;
		private String taxNumber;
		private String taxEntityTypeCode;
		private String activeDateString;
		private String taxName;
		private String firstName;
		private String middleName;
		private String lastName;
		private Boolean businessFlag;
		private String taxVerificationStatusCode;
		private Boolean verificationPending = Boolean.FALSE;
		private Boolean deliveryPolicyFlag = Boolean.FALSE;
		private String address;
		private String city;
		private String state;
		private String postalCode;
		private String country;

		//USA
		private String businessName;
		private Boolean businessNameFlag;
		private String effectiveDateString;
		private String signature;
		private String signatureDateString;

		// foreign
		private String countryOfIncorporation;
		private Boolean foreignStatusAcceptedFlag;

		public Builder(TaxInfoDTO dto) {
			this.id = dto.id;
			this.companyNumber = dto.companyNumber;
			this.taxCountry = dto.taxCountry;
			this.activeFlag = dto.activeFlag;
			this.taxNumber = dto.taxNumber;
			this.taxEntityTypeCode = dto.taxEntityTypeCode;
			this.activeDateString = dto.activeDateString;
			this.taxName = dto.taxName;
			this.firstName = dto.firstName;
			this.middleName = dto.middleName;
			this.lastName = dto.lastName;
			this.businessFlag = dto.businessFlag;
			this.taxVerificationStatusCode = dto.taxVerificationStatusCode;
			this.verificationPending = dto.verificationPending;
			this.deliveryPolicyFlag = dto.deliveryPolicyFlag;
			this.address = dto.address;
			this.city = dto.city;
			this.state = dto.state;
			this.country = dto.country;
			this.postalCode = dto.postalCode;
			this.businessName = dto.businessName;
			this.businessNameFlag = dto.businessNameFlag;
			this.effectiveDateString = dto.effectiveDateString;
			this.signature = dto.signature;
			this.signatureDateString = dto.signatureDateString;
			this.countryOfIncorporation = dto.countryOfIncorporation;
			this.foreignStatusAcceptedFlag = dto.foreignStatusAcceptedFlag;
		}

		public Builder() {}

		@JsonProperty("id") public Builder setId(final Long id) {
			this.id = id;
			return this;
		}

		@JsonProperty("companyNumber") public Builder setCompanyNumber(final Long companyNumber) {
			this.companyNumber = companyNumber;
			return this;
		}

		@JsonProperty("taxCountry") public Builder setTaxCountry(final String taxCountry) {
			this.taxCountry = taxCountry;
			return this;
		}

		@JsonProperty("activeFlag") public Builder setActiveFlag(final Boolean activeFlag) {
			this.activeFlag = activeFlag;
			return this;
		}

		@JsonProperty("taxNumber") public Builder setTaxNumber(final String taxNumber) {
			this.taxNumber = taxNumber;
			return this;
		}

		@JsonProperty("taxEntityTypeCode") public Builder setTaxEntityTypeCode(final String taxEntityTypeCode) {
			this.taxEntityTypeCode = taxEntityTypeCode;
			return this;
		}

		@JsonProperty("activeDateString") public Builder setActiveDateString(final String activeDateString) {
			this.activeDateString = activeDateString;
			return this;
		}

		@JsonProperty("taxName") public Builder setTaxName(final String taxName) {
			this.taxName = taxName;
			return this;
		}

		@JsonProperty("firstName") public Builder setFirstName(final String firstName) {
			this.firstName = firstName;
			return this;
		}

		@JsonProperty("middleName") public Builder setMiddleName(final String middleName) {
			this.middleName = middleName;
			return this;
		}

		@JsonProperty("lastName") public Builder setLastName(final String lastName) {
			this.lastName = lastName;
			return this;
		}

		@JsonProperty("businessFlag") public Builder setBusinessFlag(final Boolean businessFlag) {
			this.businessFlag = businessFlag;
			return this;
		}

		@JsonProperty("taxVerificationStatusCode") public Builder setTaxVerificationStatusCode(final String taxVerificationStatusCode) {
			this.taxVerificationStatusCode = taxVerificationStatusCode;
			return this;
		}

		@JsonProperty("verificationPending") public Builder setVerificationPending(final Boolean verificationPending) {
			this.verificationPending = verificationPending;
			return this;
		}

		@JsonProperty("deliveryPolicyFlag") public Builder setDeliveryPolicyFlag(final Boolean deliveryPolicyFlag) {
			this.deliveryPolicyFlag = deliveryPolicyFlag;
			return this;
		}

		@JsonProperty("address") public Builder setAddress(final String address) {
			this.address = address;
			return this;
		}

		@JsonProperty("city") public Builder setCity(final String city) {
			this.city = city;
			return this;
		}

		@JsonProperty("state") public Builder setState(final String state) {
			this.state = state;
			return this;
		}

		@JsonProperty("postalCode") public Builder setPostalCode(final String postalCode) {
			this.postalCode = postalCode;
			return this;
		}

		@JsonProperty("country") public Builder setCountry(final String country) {
			this.country = country;
			return this;
		}

		@JsonProperty("businessName") public Builder setBusinessName(final String businessName) {
			this.businessName = businessName;
			return this;
		}

		@JsonProperty("businessNameFlag") public Builder setBusinessNameFlag(final Boolean businessNameFlag) {
			this.businessNameFlag = businessNameFlag;
			return this;
		}

		@JsonProperty("effectiveDateString") public Builder setEffectiveDateString(final String effectiveDateString) {
			this.effectiveDateString = effectiveDateString;
			return this;
		}

		@JsonProperty("signature") public Builder setSignature(final String signature) {
			this.signature = signature;
			return this;
		}

		@JsonProperty("signatureDateString") public Builder setSignatureDateString(final String signatureDateString) {
			this.signatureDateString = signatureDateString;
			return this;
		}

		@JsonProperty("countryOfIncorporation") public Builder setCountryOfIncorporation(final String countryOfIncorporation) {
			this.countryOfIncorporation = countryOfIncorporation;
			return this;
		}

		@JsonProperty("foreignStatusAcceptedFlag") public Builder setForeignStatusAcceptedFlag(final Boolean foreignStatusAcceptedFlag) {
			this.foreignStatusAcceptedFlag = foreignStatusAcceptedFlag;
			return this;
		}

		public TaxInfoDTO build() {
			return new TaxInfoDTO(this);
		}
	}
}
