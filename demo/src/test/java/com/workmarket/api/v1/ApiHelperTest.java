package com.workmarket.api.v1;

import com.workmarket.api.model.ApiPhoneNumberDTO;
import com.workmarket.domains.model.CallingCode;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.service.business.DateTimeService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.UserService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.business.AuthorizationService;
import com.workmarket.thrift.core.Location;
import com.workmarket.thrift.core.User;
import com.workmarket.thrift.work.Work;
import com.workmarket.utility.DateUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApiHelperTest {
	@Mock Profile profile;
	@Mock CallingCode callingcode;
	@Mock User buyer;
	@Mock Work work;
	@Mock Location location;
	@Mock TimeZone timeZone;
	@Mock TimeZone wmTimeZone;
	@Mock WorkService workService;
	@Mock UserService userService;
	@Mock ProfileService profileService;
	@Mock DateTimeService dateTimeService;
	@Mock AuthorizationService authorizationService;

	@InjectMocks ApiHelper apiHelper = new ApiHelper();

	@Before
	public void setup() {
		when(location.getId()).thenReturn(1L);
		when(location.isSetAddress()).thenReturn(Boolean.FALSE);

		when(buyer.getId()).thenReturn(1L);
		when(work.getBuyer()).thenReturn(buyer);

		when(wmTimeZone.getTimeZoneId()).thenReturn(Constants.WM_TIME_ZONE);
		when(wmTimeZone.getId()).thenReturn(Constants.WM_TIME_ZONE_ID);

		when(profileService.findProfile(anyLong())).thenReturn(profile);
		when(dateTimeService.findTimeZonesById(Constants.WM_TIME_ZONE_ID)).thenReturn(wmTimeZone);

		when(profile.getWorkPhone()).thenReturn("2122341234");
		when(profile.isWorkPhoneInternationalCodeSet()).thenReturn(true);
		when(profile.getWorkPhoneInternationalCode()).thenReturn(callingcode);
		when(profile.getWorkPhoneExtension()).thenReturn("123");

		when(profile.getMobilePhone()).thenReturn("2121234567");
		when(profile.isMobilePhoneInternationalCodeSet()).thenReturn(true);
		when(profile.getMobilePhoneInternationalCode()).thenReturn(callingcode);
	}

	@Test
	public void buildUserProfilePhones_ProfileArgWithWorkCountryCode_ReturnCode() throws Exception {
		when(callingcode.getCallingCodeId()).thenReturn("56");
		List<ApiPhoneNumberDTO> phones =  apiHelper.buildUserProfilePhones(profile);
		assertEquals("56", phones.get(0).getCountryCode());
	}

	@Test
	public void buildUserProfilePhones_ProfileArgWithMobileCountryCode_ReturnCode() throws Exception {
		when(callingcode.getCallingCodeId()).thenReturn("76");
		List<ApiPhoneNumberDTO> phones =  apiHelper.buildUserProfilePhones(profile);
		assertEquals("76", phones.get(1).getCountryCode());
	}

	@Test
	public void parseScheduleForWork_withTZ_Success() throws Exception {
		when(work.getLocation()).thenReturn(null);
		when(work.getBuyer()).thenReturn(buyer);
		Date date = apiHelper.parseScheduleForWork("2013/12/31 8:00 AM CST", work);

		assertEquals(DateUtilities.getDateFromISO8601("2013-12-31T08:00:00-06:00"), date);
	}

	@Test
	public void parseScheduleForWork_fallBackToProfileTZ_Success() throws Exception {
		when(work.getLocation()).thenReturn(null);
		when(profile.getTimeZone()).thenReturn(timeZone);
		when(timeZone.getTimeZoneId()).thenReturn("US/Central");

		Date date = apiHelper.parseScheduleForWork("2013/12/31 8:00 AM", work);
		assertEquals(DateUtilities.getDateFromISO8601("2013-12-31T08:00:00-06:00"), date);
	}

	@Test
	public void parseScheduleForWork_fallBackToWMTZ_Success() throws Exception {
		when(work.getLocation()).thenReturn(null);
		when(profile.getTimeZone()).thenReturn(null);
		Date date = apiHelper.parseScheduleForWork("2013/12/31 8:00 AM", work);
		assertEquals(DateUtilities.getDateFromISO8601("2013-12-31T08:00:00-05:00"), date);
	}

	@Test
	public void parseScheduleForWork_withTZNumeric_Success() throws Exception {
		when(work.getLocation()).thenReturn(null);
		when(work.getBuyer()).thenReturn(buyer);
		Date date = apiHelper.parseScheduleForWork("2013/12/31 8:00 AM GMT-07:00", work);

		assertEquals(DateUtilities.getDateFromISO8601("2013-12-31T08:00:00-07:00"), date);
	}
}
