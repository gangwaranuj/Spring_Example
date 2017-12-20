package com.workmarket.service.business;

import au.com.bytecode.opencsv.CSVReader;
import com.Ostermiller.util.RandPass;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.common.template.NotificationTemplateFactory;
import com.workmarket.domains.model.CallingCode;
import com.workmarket.domains.model.MimeType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.asset.Upload;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.business.dto.UserImportDTO;
import com.workmarket.service.business.event.BulkUserUploadDispatchEvent;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.upload.users.model.BulkUserUploadCompletionStatus;
import com.workmarket.service.business.upload.users.model.BulkUserUploadRequest;
import com.workmarket.service.business.upload.users.model.BulkUserUploadResponse;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.business.UploadService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.file.RemoteFileAdapter;
import com.workmarket.service.infra.file.RemoteFileType;
import com.workmarket.service.infra.notification.NotificationDispatcher;
import com.workmarket.service.orgstructure.OrgStructureService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.FileUploadUtilities;
import com.workmarket.utility.RandomUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.UserImportValidator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static com.workmarket.service.business.UserBulkUploadServiceImpl.UserColumn.EMAIL;
import static com.workmarket.service.business.UserBulkUploadServiceImpl.UserColumn.FIRST_NAME;
import static com.workmarket.service.business.UserBulkUploadServiceImpl.UserColumn.JOB_TITLE;
import static com.workmarket.service.business.UserBulkUploadServiceImpl.UserColumn.LAST_NAME;
import static com.workmarket.service.business.UserBulkUploadServiceImpl.UserColumn.ROLE;
import static com.workmarket.service.business.UserBulkUploadServiceImpl.UserColumn.WORK_PHONE;
import static com.workmarket.service.business.UserBulkUploadServiceImpl.UserColumn.WORK_PHONE_EXT;
import static com.workmarket.service.business.UserBulkUploadServiceImpl.UserColumn.WORK_PHONE_INTERNATIONAL_CODE;
import static com.workmarket.service.business.UserBulkUploadServiceImpl.UserColumnForOrgEnabled.ORG_UNIT_NAME;

@Service
public class UserBulkUploadServiceImpl implements UserBulkUploadService {

	private static final Log logger = LogFactory.getLog(UserBulkUploadServiceImpl.class);
	private static final String ALL_BULK_USER_UPLOAD_IN_PROGRESS_KEY = RedisFilters.userBulkUserUploadAllInProgressKey();
	private final static String US_CALLING_CODE = "1";
	private static final int NUM_USER_COLUMNS = UserBulkUploadServiceImpl.UserColumn.values().length;
	private static final Integer ROW_OFFSET = 1;

	private static final String FIRST_NAME_LABEL = "First Name";
	private static final String LAST_NAME_LABEL = "Last Name";
	private static final String EMAIL_LABEL = "Email";
	private static final String WORK_PHONE_LABEL = "Work Phone";
	private static final String WORK_PHONE_EXT_LABEL = "Work Phone Ext.";
	private static final String WORK_PHONE_INTERNATIONAL_CODE_LABEL = "Work Phone International Code";
	private static final String JOB_TITLE_LABEL = "Job Title";
	private static final String ROLE_LABEL = "Role";
	private static final String ORG_LABEL = "Org";
	private static final int FIRST_NAME_INDEX = 0;
	private static final int LAST_NAME_INDEX = 1;
	private static final int EMAIL_LABEL_INDEX = 2;
	private static final int WORK_PHONE_LABEL_INDEX = 3;
	private static final int WORK_PHONE_EXT_LABEL_INDEX = 4;
	private static final int WORK_PHONE_INTERNATIONAL_CODE_LABEL_INDEX = 5;
	private static final int JOB_TITLE_LABEL_INDEX = 6;
	private static final int ROLE_LABEL_INDEX = 7;
	private static final int ORG_LABEL_INDEX = 8;

	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private RegistrationService registrationService;
	@Autowired private ProfileService profileService;
	@Autowired private UserService userService;
	@Autowired @Qualifier("userImportValidator")
	private UserImportValidator userValidator;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private UploadService uploadService;
	@Autowired private NotificationTemplateFactory notificationTemplateFactory;
	@Autowired private NotificationDispatcher notificationDispatcher;
	@Autowired private RedisAdapter redisAdapter;
	@Autowired private RemoteFileAdapter remoteFileAdapter;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private EventFactory eventFactory;
	@Autowired private EventRouter eventRouter;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired private OrgStructureService orgStructureService;
	@Autowired private WebRequestContextProvider webRequestContextProvider;


	@Value("${bulk.user.upload.size.limit}")
	private String MAX_UPLOAD_EMPLOYEES;
	private Meter uploadMeter;
	private Histogram uploadSizeMetric;
	private Meter uploadWithValidationErrorMetric;
	private Meter uploadWithSystemErrorMetric;
	private Meter uploadSuccessMetric;

	@PostConstruct
	private void init() {
		WMMetricRegistryFacade wmMetricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "user_bulk_upload_service");
		uploadMeter = wmMetricRegistryFacade.meter("employee_upload");
		uploadSizeMetric = wmMetricRegistryFacade.histogram("size");
		uploadWithValidationErrorMetric = wmMetricRegistryFacade.meter("validation_error");
		uploadWithSystemErrorMetric = wmMetricRegistryFacade.meter("system_error");
		uploadSuccessMetric = wmMetricRegistryFacade.meter("success");
	}

	@Override
	public void start(final BulkUserUploadRequest request, final BulkUserUploadResponse response, final boolean orgEnabledForUser) {
		Assert.notNull(request);
		Assert.notNull(request.getUUID());
		response.setUser(userService.findUserById(request.getUserId()));
		response.setFileUUID(request.getUUID());
		sendMeterMetric(uploadMeter, 1L);
		try {
			final Optional<Map<UserImportDTO, List<String>>> userOrgPathsMap = bulkUploadFileReadAndValidate(
				request,
				response,
				orgEnabledForUser);

			if (!response.getErrors().isEmpty()) {
				logger.info("File validation completed with errors, uuid: " + request.getUUID());
				response.setStatus(BulkUserUploadCompletionStatus.COMPLETED_WITH_VALIDATION_ERROR);
				finish(response);
			} else {
				dispatchUpload(response, orgEnabledForUser, userOrgPathsMap);
			}
		} catch (Exception e) {
			logger.error("There is an error occurred when starting user bulk upload.", e);
			response.addError(e.getMessage());
		}
	}

	@Override
	public void upload(
		final Long userId,
		final String uuid,
		final UserImportDTO userImportDTO,
		final List<String> orgUnitPaths) {

		final User currentUser = userService.findUserById(userId);
		final String companyUuid = currentUser.getCompany().getUuid();
		boolean success = false;
		final String uploadSuccessCounterKey = RedisFilters.userBulkUserUploadSuccessCounterKey(userId, uuid);
		final String failedUploadKey = RedisFilters.userBulkUserFailedUploadKey(userId, uuid);

		try {
			Thread.sleep(RandomUtilities.nextIntInRange(50, 150));
			UserDTO userDTO = new UserDTO();
			RandPass randomPasswords = new RandPass(new char[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0});
			userDTO.setFirstName(userImportDTO.getFirstName());
			userDTO.setLastName(userImportDTO.getLastName());
			userDTO.setEmail(userImportDTO.getEmail());
			userDTO.setPassword(randomPasswords.getPass(4));

			List<Long> roles = Lists.newLinkedList();
			AclRole role = authenticationService.findSystemRoleByName(userImportDTO.getRole());
			Assert.notNull(role);
			roles.add(role.getId());

			User newUser = registrationService.registerNewForCompany(userDTO, currentUser.getCompany().getId(), roles.toArray(new Long[roles.size()]));
			CallingCode callingCode = invariantDataService.findCallingCodeFromCallingCodeId(userImportDTO.getWorkPhoneInternationalCode());
			if (callingCode == null) {
				callingCode = invariantDataService.findCallingCodeFromCallingCodeId(US_CALLING_CODE); //if calling code is missing, then default to US
			}
			Map<String, String> profileMap = CollectionUtilities.newStringMap(
				"workPhone", StringUtilities.formatPhoneNumber(userImportDTO.getWorkPhone()),
				//reverse lookup from calling_code_id to id
				"workPhoneInternationalCode", String.valueOf(callingCode.getId()),
				"workPhoneExtension", userImportDTO.getWorkPhoneExtension(),
				"jobTitle", userImportDTO.getJobTitle());

			authenticationService.setCustomAccess(false, false, false, false, false, false, newUser.getId());

			profileService.updateProfileProperties(newUser.getId(), profileMap);
			if (redisAdapter.increment(uploadSuccessCounterKey, 1, (long) DateTimeConstants.SECONDS_PER_DAY) == -1) {
				throw new RuntimeException(String.format("Operation failed when updating upload success counter for key: %s", uploadSuccessCounterKey));
			}
			boolean orgAssignStatus = false;
			if (!CollectionUtils.isEmpty(orgUnitPaths)) {
				final String orgChartUuid = orgStructureService.getOrgChartUuidFromCompanyUuid(companyUuid);
				if (!StringUtils.isEmpty(orgChartUuid)) {
					orgAssignStatus = orgStructureService.assignUsersFromBulk(
						orgUnitPaths,
						newUser.getUuid(),
						newUser.getCompany().getUuid(),
						newUser.getEmail(),
						orgChartUuid);
				} else {
					logger.error(String.format("Failed to get orgChartUuid for companyUuid:%s", companyUuid));
				}
			}
			success = CollectionUtils.isEmpty(orgUnitPaths) || orgAssignStatus;
		} catch (InterruptedException e) {
			logger.error("Employee Bulk Upload Interrupted", e);
		} catch (Exception e) {
			logger.error("Exception caught when uploading users.", e);
		} finally {
			try {
				if (!success) {
					redisAdapter.addToSet(failedUploadKey, new HashSet<>(Arrays.asList(userImportDTO.toString())), (long) DateTimeConstants.SECONDS_PER_DAY);
					registrationService.updateUserStatusToDeleted(userImportDTO.getEmail());
				}
			} catch (Exception e) {
				logger.error("Exception caught when setting failed users to cache.", e);
			}
		}
	}

	@Override
	public void finish(BulkUserUploadResponse response) {
		String originalUUID = response.getFileUUID();
		try {
			switch (response.getStatus()) {
				case COMPLETED_WITH_VALIDATION_ERROR:
					if (response.getUserUploads() == null) {
						//send notification with original file link
						notificationDispatcher.dispatchNotification(notificationTemplateFactory.buildBulkUserUploadFailedNotificationTemplate(response.getUser().getId(), response, true));
					} else {
						//send notification with new csv file with inline error
						List<String> headers = UserBulkUploadServiceImpl.getUserColumns();
						String uuid = generateCSVFileWithError(headers, response.getUserUploads(), originalUUID);
						response.setFileUUID(uuid);
						notificationDispatcher.dispatchNotification(notificationTemplateFactory.buildBulkUserUploadFailedNotificationTemplate(response.getUser().getId(), response, false));
					}
					sendMeterMetric(uploadWithValidationErrorMetric, new Integer(response.getNumOfRowsWithValidationError()).longValue());
					break;
				case COMPLETED_WITH_SYSTEM_ERROR:
					Set<String> users = redisAdapter.getSet(RedisFilters.userBulkUserFailedUploadKey(response.getUser().getId(), originalUUID));
					String uuid = generateCSVFileWithError(UserBulkUploadServiceImpl.getUserColumns(), ImmutableList.copyOf(users), originalUUID);
					response.setFileUUID(uuid);
					notificationDispatcher.dispatchNotification(notificationTemplateFactory.buildBulkUserUploadFailedNotificationTemplate(response.getUser().getId(), response, false));
					sendMeterMetric(uploadWithSystemErrorMetric, new Integer(users.size()).longValue());
					break;
				case COMPLETED_WITH_NO_ERROR:
				default:
					Integer uploadCount = Integer.valueOf((String) redisAdapter.get(RedisFilters.userBulkUserUploadSuccessCounterKey(response.getUser().getId(), originalUUID)).get());
					response.setUploadCount(uploadCount);
					notificationDispatcher.dispatchNotification(notificationTemplateFactory.buildBulkUserUploadFinishedNotificationTemplate(response.getUser().getId(), response));
					sendMeterMetric(uploadSuccessMetric, uploadCount.longValue());
					break;
			}

			String uploadKey = RedisFilters.userBulkUserUploadKey(response.getUser().getId(), originalUUID);
			redisAdapter.removeFromSet(ALL_BULK_USER_UPLOAD_IN_PROGRESS_KEY, Lists.newArrayList(uploadKey));

		} catch (Exception e) {
			logger.error("Exception caught when dispatching notification", e);
		}
	}

	private File getRemoteFile(String fileUUID) throws Exception {
		return remoteFileAdapter.getFile(RemoteFileType.TMP, fileUUID);
	}

	private String generateCSVFileWithError(List<String> cvsHeader, ImmutableList<? extends Object> users, String sourceUUID) throws Exception {
		// Prepare data
		List<String> csv = Lists.newArrayList();
		csv.add(StringUtils.join(cvsHeader, ","));
		for (Object user : users) {
			if (user instanceof UserImportDTO) {
				csv.add(StringUtils.join(((UserImportDTO) user).toCSVRow(), ","));
			} else if (user instanceof String) {
				csv.add((String) user);
			}
		}

		Upload originalFile = uploadService.findUploadByUUID(sourceUUID);
		String data = StringUtils.join(csv, "\n");
		InputStream is = new ByteArrayInputStream(data.getBytes());
		Upload upload = uploadService.storeUpload(is, originalFile.getFilename(), MimeType.TEXT_CSV.getMimeType(), data.getBytes().length);
		return upload.getUUID();
	}

	Optional<Map<UserImportDTO, List<String>>> bulkUploadFileReadAndValidate(final BulkUserUploadRequest request,
	                                                                   final BulkUserUploadResponse response,
	                                                                   final boolean isOrgEnabledForUser) throws Exception {
		Assert.notNull(request.getUUID());
		final MessageBundle messages = messageHelper.newBundle();

		final File file = getRemoteFile(request.getUUID());
		final CSVReader reader = new CSVReader(new FileReader(file));
		if (FileUploadUtilities.isFileEmpty(file, ROW_OFFSET)) {
			messageHelper.addError(messages, "users.upload.file_empty");
		} else if (isOrgEnabledForUser) {
			parseFile(file, reader, messages, getUserColumnsForOrgEnabled());
		} else {
			parseFile(file, reader, messages, getUserColumns());
		}
		if (messages.hasErrors()) {
			response.addAllErrors(messages.getErrors());
			return Optional.absent();
		} else {
			return Optional.of(validateParsedFile(reader, response, isOrgEnabledForUser));
		}
	}

	private void parseFile(final File file,
	                       final CSVReader reader,
	                       final MessageBundle messages,
	                       final List<String> userColumns) throws IOException {

		final List<String> headerErrors = FileUploadUtilities.handleHeader(reader.readNext(), userColumns);
		if (!headerErrors.isEmpty()) {
			for (final String error : headerErrors) {
				messageHelper.addError(messages, error);
			}
		} else if (!FileUploadUtilities.hasUserData(file, ROW_OFFSET)) {
			messageHelper.addError(messages, "users.upload.file_no_user_data");
		} else if (FileUploadUtilities.IsImportSizeExceeded(file, ROW_OFFSET, Integer.valueOf(MAX_UPLOAD_EMPLOYEES))) {
			messageHelper.addError(messages, "users.upload.row_limit_exceeded", Integer.valueOf(MAX_UPLOAD_EMPLOYEES));
		}
	}

	private Map<UserImportDTO, List<String>> validateParsedFile(final CSVReader reader,
	                                                      final BulkUserUploadResponse response,
	                                                      final boolean isOrgEnabledForUser) throws Exception {

		int numOfRowsWithValidationError = 0;
		String[] row;
		UserImportDTO dto;
		final LinkedHashSet<UserImportDTO> users = Sets.newLinkedHashSet();
		final Map<UserImportDTO, List<String>> userOrgPathsMap = new HashMap<>();
		while ((row = reader.readNext()) != null) {
			dto = new UserImportDTO();
			DataBinder dataBinder = new DataBinder(dto);
			BindingResult binding = dataBinder.getBindingResult();

			dto.setFirstName(FIRST_NAME.getValue(row));
			dto.setLastName(LAST_NAME.getValue(row));
			dto.setEmail(EMAIL.getValue(row));
			dto.setJobTitle(JOB_TITLE.getValue(row));
			dto.setWorkPhone(WORK_PHONE.getValue(row));
			dto.setWorkPhoneExtension(WORK_PHONE_EXT.getValue(row));
			dto.setWorkPhoneInternationalCode(WORK_PHONE_INTERNATIONAL_CODE.getValue(row));
			dto.setRole(ROLE.getValue(row));

			//validate user input
			if (!dto.isEmpty()) {
				userValidator.validate(dto, binding);
			} else {
				continue;
			}

			if (binding.hasErrors()) {
				List<String> errorMessages = extract(binding.getAllErrors(), on(ObjectError.class).getDefaultMessage());
				response.addError(StringUtils.join(errorMessages, " "));
				dto.setError(StringUtils.join(errorMessages, " "));
				numOfRowsWithValidationError++;
			}
			users.add(dto);
			if (isOrgEnabledForUser) {
				final String orgPath = ORG_UNIT_NAME.getValue(row);
				if (StringUtils.isBlank(orgPath)) {
					throw new RuntimeException("Empty org path for user:" + dto.toString());
				}
				final List<String> existingOrgPaths;
				if (!userOrgPathsMap.containsKey(dto)) {
					existingOrgPaths = new ArrayList<>();
				} else {
					existingOrgPaths = userOrgPathsMap.get(dto);
				}
				existingOrgPaths.add(orgPath);
				userOrgPathsMap.put(dto, existingOrgPaths);
			}
		}
		response.setNumOfRowsWithValidationError(numOfRowsWithValidationError);
		response.setUserUploads(ImmutableSet.copyOf(users).asList());
		return userOrgPathsMap;
	}

	void dispatchUpload(final BulkUserUploadResponse response,
	                    final boolean isOrgEnabledForUser,
	                    final Optional<Map<UserImportDTO,List<String>>> orgUnitPathMap) {
		Integer totalUploadSize = response.getUserUploads().size();
		sendHistogramMetric(uploadSizeMetric, totalUploadSize);

		String uploadKey = RedisFilters.userBulkUserUploadKey(response.getUser().getId(), response.getFileUUID());
		redisAdapter.addToSet(RedisFilters.userBulkUserUploadAllInProgressKey(), uploadKey);

		String uploadSizeKey = RedisFilters.userBulkUserUploadSizeKey(response.getUser().getId(), response.getFileUUID());
		redisAdapter.set(uploadSizeKey, String.valueOf(totalUploadSize), (long) DateTimeConstants.SECONDS_PER_DAY);
		Integer totalBatches = 0;
		for(UserImportDTO dto : response.getUserUploads()) {
			totalBatches++;
			final BulkUserUploadDispatchEvent event = eventFactory.buildBulkUserUploadDispatchEvent(
				response.getUser().getId(),
				response.getFileUUID(),
				dto,
				isOrgEnabledForUser,
				orgUnitPathMap.isPresent() ? orgUnitPathMap.get().get(dto) : null);
			eventRouter.sendEvent(event);
		}
		logger.info(String.format("Total %s %s dispatched, file uuid: %s", totalBatches, StringUtilities.pluralize("batch", totalBatches), response.getFileUUID()));
	}

	protected void sendHistogramMetric(Histogram histogram, Integer value) {
		histogram.update(value);
	}

	protected void sendMeterMetric(Meter meter, Long value) {
		meter.mark(value);
	}

	enum UserColumn {
		FIRST_NAME(FIRST_NAME_INDEX, FIRST_NAME_LABEL),
		LAST_NAME(LAST_NAME_INDEX, LAST_NAME_LABEL),
		EMAIL(EMAIL_LABEL_INDEX, EMAIL_LABEL),
		WORK_PHONE(WORK_PHONE_LABEL_INDEX, WORK_PHONE_LABEL),
		WORK_PHONE_EXT(WORK_PHONE_EXT_LABEL_INDEX, WORK_PHONE_EXT_LABEL),
		WORK_PHONE_INTERNATIONAL_CODE(WORK_PHONE_INTERNATIONAL_CODE_LABEL_INDEX, WORK_PHONE_INTERNATIONAL_CODE_LABEL),
		JOB_TITLE(JOB_TITLE_LABEL_INDEX, JOB_TITLE_LABEL),
		ROLE(ROLE_LABEL_INDEX, ROLE_LABEL);

		private final int index;
		private final String name;

		UserColumn(final int index, final String name) {
			this.index = index;
			this.name = name;
		}

		public String getValue(String[] row) {
			return (row != null && row.length > index) ? row[index] : null;
		}
	}

	enum UserColumnForOrgEnabled {
		FIRST_NAME(FIRST_NAME_INDEX, FIRST_NAME_LABEL),
		LAST_NAME(LAST_NAME_INDEX, LAST_NAME_LABEL),
		EMAIL(EMAIL_LABEL_INDEX, EMAIL_LABEL),
		WORK_PHONE(WORK_PHONE_LABEL_INDEX, WORK_PHONE_LABEL),
		WORK_PHONE_EXT(WORK_PHONE_EXT_LABEL_INDEX, WORK_PHONE_EXT_LABEL),
		WORK_PHONE_INTERNATIONAL_CODE(WORK_PHONE_INTERNATIONAL_CODE_LABEL_INDEX, WORK_PHONE_INTERNATIONAL_CODE_LABEL),
		JOB_TITLE(JOB_TITLE_LABEL_INDEX, JOB_TITLE_LABEL),
		ROLE(ROLE_LABEL_INDEX, ROLE_LABEL),
		ORG_UNIT_NAME(ORG_LABEL_INDEX, ORG_LABEL);

		private final int index;
		private final String name;

		UserColumnForOrgEnabled(final int index, final String name) {
			this.index = index;
			this.name = name;
		}

		public String getValue(String[] row) {
			return (row != null && row.length > index) ? row[index] : null;
		}
	}

	private static List<String> getUserColumns() {
		List<String> userColumns = Lists.newArrayListWithCapacity(NUM_USER_COLUMNS);
		for (UserBulkUploadServiceImpl.UserColumn column : UserBulkUploadServiceImpl.UserColumn.values()) {
			userColumns.add(column.name);
		}
		return userColumns;
	}

	private static List<String> getUserColumnsForOrgEnabled() {
		final List<String> userColumns = Lists.newArrayListWithCapacity(UserColumnForOrgEnabled.values().length);
		for (final UserColumnForOrgEnabled column : UserColumnForOrgEnabled.values()) {
			userColumns.add(column.name);
		}
		return userColumns;
	}

}
