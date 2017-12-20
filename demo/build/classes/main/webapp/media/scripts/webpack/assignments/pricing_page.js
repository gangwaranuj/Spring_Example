import $ from 'jquery';
import _ from 'underscore';
import roundDecimals from '../helpers/roundDecimals.js';

export default function (options) {
	var assignmentsPricing,
		pricingFilters = {},
		selectedPrice,
		$pricing = $('#pricing'),
		$pricingFlatFee = $('#pricing-flat-fee'),
		$pricingPerHour = $('#pricing-per-hour'),
		$pricingPerUnit = $('#pricing-per-unit'),
		$pricingPerBlendedHour = $('#pricing-blended-per-hour'),
		$pricingInternal = $('#pricing-internal');

	function get_as_num(num) {
		num = num.toString().replace(/\$|\,/g, '');
		if (isNaN(num))
			num = 0;
		else
			num = parseFloat(num);
		return num;
	}

	if (options.isPricingMode) {
		pricingFilters.mode = options.pricingMode;
	}

	pricingFilters.wm_fee = get_as_num(options.wmFee);
	pricingFilters.pricing_type = get_as_num(options.pricingType);


	var AssignmentPricing = function (data) {
		var self = this;

		function get_as_num(num) {
			num = num.toString().replace(/\$|\,/g, '');
			if (isNaN(num)) {
				num = 0;
			} else {
				num = parseFloat(num);
			}

			return num;
		}

		// Class variables.
		this.data = data || {
				'wm_fee': 0.10,
				'wm_max_fee': 400.0,
				'mode':'spend', // spend | pay
				'mode_trigger_text':{
					'spend':'Fee structure: Paid by worker<br/> <small class="meta">Switch calculator to allocate fees to your account.</small>',
					'pay':'Fee structure: Paid by you<br/> <small class="meta">Switch calculator to allocate fees to the worker.</small>'
				},
				'mode_text':{
					'spend':'I want to spend',
					'pay':'Pay the resource'
				},
				'instructions':{
					'spend':'Enter the amount you want to spend. The calculator to the right will show your cost and how much the resource earns.',
					'pay':'Enter the amount you want to pay the resource. The calculator to the right will show how much the resource earns and your cost.'
				},
				'pricing_type':0
			};

		this.loadData = function (options) {
			if (options) {
				_.extend(this.data, options);
			}
			// Sanitize wm_fee
			if (this.data.wm_fee < 0) {
				this.data.wm_fee = 0.00;
			}
			// Sanitize mode
			if (this.data.mode != 'spend') {
				this.data.mode = 'pay';
			}
		};

		this.initialize = function () {
			$('.pricing_switch_trigger').bind('click', function (e) {
				e.preventDefault();
				self.switch_mode($('#pricing_mode').val());
			});
			self.set_mode_text();

			$('ul.pricing li a').click(function (e) {
				$('#pricing').val($(e).data('pricing'));
				$('#pricing_summary_outlet').show();
				$('#pricing-options').show();
			});
			$('#pricing-internal').click(function () {
				$('#pricing_summary_outlet').hide();
				$('#pricing-options').hide();
			});

			$('#flat_price').keyup(function () {
				self.set_max_spend()
			});

			$('#per_hour_price').keyup(function () {
				self.set_hourly_max_spend()
			});
			$('#max_number_of_hours').keyup(function () {
				self.set_hourly_max_spend()
			});

			// prevent user from entering 'per unit' price with more than allowed number of decimal places
			var MAX_DECIMALS = 3;
			$('#per_unit_price').on("input", function () {
				// http://stackoverflow.com/questions/4912788/truncate-not-round-off-decimal-numbers-in-javascript
				var re = new RegExp("(\\d+\\.\\d{" + MAX_DECIMALS + "})(\\d)"),
					m = this.value.toString().match(re);
				if(m) {
					$(this).val(m[1]);
				}
				self.set_units_max_spend()
			});
			$('#max_number_of_units').keyup(function () {
				self.set_units_max_spend()
			});

			$('#initial_per_hour_price').keyup(function () {
				self.set_blended_max_spend()
			});
			$('#initial_number_of_hours').keyup(function () {
				self.set_blended_max_spend()
			});
			$('#additional_per_hour_price').keyup(function () {
				self.set_blended_max_spend()
			});
			$('#max_blended_number_of_hours').keyup(function () {
				self.set_blended_max_spend()
			});
			if (this.data.wm_fee > 0){
				$('.transaction-fee-col').show();
			} else {
				$('.transaction-fee-col').hide();

			}
		};

		this.set_mode_text = function () {
			var text = this.data.mode_text[this.data.mode];
			$("#price-container .pricing_mode").each(function () {
				$(this).html(text);
			});

			text = this.data.mode_trigger_text[this.data.mode];
			$("#price-container .pricing_switch_text").each(function () {
				$(this).html(text);
			});

			text = this.data.instructions[this.data.mode];
			$("#price-container .pricing_spend_blurb").each(function () {
				$(this).html(text);
			});

			// Update the hidden field.
			if (this.data.pricing_type == 0) {
				$('#pricing_mode').val(this.data.mode);
			} else if (this.data.pricing_type == 1) {
				$('#pricing_mode').val('spend');
			} else {
				$('#pricing_mode').val('pay');
			}
		};

		this.formatNumber = function(number) {
			return roundDecimals(number, 2).toString().replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,");
		};

		this.switch_mode = function (mode) {
			if (mode == 'pay') {
				this.data.mode = 'spend';
			}
			else {
				this.data.mode = 'pay';
			}

			self.set_mode_text();

			$('ul.pricing li.active').find('a').trigger('click');
		};

		this.set_internal_spend = function () {
			$('#you-pay').html(0.00);
			$('#resource-earns').html(0.00);
			$('.transaction-fee').html(0.00);
			$('.total').html(0.00);
		};

		this.number_value = function (field) {
			var num = $(field).val();
			num = num.toString().replace(/\$|\,/g, '');
			return parseFloat(num) || 0.00;
		};

		this.set_max_spend = function () {
			var cost = 0;
			var earn = 0;
			var fee = 0;
			var h = get_as_num(self.number_value('#flat_price'));
			fee = h * this.data.wm_fee;
			// Cap the fee at this.data.wm_max_fee.
			if (fee > this.data.wm_max_fee) {
				fee = this.data.wm_max_fee;
			}
			this.set_values(fee, h);

			if (this.data.mode == 'spend') {
				earn = h / (1 + this.data.wm_fee);
				cost = h;
				// Cap the fee at this.data.wm_max_fee.
				if ((cost - earn) > this.data.wm_max_fee) {
					earn = cost - this.data.wm_max_fee;
				}
			} else {
				cost = h + fee;
				earn = h;
			}

			$('#you-pay').html(this.formatNumber(cost));
			$('#resource-earns').html(this.formatNumber(earn));
		};

		this.set_hourly_max_spend = function () {
			var cost = 0;
			var earn = 0;
			var h = get_as_num(self.number_value('#per_hour_price')) * get_as_num(self.number_value('#max_number_of_hours'));
			var fee = 0;
			fee = h * this.data.wm_fee;
			// Cap the fee at this.data.wm_max_fee.
			if (fee > this.data.wm_max_fee) {
				fee = this.data.wm_max_fee;
			}
			this.set_values(fee, h);

			if (this.data.mode == 'spend') {
				earn = h / (1 + this.data.wm_fee);
				cost = h;
				// Cap the fee at this.data.wm_max_fee.
				if ((cost - earn) > this.data.wm_max_fee) {
					earn = cost - this.data.wm_max_fee;
				}
			}
			else {
				cost = h + fee;
				earn = h;
			}

			$('#you-pay').html(this.formatNumber(cost));
			$('#resource-earns').html(this.formatNumber(earn));
		};

		this.set_values = function (fee, cost) {
			var total = 0;
			if (this.data.pricing_type==1 || this.data.pricing_type==0){
				total=cost;
			} else if (this.data.pricing_type == 2) {
				total=cost+fee;
			}
			$('.transaction-fee').html(this.formatNumber(fee));
			$('.total').html(this.formatNumber(total));
		}

		this.set_units_max_spend = function () {
			var cost = 0;
			var earn = 0;
			var h = get_as_num(self.number_value('#per_unit_price')) * get_as_num(self.number_value('#max_number_of_units'));
			var fee = 0;
			fee = h * this.data.wm_fee;
			// Cap the fee at this.data.wm_max_fee.
			if (fee > this.data.wm_max_fee) {
				fee = this.data.wm_max_fee;
			}
			this.set_values(fee, h);

			if (this.data.mode == 'spend') {
				earn = h / (1 + this.data.wm_fee);
				cost = h;
				// Cap the fee at this.data.wm_max_fee.
				if ((cost - earn) > this.data.wm_max_fee) {
					earn = cost - this.data.wm_max_fee;
				}
			}
			else {
				cost = h + fee;
				earn = h;
			}

			$('#you-pay').html(this.formatNumber(cost));
			$('#resource-earns').html(this.formatNumber(earn));
		};

		this.set_blended_max_spend = function () {
			var cost = 0;
			var earn = 0;
			var h = get_as_num(self.number_value('#initial_per_hour_price')) * get_as_num(self.number_value('#initial_number_of_hours')) + get_as_num(self.number_value('#additional_per_hour_price')) * get_as_num(self.number_value('#max_blended_number_of_hours'));
			var fee = 0;
			var total=0;
			fee = h * this.data.wm_fee;
			// Cap the fee at this.data.wm_max_fee.
			if (fee > this.data.wm_max_fee) {
				fee = this.data.wm_max_fee;
			}
			this.set_values(fee,h);

			if (this.data.mode == 'spend') {
				earn = h / (1 + this.data.wm_fee);
				cost = h;
				// Cap the fee at this.data.wm_max_fee.
				if ((cost - earn) > this.data.wm_max_fee) {
					earn = cost - this.data.wm_max_fee;
				}
			}
			else {
				cost = h + fee;
				earn = h;
			}

			$('#you-pay').html(this.formatNumber(cost));
			$('#resource-earns').html(this.formatNumber(earn));
		};
	};

	assignmentsPricing = new AssignmentPricing();
	assignmentsPricing.loadData(pricingFilters);
	assignmentsPricing.initialize();

	$pricingFlatFee.on('click', function () {
		$pricing.val('1');
		assignmentsPricing.set_max_spend();
	});

	$pricingPerHour.on('click', function () {
		$pricing.val('2');
		assignmentsPricing.set_hourly_max_spend();
	});

	$pricingPerUnit.on('click', function () {
		$pricing.val('3');
		assignmentsPricing.set_units_max_spend();
	});

	$pricingPerBlendedHour.on('click', function () {
		$pricing.val('4');
		assignmentsPricing.set_blended_max_spend();
	});

	$pricingInternal.on('click', function () {
		$pricing.val('7');
		assignmentsPricing.set_internal_spend();
	});


	if (options.isPricingMode) {
		selectedPrice = options.pricing
	} else {
		selectedPrice = null;
	}

	if (selectedPrice === '2') {
		$pricingPerHour.trigger('click');
		assignmentsPricing.set_hourly_max_spend();
	} else if (selectedPrice === '3') {
		$pricingPerUnit.trigger('click');
		assignmentsPricing.set_units_max_spend();
	} else if (selectedPrice === '4') {
		$pricingPerBlendedHour.trigger('click');
		assignmentsPricing.set_blended_max_spend();
	} else if (selectedPrice === '7') {
		$pricingInternal.trigger('click');
		assignmentsPricing.set_internal_spend();
	} else {
		$pricingFlatFee.trigger('click');
		assignmentsPricing.set_max_spend();
	}
	$('#price-container').removeClass('active');
}
