package com.workmarket.utility;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.State;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;

@RunWith(BlockJUnit4ClassRunner.class)
public class ProjectionUtilitiesTest {

	private ClientLocation clientLocation;

	@Before
	public void setup() {
		clientLocation = new ClientLocation();
		clientLocation.setId(1L);

		Address address = new Address();
		address.setAddress1("240 West 37th. St");
		address.setCity("New York");
		State state = new State();
		state.setName("New York");
		state.setShortName("NY");
		address.setState(state);

		Country country = new Country();
		country.setName("United States");
		country.setISO3("USA");
		address.setCountry(country);
		clientLocation.setAddress(address);
	}

	@Test
	public void testNestedBeanProperties() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		List<ClientLocation> clientLocations = ImmutableList.of(clientLocation);
		ImmutableList<Map> maps =  ImmutableList.copyOf(ProjectionUtilities.projectAsArray(
			new String[]{"id", "address", "city", "state", "country"},
			ImmutableMap.of(
				"address", "address.address1",
				"city", "address.city",
				"state", "address.state.name",
				"country", "address.country.name"),
			clientLocations));

		Map<String, String> result = maps.get(0);
		assertThat(result, hasEntry("id", String.valueOf(clientLocation.getId())));
		assertThat(result, hasEntry("address", String.valueOf(clientLocation.getAddress().getAddress1())));
		assertThat(result, hasEntry("city", String.valueOf(clientLocation.getAddress().getCity())));
		assertThat(result, hasEntry("state", String.valueOf(clientLocation.getAddress().getState().getName())));
		assertThat(result, hasEntry("country", String.valueOf(clientLocation.getAddress().getCountry().getName())));
	}


	@Test(expected = NoSuchMethodException.class)
	public void testNestedBeanPropertyWithNoMatchingMethod() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		List<ClientLocation> clientLocations = ImmutableList.of(clientLocation);
		ImmutableList<Map> maps =  ImmutableList.copyOf(ProjectionUtilities.projectAsArray(
			new String[]{"address"},
			ImmutableMap.of(
				"address", "address.address3"),
			clientLocations));
	}
}
