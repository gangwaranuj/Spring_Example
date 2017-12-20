package com.workmarket.domains.model.settings;


import com.workmarket.domains.model.ManageMyWorkMarket;

public class AssignmentSettingsCompletenessPredicate implements CompletenessPredicate<ManageMyWorkMarket> {

	@Override
	public boolean test(final ManageMyWorkMarket manageMyWorkMarket) {
		return manageMyWorkMarket != null;
	}
}
