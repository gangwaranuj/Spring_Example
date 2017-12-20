'use strict';

import Template from './templates/abandoned-requirement-form.hbs';
import AbstractMaxValueView from './abstract_max_value_form_view';

export default AbstractMaxValueView.extend({
	formTemplate: Template,

	getName: function () {
		return this.getMaxValue() + ' within last 6 months';
	},

	getMaxValue: function () {
		return this.$('#maximum-abandoned').val();
	}
});
