'use strict';

import $ from 'jquery';
import Backbone from 'backbone';

export default Backbone.View.extend({
	setHeader: function () {
		var linkDiv = $('.page-header-link');
		if (this.isOnSignPage()) {
			$('.page-header h2').html('Tax Information - Confirm & Sign');
			linkDiv.empty();

		} else if (this.isOnEditPage()) {
			$('.page-header h2').html('Tax Information - Edit');
			linkDiv.empty();

		} else if (this.isOnViewPage() && !this.options.is_masquerading) {
			$('.page-header h2').html('Tax Information');
			var button = '<button id="edit-tax-btn" class="pull-right button -small">Edit Tax Info</button>';
			linkDiv.append(button);
		}
	},

	hasData: function () {
		return !($.isEmptyObject(this.options.tax_entities) || $.isEmptyObject(this.options.active_tax_entity));
	},

	isOnSignPage: function () {
		return this.options.router.currentView === 'sign';
	},

	isOnEditPage: function () {
		return this.options.router.currentView === 'edit';
	},

	isOnViewPage: function () {
		return this.options.router.currentView === 'view';
	},

	hideAllForms: function () {
		$('#tax-edit-form').empty();
		$('#tax-view-form').empty();
		$('#tax-form').empty();
		$('#tax-sign-form').empty();
	},

	// fill up with null entries so tmpl doesn't puke
	padFields: function (model) {
		var result = {
			'business_flag': null,
			'tax_name': null,
			'tax_number': null,
			'formatted_tax_number': null,
			'tax_country': null,
			'tax_entity_type_code': null,
			'address': null,
			'city': null,
			'state': null,
			'country': null,
			'postal_code': null,
			'business_name_flag': null,
			'llc_type': null,
			'effective_date_string': null,
			'signature': null,
			'signature_date_string': null,
			'country_of_incorporation': null,
			'foreign_status_accepted_flag': null,
			'tax_verification_status_code': null,
			'delivery_policy_flag': null
		};
		$.extend(result, model);

		return result;
	}
});
