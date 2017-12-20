package com.workmarket.domains.velvetrope.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.velvetrope.model.Admission;
import com.workmarket.velvetrope.Venue;

import java.util.List;

public interface AdmissionDAO extends DAOInterface<Admission> {
	List<Admission> findAllAdmissionsByKeyNameForVenue(String keyName, Venue... Venues);
	List<Admission> findAllAdmissionsByKeyNameForVenueExcludingVenue(String keyName, Venue includedVenue, Venue excludedVenue);
	List<Admission> findAllAdmissionsByKeyNameExcludingVenueForVenues(String keyName, Venue excludedVenue, Venue... includedVenues);
	List<Admission> findAllAdmissionsByCompanyIdForVenue(Long companyId, String keyName, Venue... venues);
}
