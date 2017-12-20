package com.workmarket.dto;

import com.google.common.collect.Maps;

import java.math.BigDecimal;
import java.util.Map;

public class AggregatesDTO {

	Map<String,Integer> counts = Maps.newLinkedHashMap();
	Map<String,BigDecimal> totals = Maps.newLinkedHashMap();

	public Integer getCountForStatus(String status) {
		return (counts.containsKey(status)) ? counts.get(status) : 0;
	}
	public void setCountForStatus(String status, Integer count) {
		counts.put(status, count);
	}

	public Map<String,Integer> getCounts() {
		return counts;
	}

	public Integer getTotalCount() {
		int total = 0;
		for (String s: counts.keySet()) {
			total += counts.get(s);
		}
		return total;
	}

	public void addToStatusCount(String status, Integer count) {
		if (counts.containsKey(status)) {
			setCountForStatus(status, counts.get(status) + count);
		}
		else {
			setCountForStatus(status, count);
		}
	}

	//TOTALS
	public BigDecimal getTotalForStatus(String status) {
		return (totals.containsKey(status)) ? totals.get(status) : BigDecimal.ZERO;
	}
	public void setTotalForStatus(String status, BigDecimal total) {
		totals.put(status, total);
	}

	public Map<String,BigDecimal> getTotals() {
		return totals;
	}

}
