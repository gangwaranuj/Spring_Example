package com.workmarket.domains.work.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.service.business.dto.DispatcherDTO;
import com.workmarket.service.business.dto.WorkResourceDetailPagination;
import com.workmarket.domains.model.WorkResourcePagination;
import com.workmarket.domains.model.resource.LiteResource;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkResourceFeedbackPagination;
import com.workmarket.domains.work.model.WorkSchedule;
import com.workmarket.service.business.dto.WorkResourceDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface WorkResourceDAO extends DAOInterface<WorkResource> {

	WorkResource findByUserAndWork(Long userId, Long workId);

	List<WorkResource> findByUserIdsAndWorkId(Collection<Long> userIds, Long workId);

	List<String> findAssignToFirstResourceWorkNumbersByWorkIds(Collection<Long> workIds);

	List<String> findAssignToFirstResourceWorkNumbersByUserNumberAndWorkIds(String userNumber, Collection<Long> workIds);

	WorkResourcePagination findByWork(Long workId, WorkResourcePagination pagination);

	List<WorkResource> findNotDeclinedForWork(Long workId);
	List<Long> findUserIdsNotDeclinedForWork(Long workId);
	List<Long> findUserIdsDeclinedForWork(Long workId);

	List<Long> findWorkerIdsForWork(Long workId);

	List<WorkResource> findAllResourcesForWork(long workId);

	List<WorkResource> findResourcesInFromWorkNotInToWork(long fromWorkId, long toWorkId);

	/**
	 * Returns the active (assigned) worker.
	 *
	 * @param workId
	 * @return {@link com.workmarket.domains.model.WorkResource WorkResource}
	 * @throws Exception
	 */
	WorkResource findActiveWorkResource(Long workId);

	Long findActiveWorkerId(Long workId);

	WorkResource findById(Long workResourceId);

	boolean isUserActiveResourceForWorkWithAssessment(Long userId, Long assessmentId);

	boolean isUserResourceForWork(Long workId, Long userId);

	WorkResourceDetailPagination findAllResourcesForWork(Long workId, WorkResourceDetailPagination pagination);

	List<WorkResourceDTO> findAllResourcesForWorkSolrReindexOnly(Long workId);

	WorkResourceFeedbackPagination findResourceFeedbackForUserVisibleToUserAtCompany(Long userId, Long viewingUserId, Long viewingCompanyId, WorkResourceFeedbackPagination pagination);

	WorkResource createOpenWorkResource(Work work, User user, boolean targeted, boolean isVendor);

	List<LiteResource> findLiteResourceByWorkNumber(String workNumber);

	List<WorkSchedule> findWorkSchedulesByWorker(long userId);

	Map<Long, List<WorkSchedule>> findActiveWorkScheduleByWorkResourceExcludingCurrentWork(long workId);

	Map<Long,List<WorkSchedule>> findActiveSchedulesExcludingWorkByUserIds(long workId, Set<Long> userIds);

	List<Long> findAllResourcesUserIdsForWorkWithNotificationAllowed(Long workId, String notificationTypeCode, boolean openOnly);

	boolean isWorkNotifyAvailable(Long workId, String notificationTypeCode, boolean openOnly);

	List<Long> findAllResourceUserIdsForWorkWithSmsAllowed(Long workId, String notificationTypeCode);

	List<Long> findAllResourceUserIdsForWorkWithPushAllowed(Long workId, String notificationTypeCode);

	List<Long> getAllWorkersFromCompanyInvitedToWork(Long companyId, Long workId);

	boolean isAtLeastOneWorkerFromCompanyInvitedToWork(Long companyId, Long workId);

	List<Long> getAllDispatcherIdsForWorker(Long workerId);

	List<Long> getAllDispatcherIdsInCompany(Long companyId);

	Long getDispatcherIdForWorkAndWorker(Long workId, Long workerId);

	DispatcherDTO getDispatcherForWorkAndWorker(Long workId, Long workerId);

	List<Long> getDispatcherIdsForWorkAndWorkers(Long workId, Collection<Long> workerIds);

	void setDispatcherForWorkAndWorker(Long dispatcherId, Long workId, Long workerId);
}
