package com.workmarket.web.validators;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VisibilityType;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Created by alejandrosilva on 1/20/15.
 */
@Component
public class UpdateDocumentVisibilityValidator {

	@Autowired AssetManagementService assetManagementService;
	@Autowired MessageBundleHelper messageHelper;
	@Autowired FeatureEvaluator featureEvaluator;
	@Autowired AuthenticationService authenticationService;
	@Autowired TWorkFacadeService tWorkFacadeService;
	@Autowired WorkService workService;

	private static final Log logger = LogFactory.getLog(UpdateDocumentVisibilityValidator.class);
	public final static String GENERIC_ERROR = "assignment.documents.toggle_visibility.generic_error";

	public void validate(String workNumber, Long assetId, String visibilityCode, MessageBundle messageBundle) {
		Assert.notNull(messageBundle);

		Long workId = null;
		User currentUser = authenticationService.getCurrentUser();

		if (workNumber == null) {
			messageBundle.addError("assignment.documents.toggle_visibility.missing_work");
		} else {
			workId = workService.findWorkId(workNumber);
			if (workId == null) {
				messageHelper.addError(messageBundle, "assignment.documents.toggle_visibility.invalid_work", workNumber);
			}
		}

		if (workId != null) {
			WorkResponse workResponse = null;
			try {
				workResponse = tWorkFacadeService.findWork(buildWorkRequest(currentUser, workNumber));
			} catch (WorkActionException e) {
				logger.error("An error occurred while finding work with workNumber:" + workNumber, e);
			}

			if (workResponse == null || !CollectionUtilities.contains(workResponse.getAuthorizationContexts(), AuthorizationContext.ADMIN)) {
				// Overwrite other errors
				messageBundle.setErrors(ImmutableList.of(GENERIC_ERROR));
				return;
			}
		}

		if (assetId == null) {
			messageBundle.addError("assignment.documents.toggle_visibility.missing_asset");
		} else if (assetManagementService.findAssetById(assetId) == null) {
			messageHelper.addError(messageBundle, "assignment.documents.toggle_visibility.invalid_asset", assetId);
		}

		if (!StringUtils.hasText(visibilityCode)) {
			messageBundle.addError("assignment.documents.toggle_visibility.missing_code");
		} else if (!VisibilityType.isValidTypeCode(visibilityCode)) {
			messageHelper.addError(messageBundle, "assignment.documents.toggle_visibility.invalid_code", visibilityCode);
		}

	}

	public WorkRequest buildWorkRequest(User currentUser, String workNumber) {
		return new WorkRequest()
			.setUserId(currentUser.getId())
			.setWorkNumber(workNumber)
			.setIncludes(Sets.newHashSet(
				WorkRequestInfo.CONTEXT_INFO
			));
	}
}
