package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.workmarket.common.template.NotificationTemplateFactory;
import com.workmarket.dao.MessageDAO;
import com.workmarket.dao.assessment.AbstractAssessmentDAO;
import com.workmarket.domains.groups.dao.UserGroupDAO;
import com.workmarket.domains.model.Message;
import com.workmarket.domains.model.MessagePagination;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.business.dto.EMailDTO;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.notification.NotificationService;
import com.workmarket.utility.ArrayUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class MessagingServiceImpl implements MessagingService {

	private static final Log logger = LogFactory.getLog(MessagingServiceImpl.class);

	@Autowired private MessageDAO messageDAO;
	@Autowired private UserGroupDAO userGroupDAO;
	@Autowired private UserGroupService userGroupService;
	@Autowired private AbstractAssessmentDAO assessmentDAO;
	@Autowired private NotificationService notificationService;
	@Autowired private UserService userService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private NotificationTemplateFactory notificationTemplateFactory;
	@Autowired private EventRouter eventRouter;
	@Autowired private EventFactory eventFactory;

	@Override
	public void sendEmailToUsers(long fromUserId, List<Long> toUserIds, EMailDTO emailDTO) {
		Assert.notEmpty(toUserIds);
		Assert.notNull(emailDTO);

		for (Long id : toUserIds) {
			sendEmailToUser(fromUserId, id, emailDTO);
		}
	}

	@Override
	public void sendEmailToUsers(Long fromUserId, Integer[] toUserIds, EMailDTO emailDTO) {
		sendEmailToUsers(fromUserId, Lists.newArrayList(ArrayUtilities.convertToLongArrays(toUserIds)), emailDTO);
	}

	@Override
	public void sendEmailToUser(long fromUserId, long toUserId, EMailDTO emailDTO) {
		Assert.notNull(emailDTO);

		notificationService.sendNotification(
			notificationTemplateFactory.buildGenericEmailOnlyNotificationTemplate(fromUserId, toUserId, emailDTO.getSubject(), emailDTO.getText(), NotificationType.newNotificationType(NotificationType.WORKMARKET_MESSAGE))
		);
	}

	@Override
	public void sendEmailToGroupMembers(Long fromUserId, Long groupId, EMailDTO emailDTO) {
		User user = userService.getUser(fromUserId);

		Message message = new Message();
		message.setSender(user);
		message.setSubject(emailDTO.getSubject());
		message.setContent(emailDTO.getText());

		UserGroup group = userGroupService.findGroupById(groupId);
		if (user.getCompany().getId().equals(group.getCompany().getId())) {
			message.getUserGroups().add(group);
		} else {
			logger.warn("User " + fromUserId + " attempted to send a message to user group id: " + groupId);
		}

		messageDAO.saveOrUpdate(message);
		eventRouter.sendEvent(eventFactory.buildUserGroupMessageNotificationEvent(message));
	}


	@Override
	public MessagePagination findAllSentMessagesByUserGroup(long groupId, MessagePagination pagination) {
		Assert.notNull(pagination);

		return messageDAO.findAllSentMessagesByUserGroup(groupId, pagination);
	}

	@Override
	public Integer countAllSentMessagesByUserGroup(long groupId) {
		return messageDAO.countAllSentMessagesByUserGroup(groupId);
	}

	@Override
	public void sendAssessmentInvitation(AbstractAssessment assessment, long toUserId) {
		Assert.notNull(assessment);
		notificationService.sendNotification(
			notificationTemplateFactory.buildAssessmentInvitationEmailTemplate(authenticationService.getCurrentUser().getId(), assessment, toUserId)
		);
	}

	@Override
	public void sendAssessmentInvitation(Long[] assessmentIds, long toUserId) {
		Assert.notNull(assessmentIds);

		List<AbstractAssessment> assessments = assessmentDAO.get(assessmentIds);

		notificationService.sendNotification(
			notificationTemplateFactory.buildMultipleAssessmentInvitationsNotificationTemplate(authenticationService.getCurrentUser().getId(), assessments, toUserId)
		);
	}
}
