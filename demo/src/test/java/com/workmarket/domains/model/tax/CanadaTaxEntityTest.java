package com.workmarket.domains.model.tax;

import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.service.business.dto.TaxEntityDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * Author: rocio
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class CanadaTaxEntityTest {

	@Test
	public void getFormattedTaxNumber() {
		CanadaTaxEntity caTaxEntity = new CanadaTaxEntity();
		caTaxEntity.setTaxNumber("046454286");
		caTaxEntity.setBusinessFlag(false);
		Assert.assertEquals(caTaxEntity.getFormattedTaxNumber(), "046-454-286");
		Assert.assertEquals(caTaxEntity.getTaxNumber(), "046454286");
		Assert.assertEquals(caTaxEntity.getSecureFormattedTaxNumber(), "***-***-286");
		Assert.assertEquals(caTaxEntity.getSecureTaxNumber(), "******286");
	}


	@Test
	public void getFormattedTaxNumberBusiness() {
		CanadaTaxEntity caTaxEntity = new CanadaTaxEntity();
		caTaxEntity.setTaxNumber("567811234RT001");
		caTaxEntity.setBusinessFlag(true);
		Assert.assertEquals(caTaxEntity.getFormattedTaxNumber(), "567811234-RT-001");
		Assert.assertEquals(caTaxEntity.getTaxNumber(), "567811234RT001");
		Assert.assertEquals(caTaxEntity.getSecureFormattedTaxNumber(), "*********-**-001");
		Assert.assertEquals(caTaxEntity.getSecureTaxNumber(), "***********001");
	}

	@Test
	public void toDTO_taxBnNumberMasked() {
		CanadaTaxEntity caTaxEntity = new CanadaTaxEntity();
		caTaxEntity.setTaxNumber("567811234RT001");
		caTaxEntity.setBusinessFlag(true);

		TaxEntityDTO dto = TaxEntityDTO.toDTO(caTaxEntity);

		Assert.assertEquals(dto.getTaxNumber(), "***********001");
	}

	@Test
	public void toDTO_taxSinNumberMasked() {
		CanadaTaxEntity caTaxEntity = new CanadaTaxEntity();
		caTaxEntity.setTaxNumber("046454286");
		caTaxEntity.setBusinessFlag(false);

		TaxEntityDTO dto = TaxEntityDTO.toDTO(caTaxEntity);

		Assert.assertEquals(dto.getTaxNumber(), "******286");
	}

	@Test
	public void getIsoCountry_returnsCorrectCountryObject() {
		Assert.assertEquals(Country.CANADA_COUNTRY, new CanadaTaxEntity().getIsoCountry());
	}

}
