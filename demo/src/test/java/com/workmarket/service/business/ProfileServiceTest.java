package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.dao.asset.UserAssetAssociationDAO;
import com.workmarket.dao.asset.UserLinkAssociationDAO;
import com.workmarket.dao.profile.ProfileDAO;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.CallingCode;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.asset.UserLinkAssociation;
import com.workmarket.domains.model.asset.type.UserAssetAssociationType;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.service.business.dto.ProfileDTO;
import com.workmarket.service.infra.business.InvariantDataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProfileServiceTest {

	@Mock UserAssetAssociationDAO userAssetAssociationDAO;
	@Mock UserLinkAssociationDAO userLinkAssociationDAO;
	@Mock ProfileDAO profileDAO;
	@Mock AddressService addressService;
	@Mock InvariantDataService invariantDataService;
	@Mock DateTimeService dateTimeService;
	@InjectMocks ProfileServiceImpl profileService;

	private static final String
		JOB_TITLE = "JOB_TITLE",
		OVERVIEW = "OVERVIEW",
		WORK_PHONE = "WORK_PHONE",
		WORK_PHONE_EXT = "WORK_PHONE_EXT",
		WORK_PHONE_INT = "WORK_PHONE_INT",
		MOBILE = "MOBILE",
		MOBILE_INT = "MOBILE_INT",
		FIRST_NAME = "FIRST",
		LAST_NAME = "LAST",
		TIME_ZONE_CODE = "TIME_ZONE_CODE",
		ADDRESS1 = "1",
		ADDRESS2 = "2",
		CITY = "C",
		STATECODE = "NY",
		ZIP_CODE = "ZIP_CODE",
		ZIP_CODE2 = "ZIP_CODE2",
		COUNTRYCODE = "USA";
	private static final Long
		USER_ID = 1L,
		PROFILE_ID = 4L,
		ADDRESS_ID = 5L,
		TIME_ZONE_ID = 6L;
	private static final BigDecimal
		MAXTRAVELDISTANCE = BigDecimal.ONE,
		MINONSITEHOURLYRATE = BigDecimal.TEN,
		MINONSITEWORKPRICE = BigDecimal.ZERO,
		MINOFFSITEHOURLYRATE = BigDecimal.valueOf(2),
		MINOFFSITEWORKPRICE = BigDecimal.valueOf(3);

	UserAssetAssociation association;
	List<UserAssetAssociation> associations;
	UserLinkAssociation linkAssociation;
	List<UserLinkAssociation> linkAssociations;

	User user;
	Profile profile;
	CallingCode callingCode, callingCode2;
	PostalCode postalCode, postalCode2;
	TimeZone timeZone;
	Address address;
	State state;
	Country country;

	@Before
	public void setup() {
		user = mock(User.class);
		association = mock(UserAssetAssociation.class);
		linkAssociation = mock(UserLinkAssociation.class);

		associations = new ArrayList<>(1);
		linkAssociations = new ArrayList<>(1);
		associations.add(association);
		linkAssociations.add(linkAssociation);

		profile = mock(Profile.class);
		when(profile.getUser()).thenReturn(user);
		postalCode = mock(PostalCode.class);
		postalCode2 = mock(PostalCode.class);
		callingCode = mock(CallingCode.class);
		callingCode2 = mock(CallingCode.class);
		timeZone = mock(TimeZone.class);
		setUpProfileDTO();

		when(user.getId()).thenReturn(USER_ID);
		when(user.getFirstName()).thenReturn(FIRST_NAME);
		when(user.getLastName()).thenReturn(LAST_NAME);
		when(userAssetAssociationDAO.findAllActiveUserAssetsWithAvailabilityByUserAndType(user.getId(), UserAssetAssociationType.PROFILE_VIDEO)).thenReturn(associations);
		when(userLinkAssociationDAO.findUserLinkAssociationsByUserId(user.getId())).thenReturn(linkAssociations);
		when(profileDAO.findByUser(USER_ID)).thenReturn(profile);

		address = mock(Address.class);
		state = mock(State.class);
		country = mock(Country.class);
		when(address.getAddress1()).thenReturn(ADDRESS1);
		when(address.getAddress2()).thenReturn(ADDRESS2);
		when(address.getCity()).thenReturn(CITY);
		when(address.getState()).thenReturn(state);
		when(address.getCountry()).thenReturn(country);
		when(state.getShortName()).thenReturn(STATECODE);
		when(country.getId()).thenReturn(COUNTRYCODE);
		when(addressService.findById(ADDRESS_ID)).thenReturn(address);
	}

	@Test
	public void findAllUserProfileVideoAssociations() {
		List<UserAssetAssociation> assetAssociations = profileService.findAllUserProfileVideoAssociations(user.getId());
		assertEquals(assetAssociations.size(), 1);
	}

	@Test
	public void findAllUserProfileEmbedVideoAssociations() {
		List<UserLinkAssociation> linkAssociations = profileService.findAllUserProfileEmbedVideoAssociations(user.getId());
		assertEquals(linkAssociations.size(), 1);
	}

	@Test
	public void findProfileDTO_copyAllProperties() {
		ProfileDTO profileDTO = profileService.findProfileDTO(USER_ID);

		assertEquals(profileDTO.getJobTitle(), profile.getJobTitle());
		assertEquals(profileDTO.getOverview(), profile.getOverview());
		assertEquals(profileDTO.getPostalCode(), profile.getProfilePostalCode().getPostalCode());
		assertEquals(profileDTO.getMaxTravelDistance(), profile.getMaxTravelDistance());
		assertEquals(profileDTO.getMinOnsiteHourlyRate(), profile.getMinOnsiteHourlyRate());
		assertEquals(profileDTO.getMinOnsiteWorkPrice(), profile.getMinOnsiteWorkPrice());
		assertEquals(profileDTO.getMinOffsiteHourlyRate(), profile.getMinOffsiteHourlyRate());
		assertEquals(profileDTO.getMinOffsiteWorkPrice(), profile.getMinOffsiteWorkPrice());
		assertEquals(profileDTO.getBlacklistedPostalCodes().get(0), Lists.newArrayList(profile.getBlacklistedPostalCodes()).get(0).getPostalCode());
		assertEquals(profileDTO.getWorkPhone(), profile.getWorkPhone());
		assertEquals(profileDTO.getWorkPhoneExtension(), profile.getWorkPhoneExtension());
		assertEquals(profileDTO.getWorkPhoneInternationalCallingCodeId(), profile.getWorkPhoneInternationalCode().getCallingCodeId());
		assertEquals(profileDTO.getMobilePhone(), profile.getMobilePhone());
		assertEquals(profileDTO.getMobilePhoneInternationalCallingCodeId(), profile.getMobilePhoneInternationalCode().getCallingCodeId());
		assertEquals(profileDTO.getAddressId(), profile.getAddressId());
		assertEquals(profileDTO.getAddress1(), address.getAddress1());
		assertEquals(profileDTO.getAddress2(), address.getAddress2());
		assertEquals(profileDTO.getCity(), address.getCity());
		assertEquals(profileDTO.getState(), state.getShortName());
		assertEquals(profileDTO.getCountry(), country.getId());
		assertEquals(profileDTO.getLatitude(), new BigDecimal(postalCode.getLatitude()));
		assertEquals(profileDTO.getLongitude(), new BigDecimal(postalCode.getLongitude()));
	}

	private void setUpProfileDTO() {
		when(postalCode.getPostalCode()).thenReturn(ZIP_CODE);
		when(postalCode2.getPostalCode()).thenReturn(ZIP_CODE2);
		when(callingCode.getCallingCodeId()).thenReturn(WORK_PHONE_INT);
		when(callingCode2.getCallingCodeId()).thenReturn(MOBILE_INT);
		when(timeZone.getId()).thenReturn(TIME_ZONE_ID);
		when(timeZone.getTimeZoneId()).thenReturn(TIME_ZONE_CODE);

		when(profile.getId()).thenReturn(PROFILE_ID);
		when(profile.getAddressId()).thenReturn(ADDRESS_ID);
		when(profile.getJobTitle()).thenReturn(JOB_TITLE);
		when(profile.getOverview()).thenReturn(OVERVIEW);
		when(profile.getProfilePostalCode()).thenReturn(postalCode);
		when(profile.getMaxTravelDistance()).thenReturn(MAXTRAVELDISTANCE);
		when(profile.getMinOnsiteHourlyRate()).thenReturn(MINONSITEHOURLYRATE);
		when(profile.getMinOnsiteWorkPrice()).thenReturn(MINONSITEWORKPRICE);
		when(profile.getMinOffsiteHourlyRate()).thenReturn(MINOFFSITEHOURLYRATE);
		when(profile.getMinOffsiteWorkPrice()).thenReturn(MINOFFSITEWORKPRICE);
		when(profile.getBlacklistedPostalCodes()).thenReturn(Sets.newHashSet(postalCode2));
		when(profile.getWorkPhone()).thenReturn(WORK_PHONE);
		when(profile.getWorkPhoneExtension()).thenReturn(WORK_PHONE_EXT);
		when(profile.isWorkPhoneInternationalCodeSet()).thenReturn(true);
		when(profile.getWorkPhoneInternationalCode()).thenReturn(callingCode);
		when(profile.getMobilePhone()).thenReturn(MOBILE);
		when(profile.isMobilePhoneInternationalCodeSet()).thenReturn(true);
		when(profile.getMobilePhoneInternationalCode()).thenReturn(callingCode2);
		when(profile.getAddressId()).thenReturn(ADDRESS_ID);
		when(profile.getTimeZone()).thenReturn(timeZone);
	}

	@Test
	public void getTimeZoneByUserId_nullProfile() {
		when(profileService.findProfile(USER_ID)).thenReturn(null);
		profileService.getTimeZoneByUserId(USER_ID);
		verify(invariantDataService).findTimeZonesById(anyLong());
	}

	@Test
	public void getTimeZoneByUserId_nullAddress() {
		when(profile.getAddressId()).thenReturn(null);
		profileService.getTimeZoneByUserId(USER_ID);
		verify(profile).getTimeZone();
	}

	@Test
	public void getTimeZoneByUserId_ProfileHasAddress() {
		when(dateTimeService.matchTimeZoneForPostalCode(anyString(), anyString(), anyString(), anyString())).thenReturn(timeZone);
		assertEquals(profileService.getTimeZoneByUserId(USER_ID), timeZone);
		verify(profile, never()).getTimeZone();
	}
}
