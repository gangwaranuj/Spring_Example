package com.workmarket.domains.velvetrope.service;

import com.workmarket.domains.velvetrope.model.Admission;
import com.workmarket.velvetrope.Venue;

import java.util.List;

public interface AdmissionService {
	List<Admission> findAllAdmissionsForCompanyId(Long companyId);
	List<Admission> findAllAdmissionsForPlanId(Long planId);
	List<Admission> findAllAdmissionsByKeyNameForVenue(String keyName, Venue... venues);
	List<Admission> findAllAdmissionsByKeyNameForVenueExcludingVenue(String keyName, Venue includedVenue, Venue excludedVenue);
	List<Admission> findAllAdmissionsByCompanyIdForVenue(Long companyId, Venue... venues);
	List<Admission> findAllAdmissionsByKeyNameExcludingVenueForVenues(String keyName, Venue excludedVenue, Venue... includedVenues);
	void saveAdmissionForCompanyIdAndVenue(Long companyId, Venue venue);
	void saveAdmissionsForPlanId(Long planId, List<Admission> admissions);
	void destroyAdmissionForCompanyIdAndVenue(Long companyId, Venue venue);
	void destroyAdmissionsForCompanyId(long companyId);
	void destroyAdmissionsForPlanId(Long planId);
	void grantAdmissionsForCompanyIdByPlanCode(Long companyId, String planCode);
}
