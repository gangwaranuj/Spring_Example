package com.workmarket.api.v2.employer.assignments.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.api.ApiBaseError;
import com.workmarket.api.ApiBaseHttpMessageConverter;
import com.workmarket.api.v2.ApiV2BaseIT;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.assignments.controllers.support.AssignmentMaker;
import com.workmarket.api.v2.employer.assignments.controllers.support.DeliverableMaker;
import com.workmarket.api.v2.employer.assignments.controllers.support.DocumentMaker;
import com.workmarket.api.v2.employer.assignments.controllers.support.ShipmentGroupMaker;
import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.employer.assignments.models.ConfigurationDTO;
import com.workmarket.api.v2.employer.assignments.models.DeliverableDTO;
import com.workmarket.api.v2.employer.assignments.models.DeliverablesGroupDTO;
import com.workmarket.api.v2.employer.assignments.models.DocumentDTO;
import com.workmarket.api.v2.employer.assignments.models.PricingDTO;
import com.workmarket.api.v2.employer.assignments.models.RecurrenceDTO;
import com.workmarket.api.v2.employer.assignments.models.RoutingDTO;
import com.workmarket.api.v2.employer.assignments.models.ScheduleDTO;
import com.workmarket.api.v2.employer.assignments.models.ShipmentDTO;
import com.workmarket.api.v2.employer.assignments.models.ShipmentGroupDTO;
import com.workmarket.api.v2.employer.assignments.services.AssignmentService;
import com.workmarket.api.v2.employer.uploads.models.StatusDTO;
import com.workmarket.api.v2.model.ContactDTO;
import com.workmarket.api.v2.model.CustomFieldDTO;
import com.workmarket.api.v2.model.CustomFieldGroupDTO;
import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.api.v2.model.SurveyDTO;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.part.ShippingDestinationType;
import com.workmarket.helpers.WMCallable;
import com.workmarket.recurrence.common.RecurrenceType;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.DateUtilities;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import static com.jayway.awaitility.Awaitility.await;
import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.an;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static com.natpryce.makeiteasy.MakeItEasy.withNull;
import static com.workmarket.api.v2.employer.assignments.controllers.support.AssignmentMaker.AssignmentDTO;
import static com.workmarket.api.v2.employer.assignments.controllers.support.AssignmentMaker.configuration;
import static com.workmarket.api.v2.employer.assignments.controllers.support.AssignmentMaker.document;
import static com.workmarket.api.v2.employer.assignments.controllers.support.AssignmentMaker.recurrence;
import static com.workmarket.api.v2.employer.assignments.controllers.support.AssignmentMaker.routing;
import static com.workmarket.api.v2.employer.assignments.controllers.support.AssignmentMaker.schedule;
import static com.workmarket.api.v2.employer.assignments.controllers.support.AssignmentMaker.shipmentGroup;
import static com.workmarket.api.v2.employer.assignments.controllers.support.AssignmentMaker.survey;
import static com.workmarket.api.v2.employer.assignments.controllers.support.AssignmentMaker.title;
import static com.workmarket.api.v2.employer.assignments.controllers.support.AssignmentMaker.uniqueExternalId;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ConfigurationMaker.ConfigurationDTO;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ConfigurationMaker.customFieldsEnabled;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ConfigurationMaker.deliverablesEnabled;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ConfigurationMaker.requirementSetsEnabled;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ConfigurationMaker.shipmentsEnabled;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ConfigurationMaker.surveysEnabled;
import static com.workmarket.api.v2.employer.assignments.controllers.support.CustomFieldsMaker.CustomFieldDTO;
import static com.workmarket.api.v2.employer.assignments.controllers.support.CustomFieldsMaker.CustomFieldGroupDTO;
import static com.workmarket.api.v2.employer.assignments.controllers.support.CustomFieldsMaker.customFieldGroupId;
import static com.workmarket.api.v2.employer.assignments.controllers.support.CustomFieldsMaker.customFieldGroupName;
import static com.workmarket.api.v2.employer.assignments.controllers.support.CustomFieldsMaker.customFieldId;
import static com.workmarket.api.v2.employer.assignments.controllers.support.CustomFieldsMaker.customFieldName;
import static com.workmarket.api.v2.employer.assignments.controllers.support.CustomFieldsMaker.field;
import static com.workmarket.api.v2.employer.assignments.controllers.support.DeliverableMaker.deliverable;
import static com.workmarket.api.v2.employer.assignments.controllers.support.DeliverableMaker.deliverableDescription;
import static com.workmarket.api.v2.employer.assignments.controllers.support.DocumentMaker.documentId;
import static com.workmarket.api.v2.employer.assignments.controllers.support.LocationMaker.LocationDTO;
import static com.workmarket.api.v2.employer.assignments.controllers.support.LocationMaker.addressLine1;
import static com.workmarket.api.v2.employer.assignments.controllers.support.LocationMaker.addressLine2;
import static com.workmarket.api.v2.employer.assignments.controllers.support.LocationMaker.city;
import static com.workmarket.api.v2.employer.assignments.controllers.support.LocationMaker.country;
import static com.workmarket.api.v2.employer.assignments.controllers.support.LocationMaker.name;
import static com.workmarket.api.v2.employer.assignments.controllers.support.LocationMaker.state;
import static com.workmarket.api.v2.employer.assignments.controllers.support.LocationMaker.zip;
import static com.workmarket.api.v2.employer.assignments.controllers.support.PricingMaker.PricingDTO;
import static com.workmarket.api.v2.employer.assignments.controllers.support.PricingMaker.flatPrice;
import static com.workmarket.api.v2.employer.assignments.controllers.support.RoutingMaker.RoutingDTO;
import static com.workmarket.api.v2.employer.assignments.controllers.support.RoutingMaker.groupIds;
import static com.workmarket.api.v2.employer.assignments.controllers.support.RoutingMaker.resourceNumbers;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ScheduleMaker.ScheduleDTO;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ScheduleMaker.from;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ScheduleMaker.through;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ShipmentGroupMaker.ShipmentGroupDTO;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ShipmentGroupMaker.returnAddress;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ShipmentGroupMaker.returnShipment;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ShipmentGroupMaker.shipments;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ShipmentGroupMaker.shippingAddress;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ShipmentGroupMaker.shippingDestinationType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ShipmentGroupMaker.suppliedByWorker;
import static com.workmarket.api.v2.employer.assignments.controllers.support.SurveyMaker.SurveyDTO;
import static com.workmarket.api.v2.employer.assignments.controllers.support.SurveyMaker.surveyId;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.assignmentType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.configurationType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.customFieldGroupsType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.deliverablesType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.documentType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.errorType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.locationType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.pricingType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.routingType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.scheduleType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.shipmentsType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.surveyType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.validationErrorListType;
import static com.workmarket.api.v2.employer.assignments.services.AbstractAssignmentUseCase.DATE_TIME_FORMAT;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class AssignmentDraftsControllerIT extends ApiV2BaseIT {
	@Autowired private AssignmentService assignmentService;
	@Autowired private EventRouter eventRouter;

	private static final String ENDPOINT = "/employer/v2/assignments/drafts";
	public static final long DATE_TIME_OFFSET = 360000 * 2;
	protected static final TypeReference<ApiV2Response<StatusDTO>> statusType = new TypeReference<ApiV2Response<StatusDTO>>() {
	};

	private UserGroup group;
	private User worker;
	private WorkCustomFieldGroup customFieldGroup;
	private WorkCustomField customField;
	private AbstractAssessment assessment;
	private Asset asset;


	@Before
	public void setUp() throws Exception {
		login();

		worker = newCompanyEmployee(user.getCompany().getId());
		group = newCompanyUserGroup(user.getCompany().getId());
		List<Long> ids = Lists.newArrayList();
		ids.add(worker.getId());
		userGroupService.addUsersToGroup(ids, group.getId(), user.getId());
		eventRouter.sendEvent(new UserSearchIndexEvent(worker.getId()));
		customFieldGroup = createCustomFieldGroup(user.getId());
		customField = customFieldGroup.getWorkCustomFields().get(0);

		assessment = newAssessmentForUser(user, null, false);
		asset = assetManagementService.storeAssetForUser(newAssetDTO(), user.getId(), true);
	}

	@Test
	public void getAssignmentDraftBasics() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);

		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT + "/" + assignmentDTO.getId())
		).andExpect(status().isOk()).andReturn();

		AssignmentDTO result = getFirstResult(mvcResult, assignmentType);

		assertThat(result, hasProperty("id", is(assignmentDTO.getId())));
		assertThat(result, hasProperty("title", is(assignmentDTO.getTitle())));
		assertThat(result, hasProperty("description", is(assignmentDTO.getDescription())));
		assertThat(result, hasProperty("instructions", is(assignmentDTO.getInstructions())));
		assertThat(result, hasProperty("skills", is(assignmentDTO.getSkills())));
		assertThat(result, hasProperty("industryId", is(assignmentDTO.getIndustryId())));
		assertThat(result, hasProperty("supportContactId", is(assignmentDTO.getSupportContactId())));
	}

	@Test
	public void getAssignmentDraftWithSchedule() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);

		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT + "/" + assignmentDTO.getId())
		).andExpect(status().isOk()).andReturn();

		AssignmentDTO result = getFirstResult(mvcResult, assignmentType);

		assertThat(result, hasProperty("schedule", samePropertyValuesAs(assignmentDTO.getSchedule())));
	}

	@Test
	public void getAssignmentDraftWithPricing() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);

		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT + "/" + assignmentDTO.getId())
		).andExpect(status().isOk()).andReturn();

		AssignmentDTO result = getFirstResult(mvcResult, assignmentType);

		assertThat(result, hasProperty("pricing", samePropertyValuesAs(assignmentDTO.getPricing())));
	}

	@Test
	public void getAssignmentDraftWithLocation() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);
		LocationDTO locationDTO = assignmentDTO.getLocation();

		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT + "/" + assignmentDTO.getId())
		).andExpect(status().isOk()).andReturn();

		LocationDTO locationResult = getFirstResult(mvcResult, assignmentType).getLocation();

		assertThat(locationResult, hasProperty("id", is(not(nullValue()))));
		assertThat(locationResult, hasProperty("number", is(locationDTO.getNumber())));
		assertThat(locationResult, hasProperty("name", is(locationDTO.getName())));
		assertThat(locationResult, hasProperty("addressLine1", is(locationDTO.getAddressLine1())));
		assertThat(locationResult, hasProperty("addressLine2", is(locationDTO.getAddressLine2())));
		assertThat(locationResult, hasProperty("city", is(locationDTO.getCity())));
		assertThat(locationResult, hasProperty("state", is(locationDTO.getState())));
		assertThat(locationResult, hasProperty("zip", is(locationDTO.getZip())));
		assertThat(locationResult, hasProperty("country", is(locationDTO.getCountry())));
		assertThat(locationResult, hasProperty("longitude", is(locationDTO.getLongitude())));
		assertThat(locationResult, hasProperty("latitude", is(locationDTO.getLatitude())));
	}

	@Test
	public void getAssignmentDraftWithLocationContact() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);
		ContactDTO contactDTO = assignmentDTO.getLocation().getContact();

		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT + "/" + assignmentDTO.getId())
		).andExpect(status().isOk()).andReturn();

		ContactDTO contactResult = getFirstResult(mvcResult, assignmentType).getLocation().getContact();

		assertThat(contactResult, hasProperty("id", is(not(nullValue()))));
		assertThat(contactResult, hasProperty("firstName", is(contactDTO.getFirstName())));
		assertThat(contactResult, hasProperty("lastName", is(contactDTO.getLastName())));
		assertThat(contactResult, hasProperty("email", is(contactDTO.getEmail())));
		assertThat(contactResult, hasProperty("workPhone", is(contactDTO.getWorkPhone())));
		assertThat(contactResult, hasProperty("workPhoneExtension", is(contactDTO.getWorkPhoneExtension())));
		assertThat(contactResult, hasProperty("mobilePhone", is(contactDTO.getMobilePhone())));
	}

	@Test
	public void getAssignmentDraftWithSecondaryLocationContact() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);
		ContactDTO secondaryContactDTO = assignmentDTO.getLocation().getSecondaryContact();

		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT + "/" + assignmentDTO.getId())
		).andExpect(status().isOk()).andReturn();

		ContactDTO secondaryContactResult = getFirstResult(mvcResult, assignmentType)
						.getLocation()
						.getSecondaryContact();

		assertThat(secondaryContactResult, hasProperty("id", is(not(nullValue()))));
		assertThat(secondaryContactResult, hasProperty("firstName", is(secondaryContactDTO.getFirstName())));
		assertThat(secondaryContactResult, hasProperty("lastName", is(secondaryContactDTO.getLastName())));
		assertThat(secondaryContactResult, hasProperty("email", is(secondaryContactDTO.getEmail())));
		assertThat(secondaryContactResult, hasProperty("workPhone", is(secondaryContactDTO.getWorkPhone())));
		assertThat(secondaryContactResult, hasProperty("workPhoneExtension", is(secondaryContactDTO.getWorkPhoneExtension())));
		assertThat(secondaryContactResult, hasProperty("mobilePhone", is(secondaryContactDTO.getMobilePhone())));
	}

	@Test
	public void getAssignmentDraftWithRouting() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);
		RoutingDTO routingDTO = assignmentDTO.getRouting();

		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT + "/" + assignmentDTO.getId())
		).andExpect(status().isOk()).andReturn();

		RoutingDTO result = getFirstResult(mvcResult, assignmentType).getRouting();

		assertThat(result, hasProperty("groupIds", is(routingDTO.getGroupIds())));
		assertThat(result, hasProperty("resourceNumbers", is(routingDTO.getResourceNumbers())));
		assertThat(result, hasProperty("vendorCompanyNumbers", is(routingDTO.getVendorCompanyNumbers())));
		assertThat(result, hasProperty("assignToFirstToAccept", is(routingDTO.isAssignToFirstToAccept())));
		assertThat(result, hasProperty("shownInFeed", is(routingDTO.isShownInFeed())));
		assertThat(result, hasProperty("smartRoute", is(routingDTO.isSmartRoute())));
		assertThat(result, hasProperty("firstToAcceptCandidates", samePropertyValuesAs(routingDTO.getFirstToAcceptCandidates())));
		assertThat(result, hasProperty("needToApplyCandidates", samePropertyValuesAs(routingDTO.getNeedToApplyCandidates())));
	}

	@Test
	public void getAssignmentDraftWithCustomFieldGroups() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);

		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT + "/" + assignmentDTO.getId())
		).andExpect(status().isOk()).andReturn();

		AssignmentDTO result = getFirstResult(mvcResult, assignmentType);

		assertThat(result, hasProperty("customFieldGroups", samePropertyValuesAs(assignmentDTO.getCustomFieldGroups())));
	}

	@Test
	public void getAssignmentDraftWithShipments() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);
		ShipmentGroupDTO shipmentGroupDTO = assignmentDTO.getShipmentGroup();
		ShipmentDTO shipmentDTO = shipmentGroupDTO.getShipments().iterator().next();

		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT + "/" + assignmentDTO.getId())
		).andExpect(status().isOk()).andReturn();

		AssignmentDTO result = getFirstResult(mvcResult, assignmentType);
		ShipmentGroupDTO shipmentGroupResult = result.getShipmentGroup();
		ShipmentDTO shipmentResult = shipmentGroupResult.getShipments().iterator().next();
		assertThat(shipmentResult, hasProperty("name", is(shipmentDTO.getName())));

		assertThat(shipmentResult, hasProperty("trackingNumber", is(shipmentDTO.getTrackingNumber())));
		assertThat(shipmentResult, hasProperty("shippingProvider", is(shipmentDTO.getShippingProvider())));
		assertThat(shipmentGroupResult, hasProperty("returnShipment", is(shipmentGroupDTO.isReturnShipment())));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("id", is(not(nullValue())))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("number", is(shipmentGroupDTO.getShipToAddress().getNumber()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("name", is(shipmentGroupDTO.getShipToAddress().getName()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("addressLine1", is(shipmentGroupDTO.getShipToAddress().getAddressLine1()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("addressLine2", is(shipmentGroupDTO.getShipToAddress().getAddressLine2()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("city", is(shipmentGroupDTO.getShipToAddress().getCity()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("state", is(shipmentGroupDTO.getShipToAddress().getState()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("zip", is(shipmentGroupDTO.getShipToAddress().getZip()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("country", is(shipmentGroupDTO.getShipToAddress().getCountry()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("longitude", is(shipmentGroupDTO.getShipToAddress().getLongitude()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("latitude", is(shipmentGroupDTO.getShipToAddress().getLatitude()))));
	}

	@Test
	public void getAssignmentDraftWithSurveys() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);

		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT + "/" + assignmentDTO.getId())
		).andExpect(status().isOk()).andReturn();

		AssignmentDTO result = getFirstResult(mvcResult, assignmentType);

		assertThat(result, hasProperty("surveys", samePropertyValuesAs(assignmentDTO.getSurveys())));
	}

	@Test
	public void getAssignmentDraftWithDeliverables() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);

		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT + "/" + assignmentDTO.getId())
		).andExpect(status().isOk()).andReturn();

		AssignmentDTO result = getFirstResult(mvcResult, assignmentType);

		assertThat(result, hasProperty("deliverablesGroup", hasProperty("id", not(nullValue()))));
		assertThat(result, hasProperty("deliverablesGroup", hasProperty("instructions", is(assignmentDTO.getDeliverablesGroup().getInstructions()))));
		assertThat(result, hasProperty("deliverablesGroup", hasProperty("hoursToComplete", is(assignmentDTO.getDeliverablesGroup().getHoursToComplete()))));
		assertThat(result, hasProperty("deliverablesGroup", hasProperty("deliverables", not(empty()))));
	}

	@Test
	public void getAssignmentDraftWithConfiguration() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);

		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT + "/" + assignmentDTO.getId())
		).andExpect(status().isOk()).andReturn();

		AssignmentDTO result = getFirstResult(mvcResult, assignmentType);

		assertThat(result, hasProperty("configuration", samePropertyValuesAs(assignmentDTO.getConfiguration())));
	}

	@Test
	public void getAssignmentDraftWithDocuments() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);

		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT + "/" + assignmentDTO.getId())
		).andExpect(status().isOk()).andReturn();

		AssignmentDTO result = getFirstResult(mvcResult, assignmentType);

		assertThat(result, hasProperty("documents", samePropertyValuesAs(assignmentDTO.getDocuments())));
	}

	@Test
	public void postValidAssignmentDraftWithTheWorks() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		String assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		AssignmentDTO result = getFirstResult(mvcResult, assignmentType);

		assertThat(result, hasProperty("id", is(not(nullValue()))));
		assertThat(result, hasProperty("title", is(assignmentDTO.getTitle())));
		assertThat(result, hasProperty("description", is(assignmentDTO.getDescription())));
		assertThat(result, hasProperty("instructions", is(assignmentDTO.getInstructions())));
		assertThat(result, hasProperty("skills", is(assignmentDTO.getSkills())));
		assertThat(result, hasProperty("industryId", is(assignmentDTO.getIndustryId())));
		assertThat(result, hasProperty("supportContactId", is(assignmentDTO.getSupportContactId())));
		assertThat(result, hasProperty("location", is(not(nullValue()))));
		assertThat(result, hasProperty("schedule", samePropertyValuesAs(assignmentDTO.getSchedule())));
		assertThat(result, hasProperty("pricing", samePropertyValuesAs(assignmentDTO.getPricing())));
		assertThat(result, hasProperty("routing", is(not(nullValue()))));
		assertThat(result, hasProperty("customFieldGroups", samePropertyValuesAs(assignmentDTO.getCustomFieldGroups())));
		assertThat(result, hasProperty("surveys", samePropertyValuesAs(assignmentDTO.getSurveys())));
		assertThat(result, hasProperty("documents", samePropertyValuesAs(assignmentDTO.getDocuments())));
		assertThat(result, hasProperty("deliverablesGroup", is(not(nullValue()))));
		assertThat(result, hasProperty("configuration", samePropertyValuesAs(assignmentDTO.getConfiguration())));
		assertThat(result.getLocation(), hasProperty("contact", is(not(nullValue()))));
		assertThat(result.getLocation(), hasProperty("secondaryContact", is(not(nullValue()))));
	}

	@Test
	public void postValidAssignmentDraftWithLocation() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		String assignmentJson = jackson.writeValueAsString(assignmentDTO);
		LocationDTO locationDTO = assignmentDTO.getLocation();

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		AssignmentDTO assignmentDTOResult = getFirstResult(mvcResult, assignmentType);
		LocationDTO locationResult = assignmentDTOResult.getLocation();

		assertThat(locationResult, hasProperty("id", is(not(nullValue()))));
		assertThat(locationResult, hasProperty("number", is(locationDTO.getNumber())));
		assertThat(locationResult, hasProperty("name", is(locationDTO.getName())));
		assertThat(locationResult, hasProperty("addressLine1", is(locationDTO.getAddressLine1())));
		assertThat(locationResult, hasProperty("addressLine2", is(locationDTO.getAddressLine2())));
		assertThat(locationResult, hasProperty("city", is(locationDTO.getCity())));
		assertThat(locationResult, hasProperty("state", is(locationDTO.getState())));
		assertThat(locationResult, hasProperty("zip", is(locationDTO.getZip())));
		assertThat(locationResult, hasProperty("country", is(locationDTO.getCountry())));
		assertThat(locationResult, hasProperty("longitude", is(locationDTO.getLongitude())));
		assertThat(locationResult, hasProperty("latitude", is(locationDTO.getLatitude())));

		Work work = workService.findWorkByWorkNumber(assignmentDTOResult.getId(), false);
		String savedTimezone = invariantDataService.getPostalCodeByCode(locationResult.getZip()).getTimeZone().getTimeZoneId();
		assertThat(savedTimezone, is(work.getTimeZone().getTimeZoneId()));
	}

	@Test
	public void postValidAssignmentDraftWithSchedule() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		String assignmentJson = jackson.writeValueAsString(assignmentDTO);
		ScheduleDTO scheduleDTO = assignmentDTO.getSchedule();

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		ScheduleDTO result = getFirstResult(mvcResult, assignmentType).getSchedule();

		assertThat(result, hasProperty("from", is(scheduleDTO.getFrom())));
		assertThat(result, hasProperty("through", is(scheduleDTO.getThrough())));
		assertThat(result, hasProperty("range", is(true)));
	}

	@Test
	public void postValidAssignmentDraftWithGroupRouting() throws Exception {
		RoutingDTO.Builder builder = new RoutingDTO.Builder(
			make(a(RoutingDTO, with(groupIds, ImmutableSet.of(group.getId())))));

		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(routing, builder)));

		String assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		RoutingDTO result = getFirstResult(mvcResult, assignmentType).getRouting();

		assertThat(result, hasProperty("groupIds", contains(group.getId())));
		assertThat(result, hasProperty("resourceNumbers", is(empty())));
		assertThat(result, hasProperty("assignToFirstToAccept", is(false)));
	}

	@Test
	public void postValidAssignmentDraftWithWorkerRouting() throws Exception {
		RoutingDTO.Builder builder = new RoutingDTO.Builder(
			make(a(RoutingDTO, with(resourceNumbers, ImmutableSet.of(worker.getUserNumber())))));

		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(routing, builder)));

		String assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		RoutingDTO result = getFirstResult(mvcResult, assignmentType).getRouting();

		assertThat(result, hasProperty("groupIds", is(empty())));
		assertThat(result, hasProperty("resourceNumbers", contains(worker.getUserNumber())));
		assertThat(result, hasProperty("assignToFirstToAccept", is(false)));
	}

	@Test
	public void postValidAssignmentDraftWithCustomFieldGroup() throws Exception {
		CustomFieldGroupDTO.Builder builder =
			new CustomFieldGroupDTO.Builder(
				make(a(CustomFieldGroupDTO,
					with(customFieldGroupId, customFieldGroup.getId()),
					with(field, new CustomFieldDTO.Builder(
						make(a(CustomFieldDTO, with(customFieldId, customField.getId())))
					)))));

		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(AssignmentMaker.customFieldGroup, builder)));

		String assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		Set<CustomFieldGroupDTO> results = getFirstResult(mvcResult, assignmentType).getCustomFieldGroups();

		for (CustomFieldGroupDTO customFieldGroupDTO : assignmentDTO.getCustomFieldGroups()) {
			assertThat(results, contains(hasProperty("id", is(customFieldGroup.getId()))));
			assertThat(results, contains(hasProperty("name", is(customFieldGroup.getName()))));
			assertThat(results, contains(hasProperty("position", is(customFieldGroupDTO.getPosition()))));
			assertThat(results, contains(hasProperty("required", is(customFieldGroupDTO.isRequired()))));

			for (CustomFieldDTO customFieldDTO : customFieldGroupDTO.getFields()) {
				assertThat(results, contains(hasProperty("fields", hasItem(hasProperty("id", is(customField.getId()))))));
				assertThat(results, contains(hasProperty("fields", hasItem(hasProperty("name", is(customField.getName()))))));
			}
		}
	}

	@Test
	public void postValidAssignmentDraftWithShipment() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		ShipmentGroupDTO shipmentGroupDTO = make(a(ShipmentGroupDTO));
		ShipmentDTO shipmentDTO = shipmentGroupDTO.getShipments().iterator().next();
		String assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		ShipmentGroupDTO shipmentGroupResult = getFirstResult(mvcResult, assignmentType).getShipmentGroup();
		ShipmentDTO shipmentResult = shipmentGroupResult.getShipments().iterator().next();

		assertThat(shipmentResult, hasProperty("name", is(shipmentDTO.getName())));
		assertThat(shipmentResult, hasProperty("trackingNumber", is(shipmentDTO.getTrackingNumber())));
		assertThat(shipmentResult, hasProperty("shippingProvider", is(shipmentDTO.getShippingProvider())));
		assertThat(shipmentGroupResult, hasProperty("returnShipment", is(shipmentGroupDTO.isReturnShipment())));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("id", is(not(nullValue())))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("number", is(shipmentGroupDTO.getShipToAddress().getNumber()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("name", is(shipmentGroupDTO.getShipToAddress().getName()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("addressLine1", is(shipmentGroupDTO.getShipToAddress().getAddressLine1()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("addressLine2", is(shipmentGroupDTO.getShipToAddress().getAddressLine2()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("city", is(shipmentGroupDTO.getShipToAddress().getCity()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("state", is(shipmentGroupDTO.getShipToAddress().getState()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("zip", is(shipmentGroupDTO.getShipToAddress().getZip()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("country", is(shipmentGroupDTO.getShipToAddress().getCountry()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("longitude", is(shipmentGroupDTO.getShipToAddress().getLongitude()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("latitude", is(shipmentGroupDTO.getShipToAddress().getLatitude()))));
	}

	@Test
	public void postValidAssignmentDraftWithShipmentToWorker() throws Exception {

		ShipmentGroupDTO.Builder shipmentGroupBuilder = new ShipmentGroupDTO.Builder(make(a(ShipmentGroupDTO,
			with(shippingDestinationType, ShippingDestinationType.WORKER),
			withNull(shippingAddress))));
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(shipmentGroup, shipmentGroupBuilder)));
		ShipmentGroupDTO shipmentGroupDTO = shipmentGroupBuilder.build();
		ShipmentDTO shipmentDTO = shipmentGroupDTO.getShipments().iterator().next();
		String assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		ShipmentGroupDTO shipmentGroupResult = getFirstResult(mvcResult, assignmentType).getShipmentGroup();
		ShipmentDTO shipmentResult = shipmentGroupResult.getShipments().iterator().next();

		assertThat(shipmentResult, hasProperty("name", is(shipmentDTO.getName())));
		assertThat(shipmentResult, hasProperty("trackingNumber", is(shipmentDTO.getTrackingNumber())));
		assertThat(shipmentResult, hasProperty("shippingProvider", is(shipmentDTO.getShippingProvider())));
		assertThat(shipmentGroupResult, hasProperty("returnShipment", is(shipmentGroupDTO.isReturnShipment())));
		assertThat(shipmentGroupResult, hasProperty("shippingDestinationType", is(shipmentGroupDTO.getShippingDestinationType())));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", is(nullValue())));
	}

	@Test
	public void postValidAssignmentDraftWithShipmentToOnsite() throws Exception {

		ShipmentGroupDTO.Builder shipmentGroupBuilder = new ShipmentGroupDTO.Builder(make(a(ShipmentGroupDTO,
			with(shippingDestinationType, ShippingDestinationType.ONSITE),
			withNull(shippingAddress))));
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(shipmentGroup, shipmentGroupBuilder)));
		ShipmentGroupDTO shipmentGroupDTO = shipmentGroupBuilder.build();
		ShipmentDTO shipmentDTO = shipmentGroupDTO.getShipments().iterator().next();
		String assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		ShipmentGroupDTO shipmentGroupResult = getFirstResult(mvcResult, assignmentType).getShipmentGroup();
		ShipmentDTO shipmentResult = shipmentGroupResult.getShipments().iterator().next();
		LocationDTO locationDTO = assignmentDTO.getLocation();

		assertThat(shipmentResult, hasProperty("name", is(shipmentDTO.getName())));
		assertThat(shipmentResult, hasProperty("trackingNumber", is(shipmentDTO.getTrackingNumber())));
		assertThat(shipmentResult, hasProperty("shippingProvider", is(shipmentDTO.getShippingProvider())));
		assertThat(shipmentGroupResult, hasProperty("returnShipment", is(shipmentGroupDTO.isReturnShipment())));
		assertThat(shipmentGroupResult, hasProperty("shippingDestinationType", is(shipmentGroupDTO.getShippingDestinationType())));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("id", is(not(nullValue())))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("number", is(locationDTO.getNumber()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("name", is(locationDTO.getName()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("addressLine1", is(locationDTO.getAddressLine1()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("addressLine2", is(locationDTO.getAddressLine2()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("city", is(locationDTO.getCity()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("state", is(locationDTO.getState()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("zip", is(locationDTO.getZip()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("country", is(locationDTO.getCountry()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("longitude", is(locationDTO.getLongitude()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("latitude", is(locationDTO.getLatitude()))));
	}

	@Test
	public void postValidAssignmentDraftWithShipmentToOnsiteWithWorkerReturnsParts() throws Exception {

		LocationDTO.Builder returnAddressDTOBuilder =  new LocationDTO.Builder(make(an(LocationDTO, with(name, "returnTo"))));
		ShipmentGroupDTO.Builder shipmentGroupBuilder = new ShipmentGroupDTO.Builder(make(a(ShipmentGroupDTO,
			with(shippingDestinationType, ShippingDestinationType.ONSITE),
			with(returnShipment, true),
			withNull(shippingAddress),
			with(returnAddress, returnAddressDTOBuilder))));
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(shipmentGroup, shipmentGroupBuilder)));
		ShipmentGroupDTO shipmentGroupDTO = shipmentGroupBuilder.build();
		ShipmentDTO shipmentDTO = shipmentGroupDTO.getShipments().iterator().next();
		String assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		ShipmentGroupDTO shipmentGroupResult = getFirstResult(mvcResult, assignmentType).getShipmentGroup();
		ShipmentDTO shipmentResult = shipmentGroupResult.getShipments().iterator().next();
		LocationDTO shipToAddress = assignmentDTO.getLocation();
		LocationDTO returnAddress = returnAddressDTOBuilder.build();

		assertThat(shipmentResult, hasProperty("name", is(shipmentDTO.getName())));
		assertThat(shipmentResult, hasProperty("trackingNumber", is(shipmentDTO.getTrackingNumber())));
		assertThat(shipmentResult, hasProperty("shippingProvider", is(shipmentDTO.getShippingProvider())));
		assertThat(shipmentGroupResult, hasProperty("returnShipment", is(shipmentGroupDTO.isReturnShipment())));
		assertThat(shipmentGroupResult, hasProperty("shippingDestinationType", is(shipmentGroupDTO.getShippingDestinationType())));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("id", is(not(nullValue())))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("number", is(shipToAddress.getNumber()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("name", is(shipToAddress.getName()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("addressLine1", is(shipToAddress.getAddressLine1()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("addressLine2", is(shipToAddress.getAddressLine2()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("city", is(shipToAddress.getCity()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("state", is(shipToAddress.getState()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("zip", is(shipToAddress.getZip()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("country", is(shipToAddress.getCountry()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("longitude", is(shipToAddress.getLongitude()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("latitude", is(shipToAddress.getLatitude()))));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", hasProperty("id", is(not(nullValue())))));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", hasProperty("number", is(returnAddress.getNumber()))));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", hasProperty("name", is(returnAddress.getName()))));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", hasProperty("addressLine1", is(returnAddress.getAddressLine1()))));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", hasProperty("addressLine2", is(returnAddress.getAddressLine2()))));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", hasProperty("city", is(returnAddress.getCity()))));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", hasProperty("state", is(returnAddress.getState()))));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", hasProperty("zip", is(returnAddress.getZip()))));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", hasProperty("country", is(returnAddress.getCountry()))));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", hasProperty("longitude", is(returnAddress.getLongitude()))));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", hasProperty("latitude", is(returnAddress.getLatitude()))));
	}


	@Test
	public void postValidAssignmentDraftWithShipmentSuppliedByWorker() throws Exception {

		ShipmentGroupDTO.Builder shipmentGroupBuilder = new ShipmentGroupDTO.Builder(make(a(ShipmentGroupDTO,
			with(shippingDestinationType, ShippingDestinationType.NONE),
			with(suppliedByWorker, true),
			withNull(shippingAddress),
			withNull(returnAddress),
			with(shipments, Lists.<ShipmentDTO.Builder>newArrayList())
		)));
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(shipmentGroup, shipmentGroupBuilder)));
		ShipmentGroupDTO shipmentGroupDTO = shipmentGroupBuilder.build();
		String assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		ShipmentGroupDTO shipmentGroupResult = getFirstResult(mvcResult, assignmentType).getShipmentGroup();
		List<ShipmentDTO> shipmentsResult = shipmentGroupResult.getShipments();

		assertThat(shipmentsResult, is(empty()));
		assertThat(shipmentGroupResult, hasProperty("suppliedByWorker", is(shipmentGroupDTO.isSuppliedByWorker())));
		assertThat(shipmentGroupResult, hasProperty("returnShipment", is(shipmentGroupDTO.isReturnShipment())));
		assertThat(shipmentGroupResult, hasProperty("shippingDestinationType", is(shipmentGroupDTO.getShippingDestinationType())));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", is(nullValue())));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", is(nullValue())));
	}


	@Test
	public void postValidAssignmentDraftWithShipmentSuppliedByWorkerAndWorkerReturnParts() throws Exception {

		LocationDTO.Builder returnAddressDTOBuilder =  new LocationDTO.Builder(make(an(LocationDTO, with(name, "return"))));
		ShipmentGroupDTO.Builder shipmentGroupBuilder = new ShipmentGroupDTO.Builder(make(a(ShipmentGroupDTO,
			with(shippingDestinationType, ShippingDestinationType.NONE),
			with(suppliedByWorker, true),
			with(returnShipment, true),
			withNull(shippingAddress),
			with(returnAddress, returnAddressDTOBuilder))));
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(shipmentGroup, shipmentGroupBuilder)));
		ShipmentGroupDTO shipmentGroupDTO = shipmentGroupBuilder.build();
		ShipmentDTO shipmentDTO = shipmentGroupDTO.getShipments().iterator().next();
		String assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		ShipmentGroupDTO shipmentGroupResult = getFirstResult(mvcResult, assignmentType).getShipmentGroup();
		ShipmentDTO shipmentResult = shipmentGroupResult.getShipments().iterator().next();
		LocationDTO returnAddress = returnAddressDTOBuilder.build();

		assertThat(shipmentResult, hasProperty("name", is(shipmentDTO.getName())));
		assertThat(shipmentResult, hasProperty("trackingNumber", is(shipmentDTO.getTrackingNumber())));
		assertThat(shipmentResult, hasProperty("shippingProvider", is(shipmentDTO.getShippingProvider())));
		assertThat(shipmentGroupResult, hasProperty("suppliedByWorker", is(shipmentGroupDTO.isSuppliedByWorker())));
		assertThat(shipmentGroupResult, hasProperty("returnShipment", is(shipmentGroupDTO.isReturnShipment())));
		assertThat(shipmentGroupResult, hasProperty("shippingDestinationType", is(shipmentGroupDTO.getShippingDestinationType())));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", is((nullValue()))));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", hasProperty("id", is(not(nullValue())))));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", hasProperty("number", is(returnAddress.getNumber()))));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", hasProperty("name", is(returnAddress.getName()))));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", hasProperty("addressLine1", is(returnAddress.getAddressLine1()))));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", hasProperty("addressLine2", is(returnAddress.getAddressLine2()))));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", hasProperty("city", is(returnAddress.getCity()))));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", hasProperty("state", is(returnAddress.getState()))));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", hasProperty("zip", is(returnAddress.getZip()))));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", hasProperty("country", is(returnAddress.getCountry()))));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", hasProperty("longitude", is(returnAddress.getLongitude()))));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", hasProperty("latitude", is(returnAddress.getLatitude()))));
	}

	@Test
	public void postAssignmentDraftWithdWorkerReturnPartsOnWithEmptyReturningAddress() throws Exception {

		LocationDTO.Builder returnAddressDTOBuilder =  new LocationDTO.Builder(make(a(LocationDTO,
			with(addressLine1, ""),
			with(addressLine2, ""),
			with(city, ""),
			with(state, ""),
			with(country, ""),
			with(zip, "")
		)));
		ShipmentGroupDTO.Builder shipmentGroupBuilder = new ShipmentGroupDTO.Builder(make(a(ShipmentGroupDTO,
			with(shippingDestinationType, ShippingDestinationType.WORKER),
			with(suppliedByWorker, false),
			with(returnShipment, true),
			withNull(shippingAddress),
			with(returnAddress, returnAddressDTOBuilder))));
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(shipmentGroup, shipmentGroupBuilder)));
		ShipmentGroupDTO shipmentGroupDTO = shipmentGroupBuilder.build();
		ShipmentDTO shipmentDTO = shipmentGroupDTO.getShipments().iterator().next();
		String assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(assignmentJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		assertThat(result, hasProperty("message", is("Parts return location is missing.")));
	}

	@Test
	public void postValidAssignmentWithShipmentRequiredFalse() throws Exception {
		ConfigurationDTO.Builder configurationDTOBuilder = new ConfigurationDTO.Builder(make(an(ConfigurationDTO, with(shipmentsEnabled, false))));

		ShipmentGroupDTO.Builder shipmentGroupBuilder = new ShipmentGroupDTO.Builder(make(a(ShipmentGroupMaker.ShipmentGroupDTO,
				with(shippingDestinationType, ShippingDestinationType.NONE),
				with(suppliedByWorker, true),
				withNull(shippingAddress),
				withNull(returnAddress),
				with(shipments, Lists.<ShipmentDTO.Builder>newArrayList())
		)));
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO,
				with(configuration, configurationDTOBuilder),
				with(shipmentGroup, shipmentGroupBuilder)));

		String assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT).content(assignmentJson))
			.andExpect(status().isOk())
			.andReturn();

		ShipmentGroupDTO shipmentGroupResult = getFirstResult(mvcResult, assignmentType).getShipmentGroup();
		List<ShipmentDTO> shipmentResult = shipmentGroupResult.getShipments();

		assertThat(shipmentResult, is(empty()));
		assertThat(shipmentGroupResult, hasProperty("uuid", is(nullValue())));
		assertThat(shipmentGroupResult, hasProperty("returnShipment", is(false)));
		assertThat(shipmentGroupResult, hasProperty("suppliedByWorker", is(false)));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", is(nullValue())));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", is(nullValue())));
	}

	@Test
	public void postUpdateValidAssignmentWithShipmentRequiredFalse() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		ShipmentGroupDTO shipmentGroupDTO = assignmentDTO.getShipmentGroup();
		ShipmentDTO shipmentDTO = shipmentGroupDTO.getShipments().iterator().next();

		String assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT).content(assignmentJson))
			.andExpect(status().isOk())
			.andReturn();

		AssignmentDTO assignmentDTOResult = getFirstResult(mvcResult, assignmentType);
		ShipmentGroupDTO shipmentGroupResult = assignmentDTOResult.getShipmentGroup();
		ShipmentDTO shipmentResult = shipmentGroupResult.getShipments().iterator().next();

		assertThat(shipmentResult, hasProperty("name", is(shipmentDTO.getName())));
		assertThat(shipmentResult, hasProperty("trackingNumber", is(shipmentDTO.getTrackingNumber())));
		assertThat(shipmentGroupResult, hasProperty("returnShipment", is(assignmentDTO.getShipmentGroup().isReturnShipment())));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("id", is(not(nullValue())))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("number", is(shipmentGroupDTO.getShipToAddress().getNumber()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("name", is(shipmentGroupDTO.getShipToAddress().getName()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("addressLine1", is(shipmentGroupDTO.getShipToAddress().getAddressLine1()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("addressLine2", is(shipmentGroupDTO.getShipToAddress().getAddressLine2()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("city", is(shipmentGroupDTO.getShipToAddress().getCity()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("state", is(shipmentGroupDTO.getShipToAddress().getState()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("zip", is(shipmentGroupDTO.getShipToAddress().getZip()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("country", is(shipmentGroupDTO.getShipToAddress().getCountry()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("longitude", is(shipmentGroupDTO.getShipToAddress().getLongitude()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("latitude", is(shipmentGroupDTO.getShipToAddress().getLatitude()))));

		ConfigurationDTO.Builder configurationDTO = new ConfigurationDTO.Builder(make(an(ConfigurationDTO, with(shipmentsEnabled,false))));

		AssignmentDTO updateAssignmentDTO = new AssignmentDTO.Builder(assignmentDTOResult)
			.setConfiguration(configurationDTO).build();

		assignmentJson = jackson.writeValueAsString(updateAssignmentDTO);
		mvcResult = mockMvc.perform(doPost(ENDPOINT + "/" + assignmentDTOResult.getId()).content(assignmentJson))
			.andExpect(status().isOk())
			.andReturn();

		assignmentDTOResult = getFirstResult(mvcResult, assignmentType);
		shipmentGroupResult = assignmentDTOResult.getShipmentGroup();
		List<ShipmentDTO> shipmentsResult = shipmentGroupResult.getShipments();
		ConfigurationDTO updatedConfigurationResult = assignmentDTOResult.getConfiguration();

		assertThat(shipmentsResult, is(empty()));
		assertThat(updatedConfigurationResult, samePropertyValuesAs(configurationDTO.build()));
		assertThat(shipmentGroupResult, hasProperty("uuid", is(nullValue())));
		assertThat(shipmentGroupResult, hasProperty("returnShipment", is(false)));
		assertThat(shipmentGroupResult, hasProperty("suppliedByWorker", is(false)));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", is(nullValue())));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", is(nullValue())));
	}

	@Test
	public void postValidAssignmentDraftWithSurvey() throws Exception {
		SurveyDTO.Builder builder = new SurveyDTO.Builder(
			make(a(SurveyDTO,
				with(surveyId, assessment.getId())
			)));

		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(survey, builder)));

		final FilterProvider filters = new SimpleFilterProvider().addFilter(
			ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS,
			new ApiBaseHttpMessageConverter.APIProjectionsFilter(Collections.EMPTY_SET)
		);

		String assignmentJson = jackson.writer(filters).writeValueAsString(assignmentDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		Set<SurveyDTO> results = getFirstResult(mvcResult, assignmentType).getSurveys();

		for (SurveyDTO surveyDTO : assignmentDTO.getSurveys()) {
			assertThat(results, contains(hasProperty("id", is(surveyDTO.getId()))));
			assertThat(results, contains(hasProperty("required", is(surveyDTO.getRequired()))));
		}
	}

	@Test
	public void postValidAssignmentDraftWithDeliverables() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		String assignmentJson = jackson.writeValueAsString(assignmentDTO);
		DeliverablesGroupDTO deliverablesGroupDTO = assignmentDTO.getDeliverablesGroup();
		DeliverableDTO deliverableDTO = deliverablesGroupDTO.getDeliverables().iterator().next();

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		DeliverablesGroupDTO result = getFirstResult(mvcResult, assignmentType).getDeliverablesGroup();

		assertThat(result, hasProperty("id"));
		assertThat(result, hasProperty("instructions", is(deliverablesGroupDTO.getInstructions())));
		assertThat(result, hasProperty("hoursToComplete", is(deliverablesGroupDTO.getHoursToComplete())));

		assertThat(result, hasProperty("deliverables", hasItem(hasProperty("id"))));
		assertThat(result, hasProperty("deliverables", hasItem(hasProperty("type", is(deliverableDTO.getType())))));
		assertThat(result, hasProperty("deliverables", hasItem(hasProperty("description", is(deliverableDTO.getDescription())))));
		assertThat(result, hasProperty("deliverables", hasItem(hasProperty("numberOfFiles", is(deliverableDTO.getNumberOfFiles())))));
		assertThat(result, hasProperty("deliverables", hasItem(hasProperty("priority", is(deliverableDTO.getPriority())))));
	}

	@Test
	public void postValidAssignmentDraftWithConfiguration() throws Exception {
		ConfigurationDTO.Builder builder = new ConfigurationDTO.Builder(
			make(a(ConfigurationDTO,
				with(customFieldsEnabled, false),
				with(deliverablesEnabled, false),
				with(requirementSetsEnabled, false),
				with(surveysEnabled, false),
				with(shipmentsEnabled, false)
			)));

		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(configuration, builder)));

		String assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		com.workmarket.api.v2.employer.assignments.models.ConfigurationDTO result = getFirstResult(mvcResult, assignmentType).getConfiguration();

		assertThat(result, hasProperty("customFieldsEnabled", is(false)));
		assertThat(result, hasProperty("shipmentsEnabled", is(false)));
		assertThat(result, hasProperty("requirementSetsEnabled", is(false)));
		assertThat(result, hasProperty("deliverablesEnabled", is(false)));
		assertThat(result, hasProperty("surveysEnabled", is(false)));
	}

	@Test
	public void postValidAssignmentDraftWithDocument() throws Exception {
		DocumentDTO.Builder builder = new DocumentDTO.Builder(
			make(a(DocumentMaker.DocumentDTO,
				with(documentId, asset.getId())
			)));

		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(document, builder)));

		String assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		Set<DocumentDTO> results = getFirstResult(mvcResult, assignmentType).getDocuments();

		for (DocumentDTO documentDTO : assignmentDTO.getDocuments()) {
			assertThat(results, contains(hasProperty("id", is(asset.getId()))));
			assertThat(results, contains(hasProperty("uuid", is(asset.getUUID()))));
			assertThat(results, contains(hasProperty("name", is(asset.getName()))));
			assertThat(results, contains(hasProperty("description", is(asset.getDescription()))));
			assertThat(results, contains(hasProperty("uploaded", is(documentDTO.isUploaded()))));
			assertThat(results, contains(hasProperty("visibilityType", is(documentDTO.getVisibilityType()))));
		}
	}

	@Test
	public void postInvalidAssignmentDraft() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, withNull(title)));
		String assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(assignmentJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);

		expectApiErrorMessage(result, "title", "Title is a required field.");
		assertThat(result, hasProperty("resource", is("work")));
	}

	@Test
	public void postValidAssignmentDraftWithInvalidScheduleIsOk() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		Date originalFromDate = DateUtilities.getDateFromString(DATE_TIME_FORMAT, assignmentDTO.getSchedule().getFrom());
		Calendar originalFrom = Calendar.getInstance();
		originalFrom.setTime(originalFromDate);
		long updatedThroughInMillis = originalFrom.getTimeInMillis() - DATE_TIME_OFFSET;
		String updatedThrough = DateUtilities.formatMillis(DATE_TIME_FORMAT, updatedThroughInMillis);
		ScheduleDTO.Builder builder = new ScheduleDTO.Builder(make(a(ScheduleDTO)))
			.setThrough(updatedThrough);

		assignmentDTO = make(an(AssignmentDTO, with(schedule, builder)));
		String expectedFrom = assignmentDTO.getSchedule().getFrom();
		String expectedThrough = assignmentDTO.getSchedule().getThrough(); // through is before from
		String assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		ScheduleDTO result = getFirstResult(mvcResult, assignmentType).getSchedule();

		assertThat(result, hasProperty("from", is(expectedFrom)));
		assertThat(result, hasProperty("through", is(expectedThrough)));
		assertThat(result, hasProperty("range", is(true)));
	}

	@Test
	public void postValidAssignmentDraftWithoutRequiredUniqueExternalId() throws Exception {

		enableUniqueExternalId();

		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, withNull(uniqueExternalId)));

		String assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		AssignmentDTO result = getFirstResult(mvcResult, assignmentType);

		assertThat(result, hasProperty("uniqueExternalId", is(assignmentDTO.getUniqueExternalId())));
	}

	@Test
	public void putUpdateValidAssignmentDraft() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		String assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult createResult = mockMvc.perform(
			doPost(ENDPOINT).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		assignmentDTO =
			new AssignmentDTO.Builder(getFirstResult(createResult, assignmentType)).setDescription("This is the update!").build();

		assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult updateResult = mockMvc.perform(
			doPut(ENDPOINT + "/" + assignmentDTO.getId()).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		AssignmentDTO updateResultDTO = getFirstResult(updateResult, assignmentType);

		assertThat(updateResultDTO, hasProperty("id"));
		assertThat(updateResultDTO, hasProperty("title", is(assignmentDTO.getTitle())));
		assertThat(updateResultDTO, hasProperty("description", is(assignmentDTO.getDescription())));
	}

	@Test
	public void putUpdateValidAssignmentDraftWithSchedule() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		String assignmentJson = jackson.writeValueAsString(assignmentDTO);
		ScheduleDTO scheduleDTO = assignmentDTO.getSchedule();

		MvcResult createResult = mockMvc.perform(
			doPost(ENDPOINT).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		long laterStartInMillis = DateTime.now()
			.withHourOfDay(10)
			.withMinuteOfHour(0)
			.withSecondOfMinute(0)
			.withMillisOfSecond(0)
			.getMillis();

		long laterThroughInMillis = laterStartInMillis + DATE_TIME_OFFSET;
		String laterStart = DateUtilities.formatMillis(DATE_TIME_FORMAT, laterStartInMillis);
		String laterThrough = DateUtilities.formatMillis(DATE_TIME_FORMAT, laterThroughInMillis);


		assignmentDTO = new AssignmentDTO.Builder(getFirstResult(createResult, assignmentType)).setSchedule(
			new ScheduleDTO.Builder()
				.setFrom(laterStart)
				.setThrough(laterThrough)
				.setRange(true)
		).build();

		assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult updateResult = mockMvc.perform(
			doPut(ENDPOINT + "/" + assignmentDTO.getId()).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		ScheduleDTO result = getFirstResult(updateResult, assignmentType).getSchedule();

		assertThat(scheduleDTO.getFrom(), not(is(laterStart)));
		assertThat(result, hasProperty("from", is(laterStart)));
		assertThat(result, hasProperty("through", is(laterThrough)));
		assertThat(result, hasProperty("range", is(true)));
	}

	@Test
	public void postUpdateValidAssignmentDraft() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		String assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult createResult = mockMvc.perform(
			doPost(ENDPOINT).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		assignmentDTO =
			new AssignmentDTO.Builder(getFirstResult(createResult, assignmentType)).setDescription("This is the update!").build();

		assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult updateResult = mockMvc.perform(
			doPost(ENDPOINT + "/" + assignmentDTO.getId()).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		AssignmentDTO updateResultDTO = getFirstResult(updateResult, assignmentType);

		assertThat(updateResultDTO, hasProperty("id"));
		assertThat(updateResultDTO, hasProperty("title", is(assignmentDTO.getTitle())));
		assertThat(updateResultDTO, hasProperty("description", is(assignmentDTO.getDescription())));
	}

	@Test
	public void putUpdateInvalidAssignmentDraft() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		String assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult createResult = mockMvc.perform(
			doPost(ENDPOINT).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		assignmentDTO =
			new AssignmentDTO.Builder(getFirstResult(createResult, assignmentType)).setTitle(null).build();

		assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult updateResult = mockMvc.perform(doPut(ENDPOINT + "/" + assignmentDTO.getId()).content(assignmentJson))
						.andExpect(status().isBadRequest())
						.andReturn();

		ApiBaseError result = getFirstResult(updateResult, errorType);

		expectApiErrorMessage(result, "title", "Title is a required field.");
		assertThat(result, hasProperty("resource", is("work")));
	}

	@Test
	public void postUpdateInvalidAssignmentDraft() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		String assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult createResult = mockMvc.perform(
			doPost(ENDPOINT).content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		assignmentDTO =
			new AssignmentDTO.Builder(getFirstResult(createResult, assignmentType)).setTitle(null).build();

		assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult updateResult = mockMvc.perform(
			doPost(ENDPOINT + "/" + assignmentDTO.getId()).content(assignmentJson)
		).andExpect(status().isBadRequest()).andReturn();

		ApiBaseError result = getFirstResult(updateResult, errorType);

		expectApiErrorMessage(result, "title", "Title is a required field.");
		assertThat(result, hasProperty("resource", is("work")));
	}

	@Test
	public void getAssignmentScheduleDraft() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);
		ScheduleDTO scheduleDTO = assignmentDTO.getSchedule();

		MvcResult updateResult = mockMvc.perform(
			doGet(ENDPOINT + "/" + assignmentDTO.getId() + "/schedule")
		).andExpect(status().isOk()).andReturn();

		ScheduleDTO result = getFirstResult(updateResult, scheduleType);

		assertThat(result, hasProperty("from", is(scheduleDTO.getFrom())));
		assertThat(result, hasProperty("through", is(scheduleDTO.getThrough())));
		assertThat(result, hasProperty("range", is(scheduleDTO.isRange())));
	}

	@Test
	public void postUpdateAssignmentDraftSchedule() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);

		Date originalFromDate = DateUtilities.getDateFromString(DATE_TIME_FORMAT, assignmentDTO.getSchedule().getFrom());
		Calendar originalFromCal = Calendar.getInstance();
		originalFromCal.setTime(originalFromDate);
		String originalFrom  = DateUtilities.format(DATE_TIME_FORMAT, originalFromCal);

		long laterStartInMillis = DateTime.now()
			.withHourOfDay(10)
			.withMinuteOfHour(0)
			.withSecondOfMinute(0)
			.withMillisOfSecond(0)
			.getMillis();

		long laterThroughInMillis = laterStartInMillis + DATE_TIME_OFFSET;
		String laterStart = DateUtilities.formatMillis(DATE_TIME_FORMAT, laterStartInMillis);
		String laterThrough = DateUtilities.formatMillis(DATE_TIME_FORMAT, laterThroughInMillis);


		ScheduleDTO scheduleDTO = make(a(ScheduleDTO,
			with(from, laterStart),
			with(through, laterThrough)
		));

		String scheduleJson = jackson.writeValueAsString(scheduleDTO);
		MvcResult updateResult = mockMvc.perform(
			doPost(ENDPOINT + "/" + assignmentDTO.getId() + "/schedule").content(scheduleJson)
		).andExpect(status().isOk()).andReturn();

		ScheduleDTO result = getFirstResult(updateResult, scheduleType);

		assertThat(result, hasProperty("from", is(not(originalFrom))));
		assertThat(result, hasProperty("from", is(laterStart)));
		assertThat(result, hasProperty("through", is(laterThrough)));
		assertThat(result, hasProperty("range", is(scheduleDTO.isRange())));
	}

	@Test
	public void putUpdateAssignmentDraftSchedule() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);

		Date originalFromDate  = DateUtilities.getDateFromString(DATE_TIME_FORMAT, assignmentDTO.getSchedule().getFrom());
		Calendar originalFromCal = Calendar.getInstance();
		originalFromCal.setTime(originalFromDate);
		String originalFrom  = DateUtilities.format(DATE_TIME_FORMAT, originalFromCal);

		long laterStartInMillis = DateTime.now()
			.withHourOfDay(10)
			.withMinuteOfHour(0)
			.withSecondOfMinute(0)
			.withMillisOfSecond(0)
			.getMillis();

		long laterThroughInMillis = laterStartInMillis + DATE_TIME_OFFSET;
		String laterStart = DateUtilities.formatMillis(DATE_TIME_FORMAT, laterStartInMillis);
		String laterThrough = DateUtilities.formatMillis(DATE_TIME_FORMAT, laterThroughInMillis);

		ScheduleDTO scheduleDTO = make(a(ScheduleDTO,
			with(from, laterStart),
			with(through, laterThrough)
		));

		String scheduleJson = jackson.writeValueAsString(scheduleDTO);
		MvcResult updateResult = mockMvc.perform(
			doPut(ENDPOINT + "/" + assignmentDTO.getId() + "/schedule").content(scheduleJson)
		).andExpect(status().isOk()).andReturn();

		ScheduleDTO result = getFirstResult(updateResult, scheduleType);

		assertThat(result, hasProperty("from", is(not(originalFrom))));
		assertThat(result, hasProperty("from", is(laterStart)));
		assertThat(result, hasProperty("through", is(laterThrough)));
		assertThat(result, hasProperty("range", is(scheduleDTO.isRange())));
	}

	@Test
	public void getAssignmentDraftPricing() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);
		PricingDTO pricingDTO = assignmentDTO.getPricing();

		MvcResult updateResult = mockMvc.perform(
			doGet(ENDPOINT + "/" + assignmentDTO.getId() + "/pricing")
		).andExpect(status().isOk()).andReturn();

		PricingDTO result = getFirstResult(updateResult, pricingType);

		assertThat(result, hasProperty("mode", is(pricingDTO.getMode())));
		assertThat(result, hasProperty("type", is(pricingDTO.getType())));
		assertThat(result, hasProperty("flatPrice", is(pricingDTO.getFlatPrice())));
		assertThat(result, hasProperty("perHourPrice", is(pricingDTO.getPerHourPrice())));
		assertThat(result, hasProperty("maxNumberOfHours", is(pricingDTO.getMaxNumberOfHours())));
		assertThat(result, hasProperty("perUnitPrice", is(pricingDTO.getPerUnitPrice())));
		assertThat(result, hasProperty("maxNumberOfUnits", is(pricingDTO.getMaxNumberOfUnits())));
		assertThat(result, hasProperty("initialPerHourPrice", is(pricingDTO.getInitialPerHourPrice())));
		assertThat(result, hasProperty("initialNumberOfHours", is(pricingDTO.getInitialNumberOfHours())));
		assertThat(result, hasProperty("additionalPerHourPrice", is(pricingDTO.getAdditionalPerHourPrice())));
		assertThat(result, hasProperty("maxBlendedNumberOfHours", is(pricingDTO.getMaxBlendedNumberOfHours())));
	}

	@Test
	public void postUpdateAssignmentDraftPricing() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);

		Double originalFlatPrice = assignmentDTO.getPricing().getFlatPrice();
		PricingDTO pricingDTO = make(a(PricingDTO, (with(flatPrice, 175.00))));

		String pricingJson = jackson.writeValueAsString(pricingDTO);
		MvcResult updateResult = mockMvc.perform(
			doPost(ENDPOINT + "/" + assignmentDTO.getId() + "/pricing").content(pricingJson)
		).andExpect(status().isOk()).andReturn();

		PricingDTO result = getFirstResult(updateResult, pricingType);

		assertThat(result, hasProperty("flatPrice", not(originalFlatPrice)));
		assertThat(result, hasProperty("flatPrice", is(pricingDTO.getFlatPrice())));
	}

	@Test
	public void putUpdateAssignmentDraftPricing() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);

		Double originalFlatPrice = assignmentDTO.getPricing().getFlatPrice();
		PricingDTO pricingDTO = make(a(PricingDTO, (with(flatPrice, 175.00))));

		String pricingJson = jackson.writeValueAsString(pricingDTO);
		MvcResult updateResult = mockMvc.perform(
			doPut(ENDPOINT + "/" + assignmentDTO.getId() + "/pricing").content(pricingJson)
		).andExpect(status().isOk()).andReturn();

		PricingDTO result = getFirstResult(updateResult, pricingType);

		assertThat(result, hasProperty("flatPrice", not(originalFlatPrice)));
		assertThat(result, hasProperty("flatPrice", is(pricingDTO.getFlatPrice())));
	}

	@Test
	public void getAssignmentDraftLocation() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);
		LocationDTO locationDTO = assignmentDTO.getLocation();

		MvcResult updateResult = mockMvc.perform(
			doGet(ENDPOINT + "/" + assignmentDTO.getId() + "/location")
		).andExpect(status().isOk()).andReturn();

		LocationDTO result = getFirstResult(updateResult, locationType);

		assertThat(result, hasProperty("id", is(not(nullValue()))));
		assertThat(result, hasProperty("number", is(locationDTO.getNumber())));
		assertThat(result, hasProperty("name", is(locationDTO.getName())));
		assertThat(result, hasProperty("addressLine1", is(locationDTO.getAddressLine1())));
		assertThat(result, hasProperty("addressLine2", is(locationDTO.getAddressLine2())));
		assertThat(result, hasProperty("city", is(locationDTO.getCity())));
		assertThat(result, hasProperty("state", is(locationDTO.getState())));
		assertThat(result, hasProperty("zip", is(locationDTO.getZip())));
		assertThat(result, hasProperty("country", is(locationDTO.getCountry())));
		assertThat(result, hasProperty("longitude", is(locationDTO.getLongitude())));
		assertThat(result, hasProperty("latitude", is(locationDTO.getLatitude())));
	}

	@Test
	public void postUpdateAssignmentDraftLocation() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);

		String originalAddressLine2 = assignmentDTO.getLocation().getAddressLine2();
		LocationDTO locationDTO = make(a(LocationDTO, with(addressLine2, "5th Floor")));

		String locationJson = jackson.writeValueAsString(locationDTO);
		MvcResult updateResult = mockMvc.perform(
			doPost(ENDPOINT + "/" + assignmentDTO.getId() + "/location").content(locationJson)
		).andExpect(status().isOk()).andReturn();

		LocationDTO result = getFirstResult(updateResult, locationType);

		assertThat(result, hasProperty("addressLine2", not(originalAddressLine2)));
		assertThat(result, hasProperty("addressLine2", is(locationDTO.getAddressLine2())));
	}

	@Test
	public void putUpdateAssignmentDraftLocation() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);

		String originalAddressLine2 = assignmentDTO.getLocation().getAddressLine2();
		LocationDTO locationDTO = make(a(LocationDTO, with(addressLine2, "5th Floor")));

		String locationJson = jackson.writeValueAsString(locationDTO);
		MvcResult updateResult = mockMvc.perform(
			doPut(ENDPOINT + "/" + assignmentDTO.getId() + "/location").content(locationJson)
		).andExpect(status().isOk()).andReturn();

		LocationDTO result = getFirstResult(updateResult, locationType);

		assertThat(result, hasProperty("addressLine2", not(originalAddressLine2)));
		assertThat(result, hasProperty("addressLine2", is(locationDTO.getAddressLine2())));
	}

	@Test
	public void getAssignmentDraftRoutingWithGroups() throws Exception {
		RoutingDTO routingDTO = make(a(RoutingDTO, with(groupIds, Sets.newHashSet(group.getId()))));
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(routing, new RoutingDTO.Builder(routingDTO))));
		assignmentDTO = assignmentService.create(assignmentDTO, false);

		MvcResult updateResult = mockMvc.perform(
			doGet(ENDPOINT + "/" + assignmentDTO.getId() + "/routing")
		).andExpect(status().isOk()).andReturn();

		RoutingDTO result = getFirstResult(updateResult, routingType);

		// groupIds is empty because the assignment gets routed
		//   and userNumbers are listed instead
		assertThat(result, hasProperty("groupIds", contains(group.getId())));
		assertThat(result.getNeedToApplyCandidates(), hasProperty("groupIds", contains(group.getId())));
		assertThat(result, hasProperty("resourceNumbers", is(empty())));
		assertThat(result, hasProperty("assignToFirstToAccept", is(routingDTO.isAssignToFirstToAccept())));
	}

	@Test
	public void getAssignmentDraftRoutingWithWorkers() throws Exception {
		RoutingDTO routingDTO = make(a(RoutingDTO, with(resourceNumbers, ImmutableSet.of(worker.getUserNumber()))));
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(routing, new RoutingDTO.Builder(routingDTO))));
		assignmentDTO = assignmentService.create(assignmentDTO, false);

		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT + "/" + assignmentDTO.getId() + "/routing")
		).andExpect(status().isOk()).andReturn();

		RoutingDTO result = getFirstResult(mvcResult, routingType);

		assertThat(result, hasProperty("groupIds", is(routingDTO.getGroupIds())));
		assertThat(result, hasProperty("resourceNumbers", is(empty())));
		assertThat(result, hasProperty("assignToFirstToAccept", is(routingDTO.isAssignToFirstToAccept())));
	}

	@Test
	public void postUpdateAssignmentDraftRoutingWithGroups() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);

		assertThat(assignmentDTO.getRouting().getGroupIds(), is(empty()));
		RoutingDTO routingDTO = make(a(RoutingDTO, with(groupIds, ImmutableSet.of(group.getId()))));

		String routingJson = jackson.writeValueAsString(routingDTO);
		MvcResult updateResult = mockMvc.perform(
			doPost(ENDPOINT + "/" + assignmentDTO.getId() + "/routing").content(routingJson)
		).andExpect(status().isOk()).andReturn();

		RoutingDTO result = getFirstResult(updateResult, routingType);

		assertThat(result, hasProperty("groupIds", is(routingDTO.getGroupIds())));
		assertThat(result, hasProperty("resourceNumbers", is(routingDTO.getResourceNumbers())));
		assertThat(result, hasProperty("assignToFirstToAccept", is(routingDTO.isAssignToFirstToAccept())));
	}

	@Test
	public void postUpdateAssignmentDraftRoutingWithWorkers() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);

		assertThat(assignmentDTO.getRouting().getResourceNumbers(), is(empty()));
		RoutingDTO routingDTO = make(a(RoutingDTO, with(resourceNumbers, ImmutableSet.of(worker.getUserNumber()))));

		String routingJson = jackson.writeValueAsString(routingDTO);
		MvcResult updateResult = mockMvc.perform(
			doPost(ENDPOINT + "/" + assignmentDTO.getId() + "/routing").content(routingJson)
		).andExpect(status().isOk()).andReturn();

		RoutingDTO result = getFirstResult(updateResult, routingType);

		assertThat(result, hasProperty("groupIds", is(routingDTO.getGroupIds())));
		assertThat(result, hasProperty("resourceNumbers", is(routingDTO.getResourceNumbers())));
		assertThat(result, hasProperty("assignToFirstToAccept", is(routingDTO.isAssignToFirstToAccept())));
	}

	@Test
	public void putUpdateAssignmentDraftRouting() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);

		RoutingDTO routingDTO = make(a(RoutingDTO, with(groupIds, ImmutableSet.of(group.getId()))));

		String routingJson = jackson.writeValueAsString(routingDTO);
		MvcResult updateResult = mockMvc.perform(
			doPut(ENDPOINT + "/" + assignmentDTO.getId() + "/routing").content(routingJson)
		).andExpect(status().isOk()).andReturn();

		RoutingDTO result = getFirstResult(updateResult, routingType);

		assertThat(result, hasProperty("groupIds", is(routingDTO.getGroupIds())));
		assertThat(result, hasProperty("resourceNumbers", is(routingDTO.getResourceNumbers())));
		assertThat(result, hasProperty("assignToFirstToAccept", is(routingDTO.isAssignToFirstToAccept())));
	}

	@Test
	public void putUpdateAssignmentDraftRoutingWithWorkers() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);

		assertThat(assignmentDTO.getRouting().getResourceNumbers(), is(empty()));
		RoutingDTO routingDTO = make(a(RoutingDTO, with(resourceNumbers, ImmutableSet.of(worker.getUserNumber()))));

		String routingJson = jackson.writeValueAsString(routingDTO);
		MvcResult updateResult = mockMvc.perform(
			doPut(ENDPOINT + "/" + assignmentDTO.getId() + "/routing").content(routingJson)
		).andExpect(status().isOk()).andReturn();

		RoutingDTO result = getFirstResult(updateResult, routingType);

		assertThat(result, hasProperty("groupIds", is(routingDTO.getGroupIds())));
		assertThat(result, hasProperty("resourceNumbers", is(routingDTO.getResourceNumbers())));
		assertThat(result, hasProperty("assignToFirstToAccept", is(routingDTO.isAssignToFirstToAccept())));
	}

	private Callable<Boolean> assignmentIsRouted(final String workNumber) {
		return new WMCallable<Boolean>(webRequestContextProvider) {
			public Boolean apply() throws Exception {
				Long workId = workService.findWorkId(workNumber);
				return workService.findWorkResource(worker.getId(), workId) != null;
			}
		};
	}

	@Test
	public void getAssignmentDraftCustomFieldGroups() throws Exception {
		CustomFieldGroupDTO customFieldGroupDTO = make(a(CustomFieldGroupDTO,
			with(customFieldGroupId, customFieldGroup.getId()),
			with(customFieldGroupName, customFieldGroup.getName()),
			with(field, new CustomFieldDTO.Builder(
				make(a(CustomFieldDTO,
					with(customFieldId, customField.getId()),
					with(customFieldName, customField.getName())
				))))));
		CustomFieldGroupDTO.Builder builder = new CustomFieldGroupDTO.Builder(customFieldGroupDTO);
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(AssignmentMaker.customFieldGroup, builder)));

		assignmentDTO = assignmentService.create(assignmentDTO, false);

		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT + "/" + assignmentDTO.getId() + "/custom_field_groups")
		).andExpect(status().isOk()).andReturn();

		CustomFieldGroupDTO result = getFirstResult(mvcResult, customFieldGroupsType);

		assertThat(result, hasProperty("id", is(customFieldGroupDTO.getId())));
		assertThat(result, hasProperty("name", is(customFieldGroupDTO.getName())));
		assertThat(result, hasProperty("position", is(customFieldGroupDTO.getPosition())));
		assertThat(result, hasProperty("required", is(customFieldGroupDTO.isRequired())));

		for (CustomFieldDTO customFieldDTO : customFieldGroupDTO.getFields()) {
			assertThat(result, hasProperty("fields", hasItem(hasProperty("id", is(customFieldDTO.getId())))));
			assertThat(result, hasProperty("fields", hasItem(hasProperty("name", is(customFieldDTO.getName())))));
		}
	}

	@Test
	public void postUpdateAssignmentDraftCustomFieldGroups() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);

		CustomFieldGroupDTO customFieldGroupDTO = make(a(CustomFieldGroupDTO,
			with(customFieldGroupId, customFieldGroup.getId()),
			with(field, new CustomFieldDTO.Builder(
				make(a(CustomFieldDTO, with(customFieldId, customField.getId())))))));

		List<CustomFieldGroupDTO> customFieldGroupDTOs = ImmutableList.of(customFieldGroupDTO);

		String customFieldGroupsJson = jackson.writeValueAsString(customFieldGroupDTOs);
		MvcResult updateResult = mockMvc.perform(
			doPost(ENDPOINT + "/" + assignmentDTO.getId() + "/custom_field_groups").content(customFieldGroupsJson)
		).andExpect(status().isOk()).andReturn();

		CustomFieldGroupDTO result = getFirstResult(updateResult, customFieldGroupsType);

		assertThat(result, hasProperty("id", is(customFieldGroup.getId())));
		assertThat(result, hasProperty("name", is(customFieldGroup.getName())));
		assertThat(result, hasProperty("position", is(customFieldGroupDTO.getPosition())));
		assertThat(result, hasProperty("required", is(customFieldGroupDTO.isRequired())));

		for (CustomFieldDTO customFieldDTO : customFieldGroupDTO.getFields()) {
			assertThat(result, hasProperty("fields", hasItem(hasProperty("id", is(customField.getId())))));
			assertThat(result, hasProperty("fields", hasItem(hasProperty("name", is(customField.getName())))));
		}
	}

	@Test
	public void putUpdateAssignmentDraftCustomFieldGroups() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);
		CustomFieldGroupDTO customFieldGroupDTO = make(a(CustomFieldGroupDTO,
			with(customFieldGroupId, customFieldGroup.getId()),
			with(field, new CustomFieldDTO.Builder(
				make(a(CustomFieldDTO, with(customFieldId, customField.getId())))))));

		List<CustomFieldGroupDTO> customFieldGroupDTOs = ImmutableList.of(customFieldGroupDTO);

		String customFieldGroupsJson = jackson.writeValueAsString(customFieldGroupDTOs);
		MvcResult updateResult = mockMvc.perform(
			doPut(ENDPOINT + "/" + assignmentDTO.getId() + "/custom_field_groups").content(customFieldGroupsJson)
		).andExpect(status().isOk()).andReturn();

		CustomFieldGroupDTO result = getFirstResult(updateResult, customFieldGroupsType);

		assertThat(result, hasProperty("id", is(customFieldGroup.getId())));
		assertThat(result, hasProperty("name", is(customFieldGroup.getName())));
		assertThat(result, hasProperty("position", is(customFieldGroupDTO.getPosition())));
		assertThat(result, hasProperty("required", is(customFieldGroupDTO.isRequired())));

		for (CustomFieldDTO customFieldDTO : customFieldGroupDTO.getFields()) {
			assertThat(result, hasProperty("fields", hasItem(hasProperty("id", is(customField.getId())))));
			assertThat(result, hasProperty("fields", hasItem(hasProperty("name", is(customField.getName())))));
		}
	}

	@Test
	public void getAssignmentShipments() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);
		ShipmentGroupDTO shipmentGroupDTO = assignmentDTO.getShipmentGroup();
		ShipmentDTO shipmentDTO = shipmentGroupDTO.getShipments().iterator().next();

		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT + "/" + assignmentDTO.getId() + "/shipments")
		).andExpect(status().isOk()).andReturn();

		ShipmentGroupDTO shipmentGroupResult = getFirstResult(mvcResult, shipmentsType);
		ShipmentDTO shipmentResult = shipmentGroupResult.getShipments().iterator().next();

		assertThat(shipmentResult, hasProperty("name", is(shipmentDTO.getName())));
		assertThat(shipmentResult, hasProperty("trackingNumber", is(shipmentDTO.getTrackingNumber())));
		assertThat(shipmentResult, hasProperty("shippingProvider", is(shipmentDTO.getShippingProvider())));
		assertThat(shipmentGroupResult, hasProperty("returnShipment", is(shipmentGroupDTO.isReturnShipment())));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("id", is(not(nullValue())))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("number", is(shipmentGroupDTO.getShipToAddress().getNumber()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("name", is(shipmentGroupDTO.getShipToAddress().getName()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("addressLine1", is(shipmentGroupDTO.getShipToAddress().getAddressLine1()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("addressLine2", is(shipmentGroupDTO.getShipToAddress().getAddressLine2()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("city", is(shipmentGroupDTO.getShipToAddress().getCity()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("state", is(shipmentGroupDTO.getShipToAddress().getState()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("zip", is(shipmentGroupDTO.getShipToAddress().getZip()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("country", is(shipmentGroupDTO.getShipToAddress().getCountry()))));
	}

	@Test
	public void postUpdateAssignmentDraftShipments() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);
		ShipmentGroupDTO shipmentGroupDTO = make(a(ShipmentGroupDTO));
		ShipmentDTO shipmentDTO = shipmentGroupDTO.getShipments().iterator().next();
		String shipmentsJson = jackson.writeValueAsString(shipmentGroupDTO);
		MvcResult updateResult = mockMvc.perform(
			doPost(ENDPOINT + "/" + assignmentDTO.getId() + "/shipments").content(shipmentsJson)
		).andExpect(status().isOk()).andReturn();

		ShipmentGroupDTO shipmentGroupResult = getFirstResult(updateResult, shipmentsType);
		ShipmentDTO shipmentResult = shipmentGroupResult.getShipments().iterator().next();

		assertThat(shipmentResult, hasProperty("name", is(shipmentDTO.getName())));
		assertThat(shipmentResult, hasProperty("trackingNumber", is(shipmentDTO.getTrackingNumber())));
		assertThat(shipmentResult, hasProperty("shippingProvider", is(shipmentDTO.getShippingProvider())));
		assertThat(shipmentGroupResult, hasProperty("returnShipment", is(shipmentGroupDTO.isReturnShipment())));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("id", is(not(nullValue())))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("number", is(shipmentGroupDTO.getShipToAddress().getNumber()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("name", is(shipmentGroupDTO.getShipToAddress().getName()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("addressLine1", is(shipmentGroupDTO.getShipToAddress().getAddressLine1()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("addressLine2", is(shipmentGroupDTO.getShipToAddress().getAddressLine2()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("city", is(shipmentGroupDTO.getShipToAddress().getCity()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("state", is(shipmentGroupDTO.getShipToAddress().getState()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("zip", is(shipmentGroupDTO.getShipToAddress().getZip()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("country", is(shipmentGroupDTO.getShipToAddress().getCountry()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("longitude", is(shipmentGroupDTO.getShipToAddress().getLongitude()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("latitude", is(shipmentGroupDTO.getShipToAddress().getLatitude()))));
	}

	@Test
	public void putUpdateAssignmentDraftShipments() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));
		assignmentDTO = assignmentService.create(assignmentDTO, false);
		ShipmentGroupDTO shipmentGroupDTO = make(a(ShipmentGroupDTO));
		ShipmentDTO shipmentDTO = shipmentGroupDTO.getShipments().iterator().next();
		String shipmentsJson = jackson.writeValueAsString(shipmentGroupDTO);
		MvcResult updateResult = mockMvc.perform(doPut(ENDPOINT + "/" + assignmentDTO.getId() + "/shipments").content(
						shipmentsJson)).andExpect(status().isOk()).andReturn();

		ShipmentGroupDTO shipmentGroupResult = getFirstResult(updateResult, shipmentsType);
		ShipmentDTO shipmentResult = shipmentGroupResult.getShipments().iterator().next();

		assertThat(shipmentResult, hasProperty("name", is(shipmentDTO.getName())));
		assertThat(shipmentResult, hasProperty("trackingNumber", is(shipmentDTO.getTrackingNumber())));
		assertThat(shipmentResult, hasProperty("shippingProvider", is(shipmentDTO.getShippingProvider())));
		assertThat(shipmentGroupResult, hasProperty("returnShipment", is(shipmentGroupDTO.isReturnShipment())));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("id", is(not(nullValue())))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("number", is(shipmentGroupDTO.getShipToAddress().getNumber()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("name", is(shipmentGroupDTO.getShipToAddress().getName()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("addressLine1", is(shipmentGroupDTO.getShipToAddress().getAddressLine1()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("addressLine2", is(shipmentGroupDTO.getShipToAddress().getAddressLine2()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("city", is(shipmentGroupDTO.getShipToAddress().getCity()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("state", is(shipmentGroupDTO.getShipToAddress().getState()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("zip", is(shipmentGroupDTO.getShipToAddress().getZip()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("country", is(shipmentGroupDTO.getShipToAddress().getCountry()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("longitude", is(shipmentGroupDTO.getShipToAddress().getLongitude()))));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("latitude", is(shipmentGroupDTO.getShipToAddress().getLatitude()))));
	}

	@Test
	public void getAssignmentDraftSurveys() throws Exception {
		SurveyDTO.Builder builder = new SurveyDTO.Builder(
			make(a(SurveyDTO,
				with(surveyId, assessment.getId())
			)));

		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(survey, builder)));

		assignmentDTO = assignmentService.create(assignmentDTO, false);
		SurveyDTO surveyDTO = assignmentDTO.getSurveys().iterator().next();

		MvcResult mvcResult = mockMvc.perform(doGet(ENDPOINT + "/" + assignmentDTO.getId() + "/surveys"))
						.andExpect(status().isOk())
						.andReturn();

		SurveyDTO result = getFirstResult(mvcResult, surveyType);

		assertThat(result, hasProperty("id", is(assessment.getId())));
		assertThat(result, hasProperty("required", is(surveyDTO.getRequired())));
	}

	@Test
	public void postUpdateAssignmentDraftSurveys() throws Exception {
		AssignmentDTO assignmentDTO = assignmentService.create(make(an(AssignmentDTO)), false);

		SurveyDTO surveyDTO = make(a(SurveyDTO,
			with(surveyId, assessment.getId())
		));

		List<SurveyDTO> surveyDTOs = ImmutableList.of(surveyDTO);

		final FilterProvider filters = new SimpleFilterProvider().addFilter(
			ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS,
			new ApiBaseHttpMessageConverter.APIProjectionsFilter(Collections.EMPTY_SET)
		);

		String surveysJson = jackson.writer(filters).writeValueAsString(surveyDTOs);

		MvcResult updateResult = mockMvc.perform(doPost(ENDPOINT + "/" + assignmentDTO.getId() + "/surveys").content(
						surveysJson)).andExpect(status().isOk()).andReturn();

		SurveyDTO result = getFirstResult(updateResult, surveyType);

		assertThat(result, hasProperty("id", is(assessment.getId())));
		assertThat(result, hasProperty("required", is(surveyDTO.getRequired())));
	}

	@Test
	public void putUpdateAssignmentDraftSurveys() throws Exception {
		AssignmentDTO assignmentDTO = assignmentService.create(make(an(AssignmentDTO)), false);

		SurveyDTO surveyDTO = make(a(SurveyDTO,
			with(surveyId, assessment.getId())
		));

		List<SurveyDTO> surveyDTOs = ImmutableList.of(surveyDTO);

		final FilterProvider filters = new SimpleFilterProvider().addFilter(
			ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS,
			new ApiBaseHttpMessageConverter.APIProjectionsFilter(Collections.EMPTY_SET)
		);

		String surveysJson = jackson.writer(filters).writeValueAsString(surveyDTOs);

		MvcResult updateResult = mockMvc.perform(doPut(ENDPOINT + "/" + assignmentDTO.getId() + "/surveys").content(
						surveysJson)).andExpect(status().isOk()).andReturn();

		SurveyDTO result = getFirstResult(updateResult, surveyType);

		assertThat(result, hasProperty("id", is(assessment.getId())));
		assertThat(result, hasProperty("required", is(surveyDTO.getRequired())));
	}

	@Test
	public void getAssignmentDraftConfiguration() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));

		assignmentDTO = assignmentService.create(assignmentDTO, false);
		ConfigurationDTO configurationDTO = assignmentDTO.getConfiguration();

		MvcResult mvcResult = mockMvc.perform(doGet(ENDPOINT + "/" + assignmentDTO.getId() + "/configuration"))
						.andExpect(status().isOk())
						.andReturn();

		ConfigurationDTO result = getFirstResult(mvcResult, configurationType);

		assertThat(result, samePropertyValuesAs(configurationDTO));
	}

	@Test
	public void postUpdateAssignmentDraftConfiguration() throws Exception {
		AssignmentDTO assignmentDTO = assignmentService.create(make(an(AssignmentDTO)), false);

		ConfigurationDTO configurationDTO = make(a(ConfigurationDTO,
			with(customFieldsEnabled, false),
			with(deliverablesEnabled, false),
			with(requirementSetsEnabled, false),
			with(surveysEnabled, false),
			with(shipmentsEnabled, false)
		));

		String configurationJson = jackson.writeValueAsString(configurationDTO);
		MvcResult updateResult = mockMvc.perform(doPost(ENDPOINT + "/" + assignmentDTO.getId() + "/configuration").content(
						configurationJson)).andExpect(status().isOk()).andReturn();

		ConfigurationDTO result = getFirstResult(updateResult, configurationType);

		assertThat(result, samePropertyValuesAs(configurationDTO));
	}

	@Test
	public void putUpdateAssignmentDraftConfiguration() throws Exception {
		AssignmentDTO assignmentDTO = assignmentService.create(make(an(AssignmentDTO)), false);

		ConfigurationDTO configurationDTO = make(a(ConfigurationDTO,
			with(customFieldsEnabled, false),
			with(deliverablesEnabled, false),
			with(requirementSetsEnabled, false),
			with(surveysEnabled, false),
			with(shipmentsEnabled, false)
		));

		String configurationJson = jackson.writeValueAsString(configurationDTO);
		MvcResult updateResult = mockMvc.perform(doPut(ENDPOINT + "/" + assignmentDTO.getId() + "/configuration").content(
						configurationJson)).andExpect(status().isOk()).andReturn();

		ConfigurationDTO result = getFirstResult(updateResult, configurationType);

		assertThat(result, samePropertyValuesAs(configurationDTO));
	}

	@Test
	public void getAssignmentDraftDocuments() throws Exception {
		DocumentDTO.Builder builder = new DocumentDTO.Builder(
			make(a(DocumentMaker.DocumentDTO,
				with(documentId, asset.getId())
			)));

		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(document, builder)));

		assignmentDTO = assignmentService.create(assignmentDTO, false);
		DocumentDTO documentDTO = assignmentDTO.getDocuments().iterator().next();

		MvcResult mvcResult = mockMvc.perform(doGet(ENDPOINT + "/" + assignmentDTO.getId() + "/documents"))
						.andExpect(status().isOk())
						.andReturn();

		DocumentDTO result = getFirstResult(mvcResult, documentType);

		assertThat(result, hasProperty("id", is(asset.getId())));
		assertThat(result, hasProperty("uuid", is(asset.getUUID())));
		assertThat(result, hasProperty("name", is(asset.getName())));
		assertThat(result, hasProperty("description", is(asset.getDescription())));
		assertThat(result, hasProperty("uploaded", is(documentDTO.isUploaded())));
		assertThat(result, hasProperty("visibilityType", is(documentDTO.getVisibilityType())));
	}

	@Test
	public void postUpdateAssignmentDraftDocuments() throws Exception {
		AssignmentDTO assignmentDTO = assignmentService.create(make(an(AssignmentDTO)), false);

		DocumentDTO documentDTO = make(a(DocumentMaker.DocumentDTO,
			with(documentId, asset.getId())
		));

		List<DocumentDTO> documentDTOs = ImmutableList.of(documentDTO);

		String surveysJson = jackson.writeValueAsString(documentDTOs);
		MvcResult updateResult = mockMvc.perform(doPost(ENDPOINT + "/" + assignmentDTO.getId() + "/documents").content(
						surveysJson)).andExpect(status().isOk()).andReturn();

		DocumentDTO result = getFirstResult(updateResult, documentType);

		assertThat(result, hasProperty("id", is(asset.getId())));
		assertThat(result, hasProperty("uuid", is(asset.getUUID())));
		assertThat(result, hasProperty("name", is(asset.getName())));
		assertThat(result, hasProperty("description", is(asset.getDescription())));
		assertThat(result, hasProperty("uploaded", is(documentDTO.isUploaded())));
		assertThat(result, hasProperty("visibilityType", is(documentDTO.getVisibilityType())));
	}

	@Test
	public void putUpdateAssignmentDraftDocuments() throws Exception {
		AssignmentDTO assignmentDTO = assignmentService.create(make(an(AssignmentDTO)), false);

		DocumentDTO documentDTO = make(a(DocumentMaker.DocumentDTO,
			with(documentId, asset.getId())
		));

		List<DocumentDTO> documentDTOs = ImmutableList.of(documentDTO);

		String surveysJson = jackson.writeValueAsString(documentDTOs);
		MvcResult updateResult = mockMvc.perform(doPut(ENDPOINT + "/" + assignmentDTO.getId() + "/documents").content(
						surveysJson)).andExpect(status().isOk()).andReturn();

		DocumentDTO result = getFirstResult(updateResult, documentType);

		assertThat(result, hasProperty("id", is(asset.getId())));
		assertThat(result, hasProperty("uuid", is(asset.getUUID())));
		assertThat(result, hasProperty("name", is(asset.getName())));
		assertThat(result, hasProperty("description", is(asset.getDescription())));
		assertThat(result, hasProperty("uploaded", is(documentDTO.isUploaded())));
		assertThat(result, hasProperty("visibilityType", is(documentDTO.getVisibilityType())));
	}

	/**
	 * Set the required uniqueExternalId and confirm that it's returned in the GET
	 */
	@Test
	public void getAssignmentDraftWithRequiredUniqueExternalId() throws Exception {

		enableUniqueExternalId();

		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));

		assignmentDTO = assignmentService.create(assignmentDTO, false);

		MvcResult mvcResult = mockMvc.perform(doGet(ENDPOINT + "/" + assignmentDTO.getId()))
						.andExpect(status().isOk())
						.andReturn();

		AssignmentDTO result = getFirstResult(mvcResult, assignmentType);

		assertThat(result, hasProperty("uniqueExternalId", is(assignmentDTO.getUniqueExternalId())));
	}

	/**
	 * WorkUniqueId is not required for drafts, only work being sent. Therefore this assigment should save
	 * even though the workUniqueId field is not set.
	 */
	@Test
	public void postAssignmentDraftWithoutRequiredWorkUniqueId() throws Exception {

		enableUniqueExternalId();

		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, withNull(uniqueExternalId)));

		assignmentDTO = assignmentService.create(assignmentDTO, false);

		MvcResult mvcResult = mockMvc.perform(doGet(ENDPOINT + "/" + assignmentDTO.getId()))
						.andExpect(status().isOk())
						.andReturn();

		AssignmentDTO result = getFirstResult(mvcResult, assignmentType);

		assertThat(result, hasProperty("uniqueExternalId", is(assignmentDTO.getUniqueExternalId())));
	}

	@Test
	public void putUpdateAssignmentDraftWithRequiredUniqueExternalId() throws Exception {

		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));

		assignmentDTO = assignmentService.create(assignmentDTO, false);

		String assignmentJSON = jackson.writeValueAsString(assignmentDTO);

		MvcResult updateResult = mockMvc.perform(
			doPost(ENDPOINT + "/" + assignmentDTO.getId()).content(assignmentJSON)
		).andExpect(status().isOk()).andReturn();

		AssignmentDTO updateResultDTO =
			new AssignmentDTO.Builder(getFirstResult(updateResult, assignmentType)).setDescription("This is the update!").build();

		assertThat(updateResultDTO, hasProperty("uniqueExternalId", is(assignmentDTO.getUniqueExternalId())));
	}

	@Test
	public void getAssignmentDeliverables() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO));

		assignmentDTO = assignmentService.create(assignmentDTO, false);
		DeliverablesGroupDTO deliverablesGroupDTO = assignmentDTO.getDeliverablesGroup();
		DeliverableDTO deliverableDTO = deliverablesGroupDTO.getDeliverables().iterator().next();

		MvcResult mvcResult = mockMvc.perform(doGet(ENDPOINT + "/" + assignmentDTO.getId() + "/deliverables_group"))
						.andExpect(status().isOk())
						.andReturn();

		DeliverablesGroupDTO result = getFirstResult(mvcResult, deliverablesType);

		assertThat(result, hasProperty("id"));
		assertThat(result, hasProperty("instructions", is(deliverablesGroupDTO.getInstructions())));
		assertThat(result, hasProperty("hoursToComplete", is(deliverablesGroupDTO.getHoursToComplete())));

		assertThat(result, hasProperty("deliverables", hasItem(hasProperty("id"))));
		assertThat(result, hasProperty("deliverables", hasItem(hasProperty("type", is(deliverableDTO.getType())))));
		assertThat(result, hasProperty("deliverables", hasItem(hasProperty("description", is(deliverableDTO.getDescription())))));
		assertThat(result, hasProperty("deliverables", hasItem(hasProperty("numberOfFiles", is(deliverableDTO.getNumberOfFiles())))));
		assertThat(result, hasProperty("deliverables", hasItem(hasProperty("priority", is(deliverableDTO.getPriority())))));
	}

	@Test
	public void postUpdateAssignmentDeliverables() throws Exception {
		AssignmentDTO assignmentDTO = assignmentService.create(make(an(AssignmentDTO)), false);
		DeliverablesGroupDTO deliverablesGroupDTO = make(a(DeliverableMaker.DeliverablesGroupDTO,
			with(deliverable, new DeliverableDTO.Builder(make(a(DeliverableMaker.DeliverableDTO,
				with(deliverableDescription, "This is the awesome!")))))));

		DeliverableDTO deliverableDTO = deliverablesGroupDTO.getDeliverables().iterator().next();

		String deliverablesGroupJson = jackson.writeValueAsString(deliverablesGroupDTO);
		MvcResult updateResult = mockMvc.perform(doPost(ENDPOINT + "/" + assignmentDTO.getId() + "/deliverables_group").content(
						deliverablesGroupJson)).andExpect(status().isOk()).andReturn();

		DeliverablesGroupDTO result = getFirstResult(updateResult, deliverablesType);

		assertThat(result, hasProperty("id"));
		assertThat(result, hasProperty("instructions", is(deliverablesGroupDTO.getInstructions())));
		assertThat(result, hasProperty("hoursToComplete", is(deliverablesGroupDTO.getHoursToComplete())));

		assertThat(result, hasProperty("deliverables", hasItem(hasProperty("id"))));
		assertThat(result, hasProperty("deliverables", hasItem(hasProperty("type", is(deliverableDTO.getType())))));
		assertThat(result, hasProperty("deliverables", hasItem(hasProperty("description", is(deliverableDTO.getDescription())))));
		assertThat(result, hasProperty("deliverables", hasItem(hasProperty("numberOfFiles", is(deliverableDTO.getNumberOfFiles())))));
		assertThat(result, hasProperty("deliverables", hasItem(hasProperty("priority", is(deliverableDTO.getPriority())))));
	}

	@Test
	public void putUpdateAssignmentDeliverables() throws Exception {
		AssignmentDTO assignmentDTO = assignmentService.create(make(an(AssignmentDTO)), false);
		DeliverablesGroupDTO deliverablesGroupDTO = make(a(DeliverableMaker.DeliverablesGroupDTO,
			with(deliverable, new DeliverableDTO.Builder(make(a(DeliverableMaker.DeliverableDTO,
				with(deliverableDescription, "This is the awesome!")))))));

		DeliverableDTO deliverableDTO = deliverablesGroupDTO.getDeliverables().iterator().next();

		String deliverablesGroupJson = jackson.writeValueAsString(deliverablesGroupDTO);
		MvcResult updateResult = mockMvc.perform(doPut(ENDPOINT + "/" + assignmentDTO.getId() + "/deliverables_group").content(
						deliverablesGroupJson)).andExpect(status().isOk()).andReturn();

		DeliverablesGroupDTO result = getFirstResult(updateResult, deliverablesType);

		assertThat(result, hasProperty("id"));
		assertThat(result, hasProperty("instructions", is(deliverablesGroupDTO.getInstructions())));
		assertThat(result, hasProperty("hoursToComplete", is(deliverablesGroupDTO.getHoursToComplete())));

		assertThat(result, hasProperty("deliverables", hasItem(hasProperty("id"))));
		assertThat(result, hasProperty("deliverables", hasItem(hasProperty("type", is(deliverableDTO.getType())))));
		assertThat(result, hasProperty("deliverables", hasItem(hasProperty("description", is(deliverableDTO.getDescription())))));
		assertThat(result, hasProperty("deliverables", hasItem(hasProperty("numberOfFiles", is(deliverableDTO.getNumberOfFiles())))));
		assertThat(result, hasProperty("deliverables", hasItem(hasProperty("priority", is(deliverableDTO.getPriority())))));
	}

	@Test
	public void validateValidAssignment() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentMaker.AssignmentDTO));
		String assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT + "/validation_errors").content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		List<ApiBaseError> result = getFirstResult(mvcResult, validationErrorListType);

		assertEquals(0, result.size());
	}

	@Test
	public void validateInvalidAssignment_missingTitle() throws Exception {
		AssignmentDTO assignmentDTO = make(an(AssignmentMaker.AssignmentDTO, withNull(title)));
		String assignmentJson = jackson.writeValueAsString(assignmentDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT + "/validation_errors").content(assignmentJson)
		).andExpect(status().isOk()).andReturn();

		List<ApiBaseError> result = getFirstResult(mvcResult, validationErrorListType);

		assertEquals(1, result.size());
	}

	@Test
	public void createMonthlyRecurringAssignmentWithEndDate() throws Exception {

		String defaultEndDate = getDateDaysFromNow(365);

		RecurrenceDTO.Builder builder = new RecurrenceDTO.Builder()
				.setType(RecurrenceType.MONTHLY.toString())
				.setEndDate(defaultEndDate)
				.setFrequencyModifier(1)
				.setDescription("Recurrs every month");

		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(recurrence, builder), with(title, "recurrence test")));

		assignmentDTO = assignmentService.create(assignmentDTO, false);

		MvcResult mvcResult = mockMvc.perform(
				doGet(ENDPOINT + "/" + assignmentDTO.getId())
		).andExpect(status().isOk()).andReturn();

		AssignmentDTO result = getFirstResult(mvcResult, assignmentType);

		assertNotNull(result.getRecurrence());
		RecurrenceDTO recurrence = result.getRecurrence();
		assertThat(recurrence, hasProperty("uuid", not(isEmptyOrNullString())));

		wait("assignments", result.getRecurrence().getUuid());

		MvcResult getResult = get("status", result.getRecurrence().getUuid());
		StatusDTO statusDTO = getFirstResult(getResult, statusType);
		assertEquals("assignments", statusDTO.getStep());
		assertEquals("0", statusDTO.getRemaining());
	}

	@Test
	public void createWeeklyRecurringAssignmentWithEndDate() throws Exception {

		String defaultEndDate = getDateDaysFromNow(21);

		RecurrenceDTO.Builder builder = new RecurrenceDTO.Builder()
				.setType(RecurrenceType.WEEKLY.toString())
				.setEndDate(defaultEndDate)
				.setWeekdays(Arrays.asList(new Boolean[]{false, true, false, true, false, true, false}))
				.setFrequencyModifier(1)
				.setDescription("Recurrs every week on MWF");

		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(recurrence, builder), with(title, "recurrence test")));

		assignmentDTO = assignmentService.create(assignmentDTO, false);

		MvcResult mvcResult = mockMvc.perform(
				doGet(ENDPOINT + "/" + assignmentDTO.getId())
		).andExpect(status().isOk()).andReturn();

		AssignmentDTO result = getFirstResult(mvcResult, assignmentType);

		assertNotNull(result.getRecurrence());
		RecurrenceDTO recurrence = result.getRecurrence();
		assertThat(recurrence, hasProperty("uuid", not(isEmptyOrNullString())));

		wait("assignments", result.getRecurrence().getUuid());

		MvcResult getResult = get("status", result.getRecurrence().getUuid());
		StatusDTO statusDTO = getFirstResult(getResult, statusType);
		assertEquals("assignments", statusDTO.getStep());
		assertEquals("0", statusDTO.getRemaining());
	}

	@Test
	public void createDailyRecurringAssignmentWithEndDate() throws Exception {

		String defaultEndDate = getDateDaysFromNow(5);

		RecurrenceDTO.Builder builder = new RecurrenceDTO.Builder()
				.setType(RecurrenceType.DAILY.toString())
				.setEndDate(defaultEndDate)
				.setFrequencyModifier(1)
				.setDescription("Recurrs every week on MWF");

		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(recurrence, builder), with(title, "recurrence test")));

		assignmentDTO = assignmentService.create(assignmentDTO, false);

		MvcResult mvcResult = mockMvc.perform(
				doGet(ENDPOINT + "/" + assignmentDTO.getId())
		).andExpect(status().isOk()).andReturn();

		AssignmentDTO result = getFirstResult(mvcResult, assignmentType);

		assertNotNull(result.getRecurrence());
		RecurrenceDTO recurrence = result.getRecurrence();
		assertThat(recurrence, hasProperty("uuid", not(isEmptyOrNullString())));

		wait("assignments", result.getRecurrence().getUuid());

		MvcResult getResult = get("status", result.getRecurrence().getUuid());
		StatusDTO statusDTO = getFirstResult(getResult, statusType);
		assertEquals("assignments", statusDTO.getStep());
		assertEquals("0", statusDTO.getRemaining());
	}

	@Test
	public void createMonthlyRecurringAssignmentWithRepetitions() throws Exception {

		RecurrenceDTO.Builder builder = new RecurrenceDTO.Builder()
				.setType(RecurrenceType.MONTHLY.toString())
				.setRepetitions(5)
				.setFrequencyModifier(1)
				.setDescription("Recurrs every month");

		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(recurrence, builder), with(title, "recurrence test")));

		assignmentDTO = assignmentService.create(assignmentDTO, false);

		MvcResult mvcResult = mockMvc.perform(
				doGet(ENDPOINT + "/" + assignmentDTO.getId())
		).andExpect(status().isOk()).andReturn();

		AssignmentDTO result = getFirstResult(mvcResult, assignmentType);

		assertNotNull(result.getRecurrence());
		RecurrenceDTO recurrence = result.getRecurrence();
		assertThat(recurrence, hasProperty("uuid", not(isEmptyOrNullString())));

		wait("assignments", result.getRecurrence().getUuid());

		MvcResult getResult = get("status", result.getRecurrence().getUuid());
		StatusDTO statusDTO = getFirstResult(getResult, statusType);
		assertEquals("assignments", statusDTO.getStep());
		assertEquals("0", statusDTO.getRemaining());
	}

	@Test
	public void createWeeklyRecurringAssignmentWithRepetitions() throws Exception {

		RecurrenceDTO.Builder builder = new RecurrenceDTO.Builder()
				.setType(RecurrenceType.WEEKLY.toString())
				.setRepetitions(5)
				.setWeekdays(Arrays.asList(new Boolean[]{false, true, false, true, false, true, false}))
				.setFrequencyModifier(1)
				.setDescription("Recurrs every week on MWF");

		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(recurrence, builder), with(title, "recurrence test")));

		assignmentDTO = assignmentService.create(assignmentDTO, false);

		MvcResult mvcResult = mockMvc.perform(
				doGet(ENDPOINT + "/" + assignmentDTO.getId())
		).andExpect(status().isOk()).andReturn();

		AssignmentDTO result = getFirstResult(mvcResult, assignmentType);

		assertNotNull(result.getRecurrence());
		RecurrenceDTO recurrence = result.getRecurrence();
		assertThat(recurrence, hasProperty("uuid", not(isEmptyOrNullString())));

		wait("assignments", result.getRecurrence().getUuid());

		MvcResult getResult = get("status", result.getRecurrence().getUuid());
		StatusDTO statusDTO = getFirstResult(getResult, statusType);
		assertEquals("assignments", statusDTO.getStep());
		assertEquals("0", statusDTO.getRemaining());
	}

	@Test
	public void createDailyRecurringAssignmentWithRepetitions() throws Exception {

		RecurrenceDTO.Builder builder = new RecurrenceDTO.Builder()
				.setType(RecurrenceType.DAILY.toString())
				.setRepetitions(5)
				.setFrequencyModifier(1)
				.setDescription("Recurrs every week on MWF");

		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(recurrence, builder), with(title, "recurrence test")));

		assignmentDTO = assignmentService.create(assignmentDTO, false);

		MvcResult mvcResult = mockMvc.perform(
				doGet(ENDPOINT + "/" + assignmentDTO.getId())
		).andExpect(status().isOk()).andReturn();

		AssignmentDTO result = getFirstResult(mvcResult, assignmentType);

		assertNotNull(result.getRecurrence());
		RecurrenceDTO recurrence = result.getRecurrence();
		assertThat(recurrence, hasProperty("uuid", not(isEmptyOrNullString())));

		wait("assignments", result.getRecurrence().getUuid());

		MvcResult getResult = get("status", result.getRecurrence().getUuid());
		StatusDTO statusDTO = getFirstResult(getResult, statusType);
		assertEquals("assignments", statusDTO.getStep());
		assertEquals("0", statusDTO.getRemaining());
	}

	protected Callable<Boolean> isProcessingComplete(
			final String uuid,
			final String step) {
		return new WMCallable<Boolean>(webRequestContextProvider) {
			public Boolean apply() throws Exception {
				MvcResult getResult = get("status", uuid);
				StatusDTO statusDTO = getFirstResult(getResult, statusType);
				return step.equals(statusDTO.getStep()) && "0".equals(statusDTO.getRemaining());
			}
		};
	}

	protected MvcResult get(String url, String uuid) throws Exception {
		final String STATUS_ENDPOINT = "/employer/v2/uploads";
		return mockMvc.perform(doGet(String.format("%s/%s/%s", STATUS_ENDPOINT, uuid, url)))
				.andExpect(status().isOk())
				.andReturn();
	}

	protected void wait(String step, String uuid) throws Exception {
		await().atMost(JMS_DELAY / 2, MILLISECONDS).until(isProcessingComplete(uuid, step));
	}

	private String getDateDaysFromNow(int days) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_TIME_FORMAT);
		String date = formatter.print(
				DateTime.now()
						.plusDays(days)
						.withHourOfDay(13)
						.withMinuteOfHour(0)
						.withSecondOfMinute(0)
						.withMillisOfSecond(0)
		);
		return date;
	}
}
