package com.workmarket.service.business.dto;

import javax.validation.constraints.NotNull;


public class CompanyCommentDTO extends CommentDTO {
	@NotNull
	private Long companyId;

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
}

