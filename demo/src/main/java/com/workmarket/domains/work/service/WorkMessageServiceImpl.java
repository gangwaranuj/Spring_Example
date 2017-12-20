package com.workmarket.domains.work.service;

import com.workmarket.dao.note.NoteDAO;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.note.WorkMessagePagination;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.utility.CollectionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Author: rocio
 */
@Service
public class WorkMessageServiceImpl implements WorkMessageService {

	@Autowired AuthenticationService authenticationService;
	@Autowired WorkService workService;
	@Autowired WorkNoteService workNoteService;
	@Autowired NoteDAO noteDAO;

	@Override
	public Note addWorkMessage(Long workId, NoteDTO noteDTO) {
		return workNoteService.addNoteToWork(workId, noteDTO);
	}

	@Override
	public Note editWorkMessage(NoteDTO noteDTO) {
		Assert.notNull(noteDTO);
		Assert.hasText(noteDTO.getContent());
		Assert.notNull(noteDTO.getNoteId());

		Note message = noteDAO.get(noteDTO.getNoteId());
		Assert.notNull(message);

		message.setContent(noteDTO.getContent());
		return message;
	}

	@Override
	public WorkMessagePagination findAllMessagesByWork(String workNumber, WorkMessagePagination pagination) {
		Work work = validateWork(workNumber, pagination);
		long currentUserId = authenticationService.getCurrentUserId();

		List<WorkContext> context = workService.getWorkContext(work.getId(), currentUserId);
		if (context != null) {
			if (context.contains(WorkContext.DISPATCHER)) {
				currentUserId = workService.findActiveWorkerId(work.getId());
			}
			boolean shouldDenyMessages = !CollectionUtilities.containsAny(context, WorkContext.OWNER, WorkContext.COMPANY_OWNED, WorkContext.ACTIVE_RESOURCE, WorkContext.DISPATCHER, WorkContext.INVITED, WorkContext.WORK_MARKET_INTERNAL);
			boolean shouldShowPublicAndPrivilegedMessages = CollectionUtilities.containsAny(context, WorkContext.ACTIVE_RESOURCE, WorkContext.INVITED, WorkContext.DISPATCHER);
			return noteDAO.findAllMessagesVisibleToUser(work.getId(), currentUserId, null, pagination, shouldDenyMessages, shouldShowPublicAndPrivilegedMessages);
		}
		return pagination;
	}

	@Override
	public WorkMessagePagination findAllTopMessagesByWork(String workNumber, WorkMessagePagination pagination) {
		Work work = validateWork(workNumber, pagination);
		long currentUSerId = authenticationService.getCurrentUserId();

		List<WorkContext> context = workService.getWorkContext(work.getId(), currentUSerId);
		if (context != null) {
			boolean shouldDenyMessages = !CollectionUtilities.containsAny(context, WorkContext.OWNER, WorkContext.COMPANY_OWNED, WorkContext.ACTIVE_RESOURCE, WorkContext.DISPATCHER, WorkContext.INVITED, WorkContext.WORK_MARKET_INTERNAL);
			boolean shouldShowPublicAndPrivilegedMessages = CollectionUtilities.containsAny(context, WorkContext.ACTIVE_RESOURCE, WorkContext.INVITED, WorkContext.DISPATCHER);
			return noteDAO.findAllTopMessagesVisibleToUser(work.getId(), currentUSerId, pagination, shouldDenyMessages, shouldShowPublicAndPrivilegedMessages);
		}
		return pagination;
	}

	@Override
	public WorkMessagePagination findAllRepliesToMessage(String workNumber, Long parentNoteId, WorkMessagePagination pagination) {
		Work work = validateWork(workNumber, pagination);
		long currentUSerId = authenticationService.getCurrentUserId();

		List<WorkContext> context = workService.getWorkContext(work.getId(), currentUSerId);
		if (context != null) {
			boolean shouldDenyMessages = !CollectionUtilities.containsAny(context, WorkContext.OWNER, WorkContext.COMPANY_OWNED, WorkContext.ACTIVE_RESOURCE, WorkContext.DISPATCHER, WorkContext.INVITED, WorkContext.WORK_MARKET_INTERNAL);
			boolean shouldShowPublicAndPrivilegedMessages = CollectionUtilities.containsAny(context, WorkContext.ACTIVE_RESOURCE, WorkContext.INVITED, WorkContext.DISPATCHER);
			return noteDAO.findAllMessagesVisibleToUser(work.getId(), currentUSerId, parentNoteId, pagination, shouldDenyMessages, shouldShowPublicAndPrivilegedMessages);
		}
		return pagination;
	}

	private Work validateWork(String workNumber, WorkMessagePagination pagination) {
		Assert.hasText(workNumber);
		Assert.notNull(pagination);

		Work work = workService.findWorkByWorkNumber(workNumber);
		Assert.notNull(work);
		return work;
	}
}
