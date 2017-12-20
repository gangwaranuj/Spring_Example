package com.workmarket.service.search.user;

import com.workmarket.search.model.PeopleSearchTransientData;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.service.exception.search.SearchException;

public interface PeopleSearchService {

	PeopleSearchResponse searchPeople(PeopleSearchRequest request) throws SearchException;

	PeopleSearchResponse searchPeople(PeopleSearchTransientData data) throws SearchException;
}
