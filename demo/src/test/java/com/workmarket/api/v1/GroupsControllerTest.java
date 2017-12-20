package com.workmarket.api.v1;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.security.RequestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class GroupsControllerTest extends BaseApiControllerTest {

	private static final TypeReference<ApiV1Response<ApiV1ResponseStatus>> apiV1ResponseStatusType = new TypeReference<ApiV1Response<ApiV1ResponseStatus>>() {};

	@Mock private UserGroupService userGroupService;
	@InjectMocks private GroupsController controller = new GroupsController();
	
	@Before
	public void setup() throws Exception {
		super.setup(controller);
		when(userGroupService.getRequestContext(anyLong())).thenReturn(ImmutableList.of(RequestContext.ADMIN));
	}

	@Test
	public void test_addWorkerToGroup_succeeds() throws Exception {
		String workerNumber = "123456890";
		Long groupId = 1L;
		
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.post("/v1/employer/groups/" + groupId + "/add_workers")
			.header("accept", MediaType.APPLICATION_JSON)
			.content(ImmutableList.of(workerNumber).toString())
			.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

		ApiV1Response<ApiV1ResponseStatus> apiResponse = expectApiV1Response(result, apiV1ResponseStatusType);
		expectStatusCode(HttpStatus.OK.value(), apiResponse.getMeta());
		verify(userGroupService).addUsersToGroupByUserNumberAsync(ImmutableList.of(workerNumber), groupId, authenticationService.getCurrentUserId());
	}

	@Test
	public void test_addWorkerToGroup_notOwner_fails() throws Exception {
		String workerNumber = "123456890";
		Long groupId = 1L;
		
		when(userGroupService.getRequestContext(anyLong())).thenReturn(ImmutableList.of(RequestContext.ADMIN_OTHER_COMPANY));

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.post("/v1/employer/groups/" + groupId + "/add_workers")
			.header("accept", MediaType.APPLICATION_JSON)
			.content(ImmutableList.of(workerNumber).toString())
			.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

		ApiV1Response<ApiV1ResponseStatus> apiResponse = expectApiV1Response(result, apiV1ResponseStatusType);
		expectStatusCode(HttpStatus.FORBIDDEN.value(), apiResponse.getMeta());
	}
	
	@Test
	public void test_removeWorkersFromGroup_succeeds() throws Exception {
		String workerNumber = "123456890";
		Long workerId = 123L;
		Long groupId = 1L;

		when(userService.findAllUserIdsByUserNumbers(anyList())).thenReturn(ImmutableSet.of(workerId));

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.post("/v1/employer/groups/" + groupId + "/remove_workers")
			.header("accept", MediaType.APPLICATION_JSON)
			.content(ImmutableList.of(workerNumber).toString())
			.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

		ApiV1Response<ApiV1ResponseStatus> apiResponse = expectApiV1Response(result, apiV1ResponseStatusType);
		expectStatusCode(HttpStatus.OK.value(), apiResponse.getMeta());
		verify(userGroupService).removeAssociations(groupId, new Long[]{workerId}, authenticationService.getCurrentUserCompanyId());
	}

	@Test
	public void test_removeWorkersFromGroup_notOwned_fails() throws Exception {
		String workerNumber = "123456890";
		Long workerId = 123L;
		Long groupId = 1L;

		when(userService.findAllUserIdsByUserNumbers(anyList())).thenReturn(ImmutableSet.of(workerId));
		when(userGroupService.getRequestContext(anyLong())).thenReturn(ImmutableList.of(RequestContext.ADMIN_OTHER_COMPANY));

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.post("/v1/employer/groups/" + groupId + "/remove_workers")
			.header("accept", MediaType.APPLICATION_JSON)
			.content(ImmutableList.of(workerNumber).toString())
			.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

		ApiV1Response<ApiV1ResponseStatus> apiResponse = expectApiV1Response(result, apiV1ResponseStatusType);
		expectStatusCode(HttpStatus.FORBIDDEN.value(), apiResponse.getMeta());
	}
}