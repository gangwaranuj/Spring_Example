'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import PopoverTemplate from './templates/calendar_popover_title.hbs';
import PopoverContentTemplate from './templates/calendar_popover_content.hbs';
import '../dependencies/jquery.fullcalendar';
import '../dependencies/jquery.fullcalendar.viewmore';
import '../dependencies/jquery.formbubble';
import '../dependencies/date.min';
import '../dependencies/jquery.bootstrap-tooltip';
import '../dependencies/jquery.bootstrap-popover';
import CalendarPopoverIntuit from './templates/calendar_popover_intuit_content.hbs';

export default Backbone.View.extend({
	el: '#calendar',
	initialize: function (options) {
		this.variables = options.variables;

		$('#assignment_results_container').hide();
		$('.bracket').hide();
		$('#custom-range-dates').hide();
		$('#dashboard_filter_title').hide();
		$('#show_calendar').addClass('active');
		$('#show_list').removeClass('active');
		$('.advanced-filters-toggle').click();
	},

	events: {
		'click .close': 'closePopOver'
	},

	render: function (options) {
		var eventsList = this.generateEvents();
		this.$el.empty();
		this.$el.fullCalendar({
			header: {
				left: 'prev,next today',
				center: 'title',
				right: 'month,agendaWeek,agendaDay'
			},
			allDayDefault: false,
			defaultView: options.currentCalendarView.toString(),
			year: options.calendarYear,
			month: options.calendarMonth,
			date: options.calendarDay,
			events: eventsList,
			eventRender: function (event, element) {
				if (event.custom && event.custom_icon) {
					element
						.find('.fc-event-title')
						.after(' ' + event.custom_icon);
				}

				element.popover({
					title: function () {
						return $('<div />').append(PopoverTemplate(event));
					},
					placement: 'bottom',
					trigger: 'manual',
					html: true,
					content: function () {
						if (event.summary) {
							return event.summary_content;
						} else if (event.custom && event.template) {
							return CalendarPopoverIntuit(event);
						} else {
							return PopoverContentTemplate(event);
						}
					}
				});
			},
			eventClick: function (e) {
				var $popover = $('.popover');

				if ($popover.is(':visible')) {
					$popover.hide();
				} else {
					$(this).popover('show');
				}
			},
			dayClick: function (date) {
				var $calendar = $('#calendar');
				$calendar.fullCalendar('changeView', 'agendaDay');
				$calendar.fullCalendar('gotoDate',date.getFullYear(), date.getMonth(), date.getDate());

			}
		});
	},

	generateEvents: function () {
		var eventsList = [];
		var self = this;
		var eventsLimit = 2;
		var hiddenEvents = [];

		_.each(this.collection.models, function (calendarEvent) {
			if(calendarEvent.get('raw_status') === 'void') {
				hiddenEvents.push(calendarEvent.get('id'));
			}
		});
		for(var i = 0; i < hiddenEvents.length; i++){
			self.collection.remove(hiddenEvents[i]);
		}
		var eventsGroupByStatus = _.groupBy(this.collection.models, function(calendarEvent){
			return calendarEvent.get('raw_status');
		});

		// Dynamic generate summary events
		_.each(eventsGroupByStatus, function (eventsGroup) {
			var eventsSummary = _.groupBy(eventsGroup, function (calendarEvent){
				return calendarEvent.get('scheduled_date');
			});
			_.each(eventsSummary, function (eventsSet){
				// Events limit
				var content = '';
				if(eventsSet.length > eventsLimit) {
					var orderedEventsSet = _.sortBy(eventsSet, function (calendarEvent){
						return calendarEvent.get('scheduled_date_from_in_millis');
					});
					_.each(orderedEventsSet, function (calendarEvent) {
						var id = calendarEvent.get('id');
						self.collection.remove(id);
						calendarEvent.attributes.detail_url =  '/assignments/details/' + id;
						var html = PopoverContentTemplate(calendarEvent.attributes);
						content += html;
					});
					var length = JSON.stringify(eventsSet.length);
					var count = {

						title: length + ' ' + eventsSet[0].get('status'),
						start: eventsSet[0].get('scheduled_date_from_in_millis')/1000,
						className: eventsSet[0].get('raw_status'),
						summary: true,
						summary_content: content
					};
					eventsList.push(count);
				}
			});
		});

		_.each(this.collection.models, function (calendarEvent) {
			var allDay = false;
			var from = calendarEvent.get('scheduled_date_from_in_millis');
			var start = from/1000;
			from = new Date(from);
			var end;
			if (calendarEvent.get('scheduled_date_through_in_millis')){
				var through = calendarEvent.get('scheduled_date_through_in_millis');
				end = through/1000;
				through = new Date(through);
				var oneDay = 24 * 60 * 60 * 1000;
				var diff = through.getTime() - from.getTime();
				if(diff >= oneDay) {
					allDay = true;
				}
			}

			var event = {
				title: calendarEvent.get('title'),
				start: start,
				end:   end,
				allDay: allDay,
				id: calendarEvent.get('id'),
				detail_url: '/assignments/details/' + calendarEvent.get('id'),
				summary: false,
				className: calendarEvent.get('raw_status'),
				scheduled_date: calendarEvent.get('scheduled_date'),
				resource_full_name: calendarEvent.get('resource_full_name'),
				address: calendarEvent.get('address'),
				price: calendarEvent.get('price'),
				buyer: calendarEvent.get('buyer'),
				status: calendarEvent.get('status'),
				isWorkerCompany: self.variables.isWorkerCompany,
				hidePricing: self.variables.hidePricing
			};
			eventsList.push(event);
		});

		// Custom Events; Promotions
		var customEvents = [
			{
				title: 'Individual Tax Returns Due for Tax Year 2015',
				start: (new Date('Apr 18, 2016')).toString(),
				end: (new Date('Apr 18, 2016')).toString(),
				allDay: true,
				id: null,
				detail_url: 'https://selfemployed.intuit.com/workmarket?utm_source=workmarket&utm_medium=IPD&utm_content=calendar&cid=IPD_workmarket_calendar_QBSE',
				summary: false,
				className: 'custom',
				scheduled_date: (new Date('Apr 18, 2016')).toString(),
				resource_full_name: "Intuit",
				address: null,
				price: null,
				buyer: null,
				status: 'Active',
				isWorkerCompany: self.variables.isWorkerCompany,
				hidePricing: self.variables.hidePricing,
				custom: true,
				custom_icon: '<span class="third-party-logo -inline -square -intuit-qb"></span>',
				custom_copy: 'Know what you owe, and pay taxes as you go.',
				custom_cta: 'Learn more',
				template: 'dashboard/calendar_popover_intuit_content'
			},
			{
				title: '1st Quarter 2016 Estimated Tax Payment Due',
				start: (new Date('Apr 18, 2016')).toString(),
				end: (new Date('Apr 18, 2016')).toString(),
				allDay: true,
				id: null,
				detail_url: 'https://selfemployed.intuit.com/workmarket?utm_source=workmarket&utm_medium=IPD&utm_content=calendar&cid=IPD_workmarket_calendar_QBSE',
				summary: false,
				className: 'custom',
				scheduled_date: (new Date('Apr 18, 2016')).toString(),
				resource_full_name: "Intuit",
				address: null,
				price: null,
				buyer: null,
				status: 'Active',
				isWorkerCompany: self.variables.isWorkerCompany,
				hidePricing: self.variables.hidePricing,
				custom: true,
				custom_icon: '<span class="third-party-logo -inline -square -intuit-qb"></span>',
				custom_copy: 'Know what you owe, and pay taxes as you go.',
				custom_cta: 'Learn more',
				template: 'dashboard/calendar_popover_intuit_content'
			}
		];

		eventsList = eventsList.concat(customEvents);

		return eventsList;
	},

	closePopOver: function closePopOver () {
		$('.popover').hide();
	}
});
