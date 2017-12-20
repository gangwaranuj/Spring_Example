package com.workmarket.service.business;

import com.codahale.metrics.MetricRegistry;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.google.code.linkedinapi.schema.Education;
import com.google.code.linkedinapi.schema.Person;
import com.google.code.linkedinapi.schema.PhoneNumber;
import com.google.code.linkedinapi.schema.Position;
import com.workmarket.common.metric.MetricRegistryFacade;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.dao.linkedin.LinkedInDAO;
import com.workmarket.dao.linkedin.LinkedInDAO.LinkedInRestriction;
import com.workmarket.dao.oauth.OAuthTokenDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.linkedin.LinkedInEducation;
import com.workmarket.domains.model.linkedin.LinkedInPerson;
import com.workmarket.domains.model.linkedin.LinkedInPhoneNumber;
import com.workmarket.domains.model.linkedin.LinkedInPosition;
import com.workmarket.domains.model.oauth.OAuthToken;
import com.workmarket.domains.model.oauth.OAuthTokenProviderType;
import com.workmarket.utility.BeanUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class LinkedInServiceImpl implements LinkedInService {
	
	@Autowired private LinkedInOAuthService linkedInOAuthService;
	@Autowired private LinkedInApiClientFactory linkedInApiClientFactory;
	@Autowired private LinkedInDAO linkedInDAO;
	@Autowired private UserService userService;
	@Autowired private ProfileService profileService;
	@Autowired private OAuthTokenDAO oAuthTokenDAO;
	@Autowired private MetricRegistry registry;
	private MetricRegistryFacade facade;

	@PostConstruct
	public void init() {
		facade = new WMMetricRegistryFacade(registry, "auth.linkedin");
	}

	@Override
	public boolean authorize(LinkedInRequestToken requestToken, String sessionId, String oAuthVerifier) {
		facade.meter("authorize").mark();
		LinkedInAccessToken accessToken = linkedInOAuthService.getOAuthAccessToken(requestToken, oAuthVerifier);

		if (accessToken == null) return false;

		OAuthToken oAuthToken = oAuthTokenDAO.findByAccessToken(accessToken.getToken());
		if (oAuthToken == null) {
			oAuthToken = new OAuthToken();
			oAuthToken.setAccessToken(accessToken.getToken());
			oAuthToken.setAccessTokenSecret(accessToken.getTokenSecret());
			oAuthToken.setProviderType(new OAuthTokenProviderType(OAuthTokenProviderType.LINKEDIN));
		}

		oAuthToken.setRequestToken(requestToken.getToken());
		oAuthToken.setRequestTokenSecret(requestToken.getTokenSecret());
		oAuthToken.setSessionId(sessionId);

		oAuthTokenDAO.saveOrUpdate(oAuthToken);
		return true;
	}

	@Override
	@Transactional(readOnly = false)
	public LinkedInPerson importPersonData(Long userId) throws LinkedInImportFailed {
		facade.meter("import").mark();
		try {
			return importPersonData(userId, newLinkedInClient(userId));
		} catch (Exception x) {
			throw new LinkedInImportFailed("LinkedIn import failed", x);
		}
	}

	@Override
	public LinkedInPerson importPersonDataForAnonymous(String sessionId) throws LinkedInImportFailed {
		facade.meter("importanonymous").mark();
		try {
			return importPersonDataForAnonymous(newLinkedInClientForAnonymous(sessionId));
		} catch (Exception x) {
			throw new LinkedInImportFailed("LinkedIn import failed", x);
		}
	}

	@Override
	public boolean attemptToLinkUserById(String linkedInId, Long userId) {
		facade.meter("attemptlinkbyid").mark();
		LinkedInPerson linkedInPerson =
			linkedInDAO.findMostRecentLinkedInPersonByLinkedInId(
				linkedInId, LinkedInRestriction.WITHOUT_USER
			);

		if (linkedInPerson != null) {
			User user = userService.getUser(userId);
			linkedInPerson.setUser(user);
			linkedInDAO.saveOrUpdate(linkedInPerson);
			return (user != null);
		}
		return false;
	}

	@Override
	public boolean attemptToLinkUser(String sessionId, Long userId) {
		facade.meter("attemptlinkbyuser").mark();
		OAuthToken oauthToken =
			oAuthTokenDAO.findBySessionIdAndProvider(
				sessionId, OAuthTokenProviderType.LINKEDIN
			);
		if (oauthToken == null) return false;

		LinkedInAccessToken accessToken =
			new LinkedInAccessToken(
				oauthToken.getAccessToken(), oauthToken.getAccessTokenSecret()
			);
		LinkedInApiClient client =
			linkedInApiClientFactory.createLinkedInApiClient(accessToken);
		if (client == null) return false;

		Person person =
			client.getProfileForCurrentUser(EnumSet.of(ProfileField.ID));
		if (person == null) return false;

		LinkedInPerson linkedInPerson =
			linkedInDAO.findMostRecentLinkedInPersonByLinkedInId(
				person.getId(), LinkedInRestriction.WITHOUT_USER
			);
		if (linkedInPerson == null) return false;

		User user = userService.getUser(userId);
		if (user == null) return false;

		oauthToken.setUser(user);
		oAuthTokenDAO.saveOrUpdate(oauthToken);

		linkedInPerson.setUser(user);
		linkedInDAO.saveOrUpdate(linkedInPerson);

		return true;
	}

	@Override
	public LinkedInResult findUsernameByOAuth(String requestToken) {
		facade.meter("findusernamebyoauth").mark();
		LinkedInResult linkedInResult = new LinkedInResult();

		OAuthToken oAuthToken = oAuthTokenDAO.findBy("requestToken", requestToken);
		if (oAuthToken == null) {
			linkedInResult.setStatus(LinkedInResult.Status.ERROR);
			linkedInResult.setMessage(
				"request token: " + requestToken + " not found."
			);
			return linkedInResult;
		}

		User user = oAuthToken.getUser();
		if (user != null) {
			linkedInResult.setStatus(LinkedInResult.Status.SUCCESS);
			linkedInResult.setUserEmail(user.getEmail());
			return linkedInResult;
		}

		LinkedInAccessToken accessToken =
			new LinkedInAccessToken(
				oAuthToken.getAccessToken(), oAuthToken.getAccessTokenSecret()
			);

		LinkedInApiClient client =
			linkedInApiClientFactory.createLinkedInApiClient(accessToken);
		if (client == null) {
			linkedInResult.setStatus(LinkedInResult.Status.ERROR);
			linkedInResult.setMessage("Unable to get LinkedIn client.");
			return linkedInResult;
		}

		Person person = client.getProfileForCurrentUser(
			EnumSet.of(ProfileField.ID, ProfileField.EMAIL_ADDRESS)
		);

		if (person == null) {
			linkedInResult.setStatus(LinkedInResult.Status.ERROR);
			linkedInResult.setMessage("Couldn't get LinkedIn Profile info.");
			return linkedInResult;
		}
		linkedInResult.setLinkedInEmail(person.getEmailAddress());
		linkedInResult.setLinkedInId(person.getId());

		user = userService.findUserByEmail(person.getEmailAddress());
		if (user != null) {
			oAuthToken.setUser(user);
			oAuthTokenDAO.saveOrUpdate(oAuthToken);
			linkedInResult.setStatus(LinkedInResult.Status.SUCCESS);
			linkedInResult.setUserEmail(user.getEmail());
			return linkedInResult;
		}

		LinkedInPerson linkedInPerson =
			linkedInDAO.findMostRecentLinkedInPersonByLinkedInId(
				person.getId(), LinkedInRestriction.WITH_USER
			);

		if (linkedInPerson != null) {
			// If permissions grant was taken away or access token expired,
			// delete old record.
			OAuthToken oldOAuthToken =
				oAuthTokenDAO.findBy("user", linkedInPerson.getUser());
			if (oldOAuthToken != null) {
				oAuthTokenDAO.delete(oldOAuthToken);
			}

			oAuthToken.setUser(linkedInPerson.getUser());
			oAuthTokenDAO.saveOrUpdate(oAuthToken);
			linkedInResult.setStatus(LinkedInResult.Status.SUCCESS);
			linkedInResult.setUserEmail(linkedInPerson.getUser().getEmail());
			return linkedInResult;
		}

		try {
			List<LinkedInPerson> linkedInPeople =
				linkedInDAO.findAllBy("linkedInId", person.getId());
			if (CollectionUtils.isEmpty(linkedInPeople))
				importPersonDataForAnonymous(client);
			linkedInResult.setStatus(LinkedInResult.Status.FAILURE);
		} catch (Exception e) {
			linkedInResult.setStatus(LinkedInResult.Status.ERROR);
			linkedInResult.setMessage("Failed to import LinkedIn Data");
		}

		return linkedInResult;
	}

	@Override
	public LinkedInPerson findMostRecentLinkedInPerson(Long userId) {
		facade.meter("findmostrecent").mark();
		return linkedInDAO.findMostRecentLinkedInPerson(userId);
	}

	private LinkedInPerson importPersonDataForAnonymous(LinkedInApiClient client) throws Exception {
		return importPersonData(null, client);
	}

	private LinkedInPerson importPersonData(Long userId, LinkedInApiClient client) throws Exception {
		Person person = client.getProfileForCurrentUser(EnumSet.of(
				ProfileField.ID,
				ProfileField.FIRST_NAME,
				ProfileField.LAST_NAME,
				ProfileField.EMAIL_ADDRESS,
				ProfileField.HEADLINE,
				ProfileField.LOCATION,
				ProfileField.LOCATION_COUNTRY,
				ProfileField.LOCATION_NAME,
				ProfileField.INDUSTRY,
				ProfileField.NUM_CONNECTIONS,
				ProfileField.NUM_CONNECTIONS_CAPPED,
				ProfileField.NUM_RECOMMENDERS,
				ProfileField.SUMMARY,
				ProfileField.PUBLIC_PROFILE_URL,
				ProfileField.INTERESTS,
				ProfileField.ASSOCIATIONS,
				ProfileField.HONORS,
				ProfileField.SPECIALTIES,
				ProfileField.MAIN_ADDRESS,
				ProfileField.PHONE_NUMBERS,
				ProfileField.PICTURE_URL,
				ProfileField.THREE_CURRENT_POSITIONS,
				ProfileField.THREE_PAST_POSITIONS,
				ProfileField.POSITIONS,
				ProfileField.EDUCATIONS,
				ProfileField.HEADLINE,
				ProfileField.RECOMMENDATIONS_RECEIVED
		));

		LinkedInPerson linkedInPerson = new LinkedInPerson();

		if (userId != null) {
			User user = userService.getUser(userId);
			linkedInPerson.setUser(user);
		}

		String[] ignore = new String[]{"Location"};
		BeanUtilities.copyProperties(linkedInPerson, person, ignore);
		linkedInPerson.setLinkedInId(person.getId());
		linkedInPerson.setLocation(person.getLocation());

		linkedInDAO.saveOrUpdateLinkedInPerson(linkedInPerson);

		if (person.getEducations() != null && person.getEducations().getEducationList() != null) {
			for (Education education : person.getEducations().getEducationList()) {
				ignore = new String[]{"StartDate", "EndDate"};
				LinkedInEducation linkedInEducation = new LinkedInEducation();
				linkedInEducation.setLinkedInPerson(linkedInPerson);
				BeanUtilities.copyProperties(linkedInEducation, education, ignore);
				linkedInEducation.setStartDate(education.getStartDate());
				linkedInEducation.setEndDate(education.getEndDate());
				linkedInEducation.setId(null);

				linkedInPerson.getLinkedInEducation().add(linkedInEducation);
				linkedInDAO.saveOrUpdateLinkedInEducation(linkedInEducation);
			}
		}

		if (person.getPositions() != null && person.getPositions().getPositionList() != null) {
			for (Position position : person.getPositions().getPositionList()) {
				ignore = new String[]{"StartDate", "EndDate", "Company"};
				LinkedInPosition linkedInPosition = new LinkedInPosition();
				linkedInPosition.setLinkedInPerson(linkedInPerson);
				linkedInPosition.setCurrent(position.isIsCurrent()); // great accessor name :)
				BeanUtilities.copyProperties(linkedInPosition, position, ignore);
				linkedInPosition.setStartDate(position.getStartDate());
				linkedInPosition.setEndDate(position.getEndDate());
				linkedInPosition.setCompany(position.getCompany());
				linkedInPosition.setId(null);

				linkedInPerson.getLinkedInPositions().add(linkedInPosition);
				linkedInDAO.saveOrUpdateLinkedInPosition(linkedInPosition);
			}
		}

		if (person.getPhoneNumbers() != null && person.getPhoneNumbers().getPhoneNumberList() != null) {
			HashSet<LinkedInPhoneNumber> numbers = new HashSet<>();

			for (PhoneNumber number : person.getPhoneNumbers().getPhoneNumberList()) {
				LinkedInPhoneNumber linkedInPhoneNumber = new LinkedInPhoneNumber();
				linkedInPhoneNumber.setLinkedInPerson(linkedInPerson);
				BeanUtilities.copyProperties(linkedInPhoneNumber, number);
				linkedInPhoneNumber.setId(null);
				numbers.add(linkedInPhoneNumber);
				linkedInDAO.saveOrUpdateLinkedInPhoneNumber(linkedInPhoneNumber);
			}

			// TODO: This seems like a hack. Why doesn't look up work?
			linkedInPerson.setLinkedInPhoneNumbers(numbers);
		}

		return linkedInPerson;
	}

	private LinkedInApiClient newLinkedInClient(Long userId) throws Exception {
		LinkedInApiClient apiClient = null;

		OAuthToken oauthToken = oAuthTokenDAO.findByUserAndProvider(userId, OAuthTokenProviderType.LINKEDIN);
		if (oauthToken != null) {
			LinkedInAccessToken accessToken = new LinkedInAccessToken(oauthToken.getAccessToken(), oauthToken.getAccessTokenSecret());
			apiClient = linkedInApiClientFactory.createLinkedInApiClient(accessToken);
		}

		return apiClient;
	}

	private LinkedInApiClient newLinkedInClientForAnonymous(String sessionId) throws Exception {
		LinkedInApiClient apiClient = null;

		OAuthToken oauthToken = oAuthTokenDAO.findBySessionIdAndProvider(sessionId, OAuthTokenProviderType.LINKEDIN);

		if (oauthToken != null) {
			LinkedInAccessToken accessToken = new LinkedInAccessToken(oauthToken.getAccessToken(), oauthToken.getAccessTokenSecret());
			apiClient = linkedInApiClientFactory.createLinkedInApiClient(accessToken);
		}
		return apiClient;
	}

	public static class LinkedInImportFailed extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4327368989540481395L;

		public LinkedInImportFailed(String s, Throwable throwable) {
			super(s, throwable);
		}
	}
}
