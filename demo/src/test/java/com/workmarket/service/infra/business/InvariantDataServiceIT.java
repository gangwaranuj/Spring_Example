package com.workmarket.service.infra.business;

import com.workmarket.domains.model.MobileProvider;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.model.tax.TaxEntityType;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisConfig;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.dto.AddressDTO;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class InvariantDataServiceIT extends BaseServiceIT {

	@Autowired InvariantDataService invariantDataService;
	@Autowired @Qualifier("redisCacheOnly") RedisAdapter redisAdapter;

	@Test
	public void findAllMobileProviders() throws Exception {
		List<MobileProvider> providers = invariantDataService.findAllMobileProviders();

		assertEquals(14, providers.size());
	}

	@Test
	public void getTaxEntityTypes() throws Exception {
		List<TaxEntityType> taxEntities = invariantDataService.getTaxEntityTypes();
		assertNotNull(taxEntities);
		assertTrue(taxEntities.size() >= 14);
	}

	@Test
	public void findCountryById() throws Exception {
		assertNotNull(invariantDataService.findCountryById(Country.USA));
		assertNotNull(invariantDataService.findCountryById(Country.CANADA));
	}

	@Test
	public void getCountries() throws Exception {
		assertNotNull(invariantDataService.getCountries());
	}

	@Test
	public void getCountryDTOs() throws Exception {
		assertNotNull(invariantDataService.getCountryDTOs());
		assertNotNull(redisAdapter.get(RedisConfig.COUNTRIES));
	}

	@Test
	public void getStateCode_givenStateCode_returnCode() throws Exception {
		assertEquals("NY", invariantDataService.getStateCode("NY"));
	}

	@Test
	public void getStateCode_givenStateName_returnCode() throws Exception {
		assertEquals("NY", invariantDataService.getStateCode("New York"));
	}

	@Test
	public void findState() throws Exception {
		assertNotNull(invariantDataService.findState("NY"));
	}

	@Test
	public void findState_WithCountryAndShort_Success() throws Exception {
		assertNotNull(invariantDataService.findStateWithCountryAndState("USA", "NY"));
	}

	@Test
	public void findState_WithCountryAndFull_Success() throws Exception {
		assertNotNull(invariantDataService.findStateWithCountryAndState("USA", "New York"));
	}

	@Test
	public void getPostalCodeByCode() throws Exception {
		assertNotNull(invariantDataService.getPostalCodeByCode("10012"));
		assertNull(invariantDataService.getPostalCodeByCode("123456789"));
	}

	@Test
	public void findAllActiveTimeZones() throws Exception {
		assertNotNull(invariantDataService.findAllActiveTimeZones());
	}

	@Test
	public void findTimeZonesByTimeZoneId() throws Exception {
		assertNotNull(invariantDataService.findTimeZonesByTimeZoneId("US/Eastern"));
		assertNotNull(invariantDataService.findTimeZonesByTimeZoneId("Canada/Central"));
	}

	@Test
	@Transactional
	public void findOrCreatePostalCode() {
		AddressDTO addressDTO = new AddressDTO();
		addressDTO.setAddress1("1205 PROSPERITY WAY");
		addressDTO.setCity("WILLIAMS LAKE");
		addressDTO.setState("BC");
		addressDTO.setPostalCode("V2G3A7");
		addressDTO.setCountry("CAN");

		PostalCode p = invariantDataService.findOrCreatePostalCode(addressDTO);
		assertNotNull(p);
	}

	@Test
	@Transactional
	public void findOrCreatePostalCode_badPostalCode_returnNull() {
		AddressDTO addressDTO = new AddressDTO();
		addressDTO.setAddress1("1205 PROSPERITY WAY");
		addressDTO.setCity("WILLIAMS LAKE");
		addressDTO.setState("BC");
		addressDTO.setPostalCode("V2G3A7777D");
		addressDTO.setCountry("BUP");

		assertNull(invariantDataService.findOrCreatePostalCode(addressDTO));
	}

	@Test
	public void getStateDTOs_methodCallDoesCachePut() {
		invariantDataService.getStateDTOs();
		assertTrue(redisAdapter.get(RedisConfig.STATES).isPresent());
	}

	@Test
	public void getAllUniqueActiveCallingCodeIds_methodCallDoesCachePut() {
		invariantDataService.getAllUniqueActiveCallingCodeIds();
		assertTrue(redisAdapter.get(RedisConfig.UNIQUE_ACTIVE_CALLING_CODE_IDS).isPresent());
	}

	@Test
	public void getLocationTypeDTOs_methodCallDoesCachePut() {
		invariantDataService.getLocationTypeDTOs();
		assertTrue(redisAdapter.get(RedisConfig.LOCATION_TYPES).isPresent());
	}

	private static String getRedisKey(String p1, String p2) {
		return RedisConfig.POSTAL_CODE_DISTANCE + p1 + ":" + p2;
	}
}
