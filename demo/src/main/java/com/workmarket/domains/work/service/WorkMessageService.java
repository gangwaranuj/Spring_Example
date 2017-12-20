package com.workmarket.domains.work.service;

import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.note.WorkMessagePagination;
import com.workmarket.service.business.dto.NoteDTO;

/**
 * Author: rocio
 */
public interface WorkMessageService {

	Note addWorkMessage(Long workId, NoteDTO noteDTO);

	Note editWorkMessage(NoteDTO noteDTO);

	/**
	 * Returns all Messages for a specific assignment.
	 *
	 * @param workNumber
	 * @param pagination
	 * @return WorkMessagePagination
	 */
	WorkMessagePagination findAllMessagesByWork(String workNumber, WorkMessagePagination pagination);

	/**
	 * Returns only top level messages.
	 *
	 * @param workNumber
	 * @param pagination
	 * @return WorkMessagePagination
	 */
	WorkMessagePagination findAllTopMessagesByWork(String workNumber, WorkMessagePagination pagination);

	/**
	 * Given an assignment, find all the replies to that message.
	 *
	 * @param workNumber
	 * @param parentNoteId
	 * @param pagination
	 * @return WorkMessagePagination
	 */
	WorkMessagePagination findAllRepliesToMessage(String workNumber, Long parentNoteId, WorkMessagePagination pagination);

}
