package com.workmarket.api.v2.worker.model.validator;

import com.workmarket.api.v2.worker.model.CompleteDTO;
import com.workmarket.api.v2.worker.model.SaveCustomFieldsDTO;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by ianha on 4/10/15.
 */
@Component
public class SaveCustomFieldsDTOValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return SaveCustomFieldsDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SaveCustomFieldsDTO saveCustomFieldsDTO = (SaveCustomFieldsDTO) target;

        if (ListUtils.emptyIfNull(saveCustomFieldsDTO.getCustomFields()).isEmpty()) {
            errors.reject("api.v2.validation.error.assignment.customfield.empty","At least one custom field group is required");
        }
    }
}
