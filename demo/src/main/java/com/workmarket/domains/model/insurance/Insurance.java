package com.workmarket.domains.model.insurance;

import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.VerifiableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.Calendar;

@Entity(name="insurance")
@Table(name="insurance")
@NamedQueries({
	@NamedQuery(name="insurance.findByName", query="from insurance where name = :name"),
	@NamedQuery(name="insurance.findByIndustry", query="from insurance where (industry.id = :industry_id or industry.id = 1)")
})
@AuditChanges
public class Insurance extends VerifiableEntity {

	private static final long serialVersionUID = 1L;

	private String name;
	private Industry industry;
	private Calendar lastActivityOn;

	@Column(name="name", nullable=false, length=200)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne(fetch=FetchType.EAGER, optional=false)
	@JoinColumn(name="industry_id")
	public Industry getIndustry() {
		return industry;
	}
	public void setIndustry(Industry industry) {
		this.industry = industry;
	}

	@Column(name="last_activity_on")
	public Calendar getLastActivityOn() {
		return lastActivityOn;
	}

	public void setLastActivityOn(Calendar lastActivityOn) {
		this.lastActivityOn = lastActivityOn;
	}

}
