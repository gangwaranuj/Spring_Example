import $ from 'jquery';
import Backbone from 'backbone';
import wmModal from '../funcs/wmModal';

export default Backbone.View.extend({
	el: 'body',

	events: {
		'click #auto_send_subscription_invoice_email': 'disableSubscriptionEmail',
		'click #auto_send_invoice_email': 'disableInvoiceEmail',
		'click #assignment_resource_funds_example': 'showResourceFunds',
		'click #assignment_client_funds_example': 'showClientFunds',
		'click #assignment_calculator_example': 'showCalculator'

	},

	initialize () {
		this.disableSubscriptionEmail();
		this.disableInvoiceEmail();
	},

	showCalculator (e) {
		e.preventDefault();

		wmModal({
			autorun: true,
			destroyOnClose: true,
			content: $('#pricing-calc-example').html()
		});
	},

	showClientFunds (e) {
		e.preventDefault();

		wmModal({
			autorun: true,
			destroyOnClose: true,
			content: $('#pricing-client-example').html()
		});
	},

	showResourceFunds (e) {
		e.preventDefault();

		wmModal({
			autorun: true,
			destroyOnClose: true,
			content: $('#pricing-resource-example').html()
		});
	},

	disableSubscriptionEmail () {
		if ($('#auto_send_subscription_invoice_email').is(':checked')) {
			$('#subscription_invoice_sent_to_email').removeAttr('readonly');
		} else {
			$('#subscription_invoice_sent_to_email').val('');
			$('#subscription_invoice_sent_to_email').attr('readonly', 'readonly');
		}
	},

	disableInvoiceEmail () {
		if ($('#eu_terms').is(':checked')) {
			$('#standard_terms_end_user').removeAttr('readonly');
		} else {
			$('#standard_terms_end_user').val('');
			$('#standard_terms_end_user').attr('readonly', 'readonly');
		}
	}

});
