package com.workmarket.common.template;

import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.infra.communication.ReplyToType;

public class WorkRemindResourceToCompleteNotificationTemplate extends AbstractWorkNotificationTemplate {
	private Note note;

	public WorkRemindResourceToCompleteNotificationTemplate(Long fromId, Long toId, Work work, Note note, String noteCreatorFullName) {
		this(fromId, toId, work, note);
		this.noteCreatorFullName = noteCreatorFullName;
	}

	public WorkRemindResourceToCompleteNotificationTemplate(Long fromId, Long toId, Work work, Note note) {
		super(fromId, toId, NotificationType.newNotificationType(NotificationType.WORK_REMINDER_TO_COMPLETE), ReplyToType.TRANSACTIONAL_FROM_USER, work);
		this.note = note;
	}

	public Note getNote() {
		return note;
	}
}
