package com.workmarket.service.business.dto;

public class ContractVersionUserSignatureDTO {

	private Long ContractVersionUserSignatureId;
	private Long userId;
	private Long contractVersionId;
	private Long groupId;
	private String signature;

	public ContractVersionUserSignatureDTO() {
	}

	public ContractVersionUserSignatureDTO(Long contractVersionUserSignatureId, Long userId, Long contractVersionId, Long groupId, String signature) {
		ContractVersionUserSignatureId = contractVersionUserSignatureId;
		this.userId = userId;
		this.contractVersionId = contractVersionId;
		this.groupId = groupId;
		this.signature = signature;
	}

	public Long getContractVersionUserSignatureId() {
		return ContractVersionUserSignatureId;
	}

	public void setContractVersionUserSignatureId(Long contractVersionUserSignatureId) {
		ContractVersionUserSignatureId = contractVersionUserSignatureId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getContractVersionId() {
		return contractVersionId;
	}

	public void setContractVersionId(Long contractVersionId) {
		this.contractVersionId = contractVersionId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
}
