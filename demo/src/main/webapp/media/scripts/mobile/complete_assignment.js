var wm_complete = wm_complete  || {};
wm_complete.complete = function(data) {

	this.data = data || {
		pricingType: ''
	};

	var self = this;

	this.taxConfigurationPanel = $('#collect_tax_section');
	this.taxToggleButton = $('.collect_tax');
	this.taxField = $('.tax_percent');
	wm.funcs.maskInput({ selector: '.tax_percent' }, '99');
	this.taxCollectedDisplay = $('.tax_to_report');
	this.taxDiffDisplay = $('.tax_to_diff');

	this.bonus = $('.bonus');
	this.hoursField = $('.hours');

	wm.funcs.maskInput({ selector: '.hours' }, '99999');
	this.minutesField = $('.minutes');
	wm.funcs.maskInput({ selector: '.minutes' }, '99');

	this.unitsField = $('.units');

	this.additionalExpensesField = $('.additional_expenses');
	this.additionalExpensesMaxValue = $('#additional_expenses_maxvalue');

	this.pricingEarningsDisplay = $('.pricing_earnings_outlet');
	this.pricingEarningsToggle = $('.edit_pricing_earnings_outlet');
	this.overridePriceField = $('.override_price').closest('span').hide();

	this.showOverride = false;
	this.showTaxes = false;

	this.render = function() {
		var price = self.calculatePrice();

		if (self.minutesField.val() > 0 && !(self.hoursField.val())) self.hoursField.val(0);
		if (self.hoursField.val()) self.hoursField.val(parseInt(self.hoursField.val()), 10);  // strip leading zeroes
		if (self.minutesField.val()) self.minutesField.val(parseInt(self.minutesField.val()), 10);

		if (self.showOverride) {
			self.pricingEarningsToggle.html('cancel');
			self.overridePriceField = $('.override_price');
			self.pricingEarningsDisplay.hide();
			self.overridePriceField.closest('span').show();
		}
		else {
			$('#max_spend_limit_error_txt').html('').hide();
			self.pricingEarningsToggle.html('adjust');
			self.pricingEarningsDisplay.html(format_currency(price));
			self.overridePriceField.val(0);
			$('.override_price').closest('span').hide();
			self.pricingEarningsDisplay.show();
		}

		if(self.showTaxes){
			self.calculateTaxes();
		}

		return self;
	};

	this.calculatePrice = function() {
		var additional = parseFloat(self.additionalExpensesField.val() || 0);
		additional += parseFloat(self.bonus.val() || 0);

		if (self.data.pricingType == 'FLAT') {
			return additional + self.data.flatPrice;
		} else if (self.data.pricingType == 'PER_HOUR') {
			return additional + self.data.perHourPrice * (parseFloat(self.hoursField.val() || 0) + parseFloat((self.minutesField.val() || 0) / 60));
		} else if (self.data.pricingType == 'PER_UNIT') {
			return additional + self.data.perUnitPrice * parseFloat(self.unitsField.val() || 0);
		} else if (self.data.pricingType == 'BLENDED_PER_HOUR') {
			var hours = parseFloat(self.hoursField.val() || 0) + parseFloat((self.minutesField.val() || 0) / 60);
			if (hours <= self.data.initialNumberOfHours) return additional + self.data.initialPerHourPrice * hours;
			return additional + self.data.initialPerHourPrice * self.data.initialNumberOfHours + self.data.additionalPerHourPrice * (hours - self.data.initialNumberOfHours);
		}
		return 0.0;
	};

	this.calculateTaxes = function() {
		var val = self.taxField.val();
		var percent = parseFloat(val) / 100.00;

		var price = (self.showOverride) ? self.overridePriceField.val() : self.calculatePrice();    // ToDo -- self doesn't work, showOverride is undefined here

		self.taxCollectedDisplay.html(format_currency(price * percent));
		self.taxDiffDisplay.html(format_currency(price * ( 1- percent) ));
	};

	this.toggleCollectTaxes = function() {
		self.showTaxes = !self.showTaxes;
		self.taxField.val(0);
		self.taxConfigurationPanel.toggle();
		self.render();
	};

	this.toggleOverride = function(){
		self.showOverride = !self.showOverride;
		self.render();
	};

	this.checkAdditionalExpensesMaxValue = function() {
		var additional = parseFloat(self.additionalExpensesField.val() || 0);
		var additional_max = parseFloat(self.additionalExpensesMaxValue.val() || 0);

		if (additional > additional_max) {
			$('.additional_expenses').val(additional_max);
			$('#additional_expenses_maxvalue_error_txt').html('Cannot exceed the approved increase of $'+additional_max.toFixed(2)).show();
		}
		else {
			$('#additional_expenses_maxvalue_error_txt').html('').hide();
		}

		self.render();
	};

	this.checkMaxSpendLimit = function() {
		var priceFloat = parseFloat(self.overridePriceField.val().replace(',',''));
		var maxSpendLimitFloat = parseFloat(self.data.maxSpendLimit || 0);

		if (priceFloat > maxSpendLimitFloat) {
			$('#max_spend_limit_error_txt').html('Cannot exceed the maximum spending limit of $'+maxSpendLimitFloat.toFixed(2)).show();
		}
		else {
			$('#max_spend_limit_error_txt').html('').hide();
		}
	};

};
