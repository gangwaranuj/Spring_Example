package com.workmarket.domains.work.service;

import com.workmarket.dao.AddressDAO;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.assessment.AbstractAssessmentDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.option.WorkOption;
import com.workmarket.domains.work.dao.WorkTemplateDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.WorkTemplate;
import com.workmarket.domains.work.model.WorkTemplatePagination;
import com.workmarket.service.business.dto.WorkTemplateDTO;
import com.workmarket.service.option.OptionsService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

@Service
public class WorkTemplateServiceImpl implements WorkTemplateService {

	@Autowired private WorkService workService;
	@Autowired private WorkTemplateDAO workTemplateDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private AddressDAO addressDAO;
	@Autowired private AbstractAssessmentDAO assessmentDAO;
	@Qualifier("workOptionsService") @Autowired private OptionsService<AbstractWork> workOptionsService;

	@Override
	public WorkTemplate saveOrUpdateWorkTemplate(Long employeeId, WorkTemplateDTO workTemplateDTO) throws Exception {
		Assert.hasText(workTemplateDTO.getTemplateName(), "Template name is required");
		Assert.notNull(workTemplateDTO, "Work object is required");

		workTemplateDTO.setWorkStatusTypeCode(WorkStatusType.DRAFT);

		WorkTemplate template;

		boolean initialize = false;
		if (workTemplateDTO.getId() != null) {
			template = findWorkTemplateById(workTemplateDTO.getWorkTemplateId());
		} else {
			template = new WorkTemplate();
			initialize = true;
		}

		template.setTemplateName(workTemplateDTO.getTemplateName());
		template.setTemplateDescription(workTemplateDTO.getTemplateDescription());

		template = workService.buildWork(employeeId, workTemplateDTO, template, initialize);
		workTemplateDAO.saveOrUpdate(template);

		workOptionsService.setOption(template, WorkOption.DOCUMENTS_ENABLED, String.valueOf(workTemplateDTO.isDocumentsEnabled()));

		return template;
	}

	@Override
	public void toggleWorkTemplateActiveStatusById(Long templateId) throws Exception {
		Assert.notNull(templateId);
		WorkTemplate template = findWorkTemplateByIdFast(templateId);

		if (template.isDraft()) {
			template.setWorkStatusType(WorkStatusType.newWorkStatusType(WorkStatusType.DEACTIVATED));
		} else {
			template.setWorkStatusType(WorkStatusType.newWorkStatusType(WorkStatusType.DRAFT));
		}
	}


	@Override
	public List<WorkTemplate> findAllActiveWorkTemplates(Long companyId) {
		return workTemplateDAO.findAllActiveWorkTemplates(companyId);
	}

	@Override
	public WorkTemplatePagination findAllActiveWorkTemplates(Long companyId, WorkTemplatePagination pagination) {
		return workTemplateDAO.findAllActiveWorkTemplates(companyId, pagination);
	}

	@Override
	public WorkTemplatePagination findAllTemplatesByStatusCode(Long companyId, WorkTemplatePagination pagination, String workStatusType) {
		return workTemplateDAO.findAllTemplatesByStatusCode(companyId, pagination, workStatusType);
	}

	@Override
	public Map<Long, String> findAllActiveWorkTemplatesIdNameMap(Long companyId) {
		return workTemplateDAO.findAllActiveWorkTemplatesIdNameMap(companyId);
	}

	@Override
	public Map<Long, String> findAllActiveWorkTemplatesIdNumberMap(Long companyId) {
		return workTemplateDAO.findAllActiveWorkTemplatesIdNumberMap(companyId);
	}

	@Override
	public Map<String, Map<String, Object>> findAllActiveWorkTemplatesWorkNumberNameMap(Long companyId, Long clientId) {
		return workTemplateDAO.findAllActiveWorkTemplatesWorkNumberNameMap(companyId, clientId);
	}

	@Override
	public WorkTemplate findWorkTemplateByIdAndCompany(Long companyId, Long workTemplateId) {
		WorkTemplate template = workTemplateDAO.findBy(
				"company.id", companyId,
				"id", workTemplateId);

		return initializeTemplate(template);
	}

	@Override
	public WorkTemplate findWorkTemplateByIdFast(Long workTemplateId) {
		return workTemplateDAO.findBy("id", workTemplateId);
	}

	@Override
	public WorkTemplate findWorkTemplateById(Long workTemplateId) {
		WorkTemplate template = workTemplateDAO.findBy("id", workTemplateId);
		return initializeTemplate(template);
	}

	private WorkTemplate initializeTemplate(WorkTemplate template) {
		Assert.notNull(template);

		if (template.isSetOnsiteAddress() && template.getIsOnsiteAddress()) {
			addressDAO.initialize(template.getAddress());
		}

		userDAO.initialize(template.getBuyer());
		assessmentDAO.initialize(template.getAssessments());
		Hibernate.initialize(template.getPriceHistory());
		Hibernate.initialize(template.getWorkSubStatusTypeAssociations());
		Hibernate.initialize(template.getAssetAssociations());
		return template;
	}

	@Override
	public WorkTemplate findWorkTemplateByName(Long companyId, String name) {
		return workTemplateDAO.findWorkTemplateByName(companyId, name);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteWorkTemplate(Long workTemplateId) {
		workTemplateDAO.deleteWorkTemplate(workTemplateId);
	}

}
