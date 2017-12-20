'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import '../funcs/wmAssignmentPricing';

export default Backbone.View.extend({
	events: {
		'change input[name="reschedule_option"]' : 'toggleRangeFields',
		'change #price_negotiation'              : 'togglePriceNegotiation',
		'change #schedule_negotiation'           : 'toggleScheduleNegotiation',
		'change #offer_expiration'               : 'toggleExpiration'
	},

	initialize: function () {
		// Note: pricing and scheduling @ assignments_common.js
		this.$el.wmAssignmentPricing({ 'modal' : this.options.isModal });
		this.$el.wmAssignmentScheduling({ 'modal' : this.options.isModal });

		this.$('input[type=text]').trigger('keyup');

		$('#expires_on').datepicker({dateFormat: 'mm/dd/yy'});
		$('#expires_on_time').calendricalTime({startDate: $('#expires_on'), defaultTime: null});
	},

	togglePriceNegotiation: function () {
		this.$('#price_negotiation_config').toggle();
	},

	toggleScheduleNegotiation: function (event) {
		this.$('#schedule_negotiation_config').toggle();
		this.toggleRangeFields(event);
	},

	toggleExpiration: function () {
		this.$('#offer_expiration_config').toggle();
	},

	toggleRangeFields: function () {
		this.$('.to-date').toggle(this.$('input[name="reschedule_option"]:checked').val() === 'window');
		this.render();
	}
});
