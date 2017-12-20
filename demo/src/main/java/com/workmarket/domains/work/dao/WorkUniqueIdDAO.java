package com.workmarket.domains.work.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.work.model.WorkUniqueId;

public interface WorkUniqueIdDAO extends DAOInterface<WorkUniqueId> {

	WorkUniqueId getWorkUniqueIdForWork(Long workId);

	WorkUniqueId findByCompanyVersionIdValue(Long companyId, int version, String idValue);
}
