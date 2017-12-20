package com.workmarket.dao;

import com.workmarket.domains.model.VisibilityType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VisibilityTypeDAOImpl extends AbstractDAO<VisibilityType> implements VisibilityTypeDAO {
	protected Class<VisibilityType> getEntityClass() {
		return VisibilityType.class;
	}

	@Override
	public List<VisibilityType> getVisibilityTypes() {
		return (List<VisibilityType>) getFactory().getCurrentSession().getNamedQuery("visibilityType.find").list();
	}


}
