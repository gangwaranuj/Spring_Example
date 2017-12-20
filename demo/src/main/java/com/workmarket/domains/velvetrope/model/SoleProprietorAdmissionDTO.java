package com.workmarket.domains.velvetrope.model;

import com.workmarket.domains.model.User;

public class SoleProprietorAdmissionDTO {

	private long userId;
	private String userFullName;
	private long companyId;
	private long admissionId;

	public SoleProprietorAdmissionDTO(User user, long companyId, Admission admission) {
		this.userId = user.getId();
		this.userFullName = user.getFullName();
		this.companyId = companyId;
		this.admissionId = admission.getId();
	}
}
