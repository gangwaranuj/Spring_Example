package com.workmarket.domains.velvetrope.service;

import com.workmarket.domains.velvetrope.model.Admission;
import com.workmarket.domains.velvetrope.model.BetaFeatureAdmissionRequest;
import com.workmarket.service.helpers.ServiceResponseBuilder;
import com.workmarket.velvetrope.Venue;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BetaFeatureService {

	List<Admission> getAllCompanyBetaFeatureAdmissions();

	Map<Venue, List<Admission>> getVenuesAndAdmissionsForBetaFeatures();

	void updateFeatureParticipation(BetaFeatureAdmissionRequest betaFeatureAdmissionRequest);

	void turnFeatureOn(Venue betaFeature, Set<Long> companyIds);

	void turnFeatureOff(Venue betaFeature, Set<Long> companyIds);

	boolean canToggleOwnCompanyBetaFeatureParticipation(long userId);

	ServiceResponseBuilder getBetaFeaturesResponseBuilder();

	Map<String, Object> getBetaFeaturesResponseData();

}
