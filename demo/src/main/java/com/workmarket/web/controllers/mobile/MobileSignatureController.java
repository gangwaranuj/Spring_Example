package com.workmarket.web.controllers.mobile;

import com.google.common.collect.ImmutableSet;
import com.workmarket.domains.model.WorkProperties;
import com.workmarket.service.business.SignatureService;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.infra.business.UploadService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.file.RemoteFile;
import com.workmarket.service.infra.file.RemoteFileAdapter;
import com.workmarket.service.infra.file.RemoteFileType;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.controllers.assignments.BaseWorkController;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.views.HTML2PDFView;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/mobile/assignments/signature")
public class MobileSignatureController extends BaseWorkController {
	private static final Log logger = LogFactory.getLog(MobileWorkController.class);

	@Autowired UploadService uploadService;
	@Autowired RemoteFileAdapter remoteFileAdapter;
	@Autowired SignatureService signatureService;
	@Autowired EventRouter eventRouter;

	/**
	 * Sign Printout
	 */
	@RequestMapping(
		value = "/{workNumber}",
		method = GET)
	public String signature(@PathVariable String workNumber, Model model) {
		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.ASSETS_INFO,
			WorkRequestInfo.ACTIVE_RESOURCE_INFO
		), ImmutableSet.of(
			AuthorizationContext.BUYER,
			AuthorizationContext.ACTIVE_RESOURCE,
			AuthorizationContext.ADMIN
		), "get_signature");

		model.addAttribute("isOwner", workResponse.getAuthorizationContexts().contains(AuthorizationContext.BUYER));
		model.addAttribute("isAdmin", workResponse.getAuthorizationContexts().contains(AuthorizationContext.ADMIN));
		model.addAttribute("isActiveResource", workResponse.getAuthorizationContexts().contains(AuthorizationContext.ACTIVE_RESOURCE));

		model.addAttribute("title", "Signature");
		model.addAttribute("work", workResponse.getWork());

		return "mobile/pages/v2/assignments/signature";
	}

	/**
	 * Post signature
	 */
	@RequestMapping(
		value = "/{workNumber}",
		method = POST)
	public String doSignature(
		@PathVariable("workNumber") String workNumber,
		@RequestParam(value = "dataUrl", required = false) String dataUrl,
		@RequestParam(value = "signerName", required = false) String signerName,
		@RequestParam(value = "reqId", required = false) Long deliverableRequirementId,
		@RequestParam(value = "pos", required = false) Integer position,
		@RequestParam(value = "workResolution", required = false) String workResolution,
		Model model,
		RedirectAttributes flash,
		HttpServletRequest httpRequest) throws IOException {

		String redirectUrl = "redirect:/mobile/assignments/details/" + workNumber;
		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		final Work work = getWork(workNumber).getWork();

		if (work == null) {
			logger.error("Could not find work for workNumber:" + workNumber);
			messageHelper.addError(bundle, "assignment.add_signature.exception");
			return redirectUrl;
		}

		try {
			workService.updateWorkProperties(work.getId(), CollectionUtilities.newStringMap(
				WorkProperties.RESOLUTION.getName(), StringUtilities.stripXSSAndEscapeHtml(workResolution)
			));
		} catch (Exception e) {
			logger.error("Error occurred while trying edit work resolution", e);
			messageHelper.addError(bundle, "assignment.add_signature.exception");
			return redirectUrl;
		}

		/* Signature Pad plugin provides a "data URL" where the signature image is provided via
		 base64 encoded URL parameter.  This parses that encoded image out. */
		String base64Image = dataUrl.split("^data:image/png;base64,")[1];

		if (base64Image == null) {
			messageHelper.addError(bundle, "assignment.add_signature.exception");
		} else {
			boolean success = doAddSignature(workNumber, deliverableRequirementId, position, signerName, base64Image, model, httpRequest);

			if (success) {
				messageHelper.addSuccess(bundle, "assignment.add_signature.success");
			} else {
				messageHelper.addError(bundle, "assignment.add_signature.exception");
			}
		}
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(work.getId()));
		return redirectUrl;
	}

	protected boolean doAddSignature(String workNumber, Long deliverableRequirementId, Integer position, String signerName, String base64Image, Model model, HttpServletRequest httpRequest) throws IOException {
		WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO
		), ImmutableSet.of(
			AuthorizationContext.ADMIN,
			AuthorizationContext.ACTIVE_RESOURCE
		), "add_signature");

		RemoteFile signatureImg = null;
		String fileName = UUID.randomUUID().toString();

		try {
			/* First, get the actual signature image uploaded */
			signatureImg = signatureService.uploadSignatureImage(workResponse.getWork().getId(), base64Image, fileName);

			/* Then, add the model attributes needed by the PDF generator to fill out the fields */
			model.addAttribute("signatureImageUrl", signatureImg.getRemoteUri());
			model.addAttribute("signatureSignerName", signerName);
			model.addAttribute("signatureDate", DateUtilities.getCalendarNow());

			/* Generate the PDF into a view, then save it to a file */
			HTML2PDFView pdfView = (HTML2PDFView) generatePdf(workNumber, model);
			String filePath = pdfView.renderToFile(model.asMap(), httpRequest);

			/* Attach the file as a closing asset to the assignment */
			signatureService.attachSignaturePdfToWork(workResponse.getWork().getId(), deliverableRequirementId, position, filePath);
		} catch (Exception ex) {
			logger.error(String.format("Error doing mobile signature for assignment %s", workResponse.getWork().getWorkNumber()), ex);
		} finally {
			if (signatureImg != null) {
				/* Finally, do everything we can to delete the signature image.. we don't want it hanging around */
				try {
					remoteFileAdapter.delete(RemoteFileType.PUBLIC, fileName);
				} catch (HostServiceException ex) {
					logger.error(String.format("Error doing mobile signature for assignment %s", workResponse.getWork().getWorkNumber()), ex);
				}
			}
		}

		return true;
	}
}
