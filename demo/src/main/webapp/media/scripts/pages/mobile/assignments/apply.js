var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.mobile = wm.pages.mobile || {};
wm.pages.mobile.assignments = wm.pages.mobile.assignments || {};

wm.pages.mobile.assignments.apply = function (workPricingId, flatPriceType, hourlyPriceType, unitPriceType, blendedPriceType) {

	function togglePriceNegotiation(event, ui) {
		$('#price_negotiation_container').toggle($('#price_negotiation').is(':checked'));
	}

	function toggleScheduleNegotiation(event, ui) {
		$('#schedule_negotiation_container').toggle($('#schedule_negotiation').is(':checked'));
	}

	function toggleExpirationNegotiation(event, ui) {
		$('#expire_negotiation_container').toggle($('#expire_negotiation').is(':checked'));
	}

	function toggleScheduling(event, ui) {
		var isRange = parseInt($(this).val());

		$('#fixed_schedule_container').toggle(isRange);
		//TODO: this should clear the values of the deselected inputs
		$('#variable_schedule_container').toggle(isRange);
	}

	function safeCheckFunction (arr) {
		var collection = _.map(arr, function (element) {
			return $(element).val();
		});
		var isCollectionEmpty = _.some(collection, _.isEmpty);
		if (isCollectionEmpty) {
			_.each(arr, function (element) {
				$(element).toggleClass('missingForm', _.isEmpty($(element).val()));
			});
			return false;
		}
		return true;
	}

	// function to check if all the values on the form is being submitted.
	function checkForm(event) {
		var safeCheck = true;
		var schedulingSelectors = ['#from2', '#fromtime2', '#to', '#totime'];

		// If the "Set an expiration date" checkbox is checked
		if ($('#expire_negotiation').is(':checked')) {
			safeCheck = safeCheckFunction(['#expires_on', '#expires_on_time']);
		}
		// If the "Offer a new date/time" checkbox is checked.
		if ($('#schedule_negotiation').is(':checked')) {
			// If "At a specific time" is checked.
			if ($('#scheduling1').is(':checked')) {
				safeCheck = safeCheckFunction(['#from', '#fromtime']);
				if (safeCheck) {
					_.each(schedulingSelectors, function (element) {
						$(element).removeAttr('name');
					});
				}
			// If "During a time window" is checked.
			} else {
				safeCheck = safeCheckFunction(schedulingSelectors);
				if (safeCheck) {
					_.each(['#from', '#fromtime'], function (element) {
						$(element).removeAttr('name');
					});
				}
			}
		}
		// If "Offer a new price" checkbox is checked.
		if ($('#price_negotiation').is(':checked')) {
			// Flat rates.
			if (workPricingId == flatPriceType) {
				safeCheck = safeCheckFunction(['#flat_price']);
			}
			// Hourly rates.
			if (workPricingId == hourlyPriceType) {
				safeCheck = safeCheckFunction(['#per_hour_price', '#max_number_of_hours']);
			}
			// Rates per units.
			if (workPricingId == unitPriceType) {
				safeCheck = safeCheckFunction(['#per_unit_price', '#max_number_of_units']);
			}
			// Blended rates.
			if (workPricingId == blendedPriceType) {
				safeCheck = safeCheckFunction(['#initial_per_hour_price', '#initial_number_of_hours', '#additional_per_hour_price', '#max_blended_number_of_hours']);
			}
		}
		if (safeCheck) {
			showSpinner();
			// and now it will continue w/the submit
		} else {
			$(window).scrollTop(0);
			showErrorMessages({ messages: ['Please fill out the missing form(s)']});
			event.preventDefault();
		}
	}

	return function () {
		FastClick.attach(document.body);
		$('.pickadate').pickadate({
			min: true,
			format: 'mm/dd/yyyy'
		});
		$('.timepicker').pickatime({
			interval: 15,
			format: 'hh:iA'
		});
		$('#price_negotiation').on('change', togglePriceNegotiation);
		$('#schedule_negotiation').on('change', toggleScheduleNegotiation);
		$('#expire_negotiation').on('change', toggleExpirationNegotiation);
		$('input[name="scheduling"]').on('change', toggleScheduling);
		$('input[name="submit"]').on('click', checkForm);

		$('#form_negotiate_assignment').on('submit', function () {
			trackEvent('mobile', 'apply');
		});
	};
};
