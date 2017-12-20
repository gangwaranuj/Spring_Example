package com.workmarket.domains.model.tax;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerifiableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.*;
import java.util.Calendar;
import java.util.List;

/**
 * Created by nick on 11/29/12 4:14 PM
 */

@Entity(name = "tax_verification_request")
@Table(name = "tax_verification_request")
@AuditChanges
public class TaxVerificationRequest extends VerifiableEntity {

	public static final String TAX_TYPE_TIN = "tin";

	private List<? extends AbstractTaxEntity> taxEntities;

	private Calendar requestDate;

	private User requestor;

	private String confirmationNumber;

	public TaxVerificationRequest() {}

	private Integer tinCount;

	public TaxVerificationRequest(List<? extends AbstractTaxEntity> taxEntities, Calendar requestDate, User requestor) {
		this.taxEntities = taxEntities;
		this.requestDate = requestDate;
		this.requestor = requestor;
		if (taxEntities != null)
			tinCount = taxEntities.size();
	}

	@ManyToMany(fetch = FetchType.LAZY, targetEntity = AbstractTaxEntity.class)
	@JoinTable(name = "tax_verification_request_association",
			joinColumns = { @JoinColumn(name = "tax_verification_request_id") },
			inverseJoinColumns = { @JoinColumn(name = "tax_entity_id") })
	public List<? extends AbstractTaxEntity> getTaxEntities() {
		return taxEntities;
	}

	public void setTaxEntities(List<? extends AbstractTaxEntity> taxEntities) {
		this.taxEntities = taxEntities;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "requestor_id", referencedColumnName = "id", nullable = false, updatable = false)
	public User getRequestor() {
		return requestor;
	}

	public void setRequestor(User requestor) {
		this.requestor = requestor;
	}

	@Column(name = "request_date", nullable = false)
	public Calendar getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Calendar requestDate) {
		this.requestDate = requestDate;
	}

	@Column(name = "confirmation_number", nullable = true)
	public String getConfirmationNumber() {
		return confirmationNumber;
	}

	public void setConfirmationNumber(String confirmationNumber) {
		this.confirmationNumber = confirmationNumber;
	}

	@Transient
	public Integer getTinCount() {
		return tinCount;
	}
}
