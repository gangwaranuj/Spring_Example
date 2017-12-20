'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import formatCurrency from '../funcs/formatCurrency';
import Template from './templates/approve_assignment_for_pay.hbs';

export default Backbone.View.extend({
	tagName: 'div',
	className: 'assignment_payment_row table-center',

	render: function () {
		this.model = this.model.toJSON();
		var data = '';
		if (this.error) {
			data = '<div>Could not get payment info for this assignment.</div>';
		}	else {
			data = Template({
				model: this.model,
				isInternalAssignment: this.model.pricing.id === '7',
				formattedCurrency: formatCurrency(this.model.payment.actualSpendLimit),
				totalCost: formatCurrency(this.model.payment.totalCost),
				buyerFee: formatCurrency(this.model.payment.buyerFee),
				isImmediate: this.model.configuration.paymentTermsDays > 0,
				isNotAuthorizedForPayment: this.model.isNotAuthorizedForPayment
			});
		}
		this.$el.html(data);
		return this;
	}
});

