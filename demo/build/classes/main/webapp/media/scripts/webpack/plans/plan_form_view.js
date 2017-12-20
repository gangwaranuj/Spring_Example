'use strict';

import Backbone from 'backbone';

export default Backbone.View.extend({
	el: '#plan-form',

	initialize: function () {
		Backbone.Events.bind('planForm:show', this.show, this);
		Backbone.Events.bind('planForm:hide', this.hide, this);
	},

	render: function () {
		this.show();
	},

	show: function () {
		this.$el.slideDown();
	},

	hide: function () {
		this.$el.slideUp();
	}
});
