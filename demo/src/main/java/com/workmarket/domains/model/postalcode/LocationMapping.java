package com.workmarket.domains.model.postalcode;

import com.workmarket.domains.model.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Author: rocio
 */
@Entity(name = "locationMapping")
@Table(name = "location_mapping_simple")
public class LocationMapping extends AbstractEntity {

	private static final long serialVersionUID = -5838316078164563472L;
	private String postalCode;
	private String cbsaName;
	private String msaName;
	private String pmsaName;

	@Column(name = "cbsa_name")
	public String getCbsaName() {
		return cbsaName;
	}

	public void setCbsaName(String cbsaName) {
		this.cbsaName = cbsaName;
	}

	@Column(name = "msa_name")
	public String getMsaName() {
		return msaName;
	}

	public void setMsaName(String msaName) {
		this.msaName = msaName;
	}

	@Column(name = "pmsa_name")
	public String getPmsaName() {
		return pmsaName;
	}

	public void setPmsaName(String pmsaName) {
		this.pmsaName = pmsaName;
	}

	@Column(name = "postal_code")
	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
}
