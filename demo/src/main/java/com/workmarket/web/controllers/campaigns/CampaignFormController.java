package com.workmarket.web.controllers.campaigns;

import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.AvailabilityType;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.CompanyAssetAssociation;
import com.workmarket.domains.model.asset.Upload;
import com.workmarket.domains.model.asset.type.CompanyAssetAssociationType;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.RecruitingService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.dto.RecruitingCampaignDTO;
import com.workmarket.service.business.dto.UploadDTO;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.service.infra.business.UploadService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.web.forms.campaigns.CampaignForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/campaigns")
@PreAuthorize("hasAnyRole('ACL_ADMIN', 'ACL_MANAGER')")

public class CampaignFormController extends BaseCampaignController {
	@Autowired private AssetManagementService assetService;
	@Autowired private CompanyService companyService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private RecruitingService recruitingService;
	@Autowired private UploadService uploadService;
	@Autowired private UserGroupService userGroupService;

	@RequestMapping(
		value = "/new",
		method = GET)
	public String showNew(
		@ModelAttribute("form")
		CampaignForm form,
		Model model) {

		model.addAttribute("csvUploadTypes", MimeTypeUtilities.getMimeTypesForPage("/campaigns/new/csv"));
		model.addAttribute("imageUploadTypes", MimeTypeUtilities.getMimeTypesForPage("/campaigns/new/image"));
		model.addAttribute("currentView", "campaigns");

		return "web/pages/campaigns/form";
	}

	@RequestMapping(
		value = "/new",
		method = POST)
	public String processNewForm(
		@Valid @ModelAttribute("form") CampaignForm form,
		BindingResult bind,
		RedirectAttributes flash,
		Model model) throws Exception {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		model.addAttribute("currentView", "campaigns");

		if (!form.isUseCompanyOverview()) {
			ValidationUtils.rejectIfEmptyOrWhitespace(bind, "customCompanyOverview", "NotNull", CollectionUtilities.newArray("Company overview"));
		}

		if (bind.hasFieldErrors()) {
			if (form.getGroupId() != null) {
				UserGroup userGroup = userGroupService.findGroupById(form.getGroupId());
				form.setGroupName("'" + userGroup.getName() + "'");
			}
			return "web/pages/campaigns/form";
		}

		RecruitingCampaignDTO dto = convertFormToDTO(form);

		if (recruitingService.existRecruitingCampaignByCompanyAndTitle(dto.getCompanyId(), dto.getTitle())) {
			messageHelper.addError(bundle, "campaigns.form.error.title");
			return "redirect:/campaigns/new";
		}

		RecruitingCampaign campaign = recruitingService.saveOrUpdateRecruitingCampaign(dto);
		flash.addAttribute("id", campaign.getId());

		messageHelper.addSuccess(bundle, "campaigns.form.success");

		return "redirect:/campaigns/details/{id}";

	}


	@RequestMapping(
		value = "/{id}/edit",
		method = GET)
	public String edit(
		@PathVariable("id") Long id,
		@ModelAttribute("form") CampaignForm form,
		Model model) {

		RecruitingCampaign campaign = getCampaign(getCurrentUser().getCompanyId(), id);

		form.setTitle(campaign.getTitle());
		form.setDescription(campaign.getDescription());
		boolean hasOverview = StringUtils.isNotEmpty(campaign.getCompanyOverview());
		form.setUseCompanyOverview(!hasOverview);
		if (hasOverview) {
			form.setCustomCompanyOverview(campaign.getCompanyOverview());
		}

		if (campaign.getCompanyUserGroup() != null) {
			UserGroup userGroup = userGroupService.findGroupById(campaign.getCompanyUserGroup().getId());
			form.setGroupId(userGroup.getId());
			form.setGroupName("'" + userGroup.getName() + "'");
		}

		if (campaign.getCompanyLogo() != null) {
			if(getCompanyAvatars() != null){
				if(campaign.getCompanyLogo().getId().equals(getCompanyAvatars().getTransformedLargeAsset().getId())) {
					form.setAssetType("company");
				} else {
					form.setAssetType("upload");
				}
			} else {
				form.setAssetType("upload");
			}
			form.setAssetId(campaign.getCompanyLogo().getId());
		}
		form.setPrivateCampaign(campaign.isPrivateCampaign());

		model.addAttribute("campaign", campaign);
		model.addAttribute("currentView", "campaigns");
		model.addAttribute("csvUploadTypes", MimeTypeUtilities.getMimeTypesForPage("/campaigns/new/csv"));
		model.addAttribute("imageUploadTypes", MimeTypeUtilities.getMimeTypesForPage("/campaigns/new/image"));

		return "web/pages/campaigns/form";
	}

	@RequestMapping(
		value = "/{id}/edit",
		method = POST)
	public String submitEditForm(
		@PathVariable("id") Long id,
		@Valid @ModelAttribute("form") CampaignForm form,
		BindingResult bind,
		Model model,
		RedirectAttributes flash) throws Exception {

		RecruitingCampaign campaign = getCampaign(getCurrentUser().getCompanyId(), id);

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (!form.isUseCompanyOverview()) {
			ValidationUtils.rejectIfEmptyOrWhitespace(bind, "customCompanyOverview", "NotNull", CollectionUtilities.newArray("Company overview"));
		}

		if (bind.hasFieldErrors()) {
			if (campaign.getCompanyUserGroup() != null) {
				UserGroup userGroup = userGroupService.findGroupById(campaign.getCompanyUserGroup().getId());
				form.setGroupId(userGroup.getId());
				form.setGroupName("'" + userGroup.getName() + "'");
			}
			model.addAttribute("currentView", "campaigns");
			model.addAttribute("campaign", campaign);

			return "web/pages/campaigns/form";
		}

		RecruitingCampaignDTO dto = convertFormToDTO(form);
		dto.setRecruitingCampaignId(id);

		recruitingService.saveOrUpdateRecruitingCampaign(dto);

		messageHelper.addSuccess(bundle, "campaigns.form.success");

		return "redirect:/campaigns/details/{id}";
	}

	@ModelAttribute("company")
	public Company getCompany() {
		return companyService.findCompanyById(getCurrentUser().getCompanyId());
	}

	@ModelAttribute("companyAvatars")
	public CompanyAssetAssociation getCompanyAvatars() {
		return companyService.findCompanyAvatars(getCurrentUser().getCompanyId());
	}

	private RecruitingCampaignDTO convertFormToDTO(CampaignForm form) throws HostServiceException, AssetTransformationException, IOException {
		RecruitingCampaignDTO dto = new RecruitingCampaignDTO();
		dto.setTitle(form.getTitle());
		dto.setDescription(form.getDescription());
		dto.setCompanyId(getCurrentUser().getCompanyId());
		if (form.getGroupId() != null) {
			dto.setCompanyUserGroupId(form.getGroupId());
		}
		if (!form.isUseCompanyOverview()) {
			dto.setCompanyOverview(form.getCustomCompanyOverview());
		}

		if (form.getAssetType().equals("company")) {
			dto.setCompanyLogoAssetId(getCompanyAvatars().getTransformedLargeAsset().getId());
		} else if (form.getAssetType().equals("library")) {
			dto.setCompanyLogoAssetId(form.getAssetId());
		} else if (form.getAssetType().equals("upload") && StringUtils.isNotBlank(form.getUploadUuid())) {
			Upload upload = uploadService.findUploadByUUID(form.getUploadUuid());
			UploadDTO udto = UploadDTO.newDTO(upload);
			udto.setDescription(form.getUploadDescription());
			udto.setAssociationType(CompanyAssetAssociationType.RECRUITING_CAMPAIGN_LOGO);
			udto.setLargeTransformation(true);
			udto.setAddToCompanyLibrary(true);
			udto.setAvailabilityTypeCode(AvailabilityType.GUEST);

			Asset asset = assetService.addUploadToCompany(udto, getCurrentUser().getCompanyId());
			dto.setCompanyLogoAssetId(asset.getId());
		} else {
			dto.setCompanyLogoAssetId(null);
		}
		dto.setPrivateCampaign(form.getPrivateCampaign());
		return dto;
	}
}
