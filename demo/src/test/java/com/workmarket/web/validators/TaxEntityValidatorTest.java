package com.workmarket.web.validators;

import com.workmarket.domains.model.tax.TaxEntityType;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.service.business.dto.TaxEntityDTO;
import com.workmarket.utility.DateUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.validation.Validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(BlockJUnit4ClassRunner.class)
public class TaxEntityValidatorTest extends BaseValidatorTest {

	private TaxEntityValidator validator;

	@Before
	public void setUp() throws Exception {
		validator = new TaxEntityValidator();
	}

	// ---- Tests ----

	@Test
	public void testUsaBusinessRequiredFields() {
		TaxEntityDTO t = getDefaultUsaBusinessTaxEntity();
		assertFalse(validate(t).hasErrors());

		t.setTaxEntityTypeCode(" ");
		assertTrue(validate(t).hasErrors());

		t = getDefaultUsaBusinessTaxEntity();
		t.setCity(" ");
		assertTrue(validate(t).hasErrors());

		t = getDefaultUsaBusinessTaxEntity();
		t.setState(" ");
		assertTrue(validate(t).hasErrors());

		t = getDefaultUsaBusinessTaxEntity();
		t.setPostalCode("");
		assertTrue(validate(t).hasErrors());

		t = getDefaultUsaBusinessTaxEntity();
		t.setTaxNumber("");
		assertTrue(validate(t).hasErrors());

		t = getDefaultUsaBusinessTaxEntity();
		t.setDeliveryPolicyFlag(false);
		assertTrue(validate(t).hasErrors());
	}

	@Test
	public void testUsaIndividualRequiredFields() {
		TaxEntityDTO t = getDefaultUsaIndividualTaxEntity();
		assertFalse(validate(t).hasErrors());
	}

	@Test
	public void testUsaBusinessFieldValidation() {
		// invalid EIN
		TaxEntityDTO t = getDefaultUsaBusinessTaxEntity();
		t.setTaxNumber("43");
		assertTrue(validate(t).hasErrors());

		// invalid entity type type
		t = getDefaultUsaBusinessTaxEntity();
		t.setTaxEntityTypeCode("barf");
		assertTrue(validate(t).hasErrors());

		// missing Business name
		t = getDefaultUsaBusinessTaxEntity();
		t.setBusinessName("");
		assertTrue(validate(t).hasErrors());
	}

	@Test
	public void testUsaIndividualFieldValidation() {
		// invalid SSN
		TaxEntityDTO t = getDefaultUsaIndividualTaxEntity();
		t.setTaxNumber("43");
		assertTrue(validate(t).hasErrors());

		// invalid tax name
		t = getDefaultUsaIndividualTaxEntity();
		t.setLastName("this name which previously was not longer than, " +
			"is, now and forever, longer than the maximum length which happens to be exactly and precisely "
			+ TaxEntityValidator.TAX_NAME_MAX_LENGTH + " characters");
		assertTrue(validate(t).hasErrors());

		// invalid tax entity
		t = getDefaultUsaIndividualTaxEntity();
		t.setTaxEntityTypeCode(TaxEntityType.LLC_DISREGARDED);
		assertTrue(validate(t).hasErrors());

		// invalid effective date
		t = getDefaultUsaIndividualTaxEntity();
		t.setEffectiveDateStringFromCalendar(DateUtilities.getCalendarFromDateString("1985-01-01 00:00:00", "UTC"));
		assertTrue(validate(t).hasErrors());
	}

	@Test
	public void testCanadaBusinessRequiredFields() {
		TaxEntityDTO t = getDefaultCanadaBusinessTaxEntity();
		assertTrue(validate(t).hasErrors());

		t.setCity(" ");
		assertTrue(validate(t).hasErrors());

		t = getDefaultCanadaBusinessTaxEntity();
		t.setState(" ");
		assertTrue(validate(t).hasErrors());

		t = getDefaultCanadaBusinessTaxEntity();
		t.setPostalCode("");
		assertTrue(validate(t).hasErrors());

		t = getDefaultCanadaBusinessTaxEntity();
		t.setTaxNumber("");
		assertTrue(validate(t).hasErrors());
	}

	@Test
	public void testCanadaIndividualRequiredFields() {
		TaxEntityDTO t = getDefaultCanadaIndividualTaxEntity();
		assertFalse(validate(t).hasErrors());

		t.setCity(" ");
		assertTrue(validate(t).hasErrors());

		t = getDefaultCanadaIndividualTaxEntity();
		t.setState(" ");
		assertTrue(validate(t).hasErrors());

		t = getDefaultCanadaIndividualTaxEntity();
		t.setPostalCode("");
		assertTrue(validate(t).hasErrors());

		t = getDefaultCanadaIndividualTaxEntity();
		t.setTaxNumber("");
		assertTrue(validate(t).hasErrors());
	}


	@Test
	public void testCanadaBusinessFieldValidation() {
		// invalid BN
		TaxEntityDTO t = getDefaultCanadaBusinessTaxEntity();
		t.setTaxNumber("123424243-FX-7894");
		assertTrue(validate(t).hasErrors());
	}

	@Test
	public void testCanadaIndividualFieldValidation() {
		// invalid SIN -- Luhn
		TaxEntityDTO t = getDefaultCanadaIndividualTaxEntity();
		t.setTaxNumber("502-329-0234");
		assertTrue(validate(t).hasErrors());

		// empty SIN
		t = getDefaultCanadaIndividualTaxEntity();
		t.setTaxNumber("");
		assertTrue(validate(t).hasErrors());
	}

	@Test
	public void testForeignBusinessRequiredFields() {
		TaxEntityDTO t = getDefaultForeignBusinessTaxEntity();
		assertFalse(validate(t).hasErrors());

		t.setTaxEntityTypeCode(" ");
		assertTrue(validate(t).hasErrors());

		t = getDefaultForeignBusinessTaxEntity();
		t.setCity(" ");
		assertTrue(validate(t).hasErrors());

		t = getDefaultForeignBusinessTaxEntity();
		t.setState(" ");
		assertTrue(validate(t).hasErrors());

		t = getDefaultForeignBusinessTaxEntity();
		t.setPostalCode("");
		assertTrue(validate(t).hasErrors());
	}

	@Test
	public void testForeignIndividualRequiredFields() {
		TaxEntityDTO t = getDefaultForeignIndividualTaxEntity();
		assertFalse(validate(t).hasErrors());

		t.setTaxEntityTypeCode(" ");
		assertTrue(validate(t).hasErrors());

		t = getDefaultForeignIndividualTaxEntity();
		t.setCity(" ");
		assertTrue(validate(t).hasErrors());

		t = getDefaultForeignIndividualTaxEntity();
		t.setState(" ");
		assertTrue(validate(t).hasErrors());

		t = getDefaultForeignIndividualTaxEntity();
		t.setPostalCode("");
		assertTrue(validate(t).hasErrors());
	}


	@Test
	public void testForeignBusinessFieldValidation() {
		// invalid country of incorporation
		TaxEntityDTO t = getDefaultForeignBusinessTaxEntity();
		t.setCountryOfIncorporation("steve");
		assertTrue(validate(t).hasErrors());

		// invalid business designation
		t = getDefaultForeignBusinessTaxEntity();
		t.setTaxEntityTypeCode(TaxEntityType.TRUST);
		assertTrue(validate(t).hasErrors());
	}


	@Test
	public void testForeignIndividualFieldValidation() {
		// invalid business designation
		TaxEntityDTO t = getDefaultForeignIndividualTaxEntity();
		t.setTaxEntityTypeCode(TaxEntityType.TRUST);
		assertTrue(validate(t).hasErrors());

		// terms not accepted
		t = getDefaultForeignIndividualTaxEntity();
		t.setForeignStatusAcceptedFlag(null);
		assertTrue(validate(t).hasErrors());
	}

	@Test
	public void testForeignIndividual_withoutTaxNumber_fail() {
		// invalid business designation
		TaxEntityDTO t = getDefaultForeignIndividualTaxEntity();
		t.setTaxNumber("");
		assertTrue(validate(t).hasErrors());
	}


	private void setUsaDefaultAddress(TaxEntityDTO a) {
		a.setAddress("20 West 20th Street");
		a.setCity("New York");
		a.setState("NY");
		a.setPostalCode("10010");
	}

	private void setCanadaDefaultAddress(TaxEntityDTO a) {
		a.setAddress("800 Wellington Street");
		a.setCity("Ottawa");
		a.setState("ON");
		a.setPostalCode("K0A1B0");
	}

	private void setForeignDefaultAddress(TaxEntityDTO a) {
		a.setAddress("800 Chemin de la Marde");
		a.setCity("Aix-en-Provence");
		a.setState("CÃ´te-d'Azur");
		a.setPostalCode("13001");
	}

	private TaxEntityDTO getDefaultUsaBusinessTaxEntity() {
		TaxEntityDTO t = new TaxEntityDTO();
		t.setTaxCountry(AbstractTaxEntity.COUNTRY_USA);

		setUsaDefaultAddress(t);
		t.setBusinessFlag(true);
		t.setTaxName("The TEST Company");
		t.setLastName("The TEST Company");
		t.setBusinessNameFlag(true);
		t.setBusinessName("The TEST Company Business");
		t.setTaxEntityTypeCode(TaxEntityType.C_CORP);
		t.setTaxNumber("23-2349910"); // EIN
		t.setDeliveryPolicyFlag(true);
		return t;
	}

	private TaxEntityDTO getDefaultCanadaBusinessTaxEntity() {
		TaxEntityDTO t = new TaxEntityDTO();
		t.setTaxCountry(AbstractTaxEntity.COUNTRY_CANADA);

		setCanadaDefaultAddress(t);
		t.setTaxName("Doug Flutie, Eh");
		t.setLastName("Doug Flutie, Eh");
		t.setBusinessFlag(true);
		t.setTaxNumber("123456789-RN-2345"); // BN
		t.setDeliveryPolicyFlag(true);
		return t;
	}

	private TaxEntityDTO getDefaultForeignBusinessTaxEntity() {
		TaxEntityDTO t = new TaxEntityDTO();
		t.setTaxCountry(AbstractTaxEntity.COUNTRY_OTHER);

		setCanadaDefaultAddress(t);
		t.setTaxName("ChÃ¢teaublanc");
		t.setLastName("ChÃ¢teaublanc");
		t.setBusinessFlag(true);
		t.setTaxEntityTypeCode(TaxEntityType.LLC_DISREGARDED);
		t.setCountryOfIncorporation("FRA");
		t.setTaxNumber("123-456-pommes-frites-99"); // made-up
		t.setDeliveryPolicyFlag(true);
		return t;
	}

	private TaxEntityDTO getDefaultUsaIndividualTaxEntity() {
		TaxEntityDTO t = new TaxEntityDTO();
		t.setTaxCountry(AbstractTaxEntity.COUNTRY_USA);

		setUsaDefaultAddress(t);
		t.setTaxName("The TEST Company");
		t.setFirstName("The TEST");
		t.setLastName("Company");
		t.setBusinessFlag(false);
		t.setTaxNumber("233-23-9910"); // SSN
		t.setTaxEntityTypeCode(TaxEntityType.INDIVIDUAL);
		t.setDeliveryPolicyFlag(true);
		return t;
	}

	private TaxEntityDTO getDefaultCanadaIndividualTaxEntity() {
		TaxEntityDTO t = new TaxEntityDTO();
		t.setTaxCountry(AbstractTaxEntity.COUNTRY_CANADA);

		setCanadaDefaultAddress(t);
		t.setFirstName("Doug Flutie");
		t.setLastName("Eh");
		t.setBusinessFlag(false);
		t.setTaxNumber("046 454 286"); // SIN
		t.setDeliveryPolicyFlag(true);
		return t;
	}

	private TaxEntityDTO getDefaultForeignIndividualTaxEntity() {
		TaxEntityDTO t = new TaxEntityDTO();
		t.setTaxCountry(AbstractTaxEntity.COUNTRY_OTHER);

		setForeignDefaultAddress(t);
		t.setFirstName("Jean-Guy");
		t.setLastName("Frontenac");
		t.setBusinessFlag(false);
		t.setTaxEntityTypeCode(TaxEntityType.INDIVIDUAL);
		t.setForeignStatusAcceptedFlag(true);
		t.setTaxNumber("123-456-JeanGuy-99"); // made-up
		t.setDeliveryPolicyFlag(true);
		return t;
	}

	protected Validator getValidator() {
		return validator;
	}

}
