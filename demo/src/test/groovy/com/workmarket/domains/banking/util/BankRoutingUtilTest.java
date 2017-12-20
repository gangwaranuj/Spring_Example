package com.workmarket.domains.banking.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by ianha on 4/27/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class BankRoutingUtilTest {
    private static final String INSTITUTION_NUMBER = "789";
    private static final String BRANCH_NUMBER = "12345"; // also known as branch number
    private static final String ROUTING_NUMBER = "0" + INSTITUTION_NUMBER + BRANCH_NUMBER;

    @Test
    public void shouldCreateValidRoutingNumber() {
        assertEquals(ROUTING_NUMBER, BankRoutingUtil.buildRoutingNumber(BRANCH_NUMBER, INSTITUTION_NUMBER));
    }

    @Test
    public void shouldReturnTransitNumber() {
        assertEquals(BRANCH_NUMBER, BankRoutingUtil.getBranchNumber(ROUTING_NUMBER));
    }

    @Test
    public void shouldReturnInstitutionNumber() {
        assertEquals(INSTITUTION_NUMBER, BankRoutingUtil.getInstitutionNumber(ROUTING_NUMBER));
    }
}