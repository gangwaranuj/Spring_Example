package com.workmarket.api.v2.worker.service;

import com.workmarket.api.v2.model.ApiBankRoutingDTO;
import com.workmarket.domains.model.banking.BankRouting;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.service.infra.business.SuggestionService;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ApiBankRoutingSuggestionServiceTest {

    SuggestionService suggestionService;

    @Before
    public void setUp() {
        this.suggestionService = mock(SuggestionService.class);
    }

    @Test
    public void testSuggestBankRouting() throws Exception {
        final String search = "6550600";
        final String country = Country.USA;
        final BankRouting r1 = new BankRouting();
        final BankRouting r2 = new BankRouting();
        final ApiBankRoutingSuggestionService service = new ApiBankRoutingSuggestionService(suggestionService);

        r1.setBankName("FEDERAL RESERVE BANK");
        r1.setAddress("1000 PEACHTREE ST N.E");
        r1.setCountry(Country.USA_COUNTRY);
        r1.setRoutingNumber("011000015");
        r1.setPostalCode("30309");
        r1.setCity("ATLANTA");
        r1.setState("GA");
        r1.setId(1L);

        r2.setAddress("8001 VILLA PARK DRIVE");
        r2.setBankName("STATE STREET BANK");
        r2.setCountry(Country.USA_COUNTRY);
        r2.setRoutingNumber("011000138");
        r2.setPostalCode("23228");
        r2.setCity("HENRICO");
        r2.setState("VA");
        r2.setId(2L);

        when(suggestionService.suggestBankRouting(search, country))
            .thenReturn(Arrays.asList(r1, r2));

        final List<ApiBankRoutingDTO> result = service.suggestBankRouting(country, search);

        assertEquals(2, result.size());

        final ApiBankRoutingDTO routing1 = result.get(0);
        final ApiBankRoutingDTO routing2 = result.get(1);

        assertEquals(new Long(1), routing1.getId());
        assertEquals("GA", routing1.getState());
        assertEquals("USA", routing1.getCountry());
        assertEquals("ATLANTA", routing1.getCity());
        assertEquals("30309", routing1.getPostalCode());
        assertEquals("011000015", routing1.getRoutingNumber());
        assertEquals("FEDERAL RESERVE BANK", routing1.getBankName());
        assertEquals("1000 PEACHTREE ST N.E", routing1.getAddress());

        assertEquals(new Long(2), routing2.getId());
        assertEquals("VA", routing2.getState());
        assertEquals("USA", routing2.getCountry());
        assertEquals("HENRICO", routing2.getCity());
        assertEquals("23228", routing2.getPostalCode());
        assertEquals("011000138", routing2.getRoutingNumber());
        assertEquals("STATE STREET BANK", routing2.getBankName());
        assertEquals("8001 VILLA PARK DRIVE", routing2.getAddress());
    }

}