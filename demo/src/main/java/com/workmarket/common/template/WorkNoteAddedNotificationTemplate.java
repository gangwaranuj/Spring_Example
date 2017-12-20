package com.workmarket.common.template;

import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class WorkNoteAddedNotificationTemplate extends AbstractWorkNotificationTemplate {

	private static final long serialVersionUID = -3395310634350111020L;
	private Note note;
	private String type;

	public WorkNoteAddedNotificationTemplate(Long toId, Work work, Note note, String noteCreatorFullName, Long activeWorkerId, String type) {
		this(toId, work, note, activeWorkerId, type);
		this.noteCreatorFullName = noteCreatorFullName;
	}

	public WorkNoteAddedNotificationTemplate(Long toId, Work work, Note note, Long activeWorkerId, String type) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL,
			toId,
			new NotificationType(type),
			ReplyToType.TRANSACTIONAL,
			work,
			activeWorkerId);

		if (isToActiveWorker()) {
			setNotificationType(new NotificationType(NotificationType.RESOURCE_WORK_NOTE_ADDED));
		}

		this.note = note;
		this.type = work.isWorkBundle() ? Constants.WORK_TYPE_BUNDLE : Constants.WORK_TYPE_ASSIGNMENT;
	}

	public Note getNote() {
		return note;
	}
	public String getType() {
		return type;
	}
}
