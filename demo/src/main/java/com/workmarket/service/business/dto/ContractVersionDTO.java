package com.workmarket.service.business.dto;


public class ContractVersionDTO {

	private Long contractVersionId;
	private Long contractId;

	public ContractVersionDTO() {
	}

	public ContractVersionDTO(Long contractVersionId, Long contractId) {
		this.contractVersionId = contractVersionId;
		this.contractId = contractId;
	}

	public Long getContractVersionId() {
		return contractVersionId;
	}

	public void setContractVersionId(Long contractVersionId) {
		this.contractVersionId = contractVersionId;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}
}
