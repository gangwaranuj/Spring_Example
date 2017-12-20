package com.workmarket.dao.certification;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.certification.Certification;
import com.workmarket.domains.model.certification.CertificationPagination;


public interface CertificationDAO extends DAOInterface<Certification> {

    Certification findCertificationById(Long certificationId);
    
    CertificationPagination findAllCertifications(CertificationPagination pagination);
      
    Certification findCertificationByName(String name);
    
    Certification findCertificationByNameAndVendorId(String name, Long certificationVendorId);

    List<Certification> findAll();

	Map<Long, String> findAllCertificationNamesToHydrateSearchData(
			Set<Long> certificationIdsInResponse);
}
