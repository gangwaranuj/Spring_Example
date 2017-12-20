package com.workmarket.domains.model.customfield;

import com.workmarket.service.business.dto.WorkCustomFieldDTO;

import java.io.Serializable;
import java.util.List;

public class BulkSaveCustomFieldsRequest implements Serializable {

	private static final long serialVersionUID = -4223595698315412247L;

	private Long customFieldGroupId;
	private Long workId;
	private List<WorkCustomFieldDTO> dtos;

	public BulkSaveCustomFieldsRequest(Long customFieldGroupId, Long workId, List<WorkCustomFieldDTO> dtos) {
		this.customFieldGroupId = customFieldGroupId;
		this.workId = workId;
		this.dtos = dtos;
	}

	public BulkSaveCustomFieldsRequest() {
	}

	public Long getCustomFieldGroupId() {
		return customFieldGroupId;
	}

	public void setCustomFieldGroupId(Long customFieldGroupId) {
		this.customFieldGroupId = customFieldGroupId;
	}

	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	public List<WorkCustomFieldDTO> getDtos() {
		return dtos;
	}

	public void setDtos(List<WorkCustomFieldDTO> dtos) {
		this.dtos = dtos;
	}
}
