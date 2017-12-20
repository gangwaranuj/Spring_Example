package com.workmarket.service.business;

import com.workmarket.dao.asset.AssetDAO;
import com.workmarket.data.report.assessment.AttemptResponseAssetReportPagination;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.AssetCdnUri;
import com.workmarket.domains.model.asset.AssetRemoteUri;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AssetServiceImpl implements AssetService {

	@Resource private AssetDAO assetDao;

	@Override
	public Asset get(long id) {
		return assetDao.get(id);
	}

	@Override
	public Asset get(String uuid) {
		return assetDao.get(uuid);
	}

	@Override
	public void initialize(Asset asset) {
		assetDao.initialize(asset);
	}

	@Override
	public void saveOrUpdate(Asset asset) {
		assetDao.saveOrUpdate(asset);
	}

	@Override
	public List<Asset> get(String... uuids) {
		return assetDao.get(uuids);
	}

	@Override
	public Asset getAssetByUUID(String uuid) {
		return assetDao.findAssetByUUID(uuid);
	}

	@Override
	public Asset getAssetByIdAndCompany(Long assetId, Long companyId) {
		return assetDao.findAssetByIdAndCompany(assetId, companyId);
	}

	@Override
	public int countDeliverableAssetsByDeliverableRequirementId(Long id) {
		return assetDao.countDeliverableAssetsByDeliverableRequirementId(id);
	}

	@Override
	public List<Integer> findDeliverableAssetPositionsByDeliverableRequirementId(Long id) {
		return assetDao.findDeliverableAssetPositionsByDeliverableRequirementId(id);
	}

	@Override
	public void setPositionToWorkAssetAssociationId(Long id) {
		assetDao.setPositionToWorkAssetAssociationId(id);
	}

	@Override
	public boolean authorizeByUserId(long userId, long companyId, long assetId, boolean isAdminOrManager) {
		return assetDao.authorizeByUserId(userId, companyId, assetId, isAdminOrManager);
	}

	@Override
	public AttemptResponseAssetReportPagination getAssessmentAttemptResponseAssets(AttemptResponseAssetReportPagination pagination) {
		return assetDao.findAssessmentAttemptResponseAssets(pagination);
	}

	@Override
	public List<String> getAssessmentAttemptResponseAssetUuidsByAssessment(Long assessmentId) {
		return assetDao.findAssessmentAttemptResponseAssetUuidsByAssessment(assessmentId);
	}

	@Override
	public List<String> getAssessmentAttemptResponseAssetUuidsByAttempt(Long attemptId) {
		return assetDao.findAssessmentAttemptResponseAssetUuidsByAttempt(attemptId);
	}

	@Override
	public AssetCdnUri getDefaultAssetCdnUri() {
		return assetDao.findDefaultAssetCdnUri();
	}

	@Override
	public AssetRemoteUri getDefaultAssetRemoteUri() {
		return assetDao.findDefaultAssetRemoteUri();
	}
}
