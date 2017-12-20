var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.admin = wm.pages.admin || {};
wm.pages.admin.insurance = wm.pages.admin.insurance || {};

wm.pages.admin.insurance.edit_userinsurance = function () {
	'use strict';

	return function () {
		$('.action_cta').on('click', function () {
			var action = $(this).attr('id');
			$('#action_to_take').val(action);

			if (action === 'decline') {
				$.colorbox({
					inline: true,
					href: '#decline_note_popup',
					title: 'Decline Note',
					transition: 'none',
					innerWidth: 500
				});
			}
			else if (action === 'need_info') {
				$.colorbox({
					inline: true,
					href: '#more_info_popup',
					title: 'Need More Info Notice',
					transition: 'none',
					innerWidth: 500
				});
			}
			else {
				$('#certs_form').trigger('submit');
			}
		});

		$('#decline_action_cta').on('click', function () {
			$('#note_to_send').val($('#decline_note').val());
			$('#certs_form').trigger('submit');
		});

		$('#decline_nonote_action_cta').on('click', function () {
			$('#certs_form').trigger('submit');
		});

		$('#more_info_action_cta').on('click', function () {
			$('#note_to_send').val($('#more_information_note').val());
			$('#certs_form').trigger('submit');
		});

		$('#issue_date').datepicker({ dateFormat: 'mm/dd/yy' });
		$('#expiration_date').datepicker({ dateFormat: 'mm/dd/yy' });

		$('#override').on('change', function () {
			if (this.checked) {
				$('#affirm').hide();
				$('#affirmation').wrap('<strong></strong>')
			} else {
				$('#affirm').show();
				$('#affirmation').unwrap('<strong></strong>')
			}
		});
		//wm.funcs.maskInput({ selector: '#coverage' }, '999999999');
		$('#override').trigger('change');
	};
};
