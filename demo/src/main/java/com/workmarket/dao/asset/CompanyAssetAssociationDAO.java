package com.workmarket.dao.asset;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.AssetPagination;
import com.workmarket.domains.model.asset.CompanyAssetAssociation;

public interface CompanyAssetAssociationDAO extends DAOInterface<CompanyAssetAssociation> {

	CompanyAssetAssociation findByCompanyAndAssetId(Long companyId, Long assetId);
	
	AssetPagination findAllCSRInternalAssetsByCompany(Long companyId, AssetPagination pagination);

	Asset findCompanyAvatarOriginal(Long companyId);

	CompanyAssetAssociation findCompanyAvatars(Long companyId);
	CompanyAssetAssociation findPreviousCompanyAvatars(Long companyId);
}
