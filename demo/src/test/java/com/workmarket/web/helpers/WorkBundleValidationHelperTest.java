package com.workmarket.web.helpers;

import com.google.api.client.util.Lists;
import com.google.common.collect.ImmutableList;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.dto.WorkBundleDTO;
import com.workmarket.domains.work.service.route.WorkBundleRouting;
import com.workmarket.service.business.wrapper.ValidateWorkResponse;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.web.models.MessageBundle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkBundleValidationHelperTest {

	@Mock private MessageBundleHelper messageHelper;
	@Mock private WorkService workService;
	@Mock private WorkBundleService workBundleService;
	@Mock private WorkBundleRouting workBundleRouting;
	@Mock private WorkSearchService workSearchService;
	@InjectMocks WorkBundleValidationHelper workBundleValidationHelper;

	WorkBundleDTO workBundleDTO;
	ExtendedUserDetails user;
	WorkBundle bundle;
	AjaxResponseBuilder response;
	MessageBundle messages;

	List<String> workNumbers = ImmutableList.of("111", "222");
	List<String> tooManyWorkNumbers;
	List<String> maximumWorkNumbers;

	List<ValidateWorkResponse> failList = ImmutableList.of(ValidateWorkResponse.fail(), ValidateWorkResponse.fail());
	List<ValidateWorkResponse> successList = ImmutableList.of(ValidateWorkResponse.success(), ValidateWorkResponse.success());
	List<ValidateWorkResponse> partialList = ImmutableList.of(ValidateWorkResponse.success(), ValidateWorkResponse.fail());

	@Before
	public void setup() {
		workBundleDTO = mock(WorkBundleDTO.class);
		user = mock(ExtendedUserDetails.class);
		bundle = mock(WorkBundle.class);
		response = mock(AjaxResponseBuilder.class);
		messages = mock(MessageBundle.class);

		when(messageHelper.newBundle()).thenReturn(messages);
		when(user.getId()).thenReturn(1L);
		when(bundle.getTitle()).thenReturn("MyTitle");

		maximumWorkNumbers = Lists.newArrayList();
		tooManyWorkNumbers = Lists.newArrayList();
		for (int workNumber = 1; workNumber <= 250; workNumber++ ) {
			maximumWorkNumbers.add("" + workNumber);
			tooManyWorkNumbers.add("" + workNumber);
		}
		tooManyWorkNumbers.add("251");
	}

	@Test
	public void validateWorkBundle_CreateNoWork_Fail() {
		when(workBundleDTO.getId()).thenReturn(null);
		when(workBundleDTO.getWorkNumbers()).thenReturn(null);
		when(response.setSuccessful(false)).thenReturn(response);

		WorkBundleValidationResult workBundleValidationResult = workBundleValidationHelper.validateWorkBundle(workBundleDTO, user, response);

		verify(messageHelper).addMessage(response, "assignment_bundle.create.fail.no_work");
		assertEquals(WorkBundleValidationResult.STATUS.FAILURE, workBundleValidationResult.getStatus());
	}

	@Test
	public void validateWorkBundle_AddNoWork_Fail() {
		when(workBundleDTO.getId()).thenReturn(1L);
		when(workBundleDTO.getWorkNumbers()).thenReturn(null);
		when(response.setSuccessful(false)).thenReturn(response);

		WorkBundleValidationResult workBundleValidationResult = workBundleValidationHelper.validateWorkBundle(workBundleDTO, user, response);

		verify(messageHelper).addMessage(response, "assignment_bundle.add.fail.no_work");
		assertEquals(WorkBundleValidationResult.STATUS.FAILURE, workBundleValidationResult.getStatus());
	}

	@Test
	public void validateWorkBundle_AddTooManyWork_Fail() {
		when(workBundleDTO.getId()).thenReturn(1L);
		when(workBundleDTO.getWorkNumbers()).thenReturn(workNumbers);
		when(workBundleDTO.getWorkCount()).thenReturn(249L);
		when(response.setSuccessful(false)).thenReturn(response);

		WorkBundleValidationResult workBundleValidationResult = workBundleValidationHelper.validateWorkBundle(workBundleDTO, user, response);

		verify(messageHelper).addMessage(response, "assignment_bundle.add.fail.too_many", 2, 249L, 250);
		assertEquals(WorkBundleValidationResult.STATUS.FAILURE, workBundleValidationResult.getStatus());
	}

	@Test
	public void validateWorkBundle_CreateTooManyWork_Fail() {
		when(workBundleDTO.getId()).thenReturn(null);
		when(workBundleDTO.getWorkNumbers()).thenReturn(tooManyWorkNumbers);
		when(response.setSuccessful(false)).thenReturn(response);

		WorkBundleValidationResult workBundleValidationResult = workBundleValidationHelper.validateWorkBundle(workBundleDTO, user, response);

		verify(messageHelper).addMessage(response, "assignment_bundle.create.fail.too_many", 251, 250);
		assertEquals(WorkBundleValidationResult.STATUS.FAILURE, workBundleValidationResult.getStatus());
	}

	@Test
	public void validateWorkBundle_CreateWorkAllFail_Fail() {
		when(workBundleDTO.getId()).thenReturn(null);
		when(response.setSuccessful(false)).thenReturn(response);

		when(workBundleDTO.getWorkNumbers()).thenReturn(workNumbers);

		when(workBundleService.validateAllBundledWorkForAdd(workNumbers, null)).thenReturn(failList);

		WorkBundleValidationResult workBundleValidationResult = workBundleValidationHelper.validateWorkBundle(workBundleDTO, user, response);

		verify(messageHelper).addMessage(response, "assignment_bundle.create.fail.no_valid_work");
		assertEquals(WorkBundleValidationResult.STATUS.FAILURE, workBundleValidationResult.getStatus());
	}

	@Test
	public void validateWorkBundle_AddWorkAllFail_Fail() {
		when(workBundleDTO.getId()).thenReturn(2L);
		when(response.setSuccessful(false)).thenReturn(response);

		when(workBundleDTO.getWorkNumbers()).thenReturn(workNumbers);

		when(workBundleService.validateAllBundledWorkForAdd(workNumbers, null)).thenReturn(failList);


		WorkBundleValidationResult workBundleValidationResult = workBundleValidationHelper.validateWorkBundle(workBundleDTO, user, response);

		verify(messageHelper).addMessage(response, "assignment_bundle.add.fail.no_valid_work");
		assertEquals(WorkBundleValidationResult.STATUS.FAILURE, workBundleValidationResult.getStatus());
	}

	@Test
	public void validateWorkBundle_CreateNotDraft_Fail() {
		when(workBundleDTO.getId()).thenReturn(null);
		when(response.setSuccessful(false)).thenReturn(response);

		when(workBundleDTO.getWorkNumbers()).thenReturn(workNumbers);

		when(workBundleService.validateAllBundledWorkForAdd(workNumbers, null)).thenReturn(successList);

		when(workBundleService.saveOrUpdateWorkBundle(1L, workBundleDTO)).thenReturn(bundle);

		when(bundle.getWorkStatusType()).thenReturn(WorkStatusType.newWorkStatusType(WorkStatusType.ACTIVE));

		WorkBundleValidationResult workBundleValidationResult = workBundleValidationHelper.validateWorkBundle(workBundleDTO, user, response);

		verify(messageHelper).addMessage(response, "assignment_bundle.add.fail.bad_state");
		assertEquals(WorkBundleValidationResult.STATUS.FAILURE, workBundleValidationResult.getStatus());
	}

	@Test
	public void validateWorkBundle_AddNotDraft_Fail() {
		when(workBundleDTO.getId()).thenReturn(2L);
		when(response.setSuccessful(false)).thenReturn(response);

		when(workBundleDTO.getWorkNumbers()).thenReturn(workNumbers);

		when(workBundleService.validateAllBundledWorkForAdd(workNumbers, 2L)).thenReturn(successList);

		when(workBundleService.findById(2L)).thenReturn(bundle);

		when(bundle.getWorkStatusType()).thenReturn(WorkStatusType.newWorkStatusType(WorkStatusType.ACTIVE));

		WorkBundleValidationResult workBundleValidationResult = workBundleValidationHelper.validateWorkBundle(workBundleDTO, user, response);

		verify(messageHelper).addMessage(response, "assignment_bundle.add.fail.bad_state");
		assertEquals(WorkBundleValidationResult.STATUS.FAILURE, workBundleValidationResult.getStatus());
	}

	@Test
	public void validateWorkBundle_Create_Fail() {
		when(workBundleDTO.getId()).thenReturn(null);
		when(response.setSuccessful(false)).thenReturn(response);

		when(workBundleDTO.getWorkNumbers()).thenReturn(workNumbers);

		when(workBundleService.validateAllBundledWorkForAdd(workNumbers, null)).thenReturn(successList);

		when(workBundleService.saveOrUpdateWorkBundle(1L, workBundleDTO)).thenReturn(null);

		WorkBundleValidationResult workBundleValidationResult = workBundleValidationHelper.validateWorkBundle(workBundleDTO, user, response);

		verify(messageHelper).addMessage(response, "assignment_bundle.create.fail.create_failure");
		assertEquals(WorkBundleValidationResult.STATUS.FAILURE, workBundleValidationResult.getStatus());
	}

	@Test
	public void validateWorkBundle_AddNotFound_Fail() {
		when(workBundleDTO.getId()).thenReturn(2L);
		when(response.setSuccessful(false)).thenReturn(response);

		when(workBundleDTO.getWorkNumbers()).thenReturn(workNumbers);

		when(workBundleService.validateAllBundledWorkForAdd(workNumbers, 2L)).thenReturn(successList);

		when(workBundleService.findById(2L)).thenReturn(null);

		WorkBundleValidationResult workBundleValidationResult = workBundleValidationHelper.validateWorkBundle(workBundleDTO, user, response);

		verify(messageHelper).addMessage(response, "assignment_bundle.add.fail.bundle_not_found");
		assertEquals(WorkBundleValidationResult.STATUS.FAILURE, workBundleValidationResult.getStatus());
	}

	@Test
	public void validateWorkBundle_Create_PartialSuccess() {
		when(workBundleDTO.getId()).thenReturn(null);
		when(response.setSuccessful(false)).thenReturn(response);

		when(workBundleDTO.getWorkNumbers()).thenReturn(workNumbers);

		when(workBundleService.validateAllBundledWorkForAdd(workNumbers, null)).thenReturn(partialList);

		when(workBundleService.saveOrUpdateWorkBundle(1L, workBundleDTO)).thenReturn(bundle);

		when(bundle.getWorkStatusType()).thenReturn(WorkStatusType.newWorkStatusType(WorkStatusType.DRAFT));

		when(workBundleService.addAllToBundleByWorkNumbers(bundle.getId(), workNumbers)).thenReturn(partialList);

		WorkBundleValidationResult workBundleValidationResult = workBundleValidationHelper.validateWorkBundle(workBundleDTO, user, response);

		verify(messageHelper).addMessage(response, "assignment_bundle.create.partial_success", "MyTitle", "" + 1);
		assertEquals(WorkBundleValidationResult.STATUS.PARTIAL_SUCCESS, workBundleValidationResult.getStatus());
	}

	@Test
	public void validateWorkBundle_Add_PartialSuccess() {
		when(workBundleDTO.getId()).thenReturn(2L);
		when(response.setSuccessful(false)).thenReturn(response);

		when(workBundleDTO.getWorkNumbers()).thenReturn(workNumbers);

		when(workBundleService.validateAllBundledWorkForAdd(workNumbers, 2L)).thenReturn(partialList);

		when(workBundleService.findById(2L)).thenReturn(bundle);

		when(bundle.getWorkStatusType()).thenReturn(WorkStatusType.newWorkStatusType(WorkStatusType.DRAFT));

		when(workBundleService.addAllToBundleByWorkNumbers(bundle.getId(), workNumbers)).thenReturn(partialList);

		WorkBundleValidationResult workBundleValidationResult = workBundleValidationHelper.validateWorkBundle(workBundleDTO, user, response);

		verify(messageHelper).addMessage(response, "assignment_bundle.add.partial_success", "MyTitle", "" + 1);
		assertEquals(WorkBundleValidationResult.STATUS.PARTIAL_SUCCESS, workBundleValidationResult.getStatus());
	}

	@Test
	public void validateWorkBundle_Create_Success() {
		when(workBundleDTO.getId()).thenReturn(null);
		when(response.setSuccessful(true)).thenReturn(response);

		when(workBundleDTO.getWorkNumbers()).thenReturn(workNumbers);

		when(workBundleService.validateAllBundledWorkForAdd(workNumbers, null)).thenReturn(successList);

		when(workBundleService.saveOrUpdateWorkBundle(1L, workBundleDTO)).thenReturn(bundle);

		when(bundle.getWorkStatusType()).thenReturn(WorkStatusType.newWorkStatusType(WorkStatusType.DRAFT));

		when(workBundleService.addAllToBundleByWorkNumbers(bundle.getId(), workNumbers)).thenReturn(successList);

		WorkBundleValidationResult workBundleValidationResult = workBundleValidationHelper.validateWorkBundle(workBundleDTO, user, response);

		verify(messageHelper).addMessage(response, "assignment_bundle.create.success", "MyTitle", "" + 2, "assignments");
		assertEquals(WorkBundleValidationResult.STATUS.SUCCESS, workBundleValidationResult.getStatus());
	}

	@Test
	public void validateWorkBundle_CreateMaxWork_Success() {
		when(workBundleDTO.getId()).thenReturn(null);
		when(response.setSuccessful(true)).thenReturn(response);
		when(workBundleDTO.getWorkNumbers()).thenReturn(maximumWorkNumbers);
		when(workBundleService.validateAllBundledWorkForAdd(maximumWorkNumbers, null)).thenReturn(successList);
		when(workBundleService.saveOrUpdateWorkBundle(1L, workBundleDTO)).thenReturn(bundle);
		when(bundle.getWorkStatusType()).thenReturn(WorkStatusType.newWorkStatusType(WorkStatusType.DRAFT));
		when(workBundleService.addAllToBundleByWorkNumbers(bundle.getId(), workNumbers)).thenReturn(successList);

		WorkBundleValidationResult workBundleValidationResult = workBundleValidationHelper.validateWorkBundle(workBundleDTO, user, response);

		verify(messageHelper).addMessage(response, "assignment_bundle.create.success", "MyTitle", "" + 2, "assignments");
		assertEquals(WorkBundleValidationResult.STATUS.SUCCESS, workBundleValidationResult.getStatus());
	}

	@Test
	public void validateWorkBundle_Add_Success() {
		when(workBundleDTO.getId()).thenReturn(2L);
		when(response.setSuccessful(true)).thenReturn(response);

		when(workBundleDTO.getWorkNumbers()).thenReturn(workNumbers);

		when(workBundleService.validateAllBundledWorkForAdd(workNumbers, 2L)).thenReturn(successList);

		when(workBundleService.findById(2L)).thenReturn(bundle);

		when(bundle.getWorkStatusType()).thenReturn(WorkStatusType.newWorkStatusType(WorkStatusType.DRAFT));

		when(workBundleService.addAllToBundleByWorkNumbers(bundle.getId(), workNumbers)).thenReturn(successList);

		WorkBundleValidationResult workBundleValidationResult = workBundleValidationHelper.validateWorkBundle(workBundleDTO, user, response);

		verify(messageHelper).addMessage(response, "assignment_bundle.add.success", "MyTitle", "" + 2);
		assertEquals(WorkBundleValidationResult.STATUS.SUCCESS, workBundleValidationResult.getStatus());
	}

	@Test
	public void validateWorkBundle_AddMaxWork_Success() {
		when(workBundleDTO.getId()).thenReturn(2L);
		when(response.setSuccessful(true)).thenReturn(response);
		when(workBundleDTO.getWorkNumbers()).thenReturn(workNumbers);
		when(workBundleDTO.getWorkCount()).thenReturn(248L);
		when(workBundleService.validateAllBundledWorkForAdd(workNumbers, 2L)).thenReturn(successList);
		when(workBundleService.findById(2L)).thenReturn(bundle);
		when(bundle.getWorkStatusType()).thenReturn(WorkStatusType.newWorkStatusType(WorkStatusType.DRAFT));
		when(workBundleService.addAllToBundleByWorkNumbers(bundle.getId(), workNumbers)).thenReturn(successList);

		WorkBundleValidationResult workBundleValidationResult = workBundleValidationHelper.validateWorkBundle(workBundleDTO, user, response);

		verify(messageHelper).addMessage(response, "assignment_bundle.add.success", "MyTitle", "" + 2);
		assertEquals(WorkBundleValidationResult.STATUS.SUCCESS, workBundleValidationResult.getStatus());
	}

	@Test
	public void validateWorkBundle_Add_Fail_Routing() {
		when(workBundleDTO.getId()).thenReturn(2L);
		when(response.setSuccessful(false)).thenReturn(response);

		when(workBundleDTO.getWorkNumbers()).thenReturn(workNumbers);

		when(workBundleRouting.isWorkBundlePendingRouting(2L)).thenReturn(true);

		WorkBundleValidationResult workBundleValidationResult = workBundleValidationHelper.validateWorkBundle(workBundleDTO, user, response);

		verify(messageHelper).addMessage(response, "assignment_bundle.add.fail.pending_routing", "assignments");
		assertEquals(WorkBundleValidationResult.STATUS.FAILURE, workBundleValidationResult.getStatus());
	}
}
