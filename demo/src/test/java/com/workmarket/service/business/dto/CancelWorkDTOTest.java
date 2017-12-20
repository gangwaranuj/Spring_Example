package com.workmarket.service.business.dto;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CancelWorkDTOTest {

	private CancelWorkDTO cancelWorkDTO;

	@Before
	public void setUp() {
		cancelWorkDTO = new CancelWorkDTO();
	}

	@Test
	public void isPaid_priceIsNull_returnFalse() throws Exception {
		cancelWorkDTO.setPrice(null);

		assertFalse(cancelWorkDTO.isPaid());
	}

	@Test
	public void isPaid_priceIsNegative_returnFalse() throws Exception {
		cancelWorkDTO.setPrice(-1d);

		assertFalse(cancelWorkDTO.isPaid());
	}

	@Test
	public void isPaid_priceIsZero_returnFalse() throws Exception {
		cancelWorkDTO.setPrice(0d);

		assertFalse(cancelWorkDTO.isPaid());
	}

	@Test
	public void isPaid_priceIsPositive_returnTrue() throws Exception {
		cancelWorkDTO.setPrice(1d);

		assertTrue(cancelWorkDTO.isPaid());
	}
}
