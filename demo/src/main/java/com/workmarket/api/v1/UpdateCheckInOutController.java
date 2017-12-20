package com.workmarket.api.v1;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v1.model.ApiCheckInOutDTO;
import com.workmarket.api.v1.model.validator.ApiCheckInOutValidator;
import com.workmarket.common.jwt.Either;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.thrift.work.TimeTrackingRequest;
import com.workmarket.thrift.work.TimeTrackingResponse;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.exceptions.HttpException403;
import com.workmarket.web.models.MessageBundle;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Calendar;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Api(tags = "UpdateCheckIn")
@Controller("apiCheckInController")
@RequestMapping(value = {"/v1/employer/", "/api/v1/"})
public class UpdateCheckInOutController extends ApiBaseController {
  private static final Logger logger = LoggerFactory.getLogger(UpdateCheckInOutController.class);

  @Autowired
  private TWorkFacadeService tWorkFacadeService;
  @Autowired
  private WorkService workService;
  @Autowired
  private ApiResponseBuilder apiResponseBuilder;
  @Autowired
  private ApiCheckInOutValidator apiCheckInOutValidator;
  
  @ApiOperation(value = "Check-in action used for both new check-ins or updating an existing entry")
  @ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
  @RequestMapping(value = "/{id}/update_checkin", method = RequestMethod.POST)
  @ResponseBody
  public ApiV1Response updateCheckInAction(
      @PathVariable(value = "id") String workNumber,
      @RequestParam(value = "check_in_out_id", required = false) String trackingId,
      @RequestParam(value = "datetime", required = false) String datetime) {

    final MessageBundle validationErrors = new MessageBundle();
    apiCheckInOutValidator.validate(workNumber, trackingId, datetime, validationErrors);

    if (validationErrors.hasErrors()) {
      return apiResponseBuilder.createErrorResponse(validationErrors.getErrors().toString());
    }

    final Either<ApiV1Response<ApiV1ResponseStatus>, AbstractWork> errorOrWork = getWork(workNumber);

    if (errorOrWork.isLeft()) {
      return errorOrWork.getLeft();
    }
    final AbstractWork work = errorOrWork.get();

    final Either<ApiV1Response<ApiV1ResponseStatus>, Calendar> errorOrCalendar = getCalendar(work, datetime);

    if (errorOrCalendar.isLeft()) {
      return errorOrCalendar.getLeft();
    }
    final Calendar checkInDateTime = errorOrCalendar.get();

    final TimeTrackingRequest checkInRequest = new TimeTrackingRequest()
        .setWorkId(work.getId())
        .setTimeTrackingId(StringUtilities.parseLong(trackingId))
        .setDate(checkInDateTime);

    final TimeTrackingResponse checkInResponse = tWorkFacadeService.checkInActiveResource(checkInRequest);

    if (!checkInResponse.isSuccessful() || checkInResponse.getTimeTracking() == null) {
      final String errorMsg = checkInResponse.getMessage();
      logger.error(errorMsg);
      return apiResponseBuilder.createErrorResponse(errorMsg);
    }

    final ApiCheckInOutDTO apiCheckInOutDTO = new ApiCheckInOutDTO.Builder()
        .withId(checkInResponse.getTimeTracking().getId())
        .withCheckedInOn(checkInDateTime.getTimeInMillis())
        .withCheckoutOutOn(getCheckoutTimeIfLoggedOut(checkInResponse))
        .withCreatedOn(work.getCreatedOn().getTimeInMillis())
        .withModifiedOn(Calendar.getInstance().getTimeInMillis())
        .withNote(getCheckoutNoteIfExists(checkInResponse))
        .build();

    return new ApiV1Response<>(apiCheckInOutDTO);
  }

  @VisibleForTesting
  Long getCheckoutTimeIfLoggedOut(final TimeTrackingResponse checkInResponse) {
    if (checkInResponse.getTimeTracking().isCheckedOut()) {
      return checkInResponse.getTimeTracking().getCheckedOutOn().getTimeInMillis();
    }
    return null;
  }

  @ApiOperation(value = "Check-out action used for both new check-outs or updating an existing entry")
  @ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
  @RequestMapping(value = "/{id}/update_checkout", method = RequestMethod.POST)
  @ResponseBody
  public ApiV1Response updateCheckOutAction(
      @PathVariable("id") String workNumber,
      @RequestParam(value = "check_in_out_id", required = false) String trackingId,
      @RequestParam(value = "datetime", required = false) String datetime,
      @RequestParam(value = "note", required = false) String noteText) {

    final MessageBundle validationErrors = new MessageBundle();
    apiCheckInOutValidator.validate(workNumber, trackingId, datetime, validationErrors);

    if (validationErrors.hasErrors()) {
      return apiResponseBuilder.createErrorResponse(validationErrors.getErrors().toString());
    }

    final Either<ApiV1Response<ApiV1ResponseStatus>, AbstractWork> errorOrWork = getWork(workNumber);

    if (errorOrWork.isLeft()) {
      return errorOrWork.getLeft();
    }
    final AbstractWork work = errorOrWork.get();

    final Either<ApiV1Response<ApiV1ResponseStatus>, Calendar> errorOrCalendar = getCalendar(work, datetime);

    if (errorOrCalendar.isLeft()) {
      return errorOrCalendar.getLeft();
    }
    final Calendar checkOutDateTime = errorOrCalendar.get();

    final TimeTrackingRequest timeTrackingRequest = new TimeTrackingRequest()
        .setWorkId(work.getId())
        .setTimeTrackingId(StringUtilities.parseLong(trackingId))
        .setDate(checkOutDateTime);

    if (noteText != null) {
      timeTrackingRequest.setNoteOnCheckOut(noteText);
    }

    final TimeTrackingResponse checkoutResponse = tWorkFacadeService.checkOutActiveResource(timeTrackingRequest);

    if (!checkoutResponse.isSuccessful() || checkoutResponse.getTimeTracking() == null) {
      final String errorMsg = "Issue with timeTracking";
      logger.error(errorMsg);
      return apiResponseBuilder.createErrorResponse(errorMsg);
    }

    final ApiCheckInOutDTO apiCheckInOutDTO = new ApiCheckInOutDTO.Builder()
        .withId(checkoutResponse.getTimeTracking().getId())
        .withNote(getCheckoutNoteIfExists(checkoutResponse))
        .withCheckedInOn(checkoutResponse.getTimeTracking().getCheckedInOn().getTimeInMillis())
        .withCheckoutOutOn(checkOutDateTime.getTimeInMillis())
        .withCreatedOn(work.getCreatedOn().getTimeInMillis())
        .withModifiedOn(Calendar.getInstance().getTimeInMillis())
        .build();
    return new ApiV1Response<>(apiCheckInOutDTO);
  }

  @VisibleForTesting
  Either<ApiV1Response<ApiV1ResponseStatus>, Calendar> getCalendar(final AbstractWork work, final String datetime) {
    final Calendar checkInDateTime = getCheckInOutDateTime(work, datetime);

    if (checkInDateTime == null) {
      final String errorMsg = "Datetime parsing issue";
      logger.error(errorMsg);
      return Either.left(apiResponseBuilder.createErrorResponse(errorMsg));
    }
    return Either.right(checkInDateTime);
  }

  @VisibleForTesting
  Calendar getCheckInOutDateTime(AbstractWork work, String datetime) {
    if (isBlank(datetime)) {
      return Calendar.getInstance();
    }
    return DateUtilities.getCalendarFromDateTimeString(datetime, work.getTimeZone().getTimeZoneId());
  }

  @VisibleForTesting
  Either<ApiV1Response<ApiV1ResponseStatus>, AbstractWork> getWork(final String workNumber) {
    final AbstractWork work = getAndAuthorizeWorkByNumber(workNumber, ImmutableList.of(
        WorkContext.ACTIVE_RESOURCE,
        WorkContext.DISPATCHER,
        WorkContext.COMPANY_OWNED,
        WorkContext.OWNER));

    if (work == null || work.getTimeZone() == null) {
      final String errorMsg = "Work not existing or has no timezone";
      logger.error(errorMsg);
      return Either.left(apiResponseBuilder.createErrorResponse(errorMsg));
    }

    return Either.right(work);
  }

  @VisibleForTesting
  AbstractWork getAndAuthorizeWorkByNumber(String workNumber, List<WorkContext> validContexts) {
    final AbstractWork work = workService.findWorkByWorkNumber(workNumber);
    final List<WorkContext> context = workService.getWorkContext(work.getId(), getCurrentUser().getId());

    if (!CollectionUtils.containsAny(context, validContexts) && !getCurrentUser().hasAnyRoles("ROLE_INTERNAL")) {
      throw new HttpException403();
    }
    return work;
  }

  @VisibleForTesting
  String getCheckoutNoteIfExists(final TimeTrackingResponse checkInOutResponse) {
    if (checkInOutResponse.getTimeTracking().isCheckedOut()) {
      return checkInOutResponse.getTimeTracking().getNote().getContent();
    }
    return null;
  }
}
