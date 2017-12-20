package com.workmarket.domains.work.service.actions.handlers;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.note.WorkNote;
import com.workmarket.domains.work.service.WorkNoteService;
import com.workmarket.domains.work.service.actions.AbstractWorkEvent;
import com.workmarket.domains.work.service.actions.AddNotesWorkEvent;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
public class AddNotesWorkEventHandler implements WorkEventHandler {

	@Autowired WorkNoteService workNoteService;
	@Autowired MessageBundleHelper messageHelper;
	@Autowired EventRouter eventRouter;

	@Override
		public AjaxResponseBuilder handleEvent(AbstractWorkEvent event) {
		Assert.notNull(event);
		Assert.isTrue(event instanceof AddNotesWorkEvent);
		@SuppressWarnings("ConstantConditions") AddNotesWorkEvent addNotesWorkEvent = (AddNotesWorkEvent) event;
		if (!event.isValid()) {
			messageHelper.addMessage(event.getResponse().setSuccessful(false), event.getMessageKey() + ".empty");
			return event.getResponse();
		}

		NoteDTO dto = new NoteDTO();
		dto.setContent(addNotesWorkEvent.getContent());
		dto.setIsPrivate(addNotesWorkEvent.isPrivate());
		Collection<WorkNote> workNotes = workNoteService.bulkAddNoteToWorkList(event.getWorks(), dto, event.getUser());

		if (isNotEmpty(workNotes)) {
			List<Long> workIds = Lists.newArrayListWithExpectedSize(workNotes.size());
			for (WorkNote workNote : workNotes) {
				if (workNote.getWork() != null) {
					workIds.add(workNote.getWork().getId());
				}
			}

			workNoteService.bulkAuditAndNotifyAddNote(workNotes, event.getUser(), event.getOnBehalfOfUser());
			eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workIds));
		} else {
			messageHelper.addMessage(event.getResponse().setSuccessful(false), event.getMessageKey() + ".exception");
		}
		return event.getResponse();
	}


}
