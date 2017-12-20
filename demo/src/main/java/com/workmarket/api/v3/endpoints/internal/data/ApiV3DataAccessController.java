package com.workmarket.api.v3.endpoints.internal.data;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.ApiBaseError;
import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.exceptions.BadRequestApiException;
import com.workmarket.api.v3.ApiV3ResponseImpl;
import com.workmarket.api.v3.model.ApiReportDTO;
import com.workmarket.api.v3.model.ApiReportData;
import com.workmarket.api.v3.model.ApiReportDatatype;
import com.workmarket.api.v3.model.ApiReportFilterField;
import com.workmarket.api.v3.model.ApiReportMetadata;
import com.workmarket.api.v3.model.ApiReportRequestDTO;
import com.workmarket.api.v3.model.ApiReportResponseDTO;
import com.workmarket.api.v3.response.ApiV3Response;
import com.workmarket.api.v3.response.ApiV3ResponseResultPaginationImpl;
import com.workmarket.api.v3.response.result.ApiV3Error;
import com.workmarket.collection.CollectionClient;
import com.workmarket.collection.gen.Common;
import com.workmarket.common.api.exception.InternalServerErrorException;
import com.workmarket.configuration.Constants;
import com.workmarket.data.DataAccessClient;
import com.workmarket.data.gen.Messages;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.AssessmentPagination;
import com.workmarket.domains.reports.service.WorkReportService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.dto.AssessmentUser;
import com.workmarket.dto.AssessmentUserPagination;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.web.WebRequestContextProvider;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import rx.functions.Func1;

import static com.workmarket.data.gen.Messages.ReportResponse;
import static com.workmarket.data.gen.Messages.ReportsResponse;
import static com.workmarket.collection.gen.Response.Collection;
import static com.workmarket.collection.gen.Response.CollectionResponse;
import static com.workmarket.collection.gen.Response.CollectionsResponse;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Api(tags = {"constants"})
@Controller
public class ApiV3DataAccessController extends ApiBaseController {
  private static final Logger logger = LoggerFactory.getLogger(ApiV3DataAccessController.class);

  @Autowired private DataAccessClient dataAccessClient;
  @Autowired private WebRequestContextProvider webRequestContextProvider;
  @Autowired private CollectionClient collectionClient;
  @Autowired private WorkService workService;
  @Autowired private WorkReportService workReportService;
  @Autowired private CompanyService companyService;
  @Autowired private AssessmentService assessmentService;

  public final static String DATA_API_REPORTS_NAMESPACE = "data.api.reports";
  public final static String DATA_API_REPORTS_NAMESPACE_BY_CID = DATA_API_REPORTS_NAMESPACE + ".cid.%d";
  public final static String DATE_API_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
  public final static String DATE_API_DOUBLE_FORMAT = "#0.00";
  private final static String UNEXPECTED_ERROR_RESPONSE = "There was an unexpected error.";

  private Map<String, ServiceResource> serviceMap = Maps.newHashMap();

  @VisibleForTesting // would be private otherwise
  public static class ServiceResource {
    private final Object serviceRef;
    private final Set<String> whitelistedCallables;

    ServiceResource(final Object serviceRef, final Set<String> whitelistedCallables) {
      this.serviceRef = serviceRef;
      this.whitelistedCallables = whitelistedCallables;
    }

    Object getServiceRef() {
      return serviceRef;
    }

    Set<String> getWhitelistedCallables() {
      return whitelistedCallables;
    }
  }

  @PostConstruct
  public void initServiceMap() throws Exception {
    serviceMap = ImmutableMap.<String, ServiceResource>builder()
        .put("com.workmarket.domains.work.service.WorkServiceImpl",
            new ServiceResource(
                workService,
                ImmutableSet.of("getAssignmentDataOne")))

        .put("com.workmarket.domains.reports.service.WorkReportServiceImpl",
            new ServiceResource(
                workReportService,
                ImmutableSet.of("getWorkCustomFieldsMapForBuyer")))

        .put("com.workmarket.service.business.AssessmentServiceImpl",
            new ServiceResource(
                assessmentService,
                ImmutableSet.of("findLatestAssessmentUserAttempts", "findAssessmentsByCompany")))
        .build();
  }

  @ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
  @ApiOperation(value = "Get report by companyUuid and reportId", tags = {"data", "reports"})
  @RequestMapping(
      value = "/v3/data/reports/{companyUuid}/{reportId}",
      method = RequestMethod.GET,
      produces = APPLICATION_JSON_VALUE)
  @ResponseBody
  public ApiV3Response<ApiReportResponseDTO> getReportByCompany(
      final @ApiParam @Valid ApiReportRequestDTO reportRequestDTO,
      final @PathVariable("companyUuid") String companyUuid,
      final @PathVariable("reportId") Long reportId) {

    final Long companyId = companyService.findCompanyIdByUuid(companyUuid);
    final Long currentUserCompanyId = getCurrentUser().getCompanyId();
    checkForInternalUserOrCompanyUser(companyId, currentUserCompanyId);

    final Optional<ApiReportMetadata> reportMetadataOptional = getReportMetadata(reportId, companyId);
    if (!reportMetadataOptional.isPresent()) {
        throw new BadRequestApiException(String.format("No record for report with id %d for company with uuid %s", reportId, companyUuid));
    }

    final ApiReportMetadata reportMetadata = reportMetadataOptional.get();

    final Map<String, Object> typedFilterFieldsMap = getTypedFilterFieldsMap(
        reportRequestDTO,
        validateFilterFieldsAndReturnFilterFieldsLookup(reportRequestDTO, reportMetadata));

    final ApiReportData reportData = getApiReportData(
        reportRequestDTO,
        reportId,
        companyId,
        reportMetadata,
        typedFilterFieldsMap);

    if (reportData.getTotalCount() == 0) {
      return ApiV3ResponseImpl.valueWithMessage("Report contains no data", HttpStatus.OK);
    }

    final List<Map<String, Object>> validatedPayload = validateReportPayload(
        reportData,
        reportMetadata,
        reportId,
        companyId);

    final ApiReportResponseDTO apiReportResponseDTO = new ApiReportResponseDTO.Builder()
        .withName(reportMetadata.getName())
        .withDescription(reportMetadata.getDescription())
        .withSchema(formatReportSchema(reportData, reportMetadata))
        .withResults(formatReportPayload(validatedPayload, reportData))
        .build();

    final ApiV3ResponseResultPaginationImpl pagination = new ApiV3ResponseResultPaginationImpl(
        reportRequestDTO.getOffset(),
        reportRequestDTO.getLimit(),
        reportData.getTotalCount());

    return new ApiV3ResponseImpl(
        new ApiJSONPayloadMap(),
        null,
        null,
        pagination,
        Lists.newArrayList(apiReportResponseDTO));
  }

  private ApiReportData getApiReportData(
      final ApiReportRequestDTO reportRequestDTO,
      final Long reportId,
      final Long companyId,
      final ApiReportMetadata reportMetadata,
      final Map<String, Object> typedFilterFieldsMap) {

    final Map<String, Object> serviceRequestParameters = ImmutableMap.<String, Object>builder()
        .put("companyId", companyId)
        .put("reportId", reportId)
        .put("offset", reportRequestDTO.getOffset())
        .put("limit", reportRequestDTO.getLimit())
        .putAll(typedFilterFieldsMap)
        .build();

    final ApiReportData reportDataSource = getReportSourceData(reportId, companyId, reportMetadata, serviceRequestParameters);

    return getReportDataWithJoins(reportId, companyId, reportMetadata, reportDataSource);
  }

  private ApiReportData getReportDataWithJoins(
      final Long reportId,
      final Long companyId,
      final ApiReportMetadata reportMetadata,
      final ApiReportData reportSourceData) {

    if (reportSourceData.getTotalCount() == 0) {
      return reportSourceData;
    }

    final List<ApiReportMetadata.ApiReportJoin> joinsMetadata = reportMetadata.getJoins();
    if (joinsMetadata.isEmpty()) {
      return reportSourceData;
    }

    ApiReportData reportDataWithJoins = ApiReportData.copyOf(reportSourceData);

    // Loop over all joins specified in the report metadata,
    // fetch join data and merge into report data.

    for (final ApiReportMetadata.ApiReportJoin joinMetadata : joinsMetadata) {

      final String foreignKey = joinMetadata.getForeignKey();

      // Create foreign key ids
      final List<Map<String, Object>> fromDataResults = reportSourceData.getResults();
      final List<Long> foreignKeyIds = Lists.newArrayList();
      for (final Map<String, Object> reportRecord : fromDataResults) {
        foreignKeyIds.add((Long) reportRecord.get(foreignKey));
      }

      final String joinName = joinMetadata.getName();

      // Create join callable args
      final Map<String, Object> callableParameter = ImmutableMap.<String, Object>of(
          joinName,
          joinMetadata.getFilterIds(),
          foreignKey,
          foreignKeyIds);

      // Get join data
      final Object joinDataRaw = invokeServiceCallable(
          serviceMap,
          joinMetadata.getService(),
          joinMetadata.getCallable(),
          Collections.singleton(Map.class).toArray(new Class[0]),
          Collections.singleton(callableParameter).toArray());

      // Merge join data into reportData
      try {
        final Method callableMerger = this.getClass().getMethod(
            joinMetadata.getCallableMerger(),
            String.class,
            ApiReportData.class,
            Map.class,
            String.class);
        reportDataWithJoins = (ApiReportData) callableMerger.invoke(this, joinName, reportDataWithJoins, joinDataRaw, foreignKey);

      } catch (final NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
        logger.error(String.format("There was an error merging join data for company id %s and report id %s", companyId, reportId), e);
        throw new InternalServerErrorException(UNEXPECTED_ERROR_RESPONSE);
      }
    }
    return reportDataWithJoins;
  }

  private ApiReportData getReportSourceData(
      final Long reportId,
      final Long companyId,
      final ApiReportMetadata reportMetadata,
      final Map<String, Object> reportRequest) {

    final ApiReportMetadata.ApiReportSource sourceMetadata = reportMetadata.getSource();
    Object[] callableParameters;

    try {
      final Method callableInputConverter = this.getClass().getMethod(
          sourceMetadata.getCallableInputConverter(),
          Map.class);

      callableParameters = (Object[])callableInputConverter.invoke(this, reportRequest);

    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      logger.error(String.format("There was an error fetching source data for company id %s and report id %s", companyId, reportId), e);
      throw new InternalServerErrorException(UNEXPECTED_ERROR_RESPONSE);
    }

    final Object sourceDataUnchecked = invokeServiceCallable(
        serviceMap,
        sourceMetadata.getService(),
        sourceMetadata.getCallable(),
        sourceMetadata.getCallableParameterTypes(),
        callableParameters);

    // Convert unchecked report source data to checked report data model
    final ApiReportData reportData;
    try {
      final Method callableOutputConverter = this.getClass().getMethod(
          sourceMetadata.getCallableOutputConverter(),
          sourceMetadata.getCallableOutputConverterParameterTypes());

      reportData = (ApiReportData) callableOutputConverter.invoke(this, sourceDataUnchecked);

    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      logger.error(String.format("There was an error fetching source data for company id %s and report id %s", companyId, reportId), e);
      throw new InternalServerErrorException(UNEXPECTED_ERROR_RESPONSE);
    }
    return reportData;
  }

  private Map<String, Object> getTypedFilterFieldsMap(
      final ApiReportRequestDTO reportRequestDTO,
      final Map<String, ApiReportFilterField> filterFieldsLookup) {

    if (filterFieldsLookup.isEmpty()) {
      return Maps.newHashMap();
    }

    final ImmutableMap.Builder<String, Object> typedFilterFieldsMap = ImmutableMap.builder();

    for (final AbstractMap.SimpleEntry<String, String> filterField : reportRequestDTO.getFilterFields()) {

      final String filterFieldKey = filterField.getKey();
      if (!filterFieldsLookup.containsKey(filterFieldKey)) {
        continue;
      }

      final Object filterFieldValueTyped = getFilterFieldValueTyped(filterFieldsLookup, filterField, filterFieldKey);

      typedFilterFieldsMap.put(filterFieldKey, filterFieldValueTyped);
    }

    return typedFilterFieldsMap.build();
  }

  private Object getFilterFieldValueTyped(
      final Map<String, ApiReportFilterField> filterFieldsLookup,
      final AbstractMap.SimpleEntry<String, String> filterField,
      final String filterFieldKey) {

    final String filterFieldValue = filterField.getValue();

    if (ApiReportDatatype.DATETIME.equals(filterFieldsLookup.get(filterFieldKey).getFieldType())) {
      try {
        return DateTime.parse(filterFieldValue).toDate();

      } catch (IllegalArgumentException e) {
        throw new BadRequestApiException(String.format(
            "Could not parse value %s for filter %s. Datetime filters must conform to YYYY-MM-DD format.",
            filterFieldValue, filterFieldKey));
      }
    }

    if (ApiReportDatatype.INT.equals(filterFieldsLookup.get(filterFieldKey).getFieldType())) {
      try {
        return Long.parseLong(filterFieldValue);

      } catch (IllegalArgumentException e) {
        throw new BadRequestApiException(String.format(
            "Could not parse value %s for filter %s. Valid Int filter values ranges from -9223372036854775808 to " +
                "9223372036854775807.",
            filterFieldValue, filterFieldKey));
      }
    }

    logger.error(String.format("Filter %s has an unrecognized type of %s.",
        filterFieldKey,
        filterFieldsLookup.get(filterFieldKey).getFieldType()));
    throw new InternalServerErrorException(UNEXPECTED_ERROR_RESPONSE);
  }

  private Map<String, ApiReportFilterField> validateFilterFieldsAndReturnFilterFieldsLookup(
      final ApiReportRequestDTO reportRequestDTO,
      final ApiReportMetadata reportMetadata) {

    // Build dynamic filter fields lookup

    final Map<String, ApiReportMetadata.ApiReportField> schemaMap = reportMetadata.getSchemaMap();
    final Map<String, ApiReportFilterField> filterFieldsLookup = Maps.newHashMap();

    for (final String filterField : reportMetadata.getSource().getFilterFieldWhiteList()) {

      final ApiReportMetadata.ApiReportField field = schemaMap.get(filterField);

      if (ApiReportDatatype.DATETIME.equals(field.getFieldType())) {

        final List<String> legalOps = Lists.newArrayList("from", "to");

        for (final String op : legalOps) {
          filterFieldsLookup.put(
              String.format("%s_%s", field.getFieldName(), op),
              new ApiReportFilterField(field.getFieldName(), field.getFieldType()));
        }

      } else if (ApiReportDatatype.INT.equals(field.getFieldType())) {
        filterFieldsLookup.put(
            field.getFieldName(),
            new ApiReportFilterField(field.getFieldName(), field.getFieldType()));
      }
    }

    // Validate

    for (final AbstractMap.SimpleEntry<String, String> filterField : reportRequestDTO.getFilterFields()) {

      final int filterFieldNumber = reportRequestDTO.getFilterFields().indexOf(filterField) + 1;

      if (StringUtils.isBlank(filterField.getKey()) && StringUtils.isBlank(filterField.getValue())) {
        continue;
      }

      if (StringUtils.isBlank(filterField.getKey()) && !StringUtils.isBlank(filterField.getValue())) {
        throw new BadRequestApiException(String.format("filterField%dValue is set but filterField%d is not.",
            filterFieldNumber, filterFieldNumber));
      }

      if (!StringUtils.isBlank(filterField.getKey()) && StringUtils.isBlank(filterField.getValue())) {
        throw new BadRequestApiException(String.format("filterField%s is set but filterField%sValue is not.",
            filterFieldNumber, filterFieldNumber));
      }
    }

    final Set<String> invalidFilterFields = Sets.difference(reportRequestDTO.getFilterFieldKeySet(), filterFieldsLookup.keySet());

    if (!invalidFilterFields.isEmpty()) {
      throw new BadRequestApiException(String.format(
          "Unrecognized filterField(s) %s. Your available filterFields are: %s",
          invalidFilterFields.toString(),
          filterFieldsLookup.keySet().toString()));
    }

    final Set<String> filterFieldRequired = Sets.newHashSet(reportMetadata.getSource().getFilterFieldRequired());

    final Set<String> missingRequiredFilterFields = Sets.difference(filterFieldRequired, reportRequestDTO.getFilterFieldKeySet());

    if (!missingRequiredFilterFields.isEmpty()) {
      throw new BadRequestApiException(String.format(
          "Required filterField(s) %s are not present in your request.",
          missingRequiredFilterFields.toString()));
    }

    return filterFieldsLookup;
  }

  /**
   * Properly formats a report's schema
   * including hydrating a custom field field with it's actual configured name
   * and lower-casing the field types
   * @param reportData
   * @param reportMetadata
   * @return
   */
  private Map<String, String> formatReportSchema(
      final ApiReportData reportData,
      final ApiReportMetadata reportMetadata) {

    final Map<String, String> schemaWithCustomFields = Maps.newLinkedHashMap();

    for (final ApiReportMetadata.ApiReportField field : reportMetadata.getSchema()) {

      final String fieldName;

      // Hydrates custom field fields with their *actual* names
      if (reportData.getMapCustomFieldSchemaNameToActualName().containsKey(field.getFieldName())) {
        fieldName = reportData.getMapCustomFieldSchemaNameToActualName()
            .get(field.getFieldName())
            .replace(" ", "_").toLowerCase();
      } else {
        fieldName = field.getFieldName();
      }

      // TODO: double check this isn't a breaking change
      schemaWithCustomFields.put(fieldName, field.getFieldType().toString().toLowerCase());
    }

    return schemaWithCustomFields;
  }

  /**
   * Ensure report data conforms to schema.
   * @param apiReportData
   * @param apiReportMetadata
   * @param reportId
   * @param companyId
   * @return
   */
  private List<Map<String, Object>> validateReportPayload(
      final ApiReportData apiReportData,
      final ApiReportMetadata apiReportMetadata,
      final Long reportId,
      final Long companyId) {

    final Set<String> fieldNames = Sets.newHashSet(apiReportMetadata.getFieldNames());

    final List<Map<String, Object>> payload = apiReportData.getResults();
    final List<Map<String, Object>> validatedPayload = Lists.newArrayList();

    for (final Map<String, Object> row : payload) {

      final Map<String, Object> validatedRow = Maps.newLinkedHashMap();
      final Set<String> missingFields = Sets.difference(fieldNames, row.keySet());

      if (!missingFields.isEmpty()) {
        logger.error(
            String.format("Report with id %d for company with id %d is missing fields: %s",
              reportId, companyId, missingFields.toString()));
        throw new InternalServerErrorException(UNEXPECTED_ERROR_RESPONSE);
      }

      final Set<String> extraFields = Sets.difference(row.keySet(), fieldNames);
      for (Map.Entry<String, Object> field : row.entrySet()) {
        if (!extraFields.contains(field.getKey())) {
          validatedRow.put(field.getKey(), field.getValue());
        }
      }

      validatedPayload.add(validatedRow);
    }

    return validatedPayload;
  }

  private List<Map<String, Object>> formatReportPayload(
      final List<Map<String, Object>> reportPayload,
      final ApiReportData reportData) {

    final List<Map<String, Object>> formattedReportPayload = Lists.newArrayList();

    for (final Map<String, Object> row : reportPayload) {

      final Map<String, Object> formattedRow = Maps.newLinkedHashMap(row);
      for (final Map.Entry<String, Object> field : row.entrySet()) {

        if (field.getValue() == null) {
          formattedRow.put(field.getKey(), "");

        } else if (field.getValue() instanceof Date) {
          formattedRow.put(field.getKey(), new SimpleDateFormat(DATE_API_DATETIME_FORMAT).format(field.getValue()));

        } else if (field.getValue() instanceof Calendar) {
          formattedRow.put(field.getKey(), new SimpleDateFormat(DATE_API_DATETIME_FORMAT).format(((Calendar) field.getValue()).getTime()));

        } else if (field.getValue() instanceof BigDecimal) {
          formattedRow.put(field.getKey(), new DecimalFormat(DATE_API_DOUBLE_FORMAT).format(field.getValue()));

        } else if (reportData.getMapCustomFieldSchemaNameToActualName().containsKey(field.getKey())) {
          final String actualCustomFieldName = reportData.getMapCustomFieldSchemaNameToActualName().get(field.getKey());
          formattedRow.remove(field.getKey());
          formattedRow.put(actualCustomFieldName.replace(" ", "_").toLowerCase(), field.getValue());
        }
      }
      formattedReportPayload.add(formattedRow);
    }

    return formattedReportPayload;
  }

  private void checkForInternalUserOrCompanyUser(final Long companyIdInRequest, final Long currentUserCompanyId) {
    boolean isInternal = Constants.WM_COMPANY_ID.equals(currentUserCompanyId);
    boolean isCompanyUser = currentUserCompanyId.equals(companyIdInRequest);

    if (!isInternal && !isCompanyUser) {
      logger.warn("User is neither internal nor a user of the requested resource's company");
      throw new AccessDeniedException("You cannot view the requested resource.");
    }
  }

  private Optional<ApiReportMetadata> getReportMetadata(final Long reportId, final Long companyId) {

    // Get all reports by companyId
    final CollectionsResponse collectionsResponse = collectionClient.getCollectionsInNamespace(
        String.format(DATA_API_REPORTS_NAMESPACE_BY_CID, companyId),
        webRequestContextProvider.getRequestContext())
        .onErrorReturn(new Func1<Throwable, CollectionsResponse>() {
          @Override
          public CollectionsResponse call(Throwable throwable) {
            logger.error("Error fetching reports metadata from Collection Service", throwable);
            throw new InternalServerErrorException(UNEXPECTED_ERROR_RESPONSE);
          }
        })
        .toBlocking()
        .singleOrDefault(CollectionsResponse.getDefaultInstance());

    final List<Collection> reports = collectionsResponse.getCollectionsList();

    // Get report metadata (uuid and name) by reportId
    Optional<Collection> reportMetaDataOptional = Optional.absent();
    for (final Collection report : reports) {
      for (final Common.MetadataItem reportMetaDataItem : report.getMetadata().getDataList()) {
        if ("id".equals(reportMetaDataItem.getKey()) && reportId.toString().equals(reportMetaDataItem.getValue())) {
          reportMetaDataOptional = Optional.of(report);
          break;
        }
      }
    }

    if (!reportMetaDataOptional.isPresent()) {
      return Optional.absent();
    }

    final ApiReportMetadata.Builder builder = new ApiReportMetadata.Builder()
        .withId(reportId)
        .withName(reportMetaDataOptional.get().getDisplayName());

    // Get additional report metadata (source, schema, and any joins)
    final CollectionResponse additionalReportMetadata = collectionClient.getItemsInCollection(
        reportMetaDataOptional.get().getUuid(),
        webRequestContextProvider.getRequestContext())
        .onErrorReturn(new Func1<Throwable, CollectionResponse>() {
          @Override
          public CollectionResponse call(Throwable throwable) {
            logger.error("Error fetching report metadata from Collection Service", throwable);
            throw new InternalServerErrorException(UNEXPECTED_ERROR_RESPONSE);
          }
        })
        .toBlocking()
        .singleOrDefault(CollectionResponse.getDefaultInstance());

    if (!additionalReportMetadata.hasCollection()) {
      logger.error("Report Collection record exists, but no metadata has been added.");
      throw new InternalServerErrorException(UNEXPECTED_ERROR_RESPONSE);
    }


    // Merge report data into ApiReportMetadata
    for (final Common.MetadataItem data : reportMetaDataOptional.get().getMetadata().getDataList()) {
      if ("description".equals(data.getKey())) {
        builder.withDescription(data.getValue());
      }
    }

    // Merge other report data into ApiReportMetadata
    final ObjectMapper mapper = new ObjectMapper();
    for (final Common.Item item : additionalReportMetadata.getCollection().getItemsList()) {

      final Map<String, String> itemDataMap = Maps.newLinkedHashMap();
      for (final Common.MetadataItem metadataItem : item.getMetadata().getDataList()) {
        itemDataMap.put(metadataItem.getKey(), metadataItem.getValue());
      }

      switch (item.getObjectType()) {
        case "source":

          final Class[] callableParameterTypes = getCallableParameterTypes(itemDataMap.get("callableParameterTypes"), mapper);
          final Class[] callableOutputConverterParameterTypes = getCallableParameterTypes(itemDataMap.get("callableOutputConverterParameterTypes"), mapper);
          final List<String> filterFieldWhiteList = parseJsonList(itemDataMap.get("filterFieldWhiteList"), mapper);
          final List<String> filterFieldRequired = parseJsonList(itemDataMap.get("filterFieldRequired"), mapper);

          final ApiReportMetadata.ApiReportSource apiReportSource = new ApiReportMetadata.ApiReportSource.Builder()
              .withService(itemDataMap.get("service"))
              .withCallable(itemDataMap.get("callable"))
              .withCallableParameterTypes(callableParameterTypes)
              .withCallableInputConverter(itemDataMap.get("callableInputConverter"))
              .withCallableOutputConverter(itemDataMap.get("callableOutputConverter"))
              .withCallableOutputConverterParameterTypes(callableOutputConverterParameterTypes)
              .withFilterFieldWhiteList(filterFieldWhiteList)
              .withFilterFieldRequired(filterFieldRequired)
              .build();
          builder.withSource(apiReportSource).build();

          break;

        case "schema":
          final List<ApiReportMetadata.ApiReportField> schema = Lists.newArrayList();
          for (Map.Entry<String, String> metadataItem : itemDataMap.entrySet()) {
            try {
              schema.add(new ApiReportMetadata.ApiReportField(
                  metadataItem.getKey(),
                  ApiReportDatatype.valueOf(metadataItem.getValue().toUpperCase())));

            } catch (IllegalArgumentException e) {
              logger.error(String.format("Could not parse datatype with value %s", metadataItem.getValue()), e);
              throw new InternalServerErrorException(UNEXPECTED_ERROR_RESPONSE);
            }
          }
          builder.withSchema(schema);
          break;

        case "join":
          final String filterIdsJson = itemDataMap.get("filter_ids");
          List<Long> filterIds;

          try {
            filterIds = mapper.readValue(filterIdsJson, mapper.getTypeFactory().constructCollectionType(List.class, Long.class));
          } catch (final IOException e) {
            logger.error(String.format("Could not parse report metadata item: %s", filterIdsJson), e);
            throw new InternalServerErrorException(UNEXPECTED_ERROR_RESPONSE);
          }

          builder.addJoin(new ApiReportMetadata.ApiReportJoin(
              itemDataMap.get("name"),
              itemDataMap.get("foreignKey"),
              itemDataMap.get("service"),
              itemDataMap.get("callable"),
              itemDataMap.get("callableMerger"),
              filterIds));

          break;
      }
    }

    final ApiReportMetadata apiReportMetadata = builder.build();

    final Set<String> invalidFilterFieldsInWhiteList = Sets.difference(
        Sets.newHashSet(apiReportMetadata.getSource().getFilterFieldWhiteList()),
        Sets.newHashSet(apiReportMetadata.getFieldNames()));

    if (!invalidFilterFieldsInWhiteList.isEmpty()) {
      logger.error(String.format("Whitelisted filter fields - %s - do not match any fields in the report's schema: " +
          "%s",
          invalidFilterFieldsInWhiteList,
          apiReportMetadata.getFieldNames()));
      throw new InternalServerErrorException(UNEXPECTED_ERROR_RESPONSE);
    }

    return Optional.of(builder.build());
  }

  private List<String> parseJsonList(final String json, final ObjectMapper mapper) {

    List<String> stringList;

    try {
      stringList = mapper.readValue(
          json,
          mapper.getTypeFactory().constructCollectionType(List.class, String.class));

    } catch (final IOException e) {
      logger.error(String.format("Could not parse report metadata item: %s", json), e);
      throw new InternalServerErrorException(UNEXPECTED_ERROR_RESPONSE);
    }

    return stringList;
  }

  private Class[] getCallableParameterTypes(final String json, final ObjectMapper mapper) {

    final List<String> classPaths = parseJsonList(json, mapper);
    List<Class> callableParameterTypes;

    try {
      callableParameterTypes = Lists.newArrayList();
      for (final String classPath : classPaths) {
        callableParameterTypes.add(Class.forName(classPath));
      }

    } catch (final ClassNotFoundException e) {
      logger.error(String.format("Could not parse report metadata item: %s", json), e);
      throw new InternalServerErrorException(UNEXPECTED_ERROR_RESPONSE);
    }

    return callableParameterTypes.toArray(new Class[0]);
  }

  private Object invokeServiceCallable(
      final Map<String, ServiceResource> serviceMap,
      final String serviceName,
      final String callableName,
      final Class[] callableParameterTypes,
      final Object[] callableParameters) {

    try {
      if (!serviceMap.containsKey(serviceName)) {
        logger.error(String.format("%s service does not exist in Service Map.", serviceName));
        throw new InternalServerErrorException(UNEXPECTED_ERROR_RESPONSE);
      }

      final ServiceResource serviceResource = serviceMap.get(serviceName);

      if (!serviceResource.getWhitelistedCallables().contains(callableName)) {
        logger.error(String.format("%s is not a whitelisted callable for service %s", callableName, serviceName));
        throw new InternalServerErrorException(UNEXPECTED_ERROR_RESPONSE);
      }

      final Object service = serviceResource.getServiceRef();
      final Method callable = service.getClass().getMethod(callableName, callableParameterTypes);
      return callable.invoke(service, callableParameters);

    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      logger.error(String.format("There was an unexpected error calling method %s on %s", callableName, serviceName), e);
      throw new InternalServerErrorException(UNEXPECTED_ERROR_RESPONSE);
    }
  }

  public ApiReportData mapToApiReportData(final Map<String, Object> fromMap) {
    return new ApiReportData(
        (List<Map<String, Object>>) fromMap.get("results"),
        (Integer) fromMap.get("totalRows"));
  }

  public ApiReportData assessmentUserPaginationToApiReportData(final AssessmentUserPagination assessmentUserPagination) {
    final ImmutableList.Builder<Map<String, Object>> resultsBuilder = ImmutableList.builder();
    for (final AssessmentUser assessmentUser : assessmentUserPagination.getResults()) {

      final Map<String, Object> row = Maps.newHashMap();
      row.put("user_id", assessmentUser.getUserNumber());
      row.put("score", assessmentUser.getScore());
      row.put("pass", assessmentUser.getPassedFlag());
      row.put("completed_date", assessmentUser.getCompletedOn());
      row.put("test_id", assessmentUser.getAssessmentId());

      resultsBuilder.add(row);
    }
    final List<Map<String, Object>> results = resultsBuilder.build();

    return new ApiReportData(results, assessmentUserPagination.getRowCount());
  }

  public ApiReportData assessmentPaginationToApiReportData(final AssessmentPagination assessmentPagination) {
    final ImmutableList.Builder<Map<String, Object>> resultsBuilder = ImmutableList.builder();
    for (final AbstractAssessment assessment : assessmentPagination.getResults()) {

      final Map<String, Object> row = Maps.newHashMap();
      row.put("test_id", assessment.getId());
      row.put("test_name", assessment.getName());

      resultsBuilder.add(row);
    }
    final List<Map<String, Object>> results = resultsBuilder.build();

    return new ApiReportData(results, assessmentPagination.getRowCount());
  }

  public ApiReportData mergeWorkCustomFieldsIntoApiReportData(
      final String joinName,
      final ApiReportData reportData,
      final Map<String, Object> joinData,
      final String foreignKey) {

    final List<Map<String, Object>> customFieldsRaw = (List<Map<String, Object>>) joinData.get("customFields");
    final Map<Long, List<Map<String, Object>>> workIdToCustomFields = (Map<Long, List<Map<String, Object>>>) joinData.get("workIdToCustomFields");

    final List<ApiReportData.CustomField> customFields = Lists.newArrayList();
    final List<Map<String, Object>> reportDataWithCustomFields = Lists.newArrayList();

    // Package custom field information (names, ids)
    for (final Map<String, Object> customField : customFieldsRaw) {
      final Long customFieldId = (Long) customField.get("customFieldId");
      final String actualCustomFieldName = (String) customField.get("customFieldName");
      final String schemaCustomFieldName = String.format("%s__%d", joinName, customFieldId);

      customFields.add(new ApiReportData.CustomField(customFieldId, schemaCustomFieldName, actualCustomFieldName));
    }

    // Add custom field values to report rows, if they exist
    for (final Map<String, Object> reportRow : reportData.getResults()) {

      final Map<String, Object> reportRowWithCustomFields = Maps.newLinkedHashMap(reportRow);
      final Long workId = (Long) reportRow.get(foreignKey);

      if (!workIdToCustomFields.containsKey(workId)) {

        // Add default blank custom field values
        for (final ApiReportData.CustomField customField : customFields) {
          reportRowWithCustomFields.put(customField.getSchemaName(), "");
        }

      } else {

        // Add actual custom field value
        for (final Map<String, Object> customFieldValue : workIdToCustomFields.get(workId)) {

          final Long customFieldId = (Long) customFieldValue.get("customFieldId");
          ApiReportData.CustomField customFieldForHydration = null;

          for (final ApiReportData.CustomField customField : customFields) {
            if (customFieldId.equals(customField.getId())) {
              customFieldForHydration = customField;
              break;
            }
          }

          if (customFieldForHydration != null) {
            reportRowWithCustomFields.put(
                customFieldForHydration.getSchemaName(),
                customFieldValue.get("customFieldValue"));
          }
        }

      }

      reportDataWithCustomFields.add(reportRowWithCustomFields);
    }

    return new ApiReportData(
        reportDataWithCustomFields,
        reportData.getTotalCount(),
        customFields);
  }

  public Object[] reportRequestToParameterListForFindLatestAssessmentUserAttempts(final Map<String, Object> params) {
    final AssessmentUserPagination pagination = new AssessmentUserPagination();
    pagination.setStartRow((Integer)params.get("offset"));
    pagination.setResultsLimit((Integer) params.get("limit"));
    pagination.addFilter(AssessmentUserPagination.FILTER_KEYS.ATTEMPT_STATUS, AssessmentUserPagination.GRADED);

    if (params.containsKey("user_id")) {
      pagination.addFilter(AssessmentUserPagination.FILTER_KEYS.USER_NUMBER, params.get("user_id"));
    }

    return Lists.newArrayList(params.get("test_id"), pagination).toArray();
  }

  public Object[] reportRequestToParameterListForGetAssignmentDataOne(final Map<String, Object> params) {
    return Lists.newArrayList((Object)params).toArray();
  }

  public Object[] reportRequestToParameterListForFindAssessmentsByCompany(final Map<String, Object> params) {
    final AssessmentPagination pagination = new AssessmentPagination();
    pagination.setStartRow((Integer)params.get("offset"));
    pagination.setResultsLimit((Integer) params.get("limit"));

    pagination.addFilter(AssessmentPagination.FILTER_KEYS.TYPE, AbstractAssessment.GRADED_ASSESSMENT_TYPE);
    pagination.addFilter(AssessmentPagination.FILTER_KEYS.NOT_REMOVED, true);

    return Lists.newArrayList(params.get("companyId"), pagination).toArray();
  }

  @Deprecated
  @ApiOperation(value = "Get metadata for all available reports ", tags = {"data", "reports"})
  @RequestMapping("/v3/data/reports")
  @ResponseBody
  public ApiV3Response<ApiReportDTO> getReports() {
    checkForInternalUser();
    final ReportsResponse reportsResponse = dataAccessClient.getAllReports(webRequestContextProvider
        .getRequestContext()).toBlocking().single();
    return convertResponses(reportsResponse);
  }

  @Deprecated
  @ApiOperation(value = "Get report data", tags = {"data", "reports"})
  @RequestMapping("/v3/data/report/{reportId}")
  @ResponseBody
  public ApiV3Response<ApiReportDTO> getReport(final @PathVariable("reportId") Integer reportId) {
    checkForInternalUser();
    final ReportsResponse reportsResponse = dataAccessClient.getReports(ImmutableList.of(reportId),
        webRequestContextProvider.getRequestContext()).toBlocking().single();
    return convertResponses(reportsResponse);
  }

  /**
   * Since API users don't inherit the WM_INTERNAL role, we check for a company id == WM company id.
   */
  @Deprecated
  private void checkForInternalUser() {
    if (!Constants.WM_COMPANY_ID.equals(getCurrentUser().getCompanyId())) {
      logger.warn("Non internal user attempting to hit internal-only endpoint");
      throw new AccessDeniedException("You cannot view the requested resource.");
    }
  }

  @Deprecated
  private ApiV3Response<ApiReportDTO> convertResponses(final ReportsResponse reportsResponse) {
    ImmutableList.Builder<ApiReportDTO> reports = ImmutableList.builder();
    ImmutableList.Builder<ApiV3Error> errors = ImmutableList.builder();

    for (final ReportResponse reportResponse : reportsResponse.getReportsList()) {
      if (reportResponse.getStatus().getFailure()) {
        errors.add(new ApiBaseError("error", "There was an error fetching report with id: " + reportResponse.getId()));

      } else {
        reports.add(convertResponse(reportResponse));
      }
    }

    ApiV3Response<ApiReportDTO> response = ApiV3ResponseImpl.valueWithResults(reports.build());
    response.getResult().setErrors(errors.build());

    if (response.getResult().getPayload().isEmpty()) {
      response.getMeta().setStatusCode(400);
    }

    return response;
  }

  @Deprecated
  private ApiReportDTO convertResponse(final ReportResponse reportResponse) {
    ApiReportDTO.Builder builder = new ApiReportDTO.Builder()
        .withTitle(reportResponse.getTitle())
        .withDescription(reportResponse.getDescription())
        .withCreatedAt(reportResponse.getCreatedAt())
        .withUpdatedAt(reportResponse.getUpdatedAt())
        .withUrl(reportResponse.getUrl())
        .withId(reportResponse.getId());

    if (reportResponse.getDataCount() > 0) {
      ImmutableList.Builder<List<String>> rows = ImmutableList.builder();
      for (Messages.Data row : reportResponse.getDataList()) {
        rows.add(row.getDataEntryList());
      }
      builder.withData(rows.build());
    }

    return builder.build();
  }
}
