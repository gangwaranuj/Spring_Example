'use strict';

import Template from './templates/cart-row.hbs';
import Backbone from 'backbone';
import _ from 'underscore';

export default Backbone.View.extend({
	tagName: 'li',

	events: {
		'click .remove': 'remove'
	},

	initialize: function () {
		_.bindAll(this, 'remove');
		this.template = Template;
	},

	render: function () {
		this.$el.append(this.template(this.model));
		return this;
	},

	remove: function () {
		this.model=[];
		this.render();
		return false;
	}
});
