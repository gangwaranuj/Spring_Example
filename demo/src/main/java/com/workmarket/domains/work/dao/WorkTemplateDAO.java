package com.workmarket.domains.work.dao;

import com.workmarket.dao.PaginatableDAOInterface;
import com.workmarket.domains.work.model.WorkTemplate;
import com.workmarket.domains.work.model.WorkTemplatePagination;

import java.util.List;
import java.util.Map;

public interface WorkTemplateDAO extends PaginatableDAOInterface<WorkTemplate> {
	List<WorkTemplate> findAllActiveWorkTemplates(Long companyId);
	WorkTemplatePagination findAllActiveWorkTemplates(Long companyId, WorkTemplatePagination pagination);
	WorkTemplatePagination findAllTemplatesByStatusCode(Long companyId, WorkTemplatePagination pagination, String workStatusType);

	WorkTemplate findWorkTemplateById(Long workTemplateId);
	WorkTemplate findWorkTemplateByName(Long companyId, String name);

	void deleteWorkTemplate(Long workTemplateId);

	Map<Long,String> findAllActiveWorkTemplatesIdNameMap(Long companyId);
	Map<Long,String> findAllActiveWorkTemplatesIdNumberMap(Long companyId);
	Map<String, Map<String, Object>> findAllActiveWorkTemplatesWorkNumberNameMap(Long companyId, Long clientId);
}