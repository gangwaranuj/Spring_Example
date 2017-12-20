package com.workmarket.web.validators;

import com.workmarket.domains.model.DeliverableRequirement;
import com.workmarket.domains.model.asset.type.WorkAssetAssociationType;
import com.workmarket.domains.work.service.DeliverableService;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeliverableValidator {

	@Autowired protected DeliverableService deliverableService;
	@Autowired protected MessageBundleHelper messageHelper;

	public void validate(Long deliverableRequirementId, String mimeType, MessageBundle messageBundle) {

		if (StringUtils.isBlank(mimeType)) {
			messageHelper.addError(messageBundle, "deliverable.validation.invalid_format");
		}

		if (deliverableRequirementId != null) {
			DeliverableRequirement deliverableRequirement = deliverableService.findDeliverableRequirementById(deliverableRequirementId);
			if (deliverableRequirement == null) {
				messageHelper.addError(messageBundle, "deliverable.validation.invalid_deliverable_requirement");
			} else {
				// If deliverable asset is associated with a photo requirement but is NOT an image
				boolean isImage = MimeTypeUtilities.isImage(mimeType);
				if (WorkAssetAssociationType.createPhotoAssociationType().equals(deliverableRequirement.getType()) && !isImage) {
					messageHelper.addError(messageBundle, "deliverable.validation.photos.error.wrong_format");
				}
			}
		}
	}
}
