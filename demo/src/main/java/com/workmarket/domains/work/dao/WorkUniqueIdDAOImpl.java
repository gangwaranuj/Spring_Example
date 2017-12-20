package com.workmarket.domains.work.dao;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.work.model.WorkUniqueId;
import com.workmarket.service.infra.security.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Calendar;

@Repository
public class WorkUniqueIdDAOImpl extends AbstractDAO<WorkUniqueId> implements WorkUniqueIdDAO  {

	@Autowired
	private SecurityContext securityContext;

	@Override
	protected Class<?> getEntityClass() {
		return WorkUniqueId.class;
	}

	@Override
	public void saveOrUpdate(WorkUniqueId entity) {

		entity.setModifiedOn(Calendar.getInstance());
		entity.setModifierId(securityContext.getCurrentUserId());
		if(entity.getCreatedOn() == null) {
			entity.setCreatedOn(Calendar.getInstance());
			entity.setCreatorId(securityContext.getCurrentUserId());
		}
		super.saveOrUpdate(entity);
	}

	@Override
	public WorkUniqueId getWorkUniqueIdForWork(Long workId) {
		return this.findBy("workId", workId);
	}

	public WorkUniqueId findByCompanyVersionIdValue(Long companyId, int version, String idValue) {
		return findBy("company.id", companyId, "version", version, "idValue", idValue);
	}
}
