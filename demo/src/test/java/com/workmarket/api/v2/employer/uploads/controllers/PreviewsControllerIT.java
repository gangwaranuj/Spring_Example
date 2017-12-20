package com.workmarket.api.v2.employer.uploads.controllers;

import com.google.common.collect.Lists;
import com.workmarket.api.v2.model.CustomFieldDTO;
import com.workmarket.api.v2.model.CustomFieldGroupDTO;
import com.workmarket.api.v2.employer.assignments.models.*;
import com.workmarket.api.v2.model.ContactDTO;
import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.api.v2.employer.uploads.models.MappingDTO;
import com.workmarket.api.v2.employer.uploads.models.PreviewDTO;
import com.workmarket.api.v2.employer.uploads.models.PreviewsDTO;
import com.workmarket.api.v2.employer.uploads.models.SettingsDTO;
import com.workmarket.api.v2.employer.uploads.services.PreviewStorageService;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.part.ShippingDestinationType;
import com.workmarket.domains.work.model.part.ShippingProvider;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.DateUtilities;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import static com.workmarket.api.v2.employer.assignments.services.AbstractAssignmentUseCase.DATE_TIME_FORMAT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@NotThreadSafe
public class PreviewsControllerIT extends BaseUploadsControllerIT {
	@Autowired PreviewStorageService previewStorageService;

	@Test
	public void getPreviews() throws Exception {
		String uuid = upload(this.csv);
		processPreviews(uuid);

		PreviewsDTO getResult = getPreviews(uuid);
		assertThat(getResult, hasProperty("uuid", is(uuid)));
		assertThat(getResult, hasProperty("count", is(2L)));
		assertThat(getResult, hasProperty("previews", hasSize(2)));
	}

	@Test
	public void getPreviewAssignments_basics() throws Exception {
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Instructions,Desired Skills,Support Contact ID,Owner ID,Industry ID\n")
			.append("Work 1,First Assignment,Do this,Housekeeping,111,222,333\n");
		String uuid = uploadCSV(csvContent);

		AssignmentDTO assignment = getFirstPreview(uuid);
		assertThat(assignment, hasProperty("title", is("Work 1")));
		assertThat(assignment, hasProperty("description", is("First Assignment")));
		assertThat(assignment, hasProperty("instructions", is("Do this")));
		assertThat(assignment, hasProperty("skills", is("Housekeeping")));
		assertThat(assignment, hasProperty("supportContactId", is("111")));
		assertThat(assignment, hasProperty("ownerId", is("222")));
		assertThat(assignment, hasProperty("industryId", is(333L)));
	}

	@Test
	public void getPreviewAssignments_pricing_flatPriceClientFee() throws Exception {
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Flat Price (including fees)\n")
			.append("\"Work 1\",\"Test description\",\"100.00\"\n");
		String uuid = uploadCSV(csvContent);

		AssignmentDTO assignment = getFirstPreview(uuid);
		PricingDTO pricing = assignment.getPricing();
		assertThat(pricing, hasProperty("flatPrice", is(100.00d)));
	}

	@Test
	public void getPreviewAssignments_pricing_flatPriceResourceFee() throws Exception {
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Flat Price (excluding fees)\n")
			.append("\"Work 1\",\"Test description\",\"100.00\"\n");
		String uuid = uploadCSV(csvContent);

		AssignmentDTO assignment = getFirstPreview(uuid);
		PricingDTO pricing = assignment.getPricing();
		assertThat(pricing, hasProperty("flatPrice", is(100.00d)));
	}

	@Test
	public void getPreviewAssignments_pricing_perHourPriceClientFee() throws Exception {
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Per Hour Price (including fees),Max Number of Hours\n")
			.append("\"Work 1\",\"Test description\",\"100.00\",\"8\"\n");
		String uuid = uploadCSV(csvContent);

		AssignmentDTO assignment = getFirstPreview(uuid);
		PricingDTO pricing = assignment.getPricing();
		assertThat(pricing, hasProperty("perHourPrice", is(100.00d)));
		assertThat(pricing, hasProperty("maxNumberOfHours", is(8.0)));
	}

	@Test
	public void getPreviewAssignments_pricing_perHourPriceResourceFee() throws Exception {
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Per Hour Price (excluding fees),Max Number of Hours\n")
			.append("\"Work 1\",\"Test description\",\"100.00\",\"8\"\n");
		String uuid = uploadCSV(csvContent);

		AssignmentDTO assignment = getFirstPreview(uuid);
		PricingDTO pricing = assignment.getPricing();

		assertThat(pricing, hasProperty("perHourPrice", is(100.00d)));
		assertThat(pricing, hasProperty("maxNumberOfHours", is(8.0)));
	}

	@Test
	public void getPreviewAssignments_pricing_blendedPerHourPriceClientFee() throws Exception {
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Initial Per Hour Price (including fees),Max Number of Hours at Initial Price,")
			.append("Additional Per Hour Price (including fees), Max Number of Hours at Additional Price\n")
			.append("\"Work 1\",\"Test Description\",\"10.00\",\"8\",\"15.00\",\"4\"\n");
		String uuid = uploadCSV(csvContent);

		AssignmentDTO assignment = getFirstPreview(uuid);
		PricingDTO pricing = assignment.getPricing();
		assertThat(pricing, hasProperty("initialPerHourPrice", is(10.00)));
		assertThat(pricing, hasProperty("initialNumberOfHours", is(8.0)));
		assertThat(pricing, hasProperty("additionalPerHourPrice", is(15.00)));
		assertThat(pricing, hasProperty("maxBlendedNumberOfHours", is(4.0)));
	}

	@Test
	@Ignore("this test flickers inexplicably")
	public void getPreviewAssignments_pricing_blendedPerHourPriceResourceFee() throws Exception {
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Initial Per Hour Price (excluding fees),Max Number of Hours at Initial Price,")
			.append("Additional Per Hour Price (excluding fees), Max Number of Hours at Additional Price\n")
			.append("\"Work 1\",\"Test description\",\"10.00\",\"8\",\"15.00\",\"4\"\n");
		String uuid = uploadCSV(csvContent);

		AssignmentDTO assignment = getFirstPreview(uuid);
		PricingDTO pricing = assignment.getPricing();
		assertThat(pricing, hasProperty("initialPerHourPrice", is(10.00)));
		assertThat(pricing, hasProperty("initialNumberOfHours", is(8.0)));
		assertThat(pricing, hasProperty("additionalPerHourPrice", is(15.00)));
		assertThat(pricing, hasProperty("maxBlendedNumberOfHours", is(4.0)));
	}

	@Test
	public void getPreviewAssignments_pricing_unitPriceClientFee() throws Exception {
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Per Unit Price (including fees),Max Number of Units\n")
			.append("\"Work 1\",\"Test description\",\"10.00\",\"8\"\n");
		String uuid = uploadCSV(csvContent);
		AssignmentDTO assignment = getFirstPreview(uuid);

		PricingDTO pricing = assignment.getPricing();
		assertThat(pricing, hasProperty("perUnitPrice", is(10.00)));
		assertThat(pricing, hasProperty("maxNumberOfUnits", is(8.0)));
	}

	@Test
	@Ignore("this test flickers inexplicably")
	public void getPreviewAssignments_pricing_unitPriceResourceFee() throws Exception {
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Per Unit Price (excluding fees),Max Number of Units\n")
			.append("\"Work 1\",\"Test description\",\"10.00\",\"8\"\n");
		String uuid = uploadCSV(csvContent);
		AssignmentDTO assignment = getFirstPreview(uuid);

		PricingDTO pricing = assignment.getPricing();
		assertThat(pricing, hasProperty("perUnitPrice", is(10.00)));
		assertThat(pricing, hasProperty("maxNumberOfUnits", is(8.0)));
	}

	@Test
	public void getPreviewAssignments_routing() throws Exception {
		User worker1 = newRegisteredWorker();
		User worker2 = newRegisteredWorker();
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Worker IDs\n")
			.append("\"Work 1\",\"Test description\",\"" + worker1.getUserNumber() + "," + worker2.getUserNumber() + "\"\n");
		String uuid = uploadCSV(csvContent);

		AssignmentDTO assignment = getFirstPreview(uuid);
		RoutingDTO routing = assignment.getRouting();
		assertThat(routing.getResourceNumbers(), hasSize(2));
	}

	@Test
	public void getPreviewAssignments_locationAndContact() throws Exception {
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Location Address 1,Location Address 2,Location City,Location State/Province,")
			.append("Location Postal Code,Location Country,Location Number,Location Name,Email (primary contact),")
			.append("First Name (primary contact),Last Name (primary contact),Phone (primary contact),")
			.append("Phone Extension (primary contact)\n")
			.append("\"Work 1\",\"Test description\",\"7 High St\",\"Suite 407\",\"Huntington\",\"NY\",\"11743\",\"US\",\"11111\",")
			.append("\"Work Market\",\"frank.rizzo@workmarket.com\",\"Frank\",\"Rizzo\",\"555-555-5785\",\"2222\"\n");
		String uuid = uploadCSV(csvContent);

		AssignmentDTO assignment = getFirstPreview(uuid);
		LocationDTO location = assignment.getLocation();
		assertThat(location, hasProperty("name", is("Work Market")));
		assertThat(location, hasProperty("number", is("11111")));
		assertThat(location, hasProperty("addressLine1", is("7 High St")));
		assertThat(location, hasProperty("addressLine2", is("Suite 407")));
		assertThat(location, hasProperty("city", is("Huntington")));
		assertThat(location, hasProperty("state", is("NY")));
		assertThat(location, hasProperty("zip", is("11743")));
		assertThat(location, hasProperty("country", is("US")));
	}

	@Test
	public void getPreviewAssignments_contact() throws Exception {
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Email (primary contact),First Name (primary contact),Last Name (primary contact),")
			.append("Phone (primary contact),Phone Extension (primary contact)\n")
			.append("\"Work 1\",\"Test Description\",\"frank.rizzo@workmarket.com\",\"Frank\",\"Rizzo\",\"555-555-5785\",\"11111\"\n");
		String uuid = uploadCSV(csvContent);

		AssignmentDTO assignment = getFirstPreview(uuid);
		LocationDTO location = assignment.getLocation();
		ContactDTO contact = location.getContact();
		assertThat(contact, hasProperty("email", is("frank.rizzo@workmarket.com")));
		assertThat(contact, hasProperty("firstName", is("Frank")));
		assertThat(contact, hasProperty("lastName", is("Rizzo")));
		assertThat(contact, hasProperty("workPhone", is("555-555-5785")));
		assertThat(contact, hasProperty("workPhoneExtension", is("11111")));
	}

	@Test
	public void getPreviewAssignments_secondaryContact() throws Exception {
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Email (secondary contact), First Name (secondary contact),Last Name (secondary contact),")
			.append("Phone (secondary contact),Phone Extension (secondary contact)\n")
			.append("\"Work 1\",\"Test description\",\"frank.rizzo@workmarket.com\",\"Frank\",\"Rizzo\",\"555-555-5785\",\"11111\"\n");
		String uuid = uploadCSV(csvContent);

		AssignmentDTO assignment = getFirstPreview(uuid);
		LocationDTO location = assignment.getLocation();
		ContactDTO contact = location.getSecondaryContact();
		assertThat(contact, hasProperty("email", is("frank.rizzo@workmarket.com")));
		assertThat(contact, hasProperty("firstName", is("Frank")));
		assertThat(contact, hasProperty("lastName", is("Rizzo")));
		assertThat(contact, hasProperty("workPhone", is("555-555-5785")));
		assertThat(contact, hasProperty("workPhoneExtension", is("11111")));
	}

	@Test
	public void getPreviewAssignments_schedule_separateDateTime() throws Exception {
		String startDate = "2016-09-12";
		String startTime = "12:00PM";
		String endDate = "2016-09-13";
		String endTime = "1:00PM";
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Start Date,Start Time,End Date,End Time\n")
			.append("\"Work 1\",\"Test description\",\"" + startDate + "\",\"" + startTime + "\",\"" + endDate + "\",\"" + endTime + "\"\n");
		String uuid = uploadCSV(csvContent);

		AssignmentDTO assignment = getFirstPreview(uuid);
		ScheduleDTO schedule = assignment.getSchedule();
		Calendar startCal = DateUtilities.getCalendarFromDateTimeString(
			String.format("%s %s", startDate, startTime), Constants.DEFAULT_TIMEZONE);
		Calendar throughCal = DateUtilities.getCalendarFromDateTimeString(
			String.format("%s %s", endDate, endTime), Constants.DEFAULT_TIMEZONE);
		String start = DateUtilities.format(DATE_TIME_FORMAT, startCal);
		String through = DateUtilities.format(DATE_TIME_FORMAT, throughCal);

		assertThat(schedule, hasProperty("from", is(start)));
		assertThat(schedule, hasProperty("through", is(through)));
	}

	@Test
	public void getPreviewAssignments_schedule_dateTime() throws Exception {
		String startDateTime = "2016-09-12 12:00PM";
		String endDateTime = "2016-09-13 1:00PM";
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Start Date & Time,End Date & Time\n")
			.append("\"Work 1\",\"Test description\",\"" + startDateTime + "\",\"" + endDateTime + "\"\n");
		String uuid = uploadCSV(csvContent);

		AssignmentDTO assignment = getFirstPreview(uuid);
		ScheduleDTO schedule = assignment.getSchedule();

		Calendar startCal = DateUtilities.getCalendarFromDateTimeString(
			startDateTime, Constants.DEFAULT_TIMEZONE);
		Calendar throughCal = DateUtilities.getCalendarFromDateTimeString(
			endDateTime, Constants.DEFAULT_TIMEZONE);
		String start = DateUtilities.format(DATE_TIME_FORMAT, startCal);
		String through = DateUtilities.format(DATE_TIME_FORMAT, throughCal);
		assertThat(schedule, hasProperty("from", is(start)));
		assertThat(schedule, hasProperty("through", is(through)));
	}

	@Test
	public void getPreviewAssignments_partsSuppliedByWorker() throws Exception {
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Supplied By Resource,Pickup Tracking Number\n")
			.append("\"Work 1\",\"Test description\",\"true\",\"123456789\"\n");
		String uuid = uploadCSV(csvContent);
		AssignmentDTO assignment = getFirstPreview(uuid);

		ShipmentGroupDTO shipmentGroupDTO = assignment.getShipmentGroup();
		List<ShipmentDTO> shipments = shipmentGroupDTO.getShipments();
		assertThat(shipments, hasSize(1));
		ShipmentDTO shipment = shipments.iterator().next();
		assertThat(shipmentGroupDTO, hasProperty("suppliedByWorker", is(true)));
		assertThat(shipment, hasProperty("trackingNumber", is("123456789")));
		assertThat(shipmentGroupDTO, hasProperty("returnShipment", is(false)));
	}

	@Test
	public void getPreviewAssignments_partsByDistributionMethod() throws Exception {
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Location Address 1,Location Address 2,Location City,Location State/Province,")
			.append("Location Postal Code,Location Country,Location Number,Location Name,Email (primary contact),")
			.append("First Name (primary contact),Last Name (primary contact),Phone (primary contact),")
			.append("Phone Extension (primary contact),Distribution Method,Pickup Tracking Number\n")
			.append("\"Work 1\",\"Test description\",\"7 High St\",\"Suite 407\",\"Huntington\",\"NY\",\"11743\",\"US\",\"11111\",")
			.append("\"Work Market\",\"frank.rizzo@workmarket.com\",\"Frank\",\"Rizzo\",\"555-555-5785\",\"2222\",")
			.append("\"ONSITE\",\"123456789\"\n");
		String uuid = uploadCSV(csvContent);
		AssignmentDTO assignment = getFirstPreview(uuid);

		ShipmentGroupDTO shipmentGroupDTO = assignment.getShipmentGroup();
		List<ShipmentDTO> shipments = shipmentGroupDTO.getShipments();
		assertThat(shipments, hasSize(1));
		assertThat(shipmentGroupDTO, hasProperty("shippingDestinationType", is(ShippingDestinationType.ONSITE)));
		assertThat(shipmentGroupDTO, hasProperty("returnShipment", is(false)));
	}

	@Test
	public void getPreviewAssignments_partsByPickupLocation() throws Exception {
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Pickup Location Name,Pickup Location Number,Pickup Location Address 1,")
			.append("Pickup Location Address 2,Pickup Location City,Pickup Location State/Province,Pickup Location Postal Code,")
			.append("Pickup Location Country,Pickup Tracking Number,Pickup Shipping Provider,Pickup Part Value\n")
			.append("\"Work 1\",\"Test description\",\"Work Market\",\"11111\",\"7 High St\",\"Suite 407\",\"Huntington\",\"NY\",\"11743\",\"US\",")
			.append("\"123456789\",\"UPS\",\"100.00\"\n");

		String uuid = uploadCSV(csvContent);
		AssignmentDTO assignment = getFirstPreview(uuid);

		ShipmentGroupDTO shipmentGroupDTO = assignment.getShipmentGroup();
		List<ShipmentDTO> shipments = shipmentGroupDTO.getShipments();
		assertThat(shipments, hasSize(1));
		ShipmentDTO shipment = shipments.iterator().next();
		assertThat(shipment, hasProperty("trackingNumber", is("123456789")));
		assertThat(shipment, hasProperty("shippingProvider", is(ShippingProvider.UPS)));
		assertThat(shipmentGroupDTO, hasProperty("returnShipment", is(false)));
		LocationDTO location = shipmentGroupDTO.getShipToAddress();
		assertThat(location, is(not(nullValue())));
		assertThat(location, hasProperty("name", is("Work Market")));
		assertThat(location, hasProperty("number", is("11111")));
		assertThat(location, hasProperty("addressLine1", is("7 High St")));
		assertThat(location, hasProperty("addressLine2", is("Suite 407")));
		assertThat(location, hasProperty("city", is("Huntington")));
		assertThat(location, hasProperty("state", is("NY")));
		assertThat(location, hasProperty("zip", is("11743")));
		assertThat(location, hasProperty("country", is("US")));
	}

	@Test
	public void getPreviewAssignments_partsByPickupAndReturnLocation() throws Exception {
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Pickup Location Name,Pickup Location Number,Pickup Location Address 1,")
			.append("Pickup Location Address 2,Pickup Location City,Pickup Location State/Province,")
			.append("Pickup Location Postal Code,Pickup Location Country,Pickup Tracking Number,")
			.append("Pickup Shipping Provider,Pickup Part Value,Return Location Name,Return Location Number,")
			.append("Return Location Address 1,Return Location Address 2,Return Location City,")
			.append("Return Location State/Province,Return Location Postal Code,Return Location Country,")
			.append("Return Tracking Number,Return Shipping Provider,Return Part Value\n")
			.append("\"Work 1\",\"Test description\",\"Work Market\",\"11111\",\"7 High St\",\"Suite 407\",\"Huntington\",\"NY\",")
			.append("\"11743\",\"US\",\"123456789\",\"UPS\",\"100.00\",\"Work Market\",\"11111\",\"7 High St\",")
			.append("\"Suite 407\",\"Huntington\",\"NY\",\"11743\",\"US\",\"987654321\",\"UPS\",\"100.00\"\n");
		String uuid = uploadCSV(csvContent);
		AssignmentDTO assignment = getFirstPreview(uuid);

		ShipmentGroupDTO shipmentGroupDTO = assignment.getShipmentGroup();
		List<ShipmentDTO> shipments = shipmentGroupDTO.getShipments();
		assertThat(shipments, hasSize(2));
		ShipmentDTO shipment = shipments.iterator().next();
		assertThat(shipment, hasProperty("trackingNumber", is("987654321")));
		assertThat(shipment, hasProperty("shippingProvider", is(ShippingProvider.UPS)));
		assertThat(shipmentGroupDTO, hasProperty("returnShipment", is(true)));
		LocationDTO location = shipmentGroupDTO.getShipToAddress();
		assertThat(location, is(not(nullValue())));
		assertThat(location, hasProperty("name", is("Work Market")));
		assertThat(location, hasProperty("number", is("11111")));
		assertThat(location, hasProperty("addressLine1", is("7 High St")));
		assertThat(location, hasProperty("addressLine2", is("Suite 407")));
		assertThat(location, hasProperty("city", is("Huntington")));
		assertThat(location, hasProperty("state", is("NY")));
		assertThat(location, hasProperty("zip", is("11743")));
		assertThat(location, hasProperty("country", is("US")));
	}


	@Test
	public void createPreviewAssignment_invalid() throws Exception {
		StringBuilder csvContent = new StringBuilder()
			.append("Title,Description,Instructions,Desired Skills,Support Contact ID,Owner ID,Industry ID\n")
			.append("Work 1,First Assignment,Do this,Housekeeping,111,222,333\n")
			.append("Work 2,Second Assignment,Do that,Bookkeeping,555,555,666\n");
		String uuid = uploadCSV(csvContent);
		processAssignments(uuid);

		List<PreviewDTO> rowPreviews = previewStorageService.get(uuid, 0, 10);
		assertThat(rowPreviews, hasSize(2));
	}

	@Test
	public void getPreviewAssignmentRows_mapped() throws Exception {
		String titleHeader = "MyTitle";
		String descriptionHeader = "MyDescription";
		String instructionsHeader = "MyInstructions";
		String skillsHeader = "MySkills";
		String industryHeader = "MyIndustry";
		List<MappingDTO> mappingList = Lists.newArrayList();
		mappingList.add(new MappingDTO.Builder().setProperty("title").setHeader(titleHeader).build());
		mappingList.add(new MappingDTO.Builder().setProperty("description").setHeader(descriptionHeader).build());
		mappingList.add(new MappingDTO.Builder().setProperty("instructions").setHeader(instructionsHeader).build());
		mappingList.add(new MappingDTO.Builder().setProperty("desired_skills").setHeader(skillsHeader).build());
		mappingList.add(new MappingDTO.Builder().setProperty("industry_id").setHeader(industryHeader).build());
		StringBuilder csvContent = new StringBuilder()
			.append(String.format("%s,%s,%s,%s,%s\n", titleHeader, descriptionHeader, instructionsHeader, skillsHeader, industryHeader))
			.append("Work 1,First Assignment,Do this,Bookkeeping,111\n");
		String uuid = uploadCSV(csvContent);

		AssignmentDTO assignment = getFirstAssignmentAdHocMapping(uuid, mappingList);
		assertThat(assignment, hasProperty("title", is("Work 1")));
		assertThat(assignment, hasProperty("description", is("First Assignment")));
		assertThat(assignment, hasProperty("instructions", is("Do this")));
		assertThat(assignment, hasProperty("skills", is("Bookkeeping")));
		assertThat(assignment, hasProperty("industryId", is(111L)));
	}

	@Test
	public void getPreviewAssignmentRows_customFields() throws Exception {
		TemplateDTO template = createTemplate();

		StringBuilder csvContent = new StringBuilder();
		List<MappingDTO> mappingList = Lists.newArrayList();
		int fieldCount = 0;
		for(CustomFieldGroupDTO customFieldGroup : template.getAssignment().getCustomFieldGroups()) {
			for (CustomFieldDTO customField : customFieldGroup.getFields()) {
				// add field to header
				csvContent.append(String.format("custom field %s,", fieldCount));
				// add field to mapping
				/*
  				TODO JL - I changed this test because deep inside the upload stuff, the template fields need to match with
  				          the template we created to start the test.
				 */
				mappingList.add(new MappingDTO.Builder()
					.setProperty(String.format("%s:%d", WorkUploadColumn.CUSTOM_FIELD.getUploadColumnName(), customField.getId()))
					.setHeader(String.format("custom field %s", fieldCount)).build());
				fieldCount++;
			}
		}
		csvContent.deleteCharAt(csvContent.length()-1).append("\n");
		for(int i = 0;i < fieldCount;i++) {
			csvContent.append(String.format("%d,", i));
		}
		csvContent.deleteCharAt(csvContent.length()-1).append("\n");
		String uuid = uploadCSV(csvContent);
		setSettings(uuid, template.getId());

		AssignmentDTO assignment = getFirstAssignmentAdHocMapping(uuid, mappingList);
		Set<CustomFieldGroupDTO> customFieldGroups = assignment.getCustomFieldGroups();
		assertThat(customFieldGroups, hasSize(1));
		Set<CustomFieldDTO> customFields = customFieldGroups.iterator().next().getFields();
		// TODO JL - I changed this test - see above
		assertThat(customFields, hasSize(3));
	}

	@Test
	public void createPreviews_getPreviews() throws Exception {
		String uuid = upload(this.csv);
		processPreviews(uuid);

		PreviewsDTO result = getPreviews(uuid);
		assertThat(result, hasProperty("previews", hasSize(2)));
		assertThat(result, hasProperty("uuid", is(uuid)));
		assertThat(result, hasProperty("count", is(2L)));
		assertThat(result.getPreviews().get(0).getAssignmentDTO(), is(not(nullValue())));
		assertThat(hasParseErrors(result), is(false));
		assertThat(hasValidationErrors(result), is(false));
	}

	private SettingsDTO setSettings(String uuid, String templateId) throws Exception {
		SettingsDTO settingsDTO = new SettingsDTO.Builder().setTemplateId(templateId).build();
		String settingsJSON = jackson.writeValueAsString(settingsDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT + "/" + uuid + "/settings")
				.content(settingsJSON)
		).andExpect(status().isOk()).andReturn();

		return getFirstResult(mvcResult, settingsType);
	}
}
