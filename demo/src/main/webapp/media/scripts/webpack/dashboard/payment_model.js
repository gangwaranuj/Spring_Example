'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import wmNotify from '../funcs/wmNotify';

export default Backbone.Model.extend({
	initialize: function (options) {
		this.options = options;
	},

	submitPay: false,

	url: function () {
		var base = '';
		if (typeof this.submitPay !== 'undefined' && this.submitPay === true) {
			base = this.paymentUrl();
		} else {
			base = '/assignments/get_assignment_payment_info/' + this.options.id;
		}

		return base;
	},

	parse: function (response) {
		return response.data;
	},

	paymentUrl: function () {
		return '/assignments/pay';
	},

	performPayment: function (event) {
		event.preventDefault();
		// Submitting for payment. Set the flag.
		this.submitPay = true;
		this.save({ id: this.options.id }, {
			type: 'POST',
			success: _.bind(function (model, response) {
				_.each(response.messages, function (theMessage) {
					wmNotify({
						message: theMessage,
						type: response.successful ? 'success' : 'danger'
					});
				});
				this.submitPay = false;
			}, this),
			error: _.bind(function () {
				this.submitPay = false;
				wmNotify({
					type: 'danger',
					message: 'There was an error paying this assignment. Please try again.'
				});
			}, this)
		});
	}
});
