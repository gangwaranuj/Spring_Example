package com.workmarket.service.business;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workmarket.common.cache.UserNotificationCache;
import com.workmarket.common.core.RequestContext;
import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.common.template.NotificationTemplateFactory;
import com.workmarket.common.template.UserGroupInvitationNotificationTemplate;
import com.workmarket.common.template.WorkAcceptedDetailsNotificationTemplate;
import com.workmarket.common.template.WorkAttachmentAddedNotificationTemplate;
import com.workmarket.common.template.WorkBonusNegotiationAddedNotificationTemplate;
import com.workmarket.common.template.WorkBonusNegotiationApprovedNotificationTemplate;
import com.workmarket.common.template.WorkBudgetNegotiationAddedNotificationTemplate;
import com.workmarket.common.template.WorkBudgetNegotiationApprovedNotificationTemplate;
import com.workmarket.common.template.WorkBundleNegotiationApprovedNotificationTemplate;
import com.workmarket.common.template.WorkCancelledNotificationTemplate;
import com.workmarket.common.template.WorkCancelledWithoutPayNotificationTemplate;
import com.workmarket.common.template.WorkCompleteNotificationTemplate;
import com.workmarket.common.template.WorkCompletedByBuyerNotificationTemplate;
import com.workmarket.common.template.WorkCompletedFundsAddedNotificationTemplate;
import com.workmarket.common.template.WorkDeliverableDueReminderNotificationTemplate;
import com.workmarket.common.template.WorkDeliverableLateNotificationTemplate;
import com.workmarket.common.template.WorkExpenseNegotiationAddedNotificationTemplate;
import com.workmarket.common.template.WorkExpenseNegotiationApprovedNotificationTemplate;
import com.workmarket.common.template.WorkIncompleteNotificationTemplate;
import com.workmarket.common.template.WorkNegotiationApprovedNotificationTemplate;
import com.workmarket.common.template.WorkNotAvailableNotificationTemplate;
import com.workmarket.common.template.WorkNoteAddedNotificationTemplate;
import com.workmarket.common.template.WorkQuestionAnsweredNotificationTemplate;
import com.workmarket.common.template.WorkRatingCreatedNotificationTemplate;
import com.workmarket.common.template.WorkReinvitedNotificationTemplate;
import com.workmarket.common.template.WorkRemindResourceToCompleteNotificationTemplate;
import com.workmarket.common.template.WorkRescheduleNegotiationApprovedNotificationTemplate;
import com.workmarket.common.template.WorkRescheduleNegotiationApprovedOnBehalfOfNotificationTemplate;
import com.workmarket.common.template.WorkResourceCheckInNotificationTemplate;
import com.workmarket.common.template.WorkResourceConfirmationNotificationTemplate;
import com.workmarket.common.template.WorkSubStatusAlertNotificationTemplate;
import com.workmarket.common.template.WorkSubStatusNotificationTemplate;
import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.common.template.email.NotificationEmailTemplate;
import com.workmarket.dao.google.CalendarSyncSettingsDAO;
import com.workmarket.dao.summary.work.WorkMilestonesDAO;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.MboProfile;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.WorkQuestionAnswerPair;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.changelog.PropertyChange;
import com.workmarket.domains.model.changelog.PropertyChangeType;
import com.workmarket.domains.model.changelog.work.WorkPropertyChangeType;
import com.workmarket.domains.model.fulfillment.FulfillmentStrategy;
import com.workmarket.domains.model.note.WorkNote;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.model.notification.UserNotification;
import com.workmarket.domains.model.option.WorkOption;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.model.summary.work.WorkMilestones;
import com.workmarket.domains.model.tax.TaxVerificationStatusType;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.follow.WorkFollow;
import com.workmarket.domains.work.model.negotiation.AbstractWorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkBonusNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkBudgetNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkExpenseNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiationPagination;
import com.workmarket.domains.work.model.negotiation.WorkRescheduleNegotiation;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeAssociation;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.follow.WorkFollowService;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.dto.UnreadNotificationsDTO;
import com.workmarket.feature.FeatureToggleClient;
import com.workmarket.feature.gen.Messages;
import com.workmarket.feature.vo.FeatureToggleAndStatus;
import com.workmarket.notification.NotificationClient;
import com.workmarket.notification.user.vo.UserNotificationSearchRequest;
import com.workmarket.notification.user.vo.UserNotificationSearchResponse;
import com.workmarket.notification.vo.EmailNotifyResponse;
import com.workmarket.service.business.dto.CancelWorkDTO;
import com.workmarket.service.business.dto.EMailDTO;
import com.workmarket.service.business.dto.PaymentSummaryDTO;
import com.workmarket.service.business.dto.WorkResourceLabelDTO;
import com.workmarket.service.business.event.Event;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.event.MarkUserNotificationsAsReadEvent;
import com.workmarket.service.business.event.RefreshUserNotificationCacheEvent;
import com.workmarket.service.business.event.work.ResourceConfirmationRequiredScheduledEvent;
import com.workmarket.service.business.event.work.ValidateResourceCheckInScheduledEvent;
import com.workmarket.service.business.event.work.WorkAcceptedEvent;
import com.workmarket.service.business.event.work.WorkAutoCloseScheduledEvent;
import com.workmarket.service.business.event.work.WorkResourceLateLabelScheduledEvent;
import com.workmarket.service.business.event.work.WorkUpdatedEvent;
import com.workmarket.service.business.integration.mbo.MboProfileDAO;
import com.workmarket.service.business.integration.mbo.SalesForceClient;
import com.workmarket.service.business.pay.PaymentSummaryService;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.helpers.ServiceResponseBuilder;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.dto.UserNotificationDTO;
import com.workmarket.service.infra.email.EmailService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.notification.NotificationDispatcher;
import com.workmarket.service.infra.notification.NotificationService;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.service.option.OptionsService;
import com.workmarket.service.web.WebRequestContextProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.refEq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserNotificationServiceTest {
	@Mock WebRequestContextProvider webRequestContextProvider;
	@Mock NotificationClient notificationClient;
	@Mock UserGroupService userGroupService;
	@Mock NotificationService notificationService;
	@Mock SalesForceClient salesForceClient;
	@Mock ProfileService profileService;
	@Mock MboProfileDAO mboProfileDAO;
	@Mock UserService userService;
	@Mock WorkService workService;
	@Mock WorkBundleService workBundleService;
	@Mock CalendarSyncSettingsDAO calendarSyncSettingsDAO;
	@Mock NotificationTemplateFactory notificationTemplateFactory;
	@Mock NotificationDispatcher notificationDispatcher;
	@Mock OptionsService<AbstractWork> workOptionsService;
	@Mock EmailService emailService;
	@Mock UserNotificationCache userNotificationCache;
	@Mock EventRouter eventRouter;
	@Mock PaymentSummaryService paymentSummaryService;
	@Mock WorkFollowService workFollowService;
	@Mock WorkResourceService workResourceService;
	@Mock WorkResourceDAO workResourceDAO;
	@Mock WorkNegotiationService workNegotiationService;
	@Mock EventFactory eventFactory;
	@Mock AuthenticationService authenticationService;
	@Mock ClientServiceAlertService clientServiceAlertService;
	@Mock WorkSubStatusService workSubStatusService;
	@Mock TaxService taxService;
	@Mock WorkMilestonesDAO workMilestonesDAO;
	@Mock UserNotificationPrefsService userNotificationPrefsService;
	@Mock MetricRegistry metricRegistry;
	@Mock FeatureEvaluator featureEvaluator;
	@Mock private FeatureToggleClient featureToggleClient;
	@Mock FeatureEntitlementService featureEntitlementService;
	@Mock CompanyService companyService;
	@InjectMocks UserNotificationServiceImpl userNotificationService = spy(new UserNotificationServiceImpl());
	@Mock Meter mockMeter;

	UserGroup group;
	UserUserGroupAssociation association;

	private static final Long
		WORKER_ID = 1L,
		WORKER2_ID = 2L,
		DISPATCHER_ID = 3L,
		WORK_ID = 4L,
		FOLLOWER_ID = 5L,
		OWNER_ID = 6L,
		MEMBER_ID = 7L,
		COMPANY_ID = 8L,
		USER_GROUP_ID = 100L;

	private static final String NOTIFICATION_UUID = "notification-uuid";

	private static final String START_UUID = "Start-uuid";
	private static final String END_UUID = "End-uuid";
	private static final String
		NOTE_FOR_WORKER = "You've been unassigned",
		NOTIFICATION_TYPE_CODE = "some.type";
	private static final List<PropertyChange> propertyChangeList = Lists.newArrayList();

	private User worker, worker2, dispatcher, follower, buyer;
	private WorkFollow workFollow;
	private WorkResource workResource = mock(WorkResource.class);
	private Work work;
	private Profile buyerProfile = mock(Profile.class);
	private MboProfile mboProfile = mock(MboProfile.class);
	private PaymentSummaryDTO payment = mock(PaymentSummaryDTO.class);
	private Company company = mock(Company.class);
	private ManageMyWorkMarket manageMyWorkMarket;
	private WorkAcceptedEvent workAcceptedEvent;
	private WorkUpdatedEvent workUpdatedEvent;
	private Map<PropertyChangeType, List<PropertyChange>> propertyChanges;
	private WorkNote note;
	private List<WorkContext> workContexts;
	private CancelWorkDTO cancelWorkDTO;
	private Asset asset;
	private List<WorkFollow> workFollows;
	private Rating rating;
	private WorkQuestionAnswerPair questionAnswerPair;
	private FulfillmentStrategy fulfillmentStrategy;
	private List<WorkResource> workResources;
	private UnreadNotificationsDTO unreadNotificationsDTO;
	private UserNotificationDTO userNotificationDTO;
	private UserNotification userNotification;
	private UserNotificationPreferencePojo notificationPreferencePojo;

	// Approved Notifications
	private WorkBundleNegotiationApprovedNotificationTemplate bundleApprovedNotification, bundleApprovedNotificationForDispatcher;
	private WorkNotAvailableNotificationTemplate workNotAvailableNotification, workNotAvailableNotificationDispatcher;

	private WorkNegotiationApprovedNotificationTemplate workNegotiationApprovedNotificationTemplate;
	private WorkBudgetNegotiationAddedNotificationTemplate workBudgetNegotiationAddedNotificationTemplate, workBudgetNegotiationAddedNotificationTemplateForDispatcher;
	private WorkBudgetNegotiationApprovedNotificationTemplate workBudgetNegotiationApprovedNotificationTemplate, workBudgetNegotiationApprovedNotificationTemplateForDispatcher;
	private WorkExpenseNegotiationAddedNotificationTemplate workExpenseNegotiationAddedNotificationTemplate, workExpenseNegotiationAddedNotificationTemplateForDispatcher;
	private WorkExpenseNegotiationApprovedNotificationTemplate workExpenseNegotiationApprovedNotificationTemplate, workExpenseNegotiationApprovedNotificationTemplateForDispatcher;
	private WorkBonusNegotiationAddedNotificationTemplate workBonusNegotiationAddedNotificationTemplate, workBonusNegotiationAddedNotificationTemplateForDispatcher;
	private WorkBonusNegotiationApprovedNotificationTemplate workBonusNegotiationApprovedNotificationTemplate, workBonusNegotiationApprovedNotificationTemplateForDispatcher;
	private WorkRescheduleNegotiationApprovedNotificationTemplate workRescheduleNegotiationApprovedNotificationTemplate, workRescheduleNegotiationApprovedNotificationTemplateForDispatcher;
	private WorkRescheduleNegotiationApprovedOnBehalfOfNotificationTemplate workRescheduleNegotiationApprovedOnBehalfOfNotificationTemplate, workRescheduleNegotiationApprovedOnBehalfOfNotificationTemplateForDispatcher;

	private NotificationTemplate workNegotiationDeclinedNotificationTemplate, workNegotiationDeclinedNotificationTemplateForDispatcher;

	// Notifications
	private WorkNoteAddedNotificationTemplate workAddedNotification = mock(WorkNoteAddedNotificationTemplate.class);
	private WorkCancelledNotificationTemplate workCancelledNotification = mock(WorkCancelledNotificationTemplate.class);
	private WorkCancelledWithoutPayNotificationTemplate workCancelledWithoutPayNotification = mock(WorkCancelledWithoutPayNotificationTemplate.class);
	private WorkCompletedFundsAddedNotificationTemplate workCompletedFundsAddedNotificationTemplate, workCompletedFundsAddedNotificationTemplateForDispatcher;
	private WorkReinvitedNotificationTemplate workReinvitedNotificationTemplate, workReinvitedNotificationTemplateForDispatcher;
	private WorkRemindResourceToCompleteNotificationTemplate workRemindResourceToCompleteNotificationTemplate, workRemindResourceToCompleteNotificationTemplateForDispatcher;
	private WorkIncompleteNotificationTemplate workIncompleteNotificationTemplate, workIncompleteNotificationTemplateForDispatcher;

	// Negotiations
	private WorkNegotiationPagination workNegotiationPagination;
	private WorkNegotiation workNegotiation, declinedWorkNegotiation;
	private WorkBudgetNegotiation workBudgetNegotiation;
	private WorkExpenseNegotiation workExpenseNegotiation;
	private WorkBonusNegotiation workBonusNegotiation;
	private WorkRescheduleNegotiation workRescheduleNegotiation;
	private WorkResourceConfirmationNotificationTemplate resourceConfirmationNotificationForDispatcher;
	private WorkResourceCheckInNotificationTemplate resourceCheckInNotificationForDispatcher;
	private WorkDeliverableDueReminderNotificationTemplate deliverableDueReminderNotificationForDispatcher;

	// WorkSubStatus
	private WorkSubStatusTypeAssociation typeAssociation;
	private WorkSubStatusAlertNotificationTemplate statusAlertNotificationForDispatcher;
	private WorkSubStatusNotificationTemplate statusNotificationForDispatcher;

	// PersonaPreference
	private PersonaPreference personaPreferenceDispatcher;
	private Optional<PersonaPreference> personaPreferenceOptional;

	private RequestContext context;

	@Before
	public void setup() {
		userNotificationService.init();
		when(metricRegistry.meter(anyString())).thenReturn(mockMeter);
		User owner = new User();
		owner.setId(OWNER_ID);
		User member = new User();
		member.setId(MEMBER_ID);

		buyer = mock(User.class);
		worker = mock(User.class);
		when(worker.getId()).thenReturn(WORKER_ID);
		worker2 = mock(User.class);
		when(worker2.getId()).thenReturn(WORKER2_ID);
		dispatcher = mock(User.class);
		when(dispatcher.getId()).thenReturn(DISPATCHER_ID);
		follower = mock(User.class);
		workFollow = mock(WorkFollow.class);
		when(follower.getId()).thenReturn(FOLLOWER_ID);
		when(workFollow.getUser()).thenReturn(follower);

		manageMyWorkMarket = mock(ManageMyWorkMarket.class);
		when(manageMyWorkMarket.getAutocloseEnabledFlag()).thenReturn(false);
		when(manageMyWorkMarket.getAssignToFirstResource()).thenReturn(false);

		BigDecimal amountEarned = mock(BigDecimal.class);

		questionAnswerPair = mock(WorkQuestionAnswerPair.class);
		when(questionAnswerPair.getQuestionerId()).thenReturn(WORKER_ID);

		UsaTaxEntity taxEntity = mock(UsaTaxEntity.class, RETURNS_DEEP_STUBS);
		when(taxEntity.getStatus().getCode()).thenReturn(TaxVerificationStatusType.APPROVED);

		workResources = Lists.newArrayList();
		workResources.add(workResource);

		fulfillmentStrategy = mock(FulfillmentStrategy.class);
		when(fulfillmentStrategy.getAmountEarned()).thenReturn(amountEarned);

		work = mock(Work.class);
		when(work.getManageMyWorkMarket()).thenReturn(manageMyWorkMarket);
		when(work.getId()).thenReturn(WORK_ID);
		when(work.getBuyer()).thenReturn(buyer);
		when(work.getCompany()).thenReturn(company);
		when(work.isActive()).thenReturn(true);
		when(work.isComplete()).thenReturn(true);
		when(work.getFulfillmentStrategy()).thenReturn(fulfillmentStrategy);

		rating = mock(Rating.class);
		when(rating.isRatingSharedFlag()).thenReturn(true);
		when(rating.getRatedUser()).thenReturn(worker);
		when(rating.getWork()).thenReturn(work);

		workNegotiation = mock(WorkNegotiation.class);
		when(workNegotiation.getWork()).thenReturn(work);
		when(workNegotiation.getRequestedBy()).thenReturn(worker);

		List<AbstractWorkNegotiation> workNegotiations = Lists.newArrayList();

		declinedWorkNegotiation = mock(WorkNegotiation.class);
		when(declinedWorkNegotiation.getRequestedBy()).thenReturn(worker2);
		workNegotiations.add(declinedWorkNegotiation);

		workBudgetNegotiation = mock(WorkBudgetNegotiation.class);
		when(workBudgetNegotiation.getWork()).thenReturn(work);
		when(workBudgetNegotiation.getRequestedBy()).thenReturn(worker);

		workExpenseNegotiation = mock(WorkExpenseNegotiation.class);
		when(workExpenseNegotiation.getWork()).thenReturn(work);
		when(workExpenseNegotiation.getRequestedBy()).thenReturn(worker);

		workBonusNegotiation = mock(WorkBonusNegotiation.class);
		when(workBonusNegotiation.getWork()).thenReturn(work);
		when(workBonusNegotiation.getRequestedBy()).thenReturn(worker);

		workExpenseNegotiation = mock(WorkExpenseNegotiation.class);
		when(workExpenseNegotiation.getWork()).thenReturn(work);
		when(workExpenseNegotiation.getRequestedBy()).thenReturn(worker);

		workRescheduleNegotiation = mock(WorkRescheduleNegotiation.class);
		when(workRescheduleNegotiation.getWork()).thenReturn(work);
		when(workRescheduleNegotiation.getRequestedBy()).thenReturn(worker);
		when(workRescheduleNegotiation.getApprovedBy()).thenReturn(worker);

		workNegotiationPagination = mock(WorkNegotiationPagination.class);
		doReturn(workNegotiationPagination).when(userNotificationService).makeWorkNegotiationPagination();
		when(workNegotiationPagination.getResults()).thenReturn(workNegotiations);

		Map<String, Object> bundleData = Maps.newHashMap();

		ServiceResponseBuilder serviceResponseBuilder = mock(ServiceResponseBuilder.class);
		when(serviceResponseBuilder.getData()).thenReturn(bundleData);

		workAcceptedEvent = mock(WorkAcceptedEvent.class);
		when(workAcceptedEvent.getWorkId()).thenReturn(WORK_ID);
		when(workAcceptedEvent.getResourceUserId()).thenReturn(WORKER_ID);

		workUpdatedEvent = mock(WorkUpdatedEvent.class);
		propertyChanges = new HashMap<>();

		note = mock(WorkNote.class);
		when(note.getWork()).thenReturn(work);
		when(note.getCreatorId()).thenReturn(WORKER_ID);
		when(note.getIsPrivate()).thenReturn(false);
		when(note.getReplyToId()).thenReturn(WORKER_ID);

		workContexts = Lists.newArrayList();
		cancelWorkDTO = mock(CancelWorkDTO.class);
		when(cancelWorkDTO.getNote()).thenReturn("derp");

		asset = mock(Asset.class);

		bundleApprovedNotification = mock(WorkBundleNegotiationApprovedNotificationTemplate.class);
		bundleApprovedNotificationForDispatcher = mock(WorkBundleNegotiationApprovedNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkBundleNegotiationApprovedNotificationTemplate(
			worker.getId(), work, workNegotiation, serviceResponseBuilder.getData()))
			.thenReturn(bundleApprovedNotification);
		when(notificationTemplateFactory.buildWorkBundleNegotiationApprovedNotificationTemplate(
			dispatcher.getId(), worker.getId(), work, workNegotiation, serviceResponseBuilder.getData()))
			.thenReturn(bundleApprovedNotificationForDispatcher);

		workNotAvailableNotification = mock(WorkNotAvailableNotificationTemplate.class);
		workNotAvailableNotificationDispatcher = mock(WorkNotAvailableNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkNotAvailableNotificationTemplate(worker2.getId(), work))
			.thenReturn(workNotAvailableNotification);
		when(notificationTemplateFactory.buildWorkNotAvailableNotificationTemplate(dispatcher.getId(), worker2.getId(), work))
			.thenReturn(workNotAvailableNotificationDispatcher);

		workNegotiationApprovedNotificationTemplate = mock(WorkNegotiationApprovedNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkNegotiationApprovedNotificationTemplate(dispatcher.getId(), work, workNegotiation))
			.thenReturn(workNegotiationApprovedNotificationTemplate);

		workBudgetNegotiationAddedNotificationTemplate = mock(WorkBudgetNegotiationAddedNotificationTemplate.class);
		workBudgetNegotiationAddedNotificationTemplateForDispatcher = mock(WorkBudgetNegotiationAddedNotificationTemplate.class);
		workBudgetNegotiationApprovedNotificationTemplate = mock(WorkBudgetNegotiationApprovedNotificationTemplate.class);
		workBudgetNegotiationApprovedNotificationTemplateForDispatcher = mock(WorkBudgetNegotiationApprovedNotificationTemplate.class);
		workExpenseNegotiationAddedNotificationTemplate = mock(WorkExpenseNegotiationAddedNotificationTemplate.class);
		workExpenseNegotiationAddedNotificationTemplateForDispatcher = mock(WorkExpenseNegotiationAddedNotificationTemplate.class);
		workExpenseNegotiationApprovedNotificationTemplate = mock(WorkExpenseNegotiationApprovedNotificationTemplate.class);
		workExpenseNegotiationApprovedNotificationTemplateForDispatcher = mock(WorkExpenseNegotiationApprovedNotificationTemplate.class);
		workBonusNegotiationAddedNotificationTemplate = mock(WorkBonusNegotiationAddedNotificationTemplate.class);
		workBonusNegotiationAddedNotificationTemplateForDispatcher = mock(WorkBonusNegotiationAddedNotificationTemplate.class);
		workBonusNegotiationApprovedNotificationTemplate = mock(WorkBonusNegotiationApprovedNotificationTemplate.class);
		workBonusNegotiationApprovedNotificationTemplateForDispatcher = mock(WorkBonusNegotiationApprovedNotificationTemplate.class);
		workRescheduleNegotiationApprovedNotificationTemplate = mock(WorkRescheduleNegotiationApprovedNotificationTemplate.class);
		workRescheduleNegotiationApprovedNotificationTemplateForDispatcher = mock(WorkRescheduleNegotiationApprovedNotificationTemplate.class);
		workRescheduleNegotiationApprovedOnBehalfOfNotificationTemplate = mock(WorkRescheduleNegotiationApprovedOnBehalfOfNotificationTemplate.class);
		workRescheduleNegotiationApprovedOnBehalfOfNotificationTemplateForDispatcher = mock(WorkRescheduleNegotiationApprovedOnBehalfOfNotificationTemplate.class);

		workNegotiationDeclinedNotificationTemplate = mock(NotificationTemplate.class);
		workNegotiationDeclinedNotificationTemplateForDispatcher = mock(NotificationTemplate.class);

		workCompletedFundsAddedNotificationTemplate = mock(WorkCompletedFundsAddedNotificationTemplate.class);
		workCompletedFundsAddedNotificationTemplateForDispatcher = mock(WorkCompletedFundsAddedNotificationTemplate.class);

		workReinvitedNotificationTemplate = mock(WorkReinvitedNotificationTemplate.class);
		workReinvitedNotificationTemplateForDispatcher = mock(WorkReinvitedNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkReinvitedNotificationTemplate(worker.getId(), work))
			.thenReturn(workReinvitedNotificationTemplate);
		when(notificationTemplateFactory.buildWorkReinvitedNotificationTemplate(dispatcher.getId(), work))
			.thenReturn(workReinvitedNotificationTemplateForDispatcher);

		workRemindResourceToCompleteNotificationTemplate = mock(WorkRemindResourceToCompleteNotificationTemplate.class);
		workRemindResourceToCompleteNotificationTemplateForDispatcher = mock(WorkRemindResourceToCompleteNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkRemindResourceToComplete(worker.getId(), note.getCreatorId(), work, note))
			.thenReturn(workRemindResourceToCompleteNotificationTemplate);
		when(notificationTemplateFactory.buildWorkRemindResourceToComplete(dispatcher.getId(), note.getCreatorId(), work, note))
			.thenReturn(workRemindResourceToCompleteNotificationTemplateForDispatcher);

		workIncompleteNotificationTemplate = mock(WorkIncompleteNotificationTemplate.class);
		workIncompleteNotificationTemplateForDispatcher = mock(WorkIncompleteNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkIncompleteNotificationTemplate(work, WORKER_ID, NOTE_FOR_WORKER))
			.thenReturn(workIncompleteNotificationTemplate);
		when(notificationTemplateFactory.buildWorkIncompleteNotificationTemplate(work, DISPATCHER_ID, NOTE_FOR_WORKER))
			.thenReturn(workIncompleteNotificationTemplateForDispatcher);

		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(null);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker2.getId())).thenReturn(null);

		group = new UserGroup();
		group.setId(USER_GROUP_ID);
		group.setOwner(owner);
		group.setOpenMembership(Boolean.TRUE);
		association = new UserUserGroupAssociation();
		association.setUserGroup(group);
		association.setUser(member);
		association.setVerificationStatus(VerificationStatus.VERIFIED);

		when(userGroupService.findGroupById(any(Long.class))).thenReturn(group);
		when(userService.findUserById(anyLong())).thenReturn(worker);
		when(workService.findWork(anyLong())).thenReturn(work);
		when(workService.findWork(eq(WORK_ID), anyBoolean())).thenReturn(work);
		when(workService.getWorkContext(eq(work.getId()), anyLong())).thenReturn(workContexts);
		when(workService.findWorkResourceById(workResource.getId())).thenReturn(workResource);
		when(workService.findWorkResourceById(worker.getId())).thenReturn(workResource);
		when(buyer.getId()).thenReturn(2L);
		when(buyer.getProfile()).thenReturn(buyerProfile);
		when(workBundleService.isAssignmentBundle(any(Work.class))).thenReturn(false);
		when(workBundleService.getBundleData(worker.getId(), work.getId())).thenReturn(serviceResponseBuilder);
		when(profileService.findMboProfile(anyLong())).thenReturn(null);
		when(workResource.getUser()).thenReturn(worker);
		when(workResource.getWork()).thenReturn(work);
		when(calendarSyncSettingsDAO.findByUser(anyLong())).thenReturn(null);
		when(workOptionsService.hasOption(any(AbstractWork.class), eq(WorkOption.MBO_ENABLED), eq("true"))).thenReturn(false);
		when(workService.findActiveWorkResource(anyLong())).thenReturn(workResource);
		when(workService.findActiveWorkerId(work.getId())).thenReturn(WORKER_ID);
		when(taxService.findActiveTaxEntity(worker.getId())).thenReturn(taxEntity);
		when(notificationDispatcher.dispatchEmail(any(EmailTemplate.class)))
				.thenReturn(new EmailNotifyResponse(EmailNotifyResponse.Status.OK));

		when(paymentSummaryService.generatePaymentSummaryForWork(work)).thenReturn(payment);
		when(notificationTemplateFactory.buildWorkCompleteNotificationTemplate(
			workResource.getUser().getId(), work.getBuyer().getId(), work, payment
		)).thenReturn(mock(WorkCompleteNotificationTemplate.class));
		workFollows = Lists.newArrayList();
		when(workFollowService.getWorkFollowers(work.getId())).thenReturn(workFollows);
		when(company.getManageMyWorkMarket()).thenReturn(manageMyWorkMarket);
		when(workNegotiationService.findByWork(work.getId(), workNegotiationPagination)).thenReturn(workNegotiationPagination);
		when(eventFactory.buildWorkUpdatedEvent(work.getId(), propertyChanges)).thenReturn(workUpdatedEvent);
		when(authenticationService.getCurrentUser()).thenReturn(worker);

		// setupPreWorkResourceNotifications fixture data
		Calendar alertDate = Calendar.getInstance();
		when(workService.calculateRequiredConfirmationNotificationDate(work)).thenReturn(Calendar.getInstance());
		when(workService.calculateRequiredConfirmationDate(work)).thenReturn(alertDate);
		when(workResource.getWork()).thenReturn(work);

		when(notificationTemplateFactory.buildWorkResourceConfirmationNotificationTemplate(
			worker.getId(), work, workService.getAppointmentTime(work)
		)).thenReturn(mock(WorkResourceConfirmationNotificationTemplate.class));
		resourceConfirmationNotificationForDispatcher = mock(WorkResourceConfirmationNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkResourceConfirmationNotificationTemplate(
			dispatcher.getId(), work, workService.getAppointmentTime(work)
		)).thenReturn(resourceConfirmationNotificationForDispatcher);

		ResourceConfirmationRequiredScheduledEvent resourceConfirmEvent = mock(ResourceConfirmationRequiredScheduledEvent.class);
		when(eventFactory.buildResourceConfirmationRequiredScheduledEvent(work, alertDate)).thenReturn(resourceConfirmEvent);

		Calendar FUTURE_CHECK_IN_DATE = Calendar.getInstance();
		FUTURE_CHECK_IN_DATE.add(Calendar.DATE, 1);
		Calendar PAST_CHECK_IN_DATE = Calendar.getInstance();
		PAST_CHECK_IN_DATE.add(Calendar.DATE, -1);
		when(workService.calculateRequiredCheckinDate(work)).thenReturn(FUTURE_CHECK_IN_DATE);

		when(eventFactory.buildValidateResourceCheckInScheduledEvent(work, FUTURE_CHECK_IN_DATE))
			.thenReturn(mock(ValidateResourceCheckInScheduledEvent.class));

		Calendar checkInReminderDate = mock(Calendar.class);
		when(workService.calculateRequiredCheckinReminderDate(work)).thenReturn(checkInReminderDate);

		WorkResourceCheckInNotificationTemplate resourceCheckInNotification = mock(WorkResourceCheckInNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkResourceCheckInNotificationTemplate(worker.getId(), work))
			.thenReturn(resourceCheckInNotification);
		notificationService.sendNotification(resourceCheckInNotification, checkInReminderDate);

		resourceCheckInNotificationForDispatcher = mock(WorkResourceCheckInNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkResourceCheckInNotificationTemplate(DISPATCHER_ID, work))
			.thenReturn(resourceCheckInNotificationForDispatcher);

		DateRange assignmentAppointmentDate = mock(DateRange.class);
		Calendar checkInLateResource = mock(Calendar.class);
		doReturn(assignmentAppointmentDate).when(userNotificationService).getAssignmentAppointmentDate(work, workResource);
		when(assignmentAppointmentDate.getThrough()).thenReturn(checkInLateResource);
		when(assignmentAppointmentDate.getFrom()).thenReturn(checkInLateResource);

		when(eventFactory.buildWorkResourceLateLabelScheduledEvent(workResource.getId(), checkInLateResource))
			.thenReturn(mock(WorkResourceLateLabelScheduledEvent.class));

		// onDeliverableDueReminder fixture data
		when(workResourceDAO.findById(workResource.getId())).thenReturn(workResource);
		when(notificationTemplateFactory.buildWorkDeliverableDueReminderNotificationTemplate(
			WORKER_ID, workResource.getWork()
		)).thenReturn(mock(WorkDeliverableDueReminderNotificationTemplate.class));
		deliverableDueReminderNotificationForDispatcher = mock(WorkDeliverableDueReminderNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkDeliverableDueReminderNotificationTemplate(
			DISPATCHER_ID, workResource.getWork()
		)).thenReturn(deliverableDueReminderNotificationForDispatcher);

		// onWorkSubStatus fixture data
		typeAssociation = mock(WorkSubStatusTypeAssociation.class);
		WorkSubStatusType statusType = mock(WorkSubStatusType.class);
		when(typeAssociation.getWorkSubStatusType()).thenReturn(statusType);
		when(typeAssociation.getWorkSubStatusType().isNotifyResourceEnabled()).thenReturn(true);
		when(typeAssociation.getWorkSubStatusType().isAlert()).thenReturn(true);
		when(statusType.getCode()).thenReturn("");

		statusAlertNotificationForDispatcher = mock(WorkSubStatusAlertNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkSubStatusAlertNotificationTemplate(WORKER_ID, typeAssociation, work, workResource))
			.thenReturn(mock(WorkSubStatusAlertNotificationTemplate.class));
		when(notificationTemplateFactory.buildWorkSubStatusAlertNotificationTemplate(DISPATCHER_ID, typeAssociation, work, workResource))
			.thenReturn(statusAlertNotificationForDispatcher);
		when(workResource.getWork()).thenReturn(work);

		when(notificationTemplateFactory.buildWorkSubStatusNotificationTemplate(WORKER_ID, typeAssociation, work,
			workResource))
			.thenReturn(mock(WorkSubStatusNotificationTemplate.class));
		statusNotificationForDispatcher = mock(WorkSubStatusNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkSubStatusNotificationTemplate(DISPATCHER_ID, typeAssociation, work, workResource))
			.thenReturn(statusNotificationForDispatcher);

		unreadNotificationsDTO = mock(UnreadNotificationsDTO.class);
		when(unreadNotificationsDTO.getStartUuid()).thenReturn(START_UUID);
		when(unreadNotificationsDTO.getEndUuid()).thenReturn(END_UUID);

		userNotificationDTO = mock(UserNotificationDTO.class);
		when(userNotificationDTO.getFromUserId()).thenReturn(WORKER_ID);
		when(userNotificationDTO.getToUserId()).thenReturn(WORKER2_ID);
		when(userService.getUser(userNotificationDTO.getFromUserId())).thenReturn(worker);
		when(userService.getUser(userNotificationDTO.getToUserId())).thenReturn(worker2);

		userNotification = mock(UserNotification.class);
		when(userNotification.getUuid()).thenReturn(NOTIFICATION_UUID);
		when(userNotification.getNotificationType()).thenReturn(NotificationType.newNotificationType(NOTIFICATION_TYPE_CODE));
		doReturn(userNotification).when(userNotificationService).makeUserNotification(userNotificationDTO, worker, worker2);

		notificationPreferencePojo = mock(UserNotificationPreferencePojo.class);

		when(userNotificationPrefsService.findByUserAndNotificationType(WORKER2_ID, NOTIFICATION_TYPE_CODE))
				.thenReturn(notificationPreferencePojo);

		personaPreferenceDispatcher = mock(PersonaPreference.class);
		personaPreferenceOptional = Optional.of(personaPreferenceDispatcher);
		when(personaPreferenceDispatcher.isDispatcher()).thenReturn(false);
		when(userService.getPersonaPreference(anyLong())).thenReturn(personaPreferenceOptional);

		when(featureEvaluator.hasGlobalFeature("userNotificationTrialWhichReturnControlOnly")).thenReturn(true);
		context = new RequestContext("DUMMY_REQUEST_ID", "DUMMY_TENANT_ID");
		when(webRequestContextProvider.getRequestContext()).thenReturn(context);
		when(notificationClient.archive((String) anyObject(), eq(context))).thenReturn(Observable.just("Foo"));
		when(notificationClient.setViewed((String) anyObject(), (String) anyObject(), (String) anyObject(), eq(context)))
				.thenReturn(Observable.just("FOO"));
		when(notificationClient.notify((com.workmarket.notification.user.vo.UserNotification) anyObject(), eq(context)))
				.thenReturn(Observable.just(com.workmarket.notification.user.vo.UserNotification.builder()
						.build()));
	}

	@Test
	public void sendUserNotification_isBullhornNotification_sendRefreshUserNotificationCacheEvent() {
		when(notificationPreferencePojo.isCacheable()).thenReturn(true);
		when(userNotificationDTO.getNotificationType()).thenReturn(NotificationType.newNotificationType(NOTIFICATION_TYPE_CODE));
		userNotificationService.sendUserNotification(userNotificationDTO);

		ArgumentCaptor<RefreshUserNotificationCacheEvent> argumentCaptor = ArgumentCaptor.forClass(RefreshUserNotificationCacheEvent.class);

		verify(eventRouter).sendEvent(argumentCaptor.capture());
		verify(notificationClient).notify((com.workmarket.notification.user.vo.UserNotification) anyObject(), eq(context));
		assertEquals(userNotificationDTO.getToUserId(), argumentCaptor.getValue().getUserId());
	}

	@Test
	public void serialization_ignores_encryptedIdAndHashId() throws JsonProcessingException {
		final UserNotification u = new UserNotification();
		final String serialized = new ObjectMapper().writeValueAsString(u);
		assertNotNull(serialized);
	}

	@Test
	public void sendUserNotification_isNotBullhornNotification_doNotSendRefreshUserNotificationCacheEvent() {
		when(notificationPreferencePojo.isCacheable()).thenReturn(false);

		verify(eventRouter, never()).sendEvent(any(RefreshUserNotificationCacheEvent.class));
	}

	@Test(expected = Exception.class)
	public void onUserGroupApplication_WithNullAssociation_barf() {
		userNotificationService.onUserGroupApplication(null);
	}

	@Test(expected = Exception.class)
	public void onUserGroupApplication_WithNullGroup_barf() {
		association.setUserGroup(null);
		userNotificationService.onUserGroupApplication(association);
	}

	@Test
	public void onUserGroupApplication_WithAutogeneratedGroup_sendNothing() {
		group.setAutoGenerated(true);

		userNotificationService.onUserGroupApplication(association);

		verify(notificationService, never()).sendNotification(any(EmailTemplate.class));
		verify(notificationService, never()).sendNotification(any(NotificationTemplate.class));
	}

	@Test
	public void onUserGroupApplication_WithOpenMembershipGroup_sendPrimaryNotificationToOwner() {
		group.setOpenMembership(true);

		userNotificationService.onUserGroupApplication(association);

		verify(notificationTemplateFactory).buildUserGroupApplicationNotificationTemplate(
			eq(group.getOwner().getId()),
			refEq(group),
			eq(association.getUser()),
			eq(false));
		verify(notificationService).sendNotification(any(NotificationTemplate.class));
	}

	@Test
	public void onUserGroupApplication_WithPrivateMembershipGroupNotApproved_sendNothing() {
		group.setOpenMembership(false);
		association.setApprovalStatus(ApprovalStatus.DECLINED);

		userNotificationService.onUserGroupApplication(association);

		verify(notificationTemplateFactory, never())
				.buildUserGroupPrivateApplicationNotificationTemplate(any(Long.class), any(UserGroup.class), any(User.class));
		verify(notificationTemplateFactory, never()).buildUserGroupApplicationNotificationTemplate(
			any(Long.class),
			any(UserGroup.class),
			any(User.class),
			eq(false));
		verify(notificationService, never()).sendNotification(any(NotificationTemplate.class));
	}

	@Test
	public void onUserGroupApplication_WithPrivateMembershipGroupAndApproval_sendPrivateNotificationToOwner() {
		group.setOpenMembership(false);
		association.setApprovalStatus(ApprovalStatus.APPROVED);

		userNotificationService.onUserGroupApplication(association);

		verify(notificationTemplateFactory)
				.buildUserGroupPrivateApplicationNotificationTemplate(eq(group.getOwner().getId()), refEq(group), eq(association.getUser()));
		verify(notificationService).sendNotification(any(NotificationTemplate.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void onWorkAcceptedEvent_nullEvent_throwException() {
		userNotificationService.onWorkAcceptedEvent(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void onWorkAcceptedEvent_nullResourceUserId_throwException() {
		when(workAcceptedEvent.getResourceUserId()).thenReturn(null);
		userNotificationService.onWorkAcceptedEvent(workAcceptedEvent);
	}

	@Test(expected = IllegalArgumentException.class)
	public void onWorkAcceptedEvent_nullWorkId_throwException() {
		when(workAcceptedEvent.getWorkId()).thenReturn(null);
		userNotificationService.onWorkAcceptedEvent(workAcceptedEvent);
	}

	@Test
	public void onWorkAcceptedEvent_noActiveWorker_earlyReturn() {
		when(workService.findActiveWorkerId(work.getId())).thenReturn(null);

		userNotificationService.onWorkAcceptedEvent(workAcceptedEvent);

		verify(userService, never()).findUserById(WORKER_ID);
	}

	@Test
	public void onWorkAcceptedEvent() {
		userNotificationService.onWorkAcceptedEvent(workAcceptedEvent);
	}

	@Test
	public void onWorkAcceptedEvent_mboAssignment_doNotSendEmail() throws Exception {
		when(profileService.findMboProfile(anyLong())).thenReturn(mboProfile);
		when(workOptionsService.hasOption(any(AbstractWork.class), eq(WorkOption.MBO_ENABLED), eq("true"))).thenReturn(true);

		userNotificationService.onWorkAcceptedEvent(workAcceptedEvent);

		verify(notificationDispatcher, never()).dispatchEmail(any(EmailTemplate.class));
	}

	@Test
	public void onWorkAcceptedEvent_nonMboAssignment_sendsEmail() throws Exception {
		WorkAcceptedDetailsNotificationTemplate workAcceptedTemplate = mock(WorkAcceptedDetailsNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkAcceptedDetailsNotificationTemplate(anyLong(), any(Work.class), any(User.class)))

			.thenReturn(workAcceptedTemplate);
		when(workAcceptedTemplate.getEmailTemplate()).thenReturn(mock(NotificationEmailTemplate.class));

		userNotificationService.onWorkAcceptedEvent(workAcceptedEvent);

		verify(notificationDispatcher).dispatchEmail(any(EmailTemplate.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void onWorkUnassigned_nullWorkResource_IllegalArgumentExceptionThrown() {
		userNotificationService.onWorkUnassigned(null, NOTE_FOR_WORKER);
	}

	@Test(expected = IllegalArgumentException.class)
	public void onWorkUnassigned_nullWorker_IllegalArgumentExceptionThrown() {
		when(workResource.getUser()).thenReturn(null);

		userNotificationService.onWorkUnassigned(workResource, NOTE_FOR_WORKER);
	}

	@Test
	public void onWorkUnassigned_workResourceAndUnassignNote_UnassignNotificationsCreatedAndSent() {
		userNotificationService.onWorkUnassigned(workResource, NOTE_FOR_WORKER);

		verify(notificationTemplateFactory).buildWorkUnassignedNotificationTemplate(anyLong(), any(Work.class), any(WorkResource.class), eq(NOTE_FOR_WORKER));
		verify(notificationService).sendNotification(any(NotificationTemplate.class));
	}

	@Test
	public void sendEmailToAccounting_doesNothing() {
		userNotificationService.sendEmailToAccounting("", Sets.<String>newHashSet(), "");
		verify(emailService, never()).sendEmail(any(EMailDTO.class));
	}

	@Test
	public void sendEmailToAccounting_success() {
		userNotificationService.sendEmailToAccounting("Some title", Sets.newHashSet("Difference found"), "someEmail@someDomain.com");
		verify(emailService).sendEmail(any(EMailDTO.class));
	}

	@Test
	public void setViewedAtNotificationAsync_withUnreadNotificationsDTO_callMethodWithExplicitArgs() {
		userNotificationService.setViewedAtNotificationAsync(OWNER_ID, unreadNotificationsDTO);

		verify(userNotificationService).setViewedAtNotificationAsync(OWNER_ID, unreadNotificationsDTO.getStartUuid(), unreadNotificationsDTO.getEndUuid());
	}

	@Test
	public void setViewedAtNotificationAsync_withNullStartId_eventNotSent() {
		userNotificationService.setViewedAtNotificationAsync(OWNER_ID, null, END_UUID);

		verify(eventRouter, never()).sendEvent(any(MarkUserNotificationsAsReadEvent.class));
	}

	@Test
	public void setViewedAtNotificationAsync_withZeroStartId_eventNotSent() {
		userNotificationService.setViewedAtNotificationAsync(OWNER_ID, "", END_UUID);

		verify(eventRouter, never()).sendEvent(any(MarkUserNotificationsAsReadEvent.class));
	}

	@Test
	public void setViewedAtNotificationAsync_withNullEndId_eventNotSent() {
		userNotificationService.setViewedAtNotificationAsync(OWNER_ID, START_UUID, null);

		verify(eventRouter, never()).sendEvent(any(MarkUserNotificationsAsReadEvent.class));
	}

	@Test
	public void setViewedAtNotificationAsync_withZeroEndId_eventNotSent() {
		userNotificationService.setViewedAtNotificationAsync(OWNER_ID, START_UUID, "");

		verify(eventRouter, never()).sendEvent(any(MarkUserNotificationsAsReadEvent.class));
	}

	@Test
	public void setViewedAtNotifications_setNotificationsToViewed() {
		userNotificationService.setViewedAtNotification(OWNER_ID, unreadNotificationsDTO);
		verify(notificationClient).setViewed((String) anyObject(), (String) anyObject(), (String) anyObject(), eq(context));
	}

	@Test
	public void setViewedAtNotifications_clearUnreadNotificationInfo() {
		userNotificationService.setViewedAtNotification(OWNER_ID, unreadNotificationsDTO);

		verify(userNotificationCache).clearUnreadNotificationInfo(OWNER_ID);
	}

	@Test
	public void setViewedAtNotifications_clearNotifications() {
		userNotificationService.setViewedAtNotification(OWNER_ID, unreadNotificationsDTO);

		verify(userNotificationCache).clearNotifications(OWNER_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void onWorkCompleted_noActiveWorkResource_throwException() {
		when(workService.findActiveWorkResource(work.getId())).thenReturn(null);

		userNotificationService.onWorkCompleted(work.getId(), false);
	}

	@Test
	public void onWorkCompleted_workBundle_doNotSendNotification() {
		when(work.isWorkBundle()).thenReturn(true);

		userNotificationService.onWorkCompleted(work.getId(), false);

		verify(notificationService, never()).sendNotification(any(NotificationTemplate.class));
	}

	@Test
	public void onWorkCompleted_notWorkBundle_sendNotification() {
		when(work.isWorkBundle()).thenReturn(false);

		userNotificationService.onWorkCompleted(work.getId(), false);

		verify(notificationService).sendNotification(any(NotificationTemplate.class));
	}

	@Test
	public void onWorkCompleted_notCompletedOnBehalf_sendNotification_toBuyer() {
		WorkCompleteNotificationTemplate buyerNotification = mock(WorkCompleteNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkCompleteNotificationTemplate(
			worker.getId(), work.getBuyer().getId(), work, payment
		)).thenReturn(buyerNotification);

		userNotificationService.onWorkCompleted(work.getId(), false);

		verify(notificationService).sendNotification(buyerNotification);
	}

	@Test
	public void onWorkCompleted_notCompletedOnBehalf_sendNotification_toFollower() {
		WorkCompleteNotificationTemplate followerNotification = mock(WorkCompleteNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkCompleteNotificationTemplate(
			worker.getId(), follower.getId(), work, payment
		)).thenReturn(followerNotification);
		workFollows.add(workFollow);

		userNotificationService.onWorkCompleted(work.getId(), false);

		verify(notificationService).sendNotification(followerNotification);
	}

	@Test
	public void onWorkCompleted_notCompletedOnBehalf_doNotSendNotification_toFollower() {
		WorkCompleteNotificationTemplate followerNotification = mock(WorkCompleteNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkCompleteNotificationTemplate(
			worker.getId(), follower.getId(), work, payment
		)).thenReturn(followerNotification);

		userNotificationService.onWorkCompleted(work.getId(), false);

		verify(notificationService, never()).sendNotification(followerNotification);
	}

	@Test
	public void onWorkCompleted_completedOnBehalf_sendNotification_toWorker() {
		WorkCompletedByBuyerNotificationTemplate workerNotification = mock(WorkCompletedByBuyerNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkCompletedByBuyerNotificationTemplate(
			buyer.getId(), worker.getId(), work, false
		)).thenReturn(workerNotification);

		userNotificationService.onWorkCompleted(work.getId(), true);

		verify(notificationService).sendNotification(workerNotification);
	}

	@Test
	public void onWorkCompleted_completedOnBehalf_sendNotification_toDispatcher() {
		WorkCompletedByBuyerNotificationTemplate dispatcherNotification = mock(WorkCompletedByBuyerNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkCompletedByBuyerNotificationTemplate(
			buyer.getId(), dispatcher.getId(), work, false
		)).thenReturn(dispatcherNotification);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkCompleted(work.getId(), true);

		verify(dispatcherNotification).setOnBehalfOfId(worker.getId());
		verify(notificationService).sendNotification(dispatcherNotification);
	}

	@Test
	public void onWorkCompleted_completedOnBehalf_doNotSendNotification_toDispatcher() {
		WorkCompletedByBuyerNotificationTemplate dispatcherNotification = mock(WorkCompletedByBuyerNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkCompletedByBuyerNotificationTemplate(
			buyer.getId(), dispatcher.getId(), work, false
		)).thenReturn(dispatcherNotification);

		userNotificationService.onWorkCompleted(work.getId(), true);

		verify(notificationService, never()).sendNotification(dispatcherNotification);
	}

	@Test
	public void onWorkCompleted_autoCloseNotEnabled_doNotSendAutoCloseEvent() {
		userNotificationService.onWorkCompleted(work.getId(), true);

		verify(eventRouter, never()).sendEvent(any(WorkAutoCloseScheduledEvent.class));
	}

	@Test
	public void onWorkCompleted_autoCloseEnabled_sendAutoCloseEvent() {
		int hours = 1;
		when(manageMyWorkMarket.getAutocloseEnabledFlag()).thenReturn(true);
		when(manageMyWorkMarket.getAutocloseDelayInHours()).thenReturn(hours);
		WorkMilestones workMilestones = mock(WorkMilestones.class);
		Calendar workCompletedOn = mock(Calendar.class);
		when(workCompletedOn.clone()).thenReturn(workCompletedOn);
		when(workMilestones.getCompleteOn()).thenReturn(workCompletedOn);
		when(workMilestonesDAO.findWorkMilestonesByWorkId(work.getId())).thenReturn(workMilestones);
		WorkAutoCloseScheduledEvent event = mock(WorkAutoCloseScheduledEvent.class);
		when(eventFactory.buildWorkAutoCloseScheduledEvent(work, workCompletedOn)).thenReturn(event);

		userNotificationService.onWorkCompleted(work.getId(), true);

		verify(eventRouter).sendEvent(event);
	}

	@Test
	public void onUserGroupInvitations_notificationsSendDirectlyInBatch() {
		List<Long> invitedUserIds = Arrays.asList(1l, 2l);

		when(notificationTemplateFactory.buildUserGroupInvitationNotificationTemplate(
			anyLong(), anyLong(), any(UserGroup.class), anyBoolean())
		).thenReturn(mock(UserGroupInvitationNotificationTemplate.class));

		userNotificationService.onUserGroupInvitations(1l, 1l, invitedUserIds);

		verify(notificationService).sendNotifications(anyListOf(NotificationTemplate.class));
	}

	@Test
	public void makeWorkNegotiationPagination_setPropertiesForPendingNegotiationsFetch() {
		Mockito.reset(userNotificationService);

		WorkNegotiationPagination pagination = userNotificationService.makeWorkNegotiationPagination();

		assertEquals(Integer.MAX_VALUE, pagination.getResultsLimit().intValue());
		assertEquals(
			ApprovalStatus.PENDING.name(),
			pagination.getFilter(WorkNegotiationPagination.FILTER_KEYS.APPROVAL_STATUS)
		);
	}

	@Test
	public void onWorkBundleNegotiationApproved_getBundleData() {
		userNotificationService.onWorkBundleNegotiationApproved(workNegotiation);

		verify(workBundleService).getBundleData(worker.getId(), work.getId());
	}

	@Test
	public void onWorkBundleNegotiationApproved_sendBundleNegotiationApprovedNotification_toWorker() {
		userNotificationService.onWorkBundleNegotiationApproved(workNegotiation);

		verify(notificationService).sendNotification(bundleApprovedNotification);
	}

	@Test
	public void onWorkBundleNegotiationApproved_doNotSendBundleNegotiationApprovedNotification_toDispatcher() {
		userNotificationService.onWorkBundleNegotiationApproved(workNegotiation);

		verify(notificationService, never()).sendNotification(bundleApprovedNotificationForDispatcher);
	}

	@Test
	public void onWorkBundleNegotiationApproved_sendBundleNegotiationApprovedNotification_toDispatcher() {
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkBundleNegotiationApproved(workNegotiation);

		verify(notificationService).sendNotification(bundleApprovedNotificationForDispatcher);
	}

	@Test
	public void onWorkBundleNegotiationApproved_ifAppliedWork_fetchOtherNegotiations() {
		userNotificationService.onWorkBundleNegotiationApproved(workNegotiation);

		verify(workNegotiationService).findByWork(work.getId(), workNegotiationPagination);
	}

	@Test
	public void onWorkBundleNegotiationApproved_ifAppliedWork_notifyThatWorkIsUnavailable_toWorker() {
		userNotificationService.onWorkBundleNegotiationApproved(workNegotiation);

		verify(notificationService).sendNotification(workNotAvailableNotification);
	}

	@Test
	public void onWorkBundleNegotiationApproved_ifAppliedWork_doNotNotifyThatWorkIsUnavailable_toDispatcher() {
		userNotificationService.onWorkBundleNegotiationApproved(workNegotiation);

		verify(notificationService, never()).sendNotification(workNotAvailableNotificationDispatcher);
	}

	@Test
	public void onWorkBundleNegotiationApproved_ifAppliedWork_notifyThatWorkIsUnavailable_toDispatcher() {
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker2.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkBundleNegotiationApproved(workNegotiation);

		verify(notificationService).sendNotification(workNotAvailableNotificationDispatcher);
	}

	@Test
	public void onWorkBundleNegotiationApproved_ifAppliedWork_notifyThatWorkIsUnavailable_unlessTheNegotiationIsAcceptedWorkers() {
		when(declinedWorkNegotiation.getRequestedBy()).thenReturn(worker);

		userNotificationService.onWorkBundleNegotiationApproved(workNegotiation);

		verify(notificationService, never()).sendNotification(workNotAvailableNotification);
		verify(notificationService, never()).sendNotification(workNotAvailableNotificationDispatcher);
	}

	@Test
	public void onWorkNegotiationApproved_workNegotiation_sendNotification_toWorker() {
		when(notificationTemplateFactory.buildWorkNegotiationApprovedNotificationTemplate(worker.getId(), work, workNegotiation))
			.thenReturn(workNegotiationApprovedNotificationTemplate);

		userNotificationService.onWorkNegotiationApproved(workNegotiation);

		verify(notificationService).sendNotification(workNegotiationApprovedNotificationTemplate);
	}

	@Test
	public void onWorkNegotiationApproved_workNegotiation_sendNotification_toDispatcher() {
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkNegotiationApproved(workNegotiation);

		verify(notificationService).sendNotification(workNegotiationApprovedNotificationTemplate);
	}

	@Test
	public void onWorkNegotiationApproved_workNegotiation_doNotSendNotification_toDispatcher() {
		userNotificationService.onWorkNegotiationApproved(workNegotiation);

		verify(notificationService, never()).sendNotification(workNegotiationApprovedNotificationTemplate);
	}

	@Test
	public void onWorkNegotiationApproved_workNegotiation_ifAppliedWork_andRejectedNegotiationIsNotWorkNegotiation_doNotSendNotification_toWorker() {
		when(workNegotiationPagination.getResults()).thenReturn(Lists.<AbstractWorkNegotiation>newArrayList(workBudgetNegotiation));

		userNotificationService.onWorkNegotiationApproved(workNegotiation);

		verify(notificationService, never()).sendNotification(workNotAvailableNotification);
	}

	@Test
	public void onWorkNegotiationApproved_workNegotiation_ifAppliedWork_andRejectedNegotiationIsNotWorkNegotiation_doNotSendNotification_toDispatcher() {
		when(workNegotiationPagination.getResults()).thenReturn(Lists.<AbstractWorkNegotiation>newArrayList(workBudgetNegotiation));
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker2.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkNegotiationApproved(workNegotiation);

		verify(notificationService, never()).sendNotification(workNotAvailableNotificationDispatcher);
	}

	@Test
	public void onWorkNegotiationApproved_workNegotiation_ifAppliedWork_andDeclinedNegotiationIsFromAcceptedWorker_doNotSendNotification_toWorker() {
		when(declinedWorkNegotiation.getRequestedBy()).thenReturn(worker);

		userNotificationService.onWorkNegotiationApproved(workNegotiation);

		verify(notificationService, never()).sendNotification(workNotAvailableNotification);
	}

	@Test
	public void onWorkNegotiationApproved_workNegotiation_ifAppliedWork_andDeclinedNegotiationIsFromAcceptedWorker_doNotSendNotification_toDispatcher() {
		when(declinedWorkNegotiation.getRequestedBy()).thenReturn(worker);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkNegotiationApproved(workNegotiation);

		verify(notificationService, never()).sendNotification(workNotAvailableNotificationDispatcher);
	}

	@Test
	public void onWorkNegotiationApproved_workNegotiation_ifAppliedWork_sendNotification_toWorker() {
		userNotificationService.onWorkNegotiationApproved(workNegotiation);

		verify(notificationService).sendNotification(workNotAvailableNotification);
	}

	@Test
	public void onWorkNegotiationApproved_workNegotiation_ifAppliedWork_sendNotification_toDispatcher() {
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker2.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkNegotiationApproved(workNegotiation);

		verify(notificationService).sendNotification(workNotAvailableNotificationDispatcher);
	}

	@Test
	public void onWorkNegotiationApproved_workNegotiation_ifAppliedWork_doNotSendNotification_toDispatcher() {
		userNotificationService.onWorkNegotiationApproved(workNegotiation);

		verify(notificationService, never()).sendNotification(workNotAvailableNotificationDispatcher);
	}

	@Test
	public void onWorkNegotiationApproved_workNegotiation_ifNotAppliedWork_doNotSendNotification_toWorker() {
		when(manageMyWorkMarket.getAssignToFirstResource()).thenReturn(true);

		userNotificationService.onWorkNegotiationApproved(workNegotiation);

		verify(notificationService, never()).sendNotification(workNotAvailableNotification);
	}

	@Test
	public void onWorkNegotiationApproved_workNegotiation_ifNotAppliedWork_doNotSendNotification_toDispatcher() {
		when(manageMyWorkMarket.getAssignToFirstResource()).thenReturn(true);

		userNotificationService.onWorkNegotiationApproved(workNegotiation);

		verify(notificationService, never()).sendNotification(workNotAvailableNotificationDispatcher);
	}

	@Test
	public void onWorkNegotiationApproved_workBudgetNegotiation_initiatedByWorker_sendNotification_toWorker() {
		when(workBudgetNegotiation.isInitiatedByResource()).thenReturn(true);
		when(notificationTemplateFactory.buildWorkBudgetNegotiationApprovedNotificationTemplate(worker.getId(), work, workBudgetNegotiation))
			.thenReturn(workBudgetNegotiationApprovedNotificationTemplate);

		userNotificationService.onWorkNegotiationApproved(workBudgetNegotiation);

		verify(notificationService).sendNotification(workBudgetNegotiationApprovedNotificationTemplate);
	}

	@Test
	public void onWorkNegotiationApproved_workBudgetNegotiation_initiatedByWorker_doNotSendNotification_toDispatcher() {
		when(workBudgetNegotiation.isInitiatedByResource()).thenReturn(true);
		when(notificationTemplateFactory.buildWorkBudgetNegotiationApprovedNotificationTemplate(dispatcher.getId(), work, workBudgetNegotiation))
			.thenReturn(workBudgetNegotiationApprovedNotificationTemplateForDispatcher);

		userNotificationService.onWorkNegotiationApproved(workBudgetNegotiation);

		verify(notificationService, never()).sendNotification(workBudgetNegotiationApprovedNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkNegotiationApproved_workBudgetNegotiation_initiatedByWorker_sendNotification_toDispatcher() {
		when(workBudgetNegotiation.isInitiatedByResource()).thenReturn(true);
		when(notificationTemplateFactory.buildWorkBudgetNegotiationApprovedNotificationTemplate(dispatcher.getId(), work, workBudgetNegotiation))
			.thenReturn(workBudgetNegotiationApprovedNotificationTemplateForDispatcher);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkNegotiationApproved(workBudgetNegotiation);

		verify(notificationService).sendNotification(workBudgetNegotiationApprovedNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkNegotiationApproved_workBudgetNegotiation_notInitiatedByWorker_sendNotification_toWorker() {
		when(notificationTemplateFactory.buildWorkBudgetNegotiationAddedNotificationTemplate(worker.getId(), work, workBudgetNegotiation))
			.thenReturn(workBudgetNegotiationAddedNotificationTemplate);

		userNotificationService.onWorkNegotiationApproved(workBudgetNegotiation);

		verify(notificationService).sendNotification(workBudgetNegotiationAddedNotificationTemplate);
	}

	@Test
	public void onWorkNegotiationApproved_workBudgetNegotiation_notInitiatedByWorker_doNotSendNotification_toDispatcher() {
		when(notificationTemplateFactory.buildWorkBudgetNegotiationAddedNotificationTemplate(dispatcher.getId(), work, workBudgetNegotiation))
			.thenReturn(workBudgetNegotiationAddedNotificationTemplateForDispatcher);

		userNotificationService.onWorkNegotiationApproved(workBudgetNegotiation);

		verify(notificationService, never()).sendNotification(workBudgetNegotiationAddedNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkNegotiationApproved_workBudgetNegotiation_notInitiatedByWorker_sendNotification_toDispatcher() {
		when(notificationTemplateFactory.buildWorkBudgetNegotiationAddedNotificationTemplate(dispatcher.getId(), work, workBudgetNegotiation))
			.thenReturn(workBudgetNegotiationAddedNotificationTemplateForDispatcher);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkNegotiationApproved(workBudgetNegotiation);

		verify(notificationService).sendNotification(workBudgetNegotiationAddedNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkNegotiationApproved_workExpenseNegotiation_initiatedByWorker_sendNotification_toWorker() {
		when(workExpenseNegotiation.isInitiatedByResource()).thenReturn(true);
		when(notificationTemplateFactory.buildWorkExpenseNegotiationApprovedNotificationTemplate(worker.getId(), work, workExpenseNegotiation))
			.thenReturn(workExpenseNegotiationApprovedNotificationTemplate);

		userNotificationService.onWorkNegotiationApproved(workExpenseNegotiation);

		verify(notificationService).sendNotification(workExpenseNegotiationApprovedNotificationTemplate);
	}

	@Test
	public void onWorkNegotiationApproved_workExpenseNegotiation_initiatedByWorker_doNotSendNotification_toDispatcher() {
		when(workExpenseNegotiation.isInitiatedByResource()).thenReturn(true);
		when(notificationTemplateFactory.buildWorkExpenseNegotiationApprovedNotificationTemplate(dispatcher.getId(), work, workExpenseNegotiation))
			.thenReturn(workExpenseNegotiationApprovedNotificationTemplateForDispatcher);

		userNotificationService.onWorkNegotiationApproved(workExpenseNegotiation);

		verify(notificationService, never()).sendNotification(workExpenseNegotiationApprovedNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkNegotiationApproved_workExpenseNegotiation_initiatedByWorker_sendNotification_toDispatcher() {
		when(workExpenseNegotiation.isInitiatedByResource()).thenReturn(true);
		when(notificationTemplateFactory.buildWorkExpenseNegotiationApprovedNotificationTemplate(dispatcher.getId(), work, workExpenseNegotiation))
			.thenReturn(workExpenseNegotiationApprovedNotificationTemplateForDispatcher);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkNegotiationApproved(workExpenseNegotiation);

		verify(notificationService).sendNotification(workExpenseNegotiationApprovedNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkNegotiationApproved_workExpenseNegotiation_notInitiatedByWorker_sendNotification_toWorker() {
		when(notificationTemplateFactory.buildWorkExpenseNegotiationAddedNotificationTemplate(worker.getId(), work, workExpenseNegotiation))
			.thenReturn(workExpenseNegotiationAddedNotificationTemplate);

		userNotificationService.onWorkNegotiationApproved(workExpenseNegotiation);

		verify(notificationService).sendNotification(workExpenseNegotiationAddedNotificationTemplate);
	}

	@Test
	public void onWorkNegotiationApproved_workExpenseNegotiation_notInitiatedByWorker_doNotSendNotification_toDispatcher() {
		when(notificationTemplateFactory.buildWorkExpenseNegotiationAddedNotificationTemplate(dispatcher.getId(), work, workExpenseNegotiation))
			.thenReturn(workExpenseNegotiationAddedNotificationTemplateForDispatcher);

		userNotificationService.onWorkNegotiationApproved(workExpenseNegotiation);

		verify(notificationService, never()).sendNotification(workExpenseNegotiationAddedNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkNegotiationApproved_workExpenseNegotiation_notInitiatedByWorker_sendNotification_toDispatcher() {
		when(notificationTemplateFactory.buildWorkExpenseNegotiationAddedNotificationTemplate(dispatcher.getId(), work, workExpenseNegotiation))
			.thenReturn(workExpenseNegotiationAddedNotificationTemplateForDispatcher);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkNegotiationApproved(workExpenseNegotiation);

		verify(notificationService).sendNotification(workExpenseNegotiationAddedNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkNegotiationApproved_workBonusNegotiation_initiatedByWorker_sendNotification_toWorker() {
		when(workBonusNegotiation.isInitiatedByResource()).thenReturn(true);
		when(notificationTemplateFactory.buildWorkBonusNegotiationApprovedNotificationTemplate(worker.getId(), work, workBonusNegotiation))
			.thenReturn(workBonusNegotiationApprovedNotificationTemplate);

		userNotificationService.onWorkNegotiationApproved(workBonusNegotiation);

		verify(notificationService).sendNotification(workBonusNegotiationApprovedNotificationTemplate);
	}

	@Test
	public void onWorkNegotiationApproved_workBonusNegotiation_initiatedByWorker_doNotSendNotification_toDispatcher() {
		when(workBonusNegotiation.isInitiatedByResource()).thenReturn(true);
		when(notificationTemplateFactory.buildWorkBonusNegotiationApprovedNotificationTemplate(dispatcher.getId(), work, workBonusNegotiation))
			.thenReturn(workBonusNegotiationApprovedNotificationTemplateForDispatcher);

		userNotificationService.onWorkNegotiationApproved(workBonusNegotiation);

		verify(notificationService, never()).sendNotification(workBonusNegotiationApprovedNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkNegotiationApproved_workBonusNegotiation_initiatedByWorker_sendNotification_toDispatcher() {
		when(workBonusNegotiation.isInitiatedByResource()).thenReturn(true);
		when(notificationTemplateFactory.buildWorkBonusNegotiationApprovedNotificationTemplate(dispatcher.getId(), work, workBonusNegotiation))
			.thenReturn(workBonusNegotiationApprovedNotificationTemplateForDispatcher);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkNegotiationApproved(workBonusNegotiation);

		verify(notificationService).sendNotification(workBonusNegotiationApprovedNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkNegotiationApproved_workBonusNegotiation_notInitiatedByWorker_sendNotification_toWorker() {
		when(notificationTemplateFactory.buildWorkBonusNegotiationAddedNotificationTemplate(worker.getId(), work, workBonusNegotiation))
			.thenReturn(workBonusNegotiationAddedNotificationTemplate);

		userNotificationService.onWorkNegotiationApproved(workBonusNegotiation);

		verify(notificationService).sendNotification(workBonusNegotiationAddedNotificationTemplate);
	}

	@Test
	public void onWorkNegotiationApproved_workBonusNegotiation_notInitiatedByWorker_doNotSendNotification_toDispatcher() {
		when(notificationTemplateFactory.buildWorkBonusNegotiationAddedNotificationTemplate(dispatcher.getId(), work, workBonusNegotiation))
			.thenReturn(workBonusNegotiationAddedNotificationTemplateForDispatcher);

		userNotificationService.onWorkNegotiationApproved(workBonusNegotiation);

		verify(notificationService, never()).sendNotification(workBonusNegotiationAddedNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkNegotiationApproved_workBonusNegotiation_notInitiatedByWorker_sendNotification_toDispatcher() {
		when(notificationTemplateFactory.buildWorkBonusNegotiationAddedNotificationTemplate(dispatcher.getId(), work, workBonusNegotiation))
			.thenReturn(workBonusNegotiationAddedNotificationTemplateForDispatcher);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkNegotiationApproved(workBonusNegotiation);

		verify(notificationService).sendNotification(workBonusNegotiationAddedNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkNegotiationApproved_workRescheduleNegotiation_sendNotification_toWorker() {
		when(notificationTemplateFactory.buildWorkRescheduleNegotiationApprovedNotificationTemplate(worker.getId(), work, workRescheduleNegotiation))
			.thenReturn(workRescheduleNegotiationApprovedNotificationTemplate);

		userNotificationService.onWorkNegotiationApproved(workRescheduleNegotiation);

		verify(notificationService).sendNotification(workRescheduleNegotiationApprovedNotificationTemplate);
	}

	@Test
	public void onWorkNegotiationApproved_workRescheduleNegotiation_doNotSendNotification_toDispatcher() {
		when(notificationTemplateFactory.buildWorkRescheduleNegotiationApprovedNotificationTemplate(dispatcher.getId(), work, workRescheduleNegotiation))
			.thenReturn(workRescheduleNegotiationApprovedNotificationTemplateForDispatcher);

		userNotificationService.onWorkNegotiationApproved(workRescheduleNegotiation);

		verify(notificationService, never()).sendNotification(workRescheduleNegotiationApprovedNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkNegotiationApproved_workRescheduleNegotiation_sendNotification_toDispatcher() {
		when(notificationTemplateFactory.buildWorkRescheduleNegotiationApprovedNotificationTemplate(dispatcher.getId(), work, workRescheduleNegotiation))
			.thenReturn(workRescheduleNegotiationApprovedNotificationTemplateForDispatcher);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkNegotiationApproved(workRescheduleNegotiation);

		verify(notificationService).sendNotification(workRescheduleNegotiationApprovedNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkNegotiationApproved_workRescheduleNegotiation_onBehalfOf_sendNotification_toWorker() {
		when(notificationTemplateFactory.buildWorkRescheduleNegotiationApprovedOnBehalfOfNotificationTemplate(worker.getId(), work, workRescheduleNegotiation))
			.thenReturn(workRescheduleNegotiationApprovedOnBehalfOfNotificationTemplate);
		when(workRescheduleNegotiation.getModifierId()).thenReturn(WORKER_ID);

		userNotificationService.onWorkNegotiationApproved(workRescheduleNegotiation);

		verify(notificationService).sendNotification(workRescheduleNegotiationApprovedOnBehalfOfNotificationTemplate);
	}

	@Test
	public void onWorkNegotiationApproved_workRescheduleNegotiation_onBehalfOf_doNotSendNotification_toDispatcher() {
		when(notificationTemplateFactory.buildWorkRescheduleNegotiationApprovedOnBehalfOfNotificationTemplate(dispatcher .getId(), work, workRescheduleNegotiation))
			.thenReturn(workRescheduleNegotiationApprovedOnBehalfOfNotificationTemplateForDispatcher);

		userNotificationService.onWorkNegotiationApproved(workRescheduleNegotiation);

		verify(notificationService, never()).sendNotification(workRescheduleNegotiationApprovedOnBehalfOfNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkNegotiationApproved_workRescheduleNegotiation_onBehalfOf_sendNotification_toDispatcher() {
		when(notificationTemplateFactory.buildWorkRescheduleNegotiationApprovedOnBehalfOfNotificationTemplate(dispatcher.getId(), work, workRescheduleNegotiation))
			.thenReturn(workRescheduleNegotiationApprovedOnBehalfOfNotificationTemplateForDispatcher);
		when(workRescheduleNegotiation.getModifierId()).thenReturn(WORKER_ID);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkNegotiationApproved(workRescheduleNegotiation);

		verify(notificationService).sendNotification(workRescheduleNegotiationApprovedOnBehalfOfNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkNegotiationDeclined_workNegotiation_sendNotification_toWorker() {
		when(notificationTemplateFactory.buildAbstractWorkNegotiationDeclinedNotificationTemplate(worker.getId(), work, workNegotiation))
			.thenReturn(workNegotiationDeclinedNotificationTemplate);

		userNotificationService.onWorkNegotiationDeclined(workNegotiation);

		verify(notificationService).sendNotification(workNegotiationDeclinedNotificationTemplate);
	}

	@Test
	public void onWorkNegotiationDeclined_workNegotiation_doNotSendNotification_toDispatcher() {
		when(notificationTemplateFactory.buildAbstractWorkNegotiationDeclinedNotificationTemplate(dispatcher.getId(), work, workNegotiation))
			.thenReturn(workNegotiationDeclinedNotificationTemplateForDispatcher);

		userNotificationService.onWorkNegotiationDeclined(workNegotiation);

		verify(notificationService, never()).sendNotification(workNegotiationDeclinedNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkNegotiationDeclined_workNegotiation_sendNotification_toDispatcher() {
		when(notificationTemplateFactory.buildAbstractWorkNegotiationDeclinedNotificationTemplate(dispatcher.getId(), work, workNegotiation))
			.thenReturn(workNegotiationDeclinedNotificationTemplateForDispatcher);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkNegotiationDeclined(workNegotiation);

		verify(workNegotiationDeclinedNotificationTemplateForDispatcher).setOnBehalfOfId(worker.getId());
		verify(notificationService).sendNotification(workNegotiationDeclinedNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkUpdated_workDoesNotExist_earlyReturn() {
		when(workService.findWork(work.getId())).thenReturn(null);

		userNotificationService.onWorkUpdated(work.getId(), propertyChanges);

		verify(work, never()).isSent();
	}

	@Test
	public void onWorkUpdated_workIsSent_locationPropertyChange_doNotSendWorkUpdatedEvent() {
		when(work.isSent()).thenReturn(true);
		propertyChanges.put(WorkPropertyChangeType.LOCATION, propertyChangeList);

		userNotificationService.onWorkUpdated(work.getId(), propertyChanges);

		verify(eventRouter, never()).sendEvent(any(Event.class));
	}

	@Test
	public void onWorkUpdated_workIsSent_infoPropertyChange_doNotSendWorkUpdatedEvent() {
		when(work.isSent()).thenReturn(true);
		propertyChanges.put(WorkPropertyChangeType.INFO, propertyChangeList);

		userNotificationService.onWorkUpdated(work.getId(), propertyChanges);

		verify(eventRouter, never()).sendEvent(any(Event.class));
	}

	@Test
	public void onWorkUpdated_workIsSent_contactPropertyChange_doNotSendWorkUpdatedEvent() {
		when(work.isSent()).thenReturn(true);
		propertyChanges.put(WorkPropertyChangeType.CONTACT, propertyChangeList);

		userNotificationService.onWorkUpdated(work.getId(), propertyChanges);

		verify(eventRouter, never()).sendEvent(any(Event.class));
	}

	@Test
	public void onWorkUpdated_workIsSent_otherPropertyChange_doNotSendWorkUpdatedEvent() {
		when(work.isSent()).thenReturn(true);
		propertyChanges.put(WorkPropertyChangeType.OTHER, propertyChangeList);

		userNotificationService.onWorkUpdated(work.getId(), propertyChanges);

		verify(eventRouter, never()).sendEvent(any(Event.class));
	}

	@Test
	public void onWorkUpdated_workIsSent_pricingPropertyChange_sendWorkUpdatedEvent() {
		when(work.isSent()).thenReturn(true);
		propertyChanges.put(WorkPropertyChangeType.PRICING, propertyChangeList);

		userNotificationService.onWorkUpdated(work.getId(), propertyChanges);

		verify(eventRouter).sendEvent(workUpdatedEvent);
	}

	@Test
	public void onWorkUpdated_workIsSent_schedulePropertyChange_sendWorkUpdatedEvent() {
		when(work.isSent()).thenReturn(true);
		propertyChanges.put(WorkPropertyChangeType.SCHEDULE, propertyChangeList);

		userNotificationService.onWorkUpdated(work.getId(), propertyChanges);

		verify(eventRouter).sendEvent(workUpdatedEvent);
	}

	@Test
	public void onWorkUpdated_workIsActive_schedulePropertyChange_setupPreWorkResourceNotifications() {
		when(work.isActive()).thenReturn(true);
		propertyChanges.put(WorkPropertyChangeType.SCHEDULE, propertyChangeList);

		userNotificationService.onWorkUpdated(work.getId(), propertyChanges);

		verify(userNotificationService).setupPreWorkResourceNotifications(work, workResource);
	}

	@Test
	public void onWorkUpdated_workIsNotActive_schedulePropertyChange_doNotSetupPreWorkResourceNotifications() {
		when(work.isActive()).thenReturn(false);
		propertyChanges.put(WorkPropertyChangeType.SCHEDULE, propertyChangeList);

		userNotificationService.onWorkUpdated(work.getId(), propertyChanges);

		verify(userNotificationService, never()).setupPreWorkResourceNotifications(work, workResource);
	}

	@Test
	public void onWorkUpdated_workIsNotActive_notSchedulePropertyChange_doNotSetupPreWorkResourceNotifications() {
		when(work.isActive()).thenReturn(false);

		userNotificationService.onWorkUpdated(work.getId(), propertyChanges);

		verify(userNotificationService, never()).setupPreWorkResourceNotifications(work, workResource);
	}

	@Test(expected = IllegalArgumentException.class)
	public void onWorkNoteAdded_nullNote_throwException() {
		userNotificationService.onWorkNoteAdded(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void onWorkNoteAdded_workDoesNotExist_throwException() {
		when(workService.findWork(note.getWork().getId(), true)).thenReturn(null);

		userNotificationService.onWorkNoteAdded(note);
	}

	@Test
	public void onWorkNoteAdded_noteIsPrivate_activeWorkerContext_earlyReturn() {
		when(note.getIsPrivate()).thenReturn(true);
		workContexts.add(WorkContext.ACTIVE_RESOURCE);

		userNotificationService.onWorkNoteAdded(note);

		verify(userNotificationService, never()).buildSet();
	}

	@Test
	public void onWorkNoteAdded_noteIsPrivate_unrelatedContext_earlyReturn() {
		when(note.getIsPrivate()).thenReturn(true);
		workContexts.add(WorkContext.UNRELATED);

		userNotificationService.onWorkNoteAdded(note);

		verify(userNotificationService, never()).buildSet();
	}

	@Test
	public void onWorkNoteAdded_activeWorkerAddsNote_sendNotification_toBuyer() {
		workContexts.add(WorkContext.ACTIVE_RESOURCE);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			buyer.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_wmEmployeeAddsNote_sendNotification_toBuyer() {
		workContexts.add(WorkContext.UNRELATED);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			buyer.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_noteIsNotFromActiveWorkerOrWM_doNotSendNotification_toBuyer() {
		workContexts.add(WorkContext.OWNER);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			worker.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService, never()).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_buyerAddsNote_noteIsPublic_sendNotification_toWorker() {
		when(note.getCreatorId()).thenReturn(OWNER_ID);
		workContexts.add(WorkContext.OWNER);
		when(note.getIsPrivate()).thenReturn(false);
		when(note.getIsPublic()).thenReturn(true);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			worker.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_buyerEmployeeAddsNote_noteIsPublic_sendNotification_toWorker() {
		when(note.getCreatorId()).thenReturn(OWNER_ID);
		workContexts.add(WorkContext.COMPANY_OWNED);
		when(note.getIsPrivate()).thenReturn(false);
		when(note.getIsPublic()).thenReturn(true);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			worker.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_wmEmployeeAddsNote_noteIsPublic_sendNotification_toWorker() {
		when(note.getCreatorId()).thenReturn(OWNER_ID);
		workContexts.add(WorkContext.UNRELATED);
		when(note.getIsPrivate()).thenReturn(false);
		when(note.getIsPublic()).thenReturn(true);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			worker.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_buyerAddsNote_noteIsPrivileged_sendNotification_toWorker() {
		when(note.getCreatorId()).thenReturn(OWNER_ID);
		workContexts.add(WorkContext.OWNER);
		when(note.getIsPrivate()).thenReturn(false);
		when(note.getIsPrivileged()).thenReturn(true);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			worker.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_buyerEmployeeAddsNote_noteIsPrivileged_sendNotification_toWorker() {
		when(note.getCreatorId()).thenReturn(OWNER_ID);
		workContexts.add(WorkContext.COMPANY_OWNED);
		when(note.getIsPrivate()).thenReturn(false);
		when(note.getIsPrivileged()).thenReturn(true);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			worker.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_wmEmployeeAddsNote_noteIsPrivileged_sendNotification_toWorker() {
		when(note.getCreatorId()).thenReturn(OWNER_ID);
		workContexts.add(WorkContext.UNRELATED);
		when(note.getIsPrivate()).thenReturn(false);
		when(note.getIsPrivileged()).thenReturn(true);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			worker.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_noteIsPrivate_doNotSendNotification_toWorker() {
		when(note.getIsPrivate()).thenReturn(true);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			worker.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService, never()).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_noteIsNotFromBuyerOrBuyerCompanyOrWM_doNotSendNotification_toWorker() {
		workContexts.add(WorkContext.DISPATCHER);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			worker.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService, never()).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_noActiveWorker_doNotSendNotification_toWorker() {
		workContexts.add(WorkContext.OWNER);
		when(workService.findActiveWorkerId(work.getId())).thenReturn(null);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			worker.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService, never()).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_noReplyToId_doNotSendNotification_toWorker() {
		workContexts.add(WorkContext.OWNER);
		when(note.getReplyToId()).thenReturn(null);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			worker.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService, never()).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_workerIdIsNotReplyToId_doNotSendNotification_toWorker() {
		workContexts.add(WorkContext.OWNER);
		when(note.getReplyToId()).thenReturn(WORKER2_ID);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			worker.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService, never()).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_buyerAddsNote_noteIsPublic_sendNotification_toDispatcher() {
		when(note.getCreatorId()).thenReturn(OWNER_ID);
		workContexts.add(WorkContext.OWNER);
		when(note.getIsPrivate()).thenReturn(false);
		when(note.getIsPublic()).thenReturn(true);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			dispatcher.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_buyerEmployeeAddsNote_noteIsPublic_sendNotification_toDispatcher() {
		when(note.getCreatorId()).thenReturn(OWNER_ID);
		workContexts.add(WorkContext.COMPANY_OWNED);
		when(note.getIsPrivate()).thenReturn(false);
		when(note.getIsPublic()).thenReturn(true);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			dispatcher.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_wmEmployeeAddsNote_noteIsPublic_sendNotification_toDispatcher() {
		when(note.getCreatorId()).thenReturn(OWNER_ID);
		workContexts.add(WorkContext.UNRELATED);
		when(note.getIsPrivate()).thenReturn(false);
		when(note.getIsPublic()).thenReturn(true);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			dispatcher.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_buyerAddsNote_noteIsPrivileged_sendNotification_toDispatcher() {
		when(note.getCreatorId()).thenReturn(OWNER_ID);
		workContexts.add(WorkContext.OWNER);
		when(note.getIsPrivate()).thenReturn(false);
		when(note.getIsPrivileged()).thenReturn(true);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			dispatcher.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_buyerEmployeeAddsNote_noteIsPrivileged_sendNotification_toDispatcher() {
		when(note.getCreatorId()).thenReturn(OWNER_ID);
		workContexts.add(WorkContext.COMPANY_OWNED);
		when(note.getIsPrivate()).thenReturn(false);
		when(note.getIsPrivileged()).thenReturn(true);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			dispatcher.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_wmEmployeeAddsNote_noteIsPrivileged_sendNotification_toDispatcher() {
		when(note.getCreatorId()).thenReturn(OWNER_ID);
		workContexts.add(WorkContext.UNRELATED);
		when(note.getIsPrivate()).thenReturn(false);
		when(note.getIsPrivileged()).thenReturn(true);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			dispatcher.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_noteIsPrivate_doNotSendNotification_toDispatcher() {
		when(note.getIsPrivate()).thenReturn(true);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			dispatcher.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService, never()).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_noteIsNotFromBuyerOrBuyerCompanyOrWM_doNotSendNotification_toDispatcher() {
		workContexts.add(WorkContext.DISPATCHER);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			dispatcher.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService, never()).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_noActiveWorker_doNotSendNotification_toDispatcher() {
		workContexts.add(WorkContext.OWNER);
		when(workService.findActiveWorkerId(work.getId())).thenReturn(null);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			dispatcher.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService, never()).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_noReplyToId_doNotSendNotification_toDispatcher() {
		workContexts.add(WorkContext.OWNER);
		when(note.getReplyToId()).thenReturn(null);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			dispatcher.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService, never()).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_workerIdIsNotReplyToId_doNotSendNotification_toDispatcher() {
		workContexts.add(WorkContext.OWNER);
		when(note.getReplyToId()).thenReturn(WORKER2_ID);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			dispatcher.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService, never()).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_noFollowers_doNotSendNotification_toFollowers() {
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
				follower.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService, never()).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_followerAlreadyReceivedNotification_doNotSendTwoNotifications_toFollowers() {
		when(userNotificationService.buildSet()).thenReturn(Sets.newHashSet(FOLLOWER_ID));
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			follower.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);
		workFollows.add(workFollow);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_followerAddedNote_doNotSendNotification_toFollowers() {
		when(note.getCreatorId()).thenReturn(FOLLOWER_ID);
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			follower.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);
		workFollows.add(workFollow);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService, never()).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkNoteAdded_sendNotification_toFollowers() {
		when(notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
			follower.getId(), work, note, NotificationType.WORK_NOTE_ADDED
		)).thenReturn(workAddedNotification);
		workFollows.add(workFollow);

		userNotificationService.onWorkNoteAdded(note);

		verify(notificationService).sendNotification(workAddedNotification);
	}

	@Test
	public void onWorkCancelled_paidAssignment_sendNotification_toWorker() {
		when(notificationTemplateFactory.buildWorkCancelledPaidNotificationTemplate(
			worker.getId(), work, workResource, cancelWorkDTO.getNote()
		)).thenReturn(workCancelledNotification);

		userNotificationService.onWorkCancelled(work, workResource, cancelWorkDTO, true);

		verify(notificationService).sendNotification(workCancelledNotification);
	}

	@Test
	public void onWorkCancelled_paidAssignment_doNotSendNotification_toWorker() {
		when(notificationTemplateFactory.buildWorkCancelledWithoutPayNotificationTemplate(
				worker.getId(), work, workResource, cancelWorkDTO.getNote()
		)).thenReturn(workCancelledWithoutPayNotification);

		userNotificationService.onWorkCancelled(work, workResource, cancelWorkDTO, true);

		verify(notificationService, never()).sendNotification(workCancelledNotification);
	}

	@Test
	public void onWorkCancelled_unpaidAssignment_sendNotification_toWorker() {
		when(notificationTemplateFactory.buildWorkCancelledWithoutPayNotificationTemplate(
				worker.getId(), work, workResource, cancelWorkDTO.getNote()
		)).thenReturn(workCancelledWithoutPayNotification);

		userNotificationService.onWorkCancelled(work, workResource, cancelWorkDTO, false);

		verify(notificationService).sendNotification(workCancelledWithoutPayNotification);
	}

	@Test
	public void onWorkCancelled_unpaidAssignment_doNotSendNotification_toWorker() {
		when(notificationTemplateFactory.buildWorkCancelledPaidNotificationTemplate(
				worker.getId(), work, workResource, cancelWorkDTO.getNote()
		)).thenReturn(workCancelledNotification);

		userNotificationService.onWorkCancelled(work, workResource, cancelWorkDTO, false);

		verify(notificationService, never()).sendNotification(workCancelledNotification);
	}

	@Test
	public void onWorkCancelled_paidAssignment_sendNotification_toDispatcher() {
		when(notificationTemplateFactory.buildWorkCancelledPaidNotificationTemplate(
				dispatcher.getId(), work, workResource, cancelWorkDTO.getNote()
		)).thenReturn(workCancelledNotification);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkCancelled(work, workResource, cancelWorkDTO, true);

		verify(notificationService).sendNotification(workCancelledNotification);
	}

	@Test
	public void onWorkCancelled_paidAssignment_doNotSendNotification_toDispatcher() {
		when(notificationTemplateFactory.buildWorkCancelledWithoutPayNotificationTemplate(
				dispatcher.getId(), work, workResource, cancelWorkDTO.getNote()
		)).thenReturn(workCancelledWithoutPayNotification);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkCancelled(work, workResource, cancelWorkDTO, true);

		verify(notificationService, never()).sendNotification(workCancelledNotification);
	}

	@Test
	public void onWorkCancelled_unpaidAssignment_sendNotification_toDispatcher() {
		when(notificationTemplateFactory.buildWorkCancelledWithoutPayNotificationTemplate(
				dispatcher.getId(), work, workResource, cancelWorkDTO.getNote()
		)).thenReturn(workCancelledWithoutPayNotification);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkCancelled(work, workResource, cancelWorkDTO, false);

		verify(notificationService).sendNotification(workCancelledWithoutPayNotification);
	}

	@Test
	public void onWorkCancelled_unpaidAssignment_doNotSendNotification_toDispatcher() {
		when(notificationTemplateFactory.buildWorkCancelledPaidNotificationTemplate(
				dispatcher.getId(), work, workResource, cancelWorkDTO.getNote()
		)).thenReturn(workCancelledNotification);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkCancelled(work, workResource, cancelWorkDTO, false);

		verify(notificationService, never()).sendNotification(workCancelledNotification);
	}

	@Test
	public void setupPreWorkResourceNotifications_givenNoDispatcherId_doNotSend_workResourceConfirmation() {
		userNotificationService.setupPreWorkResourceNotifications(work, workResource);

		verify(notificationService, never()).sendNotification(resourceConfirmationNotificationForDispatcher);
	}

	@Test
	public void setupPreWorkResourceNotifications_givenDispatcherId_send_workResourceConfirmation() {
		when(workResourceService.getDispatcherIdForWorkAndWorker(workResource.getWork().getId(), WORKER_ID)).thenReturn(DISPATCHER_ID);

		userNotificationService.setupPreWorkResourceNotifications(work, workResource);

		verify(resourceConfirmationNotificationForDispatcher).setOnBehalfOfId(worker.getId());
		verify(notificationService).sendNotification(resourceConfirmationNotificationForDispatcher);
	}

	@Test
	public void setupPreWorkResourceNotifications_givenNoDispatcherId_doNotSend_workResourceCheckIn() {
		userNotificationService.setupPreWorkResourceNotifications(work, workResource);

		verify(notificationService, never()).sendNotification(resourceCheckInNotificationForDispatcher);
	}

	@Test
	public void setupPreWorkResourceNotifications_givenDispatcherId_send_workResourceCheckIn() {
		when(workResourceService.getDispatcherIdForWorkAndWorker(workResource.getWork().getId(), WORKER_ID)).thenReturn(DISPATCHER_ID);

		userNotificationService.setupPreWorkResourceNotifications(work, workResource);

		verify(resourceCheckInNotificationForDispatcher).setOnBehalfOfId(worker.getId());
		verify(notificationService).sendNotification(resourceCheckInNotificationForDispatcher);
	}

	@Test
	public void onDeliverableDueReminder_givenNoDispatcherId_doNotSend_deliverableDueReminder() {
		userNotificationService.onDeliverableDueReminder(workResource);

		verify(notificationService, never()).sendNotification(deliverableDueReminderNotificationForDispatcher);
	}

	@Test
	public void onDeliverableDueReminder_givenDispatcherId_send_deliverableDueReminder() {
		when(workResourceService.getDispatcherIdForWorkAndWorker(workResource.getWork().getId(), WORKER_ID)).thenReturn(DISPATCHER_ID);

		userNotificationService.onDeliverableDueReminder(workResource);

		verify(deliverableDueReminderNotificationForDispatcher).setOnBehalfOfId(worker.getId());
		verify(notificationService).sendNotification(deliverableDueReminderNotificationForDispatcher);
	}

	@Test
	public void onWorkSubStatus_sendWorkSubStatusAlert_givenAlertIsTrue_doNotSendNotification_toDispatcher() {
		userNotificationService.onWorkSubStatus(WORK_ID, workResource.getId(), typeAssociation);

		verify(notificationService, never()).sendNotification(statusAlertNotificationForDispatcher);
	}

	@Test
	public void onWorkSubStatus_sendWorkSubStatusAlert_givenAlertIsTrueAndIdIsBuyer_doNotSendNotification_toDispatcher() {
		when(typeAssociation.getWorkSubStatusType().isNotifyResourceEnabled()).thenReturn(false);
		when(typeAssociation.getWorkSubStatusType().isNotifyClientEnabled()).thenReturn(true);

		userNotificationService.onWorkSubStatus(WORK_ID, workResource.getId(), typeAssociation);

		verify(notificationService, never()).sendNotification(statusAlertNotificationForDispatcher);
	}

	@Test
	public void onWorkSubStatus_sendWorkSubStatusAlert_givenAlertIsTrue_sendNotification_toDispatcher() {
		when(workResourceService.getDispatcherIdForWorkAndWorker(workResource.getWork().getId(), WORKER_ID)).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkSubStatus(WORK_ID, workResource.getId(), typeAssociation);

		verify(statusAlertNotificationForDispatcher).setOnBehalfOfId(WORKER_ID);
		verify(notificationService).sendNotification(statusAlertNotificationForDispatcher);
	}

	@Test
	public void onWorkSubStatus_sendWorkSubStatusAlert_givenAlertIsFalse_doNotSendNotification_toDispatcher(){
		when(typeAssociation.getWorkSubStatusType().isAlert()).thenReturn(false);

		userNotificationService.onWorkSubStatus(WORK_ID, workResource.getId(), typeAssociation);

		verify(notificationService, never()).sendNotification(statusNotificationForDispatcher);
	}

	@Test
	public void onWorkSubStatus_sendWorkSubStatusAlert_givenAlertIsFalseAndIdIsBuyer_doNotSendNotification_toDispatcher() {
		when(typeAssociation.getWorkSubStatusType().isAlert()).thenReturn(false);
		when(typeAssociation.getWorkSubStatusType().isNotifyResourceEnabled()).thenReturn(false);
		when(typeAssociation.getWorkSubStatusType().isNotifyClientEnabled()).thenReturn(true);

		userNotificationService.onWorkSubStatus(WORK_ID, workResource.getId(), typeAssociation);

		verify(notificationService, never()).sendNotification(statusAlertNotificationForDispatcher);
	}

	@Test
	public void onWorkSubStatus_sendWorkSubStatusAlert_givenAlertIsFalse_sendNotification_toDispatcher() {
		when(typeAssociation.getWorkSubStatusType().isAlert()).thenReturn(false);
		when(workResourceService.getDispatcherIdForWorkAndWorker(workResource.getWork().getId(), WORKER_ID)).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkSubStatus(WORK_ID, workResource.getId(), typeAssociation);

		verify(statusNotificationForDispatcher).setOnBehalfOfId(WORKER_ID);
		verify(notificationService).sendNotification(statusNotificationForDispatcher);
	}

	@Test
	public void onWorkSubStatus_sendWorkSubStatusAlert_givenAlertIsTrue_sendNotification_toLabelRecipients () {
		List<Long> recipientIds = new ArrayList<>();
		recipientIds.add(DISPATCHER_ID);

		when(typeAssociation.getWorkSubStatusType().isNotifyResourceEnabled()).thenReturn(false);
		when(typeAssociation.getWorkSubStatusType().isNotifyClientEnabled()).thenReturn(true);
		when(workSubStatusService.findAllRecipientsByWorkSubStatusId(typeAssociation.getWorkSubStatusType().getId()))
				.thenReturn(recipientIds);

		userNotificationService.onWorkSubStatus(WORK_ID, workResource.getId(), typeAssociation);

		verify(notificationService).sendNotification(statusAlertNotificationForDispatcher);
	}

	@Test
	public void onWorkSubStatus_sendWorkSubStatusAlert_givenAlertIsFalse_sendNotification_toLabelRecipients () {
		List<Long> recipientIds = new ArrayList<>();
		recipientIds.add(DISPATCHER_ID);

		when(typeAssociation.getWorkSubStatusType().isNotifyResourceEnabled()).thenReturn(false);
		when(typeAssociation.getWorkSubStatusType().isNotifyClientEnabled()).thenReturn(true);
		when(typeAssociation.getWorkSubStatusType().isAlert()).thenReturn(false);
		when(workSubStatusService.findAllRecipientsByWorkSubStatusId(typeAssociation.getWorkSubStatusType().getId())).thenReturn(recipientIds);

		userNotificationService.onWorkSubStatus(WORK_ID, workResource.getId(), typeAssociation);

		verify(notificationService).sendNotification(statusNotificationForDispatcher);
	}

	@Test
	public void onDeliverableLate_workResourceNull_doNotAddSystemSubStatus() {
		when(workService.findWorkResourceById(workResource.getId())).thenReturn(null);

		userNotificationService.onDeliverableLate(workResource);

		verify(workSubStatusService, never()).addSystemSubStatus(any(User.class), anyLong(), anyString());
	}

	@Test
	public void onDeliverableLate_workResourceNull_addNotAddLabelToWorkResource() {
		when(workService.findWorkResourceById(workResource.getId())).thenReturn(null);

		userNotificationService.onDeliverableLate(workResource);

		verify(workResourceService, never()).addLabelToWorkResource(any(WorkResourceLabelDTO.class));
	}

	@Test
	public void onDeliverableLate_workResourceNull_doNotSendNotifications() {
		when(workService.findWorkResourceById(workResource.getId())).thenReturn(null);

		userNotificationService.onDeliverableLate(workResource);

		verify(notificationService, never()).sendNotification(any(NotificationTemplate.class));
	}

	@Test
	public void onWorkClosedAndPaid_noWorkResource_doNotSendNotifications() {
		when(workService.findWorkResourceById(worker.getId())).thenReturn(null);

		userNotificationService.onWorkClosedAndPaid(worker.getId());

		verify(notificationService, never()).sendNotification(any(NotificationTemplate.class));
	}

	@Test
	public void onRatingCreated_ratingIsNotShared_doNotSendNotifications() {
		when(rating.isRatingSharedFlag()).thenReturn(false);

		userNotificationService.onRatingCreated(rating);

		verify(notificationService, never()).sendNotification(any(NotificationTemplate.class));
	}

	@Test
	public void onWorkClosedAndPaid_noWork_doNotSendNotifications() {
		when(workResource.getWork()).thenReturn(null);

		userNotificationService.onWorkClosedAndPaid(worker.getId());

		verify(notificationService, never()).sendNotification(any(NotificationTemplate.class));
	}

	@Test
	public void onDeliverableLate_addSystemSubStatus() {
		userNotificationService.onDeliverableLate(workResource);

		verify(workSubStatusService).addSystemSubStatus(worker, work.getId(), WorkSubStatusType.DELIVERABLE_LATE);
	}

	@Test
	public void onDeliverableLate_addLabelToWorkResource() {
		userNotificationService.onDeliverableLate(workResource);

		verify(workResourceService).addLabelToWorkResource(any(WorkResourceLabelDTO.class));
	}

	@Test
	public void onDeliverableLate_sendNotification_toWorker() {
		WorkDeliverableLateNotificationTemplate forWorker = mock(WorkDeliverableLateNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkDeliverableLateNotificationTemplate(worker.getId(), work)).thenReturn(forWorker);

		userNotificationService.onDeliverableLate(workResource);

		verify(notificationService).sendNotification(forWorker);
	}

	@Test
	public void onDeliverableLate_sendNotification_toDispatcher() {
		WorkDeliverableLateNotificationTemplate forDispatcher = mock(WorkDeliverableLateNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkDeliverableLateNotificationTemplate(dispatcher.getId(), work)).thenReturn(forDispatcher);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onDeliverableLate(workResource);

		verify(notificationService).sendNotification(forDispatcher);
	}

	@Test
	public void onDeliverableLate_doNotSendNotification_toDispatcher() {
		WorkDeliverableLateNotificationTemplate forDispatcher = mock(WorkDeliverableLateNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkDeliverableLateNotificationTemplate(dispatcher.getId(), work)).thenReturn(forDispatcher);

		userNotificationService.onDeliverableLate(workResource);

		verify(notificationService, never()).sendNotification(forDispatcher);
	}

	@Test
	public void onWorkAttachmentAdded_workIsNeitherActiveNorComplete_earlyReturn() {
		when(work.isActive()).thenReturn(false);
		when(work.isComplete()).thenReturn(false);

		userNotificationService.onWorkAttachmentAdded(work, asset);

		verify(workService, never()).findActiveWorkResource(work.getId());
	}

	@Test
	public void onWorkAttachmentAdded_noActiveResource_earlyReturn() {
		when(workService.findActiveWorkResource(work.getId())).thenReturn(null);

		userNotificationService.onWorkAttachmentAdded(work, asset);

		verify(workService, never()).findWork(work.getId());
	}

	@Test
	public void onWorkAttachmentAdded_workerAttachment_sendNotification_toBuyer() {
		when(asset.getCreatorId()).thenReturn(WORKER_ID);
		WorkAttachmentAddedNotificationTemplate buyerNotification = mock(WorkAttachmentAddedNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkAttachmentAddedNotificationTemplate(buyer.getId(), work, asset)).thenReturn(buyerNotification);

		userNotificationService.onWorkAttachmentAdded(work, asset);

		verify(notificationService).sendNotification(buyerNotification);
	}

	@Test
	public void onWorkAttachmentAdded_buyerAttachment_doNotSendNotification_toBuyer() {
		WorkAttachmentAddedNotificationTemplate buyerNotification = mock(WorkAttachmentAddedNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkAttachmentAddedNotificationTemplate(buyer.getId(), work, asset)).thenReturn(buyerNotification);

		userNotificationService.onWorkAttachmentAdded(work, asset);

		verify(notificationService, never()).sendNotification(buyerNotification);
	}

	@Test
	public void onWorkAttachmentAdded_workerAttachment_sendNotification_toFollower() {
		when(asset.getCreatorId()).thenReturn(WORKER_ID);
		workFollows.add(workFollow);
		WorkAttachmentAddedNotificationTemplate followerNotification = mock(WorkAttachmentAddedNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkAttachmentAddedNotificationTemplate(follower.getId(), work, asset)).thenReturn(followerNotification);

		userNotificationService.onWorkAttachmentAdded(work, asset);

		verify(notificationService).sendNotification(followerNotification);
	}

	@Test
	public void onWorkAttachmentAdded_workerAttachment_noFollowers_doNotSendNotification_toFollower() {
		when(asset.getCreatorId()).thenReturn(WORKER_ID);
		WorkAttachmentAddedNotificationTemplate followerNotification = mock(WorkAttachmentAddedNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkAttachmentAddedNotificationTemplate(follower.getId(), work, asset)).thenReturn(followerNotification);

		userNotificationService.onWorkAttachmentAdded(work, asset);

		verify(notificationService, never()).sendNotification(followerNotification);
	}

	@Test
	public void onWorkAttachmentAdded_buyerAttachment_doNotSendNotification_toFollower() {
		workFollows.add(workFollow);
		WorkAttachmentAddedNotificationTemplate followerNotification = mock(WorkAttachmentAddedNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkAttachmentAddedNotificationTemplate(follower.getId(), work, asset)).thenReturn(followerNotification);

		userNotificationService.onWorkAttachmentAdded(work, asset);

		verify(notificationService, never()).sendNotification(followerNotification);
	}

	@Test
	public void onWorkAttachmentAdded_buyerAttachment_sendNotification_toWorker() {
		WorkAttachmentAddedNotificationTemplate workerNotification = mock(WorkAttachmentAddedNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkAttachmentAddedNotificationTemplate(worker.getId(), work, asset)).thenReturn(workerNotification);

		userNotificationService.onWorkAttachmentAdded(work, asset);

		verify(notificationService).sendNotification(workerNotification);
	}

	@Test
	public void onWorkAttachmentAdded_workerAttachment_doNotSendNotification_toWorker() {
		when(asset.getCreatorId()).thenReturn(WORKER_ID);
		WorkAttachmentAddedNotificationTemplate workerNotification = mock(WorkAttachmentAddedNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkAttachmentAddedNotificationTemplate(worker.getId(), work, asset)).thenReturn(workerNotification);

		userNotificationService.onWorkAttachmentAdded(work, asset);

		verify(notificationService, never()).sendNotification(workerNotification);
	}

	@Test
	public void onWorkAttachmentAdded_buyerAttachment_sendNotification_toDispatcher() {
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);
		WorkAttachmentAddedNotificationTemplate dispatcherNotification = mock(WorkAttachmentAddedNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkAttachmentAddedNotificationTemplate(dispatcher.getId(), work, asset)).thenReturn(dispatcherNotification);

		userNotificationService.onWorkAttachmentAdded(work, asset);

		verify(dispatcherNotification).setOnBehalfOfId(worker.getId());
		verify(notificationService).sendNotification(dispatcherNotification);
	}

	@Test
	public void onWorkAttachmentAdded_workerAttachment_doNotSendNotification_toDispatcher() {
		when(asset.getCreatorId()).thenReturn(WORKER_ID);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);
		WorkAttachmentAddedNotificationTemplate dispatcherNotification = mock(WorkAttachmentAddedNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkAttachmentAddedNotificationTemplate(dispatcher.getId(), work, asset)).thenReturn(dispatcherNotification);

		userNotificationService.onWorkAttachmentAdded(work, asset);

		verify(notificationService, never()).sendNotification(dispatcherNotification);
	}

	@Test
	public void onWorkAttachmentAdded_noDispatcher_doNotSendNotification_toDispatcher() {
		when(asset.getCreatorId()).thenReturn(WORKER_ID);
		WorkAttachmentAddedNotificationTemplate dispatcherNotification = mock(WorkAttachmentAddedNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkAttachmentAddedNotificationTemplate(dispatcher.getId(), work, asset)).thenReturn(dispatcherNotification);

		userNotificationService.onWorkAttachmentAdded(work, asset);

		verify(notificationService, never()).sendNotification(dispatcherNotification);
	}

	@Test
	public void onRatingCreated_ratingIsShared_sendNotifications_toRatedUser() {
		WorkRatingCreatedNotificationTemplate template = mock(WorkRatingCreatedNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkRatingCreatedNotificationTemplate(rating.getRatedUser().getId(), rating))
			.thenReturn(template);

		userNotificationService.onRatingCreated(rating);

		verify(notificationService).sendNotification(template);
	}

	@Test
	public void onRatingCreated_ratingIsShared_sendNotifications_toFollower() {
		when(rating.isBuyerRating()).thenReturn(true);
		workFollows.add(workFollow);
		WorkRatingCreatedNotificationTemplate toFollower = mock(WorkRatingCreatedNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkRatingCreatedNotificationTemplate(follower.getId(), rating))
			.thenReturn(toFollower);

		userNotificationService.onRatingCreated(rating);

		verify(toFollower).setWorkFollow(workFollow);
		verify(notificationService).sendNotification(toFollower);
	}

	@Test
	public void onRatingCreated_ratingIsShared_doNotSendNotifications_toFollower() {
		when(rating.isBuyerRating()).thenReturn(true);
		WorkRatingCreatedNotificationTemplate toFollower = mock(WorkRatingCreatedNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkRatingCreatedNotificationTemplate(follower.getId(), rating))
			.thenReturn(toFollower);

		userNotificationService.onRatingCreated(rating);

		verify(notificationService, never()).sendNotification(toFollower);
	}

	@Test
	public void onRatingCreated_ratingIsShared_sendNotifications_toDispatcher() {
		WorkRatingCreatedNotificationTemplate toDispatcher = mock(WorkRatingCreatedNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkRatingCreatedNotificationTemplate(dispatcher.getId(), rating))
			.thenReturn(toDispatcher);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onRatingCreated(rating);

		verify(toDispatcher).setOnBehalfOfId(worker.getId());
		verify(notificationService).sendNotification(toDispatcher);
	}

	@Test
	public void onRatingCreated_ratingIsShared_doNotSendNotifications_toDispatcher() {
		WorkRatingCreatedNotificationTemplate toDispatcher = mock(WorkRatingCreatedNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkRatingCreatedNotificationTemplate(dispatcher.getId(), rating))
			.thenReturn(toDispatcher);

		userNotificationService.onRatingCreated(rating);

		verify(notificationService, never()).sendNotification(toDispatcher);
	}

	@Test(expected = IllegalArgumentException.class)
	public void onQuestionAnswered_questionIsNull_throwException() {
		userNotificationService.onQuestionAnswered(null, work.getId());
	}

	@Test
	public void onQuestionAnswered_sendNotifications_toWorker() {
		WorkQuestionAnsweredNotificationTemplate toWorker = mock(WorkQuestionAnsweredNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkQuestionAnsweredNotificationTemplate(worker.getId(), work, questionAnswerPair))
			.thenReturn(toWorker);

		userNotificationService.onQuestionAnswered(questionAnswerPair, WORK_ID);

		verify(notificationService).sendNotification(toWorker);
	}

	@Test
	public void onQuestionAnswered_sendNotifications_toDispatcher() {
		WorkQuestionAnsweredNotificationTemplate toDispatcher = mock(WorkQuestionAnsweredNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkQuestionAnsweredNotificationTemplate(dispatcher.getId(), work, questionAnswerPair))
			.thenReturn(toDispatcher);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), questionAnswerPair.getQuestionerId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onQuestionAnswered(questionAnswerPair, WORK_ID);

		verify(toDispatcher).setOnBehalfOfId(questionAnswerPair.getQuestionerId());
		verify(notificationService).sendNotification(toDispatcher);
	}

	@Test
	public void onQuestionAnswered_doNotSendNotifications_toDispatcher() {
		WorkQuestionAnsweredNotificationTemplate toDispatcher = mock(WorkQuestionAnsweredNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkQuestionAnsweredNotificationTemplate(dispatcher.getId(), work,
				questionAnswerPair))
			.thenReturn(toDispatcher);

		userNotificationService.onQuestionAnswered(questionAnswerPair, WORK_ID);

		verify(notificationService, never()).sendNotification(toDispatcher);
	}

	@Test
	public void onWorkClosedAndPaid_noWorkFulfillmentStrategy_doNotSendNotifications() {
		when(work.getFulfillmentStrategy()).thenReturn(null);

		userNotificationService.onWorkClosedAndPaid(worker.getId());

		verify(notificationService, never()).sendNotification(any(NotificationTemplate.class));
	}

	@Test
	public void onWorkClosedAndPaid_noAmountEarned_doNotSendNotifications() {
		when(fulfillmentStrategy.getAmountEarned()).thenReturn(null);

		userNotificationService.onWorkClosedAndPaid(worker.getId());

		verify(notificationService, never()).sendNotification(any(NotificationTemplate.class));
	}

	@Test
	public void onWorkClosedAndPaid_sendNotification_toWorker() {
		when(notificationTemplateFactory.buildWorkCompletedFundsAddedNotificationTemplate(eq(worker.getId()), eq(work), eq(workResource), anyBoolean()))
			.thenReturn(workCompletedFundsAddedNotificationTemplate);

		userNotificationService.onWorkClosedAndPaid(worker.getId());

		verify(notificationService).sendNotification(workCompletedFundsAddedNotificationTemplate);
	}

	@Test
	public void onWorkClosedAndPaid_doNotSendNotification_toDispatcher() {
		when(notificationTemplateFactory.buildWorkCompletedFundsAddedNotificationTemplate(eq(dispatcher.getId()), eq(work), eq(workResource), anyBoolean()))
			.thenReturn(workCompletedFundsAddedNotificationTemplateForDispatcher);

		userNotificationService.onWorkClosedAndPaid(worker.getId());

		verify(notificationService, never()).sendNotification(workCompletedFundsAddedNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkClosedAndPaid_sendNotification_toDispatcher() {
		when(notificationTemplateFactory.buildWorkCompletedFundsAddedNotificationTemplate(eq(dispatcher.getId()), eq(work), eq(workResource), anyBoolean()))
			.thenReturn(workCompletedFundsAddedNotificationTemplateForDispatcher);
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkClosedAndPaid(worker.getId());

		verify(workCompletedFundsAddedNotificationTemplateForDispatcher).setOnBehalfOfId(worker.getId());
		verify(notificationService).sendNotification(workCompletedFundsAddedNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkReinvited_sendNotification_toWorker() {
		userNotificationService.onWorkReinvited(work, workResources);

		verify(notificationService).sendNotification(workReinvitedNotificationTemplate);
	}

	@Test
	public void onWorkReinvited_sendNotification_toDispatcher() {
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkReinvited(work, workResources);

		verify(workReinvitedNotificationTemplateForDispatcher).setOnBehalfOfId(worker.getId());
		verify(notificationService).sendNotification(workReinvitedNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkReinvited_doNotSendNotification_toDispatcher() {
		userNotificationService.onWorkReinvited(work, workResources);

		verify(notificationService, never()).sendNotification(workReinvitedNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkRemindResourceToComplete_sendNotification_toWorker() {
		userNotificationService.onWorkRemindResourceToComplete(work, worker, note);

		verify(notificationService).sendNotification(workRemindResourceToCompleteNotificationTemplate);
	}

	@Test
	public void onWorkRemindResourceToComplete_sendNotification_toDispatcher() {
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);

		userNotificationService.onWorkRemindResourceToComplete(work, worker, note);

		verify(notificationService).sendNotification(workRemindResourceToCompleteNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkRemindResourceToComplete_doNotSendNotification_toDispatcher() {
		userNotificationService.onWorkRemindResourceToComplete(work, worker, note);

		verify(notificationService, never()).sendNotification(workRemindResourceToCompleteNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkIncomplete_noActiveWorker_earlyReturn() {
		when(workService.findActiveWorkerId(WORK_ID)).thenReturn(null);

		userNotificationService.onWorkIncomplete(work, NOTE_FOR_WORKER);

		verify(notificationService, never()).sendNotification(any(NotificationTemplate.class));
	}

	@Test
	public void onWorkIncomplete_sendNotification_toWorker() {
		userNotificationService.onWorkIncomplete(work, NOTE_FOR_WORKER);

		verify(notificationService).sendNotification(workIncompleteNotificationTemplate);
	}

	@Test
	public void onWorkIncomplete_sendNotification_toDispatcher() {
		when(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId())).thenReturn(DISPATCHER_ID);
		userNotificationService.onWorkIncomplete(work, NOTE_FOR_WORKER);

		verify(notificationService).sendNotification(workIncompleteNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkIncomplete_doSendNotification_toDispatcher() {
		userNotificationService.onWorkIncomplete(work, NOTE_FOR_WORKER);

		verify(notificationService, never()).sendNotification(workIncompleteNotificationTemplateForDispatcher);
	}

	@Test
	public void onWorkInvitationForVendor_noDispatchers_doNotSendNotifications() {
		when(workResourceService.getAllDispatcherIdsInCompany(COMPANY_ID)).thenReturn(Lists.<Long>newArrayList());

		userNotificationService.onWorkInvitationForVendor(WORK_ID, COMPANY_ID);

		verify(notificationService, never()).sendNotification(any(NotificationTemplate.class));
	}

	@Test
	public void onWorkInvitationForVendor_nullTemplate_doNotSendNotifications() {
		when(workResourceService.getAllDispatcherIdsInCompany(COMPANY_ID)).thenReturn(Lists.newArrayList(DISPATCHER_ID));
		when(notificationTemplateFactory.buildWorkResourceInvitation(WORK_ID, DISPATCHER_ID, false)).thenReturn(null);

		userNotificationService.onWorkInvitationForVendor(WORK_ID, COMPANY_ID);

		verify(notificationService, never()).sendNotification(any(NotificationTemplate.class));
	}

	@Test
	public void onWorkInvitationForVendor_sendNotifications() {
		when(workResourceService.getAllDispatcherIdsInCompany(COMPANY_ID)).thenReturn(Lists.newArrayList(DISPATCHER_ID));
		NotificationTemplate template = mock(NotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkResourceInvitation(WORK_ID, DISPATCHER_ID, false)).thenReturn(template);

		userNotificationService.onWorkInvitationForVendor(WORK_ID, COMPANY_ID);

		verify(template).setCompanyNotification(true);
		verify(notificationService).sendNotification(eq(template), any(Calendar.class));
	}

	@Test
	public void archiveUserNotification_archiveAndRefreshCache() {
		userNotificationService.archiveUserNotification(userNotification.getUuid(), OWNER_ID);

		verify(notificationClient).archive(userNotification.getUuid(), context);
	}

	@Test
	public void search_notificationClientSearch() {
		final UserNotificationSearchRequest request = UserNotificationSearchRequest.builder().setToUserId("some-id").build();
		when(notificationClient.search(request, context))
				.thenReturn(Observable.just(new UserNotificationSearchResponse(0, 0, null, 0L)));

		userNotificationService.search(request);

		verify(notificationClient).search(request, context);
	}

	@Test
	public void featureFlagReturnsFalseWhenCalloutErrors() {
		final User user = mock(User.class);
		final String toggleName = "toggle.name";

		when(featureEntitlementService.getFeatureToggle(any(User.class), anyString()))
				.thenReturn(Observable.<FeatureToggleAndStatus>error(new IllegalStateException("whoa bad feature toggle get")));

		assertFalse(userNotificationService.featureFlagIsOnForUser(user, toggleName));
	}

	@Test
	public void featureFlagReturnsFalseWhenNoFeatureFlagExists() {
		final User user = mock(User.class);
		final String toggleName = "toggle.name";

		when(featureEntitlementService.getFeatureToggle(any(User.class), anyString()))
				.thenReturn(Observable.<FeatureToggleAndStatus>empty());

		assertFalse(userNotificationService.featureFlagIsOnForUser(user, toggleName));
	}

	@Test
	public void featureFlagReturnsFalseWhenFeatureFlagIsNotLookedUp() {
		final User user = mock(User.class);
		final String toggleName = "toggle.name";
		final Messages.Status failedStatus = Messages.Status.newBuilder().setSuccess(false).build();
		final Messages.FeatureToggle featureToggle = Messages.FeatureToggle.newBuilder().build();

		when(featureEntitlementService.getFeatureToggle(any(User.class), anyString()))
				.thenReturn(Observable.just(new FeatureToggleAndStatus(failedStatus, featureToggle)));

		assertFalse(userNotificationService.featureFlagIsOnForUser(user, toggleName));
	}
}
