'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import wmModal from '../../../funcs/wmModal';

export default Backbone.View.extend({
	events: {
		'submit': 'submit'
	},

	initialize: function () {
		// Prevent submission on enter key
		this.$('input, select').keypress(function (event) {
			return event.keyCode !== 13;
		});
	},

	render: function () {
		this.$('.messages').empty();
		wmModal({
			autorun: true,
			content: this.$el.html(),
			title: this.$el.attr('title') || ''
		});

		return this;
	},

	submit: function () {}
});
