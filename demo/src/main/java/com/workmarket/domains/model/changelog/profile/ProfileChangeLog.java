package com.workmarket.domains.model.changelog.profile;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name = "profileChangeLog")
@Table(name = "profile_changelog")
@NamedQueries({})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("P")
@AuditChanges
public class ProfileChangeLog extends AuditedEntity {
	private static final long serialVersionUID = 1L;

	private Profile profile;
	private User actor;
	private User masqueradeActor;

	protected ProfileChangeLog() {
	}

	protected ProfileChangeLog(Profile profile, User actor, User masqueradeActor) {
		this.profile = profile;
		this.actor = actor;
		this.masqueradeActor = masqueradeActor;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = {})
	@JoinColumn(name = "profile_id")
	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = {})
	@JoinColumn(name = "actor_id")
	public User getActor() {
		return actor;
	}

	public void setActor(User actor) {
		this.actor = actor;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = {})
	@JoinColumn(name = "masquerade_actor_id")
	public User getMasqueradeActor() {
		return masqueradeActor;
	}

	public void setMasqueradeActor(User masqueradeActor) {
		this.masqueradeActor = masqueradeActor;
	}

	@Transient
	public String getChangeLogType() {
		return this.getClass().getSimpleName();
	}

	@Transient
	public String getDescription() {
		return "Profile change";
	}
}
