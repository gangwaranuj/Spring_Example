package com.workmarket.search.validator;

import com.workmarket.service.exception.search.SearchException;
import com.workmarket.search.request.user.BackgroundScreeningChoice;
import com.workmarket.search.request.LocationFilter;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.SearchError;
import com.workmarket.search.SearchErrorType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newLinkedList;
import static org.apache.commons.lang.StringUtils.isEmpty;

@Component
public class PeopleSearchRequestValidator {

	private static final Log logger = LogFactory.getLog(PeopleSearchRequestValidator.class);

	public boolean validate(PeopleSearchRequest request) throws SearchException {
		Assert.notNull(request);
		List<SearchError> searchErrors = newLinkedList();
		validateBackgroundCheck(searchErrors, request);
		validateUserId(searchErrors, request);
		validateSetLongTypesInRequest(searchErrors, request);
		validateLocation(searchErrors, request);
		validateKeyword(request);

		// Throw the exception if there's any errors in the result
		if (!searchErrors.isEmpty()) {
			throw new SearchException("Invalid request parameters detected in search request", searchErrors);
		}
		return true;
	}

	private void validateBackgroundCheck(List<SearchError> searchErrors, PeopleSearchRequest request) {
		if (!request.isSetBackgroundScreeningFilter()) {
			return;
		}
		if (CollectionUtils.isEmpty(request.getBackgroundScreeningFilter())) {
			request.setBackgroundScreeningFilter(null);
			logger.warn("Request had a background screening filter list set but no elements: [" + request + "]");
			return;
		}

		for (BackgroundScreeningChoice choice : request.getBackgroundScreeningFilter()) {
			if (choice == null) {
				searchErrors.add(
					new SearchError()
						.setError(SearchErrorType.INVALID_REQUEST)
						.setWhy("Background screening sent a null value in the request: [" + request + "]"));
			}
		}
		
	}

	private void validateUserId(List<SearchError> searchErrors, PeopleSearchRequest request) {
		if (request.getUserId() <= 0) {
			searchErrors.add(new SearchError().setError(SearchErrorType.INVALID_REQUEST).setWhy("user id 0 or negative"));
		}
	}

	private void validateKeyword(PeopleSearchRequest request) {
		if (StringUtils.isBlank(request.getKeyword())) {
			request.setKeyword(null);
		}
	}

	private void validateLocation(List<SearchError> searchErrors, PeopleSearchRequest request) {
		if (request.isSetLocationFilter()) {
			LocationFilter location = request.getLocationFilter();
			if (location.isSetWillingToTravelTo() && isEmpty(location.getWillingToTravelTo())) {
				location.setWillingToTravelTo(null);
			}
			if (location.isSetMaxMileFromResourceToLocation()) {
				if (location.getMaxMileFromResourceToLocation() > 20000) {
					logger.warn("Max miles set above 20000 miles. Defaulting to 20000. " + request);
					location.setMaxMileFromResourceToLocation(20000);
				}
			}
		}
	}

	private void validateSetLongTypesInRequest(List<SearchError> errors, PeopleSearchRequest request) {
		if (request.isSetGroupFilter()) {
			Set<Long> listToCheck = request.getGroupFilter();
			String fieldName = "groupFilter";
			validateLongTypeListNotNullPositiveNonZero(errors, listToCheck, fieldName);
		}
		if (request.isSetTestFilter()) {
			Set<Long> listToCheck = request.getTestFilter();
			String fieldName = "testFilter";
			validateLongTypeListNotNullPositiveNonZero(errors, listToCheck, fieldName);
		}
		if (request.isSetCertificationFilter()) {
			Set<Long> listToCheck = request.getCertificationFilter();
			String fieldName = "certificationFilter";
			validateLongTypeListNotNullPositiveNonZero(errors, listToCheck, fieldName);
		}
		if (request.isSetIndustryFilter()) {
			Set<Long> listToCheck = request.getIndustryFilter();
			String fieldName = "industryFilter";
			validateLongTypeListNotNullPositiveNonZero(errors, listToCheck, fieldName);
		}
		if (request.isSetSkillFilter()) {
			Set<Long> listToCheck = request.getSkillFilter();
			String fieldName = "skillFilter";
			validateLongTypeListNotNullPositiveNonZero(errors, listToCheck, fieldName);
		}
		if (request.isSetInsuranceFilter()) {
			Set<Long> listToCheck = request.getInsuranceFilter();
			String fieldName = "insuranceFilter";
			validateLongTypeListNotNullPositiveNonZero(errors, listToCheck, fieldName);
		}
		if (request.isSetAssessmentFilter()) {
			Set<Long> listToCheck = request.getAssessmentFilter();
			String fieldName = "assessmentFilter";
			validateLongTypeListNotNullPositiveNonZero(errors, listToCheck, fieldName);
		}
	}

	private void validateListNotNullValues(List<SearchError> searchErrors, Set<?> setToCheck, String fieldName) {
		for (Object iShouldNotBeNull : setToCheck) {
			if (iShouldNotBeNull == null) {
				searchErrors.add(
					new SearchError()
						.setError(SearchErrorType.INVALID_REQUEST)
						.setWhy(fieldName + " has null value in its list."));
			}
		}
	}

	private void validateListPositiveValues(List<SearchError> searchErrors, Set<Long> setToCheck, String fieldName) {
		for (Long value : setToCheck) {
			if (value <= 0) {
				searchErrors.add(
					new SearchError()
						.setError(SearchErrorType.INVALID_REQUEST)
						.setWhy(fieldName + " has a non-positive ID in the request."));
			}
		}
	}

	private void validateLongTypeListNotNullPositiveNonZero(List<SearchError> searchErrors, Set<Long> setToCheck, String fieldName) {
		if (CollectionUtils.isEmpty(setToCheck)) {
			searchErrors.add(
				new SearchError()
					.setError(SearchErrorType.INVALID_REQUEST)
					.setWhy(fieldName + " has a 0 size set list."));
		}
		validateListNotNullValues(searchErrors, setToCheck, fieldName);
		validateListPositiveValues(searchErrors, setToCheck, fieldName);
	}

}
