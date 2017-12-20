package com.workmarket.dao.certification;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.certification.CertificationVendor;
import com.workmarket.domains.model.certification.CertificationVendorPagination;


public interface CertificationVendorDAO extends DAOInterface<CertificationVendor> {
	
	CertificationVendor findCertificationVendorById(Long vendorId);
	
	CertificationVendor findCertificationVendorByNameAndIndustryId(String name, Long industryId);

    CertificationVendorPagination findAllCertificationVendors(CertificationVendorPagination pagination);

    CertificationVendorPagination findCertificationVendorByTypeId(CertificationVendorPagination pagination, Long certificationTypeId);
}
