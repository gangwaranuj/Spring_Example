package com.workmarket.domains.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity(name = "userLocationTypeAssociation")
@Table(name = "user_location_type_association")
@AuditChanges
public class UserLocationTypeAssociation extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	private User user;
	private LocationType locationType;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne(optional = false)
    @JoinColumn(name = "location_type_id")
	public LocationType getLocationType() {
		return locationType;
	}

	public void setLocationType(LocationType locationType) {
		this.locationType = locationType;
	}

}
