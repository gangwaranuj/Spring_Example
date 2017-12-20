package com.workmarket.dao.note.concern;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.note.concern.Concern;

public interface ConcernDAO extends DAOInterface<Concern> {
	
	Integer countConcerns();
}
