import $ from 'jquery';
import GetCSRFToken from './getCSRFToken'

export default ($button) => {

	let shouldPerformWork = $button.hasClass('perform-work-toggle'),
		shouldCreateWork = $button.hasClass('create-work-toggle'),
		shouldDispatchWork = $button.hasClass('dispatch-work-toggle'),
		params = {
			seller: shouldPerformWork,
			buyer: shouldCreateWork,
			dispatcher: shouldDispatchWork
		};

	if (!$button.hasClass('active')) {
		$.ajax({
			url: '/profile-edit/persona-toggle/' + $button.data('personatype'),
			type: 'post',
			data: params,
			beforeSend: function (xhr) {
				xhr.setRequestHeader('X-CSRF-TOKEN', GetCSRFToken());
			},
			success: function () {
				window.location = '/home';
			}
		});
	}

	return false;
};
