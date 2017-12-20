package com.workmarket.service.business;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.customfield.SavedWorkCustomFieldDAO;
import com.workmarket.dao.customfield.WorkCustomFieldDAO;
import com.workmarket.dao.customfield.WorkCustomFieldGroupAssociationDAO;
import com.workmarket.dao.customfield.WorkCustomFieldGroupDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.customfield.SavedWorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroupAssociation;
import com.workmarket.domains.model.customfield.WorkCustomFieldType;
import com.workmarket.domains.reports.model.CustomReportCustomFieldGroupDTO;
import com.workmarket.domains.work.dao.BaseWorkDAO;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.dao.WorkTemplateDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkTemplate;
import com.workmarket.domains.work.model.audit.WorkAuditType;
import com.workmarket.domains.work.service.WorkActionRequestFactory;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.domains.work.service.audit.WorkAuditService;
import com.workmarket.service.business.dto.WorkCustomFieldDTO;
import com.workmarket.service.business.dto.WorkCustomFieldGroupDTO;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.business.integration.hooks.webhook.WebHookEventService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.utility.ProjectionUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@Service
public class CustomFieldServiceImpl implements CustomFieldService {

	private static final Log logger = LogFactory.getLog(CustomFieldServiceImpl.class);

	@Autowired private AuthenticationService authenticationService;
	@Autowired private BaseWorkDAO abstractWorkDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private WorkDAO workDAO;
	@Autowired private WorkCustomFieldDAO workCustomFieldDAO;
	@Autowired private WorkCustomFieldGroupAssociationDAO workCustomFieldGroupAssociationDAO;
	@Autowired private WorkCustomFieldGroupDAO workCustomFieldGroupDAO;
	@Autowired private WorkTemplateDAO workTemplateDAO;
	@Autowired private SavedWorkCustomFieldDAO savedWorkCustomFieldDAO;
	@Autowired private WorkAuditService workAuditService;
	@Autowired private WorkActionRequestFactory workActionRequestFactory;
	@Autowired private EventRouter eventRouter;
	@Autowired private WebHookEventService webHookEventService;

	// Custom Fields
	@Override
	public List<WorkCustomFieldGroup> findActiveWorkCustomFieldGroups(Long companyId) {
		Assert.notNull(companyId);
		return workCustomFieldGroupDAO.findActiveWorkCustomFieldGroups(companyId);
	}

	@Override
	public List<WorkCustomFieldGroup> findInactiveWorkCustomFieldGroups(Long companyId) {
		Assert.notNull(companyId);
		return workCustomFieldGroupDAO.findInactiveWorkCustomFieldGroups(companyId);
	}

	@Override
	public List<WorkCustomFieldGroup> findWorkCustomFieldGroups(Long companyId) {
		Assert.notNull(companyId);
		return workCustomFieldGroupDAO.findWorkCustomFieldGroups(companyId);
	}

	@Override
	public WorkCustomFieldGroup findWorkCustomFieldGroup(Long fieldGroupId) {
		Assert.notNull(fieldGroupId);
		WorkCustomFieldGroup workCustomFieldGroup = workCustomFieldGroupDAO.get(fieldGroupId);
		if (workCustomFieldGroup != null) {
			Hibernate.initialize(workCustomFieldGroup.getWorkCustomFields());
		}

		return workCustomFieldGroup;
	}

	@Override
	public WorkCustomFieldGroup findWorkCustomFieldGroupByCompany(Long fieldGroupId, Long companyId) {

		WorkCustomFieldGroup workCustomFieldGroup = null;
		try {
			workCustomFieldGroup = getAndValidateFieldGroupByCompany(fieldGroupId, companyId);
		} catch (Exception e) {
			logger.warn(String.format("Invalid access of findWorkCustomFieldGroupByCompany by user=%d for group=%d, company=%d",
					authenticationService.getCurrentUser().getId(), fieldGroupId, companyId), e);
		}

		if (workCustomFieldGroup == null) {
			return null;
		}

		Hibernate.initialize(workCustomFieldGroup.getWorkCustomFields());
		return workCustomFieldGroup;
	}

	@Override
	public WorkCustomFieldGroup saveOrUpdateWorkFieldGroup(Long userId, WorkCustomFieldGroupDTO workFieldGroupDTO) throws Exception {
		Assert.notNull(userId);
		Assert.notNull(workFieldGroupDTO);

		WorkCustomFieldGroup workCustomFieldGroup;
		User user = checkNotNull(userDAO.get(userId));
		Long companyId = user.getCompany().getId();

		workCustomFieldGroup = null;

		if (workFieldGroupDTO.getWorkCustomFieldGroupId() != null) {
			try {
				workCustomFieldGroup = getAndValidateFieldGroupByCompany(workFieldGroupDTO.getWorkCustomFieldGroupId(), companyId);
			} catch (IllegalArgumentException e) {
				logger.warn(String.format("Invalid access of findWorkCustomFieldGroupByCompany by user=%d for group=%d, company=%d",
						authenticationService.getCurrentUser().getId(), workFieldGroupDTO.getWorkCustomFieldGroupId(), companyId), e);
			}

			if (workCustomFieldGroup == null) {
				return null;
			}
		}

		List<WorkCustomField> workCustomFields = new ArrayList<>();

		if (workFieldGroupDTO.getWorkCustomFieldGroupId() != null) {
			workCustomFieldGroup = workCustomFieldGroupDAO.get(workFieldGroupDTO.getWorkCustomFieldGroupId());

			WorkCustomField workCustomField;
			for (WorkCustomFieldDTO workCustomFieldDTO : workFieldGroupDTO.getWorkCustomFields()) {
				if (workCustomFieldDTO.getId() != null) {
					workCustomField = workCustomFieldDAO.get(workCustomFieldDTO.getId());
				} else {
					workCustomField = new WorkCustomField();
				}
				BeanUtils.copyProperties(workCustomFieldDTO, workCustomField);
				workCustomField.setWorkCustomFieldType(new WorkCustomFieldType(workCustomFieldDTO.getWorkCustomFieldTypeCode()));
				workCustomFields.add(workCustomField);
			}

			// Delete extras
			List<WorkCustomField> workCustomFieldsToDelete = workCustomFieldDAO.findAllFieldsForCustomFieldGroup(workFieldGroupDTO.getWorkCustomFieldGroupId());
			workCustomFieldsToDelete.removeAll(workCustomFields);
			for (WorkCustomField workCustomFieldToDelete : workCustomFieldsToDelete) {
				workCustomFieldToDelete.setDeleted(true);
			}
		} else {
			workCustomFieldGroup = new WorkCustomFieldGroup();
			for (WorkCustomFieldDTO workCustomFieldDTO : workFieldGroupDTO.getWorkCustomFields()) {
				WorkCustomField workCustomField = new WorkCustomField();
				BeanUtils.copyProperties(workCustomFieldDTO, workCustomField);
				workCustomField.setWorkCustomFieldType(new WorkCustomFieldType(workCustomFieldDTO.getWorkCustomFieldTypeCode()));
				workCustomFields.add(workCustomField);
			}
		}

		workCustomFieldGroup.setName(workFieldGroupDTO.getName());
		workCustomFieldGroup.setCompany(user.getCompany());
		workCustomFieldGroup.setWorkCustomFields(workCustomFields);

		workCustomFieldGroupDAO.saveOrUpdate(workCustomFieldGroup);

		requireWorkCustomFieldGroup(workCustomFieldGroup.getId(), companyId, workFieldGroupDTO.isRequired());

		return workCustomFieldGroup;
	}

	@Override
	public void requireWorkCustomFieldGroup(Long fieldGroupId, Long companyId, boolean isRequired) throws Exception {

		WorkCustomFieldGroup group = getAndValidateFieldGroupByCompany(fieldGroupId, companyId);

		if (isRequired) {
			setRequiredWorkCustomFieldGroupForCompany(group.getCompany(), group);
		}
		group.setRequired(isRequired);
	}

	@Override
	public WorkCustomFieldGroup findRequiredWorkCustomFieldGroup(Long companyId) {
		return workCustomFieldGroupDAO.findRequiredWorkCustomFieldGroup(companyId);
	}

	@Override
	public List<WorkCustomField> findAllFieldsForCustomFieldGroup(Long customFieldGroupId) {
		return workCustomFieldDAO.findAllFieldsForCustomFieldGroup(customFieldGroupId);
	}

	@Override
	public void saveOrUpdateWorkCustomField(WorkCustomField field) {
		workCustomFieldDAO.saveOrUpdate(field);
	}

	private void setRequiredWorkCustomFieldGroupForCompany(Company company, WorkCustomFieldGroup group) {
		// Only one group can be required at a time
		WorkCustomFieldGroup previousRequiredGroup = workCustomFieldGroupDAO.findRequiredWorkCustomFieldGroup(company.getId());
		Long previousRequiredGroupId = null;
		if (previousRequiredGroup != null && !previousRequiredGroup.equals(group)) {
			logger.debug(String.format("[customfields] Existing required custom field group [%d] for company [%d] => disabled", previousRequiredGroup.getId(), company.getId()));
			previousRequiredGroup.setRequired(false);
			previousRequiredGroupId = previousRequiredGroup.getId();
		}
		group.setRequired(true);

		company.getManageMyWorkMarket().setCustomFieldsEnabledFlag(true);

		// Update a company's work templates to reference the new required custom field group
		// TODO Can probably reduce this to two queries rather than an iterative Hibernate approach
		List<WorkTemplate> templates = workTemplateDAO.findAllActiveWorkTemplates(company.getId());
		if (!templates.isEmpty()) {
			logger.debug(String.format("[customfields] Setting required custom field group [%d] for %d work templates", group.getId(), templates.size()));
		}

		for (WorkTemplate t : templates) {
			if (previousRequiredGroupId != null) { deleteWorkCustomFieldGroupFromWork(t.getId(), previousRequiredGroupId); }
			addWorkCustomFieldGroupToWork(group.getId(), t.getId(), 0);
		}
	}

	@Override
	public void deleteWorkCustomFieldGroupByCompany(Long fieldGroupId, Long companyId) throws Exception {
		WorkCustomFieldGroup workCustomFieldGroup = getAndValidateFieldGroupByCompany(fieldGroupId, companyId);
		workCustomFieldGroup.setRequired(false);
		workCustomFieldGroup.setDeleted(true);
	}

	@Override
	public void deactivateWorkCustomFieldGroupByCompany(Long fieldGroupId, Long companyId) throws Exception {
		WorkCustomFieldGroup workCustomFieldGroup = getAndValidateFieldGroupByCompany(fieldGroupId, companyId);
		workCustomFieldGroup.setRequired(false);
		workCustomFieldGroup.setActive(false);
	}

	@Override
	public void activateWorkCustomFieldGroupByCompany(Long fieldGroupId, Long companyId) throws Exception {
		WorkCustomFieldGroup workCustomFieldGroup = getAndValidateFieldGroupByCompany(fieldGroupId, companyId);
		workCustomFieldGroup.setActive(true);
	}

	@Override
	public WorkCustomFieldGroup copyCustomFieldGroupByCompany(Long fieldGroupId, String newName, Long companyId) {
		Assert.hasText(newName);
		WorkCustomFieldGroup fieldGroup = getAndValidateFieldGroupByCompany(fieldGroupId, companyId);
		WorkCustomFieldGroup newGroup = WorkCustomFieldGroup.copy(fieldGroup);
		newGroup.setName(newName);

		workCustomFieldGroupDAO.saveOrUpdate(newGroup);

		return newGroup;
	}

	@Override
	public void addWorkCustomFieldGroupToWork(final long customFieldGroupId, final long workId, int position) {
		WorkCustomFieldGroupAssociation association =
				workCustomFieldGroupAssociationDAO.findByWorkAndWorkCustomFieldGroup(workId, customFieldGroupId);
		if (association != null) {
			association.setPosition(position);
			association.setDeleted(false);
			return;
		}

		WorkCustomFieldGroup customFieldGroup = workCustomFieldGroupDAO.get(customFieldGroupId);
		AbstractWork work = abstractWorkDAO.get(workId);
		association = new WorkCustomFieldGroupAssociation(work, customFieldGroup);
		association.setPosition(position);
		workCustomFieldGroupAssociationDAO.saveOrUpdate(association);
	}

	@Override
	public void removeWorkCustomFieldGroupForWork(Long customFieldGroupId, Long workId) {
		Assert.notNull(customFieldGroupId);
		Assert.notNull(workId);

		WorkCustomFieldGroupAssociation association = workCustomFieldGroupAssociationDAO.findByWorkAndWorkCustomFieldGroup(workId, customFieldGroupId);
		if (association != null) {
			association.setDeleted(true);
		}
	}

	@Override
	public void saveWorkCustomFieldForWork(WorkCustomFieldDTO customFieldDTO, Long workId) {
		Assert.notNull(customFieldDTO);
		Assert.notNull(customFieldDTO.getId());
		Assert.notNull(workId);

		WorkCustomField customField = workCustomFieldDAO.get(customFieldDTO.getId());
		WorkCustomFieldGroup customFieldGroup = customField.getWorkCustomFieldGroup();
		WorkCustomFieldGroupAssociation association = workCustomFieldGroupAssociationDAO.findByWorkAndWorkCustomFieldGroup(workId, customFieldGroup.getId());

		Assert.notNull(customField);
		Assert.notNull(customFieldGroup);
		Assert.notNull(association);
		Assert.state(!association.getDeleted());

		// We don't want multiple values for the same custom field

		SavedWorkCustomField saved = null;
		for (SavedWorkCustomField found : association.getSavedWorkCustomFields()) {
			if (found.getWorkCustomField().equals(customField)) {
				saved = found;
				break;
			}
		}

		if (saved == null) {
			saved = new SavedWorkCustomField(customField, customFieldDTO.getValue());
			saved.setWorkCustomFieldGroupAssociation(association);
			association.getSavedWorkCustomFields().add(saved);
		} else {
			saved.setValue(customFieldDTO.getValue());
		}
	}

	@Override
	public void saveWorkCustomFieldsForWorkAndIndex(WorkCustomFieldDTO[] customFieldDTOs, Long workId) {
		saveWorkCustomFieldsForWork(
			customFieldDTOs,
			workId,
			authenticationService.getCurrentUser(),
			authenticationService.getMasqueradeUserId()
		);
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workId));
	}

	@Override
	public void saveWorkCustomFieldsForWork(
		WorkCustomFieldDTO[] customFieldDTOs,
		Long workId,
		User user,
		Long onBehalfOfUserId) {

		for (WorkCustomFieldDTO dto : customFieldDTOs) {
			saveWorkCustomFieldForWork(dto, workId);
		}

		Work work = workDAO.findWorkById(workId);
		if (work != null) {
			WorkActionRequest workActionRequest =
				workActionRequestFactory.create(
					work,
					user.getId(),
					onBehalfOfUserId,
					onBehalfOfUserId,
					WorkAuditType.EDIT
				);
			workAuditService.auditWork(workActionRequest);
			// Don't fire while in draft... this also prevents the initial assignment creation from triggering this event.
			if (!WorkStatusType.DRAFT.equals(work.getWorkStatusType().getCode())) {
				webHookEventService.onWorkCustomFieldsUpdated(work.getId(), work.getCompany().getId());
			}
		}
	}

	@Override
	public void deleteWorkCustomFieldGroupFromWork(Long workId, Long customFieldGroupId) {
		if (workId == null || customFieldGroupId == null) { return; }

		WorkCustomFieldGroupAssociation workCustomFieldGroupAssociation = workCustomFieldGroupAssociationDAO.findByWorkAndWorkCustomFieldGroup(workId, customFieldGroupId);

		if (workCustomFieldGroupAssociation != null) {
			workCustomFieldGroupAssociation.setDeleted(true);
			WorkUpdateSearchIndexEvent event = new WorkUpdateSearchIndexEvent(workId);
			eventRouter.sendEvent(event);
		}
	}

	@Override
	public void deleteWorkCustomFieldGroupsFromWork(Long workId) {
		AbstractWork work = abstractWorkDAO.get(workId);
		for (WorkCustomFieldGroupAssociation a : work.getWorkCustomFieldGroupAssociations()) {
			a.setDeleted(true);
		}
	}

	@Override
	public void setWorkCustomFieldGroupsForWork(Long workId, Map<Long, Integer> workCustomFieldGroupsId) {
		if (workCustomFieldGroupsId != null && !workCustomFieldGroupsId.isEmpty()) {
			List<WorkCustomFieldGroupAssociation> customFieldGroupAssociations = workCustomFieldGroupAssociationDAO.findAllActiveByWork(workId);

			// Remove existing groups
			for (WorkCustomFieldGroupAssociation a : customFieldGroupAssociations) {
				removeWorkCustomFieldGroupForWork(a.getWorkCustomFieldGroup().getId(), workId);
			}

			//add workCustomFieldGroupsIds
			for (Map.Entry<Long, Integer> entry : workCustomFieldGroupsId.entrySet()) {
				addWorkCustomFieldGroupToWork(entry.getKey(), workId, entry.getValue());
			}
		} else {
			deleteWorkCustomFieldGroupsFromWork(workId);
		}
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workId));
	}

	@Override
	public void saveFlatWorkCustomFields(AbstractWork work, List<WorkCustomFieldGroup> groups, List<List<WorkCustomFieldDTO>> dtoSet) {
		int position = 0; // the group list is ordered by position already
		for (WorkCustomFieldGroup group : groups) {
			WorkCustomFieldGroupAssociation association = new WorkCustomFieldGroupAssociation(work, group, position++);
			workCustomFieldGroupAssociationDAO.saveOrUpdate(association);

			Map<Long, WorkCustomField> fields = Maps.newHashMap();
			for (WorkCustomField field : workCustomFieldDAO.findAllFieldsForCustomFieldGroup(group.getId())) {
				fields.put(field.getId(), field);
			}

			for (WorkCustomFieldDTO dto : dtoSet.get(groups.indexOf(group))) {
				SavedWorkCustomField saved = new SavedWorkCustomField(fields.get(dto.getId()), dto.getValue());
				saved.setWorkCustomFieldGroupAssociation(association);
				savedWorkCustomFieldDAO.saveOrUpdate(saved);
				association.getSavedWorkCustomFields().add(saved);
			}
		}
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(work.getId()));
	}

	@Override
	public Set<WorkCustomFieldGroupAssociation> findAllByWork(long workId) {
		return workCustomFieldGroupAssociationDAO.findAllByWork(workId);
	}

	@Override
	public Map<Long, String> findClientFieldSetIdsMap(Long companyId) {
		Assert.notNull(companyId);
		return workCustomFieldGroupDAO.findClientFieldSetIdsMap(companyId);
	}

	@Override
	public List<WorkCustomField> findWorkCustomFieldByIds(List<Long> workCustomFieldIds) {
		if (CollectionUtils.isNotEmpty(workCustomFieldIds)) {
			return workCustomFieldDAO.get(workCustomFieldIds);
		} else {
			return new ArrayList<>();
		}
	}

	@Override
	public List<CustomReportCustomFieldGroupDTO> findCustomReportCustomFieldGroupsForCompanyAndReport(Long companyId, Long reportId) {
		return workCustomFieldDAO.findCustomReportCustomFieldGroupsForCompanyAndReport(companyId, reportId);
	}

	@Override
	public List<WorkCustomField> findRequiredBuyerFieldsForCustomFieldGroup(Long customFieldGroupId) {
		return workCustomFieldDAO.findRequiredBuyerFieldsForCustomFieldGroup(customFieldGroupId);
	}

	@Override
	public void replaceCustomFieldGroupForWorkByPosition(Long workId,Long customFieldGroupId,List<WorkCustomFieldDTO> customFieldDTOs,  Integer position) {
		Assert.notNull(customFieldGroupId);
		Assert.notNull(workId);

		WorkCustomFieldGroupAssociation association = workCustomFieldGroupAssociationDAO.findByWorkAndWorkCustomFieldGroupPosition(workId,position);
		if (association != null) {
			association.setDeleted(true);
		}
		addWorkCustomFieldGroupToWork(customFieldGroupId, workId, position);
		saveWorkCustomFieldsForWorkAndIndex(customFieldDTOs.toArray(new WorkCustomFieldDTO[customFieldDTOs.size()]),workId);
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workId));
	}

	@Override
	public void replaceCustomFieldGroupForWork(
			final long workId,
			final long customFieldGroupId,
			final List<WorkCustomFieldDTO> customFieldDTOs) {

		WorkCustomFieldGroupAssociation association =
				workCustomFieldGroupAssociationDAO.findByWorkAndWorkCustomFieldGroup(workId, customFieldGroupId);
		int position = 0;
		if (association != null) {
			association.setDeleted(true);
			position = association.getPosition();
		}

		addWorkCustomFieldGroupToWork(customFieldGroupId, workId, position);
		saveWorkCustomFieldsForWorkAndIndex(customFieldDTOs.toArray(new WorkCustomFieldDTO[customFieldDTOs.size()]), workId);
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workId));
	}

	@Override
	public ImmutableList<Map> getProjectedCustomFieldGroups(String[] fields) throws Exception {
		List<WorkCustomFieldGroup> groups = findActiveWorkCustomFieldGroups(authenticationService.getCurrentUserCompanyId());
		return ImmutableList.copyOf(ProjectionUtilities.projectAsArray(fields, groups));
	}

	@Override
	public ImmutableList<Map> getProjectedCustomFields(long customFieldGroupId, String[] fields) throws Exception {
		Map[] customFields = ProjectionUtilities.projectAsArray(
			fields,
			ImmutableMap.of(
				"required", "requiredFlag",
				"type", "workCustomFieldType"),
			findAllFieldsForCustomFieldGroup(customFieldGroupId)
		);

		return ImmutableList.copyOf(customFields);
	}

	@Override
	public WorkCustomFieldGroup findWorkCustomFieldGroup(Long workId, Long companyId, Integer position) {
		Assert.notNull(workId);
		Assert.notNull(companyId);
		Assert.notNull(position);
		WorkCustomFieldGroupAssociation association = workCustomFieldGroupAssociationDAO.findByWorkAndWorkCustomFieldGroupPosition(workId,position);
		Assert.notNull(association);

		return association.getWorkCustomFieldGroup();
	}

	private WorkCustomFieldGroup getAndValidateFieldGroupByCompany(Long fieldGroupId, Long companyId) {
		checkNotNull(fieldGroupId);
		checkNotNull(companyId);
		WorkCustomFieldGroup group = checkNotNull(workCustomFieldGroupDAO.get(fieldGroupId));
		checkNotNull(group.getCompany());
		checkState(companyId.equals(group.getCompany().getId()));

		return group;
	}


}
