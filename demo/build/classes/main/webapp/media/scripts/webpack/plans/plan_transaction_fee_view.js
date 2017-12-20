'use strict';

import Template from './templates/plan-transaction-fee.hbs'
import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';

export default Backbone.View.extend({
	el: '#plan-transaction-fee',
	template: Template,

	initialize: function (options) {
		this.options = options || {};
	},

	events: {
		'change #percentage' : 'setPercentage'
	},

	setPercentage: function () {
		this.transactionFeePlanConfig.percentage = this.$('#percentage').val();
	},

	render: function () {
		this.$el.html(this.template({
			defaultWorkFeePercentage: this.options.defaultWorkFeePercentage,
			availablePercentages: _.range(26)
		}));

		var percentageOption = this.$('#percentage').children().first();
		var planConfigs = this.plan.get('planConfigs');

		this.transactionFeePlanConfig = _.find(planConfigs, function (planConfig) {
			return planConfig.type === 'transactionFee';
		});

		if (this.transactionFeePlanConfig) {
			percentageOption = this.$('#percentage option[value="' + this.transactionFeePlanConfig.percentage + '"]');
		} else {
			this.transactionFeePlanConfig = { type: 'transactionFee', percentage: this.$('#percentage').val() };
			planConfigs.push(this.transactionFeePlanConfig);
		}

		percentageOption.prop('selected', true);

		return this;
	}
});
