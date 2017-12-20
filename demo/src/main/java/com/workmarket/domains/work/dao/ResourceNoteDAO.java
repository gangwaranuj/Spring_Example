package com.workmarket.domains.work.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.workmarket.thrift.core.Note;
import com.workmarket.thrift.work.ResourceNote;

public interface ResourceNoteDAO {

	Map<Long, List<ResourceNote>> getResourceNotesByResourceIds(
			Collection<Long> resourceIdsFromPage);

	Map<Long, List<ResourceNote>> getResourceNotesByWorkId(Long id);

	List<Note> getResourceNotesForWorkByWorkId(Long id);

}
