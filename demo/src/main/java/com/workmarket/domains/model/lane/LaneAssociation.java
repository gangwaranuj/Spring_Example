package com.workmarket.domains.model.lane;

import com.workmarket.domains.model.ApprovableVerifiableEntity;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity(name="laneAssociation")
@Table(name="lane_association")
@NamedQueries({
        @NamedQuery(name="laneAssociation.findAllAssociationsWithApprovalStatus", query="select a from laneAssociation a where a.approvalStatus = :approvalStatus and a.deleted = false"),
        @NamedQuery(name="laneAssociation.findAllAssociationsWhereUserIdIn", query="select a from laneAssociation a inner join a.user where a.company.id = :companyId and a.user.id in (:userIds) and a.deleted = false"),
        @NamedQuery(name="laneAssociation.findAllAssociationsWhereUserIdInByLaneType", query="select a from laneAssociation a inner join a.user where a.company.id = :companyId and a.user.id in (:userIds) and laneType = :laneType")
})
@AuditChanges
public class LaneAssociation extends ApprovableVerifiableEntity {

	private static final long serialVersionUID = 1L;

    private User user;
    private Company company;
    private LaneType laneType = LaneType.LANE_3;

	public LaneAssociation() {
		setApprovalStatus(ApprovalStatus.APPROVED);
		setVerificationStatus(VerificationStatus.VERIFIED);
	}

	public LaneAssociation(User user, Company company, LaneType laneType) {
		this.user = user;
		this.company = company;
		this.laneType = laneType;
		setApprovalStatus(ApprovalStatus.APPROVED);
		setVerificationStatus(VerificationStatus.VERIFIED);
	}

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, updatable = false)
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Column(name = "lane_type_id", nullable = false, unique = false)
    public LaneType getLaneType() {
        return laneType;
    }

    public void setLaneType(LaneType laneType) {
        this.laneType = laneType;
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LaneAssociation)) return false;
		if (!super.equals(o)) return false;

		LaneAssociation that = (LaneAssociation) o;

		if (!company.equals(that.company)) return false;
		if (!user.equals(that.user)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + user.hashCode();
		result = 31 * result + company.hashCode();
		return result;
	}
}
