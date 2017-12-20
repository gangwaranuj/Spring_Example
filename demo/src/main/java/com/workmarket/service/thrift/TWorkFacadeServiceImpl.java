package com.workmarket.service.thrift;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.common.template.NotificationTemplateFactory;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.actions.WorkViewedEvent;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.domains.work.service.route.RoutingStrategyService;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.dto.WorkBundleDTO;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.business.event.work.WorkBundleAcceptEvent;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.business.integration.hooks.webhook.WebHookEventService;
import com.workmarket.service.business.wrapper.AcceptWorkResponse;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.notification.NotificationDispatcher;
import com.workmarket.service.thrift.transactional.TWorkService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.service.thrift.transactional.work.WorkResponseBuilder;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.AcceptWorkOfferRequest;
import com.workmarket.thrift.work.Resource;
import com.workmarket.thrift.work.TimeTrackingRequest;
import com.workmarket.thrift.work.TimeTrackingResponse;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkActionResponse;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.thrift.work.WorkSaveRequest;
import com.workmarket.web.helpers.WorkBundleValidationHelper;
import com.workmarket.web.helpers.WorkBundleValidationResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.EMPTY_SET;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import static ch.lambdaj.Lambda.*;

@Service
public class TWorkFacadeServiceImpl implements TWorkFacadeService {

	private static final Log logger = LogFactory.getLog(TWorkFacadeServiceImpl.class);
	@Autowired private TWorkService tWorkService;
	@Autowired private WorkResponseBuilder workResponseBuilder;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private WorkService workService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private EventRouter eventRouter;
	@Autowired private WebHookEventService webHookEventService;
	@Autowired private RedisAdapter redisAdapter;
	@Autowired private WorkBundleValidationHelper workBundleValidationHelper;
	@Autowired private NotificationTemplateFactory notificationTemplateFactory;
	@Autowired private NotificationDispatcher notificationDispatcher;
	@Autowired private WorkBundleService workBundleService;
	@Autowired private WorkResourceService workResourceService;
	@Autowired private RoutingStrategyService routingStrategyService;

	@Override
	public WorkResponse saveOrUpdateWork(final WorkSaveRequest request) throws ValidationException {
		return saveOrUpdateWork(request, EMPTY_SET);
	}

	@Override
	public WorkResponse saveOrUpdateWork(final WorkSaveRequest request, Set<WorkRequestInfo> includes) throws ValidationException {
		Work work = tWorkService.saveWork(request);

		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(work.getId()));
		eventRouter.sendEvent(new UserSearchIndexEvent(authenticationService.getCurrentUser().getId()));

		if (work == null) {
			return new WorkResponse();
		}

		tWorkService.saveAssets(request, work.getId());
		tWorkService.saveCustomFields(request, work.getId());
		tWorkService.saveAssessments(request, work.getId());
		tWorkService.saveFollowers(request, work.getId());
		tWorkService.saveOptions(request, work);

		Set<WorkAuthorizationResponse> routingAuthorization = Sets.newHashSet(WorkAuthorizationResponse.SUCCEEDED);
		// If the intention is to send the assignment, first authorize.
		if (request.isSmartRoute() || isNotEmpty(request.getRoutingStrategies()) || request.isSetAssignTo()) {
			routingAuthorization = tWorkService.validateWorkForRouting(work.getId());
			if (routingAuthorization.contains(WorkAuthorizationResponse.SUCCEEDED)) {
				tWorkService.saveResource(request, work.getId());
				tWorkService.saveRoutingStrategy(request, work.getId());
			}
		}

		try {
			WorkResponse workResponse = workResponseBuilder.buildWorkResponse(work.getId(), authenticationService.getCurrentUser().getId(), includes);
			workResponse.setWorkAuthorizationResponses(routingAuthorization);
			return workResponse;
		} catch (Exception e) {
			logger.error("Error building the response", e);
			return new WorkResponse();
		}
	}

	@Override
	public WorkResponse saveOrUpdateWorkDraft(WorkSaveRequest request) throws ValidationException {
		return saveOrUpdateWorkDraft(request, EMPTY_SET);
	}

	@Override
	public WorkResponse saveOrUpdateWorkDraft(WorkSaveRequest request, Set<WorkRequestInfo> includes) throws ValidationException {
		WorkResponse workResponse = tWorkService.saveOrUpdateWorkDraft(request, includes);
		if (workResponse != null) {
			eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workResponse.getWork().getId()));
		}
		return workResponse;
	}

	@Override
	public WorkResponse saveOrUpdateWorkTemplate(WorkSaveRequest request) throws ValidationException {
		return tWorkService.saveOrUpdateWorkTemplate(request);
	}

	// Be careful with this method, it loads a LOT of stuff.
	// Try to use tWorkFacadeService.findWork(request) instead,
	// Where you explicit declare the work properties you need
	@Deprecated
	@Override
	public WorkResponse findWorkDetail(WorkRequest request) throws WorkActionException {
		try {
			long workId = getWorkId(request);
			Assert.isTrue(workId > 0);
			WorkResponse response = workResponseBuilder.buildWorkDetailResponse(workId, request.getUserId());
			if (response != null && response.isSetViewingResource() && response.isSetWork() && WorkStatusType.SENT.equals(response.getWork().getStatus().getCode())) {
				eventRouter.sendEvent(new WorkViewedEvent(workId, request.getUserId(), request.getViewType()));
			}
			return response;
		} catch (Exception e) {
			logger.error("There was an error finding work detail workNumber: " + request.getWorkNumber(), e);
			throw new WorkActionException();
		}
	}

	@Override
	public WorkResponse findWorkDetailLight(WorkRequest request) throws WorkActionException {
		try {
			final long workId = workService.findWorkId(request.getWorkNumber());
			final WorkResponse response = workResponseBuilder.buildWorkDetailResponseLight(workId, request.getUserId(), request.getIncludes());
			if (shouldSendWorkViewedEvent(response)) {
				eventRouter.sendEvent(new WorkViewedEvent(response.getWork().getId(), request.getUserId(), request.getViewType()));
			}
			return response;
		} catch (Exception e) {
			logger.error("There was an error finding work detail workNumber: " + request.getWorkNumber(), e);
			throw new WorkActionException();
		}
	}

	private boolean shouldSendWorkViewedEvent(WorkResponse response) {
		return (response != null &&
			response.isSetViewingResource() &&
			response.isSetWork() &&
			response.getWork().isSetStatus() &&
			WorkStatusType.SENT.equals(response.getWork().getStatus().getCode()));
	}

	@Override
	public WorkResponse findWork(WorkRequest request) throws WorkActionException {
		long workId = getWorkId(request);
		return getWorkResponse(workId, request);
	}

	private long getWorkId(WorkRequest request) {
		long workId = 0;

		if (request.isSetWorkId()) {
			workId = request.getWorkId();
		} else if (request.isSetWorkNumber()) {
			workId = workService.findWorkId(request.getWorkNumber());
		}
		return workId;
	}

	private WorkResponse getWorkResponse(long workId, WorkRequest request) throws WorkActionException {
		try {
			return workResponseBuilder.buildWorkResponse(workId, request.getUserId(), request.getIncludes());
		} catch (Exception e) {
			logger.error("There was an error finding work workNumber: " + request.getWorkNumber(), e);
			throw new WorkActionException();
		}
	}

	@Override
	public List<WorkResponse> findWorks(List<WorkRequest> request) throws WorkActionException {
		if (isEmpty(request)) {
			return Collections.emptyList();
		}
		List<String> workNumbers = Lists.newArrayList();
		List<WorkResponse> workResponses = Lists.newArrayList();
		long userId = request.get(0).getUserId();
		try {

			Set<WorkRequestInfo> includes = request.get(0).getIncludes();
			// only work numbers is multiple for now, need to add work ids
			for (WorkRequest workRequest : request) {
				workNumbers.add(workRequest.getWorkNumber());
				long workId = workService.findWorkId(workRequest.getWorkNumber());
				workResponses.add(workResponseBuilder.buildWorkResponse(workId, userId, includes));
			}
		} catch (Exception e) {
			logger.error(String.format("There was an error finding work for user %d, ids %s", userId, workNumbers.toString()), e);
			throw new WorkActionException();
		}
		return workResponses;
	}


	@Override
	public AcceptWorkResponse acceptWork(Long userId, Long workId) {
		AcceptWorkResponse response = workService.acceptWork(userId, workId);

		if (response.isSuccessful()) {
			if (response.getWork().isWorkBundle()) {
				eventRouter.sendEvent(new WorkBundleAcceptEvent(userId, workId));
			}
			acceptWorkPostProcess(response.getWork());
		}
		return response;
	}

	@Override
	public AcceptWorkResponse acceptWork(User user, Work work) {
		AcceptWorkResponse response = workService.acceptWork(user, work);

		if (response.isSuccessful()) {
			if (work.isWorkBundle()) {
				eventRouter.sendEvent(new WorkBundleAcceptEvent(user.getId(), work.getId()));
			}
			acceptWorkPostProcess(response.getWork());
		}
		return response;
	}

	@Override
	public List<AcceptWorkResponse> acceptWorkBundle(Long userId, Long workId) {
		List<AcceptWorkResponse> failures = Lists.newArrayList();

		WorkBundle bundle = workBundleService.findById(workId, true);
		for (Work work : bundle.getBundle()) {
			// accept
			AcceptWorkResponse response = acceptWork(userId, work.getId());
			if (response.isFailure()) {
				failures.add(response);
			}
		}

		return failures;
	}

	@Override
	public WorkActionResponse acceptWorkOnBehalf(AcceptWorkOfferRequest acceptWorkOfferRequest) throws WorkActionException {
		WorkActionResponse response = workService.acceptWorkOnBehalf(acceptWorkOfferRequest);
		logger.warn("LOCK Released API acceptOnBehalf");
		if (response.isSuccessful()) {
			acceptWorkPostProcess(workService.findWorkByWorkNumber(acceptWorkOfferRequest.getWorkAction().getWorkNumber()));
		}
		return response;
	}

	@Override
	public TimeTrackingResponse checkInActiveResource(TimeTrackingRequest timeTrackingRequest) {
		Assert.notNull(timeTrackingRequest);
		Assert.notNull(timeTrackingRequest.getDate());
		TimeTrackingResponse response = workService.checkInActiveResource(timeTrackingRequest);
		if (response.isSuccessful()) {
			eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(timeTrackingRequest.getWorkId()));
		}
		return response;
	}

	@Override
	public TimeTrackingResponse checkOutActiveResource(TimeTrackingRequest timeTrackingRequest) {
		Assert.notNull(timeTrackingRequest);
		Assert.notNull(timeTrackingRequest.getDate());
		TimeTrackingResponse response = workService.checkOutActiveResource(timeTrackingRequest);
		if (response.isSuccessful()) {
			eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(timeTrackingRequest.getWorkId()));
		}
		return response;
	}

	@Override
	public WorkResponse eventUploadHelper(
		final WorkSaveRequest saveRequest,
		final String uploadKey,
		final String uploadSizeKey
	) {
		String workNumber = "";
		WorkResponse response = null;
		try {
			saveRequest.setPartOfBulk(true);

			response = tWorkService.saveOrUpdateWorkDraft(saveRequest);
			//If the intention is to send the assignment, first authorize.
			if (saveRequest.getWork().isSetResources() && !saveRequest.isBundle()) {
				final Set<WorkAuthorizationResponse> routingAuthorization = tWorkService.validateWorkForRouting(response.getWork().getId());
				if (routingAuthorization.contains(WorkAuthorizationResponse.SUCCEEDED)) {
					if (saveRequest.getWork().isSetResources()) {
						List<Long> userIdsToAdd = extract(saveRequest.getWork().getResources(), on(Resource.class).getId());
						routingStrategyService.addUserIdsRoutingStrategy(
								response.getWork().getId(),
								Sets.newHashSet(userIdsToAdd),
								0,
								saveRequest.isAssignToFirstToAccept());
					}
				}
			}

			workNumber = response.getWork().getWorkNumber();
			if (saveRequest.isBundle()) {
				final WorkBundleDTO dto = new WorkBundleDTO();
				dto.setWorkNumbers(Arrays.asList(response.getWork().getWorkNumber()));
				final String redisKey = uploadKey + saveRequest.getBundleTitle();
				final Optional<Object> redisVal = redisAdapter.get(redisKey);
				if (redisVal.isPresent()) {
					dto.setId(Long.valueOf((String)redisVal.or("0")));
				} else {
					dto.setTitle(saveRequest.getBundleTitle());
					dto.setDescription(saveRequest.getBundleDescription());
				}
				final WorkBundleValidationResult result = workBundleValidationHelper.validateWorkBundle(dto, saveRequest.getUserId());
				if (!redisVal.isPresent()) {
					redisAdapter.set(redisKey, String.valueOf(result.getBundle().getId()), (long) DateTimeConstants.SECONDS_PER_DAY);
				}
			}
		} catch (ValidationException e) {
			logger.error("There was an error validating the bulk upload assignment.", e);
		} catch (Exception e) {
			logger.error("There was an error saving a bulk upload assignment", e);
		}

		final Long listLength = redisAdapter.addToList(uploadKey, workNumber, (long) DateTimeConstants.SECONDS_PER_DAY);
		final Long totalAssignments = Long.valueOf((String) redisAdapter.get(uploadSizeKey).or("0"));
		logger.info(String.format("%d assignment(s) saved out of %d total.", listLength, totalAssignments));
		final double uploadProgress = (1.0 * listLength)/totalAssignments;
		final String uploadProgressKey = RedisFilters.userBulkUploadProgressKey(saveRequest.getUserId());
		redisAdapter.set(uploadProgressKey, String.valueOf(uploadProgress), (long) DateTimeConstants.SECONDS_PER_DAY);

		if (totalAssignments.equals(listLength)) {
			// Reindex work
			final List<String> workNumbers = redisAdapter.getList(uploadKey);
			List<Long> failedRows = new ArrayList<>();
			long failedRow = workNumbers.size();
			for (Iterator<String> iterator = workNumbers.iterator(); iterator.hasNext(); failedRow--) {
				String currentWorkNumber = iterator.next();
				if (currentWorkNumber.isEmpty()) {
					iterator.remove();
					failedRows.add(failedRow);
				}
			}

			if (failedRows.size() > 0) {
				Collections.reverse(failedRows);
			}

			eventRouter.sendEvent(new WorkUpdateSearchIndexEvent().setWorkNumbers(workNumbers));

			// Send notification
			try {
				notificationDispatcher.dispatchNotification(notificationTemplateFactory.buildBulkUploadFinishedNotificationTemplate(saveRequest.getUserId(), workNumbers, failedRows));
			} catch (Exception e) {
				logger.error("There was an error sending BulkUploadFinshedNotification notification.", e);
			}

			logger.info(String.format("Finished asynchronous bulk save. %d assignments were saved.", listLength));

		}

		return response;
	}

	private void acceptWorkPostProcess(final AbstractWork work) {
		final WorkResource activeResource = checkNotNull(workService.findActiveWorkResource(work.getId()));
		userNotificationService.onWorkAccepted(work.getId(), activeResource.getUser().getId());
		if (work.isActive()) {
			webHookEventService.onWorkAccepted(
				work.getId(), work.getCompany().getId(), activeResource.getUser().getId());
		}
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(work.getId()));

		// Set workResource.appointment if assignment has specific date/time set so that 'Schedule Time' report field is populated
		workResourceService.setWorkResourceAppointmentFromWork(work.getId());

	}
}
