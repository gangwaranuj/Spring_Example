package com.workmarket.dao.note;

import com.workmarket.dao.DeletableAbstractDAO;
import com.workmarket.domains.model.note.WorkNote;
import org.springframework.stereotype.Repository;

@Repository
public class WorkNoteDAOImpl extends DeletableAbstractDAO<WorkNote> implements WorkNoteDAO {

	protected Class<WorkNote> getEntityClass() {
		return WorkNote.class;
	}

}
