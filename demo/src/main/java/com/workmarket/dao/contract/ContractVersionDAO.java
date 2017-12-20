package com.workmarket.dao.contract;

import com.google.common.base.Optional;
import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.contract.ContractVersion;
import com.workmarket.domains.model.contract.ContractVersionPagination;


public interface ContractVersionDAO extends DAOInterface<ContractVersion> {

	ContractVersionPagination findAllContractVersionsByContractId(Long contractId, ContractVersionPagination pagination);

	ContractVersion findContractVersionByIdAndCompany(Long contractVersionId, Long companyId);

	ContractVersion findMostRecentContractVersionByContractId(Long contractId);

	Optional<Long> findMostRecentContractVersionIdByContractId(long contractId);

}
