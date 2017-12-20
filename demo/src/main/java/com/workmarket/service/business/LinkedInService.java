package com.workmarket.service.business;

import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.workmarket.domains.model.linkedin.LinkedInPerson;

public interface LinkedInService {
	boolean authorize(LinkedInRequestToken requestToken, String sessionId, String oAuthVerifier);

	LinkedInPerson importPersonData(Long userId) throws LinkedInServiceImpl.LinkedInImportFailed;

	LinkedInPerson importPersonDataForAnonymous(String sessionId) throws LinkedInServiceImpl.LinkedInImportFailed;

	LinkedInPerson findMostRecentLinkedInPerson(Long userId);

	boolean attemptToLinkUser(String sessionId, Long userId);

	boolean attemptToLinkUserById(String linkedInId, Long userId);

	LinkedInResult findUsernameByOAuth(String oAuthToken);
}
