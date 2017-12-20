package com.workmarket.service.business;

import com.google.common.base.Optional;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.contract.Contract;
import com.workmarket.domains.model.contract.ContractPagination;
import com.workmarket.domains.model.contract.ContractVersion;
import com.workmarket.domains.model.contract.ContractVersionPagination;
import com.workmarket.domains.model.contract.ContractVersionUserSignature;
import com.workmarket.domains.model.contract.ContractVersionUserSignaturePagination;
import com.workmarket.service.business.dto.ContractDTO;
import com.workmarket.service.business.dto.ContractVersionDTO;
import com.workmarket.service.business.dto.ContractVersionUserSignatureDTO;

import java.util.List;
import java.util.Set;

public interface ContractService {

	/**
	 * Creates a new contract.
	 *
	 * @param contractDTO
	 * @return The created Contract
	 * @throws Exception
	 */
	Contract saveOrUpdateContract(ContractDTO contractDTO) throws Exception;

	/**
	 * Creates a new contract.
	 *
	 * @param contract
	 * @return
	 * @throws Exception
	 */
	Contract saveOrUpdateContract(Contract contract) throws Exception;

	/**
	 * Creates a new contract version.
	 *
	 * @param contractVersionDTO
	 * @return The created ContractVersion
	 * @throws Exception
	 */
	ContractVersion saveOrUpdateContractVersion(ContractVersionDTO contractVersionDTO) throws Exception;

	/**
	 * Creates a contract version signature.
	 *
	 * @param contractVersionUserSignatureDTO
	 *
	 * @return The created ContractVersionUserSignature
	 * @throws Exception
	 */
	ContractVersionUserSignature saveOrUpdateContractVersionUserSignature(ContractVersionUserSignatureDTO contractVersionUserSignatureDTO) throws Exception;

	Contract findContractById(Long contractId);

	Contract findContractByIdAndCompany(Long contractId, Long companyId);

	ContractVersionPagination findAllContractVersionsByContractId(Long contractVersionId, ContractVersionPagination pagination);

	ContractPagination findAllContractsByCompanyId(Long companyId, ContractPagination contractPagination);

	ContractVersionUserSignaturePagination findAllContractVersionsUserSignaturesByUserId(long l, ContractVersionUserSignaturePagination contractVersionUserSignaturePagination);

	ContractVersion findMostRecentContractVersionByContractId(Long contractVersionId);

	Optional<Long> findMostRecentContractVersionIdByContractId(long contractVersionId);

	ContractVersionUserSignature findContractVersionUserSignatureByContractVersionIdAndUserId(Long contractId, Long userId);

	ContractVersion findContractVersionByIdAndCompany(Long contractVersionId, Long companyId);

	Contract updateContractActiveFlag(Long contractId, boolean active);

	boolean activeContractExists(String name, Long companyId);

	List<Contract> findAllByCompanyId(Long companyId);

	Set<Asset> getMostRecentAssetsForContractId(Long id);

	ContractVersionUserSignature findOrCreateContractVersionUserSignature(ContractVersionUserSignatureDTO dto) throws Exception;

	Set<ContractVersion> findUnsignedContractsByUser(User user,List<Contract> contracts);
}
