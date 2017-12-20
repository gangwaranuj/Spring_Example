package com.workmarket.service.business.feed;

import com.workmarket.search.gen.Common.PostalCode;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.web.forms.feed.FeedRequestParams;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FindWorkFeedAdapterTest {

	@Mock private InvariantDataService invariantDataService;
	@InjectMocks private FindWorkFeedAdapter findWorkFeedAdapter;

	@Test
	public void testHasValidLatLonReturnsValid() {
		final FeedRequestParams params = new FeedRequestParams();
		params.setLatitude("74.1023");
		params.setLongitude("-32.1123");
		assertTrue(findWorkFeedAdapter.hasValidLatLon(params));
	}

	@Test
	public void testHasValidLatLonReturnsInValidWithEmptyString() {
		final FeedRequestParams params = new FeedRequestParams();
		assertFalse(findWorkFeedAdapter.hasValidLatLon(params));
	}

	@Test
	public void testHasValidLatLonReturnsInValidWithInvalidNumber() {
		final FeedRequestParams params = new FeedRequestParams();
		params.setLatitude("74.1023");
		params.setLongitude("-190.2312");
		assertFalse(findWorkFeedAdapter.hasValidLatLon(params));
	}

	@Test
	public void testBuildPostalCodeReturnsEmpty() {
		FeedRequestParams params = new FeedRequestParams();
		params.setVirtual(false);
		PostalCode postalCode = findWorkFeedAdapter.buildPostalCode(params);
		assertTrue(StringUtils.isEmpty(postalCode.getCode()));
		assertTrue(StringUtils.isEmpty(postalCode.getLatitude()));
		assertTrue(StringUtils.isEmpty(postalCode.getLongitude()));
	}

	@Test
	public void testBuildPostalCodeReturnsValidPostalCode() {
		final String zipCode = "10018";
		final Double lon = -73.935242d;
		final Double lat = 40.730610d;
		final FeedRequestParams params = new FeedRequestParams();
		params.setPostalCode(zipCode);
		com.workmarket.domains.model.postalcode.PostalCode pc =
			mock(com.workmarket.domains.model.postalcode.PostalCode.class);
		when(pc.getLatitude()).thenReturn(lat);
		when(pc.getLongitude()).thenReturn(lon);
		when(invariantDataService.getPostalCodeByCode(zipCode)).thenReturn(pc);

		PostalCode postalCode = findWorkFeedAdapter.buildPostalCode(params);
		assertEquals(zipCode, postalCode.getCode());
		assertEquals(lat.toString(), postalCode.getLatitude());
		assertEquals(lon.toString(), postalCode.getLongitude());
		verify(invariantDataService, times(1)).getPostalCodeByCode(zipCode);
	}

	@Test
	public void testBuildPostalCodeReturnsValidLatLonOnly() {
		final Double lon = -73.935242d;
		final Double lat = 40.730610d;
		final FeedRequestParams params = new FeedRequestParams();
		params.setLatitude(lat.toString());
		params.setLongitude(lon.toString());

		PostalCode postalCode = findWorkFeedAdapter.buildPostalCode(params);
		assertTrue(StringUtils.isEmpty(postalCode.getCode()));
		assertEquals(lat.toString(), postalCode.getLatitude());
		assertEquals(lon.toString(), postalCode.getLongitude());
		verify(invariantDataService, never()).getPostalCodeByCode(anyString());
	}

	@Test
	public void testBuildPostalCodeWithInvalidLatLonReturnsValidPostalCode() {
		final String zipCode = "10018";
		final Double lon = -73.935242d;
		final Double lat = 40.730610d;
		final Double invalidLat = 92.123322d;
		final FeedRequestParams params = new FeedRequestParams();
		params.setPostalCode(zipCode);
		params.setLatitude(invalidLat.toString());
		params.setLongitude(lon.toString());
		com.workmarket.domains.model.postalcode.PostalCode pc =
			mock(com.workmarket.domains.model.postalcode.PostalCode.class);
		when(pc.getLatitude()).thenReturn(lat);
		when(pc.getLongitude()).thenReturn(lon);
		when(invariantDataService.getPostalCodeByCode(zipCode)).thenReturn(pc);

		PostalCode postalCode = findWorkFeedAdapter.buildPostalCode(params);
		assertEquals(zipCode, postalCode.getCode());
		assertEquals(lat.toString(), postalCode.getLatitude());
		assertEquals(lon.toString(), postalCode.getLongitude());
		assertFalse(findWorkFeedAdapter.hasValidLatLon(params));
		verify(invariantDataService, times(1)).getPostalCodeByCode(zipCode);
	}
}
