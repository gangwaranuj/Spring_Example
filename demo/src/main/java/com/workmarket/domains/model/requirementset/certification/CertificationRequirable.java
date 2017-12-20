package com.workmarket.domains.model.requirementset.certification;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.certification.CertificationVendor;
import com.workmarket.domains.model.requirementset.Requirable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "certification")
public class CertificationRequirable extends AbstractEntity implements Requirable {

	private String name;
	private CertificationVendor certificationVendor;

	@Override
	@Column(name="name", insertable = false, updatable = false)
	public String getName() {
		return certificationVendor.getName() + " - " + name;
	}

	@Override
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
}
