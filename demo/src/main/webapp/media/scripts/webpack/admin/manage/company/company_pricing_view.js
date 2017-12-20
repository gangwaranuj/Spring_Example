'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import SubscriptionFormView from './subscription_form_view';
import TransactionalFormView from './transactional_form_view';

export default Backbone.View.extend({
	el: '.content',

	events: {
		'click #switch_to_subscription' : 'switchToSubscription',
		'click .prev_toggle'            : 'togglePreviousSubscription'
	},

	initialize: function (options) {
		new TransactionalFormView(options);
		new SubscriptionFormView(options);
	},

	render: function () {
		return this;
	},

	switchToSubscription: function () {
		$('#previous_subscriptions_details').hide();
		$('h5:contains("Transaction Fee Ranges")').hide();
		$('#transactional_form').hide();
		$('#submit_transactional_form').hide();
		$('#subscription_details').show();
		$('#switch_to_subscription').remove();
	},

	togglePreviousSubscription: function (event) {
		var prevSubscriptionDetails = $(event.currentTarget).next('table');

		prevSubscriptionDetails.toggleClass('dn', !prevSubscriptionDetails.hasClass('dn'));

		// Change + and - icon for toggler
		if (prevSubscriptionDetails.hasClass('dn')) {
			$('.toggler', event.currentTarget).css('background-position', '0px 0px');
		} else {
			$('.toggler', event.currentTarget).css('background-position', '0px 9px');
		}
	}
});
