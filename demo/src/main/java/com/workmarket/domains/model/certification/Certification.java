package com.workmarket.domains.model.certification;

import com.workmarket.domains.model.VerifiableEntity;
import com.workmarket.domains.model.audit.AuditChanges;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.Calendar;


@Entity(name="certification")
@Table(name="certification")
@NamedQueries({
	@NamedQuery(name="certification.findCertificationByName", query="from certification c where c.name = :name"),
	//1 = Verified
	@NamedQuery(name="certification.findAll", query="select c from certification c where c.deleted = false and c.verificationStatus = 1")
})

@AuditChanges
public class Certification extends VerifiableEntity {

	private static final long serialVersionUID = 1L;

	private String name;
	private CertificationVendor certificationVendor;
	private Calendar lastActivityOn;

	public Certification() { }

	public Certification(Long id) {
		setId(id);
	}

	@Column(name = "name", nullable = false, length=200)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "certification_vendor_id")
	public CertificationVendor getCertificationVendor() {
		return certificationVendor;
	}

	public void setCertificationVendor(CertificationVendor certificationVendor) {
		this.certificationVendor = certificationVendor;
	}

	@Column(name="last_activity_on")
	public Calendar getLastActivityOn() {
		return lastActivityOn;
	}

	public void setLastActivityOn(Calendar lastActivityOn) {
		this.lastActivityOn = lastActivityOn;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("name", name).toString();
	}
}
