package com.workmarket.domains.work.service.workresource;

import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkResourceStatusType;
import com.workmarket.domains.work.model.CancellationReasonType;
import com.workmarket.domains.work.model.WorkResourceAggregateFilter;
import com.workmarket.domains.work.model.WorkResourceFeedbackPagination;
import com.workmarket.domains.work.model.WorkResourceLabel;
import com.workmarket.domains.work.model.WorkResourceTimeTracking;
import com.workmarket.service.business.dto.CloseWorkDTO;
import com.workmarket.service.business.dto.DispatcherDTO;
import com.workmarket.service.business.dto.WorkResourceDetailPagination;
import com.workmarket.service.business.dto.WorkResourceLabelDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface WorkResourceService {

	Integer countAssignmentsByResourceUserIdAndStatus(List<Long> userIds, WorkResourceAggregateFilter filter);

	Integer countAllAssignmentsByResourceUserIdAndStatus(List<Long> userIds, WorkResourceAggregateFilter filter);

	Integer countConfirmedWorkResourceLabelByUserId(Long userId, WorkResourceAggregateFilter filter);

	Integer countConfirmedWorkResourceLabelByUserId(List<Long> userId, WorkResourceAggregateFilter filter);

	/**
	 * Calculates the On-Time % of a user based on their experience, as a resource, on the platform
	 * @param userId
	 * @param filter
	 * @return The on-time percentage for a particular users
	 */
	double calculateOnTimePercentageForUser(Long userId, WorkResourceAggregateFilter filter);

	double calculateOnTimePercentageForUser(List<Long> userIds, WorkResourceAggregateFilter filter, Map<String, Integer> workResourceLabelCount);

	/**
	 * Calculates the Deliverable On-Time % of a user based on their experience, as a resource, on the platform
	 * @param userId
	 * @param filter
	 * @return The deliverable on-time percentage for a particular user
	 */
	double calculateDeliverableOnTimePercentageForUser(Long userId, WorkResourceAggregateFilter filter);

	double calculateDeliverableOnTimePercentageForUser(List<Long> userIds, WorkResourceAggregateFilter filter, Map<String, Integer> workResourceLabelCount);

	WorkResourceLabel addLabelToWorkResource(WorkResourceLabelDTO workResourceLabelDTO);

	WorkResourceLabel addLabelToWorkResourceAfterCancellation(Long workResourceId, CancellationReasonType cancellationReasonType);

	void deleteWorkResourceLabel(Long workResourceId, String workResourceLabelTypeCode);

	void setWorkResourceAppointmentFromWork(Long workId);

	void saveOrUpdate(WorkResource workResource);

	WorkResourceLabel ignoreWorkResourceLabel(Long workResourceLabelId);

	WorkResourceLabel ignoreWorkResourceLabel(Long workResourceId, String workResourceLabelTypeCode);

	void removeAutoAssign(long workerId, long workId);

	WorkResourceLabel confirmWorkResourceLabel(WorkResourceLabel workResourceLabel);

	WorkResourceFeedbackPagination findResourceFeedbackForUser(Long userId, WorkResourceFeedbackPagination pagination);

	WorkResource findWorkResourceById(Long workResourceId);

	WorkResource findActiveWorkResource(long workId);

	WorkResource findWorkResource(long userId, long workId);

	/**
	 * Returns work resources associated with the work and matching the userIds
	 * @param userIds
	 * @param workId
	 * @return
	 */
	List<WorkResource> findWorkResources(Collection<Long> userIds, Long workId);

	List<String> findBuyerAssignToFirstResourceWorkNumbers(Collection<Long> workIds);

	List<String> findWorkerAssignToFirstResourceWorkNumbers(String userNumber, Collection<Long> workIds);

	List<Long> findUserIdsNotDeclinedForWork(long workId);

	List<Long> getAllWorkIdsByWorkResourceUserIdAndStatus(long userId, WorkResourceStatusType workResourceStatusType);

	List<WorkResource> findAllResourcesForWork(Long workId);

	Set<WorkResource> saveAll(Set<WorkResource> workResources);

	Set<WorkResource> saveAllResourcesFromWorkToWork(long fromWorkId, long toWorkId);

	WorkResourceDetailPagination populateWorkResourceDetailCache(Long workId, WorkResourceDetailPagination pagination);

	WorkResourceDetailPagination findAllResourcesForWork(Long workId, WorkResourceDetailPagination pagination);

	List<WorkResource> findResourcesInFromWorkNotInToWork(long workId, long notInWork);

	void onPostTransitionToClosed(long workId, CloseWorkDTO closeWorkDTO);

	List<Long> getAllWorkerIdsFromCompanyInvitedToWork(Long companyId, Long workId);

	boolean isAtLeastOneWorkerFromCompanyInvitedToWork(Long companyId, Long workId);

	/**
	 * Returns all the userIds for dispatchers in the worker's company
	 * If the worker is a dispatcher, their userId is excluded from the response
	 * @param workerId
	 * @return
	 */
	List<Long> getAllDispatcherIdsForWorker(Long workerId);

	DispatcherDTO getDispatcherForWorkAndWorker(Long workId, Long workerId);

	List<Long> getAllDispatcherIdsInCompany(Long companyId);

	/**
	 * Returns the value in the dispatcher_id column in the work_resource table
	 * Where there is a matching row for workId and workerId
	 * If the worker is a dispatcher, their userId is excluded from the response
	 * @param workId
	 * @param workerId
	 * @return
	 */
	Long getDispatcherIdForWorkAndWorker(Long workId, Long workerId);

	/**
	 * Returns all the dispatcherIds associated with the provided collection for workerIds
	 * Return an empty list if no results, or if passed an empty collection of workerIds
	 * @param workId
	 * @param workerIds
	 * @return
	 */
	List<Long> getDispatcherIdsForWorkAndWorkers(Long workId, Collection<Long> workerIds);

	void setDispatcherForWorkAndWorker(Long workId, Long workerId);

	boolean isUserResourceForWork(Long workId, Long workerId);

	WorkResourceTimeTracking findTimeTrackingEntryById(Long timeTrackingId);

}
