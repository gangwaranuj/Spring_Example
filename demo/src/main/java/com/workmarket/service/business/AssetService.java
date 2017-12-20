package com.workmarket.service.business;

import com.workmarket.data.report.assessment.AttemptResponseAssetReportPagination;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.AssetCdnUri;
import com.workmarket.domains.model.asset.AssetRemoteUri;

import java.util.List;

public interface AssetService {

	Asset get(long id);

	Asset get(String uuid);

	void initialize(Asset asset);

	void saveOrUpdate(Asset asset);

	List<Asset> get(String... uuids);

	Asset getAssetByUUID(String uuid);

	Asset getAssetByIdAndCompany(Long assetId, Long companyId);

	int countDeliverableAssetsByDeliverableRequirementId(Long id);

	List<Integer> findDeliverableAssetPositionsByDeliverableRequirementId(Long id);

	void setPositionToWorkAssetAssociationId(Long id);

	boolean authorizeByUserId(long userId, long companyId, long assetId, boolean isAdminOrManager);

	AttemptResponseAssetReportPagination getAssessmentAttemptResponseAssets(AttemptResponseAssetReportPagination pagination);

	List<String> getAssessmentAttemptResponseAssetUuidsByAssessment(Long assessmentId);

	List<String> getAssessmentAttemptResponseAssetUuidsByAttempt(Long attemptId);

	AssetCdnUri getDefaultAssetCdnUri();

	AssetRemoteUri getDefaultAssetRemoteUri();

}
