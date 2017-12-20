package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.model.CustomFieldGroupDTO;
import com.workmarket.service.business.dto.WorkCustomFieldDTO;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface WorkCustomFieldsDTOFormatterService {

	public List<WorkCustomFieldDTO> verifyAndPackageCustomFields(long companyId, CustomFieldGroupDTO group, BindingResult result);

}
