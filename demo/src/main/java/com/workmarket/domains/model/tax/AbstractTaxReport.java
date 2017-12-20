package com.workmarket.domains.model.tax;

import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.utility.StringUtilities;
import com.workmarket.vault.models.Securable;
import com.workmarket.vault.models.Secured;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.Calendar;

/**
 * Author: rocio
 */

@MappedSuperclass
@Securable
public abstract class AbstractTaxReport extends AuditedEntity {

	private static final long serialVersionUID = 8650028816053920651L;

	private String taxYear;
	//Just for referential integrity but we need snapshot of the data
	private AbstractTaxEntity taxEntity;

	private String firstName;
	private String lastName;
	@Secured(mode = Secured.OBSCURED) private String taxNumber;
	private TaxEntityType taxEntityType;
	private Boolean businessFlag;

	private String address;
	private String city;
	private String state;
	private String postalCode;
	private String country;

	private Calendar effectiveDate;
	private Long companyId;

	@Column(name = "tax_year", nullable = false)
	public String getTaxYear() {
		return taxYear;
	}

	public void setTaxYear(String taxYear) {
		this.taxYear = taxYear;
	}

	@ManyToOne()
	@JoinColumn(name = "tax_entity_id", referencedColumnName = "id")
	public AbstractTaxEntity getTaxEntity() {
		return taxEntity;
	}

	public void setTaxEntity(AbstractTaxEntity taxEntity) {
		this.taxEntity = taxEntity;
	}

	@Column(name = "first_name")
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name = "last_name", nullable = false)
	public String getLastName() {
		return lastName;
	}
	
	@Transient
	public String getFullName() {
		if (this.getBusinessFlag()) {
			return this.lastName;
		}
		return StringUtilities.fullName(this.firstName, this.lastName);
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(name = "tax_number", nullable = false)
	public String getTaxNumber() {
		return taxNumber;
	}

	public void setTaxNumber(String taxNumber) {
		this.taxNumber = taxNumber;
	}

	@ManyToOne
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "tax_entity_type_code", referencedColumnName = "code", nullable = false)
	public TaxEntityType getTaxEntityType() {
		return taxEntityType;
	}

	public void setTaxEntityType(TaxEntityType taxEntityType) {
		this.taxEntityType = taxEntityType;
	}

	@Column(name = "business_flag", nullable = false)
	public Boolean getBusinessFlag() {
		return businessFlag;
	}

	public void setBusinessFlag(Boolean businessFlag) {
		this.businessFlag = businessFlag;
	}

	@Column(name = "address", nullable = false)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "city", nullable = false)
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(name = "state", nullable = false)
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Column(name = "postal_code", nullable = false)
	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	@Column(name = "country", nullable = false)
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Column(name = "effective_date")
	public Calendar getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Calendar effectiveDate) {
		this.effectiveDate = effectiveDate;
	}


	@Column(name = "company_id", nullable = false)
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	@Transient
	public String getFormattedTaxType() {
		if (taxEntityType == null) {
			return StringUtils.EMPTY;
		}
		switch (taxEntityType.getCode()){
			case TaxEntityType.INDIVIDUAL:
				return "Individual/sole proprietor";
			case TaxEntityType.CORP:
				return "C Corporation";
			case TaxEntityType.C_CORP:
				return "C Corporation";
			case TaxEntityType.S_CORP:
				return "S Corporation";
			case TaxEntityType.PARTNER:
				return "Partnership";
			case TaxEntityType.TRUST:
				return "Trust/estate";
			default:
				return StringUtils.EMPTY;
		}
	}

}


