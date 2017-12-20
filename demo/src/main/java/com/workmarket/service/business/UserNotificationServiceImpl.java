package com.workmarket.service.business;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.codahale.metrics.MetricRegistry;
import com.workmarket.common.cache.UserNotificationCache;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.common.template.AbstractWorkNotificationTemplate;
import com.workmarket.common.template.ApproveProfileModificationNotificationTemplate;
import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.common.template.NotificationTemplateFactory;
import com.workmarket.common.template.UserGroupInactiveDeactivatedNotificationTemplate;
import com.workmarket.common.template.WorkBundleAcceptedDetailsNotificationTemplate;
import com.workmarket.common.template.WorkDeliverableDueReminderNotificationTemplate;
import com.workmarket.common.template.WorkNegotiationApprovedNotificationTemplate;
import com.workmarket.common.template.WorkReportGeneratedEmailTemplate;
import com.workmarket.common.template.WorkReportGeneratedLargeEmailTemplate;
import com.workmarket.common.template.WorkResourceCheckInNotificationTemplate;
import com.workmarket.common.template.WorkResourceConfirmationNotificationTemplate;
import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.common.template.email.EmailTemplateFactory;
import com.workmarket.common.template.email.NotificationEmailTemplate;
import com.workmarket.common.template.email.TalentPoolRequirementExpirationEmailTemplate;
import com.workmarket.common.template.pdf.PDFTemplateFactory;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.InvitationDAO;
import com.workmarket.dao.LookupEntityDAO;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.lane.LaneAssociationDAO;
import com.workmarket.dao.summary.work.WorkMilestonesDAO;
import com.workmarket.domains.forums.dao.ForumPostFollowerDAO;
import com.workmarket.domains.forums.model.ForumPost;
import com.workmarket.domains.forums.model.ForumPostFollower;
import com.workmarket.domains.groups.dao.UserGroupDAO;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserGroupInvitationType;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.DateRangeUtilities;
import com.workmarket.domains.model.Invitation;
import com.workmarket.domains.model.InvitationStatusType;
import com.workmarket.domains.model.MimeType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserLaneRelationship;
import com.workmarket.domains.model.UserLaneRelationshipPagination;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.WorkQuestionAnswerPair;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.account.BankAccountTransaction;
import com.workmarket.domains.model.account.CreditCardTransaction;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTier;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.AssessmentConfiguration;
import com.workmarket.domains.model.assessment.Attempt;
import com.workmarket.domains.model.assessment.AttemptStatusType;
import com.workmarket.domains.model.assessment.GradedAssessment;
import com.workmarket.domains.model.assessment.SurveyAssessment;
import com.workmarket.domains.model.assessment.WorkScopedAttempt;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.changelog.PropertyChange;
import com.workmarket.domains.model.changelog.PropertyChangeType;
import com.workmarket.domains.model.changelog.work.WorkPropertyChangeType;
import com.workmarket.domains.model.clientservice.ClientServiceAlert;
import com.workmarket.domains.model.clientservice.LockedAccountClientServiceAlert;
import com.workmarket.domains.model.directory.Email;
import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.invoice.InvoicePagination;
import com.workmarket.domains.model.invoice.InvoiceStatusType;
import com.workmarket.domains.model.invoice.Statement;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.lane.LaneAssociation;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.note.WorkNote;
import com.workmarket.domains.model.notification.AssessmentNotificationPreference;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.model.notification.UserNotification;
import com.workmarket.domains.model.notification.UserNotificationPagination;
import com.workmarket.domains.model.option.WorkOption;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.model.request.Request;
import com.workmarket.domains.model.request.UserGroupInvitation;
import com.workmarket.domains.model.requirementset.Criterion;
import com.workmarket.domains.model.screening.BackgroundCheck;
import com.workmarket.domains.model.screening.DrugTest;
import com.workmarket.domains.model.screening.Screening;
import com.workmarket.domains.model.screening.ScreeningStatusType;
import com.workmarket.domains.model.summary.work.WorkMilestones;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.TaxVerificationStatusType;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.domains.payments.dao.InvoiceDAO;
import com.workmarket.domains.payments.dao.RegisterTransactionDAO;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkDue;
import com.workmarket.domains.work.model.WorkResourceLabelType;
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
import com.workmarket.feature.gen.Messages.FeatureToggle;
import com.workmarket.feature.vo.FeatureToggleAndStatus;
import com.workmarket.group.UserGroupExpiration;
import com.workmarket.notification.NotificationClient;
import com.workmarket.notification.user.vo.UserNotificationSearchRequest;
import com.workmarket.notification.user.vo.UserNotificationSearchRequestBuilder;
import com.workmarket.notification.user.vo.UserNotificationSearchResponse;
import com.workmarket.notification.user.vo.UserNotificationStatus;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.CancelWorkDTO;
import com.workmarket.service.business.dto.EMailDTO;
import com.workmarket.service.business.dto.FileDTO;
import com.workmarket.service.business.dto.PaymentSummaryDTO;
import com.workmarket.service.business.dto.WorkResourceLabelDTO;
import com.workmarket.service.business.event.Event;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.event.MarkUserNotificationsAsReadEvent;
import com.workmarket.service.business.event.RefreshUserNotificationCacheEvent;
import com.workmarket.service.business.event.ScheduledEvent;
import com.workmarket.service.business.event.work.ResourceConfirmationRequiredScheduledEvent;
import com.workmarket.service.business.event.work.ValidateResourceCheckInScheduledEvent;
import com.workmarket.service.business.event.work.WorkAcceptedEvent;
import com.workmarket.service.business.event.work.WorkResourceLateLabelScheduledEvent;
import com.workmarket.service.business.event.work.WorkUpdatedEvent;
import com.workmarket.service.business.pay.PaymentSummaryService;
import com.workmarket.service.business.screening.ScreeningAndUser;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.business.wrapper.AcceptWorkResponse;
import com.workmarket.service.exception.HostServiceException;
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
import com.workmarket.service.summary.SummaryService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.thrift.work.display.ReportResponse;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.FileUtilities;
import com.workmarket.utility.PDFUtilities;
import com.workmarket.utility.StringUtilities;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.codehaus.plexus.util.ExceptionUtils;
import org.hibernate.Hibernate;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import rx.functions.Func1;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
public class UserNotificationServiceImpl implements UserNotificationService {

	private static final Logger logger = LoggerFactory.getLogger(UserNotificationServiceImpl.class);

	private static String NOTIFY_BY_MICROSERVICE_FORUM_FEATURE_TOGGLE = "notification.notify_by_microservice.forum";

	@Autowired private PaymentSummaryService paymentSummaryService;
	@Autowired private UserService userService;
	@Autowired private BillingService billingService;
	@Autowired private ProfileService profileService;
	@Autowired private WorkService workService;
	@Autowired private CompanyService companyService;
	@Autowired private WorkNegotiationService workNegotiationService;
	@Autowired private NotificationService notificationService;
	@Autowired private NotificationTemplateFactory notificationTemplateFactory;
	@Autowired private RequestService requestService;
	@Autowired private UserGroupDAO userGroupDAO;
	@Autowired private UserGroupService userGroupService;
	@Autowired private EventFactory eventFactory;
	@Autowired private EventRouter eventRouter;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private AssetManagementService assetManagementService;
	@Autowired private WorkMilestonesDAO workMilestonesDAO;
	@Autowired private InvitationDAO invitationDAO;
	@Autowired private LaneAssociationDAO laneAssociationDAO;
	@Autowired private CompanyDAO companyDAO;
	@Autowired private WorkResourceService workResourceService;
	@Autowired private InvoiceDAO invoiceDAO;
	@Autowired private RegisterTransactionDAO registerTransactionDAO;
	@Autowired private ForumPostFollowerDAO followerDAO;
	@Autowired private EmailTemplateFactory emailTemplateFactory;
	@Autowired private NotificationDispatcher notificationDispatcher;
	@Autowired private TaxService taxService;
	@Autowired private WorkFollowService workFollowService;
	@Autowired private AssetManagementService assetService;
	@Autowired private PDFTemplateFactory PDFTemplateFactory;
	@Autowired private LookupEntityDAO lookupEntityDAO;
	@Autowired private WorkBundleService workBundleService;
	@Autowired private UserNotificationCache userNotificationCache;
	@Autowired private EmailService emailService;
	@Autowired private ClientServiceAlertService clientServiceAlertService;
	@Autowired private WorkSubStatusService workSubStatusService;
	@Autowired private SummaryService summaryService;
	@Autowired private CompanyAlertService companyAlertService;
	@Qualifier("workOptionsService") @Autowired private OptionsService<AbstractWork> workOptionsService;
	@Autowired @Qualifier("accountRegisterServicePrefundImpl") private AccountRegisterService accountRegisterServicePrefundImpl;
	@Resource(name = "accountRegisterReconciliationEmailNotifications")
	private String accountRegisterReconciliationEmailNotifications;
	@Autowired private WorkResourceDAO workResourceDAO;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private FeatureEntitlementService featureEntitlementService;
	@Autowired private UserNotificationPrefsService userNotificationPrefsService;
	@Autowired private NotificationClient client;

	@Value("${baseurl}")
	private String baseUrl;

	private static final long BULK_SEND_INVITATION_DELAY_ON_MILLIS = 5000L;
	private final DateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm z");

	private WMMetricRegistryFacade wmMetricRegistryFacade;


	@PostConstruct
	void init() {
		wmMetricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "usernotificationservice");
	}

	@VisibleForTesting
	void setClient(NotificationClient client) {
		this.client = client;
	}

	@Override
	public boolean sendUserNotification(
			final String uuid,
			final boolean isSticky,
			final String displayMessage,
			final Long toUserId,
			final Long fromUserId,
			final String notificationTypeCode) {
		wmMetricRegistryFacade.meter("sendUserNotification").mark();
		final String toUserUuid = userService.findUserUuidById(toUserId);
		final String fromUserUuid = userService.findUserUuidById(fromUserId);
		final com.workmarket.notification.user.vo.UserNotification userNotification =
				com.workmarket.notification.user.vo.UserNotification.builder()
						.setUuid(uuid)
						.setSticky(isSticky)
						.setDisplayMessage(displayMessage)
						.setToUserId(toUserId.toString())
						.setToUserUuid(toUserUuid)
						.setFromUserId(fromUserId.toString())
						.setFromUserUuid(fromUserUuid)
						.setNotificationType(notificationTypeCode)
						.setStatus(UserNotificationStatus.PUBLISHED)
						.build();
		logger.info("sending bullhorn notification off to notification microservice");
		final boolean result = client.notify(userNotification, webRequestContextProvider.getRequestContext())
				.map(new Func1<com.workmarket.notification.user.vo.UserNotification, Boolean>() {
					@Override
					public Boolean call(final com.workmarket.notification.user.vo.UserNotification userNotification) {
						return true;
					}
				})
				.defaultIfEmpty(false)
				.onErrorReturn(new Func1<Throwable, Boolean>() {
					@Override
					public Boolean call(final Throwable throwable) {
						return false;
					}
				})
				.toBlocking()
				.single();
		logger.info("send to microservice succeeded? [{}]", result);
		refreshUserNotificationCache(notificationTypeCode, uuid, toUserId);

		return result;
	}

	@Override
	public void sendUserNotification(final UserNotificationDTO dto) {
		Assert.notNull(dto);
		final Long toUserId = dto.getToUserId();
		Assert.notNull(toUserId);
		Assert.notNull(dto.getFromUserId());

		if (StringUtils.isEmpty(dto.getUuid())) {
			dto.setUuid(UUID.randomUUID().toString());
		}

		sendUserNotification(
				dto.getUuid(),
				dto.isSticky(),
				dto.getMsg(),
				dto.getToUserId(),
				dto.getFromUserId(),
				dto.getNotificationType().getCode());
	}

	private void refreshUserNotificationCache(final String notificationTypeCode, final String userNotificationUuid, final Long toUserId) {
		final UserNotificationPreferencePojo preference = userNotificationPrefsService.findByUserAndNotificationType(toUserId, notificationTypeCode);

		final Optional<PersonaPreference> personaPref = userService.getPersonaPreference(toUserId);
		final Boolean cacheRefresh = personaPref.isPresent() && personaPref.get().isDispatcher() ?
				preference.getDispatchBullhornFlag() : preference.isCacheable();
		if (cacheRefresh) {
			eventRouter.sendEvent(new RefreshUserNotificationCacheEvent(toUserId, userNotificationUuid));
		}
	}

	private com.workmarket.notification.user.vo.UserNotification buildRequest(final UserNotificationDTO dto) {
		return com.workmarket.notification.user.vo.UserNotification.builder()
			.setUuid(dto.getUuid())
			.setSticky(dto.isSticky())
			.setDisplayMessage(dto.getMsg())
			.setToUserId(dto.getToUserId().toString())
			.setFromUserId(dto.getFromUserId().toString())
			.setNotificationType(dto.getNotificationType().getCode())
			.setStatus(UserNotificationStatus.PUBLISHED)
			.build();
	}

	UserNotification makeUserNotification(UserNotificationDTO dto, User fromUser, User toUser) {
		return new UserNotification(dto.getNotificationType(), dto.getMsg(), dto.isSticky(), fromUser, toUser, dto.getUuid());
	}

	@Override
	public UserNotificationPagination findAllUserNotifications(final Long userId,
															   final UserNotificationPagination pagination) {
		Assert.notNull(userId);
		Assert.notNull(pagination);

		wmMetricRegistryFacade.meter("findAllUserNotifications").mark();
		final UserNotificationSearchRequest request = buildRequest(userId, pagination);
		return client.search(request, webRequestContextProvider.getRequestContext())
			.map(new Func1<UserNotificationSearchResponse, UserNotificationPagination>() {
				@Override
				public UserNotificationPagination call(final UserNotificationSearchResponse response) {
					final UserNotificationPagination pag = new UserNotificationPagination();
					pag.setIncludeFullCount(request.isIncludeCount());
					pag.setResults(UserNotificationUtil.convertToMonolitth(response.getResults()));
					pag.setResultsLimit(response.getLimit());
					pag.setRowCount(response.getRowCount());
					pag.setStartRow(response.getOffset());
					return pag;
				}
			}).toBlocking().single();
	}

	@Override
	public UserNotificationSearchResponse search(final UserNotificationSearchRequest request) {
		Assert.notNull(request);
		Assert.notNull(request.getToUserId());

		wmMetricRegistryFacade.meter("search").mark();

		return client.search(request, webRequestContextProvider.getRequestContext()).toBlocking().single();
	}

	private UserNotificationSearchRequest buildRequest(final Long userId, final UserNotificationPagination pagination) {
		final UserNotificationSearchRequestBuilder builder = UserNotificationSearchRequest.builder()
			.setIncludeCount(pagination.isIncludeFullCount())
			.setToUserId(String.valueOf(userId))
			.setOffset(pagination.getStartRow())
			.setLimit(pagination.getResultsLimit())
			.setDirection(UserNotificationUtil.convertSortDirection(pagination.getSortDirection()))
			.setOrder(UserNotificationUtil.convertSortOrder(pagination.getSortColumn()))
			.setArchived(Boolean.parseBoolean(pagination.getFilter(UserNotificationPagination.FILTER_KEYS.ARCHIVED)))
			.setType(pagination.getFilter(UserNotificationPagination.FILTER_KEYS.NOTIFICATION_TYPE))
			.setFromDate(new DateTime(DateUtilities.addTime(Calendar.getInstance(), -30, Constants.DAY))); // last 30 days

		if (!builder.isArchived()) {
			builder.setStatus(UserNotificationStatus.PUBLISHED);
		}

		return builder.build();
	}

	@Override
	public List<UserNotification> findAllCacheableUserNotifications(Long userId) {
		UserNotificationPagination pagination = UserNotificationPagination.newBullhornPagination();
		pagination.setIncludeFullCount(false);
		pagination.addFilter(UserNotificationPagination.FILTER_KEYS.LAST_30_DAYS, Boolean.TRUE);
		UserNotificationPagination result = findAllUserNotifications(userId, pagination);
		return result.getResults();
	}

	@Override
	public String findAllUserNotificationsForBullhornJson(Long userId) {
		Assert.notNull(userId);
		wmMetricRegistryFacade.meter("getCachedNewUserNotification").mark();
		Optional<String> cachedResult = userNotificationCache.getNewUserNotificationJson(userId);
		if (cachedResult.isPresent()) {
			return cachedResult.get();
		}

		wmMetricRegistryFacade.meter("populateCacheableUserNotifications").mark();
		return userNotificationCache.putNotifications(userId, findAllCacheableUserNotifications(userId));
	}

	@Override
	public void archiveUserNotification(final String userNotificationUuid, final Long userId) {
		Assert.notNull(userNotificationUuid);
		Assert.notNull(userId);

		wmMetricRegistryFacade.meter("archiveUserNotification").mark();
		client.archive(userNotificationUuid, webRequestContextProvider.getRequestContext()).toBlocking().single();
	    userNotificationCache.clearUnreadNotificationInfo(userId);
	    userNotificationCache.clearNotifications(userId);
	}

	@Override
	public void setViewedAtNotificationAsync(Long userId, String startUuid, String endUuid) {
		if (!StringUtils.isBlank(startUuid) && !StringUtils.isBlank(endUuid)) {
			eventRouter.sendEvent(new MarkUserNotificationsAsReadEvent(userId, new UnreadNotificationsDTO(startUuid, endUuid)));
		}
	}

	@Override
	public void setViewedAtNotificationAsync(Long userId, UnreadNotificationsDTO unreadNotificationsDTO) {
		Assert.notNull(unreadNotificationsDTO);
		setViewedAtNotificationAsync(userId, unreadNotificationsDTO.getStartUuid(), unreadNotificationsDTO.getEndUuid());
	}

	@Override
	public void setViewedAtNotification(final Long userId, final UnreadNotificationsDTO unreadNotificationsDTO) {
		wmMetricRegistryFacade.meter("setViewedAtNotification").mark();
		client.setViewed(userId.toString(), unreadNotificationsDTO.getStartUuid(),
			unreadNotificationsDTO.getEndUuid(), webRequestContextProvider.getRequestContext()).toBlocking().single();
		userNotificationCache.clearUnreadNotificationInfo(userId);
		userNotificationCache.clearNotifications(userId);
	}

	@Override
	public NotificationType findNotificationTypeByCode(String code) {
		if (StringUtils.isBlank(code)) return null;
		return lookupEntityDAO.findByCode(NotificationType.class, code);
	}

	@Override
	public void onLaneAssociationCreated(LaneAssociation association) {
		User user = userService.getUser(association.getUser().getId());

		if (authenticationService.getEmailConfirmed(user)) {
			if (!association.getDeleted() & association.getApprovalStatus().isApproved()
					& (association.getLaneType().equals(LaneType.LANE_2) || association.getLaneType().equals(LaneType.LANE_3))) {
				Company company = profileService.findCompanyById(association.getCompany().getId());
				Assert.notNull(company);

				NotificationTemplate template = notificationTemplateFactory.buildLane23AssociationCreatedNotificationTemplate(user.getId(),
						company, false);
				notificationService.sendNotification(template);
			}
		}
	}

	@Override
	public void onConfirmAccount(User user, boolean sendWelcomeEmail) {
		Assert.notNull(user);

		// No on-boarding email for SSO users
		if (sendWelcomeEmail) {
			notificationService.sendNotification(notificationTemplateFactory.buildWelcomeNotificationTemplate(user));
		}

		/*
		 * Send all the emails related to Lane Associations. Include welcome email in the first one. If the user doesn't have any lane23 emails, send the stand-alone welcome email version.
		 */
		UserLaneRelationshipPagination pagination = new UserLaneRelationshipPagination(true);
		pagination = laneAssociationDAO.findAllUserLaneRelationships(user.getId(), pagination);
		for (UserLaneRelationship relationship : pagination.getResults()) {
			Company company = profileService.findCompanyById(relationship.getCompanyId());
			if (relationship.getApprovalStatus().equals(ApprovalStatus.APPROVED.ordinal())) {
				notificationService.sendNotification(notificationTemplateFactory.buildLane23AssociationCreatedNotificationTemplate(user.getId(), company, true));
			}
		}

		List<Invitation> invitations = invitationDAO.findInvitationsByStatus(user.getEmail(), new InvitationStatusType(
			InvitationStatusType.REGISTERED));

		for (Invitation invitation : invitations) {
			List<Request> requests = requestService.findRequestsByInvitation(invitation.getId());
			for (Request request : requests) {

				if (request instanceof UserGroupInvitation) {
					UserGroupInvitation groupInvitation = (UserGroupInvitation) request;
					UserGroup group = groupInvitation.getUserGroup();

					if (groupInvitation.getInvitedUser() == null)
						groupInvitation.setInvitedUser(user);

					if (group != null) {
						if (group.getOpenMembership()) {
							// Send Invitation Email
							onUserGroupInvitation(group, groupInvitation, groupInvitation.getInvitationType());
						} else {
							// Apply to the private group
							userGroupService.applyToGroup(group.getId(), user.getId());
						}
					}

				}
			}
		}

	}

	@Override
	public void onCompanyAccountLocked(Long companyId) {
		Assert.notNull(companyId);

		for (User u : userNotificationPrefsService.findUsersByCompanyAndNotificationType(companyId, NotificationType.LOCKED_INVOICE_DUE_REMINDER_MY_ACCOUNT)) {
			Calendar notificationTime = DateUtilities.getCalendarWithTime(10, 15, u.getProfile().getTimeZone().getTimeZoneId());
			NotificationTemplate template = notificationTemplateFactory.buildLockedCompanyAccountNotificationTemplate(u.getId(), summaryService.getPaymentCenterAggregateSummaryForBuyer(u.getId()).getPastDue());
			template.setTimeZoneId(u.getProfile().getTimeZone().getTimeZoneId());
			notificationService.sendNotification(template, notificationTime);
		}
		Company company = companyDAO.findCompanyById(companyId);

		// Alert client services
		LockedAccountClientServiceAlert alert = new LockedAccountClientServiceAlert();
		alert.setCompany(company);
		alert.setDescription("Company account has been locked");
		clientServiceAlertService.saveOrUpdate(alert);
		// create client service alert END

		try {
			notificationDispatcher.dispatchEmail(emailTemplateFactory.buildLockedCompanyAccountClientServicesEmailTemplate(company));
		} catch (Exception e) {
			logger.error("Error sending email to CSR", new Exception());
		}
	}

	@Override
	public void onCompanyAccountLockedOverdueWarning(Set<Long> companyIds, Integer daysSinceOverdue) {
		Integer daysTillSuspension = Constants.LOCKED_ACCOUNT_WINDOW_DAYS - daysSinceOverdue;

		for (Long companyId : companyIds) {
			logger.debug("****** Sending warning email to company account " + companyId);
			Company company = companyDAO.findById(companyId);
			BigDecimal pastDue = BigDecimal.ZERO;
			Set<User> allUsersSubscribedToPastDueInvoice = userNotificationPrefsService.findUsersByCompanyAndNotificationType(companyId, NotificationType.INVOICE_DUE_REMINDER_MY_ACCOUNT);
			if (!allUsersSubscribedToPastDueInvoice.isEmpty()) {
				pastDue = summaryService.getPaymentCenterAggregateSummaryForBuyer(allUsersSubscribedToPastDueInvoice.iterator().next().getId()).getPastDue();
			}

			// In case an unknown error had occurred and the overdueAccountWarningSentOn was not set the day of the overdue,
			// it will be set here to the date it should have been set.
			Calendar overdueAccountWarningSentOn = DateUtilities.getCalendarNow();
			overdueAccountWarningSentOn.add(Calendar.DAY_OF_MONTH, -daysSinceOverdue);
			company.setOverdueAccountWarningSentOn(overdueAccountWarningSentOn);

			companyDAO.saveOrUpdate(company);
			authenticationService.refreshSessionForCompany(companyId);

			for (User u : allUsersSubscribedToPastDueInvoice) {
				Calendar notificationTime = DateUtilities.getCalendarWithTime(10, 15, u.getProfile().getTimeZone().getTimeZoneId());
				NotificationTemplate template = notificationTemplateFactory.buildLockedCompanyAccountOverdueWarningEmailTemplate(u.getId(), daysSinceOverdue, daysTillSuspension, pastDue);
				template.setTimeZoneId(u.getProfile().getTimeZone().getTimeZoneId());
				notificationService.sendNotification(template, notificationTime);
			}
		}
	}

	@Override
	public void onLowBalanceAlert(Long companyId, Calendar scheduleDate) {
		Company company = companyDAO.get(companyId);

		BigDecimal availableCash = accountRegisterServicePrefundImpl.calcAvailableCashByCompany(company.getId());
		BigDecimal spendLimit = accountRegisterServicePrefundImpl.calcSufficientBuyerFundsByCompany(company.getId());
		BigDecimal lowBalanceAmount = company.getLowBalanceAmount();

		if (company.getCustomLowBalanceFlag() && (lowBalanceAmount != null) && (availableCash.compareTo(lowBalanceAmount) != 1 && !companyAlertService.isLowBalanceAlertSentToday(companyId))) {
			logger.info("****** Sending low balance alert to company id: " + companyId);
			companyAlertService.setLowBalanceAlertSentToday(companyId);
			Set<User> users = authenticationService.findAllAdminAndControllerUsersByCompanyId(company.getId());
			for (User user : users) {
				eventRouter.sendEvent(eventFactory.buildSendLowBalanceAlertEvent(user.getId(), user.getEmail(), spendLimit, scheduleDate));
			}
		}
	}


	@Override
	public void onWorkAccepted(Long workId, Long workResourceUserId) {
		Assert.notNull(workId);

		Work work = workService.findWork(workId);
		WorkResource resource = workService.findActiveWorkResource(workId);

		User workResourceUser = resource.getUser();
		// Bring last negotiation that is approved for the work and the resource
		WorkNegotiation negotiation = workNegotiationService.findLatestApprovedByCompanyForWork(workResourceUser.getCompany().getId(), work.getId());

		boolean isAssignmentBundle = workBundleService.isAssignmentBundle(work);

		if (isAssignmentBundle) {
			NotificationTemplate template = notificationTemplateFactory.buildWorkBundleAcceptedNotificationTemplate(work.getBuyer().getId(), work, workResourceUser);
			notificationService.sendNotification(template);
		} else if (!work.isInBundle()) {
			NotificationTemplate template = notificationTemplateFactory.buildWorkAcceptedNotificationTemplate(work.getBuyer().getId(), work, workResourceUser, negotiation);
			notificationService.sendNotification(template);
		}

		if (!isAssignmentBundle) {
			List<WorkFollow> followers = workFollowService.getWorkFollowers(workId);
			for (WorkFollow follower : followers) {
				AbstractWorkNotificationTemplate followerTemplate = notificationTemplateFactory.buildWorkAcceptedNotificationTemplate(
					follower.getUser().getId(), work, workResourceUser, negotiation
				);
				followerTemplate.setWorkFollow(follower);
				notificationService.sendNotification(followerTemplate);
			}
			setupPreWorkResourceNotifications(work, resource);
		}
	}

	@Override
	public void onWorkInvitation(Long workId, List<Long> userResourceIds, boolean voiceDelivery) {
		Work work = workService.findWork(workId, false);
		Assert.notNull(work);

		if (workId != null && isNotEmpty(userResourceIds)) {
			for (Long userId : userResourceIds) {
				onWorkInvitation(workId, userId, voiceDelivery);
			}
		}
	}

	private void onWorkInvitation(long workId, Long toUserId, boolean voiceDelivery) {
		NotificationTemplate template = notificationTemplateFactory.buildWorkResourceInvitation(workId, toUserId, voiceDelivery);
		if (template != null) {
			notificationService.sendNotification(template, buildDeliveryTime());
		}
	}

	private static Calendar buildDeliveryTime() {
		Calendar deliveryTime = Calendar.getInstance();
		deliveryTime.setTimeInMillis(deliveryTime.getTimeInMillis() + BULK_SEND_INVITATION_DELAY_ON_MILLIS);
		return deliveryTime;
	}

	@Override
	public void onWorkInvitationForVendor(Long workId, Long companyId) {
		Work work = workService.findWork(workId, false);
		Assert.notNull(work);

		for (Long dispatcherId : workResourceService.getAllDispatcherIdsInCompany(companyId)) {
			NotificationTemplate template = notificationTemplateFactory.buildWorkResourceInvitation(workId, dispatcherId, false);
			if (template != null) {
				template.setCompanyNotification(true);
				notificationService.sendNotification(template, buildDeliveryTime());
			}
		}
	}

	@Override
	public void onWorkDeclined(Work work, WorkResource resource) {
		loadLazyProperties(work);

		notificationService.sendNotification(
			notificationTemplateFactory.buildWorkDeclinedNotificationTemplate(work.getBuyer().getId(), work, resource.getUser())
		);

		List<WorkFollow> followers = workFollowService.getWorkFollowers(work.getId());
		for (WorkFollow follower : followers) {
			AbstractWorkNotificationTemplate followerTemplate = notificationTemplateFactory.buildWorkDeclinedNotificationTemplate(follower.getUser().getId(), work, resource.getUser());
			followerTemplate.setWorkFollow(follower);
			notificationService.sendNotification(followerTemplate);
		}
	}

	@Override
	public void onWorkReinvited(Work work, List<WorkResource> resources) {
		loadLazyProperties(work);

		for (WorkResource resource : resources) {
			Long workerId = resource.getUser().getId();
			NotificationTemplate template = notificationTemplateFactory.buildWorkReinvitedNotificationTemplate(resource.getUser().getId(), work);
			template.setVoiceEnabled(false);
			notificationService.sendNotification(template);

		 	Long dispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), workerId);
			if (dispatcherId != null) {
				NotificationTemplate forDispatcher = notificationTemplateFactory.buildWorkReinvitedNotificationTemplate(dispatcherId, work);
				forDispatcher.setVoiceEnabled(false);
				forDispatcher.setOnBehalfOfId(workerId);
				notificationService.sendNotification(forDispatcher);
			}
		}
	}

	/**
	 * Setup scheduled events around assignment checkin and confirmation. In both cases, the worker first receives a reminder of the required action,
	 * and after a certain grace period if the action has not been executed the assignment enters an exception state, alerting everyone involved.
	 */
	public void setupPreWorkResourceNotifications(Work work, WorkResource workResource) {
		Calendar confirmationRequiredDate = workService.calculateRequiredConfirmationNotificationDate(work);
		loadLazyProperties(work);
		User worker = workResource.getUser();

		if (confirmationRequiredDate.isSet(Calendar.DAY_OF_MONTH)) {

			NotificationTemplate confirmationTemplate = notificationTemplateFactory.buildWorkResourceConfirmationNotificationTemplate(
				worker.getId(), work, workService.getAppointmentTime(work)
			);

			Long dispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(workResource.getWork().getId(), worker.getId());
			if (dispatcherId != null) {
				WorkResourceConfirmationNotificationTemplate dispatcherTemplate = notificationTemplateFactory.buildWorkResourceConfirmationNotificationTemplate(
					dispatcherId, work, workService.getAppointmentTime(work)
				);
				dispatcherTemplate.setOnBehalfOfId(worker.getId());
				notificationService.sendNotification(dispatcherTemplate);
			}


			confirmationTemplate.setVoiceEnabled(work.isIvrActive());
			notificationService.sendNotification(confirmationTemplate, confirmationRequiredDate);
			logger.debug("[confirmation date] " + DateUtilities.formatDateForEmail(confirmationRequiredDate));

			Calendar alertDate = workService.calculateRequiredConfirmationDate(work);
			logger.debug("[confirmation alert date] " + DateUtilities.formatDateForEmail(alertDate));

			ResourceConfirmationRequiredScheduledEvent event = eventFactory.buildResourceConfirmationRequiredScheduledEvent(work, alertDate);
			event.setUser(worker);
			eventRouter.sendEvent(event);
		}

		Calendar checkInRequiredDate = workService.calculateRequiredCheckinDate(work);
		if (checkInRequiredDate.isSet(Calendar.DAY_OF_MONTH)) {
			if (DateUtilities.isInFuture(checkInRequiredDate)) {
				ValidateResourceCheckInScheduledEvent checkInEvent = eventFactory.buildValidateResourceCheckInScheduledEvent(work, checkInRequiredDate);
				checkInEvent.setUser(worker);
				eventRouter.sendEvent(checkInEvent);
			}

			Calendar notificationDate = workService.calculateRequiredCheckinReminderDate(work);
			NotificationTemplate checkinTemplate = notificationTemplateFactory.buildWorkResourceCheckInNotificationTemplate(worker.getId(), work);
			notificationService.sendNotification(checkinTemplate, notificationDate);

			Long dispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(workResource.getWork().getId(), worker.getId());
			if (dispatcherId != null) {
				WorkResourceCheckInNotificationTemplate dispatcherTemplate = notificationTemplateFactory.buildWorkResourceCheckInNotificationTemplate(dispatcherId, work);
				dispatcherTemplate.setOnBehalfOfId(worker.getId());
				notificationService.sendNotification(dispatcherTemplate);
			}

			//If check-in is required, also send an event to check for the workResource being late
			DateRange assignmentAppointmentDate = getAssignmentAppointmentDate(work, workResource);
			Calendar lateResourceLabelScheduledDate = (assignmentAppointmentDate.isRange()) ? assignmentAppointmentDate.getThrough() : assignmentAppointmentDate.getFrom();
			lateResourceLabelScheduledDate.add(Calendar.MINUTE, Constants.WORK_RESOURCE_LATE_LABEL_GRACE_PERIOD_MINUTES);

			WorkResourceLateLabelScheduledEvent workResourceLateLabelScheduledEvent = eventFactory.buildWorkResourceLateLabelScheduledEvent(
				workResource.getId(), lateResourceLabelScheduledDate
			);

			workResourceLateLabelScheduledEvent.setUser(worker);

			eventRouter.sendEvent(workResourceLateLabelScheduledEvent);

		}
	}

	public DateRange getAssignmentAppointmentDate(Work work, WorkResource workResource) {
		return DateRangeUtilities.getAppointmentTime(work.getSchedule(), workResource.getAppointment());
	}

	@Override
	public void onUserApprovedToGroup(UserUserGroupAssociation userUserGroupAssociation) {
		Assert.notNull(userUserGroupAssociation);
		Assert.notNull(userUserGroupAssociation.getUserGroup());
		UserGroup group = userGroupService.findGroupById(userUserGroupAssociation.getUserGroup().getId());
		if (BooleanUtils.isFalse(group.getOpenMembership())) {
			return;
		}
		User user = userUserGroupAssociation.getUser();

		NotificationTemplate template = notificationTemplateFactory.buildUserGroupApprovalNotificationTemplate(authenticationService.getCurrentUser().getId(),
			user.getId(), group);
		notificationService.sendNotification(template);

	}

	@Override
	public void onUserDeclinedForGroup(UserUserGroupAssociation userUserGroupAssociation) {
		Assert.notNull(userUserGroupAssociation);
		Assert.notNull(userUserGroupAssociation.getUserGroup());
		UserGroup group = userGroupService.findGroupById(userUserGroupAssociation.getUserGroup().getId());
		if (BooleanUtils.isFalse(group.getOpenMembership())) {
			return;
		}
		NotificationTemplate template = notificationTemplateFactory.buildUserGroupDeclineNotificationTemplate(authenticationService.getCurrentUser().getId(),
			userUserGroupAssociation.getUser().getId(), group);
		notificationService.sendNotification(template);
	}

	@Override
	public void onUserGroupApplication(UserUserGroupAssociation userUserGroupAssociation) {
		Assert.notNull(userUserGroupAssociation);
		Assert.notNull(userUserGroupAssociation.getUserGroup());

		UserGroup group = userGroupService.findGroupById(userUserGroupAssociation.getUserGroup().getId());

		if (group.isAutoGenerated()) {
			return;
		}

		if (group.getOpenMembership()) {
			NotificationTemplate template = notificationTemplateFactory.buildUserGroupApplicationNotificationTemplate(
				group.getOwner().getId(),
				group,
				userUserGroupAssociation.getUser(),
				userUserGroupAssociation.getVerificationStatus() == VerificationStatus.FAILED
			);
			notificationService.sendNotification(template);
		} else {
			// For a private group we send notification to the owner
			if (userUserGroupAssociation.getApprovalStatus().isApproved()) {
				NotificationTemplate template = notificationTemplateFactory.buildUserGroupPrivateApplicationNotificationTemplate(
						group.getOwner().getId(), group, userUserGroupAssociation.getUser()
				);
				notificationService.sendNotification(template);
			}
		}
	}

	@Override
	public void onUserGroupInvitation(UserGroup group, Request request, UserGroupInvitationType userGroupInvitationType) {
		Assert.notNull(group);
		NotificationTemplate template = null;
		if (!group.getOpenMembership()) {
			return;
		}

		switch (userGroupInvitationType) {
			case NEW:
				template = notificationTemplateFactory.buildUserGroupInvitationNotificationTemplate(request.getRequestor().getId(), request
						.getInvitedUser().getId(), group, false);
				break;
			case CRITERIA_MODIFICATION:
				template = notificationTemplateFactory.buildUserGroupRequirementsModificationNotificationTemplate(request.getRequestor()
						.getId(), request.getInvitedUser().getId(), group);
				break;
			case PROFILE_MODIFICATION:
				template = notificationTemplateFactory.buildUserGroupInvitationForUserProfileModificationNotificationTemplate(request
						.getRequestor().getId(), request.getInvitedUser().getId(), group);

				NotificationTemplate templateToOwner = notificationTemplateFactory
						.buildUserGroupInvitationForUserProfileModificationOwnerNotificationTemplate(request.getRequestor().getId(), group,
								request.getInvitedUser());
				notificationService.sendNotification(templateToOwner);
				break;
			case TERMS_AND_AGREEMENTS_MODIFICATION:
				template = notificationTemplateFactory.buildUserGroupRequirementsModificationNotificationTemplate(request.getRequestor()
						.getId(), request.getInvitedUser().getId(), group);
				break;
			case EXPIRATION:
				template = notificationTemplateFactory.buildUserGroupRequirementsExpirationNotificationTemplate(
						request.getRequestor().getId(), request.getInvitedUser().getId(), group);
				break;
		}

		if (template != null) {
			notificationService.sendNotification(template);
		}
	}

	@Override
	public void onUserGroupInvitations(Long groupId, Long requester, List<Long> invitedUserIds) {
		Assert.notNull(groupId);
		Assert.notNull(requester);
		Assert.notNull(invitedUserIds);

		UserGroup group = userGroupService.findGroupById(groupId);
		if (group == null) {
			logger.error("Failed to resolve group " + groupId + " unable to send notifications!");
			return;
		}

		if (!group.getOpenMembership()) {
			logger.warn("Group " + groupId + " is a private group, invitations will not be sent!");
			return;
		}

		List<NotificationTemplate> notifications = new ArrayList<>(invitedUserIds.size());
		for (Long invitedUserId : invitedUserIds) {
			notifications.add(
				notificationTemplateFactory.buildUserGroupInvitationNotificationTemplate(requester, invitedUserId, group, false)
			);
		}
		notificationService.sendNotifications(notifications);
	}

	@Override
	public void onUserGroupToVendorsInvitation(final Long groupId, final Long requester, final List<Long> invitedCompanyIds) {
		Assert.notNull(groupId);
		Assert.notNull(requester);
		Assert.notNull(invitedCompanyIds);

		final UserGroup group = userGroupService.findGroupById(groupId);
		if (group == null) {
			logger.error("Failed to resolve group " + groupId + " unable to send notifications!");
			return;
		}

		if (!group.getOpenMembership()) {
			logger.warn("Group " + groupId + " is a private group, invitations will not be sent!");
			return;
		}

		final List<NotificationTemplate> notifications = Lists.newArrayList();
		for (Long userId : getAllTeamAgentsToNotify(invitedCompanyIds)) {
			notifications.add(
				notificationTemplateFactory.buildUserGroupInvitationNotificationTemplate(requester, userId, group, true)
			);
		}
		notificationService.sendNotifications(notifications);
	}

	private List<Long> getAllTeamAgentsToNotify(List<Long> companyIds) {
		ArrayList<Long> userIds = Lists.newArrayList();
		for (Long companyId : companyIds) {
			List<User> users =
				authenticationService.findAllUsersByACLRoleAndCompany(companyId, AclRole.ACL_DISPATCHER);
			userIds.addAll(extract(users, on(User.class).getId()));
		}
		return userIds;
	}

	@Override
	public void onDeactivateInactiveUserGroups(List<Long> groupIds) {
		List<UserGroup> groups = userGroupDAO.findUserGroupsByIds(groupIds);
		List<NotificationTemplate> notifications = new ArrayList<>(groupIds.size());

		for (UserGroup group : groups) {
			if (group == null) {
				String ids = Arrays.toString(groupIds.toArray());
				String message = String.format("Failed to resolve group from among group IDs %s, unable to send notification!", ids);
				logger.error(message);
				continue;
			}

			notifications.add(new UserGroupInactiveDeactivatedNotificationTemplate(group.getOwner().getId(), group));
		}

		notificationDispatcher.dispatchNotifications(notifications);
	}


	@Override
	public void onRatingCreated(Rating rating) {
		if (rating.isRatingSharedFlag()) {
			rating.getRatingCompany();
			NotificationTemplate template = notificationTemplateFactory.buildWorkRatingCreatedNotificationTemplate(
				rating.getRatedUser().getId(), rating
			);
			notificationService.sendNotification(template);

			if (rating.isBuyerRating()) {
				List<WorkFollow> followers = workFollowService.getWorkFollowers(rating.getWork().getId());
				for (WorkFollow follower : followers) {
					AbstractWorkNotificationTemplate followerTemplate = notificationTemplateFactory.buildWorkRatingCreatedNotificationTemplate(
						follower.getUser().getId(), rating
					);
					followerTemplate.setWorkFollow(follower);
					notificationService.sendNotification(followerTemplate);
				}
			} else {
				Long dispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(rating.getWork().getId(), rating.getRatedUser().getId());
				if (dispatcherId != null) {
					NotificationTemplate forDispatcher = notificationTemplateFactory.buildWorkRatingCreatedNotificationTemplate(
						dispatcherId, rating
					);
					forDispatcher.setOnBehalfOfId(rating.getRatedUser().getId());
					notificationService.sendNotification(forDispatcher);
				}
			}
		}
	}

	@Override
	public void onWorkCreated(Work work) {
		if (work != null && !work.isPartOfBulk()) {
			NotificationTemplate template = notificationTemplateFactory.buildWorkCreatedNotificationTemplate(work.getBuyer().getId(), work);
			notificationService.sendNotification(template);

			List<WorkFollow> followers = workFollowService.getWorkFollowers(work.getId());
			for (WorkFollow follower : followers) {
				AbstractWorkNotificationTemplate followerTemplate = notificationTemplateFactory.buildWorkCreatedNotificationTemplate(follower.getUser().getId(), work);
				followerTemplate.setWorkFollow(follower);
				notificationService.sendNotification(followerTemplate);
			}
		}
	}

	@Override
	public void onWorkCreated(Long workId) {
		Work work = workService.findWork(workId);
		loadLazyProperties(work);
		onWorkCreated(work);
	}

	@Override
	public void onWorkUpdated(Long workId, Map<PropertyChangeType, List<PropertyChange>> propertyChanges) {

		Work work = workService.findWork(workId);
		if (work == null) {
			return;
		}
		loadLazyProperties(work);
		// Only notify under the following conditions:
		// * Assignment is in "sent" status
		// * Either the pricing or scheduling has changed

		if (work.isSent()) {
			if (!(propertyChanges.containsKey(WorkPropertyChangeType.PRICING) || propertyChanges.containsKey(WorkPropertyChangeType.SCHEDULE))) {
				return;
			}

			WorkUpdatedEvent event = eventFactory.buildWorkUpdatedEvent(workId, propertyChanges);
			event.setUser(authenticationService.getCurrentUser());
			eventRouter.sendEvent(event);
		}

		if (work.isActive() && propertyChanges.containsKey(WorkPropertyChangeType.SCHEDULE)) {
			setupPreWorkResourceNotifications(work, workService.findActiveWorkResource(work.getId()));
		}
	}

	@Override
	public void onProfileModificationApproved(Long userId, List<String> description) {
		ApproveProfileModificationNotificationTemplate template = notificationTemplateFactory.buildApproveProfileModificationNotificationTemplate(userId, description);
		notificationService.sendNotification(template);
	}

	@Override
	public <T extends Screening> void onScreeningResponse(T screening) {
		NotificationTemplate template = null;

		Long userId = screening.getUser().getId();
		String status = screening.getScreeningStatusType().getCode();

		if (screening instanceof BackgroundCheck) {
			template = (status.equals(ScreeningStatusType.PASSED)) ?
					notificationTemplateFactory.buildBackgroundCheckPassedNotificationTemplate(userId, screening) :
					notificationTemplateFactory.buildBackgroundCheckFailedNotificationTemplate(userId, screening);
		}

		if (screening instanceof DrugTest) {
			template = (status.equals(ScreeningStatusType.PASSED)) ?
					notificationTemplateFactory.buildDrugTestPassedNotificationTemplate(userId, screening) :
					notificationTemplateFactory.buildDrugTestFailedNotificationTemplate(userId, screening);
		}

		if (template != null)
			notificationService.sendNotification(template);
	}

	@Override
	public void onDrugTestRequest(ScreeningAndUser screening) {

		ClientServiceAlert alert = new ClientServiceAlert();
		alert.setCompany(screening.getUser().getCompany());
		alert.setDescription("Drug test requested for " + screening.getUser().getFullName());
		clientServiceAlertService.saveOrUpdate(alert);
	}

	@Override
	public void onTimedAssessmentAttemptStarted(Attempt attempt) {
		AbstractAssessment assessment = attempt.getAssessmentUserAssociation().getAssessment();

		Assert.state(assessment.getConfiguration().isTimed(), "Only timed assessments can be schedule for auto-complete.");

		Calendar timeout = DateUtilities.getCalendarNow();
		DateUtilities.addMinutes(timeout, assessment.getConfiguration().getDurationMinutes());
		ScheduledEvent event = eventFactory.buildTimedAssessmentAttemptAutoCompleteScheduledEvent(attempt, timeout);
		eventRouter.sendEvent(event);
	}

	@Override
	public void onAssignmentSurveyCompleted(WorkScopedAttempt attempt) {
		AbstractAssessment assessment = attempt.getAssessmentUserAssociation().getAssessment();
		User user = attempt.getAssessmentUserAssociation().getUser();

		boolean sendEmail = false;

		Map<String, AssessmentNotificationPreference> notificationTypes = Maps.newHashMap();

		if (!assessment.getConfiguration().getNotifications().isEmpty()) {
			for (AssessmentNotificationPreference p : assessment.getConfiguration().getNotifications())
				notificationTypes.put(p.getNotificationType().getCode(), p);

			sendEmail = notificationTypes.containsKey(NotificationType.ASSESSMENT_ATTEMPT_COMPLETED);
		}

		Set<Long> recipientIds = new HashSet<>();

		for (User recipient : assessment.getConfiguration().getNotificationRecipients()) {
			recipientIds.add(recipient.getId());
			NotificationTemplate template =
					notificationTemplateFactory.buildWorkSurveyCompletedNotificationTemplate(user.getId(), recipient.getId(), attempt, attempt.getWork());
			template.setEmailEnabled(sendEmail);
			notificationService.sendNotification(template);
		}

		List<WorkFollow> followers = workFollowService.getWorkFollowers(attempt.getWork().getId());
		for (WorkFollow follower : followers) {
			// avoid sending duplicates
			if (!recipientIds.add(follower.getUser().getId()))
				continue;

			AbstractWorkNotificationTemplate followerTemplate = notificationTemplateFactory.buildWorkSurveyCompletedNotificationTemplate(user.getId(), follower.getUser().getId(), attempt, attempt.getWork());
			followerTemplate.setWorkFollow(follower);
			notificationService.sendNotification(followerTemplate);
		}
	}

	@Override
	public void onAssessmentCompleted(Attempt attempt) {

		AbstractAssessment assessment = attempt.getAssessmentUserAssociation().getAssessment();
		AssessmentConfiguration configuration = assessment.getConfiguration();
		User user = attempt.getAssessmentUserAssociation().getUser();

		if (configuration.getNotifications().isEmpty()) {
			return;
		}

		Set<User> notificationRecipients = configuration.getNotificationRecipients();
		// Creating new collection of recipients that has no mapping to the assessment_notification_user_association table.
		// We may add assessmentCreator to the recipients collection. We don't want addition to persist past this method call.

		User assessmentCreator = userService.findUserById(assessment.getCreatorId());

		Set<User> recipients = Sets.newHashSet(notificationRecipients);
		if (assessmentCreator != null) {
			recipients.add(assessmentCreator);
		}

		boolean sendEmail = false;
		boolean sendGradeReminder = false;

		Map<String, AssessmentNotificationPreference> notificationTypes = Maps.newHashMap();

		for (AssessmentNotificationPreference p : configuration.getNotifications()) {
			notificationTypes.put(p.getNotificationType().getCode(), p);
		}

		if (notificationTypes.containsKey(NotificationType.WORK_SURVEY_COMPLETED)
				&& requestService.userHasInvitationToAssessment(user.getId(), assessment.getId())) {
			sendEmail = true;
		} else if (notificationTypes.containsKey(NotificationType.ASSESSMENT_ATTEMPT_COMPLETED)) {
			sendEmail = true;
		} else if (notificationTypes.containsKey(NotificationType.ASSESSMENT_ATTEMPT_UNGRADED)) {
			sendGradeReminder = true;
		}

		AttemptStatusType attemptStatus = attempt.getStatus();
		for (User recipient : recipients) {
			NotificationTemplate template = null;
			recipient.getUserNumber();
			if (assessment.getType().equals(GradedAssessment.GRADED_ASSESSMENT_TYPE)) {
				if (attemptStatus.isGradePending()) {
					template = notificationTemplateFactory.buildAssessmentGradePendingNotificationTemplate(user.getId(), recipient.getId(), attempt);
				} else if (attemptStatus.isGraded() || attemptStatus.isComplete()) {
					template = notificationTemplateFactory.buildAssessmentCompletedNotificationTemplate(user.getId(), recipient.getId(), attempt, assessment);
				}
			} else if (assessment.getType().equals(SurveyAssessment.SURVEY_ASSESSMENT_TYPE)) {
				if (attemptStatus.isComplete()) {
					template = notificationTemplateFactory.buildSurveyCompletedNotificationTemplate(user.getId(), recipient.getId(), attempt);
				}
			}

			if (template == null)
				return;

			template.setEmailEnabled(sendEmail);
			notificationService.sendNotification(template);

			// If a grade is pending AND the assessment is configured to send a reminder for ungraded attempts...
			if (attemptStatus.isGradePending() && sendGradeReminder) {
				Calendar reminder = DateUtilities.getCalendarNow();
				reminder.add(Calendar.DAY_OF_YEAR, notificationTypes.get(NotificationType.ASSESSMENT_ATTEMPT_UNGRADED).getDays());
				template.setEmailEnabled(true);
				notificationService.sendNotification(template, reminder);
			}
		}
	}

	@Override
	public void onAssessmentGraded(Attempt attempt) {
		if (!attempt.isComplete()) {
			return;
		}

		AbstractAssessment assessment = attempt.getAssessmentUserAssociation().getAssessment();
		if (assessment.getType().equals(GradedAssessment.SURVEY_ASSESSMENT_TYPE)) {
			return;
		}

		User user = attempt.getAssessmentUserAssociation().getUser();
		NotificationTemplate notification = notificationTemplateFactory.buildAssessmentGradedNotificationTemplate(assessment.getCreatorId(), user.getId(), attempt);
		notificationService.sendNotification(notification);
	}

	@Override
	public void onWorkResourceConfirmed(Long workResourceId) {
		Assert.notNull(workResourceId);

		WorkResource resource = workService.findWorkResourceById(workResourceId);
		Assert.notNull(resource);

		Work work = resource.getWork();
		loadLazyProperties(work);
		NotificationTemplate template = notificationTemplateFactory.buildWorkResourceConfirmedNotificationTemplate(
			work.getBuyer().getId(), work, resource.getUser());
		notificationService.sendNotification(template);

		List<WorkFollow> followers = workFollowService.getWorkFollowers(work.getId());
		for (WorkFollow follower : followers) {
			AbstractWorkNotificationTemplate followerTemplate = notificationTemplateFactory.buildWorkResourceConfirmedNotificationTemplate(follower.getUser().getId(), work, resource.getUser());
			followerTemplate.setWorkFollow(follower);
			notificationService.sendNotification(followerTemplate);
		}

	}

	@Override
	public void onWorkIncomplete(Work work, String message) {
		Long activeWorkerId = workService.findActiveWorkerId(work.getId());
		if (activeWorkerId == null) { return; }

		notificationService.sendNotification(
			notificationTemplateFactory.buildWorkIncompleteNotificationTemplate(work, activeWorkerId, message)
		);

		Long dispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), activeWorkerId);
		if (dispatcherId != null) {
			NotificationTemplate forDispatcher = notificationTemplateFactory.buildWorkIncompleteNotificationTemplate(work, dispatcherId, message);
			forDispatcher.setOnBehalfOfId(activeWorkerId);
			notificationService.sendNotification(forDispatcher);
		}
	}

	@Override
	public void onWorkNegotiationRequested(AbstractWorkNegotiation negotiation) {
		Work work = negotiation.getWork();
		loadLazyProperties(work);

		Long buyerId = work.getBuyer().getId();
		Long requesterId = negotiation.getRequestedBy().getId();

		if (negotiation instanceof WorkNegotiation) {
			WorkNegotiation workNegotiation = (WorkNegotiation) negotiation;
			notificationService.sendNotification(
				notificationTemplateFactory.buildWorkNegotiationRequestedNotificationTemplate(
					buyerId, work, workNegotiation
				)
			);

			List<WorkFollow> followers = workFollowService.getWorkFollowers(work.getId());
			for (WorkFollow follower : followers) {
				AbstractWorkNotificationTemplate followerTemplate = notificationTemplateFactory.buildWorkNegotiationRequestedNotificationTemplate(
					follower.getUser().getId(), work, workNegotiation
				);
				followerTemplate.setWorkFollow(follower);
				notificationService.sendNotification(followerTemplate);
			}

			// Show in the history that the counteroffer expired.
			if (workNegotiation.hasExpirationDate()) {
				eventRouter.sendEvent(
					eventFactory.buildWorkNegotiationExpiredScheduledEvent(
						workNegotiation, workNegotiation.getExpiresOn()
					)
				);
			}
		} else if (negotiation instanceof WorkBudgetNegotiation) {

			NotificationTemplate template = null;

			// resource-initiated sends "requested" to buyer
			if (negotiation.isInitiatedByResource()) {
				template = notificationTemplateFactory.buildWorkBudgetNegotiationRequestedNotificationTemplate(
					buyerId, work, (WorkBudgetNegotiation) negotiation
				);

			// send a copy to the admin if it was created by another user in the company
			} else if (!requesterId.equals(buyerId)) {
				template = notificationTemplateFactory.buildWorkBudgetNegotiationAddedNotificationTemplate(
					buyerId, work, (WorkBudgetNegotiation) negotiation
				);
			}

			notificationService.sendNotification(template);

			List<WorkFollow> followers = workFollowService.getWorkFollowers(work.getId());
			for (WorkFollow follower : followers) {
				AbstractWorkNotificationTemplate followerTemplate = notificationTemplateFactory.buildWorkBudgetNegotiationRequestedNotificationTemplate(
					follower.getUser().getId(), work, (WorkBudgetNegotiation) negotiation
				);
				followerTemplate.setWorkFollow(follower);
				notificationService.sendNotification(followerTemplate);
			}

		} else if (negotiation instanceof WorkExpenseNegotiation) {

			NotificationTemplate template = null;

			// resource-initiated sends "requested" to buyer
			if (negotiation.isInitiatedByResource()) {
				template = notificationTemplateFactory.buildWorkExpenseNegotiationRequestedNotificationTemplate(
					buyerId, work, (WorkExpenseNegotiation) negotiation
				);

			// send a copy to the admin if it was created by another user in the company
			} else if (!requesterId.equals(buyerId)) {
				template = notificationTemplateFactory.buildWorkExpenseNegotiationAddedNotificationTemplate(
					buyerId, work, (WorkExpenseNegotiation) negotiation
				);
			}

			notificationService.sendNotification(template);

			List<WorkFollow> followers = workFollowService.getWorkFollowers(work.getId());
			for (WorkFollow follower : followers) {
				AbstractWorkNotificationTemplate followerTemplate = notificationTemplateFactory.buildWorkExpenseNegotiationRequestedNotificationTemplate(
					follower.getUser().getId(), work, (WorkExpenseNegotiation) negotiation
				);
				followerTemplate.setWorkFollow(follower);
				notificationService.sendNotification(followerTemplate);
			}

		} else if (negotiation instanceof WorkBonusNegotiation) {

			NotificationTemplate template = null;

			// resource-initiated sends "requested" to buyer
			if (negotiation.isInitiatedByResource()) {
				template = notificationTemplateFactory.buildWorkBonusNegotiationRequestedNotificationTemplate(
					buyerId, work, (WorkBonusNegotiation) negotiation
				);
			// send a copy to the admin if it was created by another user in the company
			} else if (!requesterId.equals(buyerId)) {
				template = notificationTemplateFactory.buildWorkBonusNegotiationAddedNotificationTemplate(
					buyerId, work, (WorkBonusNegotiation) negotiation
				);
			}

			notificationService.sendNotification(template);

			List<WorkFollow> followers = workFollowService.getWorkFollowers(work.getId());
			for (WorkFollow follower : followers) {
				AbstractWorkNotificationTemplate followerTemplate = notificationTemplateFactory.buildWorkBonusNegotiationRequestedNotificationTemplate(
					follower.getUser().getId(), work, (WorkBonusNegotiation) negotiation
				);
				followerTemplate.setWorkFollow(follower);
				notificationService.sendNotification(followerTemplate);
			}

		} else if (negotiation instanceof WorkRescheduleNegotiation) {
			// When the negotiation was initiated by the resource, the notification recipient is the buyer;
			// Otherwise assume an authorized admin initiated the negotiation w/the active resource.
			User recipient;
			if (negotiation.isInitiatedByResource()) {
				recipient = work.getBuyer();

				List<WorkFollow> followers = workFollowService.getWorkFollowers(work.getId());
				for (WorkFollow follower : followers) {
					AbstractWorkNotificationTemplate followerTemplate = notificationTemplateFactory.buildWorkRescheduleNegotiationRequestedNotificationTemplate(
						follower.getUser().getId(), work, (WorkRescheduleNegotiation) negotiation
					);
					followerTemplate.setWorkFollow(follower);
					notificationService.sendNotification(followerTemplate);
				}
			} else {
				WorkResource resource = workService.findActiveWorkResource(work.getId());
				Assert.notNull(resource);
				recipient = resource.getUser();
			}

			notificationService.sendNotification(
				notificationTemplateFactory.buildWorkRescheduleNegotiationRequestedNotificationTemplate(
					recipient.getId(), work, (WorkRescheduleNegotiation) negotiation
				)
			);
		}

	}

	@Override
	public void onWorkNegotiationApproved(AbstractWorkNegotiation negotiation) {
		Work work = negotiation.getWork();
		loadLazyProperties(work);

		Long acceptedRequestorId = negotiation.getRequestedBy().getId();
		Assert.notNull(acceptedRequestorId);

		Long activeWorkerId = workService.findActiveWorkerId(work.getId());
		Long acceptedRequestorDispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), acceptedRequestorId);

		Long activeWorkerDispatcherId = null;
		if (activeWorkerId != null) {
			activeWorkerDispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), activeWorkerId);
		}

		if (negotiation instanceof WorkNegotiation) {
			notificationService.sendNotification(
				notificationTemplateFactory.buildWorkNegotiationApprovedNotificationTemplate(
					acceptedRequestorId, work, (WorkNegotiation) negotiation
				)
			);

			if (acceptedRequestorDispatcherId != null) {
				WorkNegotiationApprovedNotificationTemplate forDispatcher = notificationTemplateFactory.buildWorkNegotiationApprovedNotificationTemplate(
					acceptedRequestorDispatcherId, work, (WorkNegotiation) negotiation
				);
				forDispatcher.setOnBehalfOfId(acceptedRequestorId);
				notificationService.sendNotification(forDispatcher);
			}

			// If the assignment was accepting applications, notify all those users
			// who had submit an application that was not accepted.

			if (!work.getManageMyWorkMarket().getAssignToFirstResource()) {
				WorkNegotiationPagination pagination = workNegotiationService.findByWork(work.getId(), makeWorkNegotiationPagination());

				for (AbstractWorkNegotiation n : pagination.getResults()) {
					if (!(n instanceof WorkNegotiation)) {
						continue;
					}

					Long rejectedRequestorId = n.getRequestedBy().getId();

					if (rejectedRequestorId.equals(acceptedRequestorId)) {
						continue;
					}

					notificationService.sendNotification(
						notificationTemplateFactory.buildWorkNotAvailableNotificationTemplate(rejectedRequestorId, work)
					);

					Long rejectedRequestorDispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), rejectedRequestorId);
					if (rejectedRequestorDispatcherId != null) {
						notificationService.sendNotification(
							notificationTemplateFactory.buildWorkNotAvailableNotificationTemplate(
								rejectedRequestorDispatcherId, rejectedRequestorId, work
							)
						);
					}
				}
			}

		} else if (negotiation instanceof WorkBudgetNegotiation) {
			Assert.notNull(activeWorkerId);

			NotificationTemplate template;
			NotificationTemplate forDispatcher = null;
			if (negotiation.isInitiatedByResource()) {
				template = notificationTemplateFactory.buildWorkBudgetNegotiationApprovedNotificationTemplate(
					activeWorkerId, work, (WorkBudgetNegotiation) negotiation
				);

				if (activeWorkerDispatcherId != null) {
					forDispatcher = notificationTemplateFactory.buildWorkBudgetNegotiationApprovedNotificationTemplate(
						activeWorkerDispatcherId, work, (WorkBudgetNegotiation) negotiation
					);
					forDispatcher.setOnBehalfOfId(activeWorkerId);
				}

			} else {
				template = notificationTemplateFactory.buildWorkBudgetNegotiationAddedNotificationTemplate(
					activeWorkerId, work, (WorkBudgetNegotiation) negotiation
				);

				if (activeWorkerDispatcherId != null) {
					forDispatcher = notificationTemplateFactory.buildWorkBudgetNegotiationAddedNotificationTemplate(
						activeWorkerDispatcherId, work, (WorkBudgetNegotiation) negotiation
					);
					forDispatcher.setOnBehalfOfId(activeWorkerId);
				}
			}
			notificationService.sendNotification(template);
			notificationService.sendNotification(forDispatcher);

		} else if (negotiation instanceof WorkExpenseNegotiation) {
			Assert.notNull(activeWorkerId);

			NotificationTemplate template;
			NotificationTemplate forDispatcher = null;
			if (negotiation.isInitiatedByResource()) {
				template = notificationTemplateFactory.buildWorkExpenseNegotiationApprovedNotificationTemplate(
					activeWorkerId, work, (WorkExpenseNegotiation) negotiation
				);

				if (activeWorkerDispatcherId != null) {
					forDispatcher = notificationTemplateFactory.buildWorkExpenseNegotiationApprovedNotificationTemplate(
						activeWorkerDispatcherId, work, (WorkExpenseNegotiation) negotiation
					);
					forDispatcher.setOnBehalfOfId(activeWorkerId);
				}

			} else {
				template = notificationTemplateFactory.buildWorkExpenseNegotiationAddedNotificationTemplate(
					activeWorkerId, work, (WorkExpenseNegotiation) negotiation
				);

				if (activeWorkerDispatcherId != null) {
					forDispatcher = notificationTemplateFactory.buildWorkExpenseNegotiationAddedNotificationTemplate(
						activeWorkerDispatcherId, work, (WorkExpenseNegotiation) negotiation
					);
					forDispatcher.setOnBehalfOfId(activeWorkerId);
				}
			}
			notificationService.sendNotification(template);
			notificationService.sendNotification(forDispatcher);

		} else if (negotiation instanceof WorkBonusNegotiation) {
			Assert.notNull(activeWorkerId);

			NotificationTemplate template;
			NotificationTemplate forDispatcher = null;
			if (negotiation.isInitiatedByResource()) {
				template = notificationTemplateFactory.buildWorkBonusNegotiationApprovedNotificationTemplate(
					activeWorkerId, work, (WorkBonusNegotiation) negotiation
				);

				if (activeWorkerDispatcherId != null) {
					forDispatcher = notificationTemplateFactory.buildWorkBonusNegotiationApprovedNotificationTemplate(
						activeWorkerDispatcherId, work, (WorkBonusNegotiation) negotiation
					);
					forDispatcher.setOnBehalfOfId(activeWorkerId);
				}

			} else {
				template = notificationTemplateFactory.buildWorkBonusNegotiationAddedNotificationTemplate(
					activeWorkerId, work, (WorkBonusNegotiation) negotiation
				);

				if (activeWorkerDispatcherId != null) {
					forDispatcher = notificationTemplateFactory.buildWorkBonusNegotiationAddedNotificationTemplate(
						activeWorkerDispatcherId, work, (WorkBonusNegotiation) negotiation
					);
					forDispatcher.setOnBehalfOfId(activeWorkerId);
				}
			}
			notificationService.sendNotification(template);
			notificationService.sendNotification(forDispatcher);

		} else if (negotiation instanceof WorkRescheduleNegotiation) {

			NotificationTemplate template;
			NotificationTemplate forDispatcher = null;
			if (negotiation.getRequestedBy().getId().equals(negotiation.getModifierId())) {

				// On Behalf Of
				Long approverId = negotiation.getApprovedBy().getId();
				template = notificationTemplateFactory.buildWorkRescheduleNegotiationApprovedOnBehalfOfNotificationTemplate(
					approverId, work, (WorkRescheduleNegotiation) negotiation
				);

				Long approverDispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), approverId);

				if (approverDispatcherId != null) {
					forDispatcher = notificationTemplateFactory.buildWorkRescheduleNegotiationApprovedOnBehalfOfNotificationTemplate(
						approverDispatcherId, work, (WorkRescheduleNegotiation) negotiation
					);
					forDispatcher.setOnBehalfOfId(approverId);
				}

			} else {
				template = notificationTemplateFactory.buildWorkRescheduleNegotiationApprovedNotificationTemplate(
					acceptedRequestorId, work, (WorkRescheduleNegotiation) negotiation
				);

				if (acceptedRequestorDispatcherId != null) {
					forDispatcher = notificationTemplateFactory.buildWorkRescheduleNegotiationApprovedNotificationTemplate(
						acceptedRequestorDispatcherId, work, (WorkRescheduleNegotiation) negotiation
					);
					forDispatcher.setOnBehalfOfId(acceptedRequestorId);
				}
			}

			notificationService.sendNotification(template);
			notificationService.sendNotification(forDispatcher);
			setupPreWorkResourceNotifications(work, workService.findActiveWorkResource(work.getId()));
		}
	}

	@Override
	public void onExpirationNotificationsForBuyer(UserGroupExpiration expiration) {
		String ownerEmail = (expiration.getUserGroup() != null && expiration.getUserGroup().getOwner() != null) ?
				expiration.getUserGroup().getOwner().getEmail() : null;

		if (ownerEmail == null) {
			return;
		}

		EmailTemplate template = notificationTemplateFactory.buildExpirationNotificationTemplate(expiration);
		notificationService.sendNotification(template);
	}

	@Override
	public void onWorkAcceptedEvent(WorkAcceptedEvent event) {
		Assert.notNull(event);
		Assert.notNull(event.getResourceUserId());
		Assert.notNull(event.getWorkId());

		Work work = workService.findWork(event.getWorkId());
		Long activeWorkerId = workService.findActiveWorkerId(work.getId());

		if (activeWorkerId == null) {
			logger.error("[workAccepted] error finding active resource for work " + work.getId());
			return;
		}

		// Force fetch of buyer profile
		work.getBuyer().getProfile();

		AbstractWorkNotificationTemplate template;
		User worker = userService.findUserById(event.getResourceUserId());
		boolean isAssignmentBundle = workBundleService.isAssignmentBundle(work);
		boolean isMboAssignment = workOptionsService.hasOption(work, WorkOption.MBO_ENABLED, "true");
		Long dispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId());

		String outputFile = "";
		String outputFileForDispatcher = "";

		try {
			// Don't send accepted email for MBO assignment - MBO will send their own
			if (!isMboAssignment) {
				if (isAssignmentBundle) {
					ServiceResponseBuilder serviceResponse = workBundleService.getBundleData(worker.getId(), work.getId());
					template = notificationTemplateFactory.buildWorkBundleAcceptedDetailsNotificationTemplate(
						worker.getId(), work, worker, serviceResponse.getData()
					);
					notificationDispatcher.dispatchEmail(template.getEmailTemplate());

					if (dispatcherId != null) {
						WorkBundleAcceptedDetailsNotificationTemplate dispatcherTemplate = notificationTemplateFactory.buildWorkBundleAcceptedDetailsNotificationTemplate(
							dispatcherId, work, worker, serviceResponse.getData()
						);
						dispatcherTemplate.setOnBehalfOfId(worker.getId());
						notificationDispatcher.dispatchEmail(template.getEmailTemplate());
					}

				} else {
					template = notificationTemplateFactory.buildWorkAcceptedDetailsNotificationTemplate(worker.getId(), work, worker);
					NotificationEmailTemplate emailTemplate = template.getEmailTemplate();

					// Attach work order PDF
					if (work.isEnableAssignmentPrintout()) {
						FileDTO attachment = new FileDTO();
						buildWorkOrderPDF(attachment, event, work);
						emailTemplate.addAttachment(attachment);
					}

					notificationDispatcher.dispatchEmail(emailTemplate);

					if (dispatcherId != null) {
						template = notificationTemplateFactory.buildWorkAcceptedDetailsNotificationTemplate(
							dispatcherId, work, worker
						);
						template.setOnBehalfOfId(worker.getId());

						NotificationEmailTemplate emailTemplateForDispatcher = template.getEmailTemplate();

						// Attach work order PDF
						if (work.isEnableAssignmentPrintout()) {
							FileDTO attachment = new FileDTO();
							buildWorkOrderPDF(attachment, event, work);
							emailTemplate.addAttachment(attachment);
						}

						notificationDispatcher.dispatchEmail(emailTemplateForDispatcher);
					}
				}
			}
		} catch (Exception e) {
			logger.error("[WorkAccepted] Error sending work id: " + work.getId() + " to Email: " + worker.getEmail(), e);
		} finally {
			FileUtilities.deleteFile(outputFile);
			FileUtilities.deleteFile(outputFileForDispatcher);
		}
	}

	public String buildWorkOrderPDF(FileDTO attachment, WorkAcceptedEvent event, Work work) {
		String fileName = "Assignment_" + work.getWorkNumber() + Constants.PDF_EXTENSION;
		String outputFile = Constants.TEMPORARY_FILE_DIRECTORY + fileName;
		String assignmentHTML = event.getAssignmentHTML();
		PDFUtilities.createFromHtml(assignmentHTML, outputFile);

		attachment.setName(fileName);
		attachment.setMimeType(MimeType.PDF.getMimeType());
		attachment.setSourceFilePath(outputFile);

		return outputFile;
	}

	@Override
	public void onSubscriptionThroughputDifference(Collection<String> differences) {
		sendEmailToAccounting("Subscription Throughput Report for: ", differences, accountRegisterReconciliationEmailNotifications);
	}

	@Override
	public void onAccountRegisterReconciliationDifference(Collection<String> differences) {
		sendEmailToAccounting("Account Register Reconciliation Report for: ", differences, accountRegisterReconciliationEmailNotifications);
	}

	@Override
	public void onPaymentConfigurationReconciliationDifference(Collection<String> differences) {
		sendEmailToAccounting("Payment Configuration Reconciliation Report for: ", differences, accountRegisterReconciliationEmailNotifications);
	}

	@Override
	public void onNextThroughputResetDateDifference(Collection<String> differences) {
		//sendEmailToAccounting("Next Throughput Reset Date Reconciliation Report for: ", differences, accountRegisterReconciliationEmailNotifications);
	}

	@Override
	public void sendWorkNotifyInvitations(Long workId, List<Long> usersToNotify) {
		if (workId != null && isNotEmpty(usersToNotify)) {
			List<Long> userIdsWithSms = workResourceDAO.findAllResourceUserIdsForWorkWithSmsAllowed(workId, NotificationType.RESOURCE_WORK_INVITED);
			List<Long> userIdsWithPush = workResourceDAO.findAllResourceUserIdsForWorkWithPushAllowed(workId, NotificationType.RESOURCE_WORK_INVITED);

			if (isNotEmpty(userIdsWithSms) || isNotEmpty(userIdsWithPush)) {
				for (Long userId : usersToNotify) {
					sendWorkNotifyInvitation(workId, userId, userIdsWithSms, userIdsWithPush, BULK_SEND_INVITATION_DELAY_ON_MILLIS);
				}
			}
		}
	}

	private void sendWorkNotifyInvitation(Long workId, Long userId, List<Long> userIdsWithSms, List<Long> userIdsWithPush, long bulkDelayInMillis) {
		Work work = workService.findWork(workId, true);
		Assert.notNull(work);

		NotificationTemplate template = notificationTemplateFactory.buildWorkResourceInvitation(workId, userId, false);

		if (template != null) {
			Calendar deliveryTime = Calendar.getInstance();
			if (bulkDelayInMillis > 0) {
				deliveryTime.setTimeInMillis(deliveryTime.getTimeInMillis() + bulkDelayInMillis);
			}
			notificationService.sendWorkNotifyAsync(template, userIdsWithSms, userIdsWithPush, deliveryTime);
		}
	}

	void sendEmailToAccounting(String title, Collection<String> differences, String toEmail) {
		logger.info("title " + title);
		logger.info("differences " + differences);
		logger.info("toEmail " + toEmail);
		if (isNotBlank(toEmail) && isNotBlank(title)) {
			simpleDateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
			String date = simpleDateFormat.format(new Date());

			EMailDTO emailDTO = new EMailDTO();
			emailDTO.setFromId(Constants.WORKMARKET_SYSTEM_USER_ID);
			emailDTO.setDescription(title + date);
			emailDTO.setSubject(title + date + ".");
			emailDTO.setText(title + date + "<br>" + StringUtils.join(differences, "<br>"));
			emailDTO.setNotificationType("Reconciliation Report");

			logger.info("Sending email to " + toEmail);

			String emailsA[] = StringUtils.split(toEmail, ";");
			if (ArrayUtils.isNotEmpty(emailsA) && StringUtils.isNotBlank(emailsA[0])) {
				emailDTO.setToEmail(emailsA[0]);
				emailDTO.setCcEmails(emailsA);
				emailService.sendEmail(emailDTO);
			}
		}
	}

	@Override
	public void onWorkBundleNegotiationApproved(AbstractWorkNegotiation negotiation) {
		Work work = negotiation.getWork();
		loadLazyProperties(work);

		Long acceptedRequestorId = negotiation.getRequestedBy().getId();

		// Only supporting apply to bundles right now
		if (negotiation instanceof WorkNegotiation) {
			ServiceResponseBuilder response = workBundleService.getBundleData(acceptedRequestorId, work.getId());

			notificationService.sendNotification(
				notificationTemplateFactory.buildWorkBundleNegotiationApprovedNotificationTemplate(
					acceptedRequestorId, work, (WorkNegotiation) negotiation, response.getData()
				)
			);

			Long dispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), acceptedRequestorId);
			if (dispatcherId != null) {
				notificationService.sendNotification(
					notificationTemplateFactory.buildWorkBundleNegotiationApprovedNotificationTemplate(
						dispatcherId, acceptedRequestorId, work, (WorkNegotiation) negotiation, response.getData()
					)
				);
			}

			// If the assignment was accepting applications, notify all those users
			// who had submit an application that was not accepted.

			if (!work.getManageMyWorkMarket().getAssignToFirstResource()) {
				WorkNegotiationPagination pagination = workNegotiationService.findByWork(work.getId(), makeWorkNegotiationPagination());

				for (AbstractWorkNegotiation n : pagination.getResults()) {
					if (!(n instanceof WorkNegotiation)) {
						continue;
					}

					Long requestorId = n.getRequestedBy().getId();

					if (requestorId.equals(acceptedRequestorId)) {
						continue;
					}

					notificationService.sendNotification(
						notificationTemplateFactory.buildWorkNotAvailableNotificationTemplate(requestorId, work)
					);

					dispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), requestorId);
					if (dispatcherId != null) {
						notificationService.sendNotification(
							notificationTemplateFactory.buildWorkNotAvailableNotificationTemplate(
								dispatcherId, requestorId, work
							)
						);
					}
				}
			}
		}
	}

	public WorkNegotiationPagination makeWorkNegotiationPagination() {
		WorkNegotiationPagination pagination = new WorkNegotiationPagination(true);
		pagination.addFilter(WorkNegotiationPagination.FILTER_KEYS.APPROVAL_STATUS, ApprovalStatus.PENDING);
		return pagination;
	}

	@Override
	public void onBundleWorkAcceptFailed(Long buyerId, User worker, Work work, AcceptWorkResponse failure) {
		NotificationTemplate template = notificationTemplateFactory.buildBundleWorkAcceptFailedNotificationTemplate(buyerId, worker, work, failure);
		notificationService.sendNotification(template);
	}

	@Override
	public void onWorkNegotiationDeclined(AbstractWorkNegotiation negotiation) {
		Work work = negotiation.getWork();
		loadLazyProperties(work);
		sendNegotiationDeclinedTemplateToWorker(negotiation, work);
		sendNegotiationDeclinedTemplateToDispatcher(negotiation, work);
	}

	private void sendNegotiationDeclinedTemplateToWorker(AbstractWorkNegotiation negotiation, Work work) {
		Long requesterId = negotiation.getRequestedBy().getId();
		NotificationTemplate workerTemplate = notificationTemplateFactory.buildAbstractWorkNegotiationDeclinedNotificationTemplate(requesterId, work, negotiation);
		notificationService.sendNotification(workerTemplate);
	}

	private void sendNegotiationDeclinedTemplateToDispatcher(AbstractWorkNegotiation negotiation, Work work) {
		Long requesterId = negotiation.getRequestedBy().getId();
		Long dispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), requesterId);
		if (dispatcherId == null) {
			return;
		}
		NotificationTemplate template = notificationTemplateFactory.buildAbstractWorkNegotiationDeclinedNotificationTemplate(dispatcherId, work, negotiation);
		template.setOnBehalfOfId(requesterId);
		notificationService.sendNotification(template);
	}

	@Override
	public void onWorkNegotiationExpirationExtended(WorkNegotiation negotiation) {
		Work work = negotiation.getWork();
		loadLazyProperties(work);
		notificationService.sendNotification(
			notificationTemplateFactory.buildWorkNegotiationExpirationExtendedNotificationTemplate(
				negotiation.getWork().getBuyer().getId(), work, negotiation
			)
		);

		List<WorkFollow> followers = workFollowService.getWorkFollowers(work.getId());
		for (WorkFollow follower : followers) {
			AbstractWorkNotificationTemplate followerTemplate = notificationTemplateFactory.buildWorkNegotiationExpirationExtendedNotificationTemplate(
				follower.getUser().getId(), work, negotiation
			);
			followerTemplate.setWorkFollow(follower);
			notificationService.sendNotification(followerTemplate);
		}
	}

	@Override
	public void onWorkCompleted(long workId, boolean isCompleteOnBehalf) {
		Work work = workService.findWork(workId, true);
		WorkResource resource = workService.findActiveWorkResource(work.getId());
		Assert.notNull(resource);

		// For now, no notification that a bundle is complete
		if (work.isWorkBundle()) { return; }

		if(!workService.isOfflinePayment(work)) {
			PaymentSummaryDTO payment = paymentSummaryService.generatePaymentSummaryForWork(work);
			Long workerId = resource.getUser().getId();

			if (!isCompleteOnBehalf) {
				// Don't notify the client if they closed the assignment on behalf of the resource
				NotificationTemplate template = notificationTemplateFactory.buildWorkCompleteNotificationTemplate(
					workerId, work.getBuyer().getId(), work, payment
				);
				notificationService.sendNotification(template);

				List<WorkFollow> followers = workFollowService.getWorkFollowers(work.getId());
				for (WorkFollow follower : followers) {
					AbstractWorkNotificationTemplate followerTemplate = notificationTemplateFactory.buildWorkCompleteNotificationTemplate(
						workerId, follower.getUser().getId(), work, payment
					);
					followerTemplate.setWorkFollow(follower);
					notificationService.sendNotification(followerTemplate);
				}
			} else {
				boolean fastFundsEnabled = companyService.isFastFundsEnabled(work.getCompany().getId());
				// Send notification to the worker
				notificationService.sendNotification(
					notificationTemplateFactory.buildWorkCompletedByBuyerNotificationTemplate(
						work.getBuyer().getId(), workerId, work, fastFundsEnabled
					)
				);

				Long dispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), workerId);
				if (dispatcherId != null) {
					NotificationTemplate forDispatcher = notificationTemplateFactory.buildWorkCompletedByBuyerNotificationTemplate(
						work.getBuyer().getId(), dispatcherId, work, fastFundsEnabled
					);
					forDispatcher.setOnBehalfOfId(workerId);
					notificationService.sendNotification(forDispatcher);
				}
			}
		}

		// Auto close settings
		Company company = work.getCompany();
		if (company.getManageMyWorkMarket().getAutocloseEnabledFlag()) {
			WorkMilestones milestones = workMilestonesDAO.findWorkMilestonesByWorkId(work.getId());
			Calendar completeDate = (Calendar) milestones.getCompleteOn().clone();

			completeDate.add(Calendar.HOUR_OF_DAY, company.getManageMyWorkMarket().getAutocloseDelayInHours());
			eventRouter.sendEvent(eventFactory.buildWorkAutoCloseScheduledEvent(work, completeDate));
		}
	}

	@Override
	public void onDeliverableLate(WorkResource resource) {
		Assert.notNull(resource);
		// Need to reload workResource, this is a new session
		WorkResource workResource = workService.findWorkResourceById(resource.getId());

		if (workResource != null) {
			Work work = workResource.getWork();
			User worker = workResource.getUser();

			workSubStatusService.addSystemSubStatus(worker, work.getId(), WorkSubStatusType.DELIVERABLE_LATE);
			workResourceService.addLabelToWorkResource(new WorkResourceLabelDTO(resource.getId(), WorkResourceLabelType.LATE_DELIVERABLE));

			notificationService.sendNotification(
				notificationTemplateFactory.buildWorkDeliverableLateNotificationTemplate(worker.getId(), work)
			);

			Long dispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId());
			if (dispatcherId != null) {
				NotificationTemplate forDispatcher = notificationTemplateFactory.buildWorkDeliverableLateNotificationTemplate(dispatcherId, work);
				forDispatcher.setOnBehalfOfId(worker.getId());
				notificationService.sendNotification(forDispatcher);
			}
		}
	}

	@Override
	public void onDeliverableDueReminder(WorkResource resource) {
		Assert.notNull(resource);
		// Need to reload workResource, this is a new session
		WorkResource workResource = workService.findWorkResourceById(resource.getId());

		if (workResource != null) {
			Long workerId = workResource.getUser().getId();

			WorkDeliverableDueReminderNotificationTemplate template = notificationTemplateFactory.buildWorkDeliverableDueReminderNotificationTemplate(
				workerId, workResource.getWork()
			);
			notificationService.sendNotification(template);

			Long dispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(workResource.getWork().getId(), workerId);
			if (dispatcherId != null) {
				WorkDeliverableDueReminderNotificationTemplate dispatcherTemplate = notificationTemplateFactory.buildWorkDeliverableDueReminderNotificationTemplate(
					dispatcherId, workResource.getWork()
				);
				dispatcherTemplate.setOnBehalfOfId(workerId);
				notificationService.sendNotification(dispatcherTemplate);
			}
		}
	}

	@Override
	public void onWorkClosedAndPaid(Long workResourceId) {
		// Yeah, we intentionally don't message the resource for unpaid/internal work.
		WorkResource resource = workService.findWorkResourceById(workResourceId);
		if (resource != null) {
			Work work = resource.getWork();
			if (work != null && work.getFulfillmentStrategy() != null &&
				work.getFulfillmentStrategy().getAmountEarned() != null &&
				!workService.isOfflinePayment(work)) {
				loadLazyProperties(work);
				User worker = resource.getUser();
				AbstractTaxEntity taxEntity = taxService.findActiveTaxEntity(worker.getId());
				boolean hasValidTaxEntity = !(taxEntity == null
					|| !(taxEntity instanceof UsaTaxEntity && TaxVerificationStatusType.APPROVED.equals(taxEntity.getStatus().getCode())));

				notificationService.sendNotification(
					notificationTemplateFactory.buildWorkCompletedFundsAddedNotificationTemplate(
						worker.getId(), work, resource, hasValidTaxEntity
					)
				);

				Long dispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId());
				if (dispatcherId != null) {
					NotificationTemplate template = notificationTemplateFactory.buildWorkCompletedFundsAddedNotificationTemplate(
						dispatcherId, work, resource, hasValidTaxEntity
					);
					template.setOnBehalfOfId(worker.getId());
					notificationService.sendNotification(template);
				}
			}
		}
	}

	@Override
	public void onWorkSubStatus(Long workId, Long workResourceId, WorkSubStatusTypeAssociation association) {
		Work work = workService.findWork(workId);

		WorkResource activeResource = workService.findActiveWorkResource(workId);

		boolean hasNotificationBeenSent = sendEmailsAndAlerts(association.getWorkSubStatusType().getCode(), work, workResourceId);

		// notify the client
		if (!hasNotificationBeenSent && association.getWorkSubStatusType().isNotifyClientEnabled()) {
			sendWorkSubStatusAlert(work.getBuyer().getId(), work, association, activeResource);
		}

		// notify the worker
		if (activeResource != null && !activeResource.getUser().getId().equals(work.getBuyer().getId())) {
			if (association.getWorkSubStatusType().isNotifyResourceEnabled()) {
				sendWorkSubStatusAlert(activeResource.getUser().getId(), work, association, activeResource);
			}
		}

		//notify just the recipients
		if (!hasNotificationBeenSent && !association.getWorkSubStatusType().isNotifyClientEnabled()
			&& (!association.getWorkSubStatusType().isNotifyResourceEnabled() || activeResource == null)) {
				sendWorkSubStatusAlert(null, work, association, activeResource);
		}

		if (association.getWorkSubStatusType().isAlert()) {
			// create client service alert
			ClientServiceAlert alert = new ClientServiceAlert();
			alert.setCompany(work.getCompany());
			alert.setDescription("Exception on Assignment #" + work.getWorkNumber() + " " + association.getWorkSubStatusType().getDescription());
			clientServiceAlertService.saveOrUpdate(alert);
		}
	}

	@Override
	public void onWorkStopPayment(Long workId, String reason) {
		Assert.notNull(workId);
		WorkResource resource = workService.findActiveWorkResource(workId);

		if (resource != null) {
			Work work = resource.getWork();
			for (User controller : authenticationService.findAllUsersByACLRoleAndCompany(work.getCompany().getId(), AclRole.ACL_CONTROLLER)) {
				notificationService.sendNotification(notificationTemplateFactory.buildWorkStoppedPaymentNotificationTemplate(controller.getId(), work, reason));
			}
			Hibernate.initialize(work.getBuyer());
			Hibernate.initialize(work.getBuyer().getProfile());
			notificationService.sendNotification(notificationTemplateFactory.buildWorkStoppedPaymentNotificationTemplate(resource.getUser().getId(), work, reason));
		}

	}

	@Override
	public void onWorkAttachmentAdded(Work work, Asset asset) {
		if (!work.isActive() && !work.isComplete()) {
			return;
		}

		WorkResource resource = workService.findActiveWorkResource(work.getId());
		// In the case of an exception, there may not be a resource. Ignore the event.
		if (resource == null) {
			return;
		}

		// We were getting some hibernate session issues so we refresh the work object here
		work = workService.findWork(work.getId());

		loadLazyProperties(work);

		// Depending on who created the attachment, message the opposite party.
		Long toId;
		if (asset.getCreatorId().equals(resource.getUser().getId())) {
			toId = work.getBuyer().getId();

			List<WorkFollow> followers = workFollowService.getWorkFollowers(work.getId());
			for (WorkFollow follower : followers) {
				AbstractWorkNotificationTemplate followerTemplate = notificationTemplateFactory.buildWorkAttachmentAddedNotificationTemplate(
					follower.getUser().getId(), work, asset
				);
				followerTemplate.setWorkFollow(follower);
				notificationService.sendNotification(followerTemplate);
			}
		} else {
			toId = resource.getUser().getId();

			Long dispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), toId);
			if (dispatcherId != null) {
				NotificationTemplate dispatcherTemplate = notificationTemplateFactory.buildWorkAttachmentAddedNotificationTemplate(dispatcherId, work, asset);
				dispatcherTemplate.setOnBehalfOfId(toId);
				notificationService.sendNotification(dispatcherTemplate);
			}
		}

		notificationService.sendNotification(
			notificationTemplateFactory.buildWorkAttachmentAddedNotificationTemplate(toId, work, asset)
		);
	}

	@Override
	public void onDeliverableRequirementComplete(Work work) {
		Long workCreatorId = work.getCreatorId();
		if (workCreatorId != null && !workCreatorId.equals(authenticationService.getCurrentUser().getId())) {

			// We were getting some hibernate session issues so we refresh the work object here
			work = workService.findWork(work.getId());
			loadLazyProperties(work);

			NotificationTemplate template = notificationTemplateFactory.buildWorkDeliverableFulfilledNotificationTemplate(workCreatorId, work);
			notificationService.sendNotification(template);
		}
	}

	@Override
	public void onWorkUnassigned(WorkResource workResource, String messageForWorker) {
		Assert.notNull(workResource);
		User worker = workResource.getUser();
		Work work = workResource.getWork();

		Assert.notNull(worker);
		Long resourceId = worker.getId();

		NotificationTemplate template = notificationTemplateFactory.buildWorkUnassignedNotificationTemplate(resourceId, work, workResource, messageForWorker);
		notificationService.sendNotification(template);
	}

	@Override
	public void onWorkNoteAdded(WorkNote note) {
		Assert.notNull(note);
		Work work = workService.findWork(note.getWork().getId(), true);
		Assert.notNull(work);

		List<WorkContext> context = workService.getWorkContext(work.getId(), note.getCreatorId());

		// if active workers posts a private note then do nothing
		// if WM employee posts a private note then do nothing
		if (note.getIsPrivate() &&
				(context.contains(WorkContext.ACTIVE_RESOURCE) || context.contains(WorkContext.UNRELATED))) {
			return;
		}

		Set<Long> recipientIds = buildSet();

		boolean fromEmployee =
			!context.contains(WorkContext.ACTIVE_RESOURCE) &&
			context.contains(WorkContext.COMPANY_OWNED) &&
			note.getCreatorId() != work.getBuyer().getId();
		// active worker or WM employee posts a note
		if (context.contains(WorkContext.ACTIVE_RESOURCE) || context.contains(WorkContext.UNRELATED)) {
			recipientIds.add(work.getBuyer().getId());
		} else if (fromEmployee) {
			notificationService.sendNotification(
				notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
					work.getBuyer().getId(),
					work,
					note,
					NotificationType.WORK_NOTE_ADDED_BY_EMPLOYEE
				)
			);
		}

		// buyer, buyer employee, or WM employee posts a note
		if (!note.getIsPrivate() &&
				(context.contains(WorkContext.OWNER) || context.contains(WorkContext.COMPANY_OWNED) || context.contains(WorkContext.UNRELATED))) {

			Long workerId = workService.findActiveWorkerId(work.getId());

			if (workerId != null && note.getIsPublic() || (note.getIsPrivileged() && note.getReplyToId() != null && note.getReplyToId().equals(workerId))) {
				recipientIds.add(workerId);
				Long dispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(note.getWork().getId(), workerId);
				if (dispatcherId != null) {
					recipientIds.add(dispatcherId);
				}
			}
		}

		for (Long recipientId : recipientIds) {
			if (recipientId.equals(note.getCreatorId())) {
				continue;
			}
			notificationService.sendNotification(
				notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
					recipientId,
					work,
					note,
					NotificationType.WORK_NOTE_ADDED)
			);
		}

		List<WorkFollow> followers = workFollowService.getWorkFollowers(work.getId());
		for (WorkFollow follower : followers) {
			if (recipientIds.contains(follower.getUser().getId()) || follower.getUser().getId().equals(note.getCreatorId())) {
				continue;
			}

			AbstractWorkNotificationTemplate followerTemplate =
				notificationTemplateFactory.buildWorkNoteAddedNotificationTemplate(
					follower.getUser().getId(),
					work,
					note,
					fromEmployee ? NotificationType.WORK_NOTE_ADDED_BY_EMPLOYEE : NotificationType.WORK_NOTE_ADDED
				);
			followerTemplate.setWorkFollow(follower);
			notificationService.sendNotification(followerTemplate);
		}
	}

	public Set<Long> buildSet() {
		return new HashSet<>();
	}

	@Override
	public void onQuestionCreated(WorkQuestionAnswerPair workQuestionAnswerPair, Long workId) {
		Assert.notNull(workQuestionAnswerPair);
		Work work = workService.findWork(workId);

		NotificationTemplate template = notificationTemplateFactory.buildWorkQuestionNotificationTemplate(userService.getUser(work.getBuyer().getId()).getId(), work, workQuestionAnswerPair);
		notificationService.sendNotification(template);

		for (WorkFollow follower : workFollowService.getWorkFollowers(workId)) {
			AbstractWorkNotificationTemplate followerTemplate = notificationTemplateFactory.buildWorkQuestionNotificationTemplate(follower.getUser().getId(), work, workQuestionAnswerPair);
			followerTemplate.setWorkFollow(follower);
			notificationService.sendNotification(followerTemplate);
		}
	}

	@Override
	public void onQuestionAnswered(WorkQuestionAnswerPair workQuestionAnswerPair, Long workId) {
		Assert.notNull(workQuestionAnswerPair);

		Work work = workService.findWork(workId);
		loadLazyProperties(work);
		notificationService.sendNotification(
			notificationTemplateFactory.buildWorkQuestionAnsweredNotificationTemplate(
				workQuestionAnswerPair.getQuestionerId(), work, workQuestionAnswerPair
			)
		);

		Long dispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), workQuestionAnswerPair.getQuestionerId());
		if (dispatcherId != null) {
			NotificationTemplate forDispatcher = notificationTemplateFactory.buildWorkQuestionAnsweredNotificationTemplate(
				dispatcherId, work, workQuestionAnswerPair
			);
			forDispatcher.setOnBehalfOfId(workQuestionAnswerPair.getQuestionerId());
			notificationService.sendNotification(forDispatcher);
		}
	}

	@Override
	public void onWorkRemindResourceToComplete(Work work, User worker, Note note) {
		loadLazyProperties(work);
		notificationService.sendNotification(
			notificationTemplateFactory.buildWorkRemindResourceToComplete(worker.getId(), note.getCreatorId(), work, note)
		);

		Long dispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId());
		if (dispatcherId != null) {
			NotificationTemplate forDispatcher = notificationTemplateFactory.buildWorkRemindResourceToComplete(
				dispatcherId, note.getCreatorId(), work, note
			);
			forDispatcher.setOnBehalfOfId(worker.getId());
			notificationService.sendNotification(forDispatcher);
		}
	}

	@Override
	public void onCreditCardTransaction(CreditCardTransaction creditCardTransaction, User user) {
		UserNotificationPreferencePojo userNotificationPreference = userNotificationPrefsService.findByUserAndNotificationType(
				user.getId(),
				NotificationType.MONEY_CREDIT_CARD_RECEIPT);

		if (userNotificationPreference.getEmailFlag()) {
			NotificationTemplate template = notificationTemplateFactory.buildCreditCardReceiptNotificationTemplate(
					user.getId(), creditCardTransaction);
			// attach receipt
			template.setPdfTemplate(PDFTemplateFactory.creditCardReceiptPDFTemplate(creditCardTransaction));
			notificationService.sendNotification(template);
		}
	}

	@Override
	public void onCreditTransaction(RegisterTransaction transaction) {
		Assert.notNull(transaction);
		Long companyId = transaction.getAccountRegister().getCompany().getId();

		InvoicePagination pagination = new InvoicePagination();
		pagination.setStartRow(0);
		pagination.setResultsLimit(1);
		pagination.addFilter(InvoicePagination.FILTER_KEYS.INVOICE_STATUS, InvoiceStatusType.PAYMENT_PENDING);
		pagination = invoiceDAO.findAllByCompanyId(companyId, pagination);

		BigDecimal totalBalance = pagination.getTotalBalance();
		Integer invoiceCount = pagination.getRowCount();

		AbstractInvoice earliestDueInvoice = invoiceDAO.findEarliestDueInvoice(companyId);
		Calendar earliestDue = null;
		if (earliestDueInvoice != null) {
			earliestDue = DateUtilities.cloneCalendar(earliestDueInvoice.getDueDate());
		}

		for (User u : userNotificationPrefsService.findUsersByCompanyAndNotificationType(companyId, NotificationType.MONEY_DEPOSITED)) {
			NotificationTemplate template = notificationTemplateFactory.buildFundsDepositNotificationTemplate(u.getId(), transaction, invoiceCount, totalBalance, earliestDue);
			if (u.getProfile() != null && u.getProfile().getTimeZone() != null) {
				template.setTimeZoneId(u.getProfile().getTimeZone().getTimeZoneId());
			}
			notificationService.sendNotification(template);
		}
	}

	@Override
	public void onDebitTransaction(RegisterTransaction transaction) {
		Assert.notNull(transaction);
		if (transaction.getRegisterTransactionType().getCode().equals(RegisterTransactionType.DEBIT_ACH_DEPOSIT_RETURN)) {
			for (User u : userNotificationPrefsService.findUsersByCompanyAndNotificationType(transaction.getAccountRegister().getCompany().getId(), NotificationType.MONEY_WITHDRAWN)) {
				NotificationTemplate template = notificationTemplateFactory.buildFundsDepositReturnNotificationTemplate(u.getId(), transaction);
				if (u.getProfile() != null && u.getProfile().getTimeZone() != null) {
					template.setTimeZoneId(u.getProfile().getTimeZone().getTimeZoneId());
				}
				notificationService.sendNotification(template);
			}
		}
	}

	@Override
	public void onFundsWithdrawn(BankAccountTransaction transaction) {
		Assert.notNull(transaction);
		long companyId = transaction.getAccountRegister().getCompany().getId();


		for (User u : userNotificationPrefsService.findUsersByCompanyAndNotificationType(companyId, NotificationType.MONEY_WITHDRAWN)) {
			NotificationTemplate template = notificationTemplateFactory.buildFundsWithdrawnNotificationTemplate(u.getId(), transaction, transaction.getBankAccount());
			if (u.getProfile() != null && u.getProfile().getTimeZone() != null) {
				template.setTimeZoneId(u.getProfile().getTimeZone().getTimeZoneId());
			}
			notificationService.sendNotification(template);
		}
	}

	@Override
	public void onFundsProcessed(Long transactionId) {
		Assert.notNull(transactionId);
		BankAccountTransaction transaction = (BankAccountTransaction) registerTransactionDAO.get(transactionId);
		Assert.notNull(transaction);
		long companyId = transaction.getAccountRegister().getCompany().getId();

		for (User controller : authenticationService.findAllUsersByACLRoleAndCompany(companyId, AclRole.ACL_CONTROLLER)) {
			notificationService.sendNotification(notificationTemplateFactory.buildFundsProcessedNotificationTemplate(controller.getId(), transaction));
		}
	}


	@Override
	public void sendInvoiceDueReminders() {
		// We need the assignments past due or going to be due to warn the users.
		Set<WorkDue> dueAssignments = workService.findAllAssignmentsPastDue(Calendar.getInstance());
		logger.debug("****** Found " + dueAssignments.size() + " assignments due.");

		Calendar midnight72HoursFromNow = DateUtilities.getMidnightToday();
		midnight72HoursFromNow.add(Calendar.DATE, 3);

		logger.debug("****** midnight72HoursFromNow " + DateUtilities.formatDateForEmail(midnight72HoursFromNow));
		logger.debug("****** getMidnightToday " + DateUtilities.formatDateForEmail(DateUtilities.getMidnightToday()));

		Set<WorkDue> assignmentsToBeDue = workService.findAllDueAssignmentsByDueDate(DateUtilities.getMidnightToday(), midnight72HoursFromNow);
		logger.debug("****** Found " + assignmentsToBeDue.size() + " assignments to be due.");

		dueAssignments.addAll(assignmentsToBeDue);

		final Map<Long, Set<Invoice>> invoicesByCompany = Maps.newHashMap();
		final Map<Long, Map<Long, WorkDue>> companyAssignmentsByInvoice = Maps.newHashMap();
		final Map<Invoice, Map<User, WorkDue>> allInvoices = Maps.newHashMap();

		for (WorkDue workDue : dueAssignments) {
			if (workDue.getInvoiceId() != null) {
				boolean notify = true;
				/*
				 * Only notify companies with statements turned OFF
				 * unless they had pending payment assignments at the time
				 * of turning statements ON.
				 */
				if (workDue.isStatementsEnabled()) {
					/*
					 * If the company has statements enabled, check the date of the invoice creation
					 * and compare it with the the payment cycle start date to determine if the invoice
					 * will be part of the next statement or not.
					 * If the invoice won't be part of the statement, notify the user.
					 */
					Company company = companyService.findCompanyById(workDue.getCompanyId());
					PaymentConfiguration paymentConfiguration = company.getPaymentConfiguration();
					Invoice invoice = invoiceDAO.findInvoiceById(workDue.getInvoiceId());
					if (invoice.getCreatedOn().after(paymentConfiguration.getStartDatePaymentCycle())) {
						notify = false;
					}
				}

				if (notify) {
					Invoice invoice = invoiceDAO.findInvoiceById(workDue.getInvoiceId());
					if (invoice.getBalance().compareTo(BigDecimal.ZERO) > 0) {
						// Invoices due notifications by company
						if (!invoicesByCompany.containsKey(workDue.getCompanyId())) {
							invoicesByCompany.put(workDue.getCompanyId(), Sets.newHashSet(invoice));
						} else {
							invoicesByCompany.get(workDue.getCompanyId()).add(invoice);
						}

						if (!companyAssignmentsByInvoice.containsKey(workDue.getCompanyId())) {
							companyAssignmentsByInvoice.put(workDue.getCompanyId(), CollectionUtilities.<Long, WorkDue>newTypedObjectMap(invoice.getId(), workDue));
						} else {
							companyAssignmentsByInvoice.get(workDue.getCompanyId()).put(invoice.getId(), workDue);
						}

						// get all invoices due for single invoice notification
						User buyer = userService.findUserById(workDue.getBuyerUserId());
						allInvoices.put(invoice, CollectionUtilities.<User, WorkDue>newTypedObjectMap(buyer, workDue));
					}
				}
			} else {
				logger.error("Assignment without invoice " + workDue.getWorkId());
			}
		}

		for (Map.Entry<Long, Set<Invoice>> entry : invoicesByCompany.entrySet()) {
			onInvoiceDueReminderNotifyUsersSubscribedInCompany(entry.getKey(), entry.getValue(), companyAssignmentsByInvoice.get(entry.getKey()));
		}

		for (Map.Entry<Invoice, Map<User, WorkDue>> entry : allInvoices.entrySet()) {
			onInvoiceDueReminderNotifyOwnerSubsribedUsers(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void on24HourInvoiceDueWarnings(Map<Long, Calendar> companyIds, Map<Long, Long> comingDueInvoices) {
		for (Map.Entry<Long, Calendar> entry : companyIds.entrySet()) {
			Long companyId = entry.getKey();
			logger.debug("****** Sending warning email to company account " + companyId);

			/* Set the LockAccountWarningSentOn to 24 hours before the closest assignment is due
			 * This is to properly trigger and show the LockAccountWarning banners */
			Calendar savedNotificationTime = (Calendar) entry.getValue().clone();
			savedNotificationTime.add(Calendar.DAY_OF_MONTH, -1);

			Company company = companyDAO.findById(companyId);
			company.setLockAccountWarningSentOn(savedNotificationTime);

			companyDAO.saveOrUpdate(company);
			authenticationService.refreshSessionForCompany(companyId);

			for (User user : userNotificationPrefsService.findUsersByCompanyAndNotificationType(companyId, NotificationType.INVOICE_DUE_24_HOURS)) {
				Calendar dueOn = DateUtilities.changeTimeZone(entry.getValue(), user.getProfile().getTimeZone().getTimeZoneId());

				/* Schedule the notification to be sent exactly the day before the assignment is due at 10:15 */
				Calendar notificationTime = (Calendar) dueOn.clone();
				notificationTime.add(Calendar.DAY_OF_MONTH, -1);
				notificationTime.set(Calendar.HOUR_OF_DAY, 10);
				notificationTime.set(Calendar.MINUTE, 15);

				NotificationTemplate template = notificationTemplateFactory.buildLockedCompanyAccount24HrsWarningNotificationTemplate(user.getId(), summaryService.getTotalUpcomingDueIn24Hours(user.getId()));
				template.setTimeZoneId(user.getProfile().getTimeZone().getTimeZoneId());
				notificationService.sendNotification(template, notificationTime);
			}
		}

		for (Map.Entry<Long, Long> invoiceData : comingDueInvoices.entrySet()) {
			if (authenticationService.isUserNotificationPreferenceEmailTypeEnabled(invoiceData.getKey(), NotificationType.MY_INVOICES_DUE_24_HOURS)) {
				User user = userService.findUserById(invoiceData.getKey());
				Invoice invoice = invoiceDAO.findInvoiceById(invoiceData.getValue());
				Calendar dueOn = DateUtilities.changeTimeZone(DateUtilities.cloneCalendar(invoice.getDueDate()), user.getProfile().getTimeZone().getTimeZoneId());

				/* Schedule the notification to be sent exactly the day before the assignment is due at 10:15 */
				Calendar notificationTime = (Calendar) dueOn.clone();
				notificationTime.add(Calendar.DAY_OF_MONTH, -1);
				notificationTime.set(Calendar.HOUR_OF_DAY, 10);
				notificationTime.set(Calendar.MINUTE, 15);

				NotificationTemplate template = notificationTemplateFactory.buildInvoiceDue24HoursNotificationTemplate(user.getId(), invoice.getRemainingBalance());
				template.setTimeZoneId(user.getProfile().getTimeZone().getTimeZoneId());
				notificationService.sendNotification(template, notificationTime);
			}
		}
	}

	private void onInvoiceDueReminderNotifyUsersSubscribedInCompany(Long companyId, Set<Invoice> invoices, Map<Long, WorkDue> invoiceAssignments) {
		for (User user : userNotificationPrefsService.findUsersByCompanyAndNotificationType(companyId, NotificationType.INVOICE_DUE_3_DAYS)) {
			String timeZoneId = user.getProfile().getTimeZone().getTimeZoneId();
			Calendar notificationTime = DateUtilities.getCalendarWithTime(8, 0, timeZoneId);
			NotificationTemplate template = notificationTemplateFactory.buildInvoiceDueNotificationTemplate(user.getId(), Invoice.ORDERING_BY_DUE_DATE.sortedCopy(invoices), invoiceAssignments);
			template.setTimeZoneId(timeZoneId);
			notificationService.sendNotification(template, notificationTime);
		}
	}

	private void onInvoiceDueReminderNotifyOwnerSubsribedUsers(Invoice individualInvoice,  Map<User, WorkDue> allInvoices) {
		for (Map.Entry<User, WorkDue> workInfo : allInvoices.entrySet()) {
			if (authenticationService.isUserNotificationPreferenceEmailTypeEnabled(workInfo.getKey().getId(), NotificationType.MY_INVOICES_DUE_3_DAYS)) {
				Map<Long, WorkDue> workAssignment = Maps.newHashMapWithExpectedSize(1);
				workAssignment.put(individualInvoice.getId(), workInfo.getValue());
				Set<Invoice> invoiceTable = Sets.newHashSetWithExpectedSize(1);
				invoiceTable.add(individualInvoice);
				String timeZoneId = workInfo.getKey().getProfile().getTimeZone().getTimeZoneId();
				Calendar notificationTime = DateUtilities.getCalendarWithTime(8, 0, timeZoneId);
				NotificationTemplate template = notificationTemplateFactory.buildOwnerInvoiceDueNotificationTemplate(workInfo.getKey().getId(), Invoice.ORDERING_BY_DUE_DATE.sortedCopy(invoiceTable), workAssignment);
				template.setTimeZoneId(timeZoneId);
				notificationService.sendNotification(template, notificationTime);
			}
		}
	}

	@Override
	public void onNewStatement(long statementId) {
		Statement statement = billingService.findStatementById(statementId);
		onNewInvoice(statement);
	}

	@Override
	public void onFailedStatement(Map<Long, Exception> exceptionMap) {
		for (Map.Entry<Long, Exception> exceptionEntry : exceptionMap.entrySet()) {
			onFailedStatement(exceptionEntry.getKey(), exceptionEntry.getValue());
		}
	}

	private void onFailedStatement(long companyId, Exception e) {
		EMailDTO eMailDTO = new EMailDTO();
		eMailDTO.setFromId(Constants.WORKMARKET_SYSTEM_USER_ID);
		eMailDTO.setDescription("Generate Statement Exception");
		eMailDTO.setSubject("Generate Statement Exception for Company " + companyId);
		eMailDTO.setText(ExceptionUtils.getFullStackTrace(e));
		eMailDTO.setNotificationType("System Email");
		eMailDTO.setToEmail(Constants.DEV_TEAM_EMAIL);
		emailService.sendEmail(eMailDTO);
	}

	@Override
	public void onWorkAppointmentSet(Long workId) {
		Assert.notNull(workId);
		Work work = workService.findWork(workId);
		Assert.notNull(work);
		loadLazyProperties(work);
		WorkResource workResource = workService.findActiveWorkResource(workId);

		DateRange appointment = workService.getAppointmentTime(work);

		notificationService.sendNotification(
			notificationTemplateFactory.buildWorkAppointmentNotificationTemplate(
				work.getBuyer().getId(), work, appointment
			)
		);

		List<WorkFollow> followers = workFollowService.getWorkFollowers(work.getId());
		for (WorkFollow follower : followers) {
			AbstractWorkNotificationTemplate followerTemplate = notificationTemplateFactory.buildWorkAppointmentNotificationTemplate(
				follower.getUser().getId(), work, appointment
			);
			followerTemplate.setWorkFollow(follower);
			notificationService.sendNotification(followerTemplate);
		}

		Long workerId = workResource.getUser().getId();


		notificationService.sendNotification(
			notificationTemplateFactory.buildWorkAppointmentNotificationTemplate(
				workerId, work, appointment
			)
		);

		Long dispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), workerId);
		if (dispatcherId != null) {
			notificationService.sendNotification(
				notificationTemplateFactory.buildWorkAppointmentNotificationTemplate(
					dispatcherId, workerId, work, appointment
				)
			);
		}

		setupPreWorkResourceNotifications(work, workResource);
	}

	@Override
	public void onOverridePaymentTerms(Company company, String note) {
		List<Long> uids = Lists.newArrayList(Constants.JEFF_WALD_USER_ID);

		for (Long uid : uids) {
			NotificationTemplate template = notificationTemplateFactory.buildOverridePaymentTermsNotificationTemplate(uid, company, note);
			template.setEnabledDeliveryMethods(true, false, false, false, false);
			notificationService.sendNotification(template);
		}
	}

	@Override
	public void onTalentPoolRequirementExpiration(Criterion criterion, String verb) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
		TalentPoolRequirementExpirationEmailTemplate template =
			notificationTemplateFactory.buildTalentPoolRequirementExpirationNotificationTemplate(
				criterion,
				(UserGroup) criterion.getRequirementSetable(),
				formatter.format(criterion.getExpirationDate().getTime()),
				verb);
		notificationService.sendNotification(template);
	}

	@Override
	public void onAssetBundleAvailable(User user, Asset asset) throws HostServiceException {
		Calendar expiration = DateUtilities.getCalendarNow();
		expiration.add(Calendar.HOUR, Constants.ASSET_BUNDLE_EXPIRATION_HOURS);
		String downloadUri = assetManagementService.getAuthorizedDownloadUriById(asset.getId(), expiration);
		Event event = eventFactory.buildAssetBundleExpirationEvent(asset.getUUID(), expiration);
		eventRouter.sendEvent(event);
		NotificationTemplate template = notificationTemplateFactory.buildAssetBundleAvailableNotificationTemplate(user.getId(), downloadUri, expiration);
		notificationService.sendNotification(template);
	}

	@Override
	public void onNewCompany(User user) {
		if (user.getCompany().getName() != null) {
			// Check if the new user's company is similar to one already in the system
			List<Company> companies = companyDAO.findSimilarCompaniesByName(user.getCompany().getId(), user.getCompany().getName());

			if (companies.size() > 0) {
				StringBuilder description = new StringBuilder("User " + user.getFullName() + " #" + user.getId() + " has created a new company similar to: \n");
				for (Company c : companies) {
					description.append("Company #").append(c.getId()).append(" ").append(c.getName()).append("\n");
				}

				ClientServiceAlert alert = new ClientServiceAlert();
				alert.setCompany(user.getCompany());
				alert.setDescription(description.toString());
				clientServiceAlertService.saveOrUpdate(alert);
			}
		}
	}

	@Override
	public void onLargeWorkReportGenerated(String reportKey, String reportName, Set<String> recipients, Asset asset) throws HostServiceException {

		Calendar expiration = DateUtilities.getCalendarNow();
		expiration.add(Calendar.HOUR, Constants.WORK_REPORT_CSV_EXPIRATION_HOURS);
		String downloadUri = assetService.getAuthorizedDownloadUriById(asset.getId(), expiration);
		WorkReportGeneratedLargeEmailTemplate template = notificationTemplateFactory.buildLargeWorkReportGeneratedTemplate(reportKey, reportName, recipients, downloadUri);

		// expire the asset - if/when we implement historical report download, remove/tweak this
		Event expirationEvent = eventFactory.buildAssetExpirationEvent(asset.getUUID(), expiration);
		eventRouter.sendEvent(expirationEvent);
		notificationDispatcher.dispatchEmail(template);
	}

	@Override
	public void onWorkReportGenerated(String reportKey, String reportName, Set<String> recipients, Asset asset, String filename, ReportResponse response, Long reportId) {
		AssetDTO dto = AssetDTO.newDTO(asset);
		dto.setSourceFilePath(filename);
		WorkReportGeneratedEmailTemplate template = notificationTemplateFactory.buildWorkReportGeneratedTemplate(reportId, reportName, recipients, dto.getFileDTO(), response);

		// expire the asset - if/when we implement historical report download, remove/tweak this
		Calendar expiration = DateUtilities.getCalendarNow();
		expiration.add(Calendar.HOUR, Constants.WORK_REPORT_CSV_EXPIRATION_HOURS);
		Event expirationEvent = eventFactory.buildAssetExpirationEvent(asset.getUUID(), expiration);
		eventRouter.sendEvent(expirationEvent);
		notificationDispatcher.dispatchEmail(template);
	}

	@Override
	public void onWorkResourceCheckedIn(Work work) {
		WorkResource activeResource = workResourceService.findActiveWorkResource(work.getId());

		NotificationTemplate template = notificationTemplateFactory.buildWorkResourceCheckedInNotificationTemplate(
			work.getBuyer().getId(), work, activeResource.getUser());
		notificationService.sendNotification(template);

		List<WorkFollow> followers = workFollowService.getWorkFollowers(work.getId());
		for (WorkFollow follower : followers) {
			AbstractWorkNotificationTemplate followerTemplate = notificationTemplateFactory.buildWorkResourceCheckedInNotificationTemplate(
				follower.getUser().getId(), work, activeResource.getUser());
			followerTemplate.setWorkFollow(follower);
			notificationService.sendNotification(followerTemplate);
		}
	}

	@Override
	public void onWorkResourceCheckedOut(Work work) {
		WorkResource activeResource = workResourceService.findActiveWorkResource(work.getId());

		NotificationTemplate template = notificationTemplateFactory.buildWorkResourceCheckedOutNotificationTemplate(
				work.getBuyer().getId(), work, activeResource.getUser());
		notificationService.sendNotification(template);

		List<WorkFollow> followers = workFollowService.getWorkFollowers(work.getId());
		for (WorkFollow follower : followers) {
			AbstractWorkNotificationTemplate followerTemplate = notificationTemplateFactory.buildWorkResourceCheckedOutNotificationTemplate(
					follower.getUser().getId(), work, activeResource.getUser());
			followerTemplate.setWorkFollow(follower);
			notificationService.sendNotification(followerTemplate);
		}
	}

	@Override
	public void onWorkResourceNotCheckedIn(WorkResource resource) {
		WorkResource workResource = workResourceDAO.findById(resource.getId());

		// Update work status
		Work work = workResource.getWork();
		workSubStatusService.addSystemSubStatus(workResource.getUser(), work.getId(), WorkSubStatusType.RESOURCE_NO_SHOW);

		// do we need this bologna?
		ClientServiceAlert alert = new ClientServiceAlert();
		alert.setCompany(work.getCompany());
		alert.setDescription("Resource did not check in! Assignment number " + work.getWorkNumber());
		clientServiceAlertService.saveOrUpdate(alert);
	}

	@Override
	public void onSearchCSVGenerated(Asset asset, FileDTO fileDTO, String recipient) {
		try {
			EmailTemplate template;
			if (asset.getFileByteSize() > Constants.MAX_CUSTOM_REPORT_CSV_SIZE) {
				String downloadUri = assetManagementService.getAuthorizedDownloadUriById(asset.getId());
				template = notificationTemplateFactory.buildSearchCSVGeneratedLargeEmailTemplate(downloadUri, recipient);
			} else {
				template = notificationTemplateFactory.buildSearchCSVGeneratedTemplate(recipient, fileDTO);
			}

			Calendar expiration = DateUtilities.getCalendarNow();
			expiration.add(Calendar.HOUR, Constants.WORK_REPORT_CSV_EXPIRATION_HOURS);
			eventRouter.sendEvent(eventFactory.buildAssetExpirationEvent(asset.getUUID(), expiration));
			notificationDispatcher.dispatchEmail(template);
		} catch (Exception e) {
			logger.error("CSV Generation failed", e);
		}
	}

	public void sendWorkDetailsToResource(Long resourceUserId, Long workId, String assignmentHTML) {
		Event event = eventFactory.buildWorkAcceptedEvent(resourceUserId, workId, assignmentHTML);
		eventRouter.sendEvent(event);
	}

	@Override
	public void onWorkCancelled(Work work, WorkResource workResource, CancelWorkDTO cancelWorkDTO, boolean isAssignmentPaid) {
		loadLazyPropertiesWithBuyer(work);

		NotificationTemplate forWorker;
		NotificationTemplate forDispatcher = null;
		Long workerId = workResource.getUser().getId();
		Long dispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), workerId);

		if (isAssignmentPaid) {
			forWorker = notificationTemplateFactory.buildWorkCancelledPaidNotificationTemplate(
				workerId, work, workResource, cancelWorkDTO.getNote()
			);
			if (dispatcherId != null) {
				forDispatcher = notificationTemplateFactory.buildWorkCancelledPaidNotificationTemplate(
					dispatcherId, work, workResource, cancelWorkDTO.getNote()
				);
			}
		} else {
			forWorker = notificationTemplateFactory.buildWorkCancelledWithoutPayNotificationTemplate(
				workerId, work, workResource, cancelWorkDTO.getNote()
			);
			if (dispatcherId != null) {
				forDispatcher = notificationTemplateFactory.buildWorkCancelledWithoutPayNotificationTemplate(
					dispatcherId, work, workResource, cancelWorkDTO.getNote()
				);
			}
		}

		notificationService.sendNotification(forWorker);
		if (forDispatcher != null) {
			forDispatcher.setOnBehalfOfId(workerId);
			notificationService.sendNotification(forDispatcher);
		}
	}

	@Override
	public <T extends AbstractInvoice> void onNewInvoice(T invoice) {
		if (invoice != null) {
			Set<User> users = authenticationService.findAllUsersSubscribedToInvoice(invoice.getCompany().getId(), invoice);

			for (User u : users) {
				NotificationTemplate template;
				Calendar notificationTime = DateUtilities.getCalendarWithTime(8, 0, u.getProfile().getTimeZone().getTimeZoneId());
				template = notificationTemplateFactory.buildNewInvoiceNotificationTemplate(u.getId(), invoice);
				if (template != null) {
					template.setTimeZoneId(u.getProfile().getTimeZone().getTimeZoneId());
					notificationService.sendNotification(template, notificationTime);
				}
			}

			if (invoice instanceof SubscriptionInvoice) {
				if (invoice.getCompany().hasDefaultAddressForSubscriptionInvoices()) {
					for (Email email : invoice.getCompany().getSubscriptionInvoiceEmails()) {
						try {
							EmailTemplate emailTemplate = emailTemplateFactory.buildInvoiceEmailTemplate(email.getEmail(), invoice);
							notificationService.sendNotification(emailTemplate, DateUtilities.getCalendarWithTime(8, 0, Constants.WM_TIME_ZONE));
						} catch (Exception e) {
							logger.error("Error sending subscription invoice email invoice id:" + invoice.getId(), e);
						}
					}
				}
			}
		}
	}

	@Override
	public void onSubscriptionConfigurationEffective(SubscriptionConfiguration configuration) {
		if (configuration != null) {
			for (User u : authenticationService.findAllAdminAndControllerUsersByCompanyId(configuration.getCompany().getId())) {
				Calendar notificationTime = DateUtilities.getCalendarWithTime(8, 0, u.getProfile().getTimeZone().getTimeZoneId());
				NotificationTemplate template = notificationTemplateFactory.buildSubscriptionEffectiveNotificationTemplate(u.getId(), configuration, u.getProfile().getTimeZone().getTimeZoneId());
				notificationService.sendNotification(template, notificationTime);
			}
		}
	}


	@Override
	public void onSubscriptionPaymentTierThroughputReached(SubscriptionConfiguration configuration, SubscriptionPaymentTier activeSubscriptionPaymentTier, BigDecimal throughput) {
		if (configuration != null) {
			User user = userService.findUserById(Constants.JEFF_WALD_USER_ID);
			NotificationTemplate tierBustingTemplate = notificationTemplateFactory.buildSubscriptionPaymentTierThroughputReached(user.getId(), configuration, activeSubscriptionPaymentTier, throughput, user.getProfile().getTimeZone().getTimeZoneId());
			tierBustingTemplate.setCcEmail(Constants.ACCOUNT_MANAGEMENT_TEAM_EMAIL);
			notificationService.sendNotification(tierBustingTemplate);
		}
	}

	@Override
	public void onSubscriptionConfigurationCancelled(SubscriptionConfiguration configuration) {
		if (configuration != null) {
			Set<User> recipients = authenticationService.findAllAdminAndControllerUsersByCompanyId(configuration.getCompany().getId());

			for (User u : recipients) {
				Calendar notificationTime = DateUtilities.getCalendarWithTime(8, 0, u.getProfile().getTimeZone().getTimeZoneId());
				NotificationTemplate template = notificationTemplateFactory.buildSubscriptionCancelledNotificationTemplate(u.getId(), configuration, u.getProfile().getTimeZone().getTimeZoneId());
				notificationService.sendNotification(template, notificationTime);
			}
		}
	}

	@Override
	public void onForumCommentAdded(ForumPost post, ForumPost parent) {
		Assert.notNull(parent);

		List<ForumPostFollower> followers = followerDAO.getPostFollowers(parent.getId());

		for (ForumPostFollower follower : followers) {
			final User toUser = follower.getFollowerUser();
			final Long toUserId = toUser.getId();

			//If follower is commenter, don't send notification
			if (toUserId.equals(post.getCreatorId())) {
				continue;
			}

			final boolean sendingToPostCreator = toUserId.equals(parent.getCreatorId());

			final boolean shouldNotifyByMicroservice =
					featureFlagIsOnForUser(toUser, NOTIFY_BY_MICROSERVICE_FORUM_FEATURE_TOGGLE);

			if (shouldNotifyByMicroservice) {
				final String notificationCode = NotificationType.FORUM_POST_COMMENT_ADDED;

				final String microserviceBaseTemplateKey = sendingToPostCreator ? "forum_post_creator" : "forum_post_follower";

				final String microserviceEmailSubjectTemplateKey = String.format("%1s/email-subject", microserviceBaseTemplateKey);
				final String microserviceEmailBodyTemplateKey = String.format("%1s/email-body", microserviceBaseTemplateKey);
				final String microserviceSMSTemplateKey = String.format("%1s/sms", microserviceBaseTemplateKey);
				final String microservicePushTemplateKey = String.format("%1s/push", microserviceBaseTemplateKey);
				final String microserviceUserNotificationTemplateKey = String.format("%1s/bullhorn", microserviceBaseTemplateKey);

				final Map<String, Object> props = userService.getProjectionMapById(post.getCreatorId(), "firstName", "lastName");
				final String creatorFullName = StringUtilities.fullName((String) props.get("firstName"), (String) props.get("lastName"));

				final Map<String, Object> replacements = ImmutableMap.<String, Object>of(
						"baseurl", baseUrl,
						"template", ImmutableMap.of(
								"parent", ImmutableMap.of("title", parent.getTitle()),
								"creatorFullName", creatorFullName,
								"comment", ImmutableMap.of(
										"rootId", post.getRootId(),
										"id", post.getId())),
						"toUser", ImmutableMap.of("firstName", toUser.getFirstName()),
						"rawNote", post.getComment());

				sendNotifications(
						toUserId,
						Constants.EMAIL_USER_ID_TRANSACTIONAL,
						microserviceEmailSubjectTemplateKey,
						microserviceEmailBodyTemplateKey,
						microserviceSMSTemplateKey,
						microservicePushTemplateKey,
						microserviceUserNotificationTemplateKey,
						notificationCode,
						replacements,
						Constants.LANGUAGE_CODE_ENGLISH);
			} else {
				final NotificationTemplate template = sendingToPostCreator ?
						notificationTemplateFactory.buildForumCommentAddedPostForCreatorNotificationTemplate(toUserId, post, parent) :
						notificationTemplateFactory.buildForumCommentAddedNotificationTemplate(toUserId, post, parent);

				notificationService.sendNotification(template);
			}
		}
	}

	/**
	 * Render and send all types of notification to toUserId.
	 * @param toUserId
	 * @param fromUserId
	 * @param emailSubjectTemplateKey
	 * @param emailBodyTemplateKey
	 * @param smsTemplateKey
	 * @param pushTemplateKey
	 * @param bullhornTemplateKey
	 * @param notificationCode   the notification code for the database, like "manage.work.invited"
	 * @param replacements
	 */
	public void sendNotifications(
			final long toUserId,
			final long fromUserId,
			final String emailSubjectTemplateKey, final String emailBodyTemplateKey,
			final String smsTemplateKey,
			final String pushTemplateKey,
			final String bullhornTemplateKey,
			final String notificationCode,
			final Map<String, Object> replacements,
			final String languageCode) {
		notificationDispatcher.dispatchEmail(
				toUserId,
				fromUserId,
				emailBodyTemplateKey,
				emailSubjectTemplateKey,
				languageCode,
				replacements, notificationCode);

		notificationDispatcher.dispatchUserNotification(
				false,
				bullhornTemplateKey,
				toUserId,
				fromUserId,
				notificationCode,
				replacements,
				languageCode);

		notificationDispatcher.dispatchSMS(toUserId, smsTemplateKey, languageCode, replacements, notificationCode);

		notificationDispatcher.dispatchPush(toUserId, pushTemplateKey, languageCode, replacements, notificationCode);
	}

	@VisibleForTesting
	boolean featureFlagIsOnForUser(final User user, final String toggleName) {
		return featureEntitlementService
				.getFeatureToggle(user, toggleName)
				.map(new Func1<FeatureToggleAndStatus, Boolean>() {
					@Override
					public Boolean call(final FeatureToggleAndStatus featureToggleAndStatus) {
						if (!featureToggleAndStatus.getStatus().getSuccess()) {
							return false;
						}

						final FeatureToggle toggle = featureToggleAndStatus.getFeatureToggle();

						if (toggle == null) {
							return false;
						}

						return "true".equalsIgnoreCase(toggle.getValue());
					}
				})
				.defaultIfEmpty(false)
				.onErrorReturn(new Func1<Throwable, Boolean>() {
					@Override
					public Boolean call(final Throwable throwable) {
						return false;
					}
				})
				.toBlocking()
				.single();
	}

	@Override
	public UnreadNotificationsDTO getUnreadNotificationsInfoByUser(long userId) {
		wmMetricRegistryFacade.meter("getCachedUnreadNotificationsByUser").mark();
		Optional<UnreadNotificationsDTO> cachedResult = userNotificationCache.getUnreadNotificationsInfoByUser(userId);
		if (cachedResult.isPresent()) {
			return cachedResult.get();
		}
		final UnreadNotificationsDTO unreadNotificationsDTO = getUnreadNotificationsDTO(userId);
		userNotificationCache.putUnreadNotificationInfo(userId, unreadNotificationsDTO);

		return unreadNotificationsDTO;
	}

	@Override
	public UnreadNotificationsDTO getUnreadNotificationsDTO(long userId) {
		final UserNotificationSearchRequestBuilder builder = UserNotificationSearchRequest.builder()
			.setToUserId(String.valueOf(userId))
			.setArchived(false)
			.setViewed(false)
			.setStatus(UserNotificationStatus.PUBLISHED)
			.setLimit(1000)
			.setFromDate(new DateTime(DateUtilities.addTime(Calendar.getInstance(), -30, Constants.DAY))); // last 30 days

		final UnreadNotificationsDTO unreadNotificationsDTO;
		final UserNotificationSearchResponse result = client.search(builder.build(), webRequestContextProvider.getRequestContext())
			.toBlocking().single();
		if (result.getResults().isEmpty()) {
			unreadNotificationsDTO = new UnreadNotificationsDTO(null, null);
		} else {
			final String endUuid = result.getResults().get(0).getUuid();
			final String startUuid = Iterables.getLast(result.getResults()).getUuid();
			final int count = result.getResults().size();
			unreadNotificationsDTO = new UnreadNotificationsDTO(startUuid, endUuid, count);
		}
		return unreadNotificationsDTO;
	}

	@Override
	public Set<Long> getCompaniesWithLowBalanceForAlert() {
		return Sets.newHashSet(companyDAO.findAllCompaniesWithLowBalanceAlertEnabled());
	}

	private void loadLazyProperties(Work work) {
		if (work == null) {
			return;
		}
		if (work.getCompany() != null) {
			Hibernate.initialize(work.getCompany());
		}
		if (work.getWorkCustomFieldGroupAssociations() != null) {
			Hibernate.initialize(work.getWorkCustomFieldGroupAssociations());
		}
	}

	private void loadLazyPropertiesWithBuyer(Work work) {
		loadLazyProperties(work);
		if (work != null && work.getBuyer() != null) {
			Hibernate.initialize(work.getBuyer());
		}
	}

	private void sendWorkSubStatusAlert(Long toUser, Work work, WorkSubStatusTypeAssociation association, WorkResource activeResource) {
		logger.debug("sendWorkSubStatusAlert() -- " + association.getWorkSubStatusType());
		NotificationTemplate template;
		NotificationTemplate dispatcherTemplate = null;

		List<Long> workSubStatusRecipientIds = workSubStatusService.findAllRecipientsByWorkSubStatusId(association.getWorkSubStatusType().getId());

		if (association.getWorkSubStatusType().isAlert()) {
			if (toUser == null && !workSubStatusRecipientIds.isEmpty()) {
				for (Long workSubStatusRecipientId : workSubStatusRecipientIds) {
					AbstractWorkNotificationTemplate workSubStatusRecipientTemplate = notificationTemplateFactory.buildWorkSubStatusAlertNotificationTemplate(
						workSubStatusRecipientId, association, work, activeResource
					);
					notificationService.sendNotification(workSubStatusRecipientTemplate);
				}
				return;
			} else if (toUser == null && workSubStatusRecipientIds.isEmpty()) {
				return;
			}

			template = notificationTemplateFactory.buildWorkSubStatusAlertNotificationTemplate(toUser, association, work, activeResource);
			notificationService.sendNotification(template);

			if (toUser == work.getBuyer().getId()) {
				List<WorkFollow> followers = workFollowService.getWorkFollowers(work.getId());
				for (WorkFollow follower : followers) {
					AbstractWorkNotificationTemplate followerTemplate = notificationTemplateFactory.buildWorkSubStatusAlertNotificationTemplate(
						follower.getUser().getId(), association, work, activeResource
					);
					followerTemplate.setWorkFollow(follower);
					notificationService.sendNotification(followerTemplate);
				}

				for (Long workSubStatusRecipientId : workSubStatusRecipientIds) {
					if (followers.contains(workSubStatusRecipientId)) {
						continue;
					}

					AbstractWorkNotificationTemplate workSubStatusRecipientTemplate = notificationTemplateFactory.buildWorkSubStatusAlertNotificationTemplate(
						workSubStatusRecipientId, association, work, activeResource
					);
					notificationService.sendNotification(workSubStatusRecipientTemplate);
				}

			} else {
				Long dispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(activeResource.getWork().getId(), toUser);
				if (dispatcherId != null) {
					dispatcherTemplate = notificationTemplateFactory.buildWorkSubStatusAlertNotificationTemplate(
						dispatcherId, association, work, activeResource
					);
				}
			}

		} else {
			if (toUser == null && !workSubStatusRecipientIds.isEmpty()) {
				for (Long workSubStatusRecipientId : workSubStatusRecipientIds) {
					AbstractWorkNotificationTemplate workSubStatusRecipientTemplate = notificationTemplateFactory.buildWorkSubStatusAlertNotificationTemplate(
						workSubStatusRecipientId, association, work, activeResource
					);
					notificationService.sendNotification(workSubStatusRecipientTemplate);
				}
				return;
			} else if (toUser == null && workSubStatusRecipientIds.isEmpty()) {
				return;
			}

			template = notificationTemplateFactory.buildWorkSubStatusNotificationTemplate(toUser, association, work, activeResource);
			notificationService.sendNotification(template);

			if (toUser == work.getBuyer().getId()) {
				List<WorkFollow> followers = workFollowService.getWorkFollowers(work.getId());
				for (WorkFollow follower : followers) {
					AbstractWorkNotificationTemplate followerTemplate = notificationTemplateFactory.buildWorkSubStatusNotificationTemplate(
						follower.getUser().getId(), association, work, activeResource
					);
					followerTemplate.setWorkFollow(follower);
					notificationService.sendNotification(followerTemplate);
				}

				for (Long workSubStatusRecipientId : workSubStatusRecipientIds) {
					if (followers.contains(workSubStatusRecipientId)) {
						continue;
					}

					AbstractWorkNotificationTemplate workSubStatusRecipientTemplate = notificationTemplateFactory.buildWorkSubStatusNotificationTemplate(
						workSubStatusRecipientId, association, work, activeResource
					);
					notificationService.sendNotification(workSubStatusRecipientTemplate);
				}

			} else {
				Long dispatcherId = workResourceService.getDispatcherIdForWorkAndWorker(activeResource.getWork().getId(), toUser);
				if (dispatcherId != null) {
					dispatcherTemplate = notificationTemplateFactory.buildWorkSubStatusNotificationTemplate(
						dispatcherId, association, work, activeResource
					);
				}
			}
		}
		if (dispatcherTemplate != null) {
			dispatcherTemplate.setOnBehalfOfId(toUser);
			notificationService.sendNotification(dispatcherTemplate);
		}
	}

	private boolean sendEmailsAndAlerts(String workSubStatusCode, Work work, Long workResourceId) {
		if (WorkSubStatusType.RESOURCE_CANCELLED.equals(workSubStatusCode)) {
			Set<Long> recipientIds = new HashSet<>();

			recipientIds.add(work.getBuyer().getId());

			WorkResource wResource = workService.findWorkResource(workResourceId, work.getId());

			if (work.getBuyerSupportUser() != null)
				recipientIds.add(work.getBuyerSupportUser().getId());

			for (Long recipientId : recipientIds) {
				notificationService.sendNotification(
					notificationTemplateFactory.buildWorkResourceCancelledNotificationTemplate(wResource, recipientId)
				);
			}

			List<WorkFollow> followers = workFollowService.getWorkFollowers(work.getId());
			for (WorkFollow follower : followers) {
				// avoid sending duplicates
				if (recipientIds.contains(follower.getUser().getId()))
					continue;

				AbstractWorkNotificationTemplate followerTemplate = notificationTemplateFactory
					.buildWorkResourceCancelledNotificationTemplate(wResource, follower.getUser().getId());
				followerTemplate.setWorkFollow(follower);
				notificationService.sendNotification(followerTemplate);
			}

			List<Long> workSubStatusRecipientIds = workSubStatusService
				.findAllRecipientsByWorkSubStatusCodeAndCompany(workSubStatusCode, work.getCompany().getId());
			for (Long workSubStatusRecipientId : workSubStatusRecipientIds) {
				if (followers.contains(workSubStatusRecipientId)) {
					continue;
				}

				AbstractWorkNotificationTemplate workSubStatusRecipientTemplate = notificationTemplateFactory
					.buildWorkResourceCancelledNotificationTemplate(wResource, workSubStatusRecipientId);
				notificationService.sendNotification(workSubStatusRecipientTemplate);
			}

			return true;
		} else if (WorkSubStatusType.RESOURCE_NOT_CONFIRMED.equals(workSubStatusCode)) {
			Set<Long> recipientIds = new HashSet<>();

			recipientIds.add(work.getBuyer().getId());

			WorkResource wResource = workService.findWorkResource(workResourceId, work.getId());

			if (work.getBuyerSupportUser() != null)
				recipientIds.add(work.getBuyerSupportUser().getId());

			for (Long recipientId : recipientIds) {
				notificationService.sendNotification(
					notificationTemplateFactory.buildWorkSubStatusFailedConfirmationNotificationTemplate(wResource, recipientId, work, workService.getAppointmentTime(work.getId()))
				);
			}

			List<WorkFollow> followers = workFollowService.getWorkFollowers(work.getId());
			for (WorkFollow follower : followers) {
				// avoid sending duplicates
				if (recipientIds.contains(follower.getUser().getId()))
					continue;

				AbstractWorkNotificationTemplate followerTemplate = notificationTemplateFactory
					.buildWorkSubStatusFailedConfirmationNotificationTemplate(wResource, follower.getUser().getId(), work, workService.getAppointmentTime(work.getId()));
				followerTemplate.setWorkFollow(follower);
				notificationService.sendNotification(followerTemplate);
			}

			List<Long> workSubStatusRecipientIds = workSubStatusService
				.findAllRecipientsByWorkSubStatusCodeAndCompany(workSubStatusCode, work.getCompany().getId());
			for (Long workSubStatusRecipientId : workSubStatusRecipientIds) {
				if (followers.contains(workSubStatusRecipientId)) {
					continue;
				}

				AbstractWorkNotificationTemplate workSubStatusRecipientTemplate = notificationTemplateFactory
					.buildWorkResourceCancelledNotificationTemplate(wResource, workSubStatusRecipientId);
				notificationService.sendNotification(workSubStatusRecipientTemplate);
			}

			return true;
		}

		return false;
	}
}
