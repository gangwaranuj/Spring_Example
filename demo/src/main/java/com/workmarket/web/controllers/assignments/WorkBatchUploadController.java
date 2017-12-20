package com.workmarket.web.controllers.assignments;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.workmarket.domains.model.MimeType;
import com.workmarket.domains.model.asset.Upload;
import com.workmarket.domains.work.model.WorkTemplate;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.WorkTemplateService;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.event.BulkWorkUploadStarterEvent;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.infra.business.UploadService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.thrift.TWorkUploadService;
import com.workmarket.thrift.ThriftUtilities;
import com.workmarket.thrift.work.uploader.DeleteMappingRequest;
import com.workmarket.thrift.work.uploader.FieldCategory;
import com.workmarket.thrift.work.uploader.FieldMappingGroup;
import com.workmarket.thrift.work.uploader.FieldType;
import com.workmarket.thrift.work.uploader.FindMappingsRequest;
import com.workmarket.thrift.work.uploader.FindMappingsResponse;
import com.workmarket.thrift.work.uploader.RenameMappingRequest;
import com.workmarket.thrift.work.uploader.SaveMappingRequest;
import com.workmarket.thrift.work.uploader.WorkUpload;
import com.workmarket.thrift.work.uploader.WorkUploadDuplicateMappingGroupNameException;
import com.workmarket.thrift.work.uploader.WorkUploadError;
import com.workmarket.thrift.work.uploader.WorkUploadException;
import com.workmarket.thrift.work.uploader.WorkUploadInvalidFileTypeException;
import com.workmarket.thrift.work.uploader.WorkUploadRequest;
import com.workmarket.thrift.work.uploader.WorkUploadResponse;
import com.workmarket.thrift.work.uploader.WorkUploadRowLimitExceededException;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.editors.NullSafeNumberEditor;
import com.workmarket.web.forms.work.WorkBatchUploadForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.helpers.ThriftValidationMessageHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;

import org.joda.time.DateTimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.workmarket.utility.CollectionUtilities.newObjectMap;
import static com.workmarket.utility.CollectionUtilities.newStringMap;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/assignments/upload")
@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
public class WorkBatchUploadController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(WorkBatchUploadController.class);

	@Value("${com.workmarket.assignment.bulk.success}")
	private String STATSD_UPLOAD_SUCCESS;
	@Value("${com.workmarket.assignment.bulk.failure}")
	private String STATSD_UPLOAD_FAILURE;

	@Autowired private UploadService uploadService;
	@Autowired private WorkTemplateService  templateService;
	@Autowired private WorkSubStatusService workSubStatusService;
	@Autowired private TWorkUploadService uploader;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private JsonSerializationService jsonService;
	@Autowired private EventRouter eventRouter;
	@Autowired private EventFactory eventFactory;
	@Autowired private RedisAdapter redisAdapter;

	class FieldTypeEditor extends PropertyEditorSupport {
		@Override
		public void setAsText(String s) throws IllegalArgumentException {
			setValue(new FieldType().setCode(s));
		}
	}

	@InitBinder("uploadForm")
	public void initUploadFormBinder(WebDataBinder binder) {
		binder.setIgnoreInvalidFields(true);
		binder.registerCustomEditor(int.class, new NullSafeNumberEditor(Integer.class));
		binder.registerCustomEditor(long.class, new NullSafeNumberEditor(Long.class));
		binder.registerCustomEditor(FieldType.class, new FieldTypeEditor());
	}

	@RequestMapping(method = GET)
	public String index(Model model) throws WorkUploadException {
		model.addAttribute("uploadTypes", MimeTypeUtilities.getMimeTypesForPage("/assignments/upload"));

		return "web/pages/assignments/upload/index";
	}

	@RequestMapping(
		value = "/templates.json",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody List<Map<String, String>> getTemplates() {

		Map<Long, String> templatesIdNameMap = templateService.findAllActiveWorkTemplatesIdNameMap(getCurrentUser().getCompanyId());
		List<Map<String, String>> response = Lists.newArrayList();
		for (Map.Entry<Long, String> entry : templatesIdNameMap.entrySet()) {
			response.add(newStringMap(
				"id", entry.getKey().toString(),
				"name", entry.getValue()));
		}
		return response;
	}

	@RequestMapping(
		value = "/labels.json",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody List<WorkSubStatusType> getLabels() {
		return workSubStatusService.findAllWorkUploadSubStatuses();
	}

	@RequestMapping(
		value = "/template/{templateId}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getTemplate(@PathVariable Long templateId) {

		WorkTemplate template = templateService.findWorkTemplateByIdAndCompany(getCurrentUser().getCompanyId(), templateId);
		Map<String, Object> response = Maps.newHashMap();
		response.put("id", template.getId());
		response.put("name", template.getTemplateName());
		response.put("isWorkFeed", template.getManageMyWorkMarket().getShowInFeed());
		return response;
	}

	@RequestMapping(
		value = "/map",
		method = POST,
		consumes = MULTIPART_FORM_DATA_VALUE,
		produces = TEXT_HTML_VALUE)
	public String map(
		@ModelAttribute("uploadForm") WorkBatchUploadForm form,
		Model model,
		RedirectAttributes flash)  {

		MessageBundle messages = messageHelper.newFlashBundle(flash);

		MultipartFile file = form.getUpload();

		if (file.isEmpty()) {
			messageHelper.addError(messages, "work.upload.file.NotEmpty");
			return "redirect:/assignments/upload";
		}

		if (!isCSV(file)) {
			messageHelper.addError(messages, "work.upload.file.invalid_extension");
			return "redirect:/assignments/upload";
		}

		Upload upload = storeUpload(file);

		if (upload == null) {
			messageHelper.addError(messages, "work.upload.map.exception");
			return "redirect:/assignments/upload";
		}

		form.setUploadUuid(upload.getUUID());

		WorkUploadRequest uploadRequest = newWorkUploadRequest(form);
		WorkUploadResponse uploadResponse = uploadWorkPreview(uploadRequest);

		if (uploadResponse == null) {
			messageHelper.addError(messages, "work.upload.failure.general");
			return "redirect:/assignments/upload";
		}

		List<FieldCategory> fieldTypeCategories = uploader.getFieldCategoriesForUpload(uploadRequest, uploadResponse);

		if (form.getTemplateId() != null) {
			model.addAttribute("template", templateService.findWorkTemplateByIdFast(form.getTemplateId()));
		}

		extractErrorsFromUpload(uploadResponse, messageHelper.newBundle()); // has side effects

		model.addAttribute("upload", upload);
		model.addAttribute("response", uploadResponse);
		model.addAttribute("responseJson", ThriftUtilities.serializeToJson(uploadResponse));
		model.addAttribute("fieldTypesJson", jsonService.toJson(fieldTypesToMap(fieldTypeCategories)));
		model.addAttribute("fieldTypeCategories", categoriesToMap(fieldTypeCategories));
		model.addAttribute("fieldTypeCategoryNamesJson", jsonService.toJson(CollectionUtilities.newArrayPropertyProjection(fieldTypeCategories, "description")));

		return "web/pages/assignments/upload/map";
	}

	private WorkUploadResponse uploadWorkPreview(WorkUploadRequest uploadRequest) {
		try {
			return uploader.uploadWorkPreview(uploadRequest);
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}

	private Upload storeUpload(MultipartFile file) {
		try {
			return uploadService.storeUpload(
				file.getInputStream(),
				file.getOriginalFilename(),
				file.getContentType(),
				file.getSize()
			);
		} catch (HostServiceException | IOException e) {
			logger.error("File could not be uploaded", e);
			return null;
		}
	}

	private Boolean isCSV(MultipartFile file) {
		return CollectionUtilities.containsAny(
			MimeTypeUtilities.guessMimeType(file.getOriginalFilename()),
			MimeType.TEXT_CSV.getMimeType(),
			MimeType.TEXT_CSV_ALTERNATIVE.getMimeType()
		);
	}

	@RequestMapping(value = "/map", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public AjaxResponseBuilder doMap(@ModelAttribute("uploadForm") WorkBatchUploadForm form) throws WorkUploadException, URISyntaxException, HostServiceException {
		WorkUploadRequest uploadRequest = newWorkUploadRequest(form);
		MessageBundle messages = messageHelper.newBundle();

		if (!form.isPreview()) {
			BulkWorkUploadStarterEvent event = eventFactory.buildBulkWorkUploadStarterEvent(uploadRequest, getCurrentUser().getId());
			eventRouter.sendEvent(event);
			String uploadProgressKey = RedisFilters.userBulkUploadProgressKey(getCurrentUser().getId());
			redisAdapter.set(uploadProgressKey, String.valueOf(.001), (long) DateTimeConstants.SECONDS_PER_DAY);
			return new AjaxResponseBuilder().setSuccessful(true);
		}

		try {
			WorkUploadResponse uploadResponse = uploader.uploadWorkPreview(uploadRequest);
			extractErrorsFromUpload(uploadResponse, messages);
			return new AjaxResponseBuilder()
				.setSuccessful(!messages.hasErrors())
				.addData("response", uploadResponse);
		} catch (WorkUploadInvalidFileTypeException|WorkUploadRowLimitExceededException e) {
			return new AjaxResponseBuilder()
				.setSuccessful(false)
				.addMessage(e.getMessage());
		}
	}

	@RequestMapping(
		value = "/customfields",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder loadCustomFields(@ModelAttribute("uploadForm") WorkBatchUploadForm form) {
		WorkUploadRequest uploadRequest = newWorkUploadRequest(form);
		WorkUploadResponse uploadResponse;
		AjaxResponseBuilder builder = new AjaxResponseBuilder();

		try {
			uploadResponse = uploader.uploadWorkPreview(uploadRequest);

			List<FieldCategory> fieldTypeCategories = uploader.getFieldCategoriesForUpload(uploadRequest, uploadResponse);

			if (!fieldTypeCategories.isEmpty()) {
				builder.setSuccessful(true)
					.addMessage(messageHelper.getMessage("work.upload.customfields.loaded"))
					.addData("categories", categoriesToMap(fieldTypeCategories))
					.addData("types", fieldTypesToMap(fieldTypeCategories))
					.addData("mappingGroup", uploadResponse.getMappingGroup());
			}
		}
		catch (Exception e) {
			builder.addMessage(e.getMessage());
			logger.error("error loading custom fields!", e);
		}

		return builder;
	}

	private WorkUploadRequest newWorkUploadRequest(WorkBatchUploadForm form) {
		WorkUploadRequest uploadRequest = new WorkUploadRequest()
			.setUserNumber(getCurrentUser().getUserNumber())
			.setUploadUuid(form.getUploadUuid())
			.setHeadersProvided(form.isHeadersProvided());

		if (form.getTemplateId() != null) {
			uploadRequest.setTemplateId(form.getTemplateId());
		}

		if (form.getLabelId() != null) {
			uploadRequest.setLabelId(form.getLabelId());
		}

		if (form.getMapping() != null) {
			if (form.getMapping().getId() > 0) {
				uploadRequest.setMappingGroupId(form.getMapping().getId());
			}

			if (form.getMapping().getMappingsSize() > 0) {
				uploadRequest.setMappingGroup(form.getMapping());
			}
		}
		return uploadRequest;
	}

	private void extractErrorsFromUpload(WorkUploadResponse response, MessageBundle messages) {
		BindingResult bindingResult = ThriftValidationMessageHelper.newBindingResult();

		if (response.getErrorUploadsSize() > 0) {
			for (WorkUpload u : response.getErrorUploads()) {
				for (WorkUploadError e : u.getErrors()) {
					ThriftValidationMessageHelper.rejectViolation(e.getViolation(), bindingResult);

					// NOTE We're explicitly updating the value for later presentation.

					ObjectError lastError = bindingResult.getAllErrors().get(bindingResult.getErrorCount() - 1);
					e.getViolation().setWhy(messageHelper.getMessage(lastError));
				}
			}
		}

		messageHelper.setErrors(messages, bindingResult);
	}

	private Map<String, String> fieldTypesToMap(List<FieldCategory> fieldTypeCategories) {
		Map<String,String> allFieldTypes = Maps.newLinkedHashMap();

		for (FieldCategory c : fieldTypeCategories) {
			Map<String,String> fieldTypes = CollectionUtilities.extractKeyValues(c.getFieldTypes(), "code", "description");
			allFieldTypes.putAll(fieldTypes);
		}
		return allFieldTypes;
	}

	protected Map<String,Map<String,String>> categoriesToMap(List<FieldCategory> categories) {
		Map<String,Map<String,String>> to = Maps.newLinkedHashMap();

		for (FieldCategory c : categories) {
			to.put(
				c.getDescription(),
				CollectionUtilities.<String, String>extractKeyValues(c.getFieldTypes(), "code", "description")
			);
		}

		return to;
	}

	/**
	 * Mapping CRUD
	 */

	@RequestMapping(
		value = "/mappings",
		method = GET,
		produces = TEXT_HTML_VALUE)
	public String mappings() {
		return "web/partials/assignments/upload/mappings";
	}


	@RequestMapping(
		value = "/mappings.json",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void mappingsList(HttpServletRequest httpRequest, Model model) throws WorkUploadException {
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		FindMappingsRequest mappingsRequest = new FindMappingsRequest()
			.setCompanyId(getCurrentUser().getCompanyId())
			.setStartRow(firstNonNull(request.getStart(), 0))
			.setResultsLimit(firstNonNull(request.getLimit(), 1000));
		FindMappingsResponse mappingsResponse = uploader.findMappings(mappingsRequest);

		if (request.getLimit() != null) {
			DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request);
			response.setTotalRecords(Long.valueOf(mappingsResponse.getNumResults()).intValue());
			response.setTotalDisplayRecords(Long.valueOf(mappingsResponse.getNumResults()).intValue());

			for (FieldMappingGroup g : mappingsResponse.getMappingGroups()) {
				List<String> data = Lists.newArrayList(
					g.getName(),
					null
				);

				Map<String, Object> meta = newObjectMap(
					"id", g.getId(),
					"name", g.getName()
				);

				response.addRow(data, meta);
			}
			model.addAttribute("response", response);

		} else {
			// don't return the pagination stuff for an "all results" request
			List<Map<String, Object>> response = Lists.newArrayList();
			for (FieldMappingGroup g : mappingsResponse.getMappingGroups()) {

				Map<String, Object> meta = newObjectMap(
					"id", g.getId(),
					"name", g.getName()
				);

				response.add(meta);
			}
			model.addAttribute("response", response);
		}
	}

	@RequestMapping(
		value = "/create_mapping",
		method = GET)
	public String createMapping(@ModelAttribute("uploadForm") WorkBatchUploadForm form) {
		return "web/partials/assignments/upload/create_mapping";
	}

	@RequestMapping(
		value = "/create_mapping",
		method = POST)
	@ResponseBody
	public AjaxResponseBuilder doCreateMapping(
		@ModelAttribute("uploadForm") WorkBatchUploadForm form,
		BindingResult bindingResult,
		MessageBundle messages) throws WorkUploadException {

		ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "mapping.name", "reference.validation.name.required");

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(messages, bindingResult);
			return new AjaxResponseBuilder()
				.setSuccessful(false)
				.setMessages(messages.getAllMessages());
		}

		SaveMappingRequest mappingRequest = new SaveMappingRequest()
			.setUserNumber(getCurrentUser().getUserNumber())
			.setMappingGroup(form.getMapping());

		try {
			FieldMappingGroup mappingGroup = uploader.saveMapping(mappingRequest);
			messageHelper.addSuccess(messages, "work.upload.mappings.create.successful");
			return new AjaxResponseBuilder()
				.setRedirect("/assignments/upload/mappings")
				.setSuccessful(true)
				.setMessages(messages.getAllMessages())
				.addData("mappingGroup", mappingGroup);
		}
		catch (WorkUploadDuplicateMappingGroupNameException e) {
			messageHelper.addError(messages, "work.upload.mappings.create.not_unique");
			return new AjaxResponseBuilder()
				.setSuccessful(false)
				.setMessages(messages.getAllMessages());
		}
	}

	@RequestMapping(
		value="/rename_mapping/{mappingId}",
		method = GET)
	public String renameMapping(
		@PathVariable("mappingId") Long mappingId,
		@ModelAttribute("mappingForm") FieldMappingGroup form) throws WorkUploadException {
		return "web/partials/assignments/upload/rename_mapping";
	}

	@RequestMapping(
		value = "/rename_mapping/{mappingId}",
		method = POST)
	@ResponseBody
	public AjaxResponseBuilder doRenameMapping(
		@PathVariable("mappingId") Long mappingId,
		@ModelAttribute("mappingForm") FieldMappingGroup form,
		MessageBundle messages) throws WorkUploadException {

		// TODO @Valid validation for FieldMappingGroup

		RenameMappingRequest mappingRequest = new RenameMappingRequest()
			.setUserNumber(getCurrentUser().getUserNumber())
			.setMappingGroupId(mappingId)
			.setName(form.getName());
		uploader.renameMapping(mappingRequest);

		messageHelper.addSuccess(messages, "work.upload.mappings.rename.success");

		return new AjaxResponseBuilder()
			.setRedirect("/assignments/upload/mappings")
			.setSuccessful(true)
			.setMessages(messages.getAllMessages());
	}

	@RequestMapping(
		value = "/delete_mapping/{mappingId}",
		method = POST)
	@ResponseBody
	public AjaxResponseBuilder deleteMapping(@PathVariable("mappingId") Long mappingId, MessageBundle messages) throws WorkUploadException {

		DeleteMappingRequest mappingRequest = new DeleteMappingRequest()
			.setUserNumber(getCurrentUser().getUserNumber())
			.setMappingGroupId(mappingId);
		uploader.deleteMapping(mappingRequest);

		messageHelper.addSuccess(messages, "work.upload.mappings.delete.success");

		return new AjaxResponseBuilder()
			.setRedirect("/assignments/upload/mappings")
			.setSuccessful(true)
			.setMessages(messages.getAllMessages());
	}

	private void logSessionId(HttpServletRequest request) {
		if(request != null) {
			Cookie [] cookies = request.getCookies();
			for(Cookie c : cookies) {
				if("JSESSIONID".equals(c.getName())) {
					logger.debug("SESSION ID: " + c.getValue());
					return;
				}
			}
		}
	}
}
