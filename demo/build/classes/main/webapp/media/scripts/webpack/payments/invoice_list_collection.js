'use strict';

import Backbone from 'backbone';
import _ from 'underscore';

export default Backbone.Collection.extend({
	filters: [], // Expects => [{name:'', value:''}]

	initialize: function(options) {
		this.options = options;
	},

	url: function() {
		var base = '/payments/invoices/list';
		var params = _.map(this.filters, function(item) {
			return item.name + '=' + item.value;
		}).join('&');

		return base + '?' + params;
	},

	parse: function(response) {
		this.total_results = response.total_results;
		this.fastFundsMap = response.fast_funds_map || {};
		this.balances = response.balances;
		return response.data;
	},

	setFilters: function(f) {
		this.filters = f;
	},

	hasFastFunds: function (invoiceId) {
		return invoiceId in this.fastFundsMap;
	},

	isFastFundsAvailable: function (invoiceId) {
		return Boolean(this.getFastFundsFee(invoiceId));
	},

	isFastFundsComplete: function (invoiceId) {
		return Boolean(this.getFastFundsCompletetionDate(invoiceId));
	},

	getFastFundsFee: function (invoiceId) {
		if (this.hasFastFunds(invoiceId)) {
			return this.fastFundsMap[invoiceId].fastFundsFee;
		}
	},

	getFastFundsCompletetionDate: function (invoiceId) {
		if (this.hasFastFunds(invoiceId)) {
			return this.fastFundsMap[invoiceId].fastFundedOn;
		}
	},

	getTotalResults: function() {
		return this.total_results;
	}
});
