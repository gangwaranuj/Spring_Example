package com.workmarket.dao.asset;

import com.workmarket.dao.DeletableAbstractDAO;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.model.asset.type.WorkAssetAssociationType;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WorkAssetAssociationDAOImpl extends DeletableAbstractDAO<WorkAssetAssociation> implements WorkAssetAssociationDAO {
	protected Class<WorkAssetAssociation> getEntityClass() {
		return WorkAssetAssociation.class;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<WorkAssetAssociation> findByWork(Long workId) {
		return getFactory().getCurrentSession().getNamedQuery("workAssetAssociation.findByWork")
			.setParameter("workId", workId)
			.list();
	}

	@Override
	public WorkAssetAssociation findWorkAssetAssociation(Long workId, Long assetId) {
		return (WorkAssetAssociation) getFactory().getCurrentSession().getNamedQuery("workAssetAssociation.find")
			.setParameter("workId", workId)
			.setParameter("assetId", assetId)
			.setMaxResults(1)
			.uniqueResult();
	}

	@Override
	public List<WorkAssetAssociation> findWorkAssetAssociationsByWork(List<Long> workIds, Long assetId){
		return (List<WorkAssetAssociation>) getFactory().getCurrentSession().getNamedQuery("workAssetAssociation.findBulkByWork")
				.setParameterList("workIds",workIds)
				.setParameter("assetId", assetId)
				.list();
	}

	@Override
	public List<WorkAssetAssociation> findAllAssetAssociationsByWork(List<Long> workIds){
		return (List<WorkAssetAssociation>) getFactory().getCurrentSession().getNamedQuery("workAssetAssociation.findAllBulkByWork")
				.setParameterList("workIds",workIds)
				.list();
	}

	@Override
	public List<WorkAssetAssociation> findAllAssetAssociationsByDeliverableRequirementId(Long deliverableRequirementId) {
		return getFactory().getCurrentSession().getNamedQuery("workAssetAssociation.findByDeliverableRequirement")
			.setParameter("deliverableRequirementId", deliverableRequirementId)
			.list();
	}

	@Override
	public List<WorkAssetAssociation> findAllAssetAssociationsByDeliverableRequirementIdAndPosition(Long workId, Long deliverableRequirementId, Integer position) {
		return getFactory().getCurrentSession().getNamedQuery("workAssetAssociation.findByDeliverableRequirementAndPosition")
			.setParameter("workId", workId)
			.setParameter("deliverableRequirementId", deliverableRequirementId)
			.setParameter("position", position)
			.list();
	}

	@Override
	public List<WorkAssetAssociation> findAllDeliverablesByWork(Long workId) {
		return getFactory().getCurrentSession().getNamedQuery("workAssetAssociation.findAllDeliverablesByWorkId")
			.setParameter("workId", workId)
			.list();
	}
}