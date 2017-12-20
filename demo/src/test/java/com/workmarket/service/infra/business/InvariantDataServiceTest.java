package com.workmarket.service.infra.business;

import com.vividsolutions.jts.geom.Point;
import com.workmarket.api.v2.model.NationalIdApiDTO;
import com.workmarket.dao.DressCodeDAO;
import com.workmarket.dao.LanguageDAO;
import com.workmarket.dao.LocationTypeDAO;
import com.workmarket.dao.LookupEntityDAO;
import com.workmarket.dao.banking.BankRoutingDAO;
import com.workmarket.dao.banking.BankingIntegrationGenerationRequestTypeDAO;
import com.workmarket.dao.callingcodes.CallingCodeDAO;
import com.workmarket.dao.datetime.TimeZoneDAO;
import com.workmarket.dao.industry.IndustryDAO;
import com.workmarket.dao.mobile.MobileProviderDAO;
import com.workmarket.dao.postalcode.CountryDAO;
import com.workmarket.dao.postalcode.PostalCodeDAO;
import com.workmarket.dao.postalcode.StateDAO;
import com.workmarket.dao.recruiting.RecruitingVendorDAO;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.LocationService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InvariantDataServiceTest {

	@Mock CountryDAO countryDAO;
	@Mock StateDAO stateDAO;
	@Mock PostalCodeDAO postalCodeDAO;
	@Mock RecruitingVendorDAO recruitingVendorDAO;
	@Mock LanguageDAO languageDAO;
	@Mock LookupEntityDAO lookupEntityDAO;
	@Mock BankRoutingDAO bankRoutingDAO;
	@Mock IndustryDAO industryDAO;
	@Mock CallingCodeDAO callingCodesDAO;
	@Mock MobileProviderDAO mobileProviderDAO;
	@Mock LocationTypeDAO locationTypeDAO;
	@Mock BankingIntegrationGenerationRequestTypeDAO bankingIntegrationGenerationRequestTypeDAO;
	@Mock TimeZoneDAO timeZoneDAO;
	@Mock DressCodeDAO dressCodeDAO;
	@Mock LocationService locationService;
	@InjectMocks InvariantDataServiceImpl invariantDataService = spy(new InvariantDataServiceImpl());

	private State state = mock(State.class);
	private AddressDTO addressDTO = mock(AddressDTO.class);
	private AddressDTO addressDTOSpy = spy(new AddressDTO());
	private Point p = mock(Point.class);

	private static final Double
		OFFICE_LATITUDE = 40.868101,
		OFFICE_LONGITUDE = -73.426655;

	@Before
	public void init() {
		when(p.getX()).thenReturn(OFFICE_LONGITUDE);
		when(p.getY()).thenReturn(OFFICE_LATITUDE);
		when(addressDTOSpy.getPostalCode()).thenReturn("V2G0A5");
		when(addressDTOSpy.getCountry()).thenReturn("CAN");
		when(locationService.geocode(addressDTOSpy)).thenReturn(p);
		doReturn(state).when(invariantDataService).findStateWithCountryAndState(anyString(), anyString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void findOrCreatePostalCode_NoPostalCode() {
		when(addressDTO.getPostalCode()).thenReturn(null);

		invariantDataService.findOrCreatePostalCode(addressDTO);
	}

	@Test
	public void findOrCreatePostalCode_PostalCodeExists() {
		when(addressDTO.getPostalCode()).thenReturn("V2G0A5");
		when(addressDTO.getCountry()).thenReturn("CAN");
		when(addressDTO.isLatLongSet()).thenReturn(true);
		when(postalCodeDAO.findByPostalCode(eq("V2G0A5"), any(Country.class))).thenReturn(mock(PostalCode.class));

		PostalCode postalCode = invariantDataService.findOrCreatePostalCode(addressDTO);
		assertNotNull(postalCode);
	}

	@Test
	public void findOrCreatePostalCode_NoPostalCodeExists_PostalCodeLookup_Success() {
		when(postalCodeDAO.findByPostalCode(eq("V2G0A5"), any(Country.class))).thenReturn(null);

		PostalCode postalCode = invariantDataService.findOrCreatePostalCode(addressDTOSpy);

		assertEquals(OFFICE_LATITUDE, postalCode.getLatitude());
		assertEquals(OFFICE_LONGITUDE, postalCode.getLongitude());
	}

	@Test
	public void findOrCreatePostalCode_NoPostalCodeExists_ZeroLatitude_ZeroLongitude_Null() {
		when(postalCodeDAO.findByPostalCode(eq("V2G0A5"), any(Country.class))).thenReturn(null);
		when(p.getY()).thenReturn(0.0);
		when(p.getX()).thenReturn(0.0);

		PostalCode postalCode = invariantDataService.findOrCreatePostalCode(addressDTOSpy);

		assertNull(postalCode);
	}

	@Test
	public void returnAllNationalIds() {
		final List<NationalIdApiDTO> list = invariantDataService.getAllNationalIds();
		assertTrue(list.size() > 0);
	}
}
