'use strict';

import Backbone from 'backbone';
import Template from './templates/details/activities.hbs';

export default Backbone.View.extend({
	el: '#history',
	template: Template,

	initialize: function () {
		this.listenTo(this.collection, 'sync', this.render);
		this.collection.fetch({ reset: true });
	},

	render: function () {
		this.$el.html(this.template({ activities: this.collection.toJSON() }));
	}
});
