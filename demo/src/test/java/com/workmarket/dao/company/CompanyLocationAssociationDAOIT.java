package com.workmarket.dao.company;

import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.skill.CompanyLocationAssociation;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class CompanyLocationAssociationDAOIT extends BaseServiceIT {

	@Autowired CompanyLocationAssociationDAO companyLocationAssociationDAO;

	@Test
	@Transactional
	public void addCompanyLocation() throws Exception {
		User user = newFirstEmployee();
		Location location = newLocation(user);
		companyLocationAssociationDAO.addCompanyLocation(location, user.getCompany());

		List<Location> locations = companyLocationAssociationDAO.findCompanyLocations(user.getCompany());
		assertEquals(1, locations.size());
	}

	@Test
	@Transactional
	public void removeCompanyLocation() throws Exception {
		User user = newFirstEmployee();
		Location location = newLocation(user);
		companyLocationAssociationDAO.addCompanyLocation(location, user.getCompany());

		List<Location> locations = companyLocationAssociationDAO.findCompanyLocations(user.getCompany());
		assertEquals(1, locations.size());

		companyLocationAssociationDAO.removeCompanyLocation(location, user.getCompany());

		locations = companyLocationAssociationDAO.findCompanyLocations(user.getCompany());
		assertEquals(0, locations.size());
	}

	@Test
	@Transactional
	public void findCompanyLocationAssociations() throws Exception {
		User user = newFirstEmployee();
		Location location = newLocation(user);
		companyLocationAssociationDAO.addCompanyLocation(location, user.getCompany());

		List<CompanyLocationAssociation> companyLocationAssociations = companyLocationAssociationDAO.findCompanyLocationAssociations(user.getCompany());
		assertEquals(1, companyLocationAssociations.size());
	}

	@Test
	@Transactional
	public void addRemoveAddCompanyLocation() throws Exception {
		User user = newFirstEmployee();
		Location location = newLocation(user);

		companyLocationAssociationDAO.addCompanyLocation(location, user.getCompany());
		List<Location> locations = companyLocationAssociationDAO.findCompanyLocations(user.getCompany());
		assertEquals(1, locations.size());

		companyLocationAssociationDAO.removeCompanyLocation(location, user.getCompany());
		locations = companyLocationAssociationDAO.findCompanyLocations(user.getCompany());
		assertEquals(0, locations.size());

		companyLocationAssociationDAO.addCompanyLocation(location, user.getCompany());
		locations = companyLocationAssociationDAO.findCompanyLocations(user.getCompany());
		assertEquals(1, locations.size());
	}

	private Location newLocation(User user) {

		String address1 = "7 High St";
		String address2 = "Suite 407";
		String city = "Huntington";
		String state = "NY";
		String zip = "11743";
		String country = "US";
		double latitude = 40.868094;
		double longitude = -73.4289301;

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
