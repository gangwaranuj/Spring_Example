package com.workmarket.domains.onboarding.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OnboardProfilePropertyMapCreatorTest {
	WorkerOnboardingDTO dto;
	OnboardProfilePropertyMapCreator creator;

	@Before
	public void setUp() throws Exception {
		dto = mock(WorkerOnboardingDTO.class);

		creator = new OnboardProfilePropertyMapCreator(dto);
	}

	@Test
	public void itShouldReturnUserPropertiesWhenNotEmpty() {
		when(dto.getFirstName()).thenReturn("Emeka");
		when(dto.getLastName()).thenReturn("Ganymede");
		when(dto.getSecondaryEmail()).thenReturn("secondary+email@worker.com");

		final OnboardProfilePropertyMap map = creator.userPropertiesMap();

		assertEquals("Emeka", map.get("firstName"));
		assertEquals("Ganymede", map.get("lastName"));
		assertEquals("secondary+email@worker.com", map.get("secondaryEmail"));
	}

	@Test
	public void itShouldNotReturnUserPropertiesWhenEmpty() {
		when(dto.getFirstName()).thenReturn("");
		when(dto.getLastName()).thenReturn("");
		when(dto.getSecondaryEmail()).thenReturn("");

		final OnboardProfilePropertyMap map = creator.userPropertiesMap();

		assertFalse(map.containsKey("firstName"));
		assertFalse(map.containsKey("lastName"));
		assertFalse(map.containsKey("secondaryEmail"));
	}

	@Test
	public void itShouldReturnNullMobilePhoneTypeIfPhonesNull() {
		OnboardProfilePropertyMap map = creator.profilePropertiesMap(true);
		assertNull(map.get(OnboardProfilePropertyMap.MOBILE_PHONE));
	}

	@Test
	public void itShouldStillReturnNullMobilePhoneTypeIfPhonesNull() {
		OnboardProfilePropertyMap map = creator.profilePropertiesMap(false);
		assertNull(map.get(OnboardProfilePropertyMap.MOBILE_PHONE));
	}

	@Test
	public void itShouldReturnEmptyMobilePhoneInfoIfPhonesEmpty() {
		dto.setPhones(new ArrayList<PhoneInfoDTO>());
		OnboardProfilePropertyMap map = creator.profilePropertiesMap(true);
		assertNotNull(creator.getPhoneInfoByType("mobile", true));
	}

	@Test
	public void itShouldNotReturnEmptyMobilePhoneInfoIfPhonesEmpty() {
		dto.setPhones(new ArrayList<PhoneInfoDTO>());
		OnboardProfilePropertyMap map = creator.profilePropertiesMap(false);
		assertNull(creator.getPhoneInfoByType("mobile", false));
	}

	@Test
	public void itShouldReturnNullSmsPhoneTypeIfPhonesNull() {
		OnboardProfilePropertyMap map = creator.profilePropertiesMap(true);
		assertNull(map.get(OnboardProfilePropertyMap.SMS_PHONE));
	}

	@Test
	public void itShouldStillReturnNullSmsPhoneTypeIfPhonesNull() {
		OnboardProfilePropertyMap map = creator.profilePropertiesMap(false);
		assertNull(map.get(OnboardProfilePropertyMap.SMS_PHONE));
	}

	@Test
	public void itShouldReturnEmptySmsPhoneInfoIfPhonesEmpty() {
		dto.setPhones(new ArrayList<PhoneInfoDTO>());
		OnboardProfilePropertyMap map = creator.profilePropertiesMap(true);
		assertNotNull(creator.getPhoneInfoByType("sms", true));
	}

	@Test
	public void itShouldNotReturnEmptySmsPhoneInfoIfPhonesEmpty() {
		dto.setPhones(new ArrayList<PhoneInfoDTO>());
		OnboardProfilePropertyMap map = creator.profilePropertiesMap(false);
		assertNull(creator.getPhoneInfoByType("sms", false));
	}

	@Test
	public void itShouldReturnNullWorkPhoneTypeIfPhonesNull() {
		OnboardProfilePropertyMap map = creator.profilePropertiesMap(true);
		assertNull(map.get(OnboardProfilePropertyMap.WORK_PHONE));
	}

	@Test
	public void itShouldStillReturnNullWorkPhoneTypeIfPhonesNull() {
		OnboardProfilePropertyMap map = creator.profilePropertiesMap(false);
		assertNull(map.get(OnboardProfilePropertyMap.WORK_PHONE));
	}

	@Test
	public void itShouldReturnEmptyWorkPhoneInfoIfPhonesEmpty() {
		dto.setPhones(new ArrayList<PhoneInfoDTO>());
		OnboardProfilePropertyMap map = creator.profilePropertiesMap(true);
		assertNotNull(creator.getPhoneInfoByType("work", true));
	}

	@Test
	public void itShouldNotReturnEmptyWorkPhoneInfoIfPhonesEmpty() {
		dto.setPhones(new ArrayList<PhoneInfoDTO>());
		OnboardProfilePropertyMap map = creator.profilePropertiesMap(false);
		assertNull(creator.getPhoneInfoByType("work", false));
	}
}
