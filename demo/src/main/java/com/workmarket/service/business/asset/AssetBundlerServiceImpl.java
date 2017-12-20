package com.workmarket.service.business.asset;

import com.google.common.base.Optional;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.workmarket.dao.UserDAO;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.AvailabilityType;
import com.workmarket.domains.model.MimeType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.ZipAssetBundle;
import com.workmarket.domains.model.asset.type.CompanyAssetAssociationType;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.AssetService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.file.RemoteFileAdapter;
import com.workmarket.service.infra.file.RemoteFileType;
import com.workmarket.thrift.core.DeliverableAsset;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.FileUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class AssetBundlerServiceImpl implements AssetBundlerService {

	@Autowired private AssetService assetService;
	@Autowired private UserDAO userDAO;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private AssetManagementService assetManagementService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private RemoteFileAdapter remoteFileAdapter;
	@Autowired protected FeatureEvaluator featureEvaluator;

	private static final Log logger = LogFactory.getLog(AssetBundlerQueueImpl.class);

	private static final int BUFFER = 2048;

	@Override
	public void sendAssetBundle(AssetBundle bundle)
			throws IOException, HostServiceException, AssetTransformationException {
		User user = userDAO.get(bundle.getUserId());
		authenticationService.setCurrentUser(user);

		Optional<Asset> asset = storeAsset(bundle, user);

		if (asset.isPresent())
			userNotificationService.onAssetBundleAvailable(user, asset.get());
	}

	@Override
	public Optional<Asset> downloadAsset(AssetBundle bundle)
			throws IOException, HostServiceException, AssetTransformationException {
		User user = userDAO.get(bundle.getUserId());
		authenticationService.setCurrentUser(user);

		return storeAsset(bundle, user);
	}

	private Optional<Asset> storeAsset(AssetBundle bundle, User user)
			throws HostServiceException, IOException, AssetTransformationException {
		ZipAssetBundle zipAssetBundle;
		if (bundle instanceof AssetAssignmentBundle) {
			if (CollectionUtils.isEmpty(bundle.getAssetUuids())) return Optional.absent();
			zipAssetBundle = createAssignmentsAssetBundle(bundle);
		} else if (bundle instanceof AssetGroupBundle) {
			AssetGroupBundle assetGroupBundle = (AssetGroupBundle) bundle;
			if (assetGroupBundle.getUserAssetMap().isEmpty()) return Optional.absent();
			zipAssetBundle = createGroupsAssetBundle(bundle);
		} else {
			if (CollectionUtils.isEmpty(bundle.getAssetUuids())) return Optional.absent();
			zipAssetBundle = createAssetBundle(bundle);
		}

		AssetDTO assetDTO = new AssetDTO();
		assetDTO.setSourceFilePath(zipAssetBundle.getFileName());
		assetDTO.setName(String.format("assets-download-%s.zip", DateUtilities.getISO8601(bundle.getRequestedOn())));
		assetDTO.setDescription(String.format("%d downloads bundled on %s", zipAssetBundle.getFileSize(), DateUtilities.format("yyyy-MM-dd @ HH:mm", bundle.getRequestedOn())));
		assetDTO.setAssociationType(CompanyAssetAssociationType.ASSET_BUNDLE);
		assetDTO.setMimeType(MimeType.ZIP.getMimeType());
		assetDTO.setAvailabilityTypeCode(AvailabilityType.ALL);

		logger.debug(String.format("[asset-bundle] Storing asset bundle [%s => %s]", zipAssetBundle.getFileName(), assetDTO.getName()));

		// Store new zip file for the company
		Asset asset = assetManagementService.storeAssetForCompany(assetDTO, user.getCompany().getId());

		return Optional.fromNullable(asset);
	}

	@Override
	public ZipAssetBundle createAssetBundle(AssetBundle bundle) throws IOException, HostServiceException, AssetTransformationException {
		List<Asset> assets = assetService.get(bundle.getAssetUuids().toArray(new String[] {}));
		List<String> assetNames = Lists.newArrayList();
				// Create zip file

		String tmpFile = FileUtilities.generateTemporaryFileName();
		logger.debug(String.format("[asset-bundle] Creating asset bundle [%s]", tmpFile));

		FileOutputStream dest = new FileOutputStream(tmpFile);
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

		byte data[] = new byte[BUFFER];

		try {
			for (Asset a : assets) {
				try {
					String filename = String.format("%s-%s", StringUtils.substring(a.getUUID(), 0, 8),getAssetName(StringUtils.substring(a.getUUID(), 0, 8),assetNames,a));
					logger.debug(String.format("[asset-bundle] Adding asset [%s => %s] to bundle [%s]", a.getUUID(), filename, tmpFile));
					InputStream stream = remoteFileAdapter.getFileStream(a.getAvailability().hasGuestAvailability() ? RemoteFileType.PUBLIC : RemoteFileType.PRIVATE, a.getUUID());
					out.putNextEntry(new ZipEntry(filename));
					int count;
					while ((count = stream.read(data, 0, BUFFER)) != -1)
						out.write(data, 0, count);

					stream.close();
				} catch (HostServiceException e) {
					logger.error(e);
				}
			}
		} finally {
			out.close();
		}

		ZipAssetBundle zipAssetBundle = new ZipAssetBundle();
		zipAssetBundle.setFileName(tmpFile);
		zipAssetBundle.setZipOutputStream(out);
		zipAssetBundle.setFileSize(assets.size());

		return zipAssetBundle;
	}

	public ZipAssetBundle createAssignmentsAssetBundle(AssetBundle bundle) throws IOException, HostServiceException, AssetTransformationException {
		// Create zip file

		String tmpFile = FileUtilities.generateTemporaryFileName();
		logger.debug(String.format("[asset-bundle] Creating asset bundle [%s]", tmpFile));
		FileOutputStream dest = new FileOutputStream(tmpFile);
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
		List<String> assetids = bundle.getAssetUuids();

		try {
			int i = 0;
			 List<String> assetNames = new ArrayList();
			for (String a : assetids) {
				try {
					Asset asset = assetService.get(a);
					String filename = String.format("%s-%s", bundle.getName(i), getAssetName(bundle.getName(i), assetNames, asset));
					logger.debug(String.format("[asset-bundle] Adding asset [%s => %s] to bundle [%s]", bundle.getName(i), filename, tmpFile));
					InputStream stream = remoteFileAdapter.getFileStream(asset.getAvailability().hasGuestAvailability() ? RemoteFileType.PUBLIC : RemoteFileType.PRIVATE, asset.getUUID());
					out.putNextEntry(new ZipEntry(filename));
					IOUtils.copy(stream,out);
					stream.close();
				} catch (HostServiceException e) {
					logger.error(e);
				}
				i++;
			}
		} finally {
			out.close();
		}

		ZipAssetBundle zipAssetBundle = new ZipAssetBundle();
		zipAssetBundle.setFileName(tmpFile);
		zipAssetBundle.setZipOutputStream(out);
		zipAssetBundle.setFileSize(assetids.size());

		return zipAssetBundle;
	}

	public AssetAssignmentBundle createAssignmentAssetBundle(List<WorkResponse> workResponses, Set<String> types, boolean filterForDeliverables, Long deliverableRequirementId) {
		List<String> assetsList = Lists.newArrayList();
		List<String> assignmentsList = Lists.newArrayList();
		User currentUser = authenticationService.getCurrentUser();

		AssetAssignmentBundle bundle = new AssetAssignmentBundle();
		bundle.setUserId(currentUser.getId());
		bundle.setRequestedOn(DateUtilities.getCalendarNow());

		if (CollectionUtils.isEmpty(workResponses) || CollectionUtils.isEmpty(types)) {
			bundle.setAssetUuids(assetsList);
			bundle.setAssetAssignments(assignmentsList);
			return bundle;
		}

		for (WorkResponse workResponse : workResponses) {
			Work work = workResponse.getWork();
			TreeSet<DeliverableAsset> deliverableAssets = work.getDeliverableAssets();
			if (filterForDeliverables &&
				CollectionUtils.isNotEmpty(deliverableAssets)) {
				for (DeliverableAsset deliverableAsset : deliverableAssets) {
					if (deliverableAsset.isRejected() ||
						(deliverableRequirementId != null && !deliverableRequirementId.equals(deliverableAsset.getDeliverableRequirementId()))) {
						continue;
					}
					assetsList.add(deliverableAsset.getUuid());
					assignmentsList.add(work.getWorkNumber());
				}
			} else {
				TreeSet<com.workmarket.thrift.core.Asset> assets = work.getAssets();
				if (CollectionUtils.isNotEmpty(assets)) {
					for (com.workmarket.thrift.core.Asset asset : assets) {
						if (!types.contains(asset.getType())) {
							continue;
						}
						assetsList.add(asset.getUuid());
						assignmentsList.add(work.getWorkNumber());
					}
				}
			}
		}

		bundle.setAssetUuids(assetsList);
		bundle.setAssetAssignments(assignmentsList);

		return bundle;
	}

	public ZipAssetBundle createGroupsAssetBundle(AssetBundle bundle) throws IOException, HostServiceException, AssetTransformationException {
		// Create zip file
		AssetGroupBundle groupBundle = (AssetGroupBundle) bundle;
		String tmpFile = FileUtilities.generateTemporaryFileName();
		logger.debug(String.format("[asset-bundle] Creating asset bundle [%s]", tmpFile));
		FileOutputStream dest = new FileOutputStream(tmpFile);
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
		ListMultimap<User, String> assetUserMap = groupBundle.getUserAssetMap();
		List<String> assetNames = Lists.newArrayList();
		try {
			for (User user : assetUserMap.keySet()) {
				String userName = user.getFirstName() + "_" + user.getLastName() + "_" + user.getUserNumber();
				for (String uuid : assetUserMap.get(user)) {
					try {
						Asset asset = assetService.get(uuid);
						String filename = String.format("%s-%s", userName, getAssetName(userName, assetNames, asset));
						logger.debug(String.format("[asset-bundle] Adding asset [%s => %s] to bundle [%s]", userName, filename, tmpFile));
						InputStream stream = remoteFileAdapter.getFileStream(asset.getAvailability().hasGuestAvailability() ? RemoteFileType.PUBLIC : RemoteFileType.PRIVATE, asset.getUUID());
						out.putNextEntry(new ZipEntry(filename));
						IOUtils.copy(stream, out);
						stream.close();
					} catch (HostServiceException e) {
						logger.error(e);
					}
				}
			}
		} finally {
			out.close();
		}
		ZipAssetBundle zipAssetBundle = new ZipAssetBundle();
		zipAssetBundle.setFileName(tmpFile);
		zipAssetBundle.setZipOutputStream(out);
		zipAssetBundle.setFileSize(assetUserMap.size());
		return zipAssetBundle;
	}


	private String getAssetName(String prefixName, List assetNames, Asset asset) {
		String fullName = prefixName + asset.getName();
		int occurrences = Collections.frequency(assetNames, fullName);
		assetNames.add(fullName);
		if (occurrences == 0) {
			return asset.getName();
		} else {
			return Integer.toString(occurrences + 1) + "_" + asset.getName();
		}
	}


}
