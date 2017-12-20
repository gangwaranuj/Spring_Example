import $ from 'jquery';
import 'jquery-ui';
import Application from '../core';
import MainRouter from './dashboard_router';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import '../dependencies/jquery.tmpl';
import '../config/datepicker';
import '../dependencies/jquery.bootstrap-dropdown';
import '../dependencies/jquery.bootstrap-tab';

// Passing in a no-op router to initialize
const features = Object.assign({}, config.options, config.features);
Application.init({ name: 'dashboard', features }, MainRouter);

if (Application.Features.isBuyer) {
	const loadIntroJs = async() => {
		const module = await import(/* webpackChunkName: "IntroJs" */ '../config/introjs');
		return module.default;
	};
	if (!(window.location.search.indexOf('introdisabled') > -1)) {
		loadIntroJs()
			.then((IntroJs) => {
				const intro = IntroJs('intro-assignment-dashboard-tour');
				intro.setOptions({
					steps: [
						{
							element: document.querySelector('#list_filters'),
							intro: '<h4>Powerful assignment search</h4><p>Use the keyword search to quickly find a specific assignment.</p>',
							position: 'right'
						},
						{
							element: document.querySelector('.dashboard-quick-actions .btn-group'),
							intro: '<h4>Various assignment views</h4><p>Check out the calendar view for an alternative to the default list-view.</p><p>Or, try the map view to see a geographic visualization of where your assignments are happening in real-time!</p>',
							position: 'bottom'
						}
					]
				});

				intro.watchOnce();
			});
	}
}

if (Application.Features.isSeller) {
	// all this calendar below should be a backbone view
	var refreshUi = function (isAuthed, calendars) {
		$('#save-calendar-settings').attr('disabled', true);
		if (isAuthed) {
			$('#save-action').hide();
			$('#only-google-sync').hide();
			$('#calendar-sync-access-calendar').hide();
			$('#calendar-sync-cancel').show();
			$('#calendar-sync-authed').show();
			$('#calendar-sync-unauth').hide();
		} else {
			$('#save-action').show();
			$('#select-calendar').attr('disabled', true);
			$('#only-google-sync').show();
			$('#calendar-sync-access-calendar').show();
			$('#calendar-sync-cancel').hide();
			$('#calendar-sync-authed').hide();
			$('#calendar-sync-unauth').show();
		}

		if (calendars) {
			var $selectCalendar = $('#select-calendar');
			$selectCalendar.removeAttr('disabled');
			$selectCalendar.empty();
			$selectCalendar.append(new Option('Create New Calendar', 'new-calendar'));
			$.each(calendars, function(key, value) {
				$('#select-calendar').append(new Option(value, key));
			});
		}
	};

	$('#calendar-sync').on('click', function () {
		$.ajax({
			url: '/assignments/calendar_sync_settings',
			dataType: 'json',
			type: 'get',
			success: function (data) {
				wmModal({
					autorun: true,
					title: 'Sync to Google Calendar',
					destroyOnClose: true,
					content: $('#add-cal-sync-tabs').html()
				});

				if (data.successful && data.data.has_settings) {
					refreshUi(true);
				} else if (data.successful) {
					refreshUi(false, data.data.calendars);
					$('#save-calendar-settings').removeAttr('disabled');
				} else if(data.data && data.data.error) {
					wmNotify({
						type: 'danger',
						message: data.data.error
					});
				}


				$('#cancel-sync').on('click', function () {
					$.ajax({
						url: '/assignments/cancel_calendar_sync',
						dataType: 'json',
						type: 'get',
						success : function (data) {
							if(data.successful) {
								refreshUi(false);
								$('.wm-modal--close').trigger('click');
							} else if(data.data && data.data.error) {
								wmNotify({
									type: 'danger',
									message: data.data.error
								});
							}
						}
					});
				});

				$('#calendar-access').on('click', function () {
					$.ajax({
						url: '/assignments/request_calendar_sync_access',
						dataType: 'json',
						type: 'get',
						success: function (data) {
							if(data.successful && data.data.authUrl) {
								window.open(data.data.authUrl);
								var isAuthorized = setInterval(function() {
									$.ajax({
										url: '/assignments/calendar_sync_settings',
										dataType: 'json',
										type: 'get',
										success: function (data) {
											if(data.successful) {
												refreshUi(false, data.data.calendars);
												$('#save-calendar-settings').removeAttr('disabled');
												clearInterval(isAuthorized);

											} else if(data.data && data.data.error) {
												wmNotify({
													type: 'danger',
													message: data.data.error
												});
											}
										}
									});
								},3000);
							}
						}
					});
				});

				$('#save-calendar-settings').on('click', function () {
					var $calendarSelect = $('#select-calendar'),
						calendarId = $calendarSelect.find(':selected').val(),
						calendarName = $calendarSelect.find(':selected').text(),
						new_calendar = false;
					if (calendarId === 'new-calendar') {
						calendarName = $('#new-calendar-name').val();
						new_calendar = true;
					}

					var data = {
						newCalendar: new_calendar,
						calendarId: calendarId,
						calendarName: calendarName
					};

					$.ajax({
						url: '/assignments/save_calendar_sync_settings',
						dataType: 'json',
						type: 'post',
						data: data,
						success: function (data) {
							if (data.successful) {
								refreshUi(true, false);
								$('.wm-modal--close').trigger('click');
							} else if(data.data && data.data.error) {
								wmNotify({
									type: 'danger',
									message: data.data.error
								});
							}
						}
					});
				});

				$('#select-calendar').on('change', function () {
					var $newCalendarName = $('#new-calendar-name');
					if ($(this).val() !== 'new-calendar') {
						$newCalendarName.val('');
						$newCalendarName.hide();
					} else {
						$newCalendarName.show();
					}
				});
			}
		});
	});
}
