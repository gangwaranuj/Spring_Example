import $ from 'jquery';
import getCSRFToken from '../funcs/getCSRFToken';
import wmNotify from '../funcs/wmNotify';
import '../dependencies/jquery.calendrical';

export default function () {
	$('#time_from_0, #time_to_0').calendricalTimeRange();
	$('#time_from_1, #time_to_1').calendricalTimeRange();
	$('#time_from_2, #time_to_2').calendricalTimeRange();
	$('#time_from_3, #time_to_3').calendricalTimeRange();
	$('#time_from_4, #time_to_4').calendricalTimeRange();
	$('#time_from_5, #time_to_5').calendricalTimeRange();
	$('#time_from_6, #time_to_6').calendricalTimeRange();

	$(document).on('click', '.suspend-account', () => {
		$.ajax({
			url: '/mysettings/suspend',
			type: 'post',
			beforeSend (jqXHR) {
				jqXHR.setRequestHeader('x-csrf-token', getCSRFToken());
			}
		}).done((response) => {
			wmNotify({
				message: response.message
			});
		})
			.fail((response) => {
				wmNotify({
					message: response.message,
					type: 'danger'
				});
			});
	});

	$(document).on('click', '.reactivate-account', () => {
		$.ajax({
			url: '/mysettings/reactivate',
			type: 'post',
			beforeSend (jqXHR) {
				jqXHR.setRequestHeader('x-csrf-token', getCSRFToken());
			}
		}).done((response) => {
			wmNotify({
				message: response.message
			});
		})
			.fail((response) => {
				wmNotify({
					message: response.message,
					type: 'danger'
				});
			});
	});
}
