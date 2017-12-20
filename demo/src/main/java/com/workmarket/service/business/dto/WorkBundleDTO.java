package com.workmarket.service.business.dto;

import com.workmarket.domains.model.pricing.FlatPricePricingStrategy;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.utility.BeanUtilities;

import java.util.List;

public class WorkBundleDTO extends WorkDTO {

	private static final long serialVersionUID = -4195200625328765353L;

	private List<String> workNumbers;
	private long workCount;

	public WorkBundleDTO() {
		// set defaults
		// pricing
		setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		setFlatPrice(0.001);

		//location
		setIsOnsiteAddress(Boolean.FALSE);

		//disable negotiation
		setDisablePriceNegotiation(true);

		setAssignToFirstResource(Boolean.FALSE);
		setShowInFeed(Boolean.FALSE);
	}

	public static WorkBundleDTO fromWorkBundle(WorkBundle workBundle) {
		WorkBundleDTO workBundleDTO = new WorkBundleDTO();
		BeanUtilities.copyProperties(workBundleDTO, new WorkDTO(workBundle));
		return workBundleDTO;
	}

	public List<String> getWorkNumbers() {
		return workNumbers;
	}

	public void setWorkNumbers(List<String> workNumbers) {
		this.workNumbers = workNumbers;
	}

	public long getWorkCount() {
		return workCount;
	}

	public void setWorkCount(long workCount) {
		this.workCount = workCount;
	}
}
