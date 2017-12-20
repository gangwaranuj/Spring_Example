'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import wmNotify from '../funcs/wmNotify';

export default Backbone.View.extend({
	el: '#bulk_reschedule_form',
	events: {
		'click [name="scheduling"]'     : 'showOrHideTime',
		'click #reschedule_assignments' : 'save'
	},

	initialize: function (options) {
		this.model = {};
		var $rescheduleFromTime = $('#reschedule-from-time'),
			$rescheduleToTime = $('#reschedule-to-time');

		$('.hasDatePicker').datepicker({ dateFormat: 'mm/dd/yy' });

		$('#reschedule-from-time,#reschedule-to-time').calendricalTimeRange({
			startDate:$('#reschedule-from'),
			endDate:$('#reschedule-to'),
			usa : true
		});

		if (!$rescheduleFromTime.val()) {
			$rescheduleFromTime.val('8:00am');
		}

		if (!$rescheduleToTime.val()) {
			$rescheduleToTime.val('5:00pm');
		}
	},

	showOrHideTime: function () {
		var $time = $('#reschedule-variable-time');
		this.$('#scheduling2').is(':checked') ? $time.hide() : $time.show();
	},

	save: function () {
		var errors = this.validate();

		if (!_.isEmpty(errors)) {
			_.each(errors, function (theMessage) {
				wmNotify({
					message: theMessage,
					type: 'danger'
				});
			});
		} else {
			$.ajax({
				context: this,
				type: 'POST',
				url: '/assignments/reschedule_assignments_multiple',
				dataType: 'json',
				data: {
					workNumbers: this.options.selectedWorkNumbers,
					note: $('#label_note').val(),
					from: $('input[name="from"]').val(),
					fromtime: $('input[name="fromtime"]').val(),
					to: $('input[name="to"]').val(),
					totime: $('input[name="totime"]').val()
				},
				success: _.bind(function (response) {
					if (response.successful) {
						_.each(response.messages, function (theMessage) {
							wmNotify({
								message: theMessage
							});
						});
						this.options.modal.destroy();
						Backbone.Events.trigger('getDashboardData');
					} else {
						_.each(response.messages, function (theMessage) {
							wmNotify({
								message: theMessage,
								type: 'danger'
							});
						});
					}
				}, this)

			});
		}
	},

	validate: function () {
		var errors = [];

		if (this.$('#scheduling2').is(':checked')) {
			if (!this.$('#reschedule-from').val()) {
				errors.push('Please specify time window');
			}
		} else {
			if (!this.$('#reschedule-from').val() || !this.$('#reschedule-to').val()) {
				errors.push('Please specify schedule time');
			}

		}
		return errors;
	}
});
