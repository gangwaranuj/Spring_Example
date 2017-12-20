package com.workmarket.service.search.user;

import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.service.exception.search.SearchException;

public interface AssessmentInviteSearchService {

	PeopleSearchResponse searchPeople(PeopleSearchRequest request) throws SearchException;
}
