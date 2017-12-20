import $ from 'jquery';

export default () => {
	$('#devices').on('click', '.btn', (event) => {
		if (confirm('Are you sure you want to remove this device?')) { // eslint-disable-line no-alert
			$.ajax({
				context: this,
				type: 'POST',
				dataType: 'JSON',
				url: '/mysettings/remove_device',
				data: {
					device_uid: $(event.currentTarget).parents('tr').find('.device_uid').text()
				},
				success: () => {
					location.reload();
				}
			});
		}
	});
}
;
