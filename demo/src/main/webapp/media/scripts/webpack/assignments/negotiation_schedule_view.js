'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import '../funcs/wmAssignmentPricing';

export default Backbone.View.extend({
	events: {
		'change input[name="reschedule_option"]' : 'toggleRangeFields',
		'change input[type="text"]'              : 'render'
	},

	initialize: function () {
		// Note: schedule @ assignments_common.js
		$(this.el).wmAssignmentScheduling({ 'modal': true });
		this.render();
	},

	render: function () {
		var scheduleFrom = this.$('.schedule-from').data('timestamp');
		var scheduleThrough = this.$('.schedule-through').data('timestamp');
		var isRange = scheduleThrough !== undefined;
		var wantsRange = this.$('input[name="reschedule_option"]:checked').val() === 'window';

		if (isRange && !wantsRange) {
			var inWindow = this.isSelectionInRange(scheduleFrom, scheduleThrough);

			if (inWindow) {
				if (resourceAppointmentFrom !== '') {
					this.$('#label_specific_time, #label_time_window').html('Change');
				} else {
					this.$('#label_specific_time, #label_time_window').html('Schedule');
				}
			} else {
				this.$('#label_specific_time, #label_time_window').html('Propose');
			}

			this.$('#schedule-requires-approval').hide();
			this.$('#appointment-requires-approval').toggle(!inWindow);
		} else {
			this.$('#schedule-requires-approval').show();
			this.$('#appointment-requires-approval').hide();
		}

		return this;
	},

	parseTime: function (text) {
		var match  = /(\d+)\s*[:\-\.,]\s*(\d+)\s*(am|pm)?/i.exec(text);
		if (match && match.length >= 3) {
			var hour = Number(match[1]);
			var minute = Number(match[2]);
			if (hour === 12 && match[3]) hour -= 12;
			if (match[3] && match[3].toLowerCase() === 'pm') hour += 12;
			return {
				hour:   hour,
				minute: minute
			};
		} else {
			return null;
		}
	},

	isSelectionInRange: function (from, through) {
		var appointmentTime = this.parseTime(this.$('input[name="fromtime"]').val());
		var appointment = $.datepicker.parseDate('mm/dd/yy', this.$('input[name="from"]').val());

		if (appointmentTime && appointment) {
			appointment.setUTCHours(appointmentTime.hour);
			appointment.setUTCMinutes(appointmentTime.minute);

			var appointmentMillis = appointment.getTime() - this.options.millisOffset;

			return ((from <= appointmentMillis) && (through >= appointmentMillis));
		}

		return true;
	},

	toggleRangeFields: function () {
		this.$('.to-date').toggle();
		this.render();
	}
});
