package com.workmarket.thrift.assessment.validator;

import com.google.common.collect.Lists;
import com.mysql.jdbc.StringUtils;
import com.workmarket.dao.assessment.AbstractAssessmentDAO;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.validation.MessageKeys;
import com.workmarket.thrift.assessment.Choice;
import com.workmarket.thrift.assessment.ItemSaveRequest;
import com.workmarket.thrift.assessment.ItemType;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.thrift.core.Upload;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.utility.MimeTypeUtilities;
import org.apache.commons.validator.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ItemSaveRequestValidator {
	
	@Autowired private AbstractAssessmentDAO assessmentDAO;
	
	public void validate(ItemSaveRequest request) throws ValidationException {
		List<ConstraintViolation> errors = Lists.newLinkedList();
		validateItem(request, errors);
		
		if (errors.size() > 0) {
			throw new ValidationException("Unable to save assessment", errors);
		}
	}
	
	private void validateItem(ItemSaveRequest request, List<ConstraintViolation> errors) {
		
		AbstractAssessment assessment = assessmentDAO.findAssessmentById(request.getAssessmentId());
		boolean isGraded = assessment.getType().equals(AbstractAssessment.GRADED_ASSESSMENT_TYPE);
		
		if (!request.getItem().isSetType()) {
			errors.add(new ConstraintViolation().setProperty("type").setError(MessageKeys.NOT_NULL));
		} else {
			ItemType type = request.getItem().getType();

			if (!type.equals(ItemType.DIVIDER) && StringUtils.isEmptyOrWhitespaceOnly(request.getItem().getPrompt())) {
				errors.add(new ConstraintViolation().setProperty("question text").setError(MessageKeys.NOT_NULL));
			}
			
			if (type.equals(ItemType.MULTIPLE_CHOICE) || type.equals(ItemType.SINGLE_CHOICE_LIST) || type.equals(ItemType.SINGLE_CHOICE_RADIO)) {
				if (!request.getItem().isSetChoices() || request.getItem().getChoicesSize() == 0) {
					errors.add(new ConstraintViolation().setProperty("item").setError(MessageKeys.Assessment.CHOICES_REQUIRED));
				} else if (isGraded) {
					// Verify that at least one correct answer is identified. Unless "other is an option...
					// Only a reasonable demand for graded assessments.
					
					if (request.getItem().isGraded() && !request.getItem().isOtherAllowed()) {
						boolean hasCorrect = false;
						for (Choice c : request.getItem().getChoices())
							if (c.isCorrect())
								hasCorrect = true;
						if (!hasCorrect) {
							errors.add(new ConstraintViolation().setProperty("item").setError(MessageKeys.Assessment.CORRECT_CHOICE_REQUIRED));
						}
					}
				}

				// Check for empty answers
				for (Choice choice: request.getItem().getChoices()) {
					if (StringUtils.isEmptyOrWhitespaceOnly(choice.getValue())) {
						errors.add(new ConstraintViolation().setProperty("item").setError(MessageKeys.Assessment.CHOICES_EMPTY));
						break;
					}
				}
			} else {
				if (request.getItem().isSetChoices() || request.getItem().getChoicesSize() > 0) {
					errors.add(new ConstraintViolation().setProperty("item").setError(MessageKeys.Assessment.CHOICES_NOT_ALLOWED));
				}
			}

			if(request.getItem().isSetEmbedLink()) {
				String embedUrl = request.getItem().getEmbedLink();
				String[] schemes = {"http","https"};
				UrlValidator urlValidator = new UrlValidator(schemes);
				if(!urlValidator.isValid(embedUrl)){
					errors.add(new ConstraintViolation().setProperty("item").setError(MessageKeys.Assessment.INVALID_EMBED_URL));
				}
			}

			//validate that user uploads only one video/embed
			int mediaCount = 0;
			if(request.getItem().isSetUploads()) {
				for (Upload tupload : request.getItem().getUploads()) {
					String contentType = MimeTypeUtilities.guessMimeType(tupload.getName());
					if(contentType.contains("video") || contentType.contains("audio") || MimeTypeUtilities.isMedia(contentType)){
						mediaCount += 1;
					}
				}
			}

			if(request.getItem().isSetEmbedLink())
				mediaCount += 1;

			if(mediaCount > 1){
				errors.add(new ConstraintViolation().setProperty("item").setError(MessageKeys.Assessment.TOO_MANY_MEDIA_UPLOAD_ERROR));
			}
		}
	}
}
