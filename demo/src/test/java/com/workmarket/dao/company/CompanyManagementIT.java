package com.workmarket.dao.company;

import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.domains.model.CompanyPreference;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.BaseServiceIT;
import com.google.common.collect.ImmutableList;
import com.workmarket.domains.model.AddressType;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class CompanyManagementIT extends BaseServiceIT {

	@Test
	public void testCompanyLocations() throws Exception {
		User user = newFirstEmployee();
		Location location = createNewLocation(user);
		companyService.addCompanyLocation(user.getCompany().getId(), location.getId());

		List<Location> companyLocations = companyService.getCompanyLocations(user.getCompany().getId());
		assertEquals(1, companyLocations.size());
	}

	@Test
	public void testCompanyLocations_addSameLocation_notAdded() throws Exception {
		User user = newFirstEmployee();

		// add same location twice
		Location location = createNewLocation(user);

		companyService.addCompanyLocation(user.getCompany().getId(), location.getId());
		companyService.addCompanyLocation(user.getCompany().getId(), location.getId());

		List<Location> companyLocations = companyService.getCompanyLocations(user.getCompany().getId());
		assertEquals(1, companyLocations.size());
		assertFalse(companyLocations.iterator().next().getDeleted());
	}

	@Test
	public void testCompanyLocations_removeLocation() throws Exception {
		User user = newFirstEmployee();

		// add two locations 
		Location location1 = createNewLocation(user);
		Location location2 = createNewLocation(user);
		companyService.addCompanyLocation(user.getCompany().getId(), location1.getId());
		companyService.addCompanyLocation(user.getCompany().getId(), location2.getId());

		// remove second location 
		companyService.removeCompanyLocation(user.getCompany().getId(), location2.getId());

		List<Location> companyLocations = companyService.getCompanyLocations(user.getCompany().getId());
		assertEquals(1, companyLocations.size());
		assertEquals(companyLocations.iterator().next().getId(), location1.getId());
	}

	@Test
	public void testCompanyLocations_setLocations() throws Exception {
		User user = newFirstEmployee();

		// add two locations
		Location location1 = createNewLocation(user);
		Location location2 = createNewLocation(user);
		companyService.addCompanyLocation(user.getCompany().getId(), location1.getId());
		companyService.addCompanyLocation(user.getCompany().getId(), location2.getId());

		// set location 3 - should delete first 2 and add the third
		Location location3 = createNewLocation(user);
		companyService.setCompanyLocations(ImmutableList.of(location3.getId()), user.getCompany().getId());

		List<Location> locations = companyService.getCompanyLocations(user.getCompany().getId());
		assertEquals(1, locations.size());
		assertEquals(locations.iterator().next().getId(), location3.getId());
	}

	@Test
	public void testCompanyLocations_saveLocation() throws Exception {
		User user = newFirstEmployee();

		String address1 = "7 High St";
		String address2 = "Suite 407";
		String city = "Huntington";
		String state = "NY";
		String zip = "11743";
		String country = "US";
		double latitude = 40.868094;
		double longitude = -73.4289301;

		Location savedLocation = createNewLocation(user, address1, address2, city, state, zip, country, latitude, longitude);

		assertNotNull(savedLocation.getId());
		assertNotNull(savedLocation.getAddress());
		assertNotNull(savedLocation.getAddress().getId());
		assertEquals(address1, savedLocation.getAddress().getAddress1());
		assertEquals(address2, savedLocation.getAddress().getAddress2());
		assertEquals(city, savedLocation.getAddress().getCity());
		assertEquals(state, savedLocation.getAddress().getState().getShortName());
		assertEquals(zip, savedLocation.getAddress().getPostalCode());
		assertEquals(BigDecimal.valueOf(latitude), savedLocation.getAddress().getLatitude());
		assertEquals(BigDecimal.valueOf(longitude), savedLocation.getAddress().getLongitude());
		assertEquals(user.getCompany().getId(), savedLocation.getCompany().getId());
		assertEquals(Country.newInstance(country), savedLocation.getAddress().getCountry());
		assertEquals(AddressType.SERVICE_AREA, savedLocation.getAddress().getAddressType().getCode());
	}

	@Test
	public void testCompanySkill() throws Exception {
		User user = newFirstEmployee();
		Skill skill = newSkill();
		companyService.addCompanySkill(user.getCompany().getId(), skill.getId());

		List<Skill> companySkills = companyService.getCompanySkills(user.getCompany().getId());
		assertEquals(1, companySkills.size());
	}

	@Test
	public void testCompanySkill_addSameSkill_notAdded() throws Exception {
		User user = newFirstEmployee();

		// add same skill twice
		Skill skill = newSkill();
		companyService.addCompanySkill(user.getCompany().getId(), skill.getId());
		companyService.addCompanySkill(user.getCompany().getId(), skill.getId());

		List<Skill> companySkills = companyService.getCompanySkills(user.getCompany().getId());
		assertEquals(1, companySkills.size());
		assertFalse(companySkills.iterator().next().getDeleted());
	}

	@Test
	public void testCompanySkill_removeSkill() throws Exception {
		User user = newFirstEmployee();

		// add two skills
		Skill skill1 = newSkill();
		Skill skill2 = newSkill();
		companyService.addCompanySkill(user.getCompany().getId(), skill1.getId());
		companyService.addCompanySkill(user.getCompany().getId(), skill2.getId());

		// remove second skill
		companyService.removeCompanySkill(user.getCompany().getId(), skill2.getId());

		List<Skill> companySkills = companyService.getCompanySkills(user.getCompany().getId());
		assertEquals(1, companySkills.size());
		assertEquals(companySkills.iterator().next().getId(), skill1.getId());
	}

	@Test
	public void testCompanySkills_setSkills() throws Exception {
		User user = newFirstEmployee();

		// add two skills
		Skill skill1 = newSkill();
		Skill skill2 = newSkill();

		// add first two skills
		companyService.addCompanySkill(user.getCompany().getId(), skill1.getId());
		companyService.addCompanySkill(user.getCompany().getId(), skill2.getId());

		// set skills 3 - should delete first 2 and add the third
		Skill skill3 = newSkill();
		companyService.setCompanySkills(ImmutableList.of(skill3.getId()), user.getCompany().getId());

		List<Skill> companySkills = companyService.getCompanySkills(user.getCompany().getId());
		assertEquals(1, companySkills.size());
		assertEquals(companySkills.iterator().next().getId(), skill3.getId());
	}

	@Test
	public void testCompanySkills_addSkill() throws Exception {

		String skill = "archery";

		Skill savedSkill = companyService.saveCompanySkill(skill);

		assertNotNull(savedSkill.getId());
		assertNotNull(savedSkill.getName());
		assertEquals(skill, savedSkill.getName());
	}

	@Test
	public void testCompanyVerifications() throws Exception {
		User user = newFirstEmployee();
		CompanyPreference companyPreference = companyService.getCompanyPreference(user.getCompany().getId());
		assertFalse(companyPreference.isDrugTest());
		assertFalse(companyPreference.isBackgroundCheck());

		companyPreference.setBackgroundCheck(true);
		companyPreference.setDrugTest(true);
		companyService.updateCompanyPreference(companyPreference);

		companyPreference = companyService.getCompanyPreference(user.getCompany().getId());
		assertTrue(companyPreference.isDrugTest());
		assertTrue(companyPreference.isBackgroundCheck());
	}

	private Location createNewLocation(User user) {

		String address1 = "7 High St";
		String address2 = "Suite 407";
		String city = "Huntington";
		String state = "NY";
		String zip = "11743";
		String country = "US";
		double latitude = 40.868094;
		double longitude = -73.4289301;

		// create new location
		return createNewLocation(user, address1, address2, city, state, zip, country, latitude, longitude);
	}

	private Location createNewLocation(User user, String address1, String address2, String city, String state,
		String zip, String country, double latitude, double longitude) {

		// create new location
		LocationDTO location = new LocationDTO.Builder()
			.setAddressLine1(address1)
			.setAddressLine2(address2)
			.setCity(city)
			.setState(state)
			.setLatitude(latitude)
			.setLongitude(longitude)
			.setZip(zip)
			.setCountry(country)
			.build();
		Location savedLocation = companyService.saveCompanyLocation(user.getCompany().getId(), location);
		return savedLocation;
	}
}
