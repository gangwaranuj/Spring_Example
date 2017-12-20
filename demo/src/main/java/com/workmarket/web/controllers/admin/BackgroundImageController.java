package com.workmarket.web.controllers.admin;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.workmarket.domains.model.asset.DefaultBackgroundImage;
import com.workmarket.service.admin.DefaultBackgroundImageService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.FileUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;



@Controller
@RequestMapping("/admin/background_image")
public class BackgroundImageController extends BaseController {

	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private DefaultBackgroundImageService defaultBackgroundImageService;
	@Autowired private AuthenticationService authenticationService;

	@RequestMapping(
		value = {"", "/", "/index"},
		method = GET)
	public String index(Model model) {
		List<DefaultBackgroundImage> backgroundImages = defaultBackgroundImageService.getAll();

		Long currentDefaultImageId = null;
		Optional<DefaultBackgroundImage> image = defaultBackgroundImageService.getCurrentDefaultBackgroundImage();
		if (image.isPresent()) {
			currentDefaultImageId = image.get().getId();
		}

		model.addAttribute("backgroundImages", backgroundImages);
		model.addAttribute("currentDefaultImageId", currentDefaultImageId);

		return "web/pages/admin/background_image/index";
	}

	@RequestMapping(
		value = {"", "/"},
		method = POST)
	public @ResponseBody AjaxResponseBuilder upload(MultipartHttpServletRequest request) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();
		try {
			MultipartFile mpf = request.getFile(request.getFileNames().next());
			File file = FileUtilities.temporaryStoreFile(mpf.getInputStream());
			AssetDTO dto = new AssetDTO();
			dto.setSourceFilePath(file.getAbsolutePath());
			dto.setName(mpf.getOriginalFilename());
			dto.setMimeType(mpf.getContentType());

			DefaultBackgroundImage image = defaultBackgroundImageService.saveBackgroundImage(dto);

			return response
				.setSuccessful(true)
				.setData(
					ImmutableMap.<String, Object>of(
						"id", image.getId(),
						"uri", image.getAsset().getUri()
					)
				);

		} catch (Exception e) {
			return response.setMessages(Lists.newArrayList(messageHelper.getMessage("upload.IOException")));
		}
	}

	@RequestMapping(
		value = "/{id}",
		method = POST)
	public @ResponseBody AjaxResponseBuilder setDefault(@PathVariable Long id) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();
		try {
			defaultBackgroundImageService.setCurrentDefaultBackgroundImage(id);
		} catch (Exception e) {
			return response.setMessages(Lists.newArrayList(e.getMessage()));
		}

		return response.setSuccessful(true);
	}

	@RequestMapping(
		value = "/{id}",
		method = DELETE)
	public @ResponseBody AjaxResponseBuilder removeImage(@PathVariable Long id) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();
		try {
			defaultBackgroundImageService.removeBackgroundImage(id);
		} catch (Exception e) {
			return response.setMessages(Lists.newArrayList(e.getMessage()));
		}

		return response.setSuccessful(true);
	}

	@RequestMapping(
		value = "/refresh_sessions",
		method = POST)
	public @ResponseBody AjaxResponseBuilder refreshSessions() {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();
		try {
			authenticationService.refreshSessionForAll();
		} catch (Exception e) {
			return response.setMessages(Lists.newArrayList(e.getMessage()));
		}

		return response.setSuccessful(true);
	}
}
