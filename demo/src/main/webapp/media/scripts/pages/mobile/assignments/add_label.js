var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.mobile = wm.pages.mobile || {};
wm.pages.mobile.assignments = wm.pages.mobile.assignments || {};

wm.pages.mobile.assignments.addLabel = function (label_id, jsonLabels) {
	'use strict';

	function renderAddLabelForm (data) {
		var label = data || {};
		var labelConfig = label.sub_status_descriptor;

		$('label[for="label_note"]').toggleClass('required', labelConfig.note_required);

		if (label.instructions) {
			$('#label_note_instructions').show().html(label.instructions);
		} else {
			$('#label_note_instructions').hide().empty();
		}
		if (labelConfig.schedule_required) {
			$('#label_reschedule').show();
		} else {
			$('#label_reschedule').hide();
		}
	}

	return function() {
		FastClick.attach(document.body);

		var labels_json = $.parseJSON(jsonLabels);

		$('.pickadate').pickadate({
			min: true,
			format: 'mm/dd/yyyy'
		});
		$('.timepicker').pickatime({
			interval: 30,
			format: 'hh:iA'
		});

		$('#label_id').change(function() {
			var key = $(this).val();
			renderAddLabelForm(labels_json[key]);

			// This checks that the user selected a label before adding a new label.
			if($(this).val() === '') {
				$('#add-label').prop("disabled", true);
			} else {
				$('#add-label').prop("disabled", false);
			}
		});

		if (label_id ==! undefined) {
			renderAddLabelForm(labels_json[label_id]);
		}

		$('input[name="reschedule_option"]').change(function() {
			$('.to-date').toggle($(this).val() === 'window');
		});

		$('.add-label-form').on('submit', function () {
			trackEvent('mobile', 'label', 'add');
		});
	};
};
