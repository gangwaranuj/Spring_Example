(function ($, _) {
	'use strict';

	$.fn.wmNotifications = function (options) {
		var settings, notificationEvents;

		settings = $.extend({
			timeout: 120000 // 120 seconds
		}, options);

		notificationEvents = function ($this) {
			var
				$spinner = $('.wm-spinner', $this),
				$items = $('#notification_items', $this),
				$count = $('.number', $this),
				$notificationsCount = $('.wm-notification', $this),
				getNotifications, updateNotifications, getCount, updateCount, viewedAllNotifications;

			var unreadNotificationsInfo = {};

			getNotifications = function () {
				// Fetch notifications
				$spinner.show();
				$.getJSON('/notifications/list', updateNotifications);
			};

			updateNotifications = function (response) {
				var notificationsTemplate = _.template($('#nav-notifications-tmpl').html());

				$spinner.hide();

				// TODO: Remove this once the QuickBooks promotion is over.
				response = response || [];
				var today = new Date();
				if (
					today.getFullYear() === 2016
					&& today.getMonth() === 3
					&& (today.getDate() === 8 || today.getDate() === 18)
				) {
					response.unshift({
						"notification_type": {
							"configurable_flag": false,
							"is_default": false,
							"is_bullhorn_default": false,
							"user_notification_flag": false,
							"voice_flag": false,
							"dispatch_voice_flag": false,
							"code": "workmarket.marketing"
						},
						"display_message": '<a href="https://selfemployed.intuit.com/workmarket?utm_source=workmarket&utm_medium=IPD&utm_content=announcement&cid=IPD_workmarket_announcement_QBSE&utm_email=' + $('meta[name="userEmail"]').attr('content') + '" target="_blank">Your next tax payment is due April 18, 2016. Save money on your taxes with QuickBooks Self-Employed. Try it free. <span class="third-party-logo -inline -square -intuit-qb"></span></a>',
						"sticky": true,
						"notification_status": {
							"code": "published"
						},
						"viewed": false,
						"deleted": false,
						"created_on": "2016-04-08T00:00:00.000Z",
						"id": 0
					});
				}

				// If there are notifications then populate the list
				if (response !== null) {
					$items.html(notificationsTemplate({
						notifications: response
					}));
				}

				var $dropdown = $('.dropdown-menu--item');
				$dropdown.find('.view-upload').on('click', function (event) {
					'use strict';
					var postFormFactory = (function () {
						var _html = '';
						var _uri = '';

						var ret = {
							init: function () {
								_html = '<input type="hidden" name="_tk" id="_tk" value="' + getCSRFToken() + '">';
								return this;
							},
							uri: function (uri) {
								_uri = uri;
								return this;
							},
							add: function (name, values) {
								if (!(values instanceof Array)) {
									values = [values];
								}
								values.forEach(function (value) {
									_html += '<input type="hidden" name="' + name + '" value="' + value + '">';
								});
								return this;
							},
							build: function () {
								return $('<form>', {
									html: _html,
									action: _uri,
									method: 'POST'
								}).appendTo(document.body);
							}
						};

						ret.init();
						return ret;
					})();

					var form = postFormFactory
						.uri('/assignments/bulk_send')
						.add('ids', $(event.currentTarget).data('worknumbers'));
					form.build().submit();
				});

				$dropdown.find('.error-upload').on('click', function (event) {
					'use strict';

					function redirectWithFlash(url, type, msg) {
						var e = $('<form></form>');
						e.attr({
							'action':'/message/create',
							'method':'POST'
						});
						e.append(
							$('<input>').attr({
								'name': 'message[]',
								'value': msg
							}));
						e.append(
							$('<input>').attr({
								'name': 'type',
								'value': type
							}));
						e.append(
							$('<input>').attr({
								'name': 'url',
								'value': url
							}));
						e.append(
							$('<input>').attr({
								'name': '_tk',
								'value': getCSRFToken()
							}));
						$('body').append(e);
						e.submit();
					}

					var displayMessage = '<strong>The following errors were found with the source file and mapping:</strong>' + $(event.currentTarget).data('message').replace(/Line /g, '<hr>Line ');
					redirectWithFlash('/assignments/upload', 'error', displayMessage);
				});
			};

			getCount = function () {
				$.getJSON('/notifications/unread_notifications', updateCount);
			};

			updateCount = function (response) {
				// Check to see if there are any new notifications
				if (response.data !== undefined && !_.isUndefined(response.data.notifications)) {
					unreadNotificationsInfo = response.data.notifications;
					// Update the notifications count
					$count.text(unreadNotificationsInfo.unreadCount);
					$notificationsCount.attr('data-badge', unreadNotificationsInfo.unreadCount);
					// Tell the app we've seen all new notifications
					$this.one('mouseover', viewedAllNotifications);
				}
			};

			// Tell the app we've seen all new notifications
			viewedAllNotifications = function () {
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
			};

			return {
				getNotifications: getNotifications,
				getCount: getCount
			};
		};


		return this.each(function () {
			var
				$this = $(this),
				$items = $('#notification_items', $this),
				$count = $('.number', $this),
				$notificationsCount = $('.wm-notification', $this);

			$this.on('mouseover', _.throttle(notificationEvents($this).getNotifications, settings.timeout));

			notificationEvents($this).getCount();

			// mark all notifications as read when user mouses out of the list
			$items.on('mouseleave', function () {
				// Update the notifications count
				$count.text(0);
				$notificationsCount.attr('data-badge', 0);
				$('.icon-circle', this).removeClass('power-on');
			});
		});
	};
}(jQuery, _));
