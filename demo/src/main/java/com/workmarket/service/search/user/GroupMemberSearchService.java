package com.workmarket.service.search.user;

import com.workmarket.search.request.user.GroupPeopleSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.service.exception.search.SearchException;

public interface GroupMemberSearchService {

	PeopleSearchResponse searchPeople(GroupPeopleSearchRequest request) throws SearchException;
}
