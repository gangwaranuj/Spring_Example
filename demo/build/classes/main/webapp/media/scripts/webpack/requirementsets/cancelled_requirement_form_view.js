'use strict';

import $ from 'jquery';
import AbstractMaxValueView from './abstract_max_value_form_view';
import Template from './templates/cancelled-requirement-form.hbs';

export default AbstractMaxValueView.extend({
	formTemplate: Template,

	getName: function() {
		return this.getMaxValue() + ' within last 6 months';
	},

	getMaxValue: function() {
		return this.$('#maximum-cancelled').val();
	}
});
