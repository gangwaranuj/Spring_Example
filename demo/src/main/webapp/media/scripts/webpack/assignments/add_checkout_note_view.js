'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import '../dependencies/jquery.serializeObject'; 

export default Backbone.View.extend({
	events: {
		'click .button' : 'submitCheckout'
	},

	initialize: function () {
		this.messages = this.$('#add_checkout_note_messages');
	},

	submitCheckout: function (event) {
		if (this.isDisabled(event.target)) {
			return;
		}
		$(event.target).addClass('disabled');

		event.preventDefault();
		event.stopPropagation();
		var serializedObj = this.$el.serializeObject();

		this.options.parentView.sendUpdate('checkout', this.options.date.date, this.options.date.time, serializedObj.noteText, _.bind(function () {
			this.options.parentView.trigger('timetracking:checkout');
			this.options.parentView.render();
			$('.wm-modal--close').trigger('click');
			location.reload();
		}, this), function () {
			$('.wm-modal--close').trigger('click');
		});
		return false;
	},

	isDisabled: function (o) {
		return $(o).is('.disabled');
	}
});
