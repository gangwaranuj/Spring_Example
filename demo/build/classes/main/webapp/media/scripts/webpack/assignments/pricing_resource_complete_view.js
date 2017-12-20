'use strict';

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import formatCurrency from '../funcs/formatCurrency';
import wmMaskInput from '../funcs/wmMaskInput';
import roundDecimals from '../helpers/roundDecimals.js';

export default Backbone.View.extend({
	el: '#pricing_complete',
	events: {
		'click #collect_tax'                    : 'toggleCollectTaxes',
		'keyup input[name=tax_percent]'         : 'calculateTaxes',
		'keyup input[name=override_price]'      : 'calculateTaxes',
		'keyup input[name=hours]'               : 'render',
		'keyup input[name=minutes]'             : 'render',
		'keyup input[name=units]'               : 'render',
		'keyup input[name=additional_expenses]' : 'checkAdditionalExpensesMaxValue',
		'click .edit_pricing_earnings_outlet'   : 'toggleOverride'
	},

	initialize: function () {
		_.bindAll(this, 'render');
		this.taxField = this.$('input[name=tax_percent]');
		this.taxConfigurationPanel = this.$('#collect_tax_section');
		this.taxCollectedDisplay = this.$('.tax_to_report');
		this.taxDiffDisplay = this.$('.tax_to_diff');

		this.hoursField = this.$('input[name=hours]');
		wmMaskInput({
			selector: 'input[name=hours]',
			root: this.el
		}, '099');
		this.minutesField = this.$('input[name=minutes]');
		wmMaskInput({
			selector: 'input[name=minutes]',
			root: this.el
		}, '09');

		this.unitsField = this.$('input[name=units]');
		this.additionalExpensesField = this.$('input[name=additional_expenses]');
		this.additionalExpensesMaxValue = this.$('#additional_expenses_maxvalue');
		this.overridePriceField = this.$('[name="override_price"]');

		this.bonus = this.$('input[name=bonus]');

		this.pricingEarningsDisplay = this.$('.pricing_earnings_outlet');
		this.pricingEarningsToggle = this.$('.edit_pricing_earnings_outlet');

		this.showOverride = false;
	},

	render: function () {
		var price = this.calculatePrice();
		var expenses = this.additionalExpensesField.val();

		if (this.hoursField.val() > 0 && !(this.minutesField.val())) {
			this.minutesField.val(0); // autofill the other field with zero
		}
		if (this.minutesField.val() > 0 && !(this.hoursField.val())) {
			this.hoursField.val(0);
		}
		if (this.hoursField.val()) {
			this.hoursField.val(parseInt(this.hoursField.val(), 10)); // strip leading zeroes
		}
		if (this.minutesField.val()) {
			this.minutesField.val(parseInt(this.minutesField.val(), 10));
		}

		this.pricingEarningsDisplay.find('.read-only-price').html(formatCurrency(price));
		this.pricingEarningsToggle.html(this.showOverride ? 'cancel' : 'adjust');
		this.pricingEarningsDisplay.find('.read-only-price').toggle(!this.showOverride);
		this.pricingEarningsDisplay.find('.override-price').toggle(this.showOverride);

		if (expenses) {
			this.additionalExpensesField.val(expenses.replace(/[^0-9\.]+/g, ''));
		}
		this.pricingEarningsDisplay.toggleClass('red', parseFloat(price.toFixed(2)) > parseFloat(this.model.maxSpendLimit.toFixed(2)));

		this.calculateTaxes();

		return this;
	},

	checkAdditionalExpensesMaxValue: function () {
		var additional = parseFloat(this.additionalExpensesField.val() || 0);
		var additionalMax = parseFloat(this.additionalExpensesMaxValue.val() || 0);

		if (additional > additionalMax) {
			this.additionalExpensesField.val(additionalMax);
			this.$('input[name=additional_expenses]').val(additionalMax);
			this.$('#additional_expenses_maxvalue_error_txt').html('Cannot exceed the approved increase of $' + additionalMax.toFixed(2)).show();
		}
		else {
			this.$('#additional_expenses_maxvalue_error_txt').html('').hide();
		}

		this.render();
	},

	calculatePrice: function () {
		var additional = parseFloat(this.additionalExpensesField.val() || 0);
		additional += parseFloat(this.bonus.val() || 0);
		if (this.model.id === 1) {
			return additional + this.model.flatPrice;
		} else if (this.model.id === 2) {
			return additional + this.model.perHourPrice * (parseFloat(this.hoursField.val() || 0) + parseFloat((this.minutesField.val() || 0) / 60));
		} else if (this.model.id === 3) {
			var unitPrice = this.model.perUnitPrice * parseFloat(this.unitsField.val() || 0);
			// We support 3 decimal places in per unit price so we need to accurately round to 2 decimal places
			// http://stackoverflow.com/questions/11832914/round-to-at-most-2-decimal-places-in-javascript
			return additional + roundDecimals(unitPrice, 2);
		} else if (this.model.id === 4) {
			var hours = parseFloat(this.hoursField.val() || 0) + parseFloat((this.minutesField.val() || 0) / 60);
			if (hours <= this.model.initialNumberOfHours) {
				return additional + this.model.initialPerHourPrice * hours;
			}
			return additional + this.model.initialPerHourPrice * this.model.initialNumberOfHours + this.model.additionalPerHourPrice * (hours - this.model.initialNumberOfHours);
		}
		return 0.0;
	},

	calculateTaxes: function () {
		var val = this.taxField.val();
		var percent = parseFloat(val) / 100.00;

		var price = (this.showOverride) ? $('input', this.pricingEarningsDisplay).val() : this.calculatePrice();

		this.taxCollectedDisplay.html(formatCurrency(price * percent));
		this.taxDiffDisplay.html(formatCurrency(price * ( 1- percent) ));

	},

	toggleCollectTaxes: function () {
		this.taxConfigurationPanel.toggle();
	},

	toggleOverride: function () {
		this.showOverride = !this.showOverride;
		this.render();
	}
});
