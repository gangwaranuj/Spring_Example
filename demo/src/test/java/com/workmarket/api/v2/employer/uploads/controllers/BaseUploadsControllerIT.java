package com.workmarket.api.v2.employer.uploads.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.workmarket.api.ApiBaseError;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.model.CustomFieldDTO;
import com.workmarket.api.v2.model.CustomFieldGroupDTO;
import com.workmarket.api.v2.employer.assignments.models.TemplateDTO;
import com.workmarket.api.v2.employer.assignments.services.AssignmentTemplateService;
import com.workmarket.api.v2.employer.uploads.models.*;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.helpers.WMCallable;
import com.workmarket.web.controllers.ControllerIT;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.concurrent.Callable;

import static com.jayway.awaitility.Awaitility.await;
import static com.natpryce.makeiteasy.MakeItEasy.*;
import static com.workmarket.api.v2.employer.assignments.controllers.support.AssignmentMaker.AssignmentDTO;
import static com.workmarket.api.v2.employer.assignments.controllers.support.AssignmentMaker.customFieldGroup;
import static com.workmarket.api.v2.employer.assignments.controllers.support.CustomFieldsMaker.*;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TemplateMaker.TemplateDTO;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TemplateMaker.assignment;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.mappingType;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BaseUploadsControllerIT extends ControllerIT {
	protected static final TypeReference<ApiV2Response<CsvDTO>> csvType = new TypeReference<ApiV2Response<CsvDTO>>() { };
	protected static final TypeReference<ApiV2Response<DataDTO>> dataType = new TypeReference<ApiV2Response<DataDTO>>() { };
	protected static final TypeReference<ApiV2Response<RowsDTO>> rowsType = new TypeReference<ApiV2Response<RowsDTO>>() { };
	protected static final TypeReference<ApiV2Response<PreviewsDTO>> previewsType = new TypeReference<ApiV2Response<PreviewsDTO>>() { };
	protected static final TypeReference<ApiV2Response<ErrorsDTO>> errorsType = new TypeReference<ApiV2Response<ErrorsDTO>>() { };
	protected static final TypeReference<ApiV2Response<AssignmentsDTO>> assignmentsType = new TypeReference<ApiV2Response<AssignmentsDTO>>() { };
	protected static final TypeReference<ApiV2Response<SettingsDTO>> settingsType = new TypeReference<ApiV2Response<SettingsDTO>>() { };
	protected static final TypeReference<ApiV2Response<StatusDTO>> statusType = new TypeReference<ApiV2Response<StatusDTO>>() { };

	protected static final String ENDPOINT = "/employer/v2/uploads";

	@Autowired AssignmentTemplateService assignmentTemplateService;

	protected MockMultipartFile csv;

	@Before
	public void setup() throws Exception {
		login();

		this.csv = mockCsv("file",
			"Title,Description\n",
			"awesome title,awesome description\n",
			"blarg,blurg\n"
		);
	}

	protected MockMultipartFile mockCsv(String paramName, String... rows) {
		StringBuilder content = new StringBuilder();

		for (String row : rows) {
			content.append(row);
		}

		return new MockMultipartFile(paramName, "file.csv", "text/csv", content.toString().getBytes());
	}

	protected String upload(MockMultipartFile csv) throws Exception {
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.fileUpload(ENDPOINT)
						.file(csv)
						.header("Authorization", "Bearer " + accessToken))
						.andExpect(status().isOk())
						.andReturn();
		return getFirstResult(mvcResult, csvType).getUuid();
	}

	protected MvcResult get(String url, String uuid) throws Exception {
		return mockMvc.perform(doGet(String.format("%s/%s/%s", ENDPOINT, uuid, url)))
						.andExpect(status().isOk())
						.andReturn();
	}

	protected MvcResult getFile(String url, String uuid) throws Exception {
		return mockMvc.perform(doGet(String.format("%s/%s/%s", ENDPOINT, uuid, url)))
						.andExpect(status().isFound())
						.andReturn();
	}

	protected MvcResult delete(String url, String uuid) throws Exception {
		return mockMvc.perform(doDelete(String.format("%s/%s/%s", ENDPOINT, uuid, url)))
						.andExpect(status().isNoContent())
						.andReturn();
	}

	protected MvcResult post(String url, String uuid) throws Exception {
		return mockMvc.perform(doPost(String.format("%s/%s/%s", ENDPOINT, uuid, url)))
						.andExpect(status().isAccepted())
						.andReturn();
	}

	protected MvcResult post(String url, String uuid, String json) throws Exception {
		return mockMvc.perform(doPost(String.format("%s/%s/%s", ENDPOINT, uuid, url)).content(json))
						.andExpect(status().isAccepted())
						.andReturn();
	}

	protected String uploadCSV(StringBuilder csvContent) throws Exception {
		MockMultipartFile csv = new MockMultipartFile("file", "file.csv", "text/csv", csvContent.toString().getBytes());
		return upload(csv);
	}

	protected void processRows(String uuid) throws Exception {
		post("rows", uuid);
		wait("rows", uuid);
	}

	protected void processData(String uuid) throws Exception {
		processRows(uuid);
		post("data", uuid);
		wait("data", uuid);
	}

	protected void processPreviews(String uuid) throws Exception {
		processData(uuid);
		post("previews", uuid);
		wait("previews", uuid);
	}

	protected void processAssignments(String uuid) throws Exception {
		processPreviews(uuid);
		post("assignments", uuid);
		wait("assignments", uuid);
	}

	protected void processLabels(String uuid) throws Exception {
		processAssignments(uuid);
		post("labels", uuid);
		wait("labels", uuid);
	}

	protected AssignmentDTO getFirstPreview(String uuid) throws Exception {
		processPreviews(uuid);
		PreviewsDTO previewsDTO = getPreviews(uuid);
		List<ApiBaseError> validationErrors = previewsDTO.getPreviews().get(0).getValidationErrors();
		assertThat(validationErrors.size(), is(0));
		return previewsDTO.getPreviews().get(0).getAssignmentDTO();
	}

	protected AssignmentDTO getFirstAssignmentAdHocMapping(String uuid, List<MappingDTO> mappings) throws Exception {
		post("rows", uuid);
		wait("rows", uuid);
		post("data", uuid, jackson.writeValueAsString(mappings));
		wait("data", uuid);
		post("previews", uuid);
		wait("previews", uuid);

		MvcResult mvcResult = get("previews", uuid);
		return getFirstResult(mvcResult, previewsType).getPreviews().get(0).getAssignmentDTO();
	}

	protected boolean hasValidationErrors(PreviewsDTO previewsDTO) {
		for (PreviewDTO preview : previewsDTO.getPreviews()) {
			if (!preview.getValidationErrors().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	protected boolean hasParseErrors(PreviewsDTO previewsDTO) {
		for (PreviewDTO preview : previewsDTO.getPreviews()) {
			if (!preview.getParseErrors().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	protected List<MappingDTO> getHeaders(String uuid) throws Exception {
		return getResults(get("headers", uuid), mappingType);
	}

	protected void wait(String step, String uuid) throws Exception {
		await().atMost(JMS_DELAY / 2, MILLISECONDS).until(isProcessingComplete(uuid, step));
	}

	protected RowsDTO getRows(String uuid) throws Exception {
		return getFirstResult(get("rows", uuid), rowsType);
	}

	protected DataDTO getData(String uuid) throws Exception {
		return getFirstResult(get("data", uuid), dataType);
	}

	protected ErrorsDTO getParsingErrors(String uuid) throws Exception {
		return getFirstResult(get("parsing_errors", uuid), errorsType);
	}

	protected PreviewsDTO getPreviews(String uuid) throws Exception {
		return getFirstResult(get("previews", uuid), previewsType);
	}

	protected ErrorsDTO getValidationErrors(String uuid) throws Exception {
		return getFirstResult(get("validation_errors", uuid), errorsType);
	}

	protected AssignmentsDTO getAssignments(String uuid) throws Exception {
		return getFirstResult(get("assignments", uuid), assignmentsType);
	}

	protected Callable<Boolean> isProcessingComplete(final String uuid, final String step) {
		return new WMCallable<Boolean>(webRequestContextProvider) {
			public Boolean apply() throws Exception {
				MvcResult getResult = get("status", uuid);
				StatusDTO statusDTO = getFirstResult(getResult, statusType);
				return step.equals(statusDTO.getStep()) && "0".equals(statusDTO.getRemaining());
			}
		};
	}

	protected TemplateDTO createTemplate() throws Exception {
		authenticationService.setCurrentUser(user);
		WorkCustomFieldGroup workCustomFieldGroup = createCustomFieldGroup(user.getId());
		WorkCustomField workCustomField = workCustomFieldGroup.getWorkCustomFields().get(0);

		CustomFieldGroupDTO.Builder builder =
			new CustomFieldGroupDTO.Builder(
				make(a(CustomFieldGroupDTO,
					with(customFieldGroupId, workCustomFieldGroup.getId()),
					with(field, new CustomFieldDTO.Builder(
						make(a(CustomFieldDTO, with(customFieldId, workCustomField.getId()))))))));

		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(customFieldGroup, builder)));
		TemplateDTO templateDTO = make(an(TemplateDTO, with(assignment, new AssignmentDTO.Builder(assignmentDTO))));

		return assignmentTemplateService.create(templateDTO);
	}

	protected void postSettings(String uuid, SettingsDTO settingsDTO) throws Exception {
		String settingsJson = jackson.writeValueAsString(settingsDTO);
		mockMvc.perform(doPost(ENDPOINT + "/" + uuid + "/settings").content(settingsJson))
						.andExpect(status().isOk())
						.andReturn();
	}
}
