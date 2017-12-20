import $ from 'jquery';
import 'jquery-form/jquery.form'; // eslint-disable-line
import _ from 'underscore';
import wmMaskInput from '../funcs/wmMaskInput';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import renderScreening from '../screening';
import Application from '../core';

Application.Events.on('application:initialize', () => {
	if (document.getElementById('wm-screening')) {
		renderScreening('DRUG_SCREEN', Application.Features);
	}
});

export default function (usaPrice, canadaPrice) {
	const toggle = (sec) => {
		const $section = $(sec);

		if ($section.hasClass('dn')) {
			$section.removeClass('dn');
		} else {
			$section.addClass('dn');
		}
	};

	$('#help_cvv').on('click', () => {
		wmModal({
			autorun: true,
			title: 'Security code',
			destroyOnClose: true,
			content: $('#help_cvv_content').html()
		});
	});

	$('.screeningDetailsForm').ajaxForm({
		dataType: 'json',
		method: 'POST',
		success (response) {
			if (response.successful) {
				window.location = response.redirect;
			} else {
				_.each(response.messages, (theMessage) => {
					wmNotify({
						message: theMessage,
						type: 'danger'
					});
				});
			}
		}
	});

	$('input[name="paymentType"]').on('click', () => {
		const type = $('input[name="paymentType"]:checked').val();
		$(`#payment-${type}`).show().siblings().hide();
	});

	$('.renewal').on('click', _.partial(toggle, '#renewForm'));
	$('.view_previous').on('click', _.partial(toggle, '.list_previous'));

	$('[name="country"]').on('change', function onChange () {
		const country = $(this).val();
		const cost = (country === 'CAN') ? canadaPrice : usaPrice;

		wmMaskInput({ selector: '#ssn' }, country === 'CAN' ? 'sin' : 'ssn');
		$('.bkgrnd-creening-cost').text(`$${parseFloat(cost).toFixed(2)}`);

		if (country === 'CAN') {
			$('#availability-alert').show();
			$('#screeningForm .btn.primary').prop('disabled', true);
			$('#ssn-label').text('SIN');
		} else {
			$('#availability-alert').hide();
			$('#screeningForm .btn.primary').prop('disabled', false);
			$('#ssn-label').text('SSN');
		}
	}).trigger('change');
}
