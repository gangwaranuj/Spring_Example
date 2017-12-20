package com.workmarket.api.v2.worker.model.validator;

import com.workmarket.api.v2.worker.model.WorkersSearchRequest;
import com.workmarket.search.SortDirectionType;
import com.workmarket.search.request.user.PeopleSearchSortByType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by ianha on 4/10/15.
 */
@Component
public class WorkersSearchRequestValidator implements Validator {
    private static final int MIN_PAGE_SIZE = 1;
    private static final int MAX_PAGE_SIZE = 100;

    @Override
    public boolean supports(Class<?> clazz) {
        return WorkersSearchRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        WorkersSearchRequest request = (WorkersSearchRequest) target;

        if (request.getPage() < 1) {
            errors.rejectValue("page", "page", "Invalid page value '" + request.getPage() + "'");
        }

        if (request.getPageSize() < MIN_PAGE_SIZE || request.getPageSize() > MAX_PAGE_SIZE) {
            errors.rejectValue("pageSize", "pageSize", "pageSize value '" + request.getPageSize() + "' out of range. Accepted range from " + MIN_PAGE_SIZE + " to " + MAX_PAGE_SIZE);
        }

        if (request.getOrder() != null && SortDirectionType.findByName(request.getOrder()) == null) {
            errors.rejectValue("order", "order", "Invalid order value '" + request.getOrder() + "'");
        }

        if (request.getSortby() != null && PeopleSearchSortByType.findByName(request.getSortby()) == null) {
            errors.rejectValue("sortby", "sortby", "Invalid order value '" + request.getSortby() + "'");
        }

        if (request.getRadius() < 1) {
            errors.rejectValue("radius", "radius", "Invalid radius value '" + request.getRadius() + "'");
        }
    }
}
