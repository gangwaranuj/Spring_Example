package com.workmarket.api.v2.worker.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.workmarket.api.v2.worker.model.CheckInDTO;

@Component
public class CheckInValidator implements Validator {

    public boolean supports(Class clazz) {
        return CheckInDTO.class.isAssignableFrom(clazz);
    }

    public void validate(Object target,
                         Errors errors) {

        CheckInDTO checkInDTO = (CheckInDTO)target;

        /*
          if (checkInDTO.getLatitude() == 9) {

          errors.rejectValue("latitude",
          "field.min.length",
          new Object[] { Integer.valueOf(9) },
          "The password must be at least [" + 9 + "] characters in length.");
          }
        */
    }
}
