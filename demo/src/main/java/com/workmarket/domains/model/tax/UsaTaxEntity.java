package com.workmarket.domains.model.tax;

import com.google.common.collect.ImmutableList;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Calendar;
import java.util.List;

/**
 * Created by nick on 8/16/12 3:29 PM
 */
@Entity(name = "usaTaxEntity")
@DiscriminatorValue(AbstractTaxEntity.COUNTRY_USA)
@AuditChanges
public class UsaTaxEntity extends AbstractTaxEntity {

	public static final List<String> US_TAX_COUNTRY_CODES = ImmutableList.of(
			Country.US, Country.USA, "AS", "MP", "PR", "VI", "GU", "UM"
	);
	Calendar effectiveDate;
	String signature;

	@Column(name="effective_date", nullable=true)
	public Calendar getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Calendar effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@Column(name="signature", nullable = true)
	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	@Transient
	@Override public String getCountry() {
		return AbstractTaxEntity.COUNTRY_USA;
	}

	@Transient
	@Override public Country getIsoCountry() {
		return Country.USA_COUNTRY;
	}


	@Transient
	@Override public String getFormattedTaxNumber() {
		if (BooleanUtils.isFalse(businessFlag)) {
			return StringUtilities.formatSsn(taxNumber);
		}
		return StringUtilities.formatEin(taxNumber);
	}

	@Transient
	@Override public String getSecureTaxNumber() {
		if (BooleanUtils.isFalse(businessFlag)) {
			return StringUtilities.getSecureSsn(taxNumber);
		}
		return StringUtilities.getSecureEin(taxNumber);
	}


	@Transient
	@Override public String getSecureFormattedTaxNumber() {
		if (BooleanUtils.isFalse(businessFlag)) {
			return StringUtilities.formatSecureSsn(taxNumber);
		}
		return StringUtilities.formatSecureEin(taxNumber);
	}

	@Transient
	@Override
	public String getFormattedTaxNumberForForm1099() {
		if (BooleanUtils.isFalse(businessFlag)) {
			return StringUtilities.formatSsn("XXXXX" + StringUtils.substring(getTaxNumberSanitized(), 5), true);
		}
		return StringUtilities.formatEin(taxNumber);
	}

	@Transient
	@Override
	public String getRawTaxNumber() {
		return getTaxNumberSanitized() == null ? "" : getTaxNumberSanitized().replaceAll("[^0-9]", "");
	}

}
