package com.workmarket.web.validators;

import com.workmarket.domains.model.DeliverableRequirement;
import com.workmarket.domains.model.asset.type.WorkAssetAssociationType;
import com.workmarket.domains.work.service.DeliverableService;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.anyString;

@RunWith(MockitoJUnitRunner.class)
public class DeliverableValidatorTest {

	@Mock DeliverableService deliverableService;
	@Mock MessageBundleHelper messageBundleHelper;
	@InjectMocks DeliverableValidator deliverableValidator;

	@Mock MessageBundle messageBundle;
	@Mock DeliverableRequirement deliverableRequirement;

	String mimetype = "image/png";
	Long deliverableRequirementId = 1L;
	WorkAssetAssociationType photoType = WorkAssetAssociationType.createPhotoAssociationType();

	@Before
	public void setup() {
		when(deliverableService.findDeliverableRequirementById(deliverableRequirementId)).thenReturn(deliverableRequirement);
		when(deliverableRequirement.getType()).thenReturn(photoType);
	}

	@Test
	public void validate_nullDeliverableRequirementId_pass() throws Exception {
		deliverableValidator.validate(null, mimetype, messageBundle);

		verify(messageBundleHelper, never()).addError(eq(messageBundle), anyString());
	}

	@Test
	public void validate_invalidDeliverableRequirementId_fail() throws Exception {
		when(deliverableService.findDeliverableRequirementById(deliverableRequirementId)).thenReturn(null);

		deliverableValidator.validate(deliverableRequirementId, mimetype, messageBundle);

		verify(messageBundleHelper).addError(messageBundle, "deliverable.validation.invalid_deliverable_requirement");
	}

	@Test
	public void validate_isPhotoDeliverable_pass() throws Exception {
		deliverableValidator.validate(deliverableRequirementId, mimetype, messageBundle);

		verify(messageBundleHelper, never()).addError(messageBundle, "deliverable.validation.photos.error.wrong_format");
	}

	@Test
	public void validate_isPhotoDeliverable_fail() throws Exception {
		deliverableValidator.validate(deliverableRequirementId, "derp/bip", messageBundle);

		verify(messageBundleHelper).addError(messageBundle, "deliverable.validation.photos.error.wrong_format");
	}

}
