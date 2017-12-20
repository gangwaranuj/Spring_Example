package com.workmarket.domains.model.block;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="blockedUserCompanyAssociation")
@DiscriminatorValue("2")
@AuditChanges
public class BlockedUserCompanyAssociation extends BlockedUserUserAssociation {

	private static final long serialVersionUID = 1L;

	private BlockingCompany blockingCompany;

	public BlockedUserCompanyAssociation() {}

	public BlockedUserCompanyAssociation(User user, User blockedUser) {
		super(user, blockedUser);
	}

	@Embedded
	public BlockingCompany getBlockingCompany() {
		return blockingCompany;
	}

	public void setBlockingCompany(BlockingCompany blockingCompany) {
		this.blockingCompany = blockingCompany;
	}

	public void setBlockingCompany(Company company) {
		this.blockingCompany = new BlockingCompany(company);
	}
}
