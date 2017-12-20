package com.workmarket.factory;

import com.workmarket.domains.model.tax.*;
import com.workmarket.service.business.dto.TaxEntityDTO;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class TaxEntityFactoryImpl implements TaxEntityFactory {

	@Override
	public AbstractTaxEntity newInstance(String country) throws InstantiationException {
		if (AbstractTaxEntity.COUNTRY_USA.equalsIgnoreCase(country))
			return new UsaTaxEntity();
		if (AbstractTaxEntity.COUNTRY_CANADA.equalsIgnoreCase(country))
			return new CanadaTaxEntity();
		if (AbstractTaxEntity.COUNTRY_OTHER.equalsIgnoreCase(country))
			return new ForeignTaxEntity();

		throw new InstantiationException(String.format("Invalid country for tax entity: %s", country ));
	}

	@Override public AbstractTaxEntity newInstance(TaxEntityDTO dto) throws InstantiationException {
		Assert.notNull(dto);
		Assert.isTrue(StringUtilities.equalsAny(dto.getTaxCountry(),
				AbstractTaxEntity.COUNTRY_USA, AbstractTaxEntity.COUNTRY_CANADA, AbstractTaxEntity.COUNTRY_OTHER));

		AbstractTaxEntity taxEntity;
		boolean isUSA = AbstractTaxEntity.COUNTRY_USA.equalsIgnoreCase(dto.getTaxCountry());
		boolean isOther= AbstractTaxEntity.COUNTRY_OTHER.equalsIgnoreCase(dto.getTaxCountry());
		if (isUSA)
			taxEntity = new UsaTaxEntity();
		else if (isOther)
			taxEntity = new ForeignTaxEntity();
		else
			taxEntity = new CanadaTaxEntity();

		BeanUtils.copyProperties(dto, taxEntity, "id", "businessNameFlag");
		taxEntity.setBusinessNameFlag(BooleanUtils.isTrue(dto.getBusinessNameFlag()));

		if (isUSA) {
			// nullable fields should be null when blank
			if (StringUtils.isBlank(dto.getBusinessName())) {
				taxEntity.setBusinessName(null);
			} else {
				if (taxEntity.isBusinessNameFlag()) {
					taxEntity.setBusinessName(dto.getBusinessName());
				}
			}
			if (StringUtils.isBlank(dto.getSignature())) {
				((UsaTaxEntity)taxEntity).setSignature(null);
			}

			if (StringUtils.isNotBlank(dto.getSignatureDateString())) {
				taxEntity.setSignedOn(dto.getSignatureDateAsCalendar());
			}
			if (StringUtils.isNotBlank(dto.getEffectiveDateString())) {
				((UsaTaxEntity)taxEntity).setEffectiveDate(dto.getEffectiveDateAsCalendar());
			}
		} else if (isOther) {
		 	if (StringUtils.isBlank(dto.getCountryOfIncorporation()))
				((ForeignTaxEntity)taxEntity).setCountryOfIncorporation(null);
		}

		if (StringUtils.isNotBlank(dto.getSignatureDateString())) {
			taxEntity.setSignedOn(dto.getSignatureDateAsCalendar());
		}

		if (StringUtils.isBlank(dto.getTaxEntityTypeCode())) {
			dto.setTaxEntityTypeCode(TaxEntityType.NONE);
		}

		taxEntity.setTaxEntityType(new TaxEntityType(dto.getTaxEntityTypeCode()));

		if (StringUtils.isEmpty(dto.getActiveDateString())) {
			taxEntity.setActiveDate(DateUtilities.getCalendarNowUtc());
		} else {
			taxEntity.setActiveDate(dto.getActiveDateAsCalendar());
		}

		return taxEntity;
	}
}
