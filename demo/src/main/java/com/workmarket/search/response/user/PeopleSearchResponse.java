package com.workmarket.search.response.user;

import com.google.common.collect.Lists;
import com.workmarket.search.response.FacetResult;
import com.workmarket.search.response.SearchResponse;
import com.workmarket.utility.StringUtilities;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PeopleSearchResponse extends SearchResponse<PeopleSearchResult, PeopleFacetResultType> {
	private static final long serialVersionUID = 1L;

	public PeopleSearchResponse() {
		super(new ArrayList<PeopleSearchResult>(), new LinkedHashMap<Enum<PeopleFacetResultType>, List<FacetResult>>());
	}

	public List<String[]> generateResultList() {
		final List<String[]> resultList = Lists.newArrayListWithExpectedSize(getResultsSize());
		for (PeopleSearchResult r : getResults()) {
			resultList.add(flatPeopleSearchResult(r));
		}
		return resultList;
	}

	String[] flatPeopleSearchResult(PeopleSearchResult peopleSearchResult) {
		final Map<String, String> temp = new LinkedHashMap<>();
		temp.put("user_number", peopleSearchResult.getUserNumber());
		temp.put("first_name", peopleSearchResult.getName().getFirstName());
		temp.put("last_name", peopleSearchResult.getName().getLastName());
		temp.put("email", peopleSearchResult.getEmail());
		temp.put("role", "");
		temp.put("work_phone", peopleSearchResult.getWorkPhone());
		temp.put("mobile_phone", peopleSearchResult.getMobilePhone());
		temp.put("user_created", peopleSearchResult.getCreatedOn());

		String address1 = "";
		String address2 = "";
		String city = "";
		String state = "";
		String postalCode = "";
		Double latitude = null;
		Double longitude = null;

		if (peopleSearchResult.getAddress() != null) {
			address1 = peopleSearchResult.getAddress().getAddressLine1();
			address2 = peopleSearchResult.getAddress().getAddressLine2();
			city  = peopleSearchResult.getAddress().getCity();
			state = peopleSearchResult.getAddress().getState();
			postalCode = peopleSearchResult.getAddress().getZip();
			if (peopleSearchResult.getAddress().getPoint() != null) {
				latitude = peopleSearchResult.getAddress().getPoint().getLatitude();
				longitude = peopleSearchResult.getAddress().getPoint().getLongitude();
			}
		}

		if (peopleSearchResult.isSetLocationPoint()) {
			latitude = peopleSearchResult.getLocationPoint().getLatitude();
			longitude = peopleSearchResult.getLocationPoint().getLongitude();
		}

		temp.put("address1", address1);
		temp.put("address2", address2);
		temp.put("city", city);
		temp.put("state", state);
		temp.put("postalCode", postalCode);
		temp.put("latitude", (latitude != null ? latitude.toString() : ""));
		temp.put("longitude", (longitude != null ? longitude.toString() : ""));

		temp.put("last_login", "");
		temp.put("background_check", peopleSearchResult.getLastBackgroundCheckDate());
		temp.put("drug_test", peopleSearchResult.getLastDrugTestDate());
		temp.put("shared_worker", StringUtilities.toYesNo(peopleSearchResult.isSharedWorkerRole()));
		temp.put("status", peopleSearchResult.getUserStatusType());
		temp.put("confirmed_email", StringUtilities.toYesNo(peopleSearchResult.isEmailConfirmed()));

		temp.put("company_id", String.valueOf(peopleSearchResult.getCompanyId()));
		temp.put("company_name", peopleSearchResult.getCompanyName());
		temp.put("company_status", peopleSearchResult.getCompanyStatusType());
		temp.put("bank_account", StringUtilities.toYesNo(peopleSearchResult.isConfirmedBankAccount()));
		temp.put("TIN_verified", StringUtilities.toYesNo(peopleSearchResult.isApprovedTIN()));
		temp.put("resource_active_assignments", "");
		temp.put("resource_completed_assignments", String.valueOf(peopleSearchResult.getWorkCompletedCount()));
		temp.put("cbsa_name", peopleSearchResult.getCbsaName());

		return temp.values().toArray(new String[temp.values().size()]);
	}

}