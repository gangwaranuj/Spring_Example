package com.workmarket.api.v2.employer.uploads.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.employer.assignments.models.RoutingDTO;
import com.workmarket.api.v2.employer.assignments.models.TemplateDTO;
import com.workmarket.api.v2.employer.uploads.models.AssignmentsDTO;
import com.workmarket.api.v2.employer.uploads.models.PreviewsDTO;
import com.workmarket.api.v2.employer.uploads.models.SettingsDTO;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.domains.model.User;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class AssignmentsControllerIT extends BaseUploadsControllerIT {
	private static final TypeReference<ApiV2Response<AssignmentsDTO>> assignmentsType = new TypeReference<ApiV2Response<AssignmentsDTO>>() {};

	@Test
	public void getAssignments() throws Exception {
		String uuid = upload(csv);
		processAssignments(uuid);

		MvcResult getMvcResult = get("assignments", uuid);
		AssignmentsDTO getResult = getFirstResult(getMvcResult, assignmentsType);

		assertThat(getResult, hasProperty("uuid", is(uuid)));
		assertThat(getResult, hasProperty("count", is(2L)));
		assertThat(getResult, hasProperty("assignments", hasSize(2)));
	}

	@Test
	public void routeAssignments_flatPricing() throws Exception {
		User worker1 = newRegisteredWorker();
		User worker2 = newRegisteredWorker();
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Industry ID,Start Date & Time,End Date & Time,Flat Price (including fees),Worker IDs\n")
			.append("\"Work 1\",\"interesting work\",\"" + INDUSTRY_ID + "\",\"2017-09-12 12:00PM\",\"2017-09-13 1:00PM\",\"100.00\",\"" + worker1.getUserNumber() + "," + worker2.getUserNumber() + "\"\n");

		String uuid = uploadCSV(csvContent);
		processAssignments(uuid);

		PreviewsDTO previewsDTO = getPreviews(uuid);
		assertThat(hasValidationErrors(previewsDTO), is(false));

		AssignmentsDTO assignmentsDTO = getAssignments(uuid);
		assertThat(assignmentsDTO, hasProperty("uuid", is(uuid)));
		assertThat(assignmentsDTO, hasProperty("count", is(1L)));
		assertThat(assignmentsDTO, hasProperty("assignments", hasSize(1)));

		AssignmentDTO assignmentDTO = assignmentsDTO.getAssignments().get(0);
		RoutingDTO routing = assignmentDTO.getRouting();
		assertThat(routing.getResourceNumbers(), hasSize(2));
	}

	@Test
	public void routeAssignments_template() throws Exception {
		User worker1 = newRegisteredWorker();
		User worker2 = newRegisteredWorker();
		StringBuilder csvContent = new StringBuilder()
			.append("Worker IDs\n")
			.append("\"" + worker1.getUserNumber() + "," + worker2.getUserNumber() + "\"");

		String uuid = uploadCSV(csvContent);

		TemplateDTO template = createTemplate();
		postSettings(uuid, new SettingsDTO.Builder()
			.setLabelId(111L)
			.setTemplateId(template.getId()).build());

		processAssignments(uuid);

		PreviewsDTO previewsDTO = getPreviews(uuid);
		assertThat(hasValidationErrors(previewsDTO), is(false));

		AssignmentsDTO assignmentsDTO = getAssignments(uuid);
		assertThat(assignmentsDTO, hasProperty("uuid", is(uuid)));
		assertThat(assignmentsDTO, hasProperty("count", is(1L)));
		assertThat(assignmentsDTO, hasProperty("assignments", hasSize(1)));

		AssignmentDTO assignmentDTO = assignmentsDTO.getAssignments().get(0);
		RoutingDTO routing = assignmentDTO.getRouting();
		assertThat(routing.getResourceNumbers(), hasSize(2));
	}

	@Test
	public void createAssignment_overwriteTemplateFields() throws Exception {
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Instructions,Desired Skills\n")
			.append("Work 1,First Assignment,Do this,Bookkeeping\n");

		String uuid = uploadCSV(csvContent);

		TemplateDTO template = createTemplate();
		postSettings(uuid, new SettingsDTO.Builder()
			.setLabelId(111L)
			.setTemplateId(template.getId()).build());

		processAssignments(uuid);

		PreviewsDTO previewsDTO = getPreviews(uuid);
		assertThat(hasValidationErrors(previewsDTO), is(false));

		AssignmentsDTO assignmentsDTO = getAssignments(uuid);
		assertThat(assignmentsDTO, hasProperty("uuid", is(uuid)));
		assertThat(assignmentsDTO, hasProperty("count", is(1L)));
		assertThat(assignmentsDTO, hasProperty("assignments", hasSize(1)));

		AssignmentDTO assignmentDTO = assignmentsDTO.getAssignments().get(0);
		assertThat(assignmentDTO.getTitle(), is("Work 1"));
		assertThat(assignmentDTO.getDescription(), is("First Assignment"));
		assertThat(assignmentDTO.getInstructions(), is("Do this"));
		assertThat(assignmentDTO.getSkills(), is("Bookkeeping"));
	}
}
