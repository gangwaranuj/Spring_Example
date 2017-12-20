package com.workmarket.dao.crm;

import java.util.List;
import java.util.Map;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.model.crm.ClientCompanyPagination;
import com.workmarket.dto.SuggestionDTO;

public interface ClientCompanyDAO extends DAOInterface<ClientCompany>{

	ClientCompany findClientCompanyById(long id);

	List<ClientCompany> findClientCompanyByCompanyId(Long companyId);

	ClientCompanyPagination findClientCompanyByCompanyId(Long companyId, ClientCompanyPagination pagination);

	List<Map<String, Object>> findAllClientCompaniesByCompanyId(Long companyId, final String... columnNames);

	List<Map<String, Object>> findAllClientCompaniesByCompanyWithLocationCount(Long companyId);

	List<SuggestionDTO> suggest(String prefix, String property, Long companyId);

	ClientCompany findClientCompanyByName(long companyId, String name);

	ClientCompany findClientCompanyByIdAndCompany(Long clientCompanyId, Long companyId);

	List<ClientCompany> findClientCompanyByNumberAndCompany(String clientCompanyNumber, Long companyId);
}
