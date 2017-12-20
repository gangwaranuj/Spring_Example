package com.workmarket.api.v2.worker.validators;

import com.workmarket.api.v2.worker.model.AddDeliverableDTO;
import com.workmarket.web.validators.FilenameValidator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.junit.Assert.assertTrue;

public class AddDeliverableDTOValidatorTest {

    //@Mock private MessageBundleHelper messageHelper;
    @Mock private FilenameValidator filenameValidator;

    private AddDeliverableDTOValidator addDeliverableDTOValidator;

    private AddDeliverableDTO addDeliverableDTO;

    @Before
    public void setup() {

        addDeliverableDTO = generateGoodAddDeliverableDTO();

        javax.validation.Validator v = new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean();

        addDeliverableDTOValidator = new AddDeliverableDTOValidator(v);

		/*
		when
          (
			  filenameValidator.validate(any(), any())
          )
          .thenReturn(Boolean.TRUE);
		*/

        /*
          messageHelper = mock(MessageBundleHelper.class);

          when
          (
          messageHelper.getMessage("assignment.budgetincrease.pricing_strategy_mismatch")
          )
          .thenReturn("The submitted budget request is of different price type than the assignment.");

          when
          (
          messageHelper.getMessage("Size.budgetIncreaseForm")
          )
          .thenReturn("The amount must be greater than the existing assignment budget.");

          when
          (
          messageHelper.getMessage("assignment.budgetincrease.internal_not_valid")
          )
          .thenReturn("You cannot submit a budget increase request on an internal assignment.");

          negotiationValidator.setMessageHelper(messageHelper);
        */
    }

    @Ignore @Test
    public void validateMissingName_errorResult() {

        AddDeliverableDTO addDeliverableDTO = new AddDeliverableDTO.Builder()
          .withDescription("")
          .withPosition(1)
          .withData("")
          .build();

        Errors errors = new BeanPropertyBindingResult(addDeliverableDTO, "addDeliverableDTO");
        addDeliverableDTOValidator.validate(addDeliverableDTO, errors);

        assertTrue(errors.hasErrors());
    }

    @Ignore @Test
    public void validateInvalidPosition_errorResult() {

        AddDeliverableDTO addDeliverableDTO = new AddDeliverableDTO.Builder()
          .withName("")
          .withDescription("")
          .withPosition(-1)
          .withData("")
          .build();

        Errors errors = new BeanPropertyBindingResult(addDeliverableDTO, "addDeliverableDTO");
        addDeliverableDTOValidator.validate(addDeliverableDTO, errors);

        assertTrue(errors.hasErrors());
    }

    @Ignore @Test
    public void validateMissingData_errorResult() {

        AddDeliverableDTO addDeliverableDTO = new AddDeliverableDTO.Builder()
          .withName("")
          .withDescription("")
          .withPosition(0)
          .build();
        //.withData();

        Errors errors = new BeanPropertyBindingResult(addDeliverableDTO, "addDeliverableDTO");
        addDeliverableDTOValidator.validate(addDeliverableDTO, errors);

        assertTrue(errors.hasErrors());
    }

    private AddDeliverableDTO generateGoodAddDeliverableDTO() {

        AddDeliverableDTO addDeliverableDTO = new AddDeliverableDTO.Builder().build();

        return addDeliverableDTO;
    }
}
