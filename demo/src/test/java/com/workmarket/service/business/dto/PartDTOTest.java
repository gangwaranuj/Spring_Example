package com.workmarket.service.business.dto;

import com.workmarket.domains.work.model.part.ShippingProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(BlockJUnit4ClassRunner.class)
public class PartDTOTest {

	private static final String UUID_1 = "FOOO";
	private static final String UUID_2 = "BAAR";

	@Test
	public void getShippingProvider_setToNull_returnShippingProviderOther() throws Exception {
		PartDTO part = new PartDTO();
		part.setShippingProvider(null);
		assertEquals(part.getShippingProvider(), ShippingProvider.OTHER);
	}

	@Test
	public void formConversionAndBack() {
		final PartDTO part = new PartDTO();
		part.setUuid(UUID_1);
		part.setPartGroupUuid(UUID_2);

		final PartDTO roundTrip = part.asForm().asDTO();
		assertEquals(UUID_1, roundTrip.getUuid());
		assertEquals(UUID_2, roundTrip.getPartGroupUuid());

	}
}