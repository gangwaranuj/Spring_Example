package com.workmarket.api.v2.employer.uploads.services;

import com.workmarket.api.v2.employer.uploads.models.CsvDTO;
import com.workmarket.domains.model.asset.Upload;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.infra.business.UploadService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.MimeTypeUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.workmarket.domains.model.MimeType.TEXT_CSV;
import static com.workmarket.domains.model.MimeType.TEXT_CSV_ALTERNATIVE;

@Service
public class CsvStorageServiceImpl implements CsvStorageService {
	@Autowired private UploadService uploadService;

	@Override
	public CsvDTO save(MultipartFile file) throws IOException, HostServiceException {
		if (!isCSV(file)) { return new CsvDTO.Builder().build(); }
		Upload upload = uploadService.storeUpload(
			file.getInputStream(),
			file.getOriginalFilename(),
			file.getContentType(),
			file.getSize()
		);
		return buildCsvDTO(upload);
	}

	@Override
	public CsvDTO get(String uuid) {
		Upload upload = uploadService.findUploadByUUID(uuid);
		return buildCsvDTO(upload);
	}

	private CsvDTO buildCsvDTO(Upload upload) {
		return new CsvDTO.Builder()
			.setId(upload.getId())
			.setUuid(upload.getUUID())
			.setName(upload.getFilename())
			.build();
	}

	@Override
	public String download(String uuid) throws HostServiceException {
		return uploadService.getAuthorizedDownloadUriByUuid(uuid);
	}

	private Boolean isCSV(MultipartFile file) {
		return CollectionUtilities.containsAny(
			MimeTypeUtilities.guessMimeType(file.getOriginalFilename()),
			TEXT_CSV.getMimeType(),
			TEXT_CSV_ALTERNATIVE.getMimeType()
		);
	}
}
