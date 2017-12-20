package com.workmarket.dao.asset;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.AssetPagination;
import com.workmarket.domains.model.asset.CompanyAssetAssociation;
import com.workmarket.domains.model.asset.type.CompanyAssetAssociationType;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CompanyAssetAssociationDAOImpl extends AbstractDAO<CompanyAssetAssociation> implements CompanyAssetAssociationDAO {

	protected Class<CompanyAssetAssociation> getEntityClass() {
        return CompanyAssetAssociation.class;
    }
	
	@Override
	public CompanyAssetAssociation findByCompanyAndAssetId(Long companyId, Long assetId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("entity.id", companyId))
			.add(Restrictions.eq("asset.id", assetId))
			.setMaxResults(1);
	
		return (CompanyAssetAssociation) criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public AssetPagination findAllCSRInternalAssetsByCompany(Long companyId, AssetPagination pagination) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("entity.id", companyId))
			.add(Restrictions.eq("assetType.code", CompanyAssetAssociationType.CLIENT_SERVICES_INTERNAL))
			.createAlias("asset", "asset", Criteria.INNER_JOIN)
			.setProjection(Projections.property("asset"))
			.setFirstResult(pagination.getStartRow())
    		.setMaxResults(pagination.getResultsLimit());
		
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setProjection(Projections.rowCount())	        	
			.add(Restrictions.eq("entity.id", companyId))
			.add(Restrictions.eq("assetType.code", CompanyAssetAssociationType.CLIENT_SERVICES_INTERNAL));
		
		criteria.add(Restrictions.eq("deleted", false));
		count.add(Restrictions.eq("deleted", false));
		
		String sortColumn = "asset.name"; 
		if (pagination.getSortColumn() != null) {
			
			if (pagination.getSortColumn().equals(AssetPagination.SORTS.CREATION_DATE)) {
				sortColumn = "asset.createdOn";
				} else if (pagination.getSortColumn().equals(AssetPagination.SORTS.DESCRIPTION)) {
					sortColumn = "asset.description";
				} else if (pagination.getSortColumn().equals(AssetPagination.SORTS.MODIFICATION_DATE)) {
					sortColumn = "asset.modifiedOn";
				} else if (pagination.getSortColumn().equals(AssetPagination.SORTS.NAME)) {
					sortColumn = "asset.name";
				} else if (pagination.getSortColumn().equals(AssetPagination.SORTS.TYPE)) {
					sortColumn = "asset.mimeType";				
				}
						
			if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
				criteria.addOrder(Order.desc(sortColumn));
			} else {
				criteria.addOrder(Order.asc(sortColumn));
			}
		}
		else 
			criteria.addOrder(Order.desc(sortColumn));
		
		pagination.setResults(criteria.list());
        
        if(count.list().size()> 0)
            pagination.setRowCount(((Long) count.list().get(0)).intValue());
        else
            pagination.setRowCount(0);

        return pagination;
	}
	
	@SuppressWarnings("unchecked")
	private List<CompanyAssetAssociation> findAllActiveCompanyAssetsByCompanyAndType(Long companyId, String assetTypeCode, ApprovalStatus approvalStatus) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.setFetchMode("asset", FetchMode.JOIN)
			.setFetchMode("transformedSmallAsset", FetchMode.JOIN)
			.setFetchMode("transformedLargeAsset", FetchMode.JOIN)
			.add(Restrictions.eq("entity.id", companyId))
			.add(Restrictions.eq("assetType.code", assetTypeCode))
			.add(Restrictions.eq("active", true))
			.add(Restrictions.eq("deleted", false))
			.add(Restrictions.eq("approvalStatus", approvalStatus))
			.addOrder(Order.desc("createdOn"));
			
		return  criteria.list();
	}
	
	@Override 
	public Asset findCompanyAvatarOriginal(Long companyId) {
		List<CompanyAssetAssociation> avatars = findAllActiveCompanyAssetsByCompanyAndType(companyId, CompanyAssetAssociationType.AVATAR, ApprovalStatus.APPROVED);
		if (!avatars.isEmpty()) {
			return avatars.get(0).getAsset();
		}
		return null;
	}

	@Override
	public CompanyAssetAssociation findCompanyAvatars(Long companyId) {		
		List<CompanyAssetAssociation> avatars = findAllActiveCompanyAssetsByCompanyAndType(companyId, CompanyAssetAssociationType.AVATAR, ApprovalStatus.APPROVED);
		if (!avatars.isEmpty()) {
			return avatars.get(0);
		}
		return null;
	}
	
	@Override
	public CompanyAssetAssociation findPreviousCompanyAvatars(Long companyId) {
		List<CompanyAssetAssociation> avatars = findAllActiveCompanyAssetsByCompanyAndType(companyId, CompanyAssetAssociationType.AVATAR, ApprovalStatus.APPROVED);
		if (avatars.size() > 1) {
			return avatars.get(1);
		}
		return null;
	}
}
