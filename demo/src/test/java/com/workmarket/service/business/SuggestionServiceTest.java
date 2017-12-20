package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.dto.VendorSuggestionDTO;
import com.workmarket.service.infra.business.SuggestionServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Unit tests for suggestion service.
 */
@RunWith(MockitoJUnitRunner.class)
public class SuggestionServiceTest {
	private static final long COMPANY_ID = 1L;
	private static final String COMPANY_NUMBER = "123";
	private static final String COMPANY_NAME = "companyName";
	private static final String EFFECTIVE_NAME = "effectiveName";
	private static final String CITY = "New York";
	private static final String STATE = "NY";
	private static final Country COUNTRY = Country.valueOf("USA");
	private static final String ADDRESS_STR = "New York, NY, USA";
	private static final String EMPTY_ADDRESS = "";

	@Mock private Company vendor;
	@Mock private Address address;
	@Mock private State state;
	@Mock private CompanyDAO companyDAO;
	@InjectMocks private SuggestionServiceImpl service = spy(new SuggestionServiceImpl());

	@Before
	public void setUp() {
		when(vendor.getId()).thenReturn(COMPANY_ID);
		when(vendor.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
		when(vendor.getName()).thenReturn(COMPANY_NAME);
		when(vendor.getEffectiveName()).thenReturn(EFFECTIVE_NAME);
		when(address.getCity()).thenReturn(CITY);
		when(address.getState()).thenReturn(state);
		when(state.getShortName()).thenReturn(STATE);
		when(state.getCountry()).thenReturn(COUNTRY);
	}

	@Test
	public void testSuggestVendor_noResult() throws Exception {
		when(companyDAO.suggest("prefix", true)).thenReturn(Lists.<Company>newArrayList());
		List<VendorSuggestionDTO> vendorSuggestionDTOs = service.suggestVendor("prefix");
		assertTrue(vendorSuggestionDTOs.isEmpty());

	}
	@Test
	public void testSuggestVendor_withAddress() throws Exception {
		when(companyDAO.suggest("prefix", true)).thenReturn(Lists.newArrayList(vendor));
		when(vendor.getAddress()).thenReturn(address);
		List<VendorSuggestionDTO> vendorSuggestionDTOs = service.suggestVendor("prefix");
		assertTrue(vendorSuggestionDTOs.size() == 1);
		VendorSuggestionDTO vendorSuggestionDTO = vendorSuggestionDTOs.get(0);
		assertEquals(COMPANY_NAME, vendorSuggestionDTO.getName());
		assertEquals(EFFECTIVE_NAME, vendorSuggestionDTO.getEffectiveName());
		assertEquals(COMPANY_NUMBER, vendorSuggestionDTO.getCompanyNumber());
		assertEquals(ADDRESS_STR, vendorSuggestionDTO.getCityStateCountry());
	}

	@Test
	public void testSuggestVendor_withoutAddress() throws Exception {
		when(companyDAO.suggest("prefix", true)).thenReturn(Lists.newArrayList(vendor));
		when(vendor.getAddress()).thenReturn(null);
		List<VendorSuggestionDTO> vendorSuggestionDTOs = service.suggestVendor("prefix");
		assertTrue(vendorSuggestionDTOs.size() == 1);
		VendorSuggestionDTO vendorSuggestionDTO = vendorSuggestionDTOs.get(0);
		assertEquals(COMPANY_NAME, vendorSuggestionDTO.getName());
		assertEquals(EFFECTIVE_NAME, vendorSuggestionDTO.getEffectiveName());
		assertEquals(COMPANY_NUMBER, vendorSuggestionDTO.getCompanyNumber());
		assertEquals(EMPTY_ADDRESS, vendorSuggestionDTO.getCityStateCountry());
	}
}
