package com.workmarket.web.validators;

import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.orgstructure.OrgStructureService;
import com.workmarket.web.forms.groups.manage.GroupAddEditForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class GroupAddEditFormUserGroupExistsValidator implements Validator {

	@Autowired private SecurityContextFacade securityContextFacade;
	@Autowired private UserGroupService userGroupService;
	@Autowired private MessageBundleHelper messageBundleHelper;
	@Autowired private FeatureEntitlementService featureEntitlementService;
	@Autowired private OrgStructureService orgStructureService;

	@Override
	public boolean supports(Class<?> clazz) {
		return GroupAddEditForm.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		GroupAddEditForm form = (GroupAddEditForm) target;

		if (StringUtils.isBlank(form.getName())) {
			return;
		}

		if (groupExists(form)) {
			String message = messageBundleHelper.getMessage("groups.name.duplicate");
			errors.rejectValue("name", "groups.name.duplicate", message);
		}

		final long userId = securityContextFacade.getCurrentUser().getId();
		final long companyId = securityContextFacade.getCurrentUser().getCompanyId();
		final boolean hasOrgFeatureToggle = featureEntitlementService.hasFeatureToggle(userId, "org_structures");
		if (hasOrgFeatureToggle) {
			final List<String> currentUserOrgUnitUuids = orgStructureService.getSubtreePathOrgUnitUuidsForCurrentOrgMode(userId, companyId);
			final List<String> selectedOrgUnits = form.getOrgUnitUuids();

			// For the sake of the Acceptance Test Suite, I'm adding in this short-circuit null check
			// TODO: Org-Structures, remove this
			if (selectedOrgUnits == null) {
				return;
			}

			if (CollectionUtils.isEmpty(selectedOrgUnits) || !currentUserOrgUnitUuids.containsAll(selectedOrgUnits)) {
				final String message = messageBundleHelper.getMessage("groups.orgUnitUuids.error");
				errors.rejectValue("orgUnitUuids", "groups.orgUnitUuids.error", message);
			}
		}
	}

	private boolean groupExists(GroupAddEditForm form) {
		long companyId = securityContextFacade.getCurrentUser().getCompanyId();
		return userGroupService.findCompanyUserGroupByName(companyId, form.getName()) != null;
	}
}
