package com.workmarket.api.v2.worker.search.validation;

import com.workmarket.api.v2.worker.model.WorkersSearchRequest;
import com.workmarket.api.v2.worker.model.validator.WorkersSearchRequestValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by ianha on 4/11/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class WorkersSearchRequestValidatorTest {
    WorkersSearchRequestValidator validator;
    @Mock WorkersSearchRequest request;
    @Mock Errors errors;

    @Before
    public void setup() {
        validator = new WorkersSearchRequestValidator();

        when(request.getPage()).thenReturn(1);
        when(request.getPageSize()).thenReturn(25);
        when(request.getOrder()).thenReturn("asc");
        when(request.getRadius()).thenReturn(100);
        when(request.getSortby()).thenReturn("name");
    }

    @Test
    public void shouldValidateTrue() {
        validator.validate(request, errors);
        verify(errors, never()).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void shouldValidateFalseNegativePage() {
        when(request.getPage()).thenReturn(-1);
        validator.validate(request, errors);
        verify(errors, times(1)).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void shouldValidateFalseNegativePageSize() {
        when(request.getPageSize()).thenReturn(-1);
        validator.validate(request, errors);
        verify(errors, times(1)).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void shouldValidateFalseUnknownOrder() {
        when(request.getOrder()).thenReturn("assdfsdf");
        validator.validate(request, errors);
        verify(errors, times(1)).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void shouldValidateFalseNegativeRadius() {
        when(request.getRadius()).thenReturn(-1);
        validator.validate(request, errors);
        verify(errors, times(1)).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void shouldValidateFalseUnknownSortby() {
        when(request.getSortby()).thenReturn("nsdfsfd");
        validator.validate(request, errors);
        verify(errors, times(1)).rejectValue(anyString(), anyString(), anyString());
    }
}
