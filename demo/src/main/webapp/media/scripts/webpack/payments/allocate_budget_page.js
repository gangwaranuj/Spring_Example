'use strict';
import $ from 'jquery';

export default () => {
	$('#transfer_from').on('change', () => {
		$('#transfer_to').html($('#transfer_from').html());
		const transfer_from = $('#transfer_from').val().toString();
		$('#transfer_to option[value = "' + transfer_from + '"]').remove();
	});

	$('#amount').on('keyup', validateForm);
	$('#transfer_from, #transfer_to').on('change', validateForm);

	function validateForm() {
		let empty = false;

		$('#transfer_from, #transfer_to').each(function () {
			if ($(this).val() == '') {
				empty = true;
			}
		});

		$('#amount').each(function () {
			if ($(this).val() == '') {
				empty = true;
			}
		});

		if (empty) {
			$('#allocate-funds-submit').attr('disabled', 'disabled');
		} else {
			$('#allocate-funds-submit').removeAttr('disabled', '');
		}
	}
}
