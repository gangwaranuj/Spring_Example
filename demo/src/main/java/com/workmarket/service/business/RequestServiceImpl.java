package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.dao.InvitationDAO;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.assessment.AbstractAssessmentDAO;
import com.workmarket.dao.request.FlatRequestDAO;
import com.workmarket.domains.groups.dao.UserGroupDAO;
import com.workmarket.dao.request.RequestDAO;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.model.Invitation;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserGroupInvitationType;
import com.workmarket.domains.model.request.*;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.EncryptionUtilities;
import com.workmarket.utility.NumberUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;

@Service
public class RequestServiceImpl implements RequestService {

	private static final Log logger = LogFactory.getLog(RequestServiceImpl.class);
	@Autowired private AssessmentService assessmentService;
	@Autowired private InvitationDAO invitationDAO;
	@Autowired private MessagingService messagingService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private RequestDAO requestDAO;
	@Autowired private FlatRequestDAO flatRequestDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private UserGroupDAO userGroupDAO;
	@Autowired private AbstractAssessmentDAO assessmentDAO;
	@Autowired private UserIndexer userIndexer;
	@Autowired private EventRouter eventRouter;

	@Override
	public void acceptRequest(Request request) {
		request.setRequestStatusType(new RequestStatusType(RequestStatusType.ACCEPTED));
	}

	@Override
	public void deleteRequest(Long requestId) {
		Request request = requestDAO.get(requestId);
		request.setDeleted(true);
	}

	@Override
	public List<Request> findRequestsByRequestor(Long userId) {
		return requestDAO.findByRequestor(userId);
	}

	@Override
	public List<Request> findRequestsByInvitedUser(Long userId) {
		return requestDAO.findByInvitedUser(userId);
	}

	@Override
	public List<Request> findRequestsByInvitation(Long invitationId) {
		return requestDAO.findByInvitation(invitationId);
	}

	@Override
	public List<AssessmentInvitation> findAssessmentInvitationRequestsByAssessment(Long assessmentId) {
		return requestDAO.findAssessmentInvitationsByAssessment(assessmentId);
	}

	@Override
	public RequestPagination findAssessmentInvitationRequestsByAssessment(Long assessmentId, RequestPagination pagination) {
		return requestDAO.findAssessmentInvitationsByAssessment(assessmentId, pagination);
	}

	private List<AssessmentInvitation> findAssessmentInvitationRequestsByInvitedUserAndAssessment(Long userId, Long assessmentId) {
		return requestDAO.findAssessmentInvitationsByInvitedUserAndAsseessment(userId, assessmentId);
	}

	@Override
	public int countUserGroupInvitationsByInvitedUser(Long userId) {
		return requestDAO.countUserGroupInvitationsByInvitedUser(userId);
	}

	@Override
	public List<UserGroupInvitation> findUserGroupInvitationsByUser(Long userId) {
		return requestDAO.findUserGroupInvitationsByUser(userId);
	}

	@Override
	public List<UserGroupInvitation> findUserGroupInvitationRequestsByUserGroup(Long groupId) {
		return requestDAO.findUserGroupInvitationsByUserGroup(groupId);
	}

	@Override
	public List<UserGroupInvitation> findUserGroupInvitationRequestsByInvitedUserAndUserGroup(Long userId, Long groupId) {
		return requestDAO.findUserGroupInvitationsByInvitedUserAndUserGroup(userId, groupId);
	}

	@Override
	public UserGroupInvitation findLatestUserGroupInvitationRequestByInvitedUserAndUserGroup(Long userId, Long groupId) {
		return requestDAO.findLatestUserGroupInvitationByInvitedUserAndUserGroup(userId, groupId);
	}

	@Override
	public Map<Long,List<UserGroupInvitation>> findUserGroupInvitationRequestMapByInvitedUsersAndUserGroup(List<Long> userIds, Long groupId) {
		List<UserGroupInvitation> invitations = requestDAO.findUserGroupInvitationsByInvitedUsersAndUserGroup(userIds, groupId);
		Map<Long, List<UserGroupInvitation>> invitationMap = Maps.newHashMap();
		for(Long userId : userIds){
			List<UserGroupInvitation> perUserInvitations = filter(having(on(UserGroupInvitation.class).getInvitedUser().getId(),equalTo(userId)),invitations);
			invitationMap.put(userId,perUserInvitations);
		}
		return invitationMap;
	}


	@Override
	public List<UserGroupInvitation> findUserGroupInvitationRequestsAllStatusByInvitedUsersAndUserGroup(List<Long> userIds, Long groupId) {
		Assert.notNull(groupId);
		Assert.notEmpty(userIds);
		return requestDAO.findUserGroupInvitationsAllStatusByInvitedUsersAndUserGroup(userIds, groupId);
	}

	@Override
	public UserGroupInvitationPagination findUserGroupInvitationsForInvitedUser(Long userId, UserGroupInvitationPagination pagination) {
		Assert.notNull(userId);
		Assert.notNull(pagination);
		return requestDAO.findUserGroupInvitationsForInvitedUser(userId, pagination);
	}

	@Override
	public Boolean userHasInvitationToGroup(Long userId, Long groupId) {
		return (findUserGroupInvitationRequestsByInvitedUserAndUserGroup(userId, groupId).size() > 0);
	}

	@Override
	public Boolean userHasInvitationToAssessment(Long userId, Long assessmentId) {
		return (findAssessmentInvitationRequestsByInvitedUserAndAssessment(userId, assessmentId).size() > 0);
	}

	@Override
	public void inviteUsersToAssessment(Long userId, Collection<Long> inviteeUserIds, Long assessmentId) {
		Assert.notEmpty(inviteeUserIds);
		Assert.notNull(userId);
		Assert.notNull(assessmentId);
		for (Long inviteeUserId : inviteeUserIds) {
			inviteUserToAssessment(userId, inviteeUserId, assessmentId);
		}
	}

	@Override
	public void inviteUserToAssessment(Long userId, Long inviteeUserId, Long assessmentId) {
		AbstractAssessment assessment = assessmentDAO.get(assessmentId);
		if (createAssessmentInvitation(userId, inviteeUserId, assessment) != null) {
			messagingService.sendAssessmentInvitation(assessment, inviteeUserId);
		}
	}

	@Override
	public void inviteUserToAssessments(Long userId, Long inviteeUserId, Long[] assessmentIds) {
		Assert.notEmpty(assessmentIds);

		for (Long assessmentId : assessmentIds) {
			createAssessmentInvitation(userId, inviteeUserId, assessmentDAO.get(assessmentId));
		}
		messagingService.sendAssessmentInvitation(assessmentIds, inviteeUserId);
	}

	@Override
	public void inviteUsersToGroup(long userId, long[] inviteeUserIds, long groupId) {
		for (long inviteeUserId : inviteeUserIds) {
			inviteUserToGroup(userId, inviteeUserId, groupId);
		}
	}

	@Override
	public void inviteUserToGroup(Long userId, Long inviteeUserId, Long groupId) {
		inviteUserToGroup(userId, inviteeUserId, groupId, UserGroupInvitationType.NEW);
	}

	@Override
	public void inviteUserToGroup(Long userId, Long inviteeUserId, Long groupId, UserGroupInvitationType userGroupInvitationType) {
		Assert.notNull(userId);
		Assert.notNull(inviteeUserId);
		Assert.notNull(groupId);

		if (userHasInvitationToGroup(inviteeUserId, groupId)) {
			logger.debug("userHasInvitationToGroup " + groupId + " user " + inviteeUserId);
			return;
		}

		UserGroup group = userGroupDAO.get(groupId);
		Assert.notNull(group);

		Request request = new UserGroupInvitation(
				userDAO.get(userId),
				userDAO.get(inviteeUserId),
				userGroupDAO.get(groupId),
				//We use the TERMS_MODIFICATION just for the email but we want it to be treated as a NEW invitation
				//TODO: Search only displays NEW invitation on the count so we may want to add TERMS_MODIFICATION
				(UserGroupInvitationType.TERMS_AND_AGREEMENTS_MODIFICATION.equals(userGroupInvitationType) ? UserGroupInvitationType.NEW : userGroupInvitationType)
		);
		request.setRequestStatusType(new RequestStatusType(RequestStatusType.SENT));
		requestDAO.saveOrUpdate(request);
		userNotificationService.onUserGroupInvitation(group, request, userGroupInvitationType);
	}

	@Override
	public List<Request> saveInvitesForInviteUsersToGroup(List<User> users, User inviteeUser, UserGroup group, UserGroupInvitationType userGroupInvitationType) {
		Assert.notNull(users);
		Assert.notNull(inviteeUser);
		Assert.notNull(group);
		Assert.notNull(userGroupInvitationType);
		List<Request> requests = Lists.newArrayList();
		for (User user : users) {
			UserGroupInvitation request = new UserGroupInvitation(inviteeUser, user, group, (userGroupInvitationType.equals(UserGroupInvitationType.TERMS_AND_AGREEMENTS_MODIFICATION) ? UserGroupInvitationType.NEW : userGroupInvitationType));
			request.setRequestStatusType(new RequestStatusType(RequestStatusType.SENT));
			requestDAO.saveOrUpdate(request);
			requests.add(request);
		}

		return requests;
	}

	@Override
	public List<FlatRequest> createFlatInvitesForInviteUsersToGroup(List<Long> userIds, Long inviteeUserId, Long groupId, UserGroupInvitationType userGroupInvitationType) {
		Assert.notNull(userIds);
		Assert.notNull(inviteeUserId);
		Assert.notNull(groupId);
		Assert.notNull(userGroupInvitationType);
		List<FlatRequest> requests = Lists.newArrayList();

		for (Long userId : userIds) {
			if (NumberUtilities.isNegative(userId)) {
				logger.error("Invalid user in userIds list!");
			} else {
				if (userGroupInvitationType.equals(UserGroupInvitationType.TERMS_AND_AGREEMENTS_MODIFICATION)) {
					userGroupInvitationType = UserGroupInvitationType.NEW;
				}
				FlatUserGroupInvitation request = new FlatUserGroupInvitation(inviteeUserId, userId, groupId,userGroupInvitationType);
				request.setRequestStatusTypeCode(RequestStatusType.SENT);
				requests.add(request);
			}
		}

		logger.debug("Created " + requests.size() + " invites from " + userIds.size() + " ids provided for group " + groupId + " and user " + inviteeUserId);
		return requests;
	}

	@Override
	public void saveInvitesForInviteUsersToGroup(List<FlatRequest> requests) {
		try {
			flatRequestDAO.saveAll(requests);
		}
		catch (Exception t) {
			StringBuilder sb = new StringBuilder("Failed to save requests:\n ");
			for (FlatRequest fq : requests) {
				sb.append(fq.toString() + "\n");
			}
			logger.error(sb.toString());
			throw t;
		}
	}

	// this is a batch/non-async version of the above
	public void sendInvitesForAddUserToGroup(Long groupId, Long requester, List<Long> invitedUserIds) {
		userNotificationService.onUserGroupInvitations(groupId, requester, invitedUserIds);
	}

	@Override
	public void deleteInvitationToGroup(Long userId, Long groupId) {
		Assert.notNull(userId);
		Assert.notNull(groupId);

		setGroupInvitationRequestsToDeleted(userId, groupId);
		eventRouter.sendEvent(
			new UserSearchIndexEvent(userId)
		);
	}

	@Override
	public void deleteInvitationsToGroup(List<Long> userIds, Long groupId) {
		Assert.notNull(userIds);
		Assert.notNull(groupId);

		for (Long userId : userIds) {
		   setGroupInvitationRequestsToDeleted(userId, groupId);
		}
		eventRouter.sendEvent(
			new UserSearchIndexEvent(userIds)
		);
	}

	private void setGroupInvitationRequestsToDeleted(Long userId, Long groupId) {
		for (UserGroupInvitation i : findUserGroupInvitationRequestsByInvitedUserAndUserGroup(userId, groupId)) {
			i.setDeleted(true);
		}
	}

	public void extendInvitationToGroup(Long userId, Long invitationId, Long groupId) {
		Assert.notNull(invitationId);
		extendInvitationToGroup(userId, invitationDAO.get(invitationId), groupId);
	}

	private void extendInvitationToGroup(Long userId, Invitation invitation, Long groupId) {
		Assert.notNull(userId);
		Assert.notNull(invitation);
		Assert.notNull(groupId);

		Request request = new UserGroupInvitation(
				userDAO.get(userId),
				invitation,
				userGroupDAO.get(groupId)
		);
		request.setRequestStatusType(new RequestStatusType(RequestStatusType.SENT));
		requestDAO.saveOrUpdate(request);

		// Don't send notification/email since they will have already received a message w/their invitation
	}

	@Override
	public void declineInvitationToGroup(Long userId, Long groupId) {
		Assert.notNull(userId);
		Assert.notNull(groupId);
		for (UserGroupInvitation i : findUserGroupInvitationRequestsByInvitedUserAndUserGroup(userId, groupId)) {
			i.setRequestStatusType(new RequestStatusType(RequestStatusType.DECLINED));
		}
		userIndexer.reindexById(userId);
	}

	public PasswordResetRequest requestPasswordReset(Long requestorId, Long userId) {
		Calendar expiresOn = DateUtilities.getCalendarNow();
		expiresOn.add(Calendar.MONTH, 6);

		return requestPasswordReset(requestorId, userId, expiresOn);
	}

	public PasswordResetRequest requestPasswordReset(Long requestorId, Long userId, Calendar expirationDate) {
		Assert.notNull(requestorId);
		Assert.notNull(userId);

		Request request = new PasswordResetRequest(
				userDAO.get(requestorId),
				userDAO.get(userId),
				DateUtilities.cloneCalendar(expirationDate)
		);
		request.setRequestStatusType(new RequestStatusType(RequestStatusType.SENT));
		requestDAO.saveOrUpdate(request);

		return (PasswordResetRequest) request;
	}

	public PasswordResetRequest findLatestSentPasswordResetRequestByInvitedUser(Long userId) {
		Assert.notNull(userId);
		return requestDAO.findLatestSentPasswordResetRequestByInvitedUser(userId);
	}

	public PasswordResetRequest findPasswordResetRequest(String encryptedId) {
		Assert.notNull(encryptedId);
		PasswordResetRequest request = (PasswordResetRequest) requestDAO.get(EncryptionUtilities.decryptLong(encryptedId));
		if (request == null)
			return null;
		userDAO.initialize(request.getInvitedUser());
		return request;
	}

	private AssessmentInvitation createAssessmentInvitation(Long userId, Long inviteeUserId, AbstractAssessment assessment) {
		Assert.notNull(userId);
		Assert.notNull(inviteeUserId);
		Assert.notNull(assessment);

		AssessmentInvitation request = null;

		if (assessmentService.hasUserPassedAssessment(inviteeUserId, assessment.getId())) {
			logger.debug("User " + inviteeUserId + " has already passed the Test " + assessment.getId());
			return request;
		}

		if (userHasInvitationToAssessment(inviteeUserId, assessment.getId())) {
			logger.debug("userHasInvitationToAssessment " + assessment.getId() + " user " + inviteeUserId);
			return request;
		}

		request = new AssessmentInvitation(
				userDAO.get(userId),
				userDAO.get(inviteeUserId),
				assessment
		);
		request.setRequestStatusType(new RequestStatusType(RequestStatusType.SENT));
		requestDAO.saveOrUpdate(request);

		return request;
	}
}
