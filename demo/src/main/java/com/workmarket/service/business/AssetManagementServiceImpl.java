package com.workmarket.service.business;

import com.amazonaws.services.s3.internal.RepeatableFileInputStream;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.LookupEntityDAO;
import com.workmarket.dao.UserDAO;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.dao.assessment.AbstractItemDAO;
import com.workmarket.dao.assessment.AttemptResponseDAO;
import com.workmarket.dao.asset.AttemptResponseAssetAssociationDAO;
import com.workmarket.dao.asset.CompanyAssetAssociationDAO;
import com.workmarket.dao.asset.CompanyAssetLibraryAssociationDAO;
import com.workmarket.dao.asset.ContractVersionAssetAssociationDAO;
import com.workmarket.dao.asset.TaxVerificationRequestAssetAssociationDAO;
import com.workmarket.dao.asset.UserAssetAssociationDAO;
import com.workmarket.dao.asset.WorkAssetAssociationDAO;
import com.workmarket.dao.asset.WorkAssetVisibilityDAO;
import com.workmarket.dao.certification.UserCertificationAssociationDAO;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.contract.ContractVersionDAO;
import com.workmarket.dao.insurance.UserInsuranceAssociationDAO;
import com.workmarket.dao.license.UserLicenseAssociationDAO;
import com.workmarket.dao.tax.TaxVerificationRequestDAO;
import com.workmarket.dao.upload.UploadDAO;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.AvailabilityType;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.ProfileModificationType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VisibilityType;
import com.workmarket.domains.model.assessment.AbstractItem;
import com.workmarket.domains.model.assessment.AttemptResponse;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.AssetPagination;
import com.workmarket.domains.model.asset.AttemptResponseAssetAssociation;
import com.workmarket.domains.model.asset.CompanyAssetAssociation;
import com.workmarket.domains.model.asset.CompanyAssetLibraryAssociation;
import com.workmarket.domains.model.asset.CompanyAssetPagination;
import com.workmarket.domains.model.asset.ContractVersionAssetAssociation;
import com.workmarket.domains.model.asset.TaxVerificationRequestAssetAssociation;
import com.workmarket.domains.model.asset.Upload;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.model.asset.WorkAssetVisibility;
import com.workmarket.domains.model.asset.type.AssetType;
import com.workmarket.domains.model.asset.type.AttemptResponseAssetAssociationType;
import com.workmarket.domains.model.asset.type.CompanyAssetAssociationType;
import com.workmarket.domains.model.asset.type.TaxVerificationRequestAssetAssociationType;
import com.workmarket.domains.model.asset.type.UserAssetAssociationType;
import com.workmarket.domains.model.asset.type.WorkAssetAssociationType;
import com.workmarket.domains.model.banking.BankingIntegrationGenerationRequest;
import com.workmarket.domains.model.certification.UserCertificationAssociation;
import com.workmarket.domains.model.contract.ContractVersion;
import com.workmarket.domains.model.insurance.UserInsuranceAssociation;
import com.workmarket.domains.model.license.UserLicenseAssociation;
import com.workmarket.domains.model.tax.TaxVerificationRequest;
import com.workmarket.domains.work.dao.BaseWorkDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.ContractVersionAssetDTO;
import com.workmarket.service.business.dto.UploadDTO;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.integration.hooks.webhook.WebHookEventService;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.AuthorizationService;
import com.workmarket.service.infra.file.FileTransformer;
import com.workmarket.service.infra.file.FileTransformerFactory;
import com.workmarket.service.infra.file.FileTransformerFactoryImpl.ImageBatchTransformer;
import com.workmarket.service.infra.file.RemoteFile;
import com.workmarket.service.infra.file.RemoteFileAdapter;
import com.workmarket.service.infra.file.RemoteFileType;
import com.workmarket.service.infra.jms.JmsService;
import com.workmarket.service.infra.security.RequestContext;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.FileUtilities;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.utility.ProjectionUtilities;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.workmarket.domains.model.Pagination.SORT_DIRECTION.ASC;
import static com.workmarket.domains.model.asset.CompanyAssetPagination.SORTS.NAME;

@Service
public class AssetManagementServiceImpl implements AssetManagementService {

	private static final Log logger = LogFactory.getLog(AssetManagementServiceImpl.class);

	@Autowired private RemoteFileAdapter remoteFileAdapter;
	@Autowired private FileTransformerFactory fileTransformerFactory;
	@Autowired private EventFactory eventFactory;

	@Autowired private AssetService assetService;
	@Autowired private UserDAO userDAO;
	@Autowired private CompanyDAO companyDAO;
	@Autowired private BaseWorkDAO abstractWorkDAO;
	@Autowired private UserAssetAssociationDAO userAssetAssociationDAO;
	@Autowired private WorkAssetAssociationDAO workAssetAssociationDAO;
	@Autowired private CompanyAssetLibraryAssociationDAO companyAssetLibraryAssociationDAO;
	@Autowired private CompanyAssetAssociationDAO companyAssetAssociationDAO;
	@Autowired private AttemptResponseAssetAssociationDAO attemptResponseAssetAssociationDAO;
	@Autowired private UploadDAO uploadDAO;
	@Autowired private TaxVerificationRequestDAO taxVerificationRequestDAO;
	@Autowired private TaxVerificationRequestAssetAssociationDAO taxVerificationRequestAssetAssociationDAO;
	@Autowired private ContractVersionDAO contractVersionDAO;
	@Autowired private ContractVersionAssetAssociationDAO contractVersionAssetAssociationDAO;
	@Autowired private UserCertificationAssociationDAO userCertificationAssociationDAO;
	@Autowired private UserLicenseAssociationDAO userLicenseAssociationDAO;
	@Autowired private UserInsuranceAssociationDAO userInsuranceAssociationDAO;
	@Autowired private AbstractItemDAO abstractItemDAO;
	@Autowired private AttemptResponseDAO attemptResponseDAO;

	@Autowired private AuthenticationService authenticationService;
	@Autowired private AuthorizationService authorizationService;
	@Autowired private ProfileService profileService;
	@Autowired private JmsService jmsService;
	@Autowired private UserIndexer userIndexer;
	@Autowired private UserService userService;
	@Autowired private WebHookEventService webHookEventService;
	@Autowired private WorkAssetVisibilityDAO workAssetVisibilityDAO;
	@Autowired private LookupEntityDAO lookupEntityDAO;
	@Autowired protected FeatureEvaluator featureEvaluator;
	@Autowired private UserRoleService userRoleService;

	private static Set<String> publicAssetTypes = Sets.newHashSet(
		UserAssetAssociationType.AVATAR,
		UserAssetAssociationType.BACKGROUND_IMAGE,
		CompanyAssetAssociationType.AVATAR,
		CompanyAssetAssociationType.RECRUITING_CAMPAIGN_LOGO,
		CompanyAssetAssociationType.INVITATION_COMPANY_LOGO
	);

	private void verifyStoreAsset(AssetDTO assetDTO) {
		Assert.notNull(assetDTO);
		Assert.notNull(assetDTO.getSourceFilePath());
		Assert.notNull(assetDTO.getName());
		Assert.notNull(assetDTO.getAssociationType());
	}

	private void verifyAddAsset(AssetDTO assetDTO) {
		Assert.notNull(assetDTO);
		Assert.notNull(assetDTO.getAssetId());
		Assert.notNull(assetDTO.getAssociationType());
	}

	private void verifyAddUpload(UploadDTO uploadDTO) {
		Assert.notNull(uploadDTO);
		Assert.state(uploadDTO.getUploadId() != null || uploadDTO.getUploadUuid() != null);
		Assert.notNull(uploadDTO.getAssociationType());
	}

	@Override
	public Asset findAssetById(Long id) {
		Assert.notNull(id);
		return assetService.get(id);
	}

	@Override
	public Asset findAssetByUuid(String uuid) {
		Assert.hasText(uuid);
		return assetService.getAssetByUUID(uuid);
	}

	@Override
	public Asset findAssetByIdAndCompany(Long assetId, Long companyId) {
		Assert.notNull(assetId);
		Assert.notNull(companyId);
		return assetService.getAssetByIdAndCompany(assetId, companyId);
	}

	@Override
	public List<WorkAssetAssociation> findAllAssetAssociationsByWork(List<Work> works) {
		Assert.notNull(works);
		List<Long> workIds = Lists.newArrayList();
		for (Work work : works) {
			workIds.add(work.getId());
		}
		return workAssetAssociationDAO.findAllAssetAssociationsByWork(workIds);
	}

  @Override
  public List<WorkAssetAssociation> findAllAssetAssociationsByWorkId(List<Long> workIds) {
		return workAssetAssociationDAO.findAllAssetAssociationsByWork(workIds);
  }

  @Override
	public WorkAssetAssociation findAssetAssociationsByWorkAndAsset(Long workId, Long assetId) {
		Assert.notNull(workId);
		Assert.notNull(assetId);

		return workAssetAssociationDAO.findWorkAssetAssociation(workId, assetId);
	}

	@Override
	public String getAuthorizedUriByUuid(String uuid) throws HostServiceException {
		return getAuthorizedUri(assetService.getAssetByUUID(uuid), false, null);
	}

	@Override
	public String getAuthorizedDownloadUriById(Long id) throws HostServiceException {
		Calendar expiration = DateUtilities.getCalendarNow();
		expiration.add(Calendar.HOUR, Constants.ASSET_BUNDLE_EXPIRATION_HOURS);
		return getAuthorizedUri(assetService.get(id), true, expiration);
	}

	@Override
	public String getAuthorizedDownloadUriByUuid(String uuid) throws HostServiceException {
		return getAuthorizedUri(assetService.getAssetByUUID(uuid), true, null);
	}

	@Override
	public String getAuthorizedDownloadUriByUuidForTempUpload(String uuid) throws HostServiceException {
		return getAuthorizedUri(uploadDAO.findUploadByUUID(uuid), true, null);
	}

	@Override
	public String getAuthorizedDownloadUriById(Long id, Calendar expiration) throws HostServiceException {
		return getAuthorizedUri(assetService.get(id), true, expiration);
	}

	private String getAuthorizedUri(Asset asset, boolean isDownload, Calendar expiration) throws HostServiceException {
		if (asset == null) {
			return null;
		}

		if (!isAuthorizedForAsset(asset)) {
			return null;
		}

		RemoteFileType type = asset.getAvailability().hasGuestAvailability() ? RemoteFileType.PUBLIC : RemoteFileType.PRIVATE;
		Date expirationDate = (expiration != null) ? expiration.getTime() : null;

		try {
			URL uri = remoteFileAdapter.getAuthorizedURL(type, asset.getUUID(), expirationDate, CollectionUtilities.newStringMap(
				"response-content-type", asset.getMimeType(),
				"response-content-disposition", String.format("%s; filename=%s", isDownload ? "attachment" : "inline", asset.getName())
			));

			return uri == null ? null : uri.toString();
		} catch (HostServiceException e) {
			logger.error(String.format("Unable to get authorized URI for asset [id => %d, uuid => %s]", asset.getId(), asset.getUUID()), e);
			throw e;
		}
	}

	private String getAuthorizedUri(Upload upload, boolean isDownload, Calendar expiration) throws HostServiceException {
		if (upload == null) {
			return null;
		}

		RemoteFileType type = RemoteFileType.TMP;
		Date expirationDate = (expiration != null) ? expiration.getTime() : null;

		try {
			URL uri = remoteFileAdapter.getAuthorizedURL(type, upload.getUUID(), expirationDate, CollectionUtilities.newStringMap(
				"response-content-type", upload.getMimeType(),
				"response-content-disposition", String.format("%s; filename=%s", isDownload ? "attachment" : "inline", upload.getFilename())
			));

			return uri == null ? null : uri.toString();
		} catch (HostServiceException e) {
			logger.error(String.format("Unable to get authorized URI for asset [id => %d, uuid => %s]", upload.getId(), upload.getUUID()), e);
			throw e;
		}
	}

	private boolean isAuthorizedForAsset(Asset asset) {
		Assert.notNull(asset);

		final User currentUser = authenticationService.getCurrentUserWithFallback();
		if (authenticationService.isSystemUser(currentUser))
			return true;
		if (asset.getAvailability().hasGroupAvailability())
			return true;
		if (asset.getAvailability().hasGuestAvailability())
			return true;
		if (asset.getAvailability().hasPublicAvailability())
			return true;
		List<RequestContext> contexts = getRequestContext(asset);
		if (CollectionUtilities.contains(contexts, RequestContext.OWNER, RequestContext.COMPANY_OWNED))
			return true;
		if (asset.getAvailability().hasWorkerPoolAvailability() && CollectionUtilities.contains(contexts, RequestContext.WORKER_POOL))
			return true;

		return assetService.authorizeByUserId(currentUser.getId(), currentUser.getCompany().getId(), asset.getId(), userRoleService.isAdminOrManager(currentUser));
	}

	private List<RequestContext> getRequestContext(Asset asset) {
		// FIXME Should we be using something other than the audit fields here?

		User currentUser = authenticationService.getCurrentUser();
		User assetUser = userService.getUser(asset.getCreatorId());
		Company assetCompany = assetUser.getCompany();

		return authorizationService.getEntityRequestContexts(currentUser, assetUser, assetCompany);
	}

	@Override
	public Asset updateAsset(Long assetId, AssetDTO assetDTO) {
		Asset asset = assetService.get(assetId);
		if (assetDTO.getName() != null) {
			asset.setName(assetDTO.getName());
		}
		if (assetDTO.getDescription() != null) {
			asset.setDescription(assetDTO.getDescription());
		}

		// Only allow changing the degree of privacy.
		AvailabilityType newAvailability = new AvailabilityType(assetDTO.getAvailabilityTypeCode());
		if (!asset.getAvailability().hasGuestAvailability() && !newAvailability.hasGuestAvailability()) {
			asset.setAvailability(new AvailabilityType(assetDTO.getAvailabilityTypeCode()));
		}

		return asset;
	}

	@Override
	public Asset updateAsset(Asset asset) {
		assetService.saveOrUpdate(asset);
		return asset;
	}

	@Override
	public UserAssetAssociation storeSlottedAssetForUser(AssetDTO assetDTO, Long userId, Integer order) throws HostServiceException, AssetTransformationException, IOException {
		verifyStoreAsset(assetDTO);
		Assert.notNull(userId);
		User user = authenticationService.getCurrentUser();
		List<UserAssetAssociation> assetAssociation = userAssetAssociationDAO.findAllActiveUserAssetsByUserAndType(user.getId(), UserAssetAssociationType.PROFILE_IMAGE);
		for (UserAssetAssociation assetassoc : assetAssociation) {
			if (assetassoc.getAsset().getOrder() == null) {
				continue;
			}
			if (assetassoc.getAsset().getOrder().equals(order)) {
				removeAssetFromUser(assetassoc.getAsset().getId(), user.getId());
			}

		}
		return storeProfilePhotoAssetForUser(assetDTO, userId);
	}

	@Override
	public Asset storeAssetForUser(AssetDTO assetDTO, Long userId) throws HostServiceException, AssetTransformationException, IOException {
		return storeAssetForUser(assetDTO, userId, true);
	}

	@Override
	public Asset storeAssetForUser(AssetDTO assetDTO, Long userId, boolean deleteFile) throws HostServiceException, AssetTransformationException, IOException {
		verifyStoreAsset(assetDTO);

		Assert.isTrue(UserAssetAssociationType.TYPES.contains(assetDTO.getAssociationType()), "Invalid asset type code");

		User user = userDAO.get(userId);
		Assert.notNull(user);

		Asset originalAsset = storeAsset(assetDTO, null, deleteFile);
		Asset largeAsset = null;
		Asset smallAsset = null;

		if (assetDTO.isLargeTransformation()) {
			largeAsset = storeAsset(assetDTO, fileTransformerFactory.newImageThumbnailLargeTransformer());
		}
		if (assetDTO.isSmallTransformation()) {
			smallAsset = storeAsset(assetDTO, fileTransformerFactory.newImageThumbnailSmallTransformer());
		}

		if (deleteFile) {
			FileUtils.deleteQuietly(new File(assetDTO.getSourceFilePath()));
		}

		UserAssetAssociation a = new UserAssetAssociation(user, originalAsset, new UserAssetAssociationType(assetDTO.getAssociationType()));
		a.setTransformedLargeAsset(largeAsset);
		a.setTransformedSmallAsset(smallAsset);

		if (assetDTO.getAssociationType().equals(UserAssetAssociationType.AVATAR)) {
			profileService.registerUserProfileModification(userId, new ProfileModificationType(ProfileModificationType.USER_AVATAR));
		} else if (assetDTO.getAssociationType().equals(UserAssetAssociationType.RESUME)) {
			profileService.registerUserProfileModification(userId, new ProfileModificationType(ProfileModificationType.RESUME));
		}

		userAssetAssociationDAO.saveOrUpdate(a);

		if (assetDTO.isAddToCompanyLibrary()) {
			addAssetToCompanyLibrary(originalAsset.getId(), user.getCompany().getId());
		}

		if (assetDTO.getAssociationType().equals(UserAssetAssociationType.AVATAR) || assetDTO.getAssociationType().equals(UserAssetAssociationType.PROFILE_VIDEO)) {
			userIndexer.reindexById(userId);
		}

		return originalAsset;
	}

	@Override
	public UserAssetAssociation storeProfilePhotoAssetForUser(AssetDTO assetDTO, Long userId) throws HostServiceException, AssetTransformationException, IOException {
		verifyStoreAsset(assetDTO);

		Assert.isTrue(UserAssetAssociationType.TYPES.contains(assetDTO.getAssociationType()), "Invalid asset type code");

		User user = userDAO.get(userId);
		Assert.notNull(user);

		Asset originalAsset = storeProfilePhotoAsset(assetDTO);
		Asset largeAsset = null;
		Asset smallAsset = null;

		if (assetDTO.isLargeTransformation()) {
			largeAsset = storeAsset(assetDTO, fileTransformerFactory.newImageThumbnailLargeTransformer());
		}
		if (assetDTO.isSmallTransformation()) {
			smallAsset = storeAsset(assetDTO, fileTransformerFactory.newImageThumbnailSmallTransformer());
		}

		FileUtils.deleteQuietly(new File(assetDTO.getSourceFilePath()));

		UserAssetAssociation a = new UserAssetAssociation(user, originalAsset, new UserAssetAssociationType(assetDTO.getAssociationType()));
		a.setTransformedLargeAsset(largeAsset);
		a.setTransformedSmallAsset(smallAsset);
		a.setDeleted(true);
		userAssetAssociationDAO.saveOrUpdate(a);

		if (assetDTO.isAddToCompanyLibrary()) {
			addAssetToCompanyLibrary(originalAsset.getId(), user.getCompany().getId());
		}

		if (assetDTO.getAssociationType().equals(UserAssetAssociationType.PROFILE_VIDEO)) {
			userIndexer.reindexById(userId);
		}

		return a;
	}

	@Override
	public void addAssetToUser(AssetDTO assetDTO, Long userId) throws HostServiceException, AssetTransformationException, IOException {
		verifyAddAsset(assetDTO);

		Assert.isTrue(UserAssetAssociationType.TYPES.contains(assetDTO.getAssociationType()), "Invalid asset type code");

		User user = userDAO.get(userId);
		Assert.notNull(user);
		Asset asset = assetService.get(assetDTO.getAssetId());
		Assert.notNull(asset);

		// If transform parameters were provided, we retrieve the remote file
		// and create a new instance of the asset with the transformation applied.

		if (assetDTO.requiresTransformations()) {
			RemoteFileType type = asset.getAvailability().hasGuestAvailability() ? RemoteFileType.PUBLIC : RemoteFileType.PRIVATE;
			File file = remoteFileAdapter.getFile(type, asset.getUUID());
			assetDTO.setSourceFilePath(file.getPath());
			storeAssetForUser(assetDTO, userId);
			return;
		}

		UserAssetAssociation a = new UserAssetAssociation(user, asset, new UserAssetAssociationType(assetDTO.getAssociationType()));
		userAssetAssociationDAO.saveOrUpdate(a);

		if (assetDTO.isAddToCompanyLibrary()) {
			addAssetToCompanyLibrary(asset.getId(), user.getCompany().getId());
		}
	}

	@Override
	public Asset addUploadToUser(UploadDTO uploadDTO, Long userId) throws HostServiceException {
		Assert.notNull(userId);
		verifyAddUpload(uploadDTO);

		User user = userDAO.get(userId);
		Assert.notNull(user);

		Asset asset = storeAsset(uploadDTO);

		UserAssetAssociation a = new UserAssetAssociation(user, asset, new UserAssetAssociationType(uploadDTO.getAssociationType()));
		userAssetAssociationDAO.saveOrUpdate(a);

		if (uploadDTO.getAssociationType().equals(UserAssetAssociationType.AVATAR)) {
			userIndexer.reindexById(userId);
		}

		return asset;
	}

	@Override
	public void removeAssetFromUser(Long assetId, Long userId) {
		UserAssetAssociation a = userAssetAssociationDAO.findUserAssetAssociation(userId, assetId);
		a.setDeleted(true);
	}

	@Override
	public Asset storeAssetForCompany(AssetDTO assetDTO, Long companyId) throws HostServiceException, AssetTransformationException, IOException {
		return storeAssetForCompany(assetDTO, companyId, true);
	}

	@Override
	public Asset storeAssetForCompany(AssetDTO assetDTO, Long companyId, boolean deleteFile) throws HostServiceException, AssetTransformationException, IOException {
		verifyStoreAsset(assetDTO);

		Assert.isTrue(CompanyAssetAssociationType.TYPES.contains(assetDTO.getAssociationType()), "Invalid asset type code");

		Company company = companyDAO.get(companyId);
		Assert.notNull(company);

		Asset originalAsset = storeAsset(assetDTO, null, deleteFile);
		Asset largeAsset = null;
		Asset smallAsset = null;

		if (assetDTO.isLargeTransformation()) {
			largeAsset = storeAsset(assetDTO, fileTransformerFactory.newImageThumbnailLargeTransformer());
		}
		if (assetDTO.isSmallTransformation()) {
			smallAsset = storeAsset(assetDTO, fileTransformerFactory.newImageThumbnailSmallTransformer());
		}

		if (deleteFile) {
			FileUtils.deleteQuietly(new File(assetDTO.getSourceFilePath()));
		}

		CompanyAssetAssociation a = new CompanyAssetAssociation(company, originalAsset, new CompanyAssetAssociationType(assetDTO.getAssociationType()));
		a.setTransformedLargeAsset(largeAsset);
		a.setTransformedSmallAsset(smallAsset);
		companyAssetAssociationDAO.saveOrUpdate(a);

		if (assetDTO.getAssociationType().equals(CompanyAssetAssociationType.AVATAR)) {
			// TODO Do avatars require CSR approval?
			// a.setApprovalStatus(ApprovalStatus.PENDING);'
			jmsService.sendEventMessage(eventFactory.buildCompanyAvatarUpdatedEvent(companyId));
		}

		if (assetDTO.isAddToCompanyLibrary()) {
			addAssetToCompanyLibrary(originalAsset.getId(), company.getId());
		}

		return originalAsset;
	}

	@Override
	public void addAssetToCompany(AssetDTO assetDTOto, Long companyId) {
		verifyAddAsset(assetDTOto);

		Assert.isTrue(CompanyAssetAssociationType.TYPES.contains(assetDTOto.getAssociationType()), "Invalid asset type code");

		Company company = companyDAO.get(companyId);
		Assert.notNull(company);
		Asset asset = assetService.get(assetDTOto.getAssetId());
		Assert.notNull(asset);
		CompanyAssetAssociation a = new CompanyAssetAssociation(company, asset, new CompanyAssetAssociationType(assetDTOto.getAssociationType()));
		companyAssetAssociationDAO.saveOrUpdate(a);

		if (assetDTOto.isAddToCompanyLibrary()) {
			addAssetToCompanyLibrary(asset.getId(), company.getId());
		}
	}

	@Override
	public Asset addUploadToCompany(UploadDTO uploadDTO, Long companyId) throws IOException, AssetTransformationException, HostServiceException {
		Assert.notNull(companyId);
		verifyAddUpload(uploadDTO);

		Assert.isTrue(CompanyAssetAssociationType.TYPES.contains(uploadDTO.getAssociationType()), "Invalid asset type code");

		Company company = companyDAO.get(companyId);
		Assert.notNull(company);

		// If transform parameters were provided, we retrieve the remote file
		// and create a new instance of the asset with the transformation applied.

		if (uploadDTO.requiresTransformations()) {
			File file = remoteFileAdapter.getFile(RemoteFileType.TMP, uploadDTO.getUploadUuid());
			AssetDTO assetDTO = BeanUtilities.newBean(AssetDTO.class, uploadDTO);
			assetDTO.setSourceFilePath(file.getPath());
			assetDTO.setName(uploadDTO.getName());
			assetDTO.setMimeType(uploadDTO.getMimeType());
			assetDTO.setLargeTransformation(uploadDTO.isLargeTransformation());
			assetDTO.setSmallTransformation(uploadDTO.isSmallTransformation());
			assetDTO.setAssociationType(uploadDTO.getAssociationType());
			return storeAssetForCompany(assetDTO, companyId);
		}

		Asset asset = storeAsset(uploadDTO);

		CompanyAssetAssociation a = new CompanyAssetAssociation(company, asset, new CompanyAssetAssociationType(uploadDTO.getAssociationType()));
		companyAssetAssociationDAO.saveOrUpdate(a);

		if (uploadDTO.isAddToCompanyLibrary()) {
			addAssetToCompanyLibrary(asset.getId(), company.getId());
		}

		return asset;
	}

	@Override
	public void removeAssetFromCompany(Long assetId, Long companyId) {
		CompanyAssetAssociation a = companyAssetAssociationDAO.findByCompanyAndAssetId(companyId, assetId);
		a.setDeleted(true);
	}

	private void addAssetToCompanyLibrary(Long assetId, Long companyId) {
		Assert.notNull(assetId);
		Assert.notNull(companyId);

		CompanyAssetLibraryAssociation association = companyAssetLibraryAssociationDAO.findByCompanyAndAssetId(companyId, assetId);

		if (association != null) return;

		Company company = companyDAO.get(companyId);
		Assert.notNull(company);
		Asset asset = assetService.get(assetId);
		Assert.notNull(asset);

		association = new CompanyAssetLibraryAssociation(company, asset);
		companyAssetLibraryAssociationDAO.saveOrUpdate(association);
	}

	@Override
	public boolean removeAssetFromCompanyLibrary(Long assetId, Long companyId) {
		Assert.notNull(companyId);
		Assert.notNull(assetId);

		CompanyAssetLibraryAssociation association = companyAssetLibraryAssociationDAO.findByCompanyAndAssetId(companyId, assetId);
		if (association != null) {
			association.setDeleted(true);
			return true;
		}
		return false;
	}

	@Override
	public CompanyAssetPagination getCompanyLibrary(Long companyId, CompanyAssetPagination pagination) {
		Assert.notNull(companyId);
		Assert.notNull(pagination);
		return companyAssetLibraryAssociationDAO.findAllAssetsByCompany(companyId, pagination);
	}

	@Override
	public AssetPagination findAllCsrInternalAssetsByCompany(Long companyId, AssetPagination pagination) {
		Assert.notNull(companyId);
		Assert.notNull(pagination);
		return companyAssetAssociationDAO.findAllCSRInternalAssetsByCompany(companyId, pagination);
	}

	@Override
	public WorkAssetAssociation storeAssetForWork(AssetDTO assetDTO, Long workId) throws IOException, AssetTransformationException, HostServiceException {
		return storeAssetForWork(assetDTO, workId, false);
	}

	@Override
	public WorkAssetAssociation storeAssetForWork(AssetDTO assetDTO, Long workId, boolean deleteFile) throws IOException, AssetTransformationException, HostServiceException {
		verifyStoreAsset(assetDTO);
		Assert.isTrue(WorkAssetAssociationType.TYPES.contains(assetDTO.getAssociationType()), "Invalid asset type code");

		AbstractWork work = abstractWorkDAO.get(workId);
		Assert.notNull(work);

		Asset asset = storeAsset(assetDTO);
		Asset smallAsset = null;
		Asset largeAsset = null;
		if (asset.isImage()) {
			// TODO[Jim]: reintroduce image orientation that works correctly
			// storeAsset(assetDTO, asset, true, fileTransformerFactory.newImageWorkOrientationTransformer());
			if (assetDTO.isSmallTransformation()) {
				smallAsset = storeAsset(assetDTO, fileTransformerFactory.newImageWorkThumbnailSmallTransformer());
			}
			if (assetDTO.isLargeTransformation()) {
				largeAsset = storeAsset(assetDTO, fileTransformerFactory.newImageWorkThumbnailLargeTransformer());
			}
		}

		if (deleteFile) {
			FileUtils.deleteQuietly(new File(assetDTO.getSourceFilePath()));
		}

		WorkAssetAssociation workAssetAssociation = new WorkAssetAssociation(
			work, asset, new WorkAssetAssociationType(assetDTO.getAssociationType()),
			assetDTO.isDeliverable(), assetDTO.getDeliverableRequirementId(), assetDTO.getPosition()
		);
		workAssetAssociation.setTransformedSmallAsset(smallAsset);
		workAssetAssociation.setTransformedLargeAsset(largeAsset);
		workAssetAssociationDAO.saveOrUpdate(workAssetAssociation);

		webHookEventService.onAssetAdded(work.getId(), work.getCompany().getId(), asset.getId());

		return workAssetAssociation;
	}

	@Override
	public Asset getAssetInfo(AssetDTO assetDTO, Long workId) {
		verifyStoreAsset(assetDTO);
		Assert.isTrue(WorkAssetAssociationType.TYPES.contains(assetDTO.getAssociationType()), "Invalid asset type code");

		//this creates a representation of the asset
		//with the url's where the asset will be stored in S3 (if the upload works)
		Asset asset = assetDTO.toAsset();
		RemoteFileType type = publicAssetTypes.contains(assetDTO.getAssociationType()) ? RemoteFileType.PUBLIC : RemoteFileType.PRIVATE;
		RemoteFile remote = remoteFileAdapter.getUris(FileUtilities.createRemoteFileandDirectoryStructor(asset.getUUID()), type);
		asset.setRemoteUri(remote.getRemoteUri());
		asset.setCdnUri(remote.getCdnUri());
		if (RemoteFileType.PUBLIC.equals(type)) {
			asset.setAvailability(new AvailabilityType(AvailabilityType.GUEST));
		}
		return asset;
	}

	@Override
	public WorkAssetAssociation addAssetToWork(AssetDTO assetDTO, Long workId) {
		verifyAddAsset(assetDTO);

		WorkAssetAssociation a = workAssetAssociationDAO.findWorkAssetAssociation(workId, assetDTO.getAssetId());
		if (a != null) {
			a.setDeleted(false);
			a.getAsset().setDescription(assetDTO.getDescription());

			if (WorkAssetAssociationType.ATTACHMENT.equals(assetDTO.getAssociationType())) {
				VisibilityType visibilityType = lookupEntityDAO.findByCodeWithDefault(VisibilityType.class, assetDTO.getVisibilityTypeCode(), VisibilityType.createDefaultVisibility());
				WorkAssetVisibility workAssetVisibility = workAssetVisibilityDAO.findByWorkAssetAssociationId(a.getId());
				if (workAssetVisibility == null) {
					workAssetVisibilityDAO.saveOrUpdate(new WorkAssetVisibility(a, visibilityType));
				} else {
					workAssetVisibility.setVisibilityType(visibilityType);
				}
			}

			return a;
		}

		AbstractWork work = abstractWorkDAO.get(workId);
		Assert.notNull(work);
		Asset asset = assetService.get(assetDTO.getAssetId());
		Assert.notNull(asset, "Unable to find asset id: " + assetDTO.getAssetId());

		a = new WorkAssetAssociation(work, asset, new WorkAssetAssociationType(assetDTO.getAssociationType()), assetDTO.isDeliverable(), assetDTO.getDeliverableRequirementId(), assetDTO.getPosition());
		workAssetAssociationDAO.saveOrUpdate(a);

		if (WorkAssetAssociationType.ATTACHMENT.equals(assetDTO.getAssociationType())) {
			VisibilityType visibilityType = lookupEntityDAO.findByCodeWithDefault(VisibilityType.class, assetDTO.getVisibilityTypeCode(), VisibilityType.createDefaultVisibility());
			workAssetVisibilityDAO.saveOrUpdate(new WorkAssetVisibility(a, visibilityType));
		}

		return a;
	}

	@Override
	public void addSavedAssetToWorks(Asset asset, List<Work> works, String associationType) {
		Assert.notNull(works);
		Assert.notNull(asset);
		Assert.notNull(associationType);
		Assert.isTrue(WorkAssetAssociationType.TYPES.contains(associationType), "Invalid asset type code");

		for (Work work : works) {
			WorkAssetAssociation a = new WorkAssetAssociation(work, asset, new WorkAssetAssociationType(associationType));
			workAssetAssociationDAO.saveOrUpdate(a);
		}
	}

	@Override
	public Asset addUploadToWork(UploadDTO uploadDTO, Long workId) throws HostServiceException {
		Assert.notNull(workId);
		verifyAddUpload(uploadDTO);

		AbstractWork work = abstractWorkDAO.get(workId);
		Assert.notNull(work);

		Asset asset = storeAsset(uploadDTO);

		WorkAssetAssociation a = new WorkAssetAssociation(work, asset, new WorkAssetAssociationType(uploadDTO.getAssociationType()));
		workAssetAssociationDAO.saveOrUpdate(a);

		if (WorkAssetAssociationType.ATTACHMENT.equals(uploadDTO.getAssociationType())) {
			VisibilityType visibilityType = lookupEntityDAO.findByCodeWithDefault(VisibilityType.class, uploadDTO.getVisibilityTypeCode(), VisibilityType.createDefaultVisibility());
			workAssetVisibilityDAO.saveOrUpdate(new WorkAssetVisibility(a, visibilityType));
		}

		return asset;
	}

	@Override
	public void removeAssetFromWork(Long assetId, Long workId) {
		WorkAssetAssociation a = workAssetAssociationDAO.findWorkAssetAssociation(workId, assetId);
		if (a != null && !a.getDeleted()) {
			a.setDeleted(true);
			webHookEventService.onAssetRemoved(workId, assetId);
		}
	}

	@Override
	public void removeAssetFromWork(String Uuid, Long workId) {
		Asset asset = findAssetByUuid(Uuid);
		removeAssetFromWork(asset.getId(), workId);
	}

	@Override
	public void bulkRemoveAssetFromWork(List<Work> works, String Uuid) {
		Assert.notNull(Uuid);
		Assert.notNull(works);
		Asset asset = findAssetByUuid(Uuid);
		Assert.notNull(asset);

		List<WorkAssetAssociation> workAssetAssociations = findWorkAssetAssociationsByWork(works, asset.getId());
		if (workAssetAssociations == null) return;

		for (WorkAssetAssociation workAssetAssociation : workAssetAssociations) {
			workAssetAssociation.setDeleted(true);
		}
	}

	@Override
	public void bulkNotifyRemoveAssetFromWork(List<Work> works, String Uuid) {
		Assert.notNull(works);
		Assert.notNull(Uuid);
		Asset asset = findAssetByUuid(Uuid);
		Assert.notNull(asset);
		for (AbstractWork work : works) {
			webHookEventService.onAssetRemoved(work.getId(), asset.getId());
		}
	}

	@Override
	public List<WorkAssetAssociation> findWorkAssetAssociationsByWork(List<Work> works, Long assetId) {
		Assert.notNull(works);
		Assert.notNull(assetId);
		List<Long> workIds = Lists.newArrayList();
		for (Work work : works) {
			workIds.add(work.getId());
		}
		return workAssetAssociationDAO.findWorkAssetAssociationsByWork(workIds, assetId);
	}

	@Override
	public void setAssetsForWork(AssetDTO[] assetDTOs, Long workId) {
		for (WorkAssetAssociation a : workAssetAssociationDAO.findByWork(workId)) {
			logger.info("Deleting Asset id: " + a.getAsset().getId() + " from work id: " + workId);
			a.setDeleted(true);
		}

		for (AssetDTO assetDTO : assetDTOs) {
			addAssetToWork(assetDTO, workId);
		}
	}

	@Override
	public Asset addUploadToTaxVerificationRequest(UploadDTO uploadDTO, Long requestId) throws HostServiceException {
		Assert.notNull(requestId);

		verifyAddUpload(uploadDTO);

		Assert.isTrue(TaxVerificationRequestAssetAssociationType.TYPES.contains(uploadDTO.getAssociationType()), "Invalid asset type code");

		TaxVerificationRequest request = taxVerificationRequestDAO.get(requestId);
		Assert.notNull(request);
		Asset asset = storeAsset(uploadDTO);

		TaxVerificationRequestAssetAssociation a = new TaxVerificationRequestAssetAssociation(
			request, asset, new TaxVerificationRequestAssetAssociationType(uploadDTO.getAssociationType()));
		taxVerificationRequestAssetAssociationDAO.saveOrUpdate(a);

		logger.info(String.format("[irs match] upload %s added to tax verification request %d", uploadDTO.getUploadUuid(), requestId));

		return asset;
	}

	@Override
	public Asset storeAssetForContractVersion(ContractVersionAssetDTO contractVersionAssetDTO, Long contractVersionId) {
		Assert.notNull(contractVersionId);
		Assert.hasText(contractVersionAssetDTO.getContent());
		Assert.hasText(contractVersionAssetDTO.getAssociationType());

		ContractVersion version = contractVersionDAO.get(contractVersionId);
		Assert.notNull(version);

		Asset asset = new Asset(contractVersionAssetDTO.getName(), contractVersionAssetDTO.getDescription(), UUID.randomUUID().toString(), Constants.CONTRACT_VERSION_ASSET_TYPE);
		asset.setContent(contractVersionAssetDTO.getContent());
		assetService.saveOrUpdate(asset);

		ContractVersionAssetAssociation a = new ContractVersionAssetAssociation(version, asset, new AssetType(contractVersionAssetDTO.getAssociationType()));
		contractVersionAssetAssociationDAO.saveOrUpdate(a);
		return asset;
	}

	@Override
	public Asset storeAssetForUserCertification(AssetDTO assetDTO, Long userCertificationAssociationId) throws IOException, AssetTransformationException, HostServiceException {
		Assert.notNull(userCertificationAssociationId);
		verifyStoreAsset(assetDTO);

		UserCertificationAssociation userCertificationAssociation = userCertificationAssociationDAO.findAssociationById(userCertificationAssociationId);
		Assert.notNull(userCertificationAssociation);

		Asset asset = storeAsset(assetDTO);
		userCertificationAssociation.getAssets().add(asset);
		return asset;
	}

	@Override
	public Asset storeAssetForUserLicense(AssetDTO assetDTO, Long userLicenseAssociationId) throws IOException, AssetTransformationException, HostServiceException {
		Assert.notNull(userLicenseAssociationId);
		verifyStoreAsset(assetDTO);

		UserLicenseAssociation userLicenseAssociation = userLicenseAssociationDAO.findAssociationById(userLicenseAssociationId);
		Assert.notNull(userLicenseAssociation, "Unable to find UserLicenseAssociation");

		Asset asset = storeAsset(assetDTO);
		userLicenseAssociation.getAssets().add(asset);
		return asset;
	}

	@Override
	public Asset storeAssetForUserInsurance(AssetDTO assetDTO, Long userInsuranceAssociationId) throws IOException, AssetTransformationException, HostServiceException {
		Assert.notNull(userInsuranceAssociationId);
		verifyStoreAsset(assetDTO);

		UserInsuranceAssociation userInsuranceAssociation = userInsuranceAssociationDAO.findById(userInsuranceAssociationId);
		Assert.notNull(userInsuranceAssociation);

		Asset asset = storeAsset(assetDTO);
		userInsuranceAssociation.getAssets().add(asset);
		return asset;
	}

	@Override
	public Asset storeAssetForAssessmentItem(AssetDTO assetDTO, Long assessmentItemId) throws IOException, AssetTransformationException, HostServiceException {
		Assert.notNull(assessmentItemId);
		verifyStoreAsset(assetDTO);

		AbstractItem item = abstractItemDAO.get(assessmentItemId);
		Assert.notNull(item);

		Asset asset = storeAsset(assetDTO);
		item.getAssets().add(asset);
		return asset;
	}

	@Override
	public Asset addUploadToAssessmentItem(UploadDTO uploadDTO, Long assessmentItemId) throws HostServiceException {
		Assert.notNull(assessmentItemId);
		verifyAddUpload(uploadDTO);

		AbstractItem item = abstractItemDAO.get(assessmentItemId);
		Assert.notNull(item);

		Asset asset = storeAsset(uploadDTO);
		item.getAssets().add(asset);

		return asset;
	}

	@Override
	public void removeAssetFromAssessmentItem(Long assetId, Long assessmentItemId) {
		Assert.notNull(assessmentItemId);
		Assert.notNull(assetId);

		AbstractItem item = abstractItemDAO.get(assessmentItemId);
		Asset asset = findAssetById(assetId);

		item.getAssets().remove(asset);
		asset.setDeleted(true);
	}

	@Override
	public List<Asset> setAssetsForAssessmentItem(AssetDTO[] assetDTOs, Long itemId) {
		Assert.notNull(itemId);
		Assert.notNull(assetDTOs);

		AbstractItem item = abstractItemDAO.get(itemId);
		item.getAssets().clear();

		Set<Asset> assets = Sets.newHashSet();
		for (AssetDTO assetDTO : assetDTOs)
			assets.add(findAssetById(assetDTO.getAssetId()));
		item.setAssets(assets);
		return Lists.newArrayList(assets);
	}

	@Override
	public Asset storeAssetForAttemptResponse(AssetDTO assetDTO, Long attemptResponseId) throws HostServiceException, AssetTransformationException, IOException {
		Assert.notNull(attemptResponseId);
		Assert.notNull(assetDTO);

		AttemptResponse response = attemptResponseDAO.get(attemptResponseId);
		Assert.notNull(response);

		// Note that there is no store asset method for attempt responses,
		// as all new associations are established via an upload. Thus we can assume
		// that an association already exists and that there's no need to re-create a thumbnail.
		// This assumption changes if we start allowing users to provide responses with assets
		// from their file manager.

		AttemptResponseAssetAssociation latestAssociation = attemptResponseAssetAssociationDAO.findLatestByAsset(assetDTO.getAssetId());
		Assert.notNull(latestAssociation);
		AttemptResponseAssetAssociation newestAssociation = new AttemptResponseAssetAssociation(response, latestAssociation.getAsset(), latestAssociation.getAssetType());
		newestAssociation.setTransformedSmallAsset(latestAssociation.getTransformedSmallAsset());
		attemptResponseAssetAssociationDAO.saveOrUpdate(newestAssociation);

		return newestAssociation.getAsset();
	}

	@Override
	public Asset addUploadToAttemptResponse(UploadDTO uploadDTO, Long attemptResponseId) throws HostServiceException, AssetTransformationException, IOException {
		Assert.notNull(attemptResponseId);
		verifyAddUpload(uploadDTO);

		Assert.isTrue(WorkAssetAssociationType.TYPES.contains(uploadDTO.getAssociationType()), "Invalid asset type code");

		AttemptResponse response = attemptResponseDAO.get(attemptResponseId);
		Assert.notNull(response);

		if (uploadDTO.getMimeType() == null) {
			uploadDTO.setMimeType(MimeTypeUtilities.guessMimeType(uploadDTO.getName()));
		}

		Asset asset = storeAsset(uploadDTO);
		Asset smallAsset = null;
		Asset largeAsset = null;

		if (asset.isImage()) {
			RemoteFileType type = asset.getAvailability().hasGuestAvailability() ? RemoteFileType.PUBLIC : RemoteFileType.PRIVATE;
			File file = remoteFileAdapter.getFile(type, asset.getUUID());

			AssetDTO assetDTO = AssetDTO.newDTO(asset);
			assetDTO.setSourceFilePath(file.getPath());
			assetDTO.setSmallTransformation(true);
			assetDTO.setLargeTransformation(true);

			smallAsset = storeAsset(assetDTO, fileTransformerFactory.newImageThumbnailSmallTransformer());
			largeAsset = storeAsset(assetDTO, fileTransformerFactory.newImageResizeTransformer(Constants.ASSET_ATTEMPT_RESPONSE_LARGE_THUMBNAIL_WIDTH, Constants.ASSET_ATTEMPT_RESPONSE_LARGE_THUMBNAIL_HEIGHT));

			FileUtils.deleteQuietly(file);
		}

		AttemptResponseAssetAssociation a = new AttemptResponseAssetAssociation(response, asset, new AttemptResponseAssetAssociationType(uploadDTO.getAssociationType()));
		a.setTransformedSmallAsset(smallAsset);
		a.setTransformedLargeAsset(largeAsset);
		attemptResponseAssetAssociationDAO.saveOrUpdate(a);

		return asset;
	}

	@Override
	public Asset storeAssetForBankingFile(AssetDTO assetDTO, BankingIntegrationGenerationRequest request) throws IOException, AssetTransformationException, HostServiceException {
		verifyStoreAsset(assetDTO);

		Asset asset = storeAsset(assetDTO);
		request.addAsset(asset);
		return asset;
	}

	private Asset storeAsset(AssetDTO assetDTO, FileTransformer... transformers) throws AssetTransformationException, IOException, HostServiceException {
		return storeAsset(assetDTO, null, true, transformers);
	}

	@Override
	public Asset storeAsset(AssetDTO assetDTO, Asset asset, boolean deleteFile, FileTransformer... transformers) throws AssetTransformationException, IOException, HostServiceException {
		ImageBatchTransformer transformer = getImageCropTransformer(assetDTO.getTransformerParameters());
		for (FileTransformer t : transformers) {
			transformer.addTransformer(t);
		}

		if (transformer.hasTransformers()) {
			return storeAsset(assetDTO, asset, deleteFile, transformer);
		} else {
			return storeAsset(assetDTO, asset, deleteFile);
		}
	}

	private Asset storeAsset(AssetDTO assetDTO, Asset asset, boolean deleteFile, ImageBatchTransformer transformer) throws AssetTransformationException, IOException, HostServiceException {
		File source = new File(assetDTO.getSourceFilePath());
		InputStream sourceStream = new FileInputStream(source);
		InputStream targetStream = transformer.transform(sourceStream);
		return storeAsset(assetDTO, asset, deleteFile, targetStream);
	}

	private Asset storeAsset(AssetDTO assetDTO, Asset asset, boolean deleteFile) throws AssetTransformationException, IOException, HostServiceException {
		File source = new File(assetDTO.getSourceFilePath());
		InputStream targetStream = new RepeatableFileInputStream(source);
		return storeAsset(assetDTO, asset, deleteFile, targetStream);
	}

	private Asset storeAsset(AssetDTO assetDTO, Asset asset, boolean deleteFile, InputStream targetStream) throws AssetTransformationException, IOException, HostServiceException {
		RemoteFileType type = publicAssetTypes.contains(assetDTO.getAssociationType()) ? RemoteFileType.PUBLIC : RemoteFileType.PRIVATE;

		InputStream sourceStream = null;

		try {
			File source = new File(assetDTO.getSourceFilePath());
			sourceStream = new FileInputStream(source);
			int byteSize = targetStream.available();

			if (byteSize <= 0) {
				return null;
			}

			if (asset == null) {
				asset = assetDTO.toAsset();
			}

			remoteFileAdapter.put(targetStream, byteSize, type, asset.getMimeType(), asset.getUUID());

			// If transformations are required, chances are we'll need continued access
			// to the source file. In these cases, the calling method is responsible
			// for cleaning up after itself. These cases are the exception rather than the rule,
			// but could probably afford a more obvious implementation.

			if (!assetDTO.requiresTransformations() && deleteFile) {
				FileUtils.deleteQuietly(source);
			}

			asset.setFileByteSize(byteSize);
			asset.setAssetRemoteUri(assetService.getDefaultAssetRemoteUri());
			asset.setAssetCdnUri(assetService.getDefaultAssetCdnUri());
			if (RemoteFileType.PUBLIC.equals(type)) {
				asset.setAvailability(new AvailabilityType(AvailabilityType.GUEST));
			}

			assetService.saveOrUpdate(asset);

			return asset;
		} finally {
			deleteInputStream(sourceStream);
			deleteInputStream(targetStream);
		}
	}

	@Override
	public Asset storeProfilePhotoAsset(AssetDTO assetDTO) throws AssetTransformationException, IOException, HostServiceException {
		ImageBatchTransformer transformer = getImageCropTransformer(assetDTO.getTransformerParameters());

		RemoteFileType type = publicAssetTypes.contains(assetDTO.getAssociationType()) ? RemoteFileType.PUBLIC : RemoteFileType.PRIVATE;

		InputStream sourceStream = null;
		InputStream targetStream = null;

		try {
			File source = new File(assetDTO.getSourceFilePath());
			sourceStream = new FileInputStream(source);
			targetStream = transformer.transform(sourceStream);
			int byteSize = targetStream.available();

			Asset asset = assetDTO.toAsset();
			remoteFileAdapter.put(targetStream, byteSize, type, asset.getMimeType(), asset.getUUID());

			// If transformations are required, chances are we'll need continued access
			// to the source file. In these cases, the calling method is responsible
			// for cleaning up after itself. These cases are the exception rather than the rule,
			// but could probably afford a more obvious implementation.

			if (!assetDTO.requiresTransformations()) {
				FileUtils.deleteQuietly(source);
			}

			asset.setFileByteSize(byteSize);
			asset.setAssetCdnUri(assetService.getDefaultAssetCdnUri());
			asset.setAssetRemoteUri(assetService.getDefaultAssetRemoteUri());


			assetService.saveOrUpdate(asset);

			return asset;
		} finally {
			deleteInputStream(sourceStream);
			deleteInputStream(targetStream);
		}
	}

	@Override
	public Asset storeAsset(UploadDTO uploadDTO) throws HostServiceException {
		Upload upload = uploadDAO.findUploadByIdOrUUID(uploadDTO.getUploadId(), uploadDTO.getUploadUuid());
		Assert.notNull(upload);

		RemoteFileType type = publicAssetTypes.contains(uploadDTO.getAssociationType()) ? RemoteFileType.PUBLIC : RemoteFileType.PRIVATE;
		RemoteFile remote = remoteFileAdapter.move(RemoteFileType.TMP, type, upload.getUUID());

		upload.setDeleted(true);

		Asset asset = Asset.newInstance(upload);
		asset.setDescription(uploadDTO.getDescription());
		asset.setRemoteUri(remote.getRemoteUri());
		asset.setCdnUri(remote.getCdnUri());
		if (RemoteFileType.PUBLIC.equals(type)) {
			asset.setAvailability(new AvailabilityType(AvailabilityType.GUEST));
		}
		asset.setAssetCdnUri(assetService.getDefaultAssetCdnUri());
		asset.setAssetRemoteUri(assetService.getDefaultAssetRemoteUri());
		assetService.saveOrUpdate(asset);
		return asset;
	}

	private ImageBatchTransformer getImageCropTransformer(final AssetDTO.TransformerParameters transformerParameters) {
		ImageBatchTransformer transformer = (ImageBatchTransformer) fileTransformerFactory.newImageBatchTransformer();
		if (transformerParameters.isConfigured()) {
			transformer.addTransformer(fileTransformerFactory.newImageCropTransformer(
				transformerParameters.getX1(),
				transformerParameters.getY1(),
				transformerParameters.getX2(),
				transformerParameters.getY2()
			));
		}
		return transformer;
	}

	@Override
	public void changeUserBackgroundImage(Long userId, Asset backgroundImage) {
		User user = userDAO.get(userId);
		Assert.notNull(user);

		UserAssetAssociation currentBackgroundImage = userAssetAssociationDAO.findBackgroundImage(userId);
		if (currentBackgroundImage != null) {
			currentBackgroundImage.setDeleted(true);
			userAssetAssociationDAO.saveOrUpdate(currentBackgroundImage);
		}

		UserAssetAssociation newBackgroundImage = makeUserAssetAssociation(backgroundImage, user);
		userAssetAssociationDAO.saveOrUpdate(newBackgroundImage);
	}

    @Override
	public ImmutableList<Map> getProjectedDocuments(String[] fields) throws Exception {
		Long companyId = authenticationService.getCurrentUserCompanyId();
		CompanyAssetPagination pagination = new CompanyAssetPagination();

		pagination.setSortColumn(NAME);
		pagination.setSortDirection(ASC);
		pagination.setProjection(fields);

		pagination = getCompanyLibrary(companyId, pagination);

		pagination.setProjectionResults(ProjectionUtilities.projectAsArray(pagination.getProjection(), pagination.getResults()));
		return ImmutableList.copyOf(pagination.getProjectionResults());
	}

	public UserAssetAssociation makeUserAssetAssociation(Asset backgroundImage, User user) {
		return new UserAssetAssociation(
			user,
			backgroundImage,
			makeUserAssetAssociationType(UserAssetAssociationType.BACKGROUND_IMAGE)
		);
	}

	public UserAssetAssociationType makeUserAssetAssociationType(String type) {
		return new UserAssetAssociationType(type);
	}

	private void deleteInputStream(InputStream stream) {
		try {
			if (stream != null) {
				stream.close();
			}
		} catch (IOException io) {
			//Ignore
			logger.error("error deleting stream ", io);
		}
	}
}


