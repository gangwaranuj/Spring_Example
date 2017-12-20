package com.workmarket.domains.model.tax;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang3.BooleanUtils;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Calendar;

/**
 * Created by nick on 8/16/12 3:59 PM
 */
@Entity(name = "canadaTaxEntity")
@DiscriminatorValue(AbstractTaxEntity.COUNTRY_CANADA)
@AuditChanges
public class CanadaTaxEntity extends AbstractTaxEntity {

	@Transient
	@Override
	public String getCountry() {
		return AbstractTaxEntity.COUNTRY_CANADA;
	}

	@Transient
	@Override
	public Country getIsoCountry() {
		return Country.CANADA_COUNTRY;
	}

	@Transient
	@Override
	public String getSecureTaxNumber() {
		if (BooleanUtils.isFalse(businessFlag)) {
			return StringUtilities.getSecureCanadaSin(taxNumber);
		}
		return StringUtilities.getSecureCanadaBn(taxNumber);
	}

	@Transient
	@Override
	public String getSecureFormattedTaxNumber() {
		if (BooleanUtils.isFalse(businessFlag)) {
			return StringUtilities.formatSecureCanadaSin(taxNumber);
		}
		return StringUtilities.formatSecureCanadaBn(taxNumber);
	}

	@Transient
	@Override
	public String getFormattedTaxNumber() {
		return BooleanUtils.isTrue(businessFlag) ?
			StringUtilities.formatCanadaBn(taxNumber) :
			StringUtilities.formatCanadaSin(taxNumber);
	}

	@Transient
	@Override
	public String getRawTaxNumber() {
		return getTaxNumberSanitized() == null ? "" : getTaxNumberSanitized().replaceAll("[^\\p{Alnum}]", "");
	}

	@Transient
	public Calendar getEffectiveDate() {
		return getActiveDate();
	}
}
