package com.workmarket.web.forms;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RatesLocationsForm implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigDecimal minOnsiteHourlyRate;
	private BigDecimal minOnsiteWorkPrice;
	private BigDecimal minOffsiteHourlyRate;
	private BigDecimal minOffsiteWorkPrice;
	private BigDecimal maxTravelDistance;
	
	private Map<Long,Boolean> currentLocationTypes;

	public BigDecimal getMinOnsiteHourlyRate() {
		return minOnsiteHourlyRate;
	}

	public void setMinOnsiteHourlyRate(BigDecimal minOnsiteHourlyRate) {
		this.minOnsiteHourlyRate = minOnsiteHourlyRate;
	}

	public BigDecimal getMinOnsiteWorkPrice() {
		return minOnsiteWorkPrice;
	}

	public void setMinOnsiteWorkPrice(BigDecimal minOnsiteWorkPrice) {
		this.minOnsiteWorkPrice = minOnsiteWorkPrice;
	}

	public BigDecimal getMinOffsiteHourlyRate() {
		return minOffsiteHourlyRate;
	}

	public void setMinOffsiteHourlyRate(BigDecimal minOffsiteHourlyRate) {
		this.minOffsiteHourlyRate = minOffsiteHourlyRate;
	}

	public BigDecimal getMinOffsiteWorkPrice() {
		return minOffsiteWorkPrice;
	}

	public void setMinOffsiteWorkPrice(BigDecimal minOffsiteWorkPrice) {
		this.minOffsiteWorkPrice = minOffsiteWorkPrice;
	}

	public BigDecimal getMaxTravelDistance() {
		return maxTravelDistance;
	}

	public void setMaxTravelDistance(BigDecimal maxTravelDistance) {
		this.maxTravelDistance = maxTravelDistance;
	}

	public Map<Long, Boolean> getCurrentLocationTypes() {
		return currentLocationTypes;
	}

	public void setCurrentLocationTypes(Map<Long, Boolean> currentLocationTypes) {
		this.currentLocationTypes = currentLocationTypes;
	}

	public List<Long> getSelectedCurrentLocationTypes() {
		if (currentLocationTypes == null) {
			return Collections.emptyList();
		}

		List<Long> locationTypeIds = new LinkedList<Long>();

		for (Map.Entry<Long,Boolean> entry : currentLocationTypes.entrySet()) {
			if (entry.getValue()) {
				locationTypeIds.add(entry.getKey()); // copy selected location type ids
			}
		}

		return locationTypeIds;
	}
}
