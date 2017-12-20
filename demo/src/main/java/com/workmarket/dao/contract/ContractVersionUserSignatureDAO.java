package com.workmarket.dao.contract;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.contract.ContractVersionUserSignature;
import com.workmarket.domains.model.contract.ContractVersionUserSignaturePagination;


public interface ContractVersionUserSignatureDAO extends DAOInterface<ContractVersionUserSignature> {

	ContractVersionUserSignaturePagination findAllContractVersionsUserSignaturesByUserId(long userId, ContractVersionUserSignaturePagination contractVersionUserSignaturePagination);

}
