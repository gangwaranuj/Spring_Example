package com.workmarket.domains.model.tax;

import java.math.BigDecimal;

/**
 * User: iloveopt
 * Date: 1/13/14
 */
public class NewEarningReport {

	private EarningReport earningReport;
	BigDecimal nonVorEarning = BigDecimal.ZERO;
	BigDecimal nonVorExpenses = BigDecimal.ZERO;

	public EarningReport getEarningReport() {
		return earningReport;
	}

	public void setEarningReport(EarningReport earningReport) {
		this.earningReport = earningReport;
	}

	public BigDecimal getNonVorEarning() {
		return nonVorEarning;
	}

	public void setNonVorEarning(BigDecimal nonVorEarning) {
		this.nonVorEarning = nonVorEarning;
	}

	public BigDecimal getNonVorExpenses() {
		return nonVorExpenses;
	}

	public void setNonVorExpenses(BigDecimal nonVorExpenses) {
		this.nonVorExpenses = nonVorExpenses;
	}



}
