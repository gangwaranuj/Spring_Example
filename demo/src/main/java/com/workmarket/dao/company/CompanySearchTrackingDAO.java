package com.workmarket.dao.company;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.company.CompanySearchTracking;

import java.util.List;

public interface CompanySearchTrackingDAO extends DAOInterface<CompanySearchTracking> {

	CompanySearchTracking findCompanySearchTrackingByCompanyId(long trackingCompanyId);

}
