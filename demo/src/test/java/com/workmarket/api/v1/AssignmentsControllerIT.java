package com.workmarket.api.v1;

import com.jayway.jsonpath.JsonPath;
import com.workmarket.common.core.RequestContext;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkRescheduleNegotiation;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.WorkNegotiationDTO;
import com.workmarket.test.IntegrationTest;
import com.workmarket.test.mock.auth.AuthenticationMock;
import com.workmarket.utility.DateUtilities;
import com.workmarket.web.controllers.ControllerIT;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.xml.transform.StringSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.util.Calendar;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class AssignmentsControllerIT extends ControllerIT {
	private static final String ENDPOINT = "/api/v1/assignments";

	@Autowired protected WorkNegotiationService workNegotiationService;

	@Test
	public void validateCreateAssignmentsXml() throws Exception {
		final Validator validator = buildValidator("src/test/resources/api/v1/assignment-create-response.xsd");

		try {
			User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
			User contractor = newContractor();
			Calendar today = Calendar.getInstance();
			createWorkAndSendToResourceNoPaymentTermsAndAccept(employee, contractor, today);
			login(employee);

			MvcResult mvcResult = mockMvc.perform(doPost("/api/v1/assignments/create.xml")
							.param("title", "Title")
							.param("description", "Description")
							.param("industry_id", "1000")
							.param("pricing_type", "flat")
							.param("pricing_flat_price", "1")
							.param("scheduled_start", "1893456000") // year 2030
							.param("location_address1", "254 W 31st Street")
							.param("location_city", "New York")
							.param("location_state", "NY")
							.param("location_zip", "10011")
							.param("location_country", "USA")
			).andExpect(status().isOk()).andReturn();
			String response = mvcResult.getResponse().getContentAsString();
			logger.error("response: " + response);
			validator.validate(new StringSource(response));
		} catch (SAXException e) {
			logger.error("error", e);
			fail();
		}
	}

	@Test
	public void validateListAssignmentsXml() throws Exception {
		final Validator validator = buildValidator("src/test/resources/api/v1/assignment-list-response.xsd");

		try {
			User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
			User contractor = newContractor();
			Calendar today = Calendar.getInstance();
			createWorkAndSendToResourceNoPaymentTermsAndAccept(employee, contractor, today);
			login(employee);

			MvcResult mvcResult = mockMvc.perform(doGet("/api/v1/assignments/list.xml")
							.param("status", WorkStatusType.ACTIVE)
							.param("limit", "1")
							.contentType(MediaType.APPLICATION_JSON)
			).andExpect(status().isOk()).andReturn();
			String response = mvcResult.getResponse().getContentAsString();
			logger.error("response: " + response);
			validator.validate(new StringSource(response));
		} catch (SAXException e) {
			logger.error("error", e);
			fail();
		}
	}

	@Test
	public void validateGetAssignmentXml() throws Exception {
		final Validator validator = buildValidator("src/test/resources/api/v1/assignment-get-response.xsd");

		try {
			User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
			User contractor = newContractor();
			Calendar today = Calendar.getInstance();

			Work work = createWorkAndSendToResourceNoPaymentTermsAndAccept(employee, contractor, today);

			WorkNegotiationDTO workNegotiationDTO = new WorkNegotiationDTO();

			TimeZone timeZone = invariantDataService.findTimeZonesByTimeZoneId("GMT");
			Calendar startDay = Calendar.getInstance();
			startDay.add(Calendar.DAY_OF_MONTH, 1);

			Calendar endDay = Calendar.getInstance();
			endDay.add(Calendar.DAY_OF_MONTH, 10);

			workNegotiationDTO.setScheduleFromString(DateUtilities.getISO8601(startDay.getTime()));
			workNegotiationDTO.setScheduleThroughString(DateUtilities.getISO8601(endDay.getTime()));
			workNegotiationDTO.setIsScheduleRange(Boolean.TRUE);
			workNegotiationDTO.setScheduleNegotiation(Boolean.TRUE);
			workNegotiationDTO.setTimeZoneId(timeZone.getId());

			authenticationService.setCurrentUser(contractor);
			details = (ExtendedUserDetails) extendedUserDetailsService.loadUserByUsername(contractor.getEmail());
			SecurityContextHolder.getContext().setAuthentication(new AuthenticationMock(details));

			WorkRescheduleNegotiation negotiation = workNegotiationService.createRescheduleNegotiation(work.getId(), workNegotiationDTO);

			login(employee);

			MvcResult mvcResult = mockMvc.perform(doGet("/api/v1/assignments/get.xml")
							.header("accept", MediaType.APPLICATION_XML)
							.param("id", work.getWorkNumber().toString())
			).andExpect(status().isOk()).andReturn();
			String response = mvcResult.getResponse().getContentAsString();
			logger.error("response: " + response);
			validator.validate(new StringSource(response));
		} catch (SAXException e) {
			logger.error("error", e);
			fail();
		}
	}

	@Test
	public void validateListAssignments() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		String projectName = "Project Name";
		Work work = newWorkWithProject(employee.getId(), projectName);

		authenticationService.setCurrentUser(employee);
		details = (ExtendedUserDetails) extendedUserDetailsService.loadUserByUsername(employee.getEmail());
		SecurityContextHolder.getContext().setAuthentication(new AuthenticationMock(details));
		login(employee);
		MvcResult mvcResult = mockMvc.perform(
			doGet("/api/v1/assignments/list")
				.param("status", WorkStatusType.DRAFT)
				.param("limit", "1")
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk()).andReturn();

		String response = mvcResult.getResponse().getContentAsString();

		Object projectNameActual = JsonPath.compile("$.response.data[0].project_name").read(response);
		assertEquals(projectName, projectNameActual);
	}

	@Test
	public void getAssignment() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		User contractor = newContractor();
		Calendar today = Calendar.getInstance();

		Work work = createWorkAndSendToResourceNoPaymentTermsAndAccept(employee, contractor, today);

		WorkNegotiationDTO workNegotiationDTO = new WorkNegotiationDTO();

		TimeZone timeZone = invariantDataService.findTimeZonesByTimeZoneId("GMT");
		Calendar startDay = Calendar.getInstance();
		startDay.add(Calendar.DAY_OF_MONTH, 1);

		Calendar endDay = Calendar.getInstance();
		endDay.add(Calendar.DAY_OF_MONTH, 10);

		workNegotiationDTO.setScheduleFromString(DateUtilities.getISO8601(startDay.getTime()));
		workNegotiationDTO.setScheduleThroughString(DateUtilities.getISO8601(endDay.getTime()));
		workNegotiationDTO.setIsScheduleRange(Boolean.TRUE);
		workNegotiationDTO.setScheduleNegotiation(Boolean.TRUE);
		workNegotiationDTO.setTimeZoneId(timeZone.getId());

		authenticationService.setCurrentUser(contractor);
		details = (ExtendedUserDetails) extendedUserDetailsService.loadUserByUsername(contractor.getEmail());
		SecurityContextHolder.getContext().setAuthentication(new AuthenticationMock(details));

		WorkRescheduleNegotiation negotiation = workNegotiationService.createRescheduleNegotiation(work.getId(), workNegotiationDTO);

		login(employee);

		MvcResult mvcResult = mockMvc.perform(doGet(ENDPOINT + "/get")
					.header("accept", MediaType.APPLICATION_JSON)
					.param("id", work.getWorkNumber().toString())
					.contentType(MediaType.APPLICATION_JSON)
			).andExpect(status().isOk()).andReturn();

		String response = mvcResult.getResponse().getContentAsString();

		Object requested_start = JsonPath.compile("$.response.reschedule_request.request_window_start").read(response);
		Object requested_end = JsonPath.compile("$.response.reschedule_request.request_window_end").read(response);

		authenticationService.setCurrentUser(employee);
		workNegotiationService.approveNegotiation(negotiation.getId());

		mvcResult = mockMvc.perform(doGet(ENDPOINT + "/get")
				.header("accept", MediaType.APPLICATION_JSON)
				.param("id", work.getWorkNumber().toString())
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk()).andReturn();

		response = mvcResult.getResponse().getContentAsString();

		Object start = JsonPath.compile("$.response.assignment_window_start").read(response);
		Object end = JsonPath.compile("$.response.assignment_window_end").read(response);

		Object scheduled_start = JsonPath.compile("$.response.scheduled_start").read(response);
		Object scheduled_end = JsonPath.compile("$.response.scheduled_end").read(response);

		Assert.assertEquals(start, requested_start);
		Assert.assertEquals(end, requested_end);

		Assert.assertEquals(start, scheduled_start);
		Assert.assertEquals(end, scheduled_end);
	}

	@Test
	public void sendAssignment() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		User contractor = newContractor();

		Work work = createWorkAndSendToResourceWithPaymentTerms(employee, contractor);

		login(employee);

		UserGroup group = newCompanyUserGroup(employee.getCompany().getId());

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT + "/" + work.getWorkNumber() + "/send")
						.header("accept", MediaType.APPLICATION_JSON)
						.param("group_id", group.getId().toString())
						.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk()).andReturn();

		String response = mvcResult.getResponse().getContentAsString();
		Object success = JsonPath.compile("$.response.successful").read(response);
		logger.error("response: " + response);
		assertEquals(success, true);
	}

	@Test
	public void testRateUser() throws Exception {
		final User contractor = newContractor();
		final CompleteWorkDTO completeWorkDTO = new CompleteWorkDTO();
		final User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		final Work work = createWorkAndSendToResourceWithPaymentTerms(employee, contractor);

		workService.acceptWork(contractor, work);
		authenticationService.setCurrentUser(employee);

		details = (ExtendedUserDetails) extendedUserDetailsService.loadUserByUsername(employee.getEmail());
		SecurityContextHolder.getContext().setAuthentication(new AuthenticationMock(details));

		workService.completeWork(work.getId(), completeWorkDTO);

		login(employee);

		MvcResult mvcResult = mockMvc.perform(
				doPost(ENDPOINT + "/" + "rate_assignment")
				.header("accept", MediaType.APPLICATION_JSON)
				.param("id", work.getWorkNumber())
				.param("value", "2")
				.param("quality", "2")
				.param("professionalism", "2")
				.param("communication", "2")
				.param("review", "test")
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk()).andReturn();

		final String response = mvcResult.getResponse().getContentAsString();
		final Object success = JsonPath.compile("$.response.successful").read(response);

		assertEquals(success, true);
	}

	private Validator buildValidator(final String xsdPath) throws SAXException {
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Source schemaFile = new StreamSource(new File(xsdPath));
		Schema schema = factory.newSchema(schemaFile);
		return schema.newValidator();
	}
}
