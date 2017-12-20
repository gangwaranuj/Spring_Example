package com.workmarket.api.helpers;

import com.workmarket.api.v2.model.PaginationPage;
import com.workmarket.api.v2.worker.model.WorkersSearchRequest;

/**
 * Created by ianha on 4/3/15.
 */
public interface PeopleSearchServiceAdapter {
    PaginationPage searchPeople(WorkersSearchRequest request, Long userId, Long companyId) throws Exception;
}
