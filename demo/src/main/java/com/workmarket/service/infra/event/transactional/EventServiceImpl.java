package com.workmarket.service.infra.event.transactional;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.common.cache.UserNotificationCache;
import com.workmarket.common.service.status.BaseStatus;
import com.workmarket.common.service.wrapper.response.MessageResponse;
import com.workmarket.common.template.NotificationTemplateFactory;
import com.workmarket.common.template.WorkUpdatedNotificationTemplate;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.BlacklistedEmailDAO;
import com.workmarket.dao.changelog.company.CompanyChangeLogDAO;
import com.workmarket.dao.changelog.profile.ProfileChangeLogDAO;
import com.workmarket.dao.changelog.user.UserChangeLogDAO;
import com.workmarket.dao.requirement.TravelDistanceRequirementDAO;
import com.workmarket.data.report.work.AccountStatementDetailRow;
import com.workmarket.data.solr.indexer.user.SolrVendorIndexer;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.data.solr.indexer.work.WorkIndexer;
import com.workmarket.domains.forums.service.ForumService;
import com.workmarket.domains.forums.service.event.NotifyPostFollowerEvent;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.service.UserGroupValidationService;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Message;
import com.workmarket.domains.model.MimeType;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.ProfileModificationType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserPagination;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.type.CompanyAssetAssociationType;
import com.workmarket.domains.model.asset.type.UserAssetAssociationType;
import com.workmarket.domains.model.changelog.PropertyChange;
import com.workmarket.domains.model.changelog.PropertyChangeType;
import com.workmarket.domains.model.changelog.PropertyChangeUtilities;
import com.workmarket.domains.model.changelog.company.CompanyPropertyChangeLog;
import com.workmarket.domains.model.changelog.profile.ProfilePropertyChangeLog;
import com.workmarket.domains.model.changelog.user.UserPropertyChangeLog;
import com.workmarket.domains.model.changelog.work.WorkPropertyChangeLog;
import com.workmarket.domains.model.changelog.work.WorkPropertyChangeType;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.geocoding.Coordinate;
import com.workmarket.domains.model.lane.LaneAssociation;
import com.workmarket.domains.model.reporting.ReportFilter;
import com.workmarket.domains.model.reporting.ReportRequestData;
import com.workmarket.domains.model.reporting.ReportingContext;
import com.workmarket.domains.model.requirementset.traveldistance.TravelDistanceRequirement;
import com.workmarket.domains.model.screening.BackgroundCheck;
import com.workmarket.domains.model.screening.DrugTest;
import com.workmarket.domains.model.screening.Screening;
import com.workmarket.domains.model.screening.ScreeningObjectConverter;
import com.workmarket.domains.model.screening.ScreeningStatusType;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.work.dao.follow.WorkFollowDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkResourceLabelType;
import com.workmarket.domains.work.model.follow.WorkFollow;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeAssociation;
import com.workmarket.domains.work.service.WorkChangeLogService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.actions.WorkViewedEvent;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.dto.UnreadNotificationsDTO;
import com.workmarket.reporting.query.CSVRowBasedSQLExecutor;
import com.workmarket.reporting.query.GenericQueryBuilderSqlImpl;
import com.workmarket.reporting.query.GenericRowMapper;
import com.workmarket.reporting.service.WorkReportGeneratorServiceImpl;
import com.workmarket.reporting.util.CSVReportWriter;
import com.workmarket.reporting.util.ZipCSVSearchWriter;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.CustomFieldService;
import com.workmarket.service.business.CustomReportService;
import com.workmarket.service.business.ScreeningService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.account.InvoiceNotificationService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.FileDTO;
import com.workmarket.service.business.dto.WorkResourceLabelDTO;
import com.workmarket.service.business.event.EntityUpdateEvent;
import com.workmarket.service.business.event.MarkUserNotificationsAsReadEvent;
import com.workmarket.service.business.event.RefreshUserNotificationCacheEvent;
import com.workmarket.service.business.event.UserGroupMessageNotificationEvent;
import com.workmarket.service.business.event.WorkSubStatusTypeUpdatedEvent;
import com.workmarket.service.business.event.forums.CreateWorkFromFlaggedPostEvent;
import com.workmarket.service.business.event.reporting.WorkReportGenerateEvent;
import com.workmarket.service.business.event.work.ResourceConfirmationRequiredScheduledEvent;
import com.workmarket.service.business.event.work.WorkCreatedEvent;
import com.workmarket.service.business.event.work.WorkInvoiceGenerateEvent;
import com.workmarket.service.business.event.work.WorkResourceLateLabelScheduledEvent;
import com.workmarket.service.business.event.work.WorkUpdatedEvent;
import com.workmarket.service.infra.business.GeocodingService;
import com.workmarket.service.infra.notification.NotificationDispatcher;
import com.workmarket.service.search.group.GroupSearchService;
import com.workmarket.service.search.user.PeopleSearchService;
import com.workmarket.service.search.user.SearchCSVGenerateEvent;
import com.workmarket.service.search.user.ZipCSVSearchExecutor;
import com.workmarket.thrift.work.display.ReportResponse;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.HibernateUtilities;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class EventServiceImpl implements EventService {

	private static final Log logger = LogFactory.getLog(EventServiceImpl.class);

	@Autowired @Qualifier("readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Autowired private UserNotificationService userNotificationService;
	@Autowired private InvoiceNotificationService invoiceNotificationService;
	@Autowired private GeocodingService geocodingService;
	@Autowired private CompanyService companyService;
	@Autowired private AssetManagementService assetManagementService;
	@Autowired private UserService userService;
	@Autowired private ScreeningService screeningService;
	@Autowired private UserGroupValidationService userGroupValidationService;
	@Autowired private WorkService workService;
	@Autowired private WorkSubStatusService workSubStatusService;
	@Autowired private UserGroupService userGroupService;
	@Autowired private WorkResourceService workResourceService;
	@Autowired private CustomReportService customReportService;
	@Autowired private GroupSearchService groupSearchService;
	@Autowired private BillingService billingService;
	@Autowired private PeopleSearchService peopleSearchService;
	@Autowired private WorkChangeLogService workChangeLogService;
	@Autowired private WorkIndexer workIndexer;
	@Autowired private UserIndexer userIndexer;
	@Autowired private SolrVendorIndexer vendorIndexer;
	@Autowired private NotificationTemplateFactory notificationTemplateFactory;
	@Autowired private ReportingContext reportingContext;
	@Autowired private NotificationDispatcher notificationDispatcher;
	@Autowired private UserChangeLogDAO userChangeLogDAO;
	@Autowired private CompanyChangeLogDAO companyChangeLogDAO;
	@Autowired private ProfileChangeLogDAO profileChangeLogDAO;
	@Autowired private WorkFollowDAO workFollowDAO;
	@Autowired private TravelDistanceRequirementDAO travelDistanceRequirementDAO;
	@Autowired private BlacklistedEmailDAO blacklistedEmailDAO;
	@Autowired private UserNotificationCache userNotificationCache;
	@Autowired private ForumService forumService;
	@Autowired private CustomFieldService customFieldService;
	@Autowired private WorkReportGeneratorServiceImpl workReportGeneratorService;

	private static final int MAX_EXPORT_CSV_SEARCH_RESULTS = 50000;

	@Override
	public void processEvent(EntityUpdateEvent event) {
		Assert.notNull(event);
		Assert.notNull(event.getEntity());

		if (event.getEntity() instanceof Work) {
			onUpdateWork(event);
		} else if (event.getEntity() instanceof User) {
			User user = (User) event.getEntity();
			Assert.notNull(user.getId());

			if (event.hasPropertyValueChanged("email")) {
				blacklistedEmailDAO.deleteFromBlackList(user.getEmail());
			}

			if (user.getProfile() != null) {
				userIndexer.reindexById(user.getId());
			}

			try {
				for (int i = 0; i < event.getPropertyNames().length; i++) {
					Object oldValue = event.getOldState()[i];
					Object newValue = event.getState()[i];
					String property = event.getPropertyNames()[i];

					if (HibernateUtilities.isRelevantPropertyChange(user, property, oldValue, newValue)) {
						String oldValueString = oldValue == null ? null : ConvertUtils.convert(oldValue);
						String newValueString = newValue == null ? null : ConvertUtils.convert(newValue);

						userChangeLogDAO.saveOrUpdate(new UserPropertyChangeLog(user.getId(), event.getUser().getId(), event.getMasqueradeUser().getId(), property,
								oldValueString, newValueString));
					}
				}
			} catch (Exception e) {
				logger.error("[EntityUpdate] Error processing EntityUpdateEvent", e);
			}

		} else if (event.getEntity() instanceof Profile) {
			Profile profile = (Profile) event.getEntity();

			for (int i = 0; i < event.getPropertyNames().length; i++) {
				Object oldValue = event.getOldState()[i];
				Object newValue = event.getState()[i];
				String property = event.getPropertyNames()[i];

				try {
					if (HibernateUtilities.isRelevantPropertyChange(profile, property, oldValue, newValue)) {
						String oldValueString = oldValue == null ? null : ConvertUtils.convert(oldValue);
						String newValueString = newValue == null ? null : ConvertUtils.convert(newValue);

						profileChangeLogDAO.saveOrUpdate(new ProfilePropertyChangeLog(profile, event.getUser(), event.getMasqueradeUser(), property,
								oldValueString, newValueString));
					}
				} catch (Exception e) {
					logger.error("[EntityUpdate] Error sending EntityUpdateEvent", e);
				}
			}

			userIndexer.reindexById(profile.getUser().getId());
		} else if (event.getEntity() instanceof LaneAssociation) {
			LaneAssociation association = (LaneAssociation) event.getEntity();

			if (event.hasPropertyValueChanged("deleted")) {
				int i = event.getPropertyIndex("deleted");

				Boolean oldStatus = event.getOldState() != null ? (Boolean) event.getOldState()[i] : false;
				Boolean newStatus = (Boolean) event.getState()[i];

				if (oldStatus && !newStatus) {
					userNotificationService.onLaneAssociationCreated(association);
				}
			}

			if (event.hasPropertyValueChanged("approvalStatus")) {
				int i = event.getPropertyIndex("approvalStatus");
				ApprovalStatus oldStatus = event.getOldState() != null ? (ApprovalStatus) event.getOldState()[i] : ApprovalStatus.APPROVED;
				ApprovalStatus newStatus = (ApprovalStatus) event.getState()[i];

				if (!oldStatus.equals(newStatus)) {
					userNotificationService.onLaneAssociationCreated(association);
				}
			}
		} else if (event.getEntity() instanceof BackgroundCheck) {
			onUpdateScreening(event);
		} else if (event.getEntity() instanceof DrugTest) {
			onUpdateScreening(event);
		} else if (event.getEntity() instanceof Company) {
			Company company = (Company) event.getEntity();

			if ((company.getName() != null && event.hasPropertyValueChanged("name"))
					|| (company.getWebsite() != null && event.hasPropertyValueChanged("website"))
					|| (company.getOverview() != null && event.hasPropertyValueChanged("overview"))
					|| (company.getAddress() != null && event.hasPropertyValueChanged("address"))
					|| event.hasPropertyValueChanged("companyStatusType")
					|| event.hasPropertyValueChanged("customerType")) {

				onCompanyUpdate(company.getId());
			}

			try {
				for (int i = 0; i < event.getPropertyNames().length; i++) {
					Object oldValue = event.getOldState()[i];
					Object newValue = event.getState()[i];
					String property = event.getPropertyNames()[i];

					if (property != null && HibernateUtilities.isRelevantPropertyChange(company, property, oldValue, newValue)) {
						String oldValueString = oldValue == null ? null : ConvertUtils.convert(oldValue);
						String newValueString = newValue == null ? null : ConvertUtils.convert(newValue);

						companyChangeLogDAO.saveOrUpdate(
							new CompanyPropertyChangeLog(
								company,
								event.getUser(),
								event.getMasqueradeUser(),
								property,
								oldValueString,
								newValueString
							)
						);
					}
				}
			} catch (Exception e) {
				logger.error("[EntityUpdate] Error processing ", e);
			}
		} else if (event.getEntity() instanceof TravelDistanceRequirement) {
			TravelDistanceRequirement requirement = (TravelDistanceRequirement) event.getEntity();
			if (requirement.getLatitude() == null ||
				requirement.getLongitude() == null ||
				(requirement.getLatitude() == 0 && requirement.getLongitude() == 0)) {
				Coordinate addressCoordinates = new Coordinate(geocodingService.geocode(requirement.getAddress()));

				requirement.setLatitude(addressCoordinates.getLatitude());
				requirement.setLongitude(addressCoordinates.getLongitude());

				travelDistanceRequirementDAO.saveOrUpdate(requirement);
			}
		}
	}

	@Override
	public void processEvent(ResourceConfirmationRequiredScheduledEvent event) {
		logger.debug(String.format("[ResourceConfirmationRequiredScheduled] Processing event %s", event.toString()));

		Work work = workService.findWork(event.getWorkId());
		Assert.notNull(work);
		if (work.isActive() && work.isResourceConfirmationRequired()) {
			// Ensure that the scheduled time for the assignment hasn't changed.

			WorkResource resource = workService.findActiveWorkResource(work.getId());
			if (resource != null && !resource.isConfirmed()) {

				Calendar alertDate = workService.calculateRequiredConfirmationDate(work);
				if (!DateUtilities.withinIntervalWindow(Calendar.MINUTE, 5, alertDate)) {
					logger.debug(String.format("[ResourceConfirmationRequiredScheduled] Discarding event for work %s", work.getWorkNumber()));
					return;
				}

				/*Since this is processed in a different server that could be seconds behind, we'll subtract 1 minute to the actual date
				 * to prevent the isInPast validation from failing.
				 */
				Calendar alertIdDate = DateUtilities.cloneCalendar(alertDate);
				alertIdDate.add(Calendar.MINUTE, -1);

				if (alertIdDate.before(Calendar.getInstance())) {
					workSubStatusService.addSystemSubStatus(resource.getUser(), work.getId(), WorkSubStatusType.RESOURCE_NOT_CONFIRMED);
				}
			}
		}
	}

	@Override
	public void processEvent(UserGroupMessageNotificationEvent event) {
		Assert.notNull(event);

		Message message = event.getMessage();
		Set<UserGroup> userGroups = message.getUserGroups();
		User sender = message.getSender();

		Assert.notNull(message, "Unable to find message");
		Assert.notNull(sender, "Unable to find user");

		List<Long> members = Lists.newArrayList();
		List<Long> groupIds = Lists.newArrayList();
		UserPagination pagination = new UserPagination();
		pagination.setResultsLimit(50);

		for (UserGroup group : userGroups) {
			logger.debug(new StringBuilder("[UserGroupMessageNotification] Sending emails to members of user group ").append(group.getId()).append(" message ")
					.append(message.getId().toString()));

			pagination = userGroupService.findAllUsersOfGroup(group.getId(), pagination);
			for (int i = 0; i < pagination.getNumberOfPages(); i++) {
				for (User u : pagination.getResults()) {
					if (!members.contains(u.getId())) {
						try {
							notificationDispatcher.dispatchNotification(notificationTemplateFactory.buildUserGroupMessage(sender.getId(),
									u.getId(), message.getContent(), message.getSubject(), group));
						} catch (Exception e) {
							logger.error("[UserGroupMessageNotification] Error sending email", e);
						}
						members.add(u.getId());
					}
				}
				pagination.nextPage();
				pagination = userGroupService.findAllUsersOfGroup(group.getId(), pagination);
			}
			groupIds.add(group.getId());
		}

		groupSearchService.reindexGroups(groupIds);

		logger.debug("[UserGroupMessageNotification] Message sent to " + members.size() + "distinct users");
	}

	@Override
	public void processEvent(WorkCreatedEvent event) {
		userNotificationService.onWorkCreated(event.getWorkId());
	}

	@Override
	public void processEvent(WorkUpdatedEvent event) {
		Assert.notNull(event.getWorkId());

		Work work;
		List<Long> resourceUserIds = workResourceService.findUserIdsNotDeclinedForWork(event.getWorkId());
		if (resourceUserIds.isEmpty()) {
			logger.info("[WorkUpdated] No resources were found for assignment id: " + event.getWorkId());
			return;
		} else {
			work = workService.findWork(event.getWorkId());
		}

		Set<Long> recipientIds = new HashSet<>();

		for (Long uid : resourceUserIds) {
			recipientIds.add(uid);
			WorkUpdatedNotificationTemplate template = notificationTemplateFactory.buildWorkUpdatedNotificationTemplate(uid, work);
			template.setPropertyChanges(event.getPropertyChanges());
			try {
				notificationDispatcher.dispatchNotification(template);
			} catch (Exception e) {
				logger.error("[WorkUpdated] Error sending notification", e);
			}
		}

		List<Long> dispatcherIds = workResourceService.getDispatcherIdsForWorkAndWorkers(work.getId(), recipientIds);
		for (Long dispatcherId : dispatcherIds) {
			WorkUpdatedNotificationTemplate dispatcherNotification = notificationTemplateFactory.buildWorkUpdatedNotificationTemplate(dispatcherId, work);
			dispatcherNotification.setPropertyChanges(event.getPropertyChanges());
			try {
				notificationDispatcher.dispatchNotification(dispatcherNotification);
			} catch (Exception e) {
				logger.error("[WorkUpdated] Error sending notification", e);
			}
		}

		List<WorkFollow> followers = workFollowDAO.getFollowers(work.getId());
		for (WorkFollow follower : followers) {
			// avoid sending duplicates
			if (!recipientIds.add(follower.getUser().getId())) {
				continue;
			}

			WorkUpdatedNotificationTemplate followerTemplate = notificationTemplateFactory.buildWorkUpdatedNotificationTemplate(follower.getUser().getId(), work);
			followerTemplate.setPropertyChanges(event.getPropertyChanges());
			followerTemplate.setWorkFollow(follower);
			try {
				notificationDispatcher.dispatchNotification(followerTemplate);
			} catch (Exception e) {
				logger.error("[WorkUpdated] Error sending notification", e);
			}
		}
	}

	@Override
	public void processEvent(WorkReportGenerateEvent event) {
		try {
			Assert.notNull(event);
			Assert.notNull(event.getReportRequestData());
			Assert.notEmpty(event.getRecipients(), "reports must be sent to at least one recipient");
			Assert.notNull(event.getReportRequestData().getCompanyId(), "company id is required");

			ReportRequestData entityRequest = event.getReportRequestData();
			// generate the report twice
			//  1st to save as a .csv for attachment to the email
			//  2nd to show as a summary in the email body (only if the .csv is not greater than 4MB)
			// save unescaped list of filters to pass to CustomReportService generator for summary (it escapes the list itself)
			// escape the filter list to pass to CSVRowBasedSQLExecutor query
			List<ReportFilter> unescapedList = entityRequest.getReportFilterL();
			List<ReportFilter> correctedList = workReportGeneratorService.escapeInvalidProperties(unescapedList);
			entityRequest.setReportFilterL(correctedList);
			String filename = "report-";
			if (StringUtils.isNotBlank(entityRequest.getReportName())) {
				filename += entityRequest.getReportName().replaceAll("\\W+", "") + "-";
			}
			filename += DateUtilities.formatCalendar_MMDDYY(DateUtilities.getCalendarNow()) + Constants.CSV_EXTENSION;
			if (CollectionUtils.isNotEmpty(entityRequest.getWorkCustomFieldIds())) {
				List<WorkCustomField> fields = customFieldService.findWorkCustomFieldByIds(entityRequest.getWorkCustomFieldIds());
				List<String> fieldNames = new ArrayList<>();
				for (WorkCustomField field : fields) {
					fieldNames.add(field.getName());
				}
				entityRequest.setWorkCustomFieldNames(fieldNames);
			}
			CSVReportWriter writer = new CSVReportWriter(entityRequest, reportingContext.getEntities(), filename, event.getDirectory());

			CSVRowBasedSQLExecutor executor = new CSVRowBasedSQLExecutor();
			executor.setJdbcTemplate(jdbcTemplate);
			executor.setRowMapper(new GenericRowMapper(reportingContext, entityRequest));
			executor.setCSVReportWriter(writer);
			executor.setSqlBuilder(new GenericQueryBuilderSqlImpl().buildQuery(reportingContext, entityRequest));
			executor.query();

			AssetDTO dto = new AssetDTO();
			dto.setSourceFilePath(writer.getAbsolutePath());
			dto.setName(writer.getFilename());
			dto.setMimeType(MimeType.TEXT_CSV.toString());
			dto.setAssociationType(CompanyAssetAssociationType.SCHEDULED_REPORT);
			dto.setActive(true);

			Asset asset = assetManagementService.storeAssetForCompany(dto, entityRequest.getCompanyId(), false);
			if (asset.getFileByteSize() > Constants.MAX_CUSTOM_REPORT_CSV_SIZE) {
				// for large files send just a link
				userNotificationService.onLargeWorkReportGenerated(entityRequest.getEntityKey(), entityRequest.getReportName(), event.getRecipients(), asset);

				// now delete the file - we delete it here as the local file will not be touched by the above event otherwise
				// we will fill up space
				FileUtils.deleteQuietly(new File(writer.getAbsolutePath()));
				return;
			}

			entityRequest.setReportFilterL(unescapedList);
			ReportResponse response;
			if (event.getReportId() == null) {
				response = customReportService.generateAdhocCustomReport(entityRequest);
			} else {
				Company company;
				if (event.getUser() == null) {
					company = companyService.findCompanyById(entityRequest.getCompanyId());
				} else {
					company = event.getUser().getCompany();
				}
				response = customReportService.generateSavedCustomReport(event.getReportId(), company);
			}
			userNotificationService.onWorkReportGenerated(entityRequest.getEntityKey(), entityRequest.getReportName(), event.getRecipients(), asset, writer.getAbsolutePath(), response, event.getReportId());
		} catch (Exception e) {
			logger.error("[WorkReportGenerate] Error processing event ", e);
		}

	}

	@Override
	public void processEvent(WorkResourceLateLabelScheduledEvent event) {
		logger.debug("Processing WorkResourceLateLabelScheduledEvent event");

		Assert.notNull(event.getWorkResourceId());
		WorkResource workResource = workResourceService.findWorkResourceById(event.getWorkResourceId());
		Assert.notNull(workResource);
		Work work = workResource.getWork();
		if (work.isActive() && (work.isCheckinRequired() || work.isCheckinCallRequired()) && !workResource.isCheckedIn()) {
			// Ensure that the scheduled time for the assignment hasn't changed.
			Calendar maxAppointmentDate = workService.calculateMaxAppointmentDateLatenessThreshold(work);
			if (!DateUtilities.withinIntervalWindow(Calendar.MINUTE, 5, maxAppointmentDate)) {
				logger.debug("Discarding event [WorkResourceLateLabelScheduled] for workId:" + work.getId());
				return;
			}

			/*
			 * Since this is processed in a different server that could be seconds behind, we'll subtract 1 minute to the actual date
			 * to prevent the isInPast validation from failing.
			 * E.g.
			 * 20:34:59 DEBUG [eventJMSContainer-8]:service.business.WorkServiceImpl.calculateRequiredCheckinDate()3005 Checkin date for work id: 43260 is: Fri, 16 Dec 2011 08:35 PM
			 * The checkin date was 8:35 but it was processed at 8:34:59
			 */
			maxAppointmentDate.add(Calendar.MINUTE, -1);
			if (DateUtilities.isInPast(maxAppointmentDate)) {
				workResourceService.addLabelToWorkResource(new WorkResourceLabelDTO(event.getWorkResourceId(), WorkResourceLabelType.LATE));
			}
		}
	}

	@Override
	public void processEvent(WorkSubStatusTypeUpdatedEvent event) {
		Assert.notNull(event);

		long userId = event.getUserId();
		User user = userService.getUser(userId);
		Company company = user.getCompany();
		long companyId = company.getId();
		WorkSubStatusType workSubStatusType = workSubStatusService.findWorkSubStatus(event.getWorkSubStatusTypeId());

	 	/*
		 * If workStatusCodes is empty, there's no need to resolve anything because all statuses are valid.
		 * Otherwise resolve all custom label associations between work and sub-status where invalid work-statuses are used
		 */
		String[] workStatusCodes = CollectionUtilities.newGenericArrayPropertyProjection(workSubStatusType.getWorkScopes(), String.class, "code");
		long workSubStatusId = event.getWorkSubStatusTypeId();

		if (ArrayUtils.isNotEmpty(workStatusCodes)) {
			for (Work work : workService.findAllWorkWhereWorkStatusTypeNotInAndWorkSubStatusTypeIn(companyId, workSubStatusId, workStatusCodes)) {
				workSubStatusService.resolveSubStatus(userId, work.getId(), workSubStatusId, "");
			}
		}

		if (company.getManageMyWorkMarket().getCustomFormsEnabledFlag()) {
			 /* If workTemplateIds is empty, there's no need to resolve anything because all templates are valid.
			 *  Otherwise resolve all custom label associations between work and sub-status where invalid templates are used
			 */
			Long[] workTemplateIds = CollectionUtilities.newGenericArrayPropertyProjection(workSubStatusType.getWorkTemplates(), Long.class, "id");
			if (ArrayUtils.isNotEmpty(workTemplateIds)) {
				for (Work work : workService.findAllWorkWhereTemplatesNotInAndWorkSubStatusTypeIn(companyId, workSubStatusId, workTemplateIds)) {
					workSubStatusService.resolveSubStatus(userId, work.getId(), workSubStatusId, "");
				}
			}
		}

		List<WorkSubStatusTypeAssociation> associations = workSubStatusService.findAllWorkSubStatusTypeAssociationBySubStatusId(workSubStatusId);
		List<Long> workIdsWithSubStatus = extract(associations, on(WorkSubStatusTypeAssociation.class).getWork().getId());
		workIndexer.reindexById(workIdsWithSubStatus);
	}

	@Override
	public void processEvent(WorkInvoiceGenerateEvent event) {
		checkNotNull(event.getWorkId());
		checkNotNull(event.getInvoiceId());

		Work work = (Work) checkNotNull(workService.findWork(event.getWorkId()));
		AccountStatementDetailRow invoiceDetail = billingService.findAccountStatementDetailByInvoiceId(event.getInvoiceId(), work.getBuyer());
		MessageResponse pdfResponse;

		if (invoiceDetail != null) {
			switch (event.getSendType()) {
				case ALL:
					pdfResponse = invoiceNotificationService.sendInvoiceToUsers(work, invoiceDetail);
					break;
				case AUTOEMAIL:
					pdfResponse = invoiceNotificationService.sendInvoicePdfToAutoInvoiceEnabledUsersForWork(work, invoiceDetail);
					break;
				case SUBSCRIBED:
					pdfResponse = invoiceNotificationService.sendInvoicePdfToSubscribedUsersForWork(work, invoiceDetail);
					break;
				default:
					pdfResponse = new MessageResponse(BaseStatus.FAILURE);
			}
		} else {
			pdfResponse = new MessageResponse(BaseStatus.FAILURE);
			logger.error("[WorkInvoiceGenerate] Null invoiceDetail");
		}

		if (!pdfResponse.isSuccessful()) {
			logger.error(String.format("[WorkInvoiceGenerate] Error sending pdf to %s users: %s",
					event.getSendType().getClass(), StringUtils.join(pdfResponse.getMessages(), '\n')));
		}
	}

	@Override
	public void processEvent(RefreshUserNotificationCacheEvent event) {
		checkNotNull(event);

		final Long userId = checkNotNull(event.getUserId());
		final String notificationUuid = checkNotNull(event.getNotificationUuid());
		final Optional<UnreadNotificationsDTO> cachedDTOOptional = userNotificationCache.getUnreadNotificationsInfoByUser(userId);
		final UnreadNotificationsDTO unreadNotificationsDTO;

		if (cachedDTOOptional.isPresent()) {
			final UnreadNotificationsDTO cachedDTO = cachedDTOOptional.get();
			final String startUuid = cachedDTO.getUnreadCount() > 0 ? cachedDTOOptional.get().getStartUuid() : notificationUuid;

			unreadNotificationsDTO = new UnreadNotificationsDTO(startUuid, notificationUuid, cachedDTO.getUnreadCount() + 1);
		} else {
			unreadNotificationsDTO = userNotificationService.getUnreadNotificationsDTO(userId);
		}

		userNotificationCache.putUnreadNotificationInfo(userId, unreadNotificationsDTO);
		userNotificationCache.clearNotifications(userId);
	}

	@Override
	public void processEvent(MarkUserNotificationsAsReadEvent event) {
		checkNotNull(event);
		userNotificationService.setViewedAtNotification(event.getUserId(), checkNotNull(event.getUnreadNotificationsDTO()));
	}

	@Override
	public void processEvent(SearchCSVGenerateEvent event) {
		Assert.notNull(event);
		Assert.notNull(event.getRequest());
		Assert.notNull(event.getRequest().getPaginationRequest());

		ZipCSVSearchWriter writer = new ZipCSVSearchWriter(DateUtilities.getSearchCSVFilename(), Constants.EXPORT_SEARCH_CSV_DIRECTORY);

		PeopleSearchRequest request = event.getRequest();
		request.getPaginationRequest().setPageSize(MAX_EXPORT_CSV_SEARCH_RESULTS);

		try {
			PeopleSearchResponse response = peopleSearchService.searchPeople(event.getRequest());
			ZipCSVSearchExecutor searchExecutor = new ZipCSVSearchExecutor(writer, response);
			searchExecutor.search();
		} catch (Exception e) {
			logger.error("[exportSearch] Error while executing search export to csv", e);
			return;
		}

		long toEmailUserId = request.getUserId();
		if (request.getMasqueradeUserId() > 0) {
			toEmailUserId = request.getMasqueradeUserId();
		}

		User toUser = userService.getUser(toEmailUserId);
		if (toUser != null) {
			try {
				AssetDTO dto = new AssetDTO();
				dto.setSourceFilePath(writer.getAbsolutePath());
				dto.setName(writer.getFilename());
				dto.setMimeType(MimeType.ZIP.toString());
				dto.setAssociationType(UserAssetAssociationType.SEARCH_EXPORT);
				dto.setActive(true);

				FileDTO attachment = new FileDTO();
				attachment.setName(dto.getName());
				attachment.setMimeType(dto.getMimeType());
				attachment.setSourceFilePath(dto.getSourceFilePath());

				Asset asset = assetManagementService.storeAssetForUser(dto, toEmailUserId, false);

				userNotificationService.onSearchCSVGenerated(asset, attachment, toUser.getEmail());
			} catch (Exception e) {
				logger.error("[exportSearch] Error saving asset", e);
			}
		}
	}

	@Override
	public void processEvent(WorkViewedEvent event) {
		try {
			workService.markWorkViewed(event.getWorkId(), event.getUserId(), event.getViewType());
		} catch (Exception e) {
			logger.error("[workViewedEvent] There was an error marking the assignment viewed", e);
		}
	}


	@Override
	public void processEvent(NotifyPostFollowerEvent event) {
		Assert.notNull(event);
		Assert.notNull(event.getPost());
		forumService.notifyPostFollowersEvent(event.getPost());
	}

	@Override
	public void processEvent(CreateWorkFromFlaggedPostEvent event) {
		Assert.notNull(event);
		Assert.notNull(event.getFlaggedPost());
		forumService.createWorkFromFlaggedPostEvent(event.getFlaggedPost());
	}

	private void onUpdateWork(EntityUpdateEvent event) {

		Work work = (Work) event.getEntity();
		Assert.notNull(work);

		Map<PropertyChangeType, List<PropertyChange>> propertyChanges = Maps.newHashMap();
		try {
			for (int i = 0; i < event.getPropertyNames().length; i++) {
				Object oldValue = event.getOldState()[i];
				Object newValue = event.getState()[i];
				String property = event.getPropertyNames()[i];

				if (HibernateUtilities.isRelevantPropertyChange(work, property, oldValue, newValue)) {
					String oldValueString = oldValue == null ? null : ConvertUtils.convert(oldValue);
					String newValueString = newValue == null ? null : ConvertUtils.convert(newValue);

					WorkPropertyChangeLog propertyChangeLog = new WorkPropertyChangeLog();
					propertyChangeLog.setWorkId(work.getId());
					if (event.getUser() != null) {
						propertyChangeLog.setActorId(event.getUser().getId());
					}
					if (event.getMasqueradeUser() != null) {
						propertyChangeLog.setMasqueradeActorId(event.getMasqueradeUser().getId());
					}
					if (event.getOnBehalfOfUser() != null) {
						propertyChangeLog.setOnBehalfOfActorId(event.getOnBehalfOfUser().getId());
					}
					propertyChangeLog.setPropertyName(property);
					propertyChangeLog.setOldValue(oldValueString);
					propertyChangeLog.setNewValue(newValueString);

					PropertyChangeType changeType = PropertyChangeUtilities.getPropertyChangeType(work, property, WorkPropertyChangeType.class);
					if (!propertyChanges.containsKey(changeType)) {
						propertyChanges.put(changeType, Lists.<PropertyChange>newArrayList());
					}
					propertyChanges.get(changeType).add(new PropertyChange(property, oldValueString, newValueString));
					workChangeLogService.saveWorkChangeLog(propertyChangeLog);
				}
			}

			userNotificationService.onWorkUpdated(work.getId(), propertyChanges);
		} catch (Exception e) {
			logger.error("[EntityUpdate] Error processing event ", e);
		}
	}

	private void onUpdateScreening(EntityUpdateEvent event) {
		Screening screening = (Screening) event.getEntity();

		Assert.notNull(screening);

		int i = event.getPropertyIndex("screeningStatusType");

		ScreeningStatusType oldStatus = event.getOldState() != null ? (ScreeningStatusType) event.getOldState()[i] : null;
		ScreeningStatusType newStatus = (ScreeningStatusType) event.getState()[i];

		logger.debug("[screening] screening id: " + screening.getId());
		if (oldStatus != null) {
			logger.debug(String.format("[screening] update screening (%s) status %s to %s", screening.getId(), oldStatus.getCode(), newStatus.getCode()));
		}

		// In theory we're only interested in the state transition to "passed"
		// or "failed" from "requested"...
		// ...but with various difficulties with the background check vendor,
		// there are cases where
		// we're required to go in and remedy an incorrect status.
		// We only care to update the user's record for certain response status
		// types, i.e.: passed, failed, review, etc.

		if (!ArrayUtils.contains(ScreeningStatusType.getResponseTypes(), newStatus.getCode())) {
			return;
		}

		Map<String, Object> params = new HashMap<>();
		if (screening instanceof DrugTest) {
			params.put(ProfileModificationType.DRUG_TEST, screening.getId());
		} else if (screening instanceof BackgroundCheck) {
			params.put(ProfileModificationType.BACKGROUND_CHECK, screening.getId());
		}

		userIndexer.reindexById(screening.getUser().getId());

		userNotificationService.onScreeningResponse(screening);

		// Set the user screening status
		screeningService.updateUserScreeningStatus(screening.getUser().getId());

		if (screening instanceof DrugTest) {
			DrugTest previousDt = (DrugTest) ScreeningObjectConverter.convertScreeningResponseToMonolith(
				screeningService.findPreviousPassedDrugTest(screening.getUser().getId()));
			//null check and ensuring expiration doesn't happen on a user with just one screening
			if (previousDt != null && !screening.getScreeningId().equals(previousDt.getScreeningId())) {
				try {
					// if user has previously passed DrugTest then update to expired regardless of most recent response
					screeningService.updateScreeningStatus(previousDt.getScreeningId(), ScreeningStatusType.EXPIRED);
				} catch (Exception e) {
					logger.error("[DrugTestExpiration] Error expiring drug test ", e);
				}
			}

		} else if (screening instanceof BackgroundCheck) {
			BackgroundCheck previousBg = (BackgroundCheck) ScreeningObjectConverter.convertScreeningResponseToMonolith(
				screeningService.findPreviousPassedBackgroundCheck(screening.getUser().getId()));
			//null check and ensuring expiration doesn't happen on a user with just one screening
			if (previousBg != null && !screening.getScreeningId().equals(previousBg.getScreeningId())) {
				try {
					// if user has previously passed BackgroundCheck then update to expired regardless of most recent response
					screeningService.updateScreeningStatus(previousBg.getScreeningId(), ScreeningStatusType.EXPIRED);
				} catch (Exception e) {
					logger.error("[BackgroundCheckExpiration] Error expiring background check ", e);
				}
			}
		}

		userGroupValidationService.revalidateAllAssociationsByUserAsync(screening.getUser().getId(), params);
	}

	@SuppressWarnings("unchecked")
	private void onCompanyUpdate(long companyId) {

		vendorIndexer.reindexById(companyId);

		UserPagination pagination = new UserPagination();
		pagination.setResultsLimit(100);
		pagination = userService.findAllActiveEmployees(companyId, pagination);

		for (int i = 0; i < pagination.getNumberOfPages(); i++) {
			List<Long> ids = CollectionUtilities.newListPropertyProjection(pagination.getResults(), "id");
			userIndexer.reindexById(ids);
			pagination.nextPage();
			pagination = userService.findAllActiveEmployees(companyId, pagination);
		}

	}

}
