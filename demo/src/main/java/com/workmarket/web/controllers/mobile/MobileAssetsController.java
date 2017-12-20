package com.workmarket.web.controllers.mobile;

import com.google.common.collect.ImmutableSet;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.DeliverableRequirementDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.web.controllers.assignments.BaseWorkController;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.UploadHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.DeliverableValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/mobile/assignments")
public class MobileAssetsController extends BaseWorkController {

	private static final Log logger = LogFactory.getLog(MobileWorkController.class);

	private static final String[] VALID_EXTENSIONS = UploadHelper.getValidMimeTypeExtensions().toArray(new String[UploadHelper.getValidMimeTypeExtensions().size()]);

	@Autowired AuthenticationService authenticationService;
	@Autowired FeatureEvaluator featureEvaluator;
	@Autowired JsonSerializationService jsonSerializationService;
	@Autowired private DeliverableValidator deliverableValidator;

	@RequestMapping(
		value = "/assets/{workNumber}",
		method = GET)
	public String assets(@PathVariable("workNumber") String workNumber, Model model) {

		// for v2 we get assets via json call
		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.STATUS_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ACTIVE_RESOURCE,
			AuthorizationContext.ADMIN
		), "mobile.assets");

		model.addAttribute("isOwner", workResponse.getAuthorizationContexts().contains(AuthorizationContext.BUYER));
		model.addAttribute("isAdmin", workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN));
		model.addAttribute("isActiveResource", workResponse.getAuthorizationContexts().contains(AuthorizationContext.ACTIVE_RESOURCE));
		model.addAttribute("work", workResponse.getWork());
		model.addAttribute("validExtensions", VALID_EXTENSIONS);

		return "mobile/pages/v2/assignments/assets";
	}

	@RequestMapping(
		value = "/documents/{workNumber}",
		method = GET)
	public String documents(@PathVariable("workNumber") String workNumber, Model model) {

		// for v2 we get assets via json call
		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.STATUS_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ACTIVE_RESOURCE,
			AuthorizationContext.ADMIN
		), "mobile.assets");

		model.addAttribute("isOwner", workResponse.getAuthorizationContexts().contains(AuthorizationContext.BUYER));
		model.addAttribute("isAdmin", workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN));
		model.addAttribute("isActiveResource", workResponse.getAuthorizationContexts().contains(AuthorizationContext.ACTIVE_RESOURCE));
		model.addAttribute("work", workResponse.getWork());
		model.addAttribute("validExtensions", VALID_EXTENSIONS);

		return "mobile/pages/v2/assignments/documents";
	}

	@RequestMapping(
		value = "/deliverables/{workNumber}",
		method = GET)
	public String deliverables(@PathVariable("workNumber") String workNumber, Model model) {

		// for v2 we get assets via json call
		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.DELIVERABLES_INFO,
			WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ACTIVE_RESOURCE,
			AuthorizationContext.ADMIN
		), "mobile.assets");

		// If assignment is not active, redirect to details
		String workStatus = workResponse.getWork().getStatus().getCode();
		if (WorkStatusType.SENT.equals(workStatus) || WorkStatusType.DRAFT.equals(workStatus)) {
			return "redirect:/mobile/assignments/details/" + workNumber;
		}

		model.addAttribute("isOwner", workResponse.getAuthorizationContexts().contains(AuthorizationContext.BUYER));
		model.addAttribute("isAdmin", workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN));
		model.addAttribute("isActiveResource", workResponse.getAuthorizationContexts().contains(AuthorizationContext.ACTIVE_RESOURCE));
		model.addAttribute("work", workResponse.getWork());
		model.addAttribute("validExtensions", VALID_EXTENSIONS);
		model.addAttribute("deliverableBaseJson", jsonSerializationService.toJson(workResponse.getWork().getDeliverableRequirementGroupDTO().getDeliverableRequirementDTOs()));
		model.addAttribute("allowMobileSignature", workResponse.getWork().getConfiguration().isEnableAssignmentPrintout() && workResponse.getWork().getConfiguration().isEnablePrintoutSignature());

		return "mobile/pages/v2/assignments/deliverables";
	}


	@RequestMapping(
		value = "/assets/list/{workNumber}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder getAssets(@PathVariable("workNumber") String workNumber) {

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.ASSETS_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ACTIVE_RESOURCE,
			AuthorizationContext.ADMIN
		), "mobile.assets");

		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);
		Map<String, Object> assets = new HashMap<>();
		Work work = workResponse.getWork();

		if (work.isSetAssets()) {
			assets.put("buyerAssets", work.getAssets());
		}

		if (work.isSetDeliverableAssets()) {
			assets.put("closingAssets", work.getDeliverableAssets());
		}

		if (work.getDeliverableRequirementGroupDTO() != null) {

			assets.put("assetNote", work.getDeliverableRequirementGroupDTO().getInstructions());

			int totalRequiredAttachments = 0;
			if (work.getDeliverableRequirementGroupDTO().getDeliverableRequirementDTOs() != null) {
				for (DeliverableRequirementDTO deliverableRequirementDTO : work.getDeliverableRequirementGroupDTO().getDeliverableRequirementDTOs()) {
					totalRequiredAttachments += deliverableRequirementDTO.getNumberOfFiles();
				}
			}
			assets.put("requiredCloseAssets", totalRequiredAttachments);

			int numberOfUploadedAttachments = 0;
			if (work.isSetDeliverableAssets()) {
				numberOfUploadedAttachments = Math.min(work.getDeliverableAssets().size(), totalRequiredAttachments);
			}

			assets.put("remainingCloseAssets", totalRequiredAttachments - numberOfUploadedAttachments);
		}

		response.setSuccessful(true);
		response.setData(assets);
		return response;
	}

	/**
	 * Add a deliverable
	 */
	@RequestMapping(
		value = "/dialogs/add_deliverable/{workNumber}",
		method = POST)
	public String addDeliverable(
		HttpServletRequest request,
		@PathVariable("workNumber") String workNumber,
		@RequestParam(value = "file", required = false) MultipartFile file,
		@RequestParam(value = "description", required = false) String description,
		@RequestParam(value = "deliverable_requirement_id", required = false) Long deliverableRequirementId,
		@RequestParam(value = "position", required = false) Integer position,
		RedirectAttributes flash) throws IOException {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		String fileName = file.getOriginalFilename();
		BindingResult bind = getFilenameErrors(fileName);
		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
			return "redirect:/mobile/assignments/deliverables/" + workNumber;
		}

		deliverableValidator.validate(deliverableRequirementId, file.getContentType(), bundle);
		if (bundle.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
			return "redirect:/mobile/assignments/deliverables/" + workNumber;
		}

		try {
			// Called for authorization validation side-effects
			getWork(workNumber, ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.STATUS_INFO
			), ImmutableSet.of(
				AuthorizationContext.BUYER,
				AuthorizationContext.ACTIVE_RESOURCE,
				AuthorizationContext.ADMIN
			), "mobile.assets");

			AssetDTO assetDTO = AssetDTO.newAssetDTO()
				.setName(fileName)
				.setDescription(description)
				.setDeliverableRequirementId(deliverableRequirementId)
				.setPosition(position)
				.setName(fileName)
				.setMimeType(file.getContentType())
				.setLargeTransformation(MimeTypeUtilities.isImage(file.getContentType()));

			deliverableService.addDeliverable(workNumber, file.getInputStream(), assetDTO);
			messageHelper.addSuccess(bundle, "assignment.add_attachment.success");
		} catch (Exception e) {
			logger.error("There was an error uploading an attachment", e);
			messageHelper.addError(bundle, "assignment.add_attachment.exception");
		}

		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(
		value = "/remove_attachment/{workNumber}",
		method = GET)
	public String removeAttachment(
		@PathVariable("workNumber") String workNumber,
		@RequestParam(value = "asset_id", required = false) Long assetId,
		RedirectAttributes flash) {

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.STATUS_INFO
		), ImmutableSet.of(
			AuthorizationContext.ACTIVE_RESOURCE,
			AuthorizationContext.BUYER,
			AuthorizationContext.ADMIN
		), "mobile.assets");

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		try {
			assetManagementService.removeAssetFromWork(assetId, workResponse.getWork().getId());
			messageHelper.addSuccess(bundle, "assignment.remove_attachment.success");
			return "redirect:/mobile/assignments/assets/" + workNumber;
		} catch (Exception e) {
			logger.error("", e);
		}

		messageHelper.addError(bundle, "assignment.remove_attachment.exception");
		return "redirect:/mobile/assignments/assets/" + workNumber;
	}

	@RequestMapping(
		value = "/assets/remove/{workNumber}/{assetUuid}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder removeAsset(
		@PathVariable String workNumber,
		@PathVariable String assetUuid) {

		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ACTIVE_RESOURCE,
			AuthorizationContext.ADMIN
		), "mobile.assets");

		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);

		try {
			assetManagementService.removeAssetFromWork(assetUuid, workResponse.getWork().getId());
			response.addMessage(messageHelper.getMessage("assignment.remove_attachment.success"));
			response.setSuccessful(true);
		} catch (Exception e) {
			logger.error("", e);
			response.addMessage(messageHelper.getMessage("assignment.remove_attachment.exception"));
		}

		return response;
	}

}
