package com.workmarket.web.validators;

import com.google.common.collect.ImmutableList;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.configuration.Constants;
import com.workmarket.thrift.core.Status;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RejectDeliverableValidator {

	@Autowired TWorkFacadeService tWorkFacadeService;
	@Autowired AssetManagementService assetManagementService;
	@Autowired CompanyService companyService;

	public final static List<String> VALID_WORK_STATES_TO_REJECT_A_DELIVERABLE = ImmutableList.of(
		WorkStatusType.ACTIVE,
		WorkStatusType.COMPLETE // Assignment is pending approval
	);

	public static final String
		GENERIC_ERROR = "rejectDeliverable.validation.genericError",
		COMPANY_SUSPENDED_ERROR = "assignment.sendback.suspended",
		DUPLICATE_REJECTION_ERROR = "rejectDeliverable.validation.DuplicateRejectionError",
		EMPTY_REJECTION_REASON_ERROR = "rejectDeliverable.validation.EmptyReason",
		CHARACTER_LIMIT_EXCEEDED_ERROR = "rejectDeliverable.validation.ExceededCharacterLimit";

	public void validate(WorkResponse workResponse, String rejectReason, Long assetId, Long currentUserCompanyId, MessageBundle messageBundle) {

		WorkAssetAssociation workAssetAssociation = assetManagementService.findAssetAssociationsByWorkAndAsset(workResponse.getWork().getId(), assetId);

		validateOwnership(workResponse, workAssetAssociation, messageBundle);
		validateCompanyStatus(currentUserCompanyId, messageBundle);

		if (messageBundle.hasErrors()) {
			return;
		}

		validateActiveStatusOfDeliverable(workAssetAssociation, messageBundle);
		validateRejectReason(rejectReason, messageBundle);
		validateWorkStatus(workResponse.getWork().getStatus(), messageBundle);
	}

	private void validateOwnership(WorkResponse workResponse, WorkAssetAssociation workAssetAssociation, MessageBundle messageBundle) {

		// Check that the currentUser either created this assignment or is an admin for the company that created this assignment
		if (!CollectionUtilities.containsAny(workResponse.getAuthorizationContexts(), AuthorizationContext.BUYER, AuthorizationContext.ADMIN)) {
			messageBundle.addError(GENERIC_ERROR);
			return;
		}

		if (workAssetAssociation == null) {
			messageBundle.addError(GENERIC_ERROR);
		}
	}

	private void validateCompanyStatus(Long currentUserCompanyId, MessageBundle messageBundle) {
		com.workmarket.domains.model.Company company = companyService.findCompanyById(currentUserCompanyId);
		if (company.isSuspended()) {
			messageBundle.addError(COMPANY_SUSPENDED_ERROR);
		}
	}

	private void validateActiveStatusOfDeliverable(WorkAssetAssociation workAssetAssociation, MessageBundle messageBundle) {
		if (workAssetAssociation.getRejectedOn() != null) {
			messageBundle.addError(DUPLICATE_REJECTION_ERROR);
		}
	}

	private void validateRejectReason(String rejectReason, MessageBundle messageBundle) {
		if (StringUtils.isBlank(rejectReason)) {
			messageBundle.addError(EMPTY_REJECTION_REASON_ERROR);
			return;
		}

		if (rejectReason.length() > Constants.TEXT_SHORT) {
			messageBundle.addError(CHARACTER_LIMIT_EXCEEDED_ERROR);
		}
	}

	private void validateWorkStatus(Status workStatus, MessageBundle messageBundle) {
		if (!VALID_WORK_STATES_TO_REJECT_A_DELIVERABLE.contains(workStatus.getCode())) {
			messageBundle.addError(GENERIC_ERROR);
		}
	}
}
