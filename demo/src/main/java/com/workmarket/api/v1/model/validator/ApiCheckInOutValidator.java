package com.workmarket.api.v1.model.validator;

import com.workmarket.api.v1.model.ApiCheckInOutDTO;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.FastFundsValidator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;


@Component("apiCheckInOutDTOValidator")
public class ApiCheckInOutValidator {
  private static final Log logger = LogFactory.getLog(FastFundsValidator.class);
  private final static String DATE_API_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
  private final static Pattern DATETIME_REGEX_VALIDATOR = Pattern.compile(
      "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");

  public boolean supports(Class<?> clazz) {
    return ApiCheckInOutDTO.class.isAssignableFrom(clazz);
  }

  public void validate(String workNumber, String check_in_out_id, String datetime, MessageBundle errors) {
    validateWorkNumber(workNumber, errors);

    validateTrackingId(check_in_out_id, errors);

    validateDatetime(datetime, errors);
  }

  private void validateWorkNumber(final String workNumber, final MessageBundle errors) {
    if (!StringUtils.isNumeric(workNumber)) {
      final String errorMsg = "Work Number not numeric";
      errors.addError(errorMsg);
      logger.error(errorMsg);
    }
  }

  private void validateTrackingId(final String check_in_out_id, final MessageBundle errors) {
    if (check_in_out_id != null && !StringUtils.isNumeric(check_in_out_id)) {
      final String errorMsg = "Tracking Id not numeric";
      errors.addError(errorMsg);
      logger.error(errorMsg);
    }
  }

  private void validateDatetime(final String datetime, final MessageBundle errors) {
    if (datetime != null && !isDateTimeFormatValid(datetime)) {
      final String errorMsg = "Datetime does not obey format " + DATE_API_DATETIME_FORMAT;
      errors.addError(errorMsg);
      logger.error(errorMsg);
    }
  }

  private boolean isDateTimeFormatValid(String dateTime) {
    return DATETIME_REGEX_VALIDATOR.matcher(dateTime).matches();
  }

}
