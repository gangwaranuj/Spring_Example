package com.workmarket.web.converters;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.service.business.dto.DeliverableRequirementDTO;
import com.workmarket.service.business.dto.DeliverableRequirementGroupDTO;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.thrift.assessment.Assessment;
import com.workmarket.thrift.core.Asset;
import com.workmarket.thrift.core.Upload;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.thrift.work.Work;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.thrift.ThriftUtilities;
import com.workmarket.web.forms.DeliverableRequirementForm;
import com.workmarket.web.forms.ResourceCompletionForm;
import com.workmarket.web.forms.work.WorkAssessmentForm;
import com.workmarket.web.forms.work.WorkAssetForm;
import com.workmarket.web.forms.work.WorkForm;
import com.workmarket.web.forms.work.WorkFormRouting;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ThriftWorkToWorkFormConverter implements Converter<Work, WorkForm> {

	@Autowired private SecurityContextFacade securityContextFacade;
	@Autowired private ThriftCustomFieldGroupToStringConverter customFieldConverter;
	@Autowired private PartGroupDTOToPartGroupFormConverter partGroupDTOToPartGroupFormConverter;

	@Override
	public WorkForm convert(Work source) {
		WorkForm dest = new WorkForm();
		if (source.isSetId()) {
			dest.setId(source.getId());
		}

		if (source.getWorkNumber() != null) {
			dest.setWorkNumber(source.getWorkNumber());
		}

		if (source.getTitle() != null) {
			dest.setTitle(source.getTitle());
		}

		if (source.getDescription() != null) {
			dest.setDescription(source.getDescription());
		}

		if (source.getInstructions() != null) {
			dest.setInstructions(source.getInstructions());
		}

		if (source.getPrivateInstructions() != null) {
			dest.setPrivateInstructions(source.getPrivateInstructions());
		}

		if (source.getDesiredSkills() != null) {
			dest.setDesired_skills(source.getDesiredSkills());
		}

		if (source.isSetIndustry()) {
			dest.setIndustry(source.getIndustry().getId());
		}

		if (source.isSetProject()) {
			dest.setProject(source.getProject().getId());
		}

		if (source.isSetClientCompany()) {
			dest.setClientcompany(source.getClientCompany().getId());
		}

		if (source.getCustomFieldGroupsSize() > 0) {
			List<Long> customFieldIds = Lists.newArrayList();
			Map<Long, String> customFieldsJson = new HashMap<>();

			List<CustomFieldGroup> customFieldGroups = source.getCustomFieldGroups();
			Collections.sort(customFieldGroups, new Comparator<CustomFieldGroup>() {
				public int compare(CustomFieldGroup o1, CustomFieldGroup o2) {
					return o1.getPosition().compareTo(o2.getPosition());
				}
			});

			for (CustomFieldGroup fieldGroup : customFieldGroups) {
				Long thisId = fieldGroup.getId();
				customFieldIds.add(thisId);

				customFieldsJson.put(thisId, customFieldConverter.convert(fieldGroup));
			}

			dest.setCustomfield(customFieldIds);
			dest.setCustomFieldsJson(customFieldsJson);
		}

		if (source.isSetBuyer()) {
			dest.setInternal_owner(source.getBuyer().getId());
		} else {
			dest.setInternal_owner(securityContextFacade.getCurrentUser().getId());
		}

		if (source.isSetSupportContact()) {
			dest.setSupport_contact(source.getSupportContact().getId());
		} else {
			dest.setSupport_contact(securityContextFacade.getCurrentUser().getId());
		}

		if (source.isOffsiteLocation()) {
			dest.setClientlocations(WorkForm.CLIENT_LOCATION_OFFSITE);
		} else if (source.isSetLocation()) {
			if (source.isNewLocation()) {
				dest.setClientlocations(WorkForm.CLIENT_LOCATION_NEW);
				dest.setOnetime_location_id(source.getLocation().getId());
			} else {
				dest.setClientlocations(WorkForm.CLIENT_LOCATION_CLIENT_COMPANY);
				if (source.getLocation().isSetId()) {
					dest.setClientlocation_id(source.getLocation().getId());
				}
			}

			dest.setLocation_name(source.getLocation().getName());
			dest.setLocation_number(source.getLocation().getNumber());

			// I found a location in the database that loaded with a null address - CMB
			if (null != source.getLocation().getAddress()) {
				dest.setLocation_address1(source.getLocation().getAddress().getAddressLine1());
				dest.setLocation_address2(source.getLocation().getAddress().getAddressLine2());
				dest.setLocation_city(source.getLocation().getAddress().getCity());
				dest.setLocation_state(source.getLocation().getAddress().getState());
				dest.setLocation_country(source.getLocation().getAddress().getCountry());
				if (source.getLocation().getAddress().getPoint() != null) {
					dest.setLocation_longitude(source.getLocation().getAddress().getPoint().getLongitude());
					dest.setLocation_latitude(source.getLocation().getAddress().getPoint().getLatitude());
				}
				dest.setLocation_postal_code(source.getLocation().getAddress().getZip());
				dest.setLocation_type(source.getLocation().getAddress().getType());

				try {
					dest.setLocationJson(ThriftUtilities.serializeToJson(source.getLocation()));
				} catch (Exception e) {
					// Not that big a deal
				}
			}
		}

		if (source.isSetLocationContact()) {
			dest.setOnsite_contact(String.valueOf(source.getLocationContact().getId()));
			dest.setContactfirstname(source.getLocationContact().getName().getFirstName());
			dest.setContactlastname(source.getLocationContact().getName().getLastName());
			dest.setContactemail(source.getLocationContact().getEmail());
			if (source.getLocationContact().isSetProfile() && source.getLocationContact().getProfile().getPhoneNumbersSize() > 0) {
				dest.setContactphone(source.getLocationContact().getProfile().getPhoneNumbers().get(0).getPhone());
				dest.setContactphone_extension(source.getLocationContact().getProfile().getPhoneNumbers().get(0).getExtension());
			}
		}

		if (source.isSetSecondaryLocationContact()) {
			dest.setOnsite_secondary_contact(String.valueOf(source.getSecondaryLocationContact().getId()));
			dest.setSecondarycontactfirstname(source.getSecondaryLocationContact().getName().getFirstName());
			dest.setSecondarycontactlastname(source.getSecondaryLocationContact().getName().getLastName());
			dest.setSecondarycontactemail(source.getSecondaryLocationContact().getEmail());
			if (source.getSecondaryLocationContact().isSetProfile() && source.getSecondaryLocationContact().getProfile().getPhoneNumbersSize() > 0) {
				dest.setSecondarycontactphone(source.getSecondaryLocationContact().getProfile().getPhoneNumbers().get(0).getPhone());
				dest.setSecondarycontactphone_extension(source.getSecondaryLocationContact().getProfile().getPhoneNumbers().get(0).getExtension());
			}
		}

		if (source.isSetSchedule() && source.getTimeZone() != null && source.getSchedule() != null && source.getSchedule().getFrom() != 0) {
			dest.setScheduling(source.getSchedule().isRange());
			Date from = DateUtilities.changeTimeZone(source.getSchedule().getFrom(), source.getTimeZone());
			if (source.getSchedule().isRange()) {
				if (!DateUtilities.isNearEpoch(source.getSchedule().getFrom())) {
					dest.setVariable_from(from);
					dest.setVariable_fromtime(from);
				}
				Date through = DateUtilities.changeTimeZone(source.getSchedule().getThrough(), source.getTimeZone());
				if (!DateUtilities.isNearEpoch(source.getSchedule().getThrough())) {
					dest.setTo(through);
					dest.setTotime(through);
				}
			} else {
				if (!DateUtilities.isNearEpoch(source.getSchedule().getFrom())) {
					dest.setFrom(from);
					dest.setFromtime(from);
				}
			}
		}

		if (source.isResourceConfirmationRequired()) {
			dest.setResource_confirmation(source.isResourceConfirmationRequired());
		}

		if (source.getResourceConfirmationHours() != 0) {
			dest.setResource_confirmation_hours(source.getResourceConfirmationHours());
		}

		if (source.getConfiguration().isCheckinRequiredFlag()) {
			dest.setCheck_in(source.getConfiguration().isCheckinRequiredFlag());
		}

		if (source.isCheckinCallRequired()) {
			dest.setCheck_in_call_required(source.isCheckinCallRequired());
		}

		if (dest.getCheck_in_call_required() != null) {
			dest.setCheck_in_contact_name(source.getConfiguration().getCheckinContactName());
			dest.setCheck_in_contact_phone(source.getConfiguration().getCheckinContactPhone());
		}

		if (source.getConfiguration().isShowCheckoutNotesFlag()) {
			dest.setShow_check_out_notes(source.getConfiguration().isShowCheckoutNotesFlag());
		}

		dest.setCheck_out_notes_requiredness(source.getConfiguration().isCheckoutNoteRequiredFlag() ? "required" : "optional");

		if (source.getConfiguration().getCheckoutNoteInstructions() != null) {
			dest.setCheck_out_notes_instructions(source.getConfiguration().getCheckoutNoteInstructions());
		}

		if (source.isTimetrackingRequired()) {
			dest.setRequire_timetracking(source.isTimetrackingRequired());
		}

		if (source.getConfiguration().isIvrEnabledFlag()) {
			dest.setIvr_active(source.getConfiguration().isIvrEnabledFlag());
		}

		if (source.getPricing().getId() != 0) {
			dest.setPricing(source.getPricing().getId());
		}

		dest.setPricing_mode(source.getConfiguration().isUseMaxSpendPricingDisplayModeFlag() ? "spend" : "pay");

		if (source.getPricing().getFlatPrice() != 0) {
			dest.setFlat_price(source.getPricing().getFlatPrice());
		}

		if (source.getPricing().getPerHourPrice() != 0) {
			dest.setPer_hour_price(source.getPricing().getPerHourPrice());
		}

		if (source.getPricing().getMaxNumberOfHours() != 0) {
			dest.setMax_number_of_hours(source.getPricing().getMaxNumberOfHours());
		}

		if (source.getPricing().getPerUnitPrice() != 0) {
			dest.setPer_unit_price(source.getPricing().getPerUnitPrice());
		}

		if (source.getPricing().getMaxNumberOfUnits() != 0) {
			dest.setMax_number_of_units(source.getPricing().getMaxNumberOfUnits());
		}

		if (source.getPricing().getInitialPerHourPrice() != 0) {
			dest.setInitial_per_hour_price(source.getPricing().getInitialPerHourPrice());
		}

		if (source.getPricing().getInitialNumberOfHours() != 0) {
			dest.setInitial_number_of_hours(source.getPricing().getInitialNumberOfHours());
		}

		if (source.getPricing().getAdditionalPerHourPrice() != 0) {
			dest.setAdditional_per_hour_price(source.getPricing().getAdditionalPerHourPrice());
		}

		if (source.getPricing().getMaxBlendedNumberOfHours() != 0) {
			dest.setMax_blended_number_of_hours(source.getPricing().getMaxBlendedNumberOfHours());
		}

		if (source.getConfiguration().getPaymentTermsDays() !=  0) {
			dest.setPayment_terms_days(source.getConfiguration().getPaymentTermsDays());
		}

		if (source.getConfiguration().isDisablePriceNegotiation()) {
			dest.setDisablePriceNegotiation(source.getConfiguration().isDisablePriceNegotiation());
		}

		if (source.getDeliverableRequirementGroupDTO() != null) {
			DeliverableRequirementGroupDTO deliverableRequirementGroupDTO = source.getDeliverableRequirementGroupDTO();

			ResourceCompletionForm resourceCompletionForm = new ResourceCompletionForm(deliverableRequirementGroupDTO.getId(),
					deliverableRequirementGroupDTO.getInstructions(),
					deliverableRequirementGroupDTO.getHoursToComplete());

			List<DeliverableRequirementForm> deliverableRequirementForms = Lists.newArrayList();
			List<DeliverableRequirementDTO> deliverableRequirementDTOs = deliverableRequirementGroupDTO.getDeliverableRequirementDTOs();

			if (deliverableRequirementDTOs == null) {
				deliverableRequirementDTOs = Lists.newArrayList();
			}

			for (DeliverableRequirementDTO deliverableRequirementDTO : deliverableRequirementDTOs) {
				DeliverableRequirementForm deliverableRequirementForm = new DeliverableRequirementForm(deliverableRequirementDTO.getId(),
					deliverableRequirementDTO.getType(),
					deliverableRequirementDTO.getInstructions(),
					deliverableRequirementDTO.getNumberOfFiles());

				deliverableRequirementForms.add(deliverableRequirementForm);
			}
			resourceCompletionForm.setDeliverableRequirements(deliverableRequirementForms);
			dest.setResourceCompletionForm(resourceCompletionForm);
		}

		if (source.getAssetsSize() > 0 || source.getUploadsSize() > 0) {
			List<WorkAssetForm> assets = Lists.newArrayList();
			if (source.getAssetsSize() > 0 ) {
				for (Asset a : source.getAssets()) {
					if (a != null) {
						WorkAssetForm assetForm = new WorkAssetForm();
						assetForm.setId(a.getId());
						assetForm.setUuid(a.getUuid());
						assetForm.setName(a.getName());
						assetForm.setDescription(a.getDescription());
						assetForm.setMimeType(a.getMimeType());
						assetForm.setUri(a.getUri());
						assetForm.setMimeTypeIcon(MimeTypeUtilities.getMimeIconName(a.getMimeType()));
						assetForm.setIsUpload(false);
						assetForm.setVisibilityType(a.getVisibilityCode());
						assets.add(assetForm);
					}
				}
			}
			if (source.getUploadsSize() > 0) {
				for (Upload u : source.getUploads()) {
					WorkAssetForm assetForm = new WorkAssetForm();
					assetForm.setId(u.getId());
					assetForm.setUuid(u.getUuid());
					assetForm.setName(u.getName());
					assetForm.setDescription(u.getDescription());
					assetForm.setUri(u.getUri());
					assetForm.setIsUpload(true);
					assets.add(assetForm);
				}
			}
			dest.setAttachments(assets);
		}

		if (source.getAssessmentsSize() > 0) {
			List<WorkAssessmentForm> assessments = Lists.newArrayList();
			for (Assessment a : source.getAssessments()) {
				assessments.add(BeanUtilities.newBean(WorkAssessmentForm.class, a));
			}
			dest.setAssessments(assessments);
		}

		if (source.getPartGroup() != null) {
			dest.setRequiresParts(true);
			dest.setPartGroup(partGroupDTOToPartGroupFormConverter.convert(source.getPartGroup()));
		}

		if (source.isSetTemplate()) {
			if (source.getTemplate().isSetId()) {
				dest.setWork_template_id(source.getTemplate().getId());
			}
			dest.setTemplate_name(source.getTemplate().getName());
			dest.setTemplate_description(source.getTemplate().getDescription());
		}

		if (source.getConfiguration().isBadgeShowClientName()) {
			dest.setBadge_show_client_name(source.getConfiguration().isBadgeShowClientName());
		}

		if (source.getConfiguration().isAssignToFirstResource()) {
			dest.setAssign_to_first_resource(source.getConfiguration().isAssignToFirstResource());
		}
		dest.setShow_in_feed(source.getConfiguration().isShowInFeed());
		dest.setAssign_to_first_resource(source.getConfiguration().isAssignToFirstResource());
		dest.setShow_in_feed(source.getConfiguration().isShowInFeed());

		dest.setFollowers(source.getFollowers());

		if (CollectionUtils.isNotEmpty(source.getFirstToAcceptGroups())) {
			WorkFormRouting routing = dest.getRouting();
			if (routing == null) {
				routing = new WorkFormRouting();
				routing.setAssignToFirstToAcceptGroupIds(Sets.newHashSet(source.getFirstToAcceptGroups()));
				dest.setRouting(routing);
			} else {
				routing.setAssignToFirstToAcceptGroupIds(Sets.newHashSet(source.getFirstToAcceptGroups()));
			}
		}

		if (CollectionUtils.isNotEmpty(source.getNeedToApplyGroups())) {
			WorkFormRouting routing = dest.getRouting();
			if (routing == null) {
				routing = new WorkFormRouting();
				routing.setGroupIds(Sets.newHashSet(source.getNeedToApplyGroups()));
				routing.setNeedToApplyGroupIds(Sets.newHashSet(source.getNeedToApplyGroups()));
				dest.setRouting(routing);
			} else {
				routing.setGroupIds(Sets.newHashSet(source.getNeedToApplyGroups()));
				routing.setNeedToApplyGroupIds(Sets.newHashSet(source.getNeedToApplyGroups()));
			}
			dest.setGroupIds(source.getNeedToApplyGroups());
		}

		dest.setRequirementSetIds(source.getRequirementSetIds());

		if (source.getConfiguration().isUseRequirementSets()) {
			dest.setUseRequirementSets(source.getConfiguration().isUseRequirementSets());
		}

		dest.setSmart_route(source.getConfiguration().isSmartRoute());

		dest.setUniqueExternalId(source.getUniqueExternalIdValue());
		dest.setUniqueExternalIdDisplayName(source.getUniqueExternalIdDisplayName());

		return dest;
	}
}
