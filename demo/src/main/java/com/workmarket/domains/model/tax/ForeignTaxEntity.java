package com.workmarket.domains.model.tax;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.utility.StringUtilities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Calendar;

/**
 * Created by nick on 8/16/12 3:39 PM
 */
@Entity(name = "foreignTaxEntity")
@DiscriminatorValue(AbstractTaxEntity.COUNTRY_OTHER)
@AuditChanges
public class ForeignTaxEntity extends AbstractTaxEntity {

	private String countryOfIncorporation;
	private Boolean foreignStatusAcceptedFlag;

	@Column(name="country_of_incorporation", nullable = true)
	public String getCountryOfIncorporation() {
		return countryOfIncorporation;
	}

	public void setCountryOfIncorporation(String countryOfIncorporation) {
		this.countryOfIncorporation = countryOfIncorporation;
	}

	@Column(name="foreign_status_accepted_flag", nullable = true)
	public Boolean getForeignStatusAcceptedFlag() {
		return foreignStatusAcceptedFlag;
	}

	public void setForeignStatusAcceptedFlag(Boolean foreignStatusAcceptedFlag) {
		this.foreignStatusAcceptedFlag = foreignStatusAcceptedFlag;
	}

	@Transient
	@Override public String getCountry() {
		return AbstractTaxEntity.COUNTRY_OTHER;

	}

	@Transient
	@Override public Country getIsoCountry() {
		return Country.INTERNATIONAL_COUNTRY;
	}


	@Transient
	@Override public String getSecureTaxNumber() {
		return StringUtilities.getSecureForeignTaxNumber(taxNumber);
	}


	@Transient
	@Override public String getSecureFormattedTaxNumber() {
		return StringUtilities.formatSecureForeignTaxNumber(taxNumber);
	}

	@Transient
	@Override public String getFormattedTaxNumber() {
		return StringUtilities.formatForeignTaxNumber(taxNumber);
	}

	@Transient
	@Override public String getRawTaxNumber() {
		return getTaxNumberSanitized(); // can't make assumptions about the format
	}

	@Transient
	public Calendar getEffectiveDate() {
		return getActiveDate();
	}



}
