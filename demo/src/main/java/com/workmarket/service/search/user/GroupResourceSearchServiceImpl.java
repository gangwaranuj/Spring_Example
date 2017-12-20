package com.workmarket.service.search.user;

import com.workmarket.search.request.user.GroupPeopleSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.workmarket.search.model.PeopleSearchTransientData;
import com.workmarket.search.model.SearchType;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.service.exception.search.SearchException;

@Component
public class GroupResourceSearchServiceImpl implements GroupResourceSearchService {

	@Autowired
	private PeopleSearchService peopleSearchService;
	
	@Override
	public PeopleSearchResponse searchPeople(GroupPeopleSearchRequest request) throws SearchException {
		PeopleSearchTransientData data = new PeopleSearchTransientData();
		data.setOriginalRequest(request.getRequest());
		data.setSearchType(SearchType.PEOPLE_SEARCH_GROUP);
		data.setInviteToGroupId(request.getGroupId());
		return peopleSearchService.searchPeople(data);
	}

}
