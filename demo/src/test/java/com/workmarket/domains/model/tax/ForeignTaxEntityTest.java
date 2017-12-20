package com.workmarket.domains.model.tax;

import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.service.business.dto.TaxEntityDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class ForeignTaxEntityTest {
	@Test
	public void getIsoCountry_returnsCorrectCountryObject() {
		Assert.assertEquals(Country.INTERNATIONAL_COUNTRY, new ForeignTaxEntity().getIsoCountry());
	}

}
