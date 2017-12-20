package com.workmarket.domains.work.service;

import com.google.common.collect.Lists;
import com.workmarket.dao.note.NoteDAO;
import com.workmarket.dao.note.NoteMetadataDAO;
import com.workmarket.dao.note.WorkNoteDAO;
import com.workmarket.domains.model.PrivacyType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.changelog.work.WorkNoteCreatedChangeLog;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.note.NoteMetadata;
import com.workmarket.domains.model.note.NotePagination;
import com.workmarket.domains.model.note.WorkNote;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.audit.WorkAuditType;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.domains.work.service.audit.WorkAuditService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.service.business.integration.hooks.webhook.WebHookEventService;
import com.workmarket.service.infra.business.AuthenticationService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;

@Service
public class WorkNoteServiceImpl implements WorkNoteService {

	@Autowired private AuthenticationService authenticationService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private WorkAuditService workAuditService;
	@Autowired private WebHookEventService webHookEventService;
	@Autowired private WorkActionRequestFactory workActionRequestFactory;
	@Autowired private NoteMetadataDAO noteMetadataDAO;
	@Autowired private WorkNoteDAO workNoteDAO;
	@Autowired private NoteDAO noteDAO;
	@Autowired private WorkService workService;
	@Autowired private WorkChangeLogService workChangeLogService;

	private static final Log logger = LogFactory.getLog(WorkNoteServiceImpl.class);

	@Override
	public Note addNoteToWork(final Long workId, final String message) {
		return addNoteToWork(workId, new NoteDTO(message), null);
	}

	@Override
	public Note addNoteToWork(Long workId, NoteDTO noteDTO) {
		return addNoteToWork(workId, noteDTO, null);
	}

	@Override
	public Note addNoteToWork(Long workId, NoteDTO noteDTO, User onBehalfOfUser) {
		Assert.notNull(workId);

		Work work = workService.findWork(workId);
		Assert.notNull(work);
		return addNoteToWork(work, noteDTO, onBehalfOfUser);
	}

	@Override
	public Collection<WorkNote> bulkAddNoteToWorkList(List<Work> works, NoteDTO noteDTO, User user) {
		Assert.notEmpty(works);
		Collection<WorkNote> workNotes = Lists.newArrayList();
		for (Work work : works) {
			if (work != null) {
				WorkNote note = new WorkNote(noteDTO.getContent(), work);
				note.setIsPrivate(noteDTO.getIsPrivate());
				workNoteDAO.saveOrUpdate(note);
				workNotes.add(note);
			}
		}
		return workNotes;
	}

	@Override
	public void bulkAuditAndNotifyAddNote(Collection<WorkNote> workNotes, User user, User onBehalfOfUser) {

		for (WorkNote note : workNotes) {
			Work work = note.getWork();
			Assert.notNull(work);

			Long onBehalfOfUserId = onBehalfOfUser != null ? onBehalfOfUser.getId() : null;
			if (!note.getIsPrivate()) {
				workChangeLogService.saveWorkChangeLog(
						new WorkNoteCreatedChangeLog(
								note.getWork().getId(), user.getId(), authenticationService.getMasqueradeUserId(), onBehalfOfUserId, note
						)
				);
			}
			userNotificationService.onWorkNoteAdded(note);

			WorkActionRequest workActionRequest = workActionRequestFactory.create(work, user.getId(), onBehalfOfUserId, authenticationService.getMasqueradeUserId(), WorkAuditType.NOTE);
			workAuditService.auditWork(workActionRequest);

			webHookEventService.onNoteAdded(work.getId(), work.getCompany().getId(), note.getId());

			logger.debug(String.format("Added note[id=%d] to work[id=%d, work_number=%s]", note.getId(), work.getId(), work.getWorkNumber()));
		}
	}

	@Override
	public WorkNote addWorkSubStatusTransitionNote(User user, Work work, WorkSubStatusType workSubStatusType, String transitionNote) {
		if (StringUtils.isNotBlank(transitionNote)) {
			Assert.notNull(user);
			Assert.notNull(work);
			Assert.notNull(workSubStatusType);

			String message = "Added the label " + workSubStatusType.getDescription() + ". \n" + transitionNote;

			WorkNote note = new WorkNote(message, work);
			setNotePrivacy(note, user, work, workSubStatusType);

			workNoteDAO.saveOrUpdate(note);
			webHookEventService.onNoteAdded(work.getId(), work.getCompany().getId(), note.getId());
			return note;
		}
		return null;
	}

	@Override
	public WorkNote addNoteToWork(Work work, NoteDTO noteDTO, User onBehalfOfUser) {
		Assert.notNull(noteDTO);
		Assert.hasText(noteDTO.getContent());

		WorkNote note = new WorkNote(noteDTO.getContent(), work);

		setNotePrivacy(note, noteDTO, work.getId());

		noteDAO.saveOrUpdate(note);

		//Metadata
		saveNoteMetadata(note, noteDTO);

		User user = authenticationService.getCurrentUser();
		Long onBehalfOfUserId = (onBehalfOfUser != null) ? onBehalfOfUser.getId() : null;
		if (user != null && (!note.getIsPrivate() || user.getCompany().getId().equals(work.getCompany().getId()))) {
			workChangeLogService.saveWorkChangeLog(
					new WorkNoteCreatedChangeLog(
							work.getId(), user.getId(), authenticationService.getMasqueradeUserId(), onBehalfOfUserId, note
					)
			);
			webHookEventService.onNoteAdded(work.getId(), work.getCompany().getId(), note.getId()); // put this inside here to prevent resource private notes from being exposed
		}

		if (!work.getWorkStatusType().isVoidOrCancelled()) {
			userNotificationService.onWorkNoteAdded(note);
		}

		User currentUser = authenticationService.getCurrentUser();
		if (currentUser == null) {
			currentUser = work.getBuyer();
		}

		Assert.notNull(currentUser);
		WorkActionRequest workActionRequest = workActionRequestFactory.create(work, currentUser.getId(), onBehalfOfUserId, authenticationService.getMasqueradeUserId(), WorkAuditType.NOTE);
		workAuditService.auditAndReindexWork(workActionRequest);

		logger.debug(String.format("Added note[id=%d] to work[id=%d, work_number=%s]", note.getId(), work.getId(), work.getWorkNumber()));
		return note;
	}

	void setNotePrivacy(WorkNote note, NoteDTO noteDTO, long workId) {
		WorkResource workResource = workService.findActiveWorkResource(workId);
		if (!noteDTO.getIsPrivate() && workResource != null) {
			note.setPrivacy(PrivacyType.PRIVILEGED);
			note.setReplyToId(workResource.getUser().getId());
			return;
		}

		if (noteDTO.getIsPrivate()) {
			note.setPrivacy(PrivacyType.PRIVATE);
		} else if (noteDTO.getPrivileged()) {
			note.setPrivacy(PrivacyType.PRIVILEGED);
		} else {
			note.setIsPrivate(noteDTO.getIsPrivate());
		}
	}

	WorkNote setNotePrivacy(WorkNote note, User user, Work work, WorkSubStatusType workSubStatusType) {
		Assert.notNull(note);
		Assert.notNull(user);
		Assert.notNull(work);
		Assert.notNull(workSubStatusType);

		//NOTE: this message was approved by TLars and SteveNH
		//By default all the notes added when submitting a label are PRIVATE
		note.setPrivacy(PrivacyType.PRIVATE);

		boolean isUserAddingTheNoteTheActiveWorker = workService.isUserActiveResourceForWork(user.getId(), work.getId());

		//If the user is the assigned worker, then it's privileged (otherwise they wouldn't be able to see the note)
		if (isUserAddingTheNoteTheActiveWorker) {
			note.setPrivacy(PrivacyType.PRIVILEGED);
			note.setReplyToId(user.getId());
		} else {
			//if is not the worker and the note is required, then we check for the note settings on the label configuration
			if (workSubStatusType.isNoteRequired()) {
				note.setPrivacy(workSubStatusType.getNotePrivacy());

				//If it privileged and there's an active resource, then we share it with the active worker.
				if (note.getIsPrivileged()) {
					WorkResource activeWorkResource = workService.findActiveWorkResource(work.getId());
					if (activeWorkResource != null) {
						note.setReplyToId(activeWorkResource.getUser().getId());
					} else {
						note.setReplyToId(user.getId());
					}
				}
			}
		}
		return note;
	}

	void saveNoteMetadata(WorkNote note) {
		saveNoteMetadata(note, new NoteDTO());
	}

	void saveNoteMetadata(WorkNote note, NoteDTO noteDTO) {
		if (note != null && note.getId() != null && note.getWork() != null && noteDTO != null) {
			if (noteMetadataDAO.findByNoteId(note.getId()) != null) {
				return;
			}
			NoteMetadata noteMetadata = new NoteMetadata();
			noteMetadata.setNoteId(note.getId());
			noteMetadata.setQuestion(noteDTO.isQuestion());
			noteMetadata.setOnBehalfOfUserId(noteDTO.getOnBehalfOfUserId());

			if (noteDTO.hasParent()) {
				Assert.isTrue(!noteDTO.getParentId().equals(note.getId()), "A note can't be parent of itself.");
				WorkNote parent = workNoteDAO.get(noteDTO.getParentId());
				if (parent != null && parent.getWork().getId().equals(note.getWork().getId())) {
					noteMetadata.setParent(parent);
					NoteMetadata parentMetadata = noteMetadataDAO.findByNoteId(parent.getId());
					if (parentMetadata != null) {
						noteMetadata.setLevel(parentMetadata.getLevel() + 1);
					}
				}
			}
			noteMetadataDAO.saveOrUpdate(noteMetadata);


		}
	}

	@Override
	public NotePagination findAllNotesByWorkForCompany(Long workId, Long companyId, NotePagination pagination) {
		Assert.notNull(workId);
		Assert.notNull(companyId);
		Assert.notNull(pagination);

		return noteDAO.findAllNotesByWorkIdForCompany(workId, companyId, pagination);
	}

	@Override
	public Note findNoteById(Long noteId) {
		Assert.notNull(noteId);
		return noteDAO.get(noteId);
	}

	@Override
	public Note saveOrUpdate(Note note) {
		Assert.notNull(note);
		noteDAO.saveOrUpdate(note);
		if (note instanceof WorkNote) {
			//Metadata
			saveNoteMetadata((WorkNote) note);
		}
		return note;
	}
}
