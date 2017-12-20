package com.workmarket.dto;

import com.workmarket.domains.model.geocoding.Coordinate;
import com.workmarket.dto.AddressDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

@RunWith(BlockJUnit4ClassRunner.class)
public class AddressDTOTest {

	@Test
	public void getCoordinate_latAndLngAreNotTransposed() throws Exception {
		BigDecimal lat = new BigDecimal(1), lng = new BigDecimal(2);
		AddressDTO addressDTO = new AddressDTO();
		addressDTO.setLatitude(lat);
		addressDTO.setLongitude(lng);

		Coordinate coordinate = addressDTO.getCoordinate();
		assertEquals(coordinate.getLatitude(), lat.doubleValue(), 0);
		assertEquals(coordinate.getLongitude(), lng.doubleValue(), 0);
	}
}
