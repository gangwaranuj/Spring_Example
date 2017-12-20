package com.workmarket.service.business.dto;

import com.workmarket.domains.model.contract.Contract;
import com.workmarket.utility.BeanUtilities;

public class ContractDTO extends AbstractDTO {

	private Long contractId;
	private String name;
	private Long companyId;

	public ContractDTO() {
	}

	public ContractDTO(String name, Long companyId) {
		this.name = name;
		this.companyId = companyId;
	}

	public static ContractDTO newDTO(Contract contract) {
		ContractDTO dto = new ContractDTO();
		BeanUtilities.copyProperties(dto, contract);
		dto.setContractId(contract.getId());
		return dto;
	}

	public Long getContractId() {
		return this.contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
}
