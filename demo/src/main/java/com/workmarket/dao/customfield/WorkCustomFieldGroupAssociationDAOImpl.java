package com.workmarket.dao.customfield;

import com.google.common.collect.Sets;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroupAssociation;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

@Repository
public class WorkCustomFieldGroupAssociationDAOImpl extends AbstractDAO<WorkCustomFieldGroupAssociation> implements WorkCustomFieldGroupAssociationDAO {
	protected Class<WorkCustomFieldGroupAssociation> getEntityClass() {
		return WorkCustomFieldGroupAssociation.class;
	}
	
	@Override
	public WorkCustomFieldGroupAssociation findByWorkAndWorkCustomFieldGroup(Long workId, Long customFieldGroupId) {
		return (WorkCustomFieldGroupAssociation)getFactory().getCurrentSession().getNamedQuery("workCustomFieldGroupAssociation.byWorkAndWorkCustomFieldGroup")
			.setLong("work_id", workId)
			.setLong("work_custom_field_group_id", customFieldGroupId)
			.uniqueResult();
	}

	@Override
	public WorkCustomFieldGroupAssociation findByWorkAndWorkCustomFieldGroupPosition(Long workId, Integer position) {
		return (WorkCustomFieldGroupAssociation)getFactory().getCurrentSession().getNamedQuery("workCustomFieldGroupAssociation.byWorkAndWorkCustomFieldGroupPosition")
				.setLong("work_id", workId)
				.setLong("position", position)
				.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<WorkCustomFieldGroupAssociation> findAllActiveByWork(Long workId) {
		return getFactory().getCurrentSession().getNamedQuery("workCustomFieldGroupAssociation.byWork")
			.setLong("work_id", workId)
			.list();
	}

	@Override public Set<WorkCustomFieldGroupAssociation> findAllByWork(Long workId) {
		return Sets.newHashSet(getFactory().getCurrentSession().getNamedQuery("workCustomFieldGroupAssociation.findAllByWork")
				.setLong("work_id", workId)
				.list());
	}
}