package com.workmarket.domains.model.block;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="blockedCompanyCompanyAssociation")
@NamedQueries({})
@DiscriminatorValue("4")
@AuditChanges
public class BlockedCompanyCompanyAssociation extends BlockedCompanyUserAssociation {

	private static final long serialVersionUID = 1L;

	private BlockingCompany blockingCompany;

	public BlockedCompanyCompanyAssociation() {}

	public BlockedCompanyCompanyAssociation(User user, Company blockedCompany) {
		super(user, blockedCompany);
	}

	@Embedded
	public BlockingCompany getBlockingCompany() {
		return blockingCompany;
	}

	public void setBlockingCompany(BlockingCompany blockingCompany) {
		this.blockingCompany = blockingCompany;
	}

	public void setBlockingCompany(Company blockingCompany) {
		this.blockingCompany = new BlockingCompany(blockingCompany);
	}
}
