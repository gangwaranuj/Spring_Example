'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import '../dependencies/jquery.tmpl';

export default Backbone.View.extend({
	el: '#errors',

	initialize: function (options) {
		this.options = _.defaults(options, {
			template: $('#tmpl-errors').template()
		});

		this.template = this.options.template;
	},

	render: function () {
		this.$el.html($.tmpl(this.template, {
			errors: this.model
		}));

		if (this.hasErrors()) {
			this.$el.show();
		} else {
			this.$el.hide();
		}

		return this;
	},

	hasErrors: function () {
		if (!this.model) {
			return false;
		}
		return this.model.length > 0;
	}
});
