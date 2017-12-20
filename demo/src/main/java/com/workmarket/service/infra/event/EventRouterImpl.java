package com.workmarket.service.infra.event;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.common.metric.MetricRegistryFacade;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.common.template.NotificationTemplateFactory;
import com.workmarket.common.template.email.BlockCompanyNotificationTemplate;
import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.configuration.Constants;
import com.workmarket.data.solr.indexer.user.SolrVendorIndexer;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.data.solr.indexer.work.WorkIndexer;
import com.workmarket.domains.groups.facade.GroupInvitationFacade;
import com.workmarket.domains.groups.facade.UserGroupValidationFacade;
import com.workmarket.domains.groups.model.UserGroupInvitationType;
import com.workmarket.domains.groups.model.UserGroupPagination;
import com.workmarket.domains.groups.service.UserGroupValidationService;
import com.workmarket.domains.model.MboProfile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.assessment.Attempt;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.changelog.work.WorkNegotiationExpiredChangeLog;
import com.workmarket.domains.model.customfield.BulkSaveCustomFieldsRequest;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.AbstractTaxReport;
import com.workmarket.domains.model.tax.AbstractTaxReportSet;
import com.workmarket.domains.model.tax.EarningDetailReportSet;
import com.workmarket.domains.model.tax.EarningReport;
import com.workmarket.domains.model.tax.EarningReportSet;
import com.workmarket.domains.model.tax.TaxForm1099;
import com.workmarket.domains.model.tax.TaxForm1099Set;
import com.workmarket.domains.model.tax.TaxServiceReportSet;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.payments.service.BankingService;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.search.group.indexer.service.GroupIndexer;
import com.workmarket.domains.search.solr.SolrThreadLocal;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.model.negotiation.AbstractWorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.domains.work.service.DeliverableService;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkChangeLogService;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.actions.AddAttachmentsWorkEvent;
import com.workmarket.domains.work.service.actions.AddNotesWorkEvent;
import com.workmarket.domains.work.service.actions.ApproveForPaymentWorkEvent;
import com.workmarket.domains.work.service.actions.BulkCancelWorksEvent;
import com.workmarket.domains.work.service.actions.BulkEditClientProjectEvent;
import com.workmarket.domains.work.service.actions.BulkLabelRemovalEvent;
import com.workmarket.domains.work.service.actions.RemoveAttachmentsEvent;
import com.workmarket.domains.work.service.actions.RescheduleEvent;
import com.workmarket.domains.work.service.actions.WorkViewedEvent;
import com.workmarket.domains.work.service.actions.handlers.AddAttachmentsEventHandler;
import com.workmarket.domains.work.service.actions.handlers.AddNotesWorkEventHandler;
import com.workmarket.domains.work.service.actions.handlers.ApproveForPaymentEventHandler;
import com.workmarket.domains.work.service.actions.handlers.BulkCancelWorksEventHandler;
import com.workmarket.domains.work.service.actions.handlers.BulkEditClientProjectEventHandler;
import com.workmarket.domains.work.service.actions.handlers.BulkLabelRemovalEventHandler;
import com.workmarket.domains.work.service.actions.handlers.RemoveAttachmentsEventHandler;
import com.workmarket.domains.work.service.actions.handlers.RescheduleEventHandler;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.domains.work.service.route.RoutingStrategyFacade;
import com.workmarket.domains.work.service.route.WorkBundleRouting;
import com.workmarket.domains.work.service.route.WorkRoutingService;
import com.workmarket.domains.work.service.state.WorkStatusService;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.logging.NRTrace;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.reporting.service.EvidenceReportService;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.business.BankingFileGenerationService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.CustomFieldService;
import com.workmarket.service.business.DocumentationPackagerService;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.RatingService;
import com.workmarket.service.business.RequestService;
import com.workmarket.service.business.UserBulkUploadService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.WorkCustomFieldDTO;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.service.business.dto.WorkResourceDetailPagination;
import com.workmarket.service.business.event.AddToWorkerPoolEvent;
import com.workmarket.service.business.event.BulkSaveCustomFieldsEvent;
import com.workmarket.service.business.event.BulkUserUploadDispatchEvent;
import com.workmarket.service.business.event.BulkUserUploadFinishedEvent;
import com.workmarket.service.business.event.BulkUserUploadStarterEvent;
import com.workmarket.service.business.event.BulkWorkUploadEvent;
import com.workmarket.service.business.event.BulkWorkUploadStarterEvent;
import com.workmarket.service.business.event.BuyerSignUpSugarIntegrationEvent;
import com.workmarket.service.business.event.CompanyAvatarUpdatedEvent;
import com.workmarket.service.business.event.CompanyDueInvoicesEvent;
import com.workmarket.service.business.event.EntityUpdateEvent;
import com.workmarket.service.business.event.Event;
import com.workmarket.service.business.event.FundsProcessingEvent;
import com.workmarket.service.business.event.InviteToGroupEvent;
import com.workmarket.service.business.event.InviteToGroupFromCartEvent;
import com.workmarket.service.business.event.InviteToGroupFromRecommendationEvent;
import com.workmarket.service.business.event.InvoicesDownloadedEvent;
import com.workmarket.service.business.event.MarkUserNotificationsAsReadEvent;
import com.workmarket.service.business.event.MigrateBankAccountsEvent;
import com.workmarket.service.business.event.MigrateTaxEntitiesEvent;
import com.workmarket.service.business.event.RefreshUserNotificationCacheEvent;
import com.workmarket.service.business.event.RestoreBankAccountNumbersFromVault;
import com.workmarket.service.business.event.RestoreTaxEntityTaxNumbersFromVault;
import com.workmarket.service.business.event.RestoreTaxReportTaxNumbersFromVault;
import com.workmarket.service.business.event.SendLowBalanceAlertEvent;
import com.workmarket.service.business.event.TaxReportGenerationEvent;
import com.workmarket.service.business.event.TaxReportPublishedEvent;
import com.workmarket.service.business.event.TaxVerificationEvent;
import com.workmarket.service.business.event.UnlockCompanyEvent;
import com.workmarket.service.business.event.UpdateBankTransactionsStatusEvent;
import com.workmarket.service.business.event.UserGroupAssociationUpdateEvent;
import com.workmarket.service.business.event.UserGroupMessageNotificationEvent;
import com.workmarket.service.business.event.UserGroupValidationEvent;
import com.workmarket.service.business.event.UserGroupsValidationEvent;
import com.workmarket.service.business.event.WorkResourceCacheEvent;
import com.workmarket.service.business.event.WorkSubStatusTypeUpdatedEvent;
import com.workmarket.service.business.event.assessment.InviteUsersToAssessmentEvent;
import com.workmarket.service.business.event.assessment.TimedAssessmentAttemptAutoCompleteScheduledEvent;
import com.workmarket.service.business.event.asset.AssetBundleExpirationEvent;
import com.workmarket.service.business.event.asset.AssetExpirationEvent;
import com.workmarket.service.business.event.asset.BuildDocumentationPackageEvent;
import com.workmarket.service.business.event.asset.DeleteDeliverableEvent;
import com.workmarket.service.business.event.calendar.CalendarSyncAddAssignmentsEvent;
import com.workmarket.service.business.event.calendar.CalendarSyncRemoveAssignmentsEvent;
import com.workmarket.service.business.event.company.VendorSearchIndexEvent;
import com.workmarket.service.business.event.group.AddUsersToGroupEvent;
import com.workmarket.service.business.event.group.GroupUpdateSearchIndexEvent;
import com.workmarket.service.business.event.group.RevalidateGroupAssociationsEvent;
import com.workmarket.service.business.event.reporting.WorkReportGenerateEvent;
import com.workmarket.service.business.event.reports.DownloadCertificatesEvent;
import com.workmarket.service.business.event.reports.ExportEvidenceReportEvent;
import com.workmarket.service.business.event.user.BadActorEvent;
import com.workmarket.service.business.event.user.ProfileUpdateEvent;
import com.workmarket.service.business.event.user.UserAverageRatingEvent;
import com.workmarket.service.business.event.user.UserBlockCompanyEvent;
import com.workmarket.service.business.event.user.UserReassignmentEvent;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.business.event.work.ExecuteRoutingStrategyGroupEvent;
import com.workmarket.service.business.event.work.ResourceConfirmationRequiredScheduledEvent;
import com.workmarket.service.business.event.work.RoutingStrategyCompleteEvent;
import com.workmarket.service.business.event.work.RoutingStrategyScheduledEvent;
import com.workmarket.service.business.event.work.ValidateResourceCheckInScheduledEvent;
import com.workmarket.service.business.event.work.WorkAcceptedEvent;
import com.workmarket.service.business.event.work.WorkAutoCloseScheduledEvent;
import com.workmarket.service.business.event.work.WorkBundleAcceptEvent;
import com.workmarket.service.business.event.work.WorkBundleApplySubmitEvent;
import com.workmarket.service.business.event.work.WorkBundleCancelSubmitEvent;
import com.workmarket.service.business.event.work.WorkBundleDeclineOfferEvent;
import com.workmarket.service.business.event.work.WorkBundleDeclinedEvent;
import com.workmarket.service.business.event.work.WorkBundleRoutingEvent;
import com.workmarket.service.business.event.work.WorkBundleVendorRoutingEvent;
import com.workmarket.service.business.event.work.WorkClosedEvent;
import com.workmarket.service.business.event.work.WorkCompletedEvent;
import com.workmarket.service.business.event.work.WorkCreatedEvent;
import com.workmarket.service.business.event.work.WorkInvoiceGenerateEvent;
import com.workmarket.service.business.event.work.WorkNegotiationExpiredScheduledEvent;
import com.workmarket.service.business.event.work.WorkRepriceEvent;
import com.workmarket.service.business.event.work.WorkResendInvitationsEvent;
import com.workmarket.service.business.event.work.WorkResourceInvitation;
import com.workmarket.service.business.event.work.WorkResourceLateLabelScheduledEvent;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexByCompanyEvent;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.business.event.work.WorkUpdatedEvent;
import com.workmarket.service.business.integration.hooks.sugar.SugarIntegrationService;
import com.workmarket.service.business.integration.hooks.webhook.WebHookEventService;
import com.workmarket.service.business.integration.mbo.MboProfileDAO;
import com.workmarket.service.business.integration.mbo.SalesForceClient;
import com.workmarket.service.business.queue.WorkPaidDelayedEvent;
import com.workmarket.service.business.requirementsets.RequirementSetsService;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.business.tax.report.TaxReportGenerator;
import com.workmarket.service.business.tax.report.TaxReportService;
import com.workmarket.service.business.wrapper.AcceptWorkResponse;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.service.exception.work.WorkNotFoundException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.GoogleCalendarService;
import com.workmarket.service.infra.event.transactional.EventService;
import com.workmarket.service.infra.jms.JmsService;
import com.workmarket.service.infra.notification.NotificationDispatcher;
import com.workmarket.service.infra.notification.NotificationService;
import com.workmarket.service.search.group.GroupSearchService;
import com.workmarket.service.search.user.SearchCSVGenerateEvent;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.service.thrift.transactional.TWorkService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.thrift.work.Resource;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.thrift.work.WorkSaveRequest;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.vault.models.VaultKeyValuePair;
import com.workmarket.vault.services.VaultHelper;
import com.workmarket.vault.services.VaultMigrationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Lists.partition;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
public class EventRouterImpl implements EventRouter {

	private static final Log logger = LogFactory.getLog(EventRouterImpl.class);

	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private GroupSearchService groupSearchService;
	@Autowired private JmsService jmsService;
	@Autowired private UserGroupService userGroupService;
	@Autowired private WorkService workService;
	@Autowired private WorkResourceService workResourceService;
	@Autowired private TWorkService tWorkService;
	@Autowired private TWorkFacadeService tWorkFacadeService;
	@Autowired private WorkBundleService workBundleService;
	@Autowired private EventService eventService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private AssessmentService assessmentService;
	@Autowired private UserService userService;
	@Autowired private CompanyService companyService;
	@Autowired private TaxService taxService;
	@Autowired private BillingService billingService;
	@Qualifier("taxReport1099Generator") @Autowired private TaxReportGenerator taxReport1099Generator;
	@Qualifier("earningReportGenerator") @Autowired private TaxReportGenerator earningReportGenerator;
	@Qualifier("earningDetailReportGenerator") @Autowired private TaxReportGenerator earningDetailReportGenerator;
	@Qualifier("taxServiceReportGenerator") @Autowired private TaxReportGenerator taxServiceDetailReportGenerator;
	@Autowired private RequestService requestService;
	@Autowired private WorkStatusService workStatusService;
	@Autowired private LaneService laneService;
	@Autowired private NotificationService notificationService;
	@Autowired private SugarIntegrationService sugarIntegrationService;
	@Autowired private WorkChangeLogService workChangeLogService;
	@Autowired private WorkIndexer workIndexer;
	@Autowired private NotificationTemplateFactory notificationTemplateFactory;
	@Autowired private UserIndexer userIndexer;
	@Autowired private GroupIndexer groupIndexer;
	@Autowired private SolrVendorIndexer vendorIndexer;
	@Autowired private NotificationDispatcher notificationDispatcher;
	@Autowired private EvidenceReportService evidenceReportService;
	@Autowired private AddNotesWorkEventHandler addNotesWorkEventHandler;
	@Autowired private RemoveAttachmentsEventHandler removeAttachmentsEventHandler;
	@Autowired private ApproveForPaymentEventHandler approveForPaymentEventHandler;
	@Autowired private AddAttachmentsEventHandler addAttachmentsEventHandler;
	@Autowired private UserGroupValidationService userGroupValidationService;
	@Autowired private RoutingStrategyFacade routingStrategyFacade;
	@Autowired private RescheduleEventHandler rescheduleEventHandler;
	@Autowired private GoogleCalendarService googleCalendarService;
	@Autowired private BulkLabelRemovalEventHandler bulkLabelRemovalEventHandler;
	@Autowired private TaxReportService taxReportService;
	@Autowired private DocumentationPackagerService documentationPackagerService;
	@Autowired private BulkCancelWorksEventHandler bulkCancelWorksEventHandler;
	@Autowired private BulkEditClientProjectEventHandler bulkEditClientProjectEventHandler;
	@Autowired private MboProfileDAO mboProfileDAO;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired private SalesForceClient salesForceClient;
	@Autowired private ProfileService profileService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private WorkBundleRouting workBundleRouting;
	@Autowired private WorkNegotiationService workNegotiationService;
	@Autowired private BankingFileGenerationService bankingFileGenerationService;
	@Autowired private DeliverableService deliverableService;
	@Autowired private WorkSearchService workSearchService;
	@Autowired private RedisAdapter redisAdapter;
	@Autowired private WorkRoutingService workRoutingService;
	@Autowired private GroupInvitationFacade groupInvitationFacade;
	@Autowired private VaultMigrationService vaultMigrationService;
	@Autowired private RatingService ratingService;
	@Autowired private RequirementSetsService requirementSetsService;
	@Autowired private UserBulkUploadService userBulkUploadService;
	@Autowired private VaultHelper vaultHelper;
	@Autowired private BankingService bankingService;
	@Autowired private UserGroupValidationFacade userGroupValidationFacade;
	@Autowired private WebHookEventService webHookEventService;
	@Autowired private CustomFieldService customFieldService;

	@Value(value = "${usergroup.adduserstogroup.buffer.size}")
	private int addUsersToGroupBufferSize;

	// suppress logging for these due to frequency/size
	private static final Set<Class<?>> EVENTS_NO_LOG = ImmutableSet.<Class<?>>of(
			RefreshUserNotificationCacheEvent.class);

	private Meter workResourceInvitationCreateSalesforceFeed;
	private Meter profileUpdateCreateSalesforceFeed;

	@PostConstruct
	public void init() {
		final MetricRegistryFacade metricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "mbo");
		this.workResourceInvitationCreateSalesforceFeed =
			metricRegistryFacade.meter("create_salesforce_feed.work_resource_invitation");
		this.profileUpdateCreateSalesforceFeed = metricRegistryFacade.meter("create_salesforce_feed.profile_update");
	}

	@Override
	public void sendEvent(Event event) {
		if (event != null) {
			webRequestContextProvider.inject(event);
			setCurrentUserOnEvent(event);
			jmsService.sendEventMessage(event);
		}
	}

	@Override
	public void sendEvents(Collection<? extends Event> events) {
		if (isNotEmpty(events)) {
			for (Event event : events) {
				webRequestContextProvider.inject(event);
				setCurrentUserOnEvent(event);
				jmsService.sendEventMessage(event);
			}
		}
	}

	private void setCurrentUserOnEvent(final Event event) {
		if (event.getUser() == null) {
			event.setUser(authenticationService.getCurrentUser());
		}
	}

	@Override
	public void onEvent(Object e) {
		try {

			// The listener is running in a separate context from wherever the event originated from.
			// Set the context's current user to that declared on the event.
			if (e instanceof Event) {
				Event event = (Event) e;
				User currentUser = event.getUser();
				if (currentUser == null) {
					// Defensively set to anonymous user if the event failed to
					// set its user context.
					currentUser = userService.getUser(Constants.WORKMARKET_SYSTEM_USER_ID);
				} else {
					//reload the user
					currentUser = userService.findUserById(currentUser.getId());
				}
				authenticationService.setCurrentUser(currentUser);
			}

			Method method = getMethod(e.getClass());
			if (method != null) {
				if (!EVENTS_NO_LOG.contains(e.getClass())) {
					logger.debug(String.format("Processing %s", e.toString()));
				}
				method.invoke(this, e);
			} else {
				logger.error(String.format("Can't process event for %s, event handler not found", e.toString()));
			}
		} catch (Exception ex) {
			logger.error(String.format("Can't process event for %s", e.toString()), ex);
		}
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/AddAttachmentsWorkEvent")
	public void onEvent(AddAttachmentsWorkEvent event) {
		addAttachmentsEventHandler.handleEvent(event);
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/UserGroupAssociationUpdateEvent")
	public void onEvent(UserGroupAssociationUpdateEvent event) {
		Assert.notNull(event);

		if (event.getGroupId() == null) {
			return;
		}

		groupSearchService.reindexGroup(event.getGroupId());

		if (CollectionUtils.isEmpty(event.getUserIds())) {
			return;
		}

		userIndexer.reindexById(event.getUserIds());
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/EntityUpdateEvent")
	public void onEvent(EntityUpdateEvent event) {
		eventService.processEvent(event);
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/WorkNegotiationExpiredScheduledEvent")
	public void onEvent(WorkNegotiationExpiredScheduledEvent event) {
		Assert.notNull(event);
		AbstractWorkNegotiation negotiation = workNegotiationService.findById(event.getWorkNegotiationId());
		if (!negotiation.getDeleted() && negotiation.getApprovalStatus().isPending()) {
			workChangeLogService.saveWorkChangeLog(new WorkNegotiationExpiredChangeLog(negotiation.getWork().getId(), authenticationService
				.getCurrentUser().getId(), authenticationService.getMasqueradeUserId(), null, ((WorkNegotiation) negotiation)));
		}
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/ValidateResourceCheckInScheduledEvent")
	public void onEvent(ValidateResourceCheckInScheduledEvent event) {
		workService.validateResourceCheckIn(event.getWorkId());
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/ResourceConfirmationRequiredScheduledEvent")
	public void onEvent(ResourceConfirmationRequiredScheduledEvent event) {
		eventService.processEvent(event);
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/UserGroupMessageNotificationEvent")
	public void onEvent(UserGroupMessageNotificationEvent event) {
		eventService.processEvent(event);
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/CompanyAvatarUpdatedEvent")
	@SuppressWarnings("unchecked")
	public void onEvent(CompanyAvatarUpdatedEvent event) {
		Long companyId = event.getCompanyId();
		UserGroupPagination pagination = new UserGroupPagination();
		pagination.setResultsLimit(100);
		pagination = userGroupService.findAllGroupsByCompanyId(companyId, pagination);

		for (int i = 0; i < pagination.getNumberOfPages(); i++) {
			List<Long> groupIds = CollectionUtilities.newListPropertyProjection(pagination.getResults(), "id");
			groupSearchService.reindexGroups(groupIds);
			pagination.nextPage();
			pagination = userGroupService.findAllGroupsByCompanyId(companyId, pagination);
		}
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/WorkAutoCloseScheduledEvent")
	public void onEvent(WorkAutoCloseScheduledEvent event) {
		workService.closeWork(event.getWorkId());
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/TimedAssessmentAttemptAutoCompleteScheduledEvent")
	public void onEvent(TimedAssessmentAttemptAutoCompleteScheduledEvent event) {
		Attempt attempt = event.getAttempt();
		Assert.notNull(attempt);

		logger.debug(String.format("[assessment] Time is up for attempt [%d]", attempt.getId()));
		assessmentService.completeAttemptForAssessment(attempt.getId());
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/AssetBundleExpirationEvent")
	public void onEvent(AssetBundleExpirationEvent event) {
		logger.debug(String.format("[asset-bundle] Asset bundle download expired [%s]", event.getAssetUuid()));
		// TODO Do we actually want to do anything (e.g. delete) with the asset once it has expired?
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/AssetExpirationEvent")
	public void onEvent(AssetExpirationEvent event) {
		logger.debug(String.format("[asset] Asset download expired [%s]", event.getAssetUuid()));
		// TODO Do we actually want to do anything (e.g. delete) with the asset once it has expired?
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/DeleteDeliverableEvent")
	public void onEvent(DeleteDeliverableEvent event) {
		logger.debug(String.format("[asset] Delete deliverable event [%s]", event.toString()));
		deliverableService.removeDeliverablesAtPositionFromWork(
				event.getWorkId(), event.getDeliverableRequirementId(), event.getPosition());
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/WorkUpdatedEvent")
	public void onEvent(WorkUpdatedEvent event) {
		eventService.processEvent(event);
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/WorkCreatedEvent")
	public void onEvent(WorkCreatedEvent event) {
		eventService.processEvent(event);
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/WorkBundleDeclinedEvent")
	public void onEvent(WorkBundleDeclinedEvent event) {
		Assert.notNull(event);

		WorkBundle bundle = workBundleService.findById(event.getWorkId(), true);
		Assert.notNull(bundle);

		for (Work work : bundle.getBundle()) {
			workService.declineWork(event.getUserId(), work.getId(), event.getOnBehalfOfUserId());
		}
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/WorkBundleDeclinedEvent")
	public void onEvent(WorkBundleDeclineOfferEvent event) {
		workBundleService.declineAllWorkInBundle(event.getWorkNumber(), event.getNegotiationId(), event.getNote());
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/WorkBundleApplySubmitEvent")
	public void onEvent(WorkBundleApplySubmitEvent event) {
		Assert.notNull(event);

		workBundleService.applySubmitBundleHandler(event);
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/WorkBundleCancelSubmitEvent")
	public void onEvent(WorkBundleCancelSubmitEvent event) {
		Assert.notNull(event);

		workBundleService.cancelSubmitBundleHandler(event);
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/WorkUpdateSearchIndexEvent")
	public void onEvent(WorkUpdateSearchIndexEvent event) {
		Assert.notNull(event);

		boolean byIds = isNotEmpty(event.getWorkIds());
		boolean byWorkNumbers = isNotEmpty(event.getWorkNumbers());

		if (byIds && event.isDelete()) {
			logger.info("[workReindex] Deleting work from index " + event.getWorkIds());
			workIndexer.deleteById(Sets.newHashSet(event.getWorkIds()));
		} else if (byIds) {
			workIndexer.reindexById(Sets.newHashSet(event.getWorkIds()));
		} else if (byWorkNumbers) {
			workIndexer.reindexWorkByWorkNumbers(Sets.newHashSet(event.getWorkNumbers()));
		} else if (event.getFromId() != null && event.getToId() != null) {
			logger.info("indexing work from: " + event.getFromId() + " to: " + event.getToId());
			workIndexer.reindexBetweenIds(event.getFromId(), event.getToId());
		}
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/WorkUpdateSearchIndexByCompanyEvent")
	public void onEvent(WorkUpdateSearchIndexByCompanyEvent event) {
		workIndexer.reindexWorkByCompany(event.getCompanyId());
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/UserSearchIndexEvent")
	public void onEvent(UserSearchIndexEvent event) {
		Assert.notNull(event);

		try {
			boolean byIds = isNotEmpty(event.getUserIds());
			boolean byIdRange = event.getToId() != null && event.getFromId() != null;

			// set our directed towards (which may be null but that is ok)
			SolrThreadLocal.setDirectedTowards(event.getDirectedTowards());

			if (byIds && event.isDelete()) {
				logger.info("[userIndex] Deleting users from index: " + event.getUserIds());
				userIndexer.deleteById(Sets.newHashSet(event.getUserIds()));
			} else {
				if (byIds) {
					logger.info("[userIndex] Indexing users:  " + event.getUserIds());
					userIndexer.reindexById(Lists.newArrayList(event.getUserIds()));
				}
				if (byIdRange) {
					logger.info("[userIndex] Indexing users from: " + event.getFromId() + " to: " + event.getToId());
					userIndexer.reindexBetweenIds(event.getFromId(), event.getToId());
				}
			}
		} finally {
			// make sure to clear it out of ThreadLocal since these Listener
			// threads are re-used.
			SolrThreadLocal.clear();
		}
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/VendorSearchIndexEvent")
	public void onEvent(VendorSearchIndexEvent event) {
		Assert.notNull(event);

		boolean byIds = isNotEmpty(event.getIds());
		boolean byIdRange = event.getToId() != null && event.getFromId() != null;

		if (byIds && event.isDelete()) {
			logger.info("[vendorIndex] Deleting vendors from index: " + event.getIds());
			vendorIndexer.deleteById(Sets.newHashSet(event.getIds()));
		} else {
			if (byIds) {
				logger.info("[vendorIndex] Indexing vendors:  " + event.getIds());
				vendorIndexer.reindexById(Lists.newArrayList(event.getIds()));
			} else if (byIdRange) {
				logger.info("[vendorIndex] Indexing vendors from: " + event.getFromId() + " to: " + event.getToId());
				vendorIndexer.reindexBetweenIds(event.getFromId(), event.getToId());
			} else {
				vendorIndexer.reindexAll();
			}
		}

	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/AddUsersToGroupEvent")
	public void onEvent(AddUsersToGroupEvent event) {
		if (event.getGroupId() == null || CollectionUtils.isEmpty(event.getUserIds()) || event.getInviteeUserId() == null) {
			return;
		}

		if (event.getUserIds().size() <= addUsersToGroupBufferSize) {
			userGroupService.addUsersToGroup(event.getUserIds(), event.getGroupId(), event.getInviteeUserId());
			return;
		}

		/**
		 * Let's partition the users in to more manageable chunks so we don't have a super long
		 * running thread on the workers that might cause problems.
		 */
		List<List<Long>> partitions = Lists.partition(event.getUserIds(), addUsersToGroupBufferSize);

		for (List<Long> partition : partitions) {
			sendEvent(new AddUsersToGroupEvent(event.getGroupId(), partition, event.getInviteeUserId()));
		}
	}

	@Override
	public void onEvent(MigrateBankAccountsEvent event) {
		vaultMigrationService.migrateBankAccounts(event.getBankAccountIds());
	}

	@Override
	public void onEvent(MigrateTaxEntitiesEvent event) {
		vaultMigrationService.migrateTaxEntities(event.getTaxEntityIds(), event.isSaveVaultedValues(),
				event.isSaveDuplicateTins());
	}

	@Override
	public void onEvent(GroupUpdateSearchIndexEvent event) {
		Assert.notNull(event);

		if (event.getFromId() != null && event.getToId() != null) {
			logger.info("indexing groups from: " + event.getFromId() + " to: " + event.getToId());
			groupIndexer.reindexBetweenIds(event.getFromId(), event.getToId());
		} else if (isNotEmpty(event.getGroupIds())) {
			logger.info("indexing groups: " + event.getGroupIds());
			groupIndexer.reindexById(event.getGroupIds());
		}
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/WorkAcceptedEvent")
	public void onEvent(WorkAcceptedEvent event) {
		Assert.notNull(event);
		Assert.notNull(event.getWorkId());

		userNotificationService.onWorkAcceptedEvent(event);
		googleCalendarService.addConfirmedAssignmentToCalendar(event.getWorkId());
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/WorkSubStatusTypeUpdatedEvent")
	public void onEvent(WorkSubStatusTypeUpdatedEvent event) {
		eventService.processEvent(event);
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/TaxVerificationEvent")
	public void onEvent(TaxVerificationEvent event) {

		List<Long> ids = event.getIds();
		Assert.notEmpty(ids);
		logger.debug(String.format("[irs match] Processing %d TaxVerificationEvent entities", ids.size()));

		List<? extends AbstractTaxEntity> entities = taxService.findTaxEntitiesById(ids);
		if (isNotEmpty(entities)) {
			for (AbstractTaxEntity ent : entities) {
				try {
					NotificationTemplate template = notificationTemplateFactory.buildTaxEntityNotificationTemplate(ent);
					notificationDispatcher.dispatchNotification(template);
				} catch (Exception e) {
					logger.error("[irs match] message send failed", e);
				}
			}
		}
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/TaxReportGenerationEvent")
	public void onEvent(TaxReportGenerationEvent event) {
		AbstractTaxReportSet taxReportSet = event.getTaxReportSet();
		if (taxReportSet instanceof EarningReportSet) {
			earningReportGenerator.setTaxReportSet(taxReportSet);
			earningReportGenerator.generateTaxReport();
		} else if (taxReportSet instanceof TaxForm1099Set) {
			taxReport1099Generator.setTaxReportSet(taxReportSet);
			taxReport1099Generator.generateTaxReport();
		} else if (taxReportSet instanceof EarningDetailReportSet) {
			earningDetailReportGenerator.setTaxReportSet(taxReportSet);
			earningDetailReportGenerator.generateTaxReport();
		} else if (taxReportSet instanceof TaxServiceReportSet) {
			taxServiceDetailReportGenerator.setTaxReportSet(taxReportSet);
			taxServiceDetailReportGenerator.generateTaxReport();
		}

		if (taxReportSet.isIssued()) {
			try {
				notificationDispatcher.dispatchNotification(notificationTemplateFactory.buildTaxReportGeneratedNotificationTemplate(event.getRequestor().getId()));
			} catch (Exception e) {
				logger.error("[TaxReportGenerationEvent] failed to send email", e);
			}
		}
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/TaxReportPublishedEvent")
	public void onEvent(TaxReportPublishedEvent event) {
		if (event.getTaxReportSet() != null) {
			if (event.getTaxReportSet().isPublished()) {

				if (event.getTaxReportSet() instanceof TaxForm1099Set) {
					List<TaxForm1099> taxForm1099List = taxReportService.findAllTaxForm1099ByTaxForm1099SetId(event.getTaxReportSet().getId());

					for (AbstractTaxReport report : taxForm1099List) {
						List<NotificationTemplate> templates = notificationTemplateFactory.buildTaxReportAvailableNotificationTemplates(report);
						try {
							notificationDispatcher.dispatchNotifications(templates);
						} catch (Exception e) {
							logger.error("Error sending email for report " + report, e);
						}
					}

				} else if (event.getTaxReportSet() instanceof EarningReportSet) {
					List<EarningReport> earningReportList = taxReportService.findAllEarningReportByEarningReportSetId(event.getTaxReportSet().getId());

					for (AbstractTaxReport report : earningReportList) {
						List<NotificationTemplate> templates = notificationTemplateFactory.buildTaxReportAvailableNotificationTemplates(report);
						try {
							notificationDispatcher.dispatchNotifications(templates);
						} catch (Exception e) {
							logger.error("Error sending email for report " + report, e);
						}
					}
				}


			}
		}
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/WorkResourceLateLabelScheduledEvent")
	public void onEvent(WorkResourceLateLabelScheduledEvent event) {
		eventService.processEvent(event);
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/WorkInvoiceGenerateEvent")
	public void onEvent(WorkInvoiceGenerateEvent event) {
		eventService.processEvent(event);
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/WorkReportGenerateEvent")
	public void onEvent(WorkReportGenerateEvent event) {
		eventService.processEvent(event);
	}


	@SuppressWarnings("rawtypes")
	private Method getMethod(Class c) {
		Class clazz = c;
		Method method = null;

		while (method == null && clazz != Object.class) {
			try {
				method = getClass().getMethod("onEvent", clazz);
			} catch (NoSuchMethodException e) {
				clazz = clazz.getSuperclass();
			}
		}

		return method;
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/UserReassignmentEvent")
	public void onEvent(UserReassignmentEvent event) {
		Assert.notNull(event);
		Assert.notNull(event.getCurrentUserId());
		Assert.notNull(event.getNextGroupOwnerId());
		Assert.notNull(event.getNextAssessmentOwnerId());
		Assert.notNull(event.getNextWorkOwnerId());

		long prevOwnerId = event.getCurrentUserId();
		int groupCount = userGroupService.reassignGroupOwnership(event.getCurrentUserId(), event.getNextGroupOwnerId());
		int testCount = assessmentService.reassignAssessmentsOwnership(event.getCurrentUserId(), event.getNextAssessmentOwnerId());
		int workCount = workService.reassignWorkOwnership(event.getCurrentUserId(), event.getNextWorkOwnerId());
		logger.info("Number of Tests reassigned from User: " + prevOwnerId + " to User: " + event.getNextAssessmentOwnerId() + " = " + testCount);
		logger.info("Number of Groups reassigned from User: " + prevOwnerId + " to User: " + event.getNextGroupOwnerId() + " = " + groupCount);
		logger.info("Number of Work Assignments reassigned from User: " + prevOwnerId + " to User: " + event
				.getNextWorkOwnerId() + " = " + workCount);
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/InviteUsersToAssessmentEvent")
	public void onEvent(InviteUsersToAssessmentEvent event) {
		Assert.notNull(event);
		if (isNotEmpty(event.getInviteeUserNumbers())) {
			int BULK_OPERATIONS_BUFFER_SIZE = 200;
			List<List<String>> subList = partition(Lists.newArrayList(event.getInviteeUserNumbers()), BULK_OPERATIONS_BUFFER_SIZE);
			long assessmentId = event.getAssessmentId();
			long userId = event.getUserId();
			for (List<String> userNumbers : subList) {
				Set<Long> userIds = userService.findAllUserIdsByUserNumbers(userNumbers);
				requestService.inviteUsersToAssessment(userId, userIds, assessmentId);
				logger.info(String.format("INVITE USERS TO ASSESSMENT with %d users: %s from userid %d", userIds.size(), userIds.toString(), event.getUserId()));
				userIndexer.reindexById(userIds);
			}
		}
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/InviteToGroupEvent")
	public void onEvent(InviteToGroupEvent event) {
		Assert.notNull(event);
		Long masqUserId = null;
		if (event.getMasqueradeUser() != null) {
			masqUserId = event.getMasqueradeUser().getId();
		}

		groupInvitationFacade.inviteUsersToGroups(event.getGroupId(), event.getInvitedByUserId(), masqUserId, event.getInviteeUserIds());
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/InviteToGroupFromRecommendationEvent")
	public void onEvent(InviteToGroupFromRecommendationEvent event) {
		Assert.notNull(event);
		Long masqUserId = null;
		if (event.getMasqueradeUser() != null) {
			masqUserId = event.getMasqueradeUser().getId();
		}

		groupInvitationFacade.inviteUsersToGroups(event.getGroupId(), event.getInvitedByUserId(), masqUserId, event.getInviteeUserIds(), UserGroupInvitationType.RECOMMENDATION);
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/InviteToGroupFromCartEvent")
	public void onEvent(InviteToGroupFromCartEvent event) {
		Assert.notNull(event);
		Assert.notNull(event.getGroupId());
		Assert.notNull(event.getInvitedByUserId());

		List<String> userNumbers = Lists.newArrayList();
		userNumbers.addAll(event.getInviteeUserNumbers());
		logger.info(String.format("BULK ADD TO GROUP with %d users from user %s",
			userNumbers.size(), event.getInvitedByUserId()));

		// translate our user numbers to user id's for use
		Set<User> users = userService.findAllUsersByUserNumbers(userNumbers);
		Assert.notNull(users);
		List<Long> userIds = extract(users, on(User.class).getId());

		// and then just re-use our InviteToGroupEvent
		InviteToGroupEvent newEvent = new InviteToGroupEvent();
		newEvent.setGroupId(event.getGroupId());
		newEvent.setInvitedByUserId(event.getInvitedByUserId());
		newEvent.setInviteeUserIds(userIds);
		onEvent(newEvent);
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/AddToWorkerPoolEvent")
	public void onEvent(AddToWorkerPoolEvent event) {
		Assert.notNull(event);
		Assert.notNull(event.getCompanyId());
		Assert.hasText(event.getUserNumber());
		for (String userNumber : event.getCartUsers()) {
			laneService.addUserToWorkerPool(event.getCompanyId(), event.getUserNumber(), userNumber);
		}
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/DownloadCertificatesEvent")
	public void onEvent(DownloadCertificatesEvent event) {
		Assert.notNull(event);
		evidenceReportService.bulkDownloadEvidenceReportHandler(event.getToEmail(), event.getGroupId(),
				event.getScreeningType());
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/ExportEvidenceReportEvent")
	public void onEvent(ExportEvidenceReportEvent event) {
		Assert.notNull(event);
		evidenceReportService.exportToCSVHandler(event.getToEmail(), event.getGroupId(), event.getScreeningType());
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/WorkResendInvitationsEvent")
	public void onEvent(WorkResendInvitationsEvent event) {
		Assert.notNull(event);
		if (event.getWorkId() != null) {
			workService.resendInvitations(event.getWorkId(), event.getResourcesIds());
		}
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/AddNotesWorkEvent")
	public void onEvent(AddNotesWorkEvent event) {
		addNotesWorkEventHandler.handleEvent(event);
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/RemoveAttachmentsEvent")
	public void onEvent(RemoveAttachmentsEvent event) {
		removeAttachmentsEventHandler.handleEvent(event);
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/ApproveForPaymentWorkEvent")
	public void onEvent(ApproveForPaymentWorkEvent event) {
		authenticationService.setCurrentUser(event.getUser());
		approveForPaymentEventHandler.handleEvent(event);
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/RevalidateGroupAssociationsEvent")
	public void onEvent(RevalidateGroupAssociationsEvent event) {
		Assert.notNull(event);
		if (event.getMemberUserId() != null && MapUtils.isNotEmpty(event.getModificationType())) {
			userGroupValidationService.revalidateAllAssociationsByUser(event.getMemberUserId(), event.getModificationType());
		} else if (event.getGroupId() != null) {
			userGroupValidationService.revalidateAllAssociations(event.getGroupId());
		}
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/SearchCSVGenerateEvent")
	public void onEvent(SearchCSVGenerateEvent event) {
		eventService.processEvent(event);
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/WorkViewedEvent")
	public void onEvent(WorkViewedEvent event) {
		eventService.processEvent(event);
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/WorkClosedEvent")
	public void onEvent(WorkClosedEvent event) {
		Assert.notNull(event);

		final Long activeWorkerId = workService.findActiveWorkerId(event.getWorkId());

		billingService.emailInvoiceForWork(event.getWorkId());
		try {
			workStatusService.onPostTransitionToClosed(event.getWorkId(), event.getCloseWorkDTO());
		} catch (Exception e) {
			logger.error("[workClosedEvent] Error calling workStatusService.onPostTransitionToClosed " + event.getWorkId());
		}
		try {
			workResourceService.onPostTransitionToClosed(event.getWorkId(), event.getCloseWorkDTO());
		} catch (Exception e) {
			logger.error("[workClosedEvent] Error calling workResourceService.onPostTransitionToClosed " + event.getWorkId());
		}

		sendEvent(new UserSearchIndexEvent(activeWorkerId));
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/UserBlockCompanyEvent")
	public void onEvent(UserBlockCompanyEvent event) {
		Assert.notNull(event);
		List<Long> workIds = workService.findOpenWorkIdsBetweenUserAndCompany(event.getUserId(), event.getCompanyId());
		if (isNotEmpty(workIds)) {
			for (Long workId : workIds) {
				workService.declineWork(event.getUserId(), workId);
			}
			workIndexer.reindexById(Sets.newHashSet(workIds));
		}

		// Remove the company's employees from all the blockedCompany's groups, and viceversa
		userGroupService.removeAllAssociationsBetweenCompanies(
				userService.findUserById(event.getUserId()).getCompany().getId(), event.getCompanyId());
		List<BlockCompanyNotificationTemplate> notificationTemplates = notificationTemplateFactory.buildBlockCompanyNotificationTemplates(event);
		notificationDispatcher.dispatchNotifications(notificationTemplates);
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/BadActorEvent")
	public void onEvent(BadActorEvent event) {
		Assert.notNull(event);
		for(Long blockingCompanyId : event.getBlockingCompanyIds()) {
			if(!userService.isCompanyBlockingUser(event.getBlockedUserId(), blockingCompanyId)) {
				userService.blockUserFromCompany(Constants.WORKMARKET_SYSTEM_USER_ID, event.getBlockedUserId(), blockingCompanyId);
			}
		}
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/RoutingStrategyScheduledEvent")
	public void onEvent(RoutingStrategyScheduledEvent event) {
		Assert.notNull(event);
		routingStrategyFacade.executeRoutingStrategy(event.getRoutingStrategyId());
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/ExecuteRoutingStrategyGroupEvent")
	public void onEvent(ExecuteRoutingStrategyGroupEvent event) {
		Assert.notNull(event);
		routingStrategyFacade.executeRoutingStrategyGroup(event.getRoutingStrategyGroupId());
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/RoutingStrategyCompleteEvent")
	public void onEvent(RoutingStrategyCompleteEvent event) {
		Assert.notNull(event);
		routingStrategyFacade.executeRoutingStrategyComplete(event.getRoutingStrategyId());
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/SendLowBalanceAlertEvent")
	public void onEvent(SendLowBalanceAlertEvent event) {
		Assert.notNull(event);
		logger.debug("Sending alert for low balance to user: " + event.getUserId() + " Email: " + event.getEmail());
		EmailTemplate template = notificationTemplateFactory.buildLowBalanceEmailTemplate(event.getUserId(), event.getEmail(), event.getSpendLimit());
		notificationService.sendNotification(template, event.getScheduleDate());
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/RefreshUserNotificationCacheEvent")
	public void onEvent(RefreshUserNotificationCacheEvent event) {
		eventService.processEvent(event);
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/MarkUserNotificationsAsReadEvent")
	public void onEvent(MarkUserNotificationsAsReadEvent event) {
		eventService.processEvent(event);
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/WorkResourceInvitation")
	public void onEvent(WorkResourceInvitation event) {
		Assert.notNull(event);
		Assert.notNull(event.getWorkId());
		Assert.notEmpty(event.getUserResourceIds());

		Work work = workService.findWorkForInvitation(event.getWorkId());
		Assert.notNull(work);

		if (work.isInBundle()) {
			return;
		}

		final boolean isBundle = workBundleService.isAssignmentBundle(work);
		final int mandatoryRequirementCount = isBundle ? 0 : requirementSetsService.getMandatoryRequirementCountByWorkId(work.getId());

		for (Long workerId : event.getUserResourceIds()) {
			try {
				notificationService.sendNotification(
					notificationTemplateFactory.buildWorkResourceInvitation(
						work, workerId, event.isVoiceDelivery(), isBundle, mandatoryRequirementCount
					)
				);

				for (Long dispatcherId : workResourceService.getAllDispatcherIdsForWorker(workerId)) {
					notificationService.sendNotification(
						notificationTemplateFactory.buildWorkResourceInvitation(
							work, dispatcherId, workerId, event.isVoiceDelivery(), isBundle, mandatoryRequirementCount
						)
					);
				}

			} catch (Exception e) {
				logger.error("Error sending email to userId " + workerId + " workId " + event.getWorkId(), e);
			}
		}

		// TODO: Alex - put this MBO stuff in its own event handler
		// when MBO resources get assignments, we create a Feed item in MBO Salesforce
		Set<Long> mboResources = mboProfileDAO.filterMboResourcesFromList(event.getUserResourceIds());
		for (Long userId : mboResources) {
			try {
				workResourceInvitationCreateSalesforceFeed.mark();
				salesForceClient.createFeed(work.getWorkNumber(), profileService.findMboProfile(userId));
			} catch (Exception ex) {
				logger.error("[MBO] attempt to create Feed object failed", ex);
			}
		}
	}

	@Override
	public void onEvent(BuyerSignUpSugarIntegrationEvent event) {
		Assert.notNull(event);
		logger.debug("[Create Leads in Sugar] CompanyId : " + event.getCompanyId());
		sugarIntegrationService.createLead(event.getCompanyId());
	}

	@Override
	public void onEvent(RescheduleEvent event) {
		logger.debug("Processing [rescheduleEvent]");
		rescheduleEventHandler.handleEvent(event);
		workIndexer.reindexWorkByWorkNumbers(Sets.newHashSet(event.getWorkNumbers()));
	}

	@Override
	public void onEvent(CalendarSyncAddAssignmentsEvent event) {
		Assert.notNull(event);
		Assert.notNull(event.getUserId());
		googleCalendarService.syncAllAssignmentsToCalendar(event.getUserId());
	}

	@Override
	public void onEvent(CalendarSyncRemoveAssignmentsEvent event) {
		Assert.notNull(event);
		Assert.notNull(event.getUserId());
		googleCalendarService.deleteCalendarAndEvents(event.getUserId(), event.getRefreshToken());
		googleCalendarService.revokeAuthorizationFromWM(event.getUserId());
	}

	@Override
	public void onEvent(BulkLabelRemovalEvent event) {
		logger.debug("Processing [bulkLabelRemovalEvent]");
		bulkLabelRemovalEventHandler.handleEvent(event);
		workIndexer.reindexById(Sets.newHashSet(event.getWorkIds()));
	}

	@Override
	public void onEvent(BuildDocumentationPackageEvent event) {
		Assert.notNull(event);
		Assert.notNull(event.getGroupId());
		Assert.notNull(event.getUserIds());

		logger.debug("Processing [BuildDocumentationPackageEvent]");
		documentationPackagerService.getDocumentationPackageForUsers(event.getDownloaderId(), event.getGroupId(),
				event.getUserIds());
	}

	@Override
	public void onEvent(BulkCancelWorksEvent event) {
		logger.debug("Processing [bulkCancelWorksEvent]");
		bulkCancelWorksEventHandler.handleEvent(event);
		workIndexer.reindexById(event.getWorkIds());
	}

	@Override
	public void onEvent(BulkEditClientProjectEvent event) {
		logger.debug("Processing bulkEditClientProjectEvent");
		bulkEditClientProjectEventHandler.handleEvent(event);
		workIndexer.reindexWorkByWorkNumbers(Sets.newHashSet(event.getWorkNumbers()));
	}

	@Override
	public void onEvent(ProfileUpdateEvent event) {
		logger.debug("Processing profileUpdateEvent");
		Assert.notNull(event);
		Assert.notNull(event.getUserId());
		Assert.notNull(event.getProperties());

		MboProfile mboProfile = profileService.findMboProfile(event.getUserId());
		if (mboProfile != null) {
			try {
				profileUpdateCreateSalesforceFeed.mark();
				salesForceClient.updateUser(mboProfile.getObjectId(), event.getProperties());
			} catch (Exception e) {
				logger.error("[MBO] Error updating profile for userId " + event.getUserId(), e);
			}
		}
	}

	@Override
	public void onEvent(WorkCompletedEvent event) {
		Assert.notNull(event);
		Assert.notNull(event.getWorkId());
		userNotificationService.onWorkCompleted(event.getWorkId(), event.isCompleteOnbehalf());
	}

	@Override
	public void onEvent(UnlockCompanyEvent event) {
		Assert.notNull(event);
		companyService.unlockCompanyAccount(event.getCompanyId());
	}

	@Override
	public void onEvent(CompanyDueInvoicesEvent event) {
		Assert.notNull(event);
		Assert.notNull(event.getCompanyId());
		companyService.processDueInvoicesForCompany(event.getCompanyId());
	}

	@Override
	public void onEvent(final BulkWorkUploadEvent event) {
		Assert.notNull(event);
		final ImmutableList.Builder workIdBuilder = ImmutableList.builder();
		for (final WorkSaveRequest saveRequest : event.getSaveRequests()) {
			final WorkResponse resp = tWorkFacadeService.eventUploadHelper(saveRequest, event.getUploadKey(), event.getUploadSizeKey());
			if (resp != null && resp.getWork() != null && resp.getWork().getId() > 0) {
				workIdBuilder.add(resp.getWork().getId());
			}
		}

		// route bundles
		// criteria:
		//  - must be a bundle
		//  - must have routing
		//  - we must be able to get its id
		Collection<WorkSaveRequest> bundleRequests = filter(event.getSaveRequests(), new Predicate<WorkSaveRequest>() {
			@Override public boolean apply(WorkSaveRequest workSaveRequest) {
				return workSaveRequest.isBundle() && workSaveRequest.getWork().isSetResources();
			}
		});
		List<Long> workIdsToIndex = Lists.newArrayList();
		for (final WorkSaveRequest saveRequest : bundleRequests) {
			final String redisKey = event.getUploadKey() + saveRequest.getBundleTitle();
			final Optional<Object> redisVal = redisAdapter.get(redisKey);
			if (redisVal.isPresent()) {
				Long bundleId = Long.valueOf((String) redisVal.or("0"));

				List<Resource> resources = saveRequest.getWork().getResources();
				Set<Long> resourceIds = Sets.newHashSet(extract(resources, on(Resource.class).getId()));

				try {
					Map<WorkAuthorizationResponse, Set<String>> userMap =
						workRoutingService.addToWorkResources(bundleId, resourceIds, saveRequest.isAssignToFirstToAccept()).getResponse();
					if (userMap != null) {
						workBundleRouting.routeWorkBundle(bundleId);
						workIdsToIndex.add(bundleId);
					}
				} catch (WorkNotFoundException e) {
					logger.error("assignment not found " + bundleId, e);
				}
			}
		}
		if (CollectionUtils.isNotEmpty(workIdsToIndex)) {
			workSearchService.reindexWorkAsynchronous(workIdsToIndex);
		}

		// send on create webhook
		final Long companyId = getCurrentUserCompanyId();
		final List<Long> workIds = workIdBuilder.build();
		for (final Long workId : workIds) {
			if (companyId != null) {
				webHookEventService.onWorkCreated(workId, companyId, null);
			} else {
				webHookEventService.onWorkCreated(workId, null);
			}
		}
	}

	private Long getCurrentUserCompanyId() {
	 	if (authenticationService.getCurrentUser() != null
				&& authenticationService.getCurrentUser().getCompany() != null
				&& authenticationService.getCurrentUser().getCompany().getId() != null) {
			return authenticationService.getCurrentUser().getCompany().getId();
		}

		return authenticationService.getCurrentUserCompanyId();
	}

	@Override
	public void onEvent(BulkWorkUploadStarterEvent event) {
		Assert.notNull(event);
		tWorkService.startUploadEventHelper(event.getUploadRequest(), event.getUserId());
	}

	@Override
	public void onEvent(UpdateBankTransactionsStatusEvent event) {
		Assert.notNull(event, "Update bank transaction status event is null.");
		List<Long> errorIds = Lists.newArrayList();

		for (Long tid : event.getTransactionIds()) {
			try {
				bankingFileGenerationService.updateBankTransactionStatus(event.getUserId(), tid, event.getNotes(), event.getStatusCode());
			} catch (Exception e) {
				errorIds.add(tid);
			}
		}

		if (CollectionUtils.isNotEmpty(errorIds)) {
			logger.error("There is an error when processing the following banking transactions" + errorIds.toString());
		}

	}

	@Override
	public void onEvent(WorkPaidDelayedEvent event) {
		logger.debug(event);
		Assert.notNull(event);
		Assert.notNull(event.getWorkId());
		Work work = workService.findWork(event.getWorkId());
		if (work == null) {
			return;
		}

		WorkResource workResource = workService.findWorkResourceById(event.getWorkResourceId());
		if (workResource == null) {
			return;
		}

		try {
			logger.info("****** Processing onWorkPaid for work id: " + event.getWorkId());
			userNotificationService.onWorkClosedAndPaid(event.getWorkResourceId());
			workService.updateMyPaidResourcesGroup(event.getWorkId(), event.getWorkResourceId(), event.getActor());
		} catch (Exception e) {
			logger.error("Error while processing work id " + event.getWorkId(), e);
		}
	}

	@Override
	public void onEvent(WorkResourceCacheEvent event) {
		Assert.notNull(event);
		Long workId = event.getWorkId();
		Work work = workService.findWork(workId);
		boolean requiresApplication = !work.isAssignToFirstResourceEnabled();
		WorkResourceDetailPagination pagination = new WorkResourceDetailPagination();
		if (requiresApplication) {
			pagination.setIncludeApplyNegotiation(true);
		} else {
			pagination.setIncludeLabels(true);
			pagination.setIncludeNotes(true);
		}
		pagination.setReturnAllRows(true);

		workResourceService.populateWorkResourceDetailCache(workId, pagination);
	}

	@Override
	public void onEvent(FundsProcessingEvent event) {
		Assert.notNull(event);
		userNotificationService.onFundsProcessed(event.getTransactionId());
	}

	@Override
	public void onEvent(WorkBundleRoutingEvent event) {
		Assert.notNull(event);
		Assert.notNull(event.getWorkBundleId());
		logger.info("[event] Calling routeWorkBundle for id: " + event.getWorkBundleId());
		workBundleRouting.routeWorkBundle(event.getWorkBundleId());
	}

	@Override
	public void onEvent(WorkBundleVendorRoutingEvent event) {
		Assert.notNull(event);
		Assert.notNull(event.getWorkBundleId());
		logger.info("[event] Calling routeWorkBundle for id: " + event.getWorkBundleId());
		workBundleRouting.routeWorkBundleToVendor(event.getWorkBundleId());
	}

	@Override
	public void onEvent(WorkBundleAcceptEvent event) {
		Assert.notNull(event);
		Assert.notNull(event.getWorkId());
		Assert.notNull(event.getUserId());
		logger.info("[event] Calling acceptAllWorkInBundle for id: " + event.getWorkId());

		List<AcceptWorkResponse> failures = tWorkFacadeService.acceptWorkBundle(event.getUserId(), event.getWorkId());
		Set<Long> workIds = workBundleService.getAllWorkIdsInBundle(event.getWorkId());
		workSearchService.reindexWorkAsynchronous(workIds);

		if (failures.size() > 0) {
			Work work = workService.findWork(event.getWorkId());
			User user = userService.findUserById(event.getUserId());

			for (AcceptWorkResponse failure : failures) {
				userNotificationService.onBundleWorkAcceptFailed(workService.getBuyerIdByWorkId(event.getWorkId()), user, work, failure);
				logger.error(String.format("[event] acceptAllWorkInBundle failed to accept %s/%s: %s",
					failure.getWork().getTitle(), failure.getWork().getWorkNumber(), failure.getMessages().get(0)));
			}
		}
	}

	@Override
	public void onEvent(UserAverageRatingEvent event) {
		Assert.notNull(event);
		ratingService.refreshAverageRatingForUserByCompany(event.getRatedUserId(), event.getRaterCompanyId());
	}

	@Override
	public void onEvent(InvoicesDownloadedEvent event) {
		Assert.notNull(event);
		Assert.notNull(event.getLoggedInUserId());

		billingService.updateInvoiceLastDownloadedDate(event.getInvoiceIds(), DateUtilities.getCalendarNow(),
			event.getLoggedInUserId());
	}

	@Override
	public void onEvent(BulkUserUploadStarterEvent event) {
		Assert.notNull(event);
		userBulkUploadService.start(event.getUploadRequest(), event.getResponse(), event.isOrgEnabledForUser());
	}

	public void onEvent(BulkUserUploadDispatchEvent event) {
		Assert.notNull(event);
		userBulkUploadService.upload(event.getUserId(), event.getUuid(), event.getUserImportDTO(), event.getOrgUnitPath());
	}

	@Override
	public void onEvent(BulkUserUploadFinishedEvent event) {
		Assert.notNull(event);
		userBulkUploadService.finish(event.getResponse());
	}

	@Override
	public void onEvent(final RestoreTaxEntityTaxNumbersFromVault event) {
		final List<? extends AbstractTaxEntity> entities = taxService
			.findAllTaxEntitiesFromModifiedDate(DateUtilities.getCalendarFromISO8601(event.getFromModifiedOnDate()));

		for (final AbstractTaxEntity e : entities) {
			final VaultKeyValuePair vaultValue = vaultHelper.get(e, "taxNumber", "");
			if (!vaultValue.isEmpty()) {
				e.setTaxNumber(vaultValue.getValue());
				taxService.saveTaxEntity(e);
			} else {
				logger.error("Restore error: Empty vault value for tax entity " + e.getId());
			}
		}
	}

	@Override
	public void onEvent(final RestoreBankAccountNumbersFromVault event) {
		final List<BankAccount> accounts =
			bankingService.getAllBankAccountsFrom(DateUtilities.getCalendarFromISO8601(event.getFromCreatedOnDate()));
		for (final BankAccount bc : accounts) {
			final VaultKeyValuePair pair = vaultHelper.get(bc, "accountNumber", "");
			if (!pair.isEmpty()) {
				bc.setAccountNumber(pair.getValue());
				bankingService.saveOrUpdate(bc);
			} else {
				logger.error("Restore error: Empty vault value for bank account " + bc.getId());
			}
		}
	}

	@Override
	public void onEvent(final RestoreTaxReportTaxNumbersFromVault event) {
		final List<? extends AbstractTaxReport> reports =
			taxReportService.getAllReports(DateUtilities.getCalendarFromISO8601(event.getFromCreatedOn()));

		for (final AbstractTaxReport report : reports) {
			final VaultKeyValuePair pair = vaultHelper.get(report.getTaxEntity(), "taxNumber", "");
			if (!pair.isEmpty()) {
				report.setTaxNumber(pair.getValue());
				taxReportService.saveTaxReport(report);
			} else {
				logger.error("Restore error: Empty vault value for tax report " + report.getId());
			}
		}
	}

	@Override
	public void onEvent(UserGroupsValidationEvent event) {
		userGroupValidationFacade.revalidateUserGroups();
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/RevalidateUserGroup")
	public void onEvent(UserGroupValidationEvent event) {
		userGroupValidationFacade.revalidateUserGroup(event.getUserGroupId());
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/BulkSaveCustomFields")
	public void onEvent(BulkSaveCustomFieldsEvent event) {
		Set<Long> workIdsToIndex = Sets.newHashSet();
		for (BulkSaveCustomFieldsRequest request : event.getRequests()) {
			workIdsToIndex.add(request.getWorkId());
			customFieldService.addWorkCustomFieldGroupToWork(request.getCustomFieldGroupId(), request.getWorkId(), 0);
			customFieldService.saveWorkCustomFieldsForWork(
				request.getDtos().toArray(new WorkCustomFieldDTO[request.getDtos().size()]),
				request.getWorkId(),
				event.getUser(),
				event.getMasqueradeUserId()
			);
		}
		workIndexer.reindexById(workIdsToIndex);
	}

	@Override
	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/WorkReprice")
	public void onEvent(WorkRepriceEvent event) {
		int succeeded = 0;
		int failed = 0;
		Set<Long> workIdsToIndex = Sets.newHashSet();
		for (Long workId : event.getWorkIds()) {
			WorkDTO dto = new WorkDTO();
			dto.setPricingStrategyId(event.getPricingStrategyId());
			dto.setFlatPrice(event.getFlatPrice());
			dto.setPerHourPrice(event.getPerHourPrice());
			dto.setMaxNumberOfHours(event.getMaxNumberOfHours());
			dto.setPerUnitPrice(event.getPerUnitPrice());
			dto.setMaxNumberOfUnits(event.getMaxNumberOfUnits());
			dto.setInitialPerHourPrice(event.getInitialPerHourPrice());
			dto.setInitialNumberOfHours(event.getInitialNumberOfHours());
			dto.setAdditionalPerHourPrice(event.getAdditionalPerHourPrice());
			dto.setMaxBlendedNumberOfHours(event.getMaxBlendedNumberOfHours());
			dto.setUseMaxSpendPricingDisplayModeFlag("spend".equals(event.getPricingMode()));

			try {
				List<ConstraintViolation> violations = workService.repriceWork(workId, dto);
				if (violations.isEmpty()) {
					workIdsToIndex.add(workId);
					succeeded++;
				} else {
					failed++;
				}
			} catch (InsufficientFundsException e) {
				failed++;
				logger.error("Insufficient Funds to reprice work", e);
			} catch (Exception e) {
				failed++;
				logger.error("Error repricing work", e);
			}
		}
		workIndexer.reindexById(workIdsToIndex);
		try {
			NotificationTemplate template =
				notificationTemplateFactory.buildBulkWorkRepricResultNotificationTemplate(event.getUser().getId(), succeeded, failed);
			notificationDispatcher.dispatchNotification(template);
		} catch (Exception e) {
			logger.error("buildBulkWorkRepriceResult", e);
		}
	}
}
