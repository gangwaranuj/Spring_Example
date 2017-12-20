package com.workmarket.api.v1.model.validator;

import com.workmarket.web.models.MessageBundle;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by evanercolano on 8/1/17.
 */
public class ApiCheckInOutValidatorTest {

  public static final String invalidWorkNumber = "ds1saz";
  public static final String invalidTrackingNumber = "2ds1s*!@#de1az2";
  public static final String invalidDateTimeFormat = "2015-6-11 3:59:59";

  public static final String validWorkNumber = "8041258330";
  public static final String validTrackingNumber = "88085";
  public static final String validDateTimeFormat = "2017-07-28 09:48:29";

  private final static String DATE_API_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

  @Test
  public void apiCheckInOutValidator_success() {
    MessageBundle errors = new MessageBundle();

    final ApiCheckInOutValidator apiCheckInOutValidator = new ApiCheckInOutValidator();
    apiCheckInOutValidator.validate(validWorkNumber, validTrackingNumber, validDateTimeFormat, errors);
    assertEquals(0, errors.getErrors().size());

    apiCheckInOutValidator.validate(validWorkNumber, validTrackingNumber, null, errors);
    assertEquals(0, errors.getErrors().size());

    apiCheckInOutValidator.validate(validWorkNumber, null, null, errors);
    assertEquals(0, errors.getErrors().size());
  }

  @Test
  public void apiCheckInOutValidatorInvalidWorkNumber_failure() {
    MessageBundle errors = new MessageBundle();

    final ApiCheckInOutValidator apiCheckInOutValidator = new ApiCheckInOutValidator();
    apiCheckInOutValidator.validate(invalidWorkNumber, validTrackingNumber, validDateTimeFormat, errors);
    assertEquals(1, errors.getErrors().size());
    assertTrue(errors.getErrors().get(0).equals("Work Number not numeric"));
  }

  @Test
  public void apiCheckInOutValidatorInvalidTrackingNumber_failure() {
    MessageBundle errors = new MessageBundle();

    final ApiCheckInOutValidator apiCheckInOutValidator = new ApiCheckInOutValidator();
    apiCheckInOutValidator.validate(validWorkNumber, invalidTrackingNumber, validDateTimeFormat, errors);
    assertEquals(1, errors.getErrors().size());
    assertTrue(errors.getErrors().get(0).equals("Tracking Id not numeric"));
  }

  @Test
  public void apiCheckInOutValidatorInvalidDatetime_failure() {
    MessageBundle errors = new MessageBundle();

    final ApiCheckInOutValidator apiCheckInOutValidator = new ApiCheckInOutValidator();
    apiCheckInOutValidator.validate(validWorkNumber, validTrackingNumber, invalidDateTimeFormat, errors);
    assertEquals(1, errors.getErrors().size());
    assertTrue(errors.getErrors().get(0).equals("Datetime does not obey format " + DATE_API_DATETIME_FORMAT));
  }

  @Test
  public void apiCheckInOutValidatorMoreThanOneError_failure() {
    MessageBundle errors = new MessageBundle();

    final ApiCheckInOutValidator apiCheckInOutValidator = new ApiCheckInOutValidator();
    apiCheckInOutValidator.validate(validWorkNumber, invalidTrackingNumber, invalidDateTimeFormat, errors);
    assertEquals(2, errors.getErrors().size());
    assertTrue(errors.getErrors().get(0).equals("Tracking Id not numeric"));
    assertTrue(errors.getErrors().get(1).equals("Datetime does not obey format " + DATE_API_DATETIME_FORMAT));
  }
}
