package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.model.CustomFieldDTO;
import com.workmarket.api.v2.model.CustomFieldGroupDTO;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.service.business.CustomFieldService;
import com.workmarket.service.business.dto.WorkCustomFieldDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.CoreMatchers.equalTo;

@Service
public class WorkCustomFieldsDTOFormatterServiceImpl implements WorkCustomFieldsDTOFormatterService {

	private static final Logger logger = LoggerFactory.getLogger(WorkCustomFieldsDTOFormatterService.class);


	@Autowired protected CustomFieldService customFieldService;

	@Override
	public List<WorkCustomFieldDTO> verifyAndPackageCustomFields(long companyId, CustomFieldGroupDTO group, BindingResult result) {
		List<WorkCustomFieldDTO> dtos = new ArrayList();

		WorkCustomFieldGroup fieldGroup = customFieldService.findWorkCustomFieldGroupByCompany(group.getId(), companyId);

		if (fieldGroup != null) {
			for (CustomFieldDTO customFieldsDTO : group.getFields()) {
				long fieldId = customFieldsDTO.getId();
				WorkCustomField submittedField = selectFirst(fieldGroup.getActiveWorkCustomFields(),
						having(on(WorkCustomField.class).getId(), equalTo(fieldId))
				);
				if (submittedField == null) {
					result.addError(new ObjectError(String.valueOf(fieldId), String.format("%s - Field id does not exists in a group %s", fieldId, group.getId())));
				}
			}

			if(!result.hasErrors() || result.getAllErrors().size() < group.getFields().size()){
				for (WorkCustomField field : fieldGroup.getActiveWorkCustomFields()) {
					long fieldId = field.getId();

					CustomFieldDTO submittedField = selectFirst(group.getFields(),
							having(on(CustomFieldDTO.class).getId(), equalTo(fieldId))
					);
					if (submittedField == null) {
						continue;
					}
					String submittedValue = submittedField.getValue();
					dtos.add(new WorkCustomFieldDTO(fieldId, submittedValue));
				}
			}else{
				logger.debug("No valid fields to be processed");
			}

		} else {
			result.reject(String.valueOf(group.getId()), String.format("%s - Group does not exists", group.getId()));
		}

		return dtos;
	}

}
