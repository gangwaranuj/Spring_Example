package com.workmarket.service.business;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.schema.Education;
import com.google.code.linkedinapi.schema.Educations;
import com.google.code.linkedinapi.schema.Person;
import com.google.code.linkedinapi.schema.PhoneNumber;
import com.google.code.linkedinapi.schema.PhoneNumbers;
import com.google.code.linkedinapi.schema.Position;
import com.google.code.linkedinapi.schema.Positions;
import com.workmarket.dao.linkedin.LinkedInDAO;
import com.workmarket.dao.linkedin.LinkedInDAO.LinkedInRestriction;
import com.workmarket.dao.oauth.OAuthTokenDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.linkedin.LinkedInPerson;
import com.workmarket.domains.model.oauth.OAuthToken;
import com.workmarket.domains.model.oauth.OAuthTokenProviderType;
import com.workmarket.service.business.LinkedInServiceImpl.LinkedInImportFailed;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.anySetOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: micah
 * Date: 2/26/13
 * Time: 4:08 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class LinkedInServiceTest {
	@Mock LinkedInOAuthService linkedInOAuthService;
	@Mock LinkedInApiClientFactory linkedInApiClientFactory;
	@Mock LinkedInDAO linkedInDAO;
	@Mock UserService userService;
	@Mock ProfileService profileService;
	@Mock OAuthTokenDAO oAuthTokenDAO;
	@Mock MetricRegistry registry;
	@InjectMocks LinkedInServiceImpl linkedInService;

	LinkedInApiClient client;

	OAuthToken oAuthToken;
	Person person;
	User user;
	LinkedInPerson linkedInPerson;

	@Before
	public void setup() {
		final Meter mockMeter = mock(Meter.class);
		when(registry.meter((String) anyObject())).thenReturn(mockMeter);
		linkedInService.init();
		user = new User();
		user.setEmail("me@me.com");
		oAuthToken = new OAuthToken();
		oAuthToken.setRequestToken("1234");
		oAuthToken.setUser(user);

		linkedInPerson = new LinkedInPerson();
		linkedInPerson.setUser(user);

		client = mock(LinkedInApiClient.class);

		person = mock(Person.class);
		Educations educations = mock(Educations.class);
		List<Education> educationList = new ArrayList<Education>();

		Positions positions = mock(Positions.class);
		List<Position> positionsList = new ArrayList<Position>();

		PhoneNumbers phoneNumbers = mock(PhoneNumbers.class);
		List<PhoneNumber> phoneNumbersList = new ArrayList<PhoneNumber>();

		when(person.getEducations()).thenReturn(educations);
		when(educations.getEducationList()).thenReturn(educationList);

		when(person.getPositions()).thenReturn(positions);
		when(positions.getPositionList()).thenReturn(positionsList);

		when(person.getPhoneNumbers()).thenReturn(phoneNumbers);
		when(phoneNumbers.getPhoneNumberList()).thenReturn(phoneNumbersList);
	}
	
	@Test
	public void findUsernameByOAuth_TokenExistsUserExistsNoPhones_ReturnsEmail()
			throws LinkedInImportFailed, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Person person = mock(Person.class);

		Educations educations = mock(Educations.class);
		List<Education> educationList = new ArrayList<Education>();
		educationList.add(mock(Education.class));

		when(person.getEducations()).thenReturn(educations);
		when(educations.getEducationList()).thenReturn(educationList);

		Positions positions = mock(Positions.class);
		List<Position> positionsList = new ArrayList<Position>();
		positionsList.add(mock(Position.class));

		when(person.getPositions()).thenReturn(positions);
		when(positions.getPositionList()).thenReturn(positionsList);

		LinkedInApiClient client = mock(LinkedInApiClient.class);

		when(client.getProfileForCurrentUser(any(Set.class))).thenReturn(person);

		Method method = LinkedInServiceImpl.class.getDeclaredMethod("importPersonData", Long.class, LinkedInApiClient.class);
		method.setAccessible(true);
		LinkedInPerson p = (LinkedInPerson) method.invoke(linkedInService, 1L, client);

		assertTrue(p.getLinkedInEducation().size() == 1);
		assertTrue(p.getLinkedInPositions().size() == 1);
		assertTrue(p.getLinkedInPhoneNumbers().size() == 0);
	}

	@Test
	public void attempToLinkUser_WithDummyParams_Fails() {
		when(
			oAuthTokenDAO.findBySessionIdAndProvider(
				"", OAuthTokenProviderType.LINKEDIN
			)
		).thenReturn(null);

		Boolean success = linkedInService.attemptToLinkUser("", 1L);

		assertFalse(success);
	}

	@Test
	public void attemptToLinkUser_WithDummyParams_Succeeds() {
		OAuthToken oAuthToken  = new OAuthToken();
		oAuthToken.setAccessToken("");
		oAuthToken.setAccessTokenSecret("");
		Person person = mock(Person.class);
		LinkedInPerson linkedInPerson = mock(LinkedInPerson.class);
		User user = mock(User.class);

		when(
			oAuthTokenDAO.findBySessionIdAndProvider(
				"session", OAuthTokenProviderType.LINKEDIN
			)
		).thenReturn(oAuthToken);

		when(
			linkedInApiClientFactory.
				createLinkedInApiClient(any(LinkedInAccessToken.class))
		).thenReturn(client);

		when(
			client.getProfileForCurrentUser(EnumSet.of(ProfileField.ID))
		).thenReturn(person);

		when(person.getId()).thenReturn("abcdefg");

		when(linkedInDAO.findMostRecentLinkedInPersonByLinkedInId(
			"abcdefg", LinkedInRestriction.WITHOUT_USER
		)).thenReturn(linkedInPerson);

		when(userService.getUser(1234L)).thenReturn(user);

		Boolean success = linkedInService.attemptToLinkUser("session", 1234L);

		assertTrue(success);
	}

	@Test
	public void findUsernameByOAuth_TokenDoesntExist_Error() {
		when(oAuthTokenDAO.findBy("requestToken","1234")).thenReturn(null);

		LinkedInResult linkedInResult = linkedInService.findUsernameByOAuth("1234");
		assertSame(LinkedInResult.Status.ERROR, linkedInResult.getStatus());
	}

	@Test
	public void findUsernameByOAuth_TokenExistsUserExists_ReturnsEmail() {
		when(oAuthTokenDAO.findBy("requestToken", "1234")).thenReturn(oAuthToken);

		LinkedInResult linkedInResult = linkedInService.findUsernameByOAuth("1234");

		assertEquals("me@me.com", linkedInResult.getUserEmail());
	}

	@Test
	public void findUsernameByOAuth_UserDoesntExistLinkedInFails_Error() {
		oAuthToken.setUser(null);
		when(oAuthTokenDAO.findBy("requestToken","1234")).thenReturn(oAuthToken);

		when(client.getProfileForCurrentUser(
			EnumSet.of(ProfileField.ID, ProfileField.EMAIL_ADDRESS)
		)).thenReturn(null);

		LinkedInResult linkedInResult = linkedInService.findUsernameByOAuth("1234");
		assertEquals(linkedInResult.getStatus(), LinkedInResult.Status.ERROR);
	}

	@Test
	public void findUsernameByOAuth_UserDoesntExistLinkedInPersonNull_Error() {
		oAuthToken.setUser(null);
		when(oAuthTokenDAO.findBy("requestToken","1234")).thenReturn(oAuthToken);

		when(linkedInApiClientFactory.
			createLinkedInApiClient(any(LinkedInAccessToken.class))
		).thenReturn(client);

		when(client.getProfileForCurrentUser(
			EnumSet.of(ProfileField.ID, ProfileField.EMAIL_ADDRESS)
		)).thenReturn(null);

		LinkedInResult linkedInResult = linkedInService.findUsernameByOAuth("1234");
		assertEquals(linkedInResult.getStatus(), LinkedInResult.Status.ERROR);
	}

	@Test
	public void
		findUsernameByOAuth_UserDoesntExistLinkedInPersonExists_ReturnsEmail() {
		oAuthToken.setUser(null);
		when(oAuthTokenDAO.findBy("requestToken","1234")).thenReturn(oAuthToken);

		when(linkedInApiClientFactory.
			createLinkedInApiClient(any(LinkedInAccessToken.class))
		).thenReturn(client);

		when(client.getProfileForCurrentUser(
			EnumSet.of(ProfileField.ID, ProfileField.EMAIL_ADDRESS)
		)).thenReturn(person);

		when(person.getEmailAddress()).thenReturn("me@me.com");

		when(userService.findUserByEmail("me@me.com")).thenReturn(user);

		LinkedInResult linkedInResult = linkedInService.findUsernameByOAuth("1234");

		assertEquals("me@me.com", linkedInResult.getUserEmail());
	}

	@Test
	public void
	  findUsernameByOAuth_UserDoesntExistLinkedInPersonExists_NoEmail()
	{
		oAuthToken.setUser(null);
		when(oAuthTokenDAO.findBy("requestToken","1234")).thenReturn(oAuthToken);

		when(linkedInApiClientFactory.
			createLinkedInApiClient(any(LinkedInAccessToken.class))
		).thenReturn(client);

		when(client.getProfileForCurrentUser(
			EnumSet.of(ProfileField.ID, ProfileField.EMAIL_ADDRESS)
		)).thenReturn(person);

		when(person.getEmailAddress()).thenReturn("me@me.com");
		when(person.getId()).thenReturn("uvwxyz");

		when(userService.findUserByEmail("me@me.com")).thenReturn(null);

		when(linkedInDAO.
			findMostRecentLinkedInPersonByLinkedInId("uvwxyz", LinkedInRestriction.WITH_USER)
		).thenReturn(linkedInPerson);

		LinkedInResult linkedInResult = linkedInService.findUsernameByOAuth("1234");

		assertEquals("me@me.com", linkedInResult.getUserEmail());
	}

	@Test
	public void
		findUsernameByOAuth_UserDoesntExistLinkedInPersonExists_Failure()
	{
		oAuthToken.setUser(null);
		when(oAuthTokenDAO.findBy("requestToken","1234")).thenReturn(oAuthToken);

		when(linkedInApiClientFactory.
				createLinkedInApiClient(any(LinkedInAccessToken.class))
		).thenReturn(client);

		when(client.getProfileForCurrentUser(anySetOf(ProfileField.class))).thenReturn(person);

		when(person.getEmailAddress()).thenReturn("you@me.com");
		when(person.getId()).thenReturn("uvwxyz");

		when(userService.findUserByEmail("you@me.com")).thenReturn(null);

		when(linkedInDAO.
				findMostRecentLinkedInPersonByLinkedInId("uvwxyz", LinkedInRestriction.WITH_USER)
		).thenReturn(null);

		LinkedInResult linkedInResult = linkedInService.findUsernameByOAuth("1234");

		assertEquals(LinkedInResult.Status.FAILURE, linkedInResult.getStatus());
		assertEquals("you@me.com", linkedInResult.getLinkedInEmail());
	}
}