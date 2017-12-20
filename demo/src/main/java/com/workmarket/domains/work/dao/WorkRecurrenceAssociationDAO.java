package com.workmarket.domains.work.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.WorkRecurrenceAssociation;

public interface WorkRecurrenceAssociationDAO extends DAOInterface<WorkRecurrenceAssociation> {

	void addWorkRecurrence(AbstractWork work, AbstractWork recurringWork, String recurrenceUUID);

	WorkRecurrenceAssociation findWorkRecurrenceAssociation(Long workId);
}
