package com.workmarket.domains.work.service;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.note.NotePagination;
import com.workmarket.domains.model.note.WorkNote;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.service.business.dto.NoteDTO;

import java.util.Collection;
import java.util.List;

public interface WorkNoteService {

	Note addNoteToWork(Long workId, String message);

	Note addNoteToWork(Long workId, NoteDTO noteDTO);

	Note addNoteToWork(Long workId, NoteDTO noteDTO, User onBehalfOfUser);

	Note findNoteById(Long noteId);

	Note saveOrUpdate(Note note);

	NotePagination findAllNotesByWorkForCompany(Long workId, Long companyId, NotePagination pagination);

	Collection<WorkNote> bulkAddNoteToWorkList(List<Work> works, NoteDTO noteDTO, User user);

	void bulkAuditAndNotifyAddNote(Collection<WorkNote> workNotes, User user, User onBehalfOfUser);

	WorkNote addWorkSubStatusTransitionNote(User user, Work work, WorkSubStatusType workSubStatusType, String transitionNote);

	WorkNote addNoteToWork(Work work, NoteDTO noteDTO, User onBehalfOfUser);
}
