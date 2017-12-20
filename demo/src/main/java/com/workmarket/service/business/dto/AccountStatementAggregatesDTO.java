package com.workmarket.service.business.dto;

import com.workmarket.dto.AggregatesDTO;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class AccountStatementAggregatesDTO extends AggregatesDTO {

	private Map<String, BigDecimal> amountTotals = new HashMap<>();

	public BigDecimal getAmountTotalForStatus(String status) {
		return (amountTotals.containsKey(status)) ? amountTotals.get(status) : BigDecimal.ZERO;
	}

	public void setAmountTotalForStatus(String status, BigDecimal amount) {
		amountTotals.put(status, amount);
	}

	public Map<String, BigDecimal> getAmountTotals() {
		return amountTotals;
	}

	public BigDecimal getAmountTotal() {
		BigDecimal total = BigDecimal.ZERO;
		for (String s : amountTotals.keySet()) {
			total = total.add(amountTotals.get(s));
		}
		return total;
	}
}
