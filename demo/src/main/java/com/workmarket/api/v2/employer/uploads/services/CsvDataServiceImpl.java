package com.workmarket.api.v2.employer.uploads.services;

import com.google.api.client.util.Maps;
import com.google.common.base.Optional;
import com.workmarket.api.ApiBaseError;
import com.workmarket.api.v2.employer.uploads.models.CellDTO;
import com.workmarket.api.v2.employer.uploads.models.DataDTO;
import com.workmarket.api.v2.employer.uploads.models.ErrorsDTO;
import com.workmarket.api.v2.employer.uploads.models.MappingDTO;
import com.workmarket.api.v2.employer.uploads.models.PreviewDTO;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.service.exception.HostServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.workmarket.api.v2.employer.uploads.services.PreviewStorageServiceImpl.A_DAY_IN_SECONDS;

@Service
public class CsvDataServiceImpl implements CsvDataService {
	@Autowired private CoordinationService coordinationService;
	@Autowired private CsvHeaderService csvHeaderService;
	@Autowired private CsvValidationService csvValidationService;
	@Autowired private PreviewStorageService previewStorageService;
	@Autowired private RedisAdapter redisAdapter;

	@Override
	public void create(String uuid) throws IOException, HostServiceException {
		create(uuid, csvHeaderService.get(uuid));
	}

	@Override
	public void create(String uuid, List<MappingDTO> headers) throws IOException, HostServiceException {
		long count = previewStorageService.size(uuid);
		redisAdapter.set("uploads:" + uuid + ":step", "data", A_DAY_IN_SECONDS);
		redisAdapter.set("uploads:" + uuid + ":remaining", count, A_DAY_IN_SECONDS);

		for (long index = 0; index < count; index++) {
			coordinationService.createDatum(uuid, headers, index);
		}
	}

	@Override
	public void create(String uuid, List<MappingDTO> headers, long index) throws IOException, HostServiceException {
		Optional<PreviewDTO> optional = previewStorageService.get(uuid, index);
		Map<String, CellDTO.Builder> data = Maps.newHashMap();

		List<MappingDTO> mergedHeaders = csvHeaderService.get(uuid, headers);

		if (optional.isPresent()) {
			PreviewDTO previewDTO = optional.get();
			String[] row = previewDTO.getRow();
			if (mergedHeaders.size() == row.length) {
				for (int i = 0; i < mergedHeaders.size(); i++) {
					MappingDTO header = mergedHeaders.get(i);
					data.put(header.getProperty(), new CellDTO.Builder()
						.setHeader(header.getHeader())
						.setValue(row[i])
						.setIndex(i)
					);
				}
			}

			previewStorageService.set(uuid, index, new PreviewDTO.Builder(previewDTO)
				.setRowData(data)
				.build()
			);
			coordinationService.validateDatum(uuid, index);
		}
	}

	@Override
	public void validate(String uuid, long index) {
		Optional<PreviewDTO> optional = previewStorageService.get(uuid, index);

		if (optional.isPresent()) {
			PreviewDTO preview = optional.get();
			List<ApiBaseError> errors = csvValidationService.validate(preview.getRowData(), index);

			previewStorageService.set(uuid, index, new PreviewDTO.Builder(preview)
				.setParseErrors(errors)
				.build()
			);

			redisAdapter.decrement("uploads:" + uuid + ":remaining");
		}
	}

	@Override
	public DataDTO get(String uuid) {
		return get(uuid, 1L, 10L);
	}

	@Override
	public DataDTO get(String uuid, long page, long size) {
		long start = (page - 1) * size;
		long end = start + size - 1;

		DataDTO.Builder builder = new DataDTO.Builder()
			.setUuid(uuid)
			.setCount(previewStorageService.size(uuid));

		for (PreviewDTO previewDTO : previewStorageService.get(uuid, start, end)) {
			Map<String, CellDTO.Builder> map = Maps.newHashMap();
			Map<String, CellDTO> rowData = previewDTO.getRowData();
			for (String key : rowData.keySet()) {
				map.put(key, new CellDTO.Builder(rowData.get(key)));
			}
			builder.addData(map);
		}

		return builder.build();
	}

	@Override
	public ErrorsDTO getParsingErrors(String uuid) {
		return getParsingErrors(uuid, 1L, 10L);
	}

	@Override
	public ErrorsDTO getParsingErrors(String uuid, long page, long size) {
		long start = (page - 1) * size;
		long end = start + size - 1;

		long errorCount = 0L;

		ErrorsDTO.Builder builder = new ErrorsDTO.Builder()
			.setUuid(uuid);

		for (PreviewDTO previewDTO : previewStorageService.get(uuid, start, end)) {
			List<ApiBaseError> errors = previewDTO.getParseErrors();
			if (!errors.isEmpty()) {
				builder.addErrors(errors);
				errorCount++;
			}
		}

		return builder
			.setCount(errorCount)
			.build();
	}
}
