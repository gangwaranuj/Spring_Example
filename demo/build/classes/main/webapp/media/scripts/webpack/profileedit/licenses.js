'use strict';

import $ from 'jquery';
import '../config/datepicker';

export default function (state, licenseId) {
	$('select#state')
		.val(state)
		.change(changeState);
	changeState(licenseId);

	$('#issueDate').datepicker({dateFormat: 'mm/dd/yy'});
	$('#expirationDate').datepicker({dateFormat: 'mm/dd/yy', minDate: new Date()});

	function changeState(defaultLicenseId) {
		$('#select_license').hide();
		$('#license_number').hide();
		$('#save_button').hide();

		if ($('select#state').val()) {
			$.getJSON(
				'/profile-edit/licenselist',
				{state : $('select#state').val()},
				function(data) { updateLicenses(data, defaultLicenseId); }
			);
		}
	}

	function updateLicenses(data, defaultLicenseId) {
		$('#select_license').show();

		if (data.length === 0) {
			$('#select_license .input').html('No licenses are available in this state.');
		} else {
			var select = $('<select name="license"></select>');
			$.each(data, function (i, item) {
				$('<option value="' + item.id + '">' + item.name + '</option>').appendTo(select);
			});
			$('#select_license .input').html(select);

			if (defaultLicenseId) {
				select.val(defaultLicenseId);
			}

			$('#license_number').show();
			$('#save_button').show();
		}
	}

};
