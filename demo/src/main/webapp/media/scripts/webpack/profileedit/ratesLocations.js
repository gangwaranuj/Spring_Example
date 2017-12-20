'use strict';

import $ from 'jquery';
import wmTabs from '../funcs/wmTabs';

export default function () {
	wmTabs();

	$('#add_postal_code').on('click', function () {
		const postalCode = $('#exclude_postal_code').val();
		$('#postal_code_messages').hide();
		$.ajax({
			url: '/profile-edit/exclude_postal_code',
			data: {
				postal_code: postalCode
			},
			dataType: 'json',
			type: 'POST',
			success: function (data) {
				if (data.successful) {
					$('#exclude_postal_code_list').append($('<li class="remove">' + postalCode + ' - <a href="javascript:void(0);" data-value="' + postalCode + '">Remove<\/a><\/li>').attr('id', 'postal_code_' + postalCode));
					$('#exclude_postal_code').val('');
					$('.remove').on('click', removePostalCode);
				} else {
					if (data.messages.length > 0) {
						$('#postal_code_messages').addClass('error').removeClass('success').show();
						$('#postal_code_messages div').html(data.messages.join("<br/>"));
					}
				}
			}
		});
	});

	$('.remove').on('click', removePostalCode);

	function removePostalCode () {
		let
			postalCode = $(this).children('a').data('value'),
			request = $.ajax({
				url: '/profile-edit/remove_postal_code',
				data: {postal_code: postalCode},
				dataType: 'json',
				type: 'post'
			});
		request.then(function (data) {
			if (data.successful) {
				$('#postal_code_' + postalCode).remove();
			}
		});
	}

};
