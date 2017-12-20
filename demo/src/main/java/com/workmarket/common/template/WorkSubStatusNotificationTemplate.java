package com.workmarket.common.template;

import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeAssociation;
import com.workmarket.domains.model.note.WorkNote;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class WorkSubStatusNotificationTemplate extends AbstractWorkNotificationTemplate {

	private static final long serialVersionUID = 8688610186815293451L;
	private WorkSubStatusTypeAssociation association;
	private WorkResource workResource;

	public WorkSubStatusNotificationTemplate(Long toId, WorkSubStatusTypeAssociation association, Work work, WorkResource workResource, Long activeWorkerId) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.WORK_SUBSTATUS), ReplyToType.TRANSACTIONAL, work, activeWorkerId);

		if (isToActiveWorker()) {
			setNotificationType(new NotificationType(NotificationType.RESOURCE_WORK_SUBSTATUS));
		}

		this.association = association;
		this.workResource = workResource;
	}

	public String getException() {
		return association.getWorkSubStatusType().getDescription();
	}

	public Long getExceptionId() {
		return association.getWorkSubStatusType().getId();
	}

	public WorkResource getWorkResource() {
		return workResource;
	}

	public WorkNote getWorkNote() {
		if (workResource != null && workResource.getUser().getId().equals(getToId())) {
			if (association.getTransitionNote() != null && association.getTransitionNote().getIsPrivate()) {
				return null;
			}
		}
		return association.getTransitionNote();
	}
}
