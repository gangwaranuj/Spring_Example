package com.workmarket.service.thrift;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.collect.*;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.CompanyPreference;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Upload;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.model.WorkUploadColumnType;
import com.workmarket.domains.work.model.audit.WorkAuditType;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.WorkTemplateService;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.domains.work.service.validator.WorkSaveRequestValidator;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.CustomFieldService;
import com.workmarket.service.business.queue.WorkUploadProcessQueue;
import com.workmarket.service.business.upload.WorkUploadTemplateFactory;
import com.workmarket.service.business.upload.parser.*;
import com.workmarket.service.business.upload.transactional.WorkUploadBuilder;
import com.workmarket.service.business.upload.transactional.WorkUploadColumnService;
import com.workmarket.service.business.upload.transactional.WorkUploadHelperService;
import com.workmarket.service.business.upload.transactional.WorkUploadMappingService;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.UploadService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.file.RemoteFileAdapter;
import com.workmarket.service.infra.file.RemoteFileType;
import com.workmarket.service.thrift.work.upload.WorkUploadOptions;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.*;
import com.workmarket.thrift.work.exception.WorkRowParseError;
import com.workmarket.thrift.work.exception.WorkRowParseErrorType;
import com.workmarket.thrift.work.uploader.*;
import com.workmarket.utility.SerializationUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.helpers.ThriftCustomFieldGroupHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@Service
public class TWorkUploadServiceImpl implements TWorkUploadService {

	private static final Log logger = LogFactory.getLog(TWorkUploadServiceImpl.class);
	private static final int UPLOADS_PREVIEW_SIZE = 25;

	@Autowired private AuthenticationService authenticationService;
	@Autowired private CompanyService companyService;
	@Autowired private WorkUploadColumnService columnService;
	@Autowired private WorkUploadMappingService mappingService;
	@Autowired private WorkUploadHelperService helperService;
	@Autowired private UploadService uploadService;
	@Autowired private WorkTemplateService templateService;
	@Autowired private WorkUploadProcessQueue workUploadProcessQueue;
	@Autowired private WorkSubStatusService workSubStatusService;
	@Autowired private WorkSaveRequestValidator workSaveRequestValidator;
	@Autowired private RemoteFileAdapter remoteFileAdapter;
	@Autowired private WorkUploadBuilder workUploadBuilder;
	@Autowired private WorkUploadTemplateFactory workUploadTemplateFactory;
	@Autowired private EventRouter eventRouter;
	@Autowired private CustomFieldService customFieldService;

	@Override
	public void deleteMapping(DeleteMappingRequest request) throws WorkUploadException {
		mappingService.deleteMapping(request);
	}

	@Override
	public FindMappingsResponse findMappings(FindMappingsRequest request) throws WorkUploadException {
		return mappingService.findMappings(request);
	}

	@Override
	public void renameMapping(RenameMappingRequest request) throws WorkUploadException {
		mappingService.renameMapping(request);
	}

	@Override
	public FieldMappingGroup saveMapping(SaveMappingRequest request) throws WorkUploadException, WorkUploadDuplicateMappingGroupNameException {
		return mappingService.saveMapping(request);
	}

	@Override
	public List<FieldCategory> getFieldCategories() {
		return columnService.getFieldCategories();
	}

	@Override
	public List<FieldCategory> getFieldCategoriesForTemplate(long templateId) {
		return columnService.getFieldCategoriesForTemplate(templateId);
	}

	@Override
	public List<FieldCategory> getFieldCategoriesForUpload(WorkUploadRequest request, WorkUploadResponse response) {
		Set<Long> templateIds = Sets.newHashSet();
		if (request.isSetTemplateId()) {
			templateIds.add(request.getTemplateId());
		}
		for (WorkUpload u : response.getUploads()) {
			for (WorkUploadValue v : u.getValues()) {
				if (WorkUploadColumn.TEMPLATE_ID.getUploadColumnName().equals(v.getType().getCode())) {
					Long templateId = StringUtilities.parseLong(v.getValue());
					if (templateId != null) {
						templateIds.add(templateId);
					}
				}
			}
		}
		return columnService.getFieldCategoriesForTemplates(templateIds);
	}

	@Override
	public WorkUploadResponse uploadWork(WorkUploadRequest request)
			throws WorkUploadException, WorkUploadInvalidFileTypeException, WorkUploadRowLimitExceededException, HostServiceException {

		WorkUploadOptions options = new WorkUploadOptions();
		options.setPreview(false);
		return uploadWork(request, options);
	}

	@Override
	public WorkUploadResponse uploadWorkPreview(WorkUploadRequest request)
			throws WorkUploadException, WorkUploadInvalidFileTypeException, WorkUploadRowLimitExceededException {

		WorkUploadOptions options = new WorkUploadOptions();
		options.setPreview(true);
		return uploadWork(request, options);
	}

	protected WorkUploadResponse uploadWork(WorkUploadRequest request, WorkUploadOptions options)
			throws WorkUploadException, WorkUploadInvalidFileTypeException, WorkUploadRowLimitExceededException {

		StopWatch timer = new StopWatch("doUpload");

		try {
			timer.start("parseCSV");

			LinkedList<String[]> assignments = loadAssignmentsFromCSV(request.getUploadUuid());

			timer.stop();

			validateParsedCSV(assignments, request.isHeadersProvided());

			timer.start("mapping");

			WorkUploadResponse response = new WorkUploadResponse();
			FieldMappingGroup mappingGroup = buildMappingGroup(request, assignments, response);
			response.setMappingGroup(mappingGroup);

			timer.stop();

			if (request.isHeadersProvided()) {
				assignments.removeFirst();
			}
			response.setUploadCount(assignments.size());

			authenticationService.setCurrentUser(request.getUserNumber());
			User currentUser = authenticationService.getCurrentUser();

			// Build lookup table of the company's templates
			BiMap<String, Long> templateLookup = ImmutableBiMap.<Long, String>builder()
					.putAll(templateService.findAllActiveWorkTemplatesIdNumberMap(currentUser.getCompany().getId()))
					.build()
					.inverse();

			// Build work structure for validation and (optionally) persistence
			int lineNum = 0;
			List<WorkSaveRequest> workSaveRequestList = Lists.newLinkedList();

			Multimap<WorkUploadLocation, Integer> newLocations = LinkedListMultimap.create();
			Multimap<WorkUploadLocationContact, Integer> newContacts = LinkedListMultimap.create();
			Multimap<WorkBundle, Work> newBundles = LinkedListMultimap.create();
			final List<FieldMapping> columnTypes = mappingGroup.getMappings();

			CompanyPreference companyPreference = companyService.getCompanyPreference(currentUser.getCompany().getId());
			// Track 'unique external Ids' from CSV assignments so we can catch duplicates and report validation error
			Set<String> uploadedUniqueExternalIds = Sets.newHashSet();

			for (String[] line : assignments) {
				timer.start("parseRow");

				WorkUpload workUpload = new WorkUpload().setWorkNumber(StringUtils.EMPTY);

				// Generate column type to cell value map that parsers need to construct Work object
				Map<String,String> typesToValues = Maps.newHashMap();
				Map<String,String> ignoredNamesToValues = Maps.newHashMap();
				Work work = null;
				Integer paymentTermsDays = companyService.getDefaultPaymentTermsDays();

				for (int j = 0; j < line.length; j++) {
					final String type = (j < columnTypes.size()) ?
							columnTypes.get(j).getType().getCode() :
							WorkUploadColumnType.IGNORE_TYPE;
					final String value = StringUtils.trim(line[j]);

					if (WorkUploadColumnType.IGNORE_TYPE.equals(type)) {
						if (j < columnTypes.size()) {
							ignoredNamesToValues.put(columnTypes.get(j).getColumnName(), value);
						}
					} else {
						if (WorkUploadColumn.TEMPLATE_NUMBER.getUploadColumnName().equals(type)) {
							// Rows are allowed to override the default template.
							// Extract requested template and build data for copy.
							Long templateId = templateLookup.get(value);
							if (templateId != null) {
								work = workUploadTemplateFactory.getTemplate(templateId, currentUser.getId());
							}
						}
						typesToValues.put(type, value);
					}
				}

				if (work == null) {
					// if "default" templateId provided then use it
					if (request.getTemplateId() > 0L) {
						work = workUploadTemplateFactory.getTemplate(request.getTemplateId(), currentUser.getId());
					}
					// if "default" templateId is NOT provided or NOT found use default new Work instance
					if (work == null) {
						work = new Work().setConfiguration(new ManageMyWorkMarket().setPaymentTermsDays(paymentTermsDays));
					}
				}

				WorkCustomFieldGroup requiredGroup = customFieldService.findRequiredWorkCustomFieldGroup(currentUser.getCompany().getId());

				if (requiredGroup != null) {
					ThriftCustomFieldGroupHelper.setRequiredThriftCustomFieldGroup(requiredGroup, work);
				}

				WorkUploaderBuildData buildData = new WorkUploaderBuildData()
						.setWork( (Work) SerializationUtilities.clone(work))
						.setTypes(typesToValues)
						.setLineNumber(lineNum)
						.setBundles(newBundles.keySet())
						.setTemplateLookup(templateLookup);

				WorkUploaderBuildResponse buildResponse = workUploadBuilder.buildFromRow(buildData);

				newLocations.putAll(buildResponse.getNewLocations());
				newContacts.putAll(buildResponse.getNewContacts());
				newBundles.putAll(buildResponse.getNewBundles());

				// For previews, extract values from new Work object
				if (options.isPreview()) {

					List<WorkUploadValue> values = workUploadTemplateFactory.extractValues(buildResponse);

					for (WorkUploadValue workUploadValue : values) {
						String ignoredValue = ignoredNamesToValues.remove(workUploadValue.getType().getDescription());

						if (ignoredValue != null) {
							workUploadValue.setValue(ignoredValue);
						}

						workUploadValue.setFromTemplate(typesToValues.containsKey(workUploadValue.getType().getCode()));
					}

					sortValues(values);
					workUpload.setValues(values);
				}

				if (request.isSetLabelId()) {
					WorkSubStatusType label = workSubStatusService.findWorkStatusById(request.getLabelId()); // TODO: validate company/scope
					work.addToSubStatuses(new SubStatus(label.getCode(), label.getDescription(), null, label.isUserResolvable(), label.getId()));
				}

				WorkSaveRequest saveRequest = new WorkSaveRequest()
						.setWork(buildResponse.getWork())
						.setUserId(currentUser.getId())
						.setTemplateId(request.getTemplateId())
						.setLabelId(request.getLabelId());

				if (buildResponse.getWorkBundle() != null) {
					saveRequest.setBundleTitle(buildResponse.getWorkBundle().getTitle());
					saveRequest.setBundleDescription(buildResponse.getWorkBundle().getDescription());
				}

				List<ConstraintViolation> violations = workSaveRequestValidator.getConstraintViolations(saveRequest);

				if (companyPreference.isExternalIdActive()) {
					// Check for duplicate unique external IDs in the uploaded file
					String uniqueExternalIdValue = saveRequest.getWork().getUniqueExternalIdValue();
					if (uniqueExternalIdValue != null) {

						// add validation error if this unique external id was already encountered in the file
						if (!uploadedUniqueExternalIds.add(uniqueExternalIdValue)) {
							WorkRowParseError uploadError = ParseUtils.createErrorRow(uniqueExternalIdValue,
								String.format("%s value %s is already referenced in this file",
									companyPreference.getExternalIdDisplayName(), uniqueExternalIdValue),
								WorkRowParseErrorType.INVALID_DATA, WorkUploadColumn.UNIQUE_EXTERNAL_ID);
							workUpload.addToErrors(convertParseErrorToThriftError(uploadError));
						}
					}
				}

				// Build parse and validation errors
				for (WorkRowParseError error : buildResponse.getErrors()) {
					workUpload.addToErrors(convertParseErrorToThriftError(error));
				}

				for (ConstraintViolation violation : violations) {
					workUpload.addToErrors(convertViolationToError(violation));
				}

				// if NOT a preview and no errors then create a WorkSaveRequest
				if (!options.isPreview() && (workUpload.getErrorsSize() == 0)) {
					workSaveRequestList.add(saveRequest);
				}

				lineNum++;
				workUpload.setLineNumber(lineNum);
				response.addToUploads(workUpload);

				if (workUpload.getErrorsSize() > 0) {
					response.addToErrorUploads(workUpload);
				}

				timer.stop();

				if (options.isPreview() && lineNum >= UPLOADS_PREVIEW_SIZE) {
					break;
				}
			}

			// TODO: check total terms for all assignments and give a warning

			// Save all the assignments at once.
			if (!options.isPreview() && !workSaveRequestList.isEmpty() && response.getErrorUploadsSize() == 0) {
				List<Long> uploadedWorkIds = Lists.newLinkedList();
				response.setUploads(null);
				try {
					timer.start("save");
					helperService.saveUpload(workSaveRequestList, newLocations, newContacts, currentUser.getCompany().getId());
					timer.stop();

					timer.start("buildUploadResponse");
					int resourceCount = 0;
					for (WorkSaveRequest workSaveRequest : workSaveRequestList) {
						List<Resource> resources = workSaveRequest.getWork().getResources();

						if (resources != null) { resourceCount += resources.size(); }
					}

					response.setResourceCount(resourceCount);
					timer.stop();
				} catch (ValidationException e) {
					timer.stop();
					logger.error("There was a problem saving the assignments", e);
				}
				timer.start("postUploadWork");
				onPostUploadWork(uploadedWorkIds, currentUser.getId());
				timer.stop();
			}

			logger.debug(timer.prettyPrint());

			return response;
		} catch (IOException e) {
			logger.error("There was a problem with the IO service", e);
			throw new WorkUploadException();
		} catch (HostServiceException e) {
			logger.error("There was a problem with the host service", e);
			throw new WorkUploadException();
		}
	}

	private FieldMappingGroup buildMappingGroup(WorkUploadRequest request, List<String[]> assignments, WorkUploadResponse response) {
		// Load mappings via (1) request (2) ID or (3) inferred from CSV headers
		FieldMappingGroup mappingGroup;
		if (request.getMappingGroup() != null) {
			mappingGroup = request.getMappingGroup();
		}
		else if (request.getMappingGroupId() > 0L) {
			mappingGroup = mappingService.getMappingGroupById(request, assignments, response);
		}
		else {
			mappingGroup = mappingService.createMappingGroup(assignments, request.isHeadersProvided());
		}
		return mappingGroup;
	}

	private void validateParsedCSV(List<String[]> assignments, boolean headersProvided) throws WorkUploadRowLimitExceededException {
		// n + 1 in order to account for possible header row
		if (assignments.size() > (Constants.MAX_UPLOAD_ASSIGNMENTS + 1)) {
			throw new WorkUploadRowLimitExceededException(
					String.format("Your file exceeds the limit of %s records.", Constants.MAX_UPLOAD_ASSIGNMENTS));
		}
		if (assignments.size() < (headersProvided ? 2 : 1)) {
			throw new WorkUploadRowLimitExceededException("Nothing to upload!");
		}
	}

	private LinkedList<String[]> loadAssignmentsFromCSV(String uploadUuid)
			throws WorkUploadInvalidFileTypeException, HostServiceException, IOException {

		Upload upload = uploadService.findUploadByUUID(uploadUuid);

		if (!upload.getFilename().endsWith(".csv")) {
			throw new WorkUploadInvalidFileTypeException("File extension must be .csv!");
		}

		InputStream stream = remoteFileAdapter.getFileStream(RemoteFileType.TMP, uploadUuid);

		CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(stream)));

		LinkedList<String[]> assignments = Lists.newLinkedList();
		try {
			String[] nextLine;

			while ((nextLine = reader.readNext()) != null) {
				if (StringUtilities.any(nextLine)) {
					assignments.add(nextLine);
				}
			}
		} catch (IOException e) {
			logger.error(e);
		} finally {
			reader.close();
		}
		return assignments;
	}

	private void onPostUploadWork(List<Long> uploadedWorkIds, Long currentUserId) {
		if (!uploadedWorkIds.isEmpty()) {
			WorkActionRequest workActionRequest = new WorkActionRequest();
			workActionRequest.setModifierId(currentUserId);
			User masqUser = authenticationService.getMasqueradeUser();
			if (masqUser != null) {
				workActionRequest.setMasqueradeId(masqUser.getId());
			}
			workActionRequest.setAuditType(WorkAuditType.CREATE);
			workActionRequest.setLastActionOn(Calendar.getInstance());
			workUploadProcessQueue.onWorkUploaded(uploadedWorkIds, workActionRequest);
		}
	}

	private WorkUploadError convertParseErrorToThriftError(WorkRowParseError error) {
		WorkUploadError errorToReturn = new WorkUploadError();
		ConstraintViolation violation = new ConstraintViolation();
		if (error.getColumn() != null) {
			errorToReturn.setColumn(error.getColumn().getUploadColumnName());
			violation.setProperty(error.getColumn().getUploadColumnName());
		}
		errorToReturn.setErrorType(mapErrorType(error.getErrorType()));
		violation.setError(error.getMessage());
		violation.setWhy(StringUtilities.defaultString(error.getMessage(), "Parse error from the parser"));
		List<String> params = Lists.newArrayList();
		if (isNotBlank(error.getData())) {
			params.add(error.getData());
		}
		violation.setParams(params);
		errorToReturn.setViolation(violation);
		return errorToReturn;
	}

	private WorkUploadErrorType mapErrorType(WorkRowParseErrorType errorType) {
		switch (errorType) {
			case INVALID_DATA:
				return WorkUploadErrorType.INVALID_DATA;
			case MISSING_PARAMETER:
				return WorkUploadErrorType.MISSING_DATA;
			case MULTIPLE_STRATEGIES_INFERRED:
				return WorkUploadErrorType.MULTIPLE_STRATEGIES_INFERRED;
			case NO_PRICING_STRATEGY:
				return WorkUploadErrorType.NO_PRICING_STRATEGY;
		}
		logger.error("Unknown error type passed!");
		return null;
	}

	private WorkUploadError convertViolationToError(ConstraintViolation violation) {
		WorkUploadError error = new WorkUploadError();
		error.setColumn(violation.getProperty());
		error.setErrorType(WorkUploadErrorType.VALIDATION);
		error.setViolation(violation);
		return error;
	}

	/**
	 * Sort values for preview according to field category/type sort configuration, rather than in the order provided by the CSV file.
	 *
	 * @param values is a list of WorkUploadValue objects
	 */
	private void sortValues(List<WorkUploadValue> values) {
		final Map<String, Integer> orderLookup = columnService.getColumnOrder();

		Collections.sort(values, new Comparator<WorkUploadValue>() {
			@Override
			public int compare(WorkUploadValue v1, WorkUploadValue v2) {
				boolean isValue1InLookup = orderLookup.containsKey(v1.getType().getCode());
				boolean isValue2InLookup = orderLookup.containsKey(v2.getType().getCode());

				if (!isValue1InLookup && !isValue2InLookup) {
					int value1Order = v1.getType().getOrder();
					int value2Order = v2.getType().getOrder();
					return Integer.compare(value1Order, value2Order);
				}
				if (!isValue1InLookup) {
					return 1;
				}
				if (!isValue2InLookup) {
					return -1;
				}

				Integer o1 = orderLookup.get(v1.getType().getCode());
				Integer o2 = orderLookup.get(v2.getType().getCode());
				return o1.compareTo(o2);
			}
		});
	}
}
