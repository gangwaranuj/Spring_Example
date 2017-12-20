package com.workmarket.domains.work.service.workresource;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.dao.LookupEntityDAO;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.dao.report.kpi.KpiDAO;
import com.workmarket.dao.summary.work.WorkResourceHistorySummaryDAO;
import com.workmarket.data.report.kpi.KPIRequest;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkResourceStatusType;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.kpi.DataPoint;
import com.workmarket.domains.model.kpi.Filter;
import com.workmarket.domains.model.kpi.KPIReportAggregateInterval;
import com.workmarket.domains.model.kpi.KPIReportFilter;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.domains.work.dao.WorkResourceLabelDAO;
import com.workmarket.domains.work.dao.WorkResourceTimeTrackingDAO;
import com.workmarket.domains.work.model.CancellationReasonType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkResourceAggregateFilter;
import com.workmarket.domains.work.model.WorkResourceFeedbackPagination;
import com.workmarket.domains.work.model.WorkResourceFeedbackRow;
import com.workmarket.domains.work.model.WorkResourceLabel;
import com.workmarket.domains.work.model.WorkResourceLabelType;
import com.workmarket.domains.work.model.WorkResourceTimeTracking;
import com.workmarket.domains.work.model.WorkSchedule;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.analytics.cache.ScorecardCache;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.CloseWorkDTO;
import com.workmarket.service.business.dto.DispatcherDTO;
import com.workmarket.service.business.dto.WorkResourceDetail;
import com.workmarket.service.business.dto.WorkResourceDetailPagination;
import com.workmarket.service.business.dto.WorkResourceLabelDTO;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.summary.SummaryService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
public class WorkResourceServiceImpl implements WorkResourceService {

	@Autowired private LookupEntityDAO lookupEntityDAO;
	@Autowired private WorkResourceDAO workResourceDAO;
	@Autowired private WorkResourceHistorySummaryDAO workResourceHistorySummaryDAO;
	@Autowired private WorkResourceLabelDAO workResourceLabelDAO;
	@Autowired private WorkResourceTimeTrackingDAO workResourceTimeTrackingDAO;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private SummaryService summaryService;
	@Autowired private KpiDAO kpiDAO;
	@Autowired private UserIndexer userIndexer;
	@Autowired private EventRouter eventRouter;
	@Autowired private WorkService workService;
	@Autowired private WorkResourceDetailDecorator workResourceDetailDecorator;
	@Autowired private WorkResourceDetailCache workResourceDetailCache;
	@Autowired private ScorecardCache scorecardCache;
	@Autowired private FeatureEvaluator featureEvaluator;
	@Autowired private UserService userService;
	@Autowired private WorkBundleService workBundleService;
	@Autowired private UserRoleService userRoleService;

	private static final Log logger = LogFactory.getLog(WorkResourceServiceImpl.class);

	@Override
	public Integer countAssignmentsByResourceUserIdAndStatus(List<Long> userIds, WorkResourceAggregateFilter filter) {
		Assert.notNull(filter);
		Assert.notNull(filter.getFromDate());
		Assert.hasText(filter.getWorkStatusTypeCode());
		Assert.notNull(userIds);
		List <String> userIdsStr = Lists.newArrayList();
		for (Long id : userIds) {
			userIdsStr.add(id.toString());
		}
		KPIRequest kpiRequest = new KPIRequest();
		kpiRequest.setAggregateInterval(KPIReportAggregateInterval.NONE);
		kpiRequest.setFrom(filter.getFromDate());
		kpiRequest.setTo(Calendar.getInstance());
		kpiRequest.addToFilters(new Filter(KPIReportFilter.ACTIVE_RESOURCE_USER_ID, userIdsStr));

		if (filter.isScopedToCompany()) {
			kpiRequest.addToFilters(new Filter(KPIReportFilter.COMPANY, Lists.newArrayList(filter.getCompanyId().toString())));
		}

		List<DataPoint> dataPointList = kpiDAO.countAssignmentsByStatus(filter.getWorkStatusTypeCode(), kpiRequest);
		if (isNotEmpty(dataPointList)) {
			return Double.valueOf(dataPointList.get(0).getY()).intValue();
		}
		return 0;
	}

	@Override
	public Integer countAllAssignmentsByResourceUserIdAndStatus(List<Long> userIds, WorkResourceAggregateFilter filter) {
		Assert.hasText(filter.getWorkStatusTypeCode());
		Assert.notNull(userIds);
		List <String> userIdsStr = Lists.newArrayList();
		for (Long id : userIds) {
			userIdsStr.add(id.toString());
		}
		KPIRequest kpiRequest = new KPIRequest();
		kpiRequest.addToFilters(new Filter(KPIReportFilter.ACTIVE_RESOURCE_USER_ID, userIdsStr));
		if (filter.isSetFromDate()) {
			kpiRequest.setFrom(filter.getFromDate());
		}

		if (filter.isScopedToCompany()) {
			kpiRequest.addToFilters(new Filter(KPIReportFilter.COMPANY, Lists.newArrayList(filter.getCompanyId().toString())));
		}

		List<DataPoint> dataPointList = kpiDAO.countAllAssignmentsByStatus(filter.getWorkStatusTypeCode(), kpiRequest);
		if (isNotEmpty(dataPointList)) {
			return Double.valueOf(dataPointList.get(0).getY()).intValue();
		}
		return 0;
	}

	@Override
	public Integer countConfirmedWorkResourceLabelByUserId(Long userId, WorkResourceAggregateFilter filter) {
		Assert.notNull(filter.getFromDate());
		Assert.hasText(filter.getResourceLabelTypeCode());
		Assert.notNull(userId);
		return workResourceLabelDAO.countConfirmedWorkResourceLabelByUserId(Lists.newArrayList(userId), filter);
	}

	@Override
	public Integer countConfirmedWorkResourceLabelByUserId(List<Long> userIds, WorkResourceAggregateFilter filter) {
		Assert.notNull(filter.getFromDate());
		Assert.hasText(filter.getResourceLabelTypeCode());
		Assert.notNull(userIds);
		return workResourceLabelDAO.countConfirmedWorkResourceLabelByUserId(userIds, filter);
	}

	@Override
	public double calculateOnTimePercentageForUser(Long userId, WorkResourceAggregateFilter filter) {
		Assert.notNull(filter.getFromDate());
		filter.setLessThan24Hours(null);
		Map<String, Integer> allLabels = workResourceLabelDAO.countAllConfirmedWorkResourceLabelsByUserId(filter, Lists.newArrayList(userId));
		return calculateOnTimePercentageForUser(Lists.newArrayList(userId), filter, allLabels);
	}

	@Override
	public double calculateOnTimePercentageForUser(List<Long> userIds, WorkResourceAggregateFilter filter, Map<String, Integer> workResourceLabelCount) {
		Assert.notNull(filter.getFromDate());

		filter.setWorkStatusTypeCode(WorkStatusType.CLOSED);
		filter.setResourceLabelTypeCode(WorkResourceLabelType.CANCELLED);
		filter.setLessThan24Hours(false);

		double approvedAssignments = countAllAssignmentsByResourceUserIdAndStatus(userIds, filter);
		double cancelledLabelGreaterThan24Hrs = countConfirmedWorkResourceLabelByUserId(userIds, filter);
		double dividend = approvedAssignments + cancelledLabelGreaterThan24Hrs;
		double divisor = approvedAssignments;

		/*
		 * Formula
		 * (approvedAssignments + cancelledLabelGreaterThan24Hrs - lateLabels) / (approvedAssignments + cancelledLabels + abandonedLabels)
		 */

		if (MapUtils.isNotEmpty(workResourceLabelCount)) {
			double lateLabels = MapUtils.getDoubleValue(workResourceLabelCount, WorkResourceLabelType.LATE, 0);
			double cancelledLabels = MapUtils.getDoubleValue(workResourceLabelCount, WorkResourceLabelType.CANCELLED, 0);
			double abandonedLabels = MapUtils.getDoubleValue(workResourceLabelCount, WorkResourceLabelType.ABANDONED, 0);
			divisor = divisor + cancelledLabels + abandonedLabels;
			dividend = dividend - lateLabels;
		}

		if (divisor > 0)
			return (dividend / divisor);

		return 0;
	}

	@Override
	public double calculateDeliverableOnTimePercentageForUser(Long userId, WorkResourceAggregateFilter filter) {
		Assert.notNull(filter.getFromDate());

		Map<String, Integer> allLabels = workResourceLabelDAO.countAllConfirmedWorkResourceLabelsByUserId(filter, Lists.newArrayList(userId));
		return calculateDeliverableOnTimePercentageForUser(Lists.newArrayList(userId), filter, allLabels);
	}

	@Override
	public double calculateDeliverableOnTimePercentageForUser(List<Long> userIds, WorkResourceAggregateFilter filter, Map<String, Integer> workResourceLabelCount) {
		Assert.notNull(filter.getFromDate());

		filter.setWorkStatusTypeCode(WorkStatusType.CLOSED);
		double approvedAssignments = countAllAssignmentsByResourceUserIdAndStatus(userIds, filter);
		double dividend = approvedAssignments;
		double divisor = approvedAssignments;

		/*
		 * Formula
		 * (approvedAssignments - assignmentsWithLateDeliverables) / (approvedAssignments)
		 */
		if (MapUtils.isNotEmpty(workResourceLabelCount)) {
			double deliverableLateLabels = MapUtils.getDoubleValue(workResourceLabelCount, WorkResourceLabelType.LATE_DELIVERABLE, 0);
			dividend -= deliverableLateLabels;
		}

		return divisor > 0 ? (dividend / divisor) : 0;
	}

	@Override
	public WorkResourceLabel addLabelToWorkResource(WorkResourceLabelDTO workResourceLabelDTO) {
		WorkResourceLabel workResourceLabel = addLabelToWorkResourceImpl(workResourceLabelDTO);
		eventRouter.sendEvent(new UserSearchIndexEvent(workResourceLabel.getWorkResourceUserId()));
		return workResourceLabel;
	}

	private WorkResourceLabel addLabelToWorkResourceImpl(WorkResourceLabelDTO workResourceLabelDTO) {
		Assert.notNull(workResourceLabelDTO);
		Assert.notNull(workResourceLabelDTO.getWorkResourceId());
		Assert.hasText(workResourceLabelDTO.getWorkResourceLabelTypeCode());

		WorkResource workResource = workResourceDAO.findById(workResourceLabelDTO.getWorkResourceId());
		Assert.notNull(workResource);

		WorkResourceLabel workResourceLabel = workResourceLabelDAO.findByLabelCodeAndWorkResourceId(workResourceLabelDTO.getWorkResourceLabelTypeCode(), workResourceLabelDTO.getWorkResourceId());
		if (workResourceLabel == null) {
			WorkResourceLabelType workResourceLabelType = lookupEntityDAO.findByCode(WorkResourceLabelType.class, workResourceLabelDTO.getWorkResourceLabelTypeCode());
			Assert.notNull(workResourceLabelType);

			workResourceLabel = new WorkResourceLabel(workResource, workResourceLabelType);
			workResourceLabel.setDate(summaryService.findTimeDimension(Calendar.getInstance()));
			Calendar maxAppointmentDate = workService.calculateMaxAppointmentDate(workResource.getWork());
			boolean isLessThan24HrsFromAppointment = (DateUtilities.isInFuture(maxAppointmentDate) && DateUtilities.getHoursBetween(Calendar.getInstance(), maxAppointmentDate, true) <= 24) || DateUtilities.isInPast(maxAppointmentDate);
			workResourceLabel.setLessThan24HoursFromAppointmentTime(isLessThan24HrsFromAppointment);

			workResourceLabelDAO.saveOrUpdate(workResourceLabel);

			List<String> labelsToIgnore = Lists.newArrayList();
			String addedWorkResourceLabelTypeCode = workResourceLabelDTO.getWorkResourceLabelTypeCode();

			//Late & Abandoned are mutually exclusive. Adding one removes the other.
			if (addedWorkResourceLabelTypeCode.equals(WorkResourceLabelType.ABANDONED)) {
				labelsToIgnore.add(WorkResourceLabelType.LATE);
			} else if (addedWorkResourceLabelTypeCode.equals(WorkResourceLabelType.LATE)) {
				labelsToIgnore.add(WorkResourceLabelType.ABANDONED);
			}

			if (WorkResourceLabelType.IGNORE_DELIVERABLE_LATE_STATUSES.contains(addedWorkResourceLabelTypeCode)) {
				labelsToIgnore.add(WorkResourceLabelType.LATE_DELIVERABLE);
			}

			for (String labelToIgnore : labelsToIgnore) {
				ignoreWorkResourceLabel(workResourceLabelDTO.getWorkResourceId(), labelToIgnore);
			}
		} else if (WorkResourceLabelType.LATE_DELIVERABLE.equals(workResourceLabelDTO.getWorkResourceLabelTypeCode())) {
			workResourceLabel.unIgnore();
		}

		workResourceDetailCache.evict(workResource.getWork().getId());

		if (workResourceLabelDTO.isConfirmed()) {
			return confirmWorkResourceLabel(workResourceLabel);
		}
		return workResourceLabel;
	}

	@Override
	public WorkResourceLabel addLabelToWorkResourceAfterCancellation(Long workResourceId, CancellationReasonType cancellationReasonType) {
		Assert.notNull(workResourceId);
		Assert.notNull(cancellationReasonType);
		if (cancellationReasonType.isResourceCancelled()) {
			return addLabelToWorkResource(new WorkResourceLabelDTO(workResourceId, WorkResourceLabelType.CANCELLED, true));
		} else if (cancellationReasonType.isResourceAbandoned()) {
			return addLabelToWorkResource(new WorkResourceLabelDTO(workResourceId, WorkResourceLabelType.ABANDONED, true));
		}
		return null;
	}

	@Override
	public void deleteWorkResourceLabel(Long workResourceId, String workResourceLabelTypeCode) {
		Assert.notNull(workResourceId);
		logger.info("[deleteWorkResourceLabel] workResourceId: " + workResourceId + " " + workResourceLabelTypeCode);
		WorkResourceLabel workResourceLabel = workResourceLabelDAO.findByLabelCodeAndWorkResourceId(workResourceLabelTypeCode, workResourceId);
		if (workResourceLabel != null) {
			workResourceLabelDAO.delete(workResourceLabel);
			workResourceDetailCache.evict(workResourceLabel.getWorkId());
		}

	}

	@Override
	public void setWorkResourceAppointmentFromWork(Long workId){
		final WorkResource activeResource = workService.findActiveWorkResource(workId);
		Assert.notNull(activeResource);

		if (!activeResource.getWork().getIsScheduleRange()) {
			activeResource.setAppointment(activeResource.getWork().getSchedule());
			saveOrUpdate(activeResource);
		}
	}

	@Override
	public void saveOrUpdate(WorkResource workResource) {
		Assert.notNull(workResource);
		workResourceDAO.saveOrUpdate(workResource);
	}

	@Override
	public WorkResourceLabel ignoreWorkResourceLabel(Long workResourceLabelId) {
		Assert.notNull(workResourceLabelId);
		WorkResourceLabel workResourceLabel = workResourceLabelDAO.get(workResourceLabelId);
		if (workResourceLabel != null) {
			return ignoreWorkResourceLabel(workResourceLabel);
		}
		return null;
	}

	@Override
	public WorkResourceLabel ignoreWorkResourceLabel(Long workResourceId, String workResourceLabelTypeCode) {
		Assert.notNull(workResourceId);
		Assert.notNull(workResourceLabelTypeCode);
		WorkResourceLabel workResourceLabel = workResourceLabelDAO.findByLabelCodeAndWorkResourceId(workResourceLabelTypeCode, workResourceId);
		if (workResourceLabel != null) {
			return ignoreWorkResourceLabel(workResourceLabel);
		}
		return null;
	}

	@Override
	public void removeAutoAssign(long workerId, long workId) {
		WorkResource workResource = workService.findWorkResource(workerId, workId);
		workResource.setAssignToFirstToAccept(false);
		if (workBundleService.isAssignmentBundle(workId)) {
			for (Long bundledWorkId : workBundleService.getAllWorkIdsInBundle(workId)) {
				workResource = workService.findWorkResource(workerId, bundledWorkId);
				workResource.setAssignToFirstToAccept(false);
			}
		}
		workResourceDetailCache.evict(workId);
	}

	protected WorkResourceLabel ignoreWorkResourceLabel(WorkResourceLabel workResourceLabel) {
		Assert.notNull(workResourceLabel);

		if (WorkResourceLabelType.LATE_DELIVERABLE.equals(workResourceLabel.getWorkResourceLabelType().getCode())) {
			addLabelToWorkResource(new WorkResourceLabelDTO(workResourceLabel.getWorkResourceId(), WorkResourceLabelType.COMPLETED_ONTIME, true));
		}

		if (!workResourceLabel.isIgnored()) {
			workResourceLabel.ignore(authenticationService.getCurrentUser());
			workResourceDetailCache.evict(workResourceLabel.getWorkId());
		}

		scorecardCache.evictAllResourceScoreCardsForUser(workResourceLabel.getWorkResourceUserId());

		return workResourceLabel;
	}

	@Override
	public WorkResourceLabel confirmWorkResourceLabel(WorkResourceLabel workResourceLabel) {
		Assert.notNull(workResourceLabel);
		if (!workResourceLabel.isConfirmed()) {
			workResourceLabel.setConfirmed(true);
			workResourceLabel.setConfirmedBy(authenticationService.getCurrentUser());
			workResourceLabel.setConfirmedOn(Calendar.getInstance());
			workResourceDetailCache.evict(workResourceLabel.getWorkId());
		}

		scorecardCache.evictAllResourceScoreCardsForUser(workResourceLabel.getWorkResourceUserId());
		return workResourceLabel;
	}

	@Override
	public WorkResourceFeedbackPagination findResourceFeedbackForUser(Long userId, WorkResourceFeedbackPagination pagination) {
		Assert.notNull(userId);
		Assert.notNull(pagination);
		User currentUser = authenticationService.getCurrentUser();
		Long currentUserId = currentUser.getId();
		Long currentCompanyId = currentUser.getCompany().getId();
		pagination = workResourceDAO.findResourceFeedbackForUserVisibleToUserAtCompany(userId, currentUserId, currentCompanyId, pagination);

		// Enforce additional authorization requirements
		// Final access matrix looks like:
		// Visibility       | Resource | Work Company | Internal | Everyone Else
		// Unshared Rating  |          | X            | X        |
		// Unshared Review  |          | X            |          |
		// Shared Rating    | X        | X            | X        | X
		// Shared Review    | (soon)   | X            | X        | (soon)
		// Confirmed Labels | X        | X            | X        |
		// Company Name     | X        | X            | X        |
		// Work Number      | X        | X            | X        |

		boolean isOwner = currentUser.getId().equals(userId);
		boolean isInternal = userRoleService.isInternalUser(currentUser);

		for (WorkResourceFeedbackRow r : pagination.getResults()) {

			boolean isWorkCompany = r.getCompanyId().equals(currentCompanyId);
			boolean isOther = !isOwner && !isWorkCompany && !isInternal;

			if (isOther) {
				r.setWorkNumber(null);
				r.setCompanyName(null);
				r.setRatingReview(null);
			}
		}

		return pagination;
	}

	@Override
	public WorkResource findActiveWorkResource(long workId) {
		return workResourceDAO.findActiveWorkResource(workId);
	}

	@Override
	public WorkResource findWorkResource(long userId, long workId) {
		return workResourceDAO.findByUserAndWork(userId, workId);
	}

	@Override
	public List<WorkResource> findWorkResources(Collection<Long> userIds, Long workId) {
		Assert.notNull(userIds);
		Assert.notNull(workId);
		return workResourceDAO.findByUserIdsAndWorkId(userIds, workId);
	}

	@Override
	public List<String> findBuyerAssignToFirstResourceWorkNumbers(Collection<Long> workIds) {
		Assert.notNull(workIds);
		return workResourceDAO.findAssignToFirstResourceWorkNumbersByWorkIds(workIds);
	}

	@Override
	public List<String> findWorkerAssignToFirstResourceWorkNumbers(String userNumber, Collection<Long> workIds) {
		Assert.notNull(userNumber);
		Assert.notNull(workIds);
		return workResourceDAO.findAssignToFirstResourceWorkNumbersByUserNumberAndWorkIds(userNumber, workIds);
	}

	@Override
	public WorkResource findWorkResourceById(Long workResourceId) {
		Assert.notNull(workResourceId);
		return workResourceDAO.findById(workResourceId);
	}

	@Override
	public List<Long> findUserIdsNotDeclinedForWork(long workId) {
		return workResourceDAO.findUserIdsNotDeclinedForWork(workId);
	}

	@Override
	public List<Long> getAllWorkIdsByWorkResourceUserIdAndStatus(long userId, WorkResourceStatusType workResourceStatusType) {
		Assert.notNull(workResourceStatusType);
		return workResourceHistorySummaryDAO.getAllWorkIdsByWorkResourceUserIdAndStatus(userId, workResourceStatusType);
	}

	@Override
	public List<WorkResource> findAllResourcesForWork(Long workId) {
		Assert.notNull(workId);
		return workResourceDAO.findAllResourcesForWork(workId);
	}

	@Override
	public Set<WorkResource> saveAll(Set<WorkResource> workResources) {
		if (isNotEmpty(workResources)) {
			workResourceDAO.saveAll(workResources);
			workResourceDetailCache.evict(CollectionUtilities.first(workResources).getWork().getId());
		}
		return Collections.emptySet();
	}

	@Override
	public Set<WorkResource> saveAllResourcesFromWorkToWork(long fromWorkId, long toWorkId) {
		Work fromWork = workService.findWork(fromWorkId);
		Work toWork = workService.findWork(toWorkId);
		Assert.notNull(fromWork);
		Assert.notNull(toWork);
		Assert.isTrue(toWork.isRoutable() , "Work destination is not in a routable status");
		List<WorkResource> existingWorkResources = workResourceDAO.findResourcesInFromWorkNotInToWork(fromWork.getId(), toWork.getId());
		Set<WorkResource> invitedWorkResources = Sets.newHashSetWithExpectedSize(existingWorkResources.size());
		for (WorkResource workResource : existingWorkResources) {
			WorkResource newWorkResource = new WorkResource(toWork, workResource.getUser());
			newWorkResource.setScore(workResource.getScore());
			newWorkResource.setAssignToFirstToAccept(workResource.isAssignToFirstToAccept());
			invitedWorkResources.add(newWorkResource);
		}
		if (isNotEmpty(invitedWorkResources)) {
			workResourceDAO.saveAll(invitedWorkResources);
			workResourceDetailCache.evict(toWork.getId());
		}
		return invitedWorkResources;
	}

	@Override
	public List<WorkResource> findResourcesInFromWorkNotInToWork(long fromWorkId, long toWorkId) {
		return workResourceDAO.findResourcesInFromWorkNotInToWork(fromWorkId, toWorkId);
	}

	@Override
	public void onPostTransitionToClosed(long workId, CloseWorkDTO closeWorkDTO) {
		Assert.notNull(closeWorkDTO);

		Long activeWorkerId = workService.findActiveWorkerId(workId);
		WorkResource activeWorkResource = workService.findWorkResource(activeWorkerId, workId);
		Assert.notNull(activeWorkerId);
		Assert.notNull(activeWorkResource);

		Long activeWorkResourceId = activeWorkResource.getId();
		Assert.notNull(activeWorkResourceId);

		if (closeWorkDTO.isArrivedOnTime()) {
			ignoreWorkResourceLabel(activeWorkResourceId, WorkResourceLabelType.LATE);
		} else {
			WorkResourceLabelDTO label = new WorkResourceLabelDTO(activeWorkResourceId, WorkResourceLabelType.LATE, true);
			addLabelToWorkResourceImpl(label);
		}

		if (closeWorkDTO.isCompletedOnTime()) {
			ignoreWorkResourceLabel(activeWorkResourceId, WorkResourceLabelType.LATE_DELIVERABLE);
		} else {
			ignoreWorkResourceLabel(activeWorkResourceId, WorkResourceLabelType.COMPLETED_ONTIME);
			WorkResourceLabelDTO label = new WorkResourceLabelDTO(activeWorkResourceId, WorkResourceLabelType.LATE_DELIVERABLE, true);
			addLabelToWorkResourceImpl(label);
		}
	}

	@Override
	public WorkResourceDetailPagination populateWorkResourceDetailCache(Long workId, WorkResourceDetailPagination pagination) {
		Assert.notNull(workId);
		// Conditionally decorate results w/notes and labels
		List<WorkResourceDetail> workResourceDetailList;
		Optional<WorkResourceDetailPagination> result = workResourceDetailCache.get(workId, pagination);

		if (result.isPresent()) {
			return result.get();
		}

		pagination = workResourceDAO.findAllResourcesForWork(workId, pagination);
		workResourceDetailList = pagination.getResults();

		if (pagination.isIncludeNotes()) {
			workResourceDetailDecorator.decorateNotes(workId, workResourceDetailList);
		}

		if (pagination.isIncludeLabels()) {
			workResourceDetailDecorator.decorateLabels(workId, workResourceDetailList);
		}

		workResourceDetailCache.set(workId, pagination);

		return pagination;
	}

	@Override
	public WorkResourceDetailPagination findAllResourcesForWork(Long workId, WorkResourceDetailPagination pagination) {
		Assert.notNull(workId);

		pagination = populateWorkResourceDetailCache(workId, pagination);

		// TODO: Alex - if dispatcher is searching, do we want to make sure we are passing in the companyId of work owner?
		workResourceDetailDecorator.decorateScoreCards(authenticationService.getCurrentUser().getCompany().getId(), pagination.getResults());

		workResourceDetailDecorator.decorateDispatcher(workId, pagination.getResults());

		Work work = workService.findWork(workId);
		if(work.getCompany().getId().equals(authenticationService.getCurrentUserCompanyId())) {
			workResourceDetailDecorator.decorateBlockedWorker(work.getCompany().getId(), pagination.getResults());
		}
		// For the first iteration we don't want the schedule conflicts label to be cached
		boolean hasScheduleConflictFeatureEnabled = featureEvaluator.hasFeature(authenticationService.getCurrentUser().getCompany().getId(), "scheduleConflict");
		if (hasScheduleConflictFeatureEnabled) {

			Assert.notNull(work);
			if (work.getScheduleFrom() != null) {
				WorkSchedule workSchedule = new WorkSchedule(
					new DateRange(work.getScheduleFrom(), work.getScheduleThrough())
				).setWorkId(workId);
				workResourceDetailDecorator.decorateScheduleConflicts(workSchedule, pagination.getResults());
			}
		}
		return pagination;
	}

	@Override
	public List<Long> getAllWorkerIdsFromCompanyInvitedToWork(Long companyId, Long workId) {
		Assert.notNull(companyId);
		Assert.notNull(workId);

		return workResourceDAO.getAllWorkersFromCompanyInvitedToWork(companyId, workId);
	}

	@Override
	public boolean isAtLeastOneWorkerFromCompanyInvitedToWork(Long companyId, Long workId) {
		Assert.notNull(companyId);
		Assert.notNull(workId);

		return workResourceDAO.isAtLeastOneWorkerFromCompanyInvitedToWork(companyId, workId);
	}

	@Override
	public List<Long> getAllDispatcherIdsForWorker(Long workerId) {
		Assert.notNull(workerId);

		return workResourceDAO.getAllDispatcherIdsForWorker(workerId);
	}

	@Override
	public DispatcherDTO getDispatcherForWorkAndWorker(Long workId,  Long workerId) {
		Assert.notNull(workId);
		Assert.notNull(workerId);

		return workResourceDAO.getDispatcherForWorkAndWorker(workId, workerId);
	}

	@Override
	public List<Long> getAllDispatcherIdsInCompany(Long companyId) {
		Assert.notNull(companyId);

		return workResourceDAO.getAllDispatcherIdsInCompany(companyId);
	}

	@Override
	public Long getDispatcherIdForWorkAndWorker(Long workId, Long workerId) {
		Assert.notNull(workId);
		Assert.notNull(workerId);

		return workResourceDAO.getDispatcherIdForWorkAndWorker(workId, workerId);
	}

	@Override
	public List<Long> getDispatcherIdsForWorkAndWorkers(Long workId, Collection<Long> workerIds) {
		Assert.notNull(workId);
		Assert.notNull(workerIds);

		return workResourceDAO.getDispatcherIdsForWorkAndWorkers(workId, workerIds);
	}

	@Override
	public void setDispatcherForWorkAndWorker(Long workId, Long workerId) {
		Assert.notNull(workId);
		Assert.notNull(workerId);

		Long currentUserId = authenticationService.getCurrentUserId();
		if (!workerId.equals(currentUserId)) {
			Optional<PersonaPreference> personaPreferenceOptional = userService.getPersonaPreference(currentUserId);
			boolean isDispatcher = personaPreferenceOptional.isPresent() && personaPreferenceOptional.get().isDispatcher();
			if (isDispatcher) {
				workResourceDAO.setDispatcherForWorkAndWorker(currentUserId, workId, workerId);
			}
		}
	}

	@Override
	public boolean isUserResourceForWork(Long workId, Long workerId) {
		Assert.notNull(workId);
		Assert.notNull(workerId);

		return workResourceDAO.isUserResourceForWork(workId, workerId);
	}

	@Override
	public WorkResourceTimeTracking findTimeTrackingEntryById(Long timeTrackingId) {
		Assert.notNull(timeTrackingId);
		return workResourceTimeTrackingDAO.findById(timeTrackingId);
	}

}
