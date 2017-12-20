'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import wmNotify from '../funcs/wmNotify';
import '../dependencies/jquery.tmpl';
import '../dependencies/jquery.serializeObject';
import wmModal from '../funcs/wmModal';
import Template from './templates/show_recurrence.hbs';

export default Backbone.View.extend({
	el: '#reporting-recurrence-settings',

	events: {
		'click #recurrence-save-and-schedule-modal'     : 'showRecurrenceAndSaveModal',
		'click [data-button-type="recurrence-type"]'    : 'toggleRecurrenceType',
		'click [data-button-type="recurrence-enabled"]' : 'toggleRecurrenceEnabled',
		'click .btn-group button'                       : 'toggleButton',
		'click .recurrence-submission'                  : 'saveRecurrenceAction',
		'click #save-recurrence'                        : 'saveRecurrence'
	},

	initialize: function (options) {
		this.options = options || {};
		_.bindAll(this, 'toggleRecurrencePane');
		$('#toggle-recurrence').click(this.toggleRecurrencePane); // outside view
		this.showRecurrenceType(0);
	},

	template: $('#work_report_recurrence-tmpl').template(),

	render: function (model) {
		this.model = model;

		if (model.attributes == null) {
			this.hideRecurrencePane();
			return;
		}

		if (model.attributes.recipients != null) {
			model.attributes.recipients = model.attributes.recipients.join(', ');
		}

		var recurrence = this.$el.html($.tmpl(this.template, this.padFields(model.attributes)));

		// set weekly days values
		if (model.attributes.weekly_days !== null) {
			$.each(model.attributes.weekly_days, function (i) {
				recurrence.find('input[name="weekly_days[]"][value=' + model.attributes.weekly_days[i] + ']').attr('checked', 'checked');
			});
		}

		// display logic for recurrence form
		if (!this.hasQueryString('isCopy')) {
			if (model.attributes.recurrence_enabled_flag) {
				this.showRecurrencePane();
				$('#recurrence-on').trigger('click');
			} else {
				this.hideRecurrencePane();
			}
		} else {
			var $report = $('#work_report_filter_main').find('.report-type'),
				$reportName = $report.html();
			$report.html('Copy of Report: ' + $reportName);
			$('#toggle-recurrence').hide();
		}

		if (this.options.savedReportKey) {
			$('input[name=report_key]').val(this.options.savedReportKey);
		}


		this.showRecurrenceType(model.attributes.recurrence_type);
	},

	hasQueryString: function (field) {
		var url = window.location.href;
		if (url.indexOf('?' + field + '=') !== -1) {
			return true;
		} else if (url.indexOf('&' + field + '=') !== -1) {
			return true;
		}
		return false
	},

	// to prevent tmpl from barfing on missing properties
	padFields: function (a) {
		return $.extend({
			'recurrence_enabled_flag': null,
			'recipients': null,
			'weekly_days': null,
			'recurrence_type': null,
			'daily_weekdays_only_flag': null,
			'monthly_use_day_of_month_flag': null,
			'monthly_frequency_day': null,
			'monthly_frequency_weekday': null,
			'monthly_frequency_weekday_ordinal': null,
			'time_morning_flag': null,
			'time_zone_id': null
		}, a);
	},

	toggleButton: function (e) {
		e.preventDefault();

		if (!$(e.target).hasClass('active')) {
			$(e.target).siblings().removeClass('active').removeClass('btn-gray');
			$(e.target).addClass('active').addClass('btn-gray');
		}
	},

	updateReportKey: function (key) {
		if (this.model.attributes !== null) {
			this.model.attributes.report_key = key;
		}
		$('input[name=report_key]').val(key);
	},

	showRecurrenceAndSaveModal: function () {
		this.saveModal = wmModal({
			autorun: true,
			title: 'Save & Schedule Report',
			destroyOnClose: true,
			content: Template,
			controls: [
				{
					text: 'Cancel',
					close: true,
					classList: ''
				},
				{
					text: 'Save Report',
					primary: true,
					classList: '.work_report_save_submit'
				}
			]
		});
		$('.wm-modal .-active').find('.-primary').on('click', (event) => this.saveRecurrenceAction(event));
	},

	hasRecurrence: function () {
		return $('#reporting-recurrence-settings').is(':visible');
	},

	showRecurrencePane: function () {
		$('#reporting-recurrence-settings').show();
		$('#toggle-recurrence').html('Hide Scheduled Report Settings');
	},

	hideRecurrencePane: function () {
		$('#reporting-recurrence-settings').hide();
		$('#toggle-recurrence').html('Scheduled Report Settings');
	},

	toggleRecurrencePane: function (e) {
		e.preventDefault();

		if (!$('#reporting-recurrence-settings').is(':visible')) {
			this.showRecurrencePane();
		} else {
			this.hideRecurrencePane();
		}
	},

	toggleRecurrenceEnabled: function (e) {
		var id = $(e.target).attr('id');
		$('input[name=recurrence_enabled_flag]').val((id === 'recurrence-on').toString());
	},

	toggleRecurrenceType: function (e) {
		var id = $(e.target).attr('id');
		var recurrenceType = 'daily';
		if (id === 'frequency-weekly') {
			recurrenceType = 'weekly';
		} else if (id === 'frequency-monthly') {
			recurrenceType = 'monthly';
		} else {
			recurrenceType = 'daily';
		}
		$('input[name=recurrence_type]').val(recurrenceType);
		this.showRecurrenceType(recurrenceType);
	},

	showRecurrenceType: function (num) {
		if (num === 'daily') {
			$('#recurrence-daily-options').show();
			$('#recurrence-weekly-options').hide();
			$('#recurrence-monthly-options').hide();
			$('#frequency-daily').attr('checked', 'checked');
		} else if (num === 'weekly') {
			$('#recurrence-daily-options').hide();
			$('#recurrence-weekly-options').show();
			$('#recurrence-monthly-options').hide();
			$('#frequency-weekly').attr('checked', 'checked');
		} else if (num === 'monthly') {
			$('#recurrence-daily-options').hide();
			$('#recurrence-weekly-options').hide();
			$('#recurrence-monthly-options').show();
			$('#frequency-monthly').attr('checked', 'checked');
		}
	},

	saveRecurrenceAction: function (e) {
		e.preventDefault();

		var reportName = $('.report_name').val();

		if (!reportName) {
			wmNotify({
				message: 'You must first name this report.',
				type: 'danger'
			});
			return;
		}

		this.options.router.workReportSave(reportName);
	},

	saveRecurrence: function () {
		var form = $('#recurrenceForm');
		var emailsUnfiltered = $('.email-recipient-list').val().split(/[\n\s,;]+/);
		var emails = [];
		for (var i = 0; i < emailsUnfiltered.length; i++) { // filter() doesn't work in ie8
			var val = emailsUnfiltered[i];
			if (val && val !== '') {
				emails.push(val);
			}
		}

		this.model.attributes = form.serializeObject();
		this.model.attributes.recipients = emails;
		Backbone.emulateJSON = true;
		this.model.save({}, {
			success: (model, response) => {
				if (response.successful) {
					wmNotify({message: "Your report's schedule has been saved."});
				} else {
					wmNotify({
						message: "There was a problem saving your report's schedule.",
						type: 'danger'
					});
				}
			},
			error: () => {
				wmNotify({
					message: "There was a problem saving your report's schedule.",
					type: 'danger'
				});
			}
		});
	}
});
