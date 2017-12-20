package com.workmarket.web.forms.work;

import com.workmarket.domains.model.pricing.FlatPricePricingStrategy;

/**
 * User: micah
 * Date: 7/3/13
 * Time: 10:14 AM
 */
public class WorkBundleForm extends WorkForm {
	public WorkBundleForm() {
		setPricing(new FlatPricePricingStrategy().getId());
		setFlat_price(0.001);
		setCheck_in(false);
		setIvr_active(false);
		setPricing_mode("");
		setBadge_included_on_printout(false);
		setBadge_show_client_name(false);
		setDisablePriceNegotiation(true);
		setCheck_in_call_required(false);
		setCheck_out_notes_requiredness("");
		setShow_check_out_notes(false);
		setRequire_timetracking(false);
		setResource_confirmation(false);
		setClientlocations(WorkForm.CLIENT_LOCATION_OFFSITE);
	}
}
