package com.workmarket.dao.asset;

import com.workmarket.dao.DeletableDAOInterface;
import com.workmarket.domains.model.asset.CompanyAssetLibraryAssociation;
import com.workmarket.domains.model.asset.CompanyAssetPagination;

public interface CompanyAssetLibraryAssociationDAO extends DeletableDAOInterface<CompanyAssetLibraryAssociation> {
	
	CompanyAssetLibraryAssociation findByCompanyAndAssetId(Long companyId, Long assetId);

	CompanyAssetPagination findAllAssetsByCompany(Long companyId, CompanyAssetPagination pagination);
}
