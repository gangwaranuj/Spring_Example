package com.workmarket.service.business;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.dao.UserAvailabilityDAO;
import com.workmarket.dao.UserDAOImpl;
import com.workmarket.dao.asset.UserAssetAssociationDAO;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.user.NotificationAvailability;
import com.workmarket.domains.model.user.UserAvailability;
import com.workmarket.dao.notification.UserNotificationPreferenceDAO;
import com.workmarket.dao.user.PersonaPreferenceDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.notification.UserNotificationPreference;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.service.business.dto.NotificationPreferenceDTO;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.collections.MapUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

	@Mock UserAssetAssociationDAO userAssetAssociationDAO;
	@Mock UserDAOImpl userDAO;
	@Mock UserAvailabilityDAO userAvailabilityDAO;
	@Mock ProfileService profileService;
	@Mock PersonaPreferenceDAO personaPreferenceDAO;
	@Mock UserNotificationPreferenceDAO userNotificationPreferenceDAO;
	@InjectMocks UserServiceImpl service = spy(new UserServiceImpl());

	private static String
		FIRST_NAME = "John",
		LAST_NAME = "Smith",
		TIME_ZONE_ID = "34254",
		NOTIFICATION_TYPE = "BLAH";
	private static Long
		userId = 999999L;

	List<Long> userIds = Lists.newArrayList(userId);
	List<UserAvailability> userAvailability = Lists.newArrayList();
	ImmutableList<String> userNumbers = ImmutableList.of("4134134", "1940150");
	UserAssetAssociation backgroundImage;
	Asset asset;
	Profile profile;
	TimeZone timeZone;
	PersonaPreference personaPreferenceDispatcher;
	PersonaPreference personaPreferenceNotDispatcher;
	User user;
	UserNotificationPreference userNotificationPreference;
	NotificationPreferenceDTO notificationPreferenceDTO;

	@Before
	public void setup() {
		backgroundImage = mock(UserAssetAssociation.class);
		asset = mock(Asset.class);
		Map<String, Object> map = ImmutableMap.<String, Object>of("firstName", FIRST_NAME, "lastName", LAST_NAME);

		profile = mock(Profile.class);
		timeZone = mock(TimeZone.class);

		Map<Long, Map<String, Object>> map2 = Maps.newHashMap();

		map2.put(userId, map);

		when(backgroundImage.getAsset()).thenReturn(asset);
		when(userDAO.getProjectionMapById(userId, "firstName", "lastName")).thenReturn(map);
		when(userDAO.getProjectionMapByIds(userIds, "firstName", "lastName")).thenReturn(map2);
		when(profileService.findProfile(userId)).thenReturn(profile);
		when(profile.getTimeZone()).thenReturn(timeZone);
		when(timeZone.getTimeZoneId()).thenReturn(TIME_ZONE_ID);

		personaPreferenceDispatcher = mock(PersonaPreference.class);
		when(personaPreferenceDispatcher.isDispatcher()).thenReturn(true);

		personaPreferenceNotDispatcher = mock(PersonaPreference.class);
		when(personaPreferenceNotDispatcher.isDispatcher()).thenReturn(false);

		when(personaPreferenceDAO.get(anyLong())).thenReturn(personaPreferenceDispatcher);

		user = mock(User.class);
		when(user.getId()).thenReturn(userId);
		when(userDAO.get(anyLong())).thenReturn(user);
		when(userDAO.getUser(anyLong())).thenReturn(user);

		userNotificationPreference = mock(UserNotificationPreference.class);

		notificationPreferenceDTO = mock(NotificationPreferenceDTO.class);
		when(notificationPreferenceDTO.getNotificationTypeCode()).thenReturn(NOTIFICATION_TYPE);

		when(userNotificationPreferenceDAO.findByUserAndNotificationType(anyLong(), anyString())).thenReturn(userNotificationPreference);
	}

	@Test
	public void findUserBackgroundImage_WithNullUserId_Explodes() throws Exception {
		try {
			service.findUserBackgroundImage(null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
	}

	@Test
	public void findUserBackgroundImage_WithUserId_FindsABackgroundImage() throws Exception {
		service.findUserBackgroundImage(userId);
		verify(userAssetAssociationDAO).findBackgroundImage(userId);
	}

	@Test
	public void findUserBackgroundImage_WhenNoImageFound_DoesntGetAnAsset() throws Exception {
		when(userAssetAssociationDAO.findBackgroundImage(userId)).thenReturn(null);
		service.findUserBackgroundImage(userId);
		verify(backgroundImage, never()).getAsset();
	}

	@Test
	public void findUserBackgroundImage_WhenNoImageFound_ReturnsNull() throws Exception {
		when(userAssetAssociationDAO.findBackgroundImage(userId)).thenReturn(null);
		Asset image = service.findUserBackgroundImage(userId);
		assertNull(image);
	}

	@Test
	public void findUserBackgroundImage_WhenImageFound_GetsItsAsset() throws Exception {
		when(userAssetAssociationDAO.findBackgroundImage(userId)).thenReturn(backgroundImage);
		service.findUserBackgroundImage(userId);
		verify(backgroundImage).getAsset();
	}

	@Test
	public void findUserBackgroundImage_WhenNoImageFound_ReturnsTheAsset() throws Exception {
		when(userAssetAssociationDAO.findBackgroundImage(userId)).thenReturn(backgroundImage);
		Asset image = service.findUserBackgroundImage(userId);
		assertEquals(image, asset);
	}

	@Test
	public void findAllUserIdsByUserNumbers_returnIds() throws Exception {
		service.findAllUserIdsByUserNumbers(userNumbers);
		verify(userDAO).findAllUserIdsByUserNumbers(userNumbers);
	}

	@Test
	public void getFullName_nullId_returnEmptyString() {
		String name = service.getFullName(null);
		assertEquals(name, "");
	}

	@Test
	public void getFullName_validId_returnFullName() {
		String name = service.getFullName(userId);
		assertEquals(StringUtilities.fullName(FIRST_NAME, LAST_NAME), name);
	}

	@Test
	public void getFullName_unknownId_returnEmptyString() {
		String name = service.getFullName(1L);
		assertEquals(name, "");
	}

	@Test
	public void getFullNames_nullId_returnEmptyMap() {
		Map<Long, String> result = service.getFullNames(null);
		assertTrue(MapUtils.isEmpty(result));
	}

	@Test
	public void getFullNames_validIds_returnFullName() {
		Map<Long, String> result = service.getFullNames(userIds);
		assertEquals(result.get(userId), StringUtilities.fullName(FIRST_NAME, LAST_NAME));
	}

	@Test
	public void getFullNames_invalidIds_returnEmptyMap() {
		Map<Long, String> result = service.getFullNames(Lists.newArrayList(1L));
		assertTrue(MapUtils.isEmpty(result));
	}

	@Test
	public void findWeeklyNotificationHours_noConfiguredHours_onlyUseDefaultAvailability() {
		List<UserAvailability> availabilities = service.findWeeklyNotificationHours(userId);

		assertEquals(7, availabilities.size());
		for (UserAvailability availability : availabilities) {
			assertFalse(availability.isAllDayAvailable());
		}
	}

	@Test
	public void findWeeklyNotificationHours_withTuesdayConfiguredHours_doNotUserDefaultAvailabilityForTuesday() {
		userAvailability.add(createMockConfiguredAvailability(UserAvailability.TUESDAY));
		when(userAvailabilityDAO.findWeeklyNotificationHours(userId)).thenReturn(userAvailability);

		List<UserAvailability> availabilities = service.findWeeklyNotificationHours(userId);

		assertEquals(7, availabilities.size());
		for (UserAvailability availability : availabilities) {
			if (UserAvailability.TUESDAY == availability.getWeekDay()) {
				assertTrue(availability.isAllDayAvailable());
			}
		}
	}

	@Test
	public void findWeeklyNotificationHours_withAllConfiguredHours_doNotUseDefaultAvailability() {
		for (int i = 0; i < 7; i++) {
			userAvailability.add(createMockConfiguredAvailability(i));
		}
		when(userAvailabilityDAO.findWeeklyNotificationHours(userId)).thenReturn(userAvailability);

		List<UserAvailability> availabilities = service.findWeeklyNotificationHours(userId);

		assertEquals(7, availabilities.size());
		for (UserAvailability availability : availabilities) {
			assertTrue(availability.isAllDayAvailable());
		}
	}

	@Test
	public void findWeeklyWorkingHours_noConfiguredHours_onlyUseDefaultAvailability() {
		List<UserAvailability> availabilities = service.findWeeklyWorkingHours(userId);

		assertEquals(7, availabilities.size());
		for (UserAvailability availability : availabilities) {
			assertFalse(availability.isAllDayAvailable());
		}
	}

	@Test
	public void findWeeklyWorkingHours_withTuesdayConfiguredHours_doNotUserDefaultAvailabilityForTuesday() {
		userAvailability.add(createMockConfiguredAvailability(UserAvailability.TUESDAY));
		when(userAvailabilityDAO.findWeeklyWorkingHours(userId)).thenReturn(userAvailability);

		List<UserAvailability> availabilities = service.findWeeklyWorkingHours(userId);

		assertEquals(7, availabilities.size());
		for (UserAvailability availability : availabilities) {
			if (UserAvailability.TUESDAY == availability.getWeekDay()) {
				assertTrue(availability.isAllDayAvailable());
			}
		}
	}

	@Test
	public void findWeeklyWorkingHours_withAllConfiguredHours_doNotUseDefaultAvailability() {
		for (int i = 0; i < 7; i++) {
			userAvailability.add(createMockConfiguredAvailability(i));
		}
		when(userAvailabilityDAO.findWeeklyWorkingHours(userId)).thenReturn(userAvailability);

		List<UserAvailability> availabilities = service.findWeeklyWorkingHours(userId);

		assertEquals(7, availabilities.size());
		for (UserAvailability availability : availabilities) {
			assertTrue(availability.isAllDayAvailable());
		}
	}

	public UserAvailability createMockConfiguredAvailability(Integer DAY_OF_WEEK) {
		NotificationAvailability availability = mock(NotificationAvailability.class);
		when(availability.getWeekDay()).thenReturn(DAY_OF_WEEK);
		when(availability.isAllDayAvailable()).thenReturn(true); // The default availability sets allDayAvailable to false
		return availability;
	}
}
