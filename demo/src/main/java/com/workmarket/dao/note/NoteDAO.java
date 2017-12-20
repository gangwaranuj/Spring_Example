package com.workmarket.dao.note;

import com.workmarket.dao.DeletableDAOInterface;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.note.NotePagination;
import com.workmarket.domains.model.note.WorkMessagePagination;
import com.workmarket.domains.model.note.concern.Concern;
import com.workmarket.domains.model.note.concern.ConcernPagination;

public interface NoteDAO extends DeletableDAOInterface<Note> {

	NotePagination findAllNotesByWorkIdForCompany(Long workId, Long companyId, NotePagination pagination);

	ConcernPagination findAllConcerns(Class<? extends Concern> clazz, ConcernPagination pagination);

	<T extends Note> T findById(Class<? extends Note> clazz, Long id);


	/** MESSAGES **/
	WorkMessagePagination findAllTopMessagesVisibleToUser(Long workId, Long userId, WorkMessagePagination pagination, boolean shouldDenyMessages, Boolean shouldShowPublicAndPrivilegedMessages);

	WorkMessagePagination findAllMessagesVisibleToUser(Long workId, Long userId, Long parentNoteId, WorkMessagePagination pagination, boolean shouldDenyMessages, Boolean shouldShowPublicAndPrivilegedMessages);

}


