package com.workmarket.domains.model.changelog.user;

import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name = "userChangeLog")
@Table(name = "user_changelog")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("U")
@AuditChanges
public class UserChangeLog extends AuditedEntity {
	private static final long serialVersionUID = 1L;

	private Long user;
	private Long actor;
	private Long masqueradeActor;

	protected UserChangeLog() {
	}

	protected UserChangeLog(Long user, Long actor, Long masqueradeActor) {
		this.user = user;
		this.actor = actor;
		this.masqueradeActor = masqueradeActor;
	}

	@Column(name = "user_id", updatable = false)
	public Long getUser() {
		return user;
	}

	public void setUser(Long user) {
		this.user = user;
	}

	@Column(name = "actor_id", updatable = false)
	public Long getActor() {
		return actor;
	}

	public void setActor(Long actor) {
		this.actor = actor;
	}

	@Column(name = "masquerade_actor_id")
	public Long getMasqueradeActor() {
		return masqueradeActor;
	}

	public void setMasqueradeActor(Long masqueradeActor) {
		this.masqueradeActor = masqueradeActor;
	}

	@Transient
	public String getChangeLogType() {
		return this.getClass().getSimpleName();
	}

	@Transient
	public String getDescription() {
		return "Base change log";
	}
}
