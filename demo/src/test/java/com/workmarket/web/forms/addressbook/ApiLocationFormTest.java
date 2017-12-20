package com.workmarket.web.forms.addressbook;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(BlockJUnit4ClassRunner.class)
public class ApiLocationFormTest {

	@Test
	public void useCorrectPostalCodeGetter() {
		ApiLocationForm form = new ApiLocationForm();
		final String postalCode = "12345";
		form.setPostal_code(postalCode);
		assertEquals(postalCode, form.getPostalCode());
		assertEquals(postalCode, form.getPostal_code());
	}
}
