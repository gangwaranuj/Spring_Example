package com.workmarket.api.v2.employer.uploads.services;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.workmarket.api.v2.employer.uploads.models.MappingDTO;
import com.workmarket.api.v2.employer.uploads.models.PreviewDTO;
import com.workmarket.api.v2.employer.uploads.models.PreviewsDTO;
import com.workmarket.api.ApiBaseError;
import com.workmarket.domains.model.MimeType;
import com.workmarket.domains.model.asset.Upload;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.infra.business.UploadService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.join;

@Service
public class CsvErrorsServiceImpl implements CsvErrorsService {
	private static final String COMMA = ",";
	private static final String NEW_LINE = "\n";
	private static final String ERROR_DELIMITER = "; ";
	public static final String ERROR_COLUMN_HEADER = "Work Market System Error";

	@Autowired private CsvPreviewsService csvPreviewsService;
	@Autowired private UploadService uploadService;
	@Autowired private CsvHeaderService csvHeaderService;
	@Autowired private CsvStorageService csvStorageService;

	@Override
	public String generate(final String uuid, final boolean includeValid) throws IOException, HostServiceException {
		PreviewsDTO previewsDTO = csvPreviewsService.get(uuid);
		return generate(uuid, previewsDTO.getPreviews(), includeValid);
	}

	private String generate(String uuid, List<PreviewDTO> previewDTOs, boolean includeValid) throws IOException, HostServiceException {
		List<String> rows = Lists.newArrayList();
		// get header
		rows.add(join(getHeaderRow(uuid), COMMA));

		// added data rows with error if any
		String row;
		for (PreviewDTO previewDTO : previewDTOs) {
			row = getRow(previewDTO, includeValid);
			if(isNotEmpty(row)) {
				rows.add(row);
			}
		}

		String os = join(rows, NEW_LINE);
		Upload originalFile = uploadService.findUploadByUUID(uuid);
		InputStream is = new ByteArrayInputStream(os.getBytes());
		Upload newFile = uploadService.storeUpload(is, originalFile.getFilename(), MimeType.TEXT_CSV.getMimeType(), os.getBytes().length);

		return csvStorageService.download(newFile.getUUID());
	}

	//TODO: temp solution to get header until a better way found
	private String[] getHeaderRow(String uuid) throws IOException, HostServiceException {
		List<MappingDTO> mappingDTOs = csvHeaderService.get(uuid);
		List<String> headers = Lists.newArrayList();
		for (MappingDTO mappingDTO : mappingDTOs) {
			headers.add(mappingDTO.getHeader());
		}

		//add the additional error column
		headers.add(ERROR_COLUMN_HEADER);
		return headers.toArray(new String[0]);
	}

	private String getRow(PreviewDTO previewDTO, boolean includeValid) {

		StringBuffer row = new StringBuffer();
		if (CollectionUtils.isNotEmpty(previewDTO.getParseErrors()) || CollectionUtils.isNotEmpty(previewDTO.getValidationErrors())) {
			row.append(join(previewDTO.getRow(), COMMA))
				.append(COMMA)
				.append(mergeErrors(previewDTO));
		} else {
			if (includeValid) {
				row.append(join(previewDTO.getRow(), COMMA))
					.append(COMMA);
			}
		}

		return row.toString();
	}

	private String mergeErrors(PreviewDTO previewDTO) {
		List<ApiBaseError> errors = ListUtils.union(previewDTO.getParseErrors(), previewDTO.getValidationErrors());
		List<String> errorMsgs = Lists.transform(errors, new Function<ApiBaseError, String>() {
			@Nullable
			@Override
			public String apply(@Nullable final ApiBaseError error) {
				return error.getMessage();
			}
		});
		return join(errorMsgs, ERROR_DELIMITER);
	}
}
