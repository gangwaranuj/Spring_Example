import $ from 'jquery';
import 'jquery-ui';
import '../dependencies/jquery.calendrical';
import roundDecimals from '../helpers/roundDecimals.js'

$.fn.wmAssignmentPricing = function (configuration) {
	this.configuration = $.extend(true, {
		'modal': true
	}, configuration);
	var number_value = function(field, container) {
		return parseFloat($(field, container).val()) || 0.00;
	};
	var set_spend_max = function(container, value) {
		$('.spend_max', container).html(value);
		$('[name=spend_max]').val(value);
	};
	var set_hourly_max_spend = function() {
		var container = $(this).closest('.pricing_configuration');
		set_spend_max(container, (number_value('input[name="per_hour_price"]', container) * number_value('input[name="max_number_of_hours"]', container)).toFixed(2));
	};
	var set_units_max_spend = function() {
		var container = $(this).closest('.pricing_configuration');
		var total = number_value('input[name="per_unit_price"]', container) * number_value('input[name="max_number_of_units"]', container)
		set_spend_max(container, roundDecimals(total, 2));
	};
	var set_blended_max_spend = function() {
		var container = $(this).closest('.pricing_configuration');
		set_spend_max(container, (number_value('input[name="initial_per_hour_price"]', container) * number_value('input[name="initial_number_of_hours"]', container) + number_value('input[name="additional_per_hour_price"]', container) * number_value('input[name="max_blended_number_of_hours"]', container)).toFixed(2));
	};
	var toggle_pricing_select = function (el) {
		var radio_value = el.val();
		var container = el.closest('.pricing_configuration');
		if (radio_value == 1) { // Model_Work::FLAT_PRICING_STRATEGY
			$('.pricing_flat', container).show().siblings().hide();
		} else if (radio_value == 2) { // Model_Work::PER_HOUR_PRICING_STRATEGY
			$('.pricing_hourly', container).show().siblings().hide();
		} else if (radio_value == 3) { // Model_Work::PER_UNIT_PRICING_STRATEGY
			$('.pricing_units', container).show().siblings().hide();
		} else if (radio_value == 4) { // Model_Work::BLENDED_PER_HOUR_PRICING_STRATEGY
			$('.pricing_blended', container).show().siblings().hide();
		} else if (radio_value == 5) { // Model_Work::BLENDED_PER_UNIT_PRICING_STRATEGY
			// Not implemented yet.
		} else if (radio_value == 7) { // Model_Work::INTERNAL_ONLY
			$('.pricing_internal', container).show().siblings().hide();
		}
	};
	return this.each(function () {
		var self = this;
		$('input[name=pricing]', self).click(function() {
			toggle_pricing_select($(this));
		});
		toggle_pricing_select($('input[name=pricing]:checked', self));
		$('input[name="per_hour_price"]', self).keyup(set_hourly_max_spend);
		$('input[name="max_number_of_hours"]', self).keyup(set_hourly_max_spend);
		$('input[name="per_unit_price"]', self).keyup(set_units_max_spend);
		$('input[name="max_number_of_units"]', self).keyup(set_units_max_spend);
		$('input[name="initial_per_hour_price"]', self).keyup(set_blended_max_spend);
		$('input[name="initial_number_of_hours"]', self).keyup(set_blended_max_spend);
		$('input[name="additional_per_hour_price"]', self).keyup(set_blended_max_spend);
		$('input[name="max_blended_number_of_hours"]', self).keyup(set_blended_max_spend);

		// prevent user from entering 'per unit' price with more than allowed number of decimal places
		const MAX_DECIMALS = 3;
		// http://stackoverflow.com/questions/15680506/how-to-prevent-keypress-two-digits-after-a-decimal-number
		$('input[name="per_unit_price').on("input", function (e) {
			// http://stackoverflow.com/questions/4912788/truncate-not-round-off-decimal-numbers-in-javascript
			var re = new RegExp("(\\d+\\.\\d{" + MAX_DECIMALS + "})(\\d)"),
				m = this.value.toString().match(re);
			if(m) {
				$(this).val(m[1]);
			}
		});
		return this;
	});
};

$.fn.wmAssignmentScheduling = function(config) {
	this.config = $.extend(true, {
		'modal': true
	}, config);
	return this.each(function(index) {
		var self = this;
		$('input[name="from"], input[name="to"]', self).datepicker({
			dateFormat: 'mm/dd/yy'
		});
		$('input[name="fromtime"], input[name="totime"]', self).calendricalTimeRange({
			startDate: $('input[name="from"]', self),
			endDate: $('input[name="to"]', self),
			defaultTime: {hour: 8, minute: 0}
		});
		return this;
	});
};
