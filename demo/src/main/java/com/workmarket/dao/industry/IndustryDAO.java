package com.workmarket.dao.industry;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.IndustryPagination;

public interface IndustryDAO extends DAOInterface<Industry>{

	@Override
	Industry get(Long industryId);

	List<Industry> findAllIndustries();

	IndustryPagination findAllIndustries(IndustryPagination pagination);

	Industry findIndustryById(Long industryId);

	Map<Long, String> findAllIndustryNamesToHydrateSearchData(Set<Long> industryIdsInResponse);

	Industry findIndustryByName(String name);
}
