package com.workmarket.dao.asset;

import com.workmarket.dao.DeletableAbstractDAO;
import com.workmarket.domains.model.VisibilityType;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.model.asset.WorkAssetVisibility;
import org.springframework.stereotype.Repository;

/**
 * Created by alejandrosilva on 1/19/15.
 */
@Repository
public class WorkAssetVisibilityDAOImpl extends DeletableAbstractDAO<WorkAssetVisibility> implements WorkAssetVisibilityDAO {
	protected Class<WorkAssetAssociation> getEntityClass() {
		return WorkAssetAssociation.class;
	}

	@Override
	public WorkAssetVisibility findByWorkAssetAssociationId(Long workAssetAssociationId) {
		return ((WorkAssetVisibility) getFactory().getCurrentSession().getNamedQuery("workAssetVisibility.find")
			.setParameter("workAssetAssociationId", workAssetAssociationId)
			.uniqueResult());
	}
}
