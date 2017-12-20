package com.workmarket.api.v2.employer.uploads.controllers;

import com.google.common.collect.Iterables;

import au.com.bytecode.opencsv.CSVReader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.workmarket.api.ApiRedirectResponse;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.uploads.models.AssignmentsDTO;
import com.workmarket.api.v2.employer.uploads.models.CsvDTO;
import com.workmarket.api.v2.employer.uploads.models.ErrorsDTO;
import com.workmarket.api.v2.employer.uploads.models.MappingDTO;
import com.workmarket.api.v2.employer.uploads.models.PreviewDTO;
import com.workmarket.api.v2.employer.uploads.models.RowsDTO;
import com.workmarket.api.v2.employer.uploads.models.SettingsDTO;
import com.workmarket.api.v2.employer.uploads.services.PreviewStorageService;
import com.workmarket.api.v2.employer.uploads.services.UploadMappingService;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeAssociation;
import com.workmarket.service.business.upload.transactional.WorkUploadColumnService;
import com.workmarket.service.infra.file.RemoteFileAdapter;
import com.workmarket.service.infra.file.RemoteFileType;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.StringUtilities;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.mappingType;
import static com.workmarket.api.v2.employer.uploads.services.CsvErrorsServiceImpl.ERROR_COLUMN_HEADER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.array;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class UploadsControllerIT extends BaseUploadsControllerIT {
	private static final TypeReference<ApiV2Response<CsvDTO>> csvType = new TypeReference<ApiV2Response<CsvDTO>>() {};
	private static final TypeReference<ApiV2Response<RowsDTO>> rowsType = new TypeReference<ApiV2Response<RowsDTO>>() {};
	private static final TypeReference<ApiV2Response<ErrorsDTO>> errorsType = new TypeReference<ApiV2Response<ErrorsDTO>>() {};

	@Autowired WorkUploadColumnService workUploadColumnService;
	@Autowired PreviewStorageService previewStorageService;
	@Autowired UploadMappingService mappingService;
	@Autowired RemoteFileAdapter remoteFileAdapter;

	@Test
	public void saveCSVFile() throws Exception {
		MvcResult mvcResult =  mockMvc.perform(
			MockMvcRequestBuilders
				.fileUpload(ENDPOINT)
				.file(csv)
				.header("Authorization", "Bearer " + accessToken)

		).andExpect(status().isOk()).andReturn();

		CsvDTO result = getFirstResult(mvcResult, csvType);

		assertThat(result, hasProperty("id", is(not(nullValue()))));
		assertThat(result, hasProperty("uuid", is(not(nullValue()))));
		assertThat(result, hasProperty("name", is("file.csv")));
	}

	@Test
	public void getCsvDTO() throws Exception {
		MvcResult mvcResult =  mockMvc.perform(
			MockMvcRequestBuilders
				.fileUpload(ENDPOINT)
				.file(csv)
				.header("Authorization", "Bearer " + accessToken)
		).andExpect(status().isOk()).andReturn();

		CsvDTO result = getFirstResult(mvcResult, csvType);

		MvcResult getMvcResult = get("", result.getUuid());

		CsvDTO getResult = getFirstResult(getMvcResult, csvType);

		assertThat(getResult, samePropertyValuesAs(result));
	}

	@Test
	public void downloadCSVFile() throws Exception {
		String uuid = upload(csv);

		MvcResult mvcResult = getFile("file", uuid);

		assertThat(mvcResult.getResponse().getHeader("Location"), is(not(nullValue())));
	}

	@Test
	public void resetUpload() throws Exception {
		String uuid = upload(csv);
		processRows(uuid);

		List<PreviewDTO> storedResults = previewStorageService.get(uuid, 0, -1);
		assertThat(storedResults, hasSize(2));

		delete("", uuid);

		MvcResult emptyMvcResults = get("rows", uuid);

		RowsDTO emptyResult = getFirstResult(emptyMvcResults, rowsType);
		assertThat(emptyResult.getRows(), is(empty()));

		storedResults = previewStorageService.get(uuid, 0, -1);
		assertThat(storedResults, is(empty()));
	}

	@Test
	public void getParsingErrors() throws Exception {
		MockMultipartFile csv = mockCsv("file",
			"Title,Description,Owner ID\n",
			"awesome title,awesome description,invalid\n",
			"blarg,blurg,12345\n"
		);

		String uuid = upload(csv);
		processData(uuid);

		MvcResult getMvcResult = get("parsing_errors", uuid);
		ErrorsDTO getResult = getFirstResult(getMvcResult, errorsType);
		assertThat(getResult, hasProperty("uuid", is(uuid)));
		assertThat(getResult, hasProperty("count", is(1L)));
		assertThat(getResult, hasProperty("errors", hasSize(1)));
	}

	@Test
	public void getValidationErrors() throws Exception {
		MockMultipartFile csv = mockCsv("file",
			"Title,Description\n",
			"awesome title,awesome description\n",
			",blurg\n" // <==== Missing Title
		);

		String uuid = upload(csv);
		processPreviews(uuid);

		MvcResult getMvcResult = get("validation_errors", uuid);
		ErrorsDTO getResult = getFirstResult(getMvcResult, errorsType);
		assertThat(getResult, hasProperty("uuid", is(uuid)));
		assertThat(getResult, hasProperty("count", is(1L)));
		assertThat(getResult, hasProperty("errors", hasSize(1)));
	}

	@Test
	public void getHeaders_allDefaultMapped() throws Exception {
		StringBuilder headerString = new StringBuilder();
		for (String header : workUploadColumnService.getColumnTypeCodeKeys()) {
			headerString.append("\"").append(header).append("\",");
		}
		headerString.deleteCharAt(headerString.length() - 1);

		MockMultipartFile csv = new MockMultipartFile("file", "file.csv", "text/csv", (headerString.toString() + "\n").getBytes());

		String uuid = upload(csv);

		MvcResult mvcResult = get("headers", uuid);

		List<MappingDTO> csvHeaders = getResults(mvcResult, mappingType);
		for (MappingDTO builder : csvHeaders) {
			MappingDTO header = builder;
			assertThat(header.getHeader(), is(not(nullValue())));
			assertThat(header.getProperty(), is(not(nullValue())));
		}
	}

	@Test
	public void getHeaders_allUnmapped() throws Exception {
		MockMultipartFile csv = new MockMultipartFile("file", "file.csv", "text/csv",
			("\"Ticket Number\",\"Store Number\"\n").getBytes());

		String uuid = upload(csv);

		MvcResult mvcResult = get("headers", uuid);

		List<MappingDTO> csvHeaders = getResults(mvcResult, mappingType);
		assertThat(csvHeaders, hasSize(2));

		MappingDTO header1 = csvHeaders.get(0);
		assertThat(header1, hasProperty("header", is("Ticket Number")));
		assertThat(header1, hasProperty("property", isEmptyOrNullString()));

		MappingDTO header2 = csvHeaders.get(1);
		assertThat(header2, hasProperty("header", is("Store Number")));
		assertThat(header2, hasProperty("property", isEmptyOrNullString()));
	}

	@Test
	public void createAssignmentStoreAndFetchFromRedis() throws Exception {
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Instructions,Desired Skills,Support Contact ID,Owner ID,Industry ID\n")
			.append("Work 1,First Assignment,Do this,Housekeeping,111,222,333\n");

		String uuid = uploadCSV(csvContent);
		processAssignments(uuid);

		List<PreviewDTO> rowPreviews = previewStorageService.get(uuid, 0, -1);
		assertThat(rowPreviews, is(not(nullValue())));
		assertThat(rowPreviews, hasSize(1));
		assertThat(rowPreviews.get(0).getAssignmentDTO(), is(not(nullValue())));
	}

	@Test
	public void createAssignmentStoreAndFetchAllFromRedis() throws Exception {
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Instructions,Desired Skills,Support Contact ID,Owner ID,Industry ID\n")
			.append("Work 1,First Assignment,Do this,Housekeeping,111,222,333\n")
			.append("Work 2,Second Assignment,Do that,Bookkeeping,555,555,666\n");

		String uuid = uploadCSV(csvContent);
		processAssignments(uuid);
		List<PreviewDTO> rowPreviews = previewStorageService.get(uuid, 0, 10);
		assertThat(rowPreviews, hasSize(2));
	}

	public void downloadErrorFileWithoutValidAssignments() throws Exception {
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Owner ID\n")
			.append("awesome title,awesome description,invalid\n")
			.append("blarg,blurg,12345\n");

		String uuid = uploadCSV(csvContent);
		processData(uuid);

		MvcResult getMvcResult = mockMvc.perform(
			MockMvcRequestBuilders
				.get(String.format("%s/%s/error_file", ENDPOINT, uuid))
				.header("Authorization", "Bearer " + accessToken)
		).andExpect(status().isFound())
			.andDo(new ResultHandler() {
				@Override
				public void handle(final MvcResult mr) throws Exception {
					List<String[]> rows = downloadErrorFileInRows(mr.getModelAndView().getViewName());
					assertThat(rows, is(not(empty())));
					String[] headers = rows.get(0);
					assertThat(headers[headers.length - 1], equalTo(ERROR_COLUMN_HEADER));

					for (String[] row : rows) {
						assertThat(row.length, equalTo(headers.length));
					}

					String[] row = Iterables.getLast(rows);
					assertThat(rows.size(), is(2));
					assertThat(row, is(array(equalTo("awesome title"), equalTo("awesome description"), equalTo("invalid"), equalTo("Owner ID is invalid (invalid)"))));
				}
			}).andReturn();
		assertThat(getMvcResult.getResponse().getHeader("Location"), is(not(nullValue())));
	}

	@Test
	public void downloadErrorFileWithValidAssignments() throws Exception {
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Owner ID\n")
			.append("awesome title,awesome description,invalid\n")
			.append("blarg,blurg,12345\n");

		String uuid = uploadCSV(csvContent);
		processData(uuid);

		MvcResult getMvcResult = mockMvc.perform(
			doGet(String.format("%s/%s/error_file", ENDPOINT, uuid)).param("includeValid", "true")
		).andExpect(status().isFound())
			.andDo(new ResultHandler() {
				@Override
				public void handle(final MvcResult mr) throws Exception {
					ApiRedirectResponse apiRedirectResponse = (ApiRedirectResponse)mr.getModelAndView().getView();
					List<String[]> rows = downloadErrorFileInRows(apiRedirectResponse.getUrl());
					assertThat(rows, is(not(empty())));
					String[] headers = rows.get(0);
					assertThat(headers[headers.length - 1], equalTo(ERROR_COLUMN_HEADER));

					for (String[] row : rows) {
						assertThat(row.length, equalTo(headers.length));
					}

					String[] row = Iterables.getLast(rows);
					assertThat(rows.size(), is(3));
					assertThat(row, is(array(equalTo("blarg"), equalTo("blurg"), equalTo("12345"), isEmptyOrNullString())));
				}
			}).andReturn();
		assertThat(getMvcResult.getResponse().getHeader("Location"), is(not(nullValue())));
	}

	@Test
	public void applyLabelToAssignments() throws Exception {
		WorkSubStatusType workSubStatusType = workSubStatusService.saveOrUpdateCustomWorkSubStatus(createWorkSubStatusType(user.getCompany().getId()));

		String uuid = upload(csv);
		postSettings(uuid, new SettingsDTO.Builder()
			.setLabelId(workSubStatusType.getId())
			.build()
		);

		processLabels(uuid);
		AssignmentsDTO assignmentsDTO = getAssignments(uuid);

		List<WorkSubStatusTypeAssociation> workLabelAssociations = workSubStatusService.findAllWorkSubStatusTypeAssociationBySubStatusId(workSubStatusType.getId());
		assertEquals(workLabelAssociations.size(), assignmentsDTO.getAssignments().size());
	}

	private List<String[]> downloadErrorFileInRows(String url) throws Exception {
		String uuid = extractUuidFromUrl(url);
		return getCsvReader(uuid).readAll();
	}

	private String extractUuidFromUrl(String url) {
		String uri = StringUtilities.urlDecode(url.replace("redirect:", ""));
		return StringUtils.substringAfterLast(StringUtils.substringBefore(uri, "?"), "/");
	}

	private CSVReader getCsvReader(String uuid) throws Exception {
		CSVReader reader = new CSVReader(
			new BufferedReader(
				new InputStreamReader(
					remoteFileAdapter.getFileStream(RemoteFileType.TMP, uuid))));

		return reader;
	}
}
