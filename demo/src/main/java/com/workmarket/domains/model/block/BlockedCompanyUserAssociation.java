package com.workmarket.domains.model.block;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="blockedCompanyUserAssociation")
@NamedQueries({})
@DiscriminatorValue("3")
@AuditChanges
public class BlockedCompanyUserAssociation extends AbstractBlockedAssociation {

	private static final long serialVersionUID = 1L;

	private BlockedCompany blockedCompany;

	public BlockedCompanyUserAssociation() {}

	public BlockedCompanyUserAssociation(User user, Company blockedCompany) {
		super(user);
		this.blockedCompany = new BlockedCompany(blockedCompany);
	}

	@Embedded
	public BlockedCompany getBlockedCompany() {
		return blockedCompany;
	}

	public void setBlockedCompany(BlockedCompany blockedCompany) {
		this.blockedCompany = blockedCompany;
	}

}
