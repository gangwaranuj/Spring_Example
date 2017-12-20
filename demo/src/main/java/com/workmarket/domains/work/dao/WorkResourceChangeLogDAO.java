package com.workmarket.domains.work.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.work.model.WorkResourceChangeLog;

import java.util.List;

public interface WorkResourceChangeLogDAO extends DAOInterface<WorkResourceChangeLog> {

	List<WorkResourceChangeLog> findAllWorkResourceChangeLogWithNotesByWorkId(Long workId);

}
