package com.workmarket.api.v2.employer.uploads.services;

import com.google.api.client.util.Sets;
import com.workmarket.api.v2.employer.uploads.models.MappingDTO;
import com.workmarket.api.v2.employer.uploads.models.MappingsDTO;
import com.workmarket.domains.work.model.WorkUploadColumnType;
import com.workmarket.domains.work.model.WorkUploadMapping;
import com.workmarket.domains.work.model.WorkUploadMappingGroup;
import com.workmarket.service.business.upload.transactional.WorkUploadMappingService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.thrift.work.uploader.DeleteMappingRequest;
import com.workmarket.thrift.work.uploader.WorkUploadDuplicateMappingGroupNameException;
import com.workmarket.thrift.work.uploader.WorkUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UploadMappingServiceImpl implements UploadMappingService {
	@Autowired private WorkUploadMappingService mappingService;
	@Autowired private AuthenticationService authenticationService;

	@Override
	public MappingsDTO create(MappingsDTO mappingsDTO) throws WorkUploadException, WorkUploadDuplicateMappingGroupNameException {
		return buildMappingsDTO(mappingService.saveMappingGroup(loadMappingGroup(mappingsDTO, new WorkUploadMappingGroup())));
	}

	@Override
	public MappingsDTO get(Long id) {
		return buildMappingsDTO(mappingService.getByMappingGroupId(id));
	}

	@Override
	public MappingsDTO update(Long id, MappingsDTO mappingsDTO) {
		return buildMappingsDTO(mappingService.saveMappingGroup(loadMappingGroup(mappingsDTO, mappingService.getByMappingGroupId(id))));
	}

	@Override
	public void delete(Long id) throws WorkUploadException {
		mappingService.deleteMapping(new DeleteMappingRequest()
			.setUserNumber(authenticationService.getCurrentUser().getUserNumber())
			.setMappingGroupId(id));
	}

	private MappingsDTO buildMappingsDTO(WorkUploadMappingGroup mappingGroup) {
		if (mappingGroup == null || mappingGroup.getDeleted()) { return null; }

		MappingsDTO.Builder builder = new MappingsDTO.Builder()
			.setId(mappingGroup.getId())
			.setName(mappingGroup.getName());

		for (WorkUploadMapping mapping : mappingGroup.getMappings()) {
			builder.addMapping(
				new MappingDTO.Builder()
					.setId(mapping.getId())
					.setProperty(mapping.getColumnType().getCode())
					.setHeader(mapping.getColumnName())
					.setPosition(mapping.getColumnIndex())
			);
		}

		return builder.build();
	}

	private WorkUploadMappingGroup loadMappingGroup(MappingsDTO mappingsDTO, WorkUploadMappingGroup mappingGroup) {
		mappingGroup.setName(mappingsDTO.getName());
		mappingGroup.setCompany(authenticationService.getCurrentUser().getCompany());

		Set<WorkUploadMapping> mappings = Sets.newHashSet();

		for (MappingDTO mappingDTO : mappingsDTO.getMappings()) {
			WorkUploadMapping mapping = new WorkUploadMapping();
			mapping.setId(mappingDTO.getId());
			mapping.setColumnType(WorkUploadColumnType.newInstance(mappingDTO.getProperty()));
			mapping.setColumnName(mappingDTO.getHeader());
			mapping.setColumnIndex(mappingDTO.getPosition());
			mappings.add(mapping);
		}

		mappingGroup.setMappings(mappings);

		return mappingGroup;
	}
}
