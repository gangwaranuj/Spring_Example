package com.workmarket.service.business.dto;

import com.google.common.base.MoreObjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.CanadaTaxEntity;
import com.workmarket.domains.model.tax.ForeignTaxEntity;
import com.workmarket.domains.model.tax.TaxEntityType;
import com.workmarket.domains.model.tax.TaxVerificationStatusType;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Calendar;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaxEntityDTO implements Serializable {

	private static final long serialVersionUID = -8532885397305773010L;
	private Long id;

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

	// denormalize address because we don't want table-level constraints -- these should be plaintext
	private String address;
	private String city;
	private String state;
	private String postalCode;
	private String country;

	// USA
	private String businessName;
	private Boolean businessNameFlag;
	private String effectiveDateString;
	private String signature;
	private String signatureDateString;

	// foreign
	private String countryOfIncorporation;
	private Boolean foreignStatusAcceptedFlag;

	public static TaxEntityDTO newTaxEntityDTO() {
		TaxEntityDTO taxEntityDTO = new TaxEntityDTO();
		return taxEntityDTO;
	}

	public static TaxEntityDTO toDTO(CanadaTaxEntity canadaTaxEntity) {
		TaxEntityDTO dto = new TaxEntityDTO();
		BeanUtils.copyProperties(canadaTaxEntity, dto);
		dto.setTaxCountry(AbstractTaxEntity.COUNTRY_CANADA);
		dto.setTaxEntityTypeCode(canadaTaxEntity.getTaxEntityType() == null ? TaxEntityType.NONE : canadaTaxEntity.getTaxEntityType().getCode());
		dto.setTaxNumber(canadaTaxEntity.getSecureTaxNumber());
		dto.setActiveFlag(canadaTaxEntity.getActiveFlag());
		dto.setTaxVerificationStatusCode(canadaTaxEntity.getStatus() == null ? TaxVerificationStatusType.UNVERIFIED : canadaTaxEntity.getStatus().getCode());
		dto.setEffectiveDateStringFromCalendar(canadaTaxEntity.getEffectiveDate());
		return dto;
	}

	public static TaxEntityDTO toDTO(ForeignTaxEntity foreignTaxEntity) {
		TaxEntityDTO dto = new TaxEntityDTO();
		BeanUtils.copyProperties(foreignTaxEntity, dto);
		dto.setTaxCountry(AbstractTaxEntity.COUNTRY_OTHER);
		dto.setTaxEntityTypeCode(foreignTaxEntity.getTaxEntityType() == null ? TaxEntityType.NONE : foreignTaxEntity.getTaxEntityType().getCode());
		dto.setTaxNumber(foreignTaxEntity.getSecureTaxNumber());
		dto.setActiveFlag(foreignTaxEntity.getActiveFlag());
		dto.setEffectiveDateStringFromCalendar(foreignTaxEntity.getEffectiveDate());
		dto.setTaxVerificationStatusCode(foreignTaxEntity.getStatus() == null ? TaxVerificationStatusType.UNVERIFIED : foreignTaxEntity.getStatus().getCode());
		return dto;
	}

	public static TaxEntityDTO toDTO(UsaTaxEntity usaTaxEntity) {
		TaxEntityDTO dto = new TaxEntityDTO();
		BeanUtils.copyProperties(usaTaxEntity, dto);
		dto.setTaxCountry(AbstractTaxEntity.COUNTRY_USA);
		dto.setTaxEntityTypeCode(usaTaxEntity.getTaxEntityType() == null ? TaxEntityType.NONE : usaTaxEntity.getTaxEntityType().getCode());
		dto.setTaxVerificationStatusCode(usaTaxEntity.getStatus() == null ? TaxVerificationStatusType.UNVERIFIED : usaTaxEntity.getStatus().getCode());
		dto.setTaxNumber(usaTaxEntity.getSecureTaxNumber());
		dto.setBusinessNameFlag(usaTaxEntity.isBusinessNameFlag());
		dto.setBusinessName(usaTaxEntity.getBusinessName());
		dto.setSignatureDateStringFromCalendar(usaTaxEntity.getSignedOn());
		dto.setEffectiveDateStringFromCalendar(usaTaxEntity.getEffectiveDate());

		return dto;
	}

	public static TaxEntityDTO toDTO(AbstractTaxEntity taxEntity) {
		if (taxEntity instanceof UsaTaxEntity) {
			return TaxEntityDTO.toDTO((UsaTaxEntity) taxEntity);
		} else if (taxEntity instanceof CanadaTaxEntity) {
			return TaxEntityDTO.toDTO((CanadaTaxEntity) taxEntity);
		} else if (taxEntity instanceof ForeignTaxEntity) {
			return TaxEntityDTO.toDTO((ForeignTaxEntity) taxEntity);
		} else {
			return null;
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTaxName() {
		return taxName;
	}

	public String getFirstName() {
		return firstName;
	}

	public TaxEntityDTO setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public TaxEntityDTO setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public TaxEntityDTO setActiveFlag(Boolean activeFlag) {
		this.activeFlag = activeFlag;
		return this;
	}

	public Boolean getActiveFlag() {
		return activeFlag;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public Boolean getBusinessFlag() {
		return businessFlag;
	}

	public TaxEntityDTO setBusinessFlag(Boolean businessFlag) {
		this.businessFlag = businessFlag;
		return this;
	}

	public String getTaxVerificationStatusCode() {
		return taxVerificationStatusCode;
	}

	public void setTaxVerificationStatusCode(String taxVerificationStatusCode) {
		this.taxVerificationStatusCode = taxVerificationStatusCode;
	}

	public Boolean getVerificationPending() {
		return verificationPending;
	}

	public void setVerificationPending(Boolean verificationPending) {
		this.verificationPending = verificationPending;
	}

	public Boolean getDeliveryPolicyFlag() {
		return deliveryPolicyFlag;
	}

	public void setDeliveryPolicyFlag(Boolean deliveryPolicyFlag) {
		this.deliveryPolicyFlag = deliveryPolicyFlag;
	}

	public String getTaxNumber() {
		return taxNumber;
	}

	public String getTaxEntityTypeCode() {
		return taxEntityTypeCode;
	}

	public Boolean getBusinessNameFlag() {
		return businessNameFlag;
	}

	public void setBusinessNameFlag(Boolean businessNameFlag) {
		this.businessNameFlag = businessNameFlag;
	}

	public void setTaxName(String taxName) {
		this.taxName = taxName;
	}

	public TaxEntityDTO setTaxNumber(String taxNumber) {
		this.taxNumber = taxNumber;
		return this;
	}

	public TaxEntityDTO setTaxEntityTypeCode(String taxEntityTypeCode) {
		this.taxEntityTypeCode = MoreObjects.firstNonNull(taxEntityTypeCode, TaxEntityType.NONE);
		return this;
	}

	public String getAddress() {
		return address;
	}

	public TaxEntityDTO setAddress(String address) {
		this.address = address;
		return this;
	}

	public String getCity() {
		return city;
	}

	public TaxEntityDTO setCity(String city) {
		this.city = city;
		return this;
	}

	public String getState() {
		return state;
	}

	public TaxEntityDTO setState(String state) {
		this.state = state;
		return this;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public TaxEntityDTO setPostalCode(String postalCode) {
		this.postalCode = postalCode;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getEffectiveDateString() {
		return effectiveDateString;
	}

	public void setEffectiveDateString(String effectiveDateString) {
		this.effectiveDateString = effectiveDateString;
	}

	public void setEffectiveDateStringFromCalendar(Calendar calendar) {
		this.effectiveDateString = DateUtilities.format("yyyy-MM-dd HH:mm:ss", calendar);
	}

	public String getActiveDateString() {
		return activeDateString;
	}

	public void setActiveDateString(String activeDateString) {
		this.activeDateString = activeDateString;
	}

	public String getTaxCountry() {
		return taxCountry;
	}

	public TaxEntityDTO setTaxCountry(String taxCountry) {
		this.taxCountry = taxCountry;
		return this;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getSignatureDateString() {
		return signatureDateString;
	}

	public void setSignatureDateString(String signatureDateString) {
		this.signatureDateString = signatureDateString;
	}

	public void setSignatureDateStringFromCalendar(Calendar calendar) {
		this.signatureDateString = DateUtilities.format("yyyy-MM-dd HH:mm:ss", calendar);
	}

	public String getCountryOfIncorporation() {
		return countryOfIncorporation;
	}

	public void setCountryOfIncorporation(String countryOfIncorporation) {
		this.countryOfIncorporation = countryOfIncorporation;
	}


	public Boolean getForeignStatusAcceptedFlag() {
		return foreignStatusAcceptedFlag;
	}

	public TaxEntityDTO setForeignStatusAcceptedFlag(Boolean foreignStatusAcceptedFlag) {
		this.foreignStatusAcceptedFlag = foreignStatusAcceptedFlag;
		return this;
	}

	public boolean isSigned() {
		return ((AbstractTaxEntity.COUNTRY_USA.equals(taxCountry) && StringUtilities.all(signature, signatureDateString))
				|| AbstractTaxEntity.COUNTRY_CANADA.equals(taxCountry)
				|| (AbstractTaxEntity.COUNTRY_OTHER.equals(taxCountry) && (businessFlag || foreignStatusAcceptedFlag)));
	}

	public Calendar getEffectiveDateAsCalendar() {
		return DateUtilities.getCalendarFromDateString(effectiveDateString, "UTC");
	}

	public Calendar getSignatureDateAsCalendar() {
		return DateUtilities.getCalendarFromDateString(signatureDateString, "UTC");
	}

	public Calendar getActiveDateAsCalendar() {
		return DateUtilities.getCalendarFromDateString(activeDateString, "UTC");
	}

	public String getFullName(){
		return StringUtilities.fullName(this.firstName, this.middleName, this.lastName);
	}
}
