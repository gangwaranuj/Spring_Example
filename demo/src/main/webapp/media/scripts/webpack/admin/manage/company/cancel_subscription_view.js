'use strict';

import _ from 'underscore';
import $ from 'jquery';
import '../../../config/datepicker';
import ModalView from './modal_view';
import wmNotify from '../../../funcs/wmNotify';
import wmModal from '../../../funcs/wmModal';
import regula from '../../../dependencies/regula.min';
import '../../../config/datepicker';

export default ModalView.extend({
	el: '#payment_cancel_subscription',

	render: function () {
		this.$('.messages').empty();
		wmModal({
			autorun: true,
			content: this.$el.html(),
			title: this.$el.attr('title') || ''
		});
		$('#cancellation_date').datepicker({ dateFormat: 'mm/dd/yy' });

		return this;
	},

	submit: function () {
		// Unbind previous elements
		regula.unbind();

		// Validate form
		this.formElements = $(':text', this.el).toArray();
		regula.bind({ elements: this.formElements });
		var validationErrors = regula.validate({ elements: this.formElements });
		_.pluck(validationErrors, 'message').forEach(message => wmNotify({ message, type: 'danger' }));

		return validationErrors.length === 0;
	}
});
