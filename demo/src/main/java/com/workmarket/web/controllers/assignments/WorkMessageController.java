package com.workmarket.web.controllers.assignments;

import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.note.NotePagination;
import com.workmarket.domains.model.note.WorkMessagePagination;
import com.workmarket.domains.model.note.WorkNote;
import com.workmarket.domains.work.service.WorkMessageService;
import com.workmarket.service.business.dto.NoteDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/assignments")
public class WorkMessageController extends BaseWorkController {

	private static final Logger logger = LoggerFactory.getLogger(WorkDetailsController.class);

	@Autowired private WorkMessageService workMessageService;

	@RequestMapping(
		value = "/{workNumber}/messages",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody NotePagination getMessages(@PathVariable String workNumber) {
		WorkMessagePagination messages = new WorkMessagePagination();
		messages = workMessageService.findAllMessagesByWork(workNumber, messages);

		return maskUserIds(messages);
	}

	@RequestMapping(
		value = "/{workNumber}/messages",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	@PreAuthorize("!principal.isMasquerading()")
	public @ResponseBody Note addMessages(
		@PathVariable String workNumber,
		@RequestBody NoteDTO message,
		HttpServletResponse response) {

		Long workId = workService.findWorkId(workNumber);
		Note newMessage = null;

		if (workId != null) {
			try {
				newMessage = removeWork(maskUserIds(workMessageService.addWorkMessage(workId, message)));
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				logger.error(String.format("Error saving message %s: ", message.getContent()), e);
			}
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			logger.error(String.format("Error fetching workId for assignment %s", workNumber));
		}

		return newMessage;
	}

	@RequestMapping(
		value = "/{workNumber}/messages/{messageId}",
		method = { RequestMethod.PUT, RequestMethod.PATCH },
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Note updateMessage(
		@PathVariable String workNumber,
		@PathVariable Long messageId,
		@RequestBody Note message,
		HttpServletResponse response) {

		Long workId = workService.findWorkId(workNumber);
		Note newMessage = null;

		if (workId != null) {
			try {
				NoteDTO noteDTO = new NoteDTO();
				noteDTO.setNoteId(messageId).setContent(message.getContent());
				newMessage = maskUserIds(workMessageService.editWorkMessage(noteDTO));
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				logger.error(String.format("Error saving message %s: ", message.getContent()), e);
			}
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			logger.error(String.format("Error fetching workId for assignment %s", workNumber));
		}

		return newMessage;
	}

	private Note removeWork(Note note) {
		if (note != null) {
			((WorkNote) note).setWork(null);
		}
		return note;
	}

	/* Note: We can't expose userIds to the client, so we are fetching the userNumber, typecasting it
	 * to a Long, and setting it as the creatorId before we return the object to the client.
	 * We need to build an editor to do the type conversion automagically with Spring.
	 */
	private NotePagination maskUserIds(NotePagination notes) {
		for (Note note : notes.getResults()) {
			maskUserIds(note);
		}

		return notes;
	}

	private Note maskUserIds(Note note) {
		if (note != null) {
			// Add the creator's user number
			if (isBlank(note.getCreatorNumber())) {
				note.setCreatorNumber(userService.findUserNumber(note.getCreatorId()));
			}
			if (isBlank(note.getModifierNumber())) {
				note.setModifierNumber(userService.findUserNumber(note.getModifierId()));
			}
			// Mask the creator's user id
			note.setCreatorId(null);
			note.setModifierId(null);
		}
		return note;
	}

}
