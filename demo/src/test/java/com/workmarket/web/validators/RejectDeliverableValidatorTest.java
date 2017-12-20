package com.workmarket.web.validators;

import com.google.common.collect.ImmutableSet;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.configuration.Constants;
import com.workmarket.thrift.core.Status;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyLong;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class RejectDeliverableValidatorTest {

	@Mock TWorkFacadeService tWorkFacadeService;
	@Mock AssetManagementService assetManagementService;
	@Mock CompanyService companyService;
	@InjectMocks RejectDeliverableValidator rejectDeliverableValidator;

	@Mock private WorkAssetAssociation workAssetAssociation = mock(WorkAssetAssociation.class);
	@Mock private WorkResponse workResponse = mock(WorkResponse.class);
	@Mock private Work work = mock(Work.class);
	@Mock private Status workStatus = mock(Status.class);
	@Mock private Company company = mock(Company.class);
	@Mock private Calendar rejectedOnDate = mock(Calendar.class);

	private MessageBundle messageBundle;
	private static Set<AuthorizationContext>
		ACCEPTED_WORK_AUTHORIZATION_CONTEXTS = ImmutableSet.of(AuthorizationContext.BUYER, AuthorizationContext.ADMIN),
		UNACCEPTED_WORK_AUTHORIZATION_CONTEXTS = ImmutableSet.of(AuthorizationContext.ACTIVE_RESOURCE);
	private static Long
		WORK_ID = 1L,
		ASSET_ID = 1L,
		CURRENT_USER_COMPANY_ID = 1L;
	private static String
		REJECTION_REASON = "Subject is out of focus. Please upload another photo.",
		BLANK_REJECTION_REASON = "     ",
		REALLY_LONG_REJECTION_REASON = StringUtils.leftPad("", Constants.TEXT_SHORT + 1, '*'),
		ACTIVE_WORK_STATUS = WorkStatusType.ACTIVE;

	@Before
	public void setup() {
		messageBundle = new MessageBundle();

		when(workAssetAssociation.getRejectedOn()).thenReturn(null);
		when(assetManagementService.findAssetAssociationsByWorkAndAsset(anyLong(), anyLong())).thenReturn(workAssetAssociation);
		when(company.isSuspended()).thenReturn(false);
		when(companyService.findCompanyById(anyLong())).thenReturn(company);
		when(workResponse.getAuthorizationContexts()).thenReturn(ACCEPTED_WORK_AUTHORIZATION_CONTEXTS);
		when(work.getId()).thenReturn(WORK_ID);
		when(workStatus.getCode()).thenReturn(ACTIVE_WORK_STATUS);
		when(work.getStatus()).thenReturn(workStatus);
		when(workResponse.getWork()).thenReturn(work);
	}

	@Test
	public void validateOwnership_ValidAuthorizationContext_NoErrors() {
		rejectDeliverableValidator.validate(workResponse, REJECTION_REASON, ASSET_ID, CURRENT_USER_COMPANY_ID, messageBundle);

		assertFalse(messageBundle.hasErrors());
	}

	@Test
	public void validateOwnership_InvalidAuthorizationContext_Errors() {
		when(workResponse.getAuthorizationContexts()).thenReturn(UNACCEPTED_WORK_AUTHORIZATION_CONTEXTS);

		rejectDeliverableValidator.validate(workResponse, REJECTION_REASON, ASSET_ID, CURRENT_USER_COMPANY_ID, messageBundle);

		assertTrue(messageBundle.getErrors().contains(RejectDeliverableValidator.GENERIC_ERROR));
	}

	@Test
	public void validateOwnership_NoWorkAssetAssociationFound_Errors() {
		when(assetManagementService.findAssetAssociationsByWorkAndAsset(anyLong(), anyLong())).thenReturn(null);

		rejectDeliverableValidator.validate(workResponse, REJECTION_REASON, ASSET_ID, CURRENT_USER_COMPANY_ID, messageBundle);

		assertTrue(messageBundle.getErrors().contains(RejectDeliverableValidator.GENERIC_ERROR));
	}

	@Test
	public void validateCompanyStatus_CompanyIsNotSuspended_NoErrors() {
		rejectDeliverableValidator.validate(workResponse, REJECTION_REASON, ASSET_ID, CURRENT_USER_COMPANY_ID, messageBundle);

		assertFalse(messageBundle.hasErrors());
	}

	@Test
	public void validateCompanyStatus_CompanyIsSuspended_Errors() {
		when(company.isSuspended()).thenReturn(true);

		rejectDeliverableValidator.validate(workResponse, REJECTION_REASON, ASSET_ID, CURRENT_USER_COMPANY_ID, messageBundle);

		assertTrue(messageBundle.getErrors().contains(RejectDeliverableValidator.COMPANY_SUSPENDED_ERROR));
	}

	@Test
	public void validateActiveStatusOfDeliverable_DeliverableIsActive_NoErrors() {
		rejectDeliverableValidator.validate(workResponse, REJECTION_REASON, ASSET_ID, CURRENT_USER_COMPANY_ID, messageBundle);

		assertFalse(messageBundle.hasErrors());
	}

	@Test
	public void validateActiveStatusOfDeliverable_DeliverableIsNotActive_Errors() {
		when(workAssetAssociation.getRejectedOn()).thenReturn(rejectedOnDate);

		rejectDeliverableValidator.validate(workResponse, REJECTION_REASON, ASSET_ID, CURRENT_USER_COMPANY_ID, messageBundle);

		assertTrue(messageBundle.getErrors().contains(RejectDeliverableValidator.DUPLICATE_REJECTION_ERROR));
	}

	@Test
	public void validateRejectReason_RejectionReasonProvided_NoErrors() {
		rejectDeliverableValidator.validate(workResponse, REJECTION_REASON, ASSET_ID, CURRENT_USER_COMPANY_ID, messageBundle);

		assertFalse(messageBundle.hasErrors());
	}

	@Test
	public void validateRejectReason_BlankRejectionReasonProvided_Errors() {
		rejectDeliverableValidator.validate(workResponse, BLANK_REJECTION_REASON, ASSET_ID, CURRENT_USER_COMPANY_ID, messageBundle);

		assertTrue(messageBundle.getErrors().contains(RejectDeliverableValidator.EMPTY_REJECTION_REASON_ERROR));
	}

	@Test
	public void validateRejectReason_NullRejectionReasonProvided_Errors() {
		rejectDeliverableValidator.validate(workResponse, null, ASSET_ID, CURRENT_USER_COMPANY_ID, messageBundle);

		assertTrue(messageBundle.getErrors().contains(RejectDeliverableValidator.EMPTY_REJECTION_REASON_ERROR));
	}

	@Test
	public void validateRejectReason_LongRejectionReasonProvided_Errors() {
		rejectDeliverableValidator.validate(workResponse, REALLY_LONG_REJECTION_REASON, ASSET_ID, CURRENT_USER_COMPANY_ID, messageBundle);

		assertTrue(messageBundle.getErrors().contains(RejectDeliverableValidator.CHARACTER_LIMIT_EXCEEDED_ERROR));
	}

	@Test
	public void validateWorkStatus_ActiveWorkStatus_NoErrors() {
		rejectDeliverableValidator.validate(workResponse, REJECTION_REASON, ASSET_ID, CURRENT_USER_COMPANY_ID, messageBundle);

		assertFalse(messageBundle.hasErrors());
	}

	@Test
	public void validateWorkStatus_InvalidWorkStatus_Errors() {
		when(workStatus.getCode()).thenReturn(WorkStatusType.DRAFT);

		rejectDeliverableValidator.validate(workResponse, REJECTION_REASON, ASSET_ID, CURRENT_USER_COMPANY_ID, messageBundle);

		assertTrue(messageBundle.getErrors().contains(RejectDeliverableValidator.GENERIC_ERROR));
	}
}
