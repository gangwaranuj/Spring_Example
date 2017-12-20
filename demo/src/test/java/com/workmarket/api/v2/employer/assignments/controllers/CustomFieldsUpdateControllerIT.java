package com.workmarket.api.v2.employer.assignments.controllers;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.internal.service.ApiService;
import com.workmarket.api.v2.model.CustomFieldDTO;
import com.workmarket.api.v2.employer.assignments.services.WorkCustomFieldsDTOFormatterService;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.CustomFieldService;
import com.workmarket.service.business.dto.WorkCustomFieldDTO;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.View;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class CustomFieldsUpdateControllerIT extends BaseApiControllerTest {
	private static final String FIRST_NAME = "John";
	private static final String LAST_NAME = "Smith";
	private static final String EMAIL = "john@smith.com";
	private static final String workId = "123456";

	private ObjectMapper mapper = new ObjectMapper();

	@Mock private View mockView;
	@Mock WorkService workService;
	@Mock private WorkCustomFieldsDTOFormatterService workCustomFieldsDTOFormatterService;
	@Mock private CustomFieldService customFieldService;
	@Mock private MessageBundleHelper messageBundleHelper;

	@InjectMocks
	private CustomFieldsUpdateController controller;

	@Before
	public void setup() throws Exception{
		super.setup(controller);

		when(workService.isUserActiveResourceForWork(anyLong(),anyLong())).thenReturn(true);
		when(workService.isAuthorizedToAdminister(anyLong(), anyLong())).thenReturn(true);
	}

	@Test
	public void customFieldsUpdateByIndex_statusOk() throws Exception {
		when(workService.findWorkByWorkNumber(anyString())).thenReturn(new AbstractWork(){{setWorkNumber(workId);}});
		doAnswer(new Answer() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
			  invocation.getMock();
				return null;
			}}).when(customFieldService).replaceCustomFieldGroupForWorkByPosition(anyLong(),anyLong(),anyListOf(WorkCustomFieldDTO.class),anyInt());

		final CustomFieldDTO dto1 = new CustomFieldDTO.Builder().setId(1l).setName("Field 1").build();
		final CustomFieldDTO dto2 = new CustomFieldDTO.Builder().setId(2l).setName("Field 2").build();

		final List<CustomFieldDTO> customFieldDTOs = Lists.newArrayList(dto1, dto2);

		Map<String,Object> request = new HashMap();
		request.put("id",123);
		request.put("fields",customFieldDTOs);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/v2/assignments/" + workId + "/custom_fields/0")
						.content(mapper.writeValueAsBytes(request))
						.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk());

		verify(customFieldService,times(1)).replaceCustomFieldGroupForWorkByPosition(anyLong(),anyLong(),anyListOf(WorkCustomFieldDTO.class),anyInt());
	}

	@Test
	public void customFieldsUpdateByPosiitonalIndex_statusOk() throws Exception {
		when(workService.findWorkByWorkNumber(anyString())).thenReturn(new AbstractWork(){{setWorkNumber(workId);}});

		doAnswer(new Answer() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				invocation.getMock();
				return null;
			}}).when(customFieldService).replaceCustomFieldGroupForWorkByPosition(anyLong(),anyLong(),anyListOf(WorkCustomFieldDTO.class),anyInt());


		final CustomFieldDTO dto1 = new CustomFieldDTO.Builder().setId(1l).setName("Field 1").build();
		final CustomFieldDTO dto2 = new CustomFieldDTO.Builder().setId(2l).setName("Field 2").build();

		final List<CustomFieldDTO> customFieldDTOs = Lists.newArrayList(dto1, dto2);


		Map<String, Object> group = ImmutableMap.of(
				"id", 123,
				"fields", customFieldDTOs
		);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/v2/assignments/" + workId + "/custom_fields/0")
						.content(mapper.writeValueAsBytes(group))
						.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk());
	}
}
