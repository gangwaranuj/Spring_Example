package com.workmarket.domains.model.certification;

import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.VerifiableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.*;
import java.util.Calendar;


@Entity(name = "certificationVendor")
@Table(name = "certification_vendor")
@NamedQueries({
})
@AuditChanges
public class CertificationVendor extends VerifiableEntity {

	private static final long serialVersionUID = 1L;

	private String name;
	private Industry certificationType;
	private CertificationVendorInstruction instruction = CertificationVendorInstruction.DEFAULT;
	private Boolean certificationNumberRequired = Boolean.FALSE;
	private Calendar lastActivityOn;

	public CertificationVendor() {
	}

	public CertificationVendor(String name) {
		this.name = name;
	}

	@Column(name = "name", nullable = false, length = 200)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "certification_type_id")
	public Industry getCertificationType() {
		return certificationType;
	}

	public void setCertificationType(Industry certificationType) {
		this.certificationType = certificationType;
	}

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "vendor_instruction_id")
	public CertificationVendorInstruction getInstruction() {
		return instruction;
	}

	public void setInstruction(CertificationVendorInstruction instruction) {
		this.instruction = instruction;
	}

	@Column(name = "cert_number_required")
	public Boolean isCertificationNumberRequired() {
		return certificationNumberRequired;
	}

	public void setCertificationNumberRequired(Boolean certificationNumberRequired) {
		this.certificationNumberRequired = certificationNumberRequired;
	}

	@Column(name = "last_activity_on")
	public Calendar getLastActivityOn() {
		return lastActivityOn;
	}

	public void setLastActivityOn(Calendar lastActivityOn) {
		this.lastActivityOn = lastActivityOn;
	}
}
