package com.workmarket.dao.license;

import java.util.List;
import java.util.Set;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.license.License;
import com.workmarket.domains.model.license.LicensePagination;


public interface LicenseDAO extends DAOInterface<License> {

    License findLicenseById(Long id);

	License findLicenseByName(String name);

    LicensePagination findAllLicensesByStateId(String stateId, LicensePagination pagination);
 
    List<License> findAll();
    
    LicensePagination findAll(LicensePagination pagination);

	List<License> findAllLicenseNamesByIds(Set<Long> licenseIdsInResponse);
}
