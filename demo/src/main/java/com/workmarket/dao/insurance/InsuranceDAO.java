package com.workmarket.dao.insurance;

import com.workmarket.dao.PaginatableDAOInterface;
import com.workmarket.domains.model.insurance.Insurance;
import com.workmarket.domains.model.insurance.InsurancePagination;

import java.util.Map;
import java.util.Set;

public interface InsuranceDAO extends PaginatableDAOInterface<Insurance> {

	InsurancePagination findByIndustry(long industryId, InsurancePagination pagination);
	InsurancePagination findAllInsurances(InsurancePagination pagination);
	Map<Long, String> findAllInsuranceNamesAndId();
	Map<Long, String> findAllInsuranceNamesByInsuranceId(Set<Long> insuranceIds);

}