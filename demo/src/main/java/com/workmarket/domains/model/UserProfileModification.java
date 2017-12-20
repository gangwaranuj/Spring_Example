package com.workmarket.domains.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.audit.AuditedEntity;


@Entity(name="userProfileModification")
@Table(name="user_profile_modification")
@NamedQueries({
	@NamedQuery(name="userProfileModification.findAllByUserId", query="from userProfileModification where user.id = :user_id "),
	@NamedQuery(name="userProfileModification.findAllByModificationType", query="from userProfileModification where user.id = :user_id and userProfileModificationType = :modification_type "),
	@NamedQuery(name="userProfileModification.findAllByModificationStatus", query="from userProfileModification where userProfileModificationStatus = :modification_status "),
	@NamedQuery(name="userProfileModification.countByModificationStatus", query="Select count(*) from userProfileModification where userProfileModificationStatus = :modification_status")

})

@AuditChanges
public class UserProfileModification extends AuditedEntity{

	private static final long serialVersionUID = 1L;

	private ProfileModificationType profileModificationType;
	private UserProfileModificationStatus userProfileModificationStatus;
	private User user;

	public UserProfileModification() {
		this.userProfileModificationStatus = UserProfileModificationStatus.PENDING_APPROVAL;
	}

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "modification_type", nullable = false, referencedColumnName="code")
	public ProfileModificationType getProfileModificationType() {
		return profileModificationType;
	}

    public void setProfileModificationType(ProfileModificationType profileModificationType){
		this.profileModificationType = profileModificationType;
	}

    @Column(name = "modification_status", nullable = false)
	public UserProfileModificationStatus getUserProfileModificationStatus() {
		return userProfileModificationStatus;
	}

    public void setUserProfileModificationStatus(UserProfileModificationStatus userProfileModificationStatus){
		this.userProfileModificationStatus = userProfileModificationStatus;
	}


    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}

