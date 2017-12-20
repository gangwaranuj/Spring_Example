function bullhorn_count() {
	var $bullhorn = $('.bullhorn');
	var $bubble = $('.push-bubble');
	var $container = $('.bullhorn-container');

	if ($bullhorn.length > 0) {
		$.getJSON('/notifications/unread_notifications', function (response) {
			if (response.data !== undefined && response.data.notifications.length !== 0) {
				$bubble.text(response.data.notifications.length);
				$bullhorn.on('click', function () { $bubble.empty() });
			}
		});
	}
}