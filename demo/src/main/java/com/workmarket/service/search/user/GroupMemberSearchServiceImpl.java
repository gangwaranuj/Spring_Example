package com.workmarket.service.search.user;

import com.workmarket.search.model.PeopleSearchTransientData;
import com.workmarket.search.model.SearchType;
import com.workmarket.search.request.user.GroupPeopleSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.service.exception.search.SearchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupMemberSearchServiceImpl implements GroupMemberSearchService {

	@Autowired
	private PeopleSearchService peopleSearchService;

	@Override
	public PeopleSearchResponse searchPeople(GroupPeopleSearchRequest request) throws SearchException {
		PeopleSearchTransientData data = new PeopleSearchTransientData();
		data.setOriginalRequest(request.getRequest());
		data.setSearchType(SearchType.PEOPLE_SEARCH_GROUP_MEMBER);
		data.setMemberOfGroupId(request.getGroupId());
		return peopleSearchService.searchPeople(data);
	}
}
