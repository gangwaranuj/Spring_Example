package com.workmarket.domains.model.license;

import com.workmarket.domains.model.VerifiableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.Calendar;


@Entity(name="license")
@Table(name="license")
@NamedQueries({
	@NamedQuery(name="license.findLicenseByName", query="from license c where c.name = :name")
})
@AuditChanges
public class License extends VerifiableEntity {

	private static final long serialVersionUID = 1L;

	private String name;
	private String state;
	private Calendar lastActivityOn;

	public License() {}

	@Column(name = "name", nullable = false, length=200)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "state", nullable = false, length=2)
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Column(name="last_activity_on")
	public Calendar getLastActivityOn() {
		return lastActivityOn;
	}

	public void setLastActivityOn(Calendar lastActivityOn) {
		this.lastActivityOn = lastActivityOn;
	}
}
