package com.workmarket.api.v2.employer.assignments.controllers;


import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.assignments.models.AssignmentSearchResponseDTO;
import com.workmarket.api.v2.employer.assignments.services.AssignmentCustomFieldGroupsService;
import com.workmarket.api.v2.model.CustomFieldGroupDTO;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.search.model.WorkSearchResponse;
import com.workmarket.web.forms.work.WorkDashboardForm;
import com.workmarket.web.helpers.WorkDashboardHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class AssignmentsSearchControllerTest extends BaseApiControllerTest {

    private static final String ENDPOINT ="/v2/assignments";
    private static final TypeReference<ApiV2Response<AssignmentSearchResponseDTO>> V2_RESPONSE_TYPE = new TypeReference<ApiV2Response<AssignmentSearchResponseDTO>>(){};

    @Mock
    private WorkDashboardHelper workDashboardHelper;

    @Mock
    private AssignmentCustomFieldGroupsService assignmentCustomFieldGroupsService;

    @InjectMocks
    private AssignmentSearchController controller = new AssignmentSearchController();

    @Before
    public void setUp() throws Exception {
        super.setup(controller);
    }

    @Test
    public void testListAssignments() throws Exception {
        final WorkSearchResponse searchResponse = new WorkSearchResponse();
        final ArgumentCaptor<Model> modelCaptor = ArgumentCaptor.forClass(Model.class);
        final ArgumentCaptor<WorkDashboardForm> formCaptor = ArgumentCaptor.forClass(WorkDashboardForm.class);
        final ArgumentCaptor<ExtendedUserDetails> userDetailsCaptor = ArgumentCaptor.forClass(ExtendedUserDetails.class);

        searchResponse.setResultsCount(0);
        searchResponse.setShowBulkOps(true);
        searchResponse.setTotalResultsCount(0);
        searchResponse.setData(Collections.EMPTY_LIST);
        searchResponse.setResultIds(Collections.EMPTY_LIST);
        searchResponse.setStatusCounts(Collections.EMPTY_MAP);
        searchResponse.setSubStatusCounts(Collections.EMPTY_MAP);
        searchResponse.setAssignedResources(Collections.EMPTY_MAP);

        when(workDashboardHelper.searchWork(formCaptor.capture(), userDetailsCaptor.capture()))
            .thenReturn(searchResponse);

        doCallRealMethod()
            .when(workDashboardHelper)
            .getDashboard(modelCaptor.capture(), any(WorkDashboardForm.class), any(ExtendedUserDetails.class));

        sendRequest("/list", Collections.EMPTY_MAP)
            .andExpect(status().isOk())
            .andReturn();

        verify(assignmentCustomFieldGroupsService, never()).get(anyString());
        verify(workDashboardHelper).searchWork(formCaptor.capture(), userDetailsCaptor.capture());
        verify(workDashboardHelper).getDashboard(modelCaptor.capture(), any(WorkDashboardForm.class), any(ExtendedUserDetails.class));

        final Model model = modelCaptor.getValue();
        final Map<String, Object> response = (Map<String, Object>) model.asMap().get("response");

        assertEquals(0, response.get("resultCount"));
        assertEquals(0, response.get("results_count"));
        assertEquals(true, response.get("show_bulk_ops"));
        assertEquals(Collections.EMPTY_MAP, response.get("counts"));
        assertEquals(Collections.EMPTY_MAP, response.get("substatuses"));
        assertEquals(Collections.EMPTY_LIST, response.get("data"));
        assertEquals(Collections.EMPTY_LIST, response.get("result_ids"));
        assertEquals(Collections.EMPTY_MAP, response.get("assignedResources"));
    }

    @Test
    public void testSearchAssignments() throws Exception {
        final WorkSearchResponse searchResponse = new WorkSearchResponse();
        final ArgumentCaptor<WorkDashboardForm> formCaptor = ArgumentCaptor.forClass(WorkDashboardForm.class);
        final ArgumentCaptor<ExtendedUserDetails> userDetailsCaptor = ArgumentCaptor.forClass(ExtendedUserDetails.class);
        final Map<String, String> params = new ImmutableMap.Builder<String, String>()
            .put("pageSize", "5")
            .put("title", "Foo")
            .put("start", "1")
            .build();

        searchResponse.setResultsCount(0);
        searchResponse.setShowBulkOps(true);
        searchResponse.setTotalResultsCount(0);
        searchResponse.setData(Collections.EMPTY_LIST);
        searchResponse.setResultIds(Collections.EMPTY_LIST);
        searchResponse.setStatusCounts(Collections.EMPTY_MAP);
        searchResponse.setSubStatusCounts(Collections.EMPTY_MAP);
        searchResponse.setAssignedResources(Collections.EMPTY_MAP);

        when(workDashboardHelper.searchWork(formCaptor.capture(), userDetailsCaptor.capture()))
            .thenReturn(searchResponse);

        final MvcResult mvcResult = sendRequest("/search", params)
            .andExpect(status().isOk())
            .andReturn();

        final WorkDashboardForm form = formCaptor.getValue();
        final ApiV2Response<AssignmentSearchResponseDTO> apiResponse = expectApiV2Response(mvcResult, V2_RESPONSE_TYPE);

        assertEquals("Foo", form.getTitle());
        assertEquals(new Integer(1), form.getStart());
        assertEquals(new Integer(5), form.getPageSize());

        assertEquals(1, apiResponse.getResults().size());

        final AssignmentSearchResponseDTO response = apiResponse.getResults().get(0);

        assertEquals(new Integer(0), response.getResultsCount());
        assertEquals(new Integer(0), response.getTotalResultsCount());
        assertEquals(true, response.isShowBulkOps());
        assertEquals(Collections.EMPTY_MAP, response.getStatusCounts());
        assertEquals(Collections.EMPTY_MAP, response.getSubStatusCounts());
        assertEquals(Collections.EMPTY_LIST, response.getData());
        assertEquals(Collections.EMPTY_LIST, response.getResultIds());
        assertEquals(Collections.EMPTY_MAP, response.getAssignedResources());
    }

    @Test
    public void testListAssignmentsWithCustomFields() throws Exception {
        final Map<String, Object> assignment1 = createAssignmentData();
        final Map<String, Object> assignment2 = createAssignmentData();
        final WorkSearchResponse searchResponse = new WorkSearchResponse();
        final ArgumentCaptor<Model> modelCaptor = ArgumentCaptor.forClass(Model.class);
        final List<Map<String, Object>> data = ImmutableList.of(assignment1, assignment2);
        final Set<CustomFieldGroupDTO> customFields1 = ImmutableSet.of(createCustomFieldGroup(1, "foo"));
        final Set<CustomFieldGroupDTO> customFields2 = ImmutableSet.of(createCustomFieldGroup(2, "bar"));
        final ArgumentCaptor<WorkDashboardForm> formCaptor = ArgumentCaptor.forClass(WorkDashboardForm.class);
        final ArgumentCaptor<ExtendedUserDetails> userDetailsCaptor = ArgumentCaptor.forClass(ExtendedUserDetails.class);

        final String assignmentId1 = String.valueOf(assignment1.get("id"));
        final String assignmentId2 = String.valueOf(assignment2.get("id"));
        final Map<String, String> params = new ImmutableMap.Builder<String, String>()
            .put("includeCustomFields", "true")
            .put("title", "Banana")
            .build();

        searchResponse.setData(data);
        searchResponse.setResultsCount(2);
        searchResponse.setShowBulkOps(true);
        searchResponse.setTotalResultsCount(2);
        searchResponse.setResultIds(Collections.EMPTY_LIST);
        searchResponse.setStatusCounts(Collections.EMPTY_MAP);
        searchResponse.setSubStatusCounts(Collections.EMPTY_MAP);
        searchResponse.setAssignedResources(Collections.EMPTY_MAP);

        when(workDashboardHelper.searchWork(formCaptor.capture(), userDetailsCaptor.capture()))
            .thenReturn(searchResponse);

        doCallRealMethod()
            .when(workDashboardHelper)
            .getDashboard(modelCaptor.capture(), any(WorkDashboardForm.class), any(ExtendedUserDetails.class));

        when(assignmentCustomFieldGroupsService.get(assignmentId1))
            .thenReturn(customFields1);

        when(assignmentCustomFieldGroupsService.get(assignmentId2))
            .thenReturn(customFields2);

        sendRequest("/list", params)
            .andExpect(status().isOk())
            .andReturn();

        verify(assignmentCustomFieldGroupsService).get(assignmentId1);
        verify(assignmentCustomFieldGroupsService).get(assignmentId2);
        verify(workDashboardHelper).getDashboard(modelCaptor.capture(), any(WorkDashboardForm.class), any(ExtendedUserDetails.class));

        final Model model = modelCaptor.getValue();
        final Map<String, Object> response = (Map<String, Object>) model.asMap().get("response");

        assertSame(data, response.get("data"));
        assertEquals(2, response.get("resultCount"));
        assertEquals(2, response.get("results_count"));
        assertEquals(true, response.get("show_bulk_ops"));

        assertTrue(data.get(0).containsKey("custom_field_groups"));
        assertTrue(data.get(1).containsKey("custom_field_groups"));

        assertEquals(customFields1, data.get(0).get("custom_field_groups"));
        assertEquals(customFields2, data.get(1).get("custom_field_groups"));
    }

    @Test
    public void testSearchAssignmentsWithCustomFields() throws Exception {
        final Map<String, Object> assignment1 = createAssignmentData();
        final Map<String, Object> assignment2 = createAssignmentData();
        final WorkSearchResponse searchResponse = new WorkSearchResponse();
        final List<Map<String, Object>> data = ImmutableList.of(assignment1, assignment2);
        final Set<CustomFieldGroupDTO> customFields1 = ImmutableSet.of(createCustomFieldGroup(1, "foo"));
        final Set<CustomFieldGroupDTO> customFields2 = ImmutableSet.of(createCustomFieldGroup(2, "bar"));
        final ArgumentCaptor<WorkDashboardForm> formCaptor = ArgumentCaptor.forClass(WorkDashboardForm.class);
        final ArgumentCaptor<ExtendedUserDetails> userDetailsCaptor = ArgumentCaptor.forClass(ExtendedUserDetails.class);

        final String assignmentId1 = String.valueOf(assignment1.get("id"));
        final String assignmentId2 = String.valueOf(assignment2.get("id"));
        final Map<String, String> params = new ImmutableMap.Builder<String, String>()
            .put("includeCustomFields", "true")
            .put("title", "Tapioca")
            .build();

        searchResponse.setData(data);
        searchResponse.setResultsCount(2);
        searchResponse.setShowBulkOps(true);
        searchResponse.setTotalResultsCount(2);
        searchResponse.setResultIds(Collections.EMPTY_LIST);
        searchResponse.setStatusCounts(Collections.EMPTY_MAP);
        searchResponse.setSubStatusCounts(Collections.EMPTY_MAP);
        searchResponse.setAssignedResources(Collections.EMPTY_MAP);

        when(workDashboardHelper.searchWork(formCaptor.capture(), userDetailsCaptor.capture()))
            .thenReturn(searchResponse);

        when(assignmentCustomFieldGroupsService.get(assignmentId1))
            .thenReturn(customFields1);

        when(assignmentCustomFieldGroupsService.get(assignmentId2))
            .thenReturn(customFields2);

        final MvcResult mvcResult = sendRequest("/search", params)
            .andExpect(status().isOk())
            .andReturn();

        verify(assignmentCustomFieldGroupsService).get(assignmentId1);
        verify(assignmentCustomFieldGroupsService).get(assignmentId2);
        verify(workDashboardHelper).searchWork(formCaptor.capture(), userDetailsCaptor.capture());

        final ApiV2Response<AssignmentSearchResponseDTO> apiResponse = expectApiV2Response(mvcResult, V2_RESPONSE_TYPE);

        assertEquals(1, apiResponse.getResults().size());

        final AssignmentSearchResponseDTO response = apiResponse.getResults().get(0);
        final List<Map<String, Object>> dataResult = response.getData();

        assertEquals(2, dataResult.size());
        assertEquals(true, response.isShowBulkOps());
        assertEquals(new Integer(2), response.getResultsCount());
        assertEquals(new Integer(2), response.getTotalResultsCount());

        assertTrue(dataResult.get(0).containsKey("custom_field_groups"));
        assertTrue(dataResult.get(1).containsKey("custom_field_groups"));
    }

    private Map<String, Object> createAssignmentData() {
        return new HashMap<String, Object>() {{
            put("id", UUID.randomUUID().toString());
        }};
    }

    private CustomFieldGroupDTO createCustomFieldGroup(final Integer id, final String name) {
        return new CustomFieldGroupDTO.Builder()
            .setName(name)
            .setId(id)
            .build();
    }

    private ResultActions sendRequest(final String path, final Map<String, String> params) throws Exception {
        final MockHttpServletRequestBuilder builder = get(ENDPOINT + "/" + path);
        final MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();

        multiValueMap.setAll(params);
        builder.params(multiValueMap);

        return mockMvc.perform(builder);
    }
}
