package com.workmarket.api.v2.model;

import com.workmarket.domains.model.banking.BankRouting;
import com.workmarket.domains.model.postalcode.Country;
import org.junit.Test;

import static org.junit.Assert.*;

public class ApiBankRoutingDTOTest {

    @Test
    public void testRoutingNumberBuilder() throws Exception {
        final ApiBankRoutingDTO routing = new ApiBankRoutingDTO.Builder()
            .setBankName("FEDERAL RESERVE BANK")
            .setAddress("1000 PEACHTREE ST N.E")
            .setRoutingNumber("011000015")
            .setPostalCode("30309")
            .setCity("ATLANTA")
            .setCountry("USA")
            .setState("GA")
            .setId(1L)
            .build();

        assertEquals(new Long(1), routing.getId());
        assertEquals("GA", routing.getState());
        assertEquals("USA", routing.getCountry());
        assertEquals("ATLANTA", routing.getCity());
        assertEquals("30309", routing.getPostalCode());
        assertEquals("011000015", routing.getRoutingNumber());
        assertEquals("FEDERAL RESERVE BANK", routing.getBankName());
        assertEquals("1000 PEACHTREE ST N.E", routing.getAddress());
    }

    @Test
    public void testRoutingNumberBuildeWithEntity() throws Exception {
        final BankRouting entity = new BankRouting();

        entity.setAddress("8001 VILLA PARK DRIVE");
        entity.setBankName("STATE STREET BANK");
        entity.setCountry(Country.USA_COUNTRY);
        entity.setRoutingNumber("011000138");
        entity.setPostalCode("23228");
        entity.setCity("HENRICO");
        entity.setState("VA");
        entity.setId(2L);

        final ApiBankRoutingDTO routing = new ApiBankRoutingDTO.Builder(entity).build();

        assertEquals(new Long(2), routing.getId());
        assertEquals("VA", routing.getState());
        assertEquals("USA", routing.getCountry());
        assertEquals("HENRICO", routing.getCity());
        assertEquals("23228", routing.getPostalCode());
        assertEquals("011000138", routing.getRoutingNumber());
        assertEquals("STATE STREET BANK", routing.getBankName());
        assertEquals("8001 VILLA PARK DRIVE", routing.getAddress());
    }
}