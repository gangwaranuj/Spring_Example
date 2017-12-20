package com.workmarket.api.v2.employer.uploads.services;

import au.com.bytecode.opencsv.CSVReader;
import com.workmarket.api.v2.employer.uploads.models.PreviewDTO;
import com.workmarket.api.v2.employer.uploads.models.RowsDTO;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.infra.file.RemoteFileAdapter;
import com.workmarket.service.infra.file.RemoteFileType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.workmarket.api.v2.employer.uploads.services.PreviewStorageServiceImpl.A_DAY_IN_SECONDS;

@Service
public class CsvRowsServiceImpl implements CsvRowsService {
	@Autowired private RemoteFileAdapter remoteFileAdapter;
	@Autowired private PreviewStorageService previewStorageService;
	@Autowired private RedisAdapter redisAdapter;

	@Override
	public void create(String uuid) throws HostServiceException, IOException {
		redisAdapter.set("uploads:" + uuid + ":step", "rows", A_DAY_IN_SECONDS);

		CSVReader reader = new CSVReader(
			new BufferedReader(
				new InputStreamReader(
					remoteFileAdapter.getFileStream(RemoteFileType.TMP, uuid))));

		String[] header = reader.readNext();
		String[] line;
		int rowNumber = 0;
		while ((line = reader.readNext()) != null) {
			previewStorageService.add(uuid, new PreviewDTO.Builder()
				.setRowNumber(rowNumber)
				.setRow(line)
				.build()
			);
			rowNumber++;
		}

		redisAdapter.set("uploads:" + uuid + ":remaining", "0", A_DAY_IN_SECONDS);
	}

	@Override
	public RowsDTO get(String uuid) {
		return get(uuid, 1L, 10L);
	}

	@Override
	public RowsDTO get(String uuid, long page, long size) {
		long start = (page - 1) * size;
		long end = start + size - 1;

		RowsDTO.Builder builder = new RowsDTO.Builder()
			.setUuid(uuid)
			.setCount(previewStorageService.size(uuid));

		for (PreviewDTO previewDTO : previewStorageService.get(uuid, start, end)) {
			builder.addRow(previewDTO.getRow());
		}

		return builder.build();
	}
}
