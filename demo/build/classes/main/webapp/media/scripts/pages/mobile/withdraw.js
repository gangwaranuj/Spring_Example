var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.mobile = wm.pages.mobile || {};

wm.pages.mobile.withdraw = function (paypalFees, paypalCountryCodes) {

	var calc_factory = {
		ACH: function (amount) {
			return {
				value: Math.max(amount, 0),
				description: 'Free'
			};
		},
		PPA: function (amount, cost) {
			if (cost.fixed_amount) {
				return {
					value: Math.max(amount - cost.fixed_amount, 0),
					description: format_currency(cost.fixed_amount)
				}
			} else {
				return {
					value: Math.max(amount - Math.min(cost.percentage_based_amount_limit, cost.percentage_rate * amount), 0),
					description: (cost.percentage_rate * 100) + '% up to ' + format_currency(cost.percentage_based_amount_limit)
				};
			}
		},
		GCC: function (amount) {
			return {
				value: Math.max(amount, 0),
				description: 'Free'
			};
		}
	};

	$('[name=account]').on('change', function () {
		$('#withdrawBtn').attr('disabled', 'disabled');
		$('.config').hide();
		var type = $(this).find(':selected').data('type');
		if (type) {
			var config = $('.config[rel~=' + type.toLowerCase() + ']').show();
			var country = $(this).find(':selected').data('country');

			$('[name=amount]').on('keyup', function () {
				var grossAmount = $(this).val();
				var adjusted = calc_factory[type](grossAmount, paypalFees[paypalCountryCodes[country]]);

				config.find('.amount-outlet').text(format_currency(adjusted.value));
				config.find('.fee-calc-outlet').text(adjusted.description);

			}).trigger('keyup');
			$('#withdrawBtn').removeAttr('disabled');
		}
	}).trigger('change');

};
