'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import wmNotify from '../funcs/wmNotify';

export default Backbone.View.extend({
	el: '#cancel_work_form',
	events: {
		'click #cancel_work_button' : 'submitForm'
	},

	initialize: function (options) {
		this.$('[name="workNumbers"]').val(options.selectedWorkNumbers);
	},

	submitForm: function (event) {
		event.preventDefault();

		$.ajax({
			type: 'POST',
			url: this.$el.attr('action'),
			data: this.$el.serialize(),
			dataType: 'json',
			context: this,
			success: function (response) {
				if (response.partialErrors && response.partialErrors.length > 0){
					_.each(response.partialErrors, function (theMessage) {
						wmNotify({
							message: theMessage,
							type: 'danger'
						});
					});
				}

				if (response.successful) {
					_.each(response.messages, function (theMessage) {
						wmNotify({ message: theMessage });
					});
					this.options.modal.destroy();
					Backbone.Events.trigger('getDashboardData');
				} else {
					_.each(response.messages, function (theMessage) {
						wmNotify({
							message: theMessage,
							type: 'danger'
						});
					});
				}
			}
		});
	}
});
