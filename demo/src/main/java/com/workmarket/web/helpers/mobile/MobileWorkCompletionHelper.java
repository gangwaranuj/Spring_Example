package com.workmarket.web.helpers.mobile;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.AvailabilityType;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.Attempt;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.WorkResourceTimeTracking;
import com.workmarket.domains.work.service.DeliverableService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.business.dto.DeliverableRequirementDTO;
import com.workmarket.service.business.dto.DeliverableRequirementGroupDTO;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.thrift.work.Work;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * User: micah
 * Date: 9/6/13
 * Time: 3:27 PM
 */
@Component
public class MobileWorkCompletionHelper {

	@Autowired WorkService workService;
	@Autowired AssessmentService assessmentService;
	@Autowired DeliverableService deliverableService;
	@Autowired private MessageBundleHelper messageHelper;


	public List<MobileResponse> validateAll(Work work, ExtendedUserDetails userDetails) {
		AbstractWork abstractWork = workService.findWork(work.getId(), true);

		List<MobileResponse> validationResults = Lists.newArrayList();

		// is confirmation required?
		if (abstractWork.isResourceConfirmationRequired()) {
			validationResults.add(validateConfirmation(abstractWork));
		}
		// is worker checkin or checkin call required?
		if (abstractWork.isCheckinRequired() || abstractWork.isCheckinCallRequired()) {
			validationResults.add(validateCheckInOut(abstractWork));
		}

		// is a survey required?
		if (abstractWork.getRequiredAssessments().size() > 0) {
			validationResults.add(validateAssessments(abstractWork, userDetails));
		}

		if (work.getDeliverableRequirementGroupDTO() != null) {
			if (CollectionUtils.isNotEmpty(work.getDeliverableRequirementGroupDTO().getDeliverableRequirementDTOs())) {
				validationResults.add(validateDeliverables(work.getDeliverableRequirementGroupDTO(), work.getWorkNumber()));
			}
		}

		// custom fields required?
		validationResults.add(validateCustomFields(work));
		validationResults.removeAll(Collections.singleton(null));

		return validationResults;
	}


	public MobileResponse validateConfirmation(AbstractWork work) {
		// Is resource confirmed
		MobileResponse response = new MobileResponse(false, messageHelper.getMessage("mobile.v2.validation.error"));
		WorkResource resource = workService.findActiveWorkResource(work.getId());
		if (resource != null) {
			if (!resource.isConfirmed()) {
				response.setMessage(messageHelper.getMessage("mobile.v2.confirm.message"));
				return response;
			}
			response.setSuccessful(true);
			response.setMessage(messageHelper.getMessage("mobile.v2.confirm.success"));
			return response;
		}

		return response;
	}

	public MobileResponse validateCheckInOut(AbstractWork work) {
		MobileResponse response = new MobileResponse(false, messageHelper.getMessage("mobile.v2.validation.error"));

		// If checkin is required, get checkIn/checkOut status. If resourceTimeTrackRecord is null then worker has not checked in nor out yet
		WorkResourceTimeTracking resourceTimeTrackRecord = null;
		WorkResource resource = workService.findActiveWorkResource(work.getId());

		// check to see if worker has checked in at least once. If not then resourceTimeTrackRecord will remain null
		if (resource != null) {
			resourceTimeTrackRecord = workService.findLatestTimeTrackRecordByWorkResource(resource.getId());
		}

		// If record is null then worker has not checked in yet
		if (resourceTimeTrackRecord == null) {
			response.setMessage(messageHelper.getMessage("mobile.v2.checkin.required"));

		// If true worker has checked in but not out, help em out.
		} else if (workService.isActiveResourceCurrentlyCheckedIn(work.getId())) {
			response.setMessage(messageHelper.getMessage("mobile.v2.checkout.required"));

		// Success, the worker has satisfied all the checkin requirements for this assignment
		} else {
			response.setSuccessful(true);
			response.setMessage(messageHelper.getMessage("mobile.v2.checkin.success"));
		}

		return response;
	}

	public MobileResponse validateAssessments(AbstractWork work, ExtendedUserDetails user) {
		// Check if all required Surveys have been completed
		boolean allSurveysCompleted = true;
		// Loop through required Surveys to get assessmentId
		for (Iterator<AbstractAssessment> it = work.getRequiredAssessments().iterator(); it.hasNext();) {
			AbstractAssessment abstractAssessment = it.next();
			// Check attempt to determine if Survey was completed
			Attempt attempt = assessmentService.findLatestAttemptForAssessmentByUserScopedToWork(abstractAssessment.getId(), user.getId(), work.getId());
			if (attempt == null || !attempt.isComplete()) {
				allSurveysCompleted = false;
				break;
			}
		}
		if (!allSurveysCompleted) {
			return new MobileResponse(false, messageHelper.getMessage("mobile.v2.survey.required"));
		} else {
			return new MobileResponse(true, messageHelper.getMessage("mobile.v2.survey.success"));
		}
	}

	public MobileResponse validateDeliverables(DeliverableRequirementGroupDTO deliverableRequirementGroupDTO, String workId) {
		int numberOfAttachmentsLeftToUpload = 0;
		int totalNumberOfAttachmentsRequired = 0;

		if (deliverableRequirementGroupDTO != null) {
			for (DeliverableRequirementDTO deliverableRequirementDTO : deliverableRequirementGroupDTO.getDeliverableRequirementDTOs()) {
				int requiredNumberOfFiles = deliverableRequirementDTO.getNumberOfFiles();
				totalNumberOfAttachmentsRequired += requiredNumberOfFiles;

				int numberOfSubmittedDeliverables = deliverableService.countDeliverableAssetsByDeliverableRequirementId(deliverableRequirementDTO.getId());
				if (numberOfSubmittedDeliverables < requiredNumberOfFiles) {
					numberOfAttachmentsLeftToUpload += (requiredNumberOfFiles - numberOfSubmittedDeliverables);
				}
			}
		}

		if (numberOfAttachmentsLeftToUpload > 0) {
			StringBuilder sb = new StringBuilder()
				.append(numberOfAttachmentsLeftToUpload)
				.append(StringUtilities.pluralize(" more deliverable", numberOfAttachmentsLeftToUpload))
				.append(" must be <a href=/mobile/assignments/deliverables/" + workId + " style=\"text-decoration: none\" data-ajax=\"false\">uploaded</a>");
			return new MobileResponse(false, sb.toString());
		} else {
			return new MobileResponse(true, messageHelper.getMessage("mobile.v2.deliverables.success"));
		}
	}

	public MobileResponse validateCustomFields(Work work) {
		boolean foundFault = false;
		List<CustomFieldGroup> customFieldGroups = work.getCustomFieldGroups();
		if (customFieldGroups != null) {
			Iterator<CustomFieldGroup> groupIterator = customFieldGroups.iterator();
			while (!foundFault && groupIterator.hasNext()) {
				List<CustomField> customFields =  groupIterator.next().getFields();
				if (customFields != null) {
					Iterator<CustomField> fieldIterator = customFields.iterator();
					if (!foundFault && fieldIterator.hasNext()) {
						CustomField customField = fieldIterator.next();
						if (AvailabilityType.RESOURCE.equals(customField.getType()) && customField.isIsRequired() && StringUtils.isEmpty(customField.getValue())) {
							foundFault = true;
							break;
						}
					}
				}
			}
			if (foundFault) {
				return new MobileResponse(false, "Required custom fields must be <a href=/mobile/assignments/customfields/" + work.getWorkNumber() +  " style=\"text-decoration: none\">filled out</a>");
			}
			return new MobileResponse(true, "All required custom fields are satisfied.");
		}

		return null;
	}
}
