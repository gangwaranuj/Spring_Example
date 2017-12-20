package com.workmarket.service.search.user;

import com.workmarket.search.model.PeopleSearchTransientData;
import com.workmarket.search.model.SearchType;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.service.exception.search.SearchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssessmentInviteSearchServiceImpl implements AssessmentInviteSearchService {

	@Autowired private PeopleSearchService peopleSearchService;

	@Override
	public PeopleSearchResponse searchPeople(PeopleSearchRequest request) throws SearchException {
		PeopleSearchTransientData data = new PeopleSearchTransientData();
		data.setOriginalRequest(request);
		data.setSearchType(SearchType.PEOPLE_SEARCH_ASSESSMENT_INVITE);
		data.setInviteToAssessmentId(request.getCurrentAssessmentId());
		return peopleSearchService.searchPeople(data);
	}

}
