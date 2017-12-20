'use strict';

import Template from './templates/assignment-count-compliance-form.hbs';
import $ from 'jquery';
import AbstractMaxWithPeriodFormView from './abstract_max_with_period_form_view';

export default AbstractMaxWithPeriodFormView.extend({
	formTemplate: Template,

	getViewLabel: function () {
		return this.getMaxValue() + ' (' + this.getIntervalText() + ')';
	},

	getMaxValue: function () {
		return this.$('#maximum-assignments').val();
	},

	getInterval: function () {
		return this.$('[data-selections="interval"]').val();
	},

	getIntervalText: function () {
		return this.$('[data-selections="interval"] option:selected').text().trim().toLowerCase();
	}
});
