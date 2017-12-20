package com.workmarket.domains.onboarding.model;

import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PhoneInfoDTOValidatorTest {
    @Mock MessageBundleHelper messageBundleHelper;
    @InjectMocks PhoneInfoDTOValidator validator;

    PhoneInfoDTO dto;
    Errors errors;

    @Before
    public void setup() {
        dto = mock(PhoneInfoDTO.class);
        errors = mock(Errors.class);
        when(messageBundleHelper.getMessage("phoneInfo.validation.invalidPhone")).thenReturn("The phone number (%s) is not valid for region %s.");
    }

    @Test
    public void shouldNoopOnNull() {
        validator.validate(null, errors);
        verify(errors, never()).rejectValue(any(String.class), any(String.class), any(String.class));
    }

    @Test
    public void shouldNoopOnBlank() {
        when(dto.getNumber()).thenReturn("");
        validator.validate(dto, errors);
        verify(errors, never()).rejectValue(any(String.class), any(String.class), any(String.class));
    }

    @Test
    public void shouldRejectInvalidPhoneNumber() {
        when(dto.getNumber()).thenReturn("91723");
        validator.validate(dto, errors);
        verify(errors).rejectValue(any(String.class), any(String.class), any(String.class));
    }

    @Test
    public void shouldAcceptValidPhoneNumber() {
        when(dto.getNumber()).thenReturn("9172332343");
        validator.validate(dto, errors);
        verify(errors, never()).rejectValue(any(String.class), any(String.class), any(String.class));
    }
}
