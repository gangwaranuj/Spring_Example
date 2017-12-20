package com.workmarket.web.validators;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VisibilityType;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UpdateDocumentVisibilityValidatorTest {

	// Services
	@Mock AssetManagementService assetManagementService;
	@Mock MessageBundleHelper messageHelper;
	@Mock AuthenticationService authenticationService;
	@Mock TWorkFacadeService tWorkFacadeService;
	@Mock WorkService workService;

	@InjectMocks UpdateDocumentVisibilityValidator updateDocumentVisibilityValidator = spy(new UpdateDocumentVisibilityValidator());

	// Method args
	private String workNumber = "123";
	private long assetId;
	private String visibilityCode = VisibilityType.PUBLIC;
	@Mock MessageBundle messageBundle;

	// Local vars
	@Mock WorkResponse workResponse;
	@Mock WorkRequest workRequest;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS) User currentUser;
	@Mock Asset asset;
	private Long workId, companyId, userId;

	@Before
	public void setUp() throws WorkActionException {
		workId = companyId = userId = assetId = 1L;
		when(authenticationService.getCurrentUser()).thenReturn(currentUser);
		when(currentUser.getCompany().getId()).thenReturn(companyId);
		when(currentUser.getId()).thenReturn(userId);
		when(workService.findWorkId(workNumber)).thenReturn(workId);
		when(assetManagementService.findAssetById(assetId)).thenReturn(asset);
		doReturn(workRequest).when(updateDocumentVisibilityValidator).buildWorkRequest(currentUser, workNumber);
		when(updateDocumentVisibilityValidator.buildWorkRequest(currentUser, workNumber)).thenReturn(workRequest);
		when(tWorkFacadeService.findWork(workRequest)).thenReturn(workResponse);
		when(workResponse.getAuthorizationContexts()).thenReturn(ImmutableSet.of(AuthorizationContext.ADMIN));
	}

	@Test
	public void validate_noErrors() throws Exception {
		updateDocumentVisibilityValidator.validate(workNumber, assetId, visibilityCode, messageBundle);
		verify(messageBundle, never()).addError(anyString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void validate_nullMessageBundle_throwAssertionError() throws Exception {
		updateDocumentVisibilityValidator.validate(workNumber, assetId, visibilityCode, null);
	}

	@Test
	public void validate_nullWorkNumber_addError() throws Exception {
		updateDocumentVisibilityValidator.validate(null, assetId, visibilityCode, messageBundle);
		verify(messageBundle, times(1)).addError("assignment.documents.toggle_visibility.missing_work");
	}

	@Test
	public void validate_invalidWorkNumber_addError() throws Exception {
		when(workService.findWorkId(workNumber)).thenReturn(null);
		updateDocumentVisibilityValidator.validate(workNumber, assetId, visibilityCode, messageBundle);
		verify(messageHelper, times(1)).addError(messageBundle, "assignment.documents.toggle_visibility.invalid_work", workNumber);
	}

	@Test
	public void validate_nullAssetId_addError() throws Exception {
		updateDocumentVisibilityValidator.validate(workNumber, null, visibilityCode, messageBundle);
		verify(messageBundle, times(1)).addError("assignment.documents.toggle_visibility.missing_asset");
	}

	@Test
	public void validate_invalidAssetId_addError() throws Exception {
		when(assetManagementService.findAssetById(assetId)).thenReturn(null);
		updateDocumentVisibilityValidator.validate(workNumber, assetId, visibilityCode, messageBundle);
		verify(messageHelper, times(1)).addError(messageBundle, "assignment.documents.toggle_visibility.invalid_asset", assetId);
	}

	@Test
	public void validate_blankVisibilityType_addError() throws Exception {
		updateDocumentVisibilityValidator.validate(workNumber, assetId, "", messageBundle);
		verify(messageBundle, times(1)).addError("assignment.documents.toggle_visibility.missing_code");
	}

	@Test
	public void validate_whitespaceVisibilityType_addError() throws Exception {
		updateDocumentVisibilityValidator.validate(workNumber, assetId, "\t    \t", messageBundle);
		verify(messageBundle, times(1)).addError("assignment.documents.toggle_visibility.missing_code");
	}

	@Test
	public void validate_nullVisibilityType_addError() throws Exception {
		updateDocumentVisibilityValidator.validate(workNumber, assetId, null, messageBundle);
		verify(messageBundle, times(1)).addError("assignment.documents.toggle_visibility.missing_code");
	}

	@Test
	public void validate_invalidVisibilityType_addError() throws Exception {
		String invalidCode = "FARTS";
		updateDocumentVisibilityValidator.validate(workNumber, assetId, invalidCode, messageBundle);
		verify(messageHelper, times(1)).addError(messageBundle, "assignment.documents.toggle_visibility.invalid_code", invalidCode);
	}

	@Test
	public void validate_findWorkServiceThrowsException_addError() throws Exception {
		when(tWorkFacadeService.findWork(workRequest)).thenThrow(WorkActionException.class);
		updateDocumentVisibilityValidator.validate(workNumber, assetId, visibilityCode, messageBundle);
		verify(messageBundle, times(1)).setErrors(ImmutableList.of(UpdateDocumentVisibilityValidator.GENERIC_ERROR));
	}

	@Test
	public void validate_wrongAuthorizationContext_addError() throws Exception {
		when(workResponse.getAuthorizationContexts()).thenReturn(ImmutableSet.of(AuthorizationContext.BUYER));
		updateDocumentVisibilityValidator.validate(workNumber, assetId, visibilityCode, messageBundle);
		verify(messageBundle, times(1)).setErrors(ImmutableList.of(UpdateDocumentVisibilityValidator.GENERIC_ERROR));
	}
}
