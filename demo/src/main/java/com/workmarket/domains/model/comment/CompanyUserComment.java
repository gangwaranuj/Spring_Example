package com.workmarket.domains.model.comment;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.validation.constraints.NotNull;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name = "companyUserComment")
@NamedQueries({
})
@DiscriminatorValue("CUC")
@AuditChanges
public class CompanyUserComment extends UserComment {

	private static final long serialVersionUID = 1L;
	@NotNull private Company commentatorCompany;

	@ManyToOne(cascade = {}, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "commentator_company_id")
	public Company getCommentatorCompany() {
		return commentatorCompany;
	}

	public void setCommentatorCompany(Company commentatorCompany) {
		this.commentatorCompany = commentatorCompany;
	}
}
