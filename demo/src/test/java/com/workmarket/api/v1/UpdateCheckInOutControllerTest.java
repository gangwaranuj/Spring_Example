package com.workmarket.api.v1;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.v1.model.validator.ApiCheckInOutValidator;
import com.workmarket.common.jwt.Either;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkResourceTimeTracking;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.thrift.work.TimeTrackingRequest;
import com.workmarket.thrift.work.TimeTrackingResponse;
import com.workmarket.web.models.MessageBundle;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class UpdateCheckInOutControllerTest extends BaseApiControllerTest {
  private MockHttpServletResponse response;

  @Mock private TWorkFacadeService tWorkFacadeService;
  @Mock private WorkService workService;
  @Mock private ApiResponseBuilder apiResponseBuilder;
  @Mock private ApiBaseController apiBaseController;
  @Spy private ApiCheckInOutValidator apiCheckInOutValidator;
  @InjectMocks private UpdateCheckInOutController controller = spy(new UpdateCheckInOutController());

  Work work = mock(Work.class);
  Calendar workDueDate = mock(Calendar.class);
  Calendar checkInDateTime = mock(Calendar.class);
  Calendar checkOutDateTime = mock(Calendar.class);
  Calendar createdOn = mock(Calendar.class);
  TimeZone timeZone = mock(TimeZone.class);
  WorkResourceTimeTracking workResourceTimeTracking = mock(WorkResourceTimeTracking.class);
  TimeTrackingResponse checkInOutResponse = mock(TimeTrackingResponse.class);
  Note checkOutNote = mock(Note.class);
  BigDecimal latitude, longitude;

  private static final Long
      WORK_ID = 3L,
      COMPANY1_ID = 1L,
      TIME_TRACKING_ID_PAIR = 88102L;

  private static final String POSTAL_CODE2 = "10015";
  public static final String validDateTimeFormat = "2017-07-28 09:48:29";



  @Before
  public void setup() throws Exception {
    super.setup(controller);
    response = new MockHttpServletResponse();

    when(work.getId()).thenReturn(WORK_ID);
    when(work.getBuyer()).thenReturn(user);
    when(work.isSent()).thenReturn(true);
    when(work.getDueDate()).thenReturn(workDueDate);

    when(checkInDateTime.getTimeInMillis()).thenReturn(1L);
    when(checkInOutResponse.getTimeTracking()).thenReturn(workResourceTimeTracking);
    when(workResourceTimeTracking.getId()).thenReturn(TIME_TRACKING_ID_PAIR);
    when(workResourceTimeTracking.getCheckedInOn()).thenReturn(checkInDateTime);
    when(work.getCreatedOn()).thenReturn(createdOn);
    when(createdOn.getTimeInMillis()).thenReturn(1L);
  }

  @Test
  public void CheckIn_success() {
    MessageBundle messageBundle = mock(MessageBundle.class);

    doReturn(Either.right(work)).when(controller).getWork(anyString());
    doReturn(Either.right(checkInDateTime)).when(controller).getCalendar(eq(work), anyString());

    when(tWorkFacadeService.checkInActiveResource((TimeTrackingRequest) any())).thenReturn(checkInOutResponse);
    when(checkInOutResponse.isSuccessful()).thenReturn(true);
    when(checkInOutResponse.getTimeTracking()).thenReturn(workResourceTimeTracking);

    ApiV1Response apiResponse = controller.updateCheckInAction("999", null, null);
    // ApiV1Response apiResponse = controller.updateCheckInAction(eq("1111"), isNull(String.class), isNull(String.class));

    assertEquals(HttpStatus.OK.value(), apiResponse.getMeta().getStatusCode());
    // check more fields to make sure correct data came through
  }

  @Test
  public void checkInValidation_failure() {
    List<String> errors = new ArrayList<>();
    errors.add("Work Number not numeric");
    ApiV1Response<ApiV1ResponseStatus> apiV1Response = ApiV1Response.of(false, errors, HttpStatus.BAD_REQUEST.value());
    when(apiResponseBuilder.createErrorResponse(anyString())).thenReturn(apiV1Response);

    ApiV1Response apiResponse = controller.updateCheckInAction(anyString(), isNull(String.class), isNull(String.class));
    assertEquals(HttpStatus.BAD_REQUEST.value(), apiResponse.getMeta().getStatusCode());
    assertEquals("Work Number not numeric", apiResponse.getMeta().getErrors().get(0));
  }

  @Test
  public void checkInInvalidWorkNumber_failure() {
    List<String> errors = new ArrayList<>();
    errors.add("Work not existing or has no timezone");
    ApiV1Response<ApiV1ResponseStatus> apiV1Response = ApiV1Response.of(false, errors, HttpStatus.BAD_REQUEST.value());
    when(apiResponseBuilder.createErrorResponse(anyString())).thenReturn(apiV1Response);
    doReturn(Either.left(apiV1Response)).when(controller).getWork(anyString());

    ApiV1Response apiResponse = controller.updateCheckInAction("999", null, null);
    assertEquals(HttpStatus.BAD_REQUEST.value(), apiResponse.getMeta().getStatusCode());
    assertEquals("Work not existing or has no timezone", apiResponse.getMeta().getErrors().get(0));
  }

  @Test
  public void checkInInvalidCalendar_failure() {
    List<String> errors = new ArrayList<>();
    errors.add("Datetime parsing issue");
    ApiV1Response<ApiV1ResponseStatus> apiV1Response = ApiV1Response.of(false, errors, HttpStatus.BAD_REQUEST.value());
    when(apiResponseBuilder.createErrorResponse(anyString())).thenReturn(apiV1Response);
    doReturn(Either.right(work)).when(controller).getWork(anyString());
    doReturn(Either.left(apiV1Response)).when(controller).getCalendar((Work) any(), anyString());

    ApiV1Response apiResponse = controller.updateCheckInAction("999", null, null);
    assertEquals(HttpStatus.BAD_REQUEST.value(), apiResponse.getMeta().getStatusCode());
    assertEquals("Datetime parsing issue", apiResponse.getMeta().getErrors().get(0));
  }

  @Test
  public void checkInResponse_failure() {
    List<String> errors = new ArrayList<>();
    errors.add("Check in unsuccessful.");
    ApiV1Response<ApiV1ResponseStatus> apiV1Response = ApiV1Response.of(false, errors, HttpStatus.BAD_REQUEST.value());
    when(apiResponseBuilder.createErrorResponse(anyString())).thenReturn(apiV1Response);
    doReturn(Either.right(work)).when(controller).getWork(anyString());
    doReturn(Either.right(checkInDateTime)).when(controller).getCalendar(eq(work), anyString());
    when(tWorkFacadeService.checkInActiveResource((TimeTrackingRequest) any())).thenReturn(checkInOutResponse);
    when(checkInOutResponse.isSuccessful()).thenReturn(false);
    when(checkInOutResponse.getTimeTracking()).thenReturn(null);

    ApiV1Response apiResponse = controller.updateCheckInAction("999", null, null);
    assertEquals(HttpStatus.BAD_REQUEST.value(), apiResponse.getMeta().getStatusCode());
    assertEquals("Check in unsuccessful.", apiResponse.getMeta().getErrors().get(0));
  }

  @Test
  public void checkOut_success() {
    MessageBundle messageBundle = mock(MessageBundle.class);

    doReturn(Either.right(work)).when(controller).getWork(anyString());
    doReturn(Either.right(checkInDateTime)).when(controller).getCalendar(eq(work), anyString());

    when(tWorkFacadeService.checkOutActiveResource((TimeTrackingRequest) any())).thenReturn(checkInOutResponse);
    when(checkInOutResponse.isSuccessful()).thenReturn(true);
    when(checkInOutResponse.getTimeTracking()).thenReturn(workResourceTimeTracking);

    ApiV1Response apiResponse = controller.updateCheckOutAction("999", null, null, "hello");

    assertEquals(HttpStatus.OK.value(), apiResponse.getMeta().getStatusCode());
    // check more fields to make sure correct data came through

  }

  @Test
  public void checkOutValidation_failure() {
    List<String> errors = new ArrayList<>();
    errors.add("Work Number not numeric");
    ApiV1Response<ApiV1ResponseStatus> apiV1Response = ApiV1Response.of(false, errors, HttpStatus.BAD_REQUEST.value());
    when(apiResponseBuilder.createErrorResponse(anyString())).thenReturn(apiV1Response);

    ApiV1Response apiResponse = controller.updateCheckOutAction(isNull(String.class), isNull(String.class),
        isNull(String.class), isNull(String.class));
    assertEquals(HttpStatus.BAD_REQUEST.value(), apiResponse.getMeta().getStatusCode());
    assertEquals("Work Number not numeric", apiResponse.getMeta().getErrors().get(0));
  }

  @Test
  public void checkOutInvalidWorkNumber_failure() {
    List<String> errors = new ArrayList<>();
    errors.add("Work not existing or has no timezone");
    ApiV1Response<ApiV1ResponseStatus> apiV1Response = ApiV1Response.of(false, errors, HttpStatus.BAD_REQUEST.value());
    when(apiResponseBuilder.createErrorResponse(anyString())).thenReturn(apiV1Response);
    doReturn(Either.left(apiV1Response)).when(controller).getWork(anyString());

    ApiV1Response apiResponse = controller.updateCheckOutAction("999", null, null, "hello");
    assertEquals(HttpStatus.BAD_REQUEST.value(), apiResponse.getMeta().getStatusCode());
    assertEquals("Work not existing or has no timezone", apiResponse.getMeta().getErrors().get(0));
  }

  @Test
  public void checkOutInvalidCalendar_failure() {
    List<String> errors = new ArrayList<>();
    errors.add("Datetime parsing issue");
    ApiV1Response<ApiV1ResponseStatus> apiV1Response = ApiV1Response.of(false, errors, HttpStatus.BAD_REQUEST.value());
    when(apiResponseBuilder.createErrorResponse(anyString())).thenReturn(apiV1Response);
    doReturn(Either.right(work)).when(controller).getWork(anyString());
    doReturn(Either.left(apiV1Response)).when(controller).getCalendar((Work) any(), anyString());

    ApiV1Response apiResponse = controller.updateCheckOutAction("999", null, null, "hello");
    assertEquals(HttpStatus.BAD_REQUEST.value(), apiResponse.getMeta().getStatusCode());
    assertEquals("Datetime parsing issue", apiResponse.getMeta().getErrors().get(0));
  }

  @Test
  public void checkOutCheckInResponse_failure() {
    List<String> errors = new ArrayList<>();
    errors.add("Check in unsuccessful.");
    ApiV1Response<ApiV1ResponseStatus> apiV1Response = ApiV1Response.of(false, errors, HttpStatus.BAD_REQUEST.value());
    when(apiResponseBuilder.createErrorResponse(anyString())).thenReturn(apiV1Response);
    doReturn(Either.right(work)).when(controller).getWork(anyString());
    doReturn(Either.right(checkInDateTime)).when(controller).getCalendar(eq(work), anyString());
    when(tWorkFacadeService.checkOutActiveResource((TimeTrackingRequest) any())).thenReturn(checkInOutResponse);
    when(checkInOutResponse.isSuccessful()).thenReturn(false);
    when(checkInOutResponse.getTimeTracking()).thenReturn(null);

    ApiV1Response apiResponse = controller.updateCheckOutAction("999", null, null, "hello");
    assertEquals(HttpStatus.BAD_REQUEST.value(), apiResponse.getMeta().getStatusCode());
    assertEquals("Check in unsuccessful.", apiResponse.getMeta().getErrors().get(0));
  }

  @Test
  public void getCheckoutTimeIfLoggedOut_alreadyLoggedOut() {
    when(checkInOutResponse.getTimeTracking()).thenReturn(workResourceTimeTracking);
    when(workResourceTimeTracking.isCheckedOut()).thenReturn(true);
    when(workResourceTimeTracking.getCheckedOutOn()).thenReturn(checkOutDateTime);
    when(checkOutDateTime.getTimeInMillis()).thenReturn(1L);
    Long checkOutTime = controller.getCheckoutTimeIfLoggedOut(checkInOutResponse);
    assertTrue(checkOutTime.equals(1L));
  }

  @Test
  public void getCheckoutTimeIfLoggedOut_notLoggedOut() {
    when(checkInOutResponse.getTimeTracking()).thenReturn(workResourceTimeTracking);
    when(workResourceTimeTracking.isCheckedOut()).thenReturn(false);

    Long checkOutTime = controller.getCheckoutTimeIfLoggedOut(checkInOutResponse);
    assertTrue(checkOutTime == null);
  }

  @Test
  public void getCheckoutNoteIfExists_exists() {
    when(checkInOutResponse.getTimeTracking()).thenReturn(workResourceTimeTracking);
    when(workResourceTimeTracking.isCheckedOut()).thenReturn(true);
    when(workResourceTimeTracking.getNote()).thenReturn(checkOutNote);
    when(checkOutNote.getContent()).thenReturn("Great assignment!");

    String note = controller.getCheckoutNoteIfExists(checkInOutResponse);
    assertTrue(note.equals("Great assignment!"));
  }

  @Test
  public void getCheckoutNoteIfExists_doesNotExist() {
    when(checkInOutResponse.getTimeTracking()).thenReturn(workResourceTimeTracking);
    when(workResourceTimeTracking.isCheckedOut()).thenReturn(false);

    String note = controller.getCheckoutNoteIfExists(checkInOutResponse);
    assertTrue(note == null);
  }

  @Test
  public void getCalendar_success() {
    Calendar instance = Calendar.getInstance();
    when(controller.getCheckInOutDateTime((Work) any(), anyString())).thenReturn(instance);
    Either<ApiV1Response<ApiV1ResponseStatus>, Calendar> calSuccess = controller.getCalendar(work, validDateTimeFormat);
    assertTrue(calSuccess.isRight());
    assertTrue(calSuccess.get().equals(instance));
  }

  @Test
  public void getCalendar_failure() {
    List<String> errors = new ArrayList<>();
    errors.add("Datetime parsing issue");
    ApiV1Response<ApiV1ResponseStatus> apiV1Response = ApiV1Response.of(false, errors, HttpStatus.BAD_REQUEST.value());
    when(apiResponseBuilder.createErrorResponse(anyString())).thenReturn(apiV1Response);

    when(controller.getCheckInOutDateTime((Work) any(), anyString())).thenReturn(null);
    Either<ApiV1Response<ApiV1ResponseStatus>, Calendar> calFail = controller.getCalendar(work, validDateTimeFormat);
    assertTrue(calFail.isLeft());
    assertEquals(calFail.getLeft().getMeta().getStatusCode(), HttpStatus.BAD_REQUEST.value());
    assertEquals("Datetime parsing issue", calFail.getLeft().getMeta().getErrors().get(0));
  }

  @Test
  public void getWork_success() {
    doReturn(work).when(controller).getAndAuthorizeWorkByNumber(anyString(), (List<WorkContext>) any());
    when(work.getTimeZone()).thenReturn(timeZone);
    Either<ApiV1Response<ApiV1ResponseStatus>, AbstractWork> getWorkSuccess = controller.getWork("1234");
    assertTrue(getWorkSuccess.isRight());
  }

  @Test
  public void getWork_failure() {
    List<String> errors = new ArrayList<>();
    errors.add("Work not existing or has no timezone");
    ApiV1Response<ApiV1ResponseStatus> apiV1Response = ApiV1Response.of(false, errors, HttpStatus.BAD_REQUEST.value());
    when(apiResponseBuilder.createErrorResponse(anyString())).thenReturn(apiV1Response);

    doReturn(null).when(controller).getAndAuthorizeWorkByNumber(anyString(), (List<WorkContext>) any());
    Either<ApiV1Response<ApiV1ResponseStatus>, AbstractWork> getWorkFail = controller.getWork("1234");
    assertTrue(getWorkFail.isLeft());
    assertEquals(getWorkFail.getLeft().getMeta().getStatusCode(), HttpStatus.BAD_REQUEST.value());
    assertEquals("Work not existing or has no timezone", getWorkFail.getLeft().getMeta().getErrors().get(0));
  }

}
