package com.workmarket.domains.work.service.state;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.state.DefaultWorkSubStatusDAO;
import com.workmarket.dao.state.WorkStatusDAO;
import com.workmarket.dao.state.WorkSubStatusDAO;
import com.workmarket.dao.state.WorkSubStatusTypeCompanySettingDAO;
import com.workmarket.dao.state.WorkSubStatusTypeWorkStatusScopeDAO;
import com.workmarket.data.report.work.WorkSubStatusTypeCompanyConfig;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.DefaultWorkSubStatusType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.changelog.work.WorkSubStatusChangeChangeLog;
import com.workmarket.domains.model.filter.WorkSubStatusTypeFilter;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.note.WorkNote;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.dao.WorkTemplateDAO;
import com.workmarket.domains.work.dao.state.WorkSubStatusTypeAssociationDAO;
import com.workmarket.domains.work.dao.state.WorkSubStatusTypeRecipientAssociationDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkTemplate;
import com.workmarket.domains.work.model.audit.WorkAuditType;
import com.workmarket.domains.work.model.audit.WorkSubStatusAuditType;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeAssociation;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeCompanySetting;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeDashboard;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeRecipientAssociation;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeWorkStatusScope;
import com.workmarket.domains.work.service.WorkChangeLogService;
import com.workmarket.domains.work.service.WorkNoteService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.domains.work.service.audit.WorkAuditService;
import com.workmarket.search.cache.HydratorCache;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.service.business.dto.WorkSubStatusTypeCompanySettingDTO;
import com.workmarket.service.business.dto.WorkSubStatusTypeDTO;
import com.workmarket.service.business.integration.hooks.webhook.WebHookEventService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.index.UpdateMultipleWorkSearchIndex;
import com.workmarket.service.infra.index.UpdateWorkSearchIndex;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.ProjectionUtilities;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.workmarket.utility.CollectionUtilities.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
public class WorkSubStatusServiceImpl implements WorkSubStatusService {

	private static final Log logger = LogFactory.getLog(WorkSubStatusServiceImpl.class);
	private static final int MAX_CODE_LENGTH = 35;

	@Autowired private WorkSubStatusDAO workSubStatusDAO;
	@Autowired private DefaultWorkSubStatusDAO defaultWorkSubStatusDAO;
	@Autowired private WorkStatusDAO workStatusDAO;
	@Autowired private WorkDAO workDAO;
	@Autowired private WorkSubStatusTypeAssociationDAO workSubStatusTypeAssociationDAO;
	@Autowired private WorkService workService;
	@Autowired private UserService userService;
	@Autowired private WorkNoteService workNoteService;
	@Autowired private WorkChangeLogService workChangeLogService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private WorkAuditService workAuditService;
	@Autowired private CompanyDAO companyDAO;
	@Autowired private WorkSubStatusTypeCompanySettingDAO workSubStatusTypeCompanySettingDAO;
	@Autowired private WorkSubStatusTypeWorkStatusScopeDAO workSubStatusTypeWorkStatusScopeDAO;
	@Autowired private WorkTemplateDAO workTemplateDAO;
	@Autowired private EventRouter eventRouter;
	@Autowired private HydratorCache hydratorCache;
	@Autowired private WebHookEventService webHookEventService;
	@Autowired private WorkSubStatusTypeRecipientAssociationDAO workSubStatusTypeRecipientAssociationDAO;

	@Override
	public WorkSubStatusType findWorkStatusById(Long statusId) {
		Assert.notNull(statusId);
		return workSubStatusDAO.get(statusId);
	}

	@Override
	@UpdateWorkSearchIndex(workIdArgumentPosition = 2)
	public void addSystemSubStatus(User user, Long workId, String workSubStatusCode) {
		addSubStatus(user, workId, workSubStatusCode, StringUtils.EMPTY);
	}

	@Override
	@UpdateWorkSearchIndex(workIdArgumentPosition = 1)
	public void addSystemSubStatus(Long workId, String workSubStatusCode, String transitionNote) {
		addSubStatus(authenticationService.getCurrentUser(), workId, workSubStatusCode, transitionNote);
	}

	@Override
	@UpdateWorkSearchIndex(workIdArgumentPosition = 1)
	public void addSystemSubStatus(Long workId, Long workerId, String workSubStatusCode, String transitionNote) {
		addSubStatus(userService.getUser(workerId), workId, workSubStatusCode, transitionNote);
	}

	@Override
	public boolean validateAddSubStatus(long workId, long workSubStatusId) {
		WorkSubStatusType workSubStatusType = workSubStatusDAO.get(workSubStatusId);
		Assert.notNull(workSubStatusType);

		Work work = workDAO.get(workId);
		Assert.notNull(work);

		if (workSubStatusType.isCustom()) {
			if (!work.getCompany().getId().equals(workSubStatusType.getCompany().getId()) || !isWorkSubStatusApplicableForWork(workSubStatusType, work)) {
				return false;
			}
		}

		return true;
	}

	@Override
	@UpdateWorkSearchIndex(workIdArgumentPosition = 1)
	public void addSubStatus(Long workId, Long workSubStatusId, String transitionNote) {
		Assert.notNull(workSubStatusId);
		WorkSubStatusType workSubStatusType = workSubStatusDAO.get(workSubStatusId);
		Assert.notNull(workSubStatusType, "Unable to find label");
		addSubStatus(authenticationService.getCurrentUser(), workId, workSubStatusType, transitionNote);
	}

	@Override
	@UpdateMultipleWorkSearchIndex(workIdsArgumentPosition = 1)
	public void addSubStatus(List<Long> workIds, Long workSubStatusId, String transitionNote, DateRange dateRange) {
		Assert.notNull(workSubStatusId);
		Assert.notNull(dateRange);
		WorkSubStatusType workSubStatusType = workSubStatusDAO.get(workSubStatusId);
		Assert.notNull(workSubStatusType, "Unable to find label");
		for (Long workId : workIds) {
			addSubStatus(authenticationService.getCurrentUser(), workId, workSubStatusType, transitionNote);
			workService.setAppointmentTime(workId, dateRange, null);
		}
	}

	@Override
	@UpdateMultipleWorkSearchIndex(workIdsArgumentPosition = 1)
	public void addSubStatus(List<Long> workIds, Long workSubStatusId, String transitionNote) {
		Assert.notNull(workSubStatusId);
		WorkSubStatusType workSubStatusType = workSubStatusDAO.get(workSubStatusId);
		Assert.notNull(workSubStatusType, "Unable to find label");
		for (Long workId : workIds) {
			addSubStatus(authenticationService.getCurrentUser(), workId, workSubStatusType, transitionNote);
		}
	}

	private void addSubStatus(User user, Long workId, String workSubStatusCode, String transitionNote) {
		Assert.hasText(workSubStatusCode);
		WorkSubStatusType workSubStatusType = workSubStatusDAO.findSystemWorkSubStatus(workSubStatusCode);
		Assert.notNull(workSubStatusType, workSubStatusCode + " is not a system label");
		Assert.isTrue(!workSubStatusType.isCustom(), workSubStatusCode + " is a custom label");
		addSubStatus(user, workId, workSubStatusType, transitionNote);
	}

	private void addSubStatus(User user, Long workId, WorkSubStatusType workSubStatusType, String transitionNote) {
		Assert.notNull(workId);
		Assert.notNull(workSubStatusType);

		Work work = workDAO.get(workId);
		Assert.notNull(work, "Unable to find work");

		if (workSubStatusType.isCustom()) {
			Assert.isTrue(work.getCompany().getId().equals(workSubStatusType.getCompany().getId()), "Custom label doesn't belong to company id:" + work.getCompany().getId());
			if (workSubStatusType.isNoteRequired()) {
				Assert.isTrue(StringUtils.isNotBlank(transitionNote), "Custom label " + workSubStatusType.getCode() + " requires a note");
			}
			Assert.isTrue(isWorkSubStatusApplicableForWork(workSubStatusType, work), "You cannot apply this label to this work");
		}

		addValidatedSubStatus(user, workId, workSubStatusType, transitionNote);
	}

	private void addValidatedSubStatus(User user, Long workId, WorkSubStatusType workSubStatusType, String transitionNote) {
		Work work = workDAO.get(workId);
		Assert.notNull(work, "Unable to find work");

		WorkSubStatusTypeAssociation association = workSubStatusTypeAssociationDAO.findByWorkSubStatusAndWorkId(workSubStatusType.getId(), workId);

		if (association == null) {
			association = newWorkSubStatusTypeAssociation(work, workSubStatusType);
		}

		association.setDeleted(false);
		association.setResolved(false);

		WorkNote note = workNoteService.addWorkSubStatusTransitionNote(user, work, workSubStatusType, transitionNote);
		if (note != null) {
			association.setTransitionNote(note);
		}

		workChangeLogService.saveWorkChangeLog(new WorkSubStatusChangeChangeLog(workId, user.getId(), authenticationService.getMasqueradeUserId(), null,
			null, association.getWorkSubStatusType(), association.getTransitionNote()
		));

		userNotificationService.onWorkSubStatus(workId, user.getId(), association);
		auditSubStatus(work, user, association);

		webHookEventService.onLabelAdded(workId, work.getCompany().getId(), association.getId());
	}

	private void auditSubStatus(Work work, User user, WorkSubStatusTypeAssociation workSubStatusTypeAssociation) {
		WorkActionRequest workAuditRequest = new WorkActionRequest();
		workAuditRequest.setAuditType(WorkAuditType.ADD_SUBSTATUS);
		workAuditRequest.setWorkId(work.getId());
		workAuditRequest.setModifierId(user.getId());
		User masqUser = authenticationService.getMasqueradeUser();
		if (masqUser != null) {
			workAuditRequest.setMasqueradeId(masqUser.getId());
		}
		workAuditRequest.setLastActionOn(Calendar.getInstance());
		workAuditService.auditWork(workAuditRequest);
		workAuditService.auditWorkSubStatus(WorkSubStatusAuditType.ADD_SUBSTATUS, workSubStatusTypeAssociation);
	}

	private WorkSubStatusTypeAssociation newWorkSubStatusTypeAssociation(Work work, WorkSubStatusType workSubStatusType) {
		WorkSubStatusTypeAssociation association = new WorkSubStatusTypeAssociation();
		association.setWork(work);
		association.setWorkSubStatusType(workSubStatusType);
		workSubStatusTypeAssociationDAO.saveOrUpdate(association);
		return association;
	}

	@Override
	@UpdateWorkSearchIndex(workIdArgumentPosition = 2)
	public void resolveSubStatus(Long userId, Long workId, Long workSubStatusId, String transitionNote) {
		Assert.notNull(userId);
		Assert.notNull(workId);
		Assert.notNull(workSubStatusId);

		WorkSubStatusTypeAssociation association = workSubStatusTypeAssociationDAO.findByWorkSubStatusAndWorkId(workSubStatusId, workId);

		if (association == null || association.isResolved()) {
			return;
		}

		Assert.state(association.getWork().getId().equals(workId), "Invalid operation");
		Assert.state(association.getWorkSubStatusType().isUserResolvable(), "The WorkSubStatusDescriptor requires an action to be resolved.");

		User resolvedBy = userService.getUser(userId);
		association.setResolved(true);
		association.setResolvedBy(resolvedBy);

		if (StringUtils.isNotBlank(transitionNote)) {
			String message = "Resolved the label " + association.getWorkSubStatusType().getDescription() + " \n" + transitionNote;
			NoteDTO noteDTO = new NoteDTO(message);
			if (!association.getWorkSubStatusType().getResourceVisible()) {
				noteDTO.setIsPrivate(true);
			}
			Note note = workNoteService.addNoteToWork(workId, noteDTO);
			association.setTransitionNote((WorkNote) note);
		}

		workSubStatusTypeAssociationDAO.saveOrUpdate(association);
		workChangeLogService.saveWorkChangeLog(
			new WorkSubStatusChangeChangeLog(
				workId, userId, authenticationService.getMasqueradeUserId(), null, association.getWorkSubStatusType(), null, association.getTransitionNote()
			)
		);

		webHookEventService.onLabelRemoved(workId, association.getWork().getCompany().getId(), association.getId());
		workAuditService.auditWorkSubStatus(WorkSubStatusAuditType.RESOLVE_SUBSTATUS, association);
		auditSubstatus(workId, resolvedBy, WorkAuditType.RESOLVE_SUBSTATUS);
	}

	@Override
	@UpdateWorkSearchIndex(workIdArgumentPosition = 2)
	public void resolveRequiresRescheduleSubStatus(Long userId, Long workId) {
		List<WorkSubStatusType> unresolvedSubStatuses = workSubStatusDAO.findAllUnresolvedSubStatusTypeByWork(workId);
		User resolvedBy = userService.getUser(userId);

		for (WorkSubStatusType subStatus : unresolvedSubStatuses) {
			if (subStatus.isRemoveAfterReschedule()) {
				WorkSubStatusTypeAssociation association = workSubStatusTypeAssociationDAO.findByWorkSubStatusAndWorkId(subStatus.getId(), workId);

				association.setResolved(true);
				association.setResolvedBy(resolvedBy);

				workSubStatusTypeAssociationDAO.saveOrUpdate(association);
				workChangeLogService.saveWorkChangeLog(
					new WorkSubStatusChangeChangeLog(
						workId, userId, authenticationService.getMasqueradeUserId(), null, association.getWorkSubStatusType(), null, association.getTransitionNote()
					)
				);

				webHookEventService.onLabelRemoved(workId, association.getWork().getCompany().getId(), association.getId());
				workAuditService.auditWorkSubStatus(WorkSubStatusAuditType.RESOLVE_SUBSTATUS, association);
				auditSubstatus(workId, resolvedBy, WorkAuditType.RESOLVE_SUBSTATUS);
			}
		}
	}

	@Override
	@UpdateWorkSearchIndex(workIdArgumentPosition = 1)
	public void resolveSystemSubStatusByAction(Long workId, String... workSubStatusCodes) {
		Assert.notNull(workId);
		User resolvedBy = authenticationService.getCurrentUser();

		for (String code : workSubStatusCodes) {
			WorkSubStatusType subStatus = findSystemWorkSubStatus(code);
			if (subStatus != null) {
				WorkSubStatusTypeAssociation association = workSubStatusTypeAssociationDAO.findByWorkSubStatusAndWorkId(subStatus.getId(), workId);
				if (association != null && !association.isResolved() && association.getWorkSubStatusType().isActionResolvable()) {
					association.setResolved(true);
					association.setResolvedBy(resolvedBy);
					workSubStatusTypeAssociationDAO.saveOrUpdate(association);
					workChangeLogService.saveWorkChangeLog(
						new WorkSubStatusChangeChangeLog(
							workId, resolvedBy.getId(), authenticationService.getMasqueradeUserId(), null, association.getWorkSubStatusType(), null, null
						)
					);
					workAuditService.auditWorkSubStatus(WorkSubStatusAuditType.RESOLVE_SUBSTATUS_BY_ACTION, association);
					webHookEventService.onLabelRemoved(workId, association.getWork().getCompany().getId(), association.getId());
				}
			}
		}
		auditSubstatus(workId, resolvedBy, WorkAuditType.RESOLVE_SUBSTATUS_BY_ACTION);
	}

	private void auditSubstatus(Long workId, User resolvedBy,
								WorkAuditType workAuditType) {
		WorkActionRequest workAuditRequest = new WorkActionRequest();
		workAuditRequest.setAuditType(workAuditType);
		workAuditRequest.setWorkId(workId);
		workAuditRequest.setModifierId(resolvedBy.getId());
		User masqUser = authenticationService.getMasqueradeUser();
		if (masqUser != null) {
			workAuditRequest.setMasqueradeId(masqUser.getId());
		}
		workAuditRequest.setLastActionOn(Calendar.getInstance());
		workAuditService.auditWork(workAuditRequest);
	}

	@Override
	@UpdateWorkSearchIndex(workIdArgumentPosition = 2)
	public void addSystemSubstatusAndResolve(User user, Long workId, String newWorkSubStatusCode, String... resolveSubstatuses) {
		Assert.notNull(workId);
		Assert.hasText(newWorkSubStatusCode);

		addSubStatus(user, workId, newWorkSubStatusCode, StringUtils.EMPTY);
		resolveSystemSubStatusByAction(workId, resolveSubstatuses);
	}

	@Override
	public List<WorkSubStatusType> findAllSubStatuses(boolean clientVisible, boolean resourceVisible) {
		WorkSubStatusTypeFilter workSubStatusTypeFilter = new WorkSubStatusTypeFilter();
		workSubStatusTypeFilter.setClientVisible(clientVisible);
		workSubStatusTypeFilter.setResourceVisible(resourceVisible);
		return findAllSubStatuses(workSubStatusTypeFilter);
	}

	@Override
	public List<WorkSubStatusType> findAllSubStatuses(WorkSubStatusTypeFilter workSubStatusTypeFilter) {
		User user = authenticationService.getCurrentUser();
		return findAllSubStatuses(user.getCompany().getId(), workSubStatusTypeFilter);
	}

	@Override
	public List<WorkSubStatusType> findAllEditableSubStatusesByWork(long workId, WorkSubStatusTypeFilter workSubStatusTypeFilter) {
		Work work = workDAO.findWorkById(workId);
		if (!work.isActive()) {
			workSubStatusTypeFilter.setShowRequiresRescheduleSubStatus(false);
		}
		workSubStatusTypeFilter.setWorkId(workId);
		return findAllSubStatuses(work.getCompany().getId(), workSubStatusTypeFilter);
	}

	@Override
	public List<WorkSubStatusType> findAllWorkUploadSubStatuses()  {

		Long companyId = authenticationService.getCurrentUserCompanyId();

		WorkSubStatusTypeFilter workSubStatusTypeFilter = new WorkSubStatusTypeFilter()
			.setShowSystemSubStatus(true)
			.setShowCustomSubStatus(true)
			.setShowDeactivated(true)
			.setClientVisible(true)
			.setResourceVisible(true)
			.setShowCustomSubStatus(true)
			.setTriggeredBy(Lists.newArrayList(
				WorkSubStatusType.TriggeredBy.CLIENT_OR_RESOURCE.toString(),
				WorkSubStatusType.TriggeredBy.CLIENT.toString()));

		List<WorkSubStatusType> labels = workSubStatusDAO.findAllSubStatusesByCompany(workSubStatusTypeFilter, companyId);

		// Filter labels excluding those with noteRequired and those that have scopes but not DRAFT scope
		return ImmutableList.copyOf(Iterables.filter(labels, new Predicate<WorkSubStatusType>() {
			@Override
			public boolean apply(@Nullable WorkSubStatusType label) {
				if (label != null && label.isNoteRequired()) {
					return false;
				}
				Set<WorkSubStatusTypeWorkStatusScope> scopes = findAllScopesForSubStatusId(label != null ? label.getId() : 0L);
				if (isEmpty(scopes)) {
					return true;
				}
				for (WorkSubStatusTypeWorkStatusScope scope : scopes) {
					if (WorkStatusType.DRAFT.equals(scope.getWeak().getCode())) {
						return true;
					}
				}
				return false;
			}
		}));
	}

	@Override
	public ImmutableList<Map> findAllWorkUploadLabels(String[] fields) throws Exception {
		List<WorkSubStatusType> workUploadLabels = findAllWorkUploadSubStatuses();
		return ImmutableList.copyOf(ProjectionUtilities.projectAsArray(
			fields,
			workUploadLabels
		));
	}

	@Override
	public List<WorkSubStatusType> findAllSubStatuses(long companyId, WorkSubStatusTypeFilter workSubStatusTypeFilter) {
		Assert.notNull(workSubStatusTypeFilter);
		List<String> triggeredBy = Lists.newArrayList(WorkSubStatusType.TriggeredBy.CLIENT_OR_RESOURCE.toString());
		if (workSubStatusTypeFilter.isClientVisible()) {
			triggeredBy.add(WorkSubStatusType.TriggeredBy.CLIENT.toString());
		}
		workSubStatusTypeFilter.setTriggeredBy(triggeredBy);
		return workSubStatusDAO.findAllSubStatusesByCompany(workSubStatusTypeFilter, companyId);
	}

	@Override
	public WorkSubStatusTypeDashboard findWorkSubStatusDashboardByCompany(WorkSubStatusTypeFilter workSubStatusTypeFilter) {
		User user = authenticationService.getCurrentUser();
		WorkSubStatusTypeDashboard dashboard = new WorkSubStatusTypeDashboard();
		List<WorkSubStatusType> subStatuses = workSubStatusDAO.findAllSubStatusesByCompany(workSubStatusTypeFilter, user.getCompany().getId());
		List<WorkSubStatusTypeCompanySetting> settings = workSubStatusTypeCompanySettingDAO.findWorkSubStatusTypeCompanySettingByCompany(user.getCompany().getId());
		dashboard.setWorkSubStatusList(subStatuses);
		for (WorkSubStatusTypeCompanySetting s : settings) {
			dashboard.getWorkSubStatusTypeCompanySettingsMap().put(s.getWorkSubStatusType().getId(), s);
		}
		return dashboard;
	}

	@Override
	public List<WorkSubStatusType> findAllUnResolvedSubStatuses(Long workId) {
		Assert.notNull(workId, "Work Id is required");
		return workSubStatusTypeAssociationDAO.findAllUnResolvedSubStatuses(workId);
	}

	@SuppressWarnings("unchecked")
	private WorkSubStatusType saveOrUpdateCustomWorkSubStatus(Company company, WorkSubStatusTypeDTO workSubStatusTypeDTO) {
		Assert.hasText(workSubStatusTypeDTO.getCode(), "Code is required ");
		Assert.notNull(company, "Unable to find company");

		WorkSubStatusType subStatus = null;
		if (workSubStatusTypeDTO.getWorkSubStatusTypeId() != null) {
			subStatus = workSubStatusDAO.get(workSubStatusTypeDTO.getWorkSubStatusTypeId());
		} else if (company.getId() != null && workSubStatusTypeDTO.getCode() != null) {
			subStatus = workSubStatusDAO.findByCodeAndCompany(workSubStatusTypeDTO.getCode(), company.getId());
		}

		if (subStatus == null) {
			subStatus = new WorkSubStatusType(company);
		}

		subStatus.setCode(workSubStatusTypeDTO.getCode().toLowerCase());
		subStatus.setDescription(
			StringUtils.isNotEmpty(workSubStatusTypeDTO.getDescription()) ? workSubStatusTypeDTO.getDescription() : workSubStatusTypeDTO.getCode()
		);
		subStatus.setAlert(workSubStatusTypeDTO.isAlert());
		subStatus.setIncludeInstructions(workSubStatusTypeDTO.isIncludeInstructions());
		subStatus.setInstructions(workSubStatusTypeDTO.getInstructions());
		subStatus.setNoteRequired(workSubStatusTypeDTO.isNoteRequired());
		subStatus.setNotifyClientEnabled(workSubStatusTypeDTO.isNotifyClientEnabled());
		subStatus.setNotifyResourceEnabled(workSubStatusTypeDTO.isNotifyResourceEnabled());
		subStatus.setScheduleRequired(workSubStatusTypeDTO.isScheduleRequired());
		subStatus.setResourceVisible(workSubStatusTypeDTO.isResourceVisible());
		subStatus.setRemoveAfterReschedule(workSubStatusTypeDTO.isRemoveAfterReschedule());
		subStatus.setActive(workSubStatusTypeDTO.isActive());
		subStatus.setRemoveOnVoidOrCancelled(workSubStatusTypeDTO.isRemoveOnVoidOrCancelled());
		subStatus.setRemoveOnPaid(workSubStatusTypeDTO.isRemoveOnPaid());
		subStatus.setNotePrivacy(workSubStatusTypeDTO.getNotePrivacyType());
		subStatus.setDeleted(false);

		if (workSubStatusTypeDTO.getWorkTemplateIds() != null) {
			Set<WorkTemplate> templates;
			if (workSubStatusTypeDTO.getWorkTemplateIds().length == 0) {
				templates = Sets.newLinkedHashSet();
			} else {
				templates = Sets.newLinkedHashSet(workTemplateDAO.get(workSubStatusTypeDTO.getWorkTemplateIds()));
			}
			if (templates.size() != workSubStatusTypeDTO.getWorkTemplateIds().length) {
				logger.warn("Invalid template ids found in this collection: " + Arrays.toString(workSubStatusTypeDTO.getWorkTemplateIds()));
			}
			subStatus.setWorkTemplates(templates);
		}

		if (workSubStatusTypeDTO.getWorkStatusCodes() != null) {
			Set<WorkStatusType> statusCodes;
			if (ArrayUtils.isEmpty(workSubStatusTypeDTO.getWorkStatusCodes())) {
				statusCodes = Sets.newLinkedHashSet();
			} else {
				statusCodes = Sets.newLinkedHashSet(workStatusDAO.findByCode(workSubStatusTypeDTO.getWorkStatusCodes()));
			}
			if (statusCodes.size() != workSubStatusTypeDTO.getWorkStatusCodes().length) {
				logger.warn("Invalid work status codes found in this collection: " + Arrays.toString(workSubStatusTypeDTO.getWorkStatusCodes()));
			}
			subStatus.setWorkScope(statusCodes);
		}

		if (workSubStatusTypeDTO.isResourceEditable()) {
			subStatus.setTriggeredBy(WorkSubStatusType.TriggeredBy.CLIENT_OR_RESOURCE);
		} else {
			subStatus.setTriggeredBy(WorkSubStatusType.TriggeredBy.CLIENT);
		}

		workSubStatusDAO.saveOrUpdate(subStatus);

		if (workSubStatusTypeDTO.getWorkSubStatusTypeRecipientIds() != null) {
			Set<String> recipientNumbers;
			if (ArrayUtils.isEmpty(workSubStatusTypeDTO.getWorkSubStatusTypeRecipientIds())) {
				recipientNumbers = Collections.emptySet();
			 } else {
				recipientNumbers = Sets.newHashSet(workSubStatusTypeDTO.getWorkSubStatusTypeRecipientIds());
			}

			List<Long> persistentRecipientIds = findAllRecipientsByWorkSubStatusId(subStatus.getId());
			if (recipientNumbers.isEmpty() && !persistentRecipientIds.isEmpty()) {
				for (Long id : persistentRecipientIds) {
					User recipient = userService.getUser(id);
					deleteWorkSubStatusTypeRecipientAssociation(recipient.getId(), subStatus.getId());
				}
			} else if (!recipientNumbers.isEmpty() && !persistentRecipientIds.isEmpty()) {
				for (Long id : persistentRecipientIds) {
					String userNumber = userService.getUser(id).getUserNumber();
					if (!recipientNumbers.contains(userNumber)) {
						deleteWorkSubStatusTypeRecipientAssociation(id, subStatus.getId());
					} else {
						recipientNumbers.remove(userNumber);
					}
				}
			}
			saveOrUpdateWorkSubStatusTypeRecipientAssociation(subStatus, recipientNumbers);
		}

		return subStatus;
	}

	@Override
	public WorkSubStatusType saveOrUpdateCustomWorkSubStatus(WorkSubStatusTypeDTO workSubStatusTypeDTO) {
		Assert.notNull(workSubStatusTypeDTO.getCompanyId(), "Invalid companyId");
		Company company = companyDAO.findById(workSubStatusTypeDTO.getCompanyId());
		return saveOrUpdateCustomWorkSubStatus(company, workSubStatusTypeDTO);
	}

	@Override
	public WorkSubStatusType deleteWorkSubStatus(long workSubStatusId) {
		WorkSubStatusType subStatus = workSubStatusDAO.get(workSubStatusId);
		Assert.notNull(subStatus, "Unable to find label");
		Assert.isTrue(subStatus.isCustom(), "Can't delete a system label");
		User user = authenticationService.getCurrentUser();
		Assert.isTrue(user.getCompany().getId().equals(subStatus.getCompany().getId()), "User not authorized");

		subStatus.setDeleted(true);

		Calendar now = Calendar.getInstance();
		String suffix = "-" + now.getTimeInMillis();
		String code = subStatus.getCode();
		int position = code.length() + suffix.length() > MAX_CODE_LENGTH ? MAX_CODE_LENGTH - suffix.length() : code.length();

		subStatus.setCode(code.substring(0, position) + suffix);

		return subStatus;
	}


	@Override
	public WorkSubStatusType findSystemWorkSubStatus(String code) {
		Assert.hasText(code);
		return workSubStatusDAO.findSystemWorkSubStatus(code);
	}

	@Override
	public WorkSubStatusType findCustomWorkSubStatus(String code, Long companyId) {
		Assert.hasText(code);
		Assert.notNull(companyId);
		return workSubStatusDAO.findCustomWorkSubStatus(code, companyId);
	}

	@Override
	public void saveOrUpdateAssociation(WorkSubStatusTypeAssociation workSubStatusTypeAssociation) {
		Assert.notNull(workSubStatusTypeAssociation);
		workSubStatusTypeAssociationDAO.saveOrUpdate(workSubStatusTypeAssociation);
	}

	@Override
	public WorkSubStatusTypeAssociation getAssociation(Long id) {
		Assert.notNull(id);
		return workSubStatusTypeAssociationDAO.get(id);
	}

	@Override
	public WorkSubStatusTypeCompanySetting findColorByIdAndCompany(Long id, Long companyId) {
		Assert.notNull(id);
		Assert.notNull(companyId);
		return workSubStatusTypeCompanySettingDAO.findWorkSubStatusTypeCompanySettingByWorkSubStatusAndCompany(id, companyId);
	}

	public WorkSubStatusType findWorkSubStatus(long workSubStatusId) {
		return workSubStatusDAO.findById(workSubStatusId);
	}

	@Override
	public WorkSubStatusType findCustomWorkSubStatusByCompany(long workSubStatusId, long companyId) {
		return workSubStatusDAO.findByIdAndCompany(workSubStatusId, companyId);
	}

	@Override
	public WorkSubStatusTypeCompanySetting saveWorkSubStatusTypeCompanySetting(long companyId, WorkSubStatusTypeCompanySettingDTO dto) {
		Company company = companyDAO.findById(companyId);
		Assert.notNull(company);
		return saveWorkSubStatusTypeCompanySetting(company, dto);
	}

	private WorkSubStatusTypeCompanySetting saveWorkSubStatusTypeCompanySetting(Company company, WorkSubStatusTypeCompanySettingDTO dto) {
		Assert.notNull(dto);
		Assert.notNull(dto.getWorkSubStatusTypeId());

		WorkSubStatusTypeCompanySetting settings = workSubStatusTypeCompanySettingDAO.findWorkSubStatusTypeCompanySettingByWorkSubStatusAndCompany(dto.getWorkSubStatusTypeId(), company.getId());

		if (settings == null) {
			settings = new WorkSubStatusTypeCompanySetting();
			WorkSubStatusType subStatus = workSubStatusDAO.get(dto.getWorkSubStatusTypeId());
			Assert.notNull(subStatus);
			settings.setCompany(company);
			settings.setWorkSubStatusType(subStatus);
		}

		if (dto.getColorRgb() != null) {
			settings.setColorRgb(dto.getColorRgb());
		}

		if (dto.getDashboardDisplayType() != null) {
			settings.setDashboardDisplayType(dto.getDashboardDisplayType());
		}

		workSubStatusTypeCompanySettingDAO.saveOrUpdate(settings);

		// update cache
		WorkSubStatusTypeCompanyConfig workSubStatusTypeCompanyConfig = new WorkSubStatusTypeCompanyConfig();
		workSubStatusTypeCompanyConfig.setColorRgb(settings.getColorRgb());
		workSubStatusTypeCompanyConfig.setDashboardDisplayType(settings.getDashboardDisplayType());
		workSubStatusTypeCompanyConfig.setCompanyId(company.getId());
		hydratorCache.updateWorkLabel(dto.getWorkSubStatusTypeId(), workSubStatusTypeCompanyConfig);

		return settings;
	}

	@Override
	public List<WorkSubStatusType> findAllUnresolvedSubStatusWithColor(Long workId) {
		Assert.notNull(workId);
		return workSubStatusDAO.findAllUnresolvedSubStatusTypeWithColorByWork(workId);
	}

	@Override
	public void addDefaultWorkSubStatusToCompany(Company company) {
		List<DefaultWorkSubStatusType> subStatusTypeList = defaultWorkSubStatusDAO.findAll();
		for (DefaultWorkSubStatusType subStatusType : subStatusTypeList) {
			WorkSubStatusTypeDTO dto = new WorkSubStatusTypeDTO();
			BeanUtilities.copyProperties(dto, subStatusType);
			dto.setCompanyId(company.getId());
			if (subStatusType.getSubStatusDescriptor() != null) {
				dto.setResourceEditable(WorkSubStatusType.TriggeredBy.CLIENT_OR_RESOURCE.equals(subStatusType.getSubStatusDescriptor().getTriggeredBy()));
			}
			saveOrUpdateCustomWorkSubStatus(company, dto);
		}
	}

	/**
	 * Determines whether or not the label is applicable for a given work
	 *
	 * @param subStatus The label to be applied
	 * @param work      The work to apply the label to
	 * @return True if the label is applicable for the given work
	 */
	private boolean isWorkSubStatusApplicableForWork(WorkSubStatusType subStatus, Work work) {
		if (!subStatus.isWorkSubStatusApplicableForWorkStatusType(work.getWorkStatusType())) {
			return false;
		}

		/* Are templates applicable for this work? */
		if (authenticationService.getCurrentUser().getCompany().getManageMyWorkMarket().getCustomFormsEnabledFlag()) {
			Set<WorkTemplate> templates = subStatus.getWorkTemplates();
			if (isNotEmpty(templates) && (work.getTemplate() == null || !templates.contains(work.getTemplate()))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Resolves all custom labels that are no longer applicable for a given work
	 *
	 * @param work The work whose labels need to be resolved
	 */
	@SuppressWarnings("unchecked")
	public void resolveAllInapplicableCustomWorkSubStatuses(Work work) {
		User currentUser = authenticationService.getCurrentUser();
		List<WorkSubStatusType> subStatusTypes = findAllUnResolvedSubStatuses(work.getId());
		for (WorkSubStatusType subStatus : subStatusTypes) {
			if (!isWorkSubStatusApplicableForWork(subStatus, work)) {
				resolveSubStatus(currentUser.getId(), work.getId(), subStatus.getId(), "");
			}
		}
	}

	@Override
	public List<WorkSubStatusTypeAssociation> findAllWorkSubStatusTypeAssociationBySubStatusId(long workSubStatusId) {
		return workSubStatusTypeAssociationDAO.findByWorkSubStatusId(workSubStatusId);
	}

	@Override
	public Set<WorkSubStatusTypeWorkStatusScope> findAllScopesForSubStatusId(long workSubStatusId) {
		return Sets.newHashSet(workSubStatusTypeWorkStatusScopeDAO.findAllBySubStatusId(workSubStatusId));
	}

	@Override
	public List<Long> findAllRecipientsByWorkSubStatusId(Long workSubStatusId) {
		Assert.notNull(workSubStatusId);
		return workSubStatusTypeRecipientAssociationDAO.findRecipientsByWorkSubStatusId(workSubStatusId);
	}

	@Override
	public List<Long> findAllRecipientsByWorkSubStatusCodeAndCompany(String workSubStatusCode, Long companyId) {
		Assert.notNull(workSubStatusCode);
		Assert.notNull(companyId);
		return workSubStatusTypeRecipientAssociationDAO.findRecipientsByWorkSubStatusCodeAndCompanyId(workSubStatusCode, companyId);
	}

	@Override
	public List<String> findAllRecipientsUserNumbersByWorkSubStatusId(Long workSubStatusId) {
		Assert.notNull(workSubStatusId);
		List<Long> recipientIds = findAllRecipientsByWorkSubStatusId(workSubStatusId);
		List<String> recipientNumbers = new ArrayList<>(recipientIds.size());
		for (Long id : recipientIds) {
			recipientNumbers.add(userService.getUser(id).getUserNumber());
		}
		return recipientNumbers;
	}

	@Override
	public void saveOrUpdateWorkSubStatusTypeRecipientAssociation(WorkSubStatusType workSubStatus, Set<String> recipients) {
		Assert.notNull(workSubStatus);
		for (String recipient : recipients) {
			Long userId = userService.findUserId(recipient);
			WorkSubStatusTypeRecipientAssociation association = new WorkSubStatusTypeRecipientAssociation();
			association.setWorkSubStatusType(workSubStatus);
			association.setRecipient(userService.getUser(userId));
			workSubStatusTypeRecipientAssociationDAO.saveOrUpdate(association);
		}
	}

	@Override
	public void deleteWorkSubStatusTypeRecipientAssociation(long recipientId, long workSubStatusId) {
		WorkSubStatusTypeRecipientAssociation association = workSubStatusTypeRecipientAssociationDAO
			.findUniqueAssociationByUserIdAndWorkSubStatusId(recipientId, workSubStatusId);
		workSubStatusTypeRecipientAssociationDAO.delete(association);
	}

	@Override
	public void deleteAllWorkSubStatusTypeRecipientAssociationsByWorkSubStatusId(long workSubStatusId) {
		List<WorkSubStatusTypeRecipientAssociation> associations =
			workSubStatusTypeRecipientAssociationDAO.findAssociationsByWorkSubStatusId(workSubStatusId);
		workSubStatusTypeRecipientAssociationDAO.delete(Sets.newHashSet(associations));
	}
}
