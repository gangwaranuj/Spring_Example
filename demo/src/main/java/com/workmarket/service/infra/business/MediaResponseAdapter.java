package com.workmarket.service.infra.business;

import com.workmarket.domains.model.asset.Upload;
import com.workmarket.media.MediaSuccessionResponse;
import org.springframework.stereotype.Component;

@Component
public class MediaResponseAdapter {

	public MediaSuccessionResponse asMediaSuccessionResponse(Upload upload) {
		return MediaSuccessionResponse.builder()
			.setUuid(upload.getUUID())
			.setName(upload.getFilename())
			.setEntityTag(upload.getETag())
			.setMimeType(upload.getMimeType())
			.build();
	}
}
