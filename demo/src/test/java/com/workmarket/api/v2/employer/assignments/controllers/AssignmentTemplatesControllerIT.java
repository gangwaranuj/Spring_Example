package com.workmarket.api.v2.employer.assignments.controllers;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.workmarket.api.ApiBaseError;
import com.workmarket.api.v2.ApiV2BaseIT;
import com.workmarket.api.v2.employer.assignments.controllers.support.LocationMaker;
import com.workmarket.api.v2.employer.assignments.controllers.support.ShipmentGroupMaker;
import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.employer.assignments.models.RoutingDTO;
import com.workmarket.api.v2.employer.assignments.models.ShipmentDTO;
import com.workmarket.api.v2.employer.assignments.models.ShipmentGroupDTO;
import com.workmarket.api.v2.employer.assignments.models.TemplateDTO;
import com.workmarket.api.v2.employer.assignments.services.AssignmentTemplateService;
import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.part.ShippingDestinationType;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.test.IntegrationTest;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.an;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static com.natpryce.makeiteasy.MakeItEasy.withNull;
import static com.workmarket.api.v2.employer.assignments.controllers.support.AssignmentMaker.AssignmentDTO;
import static com.workmarket.api.v2.employer.assignments.controllers.support.AssignmentMaker.routing;
import static com.workmarket.api.v2.employer.assignments.controllers.support.AssignmentMaker.shipmentGroup;
import static com.workmarket.api.v2.employer.assignments.controllers.support.AssignmentMaker.title;
import static com.workmarket.api.v2.employer.assignments.controllers.support.RoutingMaker.RoutingDTO;
import static com.workmarket.api.v2.employer.assignments.controllers.support.RoutingMaker.groupIds;
import static com.workmarket.api.v2.employer.assignments.controllers.support.RoutingMaker.resourceNumbers;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ShipmentGroupMaker.returnAddress;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ShipmentGroupMaker.returnShipment;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ShipmentGroupMaker.shipments;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ShipmentGroupMaker.shippingAddress;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ShipmentGroupMaker.shippingDestinationType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ShipmentGroupMaker.suppliedByWorker;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TemplateMaker.TemplateDTO;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TemplateMaker.assignment;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TemplateMaker.name;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.errorType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.mapType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.templateType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.hamcrest.core.IsNot.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class AssignmentTemplatesControllerIT extends ApiV2BaseIT {
	@Autowired AssignmentTemplateService assignmentTemplateService;
	@Autowired EventRouter eventRouter;

	private static final String BASE_ENDPOINT = "/employer/v2/assignments";
	private static final String TEMPLATE_ENDPOINT = BASE_ENDPOINT + "/templates";
	UserGroup group;
	User worker;

	@Before
	public void setUp() throws Exception {
		login();

		worker = newCompanyEmployee(user.getCompany().getId());
		group = newCompanyUserGroup(user.getCompany().getId());
		List<Long> ids = Lists.newArrayList();
		ids.add(worker.getId());
		userGroupService.addUsersToGroup(ids, group.getId(), user.getId());
		eventRouter.sendEvent(new UserSearchIndexEvent(worker.getId()));
	}

	@Test
	public void getTemplatesWithFields() throws Exception {
		TemplateDTO templateDTO = make(a(TemplateDTO));
		templateDTO = assignmentTemplateService.create(templateDTO);

		MvcResult mvcResult = mockMvc.perform(doGet(TEMPLATE_ENDPOINT).param("fields", "id", "name", "description"))
						.andExpect(status().isOk())
						.andReturn();

		Map<String, String> result = getFirstResult(mvcResult, mapType);

		assertThat(result, hasEntry("id", String.valueOf(templateDTO.getId())));
		assertThat(result, hasEntry("name", String.valueOf(templateDTO.getName())));
		assertThat(result, hasEntry("description", String.valueOf(templateDTO.getDescription())));
	}

	@Test
	public void getTemplatesWithOneField() throws Exception {
		TemplateDTO templateDTO = make(a(TemplateDTO));
		templateDTO = assignmentTemplateService.create(templateDTO);

		MvcResult mvcResult = mockMvc.perform(doGet(TEMPLATE_ENDPOINT).param("fields", "id"))
						.andExpect(status().isOk())
						.andReturn();

		Map<String, String> result = getFirstResult(mvcResult, mapType);

		assertThat(result, hasEntry("id", String.valueOf(templateDTO.getId())));
		assertThat(result, not(hasKey("name")));
		assertThat(result, not(hasKey("description")));
	}

	@Test
	public void getTemplatesWithTwoFields() throws Exception {
		TemplateDTO templateDTO = make(a(TemplateDTO));
		templateDTO = assignmentTemplateService.create(templateDTO);

		MvcResult mvcResult = mockMvc.perform(doGet(TEMPLATE_ENDPOINT).param("fields", "name", "description"))
						.andExpect(status().isOk())
						.andReturn();

		Map<String, String> result = getFirstResult(mvcResult, mapType);

		assertThat(result, not(hasKey("id")));
		assertThat(result, hasEntry("name", String.valueOf(templateDTO.getName())));
		assertThat(result, hasEntry("description", String.valueOf(templateDTO.getDescription())));
	}

	@Test
	public void getTemplatesWithWonkyField() throws Exception {
		TemplateDTO templateDTO = make(a(TemplateDTO));
		assignmentTemplateService.create(templateDTO);

		MvcResult mvcResult = mockMvc.perform(doGet(TEMPLATE_ENDPOINT).param("fields", "wonkyField"))
						.andExpect(status().isOk())
						.andReturn();

		Map<String, String> result = getFirstResult(mvcResult, mapType);

		assertThat(result, not(hasKey("wonkyField")));
	}

	@Test
	public void getTemplatesWithNoField() throws Exception {
		TemplateDTO templateDTO = make(a(TemplateDTO));
		assignmentTemplateService.create(templateDTO);

		MvcResult mvcResult = mockMvc.perform(doGet(TEMPLATE_ENDPOINT))
			.andExpect(status().isOk())
			.andReturn();

		Map<String, String> result = getFirstResult(mvcResult, mapType);

		assertThat(result, hasKey("id"));
		assertThat(result, hasEntry("name", templateDTO.getName()));
	}

	@Test
	public void getTemplatesWithEmptyField() throws Exception {
		TemplateDTO templateDTO = make(a(TemplateDTO));
		assignmentTemplateService.create(templateDTO);

		MvcResult mvcResult = mockMvc.perform(doGet(TEMPLATE_ENDPOINT).param("fields", ""))
			.andExpect(status().isOk())
			.andReturn();

		Map<String, String> result = getFirstResult(mvcResult, mapType);

		assertThat(result, hasKey("id"));
		assertThat(result, hasEntry("name", templateDTO.getName()));
	}

	@Test
	public void getTemplate() throws Exception {
		TemplateDTO templateDTO = make(a(TemplateDTO));
		templateDTO = assignmentTemplateService.create(templateDTO);
		AssignmentDTO assignmentDTO = templateDTO.getAssignment();
		LocationDTO locationDTO = assignmentDTO.getLocation();
		RoutingDTO routingDTO = assignmentDTO.getRouting();

		MvcResult mvcResult = mockMvc.perform(doGet(TEMPLATE_ENDPOINT + "/" + templateDTO.getId()))
						.andExpect(status().isOk())
						.andReturn();

		TemplateDTO templateResult = getFirstResult(mvcResult, templateType);
		AssignmentDTO assignmentResult = templateResult.getAssignment();
		LocationDTO locationResult = assignmentResult.getLocation();
		RoutingDTO routingResult = assignmentResult.getRouting();

		assertThat(templateResult, hasProperty("id", is(templateDTO.getId())));
		assertThat(templateResult, hasProperty("name", is(templateDTO.getName())));
		assertThat(templateResult, hasProperty("description", is(templateDTO.getDescription())));
		assertThat(assignmentResult, hasProperty("id", is(templateDTO.getId())));
		assertThat(assignmentResult, hasProperty("title", is(assignmentDTO.getTitle())));
		assertThat(assignmentResult, hasProperty("description", is(assignmentDTO.getDescription())));
		assertThat(assignmentResult, hasProperty("instructions", is(assignmentDTO.getInstructions())));
		assertThat(locationResult, hasProperty("id"));
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
		assertThat(assignmentResult, hasProperty("schedule", samePropertyValuesAs(assignmentDTO.getSchedule())));
		assertThat(assignmentResult, hasProperty("pricing", samePropertyValuesAs(assignmentDTO.getPricing())));
		assertThat(routingResult, hasProperty("groupIds", is(routingDTO.getGroupIds())));
		assertThat(routingResult, hasProperty("resourceNumbers", is(routingDTO.getResourceNumbers())));
		assertThat(routingResult, hasProperty("vendorCompanyNumbers", is(routingDTO.getVendorCompanyNumbers())));
		assertThat(routingResult, hasProperty("assignToFirstToAccept", is(routingDTO.isAssignToFirstToAccept())));
		assertThat(routingResult, hasProperty("shownInFeed", is(routingDTO.isShownInFeed())));
		assertThat(routingResult, hasProperty("smartRoute", is(routingDTO.isSmartRoute())));
		assertThat(routingResult, hasProperty("firstToAcceptCandidates", samePropertyValuesAs(routingDTO.getFirstToAcceptCandidates())));
		assertThat(routingResult, hasProperty("needToApplyCandidates", samePropertyValuesAs(routingDTO.getNeedToApplyCandidates())));
		assertThat(assignmentResult, hasProperty("customFieldGroups", samePropertyValuesAs(assignmentDTO.getCustomFieldGroups())));
		assertThat(assignmentResult, hasProperty("surveys", samePropertyValuesAs(assignmentDTO.getSurveys())));
		assertThat(assignmentResult, hasProperty("configuration", samePropertyValuesAs(assignmentDTO.getConfiguration())));
		assertThat(assignmentResult, hasProperty("documents", samePropertyValuesAs(assignmentDTO.getDocuments())));
	}

	@Test
	public void postValidTemplate() throws Exception {
		TemplateDTO templateDTO = make(a(TemplateDTO));
		String templateJson = jackson.writeValueAsString(templateDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(TEMPLATE_ENDPOINT).content(templateJson))
						.andExpect(status().isOk())
						.andReturn();

		TemplateDTO result = getFirstResult(mvcResult, templateType);

		assertThat(result, hasProperty("id"));
		assertThat(result, hasProperty("name", is(templateDTO.getName())));
		assertThat(result, hasProperty("description", is(templateDTO.getDescription())));
		assertThat(result.getAssignment(), hasProperty("title", is(templateDTO.getAssignment().getTitle())));
	}

	@Test
	public void postValidTemplateWithGroupRouting() throws Exception {
		TemplateDTO templateDTO = make(a(TemplateDTO,
			with(assignment, new AssignmentDTO.Builder(make(an(AssignmentDTO,
				with(routing, new RoutingDTO.Builder(make(a(RoutingDTO,
					with(groupIds, ImmutableSet.of(group.getId()))))))))))));
		String templateJson = jackson.writeValueAsString(templateDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(TEMPLATE_ENDPOINT).content(templateJson))
						.andExpect(status().isOk())
						.andReturn();

		RoutingDTO result = getFirstResult(mvcResult, templateType).getAssignment().getRouting();

		assertThat(result, hasProperty("groupIds", contains(group.getId())));
	}

	@Test
	public void postValidTemplateWithWorkerRouting() throws Exception {
		TemplateDTO templateDTO = make(a(TemplateDTO,
			with(assignment, new AssignmentDTO.Builder(make(an(AssignmentDTO,
				with(routing, new RoutingDTO.Builder(make(a(RoutingDTO,
					with(resourceNumbers, ImmutableSet.of(worker.getUserNumber()))))))))))));

		String templateJson = jackson.writeValueAsString(templateDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(TEMPLATE_ENDPOINT).content(templateJson))
						.andExpect(status().isOk())
						.andReturn();

		RoutingDTO result = getFirstResult(mvcResult, templateType).getAssignment().getRouting();

		assertThat(result, hasProperty("resourceNumbers", contains(worker.getUserNumber())));
	}

	@Test
	public void postValidTemplateWithShipmentToWorkerWithNoParts() throws Exception {

		ShipmentGroupDTO.Builder shipmentGroupBuilder = new ShipmentGroupDTO.Builder(make(a(ShipmentGroupMaker.ShipmentGroupDTO,
			with(shippingDestinationType, ShippingDestinationType.WORKER),
			withNull(shippingAddress),
			with(shipments, Lists.<ShipmentDTO.Builder>newArrayList()))));
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(shipmentGroup, shipmentGroupBuilder)));
		ShipmentGroupDTO shipmentGroupDTO = assignmentDTO.getShipmentGroup();
		TemplateDTO templateDTO = make(a(TemplateDTO, with(assignment, new AssignmentDTO.Builder(assignmentDTO))));
		String templateJson = jackson.writeValueAsString(templateDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(TEMPLATE_ENDPOINT).content(templateJson))
			.andExpect(status().isOk())
			.andReturn();

		TemplateDTO result = getFirstResult(mvcResult, templateType);
		ShipmentGroupDTO shipmentGroupResult = result.getAssignment().getShipmentGroup();

		assertThat(result, hasProperty("id"));
		assertThat(result, hasProperty("name", is(templateDTO.getName())));
		assertThat(result, hasProperty("description", is(templateDTO.getDescription())));
		assertThat(result.getAssignment(), hasProperty("title", is(templateDTO.getAssignment().getTitle())));
		assertThat(shipmentGroupResult, hasProperty("returnShipment", is(shipmentGroupDTO.isReturnShipment())));
		assertThat(shipmentGroupResult, hasProperty("shippingDestinationType", is(shipmentGroupDTO.getShippingDestinationType())));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", is(nullValue())));
		assertThat(shipmentGroupResult, hasProperty("shipments", is(empty())));
	}

	@Test
	public void postValidTemplateWithShipmentToOnsiteWithNoParts() throws Exception {

		ShipmentGroupDTO.Builder shipmentGroupBuilder = new ShipmentGroupDTO.Builder(make(a(ShipmentGroupMaker.ShipmentGroupDTO,
			with(shippingDestinationType, ShippingDestinationType.ONSITE),
			withNull(shippingAddress))));
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(shipmentGroup, shipmentGroupBuilder)));
		ShipmentGroupDTO shipmentGroupDTO = shipmentGroupBuilder.build();
		TemplateDTO templateDTO = make(a(TemplateDTO, with(assignment, new AssignmentDTO.Builder(assignmentDTO))));
		String templateJson = jackson.writeValueAsString(templateDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(TEMPLATE_ENDPOINT).content(templateJson))
			.andExpect(status().isOk())
			.andReturn();

		TemplateDTO result = getFirstResult(mvcResult, templateType);
		ShipmentGroupDTO shipmentGroupResult = result.getAssignment().getShipmentGroup();
		LocationDTO locationDTO = assignmentDTO.getLocation();

		assertThat(result, hasProperty("id"));
		assertThat(result, hasProperty("name", is(templateDTO.getName())));
		assertThat(result, hasProperty("description", is(templateDTO.getDescription())));
		assertThat(result.getAssignment(), hasProperty("title", is(templateDTO.getAssignment().getTitle())));
		assertThat(shipmentGroupResult, hasProperty("shippingDestinationType", is(shipmentGroupDTO.getShippingDestinationType())));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", hasProperty("id", is(Matchers.not(nullValue())))));
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
	public void postValidTemplateWithShipmentSuppliedByWorker() throws Exception {

		ShipmentGroupDTO.Builder shipmentGroupBuilder = new ShipmentGroupDTO.Builder(make(a(ShipmentGroupMaker.ShipmentGroupDTO,
			with(shippingDestinationType, ShippingDestinationType.NONE),
			with(suppliedByWorker, true),
			withNull(shippingAddress),
			withNull(returnAddress),
			with(shipments, Lists.<ShipmentDTO.Builder>newArrayList())
		)));
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(shipmentGroup, shipmentGroupBuilder)));
		ShipmentGroupDTO shipmentGroupDTO = shipmentGroupBuilder.build();
		TemplateDTO templateDTO = make(a(TemplateDTO, with(assignment, new AssignmentDTO.Builder(assignmentDTO))));
		String templateJson = jackson.writeValueAsString(templateDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(TEMPLATE_ENDPOINT).content(templateJson))
			.andExpect(status().isOk())
			.andReturn();

		TemplateDTO result = getFirstResult(mvcResult, templateType);
		ShipmentGroupDTO shipmentGroupResult = result.getAssignment().getShipmentGroup();
		List<ShipmentDTO> shipmentsResult = shipmentGroupResult.getShipments();

		assertThat(result, hasProperty("id"));
		assertThat(result, hasProperty("name", is(templateDTO.getName())));
		assertThat(result, hasProperty("description", is(templateDTO.getDescription())));
		assertThat(result.getAssignment(), hasProperty("title", is(templateDTO.getAssignment().getTitle())));
		assertThat(shipmentsResult, is(empty()));
		assertThat(shipmentGroupResult, hasProperty("suppliedByWorker", is(shipmentGroupDTO.isSuppliedByWorker())));
		assertThat(shipmentGroupResult, hasProperty("returnShipment", is(shipmentGroupDTO.isReturnShipment())));
		assertThat(shipmentGroupResult, hasProperty("shippingDestinationType", is(shipmentGroupDTO.getShippingDestinationType())));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", is(nullValue())));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", is(nullValue())));
	}

	@Test
	public void postValidTemplateWithShipmentSuppliedByWorkerAndWorkerReturnsParts() throws Exception {

		LocationDTO.Builder returnAddressDTOBuilder =  new LocationDTO.Builder(make(an(LocationMaker.LocationDTO, with(LocationMaker.name, "return"))));
		ShipmentGroupDTO.Builder shipmentGroupBuilder = new ShipmentGroupDTO.Builder(make(a(ShipmentGroupMaker.ShipmentGroupDTO,
			with(shippingDestinationType, ShippingDestinationType.NONE),
			with(suppliedByWorker, true),
			with(returnShipment, true),
			withNull(shippingAddress),
			with(returnAddress, returnAddressDTOBuilder),
			with(shipments, Lists.<ShipmentDTO.Builder>newArrayList()))));
		AssignmentDTO assignmentDTO = make(an(AssignmentDTO, with(shipmentGroup, shipmentGroupBuilder)));
		ShipmentGroupDTO shipmentGroupDTO = shipmentGroupBuilder.build();
		TemplateDTO templateDTO = make(a(TemplateDTO, with(assignment, new AssignmentDTO.Builder(assignmentDTO))));
		String templateJson = jackson.writeValueAsString(templateDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(TEMPLATE_ENDPOINT).content(templateJson))
			.andExpect(status().isOk())
			.andReturn();

		TemplateDTO result = getFirstResult(mvcResult, templateType);
		ShipmentGroupDTO shipmentGroupResult = result.getAssignment().getShipmentGroup();
		List<ShipmentDTO> shipmentsResult = shipmentGroupResult.getShipments();
		LocationDTO returnAddress = returnAddressDTOBuilder.build();

		assertThat(result, hasProperty("id"));
		assertThat(result, hasProperty("name", is(templateDTO.getName())));
		assertThat(result, hasProperty("description", is(templateDTO.getDescription())));
		assertThat(result.getAssignment(), hasProperty("title", is(templateDTO.getAssignment().getTitle())));
		assertThat(shipmentsResult, is(empty()));
		assertThat(shipmentGroupResult, hasProperty("suppliedByWorker", is(shipmentGroupDTO.isSuppliedByWorker())));
		assertThat(shipmentGroupResult, hasProperty("returnShipment", is(shipmentGroupDTO.isReturnShipment())));
		assertThat(shipmentGroupResult, hasProperty("shippingDestinationType", is(shipmentGroupDTO.getShippingDestinationType())));
		assertThat(shipmentGroupResult, hasProperty("shipToAddress", is((nullValue()))));
		assertThat(shipmentGroupResult, hasProperty("returnAddress", hasProperty("id", is(Matchers.not(nullValue())))));
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
	public void postInvalidTemplate() throws Exception {
		TemplateDTO templateDTO = make(a(TemplateDTO, withNull(name))); // - No Name

		String templateJson = jackson.writeValueAsString(templateDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(TEMPLATE_ENDPOINT).content(templateJson))
						.andExpect(status().isBadRequest())
						.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);

		expectApiErrorMessage(result, "name", "Name is a required field.");
		assertThat(result, hasProperty("resource", is("work")));
	}

	@Test
	public void postUpdateValidTemplate() throws Exception {
		TemplateDTO templateDTO = make(a(TemplateDTO));
		String templateJson = jackson.writeValueAsString(templateDTO);

		MvcResult createResult = mockMvc.perform(doPost(TEMPLATE_ENDPOINT).content(templateJson))
						.andExpect(status().isOk())
						.andReturn();

		templateDTO = new TemplateDTO.Builder(getFirstResult(createResult, templateType))
			.setDescription("This is the update!")
			.setAssignment(new AssignmentDTO.Builder(make(an(AssignmentDTO, with(title, "And a new Assignment title too!")))))
			.build();

		templateJson = jackson.writeValueAsString(templateDTO);

		MvcResult updateResult =
			mockMvc.perform(doPost(TEMPLATE_ENDPOINT + "/" + templateDTO.getId()).content(templateJson))
						.andExpect(status().isOk())
						.andReturn();

		TemplateDTO updateTemplateDTO = getFirstResult(updateResult, templateType);

		assertThat(updateTemplateDTO, hasProperty("id", is(templateDTO.getId())));
		assertThat(updateTemplateDTO, hasProperty("name", is(templateDTO.getName())));
		assertThat(updateTemplateDTO, hasProperty("description", is(templateDTO.getDescription())));
		assertThat(updateTemplateDTO.getAssignment(), hasProperty("title", is(templateDTO.getAssignment().getTitle())));
	}

	@Test
	public void postUpdateInvalidTemplate() throws Exception {
		TemplateDTO templateDTO = make(a(TemplateDTO));
		String templateJson = jackson.writeValueAsString(templateDTO);

		MvcResult createResult = mockMvc.perform(doPost(TEMPLATE_ENDPOINT).content(templateJson))
						.andExpect(status().isOk())
						.andReturn();

		templateDTO = new TemplateDTO.Builder(getFirstResult(createResult, templateType)).setName(null).build();

		templateJson = jackson.writeValueAsString(templateDTO);

		String endpoint = TEMPLATE_ENDPOINT + "/" + templateDTO.getId();
		MvcResult updateResult = mockMvc.perform(doPost(endpoint).content(templateJson))
						.andExpect(status().isBadRequest())
						.andReturn();

		ApiBaseError result = getFirstResult(updateResult, errorType);

		expectApiErrorMessage(result, "name", "Name is a required field.");
		assertThat(result, hasProperty("resource", is("work")));
	}

	@Test
	public void putUpdateValidTemplate() throws Exception {
		TemplateDTO templateDTO = make(a(TemplateDTO));
		String templateJson = jackson.writeValueAsString(templateDTO);

		MvcResult createResult = mockMvc.perform(doPost(TEMPLATE_ENDPOINT).content(templateJson))
						.andExpect(status().isOk())
						.andReturn();

		templateDTO = new TemplateDTO.Builder(getFirstResult(createResult, templateType))
			.setDescription("This is the update!")
			.setAssignment(new AssignmentDTO.Builder(make(an(AssignmentDTO, with(title, "And a new Assignment title too!")))))
			.build();

		templateJson = jackson.writeValueAsString(templateDTO);

		String endpoint = TEMPLATE_ENDPOINT + "/" + templateDTO.getId();
		MvcResult updateResult = mockMvc.perform(doPut(endpoint).content(templateJson))
						.andExpect(status().isOk())
						.andReturn();

		TemplateDTO updateTemplateDTO = getFirstResult(updateResult, templateType);

		assertThat(updateTemplateDTO, hasProperty("id", is(templateDTO.getId())));
		assertThat(updateTemplateDTO, hasProperty("name", is(templateDTO.getName())));
		assertThat(updateTemplateDTO, hasProperty("description", is(templateDTO.getDescription())));
		assertThat(updateTemplateDTO.getAssignment(), hasProperty("title", is(templateDTO.getAssignment().getTitle())));
	}

	@Test
	public void putUpdateInvalidTemplate() throws Exception {
		TemplateDTO templateDTO = make(a(TemplateDTO));
		String templateJson = jackson.writeValueAsString(templateDTO);

		MvcResult createResult = mockMvc.perform(doPost(TEMPLATE_ENDPOINT).content(templateJson))
						.andExpect(status().isOk())
						.andReturn();

		templateDTO = new TemplateDTO.Builder(getFirstResult(createResult, templateType))
			.setAssignment(new AssignmentDTO.Builder(make(an(AssignmentDTO, with(title, StringUtils.repeat("This title is far too long ", 10))))))
			.build();

		templateJson = jackson.writeValueAsString(templateDTO);

		String endpoint = TEMPLATE_ENDPOINT + "/" + templateDTO.getId();
		MvcResult updateResult = mockMvc.perform(doPut(endpoint).content(templateJson))
						.andExpect(status().isBadRequest())
						.andReturn();

		ApiBaseError result = getFirstResult(updateResult, errorType);

		expectApiErrorMessage(result, "title", "Title must be no more than 255.");
		assertThat(result, hasProperty("resource", is("work")));
	}
}
