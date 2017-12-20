package com.workmarket.service.search;

import com.google.common.collect.Sets;
import com.workmarket.search.validator.PeopleSearchRequestValidator;
import com.workmarket.service.exception.search.SearchException;
import com.workmarket.search.request.user.BackgroundScreeningChoice;
import com.workmarket.search.request.LocationFilter;
import com.workmarket.search.request.user.PeopleSearchRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class PeopleSearchRequestValidatorTest {


	@InjectMocks private PeopleSearchRequestValidator peopleSearchRequestValidator;


	@Test(expected = IllegalArgumentException.class)
	public void validate_withNullRequest_fail() throws SearchException {
		peopleSearchRequestValidator.validate(null);
	}

	@Test(expected = SearchException.class)
	public void validate_withEmptyUserId_fail() throws SearchException {
		PeopleSearchRequest request = new PeopleSearchRequest();
		peopleSearchRequestValidator.validate(request);
	}

	@Test(expected = SearchException.class)
	public void validate_withEmptyGroupIdsRequest_fail() throws SearchException {
		PeopleSearchRequest request = new PeopleSearchRequest();
		request.setUserId(1L);
		Set<Long> groups = Sets.newHashSet();
		request.setGroupFilter(groups);
		peopleSearchRequestValidator.validate(request);
	}

	@Test(expected = SearchException.class)
	public void validate_withEmptyTestIdsRequest_fail() throws SearchException {
		PeopleSearchRequest request = new PeopleSearchRequest();
		request.setUserId(1L);
		Set<Long> tests = Sets.newHashSet();
		request.setTestFilter(tests);
		peopleSearchRequestValidator.validate(request);
	}

	@Test
	public void validate_withMilesFromResourceExceeded_success() throws SearchException {
		PeopleSearchRequest request = new PeopleSearchRequest();
		request.setUserId(1L);
		LocationFilter locationFilter = new LocationFilter();
		locationFilter.setMaxMileFromResourceToLocation(20005);
		request.setLocationFilter(locationFilter);
		Assert.assertTrue(peopleSearchRequestValidator.validate(request));
	}

	@Test
	public void validate_withEmptyBackgroundCheckRequest_success() throws SearchException {
		PeopleSearchRequest request = new PeopleSearchRequest();
		request.setUserId(1L);
		Set<BackgroundScreeningChoice> choiceSet = Sets.newHashSet();
		request.setBackgroundScreeningFilter(choiceSet);
		Assert.assertTrue(peopleSearchRequestValidator.validate(request));
	}

	@Test
	public void validate_withEmptyKeywordRequest_success() throws SearchException {
		PeopleSearchRequest request = new PeopleSearchRequest();
		request.setUserId(1L);
		request.setKeyword("");
		Assert.assertTrue(peopleSearchRequestValidator.validate(request));
	}

	@Test
	public void validate_withNonEmptyKeywordRequest_success() throws SearchException {
		PeopleSearchRequest request = new PeopleSearchRequest();
		request.setUserId(1L);
		request.setKeyword("keyword");
		Assert.assertTrue(peopleSearchRequestValidator.validate(request));
	}
}
