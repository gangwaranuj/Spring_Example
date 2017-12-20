'use strict';

import Backbone from 'backbone';
import Template from './templates/cart-venue.hbs';

export default Backbone.View.extend({
	className: 'cart-item',
	template: Template,

	initialize: function (options) {
		options = options || {};
		this.venue = options.venue;
	},

	render: function () {
		this.$el.html(this.template({
			displayName: this.venue.get('displayName'),
			description: this.venue.get('description')
		}));
		return this;
	}
});
