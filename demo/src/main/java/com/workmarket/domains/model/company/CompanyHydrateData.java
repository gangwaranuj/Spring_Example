package com.workmarket.domains.model.company;

/**
 * Author: rocio
 */
public class CompanyHydrateData {

	private Long id;
	private String name;
	private String companyStatusType;
	private boolean confirmedBankAccount;
	private boolean approvedTIN;

	public boolean isApprovedTIN() {
		return approvedTIN;
	}

	public void setApprovedTIN(boolean approvedTIN) {
		this.approvedTIN = approvedTIN;
	}

	public String getCompanyStatusType() {
		return companyStatusType;
	}

	public void setCompanyStatusType(String companyStatusType) {
		this.companyStatusType = companyStatusType;
	}

	public boolean isConfirmedBankAccount() {
		return confirmedBankAccount;
	}

	public void setConfirmedBankAccount(boolean confirmedBankAccount) {
		this.confirmedBankAccount = confirmedBankAccount;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CompanyHydrateData)) return false;

		CompanyHydrateData that = (CompanyHydrateData) o;

		if (approvedTIN != that.approvedTIN) return false;
		if (confirmedBankAccount != that.confirmedBankAccount) return false;
		if (companyStatusType != null ? !companyStatusType.equals(that.companyStatusType) : that.companyStatusType != null)
			return false;
		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (companyStatusType != null ? companyStatusType.hashCode() : 0);
		result = 31 * result + (confirmedBankAccount ? 1 : 0);
		result = 31 * result + (approvedTIN ? 1 : 0);
		return result;
	}
}
