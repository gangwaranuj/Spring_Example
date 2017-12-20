package com.workmarket.web.controllers.invitations;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Invitation;
import com.workmarket.domains.model.InvitationType;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.CompanyAssetAssociation;
import com.workmarket.domains.model.asset.Upload;
import com.workmarket.domains.model.asset.type.CompanyAssetAssociationType;
import com.workmarket.domains.model.recruiting.RecruitingCampaignPagination;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.InvitationService;
import com.workmarket.service.business.RecruitingService;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.dto.InvitationDTO;
import com.workmarket.service.business.dto.UploadDTO;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.service.infra.business.UploadService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.forms.invitations.SendInvitationsForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.InvitationDTOValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/invitations/send")
public class SendInvitationsController extends BaseController {

	@Autowired private AssetManagementService assetManagementService;
	@Autowired private CompanyService companyService;
	@Autowired private InvitationService invitationService;
	@Autowired private InvitationDTOValidator invitationDTOValidator;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private RegistrationService registrationService;
	@Autowired private UploadService uploadService;
	@Autowired private RecruitingService recruitingService;

	static InvitationDTO previewTemplate = new InvitationDTO();

	static {
		previewTemplate.setFirstName("${first_name}");
		previewTemplate.setLastName("${last_name}");
		previewTemplate.setEmail("${email}");
		previewTemplate.setMessage("{{html message}}");
	}

	@RequestMapping(method=RequestMethod.GET)
	public String sendShow(
		@ModelAttribute("form")
		SendInvitationsForm form,
		Model model) throws Exception {
		model.addAttribute("uploadTypes", MimeTypeUtilities.getMimeTypesForPage("/invitations/send"));
		model.addAttribute("currentView", "invitations");

		return "web/pages/invitations/send";
	}

	@RequestMapping(method=RequestMethod.POST)
	public String sendSubmit(
			@ModelAttribute("sendForm")
			SendInvitationsForm form,
			RedirectAttributes flash) throws Exception {
		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if(form.getBulk() && StringUtils.isNotEmpty(form.getUploadCsv())) {
			List<String[]> entries = registrationService.getUsersFromCSVUpload(form.getUploadCsv());
			if (entries.isEmpty() || entries.size() == 1) {
				return "redirect:/invitations";
			}

			String[] header = entries.get(0);
			entries.remove(0);

			int emailColumn = getHeaderColumnNumber(header, "email", "e-mail");
			if (emailColumn == -1) {
				return "redirect:/invitations";
			}
			int firstNameColumn = getHeaderColumnNumber(header, "given name", "first name");
			int lastNameColumn = getHeaderColumnNumber(header, "family name", "last name");

			for (String[] row : entries) {
				copyInvitationValuesToForm(form, firstNameColumn, lastNameColumn, emailColumn, row);
				inviteByEmail(form, bundle);
			}
		} else {
			inviteByEmail(form, bundle);
		}

		return "redirect:/invitations";
	}

	private int getHeaderColumnNumber(
		String[] header,
		String headerValue,
		String altHeaderValue) {
		for (int i = 0; i < header.length; ++i) {
			if (header[i].toLowerCase().equals(headerValue) ||
				header[i].toLowerCase().equals(altHeaderValue)) {
				return i;
			}
		}
		return -1;
	}

	private void copyInvitationValuesToForm(
		SendInvitationsForm form,
		int firstNameColumn,
		int lastNameColumn,
		int emailColumn,
		String[] row) {
		form.setFirst_name("");
		form.setLast_name("");
		for (int i = 0; i < row.length; ++i) {
			if (i == firstNameColumn) {
				form.setFirst_name(row[i]);
			} else if (i == lastNameColumn) {
				form.setLast_name(row[i]);
			} else if (i == emailColumn) {
				form.setEmail(row[i]);
			}
		}
	}

	private void inviteByEmail(
		SendInvitationsForm form,
		MessageBundle bundle
	) throws HostServiceException,
		AssetTransformationException,
		IOException {
		InvitationDTO invitationDTO = getInvitationDTO(form);
		List<String> messages = invitationDTOValidator.validate(invitationDTO);
		if(!messages.isEmpty()) {
			for (String message : messages) {
				bundle.addError(message);
			}
			return;
		}
		Long invitingCompanyId = getCurrentUser().getCompanyId();
		if (StringUtils.equals(form.getLogo(), "upload") && StringUtilities.isNotEmpty(form.getLogo_uuid())) {
			Upload upload = uploadService.findUploadByUUID(form.getLogo_uuid());
			if (upload != null) {
				UploadDTO uploadDto = getUploadDTO(form, upload);
				Asset asset = assetManagementService.addUploadToCompany(uploadDto, invitingCompanyId);
				invitationDTO.setCompanyLogoAssetId(asset.getId());
			}
		} else if (StringUtils.equals(form.getLogo(), "company")) {
			CompanyAssetAssociation avatars = companyService.findCompanyAvatars(invitingCompanyId);
			if (avatars.getSmall() != null) {
				invitationDTO.setCompanyLogoAssetId(avatars.getSmall().getId());
				invitationDTO.setShowCompanyLogo("true");
			}
		}

		Invitation invitation = registrationService.inviteUser(invitationDTO, form.getUser_group_ids());
		if (invitation == null) {
			bundle.addError(messageHelper.getMessage("invitations.send.failed_invalid_email_one", invitationDTO.getEmail()));
			return;
		}
		bundle.addSuccess(messageHelper.getMessage("invitations.send.success_one", invitationDTO.getEmail()));
	}

	private InvitationDTO getInvitationDTO(SendInvitationsForm form) {
		InvitationDTO invitationDTO = new InvitationDTO();
		if (StringUtilities.any(form.getCompany_overview(), form.getCompany_overview_add())) {
			invitationDTO.setShowCompanyDescription("true");
		}
		if (StringUtilities.any(form.getCompany_logo_add(), form.getCompany_logo())) {
			invitationDTO.setShowCompanyLogo("true");
		}
		if (StringUtilities.any(form.getCompany_overview(), form.getCompany_logo_add())) {
			invitationDTO.setCompanyOverview("true");
		}
		invitationDTO.setFirstName(form.getFirst_name());
		invitationDTO.setLastName(form.getLast_name());
		invitationDTO.setEmail(form.getEmail());
		invitationDTO.setMessage(form.getCustom_message());
		invitationDTO.setRecruitingCampaignId(form.getRecruitingCampaignId());
		invitationDTO.setInvitingCompanyId(getCurrentUser().getCompanyId());
		invitationDTO.setInviterUserId(getCurrentUser().getId());
		invitationDTO.setInvitationType(form.getPrivate_invitation() ? InvitationType.EXCLUSIVE : InvitationType.CONTRACTOR);
		return invitationDTO;
	}

	private UploadDTO getUploadDTO(
		SendInvitationsForm form,
		Upload upload) {
		UploadDTO uploadDto = new UploadDTO();
		uploadDto.setUploadId(upload.getId());
		uploadDto.setUploadUuid(upload.getUUID());
		uploadDto.setMimeType(upload.getMimeType());
		uploadDto.setAssociationType(CompanyAssetAssociationType.INVITATION_COMPANY_LOGO);
		uploadDto.setLargeTransformation(true);
		uploadDto.setName(upload.getFilename());
		if (StringUtils.isNotEmpty(form.getAdd_to_filemanager())) {
			uploadDto.setDescription(form.getFile_description());
			uploadDto.setAddToCompanyLibrary(true);
		}
		return uploadDto;
	}

	@ModelAttribute("previewTemplate")
	private String getPreviewTemplate() {
		String preview = registrationService.previewInvitation(previewTemplate);
		return StringUtilities.stripTags(preview, "h1,h2,h3,h4,h5,h6,p,hr,br,strong,em");
	}

	@ModelAttribute("companyAvatars")
	private CompanyAssetAssociation getCompanyAvatars() {
		return companyService.findCompanyAvatars(getCurrentUser().getCompanyId());
	}

	@ModelAttribute("isInviteAllowed")
	private boolean isInviteAllowed() {
		int sentToday = invitationService.countInvitationsSentTodayByCompany(getCurrentUser().getCompanyId());
		return sentToday < Constants.INVITATIONS_PER_DAY_PER_COMPANY_LIMIT;
	}

	@ModelAttribute("company")
	private Company getCompany() {
		return companyService.findCompanyById(getCurrentUser().getCompanyId());
	}

	@ModelAttribute("recruitingCampaigns")
	public Map<Long, String> getRecruitingCampaigns() {
		RecruitingCampaignPagination pagination =
			recruitingService.findAllCampaignsByCompanyId(getCurrentUser().getCompanyId(), new RecruitingCampaignPagination());
		return CollectionUtilities.extractKeyValues(pagination.getResults(), "id", "title");
	}
}
