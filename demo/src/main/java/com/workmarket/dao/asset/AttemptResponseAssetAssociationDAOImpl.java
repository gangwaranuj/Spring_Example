package com.workmarket.dao.asset;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.asset.AttemptResponseAssetAssociation;

@Repository
public class AttemptResponseAssetAssociationDAOImpl extends AbstractDAO<AttemptResponseAssetAssociation> implements AttemptResponseAssetAssociationDAO {
	protected Class<AttemptResponseAssetAssociation> getEntityClass() {
		return AttemptResponseAssetAssociation.class;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<AttemptResponseAssetAssociation> findByAttemptResponse(Long attemptResponseId) {
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
			.createAlias("asset", "asset", Criteria.INNER_JOIN)
			.add(Restrictions.eq("entity.id", attemptResponseId))
			.add(Restrictions.eq("deleted", false))
			.list();
	}
	
	@Override
	public AttemptResponseAssetAssociation findLatestByAsset(Long assetId) {
		return (AttemptResponseAssetAssociation) getFactory().getCurrentSession().createCriteria(getEntityClass())
			.createAlias("asset", "asset", Criteria.INNER_JOIN)
			.add(Restrictions.eq("asset.id", assetId))
			.addOrder(Order.desc("id"))
			.setMaxResults(1)
			.uniqueResult();
	}
}