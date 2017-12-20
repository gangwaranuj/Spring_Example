import $ from 'jquery';
import getCSRFToken from '../funcs/getCSRFToken';
import wmNotify from '../funcs/wmNotify';

export default function () {
	$('.unblock').on('click', function onClick (event) {
		event.preventDefault();

		const $button = $(event.currentTarget);
		const id = $button.data('id');
		const currentRow = $(this).parent().parent();
		const table = currentRow.parent().parent();

		if ($button.data('isresource')) {
			$.ajax({
				url: '/relationships/unblocked_resources',
				type: 'POST',
				data: { resourceNumber: id },
				global: true,
				dataType: 'json',
				beforeSend (jqXHR) {
					jqXHR.setRequestHeader('x-csrf-token', getCSRFToken());
				},
				success (response) {
					if (response.successful) {
						currentRow.remove();
						wmNotify({ message: response.messages[0] });

						if (!$('#blocked_resources >tbody >tr').length) {
							table.parent().append('<div class="alert">You currently have no blocked workers.</div>');
							table.remove();
						}
					}
				}
			});
		} else {
			$.ajax({
				url: '/relationships/unblocked_clients',
				type: 'POST',
				data: { clientNumber: id },
				global: true,
				dataType: 'json',
				beforeSend (jqXHR) {
					jqXHR.setRequestHeader('x-csrf-token', getCSRFToken());
				},
				success (response) {
					if (response.successful) {
						currentRow.remove();
						wmNotify({ message: response.messages[0] });

						if (!$('#blocked_clients >tbody >tr').length) {
							table.parent().append('<div class="alert">You currently have no blocked companies.</div>');
							table.remove();
						}
					}
				}
			});
		}
	});
}
