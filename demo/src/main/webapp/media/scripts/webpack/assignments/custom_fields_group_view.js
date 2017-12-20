'use strict';

import Template from './templates/details/custom_field_group_table.hbs';
import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import CustomFieldInputView from './custom_field_input_view';
import CustomFieldSelectView from './custom_field_select_view';

export default Backbone.View.extend({
	tagName: 'div',
	numberOfFields: 0,

	initialize: function () {
		this.auth = this.options.auth;
	},

	render: function () {
		var self = this;

		this.$el.html(Template(_.extend(this.model, this.auth)));

		var fields = _.select(this.model.fields, function (item) {
			return item.type === self.options.type;
		});

		this.numberOfFields = fields.length;

		_.each(fields, this.addOne, this);
		return this;
	},

	addOne: function (field) {
		$.extend(field, {'position': this.model.position});
		var view = (field.defaultValue && field.defaultValue.indexOf(',') !== -1) ?
			new CustomFieldSelectView({model: $.extend(field, this.auth)}) :
			new CustomFieldInputView({model: $.extend(field, this.auth)});
		$('.fields', this.el).append(view.render().el);
	},

	size: function () {
		return this.numberOfFields;
	}
});
