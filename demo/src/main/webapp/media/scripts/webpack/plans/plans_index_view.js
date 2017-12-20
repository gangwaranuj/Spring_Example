'use strict';

import Backbone from 'backbone';
import PlansIndexRowView from './plans_index_row_view';

export default Backbone.View.extend({
	el: '#plans-index',

	initialize: function (options) {
		options = options || {};
		this.plans = options.plans;
		this.plans.bind('reset', this.render, this);
		this.plans.bind('add', this.add, this);
		this.render();
	},

	render: function () {
		var container = document.createDocumentFragment();
		this.plans.each(function(plan) {
			var row = new PlansIndexRowView({
				plan: plan
			});
			container.appendChild(row.el);
		});

		this.$el.html(container);
		return this;
	},

	add: function (plan) {
		this.$el.append(new PlansIndexRowView({
			plan: plan
		}).el);
	}
});
