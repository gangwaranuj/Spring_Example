package com.workmarket.web.controllers;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.CompanyAsset;
import com.workmarket.domains.model.asset.CompanyAssetPagination;
import com.workmarket.domains.model.asset.Upload;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.ContractService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.UploadDTO;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.service.infra.business.UploadService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.BaseResponse;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/filemanager")
public class FilemanagerController extends BaseController {

	private static final Log logger = LogFactory.getLog(FilemanagerController.class);
	private static final String baseRedirect = "redirect:/filemanager";

	@Autowired AssetManagementService assetService;
	@Autowired ContractService contractService;
	@Autowired MessageBundleHelper messageHelper;
	@Autowired UploadService uploadService;
	@Autowired JsonSerializationService jsonService;
	@Autowired private MessageSource messageSource;

	public static final Map<Integer, String> documentListColumnSortMap = ImmutableMap.of(
		0, CompanyAssetPagination.SORTS.CREATION_DATE.toString(),
		1, CompanyAssetPagination.SORTS.NAME.toString(),
		2, CompanyAssetPagination.SORTS.DESCRIPTION.toString());

	@RequestMapping(
		method = GET
	)
	public String index() {

		return "web/pages/filemanager/index";
	}


	@RequestMapping(
		value = "/available_documents",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void populateDocuments(
		HttpServletRequest httpRequest,
		Model model) throws Exception {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(documentListColumnSortMap);
		CompanyAssetPagination pagination = request.newPagination(CompanyAssetPagination.class);
		pagination = assetService.getCompanyLibrary(getCurrentUser().getCompanyId(), pagination);

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);
		model.addAttribute("response", response);

		for (CompanyAsset asset : pagination.getResults()) {
			String fullName = asset.getCreatorFullName();

			List<String> row = Lists.newArrayList(
				asset.getName(),
				asset.getDescription(),
				fullName,
				"");

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"id", asset.getAssetId(),
				"uri", asset.getUri(),
				"uuid", asset.getUuid());

			response.addRow(row, meta);
		}
	}


	@RequestMapping(
		value = "/add",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody BaseResponse addNewAsset(@RequestParam(required = false) String data) {

		BaseResponse response = new BaseResponse();
		List<String> errors = Lists.newArrayList();
		response.setSuccessful(false);
		response.setErrors(errors);

		if (StringUtils.isEmpty(data)) {
			response.getErrors().add(messageHelper.getMessage("filemanager.new_asset.upload_empty"));
			return response;
		}

		List<NameValuePair> dataFields = URLEncodedUtils.parse(data, Charset.forName("UTF-8"));

		String uuid = getValueFromPair("upload_uuid", dataFields);
		String description = getValueFromPair("description", dataFields);

		if (StringUtils.isEmpty(uuid)) {
			response.getErrors().add(messageHelper.getMessage("filemanager.new_asset.upload_invalid"));
			return response;
		}

		Upload upload = uploadService.findUploadByUUID(uuid);
		if (upload == null) {
			response.getErrors().add(messageHelper.getMessage("filemanager.new_asset.upload_exception"));
			return response;
		}

		UploadDTO dto = new UploadDTO();
		dto.setUploadId(upload.getId());
		dto.setUploadUuid(upload.getUUID());
		dto.setMimeType(upload.getMimeType());
		dto.setDescription(description);
		dto.setAddToCompanyLibrary(true);

		try {
			assetService.addUploadToCompany(dto, getCurrentUser().getCompanyId());
			response.setSuccessful(true);

			return response;

		} catch (HostServiceException | AssetTransformationException | IOException e) {
			logger.error(e);
		}
		response.getErrors().add(messageHelper.getMessage("filemanager.new_asset.exception"));

		return response;
	}


	@RequestMapping(
		value = "/add_new_asset_external",
		method = POST)
	public @ResponseBody
	BaseResponse addNewAssetExternal(
		@RequestParam(value = "asset_id", required = false) Long assetId,
		@RequestParam(value = "file_description", required = false) String description,
		@RequestParam(value = "file_display_with_profile", required = false) String displayWithProfile) {

		BaseResponse response = new BaseResponse();
		response.setSuccessful(false);

		if (assetId == null) {
			response.getErrors().add(messageHelper.getMessage("filemanager.new_asset.upload_invalid"));
			return response;
		}

		Asset asset = assetService.findAssetByIdAndCompany(assetId, getCurrentUser().getCompanyId());

		if (asset == null) {
			response.getErrors().add(messageHelper.getMessage("filemanager.new_asset.exception"));
			return response;
		}

		AssetDTO dto = AssetDTO.newDTO(asset);
		dto.setDescription(description);
		dto.setDisplayable(displayWithProfile != null);
		dto.setAddToCompanyLibrary(true);
		assetService.addAssetToCompany(dto, getCurrentUser().getCompanyId());

		response.setSuccessful(true);

		return response;
	}


	@RequestMapping(
		value = "/editasset",
		method = GET)
	public String editasset(
		@RequestParam(required = false) Long id,
		Model model,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (id == null) {
			messageHelper.addError(bundle, "filemanager.edit_asset.no_selection");
			return "redirect:/filemanager";
		}

		Asset asset = assetService.findAssetByIdAndCompany(id, getCurrentUser().getCompanyId());
		if (asset == null) {
			messageHelper.addError(bundle, "filemanager.edit_asset.invalid_selection");
			return "redirect:/filemanager";
		}

		model.addAttribute("asset", asset);

		return "web/pages/filemanager/editasset";
	}


	@RequestMapping(
		value = "/editasset",
		method = POST)
	public String editasset(
		@RequestParam(required = false) Long id,
		@RequestParam(value = "name", required = false) String name,
		@RequestParam(value = "description", required = false) String description,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (id == null) {
			messageHelper.addError(bundle, "filemanager.edit_asset.no_selection");
			return baseRedirect;
		}

		Asset asset = assetService.findAssetByIdAndCompany(id, getCurrentUser().getCompanyId());

		if (asset == null) {
			messageHelper.addError(bundle, "filemanager.edit_asset.invalid_selection");
			return baseRedirect;
		}

		if (StringUtils.isEmpty(name)) {
			messageHelper.addError(bundle, "NotEmpty", "Name");
		}
		if (StringUtils.isEmpty(description)) {
			messageHelper.addError(bundle, "NotEmpty", "Description");
		}

		if (!bundle.hasErrors()) {
			AssetDTO dto = AssetDTO.newDTO(asset);
			dto.setName(name);
			dto.setDescription(description);
			assetService.updateAsset(id, dto);
		}

		return baseRedirect;
	}


	@RequestMapping(
		value = "/deactivate",
		method = RequestMethod.POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map deactivate(
		@RequestBody Map<String, Long> fileManager,
		HttpServletResponse response) {

		String message;

		if (fileManager == null) {
			message = messageSource.getMessage("filemanager.delete_asset.no_selection", null, null);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			if (assetService.removeAssetFromCompanyLibrary(fileManager.get("id"), getCurrentUser().getCompanyId())) {
				message = messageSource.getMessage("filemanager.delete_asset.success", null, null);
			} else {
				message = messageSource.getMessage("filemanager.delete_asset.exception", null, null);
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		}

		return CollectionUtilities.newObjectMap(
			"message", message
		);
	}

	@RequestMapping(
		value = "/get_asset_info",
		method = GET)
	public @ResponseBody Map<String,Object> getAssetInfo(@RequestParam(required = false) Long id) {

		Asset asset = assetService.findAssetByIdAndCompany(id, getCurrentUser().getCompanyId());

		if (asset == null) {
			return ImmutableMap.<String,Object>of(
				"success", Boolean.FALSE,
				"id", "",
				"image", ""
			);
		}

		return ImmutableMap.<String,Object>of(
			"success", Boolean.TRUE,
			"id", asset.getId(),
			"image", asset.getName(),
			"uri", asset.getUri()
		);
	}


	@RequestMapping(
		value = "/get_asset_list",
		method = POST)
	public @ResponseBody Map<String,Object> getAssetList(
		@RequestParam(value = "asset[]", required = false) List<Long> assetIds) {

		if (CollectionUtils.isEmpty(assetIds)) {
			return ImmutableMap.<String,Object>of(
				"success", Boolean.FALSE,
				"errors", Lists.newArrayList()
			);
		}

		List<Map<String,Object>> assets = Lists.transform(assetIds, new Function<Long,Map<String,Object>>() {
			@Override
			public Map<String,Object> apply(@Nullable Long id) {
				Asset asset = assetService.findAssetByIdAndCompany(id, getCurrentUser().getCompanyId());
				return ImmutableMap.<String,Object>builder()
					.put("id", asset.getId())
					.put("uuid", asset.getUUID())
					.put("name", asset.getName())
					.put("description", asset.getDescription())
					.put("mime_type", asset.getMimeType())
					.put("created_on", asset.getCreatedOn())
					.build();
			}
		});

		return ImmutableMap.of(
			"success", Boolean.TRUE,
			"assets", assets
		);
	}


	@RequestMapping(
		value = "/documents_for_list",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void documentsForList(HttpServletRequest httpRequest, Model model) throws Exception {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(documentListColumnSortMap);
		CompanyAssetPagination pagination = request.newPagination(CompanyAssetPagination.class);

		pagination = assetService.getCompanyLibrary(getCurrentUser().getCompanyId(), pagination);

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);
		model.addAttribute("response", response);

		for (final CompanyAsset asset : pagination.getResults()) {

			ArrayList<String> row = Lists.newArrayList(
				"",
				asset.getName(),
				MimeTypeUtilities.getMimeIconName(asset.getMimeType()));

			Map<String, Object> meta = CollectionUtilities.newObjectMap("id", asset.getAssetId());

			response.addRow(row, meta);
		}
	}

	private String getValueFromPair(final String key, List<NameValuePair> map) {
		if (key == null) return null;
		NameValuePair pair = ((NameValuePair) CollectionUtils.find(map, new Predicate() {
			@Override public boolean evaluate(Object o) {
				NameValuePair pair = (NameValuePair) o;
				return key.equals(pair.getName());
			}
		}));
		return (pair != null) ? pair.getValue() : null;
	}
}
