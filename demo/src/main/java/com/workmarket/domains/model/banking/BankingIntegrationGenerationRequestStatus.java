package com.workmarket.domains.model.banking;

import com.workmarket.domains.model.LookupEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity(name = "banking_integration_generation_request_status")
@Table(name = "banking_integration_generation_request_status")
public class BankingIntegrationGenerationRequestStatus extends LookupEntity {

	private static final long serialVersionUID = 1L;

	public static String SUBMITTED = "submitted";
	public static String COMPLETE = "complete";
	public static String ERROR = "error";

	public BankingIntegrationGenerationRequestStatus() {
	}

	public BankingIntegrationGenerationRequestStatus(String code) {
		super(code);
	}

	@Transient
	public boolean isSubmitted() {
		return SUBMITTED.equals(this.code);
	}

	@Transient
	public boolean isCompleted() {
		return COMPLETE.equals(this.code);
	}

	@Transient
	public boolean isError() {
		return ERROR.equals(this.code);
	}

}
