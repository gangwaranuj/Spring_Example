var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.mobile = wm.pages.mobile || {};
wm.pages.mobile.assignments = wm.pages.mobile.assignments || {};

wm.pages.mobile.assignments.complete = function (data) {

	var $taxConfigurationPanel = $('.collect-tax-section');
	var $taxField = $('.tax-percent');
	var $taxCollectedDisplay = $('.tax-to-report');
	var $taxDiffDisplay = $('.tax-to-diff');

	var $bonus = $('.bonus');
	var $hoursField = $("input[name='hours']");
	var $minutesField = $("input[name='minutes']");
	var $unitsField = $("input[name='units']");

	var $additionalExpensesField = $('.additional-expenses');

	var $pricingEarningsDisplay = $('.pricing-earnings-outlet');
	var $pricingEarningsToggle = $('.edit-pricing-earnings-outlet');
	var $overridePriceField = $('.override-price');

	var showOverride = false;
	var showTaxes = false;

	var render = function() {
		var price = calculatePrice();

		if (showOverride) {
			$pricingEarningsToggle.html('cancel');
			$pricingEarningsDisplay.hide();
			$overridePriceField.show();
		}
		else {
			$('.max-spend-limit-error-txt').html('').hide();
			$pricingEarningsToggle.html('adjust');
			$pricingEarningsDisplay.html(format_currency(price));
			$overridePriceField.val('');
			$overridePriceField.hide();
			$pricingEarningsDisplay.show();
		}

		if(showTaxes){
			calculateTaxes();
		}

		checkMaxSpendLimit();
		return this;
	};

	var calculatePrice = function() {
		var additional = parseFloat($additionalExpensesField.val() || 0);
		additional += parseFloat($bonus.val() || 0);

		if (data.type == 'FLAT') {
			return additional + data.flat_price;
		} else if (data.type == 'PER_HOUR') {
			return additional + data.per_hour_price * (parseFloat($hoursField.val() || 0) + parseFloat(($minutesField.val() || 0) / 60));
		} else if (data.type == 'PER_UNIT') {
			return additional + data.per_unit_price * parseFloat($unitsField.val() || 0);
		} else if (data.type == 'BLENDED_PER_HOUR') {
			var hours = parseFloat($hoursField.val() || 0) + parseFloat(($minutesField.val() || 0) / 60);
			if (hours <= data.initial_number_of_hours) return additional + data.initial_per_hour_price * hours;
			return additional + data.initial_per_hour_price * data.initial_number_of_hours + data.additional_per_hour_price * (hours - data.initial_number_of_hours);
		}
		return 0.0;
	};

	var calculateTaxes = function() {
		var val = $taxField.val();
		var percent = parseFloat(val) / 100.00;

		var price = (showOverride) ? $overridePriceField.val() : calculatePrice();

		$taxCollectedDisplay.html(format_currency(price * percent));
		$taxDiffDisplay.html(format_currency(price * ( 1- percent) ));
	};

	var toggleCollectTaxes = function() {
		showTaxes = !showTaxes;
		$taxField.empty();
		// toggle all but first two li elements
		$(_.rest($taxConfigurationPanel.find('li'), 2)).toggle();
		render();
	};

	var toggleOverride = function(){
		showOverride = !showOverride;
		render();
	};

	var checkAdditionalExpensesMaxValue = function() {
		var additional = parseFloat($additionalExpensesField.val() || 0);

		if (additional > data.additional_expenses) {
			$additionalExpensesField.val(data.additional_expenses);
			$('.additional-expenses-maxvalue-error-txt').html('Cannot exceed the approved increase of $' + data.additional_expenses.toFixed(2)).show();
		}
		else {
			$('.additional-expenses-maxvalue-error-txt').html('').hide();
		}

		render();
	};

	var checkMaxSpendLimit = function() {
		var price = (showOverride) ? $overridePriceField.val() : calculatePrice();

		if (price > data.max_spend_limit) {
			$('.max-spend-limit-error-txt').html('Cannot exceed the maximum spending limit of $' + data.max_spend_limit.toFixed(2)).show();
		}
		else {
			$('.max-spend-limit-error-txt').html('').hide();
		}
	};

	return function () {
		FastClick.attach(document.body);
		$pricingEarningsToggle.click(toggleOverride);
		$hoursField.keyup(render);
		$minutesField.keyup(render);
		$unitsField.keyup(render);
		$additionalExpensesField.keyup(render);
		$additionalExpensesField.keyup(checkAdditionalExpensesMaxValue);
		$overridePriceField.keyup(render);
		$overridePriceField.keyup(checkMaxSpendLimit);
		$taxField.keyup(render);
		$(".collect-tax").click(toggleCollectTaxes);

		render();

		$('form[name=completeAssignment]').on('submit', function () {
			trackEvent('mobile', 'complete');
		});
	};
};
