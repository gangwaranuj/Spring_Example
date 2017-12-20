package com.workmarket.api.v2.worker.model.validator;

import com.workmarket.api.v2.worker.model.CompleteDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by ianha on 4/10/15.
 */
@Component
public class CompleteDTOValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CompleteDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CompleteDTO completeDTO = (CompleteDTO) target;

        if (StringUtils.isEmpty(completeDTO.getResolution())) {
            errors.reject("assignment.complete.noresolution", "Resolution is a required field");
        }
    }
}
