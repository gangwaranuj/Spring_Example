var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.mobile = wm.pages.mobile || {};
wm.pages.mobile.assignments = wm.pages.mobile.assignments || {};

wm.pages.mobile.assignments.reschedule = function () {

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
		// If "At a specific time" is selected.
		if ($('#scheduling1').is(':checked')) {
			safeCheck = safeCheckFunction(['#from', '#fromtime']);
			if (safeCheck) {
				_.each(schedulingSelectors, function (element) {
					$(element).removeAttr('name');
				});
			}
		// If "During a time window" is selected.
		} else {
			safeCheck = safeCheckFunction(schedulingSelectors);
			if (safeCheck) {
				$("#from").removeAttr('name');
				$("#fromtime").removeAttr('name');
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
		$('input[name="scheduling"]').on('change', toggleScheduling);
		$('input[name="submit"]').on('click', checkForm);

		$('#reschedule-form').on('submit', function () {
			trackEvent('mobile', 'reschedule', 'request');
		});
	};
};
