'use strict';

import Template from './templates/compliance-rules-cart.hbs';
import Backbone from 'backbone';
import CartItemView from './cart_item_view';

export default Backbone.View.extend({
	template: Template,
	className: 'well-b2',

	initialize: function (options) {
		this.eventDispatcher = options.eventDispatcher;
		this.complianceRules = options.complianceRules;
		this.complianceRuleTypes = options.complianceRuleTypes;
		this.complianceRules.bind('add', this.addComplianceRule, this);
		this.render();
	},

	render: function () {
		var container = document.createDocumentFragment();
		this.complianceRules.each(function(complianceRule) {
			 var cartItem = new CartItemView({
				eventDispatcher : this.eventDispatcher,
				complianceRule : complianceRule,
				complianceRuleTypes : this.complianceRuleTypes
			 });
			container.appendChild(cartItem.el);
		}, this);

		this.$el.html(this.template());
		this.$el.find('[data-placeholder="cart"]').html(container);
		return this;
	},

	addComplianceRule: function (complianceRule) {
		this.$el
			.find('[data-placeholder="cart"]')
			.append(new CartItemView({
				eventDispatcher : this.eventDispatcher,
				complianceRule : complianceRule,
				complianceRuleTypes : this.complianceRuleTypes
			}).el);
	}
});
