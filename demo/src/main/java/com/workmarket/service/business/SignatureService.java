package com.workmarket.service.business;

import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.infra.file.RemoteFile;

public interface SignatureService {

	RemoteFile uploadSignatureImage(Long workId, String base64Image, String fileName) throws Exception;
	AssetDTO attachSignaturePdfToWork(Long workId, Long deliverableRequirementId, Integer position, String filePath) throws Exception;

}
