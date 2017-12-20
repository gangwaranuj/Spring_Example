package com.workmarket.domains.work.service.state;

import com.google.common.collect.ImmutableList;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.filter.WorkSubStatusTypeFilter;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeAssociation;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeCompanySetting;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeDashboard;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeWorkStatusScope;
import com.workmarket.service.business.dto.WorkSubStatusTypeCompanySettingDTO;
import com.workmarket.service.business.dto.WorkSubStatusTypeDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface WorkSubStatusService {
	WorkSubStatusType findWorkStatusById(Long statusId);

	/**
	 * Adds a SYSTEM sub status to an assignment, sets the user as the actor.
	 *
	 * @param user
	 * @param workId
	 * @param workSubStatusCode
	 * @
	 */
	void addSystemSubStatus(User user, Long workId, String workSubStatusCode);

	/**
	 * Adds a SYSTEM sub status to an assignment, sets the current user as the actor and adds a note.
	 *
	 * @param workId
	 * @param workSubStatusCode
	 * @param transitionNote
	 * @
	 */
	void addSystemSubStatus(Long workId, String workSubStatusCode, String transitionNote);

	void addSystemSubStatus(Long workId, Long workerId, String workSubStatusCode, String transitionNote);

	/**
	 * Checks whether it is legal for a user to add a given substatus to a given assignment
	 *  @param workId
	 * @param workSubStatusId
	 */
	boolean validateAddSubStatus(long workId, long workSubStatusId);

	/**
	 * Adds a sub status to an assignment, sets the current user as the actor and adds a note.
	 *
	 * @param workId
	 * @param workSubStatusId
	 * @param transitionNote
	 */
	void addSubStatus(Long workId, Long workSubStatusId, String transitionNote);


	/**
	 * Adds a sub status to an assignment, sets the current user as the actor and adds a note.
	 *
	 * @param workIds
	 * @param workSubStatusId
	 * @param transitionNote
	 * @param dateRange
	 */
	void addSubStatus(List<Long> workIds, Long workSubStatusId, String transitionNote, DateRange dateRange);

	/**
	 * Adds a sub status to an assignment, sets the current user as the actor and adds a note.
	 *
	 * @param workIds
	 * @param workSubStatusId
	 * @param transitionNote
	 */
	void addSubStatus(List<Long> workIds, Long workSubStatusId, String transitionNote);

	/**
	 * Resolves a sub status.
	 *
	 * @param userId
	 * @param workId
	 * @param workSubStatusId
	 * @param transitionNote
	 * @
	 */
	void resolveSubStatus(Long userId, Long workId, Long workSubStatusId, String transitionNote);

	void resolveRequiresRescheduleSubStatus(Long userId, Long workId);

	/**
	 * Resolves the sub-statuses that are "action resolvable".
	 *
	 * @param workId
	 * @param workSubStatusCodes
	 * @
	 */
	void resolveSystemSubStatusByAction(Long workId, String... workSubStatusCodes);

	void addSystemSubstatusAndResolve(User user, Long workId, String newWorkSubStatusCode, String ... resolveSubstatuses);

	/**
	 * Finds all the sub-statuses visible to the resource and/or client.
	 *
	 * @param clientVisible
	 * @param resourceVisible
	 * @return
	 */
	List<WorkSubStatusType> findAllSubStatuses(boolean clientVisible, boolean resourceVisible);

	/**
	 * Finds all the sub-statuses visible to the resource and/or client. Can filter deactivated sub statuses.
	 *
	 * @param workSubStatusTypeFilter
	 * @return
	 */
	List<WorkSubStatusType> findAllSubStatuses(WorkSubStatusTypeFilter workSubStatusTypeFilter);

	List<WorkSubStatusType> findAllWorkUploadSubStatuses();

	ImmutableList<Map> findAllWorkUploadLabels(String[] fields) throws Exception;

	List<WorkSubStatusType> findAllSubStatuses(long companyId, WorkSubStatusTypeFilter workSubStatusTypeFilter);

	List<WorkSubStatusType> findAllEditableSubStatusesByWork(long workId, WorkSubStatusTypeFilter workSubStatusTypeFilter);

	/**
	 * Finds all the unresolved sub-statuses by work Id.
	 *
	 * @param workId
	 * @return
	 * @
	 */
	List<WorkSubStatusType> findAllUnResolvedSubStatuses(Long workId);

	WorkSubStatusType saveOrUpdateCustomWorkSubStatus(WorkSubStatusTypeDTO workSubStatusTypeDTO);

	WorkSubStatusType deleteWorkSubStatus(long workSubStatusId) throws IllegalArgumentException;

	WorkSubStatusType findSystemWorkSubStatus(String code);

	WorkSubStatusType findWorkSubStatus(long workSubStatusId);

	WorkSubStatusTypeCompanySetting saveWorkSubStatusTypeCompanySetting(long companyId, WorkSubStatusTypeCompanySettingDTO dto);

	WorkSubStatusTypeDashboard findWorkSubStatusDashboardByCompany(WorkSubStatusTypeFilter workSubStatusTypeFilter);

	List<WorkSubStatusType> findAllUnresolvedSubStatusWithColor(Long workId);

	void addDefaultWorkSubStatusToCompany(Company company);

	WorkSubStatusType findCustomWorkSubStatusByCompany(long workSubStatusId, long companyId);

	void resolveAllInapplicableCustomWorkSubStatuses(Work work);

	List<WorkSubStatusTypeAssociation> findAllWorkSubStatusTypeAssociationBySubStatusId(long workSubStatusId);

	Set<WorkSubStatusTypeWorkStatusScope> findAllScopesForSubStatusId(long workSubStatusId);

	WorkSubStatusType findCustomWorkSubStatus(String code, Long companyId);

	void saveOrUpdateAssociation(WorkSubStatusTypeAssociation workSubStatusTypeAssociation);

	WorkSubStatusTypeAssociation getAssociation(Long id);

	WorkSubStatusTypeCompanySetting findColorByIdAndCompany(Long id, Long companyId);

	List<Long> findAllRecipientsByWorkSubStatusId(Long workSubStatusId);

	List<Long> findAllRecipientsByWorkSubStatusCodeAndCompany(String workSubStatusCode, Long companyId);

	List<String> findAllRecipientsUserNumbersByWorkSubStatusId(Long workSubStatusId);

	void saveOrUpdateWorkSubStatusTypeRecipientAssociation(WorkSubStatusType workSubStatus, Set<String> recipients);

	void deleteWorkSubStatusTypeRecipientAssociation(long recipientId, long workSubStatusId);

	void deleteAllWorkSubStatusTypeRecipientAssociationsByWorkSubStatusId(long workSubStatusId);
}
