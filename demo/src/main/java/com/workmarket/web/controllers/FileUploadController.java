package com.workmarket.web.controllers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.workmarket.domains.model.asset.Upload;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.infra.business.UploadService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.exceptions.HttpException404;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.FilenameValidator;
import com.workmarket.web.validators.FiletypeValidator;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Controller
@RequestMapping("/upload")
public class FileUploadController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(ProfileEditController.class);

	@Autowired private UploadService uploadService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private FilenameValidator filenameValidator;
	@Autowired private FiletypeValidator filetypeValidator;

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public String upload() {
		return "web/pages/upload/upload";
	}

	/**
	 * Handle file uploads from the <code>qquploader</code> jQuery file uploader
	 * which does NOT upload via <code>multipart/form-data</code> content type requests but rather
	 * as a <code>application/octet-stream</code> content type.
	 *
	 * @param request
	 * @throws IOException
	 * @throws HostServiceException
	 * @see https://github.com/bencolon/file-uploader/blob/master/server/OctetStreamReader.java
	 */
	@RequestMapping(
		value = "/uploadqq",
		method = RequestMethod.POST,
		produces = MediaType.APPLICATION_JSON_VALUE,
		consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody Map<String,Object> uploadqq(HttpServletRequest request) throws IOException, HostServiceException {
		String fileName = StringUtilities.urlDecode(request.getHeader("X-File-Name"));
		String fileType = StringUtils.substring(fileName, StringUtils.lastIndexOf(fileName, ".") + 1);
		String referer = StringUtils.defaultIfEmpty(request.getHeader("Referer"), EMPTY);
		String path = StringUtilities.getPathFromURL(StringUtilities.urlDecode(referer));
		List<String> errorList = new LinkedList<>();

		if(path.isEmpty()) {
			logger.error(String.format("Malformed URL in file upload: %s", referer));
			errorList.add(messageHelper.getMessage("upload.exception"));
		}

		MapBindingResult bindName = getFilenameErrors(fileName);
		MapBindingResult bindType = getFiletypeErrors(fileType, path);
		if (bindName.hasErrors() || bindType.hasErrors() || !errorList.isEmpty()) {

			errorList.addAll(messageHelper.getAllErrors(bindName));
			errorList.addAll(messageHelper.getAllErrors(bindType));

			return ImmutableMap.of(
					"successful", false,
					"errors", errorList
			);
		}

		String contentType = MimeTypeUtilities.guessMimeType(fileName);

		return uploadService.doFileUpload(fileName, contentType, request.getContentLength(), request.getInputStream());
	}

	/**
	 * Handle file uploads from the <code>qquploader</code> jQuery file uploader for IE
	 * which DOES upload via <code>multipart/form-data</code> content type requests and
	 * NOT as a <code>application/octet-stream</code> content type.
	 *
	 * @param attachment
	 * @throws IOException
	 * @throws HostServiceException
	 * @see https://github.com/bencolon/file-uploader/blob/master/server/OctetStreamReader.java
	 */
	@RequestMapping(
		value = "/uploadqq",
		method = RequestMethod.POST,
		produces = MediaType.TEXT_HTML_VALUE,
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public @ResponseBody String uploadqqForIE(@RequestParam("qqfile") MultipartFile attachment) throws IOException, HostServiceException {
		return new JSONObject(uploadService.doFileUpload(
				attachment.getOriginalFilename(),
				attachment.getContentType(),
				attachment.getSize(),
				attachment.getInputStream())
		).toString();
	}

	@RequestMapping(value = "/status/{uuid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public void status(@PathVariable("uuid") String uuid, Model model) {
		Upload upload = uploadService.findUploadByUUID(uuid);

		if (upload == null) {
			MessageBundle messages = messageHelper.newBundle();
			messageHelper.addError(messages, "upload.404");
			model.addAttribute("successful", false);
			model.addAttribute("errors", messages.getErrors());
			return;
		}

		model.addAttribute("successful", true);
		model.addAttribute("file_name", upload.getFilename());
	}

	@RequestMapping(value = "/download/{uuid}", method = RequestMethod.GET)
	public String download(@PathVariable("uuid") String uuid) throws HostServiceException {
		String uri = uploadService.getAuthorizedDownloadUriByUuid(uuid);
		if (StringUtils.isEmpty(uri)) throw new HttpException404();
		return String.format("redirect:%s", uri);
	}

	@RequestMapping(value = "/display/{uuid}", method = RequestMethod.GET)
	public String display(@PathVariable("uuid") String uuid) throws HostServiceException {
		String uri = uploadService.getAuthorizedUriByUuid(uuid);
		if (StringUtils.isEmpty(uri)) throw new HttpException404();
		return String.format("redirect:%s", uri);
	}

	@ExceptionHandler(IOException.class)
	public void handleIOException(IOException e, HttpServletRequest request, Model model) {
		MessageBundle messages = messageHelper.newBundle();
		messageHelper.addError(messages, "upload.IOException");

		model.addAttribute("successful", false);
		model.addAttribute("errors", messages.getErrors());
	}

	@ExceptionHandler(HostServiceException.class)
	public void handleHostServiceException(HostServiceException e, HttpServletRequest request, Model model) {
		MessageBundle messages = messageHelper.newBundle();
		messageHelper.addError(messages, "upload.HostServiceException");

		model.addAttribute("successful", false);
		model.addAttribute("errors", messages.getErrors());
	}

	private MapBindingResult getFilenameErrors(String fileName) {
		MapBindingResult bind = new MapBindingResult(Maps.newHashMap(), "fileName");
		filenameValidator.validate(fileName, bind);
		return bind;
	}

	private MapBindingResult getFiletypeErrors(String filetype, String page) {
		MapBindingResult bind = new MapBindingResult(Maps.newHashMap(), "fileType");
		Set<String> validFileTypesForPage = MimeTypeUtilities.getMimeTypesForPage(page);
		filetypeValidator.validate(
				CollectionUtilities.newObjectMap("filetype", filetype, "pageSet", validFileTypesForPage), bind
		);
		return bind;
	}
}
