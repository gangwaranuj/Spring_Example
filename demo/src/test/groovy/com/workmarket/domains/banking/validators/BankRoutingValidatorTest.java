package com.workmarket.domains.banking.validators;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by ianha on 4/27/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class BankRoutingValidatorTest {
    private static final String INSTITUTION_NUMBER = "789";
    private static final String TRANSIT_NUMBER = "12345"; // also known as branch number
    private static final String ROUTING_NUMBER = "0" + TRANSIT_NUMBER + INSTITUTION_NUMBER;

    BankRoutingValidator validator;

    @Before
    public void setup() {
        validator = new BankRoutingValidator();
    }

    @Test
    public void shouldValidateRoutingNumber() {
        assertTrue(validator.isValidRoutingNumber(ROUTING_NUMBER));
    }

    @Test
    public void shouldValidateFalseRoutingNumberNull() {
        assertFalse(validator.isValidRoutingNumber(null));
    }

    @Test
    public void shouldValidateFalseRoutingNumberEmpty() {
        assertFalse(validator.isValidRoutingNumber(""));
    }

    @Test
    public void shouldValidateTransitNumber() {
        assertTrue(validator.isValidBranchNumber(TRANSIT_NUMBER));
    }

    @Test
    public void shouldValidateFalseTransitNumberNull() {
        assertFalse(validator.isValidBranchNumber(null));
    }

    @Test
    public void shouldValidateFalseTransitNumberEmpty() {
        assertFalse(validator.isValidBranchNumber(""));
    }

    @Test
    public void shouldValidateInstitutionNumber() {
        assertTrue(validator.isValidInstitutionNumber(INSTITUTION_NUMBER));
    }

    @Test
    public void shouldValidateFalseInstitutionNumberNull() {
        assertFalse(validator.isValidInstitutionNumber(null));
    }

    @Test
    public void shouldValidateFalseInstitutionNumberEmpty() {
        assertFalse(validator.isValidInstitutionNumber(""));
    }
}