package com.workmarket.service.business.event;

import java.util.List;

public class MigrateTaxEntitiesEvent extends Event {
	private final List<Long> taxEntityIds;
	private final boolean saveVaultedValues;
	private final boolean saveDuplicateTins;

	public MigrateTaxEntitiesEvent(List<Long> taxEntityIds, boolean saveVaultedValues, boolean saveDuplicateTins) {
		this.taxEntityIds = taxEntityIds;
		this.saveVaultedValues = saveVaultedValues;
		this.saveDuplicateTins = saveDuplicateTins;
	}

	public boolean isSaveVaultedValues() {
		return saveVaultedValues;
	}

	public boolean isSaveDuplicateTins() {
		return saveDuplicateTins;
	}

	public List<Long> getTaxEntityIds() {
		return taxEntityIds;
	}
}
