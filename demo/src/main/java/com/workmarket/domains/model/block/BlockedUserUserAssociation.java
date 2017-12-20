package com.workmarket.domains.model.block;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="blockedUserUserAssociation")
@DiscriminatorValue("1")
@AuditChanges
public class BlockedUserUserAssociation extends AbstractBlockedAssociation {

	private static final long serialVersionUID = 1L;

	private BlockedUser blockedUser;

	public BlockedUserUserAssociation() {}

	public BlockedUserUserAssociation(User user, User blockedUser) {
		super(user);
		this.blockedUser = new BlockedUser(blockedUser);
	}

	@Embedded
	public BlockedUser getBlockedUser() {
		return blockedUser;
	}

	public void setBlockedUser(BlockedUser blockedUser) {
		this.blockedUser = blockedUser;
	}

}
