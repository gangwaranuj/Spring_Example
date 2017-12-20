package com.workmarket.service.business;

import com.google.common.base.Optional;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Invitation;
import com.workmarket.domains.model.InvitationPagination;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.integration.IntegrationUser;
import com.workmarket.domains.model.integration.autotask.AutotaskUser;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.dto.CreateNewWorkerRequest;
import com.workmarket.service.business.dto.InvitationDTO;
import com.workmarket.service.business.dto.InvitationUserRegistrationDTO;
import com.workmarket.service.business.dto.ProfileDTO;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.authentication.InvalidAclRoleException;
import com.workmarket.web.models.MessageBundle;
import org.springframework.security.authentication.BadCredentialsException;

import java.io.IOException;
import java.util.List;

public interface RegistrationService {

	public static enum OnboardingNotificationStrategy {
		NONE, WELCOME_EMAIL, PASSWORD_RESET
	}

	void blacklistEmail(String email);

	Boolean isBlacklisted(String email);

	void addNewIntegrationUser(IntegrationUser integrationUser);

	Optional<AutotaskUser> getIntegrationUserByUserId(Long userId);

	User registerNew(UserDTO userDTO, Long invitationId) throws Exception;

	User registerNew(UserDTO userDTO, Long invitationId, String companyName, AddressDTO addressDTO, ProfileDTO profileDTO, boolean isBuyer, boolean notifyUser) throws Exception;

	User registerNewForCompany(UserDTO userDTO, Long companyId) throws Exception;

	User registerNewForCompany(UserDTO userDTO, Long companyId, Long[] aclRoleIds) throws Exception;

	User registerNewForCompany(UserDTO userDTO, Long companyId, Long[] aclRoleIds, boolean emailUser) throws Exception;

	User registerNewInternalUser(UserDTO userDTO) throws Exception;

	User registerUserSimple(InvitationUserRegistrationDTO invitationUserRegistrationDTO, boolean isBuyer) throws Exception;

	User registerWorker(InvitationUserRegistrationDTO invitationDTO) throws Exception;

	User registerNew(UserDTO userDTO, Long invitationId, String companyName, AddressDTO addressDTO, ProfileDTO profileDTO, boolean isBuyer) throws Exception;

	User registerNew(CreateNewWorkerRequest dto) throws Exception;

	User registerNew(final CreateNewWorkerRequest dto, boolean notify) throws Exception;

	User registerNewApiUserForCompany(long companyId);

	void validatePassword(String password);

	void checkForExistingUser(String email) throws Exception;

	Company createUserCompany(String companyName, AddressDTO addressDTO, boolean operatingAsIndividualFlag, boolean isBuyer, boolean isCompanyNameBlank);

	User createUserObject(UserDTO userDTO, Company userCompany);

	void registerUserInvitation(User user, Long invitationId);

	void associateCompanyWithUser(Company company, User user);

	void createUserProfile(User user,
	                       ProfileDTO profileDTO,
	                       AddressDTO addressDTO,
	                       boolean isBuyer,
	                       Address companyAddress,
	                       Long recruitingCampaignId,
	                       Long invitationId);

	void assignRolesAndRelationships(User user, ProfileDTO profileDTO) throws InvalidAclRoleException;

	void handleRecruitingCampaignScenario(Long recruitingCampaignId, User user);

	void dispatchNotifications(User user, Company company, boolean isBlankCompanyName, boolean isBuyer);

	User registerExistingUserForCompany(Long userId, Long newCompanyId, Long[] aclRoleIds) throws Exception;

	Optional<User> getApiUserByUserId(Long userId);

	String generateConfirmationURL(Long id);

	/* Confirm a user's email address, finding it through it's id. Return null if the email already exists or user is suspended. */
	User confirmAccount(Long id);

	/* Confirm a user's email address, avoiding or not welcome email notification. Return null if the email already exists or user is suspended. */
	User confirmAccount(Long id, boolean sendWelcomeEmail);

	/* Confirm a user's email address and approve the user account. Return null if the email already exists or user is suspended. */
	User confirmAndApproveAccount(Long id);

	void sendRemindConfirmationEmail(Long id) throws Exception;

	void sendRemindConfirmationWithPasswordResetEmail(Long id) throws Exception;

	void sendConfirmationWithPasswordResetEmail(Long fromUserId, Long toUserId) throws Exception;

	String generateForgotPasswordURL(Long id);

	void sendForgotPasswordEmail(String emailAddress) throws BadCredentialsException;

	User validatePasswordResetRequest(String encryptedId);

	MessageBundle declineInvitation(String encryptedId);

	MessageBundle acceptInvitation(String encryptedId);

	List<String[]> getUsersFromCSVUpload(String fileUUID) throws IOException, HostServiceException;

	Invitation inviteUser(InvitationDTO invitationDTO, Long[] groupIds);

	void processContractorInvitationsForUser(Long userId);

	boolean invitationExists(Long userId, String emailAddress);

	String previewInvitation(InvitationDTO invitationDTO);

	InvitationPagination findInvitations(Long invitingUserId, InvitationPagination pagination);

	void remindInvitations(List<Long> invitationIds);

	void updateUserStatusToDeleted(String email);
}
