package com.workmarket.dao.asset;

import com.workmarket.dao.DeletableDAOInterface;
import com.workmarket.data.report.assessment.AttemptResponseAssetReportPagination;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.AssetCdnUri;
import com.workmarket.domains.model.asset.AssetRemoteUri;

import java.util.List;

public interface AssetDAO extends DeletableDAOInterface<Asset> {

	Asset get(String uuid);

	List<Asset> get(String... uuids);

	Asset findAssetByUUID(String uuid);

	Asset findAssetByIdAndCompany(Long assetId, Long companyId);

	int countDeliverableAssetsByDeliverableRequirementId(Long id);

	List<Integer> findDeliverableAssetPositionsByDeliverableRequirementId(Long id);

	void setPositionToWorkAssetAssociationId(Long deliverableRequirementId);

	boolean authorizeByUserId(Long userId, Long companyId, Long assetId, boolean isAdminOrManager);

	AttemptResponseAssetReportPagination findAssessmentAttemptResponseAssets(AttemptResponseAssetReportPagination pagination);

	List<String> findAssessmentAttemptResponseAssetUuidsByAssessment(Long assessmentId);

	List<String> findAssessmentAttemptResponseAssetUuidsByAttempt(Long attemptId);

	AssetCdnUri findDefaultAssetCdnUri();

	AssetRemoteUri findDefaultAssetRemoteUri();

}
