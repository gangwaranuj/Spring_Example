'use strict';

import Backbone from 'backbone';
import CompanyPricingView from '../company/company_pricing_view';

export default Backbone.View.extend({
	events: {},

	initialize: function (options) {
		this.options = options;
		this.pricingView = new CompanyPricingView(options);
	},

	render: function () {
		return this;
	}
});
