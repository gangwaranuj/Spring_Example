'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import CustomFieldSelectView from './custom_fields_select_view';
import CustomFieldInputView from './custom_fields_input_view';

export default Backbone.View.extend({
	numberOfFields: 0,

	render: function () {
		var self = this;
		this.$el.html('<div class="fields mr"></div>');
		var fields = _.select(this.model.fields, function (item) {
			return item.type === self.options.type;
		});

		this.numberOfFields = fields.length;

		_.each(fields, this.addOne, this);
		return this;
	},

	addOne: function (field) {
		var view = (field.defaultValue && field.defaultValue.indexOf(',') !== -1) ?
			new CustomFieldSelectView({model: field}) :
			new CustomFieldInputView({model: field});
		$('.fields', this.el).append(view.render().el);
	},

	size: function () {
		return this.numberOfFields;
	}
});
