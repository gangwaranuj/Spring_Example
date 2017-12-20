package com.workmarket.domains.model.tax;

import com.workmarket.domains.model.LookupEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name="taxEntityStatusType")
@Table(name="tax_entity_status_type")
public class TaxVerificationStatusType extends LookupEntity {

	private static final long serialVersionUID = 1L;

	public static String UNVERIFIED = "unverified";
	public static String APPROVED = "approved";
	public static String INVALID_TIN_FORMAT = "invalid_tin_format";
	public static String INVALID_REQUEST = "invalid_request";
	public static String NOT_ISSUED = "not_issued";
	public static String NOT_MATCHED = "not_matched";
	public static String DUPLICATE_TIN = "duplicate_tin";
	public static String WRONG_TYPE_SSN = "wrong_type_ssn";
	public static String WRONG_TYPE_EIN = "wrong_type_ein";
	public static String WRONG_TYPE_BOTH = "wrong_type_both";
	public static String VALIDATED = "validated";
	public static String SIGNED_FORM_W8 = "signed_form_w8";

	public static TaxVerificationStatusType newInstance(String code) {
		return new TaxVerificationStatusType(code);
	}

	public TaxVerificationStatusType() {}
	public TaxVerificationStatusType(String code){
		super(code);
	}

	@Transient
	public boolean isUnverified() {
		return getCode().equals(UNVERIFIED);
	}

	@Transient
	public boolean isApproved() {
		return getCode().equals(APPROVED);
	}

	@Transient
	public boolean isRejected() {
		return !(getCode().equals(APPROVED) || getCode().equals(UNVERIFIED) ||
			getCode().equals(VALIDATED) || getCode().equals(SIGNED_FORM_W8));
	}
	/***
	 * Returns "Verified", "Unverified", "or "Rejected"
	 * @return
	 */
	@Transient
	public String getDisplayableStatus() {
		if (isUnverified()) return "Unverified";
		if (isApproved()) return "Verified";
		else return "Rejected";
	}
}
