package com.workmarket.service.web;

import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.web.facade.ProfileFacade;

import java.util.List;

public interface ProfileFacadeService {

	boolean isCurrentUserAuthorizedToSeeProfile(ExtendedUserDetails currentUser, ProfileFacade profileFacade);
	boolean isCurrentUserAuthorizedToEditProfile(ExtendedUserDetails currentUser, ProfileFacade profileFacade);

	ProfileFacade findProfileFacadeByUserNumber(String userNumber) throws Exception;

	ProfileFacade findProfileFacadeByCompanyNumber(String companyNumber) throws Exception;

	List<ProfileFacade> findSearchCardProfileFacadeByUserIds(List<Long> userIds, Long currentUserId) throws Exception;
}
