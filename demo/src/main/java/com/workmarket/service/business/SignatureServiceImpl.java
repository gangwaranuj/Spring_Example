package com.workmarket.service.business;

import com.workmarket.domains.model.AvailabilityType;
import com.workmarket.domains.model.MimeType;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.type.WorkAssetAssociationType;
import com.workmarket.service.business.asset.AssetUploaderService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.UploadService;
import com.workmarket.service.infra.file.RemoteFile;
import com.workmarket.service.infra.file.RemoteFileAdapter;
import com.workmarket.service.infra.file.RemoteFileType;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.utility.SerializationUtilities;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class SignatureServiceImpl implements SignatureService {

	private static final Log logger = LogFactory.getLog(SignatureServiceImpl.class);
	protected static final String TEMP_FILE = "work_attachment_";

	@Autowired UploadService uploadService;
	@Autowired RemoteFileAdapter remoteFileAdapter;
	@Autowired WorkService workService;
	@Autowired AssetManagementService assetManagementService;
	@Autowired AssetUploaderService assetUploaderService;
	@Autowired AuthenticationService authenticationService;
	@Autowired FeatureEvaluator featureEvaluator;

	@Override
	public RemoteFile uploadSignatureImage(Long workId, String base64Image, String fileName) throws Exception {
		File file = null;
		RemoteFile signatureImg = null;

		try {
			file = File.createTempFile(String.format("%s%s", TEMP_FILE, workId.toString()), ".dat");
			SerializationUtilities.decodeBase64File(base64Image, file);
			signatureImg = remoteFileAdapter.put(file, RemoteFileType.PUBLIC, fileName);
		} catch (Exception ex) {
			logger.error("Failed to upload signature file", ex);
		} finally {
			FileUtils.deleteQuietly(file);
		}

		return signatureImg;
	}

	@Override
	public AssetDTO attachSignaturePdfToWork(Long workId, Long deliverableRequirementId, Integer position, String filePath) throws Exception {
		String workNumber = workService.findWork(workId).getWorkNumber();

		/* Take the PDF and attach it to the assignment as a closing asset */
		File pdfFile = FileUtils.getFile(filePath);

		AssetDTO signatureAssetDto = new AssetDTO();
		signatureAssetDto.setMimeType(MimeType.PDF.getMimeType());
		signatureAssetDto.setName(workNumber + "-printout.pdf");
		signatureAssetDto.setDescription("Signed assignment printout");

		signatureAssetDto.setAssociationType(WorkAssetAssociationType.SIGN_OFF_SHEET);
		signatureAssetDto.setDeliverable(true);
		signatureAssetDto.setDeliverableRequirementId(deliverableRequirementId);
		signatureAssetDto.setPosition(position);

		signatureAssetDto.setFileByteSize(Long.valueOf(pdfFile.length()).intValue());
		signatureAssetDto.setSourceFilePath(pdfFile.getAbsolutePath());
		signatureAssetDto.setLargeTransformation(true);
		signatureAssetDto.setAvailabilityTypeCode(AvailabilityType.ALL);

		Asset asset = assetManagementService.getAssetInfo(signatureAssetDto, workId);
		assetUploaderService.uploadAsset(signatureAssetDto, asset, workId, authenticationService.getCurrentUser());
		signatureAssetDto.setAssetId(asset.getId());

		return signatureAssetDto;
	}
}
