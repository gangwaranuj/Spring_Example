package com.workmarket.api.v2.employer.settings.controllers.support;

import com.google.common.collect.ImmutableList;
import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.MakeItEasy;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.api.v2.employer.settings.models.CompanyProfileDTO;
import com.workmarket.api.v2.employer.settings.models.SkillDTO;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static com.natpryce.makeiteasy.Property.newProperty;

public class CompanyProfileMaker {

	public static final Property<CompanyProfileDTO, String> name = newProperty();
	public static final Property<CompanyProfileDTO, String> overview = newProperty();
	public static final Property<CompanyProfileDTO, String> website = newProperty();
	public static final Property<CompanyProfileDTO, String> avatar = newProperty();
	public static final Property<CompanyProfileDTO, LocationDTO.Builder> location = newProperty();
	public static final Property<CompanyProfileDTO, Integer> yearFounded = newProperty();
	public static final Property<CompanyProfileDTO, String> workInviteSentToUserId = newProperty();
	public static final Property<CompanyProfileDTO, Boolean> isInVendorSearch = newProperty();

	public static final Instantiator<CompanyProfileDTO> CompanyProfileDTO = new Instantiator<CompanyProfileDTO>() {

		@Override
		public CompanyProfileDTO instantiate(PropertyLookup<CompanyProfileDTO> lookup) {
			return new CompanyProfileDTO.Builder()
				.setName(lookup.valueOf(name, "My Awesome Company"))
				.setOverview(lookup.valueOf(overview, "Work Market Test"))
				.setWebsite(lookup.valueOf(website, "http://www.workmarket.com"))
				.setAvatar(lookup.valueOf(avatar, ""))
				.setLocation(lookup.valueOf(location, new LocationDTO.Builder(make(a(LocationMaker.LocationDTO)))))
				.setYearFounded(lookup.valueOf(yearFounded, 2010))
				.setWorkInviteSentToUserId(lookup.valueOf(workInviteSentToUserId, "6524291"))
				.setInVendorSearch(lookup.valueOf(isInVendorSearch, true))
				.build();
		}
	};

	public static final Instantiator<CompanyProfileDTO> CompanyProfileDTOWithEmptyLocation = new Instantiator<CompanyProfileDTO>() {

		@Override
		public CompanyProfileDTO instantiate(PropertyLookup<CompanyProfileDTO> lookup) {
			return new CompanyProfileDTO.Builder()
				.setName(lookup.valueOf(name, "My Awesome Company"))
				.setOverview(lookup.valueOf(overview, "Work Market Test"))
				.setWebsite(lookup.valueOf(website, "http://www.workmarket.com"))
				.setAvatar(lookup.valueOf(avatar, ""))
				.setLocation(lookup.valueOf(location, new LocationDTO.Builder(make(a(LocationMaker.EmptyLocationDTO)))))
				.setYearFounded(lookup.valueOf(yearFounded, 2010))
				.setWorkInviteSentToUserId(lookup.valueOf(workInviteSentToUserId, "6524291"))
				.setInVendorSearch(lookup.valueOf(isInVendorSearch, true))
				.build();
		}
	};

	public static final Instantiator<CompanyProfileDTO> CompanyProfileDTOWithVerifications = new Instantiator<CompanyProfileDTO>() {

		@Override
		public CompanyProfileDTO instantiate(PropertyLookup<CompanyProfileDTO> lookup) {
			return new CompanyProfileDTO.Builder()
				.setName(lookup.valueOf(name, "My Awesome Company"))
				.setOverview(lookup.valueOf(overview, "Work Market Test"))
				.setWebsite(lookup.valueOf(website, "http://www.workmarket.com"))
				.setAvatar(lookup.valueOf(avatar, ""))
				.setLocation(lookup.valueOf(location, new LocationDTO.Builder(make(a(LocationMaker.LocationDTO)))))
				.setYearFounded(lookup.valueOf(yearFounded, 2010))
				.setWorkInviteSentToUserId(lookup.valueOf(workInviteSentToUserId, "6524291"))
				.setInVendorSearch(lookup.valueOf(isInVendorSearch, true))
				.setDrugTest(true)
				.setBackgroundCheck(true)
				.build();
		}
	};

	public static final Instantiator<CompanyProfileDTO> CompanyProfileDTOWithLocationsServiced = new Instantiator<CompanyProfileDTO>() {

		@Override
		public CompanyProfileDTO instantiate(PropertyLookup<CompanyProfileDTO> lookup) {
			return new CompanyProfileDTO.Builder()
				.setName(lookup.valueOf(name, "My Awesome Company"))
				.setOverview(lookup.valueOf(overview, "Work Market Test"))
				.setWebsite(lookup.valueOf(website, "http://www.workmarket.com"))
				.setAvatar(lookup.valueOf(avatar, ""))
				.setLocation(lookup.valueOf(location, new LocationDTO.Builder(make(a(LocationMaker.LocationDTO)))))
				.setYearFounded(lookup.valueOf(yearFounded, 2010))
				.setWorkInviteSentToUserId(lookup.valueOf(workInviteSentToUserId, "6524291"))
				.setInVendorSearch(lookup.valueOf(isInVendorSearch, true))
				.setLocationsServiced(ImmutableList.of(new LocationDTO.Builder(make(a(LocationMaker.LocationDTO)))))
				.build();
		}
	};

	public static final Instantiator<CompanyProfileDTO> CompanyProfileDTOWithSkills = new Instantiator<CompanyProfileDTO>() {

		@Override
		public CompanyProfileDTO instantiate(PropertyLookup<CompanyProfileDTO> lookup) {
			return new CompanyProfileDTO.Builder()
				.setName(lookup.valueOf(name, "My Awesome Company"))
				.setOverview(lookup.valueOf(overview, "Work Market Test"))
				.setWebsite(lookup.valueOf(website, "http://www.workmarket.com"))
				.setAvatar(lookup.valueOf(avatar, ""))
				.setLocation(lookup.valueOf(location, new LocationDTO.Builder(make(a(LocationMaker.LocationDTO)))))
				.setYearFounded(lookup.valueOf(yearFounded, 2010))
				.setWorkInviteSentToUserId(lookup.valueOf(workInviteSentToUserId, "6524291"))
				.setInVendorSearch(lookup.valueOf(isInVendorSearch, true))
				.setSkills(ImmutableList.of(new SkillDTO.Builder(make(a(SkillMaker.SkillDTO)))))
				.setBackgroundCheck(true)
				.setDrugTest(true)
				.build();
		}
	};

	public static final Instantiator<CompanyProfileDTO> CompanyProfileDTOWithEmptyAddressLine1 = new Instantiator<CompanyProfileDTO>() {

		@Override
		public CompanyProfileDTO instantiate(PropertyLookup<CompanyProfileDTO> lookup) {
			return new CompanyProfileDTO.Builder()
				.setName(lookup.valueOf(name, "My Awesome Company"))
				.setOverview(lookup.valueOf(overview, "Work Market Test"))
				.setWebsite(lookup.valueOf(website, "http://www.workmarket.com"))
				.setAvatar(lookup.valueOf(avatar, ""))
				.setLocation(lookup.valueOf(location, new LocationDTO.Builder(make(a(LocationMaker.LocationDTO, withNull(LocationMaker.addressLine1))))))
				.setYearFounded(lookup.valueOf(yearFounded, 2010))
				.setWorkInviteSentToUserId(lookup.valueOf(workInviteSentToUserId, "6524291"))
				.setInVendorSearch(lookup.valueOf(isInVendorSearch, true))
				.build();
		}
	};

	public static final Instantiator<CompanyProfileDTO> CompanyProfileDTOWithEmptyCity = new Instantiator<CompanyProfileDTO>() {

		@Override
		public CompanyProfileDTO instantiate(PropertyLookup<CompanyProfileDTO> lookup) {
			return new CompanyProfileDTO.Builder()
				.setName(lookup.valueOf(name, "My Awesome Company"))
				.setOverview(lookup.valueOf(overview, "Work Market Test"))
				.setWebsite(lookup.valueOf(website, "http://www.workmarket.com"))
				.setAvatar(lookup.valueOf(avatar, ""))
				.setLocation(lookup.valueOf(location, new LocationDTO.Builder(make(a(LocationMaker.LocationDTO, withNull(LocationMaker.city))))))
				.setYearFounded(lookup.valueOf(yearFounded, 2010))
				.setWorkInviteSentToUserId(lookup.valueOf(workInviteSentToUserId, "6524291"))
				.setInVendorSearch(lookup.valueOf(isInVendorSearch, true))
				.build();
		}
	};

	public static final Instantiator<CompanyProfileDTO> CompanyProfileDTOWithEmptyState = new Instantiator<CompanyProfileDTO>() {

		@Override
		public CompanyProfileDTO instantiate(PropertyLookup<CompanyProfileDTO> lookup) {
			return new CompanyProfileDTO.Builder()
				.setName(lookup.valueOf(name, "My Awesome Company"))
				.setOverview(lookup.valueOf(overview, "Work Market Test"))
				.setWebsite(lookup.valueOf(website, "http://www.workmarket.com"))
				.setAvatar(lookup.valueOf(avatar, ""))
				.setLocation(lookup.valueOf(location, new LocationDTO.Builder(make(a(LocationMaker.LocationDTO, withNull(LocationMaker.state))))))
				.setYearFounded(lookup.valueOf(yearFounded, 2010))
				.setWorkInviteSentToUserId(lookup.valueOf(workInviteSentToUserId, "6524291"))
				.setInVendorSearch(lookup.valueOf(isInVendorSearch, true))
				.build();
		}
	};

	public static final Instantiator<CompanyProfileDTO> CompanyProfileDTOWithEmptyCountry = new Instantiator<CompanyProfileDTO>() {

		@Override
		public CompanyProfileDTO instantiate(PropertyLookup<CompanyProfileDTO> lookup) {
			return new CompanyProfileDTO.Builder()
				.setName(lookup.valueOf(name, "My Awesome Company"))
				.setOverview(lookup.valueOf(overview, "Work Market Test"))
				.setWebsite(lookup.valueOf(website, "http://www.workmarket.com"))
				.setAvatar(lookup.valueOf(avatar, ""))
				.setLocation(lookup.valueOf(location, new LocationDTO.Builder(make(a(LocationMaker.LocationDTO, withNull(LocationMaker.country))))))
				.setYearFounded(lookup.valueOf(yearFounded, 2010))
				.setWorkInviteSentToUserId(lookup.valueOf(workInviteSentToUserId, "6524291"))
				.setInVendorSearch(lookup.valueOf(isInVendorSearch, true))
				.build();
		}
	};

	public static final Instantiator<CompanyProfileDTO> CompanyProfileDTOWithEmptyLatLong = new Instantiator<CompanyProfileDTO>() {

		@Override
		public CompanyProfileDTO instantiate(PropertyLookup<CompanyProfileDTO> lookup) {
			return new CompanyProfileDTO.Builder()
				.setOverview(lookup.valueOf(overview, "Work Market Test"))
				.setWebsite(lookup.valueOf(website, "http://www.workmarket.com"))
				.setAvatar(lookup.valueOf(avatar, ""))
				.setLocation(lookup.valueOf(location, new LocationDTO.Builder(make(a(LocationMaker.LocationDTO, withNull(LocationMaker.latitude), withNull(LocationMaker.longitude), withNull(LocationMaker.state), withNull(LocationMaker.zip))))))
				.setYearFounded(lookup.valueOf(yearFounded, 2010))
				.setWorkInviteSentToUserId(lookup.valueOf(workInviteSentToUserId, "6524291"))
				.setInVendorSearch(lookup.valueOf(isInVendorSearch, true))
				.build();
		}
	};

	public static final Instantiator<CompanyProfileDTO> CompanyProfileDTOWithLatLonZeros = new Instantiator<CompanyProfileDTO>() {

		@Override
		public CompanyProfileDTO instantiate(PropertyLookup<CompanyProfileDTO> lookup) {
			return new CompanyProfileDTO.Builder()
				.setOverview(lookup.valueOf(overview, "Work Market Test"))
				.setWebsite(lookup.valueOf(website, "http://www.workmarket.com"))
				.setAvatar(lookup.valueOf(avatar, ""))
				.setLocation(lookup.valueOf(location, new LocationDTO.Builder(make(a(LocationMaker.LocationDTO, MakeItEasy.with(LocationMaker.latitude, Double.valueOf(0.0)), MakeItEasy.with(LocationMaker.longitude, Double.valueOf(0.0)), withNull(LocationMaker.state), withNull(LocationMaker.zip))))))
				.setYearFounded(lookup.valueOf(yearFounded, 2010))
				.setWorkInviteSentToUserId(lookup.valueOf(workInviteSentToUserId, "6524291"))
				.setInVendorSearch(lookup.valueOf(isInVendorSearch, true))
				.build();
		}
	};
}
