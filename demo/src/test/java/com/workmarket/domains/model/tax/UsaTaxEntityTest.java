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
public class UsaTaxEntityTest {

	@Test
	public void getFormattedTaxNumberForForm1099() {
		UsaTaxEntity usaTaxEntity = new UsaTaxEntity();
		usaTaxEntity.setTaxNumber("567811234");
		usaTaxEntity.setBusinessFlag(false);
		Assert.assertEquals(usaTaxEntity.getFormattedTaxNumberForForm1099(), "XXX-XX-1234");
		Assert.assertEquals(usaTaxEntity.getTaxNumber(), "567811234");
		Assert.assertEquals(usaTaxEntity.getSecureFormattedTaxNumber(), "***-**-1234");
		Assert.assertEquals(usaTaxEntity.getSecureTaxNumber(), "*****1234");
	}


	@Test
	public void getFormattedTaxNumberBusiness() {
		UsaTaxEntity usaTaxEntity = new UsaTaxEntity();
		usaTaxEntity.setTaxNumber("567811234");
		usaTaxEntity.setBusinessFlag(true);
		Assert.assertEquals(usaTaxEntity.getFormattedTaxNumberForForm1099(), "56-7811234");
		Assert.assertEquals(usaTaxEntity.getTaxNumber(), "567811234");
		Assert.assertEquals(usaTaxEntity.getSecureFormattedTaxNumber(), "**-***1234");
		Assert.assertEquals(usaTaxEntity.getSecureTaxNumber(), "*****1234");
	}


	@Test
	public void toDTO_einNumberIsMasked() {
		UsaTaxEntity usaTaxEntity = new UsaTaxEntity();
		usaTaxEntity.setTaxNumber("567811234");
		usaTaxEntity.setBusinessFlag(true);

		TaxEntityDTO dto = TaxEntityDTO.toDTO(usaTaxEntity);

		Assert.assertEquals(dto.getTaxNumber(), "*****1234");
	}

	@Test
	public void toDTOBusiness_ssnIsMasked() {
		UsaTaxEntity usaTaxEntity = new UsaTaxEntity();
		usaTaxEntity.setTaxNumber("567811234");
		usaTaxEntity.setBusinessFlag(false);

		TaxEntityDTO dto = TaxEntityDTO.toDTO(usaTaxEntity);

		Assert.assertEquals(dto.getTaxNumber(), "*****1234");
	}

	@Test
	public void getIsoCountry_returnsCorrectCountryObject() {
		Assert.assertEquals(Country.USA_COUNTRY, new UsaTaxEntity().getIsoCountry());
	}

}
