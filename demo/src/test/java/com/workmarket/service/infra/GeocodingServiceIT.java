package com.workmarket.service.infra;

import com.vividsolutions.jts.geom.Point;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisConfig;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.dto.AddressVerificationDTO;
import com.workmarket.service.infra.business.GeocodingService;
import com.workmarket.test.IntegrationTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

// TODO: Configure ignored tests to run against actual Geocode service, not the mocked service
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class GeocodingServiceIT extends BaseServiceIT {

	@Autowired private GeocodingService geocodingService;
	@Autowired @Qualifier("redisCacheOnly") private RedisAdapter redisAdapter;

	@Test
	@Ignore
	public void geocodeAddress() throws Exception {
		Point point = geocodingService.geocode("377 Sterling Place, Brooklyn, NY 11238");

		assertEquals(40.67534, point.getY(), 0);
		assertEquals(-73.965535, point.getX(), 0);

		point = geocodingService.geocode("305 Smith Ct, Edgewater, NJ 07020");

		assertEquals(40.8092693, point.getY(), 0);
		assertEquals(-73.98540969999999, point.getX(), 0);

		point = geocodingService.geocode("P.O. Box 360001, Fort Lauderdale, FL 33336-0001");

		assertEquals(-80.27, point.getX(), 0);
		assertEquals(26.11, point.getY(), 0);
	}

	@Test
	@Ignore
	public void parseAddress() throws Exception {
		AddressDTO address = geocodingService.parseAddress("377 Sterling Place, Brooklyn, NY 11238");

		assertEquals("377 Sterling Place", address.getAddress1());
		assertEquals("Brooklyn", address.getCity());
		assertEquals("NY", address.getState());
		assertEquals("11238", address.getPostalCode());
	}

	@Test
	@Ignore
	public void verifyAmbiguousAddress() throws Exception {
		AddressVerificationDTO verification = geocodingService.verify("20 20th Street, NY, NY 10001");

		assertFalse(verification.isVerified());
		assertTrue(verification.getMatches().size() > 1);

		for (String a : verification.getMatches()) {
			System.out.println("[geomatch] " + a);
		}
	}

	@Test
	@Ignore
	public void verifyMultipleMatches() throws Exception {
		AddressVerificationDTO verify = geocodingService.verify("303 Main St.");
		assertFalse(verify.isVerified());
		assertTrue(verify.getComponentMatches().size() > 1);
	}

	@Test
	public void verifyVerifiedAddress() throws Exception {
		AddressVerificationDTO verification = geocodingService.verify("377 Sterling Place, Brooklyn, NY 11238");
		assertTrue(verification.isVerified());
	}

	@Test
	public void verifyLegitimateAddress() throws Exception {
		AddressVerificationDTO verification = geocodingService.verify("300 Chestnut Ridge Road, Woodcliff Lake, NJ 07677");

		assertTrue(verification.isVerified());
	}

	@Test
	public void geocode_methodCallDoesCachePut() {
		final String address = "1600 Pennsylvania Avenue, Washington, DC";
		geocodingService.geocode(address);
		assertThat(redisAdapter.get(RedisConfig.GEOCODE + address).isPresent(), is(true));
	}
}
