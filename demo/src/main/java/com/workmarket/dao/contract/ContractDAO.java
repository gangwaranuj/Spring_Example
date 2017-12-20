package com.workmarket.dao.contract;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.contract.Contract;
import com.workmarket.domains.model.contract.ContractPagination;


public interface ContractDAO extends DAOInterface<Contract> {

	Contract findContractById(Long id);

	ContractPagination findAllContractsByCompanyId(Long companyId, ContractPagination contractPagination);

	Contract findContractByIdAndCompany(Long contractId, Long companyId);

	Contract findActiveContractByNameAndCompanyId(String name, Long companyId);
}
