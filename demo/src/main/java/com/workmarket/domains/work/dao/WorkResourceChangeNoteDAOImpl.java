package com.workmarket.domains.work.dao;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.work.model.WorkResourceChangeNote;
import org.springframework.stereotype.Component;

@Component
public class WorkResourceChangeNoteDAOImpl extends AbstractDAO<WorkResourceChangeNote> implements WorkResourceChangeNoteDAO {

	@Override
	protected Class<?> getEntityClass() {
		return WorkResourceChangeNote.class;
	}

}