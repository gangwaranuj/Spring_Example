package com.workmarket.domains.work.service;

import com.workmarket.domains.work.model.WorkTemplate;
import com.workmarket.domains.work.model.WorkTemplatePagination;
import com.workmarket.service.business.dto.WorkTemplateDTO;

import java.util.List;
import java.util.Map;

public interface WorkTemplateService {
	/**
	 * Saves or updates a work template using DTO that inherits from WorkDTO
	 *
	 * @param employeeId
	 * @param work
	 * @return work template
	 * @throws Exception
	 */
	WorkTemplate saveOrUpdateWorkTemplate(Long employeeId, WorkTemplateDTO work) throws Exception;
	void toggleWorkTemplateActiveStatusById(Long templateId) throws Exception;

	/**
	 * Finds all not deleted work templates by a company
	 *
	 * @param companyId
	 * @return work template pagination object
	 */
	List<WorkTemplate> findAllActiveWorkTemplates(Long companyId);

	/**
	 * Finds all not deleted work templates by a company
	 *
	 * @param companyId
	 * @param pagination
	 * @return work template pagination object
	 */
	WorkTemplatePagination findAllActiveWorkTemplates(Long companyId, WorkTemplatePagination pagination);

	WorkTemplatePagination findAllTemplatesByStatusCode(Long companyId, WorkTemplatePagination pagination, String workStatusType);

	Map<Long, String> findAllActiveWorkTemplatesIdNameMap(Long companyId);

	Map<Long, String> findAllActiveWorkTemplatesIdNumberMap(Long companyId);

	Map<String, Map<String, Object>> findAllActiveWorkTemplatesWorkNumberNameMap(Long companyId, Long clientId);

	void deleteWorkTemplate(Long workTemplateId);

	/**
	 * Finds a work template by id, it will find rows with deleted flag = true
	 *
	 * @param workTemplate
	 * @return work template
	 */
	WorkTemplate findWorkTemplateById(Long workTemplate);

	/**
	 * Finds a work template by name, it will find rows with deleted flag = true
	 *
	 * @param companyId
	 * @param name
	 * @return work template
	 */
	WorkTemplate findWorkTemplateByName(Long companyId, String name);

	WorkTemplate findWorkTemplateByIdFast(Long workTemplateId);

	WorkTemplate findWorkTemplateByIdAndCompany(Long companyId, Long workTemplateId);
}
