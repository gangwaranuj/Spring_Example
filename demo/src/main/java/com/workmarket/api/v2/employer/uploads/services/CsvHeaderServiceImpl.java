package com.workmarket.api.v2.employer.uploads.services;

import au.com.bytecode.opencsv.CSVReader;
import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.workmarket.api.v2.employer.uploads.models.MappingDTO;
import com.workmarket.service.business.upload.transactional.WorkUploadColumnService;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.infra.file.RemoteFileAdapter;
import com.workmarket.service.infra.file.RemoteFileType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

@Service
public class CsvHeaderServiceImpl implements CsvHeaderService {
	@Autowired private RemoteFileAdapter remoteFileAdapter;
	@Autowired private WorkUploadColumnService columnService;

	@Override
	public List<MappingDTO> get(String uuid) throws HostServiceException, IOException {
		return get(uuid, Maps.<String, MappingDTO>newHashMap());
	}

	@Override
	public List<MappingDTO> get(String uuid, List<MappingDTO> mappings) throws HostServiceException, IOException {
		return get(uuid, getHeaderMap(mappings));
	}

	private List<MappingDTO> get(String uuid, Map<String, MappingDTO> headerMap) throws HostServiceException, IOException {
		String[] headers = getHeaderRow(uuid);
		List<MappingDTO> effectiveMappings = Lists.newArrayListWithCapacity(headers.length);

		for (String header : headers) {
			String sanitizedHeader = (header == null) ? null : header.trim();
			MappingDTO mapping = headerMap.get(sanitizedHeader);
			if (mapping != null) {
				effectiveMappings.add(mapping);
			} else {
				effectiveMappings.add(new MappingDTO.Builder()
					.setProperty(columnService.getColumnTypeCode(sanitizedHeader, null))
					.setHeader(header)
					.build());
			}
		}
		return effectiveMappings;
	}

	private String[] getHeaderRow(String uuid) throws HostServiceException, IOException {
		InputStream stream = remoteFileAdapter.getFileStream(RemoteFileType.TMP, uuid);
		CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(stream)));

		String[] headers = reader.readNext();
		reader.close();
		return headers;
	}

	private Map<String, MappingDTO> getHeaderMap(List<MappingDTO> mappings) {
		Map<String, MappingDTO> headerMap = Maps.newHashMap();

		for(MappingDTO mapping : mappings) {
			headerMap.put(mapping.getHeader(), mapping);
		}
		return headerMap;
	}
}
