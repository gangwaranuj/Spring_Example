package com.workmarket.api.v2.worker.marshaller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.workmarket.api.v2.ApiV2Pagination;
import com.workmarket.api.v2.worker.fulfillment.FulfillmentPayloadDTO;
import com.workmarket.data.solr.model.GeoPoint;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.PrivacyType;
import com.workmarket.domains.model.VisibilityType;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.assessment.AttemptStatusType;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.model.asset.type.WorkAssetAssociationType;
import com.workmarket.domains.model.directory.ContactContextType;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.work.model.part.ShippingProvider;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.dto.DeliverableRequirementDTO;
import com.workmarket.service.business.dto.DeliverableRequirementGroupDTO;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.business.dto.PartGroupDTO;
import com.workmarket.service.business.dto.WorkBundleDTO;
import com.workmarket.thrift.assessment.Assessment;
import com.workmarket.thrift.assessment.AssessmentAttemptPair;
import com.workmarket.thrift.assessment.Attempt;
import com.workmarket.thrift.core.Address;
import com.workmarket.thrift.core.Asset;
import com.workmarket.thrift.core.Company;
import com.workmarket.thrift.core.DeliverableAsset;
import com.workmarket.thrift.core.Location;
import com.workmarket.thrift.core.Name;
import com.workmarket.thrift.core.Note;
import com.workmarket.thrift.core.Phone;
import com.workmarket.thrift.core.Profile;
import com.workmarket.thrift.core.Status;
import com.workmarket.thrift.core.User;
import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.thrift.work.ManageMyWorkMarket;
import com.workmarket.thrift.work.Negotiation;
import com.workmarket.thrift.work.PaymentSummary;
import com.workmarket.thrift.work.PricingStrategy;
import com.workmarket.thrift.work.Resource;
import com.workmarket.thrift.work.Schedule;
import com.workmarket.thrift.work.SubStatus;
import com.workmarket.thrift.work.TimeTrackingEntry;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkMilestones;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.web.validators.FastFundsValidator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AssignmentMarshallerTest {

	private AssignmentMarshaller marshaller;
	private List<Map<String, Object>> serviceListResults;
	private WorkResponse serviceDetailsResults;
	List <PartDTO> assignmentParts;
	private AssetManagementService assetManagementService;
    private FastFundsValidator fastFundsValidator;
    private SecurityContextFacade securityContextFacade;

    private static final Integer PAGE = 5;
    private static final Integer PAGE_SIZE = 25;
    private static final Integer TOTAL_PAGES = 15;
    private static final Integer TOTAL_RESULTS = 150;

    @Before
    public void setup() {
        Locale.setDefault(Locale.US);
        assetManagementService = mock(AssetManagementService.class);
        fastFundsValidator = mock(FastFundsValidator.class);
        securityContextFacade = mock(SecurityContextFacade.class);
        ExtendedUserDetails userDetails = mock(ExtendedUserDetails.class);
        when(userDetails.isCompanyHidesPricing()).thenReturn(false);
        when(securityContextFacade.getCurrentUser()).thenReturn(userDetails);
        when(assetManagementService.findAllAssetAssociationsByWorkId(anyList())).thenReturn(new ArrayList<WorkAssetAssociation>());
        when(fastFundsValidator.isWorkFastFundable(anyString())).thenReturn(false);
        marshaller = new AssignmentMarshaller(assetManagementService, fastFundsValidator, securityContextFacade);
    }

    @Test
    public void marshallServiceResponseList_goodResponse_goodMarshall() {

        setupAssignmentsServiceListResults();

        final FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();
        marshaller.getListFulfillmentResponse(serviceListResults, null, response);

        final List marshalledAssignments = response.getPayload();
        assertEquals(2, marshalledAssignments.size());

        final Map result = (Map)marshalledAssignments.get(0);
        assertEquals("7654930", result.get("id"));
        assertEquals("Test Assignment Title", result.get("title"));
        assertEquals("active", result.get("status"));
        assertEquals(Boolean.FALSE, result.get("locationOffsite"));
        assertNull(result.get("workBundleId"));

        final Map company = (Map)result.get("company");
        assertEquals("6060842", company.get("id"));
        assertEquals("Amalgamated Bank & Rail", company.get("name"));

        final Map location = (Map)result.get("location");
        assertEquals("Lodi", location.get("city"));
        assertEquals("NJ", location.get("state"));
        assertEquals("40039", location.get("postalCode"));
        assertEquals("413 Combo Lane", location.get("address1"));

        final Map coordinates = (Map) location.get("coordinates");
        assertEquals(78.728D, coordinates.get("longitude"));
        assertEquals(80.9290D, coordinates.get("latitude"));

        final Map pricing = (Map)result.get("pricing");
        assertEquals(500L, pricing.get("flatPrice"));
        assertNull(pricing.get("perHour"));
        assertNull(pricing.get("perUnit"));
        assertNull(pricing.get("blendedPerHour"));
        assertEquals(Boolean.FALSE, pricing.get("internal"));

        //final Map schedule = (Map)result.get("schedule");
        //assertEquals(1463317200000L, schedule.get("startWindowBegin"));
        //assertEquals(1463396400000L, schedule.get("startWindowEnd"));

        final Map payment = (Map)result.get("payment");
        assertEquals(520.00D, payment.get("total"));
        assertEquals(1463738400000L, payment.get("paidDate"));
        assertEquals(1463875140000L, payment.get("dueDate"));
        assertEquals(Boolean.TRUE, payment.get("paymentTermsEnabled"));
        assertEquals(9, payment.get("paymentTermsDays"));

        // TODO: milestones comes from mysql, not solr
        //final Map milestones = (Map)result.get("milestones");
        //assertEquals(1462491300000L, milestones.get("sentDate"));
    }

    @Test
    public void marshallServiceResponseList_DifferentPricingType_DifferentResults() {

        final Map<String, Object> row1 = new HashMap<>();
        row1.put("work_id",1L);
        row1.put("pricing_type", "Hourly");
        row1.put("price", "$30.15");

        final Map<String, Object> row2 = new HashMap<>();
        row2.put("work_id",1L);
        row2.put("pricing_type", "Unit");
        row2.put("price", "$75.56");

        final Map<String, Object> row3 = new HashMap<>();
        row3.put("work_id",1L);
        row3.put("pricing_type", "Blended");
        row3.put("price", "$25");

        final Map<String, Object> row4 = new HashMap<>();
        row4.put("work_id",1L);
        row4.put("pricing_type", "Internal");
        row4.put("price", "-");

        final List<Map<String, Object>> results = ImmutableList.of(row1, row2, row3, row4);

        final FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();
        marshaller.getListFulfillmentResponse(results, null, response);

        final List marshalledAssignments = response.getPayload();
        assertEquals(4, marshalledAssignments.size());

        Map pricing = (Map)((Map)marshalledAssignments.get(0)).get("pricing");
        assertEquals(30.15D, pricing.get("perHour"));
        assertNull(pricing.get("flatPrice"));
        assertNull(pricing.get("perUnit"));
        assertNull(pricing.get("blendedPerHour"));
        assertEquals(Boolean.FALSE, pricing.get("internal"));

        pricing = (Map)((Map)marshalledAssignments.get(1)).get("pricing");
        assertEquals(75.56D, pricing.get("perUnit"));
        assertNull(pricing.get("flatPrice"));
        assertNull(pricing.get("perHour"));
        assertNull(pricing.get("blendedPerHour"));
        assertEquals(Boolean.FALSE, pricing.get("internal"));

        pricing = (Map)((Map)marshalledAssignments.get(2)).get("pricing");
        assertEquals(25L, pricing.get("blendedPerHour"));
        assertNull(pricing.get("flatPrice"));
        assertNull(pricing.get("perUnit"));
        assertNull(pricing.get("perHour"));
        assertEquals(Boolean.FALSE, pricing.get("internal"));

        pricing = (Map)((Map)marshalledAssignments.get(3)).get("pricing");
        assertNull(pricing.get("flatPrice"));
        assertNull(pricing.get("perHour"));
        assertNull(pricing.get("perUnit"));
        assertNull(pricing.get("blendedPerHour"));
        assertEquals(Boolean.TRUE, pricing.get("internal"));
    }

    @Test
    public void generatePagination_goodData_goodResult() {

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("www.workmarket.com");
        request.setServerPort(80);
        request.setRequestURI("/worker/v2/assignments");

        final Map servicePageResults = ImmutableMap.of
            (
                "page", PAGE,
                "pageSize", PAGE_SIZE,
                "totalPages", TOTAL_PAGES,
                "totalResults", TOTAL_RESULTS
            );

        final ApiV2Pagination pagination = marshaller
            .generatePaginationFromWorkControllerResponse(servicePageResults,request);

        assertEquals(new Long(PAGE), pagination.getPage());
        assertEquals(new Long(PAGE_SIZE), pagination.getPageSize());
        assertEquals(new Long(TOTAL_PAGES), pagination.getTotalPageCount());
        assertEquals(new Long(TOTAL_RESULTS), pagination.getTotalRecordCount());
    }

    @Test
    public void marshallServiceResponseDetails_goodResponse_goodMarshall() {

        setupAssignmentsDetailsServiceResults();
        setupAssignmentDetailsParts();
        setupAssignmentDetailsCustomFields();

        final FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();
        marshaller.getDetailsFulfillmentResponse(serviceDetailsResults, null, assignmentParts, response);

		final List marshalledAssignments = response.getPayload();
		assertEquals(1, marshalledAssignments.size());

        final Map result = (Map)marshalledAssignments.get(0);

        assertEquals("6060842", result.get("id"));
        assertEquals("Test work response", result.get("title"));
        assertEquals("This is a test description", result.get("description"));
        assertEquals("ACCEPTED", result.get("status"));
        assertEquals("Do this work well.", result.get("specialInstructions"));
        assertEquals("Lion Taming, fire eating", result.get("desiredSkills"));
        assertFalse((Boolean)result.get("locationOffsite"));
        assertEquals(567839202L, result.get("confirmedDate"));
        assertEquals(7492092308L, result.get("confirmWindowStart"));
        assertEquals(810762310987L, result.get("confirmWindowEnd"));
        assertTrue((Boolean)result.get("enablePrintout"));
        assertFalse((Boolean)result.get("enablePrintSignature"));
        assertFalse((Boolean)result.get("workBundle"));
        assertEquals(891248974L, result.get("workBundleId"));
        assertFalse((Boolean)result.get("workerSuppliesParts"));

        Map<String, Object> company = (Map<String, Object>)result.get("company");

        assertEquals(784302973L, company.get("id"));
        assertEquals("Test Company X", company.get("name"));
        assertEquals("f6b00ce1-4b9a-4537-a2e1-328b331310a5", company.get("uuid"));
        assertEquals("Work for Test Company X, We Pay On Time", company.get("customSignatureLine"));

        company = (Map<String, Object>)result.get("clientCompany");

        assertEquals(12357628792L, company.get("id"));
        assertEquals("X's Client", company.get("name"));

        Map<String, Object> schedule = (Map<String, Object>)result.get("schedule");

        assertEquals(289374987L, schedule.get("startWindowBegin"));
        assertEquals(398479830L, schedule.get("startWindowEnd"));
        assertNull(schedule.get("start"));
        assertNull(schedule.get("end"));

        final Map<String, Object> location = (Map<String, Object>)result.get("location");

        assertEquals(20923480L, location.get("id"));
        assertEquals("1132 Fulton St.", location.get("address1"));
        assertEquals("Apt. 3", location.get("address2"));
        assertEquals("San Francisco", location.get("city"));
        assertEquals("CA", location.get("state"));
        assertEquals("10023", location.get("postalCode"));
        assertEquals("USA", location.get("country"));
        assertEquals("San Francisco", location.get("name"));
        assertEquals("123456", location.get("locationNumber"));

        Map<String, Object> coordinates = (Map<String, Object>) location.get("coordinates");

        assertEquals(23.54, coordinates.get("latitude"));
        assertEquals(54.23, coordinates.get("longitude"));

        final List<Map<String, Object>> locationContacts = (List<Map<String, Object>>) location.get("contacts");

        Map<String, Object> locationContact = locationContacts.get(0);

        assertEquals("Location", locationContact.get("firstName"));
        assertEquals("Contact", locationContact.get("lastName"));
        assertEquals("876143", locationContact.get("userNumber"));
        assertEquals("location_contact@company.com", locationContact.get("email"));

        List<Map<String,Object>> phoneNumbers = (List<Map<String,Object>>) locationContact.get("phoneNumbers");

        Map<String,Object> phone = phoneNumbers.get(0);

        assertEquals("932-320-0938", phone.get("number"));
        assertEquals(ContactContextType.WORK.name(), phone.get("type"));
        assertEquals("1542", phone.get("extension"));

        phone = phoneNumbers.get(1);

        assertEquals("415-443-2394", phone.get("number"));
        assertEquals(ContactContextType.OTHER.name(), phone.get("type"));

        locationContact = locationContacts.get(1);

        assertEquals("Secondary", locationContact.get("firstName"));
        assertEquals("Contact", locationContact.get("lastName"));
        assertEquals("341678", locationContact.get("userNumber"));
        assertEquals("secondary_contact@company.com", locationContact.get("email"));

        phoneNumbers = (List<Map<String,Object>>) locationContact.get("phoneNumbers");
        phone = phoneNumbers.get(0);

        assertEquals("239-023-8390", phone.get("number"));
        assertEquals(ContactContextType.WORK.name(), phone.get("type"));
        assertEquals("2451", phone.get("extension"));

        phone = phoneNumbers.get(1);

        assertEquals("514-344-4932", phone.get("number"));
        assertEquals(ContactContextType.OTHER.name(), phone.get("type"));

        Map<String, Object> pricing = (Map<String, Object>)result.get("pricing");

        assertEquals(27.45, pricing.get("blendedPerHour"));
        assertEquals(22.00, pricing.get("perAdditionalHour"));
        assertEquals(16.0, pricing.get("initialHours"));
        assertEquals(8.0, pricing.get("maxAdditionalHours"));
        assertEquals(700.31, pricing.get("maxSpendLimit"));
        assertEquals(35.76, pricing.get("additionalExpenses"));
        assertEquals(45.01, pricing.get("bonus"));
        assertEquals(554.32, pricing.get("overridePrice"));
        assertNull(pricing.get("flatPrice"));
        assertNull(pricing.get("perHour"));
        assertNull(pricing.get("maxHours"));
        assertNull(pricing.get("perUnit"));
        assertNull(pricing.get("maxUnits"));
        assertTrue((Boolean) pricing.get("offlinePayment"));
        assertFalse((Boolean) pricing.get("internal"));
        assertFalse((Boolean) pricing.get("disablePriceNegotiation"));

        final List<Map<String, Object>> messages = (List<Map<String, Object>>)result.get("messages");

        Map<String, Object> note = messages.get(0);

        assertEquals(84862392L, note.get("createdDate"));
        assertEquals("This is a very important note.", note.get("text"));
        assertEquals("Note Creator", note.get("createdBy"));
        assertEquals(PrivacyType.PRIVILEGED.name(), note.get("visibility"));
        assertNull(note.get("onBehalfOf"));

        note = messages.get(1);

        assertEquals(63218769L, note.get("createdDate"));
        assertEquals("This is not such an important note.", note.get("text"));
        assertEquals("Second Creator", note.get("createdBy"));
        assertEquals(PrivacyType.PRIVATE.name(), note.get("visibility"));
        assertEquals("Singen Stythe", note.get("onBehalfOf"));

        note = messages.get(2);

        assertEquals(13289713L, note.get("createdDate"));
        assertEquals("Public note", note.get("text"));
        assertEquals(PrivacyType.PUBLIC.name(), note.get("visibility"));

        final Map<String, Object> deliverablesConfig = (Map<String, Object>)result.get("deliverablesConfiguration");

        assertEquals("Deliverable Instructions...", deliverablesConfig.get("overview"));
        assertEquals(8, deliverablesConfig.get("hoursToComplete"));

        final List<Map<String, Object>> requirements =
            (List<Map<String, Object>>) deliverablesConfig.get("deliverableRequirements");

        Map<String,Object> requirement = requirements.get(0);

        assertEquals(35L, requirement.get("id"));
        assertEquals("photos", requirement.get("type"));
        assertEquals("Insert photo here", requirement.get("instructions"));
        assertEquals(3, requirement.get("requiredNumberOfFiles"));

        requirement = requirements.get(1);

        assertEquals(36L, requirement.get("id"));
        assertEquals("SIGN OFF", requirement.get("type"));
        assertEquals("Include customer sign off sheet.", requirement.get("instructions"));
        assertEquals(1, requirement.get("requiredNumberOfFiles"));

        final List<Map<String, Object>> deliverables = (List<Map<String, Object>>)requirement.get("deliverables");

        Map<String, Object> deliverable = deliverables.get(0);

        assertEquals("Sign Off", deliverable.get("name"));
        assertEquals("Customer signed off on work.", deliverable.get("description"));
        assertEquals(1, deliverable.get("position"));
        assertEquals("Worker Bee 1", deliverable.get("uploadedBy"));
        assertEquals(829376423987L, deliverable.get("uploadDate"));
        assertEquals(8309734L, deliverable.get("rejectedDate"));
        assertEquals("This will not do.", deliverable.get("rejectionReason"));
        assertEquals("King Boss Alpha", deliverable.get("rejectedBy"));
        assertEquals("AB298CD0192ED", deliverable.get("uuid"));
        assertEquals("AC129387", deliverable.get("transformSmallUuid"));
        assertEquals("ABCDEF91287", deliverable.get("transformLargeUuid"));
        assertEquals("application/pdf", deliverable.get("mimeType"));
        assertEquals("www.pdfs.com/mypdf", deliverable.get("uri"));

        final List<Map<String, Object>> assets = (List<Map<String, Object>>)result.get("assets");

        Map<String, Object> asset = assets.get(0);

        assertEquals("abj727s9sk22l", asset.get("uuid"));
        assertEquals("Work order form", asset.get("name"));
        assertEquals("Scan of primary work order defining scope of assignment", asset.get("description"));
        assertEquals("application/pdf", asset.get("mimeType"));
        assertEquals("www.myassets.com/work_order.pdf", asset.get("uri"));
        assertEquals(WorkAssetAssociationType.INSTRUCTIONS, asset.get("type"));
        assertEquals(VisibilityType.ASSIGNED_WORKER, asset.get("visibility"));

        final List<Map<String, Object>> customFieldSets = (List<Map<String, Object>>)result.get("customFields");

        Map<String, Object> customFieldSet = (Map<String, Object>) customFieldSets.get(0);

        assertEquals(84762939L, customFieldSet.get("id"));
        assertEquals("Test custom field set:", customFieldSet.get("name"));
        assertEquals(1L, (customFieldSet.get("position")));

        final List<Map<String, Object>> customFields = (List<Map<String, Object>>) customFieldSet.get("fields");

        Map<String, Object> customField = customFields.get(0);

        assertEquals(100L, customField.get("id"));
        assertEquals("Tracking Code", customField.get("name"));
        assertEquals("Owner", customField.get("type"));
        assertEquals(Boolean.FALSE, customField.get("workerEditable"));
        assertEquals(Boolean.FALSE, customField.get("required"));
        assertNull(customField.get("default"));
        assertEquals("DG12839", customField.get("value"));
        assertTrue((Boolean) customField.get("showOnPrintout"));
        assertFalse((Boolean) customField.get("showInAssignmentHeader"));

        customField = customFields.get(1);

        assertEquals(101L, customField.get("id"));
        assertEquals("Job Level", customField.get("name"));
        assertEquals("Owner", customField.get("type"));
        assertEquals(Boolean.FALSE, customField.get("workerEditable"));
        assertEquals(Boolean.TRUE, customField.get("required"));
        assertEquals("Journeyman", customField.get("default"));
        assertEquals("Master Tech", customField.get("value"));
        assertFalse((Boolean) customField.get("showOnPrintout"));
        assertTrue((Boolean) customField.get("showInAssignmentHeader"));

        final Map<String, Object> contacts = (Map<String, Object>)result.get("contacts");

        final Map<String, Object> supportContact = (Map<String, Object>) contacts.get("supportContact");

        assertEquals("Support", supportContact.get("firstName"));
        assertEquals("Contact", supportContact.get("lastName"));
        assertEquals("support_contact@company.com", supportContact.get("email"));
        assertEquals("8373892", supportContact.get("userNumber"));

        phoneNumbers = (List<Map<String, Object>>) supportContact.get("phoneNumbers");

        Map<String,Object> phoneNumber = phoneNumbers.get(0);

        assertEquals("917-383-2902", phoneNumber.get("number"));
        assertEquals(ContactContextType.WORK.name(), phoneNumber.get("type"));
        assertEquals("4573", phoneNumber.get("extension"));

        phoneNumber = phoneNumbers.get(1);

        assertEquals("415-443-2394", phoneNumber.get("number"));
        assertEquals(ContactContextType.OTHER.name(), phoneNumber.get("type"));

        Map<String, Object> ownerContact = (Map<String, Object>) contacts.get("owner");

        assertEquals("Client", ownerContact.get("firstName"));
        assertEquals("Representative", ownerContact.get("lastName"));
        assertEquals("Boss_Man@company.com", ownerContact.get("email"));
        assertEquals("1", ownerContact.get("userNumber"));

        phoneNumbers = (List<Map<String, Object>>) ownerContact.get("phoneNumbers");
        phoneNumber = phoneNumbers.get(0);

        assertEquals("917-383-2902", phoneNumber.get("number"));
        assertEquals(ContactContextType.WORK.name(), phoneNumber.get("type"));
        assertEquals("4573", phoneNumber.get("extension"));

        Map<String, Object> timeTracking = (Map<String, Object>)result.get("timeTracking");

        assertTrue((Boolean) timeTracking.get("checkInRequired"));
        assertEquals("Key Master", timeTracking.get("checkInContactName"));
        assertEquals("1-800-777-7777", timeTracking.get("checkInContactPhone"));
        assertTrue((Boolean) timeTracking.get("showCheckOutNote"));
        assertFalse((Boolean) timeTracking.get("checkOutNoteRequired"));
        assertEquals("Gotta sign in with this person.", timeTracking.get("checkOutNoteInstructions"));

        List<Map<String, Object>> trackingEntries = (List<Map<String, Object>>) timeTracking.get("trackingEntries");

        Map<String, Object> checkInOutPair = (Map<String, Object>) trackingEntries.get(0);

        assertEquals(321987319L, checkInOutPair.get("id"));

        Map<String, Object> checkInEntry = (Map<String, Object>) checkInOutPair.get("checkIn");

        assertEquals(82349823749L, checkInEntry.get("timestamp"));
        assertEquals(293082934L, checkInEntry.get("createdBy"));
        assertEquals(13.23, checkInEntry.get("distance"));

        Map<String, Object> checkOutEntry = (Map<String, Object>) checkInOutPair.get("checkOut");

        assertEquals(981723981237L, checkOutEntry.get("timestamp"));
        assertEquals(293082945L, checkOutEntry.get("createdBy"));
        assertEquals(12.03, checkOutEntry.get("distance"));

        List<Map<String, Object>> labels = (List<Map<String, Object>>)result.get("labels");

        Map<String, Object> label = labels.get(0);

        assertEquals("TEST_LABEL", label.get("code"));
        assertEquals("This is a test label", label.get("description"));
        assertEquals("This is a test note for my test label", label.get("note"));
        assertEquals(Boolean.TRUE, label.get("workerCanUpdate"));
        assertEquals("AC2B29", label.get("colorHexCode"));

        label = labels.get(1);

        assertEquals("ANOTHER_TEST_LABEL", label.get("code"));
        assertEquals("Description test", label.get("description"));
        assertEquals("Not a very important label", label.get("note"));
        assertEquals(Boolean.FALSE, label.get("workerCanUpdate"));
        assertEquals("FFFFFF", label.get("colorHexCode"));

        Map<String, Object> payment = (Map<String, Object>)result.get("payment");

        assertEquals(240.0, payment.get("minutesWorked"));
        assertEquals(8.0, payment.get("unitsCompleted"));
        assertEquals(43.14, payment.get("additionalExpenses"));
        assertEquals(5.00, payment.get("bonus"));
        assertEquals(314.92, payment.get("total"));
        assertEquals(71981723987L, payment.get("dueDate"));
        assertEquals(6312897132L, payment.get("paidDate"));
        assertEquals(Boolean.TRUE, payment.get("paymentTermsEnabled"));
        assertEquals(15, payment.get("paymentTermsDays"));

        final List<Map<String, Object>> shipments = (List<Map<String, Object>>)result.get("shipments");

        Map<String, Object> shipment = shipments.get(0);

        assertEquals("Super Widget", shipment.get("name"));
        assertFalse((Boolean) shipment.get("returnShipment"));
        assertEquals("CT2139487", shipment.get("trackingNumber"));
        assertEquals(ShippingProvider.UPS.getCode(), shipment.get("shippingProvider"));
        assertEquals(new BigDecimal(99.99), shipment.get("value"));

        Map<String, Object> shippingAddress = (Map<String,Object>) shipment.get("shippingAddress");

        assertEquals(123798173L, shippingAddress.get("id"));
        assertEquals("Ballpark", shippingAddress.get("name"));
        assertEquals("873 Amsterdam Ave.", shippingAddress.get("address1"));
        assertEquals("Suite 900", shippingAddress.get("address2"));
        assertEquals("New York", shippingAddress.get("city"));
        assertEquals("NY", shippingAddress.get("state"));
        assertEquals("10013", shippingAddress.get("postalCode"));
        assertEquals("USA", shippingAddress.get("country"));

        coordinates = (Map<String, Object>) shippingAddress.get("coordinates");

        assertEquals(new BigDecimal(43.87), coordinates.get("latitude"));
        assertEquals(new BigDecimal(-80.32), coordinates.get("longitude"));

        shipment = shipments.get(1);

        assertEquals("Super Widget", shipment.get("name"));
        assertTrue((Boolean) shipment.get("returnShipment"));
        assertEquals("US7263", shipment.get("trackingNumber"));
        assertEquals(ShippingProvider.USPS.getCode(), shipment.get("shippingProvider"));
        assertEquals(new BigDecimal(99.99), shipment.get("value"));

        shippingAddress = (Map<String,Object>) shipment.get("shippingAddress");

        assertEquals(89712399L, shippingAddress.get("id"));
        assertEquals("472 Hillary St.", shippingAddress.get("address1"));
        assertEquals("#2", shippingAddress.get("address2"));
        assertEquals("New Orleans", shippingAddress.get("city"));
        assertEquals("LA", shippingAddress.get("state"));
        assertEquals("70118", shippingAddress.get("postalCode"));
        assertEquals("USA", shippingAddress.get("country"));
        assertNull(shippingAddress.get("coordinates"));

        shipment = shipments.get(2);

        assertEquals("Backhoe", shipment.get("name"));
        assertFalse((Boolean) shipment.get("returnShipment"));
        assertEquals("FXS023", shipment.get("trackingNumber"));
        assertEquals(ShippingProvider.FEDEX.getCode(), shipment.get("shippingProvider"));
        assertEquals(new BigDecimal(51999.99), shipment.get("value"));

        shippingAddress = (Map<String,Object>) shipment.get("shippingAddress");

        assertEquals(123798173L, shippingAddress.get("id"));
        assertEquals("873 Amsterdam Ave.", shippingAddress.get("address1"));
        assertEquals("Suite 900", shippingAddress.get("address2"));
        assertEquals("New York", shippingAddress.get("city"));
        assertEquals("NY", shippingAddress.get("state"));
        assertEquals("10013", shippingAddress.get("postalCode"));
        assertEquals("USA", shippingAddress.get("country"));

        coordinates = (Map<String, Object>) shippingAddress.get("coordinates");

        assertEquals(new BigDecimal(43.87), coordinates.get("latitude"));
        assertEquals(new BigDecimal(-80.32), coordinates.get("longitude"));

        final Map<String, Object> milestones = (Map<String, Object>)result.get("milestones");

        assertEquals(2437823468L, milestones.get("sentDate"));
        assertEquals(737864393L, milestones.get("acceptedDate"));
        assertEquals(898279002L, milestones.get("activeDate"));
        assertEquals(93277379L, milestones.get("cancelledDate"));
        assertEquals(892734987L, milestones.get("closedDate"));
        assertEquals(982347809L, milestones.get("completedDate"));
        assertEquals(23870204L, milestones.get("createdDate"));
        assertEquals(8917312080L, milestones.get("declinedDate"));
        assertEquals(90823092489L, milestones.get("draftDate"));
        assertEquals(189238691240L, milestones.get("paidDate"));
        assertEquals(100910209L, milestones.get("refundedDate"));
        assertEquals(98273092340L, milestones.get("voidedDate"));
        assertEquals(712368923L, milestones.get("dueDate"));

        final List<Map<String, Object>> negotiations = (List<Map<String, Object>>)result.get("negotiations");

        Map<String, Object> negotiation  = negotiations.get(0);

        assertEquals(9812398709L, negotiation.get("id"));
        assertEquals("Work User", negotiation.get("requestedBy"));
        assertEquals(67828764L, negotiation.get("requestedOn"));
        assertTrue((Boolean) negotiation.get("initiatedByResource"));
        assertEquals(ApprovalStatus.APPROVED.name(), negotiation.get("approvalStatus"));
        assertEquals("Work Approver", negotiation.get("approvedBy"));
        assertEquals(79283498723L, negotiation.get("approvedOn"));
        assertEquals(AssignmentMarshaller.EXPENSE_NEGOTIATION_TYPE, negotiation.get("type"));
        assertNotNull(negotiation.get("message"));

        Map<String, Object> message = (Map<String, Object>) negotiation.get("message");

        assertEquals("I had to get a new part", message.get("text"));
        assertEquals("WorkStill UserStill", message.get("createdBy"));
        assertNull(negotiation.get("schedule"));

        pricing = (Map<String, Object>) negotiation.get("pricing");

        assertEquals(564.23, pricing.get("additionalExpenses"));
        assertNull(pricing.get("flatPrice"));
        assertFalse((Boolean) pricing.get("internal"));

        negotiation = negotiations.get(1);

        assertEquals(789723487L, negotiation.get("id"));
        assertEquals("Work2 Approver2", negotiation.get("requestedBy"));
        assertEquals(612367598L, negotiation.get("requestedOn"));
        assertFalse((Boolean) negotiation.get("initiatedByResource"));
        assertEquals(ApprovalStatus.DECLINED.name(), negotiation.get("approvalStatus"));
        assertNull(negotiation.get("approvedBy"));
        assertNull(negotiation.get("approvedOn"));
        assertEquals(AssignmentMarshaller.BUDGET_NEGOTIATION_TYPE, negotiation.get("type"));
        assertNotNull(negotiation.get("message"));

        message = (Map<String, Object>) negotiation.get("message");

        assertEquals("We're broke, can't pay.", message.get("text"));
        assertEquals("Approver2s Boss", message.get("createdBy"));
        assertNull(negotiation.get("schedule"));

        pricing = (Map<String, Object>) negotiation.get("pricing");

        assertEquals(500.00, pricing.get("flatPrice"));
        assertEquals(575.00, pricing.get("maxSpendLimit"));
        assertFalse((Boolean) pricing.get("internal"));

        negotiation = negotiations.get(2);

        assertEquals(7812098723L, negotiation.get("id"));
        assertEquals("Work3 User3", negotiation.get("requestedBy"));
        assertEquals(67828764L, negotiation.get("requestedOn"));
        assertTrue((Boolean) negotiation.get("initiatedByResource"));
        assertEquals(ApprovalStatus.PENDING.name(), negotiation.get("approvalStatus"));
        assertNull(negotiation.get("approvedBy"));
        assertNull(negotiation.get("approvedOn"));
        assertEquals(AssignmentMarshaller.BONUS_NEGOTIATION_TYPE, negotiation.get("type"));
        assertNull(negotiation.get("message"));
        assertNull(negotiation.get("schedule"));

        pricing = (Map<String, Object>) negotiation.get("pricing");

        assertEquals(35.00, pricing.get("bonus"));
        assertEquals(625.00, pricing.get("overridePrice"));
        assertFalse((Boolean) pricing.get("internal"));

        negotiation  = negotiations.get(3);

        assertEquals(467321523L, negotiation.get("id"));
        assertEquals("Work5 User5", negotiation.get("requestedBy"));
        assertEquals(8917232L, negotiation.get("requestedOn"));
        assertTrue((Boolean) negotiation.get("initiatedByResource"));
        assertEquals(ApprovalStatus.APPROVED.name(), negotiation.get("approvalStatus"));
        assertEquals("krow5 Approver5", negotiation.get("approvedBy"));
        assertEquals(3578289239L, negotiation.get("approvedOn"));
        assertEquals(AssignmentMarshaller.SCHEDULE_NEGOTIATION_TYPE, negotiation.get("type"));
        assertNotNull(negotiation.get("message"));

        message = (Map<String, Object>) negotiation.get("message");

        assertEquals("I can come on this date", message.get("text"));
        assertEquals("Work5Still User5Still", message.get("createdBy"));
        assertNull(negotiation.get("pricing"));

        schedule = (Map<String, Object>) negotiation.get("schedule");

        //assertEquals(892349872L, schedule.get("start"));
        //assertNull(schedule.get("startWindowBegin"));
        //assertNull(schedule.get("startWindowEnd"));

        negotiation  = negotiations.get(4);

        assertEquals(65578123780L, negotiation.get("id"));
        assertEquals("Work6 User6", negotiation.get("requestedBy"));
        assertEquals(99729789234L, negotiation.get("requestedOn"));
        assertFalse((Boolean) negotiation.get("initiatedByResource"));
        assertEquals(ApprovalStatus.REMOVED.name(), negotiation.get("approvalStatus"));
        assertNull(negotiation.get("approvedBy"));
        assertNull(negotiation.get("approvedOn"));
        assertEquals(AssignmentMarshaller.SCHEDULE_NEGOTIATION_TYPE, negotiation.get("type"));
        assertNull(negotiation.get("message"));
        assertNull(negotiation.get("pricing"));

        schedule = (Map<String, Object>) negotiation.get("schedule");

        assertNull(schedule.get("start"));
        assertEquals(892349872L, schedule.get("startWindowBegin"));
        assertEquals(987123987124L, schedule.get("startWindowEnd"));

        final List<Map<String, Object>> assessments = (List<Map<String, Object>>)result.get("assessments");

        Map<String, Object> assessment = assessments.get(0);

        assertEquals(8213987087L, assessment.get("id"));
        assertEquals("Job Survey", assessment.get("name"));
        assertTrue((Boolean) assessment.get("required"));
        assertNull(assessment.get("latestAttempt"));

        assessment = assessments.get(1);

        assertEquals(987234987L, assessment.get("id"));
        assertEquals("Another Assessment", assessment.get("name"));
        assertFalse((Boolean) assessment.get("required"));
        assertNotNull(assessment.get("latestAttempt"));

        final Map<String, Object> attempt = (Map<String, Object>) assessment.get("latestAttempt");

        assertEquals(1234671298L, attempt.get("id"));
        assertEquals(AttemptStatusType.INPROGRESS, attempt.get("status"));
        assertTrue((Boolean) attempt.get("respondedToAllItems"));
    }

    @Test
    public void marshallServiceResponseDetails_goodBundleResponse_goodMarshall() {

        setupBundleDetailsServiceResults();
        setupAssignmentDetailsParts();
        setupAssignmentDetailsCustomFields();

        final FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();
        marshaller.getDetailsFulfillmentResponse(serviceDetailsResults, null, assignmentParts, response, 11L);

        final List marshalledAssignments = response.getPayload();
        assertEquals(1, marshalledAssignments.size());

        final Map result = (Map)marshalledAssignments.get(0);
        assertEquals("6060842", result.get("id"));
        assertTrue((Boolean)result.get("workBundle"));
        assertEquals(7777777L, result.get("workBundleId"));
    }

    @Test
    public void marshallServiceResponseList_goodBundleResponse_goodMarshall() {
        setupAssignmentsServiceListResults();

        final FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();
        WorkResponse workResponse = new WorkResponse();
        workResponse.setInWorkBundle(true);
        WorkBundleDTO workBundleDTO = new WorkBundleDTO();
        workBundleDTO.setId(12345L);
        workResponse.setWorkBundleParent(workBundleDTO);
        Map<String, WorkResponse> workMap = new HashMap<>();
        workMap.put("7654930", workResponse);
        serviceListResults.get(0).put("work_number", "7654930");
        marshaller.getListFulfillmentResponse(serviceListResults, workMap, response);

        final List marshalledAssignments = response.getPayload();
        assertEquals(2, marshalledAssignments.size());

        final Map result = (Map)marshalledAssignments.get(0);
        assertEquals("7654930", result.get("id"));
        assertEquals(12345L, result.get("workBundleId"));
    }

    private void setupAssignmentsServiceListResults() {

        final Map<String, Object> row1 = new HashMap<>();

        row1.put("id", "7654930");
        row1.put("work_id", 7654930L);
        row1.put("title", "Test Assignment Title");
        row1.put("status", WorkStatusType.ACTIVE);
        row1.put("status_description", "Assigned");
        row1.put("company_id", "6060842");
        row1.put("company", "Amalgamated Bank & Rail");
        row1.put("location_offsite", Boolean.FALSE);
        row1.put("city", "Lodi");
        row1.put("state", "NJ");
        row1.put("postal_code", "40039");
        row1.put("address","413 Combo Lane");
        row1.put("longitude", 78.728D);
        row1.put("latitude", 80.9290D);
        row1.put("pricing_type", "Flat");
        row1.put("price", "$500.00");
        row1.put("start_date", "May 15 2016");
        row1.put("start_time", "1:00 PM");
        row1.put("start_datetime_millis", 1463317200000L);
        row1.put("end_date", "May 16");
        row1.put("end_time", "11:00 AM");
        row1.put("end_datetime_millis", 1463396400000L);
        row1.put("amount_earned", 520.00D);
        row1.put("paid_on", "May 20");
        row1.put("paid_on_millis", 1463738400000L);
        row1.put("due_on", "May 21");
        row1.put("due_on_millis", 1463875140000L);
        row1.put("sent_date", 1462491300000L);
        row1.put("payment_terms_enabled", Boolean.TRUE);
        row1.put("payment_terms_days", 9);

        final Map<String, Object> row2 = new HashMap<>();

        serviceListResults = ImmutableList.of(row1, row2);
    }

    private void setupAssignmentsDetailsServiceResults() {

        serviceDetailsResults = new WorkResponse();
        serviceDetailsResults.setWorkBundle(false);
        serviceDetailsResults.setInWorkBundle(true);

        final WorkBundleDTO bundleParent = new WorkBundleDTO();
        bundleParent.setId(891248974L);
        serviceDetailsResults.setWorkBundleParent(bundleParent);

        final Work work = new Work();
        serviceDetailsResults.setWork(work);
        work.setWorkNumber("6060842");
        work.setId(7777777);
        work.setTitle("Test work response");
        work.setDescription("This is a test description");

        final Status status = new Status("ACCEPTED", "Assignment taken", "");
        work.setStatus(status);
        work.setInstructions("Do this work well.");
        work.setDesiredSkills("Lion Taming, fire eating");
        work.setOffsiteLocation(Boolean.FALSE);

        final Resource activeResource = new Resource();
        activeResource.setConfirmedOn(567839202L);
        work.setActiveResource(activeResource);

        final WorkMilestones milestones = new WorkMilestones();
        milestones.setSentOn(2437823468L);
        milestones.setAcceptedOn(737864393L);
        milestones.setActiveOn(898279002L);
        milestones.setCancelledOn(93277379L);
        milestones.setClosedOn(892734987L);
        milestones.setCompleteOn(982347809L);
        milestones.setCreatedOn(23870204L);
        milestones.setDeclinedOn(8917312080L);
        milestones.setDraftOn(90823092489L);
        milestones.setPaidOn(189238691240L);
        milestones.setRefundedOn(100910209L);
        milestones.setVoidOn(98273092340L);
        milestones.setDueOn(712368923L);

        serviceDetailsResults.setWorkMilestones(milestones);

        work.setConfirmableDate(new GregorianCalendar());
        work.getConfirmableDate().setTimeInMillis(7492092308L);
        work.setConfirmByDate(new GregorianCalendar());
        work.getConfirmByDate().setTimeInMillis(810762310987L);

        final Company company = new Company();
        work.setCompany(company);
        company.setId(784302973L);
        company.setName("Test Company X");
        company.setCompanyUuid("f6b00ce1-4b9a-4537-a2e1-328b331310a5");
        company.setCustomSignatureLine("Work for Test Company X, We Pay On Time");

        final Company clientCompany = new Company();
        work.setClientCompany(clientCompany);
        clientCompany.setId(12357628792L);
        clientCompany.setName("X's Client");

        Schedule schedule = new Schedule();
        work.setSchedule(schedule);

        schedule.setFrom(289374987L);
        schedule.setThrough(398479830L);

        final Location location = new Location();
        work.setLocation(location);
        location.setId(20923480L);
        location.setName("San Francisco");
        location.setNumber("123456");

        final Address locationAddress = new Address();

        location.setAddress(locationAddress);
        locationAddress.setAddressLine1("1132 Fulton St.");
        locationAddress.setAddressLine2("Apt. 3");
        locationAddress.setCity("San Francisco");
        locationAddress.setState("CA");
        locationAddress.setZip("10023");
        locationAddress.setCountry("USA");

        final GeoPoint point = new GeoPoint();
        locationAddress.setPoint(point);

        point.setLatitude(23.54);
        point.setLongitude(54.23);

        User locationContact = new User();

        work.setLocationContact(locationContact);
        locationContact.setName(new Name("Location", "Contact"));
        locationContact.setEmail("location_contact@company.com");
        locationContact.setUserNumber("876143");
        locationContact.setProfile(new Profile());
        locationContact.getProfile().setPhoneNumbers(new LinkedList<Phone>());

        Phone locationPhone = new Phone();

        locationPhone.setPhone("932-320-0938");
        locationPhone.setType(ContactContextType.WORK.name());
        locationPhone.setExtension("1542");
        locationContact.getProfile().getPhoneNumbers().add(locationPhone);

        locationPhone = new Phone();

        locationPhone.setPhone("415-443-2394");
        locationPhone.setType(ContactContextType.OTHER.name());
        locationContact.getProfile().getPhoneNumbers().add(locationPhone);

        locationContact = new User();
        work.setSecondaryLocationContact(locationContact);

        locationContact.setName(new Name("Secondary", "Contact"));
        locationContact.setEmail("secondary_contact@company.com");
        locationContact.setUserNumber("341678");

        locationContact.setProfile(new Profile());
        locationContact.getProfile().setPhoneNumbers(new LinkedList<Phone>());

        locationPhone = new Phone();

        locationPhone.setPhone("239-023-8390");
        locationPhone.setType(ContactContextType.WORK.name());
        locationPhone.setExtension("2451");
        locationContact.getProfile().getPhoneNumbers().add(locationPhone);

        locationPhone = new Phone();
        locationPhone.setPhone("514-344-4932");
        locationPhone.setType(ContactContextType.OTHER.name());
        locationContact.getProfile().getPhoneNumbers().add(locationPhone);

        PricingStrategy pricing = new PricingStrategy();
        work.setPricing(pricing);

        pricing.setType(PricingStrategyType.BLENDED_PER_HOUR);
        pricing.setInitialPerHourPrice(27.45);
        pricing.setAdditionalPerHourPrice(22.00);
        pricing.setInitialNumberOfHours(16);
        pricing.setMaxBlendedNumberOfHours(8);
        pricing.setMaxSpendLimit(700.31);
        pricing.setAdditionalExpenses(35.76);
        pricing.setOfflinePayment(true);
        pricing.setBonus(45.01);
        pricing.setOverridePrice(554.32);

        final List<Note> noteList = new LinkedList();
        work.setNotes(noteList);

        Note note = new Note();

        note.setCreatedOn(84862392L);
        note.setText("This is a very important note.");
        note.setIsPrivileged(true);

        User noteCreator = new User();

        note.setCreator(noteCreator);
        noteCreator.setName(new Name("Note", "Creator"));
        noteList.add(note);

        note = new Note();

        note.setCreatedOn(63218769L);
        note.setText("This is not such an important note.");
        note.setIsPrivate(true);
        note.setOnBehalfOf(new User().setName(new Name("Singen", "Stythe")));
        note.setCreator(new User().setName(new Name("Second", "Creator")));
        noteList.add(note);

        note = new Note();

        note.setCreatedOn(13289713L);
        note.setText("Public note");
        noteList.add(note);

        final DeliverableRequirementGroupDTO deliverables = new DeliverableRequirementGroupDTO();
        work.setDeliverableRequirementGroupDTO(deliverables);

        deliverables.setInstructions("Deliverable Instructions...");
        deliverables.setHoursToComplete(8);

        final List<DeliverableRequirementDTO> requirementsList = new LinkedList();
        deliverables.setDeliverableRequirementDTOs(requirementsList);

        DeliverableRequirementDTO requirement = new DeliverableRequirementDTO();

        requirement.setType("photos");
        requirement.setInstructions("Insert photo here");
        requirement.setNumberOfFiles(3);
        requirement.setId(35L);
        requirementsList.add(requirement);

        requirement = new DeliverableRequirementDTO();
        requirement.setType("SIGN OFF");
        requirement.setInstructions("Include customer sign off sheet.");
        requirement.setNumberOfFiles(1);
        requirement.setId(36L);
        requirementsList.add(requirement);

        final TreeSet<DeliverableAsset> deliverableAssets = new TreeSet();
        work.setDeliverableAssets(deliverableAssets);

        final DeliverableAsset dAsset = new DeliverableAsset();

        dAsset.setName("Sign Off");
        dAsset.setDescription("Customer signed off on work.");
        dAsset.setPosition(1);
        dAsset.setUploadedBy("Worker Bee 1");
        dAsset.setUploadDate(829376423987L);
        dAsset.setRejectedOn(8309734L);
        dAsset.setRejectionReason("This will not do.");
        dAsset.setRejectedBy("King Boss Alpha");
        dAsset.setUuid("AB298CD0192ED");
        dAsset.setTransformSmallUuid("AC129387");
        dAsset.setTransformLargeUuid("ABCDEF91287");
        dAsset.setMimeType("application/pdf");
        dAsset.setUri("www.pdfs.com/mypdf");
        dAsset.setDeliverableRequirementId(36L);
        deliverableAssets.add(dAsset);

        final TreeSet<Asset> assets = new TreeSet<Asset>();
        work.setAssets(assets);

        final Asset asset = new Asset();

        asset.setUuid("abj727s9sk22l");
        asset.setName("Work order form");
        asset.setDescription("Scan of primary work order defining scope of assignment");
        asset.setMimeType("application/pdf");
        asset.setUri("www.myassets.com/work_order.pdf");
        asset.setType(WorkAssetAssociationType.INSTRUCTIONS);
        asset.setVisibilityCode(VisibilityType.ASSIGNED_WORKER);
        assets.add(asset);

        final List<CustomFieldGroup> customFieldGroups = new LinkedList();
        work.setCustomFieldGroups(customFieldGroups);

        final CustomFieldGroup fieldGroup = new CustomFieldGroup();
        customFieldGroups.add(fieldGroup);

        fieldGroup.setId(84762939L);
        fieldGroup.setName("Test custom field set:");
        fieldGroup.setPosition(1);

        final List<CustomField> fields = new LinkedList();
        fieldGroup.setFields(fields);

        CustomField field = new CustomField();
        field.setId(100L);
        field.setName("Tracking Code");
        field.setType("Owner");
        field.setReadOnly(Boolean.TRUE);
        field.setIsRequired(Boolean.FALSE);
        field.setValue("DG12839");
        field.setShowOnPrintout(true);
        field.setShowInAssignmentHeader(false);
        fields.add(field);

        field = new CustomField();
        field.setId(101L);
        field.setName("Job Level");
        field.setType("Owner");
        field.setReadOnly(Boolean.TRUE);
        field.setIsRequired(Boolean.TRUE);
        field.setDefaultValue("Journeyman");
        field.setValue("Master Tech");
        field.setShowOnPrintout(false);
        field.setShowInAssignmentHeader(true);
        fields.add(field);

        final User supportContact = new User();
        work.setSupportContact(supportContact);

        supportContact.setName(new Name("Support", "Contact"));
        supportContact.setEmail("support_contact@company.com");
        supportContact.setUserNumber("8373892");
        supportContact.setProfile(new Profile());
        supportContact.getProfile().setPhoneNumbers(new LinkedList<Phone>());

        Phone supportPhone = new Phone();
        supportPhone.setPhone("917-383-2902");
        supportPhone.setType(ContactContextType.WORK.name());
        supportPhone.setExtension("4573");
        supportContact.getProfile().getPhoneNumbers().add(supportPhone);

        supportPhone = new Phone();
        supportPhone.setPhone("415-443-2394");
        supportPhone.setType(ContactContextType.OTHER.name());
        supportContact.getProfile().getPhoneNumbers().add(supportPhone);

        final User owner = new User();
        work.setBuyer(owner);
        owner.setName(new Name("Client", "Representative"));
        owner.setEmail("Boss_Man@company.com");
        owner.setUserNumber("1");
        owner.setProfile(new Profile());
        owner.getProfile().setPhoneNumbers(new LinkedList<Phone>());
        final Phone ownerPhone = new Phone();
        ownerPhone.setPhone("917-383-2902");
        ownerPhone.setType(ContactContextType.WORK.name());
        ownerPhone.setExtension("4573");
        owner.getProfile().getPhoneNumbers().add(ownerPhone);

        work.setCheckinCallRequired(true);
        work.setCheckinContactName("Key Master");
        work.setCheckinContactPhone("1-800-777-7777");
        work.setShowCheckoutNotesFlag(true);
        work.setCheckoutNoteRequiredFlag(false);
        work.setCheckoutNoteInstructions("Gotta sign in with this person.");

        final List<TimeTrackingEntry> timeTrackingList = new LinkedList<TimeTrackingEntry>();
        work.getActiveResource().setTimeTrackingLog(timeTrackingList);
        final TimeTrackingEntry ttEntry = new TimeTrackingEntry();
        ttEntry.setId(321987319L);
        ttEntry.setCheckedInOn(82349823749L);
        ttEntry.setCheckedInBy(new User().setId(293082934));
        ttEntry.setDistanceIn(13.23);
        ttEntry.setCheckedOutOn(981723981237L);
        ttEntry.setCheckedOutBy(new User().setId(293082945));
        ttEntry.setDistanceOut(12.03);
        timeTrackingList.add(ttEntry);

        final List<SubStatus> labels = new LinkedList<SubStatus>();
        work.setSubStatuses(labels);
        SubStatus label = new SubStatus();
        label.setCode("TEST_LABEL");
        label.setDescription("This is a test label");
        label.setNote("This is a test note for my test label");
        label.setUserResolvable(Boolean.TRUE);
        label.setColorRgb("AC2B29");
        labels.add(label);

        label = new SubStatus();
        label.setCode("ANOTHER_TEST_LABEL");
        label.setDescription("Description test");
        label.setNote("Not a very important label");
        label.setUserResolvable(Boolean.FALSE);
        label.setColorRgb("FFFFFF");
        labels.add(label);

        final PaymentSummary payment = new PaymentSummary();
        work.setPayment(payment);

        payment.setHoursWorked(4);
        payment.setUnitsProcessed(8);
        payment.setAdditionalExpenses(43.14);
        payment.setBonus(5.00);
        payment.setActualSpendLimit(314.92);
        payment.setPaymentDueOn(71981723987L);
        payment.setPaidOn(6312897132L);

        final ManageMyWorkMarket configuration = new ManageMyWorkMarket();
        work.setConfiguration(configuration);
        configuration.setPaymentTermsDays(15);
        configuration.setEnableAssignmentPrintout(true);
        configuration.setEnablePrintoutSignature(false);
        configuration.setDisablePriceNegotiation(false);
        configuration.setCheckinRequiredFlag(true);

        final PartGroupDTO partGroup = new PartGroupDTO();
        work.setPartGroup(partGroup);
        partGroup.setSuppliedByWorker(Boolean.FALSE);
        final LocationDTO shipToAddress = new LocationDTO();
        partGroup.setShipToLocation(shipToAddress);
        shipToAddress.setName("Ballpark");
        shipToAddress.setId(123798173L);
        shipToAddress.setAddress1("873 Amsterdam Ave.");
        shipToAddress.setAddress2("Suite 900");
        shipToAddress.setCity("New York");
        shipToAddress.setState("NY");
        shipToAddress.setPostalCode("10013");
        shipToAddress.setCountry("USA");
        shipToAddress.setLatitude(new BigDecimal(43.87));
        shipToAddress.setLongitude(new BigDecimal(-80.32));

        final LocationDTO returnToAddress = new LocationDTO();
        partGroup.setReturnToLocation(returnToAddress);
        returnToAddress.setId(89712399L);
        returnToAddress.setAddress1("472 Hillary St.");
        returnToAddress.setAddress2("#2");
        returnToAddress.setCity("New Orleans");
        returnToAddress.setState("LA");
        returnToAddress.setPostalCode("70118");
        returnToAddress.setCountry("USA");

        Negotiation negotiation = new Negotiation();

        negotiation.setId(9812398709L);
        negotiation.setRequestedBy(new User().setName(new Name("Work", "User")));
        negotiation.setRequestedOn(67828764L);
        negotiation.setInitiatedByResource(true);
        negotiation.setApprovalStatus(new Status().setCode(ApprovalStatus.APPROVED.name()));
        negotiation.setApprovedBy(new User().setName(new Name("Work", "Approver")));
        negotiation.setApprovedOn(79283498723L);

        note = new Note();

        note.setText("I had to get a new part");
        note.setCreator(new User().setName(new Name("WorkStill", "UserStill")));

        negotiation.setNote(note);

        pricing = new PricingStrategy();
        negotiation.setPricing(pricing);
        pricing.setAdditionalExpenses(564.23);
        work.getActiveResource().setExpenseNegotiation(negotiation);

        negotiation = new Negotiation();

        negotiation.setId(789723487L);
        negotiation.setRequestedBy(new User().setName(new Name("Work2", "Approver2")));
        negotiation.setRequestedOn(612367598L);
        negotiation.setInitiatedByResource(false);
        negotiation.setApprovalStatus(new Status().setCode(ApprovalStatus.DECLINED.name()));

        note = new Note();

        note.setText("We're broke, can't pay.");
        note.setCreator(new User().setName(new Name("Approver2s", "Boss")));
        negotiation.setNote(note);

        pricing = new PricingStrategy();

        negotiation.setPricing(pricing);
        pricing.setFlatPrice(500.00);
        pricing.setMaxSpendLimit(575.00);
        work.getActiveResource().setBudgetNegotiation(negotiation);

        negotiation = new Negotiation();

        negotiation.setId(7812098723L);
        negotiation.setRequestedBy(new User().setName(new Name("Work3", "User3")));
        negotiation.setRequestedOn(67828764L);
        negotiation.setInitiatedByResource(true);
        negotiation.setApprovalStatus(new Status().setCode(ApprovalStatus.PENDING.name()));
        negotiation.setApprovedOn(0L);

        pricing = new PricingStrategy();

        negotiation.setPricing(pricing);
        pricing.setBonus(35.00);
        pricing.setOverridePrice(625.00);
        work.getActiveResource().setBonusNegotiation(negotiation);

        negotiation = new Negotiation();

        negotiation.setId(467321523L);
        negotiation.setRequestedBy(new User().setName(new Name("Work5", "User5")));
        negotiation.setRequestedOn(8917232L);
        negotiation.setInitiatedByResource(true);
        negotiation.setApprovalStatus(new Status().setCode(ApprovalStatus.APPROVED.name()));
        negotiation.setApprovedBy(new User().setName(new Name("krow5", "Approver5")));
        negotiation.setApprovedOn(3578289239L);

        note = new Note();

        note.setText("I can come on this date");
        note.setCreator(new User().setName(new Name("Work5Still", "User5Still")));
        negotiation.setNote(note);

        schedule = new Schedule();
        negotiation.setSchedule(schedule);
        schedule.setRange(false);
        schedule.setFrom(892349872L);
        work.getActiveResource().setRescheduleNegotiation(negotiation);

        negotiation = new Negotiation();

        negotiation.setId(65578123780L);
        negotiation.setRequestedBy(new User().setName(new Name("Work6", "User6")));
        negotiation.setRequestedOn(99729789234L);
        negotiation.setInitiatedByResource(false);
        negotiation.setApprovalStatus(new Status().setCode(ApprovalStatus.REMOVED.name()));

        schedule = new Schedule();

        negotiation.setSchedule(schedule);
        schedule.setRange(true);
        schedule.setFrom(892349872L);
        schedule.setThrough(987123987124L);
        serviceDetailsResults.setBuyerRescheduleNegotiation(negotiation);

        final List<Assessment> assessments = new LinkedList<Assessment>();

        Assessment assessment = new Assessment();
        assessment.setId(8213987087L);
        assessment.setName("Job Survey");
        assessment.setIsRequired(true);
        assessments.add(assessment);

        assessment = new Assessment();
        assessment.setId(987234987L);
        assessment.setName("Another Assessment");
        assessment.setIsRequired(false);
        assessments.add(assessment);
        work.setAssessments(assessments);

        final AssessmentAttemptPair attemptPair = new AssessmentAttemptPair();
        assessment = new Assessment();
        assessment.setId(987234987L);
        attemptPair.setAssessment(assessment);
        final Attempt attempt = new Attempt();
        attempt.setId(1234671298L);
        attempt.setStatus(new Status().setCode(AttemptStatusType.INPROGRESS));
        attempt.setRespondedToAllItems(true);
        attemptPair.setLatestAttempt(attempt);

        work.getActiveResource().setAssessmentAttempts(ImmutableList.of(attemptPair));
    }

	private void setupAssignmentDetailsCustomFields() {
	}

    private void setupAssignmentDetailsParts() {

        assignmentParts = new LinkedList<PartDTO>();

        PartDTO newPart = new PartDTO();
        newPart.setName("Super Widget");
        newPart.setTrackingNumber("CT2139487");
        newPart.setShippingProvider(ShippingProvider.UPS);
        newPart.setPartValue(new BigDecimal(99.99));
        newPart.setReturn(Boolean.FALSE);
        assignmentParts.add(newPart);

        newPart = new PartDTO();
        newPart.setName("Super Widget");
        newPart.setTrackingNumber("US7263");
        newPart.setShippingProvider(ShippingProvider.USPS);
        newPart.setPartValue(new BigDecimal(99.99));
        newPart.setReturn(Boolean.TRUE);
        assignmentParts.add(newPart);

        newPart = new PartDTO();
        newPart.setName("Backhoe");
        newPart.setTrackingNumber("FXS023");
        newPart.setShippingProvider(ShippingProvider.FEDEX);
        newPart.setPartValue(new BigDecimal(51999.99));
        newPart.setReturn(Boolean.FALSE);
        assignmentParts.add(newPart);
    }

    private void setupBundleDetailsServiceResults() {
        setupAssignmentsDetailsServiceResults();
        serviceDetailsResults.setWorkBundle(true);
    }
}
