'use strict';

import $ from 'jquery';
import wmMaskInput from '../funcs/wmMaskInput';
import '../config/datepicker';

export default function () {

	$('select#industry').on('change', function () {
		change_industry();
	});

	$('#issueDate').datepicker({ dateFormat: 'mm/dd/yy' });
	$('#expirationDate').datepicker({
		dateFormat: 'mm/dd/yy',
		minDate: new Date()
	});

	$('#override').on('change', function () {
		if (this.checked) {
			$('#affirm').hide();
		} else {
			$('#affirm').show();
		}
	});

	function change_industry (default_insurance_id) {
		$('#select_provider').hide();
		$('#select_insurance').hide();
		$('#insurance_details').hide();

		if ($('select#industry').val()) {
			$.getJSON(
				'insurancelist',
				{industry : $('select#industry').val()},
				function (data) {
					update_insurance(data, default_insurance_id);
				}
			);
		}
	}

	function update_insurance (data, default_insurance_id) {
		var $insurance = $('#insurance');
		$('#select_insurance').show();

		if (typeof data.list == 'undefined' || data.list.length == 0) {
			$('#select_insurance #insurance_type').html('No insurance types are available for this industry.');
		} else {
			var select = $('<select name="insuranceId" id="insurance">');
			$.each(data.list, function (i, item) {
				$('<option value="' + item.id + '">' + item.name + '</option>').appendTo(select);
			});
			$('#select_insurance #insurance_type').html(select);

			if (default_insurance_id) {
				select.val(default_insurance_id);
			}

			$('#insurance_details').show();

			$insurance.on('change', function () {
				if ($(this).val() === $('#workers_comp_insurance_id').val()) {
					$('#override-section').show();
				} else {
					$('#override-section').hide();
				}
			});
			$insurance.trigger('change');
		}
	}


	change_industry();
	wmMaskInput({ selector: '#coverage' }, '999999999');
}
