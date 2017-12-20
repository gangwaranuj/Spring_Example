'use strict';

import Backbone from 'backbone';

export default Backbone.View.extend({
	tagName: 'option',

	initialize: function (options) {
		options = options || {};
		this.venue = options.venue;
		this.render();
	},

	render: function () {
		this.$el.val(this.venue.get('name'));
		this.$el.text(this.venue.get('displayName') + ' - ' + this.venue.get('description'));
		return this;
	}
});
