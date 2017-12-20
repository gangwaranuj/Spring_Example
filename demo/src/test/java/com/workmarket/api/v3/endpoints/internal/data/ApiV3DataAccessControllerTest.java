package com.workmarket.api.v3.endpoints.internal.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.collection.CollectionClient;
import com.workmarket.collection.gen.Common;
import com.workmarket.common.core.RequestContext;
import com.workmarket.configuration.Constants;
import com.workmarket.data.DataAccessClient;
import com.workmarket.data.gen.Messages;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.AssessmentPagination;
import com.workmarket.domains.model.assessment.GradedAssessment;
import com.workmarket.domains.reports.service.WorkReportService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.dto.AssessmentUser;
import com.workmarket.dto.AssessmentUserPagination;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.web.models.MessageBundle;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import rx.Observable;

import static com.workmarket.collection.gen.Response.Collection;
import static com.workmarket.collection.gen.Response.CollectionResponse;
import static com.workmarket.collection.gen.Response.CollectionsResponse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ApiV3DataAccessControllerTest extends BaseApiControllerTest {

  @Mock private DataAccessClient dataAccessClient;
  @Mock private CompanyService companyService;
  @Mock private CollectionClient collectionClient;

  @Mock private WorkService workService;
  @Mock private WorkReportService workReportService;

  @Mock private AssessmentService assessmentService;

  @Mock private Map<String, ApiV3DataAccessController.ServiceResource> serviceMap;
  @InjectMocks private ApiV3DataAccessController controller = new ApiV3DataAccessController();

  private RequestContext requestContext;

  private static String COMPANY_UUID = "1234fxffdsfd";
  private static Long INTERNAL_COMPANY_ID = Constants.WM_COMPANY_ID;
  private static Long REPORT_OWNER_COMPANY_ID = Constants.WM_COMPANY_ID + 1;
  private static Long NON_REPORT_OWNER_NON_INTERNAL_COMPANY_ID = Constants.WM_COMPANY_ID + 2;

  private ApiV3DataAccessController.ServiceResource serviceResource1 = mock(ApiV3DataAccessController.ServiceResource.class);
  private ApiV3DataAccessController.ServiceResource serviceResource2 = mock(ApiV3DataAccessController.ServiceResource.class);

  private static Integer REPORT_ID = 1;
  private static Integer TOTAL_ROW_COUNT = 13456;
  private static String REPORTS_URL = "/v3/data/reports";
  private static String REPORT_URL = "/v3/data/report/" + REPORT_ID.toString();
  private static String REPORTS_URL_BY_COMPANY = "/v3/data/reports/%s/%s";

  private static String REPORT_UUID = "e345yhrgfd23456u";
  private static String REPORT_DESCRIPTION = "it's a report";
  private static String REPORT_NAME = "name";

  // WORK REPORTS
  private static String REPORT_SOURCE_SERVICE = "com.workmarket.domains.work.service.WorkServiceImpl";
  private static String REPORT_SOURCE_CALLABLE = "getAssignmentDataOne";
  private static String REPORT_SOURCE_CALLABLE_PARAMETER_TYPES = "[\"java.util.Map\"]";
  private static String REPORT_SOURCE_CALLABLE_INPUT_CONVERTER = "reportRequestToParameterListForGetAssignmentDataOne";
  private static String REPORT_SOURCE_CALLABLE_OUTPUT_CONVERTER = "mapToApiReportData";
  private static String REPORT_SOURCE_CALLABLE_OUTPUT_CONVERTER_PARAMETER_TYPES = "[\"java.util.Map\"]";
  private static String REPORT_SOURCE_FILTER_FIELD_WHITELIST = "[\"sent_date\"]";
  private static String REPORT_SOURCE_FILTER_FIELD_WHITELIST_INVALID = "[\"garp0\"]";
  private static String REPORT_SOURCE_FILTER_FIELD_REQUIRED = "[]";

  private static String REPORT_JOIN_SERVICE = "com.workmarket.domains.reports.service.WorkReportServiceImpl";
  private static String REPORT_JOIN_CALLABLE = "getWorkCustomFieldsMapForBuyer";

  private Map<String, Object> rawReportDataWork;
  private static Long WORK_ID = 1L;
  private static Long CUSTOM_FIELD_ID_1 = 3L;

  // TEST REPORTS
  private static Long TEST_ID = 1L;

  private static String REPORT_SOURCE_SERVICE_TEST_RESULTS = "com.workmarket.service.business.AssessmentServiceImpl";
  private static String REPORT_SOURCE_CALLABLE_TEST_RESULTS = "findLatestAssessmentUserAttempts";
  private static String REPORT_SOURCE_CALLABLE_PARAMETER_TYPES_TEST_RESULTS = "[\"java.lang.Long\", \"com.workmarket.dto.AssessmentUserPagination\"]";
  private static String REPORT_SOURCE_CALLABLE_INPUT_CONVERTER_TEST_RESULTS = "reportRequestToParameterListForFindLatestAssessmentUserAttempts";
  private static String REPORT_SOURCE_CALLABLE_OUTPUT_CONVERTER_TEST_RESULTS = "assessmentUserPaginationToApiReportData";
  private static String REPORT_SOURCE_CALLABLE_OUTPUT_CONVERTER_PARAMETER_TYPES_TEST_RESULTS = "[\"com.workmarket.dto.AssessmentUserPagination\"]";
  private static String REPORT_SOURCE_FILTER_FIELD_WHITELIST_TEST_RESULTS = "[\"test_id\", \"user_id\"]";
  private static String REPORT_SOURCE_FILTER_FIELD_REQUIRED_TEST_RESULTS = "[\"test_id\"]";
  private AssessmentUserPagination rawReportDataTestResults;
  private static String TEST_USER_ID = "823716498123";
  private static String TEST_USER_ID_2 = "1234567";
  private static Boolean TEST_PASS = Boolean.FALSE;
  private static Double TEST_SCORE = 0.12;
  private static Calendar TEST_COMPLETED_DATE = Calendar.getInstance();

  private static String REPORT_SOURCE_SERVICE_TESTS = "com.workmarket.service.business.AssessmentServiceImpl";
  private static String REPORT_SOURCE_CALLABLE_TESTS = "findAssessmentsByCompany";
  private static String REPORT_SOURCE_CALLABLE_PARAMETER_TYPES_TESTS = "[\"java.lang.Long\", \"com.workmarket.domains.model.assessment.AssessmentPagination\"]";
  private static String REPORT_SOURCE_CALLABLE_INPUT_CONVERTER_TESTS = "reportRequestToParameterListForFindAssessmentsByCompany";
  private static String REPORT_SOURCE_CALLABLE_OUTPUT_CONVERTER_TESTS = "assessmentPaginationToApiReportData";
  private static String REPORT_SOURCE_CALLABLE_OUTPUT_CONVERTER_PARAMETER_TYPES_TESTS = "[\"com.workmarket.domains.model.assessment.AssessmentPagination\"]";
  private static String REPORT_SOURCE_FILTER_FIELD_WHITELIST_TESTS = "[]";
  private static String REPORT_SOURCE_FILTER_FIELD_REQUIRED_TESTS = "[]";
  private AssessmentPagination rawReportDataTests;
  private static String TEST_NAME = "test name";

  private List<Common.MetadataItem> getReportMetadataSource(
    final String service,
    final String callable,
    final String callableParameterTypes,
    final String callableInputConverter,
    final String callableOutputConverter,
    final String callableOutputConverterParameterTypes,
    final String filterFieldWhiteList,
    final String filterFieldRequired) {

    return Lists.newArrayList(
        Common.MetadataItem.newBuilder()
            .setKey("service")
            .setValue(service)
            .build(),
        Common.MetadataItem.newBuilder()
            .setKey("callable")
            .setValue(callable)
            .build(),
        Common.MetadataItem.newBuilder()
            .setKey("callableParameterTypes")
            .setValue(callableParameterTypes)
            .build(),
        Common.MetadataItem.newBuilder()
            .setKey("callableInputConverter")
            .setValue(callableInputConverter)
            .build(),
        Common.MetadataItem.newBuilder()
            .setKey("callableOutputConverter")
            .setValue(callableOutputConverter)
            .build(),
        Common.MetadataItem.newBuilder()
            .setKey("callableOutputConverterParameterTypes")
            .setValue(callableOutputConverterParameterTypes)
            .build(),
        Common.MetadataItem.newBuilder()
            .setKey("filterFieldWhiteList")
            .setValue(filterFieldWhiteList)
            .build(),
        Common.MetadataItem.newBuilder()
            .setKey("filterFieldRequired")
            .setValue(filterFieldRequired)
            .build());
  }

  private CollectionResponse getTestResultsReportMetadata() {
    final List<Common.MetadataItem> sourceMetadata = getReportMetadataSource(
        REPORT_SOURCE_SERVICE_TEST_RESULTS,
        REPORT_SOURCE_CALLABLE_TEST_RESULTS,
        REPORT_SOURCE_CALLABLE_PARAMETER_TYPES_TEST_RESULTS,
        REPORT_SOURCE_CALLABLE_INPUT_CONVERTER_TEST_RESULTS,
        REPORT_SOURCE_CALLABLE_OUTPUT_CONVERTER_TEST_RESULTS,
        REPORT_SOURCE_CALLABLE_OUTPUT_CONVERTER_PARAMETER_TYPES_TEST_RESULTS,
        REPORT_SOURCE_FILTER_FIELD_WHITELIST_TEST_RESULTS,
        REPORT_SOURCE_FILTER_FIELD_REQUIRED_TEST_RESULTS
    );

    final List<Common.MetadataItem> schemaMetadata = Lists.newArrayList(
        Common.MetadataItem.newBuilder()
            .setKey("test_id")
            .setValue("int")
            .build(),
        Common.MetadataItem.newBuilder()
            .setKey("user_id")
            .setValue("int")
            .build(),
        Common.MetadataItem.newBuilder()
            .setKey("score")
            .setValue("double")
            .build(),
        Common.MetadataItem.newBuilder()
            .setKey("pass")
            .setValue("boolean")
            .build(),
        Common.MetadataItem.newBuilder()
            .setKey("completed_date")
            .setValue("datetime")
            .build());

    return getReportMetadata(sourceMetadata, schemaMetadata, null);
  }

  public void testResultsReportSetup() {
    when(collectionClient.getItemsInCollection(REPORT_UUID, requestContext))
        .thenReturn(Observable.just(getTestResultsReportMetadata()));

    when(serviceMap.containsKey(REPORT_SOURCE_SERVICE_TEST_RESULTS)).thenReturn(true);
    when(serviceResource1.getServiceRef()).thenReturn(assessmentService);
    when(serviceResource1.getWhitelistedCallables()).thenReturn(Sets.newHashSet(REPORT_SOURCE_CALLABLE_TEST_RESULTS));
    when(serviceMap.get(REPORT_SOURCE_SERVICE_TEST_RESULTS)).thenReturn(serviceResource1);

    rawReportDataTestResults = new AssessmentUserPagination();

    final AssessmentUser assessmentUser = new AssessmentUser();
    assessmentUser.setAssessmentId(TEST_ID);
    assessmentUser.setPassedFlag(TEST_PASS);
    assessmentUser.setScore(TEST_SCORE);
    assessmentUser.setCompletedOn(TEST_COMPLETED_DATE);
    assessmentUser.setUserNumber(TEST_USER_ID);

    final AssessmentUser assessmentUser2 = new AssessmentUser();
    assessmentUser2.setAssessmentId(TEST_ID);
    assessmentUser2.setPassedFlag(TEST_PASS);
    assessmentUser2.setUserNumber(TEST_USER_ID_2);

    final List<AssessmentUser> results = Lists.newArrayList(assessmentUser, assessmentUser2);
    rawReportDataTestResults.setResults(results);
    rawReportDataTestResults.setRowCount(TOTAL_ROW_COUNT);

    when(assessmentService.findLatestAssessmentUserAttempts(eq(TEST_ID), any(AssessmentUserPagination.class))).thenReturn(rawReportDataTestResults);
  }

  @Test
  public void getReportByCompany_missingRequiredFilterFields_returnBadRequest_testResults() throws Exception {
    testResultsReportSetup();

    mockMvc
        .perform(get(
            String.format(REPORTS_URL_BY_COMPANY, COMPANY_UUID, REPORT_ID)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  public void getReportByCompany_returnSuccess_testResults() throws Exception {
    testResultsReportSetup();

    final MockHttpServletResponse response = mockMvc
        .perform(get(
            String.format(REPORTS_URL_BY_COMPANY, COMPANY_UUID, REPORT_ID) +
            String.format("?filterField1=test_id&filterField1Value=%d&filterField2=user_id&filterField2Value=%s",
                TEST_ID,
                TEST_USER_ID)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse();

    final Map<String, Object> responseMap = getHttpResponseMap(response);

    // pagination
    final Map<String, Integer> pagination = getResponsePagination(responseMap);
    Assert.assertEquals(0, (int)pagination.get("offset"));
    Assert.assertEquals(100, (int)pagination.get("limit"));
    Assert.assertEquals(TOTAL_ROW_COUNT, pagination.get("results"));

    final List<Map<String, Object>> payload = getResponsePayload(responseMap);

    // report metadata
    Assert.assertEquals(1, payload.size());
    Assert.assertEquals(5, getResponseReportSchema(payload).size());
    Assert.assertEquals(REPORT_DESCRIPTION, payload.get(0).get("description"));
    Assert.assertEquals(REPORT_NAME, payload.get(0).get("name"));

    // report data
    final List<Map<String, Object>> reportRows = getResponseReportRows(payload);
    final Map<String, Object> reportRow = reportRows.get(0);

    Assert.assertEquals(TEST_ID, new Long((Integer)reportRow.get("test_id")));
    Assert.assertEquals(TEST_USER_ID, reportRow.get("user_id"));

    final SimpleDateFormat sdf = new SimpleDateFormat(ApiV3DataAccessController.DATE_API_DATETIME_FORMAT);
    final String reportDateString = (String)reportRow.get("completed_date");

    Assert.assertEquals(sdf.format(TEST_COMPLETED_DATE.getTime()), reportDateString);

    Assert.assertEquals(TEST_PASS, reportRow.get("pass"));
    Assert.assertEquals(TEST_SCORE, reportRow.get("score"));

    final Map<String, Object> reportRow2 = reportRows.get(1);
    Assert.assertEquals("", reportRow2.get("completed_date"));
    Assert.assertEquals("", reportRow2.get("score"));
  }

  private CollectionResponse getTestsReportMetadata() {
    final List<Common.MetadataItem> sourceMetadata = getReportMetadataSource(
        REPORT_SOURCE_SERVICE_TESTS,
        REPORT_SOURCE_CALLABLE_TESTS,
        REPORT_SOURCE_CALLABLE_PARAMETER_TYPES_TESTS,
        REPORT_SOURCE_CALLABLE_INPUT_CONVERTER_TESTS,
        REPORT_SOURCE_CALLABLE_OUTPUT_CONVERTER_TESTS,
        REPORT_SOURCE_CALLABLE_OUTPUT_CONVERTER_PARAMETER_TYPES_TESTS,
        REPORT_SOURCE_FILTER_FIELD_WHITELIST_TESTS,
        REPORT_SOURCE_FILTER_FIELD_REQUIRED_TESTS
    );

    final List<Common.MetadataItem> schemaMetadata = Lists.newArrayList(
        Common.MetadataItem.newBuilder()
            .setKey("test_id")
            .setValue("int")
            .build(),
        Common.MetadataItem.newBuilder()
            .setKey("test_name")
            .setValue("string")
            .build());

    return getReportMetadata(sourceMetadata, schemaMetadata, null);
  }

  public void testsReportSetup() {
    when(collectionClient.getItemsInCollection(REPORT_UUID, requestContext))
        .thenReturn(Observable.just(getTestsReportMetadata()));

    when(serviceMap.containsKey(REPORT_SOURCE_SERVICE_TESTS)).thenReturn(true);
    when(serviceResource1.getServiceRef()).thenReturn(assessmentService);
    when(serviceResource1.getWhitelistedCallables()).thenReturn(Sets.newHashSet(REPORT_SOURCE_CALLABLE_TESTS));
    when(serviceMap.get(REPORT_SOURCE_SERVICE_TESTS)).thenReturn(serviceResource1);

    rawReportDataTests = new AssessmentPagination();

    final AbstractAssessment assessment = new GradedAssessment();
    assessment.setId(TEST_ID);
    assessment.setName(TEST_NAME);

    final List<AbstractAssessment> results = Lists.newArrayList(assessment);
    rawReportDataTests.setResults(results);
    rawReportDataTests.setRowCount(TOTAL_ROW_COUNT);

    when(assessmentService.findAssessmentsByCompany(eq(REPORT_OWNER_COMPANY_ID), any(AssessmentPagination.class))).thenReturn(rawReportDataTests);
  }

  @Test
  public void getReportByCompany_returnSuccess_tests() throws Exception {
    testsReportSetup();

    final MockHttpServletResponse response = mockMvc
        .perform(get(
            String.format(REPORTS_URL_BY_COMPANY, COMPANY_UUID, REPORT_ID)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse();

    final Map<String, Object> responseMap = getHttpResponseMap(response);

    // pagination
    final Map<String, Integer> pagination = getResponsePagination(responseMap);
    Assert.assertEquals(0, (int)pagination.get("offset"));
    Assert.assertEquals(100, (int)pagination.get("limit"));
    Assert.assertEquals(TOTAL_ROW_COUNT, pagination.get("results"));

    final List<Map<String, Object>> payload = getResponsePayload(responseMap);

    // report metadata
    Assert.assertEquals(1, payload.size());
    Assert.assertEquals(2, getResponseReportSchema(payload).size());
    Assert.assertEquals(REPORT_DESCRIPTION, payload.get(0).get("description"));
    Assert.assertEquals(REPORT_NAME, payload.get(0).get("name"));

    // report data
    final List<Map<String, Object>> reportRows = getResponseReportRows(payload);
    final Map<String, Object> reportRow = reportRows.get(0);

    Assert.assertEquals(TEST_ID, new Long((Integer)reportRow.get("test_id")));
    Assert.assertEquals(TEST_NAME, reportRow.get("test_name"));
  }

  private List<Common.MetadataItem> getWorkReportSchemaMetadata() {
    return Lists.newArrayList(
        Common.MetadataItem.newBuilder()
            .setKey("internal_owner_last_name")
            .setValue("string")
            .build(),
        Common.MetadataItem.newBuilder()
            .setKey("per_hour_price_initial")
            .setValue("double")
            .build(),
        Common.MetadataItem.newBuilder()
            .setKey("per_hour_price_additional")
            .setValue("double")
            .build(),
        Common.MetadataItem.newBuilder()
            .setKey("hours_worked")
            .setValue("int")
            .build(),
        Common.MetadataItem.newBuilder()
            .setKey("final_cost")
            .setValue("double")
            .build(),
        Common.MetadataItem.newBuilder()
            .setKey("location_state")
            .setValue("string")
            .build(),
        Common.MetadataItem.newBuilder()
            .setKey("sent_date")
            .setValue("datetime")
            .build(),
        Common.MetadataItem.newBuilder()
            .setKey("complete_date")
            .setValue("datetime")
            .build(),
        Common.MetadataItem.newBuilder()
            .setKey("close_date")
            .setValue("datetime")
            .build(),
        Common.MetadataItem.newBuilder()
            .setKey("custom_field__" + CUSTOM_FIELD_ID_1)
            .setValue("string")
            .build());
  }

  private CollectionResponse getWorkReportMetadata() {
    final List<Common.MetadataItem> sourceMetadata = getReportMetadataSource(
        REPORT_SOURCE_SERVICE,
        REPORT_SOURCE_CALLABLE,
        REPORT_SOURCE_CALLABLE_PARAMETER_TYPES,
        REPORT_SOURCE_CALLABLE_INPUT_CONVERTER,
        REPORT_SOURCE_CALLABLE_OUTPUT_CONVERTER,
        REPORT_SOURCE_CALLABLE_OUTPUT_CONVERTER_PARAMETER_TYPES,
        REPORT_SOURCE_FILTER_FIELD_WHITELIST,
        REPORT_SOURCE_FILTER_FIELD_REQUIRED
    );

    final List<Common.MetadataItem> schemaMetadata = getWorkReportSchemaMetadata();

    final List<Common.MetadataItem> joinMetadata = getWorkReportJoinMetadata();

    return getReportMetadata(sourceMetadata, schemaMetadata, joinMetadata);
  }

  private List<Common.MetadataItem> getWorkReportJoinMetadata() {
    return Lists.newArrayList(
          Common.MetadataItem.newBuilder()
              .setKey("name")
              .setValue("custom_field")
              .build(),
          Common.MetadataItem.newBuilder()
              .setKey("foreignKey")
              .setValue("work_id")
              .build(),
          Common.MetadataItem.newBuilder()
              .setKey("service")
              .setValue(REPORT_JOIN_SERVICE)
              .build(),
          Common.MetadataItem.newBuilder()
              .setKey("callable")
              .setValue(REPORT_JOIN_CALLABLE)
              .build(),
          Common.MetadataItem.newBuilder()
              .setKey("callableMerger")
              .setValue("mergeWorkCustomFieldsIntoApiReportData")
              .build(),
          Common.MetadataItem.newBuilder()
              .setKey("filter_ids")
              .setValue("[" + CUSTOM_FIELD_ID_1 + "]")
              .build());
  }

  @Before
  public void workReportSetup() throws Exception {
    super.setup(controller);
    requestContext = new RequestContext(UUID.randomUUID().toString(), "DUMMY_TENANT_ID");
    requestContext.setUserId("workmarket");
    when(webRequestContextProvider.getRequestContext()).thenReturn(requestContext);
    when(dataAccessClient.getReports(Lists.newArrayList(REPORT_ID), requestContext)).thenReturn(Observable.just(Messages.ReportsResponse.getDefaultInstance()));
    when(dataAccessClient.getAllReports(requestContext)).thenReturn(Observable.just(Messages.ReportsResponse.getDefaultInstance()));
    when(messageHelper.newBundle()).thenReturn(new MessageBundle());
    when(companyService.findCompanyIdByUuid(COMPANY_UUID)).thenReturn(REPORT_OWNER_COMPANY_ID);
    when(extendedUserDetailsService.loadUser(any(User.class))).thenReturn(getExtendedUserDetailsByCompany(REPORT_OWNER_COMPANY_ID));

    final CollectionsResponse collectionsResponse = CollectionsResponse.newBuilder()
        .addCollections(Collection.newBuilder()
            .setDisplayName(REPORT_NAME)
            .setUuid(REPORT_UUID)
            .setMetadata(
                Common.Metadata.newBuilder()
                    .addData(Common.MetadataItem.newBuilder()
                        .setKey("id")
                        .setValue(REPORT_ID.toString())
                        .build())
                    .addData(Common.MetadataItem.newBuilder()
                        .setKey("description")
                        .setValue(REPORT_DESCRIPTION)
                        .build()))
            .build())
        .build();

    when(collectionClient.getCollectionsInNamespace(
        String.format(ApiV3DataAccessController.DATA_API_REPORTS_NAMESPACE_BY_CID, REPORT_OWNER_COMPANY_ID),
        requestContext))
        .thenReturn(Observable.just(collectionsResponse));

    when(collectionClient.getItemsInCollection(REPORT_UUID, requestContext))
        .thenReturn(Observable.just(getWorkReportMetadata()));

    when(serviceMap.containsKey(REPORT_SOURCE_SERVICE)).thenReturn(true);
    when(serviceResource1.getServiceRef()).thenReturn(workService);
    when(serviceResource1.getWhitelistedCallables()).thenReturn(Sets.newHashSet(REPORT_SOURCE_CALLABLE));
    when(serviceMap.get(REPORT_SOURCE_SERVICE)).thenReturn(serviceResource1);

    final Map<String, Object> row = Maps.newLinkedHashMap();
    row.put("work_id", WORK_ID);
    row.put("location_state", "A");
    row.put("internal_owner_last_name", "B");
    row.put("company_id", 2L);
    row.put("hours_worked", 3);
    row.put("per_hour_price_initial", null);
    row.put("per_hour_price_additional", BigDecimal.valueOf(2));
    row.put("final_cost", BigDecimal.valueOf(3));
    row.put("close_date", new Date());
    row.put("sent_date", new Date());
    row.put("complete_date", new Date());
    final List<Map<String, Object>> results = Lists.newArrayList(row);

    rawReportDataWork = ImmutableMap.of("results", results, "totalRows", 1);
    when(workService.getAssignmentDataOne(anyMapOf(String.class, Object.class))).thenReturn(rawReportDataWork);

    when(serviceMap.containsKey(REPORT_SOURCE_SERVICE)).thenReturn(true);
    when(serviceResource1.getServiceRef()).thenReturn(workService);
    when(serviceResource1.getWhitelistedCallables()).thenReturn(Sets.newHashSet(REPORT_SOURCE_CALLABLE));
    when(serviceMap.get(REPORT_SOURCE_SERVICE)).thenReturn(serviceResource1);

    final Map<String, Object> rawJoinData = ImmutableMap.<String, Object>of(
        "workIdToCustomFields", ImmutableMap.of(
            WORK_ID,
            Lists.newArrayList(ImmutableMap.<String, Object>of(
                "customFieldId", CUSTOM_FIELD_ID_1,
                "customFieldValue", "foo"))),
        "customFields", Lists.newArrayList(ImmutableMap.<String, Object>of(
            "customFieldName", "worker's fav soda",
            "customFieldId", CUSTOM_FIELD_ID_1)));
    when(workReportService.getWorkCustomFieldsMapForBuyer(Matchers.<Map<String, List<Long>>>any())).thenReturn(rawJoinData);

    when(serviceMap.containsKey(REPORT_JOIN_SERVICE)).thenReturn(true);
    when(serviceResource2.getServiceRef()).thenReturn(workReportService);
    when(serviceResource2.getWhitelistedCallables()).thenReturn(Sets.newHashSet(REPORT_JOIN_CALLABLE));
    when(serviceMap.get(REPORT_JOIN_SERVICE)).thenReturn(serviceResource2);
  }

  private CollectionResponse getReportMetadata(
      final List<Common.MetadataItem> sourceMetadata,
      final List<Common.MetadataItem> schemaMetadata,
      final List<Common.MetadataItem> joinMetadata) {

    final Collection.Builder collectionBuilder = Collection.newBuilder()
        .addAllItems(Lists.newArrayList(
            Common.Item.newBuilder()
                .setObjectType("source")
                .setMetadata(Common.Metadata.newBuilder().addAllData(sourceMetadata).build())
                .build(),
            Common.Item.newBuilder()
                .setObjectType("schema")
                .setMetadata(Common.Metadata.newBuilder().addAllData(schemaMetadata).build())
                .build()));

    if (joinMetadata != null) {
      collectionBuilder.addItems(
          Common.Item.newBuilder()
              .setObjectType("join")
              .setMetadata(Common.Metadata.newBuilder().addAllData(joinMetadata).build())
              .build());
    }

    return CollectionResponse.newBuilder()
          .setCollection(collectionBuilder.build())
          .build();
  }

  private ExtendedUserDetails getExtendedUserDetailsByCompany(final Long companyId) {
    ExtendedUserDetails extendedUserDetails = getExtendedUserDetails();
    extendedUserDetails.setCompanyId(companyId);
    return extendedUserDetails;
  }

  @Test
  public void getReports_isInternalUser_returnSuccess() throws Exception {
    ExtendedUserDetails extendedUserDetails = getExtendedUserDetails();
    extendedUserDetails.setCompanyId(Constants.WM_COMPANY_ID);
    when(extendedUserDetailsService.loadUser(any(User.class))).thenReturn(extendedUserDetails);

    mockMvc
        .perform(get(REPORT_URL).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();
  }

  @Test
  public void getReports_isNotInternalUser_returnForbidden() throws Exception {
    when(extendedUserDetailsService.loadUser(any(User.class))).thenReturn(getExtendedUserDetailsByCompany(REPORT_OWNER_COMPANY_ID));

    mockMvc
        .perform(get(REPORT_URL).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andReturn();
  }

  @Test
  public void getAllReports_isInternalUser_returnSuccess() throws Exception {
    ExtendedUserDetails extendedUserDetails = getExtendedUserDetails();
    extendedUserDetails.setCompanyId(Constants.WM_COMPANY_ID);
    when(extendedUserDetailsService.loadUser(any(User.class))).thenReturn(extendedUserDetails);

    mockMvc
        .perform(get(REPORTS_URL).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();
  }

  @Test
  public void getAllReports_isNotInternalUser_returnForbidden() throws Exception {
    when(extendedUserDetailsService.loadUser(any(User.class))).thenReturn(getExtendedUserDetailsByCompany(REPORT_OWNER_COMPANY_ID));

    mockMvc
        .perform(get(REPORTS_URL).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andReturn();
  }

  @Test
    public void getReportByCompany_isNeitherInternalUserNorReportOwner_returnForbidden() throws Exception {
    when(extendedUserDetailsService.loadUser(any(User.class))).thenReturn(getExtendedUserDetailsByCompany(NON_REPORT_OWNER_NON_INTERNAL_COMPANY_ID));

    mockMvc
        .perform(get(String.format(REPORTS_URL_BY_COMPANY, COMPANY_UUID, REPORT_ID)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andReturn();
  }

  @Test
  public void getReportByCompany_companyDoesNotExist_returnForbidden() throws Exception {
    when(companyService.findCompanyIdByUuid(COMPANY_UUID)).thenReturn(null);

    mockMvc
        .perform(get(String.format(REPORTS_URL_BY_COMPANY, COMPANY_UUID, REPORT_ID)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andReturn();
  }

  @Test
  public void getReportByCompany_reportsCollectionCallThrowsError_returnServerError() throws Exception {
    when(collectionClient.getCollectionsInNamespace(anyString(), any(RequestContext.class)))
        .thenThrow(new RuntimeException());

    mockMvc
        .perform(get(String.format(REPORTS_URL_BY_COMPANY, COMPANY_UUID, REPORT_ID)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andReturn();
  }

  @Test
  public void getReportByCompany_reportsCollectionDoesNotExists_returnBadRequest() throws Exception {
    when(collectionClient.getCollectionsInNamespace(anyString(), any(RequestContext.class)))
        .thenReturn(Observable.just(CollectionsResponse.getDefaultInstance()));

    mockMvc
        .perform(get(String.format(REPORTS_URL_BY_COMPANY, COMPANY_UUID, REPORT_ID)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  public void getReportByCompany_itemsInCollectionCallThrowsError_returnServerError() throws Exception {
    when(collectionClient.getItemsInCollection(anyString(), any(RequestContext.class)))
        .thenThrow(new RuntimeException());

    mockMvc
        .perform(get(String.format(REPORTS_URL_BY_COMPANY, COMPANY_UUID, REPORT_ID)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andReturn();
  }

  @Test
  public void getReportByCompany_noItemsInCollection_returnServerError() throws Exception {
    when(collectionClient.getItemsInCollection(REPORT_UUID, requestContext))
        .thenReturn(Observable.just(CollectionResponse.getDefaultInstance()));

    mockMvc
        .perform(get(String.format(REPORTS_URL_BY_COMPANY, COMPANY_UUID, REPORT_ID)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andReturn();
  }

  @Test
  public void getReportByCompany_reportSourceServiceDoesNotExistInServiceMap_returnServerError() throws Exception {
    when(serviceMap.containsKey(REPORT_SOURCE_SERVICE)).thenReturn(false);

    mockMvc
        .perform(get(String.format(REPORTS_URL_BY_COMPANY, COMPANY_UUID, REPORT_ID)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andReturn();
  }

  @Test
  public void getReportByCompany_reportSourceCallableIsNotWhitelisted_returnServerError() throws Exception {
    when(serviceResource1.getWhitelistedCallables()).thenReturn(Sets.<String>newHashSet());

    mockMvc
        .perform(get(String.format(REPORTS_URL_BY_COMPANY, COMPANY_UUID, REPORT_ID)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andReturn();
  }

  @Test
  public void getReportByCompany_noReportData_returnEmptyResponse() throws Exception {
    when(workService.getAssignmentDataOne(anyMapOf(String.class, Object.class))).thenReturn(
        ImmutableMap.<String, Object>of("results", Lists.newArrayList(), "totalRows", 0)
    );

    final MockHttpServletResponse response = mockMvc
        .perform(get(String.format(REPORTS_URL_BY_COMPANY, COMPANY_UUID, REPORT_ID)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse();
    final ObjectMapper mapper = new ObjectMapper();
    final Map<String, Map<String, Object>> responseMap = mapper.readValue(
        response.getContentAsString(),
        mapper.getTypeFactory().constructMapType(Map.class, String.class, Map.class));
    Assert.assertEquals(0, ((List<Object>)responseMap.get("result").get("payload")).size());
  }

  @Test
  public void getReportByCompany_validate_missingField_returnServerError() throws Exception {;
    Map<String, Object> reportRow = Maps.newHashMap(((List<Map<String, Object>>) rawReportDataWork.get("results")).get(0));
    reportRow.remove("hours_worked");
    Map<String, Object> rawReportDataMissingField = Maps.newHashMap(rawReportDataWork);
    rawReportDataMissingField.put("results", Lists.newArrayList(reportRow));

     when(workService.getAssignmentDataOne(anyMapOf(String.class, Object.class))).thenReturn(rawReportDataMissingField);

    mockMvc
        .perform(get(String.format(REPORTS_URL_BY_COMPANY, COMPANY_UUID, REPORT_ID)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andReturn();
  }

  @Test
  public void getReportByCompany_withReportData_returnSuccess() throws Exception {
    final MockHttpServletResponse response = mockMvc
        .perform(get(String.format(REPORTS_URL_BY_COMPANY, COMPANY_UUID, REPORT_ID)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse();

    final Map<String, Object> responseMap = getHttpResponseMap(response);

    // pagination
    final Map<String, Integer> pagination = getResponsePagination(responseMap);
    Assert.assertEquals(0, (int)pagination.get("offset"));
    Assert.assertEquals(100, (int)pagination.get("limit"));
    Assert.assertEquals(1, (int)pagination.get("results"));

    final List<Map<String, Object>> payload = getResponsePayload(responseMap);

    // report metadata
    Assert.assertEquals(1, payload.size());
    Assert.assertEquals(10, getResponseReportSchema(payload).size());
    Assert.assertEquals(REPORT_DESCRIPTION, payload.get(0).get("description"));
    Assert.assertEquals(REPORT_NAME, payload.get(0).get("name"));

    // report data
    final Map<String, Object> reportRow = getResponseReportRows(payload).get(0);
    Assert.assertEquals("3.00", reportRow.get("final_cost"));

    final SimpleDateFormat sdf = new SimpleDateFormat(ApiV3DataAccessController.DATE_API_DATETIME_FORMAT);
    final Date date = sdf.parse((String)reportRow.get("close_date"));
    Assert.assertEquals(reportRow.get("close_date"), sdf.format(date));

    Assert.assertEquals(3, reportRow.get("hours_worked"));
    Assert.assertEquals("", reportRow.get("per_hour_price_initial"));
  }

  private List<Map<String, Object>> getResponsePayload(Map<String, Object> responseMap) {
    final Map<String, Object> resultMap = ((Map<String, Object>) responseMap.get("result"));
    return ((List<Map<String, Object>>)resultMap.get("payload"));
  }

  private Map<String, Object> getHttpResponseMap(MockHttpServletResponse response) throws java.io.IOException {
    final ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(
        response.getContentAsString(),
        mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class));
  }

  private Map<String, Integer> getResponsePagination(final Map<String, Object> responseMap) {
    final Map<String, Object> resultMap = ((Map<String, Object>) responseMap.get("result"));
    return (Map<String, Integer>)resultMap.get("pagination");
  }

  private Map<String, Object> getResponseReportSchema(final List<Map<String, Object>> payload) {
    return (Map<String, Object>)payload.get(0).get("schema");
  }

  private List<Map<String, Object>> getResponseReportRows(final List<Map<String, Object>> payload) {
    return (List<Map<String, Object>>)payload.get(0).get("results");
  }

  @Test
  public void getReportByCompany_filterField1IsNotSetAndFilterField1ValueIsSet_returnBadRequest() throws Exception {
    mockMvc
        .perform(
            get(String.format(REPORTS_URL_BY_COMPANY, COMPANY_UUID, REPORT_ID) + "?filterField1Value=2017-07-01")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  public void getReportByCompany_filterField1IsSetFilterField1ValueIsNotSet_returnBadRequest() throws Exception {
    mockMvc
        .perform(
            get(String.format(REPORTS_URL_BY_COMPANY, COMPANY_UUID, REPORT_ID) + "?filterField1=sent_date_to")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  public void getReportByCompany_filterField1IsInvalid_returnBadRequest() throws Exception {
    mockMvc
        .perform(
            get(String.format(REPORTS_URL_BY_COMPANY, COMPANY_UUID, REPORT_ID) + "?filterField1=jorp&filterField1Value=2017-07-01")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  public void getReportByCompany_filterField1ValueIsInvalid_returnBadRequest() throws Exception {
    mockMvc
        .perform(
            get(String.format(REPORTS_URL_BY_COMPANY, COMPANY_UUID, REPORT_ID) + "?filterField1=sent_date_to&filterField1Value=2017-07-01zzz")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  public void getReportByCompany_filterField1IsSetFilterField1ValueIsSet_returnSuccess() throws Exception {

    mockMvc
        .perform(
            get(String.format(REPORTS_URL_BY_COMPANY, COMPANY_UUID, REPORT_ID) + "?filterField1=sent_date_to&filterField1Value=2017-07-01")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();
  }

  @Test
  public void getReportByCompany_filterFieldWhiteListReferencesUnknownField_returnServerError() throws Exception {
    final List<Common.MetadataItem> metadataSourceWithBadFilterFieldWhiteList = getReportMetadataSource(
        REPORT_SOURCE_SERVICE,
        REPORT_SOURCE_CALLABLE,
        REPORT_SOURCE_CALLABLE_PARAMETER_TYPES,
        REPORT_SOURCE_CALLABLE_INPUT_CONVERTER,
        REPORT_SOURCE_CALLABLE_OUTPUT_CONVERTER,
        REPORT_SOURCE_CALLABLE_OUTPUT_CONVERTER_PARAMETER_TYPES,
        REPORT_SOURCE_FILTER_FIELD_WHITELIST_INVALID,
        REPORT_SOURCE_FILTER_FIELD_REQUIRED
    );

    final CollectionResponse workReportMetadataWithBadFilterFieldWhiteList = getReportMetadata(
        metadataSourceWithBadFilterFieldWhiteList,
        getWorkReportSchemaMetadata(),
        getWorkReportJoinMetadata());

    when(collectionClient.getItemsInCollection(REPORT_UUID, requestContext))
        .thenReturn(Observable.just(workReportMetadataWithBadFilterFieldWhiteList));

    mockMvc
        .perform(
            get(String.format(REPORTS_URL_BY_COMPANY, COMPANY_UUID, REPORT_ID))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andReturn();
  }
}
