package com.workmarket.service.business;

import com.workmarket.domains.model.MessagePagination;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.service.business.dto.EMailDTO;

import java.util.List;

public interface MessagingService {

	void sendEmailToUsers(long fromUserId, List<Long> toUserIds, EMailDTO emailDTO);

	void sendEmailToUsers(Long fromUserId, Integer[] toUserIds, EMailDTO emailDTO);

	void sendEmailToUser(long fromUserId, long toUserId, EMailDTO emailDTO);

	void sendEmailToGroupMembers(Long fromUserId, Long groupId, EMailDTO emailDTO);

	MessagePagination findAllSentMessagesByUserGroup(long groupId, MessagePagination pagination);

	Integer countAllSentMessagesByUserGroup(long groupId);

	void sendAssessmentInvitation(AbstractAssessment assessment, long toUserId);

	void sendAssessmentInvitation(Long[] assessmentIds, long toUserId);
}
