'use strict';

import Backbone from 'backbone';
import Template from './templates/requirements-cart.hbs';
import CartItemView from './cart_item_view';

export default Backbone.View.extend({
	template: Template,
	className: 'well-b2',

	initialize: function (options) {
		this.requirementSet   = options.requirementSet;
		this.requirementTypes = options.requirementTypes;
		this.requirementSet.get('requirements').bind('add', this.addRequirement, this);
		this.render();
	},

	render: function () {
		var self = this;
		var container = document.createDocumentFragment();
		this.requirementSet.get('requirements').each(function (requirement) {
			var cartItem = new CartItemView({
				requirement: requirement,
				requirementTypes: self.requirementTypes
			});
			container.appendChild(cartItem.el);
		});

		this.$el.html(this.template());
		this.$el.find('[data-placeholder="cart"]').html(container);
		return this;
	},

	addRequirement: function (requirement) {
		this.$el
			.find('[data-placeholder="cart"]')
			.append(new CartItemView({
				requirement: requirement,
				requirementTypes: this.requirementTypes
			}).el);
	}
});
