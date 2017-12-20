package com.workmarket.web.validators;

import com.workmarket.domains.work.model.project.Project;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.math.BigDecimal;

@Component("projectValidator")
public class ProjectValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return Project.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Project project = (Project)target;

		ValidationUtils.rejectIfEmpty(errors, "name", "projects.title.required");
		ValidationUtils.rejectIfEmpty(errors, "description", "projects.description.required");

		if (project.getOwner() == null) {
			errors.rejectValue("creatorId", "projects.creator.required");
		}

		if (project.getClientCompany() == null) {
			errors.rejectValue("clientCompany", "projects.client.required");
		}

		if(project.getBudgetEnabledFlag() && project.getRemainingBudget().compareTo(BigDecimal.ZERO) != 1) {
			errors.rejectValue("remainingBudget", "projects.remainingBudget.greaterThanZero");
		}

		if (StringUtils.isNotBlank(project.getName())) {
			if (!StringUtilities.stripXSSAndEscapeHtml(project.getName()).equals(project.getName()) ||
					!StringUtilities.stripHTML(project.getName()).equals(project.getName())) {
				errors.rejectValue("name", "projects.title.invalid");
			}
		}

	}
}
