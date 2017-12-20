package com.workmarket.api.v2.utils;

import com.workmarket.api.internal.model.UserRegistration;
import com.workmarket.domains.onboarding.model.PhoneInfoDTO;
import com.workmarket.dto.AddressDTO;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserRegistrationValidatorTest {
    @Mock MessageBundleHelper messageBundleHelper;
    @Mock Validator phoneValidator;
    @Mock Validator addressValidator;
    @InjectMocks UserRegistrationValidator validator;

    UserRegistration userRegistration;
    Errors errors;

    @Before
    public void setup() {
        userRegistration = mock(UserRegistration.class);
        errors = mock(Errors.class);
        when(messageBundleHelper.getMessage(any(String.class))).thenReturn("Some validation message goes here");
        when(userRegistration.getEmail()).thenReturn("ian@test.com");
        when(userRegistration.getPhoneNumber()).thenReturn("");
        when(userRegistration.getIsoCountryCode()).thenReturn("");
        when(userRegistration.getResumeUrl()).thenReturn("http://cdn.aws.com/some/user");
    }

    @Test
    public void shouldNoopOnNull() {
        validator.validate(null, errors);
        verify(errors, never()).rejectValue(any(String.class), any(String.class), any(String.class));
    }

    @Test
    public void shouldNoopEmptyPhoneNumberEmptyAddress() {
        validator.validate(userRegistration, errors);
        verify(errors, never()).rejectValue(any(String.class), any(String.class), any(String.class));
    }

    @Test
    public void shouldValidateTrueEmail() {
        validator.validate(userRegistration, errors);
        verify(errors, never()).rejectValue(any(String.class), any(String.class), any(String.class));
    }

    @Test
    public void shouldValidateFalseEmptyEmail() {
        when(userRegistration.getEmail()).thenReturn("");
        validator.validate(userRegistration, errors);
        verify(errors).rejectValue(any(String.class), any(String.class), any(String.class));
    }

    @Test
    public void shouldValidateFalseMalformedEmail() {
        when(userRegistration.getEmail()).thenReturn("ian@.com");
        validator.validate(userRegistration, errors);
        verify(errors).rejectValue(any(String.class), any(String.class), any(String.class));
    }

    @Test
    public void shouldValidatePhoneNumber() {
        when(userRegistration.getPhoneNumber()).thenReturn("9271231232");
        validator.validate(userRegistration, errors);
        verify(phoneValidator).validate(any(PhoneInfoDTO.class), any(Errors.class));
    }

    @Test
    public void shouldValidateFalseResumeUrl() {
        when(userRegistration.getResumeUrl()).thenReturn("httpkdjfd");
        validator.validate(userRegistration, errors);
        verify(errors, times(1)).rejectValue(any(String.class), any(String.class), any(String.class));
    }

    @Test
    public void shouldValidateAddress() {
        when(userRegistration.getAddress()).thenReturn("430 Glenn Dr.");
        when(userRegistration.getCity()).thenReturn("Belchertown");
        when(userRegistration.getIsoCountryCode()).thenReturn("US");
        when(userRegistration.getPostalCode()).thenReturn("01475");
        when(userRegistration.getStateCode()).thenReturn("MA");
        validator.validate(userRegistration, errors);
        verify(addressValidator).validate(any(AddressDTO.class), any(Errors.class));
    }
}