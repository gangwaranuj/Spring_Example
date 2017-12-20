package com.workmarket.domains.model.contract;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="contractVersionUserSignature")
@Table(name="contract_version_user_signature")
@AuditChanges
public class ContractVersionUserSignature extends AuditedEntity {

	private static final long serialVersionUID = 1L;

    private User user;
    private ContractVersion contractVersion;
    private UserGroup userGroup;
    private String signature;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_version_id")
    public ContractVersion getContractVersion() {
        return contractVersion;
    }
    public void setContractVersion(ContractVersion contractVersion) {
        this.contractVersion = contractVersion;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    @Column(name="signature", nullable=false, length = 45)
    public String getSignature() {
        return signature;
    }
    public void setSignature(String signature) {
        this.signature = signature;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_group_id")
    public UserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(UserGroup group) {
		this.userGroup = group;
	}
}
