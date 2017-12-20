'use strict';

import $ from 'jquery';
import _ from 'underscore';
import '../config/datepicker';

export default (providerId, certificationId) => {

	function changeIndustry (default_provider_id, default_certification_id) {
		$('#select_provider').hide();
		$('#select_certification').hide();
		$('#certification_details').hide();
		$('#certification_number').hide();
		$('#certification_attachment').hide();
		$('#provider_instructions').hide();
		$('#save_button').hide();

		if ($('select#industry').val()) {

			$.getJSON(
				'/profile-edit/certificationslist',
				{industry : $('select#industry').val()},
				function (data) {
					$('#select_provider').show();

					if (data.length == 0) {
						$('#select_provider .input').html('No providers are available in this industry.');
					} else {
						var select = $('<select name="provider" id="provider"></select>');
						select.append('<option value="">- Provider -</option>')
						$.each(data, function(i, item) {
							$('<option value="' + item.id + '">' + item.name + '</option>').appendTo(select);
						});
						$('#select_provider .input').html(select);

						if (default_provider_id) {
							select.val(default_provider_id);
							changeProvider(default_certification_id);
						}
					}
				}
			);
		}
	}

	function changeProvider (default_certification_id) {
		$('#select_certification').hide();
		$('#certification_details').hide();
		$('#certification_number').hide();
		$('#certification_attachment').hide();
		$('#provider_instructions').hide();
		$('#save_button').hide();

		if ($('select#provider').val()) {
			$.getJSON(
				'/profile-edit/certificationslist', {
					industry : $('select#industry').val(),
					provider : $('select#provider').val()
				},
				function (data) {
					$('#select_certification').show();
					$('#certification_details').show();
					$('#certification_attachment').show();
					$('#save_button').show();

					if (typeof data.list == 'undefined' || data.list.length == 0) {
						$('#select_certification .input').html('No certifications are available for this provider.');
					} else {
						var select = $('<select name="certification" id="certification"></select>');
						select.append('<option value="">- Certification -</option>')
						_.each(data.list, function (item) {
							$('<option value="' + item.id + '">' + item.name + '</option>').appendTo(select);
						});
						$('#select_certification .input').html(select);

						if (default_certification_id) {
							select.val(default_certification_id);
						}

						if (data.vendor.instructions) {
							$('#provider_instructions').html(data.vendor.instructions).show();
						} else {
							$('#provider_instructions').hide();
						}

						if (typeof data.vendor.is_required != 'undefined' && data.vendor.is_required == true) {
							$('#certification_number').show();
						}
					}
				}
			);
		}
	}

	$(document).on('change','select#provider', () => {
		changeProvider();
	});

	$(document).on('change','select#industry', () => {
		changeIndustry();
	});

	$('#issueDate').datepicker({dateFormat: 'mm/dd/yy'});

	$('#expirationDate').datepicker({dateFormat: 'mm/dd/yy', minDate: new Date()});


	changeIndustry(providerId, certificationId);

};
