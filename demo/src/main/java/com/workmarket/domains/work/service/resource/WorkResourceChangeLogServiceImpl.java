package com.workmarket.domains.work.service.resource;

import com.workmarket.common.template.OnBehalfofEmailTemplate;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.note.NoteDAO;
import com.workmarket.dao.note.NoteMetadataDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.work.dao.ResourceNoteDAO;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.dao.WorkResourceChangeLogDAO;
import com.workmarket.domains.work.dao.WorkResourceChangeNoteDAO;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkResourceAction;
import com.workmarket.domains.work.model.WorkResourceActionType;
import com.workmarket.domains.work.model.WorkResourceChangeLog;
import com.workmarket.domains.work.model.WorkResourceChangeNote;
import com.workmarket.domains.work.model.audit.WorkAuditType;
import com.workmarket.domains.work.service.WorkNoteService;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.domains.work.service.audit.WorkAuditService;
import com.workmarket.domains.work.service.resource.action.WorkResourceActionService;
import com.workmarket.domains.work.service.workresource.WorkResourceDetailCache;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.service.exception.InvalidParameterException;
import com.workmarket.service.infra.notification.NotificationService;
import com.workmarket.thrift.core.Note;
import com.workmarket.thrift.work.DeclineWorkOfferRequest;
import com.workmarket.thrift.work.ResourceNote;
import com.workmarket.thrift.work.ResourceNoteRequest;
import com.workmarket.thrift.work.ResourceNoteType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
public class WorkResourceChangeLogServiceImpl implements WorkResourceChangeLogService {

	private static final Log logger = LogFactory.getLog(WorkResourceChangeLogServiceImpl.class);

	@Autowired private WorkResourceActionService actionService;
	@Autowired private WorkResourceChangeLogDAO workResourceChangeLogDAO;
	@Autowired private WorkResourceChangeNoteDAO workResourceChangeNoteDAO;
	@Autowired private WorkResourceDAO workResourceDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private WorkDAO workDAO;
	@Autowired private ResourceNoteDAO resourceNoteDAO;
	@Autowired private NotificationService notificationService;
	@Autowired private WorkAuditService workAuditService;
	@Autowired private WorkNoteService workNoteService;
	@Autowired private WorkResourceDetailCache workResourceDetailCache;

	private void fillOutCreatorAndModifiedFields(User onBehalfOfUser, WorkResourceChangeLog changeLog) {
		changeLog.setCreatedOn(Calendar.getInstance());
		changeLog.setCreatorId(onBehalfOfUser.getId());
		changeLog.setModifiedOn(Calendar.getInstance());
		changeLog.setModifierId(onBehalfOfUser.getId());
	}

	@Override
	public void declineWorkSuccess(DeclineWorkOfferRequest request, User user,
								   User onBehalfOfUser, Work work, User masqueradeUser) throws InvalidParameterException {
		Assert.notNull(request);
		Assert.notNull(work);

		WorkResourceAction action = actionService.findAction(request);
		WorkResource workResource = workResourceDAO.findByUserAndWork(user.getId(), work.getId());
		Assert.notNull(workResource, "work resource not found");

		WorkResourceChangeLog changeLog = new WorkResourceChangeLog(workResource, masqueradeUser, onBehalfOfUser, action);
		fillOutCreatorAndModifiedFields(onBehalfOfUser, changeLog);
		workResourceChangeLogDAO.saveOrUpdate(changeLog);

		//Store Note in the regular note table
		saveNoteForWorkResourceChangeLog(request.getNote(), user, work.getId(), changeLog);

		sendNotification(work, workResource, changeLog, onBehalfOfUser, request.getNote());
	}

	@Override
	public void resourceNoteSuccess(ResourceNoteRequest request) throws InvalidParameterException {
		Assert.notNull(request);

		WorkResourceAction action = actionService.findAction(request);
		WorkActionRequest workAction = request.getWorkAction();
		String note = request.getNote();
		WorkActionRequest wmAction = createWorkResourceChangeLog(action, workAction, note, request);
		wmAction.setAuditType(WorkAuditType.NOTE);
		workAuditService.auditAndReindexWork(wmAction);
	}

	com.workmarket.domains.work.service.audit.WorkActionRequest createWorkResourceChangeLog(WorkResourceAction action,
																									 WorkActionRequest workAction, String note, Object tRequest) throws InvalidParameterException {
		if (action == null) {
			throw new InvalidParameterException("Invalid action sent over. Make sure it's registered in the database." + tRequest);
		}
		User workerUser = userDAO.findUserByUserNumber(workAction.getResourceUserNumber(), true);
		Assert.notNull(workerUser, "Invalid user number for resource " + tRequest);

		Work work = workDAO.findWorkByWorkNumber(workAction.getWorkNumber());
		Assert.notNull(work, "The work was not found." + tRequest);

		WorkResource workResource = workResourceDAO.findByUserAndWork(workerUser.getId(), work.getId());
		Assert.notNull(workResource, "The work resource was not found." + tRequest);

		User masqueradeUser = userDAO.findUserByUserNumber(workAction.getMasqueradeUserNumber(), false, true);
		User onBehalfOfUser = userDAO.findUserByUserNumber(workAction.getOnBehalfOfUserNumber(), false);
		WorkResourceChangeLog changeLog = new WorkResourceChangeLog(workResource, masqueradeUser, onBehalfOfUser, action);
		fillOutCreatorAndModifiedFields(onBehalfOfUser, changeLog);
		workResourceChangeLogDAO.saveOrUpdate(changeLog);

		//Store Note in the normal note table
		saveNoteForWorkResourceChangeLog(note, workerUser, work.getId(), changeLog);

		//Send Email
		sendNotification(work, workResource, changeLog, onBehalfOfUser, note);

		//Wm Action
		com.workmarket.domains.work.service.audit.WorkActionRequest wmAction = new com.workmarket.domains.work.service.audit.WorkActionRequest();
		wmAction.setWorkId(work.getId());
		wmAction.setModifierId(workResource.getUser().getId());
		wmAction.setMasqueradeId(masqueradeUser != null ? masqueradeUser.getId() : null);
		wmAction.setOnBehalfOfId(onBehalfOfUser != null ? onBehalfOfUser.getId() : null);

		workResourceDetailCache.evict(work.getId());
		return wmAction;
	}

	/*** NOTE
	 *
	 *  The logic for onbehalfOfUserId is backwards for Realtime.
	 *  onBehalfOfUser means the user who is taking the action.
	 *  However, in notes, we want to store so it means the user id of the worker which we are impersonating
	 *  and the creator of the note is the onbehalfOfUserId
	 */
	com.workmarket.domains.model.note.Note saveNoteForWorkResourceChangeLog(String note, User workerUser, long workId, WorkResourceChangeLog changeLog) {
		Assert.notNull(changeLog);
		Assert.notNull(workerUser);

		if (isNotBlank(note)) {

			NoteDTO noteDTO = new NoteDTO()
				.setContent(note)
				.setIsPrivate(true)
				.setOnBehalfOfUserId(workerUser.getId());

			com.workmarket.domains.model.note.Note workNote = workNoteService.addNoteToWork(workId, noteDTO, workerUser);
			if (workNote != null) {
				WorkResourceChangeNote workResourceChangeNote = new WorkResourceChangeNote(workNote, changeLog);
				workResourceChangeNoteDAO.saveOrUpdate(workResourceChangeNote);
				return workNote;
			}
		}
		return null;
	}

	@Override
	public List<Note> findNotesByResourceByWorkId(Long workId) {
		Assert.notNull(workId);
		List<Note> resourceNotes = resourceNoteDAO.getResourceNotesForWorkByWorkId(workId);
		hydrateWorkNotes(resourceNotes);
		return resourceNotes;
	}

	@Override
	public Map<Long, List<ResourceNote>> findResourceNotesByWorkId(Long workId) {
		Assert.notNull(workId);
		Map<Long, List<ResourceNote>> resourceNotes = resourceNoteDAO.getResourceNotesByWorkId(workId);
		hydrateResourceNotes(resourceNotes);
		return resourceNotes;
	}

	private void hydrateWorkNotes(List<Note> resourceNotes) {
		if (CollectionUtils.isEmpty(resourceNotes)) {
			return;
		}
		for (Note note : resourceNotes) {
			WorkResourceAction action = actionService.findById(Short.valueOf(note.getActionCodeId()).longValue());
			note.setActionCodeText(action.getActionDescription());
		}
	}

	private void hydrateResourceNotes(Map<Long, List<ResourceNote>> resourceNoteMap) {
		for (List<ResourceNote> resourceNotes : resourceNoteMap.values()) {
			for (ResourceNote note : resourceNotes) {
				WorkResourceAction action = actionService.findById(note.getActionCodeId());
				note.setActionCodeDescription(action.getActionDescription());
				note.setHoverType(findResourceNoteType(action));
			}
		}
	}

	private ResourceNoteType findResourceNoteType(WorkResourceAction action) {
		switch (action.actionType()) {
			case ACCEPT_WORK:
				return ResourceNoteType.ACCEPT;
			case COUNTER_OFFER:
				return ResourceNoteType.COUNTER;
			case DECLINE_WORK:
				return ResourceNoteType.DECLINE;
			case NOTE:
				return ResourceNoteType.NOTE;
			case QUESTION:
				return ResourceNoteType.QUESTION;
			case REROUTE_WORK:
				return ResourceNoteType.REROUTE;
		}
		return null;
	}


	private String findResourceEmailString(String actionType) {
		if (actionType.equals(WorkResourceActionType.ACCEPT_WORK.getActionTypeName())) {
			return "accepted";
		} else if (actionType.equals(WorkResourceActionType.COUNTER_OFFER.getActionTypeName())) {
			return "created a counter offer";
		} else if (actionType.equals(WorkResourceActionType.DECLINE_WORK.getActionTypeName())) {
			return "declined";
		} else if (actionType.equals(WorkResourceActionType.NOTE.getActionTypeName())) {
			return "posted a note";
		} else if (actionType.equals(WorkResourceActionType.QUESTION.getActionTypeName())) {
			return "asked a question about";
		} else if (actionType.equals(WorkResourceActionType.REROUTE_WORK.getActionTypeName())) {
			return "rerouted";
		}

		return null;
	}

	private void sendNotification(Work work, WorkResource workResource, WorkResourceChangeLog changeLog, User onBehalfOfUser, String changeLogNote) {
		OnBehalfofEmailTemplate template = new OnBehalfofEmailTemplate(work.getBuyer().getId(),
			workResource.getUser().getId(),
			work.getTitle(),
			work.getWorkNumber(),
			findResourceEmailString(changeLog.getWorkResourceAction().getActionType()),
			onBehalfOfUser.getFirstName() + " " + onBehalfOfUser.getLastName(),
			changeLogNote);

		notificationService.sendNotification(template);
	}
}
