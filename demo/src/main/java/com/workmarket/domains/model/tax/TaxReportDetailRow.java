package com.workmarket.domains.model.tax;

/**
 * User: iloveopt
 * Date: 1/6/14
 */
public class TaxReportDetailRow extends TaxReportRow {

	private Long buyerCompanyId;
	private String buyerCompanyName;
	private boolean useWMTaxEntity;

	public Long getBuyerCompanyId() {
		return buyerCompanyId;
	}

	public void setBuyerCompanyId(Long buyerCompanyId) {
		this.buyerCompanyId = buyerCompanyId;
	}

	public String getBuyerCompanyName() {
		return buyerCompanyName;
	}

	public void setBuyerCompanyName(String buyerCompanyName) {
		this.buyerCompanyName = buyerCompanyName;
	}

	public boolean isUseWMTaxEntity() {
		return useWMTaxEntity;
	}

	public void setUseWMTaxEntity(boolean useWMTaxEntity) {
		this.useWMTaxEntity = useWMTaxEntity;
	}

}
