import Application from '../../core';
import $ from 'jquery';
import _ from 'underscore';
import notificationsTemplate from './notifications.hbs';
import { parseNotifications } from './utils';

const postFormFactory = () => {
	let html = `<input type="hidden" name="_tk" id="_tk" value="${Application.CSRFToken}">`,
		action = '';

	return {
		uri: function uri(url) {
			action = url;
			return this;
		},
		add: function add(name, values) {
			let valuesArray = Array.isArray(values) ? values : [values];
			html = valuesArray.reduce((memo, value) => `${memo}<input type="hidden" name="${name}" value="${value}">`, html);
			return this;
		},
		build: function build() {
			return $('<form>', {
				html,
				action,
				method: 'POST'
			}).appendTo(document.body);
		}
	};
};

const redirectWithFlash = (url, type, msg) => {
	let form = $(`
		<form action="/message/create" method="POST">
			<input name="message[]" value="${msg}" />
			<input name="type" value="${type}" />
			<input name="url" value="${url}" />
			<input name="_tk" value="${Application.CSRFToken}" />
		</form>
	`);
	$('body').append(form);
	form.submit();
};

let notificationEvents = function notificationEvents($this) {
	let $spinner = $('.wm-spinner', $this),
		$items = $('#notification_items', $this),
		$count = $('.number', $this),
		$notificationsCount = $('.wm-notification', $this),
		unreadNotificationsInfo = {};

	function updateNotifications(response) {
		$spinner.hide();
		// If there are notifications then populate the list
		if (response !== null) {
			let notifications = parseNotifications(response);
			$items.html(notificationsTemplate({ notifications }));

			var $dropdown = $('.dropdown-menu--item');
			$dropdown.find('.view-upload').on('click', (event) => {
				postFormFactory()
					.uri('/assignments/bulk_send')
					.add('ids', $(event.currentTarget).data('worknumbers'))
					.build()
					.submit();
			});

			$dropdown.find('.error-upload').on('click', (event) => {
				var displayMessage = '<strong>The following errors were found with the source file and mapping:</strong>' + $(event.currentTarget).data('message').replace(/Line /g, '<hr>Line ');
				redirectWithFlash('/assignments/upload', 'error', displayMessage);
			});
		}
	}

	function getNotifications() {
		// Fetch notifications
		$spinner.show();
		$.getJSON('/notifications/list', updateNotifications);
	}

	function viewedAllNotifications() {
		if (unreadNotificationsInfo.startUuid && unreadNotificationsInfo.endUuid) {
			$.ajax({
				type: 'POST',
				url: '/notifications/all_viewed',
				dataType: 'json',
				data: {
					startUuid: unreadNotificationsInfo.startUuid,
					endUuid: unreadNotificationsInfo.endUuid
				}
			});
		}
	}

	function updateCount(response) {
		// Check to see if there are any new notifications
		if (typeof response.data !== 'undefined' && typeof response.data.notifications !== 'undefined') {
			unreadNotificationsInfo = response.data.notifications;
			// Update the notifications count
			$count.text(unreadNotificationsInfo.unreadCount);
			$notificationsCount.attr('data-badge', unreadNotificationsInfo.unreadCount);
			// Tell the app we've seen all new notifications
			$this.one('mouseover', viewedAllNotifications);
		}
	}

	function getCount() {
		$.getJSON('/notifications/unread_notifications', updateCount);
	}

	return { getNotifications, getCount };
};

$.fn.wmNotifications = function wmNotifications(options) {
	let settings = Object.assign({
		timeout: 120000 // 120 seconds
	}, options);

	return this.each(function init() {
		var
			$this = $(this),
			$items = $('#notification_items', $this),
			$count = $('.number', $this),
			$notificationsCount = $('.wm-notification', $this);

		$this.on('mouseover', _.throttle(notificationEvents($this).getNotifications, settings.timeout));

		notificationEvents($this).getCount();

		// mark all notifications as read when user mouses out of the list
		$items.on('mouseleave', function mouseleave() {
			// Update the notifications count
			$count.text(0);
			$notificationsCount.attr('data-badge', 0);
			$('.icon-circle', this).removeClass('power-on');
		});
	});
};
