package com.workmarket.domains.work.service;

import com.workmarket.domains.model.VisibilityType;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.model.asset.WorkAssetVisibility;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.service.business.dto.AssetDTO;

import java.io.InputStream;

public interface DocumentService {

	WorkAssetAssociation addDocument(String workNumber, AssetDTO assetDTO, InputStream inputStream) throws Exception;

	WorkAssetAssociation addDocument(String workNumber, AssetDTO assetDTO) throws Exception;

	void updateDocumentVisibility(Long workId, Long assetId, String visibilityTypeCode);

	boolean isDocumentVisible(WorkAssetVisibility workAssetVisibility, AbstractWork work);

	boolean isDocumentVisible(VisibilityType visibilityType, AbstractWork work);

}
