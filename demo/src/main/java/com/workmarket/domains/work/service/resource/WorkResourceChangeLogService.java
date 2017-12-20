package com.workmarket.domains.work.service.resource;

import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkResourceChangeLog;
import com.workmarket.service.exception.InvalidParameterException;
import com.workmarket.thrift.core.Note;
import com.workmarket.thrift.work.DeclineWorkOfferRequest;
import com.workmarket.thrift.work.ResourceNote;
import com.workmarket.thrift.work.ResourceNoteRequest;

import java.util.List;
import java.util.Map;

public interface WorkResourceChangeLogService {

	void declineWorkSuccess(DeclineWorkOfferRequest request, User user,
			User onBehalfOfUser, Work work, User masqueradeUser) throws InvalidParameterException;

	void resourceNoteSuccess(ResourceNoteRequest request) throws InvalidParameterException;

	Map<Long, List<ResourceNote>> findResourceNotesByWorkId(Long id);

	List<Note> findNotesByResourceByWorkId(Long workId);

}
