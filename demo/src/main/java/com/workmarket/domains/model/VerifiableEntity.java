package com.workmarket.domains.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class VerifiableEntity extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	private VerificationStatus verificationStatus = VerificationStatus.PENDING;

	@Column(name = "verification_status")
	public VerificationStatus getVerificationStatus() {
		return verificationStatus;
	}

	public void setVerificationStatus(VerificationStatus verificationStatus) {
		this.verificationStatus = verificationStatus;
	}

	@Transient
	public boolean isVerified() {
		return VerificationStatus.VERIFIED.equals(verificationStatus);
	}
}
