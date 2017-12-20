package com.workmarket.service.business;

import com.google.common.collect.ImmutableList;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.AssetPagination;
import com.workmarket.domains.model.asset.CompanyAssetPagination;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.model.banking.BankingIntegrationGenerationRequest;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.ContractVersionAssetDTO;
import com.workmarket.service.business.dto.UploadDTO;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.service.infra.file.FileTransformer;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public interface AssetManagementService {

	Asset findAssetById(Long id);

	Asset findAssetByUuid(String uuid);

	Asset findAssetByIdAndCompany(Long assetId, Long companyId);

	List<WorkAssetAssociation> findAllAssetAssociationsByWork(List<Work> works);

	List<WorkAssetAssociation> findAllAssetAssociationsByWorkId(List<Long> workIds);
	
	WorkAssetAssociation findAssetAssociationsByWorkAndAsset(Long workId, Long assetId);

	List<WorkAssetAssociation> findWorkAssetAssociationsByWork(List<Work> works, Long assetId);

	String getAuthorizedUriByUuid(String uuid) throws HostServiceException;

	String getAuthorizedDownloadUriById(Long id) throws HostServiceException;

	String getAuthorizedDownloadUriByUuid(String uuid) throws HostServiceException;

	String getAuthorizedDownloadUriById(Long id, Calendar expiration) throws HostServiceException;

	String getAuthorizedDownloadUriByUuidForTempUpload(String uuid) throws HostServiceException;

	Asset updateAsset(Long assetId, AssetDTO assetDTO);

	Asset updateAsset(Asset asset);

	Asset storeAsset(UploadDTO uploadDTO) throws HostServiceException;

	Asset storeAsset(AssetDTO assetDTO, Asset asset, boolean deleteFile, FileTransformer... transformers) throws AssetTransformationException, IOException, HostServiceException;

	Asset storeAssetForUser(AssetDTO assetDTO, Long userId) throws HostServiceException, AssetTransformationException, IOException;

	Asset storeAssetForUser(AssetDTO assetDTO, Long userId, boolean deleteFile) throws HostServiceException, AssetTransformationException, IOException;

	Asset storeProfilePhotoAsset(AssetDTO assetDTO) throws AssetTransformationException, IOException, HostServiceException;

	WorkAssetAssociation storeAssetForWork(AssetDTO assetDTO, Long workId) throws HostServiceException, AssetTransformationException, IOException;

	WorkAssetAssociation storeAssetForWork(AssetDTO assetDTO, Long workId, boolean deleteFile) throws HostServiceException, AssetTransformationException, IOException;

	UserAssetAssociation storeProfilePhotoAssetForUser(AssetDTO assetDTO, Long userId) throws HostServiceException, AssetTransformationException, IOException;

	UserAssetAssociation storeSlottedAssetForUser(AssetDTO assetDTO, Long userId, Integer order) throws HostServiceException, AssetTransformationException, IOException;

	Asset storeAssetForContractVersion(ContractVersionAssetDTO contractVersionAssetDTO, Long contractVersionId) throws HostServiceException;

	Asset storeAssetForUserCertification(AssetDTO assetDTO, Long userCertificationAssociationId) throws HostServiceException, AssetTransformationException, IOException;

	Asset storeAssetForUserLicense(AssetDTO assetDTO, Long userLicenseAssociationId) throws HostServiceException, AssetTransformationException, IOException;

	Asset storeAssetForUserInsurance(AssetDTO assetDTO, Long userInsuranceAssociationId) throws HostServiceException, AssetTransformationException, IOException;

	Asset storeAssetForAssessmentItem(AssetDTO assetDTO, Long assessmentItemId) throws HostServiceException, AssetTransformationException, IOException;

	Asset storeAssetForAttemptResponse(AssetDTO assetDTO, Long attemptResponseId) throws HostServiceException, AssetTransformationException, IOException;

	Asset storeAssetForBankingFile(AssetDTO assetDTO, BankingIntegrationGenerationRequest request) throws HostServiceException, AssetTransformationException, IOException;

	Asset storeAssetForCompany(AssetDTO assetDTO, Long companyId, boolean deleteFile) throws HostServiceException, AssetTransformationException, IOException;

	void addAssetToUser(AssetDTO assetDTO, Long userId) throws HostServiceException, AssetTransformationException, IOException;

	Asset addUploadToUser(UploadDTO uploadDTO, Long userId) throws HostServiceException;

	void addAssetToCompany(AssetDTO assetDTOto, Long companyId);

	Asset addUploadToCompany(UploadDTO uploadDTO, Long companyId) throws HostServiceException, AssetTransformationException, IOException;

	void removeAssetFromUser(Long assetId, Long userId);

	void removeAssetFromWork(Long assetId, Long workId);

	void removeAssetFromWork(String Uuid, Long workId);

	void removeAssetFromCompany(Long assetId, Long companyId);

	boolean removeAssetFromCompanyLibrary(Long assetId, Long companyId);

	Asset storeAssetForCompany(AssetDTO assetDTO, Long companyId) throws HostServiceException, AssetTransformationException, IOException;

	CompanyAssetPagination getCompanyLibrary(Long companyId, CompanyAssetPagination pagination);

	AssetPagination findAllCsrInternalAssetsByCompany(Long companyId, AssetPagination pagination);

	Asset getAssetInfo(AssetDTO assetDTO, Long workId);

	WorkAssetAssociation addAssetToWork(AssetDTO assetDTO, Long workId);

	void addSavedAssetToWorks(Asset asset, List<Work> works, String associationType);

	Asset addUploadToWork(UploadDTO uploadDTO, Long workId) throws HostServiceException;

	void bulkRemoveAssetFromWork(List<Work> works, String Uuid);

	void bulkNotifyRemoveAssetFromWork(List<Work> works, String Uuid);

	void setAssetsForWork(AssetDTO[] assetDTOs, Long workId);

	Asset addUploadToTaxVerificationRequest(UploadDTO uploadDTO, Long requestId) throws HostServiceException;

	Asset addUploadToAssessmentItem(UploadDTO uploadDTO, Long assessmentItemId) throws HostServiceException;

	Asset addUploadToAttemptResponse(UploadDTO uploadDTO, Long attemptResponseId) throws HostServiceException, AssetTransformationException, IOException;

	void removeAssetFromAssessmentItem(Long assetId, Long assessmentItemId);

	List<Asset> setAssetsForAssessmentItem(AssetDTO[] assetDTOs, Long itemId);

	void changeUserBackgroundImage(Long id, Asset backgroundImage);


	ImmutableList<Map> getProjectedDocuments(String[] fields) throws Exception;
}
